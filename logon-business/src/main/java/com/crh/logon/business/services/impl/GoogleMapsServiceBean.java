/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.beans.GoogleMapsDistanceMatrixResultBean;
import com.crh.logon.business.services.dispatchers.GoogleMapsServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.GoogleMapsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.ExtSiteEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.TruckRouteEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.SapTrspModeBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class GoogleMapsServiceBean extends GoogleMapsServiceDispatcher implements GoogleMapsServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(GoogleMapsServiceBean.class);

	@EJB
	private DispoServiceLocalInterface dispoService;

	@EJB
	private RefDataGenericsServiceLocalInterface refDataGenericsService;

	public GoogleMapsDistanceMatrixResultBean getDistanceMatrixResult(
			ShippingpointEntity startShippingpoint, ShiptoEntity endShipto) {

		final String transportModeCd = Constant.TRANSPORTATION_MODE_TRUCK;
		final SapTrspModeBsEntity sapTrspMode = refDataGenericsService.findBsEntity(
				SapTrspModeBsEntity.class, transportModeCd);
		return getDistanceMatrixResult(startShippingpoint, endShipto, sapTrspMode);
	}

	public GoogleMapsDistanceMatrixResultBean getDistanceMatrixResult(
			ShippingpointEntity startShippingpoint, ShiptoEntity endShipto, Long deliveryTypeCd) {

		final DeliveryTypeBsEntity deliveryTypeBsEntity = refDataGenericsService.findBsEntity(
				DeliveryTypeBsEntity.class, deliveryTypeCd);
		final SapTrspModeBsEntity sapTrspMode = deliveryTypeBsEntity.getSapTrspMode();

		return getDistanceMatrixResult(startShippingpoint, endShipto, sapTrspMode);
	}

	public GoogleMapsDistanceMatrixResultBean getDistanceMatrixResult(ShippingpointEntity startShippingpoint,
			ShiptoEntity endShipto,
			SapTrspModeBsEntity sapTrspMode) {

		if ((startShippingpoint == null) || (endShipto == null)) {
			if (LOG.isWarnEnabled()) {
				LOG.warn("getDistanceMatrixResult called without a shippingPoint or shipTo");
			}
			return null;
		}
		final TruckRouteEntity route = findTruckRouteEntity(startShippingpoint, endShipto, sapTrspMode);

		GoogleMapsDistanceMatrixResultBean uncorrectedResult = null;

		double timeCorrectionFactor = 1d;
		double distanceCorrectionFactor = 1d;

		if (route == null) {
			uncorrectedResult = callGoogleMapsDistanceMatrix(
					new LatLonCoordinate(startShippingpoint.getCoordLat(), startShippingpoint.getCoordLon()),
					new LatLonCoordinate(endShipto.getCoordLat(), endShipto.getCoordLon()));

			if (uncorrectedResult.getStatus().equals(Constant.GMAPS_DISTANCE_MATRIX_RESPONSE_STATUS_OK)
					&& uncorrectedResult.getDurationMin() != null) {
				createNewTruckRouteEntity(uncorrectedResult, startShippingpoint, endShipto, sapTrspMode);

				// Using the app default correction factor, since there is no route data.
				timeCorrectionFactor = getConfigBean().getAppDefaultTruckRouteTimeCorrectionFactor();

				// distance correction factor should be defaulted to 1
			}
		}
		else {
			// We are only allowed to use route data which is not older than 30 days.
			if (!isUpToDate(route)) {
				// Route data is expired, we should update it
				uncorrectedResult = callGoogleMapsDistanceMatrix(
						new LatLonCoordinate(startShippingpoint.getCoordLat(), startShippingpoint.getCoordLon()),
						new LatLonCoordinate(endShipto.getCoordLat(), endShipto.getCoordLon()));

				if (uncorrectedResult.getStatus().equals(Constant.GMAPS_DISTANCE_MATRIX_RESPONSE_STATUS_OK)
						&& uncorrectedResult.getDurationMin() != null) {
					route.setDistanceCalculatedM(uncorrectedResult.getDistance());

					// Using the app default correction factor, since the route data is expired.
					timeCorrectionFactor = getConfigBean().getAppDefaultTruckRouteTimeCorrectionFactor();
					route.setTimeCorrectionFactor(BigDecimal.valueOf(timeCorrectionFactor));
					route.setTimeCalculatedMin(uncorrectedResult.getDurationMin());
					route.setLastCalculatedDat(new Date());
				}
			}
			else {
				// Route data is not expired - Using the correction factor form the route data.
				timeCorrectionFactor = route.getTimeCorrectionFactor().doubleValue();
				if (route.getDistanceCorrectionFactor() != null) {
					distanceCorrectionFactor = route.getDistanceCorrectionFactor().doubleValue();
				}
				uncorrectedResult = new GoogleMapsDistanceMatrixResultBean(
						Constant.GMAPS_DISTANCE_MATRIX_RESPONSE_STATUS_OK,
						route.getDistanceCalculatedM(),
						route.getTimeCalculatedMin());
			}
		}

		// Use the correction factor to correct the raw Google-Map duration result
		return new GoogleMapsDistanceMatrixResultBean(uncorrectedResult, timeCorrectionFactor, distanceCorrectionFactor);
	}

	private TruckRouteEntity findTruckRouteEntity(ShippingpointEntity startShippingpoint, ShiptoEntity endShipto,
			SapTrspModeBsEntity sapTrspMode) {
		final ReadWriteDao<TruckRouteEntity> dao = getDaoProvider().getTruckRouteEntityDao();
		final Filter<TruckRouteEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(TruckRouteEntity.ATTR_START_SHIPPINGPOINT, startShippingpoint));
		filter.addExpression(Restrictions.eq(TruckRouteEntity.ATTR_END_SHIPTO, endShipto));
		filter.addExpression(Restrictions.eq(TruckRouteEntity.ATTR_SAP_TRSP_MODE, sapTrspMode));
		filter.addExpression(Order.desc(TruckRouteEntity.ATTR_LAST_CALCULATED_DAT));

		return dao.findSingle(filter);
	}

	private boolean isUpToDate(TruckRouteEntity route) {
		if (route == null) {
			return false;
		}

		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, Constant.TRUCK_ROUTE_DATA_EXPIRES_AFTER_DAYS * (-1));
		return !route.getLastCalculatedDat().before(cal.getTime());
	}

	private TruckRouteEntity createNewTruckRouteEntity(GoogleMapsDistanceMatrixResultBean result,
			ShippingpointEntity startShippingpoint, ShiptoEntity endShipto,
			SapTrspModeBsEntity sapTrspMode) {

		TruckRouteEntity newTruckRoute = null;

		if (result != null) {

			final ReadWriteDao<TruckRouteEntity> dao = getDaoProvider().getTruckRouteEntityDao();

			newTruckRoute = new TruckRouteEntity();

			newTruckRoute.setStartShippingpoint(startShippingpoint);
			newTruckRoute.setEndShipto(endShipto);
			newTruckRoute.setTimeCorrectionFactor(BigDecimal.valueOf(getConfigBean().getAppDefaultTruckRouteTimeCorrectionFactor()));
			newTruckRoute.setDistanceCorrectionFactor(BigDecimal.ONE);

			if (sapTrspMode == null) {
				final String transportModeCd = Constant.TRANSPORTATION_MODE_TRUCK;
				sapTrspMode = refDataGenericsService.findBsEntity(
						SapTrspModeBsEntity.class, transportModeCd);
			}
			newTruckRoute.setSapTrspMode(sapTrspMode);

			newTruckRoute.setDistanceCalculatedM(result.getDistance());
			newTruckRoute.setTimeCalculatedMin(result.getDurationMin());

			newTruckRoute.setLastCalculatedDat(new Date());

			dao.persist(newTruckRoute);

		}
		else {
			LOG.warn("callGoogleMapsDistanceMatrix did not return a result, unable to persist the TruckRouteEntity");
		}

		return newTruckRoute;
	}

	/**
	 * Calculates the distance in meters and the duration in seconds for the DispoAjaxService.createCarrierStockAssignment().
	 * Uses
	 * the truck route table if record is available or the Google Maps API for the calculation. Duration will be corrected with
	 * the correction factor (from truck route table if any record or the app default).
	 *
	 * @param shiptoSapBp           The shipto - the destination of the route.
	 * @param bagStockShippingpoint The bag stock shippingpoint of the carrier from the order. This is the origin of the route.
	 * @see DispoAjaxService.createCarrierStockAssignment()
	 */
	public GoogleMapsDistanceMatrixResultBean getCarrierStockAssignmentDistanceMatrixResult(
			SapBpEntity shiptoSapBp, ShippingpointEntity bagStockShippingpoint,
			DeliveryTypeBsEntity deliveryType) {

		if (shiptoSapBp == null || bagStockShippingpoint == null || deliveryType == null) {
			LOG.warn("getCarrierStockAssignmentDistanceMatrixResult called without a shiptoSapBp, shippingpoint or deliveryType.");
			return null;
		}

		final ShiptoEntity shipto = dispoService.fetchShipto(shiptoSapBp.getSapBpId());

		final SapTrspModeBsEntity sapTrspMode = deliveryType.getSapTrspMode();

		return getDistanceMatrixResult(bagStockShippingpoint, shipto, sapTrspMode);
	}

	public GoogleMapsDistanceMatrixResultBean getDistanceMatrixResult(ShippingpointEntity startShippingpoint,
			ShippingpointEntity endShippingpoint) {
		GoogleMapsDistanceMatrixResultBean result = null;
		if ((startShippingpoint != null) && (endShippingpoint != null)) {
			result = callGoogleMapsDistanceMatrix(
					new LatLonCoordinate(startShippingpoint.getCoordLat(), startShippingpoint.getCoordLon()),
					new LatLonCoordinate(endShippingpoint.getCoordLat(), endShippingpoint.getCoordLon()));
		}
		else {
			if (LOG.isWarnEnabled()) {
				LOG.warn("getDistanceMatrixResult called without a start or end ShippingpointEntity");
			}
		}
		return result;
	}

	public GoogleMapsDistanceMatrixResultBean getDistanceMatrixResult(ShiptoEntity startShipto, ShiptoEntity endShipto) {
		GoogleMapsDistanceMatrixResultBean result = null;
		if ((startShipto != null) && (endShipto != null)) {
			result = callGoogleMapsDistanceMatrix(
					new LatLonCoordinate(startShipto.getCoordLat(), startShipto.getCoordLon()),
					new LatLonCoordinate(endShipto.getCoordLat(), endShipto.getCoordLon()));
		}
		else {
			if (LOG.isWarnEnabled()) {
				LOG.warn("getDistanceMatrixResult called without a start or end ShiptoEntity");
			}
		}
		return result;
	}

	public GoogleMapsDistanceMatrixResultBean getDistanceMatrixResult(ShippingpointEntity startShippingpoint, Double endCoordLat,
			Double endCoordLon) {
		GoogleMapsDistanceMatrixResultBean result = null;
		if ((startShippingpoint != null) && (endCoordLat != null) && (endCoordLon != null)) {
			result = callGoogleMapsDistanceMatrix(
					new LatLonCoordinate(startShippingpoint.getCoordLat(), startShippingpoint.getCoordLon()),
					new LatLonCoordinate(endCoordLat, endCoordLon));
		}
		else {
			if (LOG.isWarnEnabled()) {
				LOG.warn("getDistanceMatrixResult called without a start ShippingpointEntity or end coordinates.");
			}
		}
		return result;
	}

	public GoogleMapsDistanceMatrixResultBean getDistanceMatrixResult(ShiptoEntity startShipto, Double endCoordLat,
			Double endCoordLon) {
		GoogleMapsDistanceMatrixResultBean result = null;
		if ((startShipto != null) && (endCoordLat != null) && (endCoordLon != null)) {
			result = callGoogleMapsDistanceMatrix(
					new LatLonCoordinate(startShipto.getCoordLat(), startShipto.getCoordLon()),
					new LatLonCoordinate(endCoordLat, endCoordLon));
		}
		else {
			if (LOG.isWarnEnabled()) {
				LOG.warn("getDistanceMatrixResult called without a start ShiptoEntity or end coordinates.");
			}
		}
		return result;
	}

	public GoogleMapsDistanceMatrixResultBean getDistanceMatrixResult(Double startCoordLat, Double startCoordLon,
			ShippingpointEntity endShippingpoint) {
		GoogleMapsDistanceMatrixResultBean result = null;
		if ((startCoordLat != null) && (startCoordLon != null) && (endShippingpoint != null)) {
			result = callGoogleMapsDistanceMatrix(
					new LatLonCoordinate(startCoordLat, startCoordLon),
					new LatLonCoordinate(endShippingpoint.getCoordLat(), endShippingpoint.getCoordLon()));
		}
		else {
			if (LOG.isWarnEnabled()) {
				LOG.warn("getDistanceMatrixResult called without a start coordinates or end ShippingpointEntity.");
			}
		}
		return result;
	}

	public GoogleMapsDistanceMatrixResultBean getDistanceMatrixResult(Double startCoordLat, Double startCoordLon,
			ShiptoEntity endShipto) {
		GoogleMapsDistanceMatrixResultBean result = null;
		if ((startCoordLat != null) && (startCoordLon != null) && (endShipto != null)) {
			result = callGoogleMapsDistanceMatrix(
					new LatLonCoordinate(startCoordLat, startCoordLon),
					new LatLonCoordinate(endShipto.getCoordLat(), endShipto.getCoordLon()));
		}
		else {
			if (LOG.isWarnEnabled()) {
				LOG.warn("getDistanceMatrixResult called without a start coordinates or end ShiptoEntity.");
			}
		}
		return result;
	}

	public GoogleMapsDistanceMatrixResultBean getDistanceMatrixResult(Double startCoordLat, Double startCoordLon,
			Double endCoordLat, Double endCoordLon) {
		GoogleMapsDistanceMatrixResultBean result = null;
		if ((startCoordLat != null) && (startCoordLon != null) && (endCoordLat != null) && (endCoordLon != null)) {
			result = callGoogleMapsDistanceMatrix(
					new LatLonCoordinate(startCoordLat, startCoordLon),
					new LatLonCoordinate(endCoordLat, endCoordLon));
		}
		else {
			if (LOG.isWarnEnabled()) {
				LOG.warn("getDistanceMatrixResult called without a start or end coordinates.");
			}
		}
		return result;
	}

	public GoogleMapsDistanceMatrixResultBean getDistanceMatrixResult(ExtSiteEntity start, ExtSiteEntity end) {
		GoogleMapsDistanceMatrixResultBean result = null;
		if ((start != null) && (end != null)) {
			result = callGoogleMapsDistanceMatrix(
					new LatLonCoordinate(start.getCoordLat(), start.getCoordLon()),
					new LatLonCoordinate(end.getCoordLat(), end.getCoordLon()));
		}
		else {
			if (LOG.isWarnEnabled()) {
				LOG.warn("getDistanceMatrixResult called without a start or end ExtSiteEntity");
			}
		}
		return result;
	}

	private GoogleMapsDistanceMatrixResultBean callGoogleMapsDistanceMatrix(LatLonCoordinate start, LatLonCoordinate end) {
		HttpURLConnection conn = null;

		try {
			final GoogleMapsDistanceMatrixResultBean result;

			final Map<String, String> params = new HashMap<>();

			params.put("origins",
					StringUtils.join(Arrays.asList(
							start.getCoordLat(),
							start.getCoordLon()), ','));

			params.put("destinations",
					StringUtils.join(Arrays.asList(
							end.getCoordLat(),
							end.getCoordLon()), ','));

			params.put("mode", "driving");
			params.put("units", "metric");
			params.put("sensor", "false");

			final String baseUrl = this.getGoogleMapsBaseUrl("distancematrix", "xml");
			conn = callGoogleMaps(baseUrl, params);
			final Document doc = parseXML(conn.getInputStream(), baseUrl);

			final XPathFactory factory = XPathFactory.newInstance();
			final XPath xpath = factory.newXPath();

			final Node statusNode = getFirstNodeWithXPath(xpath, doc, "DistanceMatrixResponse/status");

			if (statusNode != null) {
				final String status = statusNode.getTextContent();
				if (Constant.GMAPS_DISTANCE_MATRIX_RESPONSE_STATUS_OK.equals(status)) {
					final Node elementNode = getFirstNodeWithXPath(xpath, doc,
							"DistanceMatrixResponse/row/element");

					if (elementNode != null) {
						final Node distanceNode = getFirstNodeWithXPath(xpath, elementNode,
								"distance/value");
						final Node durationNode = getFirstNodeWithXPath(xpath, elementNode,
								"duration/value");

						result = new GoogleMapsDistanceMatrixResultBean(status,
								(distanceNode != null) ? distanceNode.getTextContent() : null,
								(durationNode != null) ? durationNode.getTextContent() : null);
					}
					else {
						LOG.warn("callGoogleMaps with distancematrix/xml resulted in no DistanceMatrixResponse/row/element "
								+ "node");
						result = new GoogleMapsDistanceMatrixResultBean(status);
					}
				}
				else {
					LOG.warn("callGoogleMaps with distancematrix/xml resulted in invalid result - status code: '{}'", status);
					result = new GoogleMapsDistanceMatrixResultBean(status);
				}
			}
			else {
				LOG.error("callGoogleMaps with distancematrix/xml resulted in unexpected response - unable to parse status.");
				throw new LogonException(
						"callGoogleMaps with distancematrix/xml resulted in unexpected response - unable to parse status.",
						Reason.RES_ERROR_GMAPS_DISTANCE_UNEXPECTED_RESPONSE);
			}

			return result;
		}
		catch (Exception t) {
			throw new LogonException("Call to GoogleMaps api DistanceMatrix failed.", t);
		}
		finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	private String getGoogleMapsBaseUrl(String apiType, String responseType) {
		return getConfigBean().getGmapApiBaseUrl()
				+ "maps/api/" + apiType + '/' + responseType;
	}

	private HttpURLConnection callGoogleMaps(String baseUrl, Map<String, String> params) throws IOException {
		final StringBuilder gmapURLbuilder = new StringBuilder(baseUrl);
		if ((params != null) && (!params.isEmpty())) {
			gmapURLbuilder.append('?');
			int idx = 0;
			for (Map.Entry<String, String> param : params.entrySet()) {
				if (idx > 0) {
					gmapURLbuilder.append('&');
				}
				gmapURLbuilder.append(param.getKey());
				gmapURLbuilder.append('=');
				gmapURLbuilder.append(param.getValue());
				idx++;
			}
		}
		//If in the future there is a need for Base64 signed signature then check the GoogleMapsUrlSigner class in git repo pre
		// 2018.10.03.
		//It was implemented before, but at the moment it is not used anymore -> deleted.
		//At the moment only the API key is in the url, it does not contain signature		
		gmapURLbuilder.append("&key=" + Constant.GMAPS_API_CRH_PRIVATE_KEY);

		final Proxy proxy = getConfiguredProxy();
		final URL gmapURL = new URL(gmapURLbuilder.toString());

		return (HttpURLConnection) gmapURL.openConnection(proxy);
	}

	private Proxy getConfiguredProxy() {
		// Default is no proxy.
		Proxy proxy = Proxy.NO_PROXY;

		// Using the webservice client proxy settings from config if any.
		// TODO (paksyd May 16, 2013): Config switch to use wsclient proxy settings or no proxy?!
		final String proxyHost = getConfigBean().getWsclientProxyHost();
		final int proxyPort = getConfigBean().getWsclientProxyPort();
		final Proxy.Type proxyType = Proxy.Type.HTTP; // wsclient uses html proxy
		if (proxyHost != null && proxyPort > -1) {
			final SocketAddress address = new InetSocketAddress(proxyHost, proxyPort);
			proxy = new Proxy(proxyType, address);
		}

		return proxy;
	}

	private Document parseXML(InputStream inputStream, String baseURL)
			throws ParserConfigurationException, SAXException, IOException {
		final DocumentBuilderFactory docbf = DocumentBuilderFactory.newInstance();
		docbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		docbf.setNamespaceAware(true);

		final DocumentBuilder docbuilder = docbf.newDocumentBuilder();
		return docbuilder.parse(inputStream, baseURL);
	}

	private Node getFirstNodeWithXPath(XPath xpath, Node context, String expression) throws XPathExpressionException {
		NodeList nodeList = (NodeList) xpath.evaluate(expression, context, XPathConstants.NODESET);

		if (nodeList.getLength() == 0) {
			if (LOG.isWarnEnabled()) {
				LOG.warn("xpath expression " + expression + " did not match any node");
			}
			//TODO: to throw or not to throw?
			return null;
		}
		else {
			return nodeList.item(0);
		}
	}

	private static final class LatLonCoordinate implements Serializable {

		private static final long serialVersionUID = 1L;

		private final String coordLat;

		private final String coordLon;

		private LatLonCoordinate(Double coordLat, Double coordLon) {
			this(ObjectUtils.toString(coordLat, null), ObjectUtils.toString(coordLon, null));
		}

		private LatLonCoordinate(String coordLat, String coordLon) {
			this.coordLat = coordLat;
			this.coordLon = coordLon;
		}

		public String getCoordLat() {
			return coordLat;
		}

		public String getCoordLon() {
			return coordLon;
		}
	}
}