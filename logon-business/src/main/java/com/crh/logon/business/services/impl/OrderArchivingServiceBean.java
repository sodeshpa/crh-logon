/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.OrderArchivingServiceDispatcher;
import com.crh.logon.business.services.impl.cron.ScheduledTask;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.ConfigServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderArchivingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.stores.domain.ArchivingLogStoreInterface;
import com.crh.logon.business.stores.domain.OrderStoreInterface;
import com.crh.logon.business.stores.domain.views.ArchivingMetaDataStoreInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.views.ArchivingMetadataEntity;
import com.crh.logon.persistence.entities.views.ArchivingOrderLogEntity;
import com.crh.logon.persistence.entities.views.ArchivingPdfEntity;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.webservices.clients.LogonToECMService;
import com.crh.logon.webservices.clients.beans.ecmservice.ArchivableOrderData;
import com.crh.logon.webservices.clients.beans.ecmservice.PdfData;

@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
@AdnTrace
public class OrderArchivingServiceBean extends OrderArchivingServiceDispatcher implements OrderArchivingServiceLocalInterface {

	private static final String ARCHIVE_CRON_TIME_FORMAT = "HH:mm";
	private static final Boolean UNSUCCESSFUL_COMMUNICATION = false;
	

	@EJB RefDataGenericsServiceLocalInterface referentialDataService;
	@EJB ConfigServiceLocalInterface configService;
	@EJB OrderArchivingServiceLocalInterface orderArchivingService;	
	@EJB OrderStoreInterface orderStore;
	@EJB ArchivingMetaDataStoreInterface archivingMetaDataStore;
	@EJB ArchivingLogStoreInterface archivingLogStore;
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(OrderArchivingServiceBean.class);


	@ScheduledTask(name = "archive_orders", second = "0", minute = "0", hour = "21", configurationMethodName = "getArchiveOrdersCronEnabled", schedule = true)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void doOrderArchiving() {
		int maximumNumberOfArchivableOrders = getConfigBean().getArchiveOrdersCronBatchSize();
		Calendar now = Calendar.getInstance();
		Calendar latestTimeOfRunning = getLatestTimeOfRunning(now);
		Date boundaryDateOfExpiration = getBoundaryDateOfExpirationFromConfiguration();
		boolean isThereAnyArchivableData = true;
		while (now.before(latestTimeOfRunning) && isThereAnyArchivableData) {
			List<ArchivingMetadataEntity> archivableOrders = 
					orderArchivingService.getArchivableOrders(maximumNumberOfArchivableOrders, boundaryDateOfExpiration);
			isThereAnyArchivableData = !archivableOrders.isEmpty();
			boolean wholeBatchFailed = true;
			for(ArchivingMetadataEntity currentOrder : archivableOrders)
			{
				Long orderId = currentOrder.getOrderId();
				LOG.debug("Archive order: " + orderId);
				boolean archived = false;
				try {
					archived = orderArchivingService.archiveOrder(currentOrder);
					wholeBatchFailed = false;
				}
				catch (RuntimeException e) {
					LOG.error("Error while archiving order " + orderId, e);
					//Every runtime exception is wrapped into an EJBException.
					Throwable cause = e.getCause();
					if (cause != null && cause instanceof LogonException) {
						Reason reason = ((LogonException)cause).getReason();
						if(Reason.RES_ERROR_WSCLIENT.equals(reason)) {
							LOG.error("Stopping archiving", e);
							throw e; // we need to stop, this is a connection related problem
						}
						else {
							continue; // don't stop, but archiving wasn't succesful, so don't delete it either.
						}
					}
				}
				
			}
			
			
			if (wholeBatchFailed && isThereAnyArchivableData) {
				//too many orders failed, might be a critical bug.
				LOG.error("A whole batch of orders have failed");
				orderArchivingService.logWholeBatchFailed();
			}
			now = Calendar.getInstance();
		}
		
		//Delete all Deletable order
		orderArchivingService.removeDeletableOrders();
	}

	/**
	 * 
	 */
	public void logWholeBatchFailed() {
		archivingLogStore.wholeBatchFailed();
	}

