/*
 * n * Author : AdNovum Informatik AG
 * Version Number : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients;

import javax.xml.stream.XMLStreamReader;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.webservices.clients.beans.deliverydocument.SalesDocumentAssembler;
import com.crh.logon.webservices.clients.beans.deliverydocument.SalesDocumentDTO;
import com.crh.logon.webservices.clients.beans.goodsmovement.GoodsMovementAssembler;
import com.crh.logon.webservices.clients.beans.goodsmovement.GoodsMovementDTO;
import com.crh.logon.webservices.clients.beans.logisticreport.LogisticReportAssembler;
import com.crh.logon.webservices.clients.beans.pricingcall.PricingCallAssembler;
import com.crh.logon.webservices.clients.beans.pricingcall.PricingCallDTO;
import com.crh.logon.webservices.clients.beans.saplogisticreport.SapLogisticReportDTO;
import com.crh.logon.webservices.clients.beans.shipmentdocument.ShipmentDocumentAssembler;
import com.crh.logon.webservices.clients.beans.shipmentdocument.ShipmentDocumentDTO;
import com.crh.logon.webservices.clients.beans.transferdeliverydocument.TransferDeliveryDocumentAssembler;
import com.crh.logon.webservices.clients.beans.transferdeliverydocument.TransferDeliveryDocumentDTO;
import com.crh.logon.webservices.clients.util.ClientConfig;
import com.crh.logon.webservices.clients.util.WebserviceClientUtil;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.SalesDocumentCreation;
import goodsmovements.mm.net.crh.ea.XI_90E6B0A6Da7B3Afbb45604298Fccd288_ServiceStub.GoodsMovements;
import logisticsreporting.qm.net.holcim.ea.DX1_Java_Instance_LogisticsReporting_OutStub;
import org.apache.axis2.AxisFault;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.ZFSD_PRICE_SIMULATION;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.ZFSD_PRICE_SIMULATIONResponse;
import pricesimulation.sales.net.crh.ea.P2ZFSD_PRICE_SIMULATIONException;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.ShipmentDocumentCreation;
import transferdeliverydocument.sales.net.crh.ea.XI_fea67Eb194643B05Ae97B85A3F32C901_ServiceStub.TransferDeliveryCreation;

/**
 * @author AdNovum Informatik AG
 */
public class LogonToSap {
	private static JcanLogger LOG = JcanLoggerFactory.getLogger(LogonToSap.class);

	/**
	 * This method allows you to send a 'raw' XML request; this is used from the
	 * web service test page.
	 * This should never be used for any other purpose (hence the method name).
	 * 
	 * @param configBean
	 * @param xmlStreamReader
	 */
	public static void sendSalesDocumentForTesting(ConfigBean configBean, XMLStreamReader xmlStreamReader) {
		try {
			sendSalesDocument(configBean, SalesDocumentCreation.Factory.parse(xmlStreamReader));
		}
		catch (Exception e) {
			if (e instanceof LogonException) {
				throw (LogonException) e;
			}
			else {
				throw new LogonException(e, Reason.RES_ERROR_WSCLIENT);
			}
		}
	}

	public static void sendSalesDocument(ConfigBean configBean, SalesDocumentDTO salesDocument) {
		try {
			sendSalesDocument(configBean, SalesDocumentAssembler.createSalesDocumentCreation(salesDocument));
		}
		catch (Exception e) {
			if (e instanceof LogonException) {
				throw (LogonException) e;
			}
			else {
				throw new LogonException(e, Reason.RES_ERROR_WSCLIENT);
			}
		}
	}

