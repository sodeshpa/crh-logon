/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.kafka;

import javax.ejb.Singleton;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import com.crh.logon.business.kafka.dto.KafkaDTO;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Singleton
@TransactionManagement(TransactionManagementType.BEAN)
public class KafkaDTOSerializer {

	private final ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule())
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			.serializationInclusion(JsonInclude.Include.NON_EMPTY).build();

	public String serialize(KafkaDTO kafkaDTO) {
		if (objectMapper == null) {
			throw new LogonException("ObjectMapper should be configured before serialization attempt", Reason.RES_IMPL_BUG);
		}
		try {
			return objectMapper.writeValueAsString(kafkaDTO);
		}
		catch (JsonProcessingException e) {
			throw new LogonException("Couldn't serialize the dto of class " + kafkaDTO.toString(), e);
		}
	}
}
