/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients;

import java.util.Calendar;
import java.util.List;

import javax.activation.DataHandler;

import com.crh.logon.webservices.clients.beans.ecmservice.ArchivableOrderData;
import com.crh.logon.webservices.clients.beans.ecmservice.PdfData;
import com.crh.logon.webservices.clients.util.ClientConfig;
import org.datacontract.schemas._2004._07.ixtent_contentserver_crh_ecminterface_dataaccess.ArrayOfPdfFile;
import org.datacontract.schemas._2004._07.ixtent_contentserver_crh_ecminterface_dataaccess.FaultDetail;
import org.datacontract.schemas._2004._07.ixtent_contentserver_crh_ecminterface_dataaccess.FaultDetailE;
import org.datacontract.schemas._2004._07.ixtent_contentserver_crh_ecminterface_dataaccess.GlobalMetadata;
import org.datacontract.schemas._2004._07.ixtent_contentserver_crh_ecminterface_dataaccess.PdfFile;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.to.ecmservice.ArchiveOrderData;
import com.crh.logon.to.ecmservice.ArchiveOrderDataResponse;
import com.crh.logon.to.ecmservice.IECMService_ArchiveOrderData_FaultDetailFault_FaultMessage;
import com.crh.logon.to.ecmservice.VerifyLoginCredentials;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
public class LogonToECMService {

	/**
	 * 
	 */
	private static final int FAULT_NON_UNIQUE_FILE_NAME_114 = 114;

	public static final int ARCHIVE_ORDER_RESULT_SUCCESS = 1;