	/**
	 * @param orderId
	 * @param e
	 */
	public void logUnableToDelete(Long orderId, String message) {
		archivingLogStore.unableToDeleteOrder(orderId, message);
	}

	/**
	 * @param orderId
	 */
	public void logStart(Long orderId) {
		archivingLogStore.startArchiving(orderId);
	}

	/**
	 * wrapper method for REQUIRED transaction
	 * @param maximumNumberOfArchivableOrders
	 * @param boundaryDateOfExpiration
	 * @return
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<ArchivingMetadataEntity> getArchivableOrders(int maximumNumberOfArchivableOrders,
			Date boundaryDateOfExpiration) {
		return archivingMetaDataStore.findDataOfArchivableOrders(maximumNumberOfArchivableOrders, boundaryDateOfExpiration);
	}

	/**
	 * It transmits and archives the given order 
	 * 
	 * @param informationsnOfArchivableOrder Contains the related informations of the archivable order
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean archiveOrder(ArchivingMetadataEntity informationsnOfArchivableOrder) {			
		orderArchivingService.logStart(informationsnOfArchivableOrder.getOrderId());
		Boolean isArchivedSuccessfully = transmitOrderToArchiveService(informationsnOfArchivableOrder);
		Long idOfArchivableOrder = informationsnOfArchivableOrder.getOrderId();
		
		if (isArchivedSuccessfully) {
			orderStore.markOrderAsArchived(idOfArchivableOrder);
			return true;
		}
		
		orderStore.markOrderAsNonArchivable(idOfArchivableOrder);
		return false;
	}
	
	/**
	 * It starts a new transaction and removes the related order from the database 
	 * 
	 * @param informationsnOfArchivableOrder Contains the related informations of the removable order
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void removeOrder(ArchivingMetadataEntity informationsnOfArchivableOrder) {						
			Long idOfRemovableOrder = informationsnOfArchivableOrder.getOrderId();
			orderStore.removeOrder(idOfRemovableOrder);
	}
	
	/**
	 * It starts a new transaction and removes the related order from the database 
	 * 
	 * @param idOfRemovableOrder Id of the removable order
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void removeOrder(Long idOfRemovableOrder) {
			LOG.debug("Delete order: " + idOfRemovableOrder);
			try {
				orderStore.removeOrder(idOfRemovableOrder);
			}
			catch (Exception e) {
				LOG.error("Could not delete order " + idOfRemovableOrder, e);
				orderArchivingService.logUnableToDelete(idOfRemovableOrder, e.getMessage());
				orderStore.markOrderAsDeletionFailed(idOfRemovableOrder);
			}
			
	}
	
	/**
	 * It starts a new transaction and sets the related order's archiving status to DELETABLE
	 * 
	 * @param orderId
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void markOrderAsDeletable(Long orderId) {
		orderStore.markOrderAsDeletable(orderId);
	}
	
	/**
	 * It starts a new transaction and removes all order which has DELETABLE archiving status
	 */
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void removeDeletableOrders() {
		ReadWriteDao<OrderEntity> dao = getDaoProvider().getOrderEntityDao();
		Filter<OrderEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(OrderEntity.ATTR_ARCHIVING_STATUS_CD, Constant.ARCHIVING_STATUS_DELETABLE));
		List<OrderEntity> deletableOrders = dao.find(filter);
		for (OrderEntity order : deletableOrders) {
			Long orderId = order.getOrderId();
			orderArchivingService.removeOrder(orderId);
		}
	}
	
	/**
	 * @param start
	 * @return
	 */
	private Calendar getLatestTimeOfRunning(Calendar now) {
		
		String archiveOrdersCronEndTime = getConfigBean().getArchiveOrdersCronEndTime();
		SimpleDateFormat sdf = new SimpleDateFormat(ARCHIVE_CRON_TIME_FORMAT);
		Date endTime;
		try {
			endTime = sdf.parse(archiveOrdersCronEndTime);
		}
		catch (ParseException e) {
			throw new LogonException("archive_orders_cron_end_time does not have the expected format " +
					ARCHIVE_CRON_TIME_FORMAT, e);
		}
		Calendar timeOfLatestRun = Calendar.getInstance();
		timeOfLatestRun.setTime(endTime);
				
		int hour = timeOfLatestRun.get(Calendar.HOUR_OF_DAY);
		int minutes = timeOfLatestRun.get(Calendar.MINUTE);
		
		timeOfLatestRun.setTime(now.getTime());
		timeOfLatestRun.add(Calendar.HOUR_OF_DAY, hour);
		timeOfLatestRun.add(Calendar.MINUTE, minutes);
	
		return timeOfLatestRun;
	}	

	private Date getBoundaryDateOfExpirationFromConfiguration() {
		final Calendar cal = Calendar.getInstance();
		ConfigBean configBean = getConfigBean();
		int archiveOrderAfterDays = configBean.getArchiveOrdersArchiveOrderAfterDays();
		cal.add(Calendar.DATE, -archiveOrderAfterDays);
		return cal.getTime();
	}


	/**
	 * It initiates the connection and transmits the necessary informations of the archivable order to the related webservice 
	 * 
	 * @param informationsOfArchivableOrder Containes all of the informations that needs to the archiving
	 */
	private  boolean transmitOrderToArchiveService(ArchivingMetadataEntity informationsOfArchivableOrder) {
		ArchivableOrderData archivableOrderData = fetchArchivableDataFromDataBase(informationsOfArchivableOrder);

		ConfigBean configBean = getConfigBean();
		LogonToECMService archivingService = new LogonToECMService(configBean);
		
		Long orderId = archivableOrderData.getOrderId();
		archivingLogStore.transmitDataToArchivingService(orderId, archivableOrderData.toString());
		Integer resultOfCommunication = archivingService.archiveOrder(archivableOrderData);
		
		if (resultOfCommunication != null) {
			archivingLogStore.receivedResponseFromArchivingService(orderId, resultOfCommunication.toString());
			return LogonToECMService.ARCHIVE_ORDER_RESULT_SUCCESS == resultOfCommunication;
		} 
		else {
			archivingLogStore.receivedResponseFromArchivingService(orderId, "NO RESPONSE");
			return UNSUCCESSFUL_COMMUNICATION;
		}		
	} 


	private ArchivableOrderData fetchArchivableDataFromDataBase(ArchivingMetadataEntity orderMetadata) {
		
		Long orderId = orderMetadata.getOrderId();

		ArchivableOrderData archivableOrderData = null;
			ReadOnlyDao<ArchivingPdfEntity> pdfDao = getDaoProvider().getArchivingPdfEntityDao();
			Filter<ArchivingPdfEntity> bizFilter = new FilterImpl<ArchivingPdfEntity>();
			bizFilter.addExpression(Restrictions.eq(ArchivingPdfEntity.ATTR_ORDER_ID, orderId));

			List<ArchivingPdfEntity> pdfList = pdfDao.find(bizFilter);

			ReadOnlyDao<ArchivingOrderLogEntity> orderLogDao = getDaoProvider().getArchivingOrderLogEntityDao();
			Filter<ArchivingOrderLogEntity> docFilter = new FilterImpl<ArchivingOrderLogEntity>();
			docFilter.addExpression(Restrictions.eq(ArchivingOrderLogEntity.ATTR_ORDER_ID, orderId));

			List<ArchivingOrderLogEntity> orderLogList = orderLogDao.find(docFilter);

			archivableOrderData = extractArchivableOrderData(orderMetadata, pdfList, orderLogList);
		return archivableOrderData;
	}

	private ArchivableOrderData extractArchivableOrderData(ArchivingMetadataEntity orderMetadata, List<ArchivingPdfEntity> pdfList, List<ArchivingOrderLogEntity> orderLogList) {

		ArchivableOrderData orderData = new ArchivableOrderData();

		orderData.setDocumentCreationDate(orderMetadata.getDocTs());

		Long orderStatusCd = orderMetadata.getOrderStatusCd();
		Long documentValidity = null;
		if (Constant.ORDER_STATUS_FINISHED == orderStatusCd) {
			documentValidity = Long.valueOf(1);
		}
		else if (Constant.ORDER_STATUS_DELETED == orderStatusCd) {
			documentValidity = Long.valueOf(2);
		}
		
		orderData.setOrderId(orderMetadata.getOrderId());
		orderData.setDocumentValidity(documentValidity);
		orderData.setCompanyCode(orderMetadata.getCompanycodeCd());
		orderData.setSalesOrganization(orderMetadata.getSorgCd());
		orderData.setDivision(orderMetadata.getDivisionCd());
		orderData.setDistributionChannel(orderMetadata.getDistributionChannel());
		orderData.setOrderNumber(orderMetadata.getOrderNumber());
		orderData.setDeliveryNoteNumber(orderMetadata.getLdsDeliveryNoteNo());
		orderData.setSequentialOrderNumber(orderMetadata.getOrderNumberFromSeqUsage());
		orderData.setCustomerOrderNumber(orderMetadata.getCustorderNumber());
		orderData.setShipToSapId(orderMetadata.getShiptoSapId());
		orderData.setShipToSapName(orderMetadata.getShiptoSapName());
		orderData.setSoldToSapId(orderMetadata.getSoldtoSapId());
		orderData.setSoldToSapName(orderMetadata.getSoldtoSapName());
		orderData.setBillToSapId(orderMetadata.getBilltoSapId());
		orderData.setBillToSapName(orderMetadata.getBilltoSapName());
		orderData.setPayerSapId(orderMetadata.getPayerSapId());
		orderData.setPayerSapName(orderMetadata.getPayerSapName());
		orderData.setCommercialCarrierSapId(orderMetadata.getCommSapId());
		orderData.setCommercialCarrierName(orderMetadata.getCommDisplayName());
		orderData.setExecutingCarrierSapId(orderMetadata.getExecSapId());
		orderData.setExecutingCarrierName(orderMetadata.getExecDisplayName());
		orderData.setContractNumber(orderMetadata.getSapContractNo());
		orderData.setContractType(orderMetadata.getSapContractTypeCd());
		orderData.setContractIncoterm(orderMetadata.getIncotermsCd());
		orderData.setDeliveryType(orderMetadata.getDeliveryTypeCd());
		orderData.setPoolName(orderMetadata.getPoolId());
		orderData.setDeliveryDateFrom(orderMetadata.getDeliveryFromDat());
		orderData.setDeliveryDateTo(orderMetadata.getDeliveryToDat());
		orderData.setActualDeliveryDate(orderMetadata.getActualDeliveryDat());
		orderData.setLoadingDate(orderMetadata.getLoadingTs());
		orderData.setDeliveryPlant(orderMetadata.getPlantCd());
		orderData.setShippingPoint(orderMetadata.getSapShippingpointNo());
		orderData.setShippingCondition(orderMetadata.getSapShippingCondCd());

		List<PdfData> pdfDataList = orderData.getPdfData();
		for (ArchivingPdfEntity pdf : pdfList) {
			PdfData pdfData = extractPdfData(pdf);
			pdfDataList.add(pdfData);
		}

		StringBuilder orderLog = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm");
		for (ArchivingOrderLogEntity orderLogEntry : orderLogList) {
			Date orderLogTs = orderLogEntry.getOrderLogTs();
			orderLog.append(sdf.format(orderLogTs));
			orderLog.append(" ");
			orderLog.append(orderLogEntry.getTxt());
			orderLog.append("\n");
		}
		orderData.setOrderLog(orderLog.toString());

		return orderData;
	}

	private PdfData extractPdfData(ArchivingPdfEntity pdf) {
		PdfData pdfData = new PdfData();
		Date docTs = pdf.getDocTs();
		pdfData.setCreationDate(docTs);
		pdfData.setData(pdf.getData());
		
		StringBuilder fileName = new StringBuilder();
		String orderNumber = String.valueOf(pdf.getOrderNumber());
		String outputdocId = String.valueOf(pdf.getOutputdocId());
		fileName.append(orderNumber);
		fileName.append("_");
		fileName.append(outputdocId);
		fileName.append(".pdf");
		pdfData.setFileName(fileName.toString());
		return pdfData;
	}
}
