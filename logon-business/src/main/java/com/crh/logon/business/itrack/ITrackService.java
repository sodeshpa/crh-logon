/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.itrack;

import java.time.OffsetDateTime;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.common.base.ConfigReloadListener;
import com.crh.logon.business.common.http.RestHttpClientServiceBean;
import com.crh.logon.business.itrack.api.CrhApi;
import com.crh.logon.business.itrack.model.OdometerReadings;
import com.crh.logon.business.itrack.model.Position;
import com.crh.logon.business.itrack.model.Target;
import com.crh.logon.business.itrack.model.TollAmount;
import com.crh.logon.business.itrack.model.Unloads;
import com.crh.logon.business.services.interfaces.ConfigService;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.impl.client.CloseableHttpClient;

@Stateless
@LocalBean
public class ITrackService implements ConfigReloadListener {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ITrackService.class);

	private ApiClient client;
	private CrhApi api;

	private volatile boolean disabled = true;

	@EJB
	private ConfigService configService;

	@EJB
	private RestHttpClientServiceBean restHttpClientServiceBean;

	@PostConstruct
	public void init() {
		ConfigBean configBean = configService.getConfigBean();
		String itruckApiUser = configBean.getItruckApiUser();
		String itruckApiPass = configBean.getItruckApiPass();
		if (StringUtils.empty(itruckApiUser)) {
			LOG.warn("iTrack API username is missing, iTruck is disabled");
			disabled = true;
			return;
		}
		if (StringUtils.empty(itruckApiPass)) {
			LOG.warn("iTrack API password is missing, iTruck is disabled");
			disabled = true;
			return;
		}

		if (client == null) {
			CloseableHttpClient httpClient = restHttpClientServiceBean.getHttpClient();
			ObjectMapper objectMapper = restHttpClientServiceBean.getObjectMapper();
			client = new ApiClient(httpClient, objectMapper);
		}
		if (api == null) {
			api = new CrhApi(client);
		}

		client.setUsername(itruckApiUser);
		client.setPassword(itruckApiPass);

		String basePath = configBean.getItruckApiBasepathOverride();
		client.setBasePath(basePath != null ? basePath : ApiClient.DEFAULT_BASEPATH);

		disabled = false;
	}

	@Override
	public void configReloaded() {
		init();
	}

	public List<Target> getAllTargets() {
		assertITruckEnabled();
		List<Target> target = null;
		try {
			target = api.getAllTargets();
		}
		catch (ApiException e) {
			LOG.error("error", e);
		}
		return target;
	}

	public List<Position> getLastPositions(List<Integer> targetIds) {
		assertITruckEnabled();
		List<Position> positions = null;
		try {
			positions = api.getLastPositions(targetIds);
		}
		catch (ApiException e) {
			LOG.error("error", e);
		}
		return positions;
	}

	public Unloads getUnloadsForTarget(Integer targetId, OffsetDateTime fromTime, OffsetDateTime toTime) {
		assertITruckEnabled();
		Unloads unloads = null;
		try {
			unloads = api.getUnloadsForTarget(targetId, fromTime, toTime);
		}
		catch (ApiException e) {
			if (e.getCause() instanceof java.net.SocketTimeoutException){
				LOG.error("java.net.SocketTimeoutException for getUnloadsForTarget call. [TargetId: " + targetId + "] [fromTime: " + fromTime + "] [toTime: " + toTime + "]");
			}
			else {
				LOG.error("error", e);
			}
		}
		return unloads;
	}

	public TollAmount getTollAmountForTarget(Integer targetId, OffsetDateTime fromTime, OffsetDateTime toTime) {
		assertITruckEnabled();
		TollAmount tollAmount = null;
		try {
			tollAmount = api.getTollAmountForTarget(targetId, fromTime, toTime);
		}
		catch (ApiException e) {
			if (e.getCause() instanceof java.net.SocketTimeoutException){
				LOG.error("java.net.SocketTimeoutException for getTollAmountForTarget call. [TargetId: " + targetId + "] [fromTime: " + fromTime + "] [toTime: " + toTime + "]");
			}
			else {
				LOG.error("error", e);
			}
		}
		return tollAmount;
	}

	public OdometerReadings getOdometerReadings(Integer targetId, OffsetDateTime fromTime, OffsetDateTime toTime) {
		assertITruckEnabled();
		OdometerReadings odometerReadings = null;
		try {
			odometerReadings = api.getOdometerReadings(targetId, fromTime, toTime);
		}
		catch (ApiException e) {
			if (e.getCause() instanceof java.net.SocketTimeoutException){
				LOG.error("java.net.SocketTimeoutException for getOdometerReadings call. [TargetId: " + targetId + "] [fromTime: " + fromTime + "] [toTime: " + toTime + "]");
			}
			else {
				LOG.error("error", e);
			}
		}
		return odometerReadings;
	}

	private void assertITruckEnabled() {
		if (disabled) {
			throw new IllegalStateException("iTruck is disabled");
		}
	}

}