	private static final String APPLICATION_IDENTIFIER = "LGN";

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LogonToECMService.class);
	private static final String CONFIG_LABEL = "ixtent";
	
	private static final Boolean DO_NOT_SEND_EMAIL = false;
	private static final Boolean IGNORE_UNEXPECTED_END_OF_FILE_EXCEPTIOPN = true;
	
	private ConfigBean configBean;

	/**
	 * @param configBean
	 */
	public LogonToECMService(ConfigBean configBean) {
		this.configBean = configBean;
	}

	public Integer archiveOrder(ArchivableOrderData orderData) {
		Object[][] params = {{"orderData", orderData}};
		final boolean isEnteringExitingEnabled = LOG.isEnteringExitingEnabled();
		if (isEnteringExitingEnabled) {
			LOG.entering(this, "archiveOrder", params);
		}
		ArchiveOrderData archiveOrderRequest = null;
		try {
			final ClientConfig clientConfig = new ClientConfig(configBean,
																CONFIG_LABEL, 
																DO_NOT_SEND_EMAIL, 
																IGNORE_UNEXPECTED_END_OF_FILE_EXCEPTIOPN);
			final EnhancedECMServiceStub stub = new EnhancedECMServiceStub(clientConfig);
			archiveOrderRequest = createArchiveOrderRequest(orderData);
			ArchiveOrderDataResponse archiveOrderDataResponse = stub.archiveOrderData(archiveOrderRequest);
			
			if (archiveOrderDataResponse != null) {
				return archiveOrderDataResponse.getArchiveOrderDataResult();
			} 
			else {
				
				throw new LogonException(Reason.RES_ERROR_WSCLIENT);				
			}
		}
		catch (Exception e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Call to ArchiveOrderData failed for Input: " + toString(archiveOrderRequest));
			}
			if (e instanceof IECMService_ArchiveOrderData_FaultDetailFault_FaultMessage) {
				IECMService_ArchiveOrderData_FaultDetailFault_FaultMessage fault = (IECMService_ArchiveOrderData_FaultDetailFault_FaultMessage) e;
				FaultDetailE faultMessage = fault.getFaultMessage();
				FaultDetail faultDetail = faultMessage.getFaultDetail();
				int number = faultDetail.getNumber();
				if (FAULT_NON_UNIQUE_FILE_NAME_114 == number) {
					LOG.warn(String.format(
							"Order with id: %d was previously archived, document already exists in the archive.",
							orderData.getOrderId()));
					return null;
				}
				else {
					throw new LogonException(e, Reason.RES_ERROR_WSCLIENT);
				}
			}
			else if (e instanceof LogonException) {
				throw (LogonException) e;
			}
			else {
				throw new LogonException(e, Reason.RES_ERROR_WSCLIENT);
			}
		}
		finally {
			if (isEnteringExitingEnabled) {
				LOG.exiting();
			}
		}	
	}

	/**
	 * @param archiveOrderRequest
	 * @return
	 */
	private String toString(ArchiveOrderData archiveOrderRequest) {
		StringBuilder string = new StringBuilder("ArchiveOrderData {");
		string.append(toString(archiveOrderRequest.getGlobalMetadata()));
		string.append(", OrderLog = ");
		string.append(archiveOrderRequest.getOrderLog());
		string.append(toString(archiveOrderRequest.getPdfFiles()));
		string.append("}");

		return string.toString();
	}

	private String toString(ArrayOfPdfFile pdfFiles) {
		StringBuilder string = new StringBuilder();
		string.append("PdfFiles");
		if (pdfFiles != null) {
			PdfFile[] pdfFileArray = pdfFiles.getPdfFile();
			string.append(" (len = ");
			string.append(pdfFileArray.length);
			string.append(") = [");
			if (pdfFileArray != null) {
				for (int i = 0; i < pdfFileArray.length; i++) {
					PdfFile pdfFile = pdfFileArray[i];
					if (i > 0) {
						string.append(", ");
					}
					string.append(pdfFile.getFileName());
				}
			}
			string.append("]");
		}
		else {
			string.append(" = null");
		}
		return string.toString();
	}

	/**
	 * @param globalMetadata
	 * @return
	 */
	private String toString(GlobalMetadata globalMetadata) {
		StringBuilder string = new StringBuilder("GlobalMetadata {");
		string.append("LogonOrderNumber = ");
		string.append(globalMetadata.getLogonOrderNumber());
		string.append("}");
		return string.toString();
	}

	/**
	 * 
	 * 
	 */
	public boolean archiveOrderFailed(ArchiveOrderDataResponse archiveOrderDataResponse) {
		boolean failed = true;
		if (archiveOrderDataResponse != null) {
			int archiveOrderDataResult = archiveOrderDataResponse.getArchiveOrderDataResult();
			failed = archiveOrderDataResult != ARCHIVE_ORDER_RESULT_SUCCESS;
		}
		return failed;
	}

	/**
	 * @return
	 */
	private ArchiveOrderData createArchiveOrderRequest(ArchivableOrderData orderData) {
		ArchiveOrderData archiveOrderDataRequest = new ArchiveOrderData();
		
		String userName = configBean.getWsclientIxtentRequestUser();
		archiveOrderDataRequest.setUserName(userName);

		String password = configBean.getWsclientIxtentRequestPassword();
		archiveOrderDataRequest.setPassword(password);
		
		GlobalMetadata globalMetadata = extractGlobalMetadata(orderData);
		archiveOrderDataRequest.setGlobalMetadata(globalMetadata);
		
		ArrayOfPdfFile arrayOfPdfFile = extractArrayOfPdf(orderData);
		archiveOrderDataRequest.setPdfFiles(arrayOfPdfFile);
		
		String orderLog = orderData.getOrderLog();
		archiveOrderDataRequest.setOrderLog(orderLog);
		
		return archiveOrderDataRequest;
	}

	private ArrayOfPdfFile extractArrayOfPdf(ArchivableOrderData orderData) {
		List<PdfData> pdfList = orderData.getPdfData();
		
		PdfFile[] arrayOfPdf = new PdfFile[pdfList.size()];
		for (int i = 0; i < pdfList.size(); i++) {
			PdfFile pdfFile = convertToPdfFile(pdfList.get(i));
			arrayOfPdf[i] = pdfFile;
		}
		ArrayOfPdfFile arrayOfPdfFile = new ArrayOfPdfFile();
		arrayOfPdfFile.setPdfFile(arrayOfPdf);
		return arrayOfPdfFile;
	}

	/**
	 * @param object
	 * @return
	 */
	private PdfFile convertToPdfFile(PdfData pdfData) {
		String fileName = pdfData.getFileName();
		Calendar createDate = pdfData.getCreationDate();
		DataHandler fileContent = pdfData.getData();
		
		PdfFile pdfFile = new PdfFile();
		pdfFile.setFileName(fileName);
		pdfFile.setCreateDate(createDate);
		pdfFile.setFileContent(fileContent);
		
		return pdfFile;
	}

	/**
	 * @param orderData
	 * @return
	 */
	private GlobalMetadata extractGlobalMetadata(ArchivableOrderData orderData) {
		GlobalMetadata globalMetadata = new GlobalMetadata();
		globalMetadata.setDocumentCreationDate(orderData.getDocumentCreationDate());
		globalMetadata.setCompanyCode(orderData.getCompanyCode());
		globalMetadata.setSalesOrganization(orderData.getSalesOrganization());
		globalMetadata.setDivision(orderData.getDivision());
		globalMetadata.setDistributionChannel(orderData.getDistributionChannel());
		globalMetadata.setApplicationIdentifier(APPLICATION_IDENTIFIER);
		globalMetadata.setLogonOrderNumber(orderData.getOrderNumber());
		globalMetadata.setDeliveryNoteNumber(orderData.getDeliveryNoteNumber());
		globalMetadata.setSequentialOrderNumber(orderData.getSequentialOrderNumber());
		globalMetadata.setCustomerOrderNumber(orderData.getCustomerOrderNumber());
		globalMetadata.setShipToSapId(orderData.getShipToSapId());
		globalMetadata.setShipToSapName(orderData.getShipToSapName());
		globalMetadata.setSoldToSapId(orderData.getSoldToSapId());
		globalMetadata.setSoldToSapName(orderData.getSoldToSapName());
		globalMetadata.setBillToSapId(orderData.getBillToSapId());
		globalMetadata.setBillToSapName(orderData.getBillToSapName());
		globalMetadata.setPayerSapId(orderData.getPayerSapId());
		globalMetadata.setPayerSapName(orderData.getPayerSapName());
		globalMetadata.setCommerciaCarrierSapId(orderData.getCommercialCarrierSapId());
		globalMetadata.setCommerciaCarrierName(orderData.getCommercialCarrierName());
		globalMetadata.setExecutingCarrierSapId(orderData.getExecutingCarrierSapId());
		globalMetadata.setExecutingCarrierName(orderData.getExecutingCarrierName());
		globalMetadata.setContactNumber(orderData.getContractNumber());
		globalMetadata.setContactType(orderData.getContractType());
		globalMetadata.setContactIncoterm(orderData.getContractIncoterm());
		globalMetadata.setDeliveryType(orderData.getDeliveryType());
		globalMetadata.setPoolName(orderData.getPoolName());
		globalMetadata.setDeliveryDateFrom(orderData.getDeliveryDateFrom());
		globalMetadata.setDeliveryDateTo(orderData.getDeliveryDateTo());
		globalMetadata.setActualDeliveryDate(orderData.getActualDeliveryDate());
		globalMetadata.setLoadingDate(orderData.getLoadingDate());
		globalMetadata.setDeliveryPlant(orderData.getDeliveryPlant());
		globalMetadata.setShippingPoint(orderData.getShippingPoint());
		globalMetadata.setShippingCondition(orderData.getShippingCondition());
		return globalMetadata;
	}

	/**
	 * @return
	 */
	private VerifyLoginCredentials createVerifyLoginCredentialsRequest() {
		VerifyLoginCredentials verifyLoginCredentials = new VerifyLoginCredentials();
		
		String userName = configBean.getWsclientIxtentRequestUser();
		verifyLoginCredentials.setUserName(userName);
		
		String password = configBean.getWsclientIxtentRequestPassword();
		verifyLoginCredentials.setPassword(password);
		
		return verifyLoginCredentials;
	}

}
