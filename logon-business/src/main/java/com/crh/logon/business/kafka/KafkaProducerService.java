/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.kafka;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.function.Function;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Asynchronous;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.common.base.ConfigReloadListener;
import com.crh.logon.business.services.interfaces.ConfigService;
import com.crh.logon.utils.ConfigBean;
import org.apache.kafka.clients.consumer.ConsumerGroupMetadata;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.VoidSerializer;

@Singleton
@AdnTrace
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@LocalBean
public class KafkaProducerService implements ConfigReloadListener {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(KafkaProducerService.class);

	private static final String HEADER_CONTENTTYPE = "dataContentType";

	private static final byte[] HEADER_CONTENTTYPE_VALUE = "application/json".getBytes(UTF_8);

	@EJB
	private ConfigService configService;

	private volatile Producer<Object, String> kafkaProducer = null;

	@PostConstruct
	public void init() {
		ConfigBean configBean = configService.getConfigBean();

		String config = configBean.getKafkaProducerConfig();
		if (config == null || config.isEmpty()) {
			LOG.info("Kafka integration is disabled");
			kafkaProducer = null;
			return;
		}

		// parse kafka producer properties
		Properties props = new Properties();
		props.put(ProducerConfig.CLIENT_ID_CONFIG, configBean.getInstance());
		// we clear out the \r characters as JDBC or something unnecessarily inserts them. (The DB doesn't have this character.)
		try (StringReader stringReader = new StringReader(config.replace("\r", ""))) {
			props.load(stringReader);
		}
		catch (IOException e) {
			LOG.error("Couldn't parse KafkaProducerConfig: {}", config);
			kafkaProducer = null;
			return;
		}
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, VoidSerializer.class.getName()); // no key
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		try {
			KafkaProducer<Object, String> kafkaProducer = new KafkaProducer<>(props);

			String topicSuffix = configBean.getKafkaProducerTopicSuffix();
			if (topicSuffix != null && !topicSuffix.isEmpty()) {
				this.kafkaProducer = new TopicTransformingProducer<>(kafkaProducer, topic -> topic + "." + topicSuffix);
			} else {
				this.kafkaProducer = kafkaProducer;
			}
		}
		catch (Exception e) {
			LOG.error("Couldn't start KafkaProducer", e);
			kafkaProducer = null;
		}

	}

	@Override
	public void configReloaded() {
		stop();
		init();
	}

	@PreDestroy
	public void stop() {
		if (kafkaProducer != null) {
			try {
				kafkaProducer.close();
			}
			catch (KafkaException e) {
				LOG.error("Couldn't stop KafkaProducer", e);
			}
		}
	}

	public boolean isKafkaNotConfigured() {
		return kafkaProducer == null;
	}

	@Asynchronous
	public void publish(List<KafkaRecordDTO> dtos) {
		if (isKafkaNotConfigured()) {
			LOG.warn("Kafka integration is disabled, skip records publishing");
			return;
		}
		LOG.debug("Publishing " + dtos.size() + " messages");
		for (KafkaRecordDTO dto : dtos) {
			convertAndSend(dto);
		}
	}

	@Asynchronous
	public void publish(KafkaRecordDTO dto) {
		if (isKafkaNotConfigured()) {
			LOG.warn("Kafka integration is disabled, skip record publishing");
			return;
		}
		LOG.debug("Publishing single message");
		convertAndSend(dto);
	}

	private void convertAndSend(KafkaRecordDTO dto) {
		ProducerRecord<Object, String> record = new ProducerRecord<>(dto.topic, null, dto.body);
		Headers headers = record.headers();
		headers.add(HEADER_CONTENTTYPE, HEADER_CONTENTTYPE_VALUE);

		try {
			kafkaProducer.send(record);
		}
		catch (KafkaException e) {
			LOG.error("Couldn't send record: " + record.toString(), e);
		}
	}

	public static class KafkaRecordDTO implements Serializable {

		private final String topic;

		private final String body;

		public KafkaRecordDTO(String topic, String body) {
			this.topic = topic;
			this.body = body;
		}

	}

	private static class TopicTransformingProducer<K, V> implements Producer<K, V> {
		private final KafkaProducer<K, V> kafkaProducer;
		private final Function<String, String> topicTransformer;

		private TopicTransformingProducer(KafkaProducer<K, V> kafkaProducer,
				Function<String, String> topicTransformer) {
			this.kafkaProducer = kafkaProducer;
			this.topicTransformer = topicTransformer;
		}

		@Override
		public void initTransactions() {
			kafkaProducer.initTransactions();
		}

		@Override
		public void beginTransaction() throws ProducerFencedException {
			kafkaProducer.beginTransaction();
		}

		@Override
		public void sendOffsetsToTransaction(Map<TopicPartition, OffsetAndMetadata> offsets, String consumerGroupId)
				throws ProducerFencedException {
			kafkaProducer.sendOffsetsToTransaction(offsets, consumerGroupId);
		}

		@Override
		public void sendOffsetsToTransaction(Map<TopicPartition, OffsetAndMetadata> offsets, ConsumerGroupMetadata groupMetadata)
				throws ProducerFencedException {
			kafkaProducer.sendOffsetsToTransaction(offsets, groupMetadata);
		}

		@Override
		public void commitTransaction() throws ProducerFencedException {
			kafkaProducer.commitTransaction();
		}

		@Override
		public void abortTransaction() throws ProducerFencedException {
			kafkaProducer.abortTransaction();
		}

		@Override
		public Future<RecordMetadata> send(ProducerRecord<K, V> record) {
			return kafkaProducer.send(copyAndTransform(record));
		}

		@Override
		public Future<RecordMetadata> send(ProducerRecord<K, V> record, Callback callback) {
			return kafkaProducer.send(copyAndTransform(record), callback);
		}

		@Override
		public void flush() {
			kafkaProducer.flush();
		}

		@Override
		public List<PartitionInfo> partitionsFor(String topic) {
			return kafkaProducer.partitionsFor(topic);
		}

		@Override
		public Map<MetricName, ? extends Metric> metrics() {
			return kafkaProducer.metrics();
		}

		@Override
		public void close() {
			kafkaProducer.close();
		}

		@Override
		public void close(Duration timeout) {
			kafkaProducer.close(timeout);
		}

		private ProducerRecord<K, V> copyAndTransform(ProducerRecord<K, V> o) {
			return new ProducerRecord<>(topicTransformer.apply(o.topic()), o.partition(), o.timestamp(), o.key(), o.value(),
					o.headers());
		}
	}
}