	private static void sendSalesDocument(ConfigBean configBean, SalesDocumentCreation salesDocumentCreation)
			throws AxisFault {
		// [[
		Object[][] params = { { "configBean", configBean }, { "salesDocumentCreation", salesDocumentCreation } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonToSap.class, "sendSalesDocument", params);
		}
		// ]]
		try {
			final ClientConfig clientConfig = new ClientConfig(configBean, "sap_deliverydocument", false, true);
			final EnhancedXI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub stub = new EnhancedXI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub(
					clientConfig);
			stub.deliveryDocument_Async(salesDocumentCreation);
		}
		catch (deliverydocument.sales.net.crh.ea.SystemError e) {
			throw new LogonException(e.getFaultMessage().getCode() + ":" + e.getFaultMessage().getContext() + ":"
					+ e.getFaultMessage().getText(), Reason.RES_ERROR_WSCLIENT);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	/**
	 * This method allows you to send a 'raw' XML request; this is used from the
	 * web service test page.
	 * This should never be used for any other purpose (hence the method name).
	 * 
	 * @param configBean
	 * @param xmlStreamReader
	 */
	public static void sendGoodsMovements(ConfigBean configBean, XMLStreamReader xmlStreamReader) {
		// [[
		Object[][] params = { { "configBean", configBean }, { "xmlStreamReader", xmlStreamReader } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonToSap.class, "sendGoodsMovements", params);
		}
		// ]]
		try {
			try {
				sendGoodsMovement(configBean, GoodsMovements.Factory.parse(xmlStreamReader));
			}
			catch (Exception e) {
				if (e instanceof LogonException) {
					throw (LogonException) e;
				}
				else {
					throw new LogonException(e, Reason.RES_ERROR_WSCLIENT);
				}
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public static void sendGoodsMovement(ConfigBean configBean, GoodsMovementDTO goodsMovementsDto) {
		// [[
		Object[][] params = { { "configBean", configBean }, { "goodsMovementsDto", goodsMovementsDto } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonToSap.class, "sendGoodsMovements", params);
		}
		// ]]
		try {
			try {
				sendGoodsMovement(configBean, GoodsMovementAssembler.createGoodsMovement(goodsMovementsDto));
			}
			catch (Exception e) {
				if (e instanceof LogonException) {
					throw (LogonException) e;
				}
				else {
					throw new LogonException(e, Reason.RES_ERROR_WSCLIENT);
				}
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}



	public static void sendGoodsMovement(ConfigBean configBean, GoodsMovements goodsMovements) throws AxisFault {
		// [[
		Object[][] params = { { "configBean", configBean }, { "goodsMovements", goodsMovements } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonToSap.class, "sendGoodsMovements", params);
		}
		// ]]
		try {
			final ClientConfig clientConfig = new ClientConfig(configBean, "sap_goodsmovement", false, true);
			final EnhancedXI_90E6B0A6Da7B3Afbb45604298Fccd288_ServiceStub stub = new EnhancedXI_90E6B0A6Da7B3Afbb45604298Fccd288_ServiceStub(
					clientConfig);
			stub.goodsMovements_Async(goodsMovements);
		}
		catch (goodsmovements.mm.net.crh.ea.SystemError e) {
			throw new LogonException(e.getFaultMessage().getCode() + ":" + e.getFaultMessage().getContext() + ":"
					+ e.getFaultMessage().getText(), Reason.RES_ERROR_WSCLIENT);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public static void sendShipmentDocumentForTesting(ConfigBean configBean, XMLStreamReader xmlStreamReader) {
		try {
			sendShipmentDocument(configBean, ShipmentDocumentCreation.Factory.parse(xmlStreamReader));
		}
		catch (Exception e) {
			if (e instanceof LogonException) {
				throw (LogonException) e;
			}
			else {
				throw new LogonException(e, Reason.RES_ERROR_WSCLIENT);
			}
		}
	}

	public static void sendShipmentDocument(ConfigBean configBean, ShipmentDocumentDTO shipmentDocument) {
		try {
			sendShipmentDocument(configBean, ShipmentDocumentAssembler.createShipmentDocumentCreation(shipmentDocument));
		}
		catch (Exception e) {
			if (e instanceof LogonException) {
				throw (LogonException) e;
			}
			else {
				throw new LogonException(e, Reason.RES_ERROR_WSCLIENT);
			}
		}
	}

	private static void sendShipmentDocument(ConfigBean configBean, ShipmentDocumentCreation shipmentDocumentCreation)
			throws AxisFault {
		// [[
		Object[][] params = { { "configBean", configBean }, { "document", shipmentDocumentCreation } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonToSap.class, "sendShipmentDocument", params);
		}
		// ]]
		try {
			final ClientConfig clientConfig = new ClientConfig(configBean, "sap_shipmentdocument", false, true);
			final EnhancedDX1_Java_Instance_ShipmentDocument_OutStub stub = new EnhancedDX1_Java_Instance_ShipmentDocument_OutStub(
					clientConfig);
			stub.shipmentDocument_Out(shipmentDocumentCreation);
		}
		catch (shipmentdocument.logisticdocument.net.crh.ea.SystemError e) {
			throw new LogonException(e.getFaultMessage().getCode() + ":" + e.getFaultMessage().getContext() + ":"
					+ e.getFaultMessage().getText(), Reason.RES_ERROR_WSCLIENT);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public static void sendTransferDeliveryDocumentForTesting(ConfigBean configBean, XMLStreamReader xmlStreamReader) {
		try {
			sendTransferDeliveryDocument(configBean, TransferDeliveryCreation.Factory.parse(xmlStreamReader));
		}
		catch (Exception e) {
			if (e instanceof LogonException) {
				throw (LogonException) e;
			}
			else {
				throw new LogonException(e, Reason.RES_ERROR_WSCLIENT);
			}
		}
	}

	public static void sendTransferDeliveryDocument(ConfigBean configBean,
			TransferDeliveryDocumentDTO transferDeliveryDocument) {
		try {
			sendTransferDeliveryDocument(configBean,
					TransferDeliveryDocumentAssembler.createTransferDeliveryCreation(transferDeliveryDocument));
		}
		catch (Exception e) {
			if (e instanceof LogonException) {
				throw (LogonException) e;
			}
			else {
				throw new LogonException(e, Reason.RES_ERROR_WSCLIENT);
			}
		}
	}

	private static void sendTransferDeliveryDocument(ConfigBean configBean, TransferDeliveryCreation transferDeliveryCreation)
			throws AxisFault {
		// [[
		Object[][] params = { { "configBean", configBean }, { "document", transferDeliveryCreation } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonToSap.class, "sendTransferDeliveryDocument", params);
		}
		// ]]
		try {
			final ClientConfig clientConfig = new ClientConfig(configBean, "sap_transferdeliverydocument", false, true);
			final EnhancedXI_fea67Eb194643B05Ae97B85A3F32C901_ServiceStub stub = new EnhancedXI_fea67Eb194643B05Ae97B85A3F32C901_ServiceStub(
					clientConfig);
			stub.transferDeliveryDocument_Async(transferDeliveryCreation);
		}
		catch (transferdeliverydocument.sales.net.crh.ea.SystemError e) {
			throw new LogonException(e.getFaultMessage().getCode() + ":" + e.getFaultMessage().getContext() + ":"
					+ e.getFaultMessage().getText(), Reason.RES_ERROR_WSCLIENT);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}
	
	/**
	 * This method allows you to send a 'raw' XML request; this is used from the
	 * web service test page.
	 * This should never be used for any other purpose (hence the method name).
	 * 
	 * @param configBean
	 * @param xmlStreamReader
	 */
	public static String sendPriceSimulationForTesting(ConfigBean configBean, XMLStreamReader xmlStreamReader) {
		try {
			
			ZFSD_PRICE_SIMULATIONResponse response = sendPriceSimulation(configBean, ZFSD_PRICE_SIMULATION.Factory.parse(xmlStreamReader));
			
			String serializedString = WebserviceClientUtil.getSerializedString(response, "ZFSD_PRICE_SIMULATION.Response");
			
			return serializedString;
		}
		catch (Exception e) {
			if (e instanceof LogonException) {
				throw (LogonException) e;
			}
			else {
				throw new LogonException(e, Reason.RES_ERROR_WSCLIENT);
			}
		}
	}

	public static ZFSD_PRICE_SIMULATIONResponse sendPriceSimulation(ConfigBean configBean, PricingCallDTO priceCallDTO) {
		try {
			return sendPriceSimulation(configBean, PricingCallAssembler.createPricingCall(priceCallDTO));
		}
		catch (Exception e) {
			if (e instanceof LogonException) {
				throw (LogonException) e;
			}
			else {
				LOG.error("Pricing call creation failed", e);
				throw new LogonException(e, Reason.RES_ERROR_WSCLIENT);
			}
		}
	}
	
	private static ZFSD_PRICE_SIMULATIONResponse sendPriceSimulation(ConfigBean configBean, ZFSD_PRICE_SIMULATION pricingCall)
			throws AxisFault {
		// [[
		ZFSD_PRICE_SIMULATIONResponse result = null;
		Object[][] params = {{"configBean", configBean},
							{"pricingCall", pricingCall}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonToSap.class, "sendPriceSimulation", params);
		}
		// ]]
		try {
			
			final ClientConfig clientConfig = new ClientConfig(configBean, "pricing_call", false, true);
			final EnhancedDX1_Java_Instance_PriceSimulation_OutStub stub = 
					new EnhancedDX1_Java_Instance_PriceSimulation_OutStub(clientConfig);
			
			ZFSD_PRICE_SIMULATIONResponse response = stub.priceSimulation_Sync(pricingCall);
			
			return result = response;
			
		}
		catch (pricesimulation.sales.net.crh.ea.SystemError e) {
			throw new LogonException(e.getFaultMessage().getCode() + ":" + e.getFaultMessage().getContext() + ":"
					+ e.getFaultMessage().getText(), Reason.RES_ERROR_PRICING_CALL_GENERAL_ERROR);
		}
		catch (P2ZFSD_PRICE_SIMULATIONException e) {
			LOG.error("There was a pricing call error ("+e.getMessage()+") which hints a logic problem.");
			throw new LogonException(e.getFaultMessage() + ":"
					+ e.getMessage(), Reason.RES_ERROR_PRICING_CALL_BUSINESS_ERROR);
		}
		catch (Exception e) {
			LOG.error("There was a general pricing call error ("+e.getMessage()+") which hints a connectivity problem.", e);
			throw new LogonException(e.getMessage(), Reason.RES_ERROR_PRICING_CALL_GENERAL_ERROR);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public static void sendLogisticReportForTesting(ConfigBean configBean, XMLStreamReader xmlStreamReader) {
		try {
			sendLogisticReport(configBean, DX1_Java_Instance_LogisticsReporting_OutStub.MT_LogisticsReporting_Out.Factory.parse(xmlStreamReader));
		}
		catch (Exception e) {
			if (e instanceof LogonException) {
				throw (LogonException) e;
			}
			else {
				throw new LogonException(e, Reason.RES_ERROR_WSCLIENT);
			}
		}
	}

	public static void sendLogisticReport(ConfigBean configBean, SapLogisticReportDTO logisticDTO) {
		try {
			sendLogisticReport(configBean, LogisticReportAssembler.createLogisticReportClientDocument(logisticDTO));
		}
		catch (Exception e) {
			if (e instanceof LogonException) {
				throw (LogonException) e;
			}
			else {
				LOG.error("Logistic Report (SAP BW) creation failed", e);
				throw new LogonException(e, Reason.RES_ERROR_WSCLIENT);
			}
		}
	}

	@AdnTrace
	private static void sendLogisticReport(ConfigBean configBean, DX1_Java_Instance_LogisticsReporting_OutStub.MT_LogisticsReporting_Out logisticReport)
			throws AxisFault {

		try {

			final ClientConfig clientConfig = new ClientConfig(configBean, "sap_bw", false, true);
			final EnhancedDX1_Java_Instance_LogisticsReporting_OutStub stub =
					new EnhancedDX1_Java_Instance_LogisticsReporting_OutStub(clientConfig);
			
			stub.logisticsReporting_Out(logisticReport);

		}
		catch (logisticsreporting.qm.net.holcim.ea.SystemError e) {
			throw new LogonException(e.getFaultMessage().getCode() + ":" + e.getFaultMessage().getContext() + ":"
					+ e.getFaultMessage().getText(), Reason.RES_ERROR_LOGISTIC_REPORT_GENERAL_ERROR);
		}
		catch (RuntimeException e) {
			LOG.error("There was a logistics report error ("+e.getMessage()+") which hints a logic problem.");
			throw new LogonException(e.getMessage(), Reason.RES_ERROR_PRICING_CALL_BUSINESS_ERROR);
		}
		catch (Exception e) {
			LOG.error("There was a general logistics report error ("+e.getMessage()+") which hints a connectivity problem.", e);
			throw new LogonException(e.getMessage(), Reason.RES_ERROR_LOGISTIC_REPORT_GENERAL_ERROR);
		}

	}

}
