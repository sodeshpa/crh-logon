/*
 * Author : AdNovum Informatik AG Version Number
 * $Revision$
 * $Date$
 */

package com.crh.logon.business.services.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;
import javax.persistence.NonUniqueResultException;

import com.crh.logon.business.services.beans.DeliveredQtyInSalesUomAndTons;
import com.crh.logon.business.services.beans.UomConversionResult;
import com.crh.logon.business.services.dispatchers.SapOutgoingDataServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.DispokarteServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.SapOutgoingDataServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UomConverterServiceLocalInterface;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.localinterfaces.GoodsMovementServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.utils.AssignmentUtils;
import com.crh.logon.business.services.utils.DbUtils;
import com.crh.logon.business.services.utils.InvoiceBean;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.OrderUtils;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.authorization.Authorizer;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.CreditNoteEntity;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.GoodsMovementEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.MatPlantEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemContractItemEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.OrderTpSapVendorEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.SimpleVehicleEntity;
import com.crh.logon.persistence.entities.common.TpSapVendorEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.common.TruckTrailerHistoryEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.frozen.OrderFinalEntity;
import com.crh.logon.persistence.entities.frozen.OrderItemFinalEntity;
import com.crh.logon.persistence.entities.refdata.CompanycodeBsEntity;
import com.crh.logon.persistence.entities.refdata.CurrencyBsEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.DivisionBsEntity;
import com.crh.logon.persistence.entities.refdata.LangBsEntity;
import com.crh.logon.persistence.entities.refdata.MaterialPackageBsEntity;
import com.crh.logon.persistence.entities.refdata.PackageTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.ReturnOrderReasonBsEntity;
import com.crh.logon.persistence.entities.refdata.SapShippingCondBsEntity;
import com.crh.logon.persistence.entities.refdata.SapTpSapVendorPfBsEntity;
import com.crh.logon.persistence.entities.refdata.SorgBsEntity;
import com.crh.logon.persistence.entities.refdata.TrspZoneBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.WaitingtimeReasonTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapBpSareaEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialDescriptionEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialSareaEntity;
import com.crh.logon.persistence.entities.sap.SapVehicleEntity;
import com.crh.logon.persistence.entities.sap.SapVendorEntity;
import com.crh.logon.persistence.entities.views.BpSareaSofficeSgroupEntity;
import com.crh.logon.persistence.entities.views.SentContractItemQtyEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonConversionException;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.utils.time.DateTimeZoneFactory;
import com.crh.logon.webservices.clients.LogonToSap;
import com.crh.logon.webservices.clients.beans.deliverydocument.PartnerInformationDTO;
import com.crh.logon.webservices.clients.beans.deliverydocument.QualifierExtraDataDTO;
import com.crh.logon.webservices.clients.beans.deliverydocument.SalesDocumentDTO;
import com.crh.logon.webservices.clients.beans.deliverydocument.SalesDocumentItemDTO;
import com.crh.logon.webservices.clients.beans.deliverydocument.TextIdentificationDTO;
import com.crh.logon.webservices.clients.beans.deliverydocument.TextsDTO;
import com.crh.logon.webservices.clients.beans.goodsmovement.GoodsMovementDTO;
import com.crh.logon.webservices.clients.beans.goodsmovement.GoodsMovementItemDTO;
import com.crh.logon.webservices.clients.beans.pricingcall.PricingCallDTO;
import com.crh.logon.webservices.clients.beans.pricingcall.PricingCallDTOHeader;
import com.crh.logon.webservices.clients.beans.pricingcall.PricingCallDTOItem;
import com.crh.logon.webservices.clients.beans.pricingcall.PricingCallDTOItems;
import com.crh.logon.webservices.clients.beans.pricingcall.PricingCallDTOPartner;
import com.crh.logon.webservices.clients.beans.pricingcall.PricingCallDTOPartners;
import com.crh.logon.webservices.clients.beans.saplogisticreport.SapLogisticReportDTO;
import com.crh.logon.webservices.clients.beans.saplogisticreport.SapLogisticReportItemDTO;
import com.crh.logon.webservices.clients.beans.shipmentdocument.ShipmentCentralAddressDTO;
import com.crh.logon.webservices.clients.beans.shipmentdocument.ShipmentDateDTO;
import com.crh.logon.webservices.clients.beans.shipmentdocument.ShipmentDeliveryHeaderDTO;
import com.crh.logon.webservices.clients.beans.shipmentdocument.ShipmentDeliveryItemDTO;
import com.crh.logon.webservices.clients.beans.shipmentdocument.ShipmentDocumentDTO;
import com.crh.logon.webservices.clients.beans.shipmentdocument.ShipmentGeneralTextDTO;
import com.crh.logon.webservices.clients.beans.shipmentdocument.ShipmentStageDTO;
import com.crh.logon.webservices.clients.beans.shipmentdocument.ShipmentTextHeaderDTO;
import com.crh.logon.webservices.clients.beans.transferdeliverydocument.HeaderPartnerInformationDTO;
import com.crh.logon.webservices.clients.beans.transferdeliverydocument.HeaderQualifierExtraDataDTO;
import com.crh.logon.webservices.clients.beans.transferdeliverydocument.HeaderTextIdentificationDTO;
import com.crh.logon.webservices.clients.beans.transferdeliverydocument.HeaderTextsDTO;
import com.crh.logon.webservices.clients.beans.transferdeliverydocument.TransferDeliveryDocumentDTO;
import com.crh.logon.webservices.clients.beans.transferdeliverydocument.TransferDeliveryItemDTO;

/**
 * Service to get the data from LOGON DB in the form of some DTO (Data Transfer
 * Object), which contains fields with SAP names.
 * 
 * Reference:
 * 
 * [1] Sales Document:
 * file:///share/doc/crh/logon_2_0_ebm/spec/tech/sap_interfaces/
 * Specification_Interface_DeliveryDocument_OUT_LRQ_041.docx
 * 
 * [2] Shipment Document:
 * file:///share/doc/crh/logon_2_0_ebm/spec/tech/sap_interfaces/
 * Specification_Interface_Shipment_Document_LRQ_049.docx
 * 
 * [3] Transfer Document:
 * file:///share/doc/crh/logon_2_0_ebm/spec/tech/sap_interfaces/
 * Specification_Interface_TransferDeliveries_OUT_LRQ082.docx
 * 
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class SapOutgoingDataServiceBean extends SapOutgoingDataServiceDispatcher implements SapOutgoingDataServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SapOutgoingDataServiceBean.class);

	public static final String SNDPRN_PREFIX = "LOGON_0";

	/** yyyyMMdd */
	private DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

	/** HHmmss */
	private DateFormat timeFormat = new SimpleDateFormat("HHmmss");

	private final int SALES_ORGANIZATION = 0;

	private final int DISTRIBUTION_CHANNEL = 1;

	private final int DIVISION = 2;

	@EJB private DispokarteServiceLocalInterface dispokarteService;
	@EJB private UomConverterServiceLocalInterface unitOfMeasurementConverterService;
	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;
	@EJB private OrderServiceLocalInterface orderService;
	@EJB private GoodsMovementServiceLocalInterface goodsMovementService;
	
	/**
	 * The maximum length of the quantity field in the SAP sales document.
	 * This is verified in the generated web service stub classes.
	 * A better way would be to somehow have this constant generated directly from
	 * the web service definition, but how?
	 * This constant was introduced as part of the fix for LOGON-5519.
	 */
	private final int SAP_DOCS_QUANTITY_MAX_LENGTH = 15;
	
	private final String RETURN_INFIX = "R";


	public PricingCallDTO getPricingCallDTO(OrderEntity order, List<OrderItemEntity> pricingCallOrderItems) {
		// [[
		PricingCallDTO result = null;
		Object[][] params = {{"order", order}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getPricingCallDTO", params);
		}
		// ]]
		try {
			
			// =========== Order related input values =============
			
			SapBpEntity payerSapBp = order.getPayerSapBp();
			SapBpEntity soldtoSapBp = order.getSoldtoSapBp();
			SapBpEntity billtoSapBp = order.getBilltoSapBp();
			
			SareaEntity sarea = order.getSarea();
			
			SapBpSareaEntity sapBpSarea = fetchSapBpSareasForSapBpAndSarea(soldtoSapBp.getSapBpId(), sarea.getSareaId());

			DivisionBsEntity division = sarea.getDivision();
			String divisionCd = division.getDivisionCd();
			
			String distributionChannel = sarea.getDistributionChannel();
			SorgBsEntity sorg = sarea.getSorg();
			
			String sapAccountGroupCd = sapBpSarea.getCustomerGroup();
			
			Date deliveryToDat = order.getDeliveryToDat();
			
			SapContractEntity sapContract = order.getSapContract();
			
			SapBpEntity shiptoSapBp = order.getShiptoSapBp();
			
			// =========== DTO setup =============
			
			result = new PricingCallDTO();

			// ----------------------- Header setup -----------------------
						
			
			ShiptoEntity shipTo = null;
			Long shiptoSapBpId = shiptoSapBp.getSapBpId();
			
			ReadOnlyDao<ShiptoEntity> shiptoDao = getDaoProvider().getShiptoEntityDao();
			Filter<ShiptoEntity> shiptoFilter = new FilterImpl<ShiptoEntity>();
			shiptoFilter.addExpression(Restrictions.eq(ShiptoEntity.ATTR_SHIPTO_SAP_BP_ID, shiptoSapBpId));
			Authorizer.disableNextAccessRestriction();
			shipTo = shiptoDao.findSingle(shiptoFilter);
			
			BpSareaSofficeSgroupEntity sapBpSareaOfficeEntity = dispokarteService.fetchBpSareaSofficeSgroup(
					shipTo.getShiptoSapBp().getSapBpId(), sarea.getSareaId());
			
			PricingCallDTOHeader header = new PricingCallDTOHeader();
			
			header.setSalesOrg(sorg.getSorgCd());			
			header.setDistributionChannel(distributionChannel);
			header.setDivision(divisionCd);
			header.setSalesOffice(sapBpSareaOfficeEntity.getSofficeCd());
			header.setCustomerGroup(sapAccountGroupCd);

			Date priceDate = deliveryToDat;
			header.setPriceDate(priceDate);
			
			String shippingConditions = getShippingConditions(order);
			header.setShippingConditions(shippingConditions);
			
			String currencyCd = null;
			for (OrderItemEntity orderItem : pricingCallOrderItems) {
				
				SapContractItemEntity sapContractItem = orderItem.getSapContractItem();
				
				// Can't identify currency code based on this order item; probably a service item 
				if (sapContractItem == null) {
					continue;
				}
				
				SapContractEntity sapContractOfCurrentItem = sapContractItem.getSapContract();
				
				// Cannot use OrderItemEntity.getCurrency() as that is only later available in the process
				CurrencyBsEntity currency = sapContractOfCurrentItem.getCurrency();
				
				// If the currency was not yet set
				if (currencyCd == null) {
					currencyCd = currency.getCurrencyCd();
					continue;
				}
				
				// If one of the item has a different currency code we have to abort
				if (!currencyCd.equals(currency.getCurrencyCd()) ) {
					String message = "Not all order items of order ["+order.getOrderNumber()+"] have the same currency code!";
					LOG.error(message);
					throw new LogonException(message, Reason.RES_ERROR_ORDER_DIFFERENT_CURRENCIES, params);
				}
				
			}
			header.setCurrency(currencyCd);
			
			// ----------------------- Partners setup -----------------------

			boolean oneTimeSoldTo = soldtoSapBp.getTreatAsOnetimeFlag();
			boolean oneTimeShipTo = shiptoSapBp.getTreatAsOnetimeFlag();
			
			boolean payerIsOneTimeSoldTo = false;
			boolean payerIsOneTimeShipTo = false;
			
			// Payer = One-time-shipto
			if (oneTimeShipTo && payerSapBp.getSapBpId().equals(shiptoSapBp.getSapBpId())) {
				payerIsOneTimeShipTo = true;
			}
			// Payer = One-time-soldTo
			if (oneTimeSoldTo && payerSapBp.getSapBpId().equals(soldtoSapBp.getSapBpId())) {
				payerIsOneTimeSoldTo = true;
			}
			
			boolean billToIsOneTimeSoldTo = false;
			boolean billToIsOneTimeShipTo = false;
			
			// BillTo = One-time-shipto
			if (oneTimeShipTo && billtoSapBp.getSapBpId().equals(shiptoSapBp.getSapBpId())) {
				billToIsOneTimeShipTo = true;
			}
			// BillTo = One-time-soldTo
			if (oneTimeSoldTo && billtoSapBp.getSapBpId().equals(soldtoSapBp.getSapBpId())) {
				billToIsOneTimeSoldTo = true;
			}
			
			PricingCallDTOPartners partners = new PricingCallDTOPartners();

			List<PricingCallDTOPartner> partnerItemList = new ArrayList<PricingCallDTOPartner>();

			PricingCallDTOPartner partnerSoldToItem = new PricingCallDTOPartner();
			partnerSoldToItem.setPartnerRole("AG");
			partnerSoldToItem.setPartnerNumber(soldtoSapBp.getSapIfcId());
			
			if (oneTimeSoldTo) {
				
				String name = order.getOneTimeSoldtoName();
				String street = order.getOneTimeSoldtoStreetHouseno();
				String city = order.getOneTimeSoldtoCity();
				String postalCode = order.getOneTimeSoldtoPostalCode();
				String country = order.getOneTimeSoldtoCountry();
				
				setPartnerAddressInformationForPricingCallPartner(partnerSoldToItem, name, street, city, postalCode, country);
				
			}

			PricingCallDTOPartner partnerShipToItem = new PricingCallDTOPartner();
			partnerShipToItem.setPartnerRole("WE");
			partnerShipToItem.setPartnerNumber(shiptoSapBp.getSapIfcId());

			if (oneTimeShipTo) {
				
				String name = order.getLnfSiteName();
				String street = order.getLnfSiteStreetHouseNo();
				String city = order.getLnfSiteCity();
				String postalCode = order.getLnfSitePostalCode();
				String country = order.getLnfSiteCountry();
				
				setPartnerAddressInformationForPricingCallPartner(partnerShipToItem, name, street, city, postalCode, country);
				
			}			

			PricingCallDTOPartner partnerBillToItem = new PricingCallDTOPartner();
			partnerBillToItem.setPartnerRole("RE");
			partnerBillToItem.setPartnerNumber(order.getBilltoSapBp().getSapIfcId());
			
			if (billToIsOneTimeShipTo) {
				
				String name = partnerShipToItem.getName1() + partnerShipToItem.getName2() + partnerShipToItem.getName3();
				String street = partnerShipToItem.getStreet();
				String city = partnerShipToItem.getCity();
				String postalCode = partnerShipToItem.getPostalCode();
				String country = partnerShipToItem.getCountry();
				
				setPartnerAddressInformationForPricingCallPartner(partnerBillToItem, name, street, city, postalCode, country);
				
			}
			else if (billToIsOneTimeSoldTo) {
				
				String name = partnerSoldToItem.getName1() + partnerSoldToItem.getName2() + partnerSoldToItem.getName3();
				String street = partnerSoldToItem.getStreet();
				String city = partnerSoldToItem.getCity();
				String postalCode = partnerSoldToItem.getPostalCode();
				String country = partnerSoldToItem.getCountry();
				
				setPartnerAddressInformationForPricingCallPartner(partnerBillToItem, name, street, city, postalCode, country);
				
			}

			PricingCallDTOPartner partnerPayerItem = new PricingCallDTOPartner();
			partnerPayerItem.setPartnerRole("RG");
			partnerPayerItem.setPartnerNumber(payerSapBp.getSapIfcId());
			
			if (payerIsOneTimeShipTo) {
				
				String name = partnerShipToItem.getName1() + partnerShipToItem.getName2() + partnerShipToItem.getName3();
				String street = partnerShipToItem.getStreet();
				String city = partnerShipToItem.getCity();
				String postalCode = partnerShipToItem.getPostalCode();
				String country = partnerShipToItem.getCountry();
				
				setPartnerAddressInformationForPricingCallPartner(partnerPayerItem, name, street, city, postalCode, country);
				
			}
			else if (payerIsOneTimeSoldTo) {
				
				String name = partnerSoldToItem.getName1() + partnerSoldToItem.getName2() + partnerSoldToItem.getName3();
				String street = partnerSoldToItem.getStreet();
				String city = partnerSoldToItem.getCity();
				String postalCode = partnerSoldToItem.getPostalCode();
				String country = partnerSoldToItem.getCountry();
				
				setPartnerAddressInformationForPricingCallPartner(partnerPayerItem, name, street, city, postalCode, country);
				
			}

			partnerItemList.add(partnerSoldToItem);
			partnerItemList.add(partnerShipToItem);
			partnerItemList.add(partnerBillToItem);
			partnerItemList.add(partnerPayerItem);

			if (order.getCommCarrier() != null) {
				PricingCallDTOPartner partnerForwardingAgentItem = new PricingCallDTOPartner();
				partnerForwardingAgentItem.setPartnerRole("SP");
				partnerForwardingAgentItem.setPartnerNumber(order.getCommCarrier().getSapVendor().getSapIfcId());
				partnerItemList.add(partnerForwardingAgentItem);
			}

			partners.setItems(partnerItemList);
			
			// ----------------------- Items setup -----------------------
			PricingCallDTOItems items = new PricingCallDTOItems();
			
			List<PricingCallDTOItem> dtoItemList = new ArrayList<PricingCallDTOItem>();
			
			for (OrderItemEntity orderItem : pricingCallOrderItems) {
				
				PricingCallDTOItem item = new PricingCallDTOItem();
				
				SapContractItemEntity sapContractItem = orderItem.getSapContractItem();
				
				if (sapContractItem != null) {
					PlantBsEntity supplyPlant = sapContractItem.getSupplyPlant();
					if (supplyPlant != null) {
						String plantCd = supplyPlant.getPlantCd();
						item.setPlant(plantCd);
					}
				}
				
				item.setItemNumber(orderItem.getOrderItemNo().toString());
				item.setMaterial(orderItem.getSapMaterial().getSapMaterialIfcNo());
				item.setIncoterms(sapContract.getIncotermsCd());
				item.setDivision(divisionCd);
				
				if (orderService.isCashSales(sapContract, sarea)) {
					BigDecimal targetQuantity = orderItem.getTargetQuantity();
					UomBsEntity targetQuantityUom = orderItem.getTargetQuantityUom();
					if (targetQuantity != null && targetQuantityUom != null) {
						item.setTargetQty(targetQuantity.toString());
						item.setTargetQuantityUom(targetQuantityUom.getUomCd());
					}
				}

				String shippingPointId = getSapShippingpointNo(order);
				item.setShippingPoint(shippingPointId);
				
				final SapMaterialSareaEntity sapMaterialSarea = orderService.fetchSapMaterialSarea(
						orderItem.getSapMaterial(), sarea);
				if (sapMaterialSarea != null) {
					item.setMaterialPricingGroup(sapMaterialSarea.getPricingGroup());
				}

				dtoItemList.add(item);

			}
			
			items.setItems(dtoItemList);
			
			result.setHeader(header);
			result.setPartners(partners);
			result.setItems(items);
			
			return result;
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Fetches SapBpSarea entity by SAP BP id and sales area id from the DB.
	 */
	private SapBpSareaEntity fetchSapBpSareasForSapBpAndSarea(Long sapBpId, Long sareaId) {
		// [[
		SapBpSareaEntity result = null;
		Object[][] params = {{"sapBpId", sapBpId},
							{"sareaId", sareaId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSapBpSareasForSapBpAndSarea", params);
		}
		// ]]
		try {
			List<Long> sareaIds = new ArrayList<Long>();
			sareaIds.add(sareaId);
			
			ReadWriteDao<SapBpSareaEntity> sbsDao = getDaoProvider().getSapBpSareaEntityDao();
			Filter<SapBpSareaEntity> f = sbsDao.createFilter();
			f.addExpression(Restrictions.eq(SapBpSareaEntity.ATTR_SAP_BP_ID, sapBpId));
			f.addExpression(Restrictions.in(SapBpSareaEntity.ATTR_SAREA_ID, sareaIds));
			SapBpSareaEntity sapBpSarea = sbsDao.findSingle(f);
			
			if (sapBpSarea != null) {
				result = sapBpSarea;
			}
			
			return result;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	
	/**
	 * Contains the logic of SAP BW KPI 5. PDA Indicator.
	 * @param e
	 * @return
	 */
	public Boolean pdaInvolvedInOrder(OrderFinalEntity orderFinalEntity) {
		Boolean pdaFlag = false;

		long deliveryTypeCode = orderFinalEntity.getODeliveryTypeCd().longValue();
		//RAIL
		if (	   deliveryTypeCode == Constant.DELIVERY_TYPE_RAIL_DIRECT
				|| deliveryTypeCode == Constant.DELIVERY_TYPE_BIMODAL_RAIL
				//Other delivery types(like blocktrain, or blocktrain pickup, are not considered rail in this context
				) {
			pdaFlag = !orderFinalEntity.getONoPdaOrRailEventsFlag();
		}
		else { //TRUCK
				//If any of these BizEvents are set on the order, there must be a PDA.
			pdaFlag = orderFinalEntity.getBizEnterStTs() != null
					|| orderFinalEntity.getBizEndUnloadTs() != null
					|| orderFinalEntity.getBizStartUnloadTs() != null
					|| orderFinalEntity.getBizLeaveStTs() != null;
		}
		return pdaFlag;
	}
	
	
	/**
	 * Based on an OrderFinalEntity, creates a DTO based on specification,
	 * that will be sent to SAP BW.
	 * @param orderFinalEntityId Id of the OrderFinalEntity the report will based upon.
	 * @return The DTO object that will be sent to SAP.
	 */
	public SapLogisticReportDTO getLogisticReportDocument(Long orderFinalEntityId) {
		
		SapLogisticReportDTO result = new SapLogisticReportDTO();
		//KPI code <  25 --> complex
		//KPI code >= 25 --> simple

		//Comments above KPIs for easier identifying:
		//KPI code,XSD element name

		//Get the orderFinal object based on ID
		DaoProvider daoProvider = getDaoProvider();
		ReadOnlyDao<OrderFinalEntity> orderFinalDao = daoProvider.getOrderFinalEntityDao();
		OrderFinalEntity orderFinalEntity = orderFinalDao.findById(orderFinalEntityId);
		
		ConfigBean configBean = getConfigBean();
		String instance = configBean.getInstance();
		String[] splitted = instance.split("\\.");
		String clusterPrefix;
		if (splitted.length < 2 || splitted[1] == null) {
			// this shouldn't really happen, but wanted to make sure we're prepared for everything
			clusterPrefix = "NA"; // aka Not Available
		} else {
			// As most KPIs where this is used are length restricted we have to truncate to 2 char length
			clusterPrefix = instance.split("\\.")[1].substring(0, 2);
		}

		//Header KPIs
		
		//Complex KPI
		
		//0,LDOC_1
		if(orderFinalEntity.getOOrderNumber() != null){
			result.setOrderNumber(BigInteger.valueOf(orderFinalEntity.getOOrderNumber()));
		}
		
		//0.1,LDOC_2
		if (orderFinalEntity.getoLifexNo() != null) {
			result.setLifexNumber(orderFinalEntity.getoLifexNo());
		}
		
		//1,LFLAG_7
		if(orderFinalEntity.getOCalcLateDeliveryFlag() != null){
			result.setOntimeDeliveryInd(orderFinalEntity.getOCalcLateDeliveryFlag());
		}
		
		//2,LORT_2
		if (orderFinalEntity.getOOrderCreationTypeCd() != null) {
			result.setOrderCreationTypeId(BigInteger.valueOf(orderFinalEntity.getOOrderCreationTypeCd()));
		}
		
		//3,LCYTM_1 
		if(orderFinalEntity.getBizCycleTimeAtSpMin() != null){
			result.setCycleTimeAtShippingPoint(orderFinalEntity.getBizCycleTimeAtSpMin());
		}
		
		//4,LDIST_1
		if (orderFinalEntity.getOTranspDistancePlanM() != null){
			result.setTransportDistance(orderFinalEntity.getOTranspDistancePlanM());
		}
		
		//5,LFLAG_9
		Boolean pdaFlag = pdaInvolvedInOrder(orderFinalEntity);
		result.setPdaOrRailInd(pdaFlag?1:0);
		
		//9,LFLAG_5
		if (orderFinalEntity.getOShortTermOrderFlag() != null) {
			Boolean shortTermOFlag =orderFinalEntity.getOShortTermOrderFlag();
			result.setShorttermOrderInd(shortTermOFlag?1:0);
		}
		
		//10,LDUR_1
		if(orderFinalEntity.getODeliveryNetTimeWindow() != null){
			BigInteger customerDeliveryTimeWindow= BigInteger.valueOf(orderFinalEntity.getODeliveryNetTimeWindow());
			result.setCustomerDeliveryTimeWindow(customerDeliveryTimeWindow);
		}
		
		//11,LDUR_3
		Date stLeave = orderFinalEntity.getBizLeaveStTs();
		Date stEnter = orderFinalEntity.getBizEnterStTs();
		if (stLeave != null && stEnter != null) {
			long leaveMs = stLeave.getTime();
			long enterMs = stEnter.getTime();
			long msDiff = leaveMs - enterMs;
			result.setCycleTimeSTBiz(BigInteger.valueOf(msDiff / 1000 / 60));
		}

		//12,LDUR_4
		if(orderFinalEntity.getBizLoadingTimeMin() != null){
			result.setLoadingTimeMinBiz(BigInteger.valueOf(orderFinalEntity.getBizLoadingTimeMin()));
		}
		
		//13,LDUR_5
		if (orderFinalEntity.getBizUnloadingMin() != null) {
			long unloadingTimeInMinutes = orderFinalEntity.getBizUnloadingMin();
			result.setUnloadingTimeBiz(BigInteger.valueOf(unloadingTimeInMinutes));
		}
		
		//14, LFLAG_1
		if (orderFinalEntity.getODelivTypePlanActual() != null) {
			if (orderFinalEntity.getODelivTypePlanActual()) {
				result.setDeviationInDeliveryType(BigInteger.valueOf(1));
			}
			else {
				result.setDeviationInDeliveryType(BigInteger.valueOf(0));
			}
		}
		
		
		//20, LDUR_2
		if (orderFinalEntity.getActualDrivingTimeMin() != null) {
			result.setDrivingTime(BigInteger.valueOf(orderFinalEntity.getActualDrivingTimeMin()));
		}
		
		//Simple KPIs

		//26,LSPCO_1
		if (orderFinalEntity.getSpCoordLat() != null) {
			String spCoordLat = orderFinalEntity.getSpCoordLat().toString();
			if (spCoordLat.length() > 16) {
				spCoordLat= spCoordLat.substring(0, 16);
			}
			LOG.debug("spCoordLat" + spCoordLat.toString());
			result.setShippingPointCoordinatesLat(spCoordLat);
		}
		
		//27,LSPCO_2
		if (orderFinalEntity.getSpCoordLon() != null) {
			
			String spCoordLon = orderFinalEntity.getSpCoordLon().toString();
			if (spCoordLon.length() > 16) {
				spCoordLon= spCoordLon.substring(0, 16);
			}
			LOG.debug("spCoordLon" + spCoordLon.toString());
			result.setShippingPointCoordinatesLon(spCoordLon);
		}

		//28,LSHCO_1
		if (orderFinalEntity.getWeCoordLat() != null) {
			String weCoordLat = orderFinalEntity.getWeCoordLat().toString();
			if (weCoordLat.length() > 16) {
				weCoordLat= weCoordLat.substring(0, 16);
			}
			LOG.debug("weCoordLat" + weCoordLat.toString());
			result.setShiptoCoordinatesLat(weCoordLat);
		}

		//29,LSHCO_2
		if (orderFinalEntity.getWeCoordLon() != null) {
			String weCoordLon = orderFinalEntity.getWeCoordLon().toString();
			if (weCoordLon.length() > 16) {
				weCoordLon = weCoordLon.substring(0, 16);
			}
			LOG.debug("weCoordLon" + weCoordLon.toString());
			result.setShiptoCoordinatesLon(weCoordLon);
		}
			

		//41,LWAIT_1
		if (orderFinalEntity.getBizWaitingtimeReasonStCd() != null) {
			Long waitingReasonCode = orderFinalEntity.getBizWaitingtimeReasonStCd();
			final WaitingtimeReasonTxEntity waitingTimeReasonTxEntity = referentialDataService.findTxEntity(WaitingtimeReasonTxEntity.class, Locale.ENGLISH, waitingReasonCode);
			String waitingReasonText = waitingTimeReasonTxEntity.getTxt();
			result.setWaitingTimeReasonShipto(waitingReasonText);
		}

		//44,LPOOL_1
		if (orderFinalEntity.getOPoolId() != null) {
			result.setPoolId(clusterPrefix + " " + orderFinalEntity.getOPoolId().toString());//Long -> String
		}

		//45,LTYP_2
		if (orderFinalEntity.getSpShipppointTypeCd() != null) {
			result.setShippingPointTypeId(BigInteger.valueOf(orderFinalEntity.getSpShipppointTypeCd()));
		}

		//46,LTYP_3
		if (orderFinalEntity.getODeliveryTypeCd() != null) {
			result.setDeliveryTypeId(BigInteger.valueOf(orderFinalEntity.getODeliveryTypeCd()));
		}

		//47.0,LDRV_2
		if (orderFinalEntity.getDriverFirstname() != null
				|| orderFinalEntity.getDriverName() != null) {
			result.setTruckDriverName(orderFinalEntity.getDriverFirstname() + " "
					+ orderFinalEntity.getDriverName());
		}

		//47.1,LDRV_1
		if (orderFinalEntity.getODeliveredDriverId() != null) {
			result.setTruckDriverID(clusterPrefix + " " + orderFinalEntity.getODeliveredDriverId().toString());
		}

		//48,LCRE_1
		if (orderFinalEntity.getOCtlCreUidName() != null) {
			result.setDeliveryCreatedBy(orderFinalEntity.getOCtlCreUidName());
		}

		//49,LCRE_2, not null
		Calendar delvieryCreatedOn = Calendar.getInstance();
		delvieryCreatedOn.setTime(orderFinalEntity.getOCtlCreTs());
		result.setDeliveryCreatedOn(delvieryCreatedOn);

		//50,LTYP_1
		if (orderFinalEntity.getWeShiptoTypeCd() != null) {
			result.setShiptoTypeId(BigInteger.valueOf(orderFinalEntity.getWeShiptoTypeCd()));
		}

		//60,LTYP_4
		if (orderFinalEntity.getWagonTypeCd() != null) {
			result.setWagonTypeId(clusterPrefix + " " + orderFinalEntity.getWagonTypeCd().toString());
		}
		
		//62,LSHI_1
		if (orderFinalEntity.getSpRsUicCountryCd() != null) {
			result.setShippingPointRailwayStationCountryCd(BigInteger.valueOf(orderFinalEntity.getSpRsUicCountryCd()));
		}
		
		//63,LSHI_2
		if (orderFinalEntity.getSpRsRailwayStationNo() != null) {
			result.setShippingPointRailwayStationNumber(new BigInteger(orderFinalEntity.getSpRsRailwayStationNo()));
		}

		//64,LSHI_3
		if (orderFinalEntity.getSpRsRailwayStationName() != null) {
			result.setShippingPointRailwayStationName(orderFinalEntity.getSpRsRailwayStationName());
		}

		//65,LG_1,not null
		Calendar deliveryFromCal = Calendar.getInstance();
		deliveryFromCal.setTime(orderFinalEntity.getODeliveryFromDat());
		result.setDeliveryFromDate(deliveryFromCal);

		//66,LG_2,not null
		Calendar deliveryToCal = Calendar.getInstance();
		deliveryToCal.setTime(orderFinalEntity.getODeliveryToDat());
		result.setDeliveryToDate(deliveryToCal);

		//67,LG_3	
		if (orderFinalEntity.getBizEnterSpTs() != null) {
			Calendar enterShippingPointBiz = Calendar.getInstance();
			enterShippingPointBiz.setTime(orderFinalEntity.getBizEnterSpTs());
			result.setEnterShippingPointBiz(enterShippingPointBiz);
		}

		//68,LG_4
		
		if (orderFinalEntity.getBizStartLoadTs() != null) {
			Calendar startLoadingBiz = Calendar.getInstance();
			startLoadingBiz.setTime(orderFinalEntity.getBizStartLoadTs());
			result.setStartLoadingBiz(startLoadingBiz);
		}

		//69,LG_5
		
		if (orderFinalEntity.getBizEndLoadTs() != null) {
			Calendar endLoadingBiz = Calendar.getInstance();
			endLoadingBiz.setTime(orderFinalEntity.getBizEndLoadTs());
			result.setEndLoadingBiz(endLoadingBiz);
		}

		//70,LG_6
		if (orderFinalEntity.getBizLeaveSpTs() != null) {
			Calendar leaveShippingPointBiz = Calendar.getInstance();
			leaveShippingPointBiz.setTime(orderFinalEntity.getBizLeaveSpTs());
			result.setLeaveShippingPointBiz(leaveShippingPointBiz);
		}

		//71,LG_7
		if (orderFinalEntity.getBizTransRailCompanyTs() != null) {
			Calendar transmissionToRailwayCompanyBiz = Calendar.getInstance();
			transmissionToRailwayCompanyBiz.setTime(orderFinalEntity.getBizTransRailCompanyTs());
			result.setTransmissionToRailwayCompanyBiz(transmissionToRailwayCompanyBiz);
		}

		//72,LG_8
		if (orderFinalEntity.getBizEnterStTs() != null) {
			Calendar enterShiptoBiz = Calendar.getInstance();
			enterShiptoBiz.setTime(orderFinalEntity.getBizEnterStTs());
			result.setEnterShiptoBiz(enterShiptoBiz);
		}

		//73,LG_9
		if (orderFinalEntity.getBizStartUnloadTs() != null) {
			Calendar startUnloadBizString = Calendar.getInstance();
			startUnloadBizString.setTime(orderFinalEntity.getBizStartUnloadTs());
			result.setStartUnloadBiz(startUnloadBizString);
		}

		//74,LG_10
		if (orderFinalEntity.getBizEndUnloadTs() != null) {
			Calendar endUnloadBiz = Calendar.getInstance();
			endUnloadBiz.setTime(orderFinalEntity.getBizEndUnloadTs());
			result.setEndUnloadBiz(endUnloadBiz);
		}

		//75,LG_11
		if (orderFinalEntity.getBizLeaveStTs() != null) {
			Calendar leaveShipto = Calendar.getInstance();
			leaveShipto.setTime(orderFinalEntity.getBizLeaveStTs());
			result.setLeaveShiptoBiz(leaveShipto);
		}

		//Item KPIs
		List<SapLogisticReportItemDTO> itemDTOs = new ArrayList<SapLogisticReportItemDTO>();
		for (OrderItemFinalEntity orderItemFinal : orderFinalEntity.getOrderFinalItems()) {
			SapLogisticReportItemDTO itemDTO = new SapLogisticReportItemDTO();
			
			//Complex
			//6,LFLA_10
			if (orderItemFinal.getLdsLoadedFlag() != null) {
				itemDTO.setLoadingWithLdsInd(orderItemFinal.getLdsLoadedFlag() ? 1:0);
			}
			
			//7,LFLAG_2
			if (orderItemFinal.getOiDispocardAvailalbe() != null) {
				itemDTO.setMaintainedDispocardIndicator(orderItemFinal.getOiDispocardAvailalbe()?1:0);
			}
			
			//8,LFLAG_4
			Long siloVerificationTypeId = orderItemFinal.getOiShiptoSiloVerifTypeCd();
			if (siloVerificationTypeId != null) {
				itemDTO.setSiloVerificationTypeId(BigInteger.valueOf(siloVerificationTypeId));
			}
			else {
				// Although the defaulting logic happens at the point of order finalization
				// we still retain it here, because at the point of go-live
				// we will process already finished orders that won't have the default yet
				itemDTO.setSiloVerificationTypeId(BigInteger.valueOf(1));//based on specs
			}
			
			//15,LFLAG_3
			if (orderItemFinal.getOiOrderPlanActual() != null) {
				if (orderItemFinal.getOiOrderPlanActual()) {
					itemDTO.setDeviationInOrderedQty(BigInteger.valueOf(1));
				}
				else {
					itemDTO.setDeviationInOrderedQty(BigInteger.valueOf(0));
				}
			}

			//Simple

			//25,LPLT_1, not null
			itemDTO.setProductionPlantId(orderItemFinal.getOiProductionPlantCd());

			//42.0,LLPT_1
			if (orderItemFinal.getLoadLoadingpointId() != null) {
				itemDTO.setLoadingPointId(clusterPrefix + " "
						+ orderItemFinal.getLoadLoadingpointId().toString());
			}

			//42.1,LLPT_2
			if (orderItemFinal.getLpLpointName() != null) {
				itemDTO.setLoadingPointName(orderItemFinal.getLpLpointName());
			}

			//61,LCON_1
			if (orderItemFinal.getContainerNo() != null) {
				itemDTO.setContainerNumber(orderItemFinal.getContainerNo());
			}

			//76,LITM_1,not null
			itemDTO.setOrderItemNumber(BigInteger.valueOf(orderItemFinal.getOiOrderItemNo()));

			itemDTOs.add(itemDTO);
		}
		result.setReportItemList(itemDTOs);
		//DTO is all set up, time to return it
		return result;
	}

	/**
	 * Collects information related to Shipment Document
	 * Corresponding specification: 
	 * Specification_Interface_Shipment_Document_LRQ_049_V1_16_20130103.pdf
	 * {@link OrderServiceBean#transmitToSap(Long)}).
	 */
	public ShipmentDocumentDTO getShipmentDocument(AssignmentEntity assignment) {
		// [[
		ShipmentDocumentDTO result = null;
		Object[][] params = {{"assignment", assignment}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getShipmentDocument", params);
		}
		// ]]
		try {
			
			final ShipmentDocumentDTO shipmentDocument = new ShipmentDocumentDTO();
			
			String spIndicator = null;
			Long headerDistance = null;
			String unitOfMeasureForDistance = null;
			Long sumWaitingTime = null;
			StringBuilder sumNoteDelivery = new StringBuilder();
			
			List<AssignmentPartEntity> assignmentParts = assignment.getAssignmentParts();
			List<ShipmentStageDTO> shipmentStageDTOs = new ArrayList<ShipmentStageDTO>();
			List<AssignmentPartEntity> orderParts = AssignmentUtils.collectOrderParts(assignmentParts);
			
			AssignmentPartEntity firstOrderPart = orderParts.get(0);
			OrderEntity firstOrder = firstOrderPart.getOrder();
			firstOrder.initialize(); // important for later use of order items

			validateShippingPoint(firstOrder);
			
			// If tour
			if (orderParts.size() >= 2) {
				
				List<AssignmentPartEntity> distanceParts = new ArrayList<AssignmentPartEntity>();
				
				for (AssignmentPartEntity assignmentPartEntity : assignmentParts) {
					if (assignmentPartEntity.getAssignmentPartType().getAssignmentPartTypeCd().equals(Constant.ASSIGNMENT_PART_TYPE_TO)) {
						distanceParts.add(assignmentPartEntity);
					}
				}
				
				Long sum = 0L;
				unitOfMeasureForDistance = Constant.UOM_METER;
				
				// Create ShipmentStageDTOs per SDO
				for (int i = 0; i < orderParts.size(); i++) {
					
					AssignmentPartEntity ap = orderParts.get(i);
					OrderEntity orderEntity = ap.getOrder();
					ShipmentStageDTO shipmentStageDTO = new ShipmentStageDTO();
					
					// Here we map the orderParts[i] to the distancePart[i]
					AssignmentPartEntity distancePart = distanceParts.get(i);
					Long nextDistance = distancePart.getTransportDistanceM();
					
					// If the assignment distance is not available we fallback for the order distance
					// This can be a typical case for rails
					if (nextDistance == null) {
						nextDistance = orderEntity.getTransportDistanceM();
					}
					
					if (nextDistance != null) {
						shipmentStageDTO.setDistance(ObjectUtils.toString(nextDistance, null));
						shipmentStageDTO.setDistanceUom(unitOfMeasureForDistance);
						sum += nextDistance;
					}
					
					Long waitingTime = orderEntity.getExtraCostWaitingTimeMin();
					if (waitingTime != null) {
						if (sumWaitingTime == null) {
							sumWaitingTime = waitingTime;
						}
						else {
							sumWaitingTime += waitingTime;
						}
					}
					
					String shipmentWaitingTime = getWaitingTime(waitingTime);
					shipmentStageDTO.setShipmentWaitingTime(shipmentWaitingTime);
					shipmentStageDTOs.add(shipmentStageDTO);
					
					sumNoteDelivery.append(orderEntity.getNoteDelivery());

				}
				
				headerDistance = sum;
				
				spIndicator = "";
				
			}
			else if (orderParts.size() == 1) {
				Long orderPartDistance = getOrderPartsDistance(firstOrderPart);
				if (orderPartDistance != null) {
					headerDistance = orderPartDistance;
					unitOfMeasureForDistance = Constant.UOM_METER;
				}
				spIndicator = getSpIndicator(firstOrder);
				sumWaitingTime = firstOrder.getExtraCostWaitingTimeMin();
				sumNoteDelivery.append(firstOrder.getNoteDelivery());
			}
			
			shipmentDocument.setShipmentStageList(shipmentStageDTOs);
			
			if (sumWaitingTime != null) {
				String shipmentWaitingTime = getWaitingTime(sumWaitingTime);
				shipmentDocument.setWaitingTime(shipmentWaitingTime);
			}
			
			// Please note before changing this that the algorith below is precisely mentioned in
			// LRQ 049.1 Shipment Document for Multi-drop scenario specification
			String functionCode = "";
			final SareaEntity sarea = firstOrder.getSarea();
			if (sarea != null) {
				final SorgBsEntity sorg = sarea.getSorg();
				if (sorg != null) {
					final CompanycodeBsEntity companyCode = sorg.getCompanycode();
					if (companyCode != null) {
						functionCode = StringUtils.defaultString(companyCode.getCompanycodeCd());
						if (functionCode.length() > 2) {
							functionCode = functionCode.substring(0, 2);
							functionCode += sarea.getDivision().getSegmentCode();
						}
					}
				}
			}
			shipmentDocument.setFunction(functionCode);
			
			String partnerNumberOfSender = getPartnerNumberOfSender();
			shipmentDocument.setPartnerNumberOfSender(partnerNumberOfSender);

			String assignmentNumber = "";
			if (assignment.getAssignmentNumber() != null) {
				assignmentNumber = assignment.getAssignmentNumber().toString(); 
			}
			shipmentDocument.setShipmentNumber(assignmentNumber);
			shipmentDocument.setSpIndicator(spIndicator);
			// Specifically asked in LOGON-5941
			shipmentDocument.setShipmentType("");
			
			//Distance informations
			if (firstOrder.getSarea().getSorg().getSkipSendDistanceSapSd()) {
				shipmentDocument.setDistance("");
				shipmentDocument.setUnitOfMeasureForDistance("");
			} else {
				shipmentDocument.setDistance(ObjectUtils.toString(headerDistance, null));
				shipmentDocument.setUnitOfMeasureForDistance(unitOfMeasureForDistance); // TODO: maybe  will change due to TLGNR_MAP_SAP_UOM_BS table (convertLogonUomToSapUom)
			}
			
			String plannedTotalTimeOfTransportationInDays = null;
			String actualTotalTimeOfShipmentInDays = null;
			AssignmentPartEntity lastAssignmentPart = AssignmentUtils.findAssignmentPartByType(
					assignment, Constant.ASSIGNMENT_PART_TYPE_FINISHED);
			if (lastAssignmentPart != null) {
				// First planned assignment part (pre-loading or loading)
				AssignmentPartEntity firstAssignmentPart = AssignmentUtils.findAssignmentPartByType(assignment,
						Constant.ASSIGNMENT_PART_TYPE_PRE_LOAD);
				if (firstAssignmentPart == null) {
					firstAssignmentPart = AssignmentUtils.findAssignmentPartByType(assignment,
							Constant.ASSIGNMENT_PART_TYPE_LOAD);
				}
				if (firstAssignmentPart != null) {
					Date firstPlannedAssignmentPartDate = firstAssignmentPart.getPlannedStart();
					Date lastPlannedAssignmentPartDate = lastAssignmentPart.getPlannedStart();
					if (firstPlannedAssignmentPartDate != null && lastPlannedAssignmentPartDate != null) {
						plannedTotalTimeOfTransportationInDays = DateTimeUtils.getDayDiff(
								DateTimeUtils.getDateAsCalendar(firstPlannedAssignmentPartDate),
								DateTimeUtils.getDateAsCalendar(lastPlannedAssignmentPartDate)).toString();
					}
					
					Date firstRealAssignmentPartDate = firstAssignmentPart.getRealStart();
					Date lastRealAssignmentPartDate = lastAssignmentPart.getRealStart();
					if (firstRealAssignmentPartDate != null && lastRealAssignmentPartDate != null) {
						actualTotalTimeOfShipmentInDays = DateTimeUtils.getDayDiff(
								DateTimeUtils.getDateAsCalendar(firstRealAssignmentPartDate),
								DateTimeUtils.getDateAsCalendar(lastRealAssignmentPartDate)).toString();
					}
				}
			}
			shipmentDocument.setPlannedTotalTimeOfTransportationInDays(plannedTotalTimeOfTransportationInDays);
			shipmentDocument.setActualTotalTimeOfShipmentInDays(actualTotalTimeOfShipmentInDays);
			
			shipmentDocument.setVersionNumberComponent(ShipmentDocumentDTO.VERSION_NUMBER_COMPONENT_NEW_ENTRY);
			
			final String vehicle;
			TruckEntity deliveredTruck = assignment.getTruck();
			SimpleVehicleEntity deliveredSimpleVehicle = assignment.getSimpleVehicle();
			WagonEntity deliveredWagon = assignment.getWagon();
			if (deliveredTruck != null) {
				vehicle = deliveredTruck.getSapVehicleNo();
			}
			else if (deliveredSimpleVehicle != null) {
				vehicle = deliveredSimpleVehicle.getSapVehicleNo();
			}
			else if (deliveredWagon != null) {
				vehicle = deliveredWagon.getSapVehicleNo();
			}
			else {
				vehicle = null;
			}
			
			shipmentDocument.setVehicle(vehicle);
			
			String shippingConditions = getShippingConditions(firstOrder);
			shipmentDocument.setShippingConditions(shippingConditions);
			
			// ExternalIdentification1
			shipmentDocument.setExternalIdentification1(assignmentNumber);
			
			// ExternalIdentification2
			shipmentDocument.setExternalIdentification2(firstOrder.getLdsDeliveryNoteNo());
			
			String trailer = null;
			OrderItemEntity firstBagOrBulkOrderItem = getFirstBagOrBulkOrderItem(firstOrder);
			if ( firstBagOrBulkOrderItem != null && firstBagOrBulkOrderItem.getDeliveredLoadTrailer() != null) {
				TrailerEntity trailerEntity = firstBagOrBulkOrderItem.getDeliveredLoadTrailer();
				trailer = trailerEntity.getSapVehicleNo();
			}
			shipmentDocument.setTrailer(trailer);
			
			List<ShipmentCentralAddressDTO> shipmentCentralAddressList = new ArrayList<ShipmentCentralAddressDTO>();
			if (firstOrder.getCommCarrier() != null) {
				ShipmentCentralAddressDTO shipmentCentralAddress = createShipmentCentralAddress(
						ShipmentCentralAddressDTO.QUALIFIER_FOR_PARTNER_FUNCTION_FORWARDING_AGENT, firstOrder.getCommCarrier().getSapVendor());
				shipmentCentralAddressList.add(shipmentCentralAddress);
			}
			
			if (firstOrder.getExecCarrier() != null) {
				ShipmentCentralAddressDTO shipmentCentralAddress = createShipmentCentralAddress(
						ShipmentCentralAddressDTO.QUALIFIER_FOR_PARTNER_FUNCTION_CARRIER, firstOrder.getExecCarrier().getSapVendor());
				shipmentCentralAddressList.add(shipmentCentralAddress);
			}
			
			List<OrderTpSapVendorEntity> otsvList = getOrderTpSapVendorList(firstOrder.getOrderId());
			
			for (OrderTpSapVendorEntity otsv : otsvList) {
				SapVendorEntity sapVendor = otsv.getTpSapVendor().getSapVendor();
				SapTpSapVendorPfBsEntity sapTpSapVendorPfBsEntity = otsv.getTpSapVendor().getSapTpSapVendorPf();
				
				ShipmentCentralAddressDTO shipmentCentralAddress = createShipmentCentralAddress(sapTpSapVendorPfBsEntity.getSapTpSapVendorPfCd(), sapVendor);
				shipmentCentralAddressList.add(shipmentCentralAddress);
			}
			shipmentDocument.setShipmentCentralAddressList(shipmentCentralAddressList);
			
			List<ShipmentDateDTO> shipmentDateList = new ArrayList<ShipmentDateDTO>();
			
			ShipmentDateDTO shipmentDate001 = new ShipmentDateDTO();
			shipmentDate001.setIdocQualifierDateShipment(ShipmentDateDTO.IDOC_QUALIFIER_DATE_SHIPMENT_001);
			String plannedStartDate = null;
			
			// Find the first and last unload part's startDate
			AssignmentPartEntity firstUnloadPart = null;
			AssignmentPartEntity lastUnloadPart = null;
			Iterator<AssignmentPartEntity> partIterator = assignmentParts.iterator();
			while (partIterator.hasNext()) {
				AssignmentPartEntity next = partIterator.next();
				if (next.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_UNLOAD) {
					if (firstUnloadPart == null) {
						firstUnloadPart = next;
					}
					lastUnloadPart = next;
				}
			}
			
			if (firstUnloadPart != null) {
				Date plannedStart = firstUnloadPart.getPlannedStart();
				if (plannedStart != null) {
					plannedStartDate = dateFormat.format(plannedStart);
				}
			}
			shipmentDate001.setActualStartExecutionDate(plannedStartDate);
			shipmentDateList.add(shipmentDate001);
			
			ShipmentDateDTO shipmentDate002 = new ShipmentDateDTO();
			shipmentDate002.setIdocQualifierDateShipment(ShipmentDateDTO.IDOC_QUALIFIER_DATE_SHIPMENT_002);
			
			String constraintForActivityStartBasic = dateFormat.format(assignment.getCtlCreTs());
			shipmentDate002.setConstraintForActivityStartBasic(constraintForActivityStartBasic);
			String actualStartExecutionDate = getLastEventTsOfBizevent(firstOrder.getOrderId(),
					Constant.BIZEVENT_TYPE_COMM_CARRIER_ACK);
			shipmentDate002.setActualStartExecutionDate(actualStartExecutionDate);
			shipmentDateList.add(shipmentDate002);
			
			ShipmentDateDTO shipmentDate003 = new ShipmentDateDTO();
			shipmentDate003.setIdocQualifierDateShipment(ShipmentDateDTO.IDOC_QUALIFIER_DATE_SHIPMENT_003);
			
			actualStartExecutionDate = null;
			AssignmentPartEntity firstLoadPart = AssignmentUtils.findAssignmentPartByType(assignment, Constant.ASSIGNMENT_PART_TYPE_LOAD);
			if (firstLoadPart != null) {
				Date realStart = firstLoadPart.getRealStart();
				if (realStart != null) {
					LoadEntity loadEntity = firstLoadPart.getLoad();
					if(loadEntity != null){
						realStart = convertDateToShippingTimeZone(loadEntity.getLoadingpoint(), realStart);
					}
					actualStartExecutionDate = dateFormat.format(realStart);
				}
			}
			shipmentDate003.setActualStartExecutionDate(actualStartExecutionDate);
			
			String actualFinishExecutionDate = null;
			OrderEntity lastUnloadOrder = lastUnloadPart.getOrder();
			LoadEntity latestLoad = getLatestLoad(lastUnloadOrder);
			if (latestLoad != null) {
				Date loadingTs = latestLoad.getLoadingTs();
				if (loadingTs != null) {
					loadingTs = convertDateToShippingTimeZone(latestLoad.getLoadingpoint(), loadingTs);
					actualFinishExecutionDate = dateFormat.format(loadingTs);
				}
			}
			shipmentDate003.setActualFinishExecutionDate(actualFinishExecutionDate);
			shipmentDateList.add(shipmentDate003);
			
			ShipmentDateDTO shipmentDate004 = new ShipmentDateDTO();
			shipmentDate004.setIdocQualifierDateShipment(ShipmentDateDTO.IDOC_QUALIFIER_DATE_SHIPMENT_004);
			actualStartExecutionDate = getLastEventTsOfBizevent(firstOrder.getOrderId(), Constant.BIZEVENT_TYPE_SHIPPING);
			shipmentDate004.setActualStartExecutionDate(actualStartExecutionDate);
			shipmentDateList.add(shipmentDate004);
			
			ShipmentDateDTO shipmentDate005 = new ShipmentDateDTO();
			shipmentDate005.setIdocQualifierDateShipment(ShipmentDateDTO.IDOC_QUALIFIER_DATE_SHIPMENT_005);
			actualStartExecutionDate = getLastEventTsOfBizevent(lastUnloadOrder.getOrderId(),
					Constant.BIZEVENT_TYPE_ARRIVED_AT_LOADINGPOINT);
			shipmentDate005.setActualStartExecutionDate(actualStartExecutionDate);
			shipmentDateList.add(shipmentDate005);
			
			// String IDOC_QUALIFIER_DATE_SHIPMENT_499 intentionally skipped as not in use by spec;
			
			shipmentDocument.setShipmentDateList(shipmentDateList);

			List<ShipmentTextHeaderDTO> shipmentTextHeaderList = new ArrayList<ShipmentTextHeaderDTO>();
			ShipmentTextHeaderDTO shipmentTextHeader = new ShipmentTextHeaderDTO();
			shipmentTextHeader.setTextID(ShipmentTextHeaderDTO.TEXT_ID_0001);
			
			String shiptoSapBpLangCd = null;
			SapBpEntity shiptoSapBp = firstOrder.getShiptoSapBp();
			LangBsEntity shiptoLang = shiptoSapBp.getLang();
			if (shiptoLang != null) {
				shiptoSapBpLangCd = shiptoLang.getLangCd().toUpperCase();
				if (Constant.LANGUAGE_CODE_GERMAN.toUpperCase().equals(shiptoSapBpLangCd)) {
					shiptoSapBpLangCd = ShipmentTextHeaderDTO.LANGUAGE_KEY_GERMAN;
				}
			}
			shipmentTextHeader.setLanguageKey(shiptoSapBpLangCd);
			
			List<ShipmentGeneralTextDTO> shipmentGeneralTextList = new ArrayList<ShipmentGeneralTextDTO>();
			if (StringUtils.isNotEmpty(sumNoteDelivery.toString())) {
				List<String> splittedStringList = splitAndStoreInList(sumNoteDelivery.toString(), ShipmentGeneralTextDTO.TEXT_LINE_MAX_LENGTH);
				for (String splittedString : splittedStringList) {
					ShipmentGeneralTextDTO shipmentGeneralText = new ShipmentGeneralTextDTO();
					shipmentGeneralText.setTextLine(splittedString);
					shipmentGeneralTextList.add(shipmentGeneralText);
				}
			}
			shipmentTextHeader.setShipmentGeneralTextList(shipmentGeneralTextList);
			
			shipmentTextHeaderList.add(shipmentTextHeader);
			shipmentDocument.setShipmentTextHeaderList(shipmentTextHeaderList);
			
			TruckEntity truck = assignment.getTruck();
			if (truck != null) {
				shipmentDocument.setAdditionalText1(truck.getTruckPlateNo());
				// In order to find the trailer (if available) for the truck we can use the first order's delivery date
				// as obviously the trailer was the same for the whole shipment
				TrailerEntity trailerOfTruck = getTrailerOfTruck(firstOrder.getActualDeliveryDat(), truck.getTruckId());
				if (trailerOfTruck != null) {
					shipmentDocument.setAdditionalText2(trailerOfTruck.getTrailerPlateNo());
				}
			}
		
			final WagonEntity wagon = assignment.getWagon();
			final SimpleVehicleEntity simpleVehicle = assignment.getSimpleVehicle();
			if (wagon != null) {
				shipmentDocument.setAdditionalText3(wagon.getWagonNo());
			}
			else if (simpleVehicle != null) {
				shipmentDocument.setAdditionalText3(deliveredSimpleVehicle.getVehicleRegNo());
			}
			
			shipmentDocument.setEtranReturnNumber(firstOrder.getEtranReturn());
			shipmentDocument.setAdditionalText4(firstOrder.getEtranOutbound());
			
			/*
			 * LOGON-4533 Shipment Deliveries IFC: Field TEXT4 should be empty
			 */
			// shipmentDocument.setAdditionalText4(orderNumber);
			
			List<ShipmentDeliveryHeaderDTO> deliveryHeaderDTOs = new ArrayList<ShipmentDeliveryHeaderDTO>();
			
			for (AssignmentPartEntity partEntity : orderParts) {
				
				final OrderEntity iterOrder = partEntity.getOrder();
				
				ShipmentDeliveryHeaderDTO deliveryHeaderDTO = new ShipmentDeliveryHeaderDTO();
			
				final String externalIdentificationOfDeliveryNote = getExternalDeliveryNumber(iterOrder);
				deliveryHeaderDTO.setExternalIdentificationOfDeliveryNote(externalIdentificationOfDeliveryNote);
				
				final String orderNumber = getOrderNumber(iterOrder);
				deliveryHeaderDTO.setSalesAndDistributionDocumentNumber(orderNumber);
	
				final String[] salesOrganizationAndDistributionChannelAndDivision = getSalesOrganizationAndDistributionChannelAndDivision(iterOrder);
				deliveryHeaderDTO.setSalesOrganization(salesOrganizationAndDistributionChannelAndDivision[SALES_ORGANIZATION]);
	
				final ShippingpointEntity shippingpoint = getShippingpoint(firstOrder);
				final String plant = getShippingpointPlantCd(firstOrder);
				List<ShipmentDeliveryItemDTO> shipmentDeliveryItemList = new ArrayList<ShipmentDeliveryItemDTO>();
	
				String deliveredLoadTrailerId = null;
				boolean sameDeliveredLoadTrailerId = true;
				
				final List<OrderItemEntity> orderItems = mapQuantityControlledOrderItems(
						iterOrder.getOrderItems());
				for (OrderItemEntity orderItem : orderItems) {
					
					ShipmentDeliveryItemDTO shipmentDeliveryItem = new ShipmentDeliveryItemDTO();
	
					String itemNumberOfTheSDDocument = getSentOrderItemNo(orderItem);
					shipmentDeliveryItem.setItemNumberOfTheSDDocument(itemNumberOfTheSDDocument);
	
					String materialNumber = getSapMaterialIfcNo(orderItem);
					shipmentDeliveryItem.setMaterialNumber(materialNumber);
	
					shipmentDeliveryItem.setPlant(plant);
					
					final String storageLocation = sapStorageLocation(orderItem, shippingpoint);
					shipmentDeliveryItem.setStorageLocation(storageLocation);
					
					DeliveredQtyInSalesUomAndTons deliveredQtyData = getDeliveredQtyInSalesUomAndTons(
							orderItem, ItemType.STANDARD_ITEM);
					
					String deliveredQtyInSalesUomStr = deliveredQtyData.getDeliveredQtyInSalesUomStr();
					String salesUomStr = deliveredQtyData.getSalesUomStr();
					String weightUnitStr = deliveredQtyData.getWeightUnitStr();
					
					shipmentDeliveryItem.setSalesUom(salesUomStr);
					shipmentDeliveryItem.setWeightUnit(weightUnitStr);
					shipmentDeliveryItem.setDeliveredQtyInSalesUom(deliveredQtyInSalesUomStr);
					
					shipmentDeliveryItem.setDistributionChannel(salesOrganizationAndDistributionChannelAndDivision[DISTRIBUTION_CHANNEL]);
					shipmentDeliveryItem.setDivision(salesOrganizationAndDistributionChannelAndDivision[DIVISION]);
	
					shipmentDeliveryItemList.add(shipmentDeliveryItem);
					
					// This check is specifically mentioned in 'Specification_Interface_Shipment_Document_LRQ_049_V1_13_20121101.pdf'
					if (LogonPackageHandlingUtil.isBag(orderItem) || LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(orderItem)) {
						TrailerEntity deliveredLoadTrailer = orderItem.getDeliveredLoadTrailer();
						if (deliveredLoadTrailer != null) {
							if (deliveredLoadTrailerId == null) {
								deliveredLoadTrailerId = deliveredLoadTrailer.getTrailerId().toString();
							}
							else {
								if (sameDeliveredLoadTrailerId
										&& !deliveredLoadTrailerId.equals(deliveredLoadTrailer.getTrailerId().toString())) {
									LOG.error("Bag and bulk order items have different delivered_load_trailer_id in order [Order ID = "
											+ iterOrder.getOrderId()
											+ ", Order Number = "
											+ iterOrder.getOrderNumber() + "]");
									sameDeliveredLoadTrailerId = false;
								}
							}
						}
					}
				}
				deliveryHeaderDTO.setShipmentDeliveryItemList(shipmentDeliveryItemList);
				
				deliveryHeaderDTOs.add(deliveryHeaderDTO);
	
				if (iterOrder.getExtraCost1() != null) {
					shipmentDocument.setExtraCost1(iterOrder.getExtraCost1().getSapOrderExtraCostCd());
				}
				if (iterOrder.getExtraCost2() != null) {
					shipmentDocument.setExtraCost2(iterOrder.getExtraCost2().getSapOrderExtraCostCd());
				}
				if (iterOrder.getExtraCost3() != null) {
					shipmentDocument.setExtraCost3(iterOrder.getExtraCost3().getSapOrderExtraCostCd());
				}
				if (iterOrder.getExtraCost4() != null) {
					shipmentDocument.setExtraCost4(iterOrder.getExtraCost4().getSapOrderExtraCostCd());
				}
				
			}
			
			shipmentDocument.setDeliveryHeaderList(deliveryHeaderDTOs);
			
			return result = shipmentDocument;
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * Calculates the waiting time based on the following formula:
	 * Format HHHHHHMM, Example for 80 minutes: "00000120"
	 * @param orderEntity - entity which has (or has not) the waiting time
	 * @return formatted result or empty string if waiting not available
	 */
	private String getWaitingTime(Long extraCostWaitingTimeMin) {
		String formatHreMinString = "";
		if (extraCostWaitingTimeMin != null) {
			formatHreMinString = com.crh.logon.utils.StringUtils.
					formatHreMinString(extraCostWaitingTimeMin);
		}
		return formatHreMinString;
	}

	/**
	 * Returns the distance set for the given {@link AssignmentPartEntity}
	 * If not available it returns the distance set on the {@link OrderEntity} field.
	 * <b>Hence this method should only be called for objects where the {@link OrderEntity} field is not null!</b>
	 * @param ap - AssignmentPartEntity
	 */
	private Long getOrderPartsDistance(AssignmentPartEntity ap) {
		if (ap.getTransportDistanceM() == null) {
			OrderEntity order = ap.getOrder();
			return order.getTransportDistanceM();
		}
		return ap.getTransportDistanceM();
	}

	public void sendShipmentDocument(ShipmentDocumentDTO shipmentDocument) {
		LogonToSap.sendShipmentDocument(getConfigBean(), shipmentDocument);
	}

	public void sendGoodsMovement(GoodsMovementDTO goodsMovement) {
		LogonToSap.sendGoodsMovement(getConfigBean(), goodsMovement);
	}
	
	/**
	 * Fill GoodsMovementDTO from GoodsMovementViewEntity
	 * 
	 * @param goodsMvmntViewEntity
	 * @return
	 */
	@AdnTrace
	public GoodsMovementDTO getGoodsMovementDTOfromGoodsMovementEntity(
			GoodsMovementEntity goodsMovementEntity) {
		GoodsMovementDTO result = null;
		GoodsMovementDTO goodsMovement = new GoodsMovementDTO();

		// BLDAT
		goodsMovement.setDocumentDateInDocument(this.getDateToday());

		// BUDAT
		LoadEntity load = goodsMovementEntity.getLoad();
		Date loadingTs = null;
		if (load != null) {
			loadingTs = load.getLoadingTs();
			loadingTs = convertDateToShippingTimeZone(load.getLoadingpoint(), loadingTs);
		}
		/*
		 * FIXME (bartha Nov 23, 2012): A quick fix for LOGON-6015 (avoid NPE when a load is canceled for bulk)
		 * This is just a workaround, the real solution would be to not send fake information
		 * (not the loading date to SAP)
		 */
		if (loadingTs == null) {
			//fallback logic, this should be avoided, according to LOGON-9128
			loadingTs = new Date();
		}
				
		String postingDateInDocument = this.dateToString(loadingTs);
		goodsMovement.setPostingDateInDocument(postingDateInDocument);

		//SNDPRN
		String partnerNumberOfSender = getPartnerNumberOfSender();
		goodsMovement.setPartnerNumberOfSender(partnerNumberOfSender);

		// TCODE
		goodsMovement.setCurrentTransactionCode(GoodsMovementDTO.CURRENT_TRANSACTION_CODE_DEFAULT);

		// USNAM
		goodsMovement.setUserName(GoodsMovementDTO.USER_NAME_DEFAULT);

		// XBLNR_LONG
		String referenceDocumentNumberSap = goodsMovementEntity.getReferenceDocumentNumberSap();
		goodsMovement.setReferenceDocumentNumber(referenceDocumentNumberSap);

		// ITEM
		GoodsMovementItemDTO goodsMovementItem = new GoodsMovementItemDTO();

		// MATNR
		SapMaterialEntity sapMaterial = goodsMovementEntity.getSapMaterial();
		String sapMaterialIfcNo = sapMaterial.getSapMaterialIfcNo();
		goodsMovementItem.setMaterialNumber(sapMaterialIfcNo);

		// WERKS
		String fromPlantCd = goodsMovementEntity.getFromPlant() == null ? null : goodsMovementEntity.getFromPlant().getPlantCd();
		goodsMovementItem.setPlant(fromPlantCd);

		// LGORT
		String fromStorageLoc = goodsMovementEntity.getFromStorageLoc();
		goodsMovementItem.setStorageLocation(fromStorageLoc);

		// BWART
		String movementType = goodsMovementEntity.getMovementType();
		goodsMovementItem.setMovementType(movementType);

		UomBsEntity baseUom = sapMaterial.getBaseUom();
		BigDecimal netQuantity = goodsMovementEntity.getNetQuantity();
		UomBsEntity netQuantityUom = goodsMovementEntity.getNetQuantityUom();

		final UomConversionResult ucr;
		try {
			ucr = unitOfMeasurementConverterService.convert(netQuantity, netQuantityUom, baseUom,
					sapMaterial, null, null, false);
		}
		catch (LogonConversionException e) {
			throw new LogonException(e);
		}

		// ERFMG
		BigDecimal netQuantityInBaseUom = ucr.getQuantity();
		String netQuantityInBaseUomString = netQuantityInBaseUom.toString();
		goodsMovementItem.setQuantityInUnitOfEntry(netQuantityInBaseUomString);

		// ERFME
		String uomCd = baseUom.getUomCd();
		uomCd = convertLogonUomToSapUom(uomCd, goodsMovementEntity);
		goodsMovementItem.setUnitOfEntry(uomCd); //load.getSapMaterial().getBaseUom().getUomCd()

		// UMWRK
		String toPlantCd = goodsMovementEntity.getToPlant() == null ? null : goodsMovementEntity.getToPlant().getPlantCd();
		if (!StringUtils.isEmpty(toPlantCd)) {
			goodsMovementItem.setReceivingPlant(toPlantCd);
		}

		// UMLGO
		String toStorageLoc = goodsMovementEntity.getToStorageLoc();
		if (!StringUtils.isEmpty(toStorageLoc)) {
			goodsMovementItem.setReceivingStorageLocation(toStorageLoc);
		}

		// Adding item (only one though) to the list
		List<GoodsMovementItemDTO> goodsMovementItemList = new ArrayList<GoodsMovementItemDTO>();
		goodsMovementItemList.add(goodsMovementItem);
		goodsMovement.setGoodsMovementItemList(goodsMovementItemList);

		return result = goodsMovement;
	}
	
	/**
	 * Return the date representing now in the following manner 03.11.2012 as
	 * 20121103
	 * 
	 * @return string with the date now
	 */
	private String getDateToday() {
		Date date = Calendar.getInstance().getTime();
		String now = dateToString(date);
		return now;
	}

	/**
	 * Converts a java.util.Date into a string in the following manner
	 * 03.11.2012 as 20121103
	 * 
	 * @param date
	 *            - a java.util.Date
	 * @return string representation of the fiven date
	 */
	private String dateToString(Date date) {
		DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String dateAsString = formatter.format(date);
		return dateAsString;
	}
	
	/**
	 * Decides if the specified partner needed to be filtered out from a document. 
	 * 
	 * see LOGON-4950
	 * Prevent sending invented sold-to / bill-to / payer data to SAP in transfer deliveries and shipment document interface 
	 *  
	 * @return true, if the partner needs to be filtered out, otherwise false. 
	 */
	private boolean partnerNeedsFilteringOut(SapBpEntity partner, OrderEntity order, 
			boolean partnerIsSoldTo, boolean partnerIsBillTo, boolean partnerIsPayer) {
		// [[
		boolean result = false;
		Object[][] params = {{"partner", partner},
							{"order", order},
							{"partnerIsSoldTo", partnerIsSoldTo + ""},
							{"partnerIsBillTo", partnerIsBillTo + ""},
							{"partnerIsPayer", partnerIsPayer + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "partnerNeedsFilteringOut", params);
		}
		// ]]
		try {
			
			boolean needsFintering = false;
			
			// Prevent sending the sold-to / bill-to / payer
			if (partnerIsSoldTo || partnerIsBillTo || partnerIsPayer) {
				
				SapContractEntity sapContract = order.getSapContract();
				
				if (sapContract != null && sapContract.getSapContractType().getContractTypeIsStpoFlag()) {
					/*
					 *  The sold-to's / bill-to's / payer's account group is flagged as
					 *  * invent_soldto_for_shipto_flag,
					 *  * invent_billto_for_shipto_flag or
					 *  * invent_payer_for_shipto_flag respectively.
					 */
					boolean isInventSoldtoForShipto = partner.getAccountGroup().getInventSoldtoForShipto();
					boolean isInventBilltoForShipto = partner.getAccountGroup().getInventBilltoForShipto();
					boolean isInventPayerForShipto = partner.getAccountGroup().getInventPayerForShipto();
				
					if((partnerIsSoldTo && isInventSoldtoForShipto) || 
							(partnerIsBillTo && isInventBilltoForShipto) ||
							(partnerIsPayer && isInventPayerForShipto)) {
						needsFintering = true;
					}
				}
			}
			
			return result = needsFintering;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	@AdnTrace
	public SalesDocumentDTO getSalesDocument(OrderEntity order, List<CreditNoteEntity> creditNoteList) {
		validateShippingPoint(order);

		SalesDocumentDTO result = null;
		SalesDocumentDTO salesDocument = new SalesDocumentDTO();
		
		CreditNoteEntity creditNote = null; 
		
		if (creditNoteList != null && !creditNoteList.isEmpty()) {
			creditNote = creditNoteList.get(0);
		}

		boolean isCreditNote = creditNote != null;

		String currency = null;
		BigDecimal wsOriginalTareMeasureT = null;
		boolean sameWsOriginalTareMeasureT = true;
		BigDecimal wsOriginalGrossMeasureT = null;
		boolean sameWsOriginalGrossMeasureT = true;
		ReturnOrderReasonBsEntity returnReason = null;

		List<OrderItemEntity> sortedOrderItems = OrderUtils.getSortedOrderItems(order);

		for (OrderItemEntity orderItem : sortedOrderItems) {
			// Currency
			String orderItemCurrency = orderItem.getCurrency().getCurrencyCd();
			if (currency == null) {
				currency = orderItemCurrency;
			}
			else {
				if (!currency.equals(orderItemCurrency)) {
					String message = "Order items have different currencies in order [Order ID = " + order.getOrderId()
							+ ", Order Number = " + order.getOrderNumber() + "]";
					LOG.error(message);
					throw new LogonException(message, Reason.RES_ERROR_ORDER_DIFFERENT_CURRENCIES);
				}
			}

			if (returnReason == null && orderItem.getReturnedQuantity() != null && orderItem.getReturnOrderReason() != null &&
					(orderItem.getReturnQuantHandling() == null || !orderItem.getReturnQuantHandling().getReturnQuantHandlingCd().equals(Constant.RETURN_QUANT_HANDLING_NO_ADJUSTMENT))
			) {
				returnReason = orderItem.getReturnOrderReason();
			}
		}

		if (currency != null && currency.length() > SalesDocumentDTO.CURRENCY_MAX_LENGTH) {
			currency = currency.substring(0, SalesDocumentDTO.CURRENCY_MAX_LENGTH);
		}

		final DeliveryTypeBsEntity deliveryType = order.getDeliveryType();
		final Long deliveryTypeCd = deliveryType.getDeliveryTypeCd();
		
		// Only check load on bag/bulk items, ie. skip service items here.
		Collection<OrderItemEntity> materialItems = LogonPackageHandlingUtil.getMaterialItems(order);
		
		if (!LogonUtil.isReturnPickupOrder(deliveryTypeCd)) { // return pickup orders doesn't have load
			for (OrderItemEntity orderItem : materialItems) {
				// EntryWeightOfTruck
				LoadEntity load = orderItem.getLoad();
				if (load == null) {
					throw new LogonException("At least one order item does not have a load assigned.", Reason.RES_ERROR_UNSPECIFIED);
				}
				BigDecimal orderItemLoadWsOriginalTareMeasureT = load.getWsOriginalTareMeasureT();
				if (wsOriginalTareMeasureT == null) {
					wsOriginalTareMeasureT = orderItemLoadWsOriginalTareMeasureT;
				}
				else {
					if (orderItemLoadWsOriginalTareMeasureT == null || sameWsOriginalTareMeasureT
							&& wsOriginalTareMeasureT.compareTo(orderItemLoadWsOriginalTareMeasureT) != 0) {
						LOG.error("Order items have loads with different ws_original_tare_measure_t in order [Order ID = "
								+ order.getOrderId() + ", Order Number = " + order.getOrderNumber() + "]");
						sameWsOriginalTareMeasureT = false;
					}
				}

				// ExitWeightOfTruck
				BigDecimal orderItemLoadWsOriginalGrossMeasureT = load.getWsOriginalGrossMeasureT();
				if (wsOriginalGrossMeasureT == null) {
					wsOriginalGrossMeasureT = orderItemLoadWsOriginalGrossMeasureT;
				}
				else {
					if (orderItemLoadWsOriginalGrossMeasureT == null || sameWsOriginalGrossMeasureT
							&& wsOriginalGrossMeasureT.compareTo(orderItemLoadWsOriginalGrossMeasureT) != 0) {
						LOG.error("Order items have loads with different ws_original_gross_measure_t in order [Order ID = "
								+ order.getOrderId() + ", Order Number = " + order.getOrderNumber() + "]");
						sameWsOriginalGrossMeasureT = false;
					}
				}
			}
		}

		if (!sameWsOriginalTareMeasureT) {
			wsOriginalTareMeasureT = null;
		}
		if (!sameWsOriginalGrossMeasureT) {
			wsOriginalGrossMeasureT = null;
		}

		String function = isCreditNote ? SalesDocumentDTO.FUNCTION_RET : SalesDocumentDTO.FUNCTION_CON;
		salesDocument.setFunction(function);

		String partnerNumberOfSender = getPartnerNumberOfSender();
		salesDocument.setPartnerNumberOfSender(partnerNumberOfSender);

		salesDocument.setCurrency(currency);

		String sapContractNo = getSapContractNo(order);
		salesDocument.setSalesDocumentNumber(sapContractNo);

		String spIndicator = getSpIndicator(order);
		salesDocument.setSpIndicator(spIndicator);

		String shippingConditions = getShippingConditions(order);
		salesDocument.setShippingConditions(shippingConditions); // FIXME: open item in [1]
		
		String orderReason = "";
		if (LogonUtil.isReturnPickupOrder(deliveryTypeCd) || LogonUtil.isReturnDeliveredOrder(deliveryTypeCd)) {
			orderReason = SalesDocumentDTO.ORDER_REASON_VOLUNTARY_RETURN;
		}
		else if (returnReason != null) {
			orderReason = returnReason.getReturnOrderReasonCd();
		}
		salesDocument.setOrderReason(orderReason); // sets return reason code for credit note case if there is one, "" otherwise.
		
		salesDocument.setIncompleteLoading(order.getSapSuppressMinChargeFlag()?Constant.SALES_DOC_CD_INCOMPLETE_LOAD:"");
		
		String shippingType = getShippingType(order);
		salesDocument.setShippingType(shippingType);
		
		String meansOfTransportType = null;
		salesDocument.setMeansOfTransportType(meansOfTransportType); // FIXME: open item in [1]

		/*
		 * see LOGON-4479 Z00001 External Fleet Transport Z00002 Own Fleet
		 * Transport Z00003 No transport relevant Z00004 Correction Flow
		 */
		if (isTransportRelevant(order, salesDocument.getFunction())
				&& !isPickupOrScheduledPickup(order)) {
			if (isOwnFleet(order)) {
				salesDocument.setRoute(SalesDocumentDTO.ROUTE_INTERNAL_SUBCONTRACTED_FLEET);
			}
			else {
				salesDocument.setRoute(SalesDocumentDTO.ROUTE_EXTERNAL_SUBCONTRACTED_FLEET);
			}
		}
		else {
			salesDocument.setRoute(SalesDocumentDTO.ROUTE_NO_TRANSPORT_RELEVANT);
		}
		
		// Net Weight (in tons) & Gross Weight (in tons)
		String allItemsNetWeightInKgStr = "";
		String allItemsGrossWeightInKgStr = "";

		if (!isCreditNote) {
			BigDecimal allItemsNetWeightInKg = BigDecimal.ZERO;

			for (OrderItemEntity orderItem : sortedOrderItems) {
				BigDecimal quantity = null;
				UomBsEntity fromUom = null;

				BigDecimal deliveredQuantity = orderItem.getDeliveredQuantity();
				UomBsEntity deliveredUom = orderItem.getDeliveredUom();
				if (orderItem.getReturnQuantHandling() != null && orderItem.getReturnQuantHandling().getReturnQuantHandlingCd().equals(Constant.RETURN_QUANT_HANDLING_NO_ADJUSTMENT)){
					//special case CRHLOGON-1393 treat as if there would be no return quantity
					deliveredQuantity = getDeliveredPlusReturnedQuantityInBaseUom(orderItem, order);
					deliveredUom = orderItem.getSapMaterial().getBaseUom();
				}

				if (deliveredQuantity != null && deliveredUom != null) {
					quantity = deliveredQuantity;
					fromUom = deliveredUom;
				}
				else {
					BigDecimal loadedQuantity = orderItem.getLoadedQuantity();
					UomBsEntity loadedUom = orderItem.getLoadedUom();

					if (loadedQuantity != null && loadedUom != null) {
						quantity = loadedQuantity;
						fromUom = loadedUom;
					}
				}
				
				if (quantity != null) {
					UomBsEntity toUom = referentialDataService.findBsEntity(UomBsEntity.class, Constant.UOM_KG);
					UomConversionResult uomConversionResult = null;
					SapMaterialEntity sapMaterial = orderItem.getSapMaterial();
					try {
						uomConversionResult = unitOfMeasurementConverterService.convert(quantity, fromUom, toUom, sapMaterial, null, null, false);
						BigDecimal quantityInKg = uomConversionResult.getQuantity();
						allItemsNetWeightInKg = allItemsNetWeightInKg.add(quantityInKg);
					}
					catch (LogonConversionException e) {
						LOG.error("Could not convert material quantity to kg "
								+ "(sapMaterialId = "
								+ sapMaterial.getSapMaterialId() + "): " + e.getMessage());
					}
				}
			}

			allItemsNetWeightInKgStr = allItemsNetWeightInKg.toPlainString();

			OrderItemEntity firstOrderItem = sortedOrderItems.get(0);
			LoadEntity load = firstOrderItem.getLoad();

			if (load != null && load.getWsOriginalGrossMeasureT() != null) {
				
				UomBsEntity toUom = referentialDataService.findBsEntity(UomBsEntity.class, Constant.UOM_KG);
				UomBsEntity fromUom = referentialDataService.findBsEntity(UomBsEntity.class, Constant.UOM_TONS);
				UomConversionResult uomConversionResult = null;
				
				BigDecimal allItemsGrossWeight = load.getWsOriginalGrossMeasureT();
				try {
					uomConversionResult = unitOfMeasurementConverterService.convert(allItemsGrossWeight, fromUom, toUom, null, null, null, false);
					BigDecimal allItemsGrossWeightInKg = uomConversionResult.getQuantity();
					allItemsGrossWeightInKgStr = allItemsGrossWeightInKg.toPlainString();
				}
				catch (LogonConversionException e) {
					LOG.error("Could not convert quantity to kg", e);
				}
				
			}
		}

		// According to LOGON-6168, the net weight must be sent in KG
		salesDocument.setAllItemsNetWeight(allItemsNetWeightInKgStr);
		salesDocument.setAllItemsGrossWeightInKg(allItemsGrossWeightInKgStr);

		salesDocument.setWeightUnit(convertLogonUomToSapUom(Constant.UOM_KG)); // always KG
		salesDocument.setEntryWeightOfTruck(wsOriginalTareMeasureT == null ? "" : wsOriginalTareMeasureT.toString()); // FIXME: open item in [1]
		salesDocument.setExitWeightOfTruck(wsOriginalGrossMeasureT == null ? "" : wsOriginalGrossMeasureT.toString());
		

		final VehicleFinder vehicleFinder = new VehicleFinder(order, true, true, true);
		String sapVehicleNo = vehicleFinder.getSapVehicleNo();
		String licensePlateNumber = vehicleFinder.getLicensePlateNo();

		if ((licensePlateNumber != null) && (licensePlateNumber.length() > SalesDocumentDTO.LICENSE_PLATE_NUMBER_MAX_LENGTH)) {
			licensePlateNumber = licensePlateNumber.substring(0, SalesDocumentDTO.LICENSE_PLATE_NUMBER_MAX_LENGTH);
		}
		
		salesDocument.setSapVehicleNo(sapVehicleNo);
		salesDocument.setLicensePlateNumber(licensePlateNumber);

		List<QualifierExtraDataDTO> qualifierExtraDataList = new ArrayList<QualifierExtraDataDTO>();
		if (order.getDeliveredTrailer() != null) {
			QualifierExtraDataDTO qualifierExtraData = new QualifierExtraDataDTO();
			
			qualifierExtraData.setQualifierType(QualifierExtraDataDTO.QUALIFIER_TYPE_TRAILER_INFORMATION);
			qualifierExtraData.setDataValue1(order.getDeliveredTrailer().getTrailerPlateNo());
			qualifierExtraData.setDataValue2(order.getDeliveredTrailer().getSapVehicleNo());
			
			qualifierExtraDataList.add(qualifierExtraData);
		}
		salesDocument.setQualifierExtraDataList(qualifierExtraDataList);

		String[] salesOrganizationAndDistributionChannelAndDivision = getSalesOrganizationAndDistributionChannelAndDivision(order);
		salesDocument.setSalesOrganization(salesOrganizationAndDistributionChannelAndDivision[SALES_ORGANIZATION]);
		salesDocument.setDistributionChannel(salesOrganizationAndDistributionChannelAndDivision[DISTRIBUTION_CHANNEL]);
		salesDocument.setDivision(salesOrganizationAndDistributionChannelAndDivision[DIVISION]);

		// LOGON-4374: AUART should be safe to be sent as empty, without information loss.
//			String salesOrderType = isCreditNote ? SalesDocumentDTO.SALES_ORDER_TYPE_RETURN_ORDER
//					: SalesDocumentDTO.SALES_ORDER_TYPE_STANDARD_ORDER;
//			salesDocument.setSalesOrderType(salesOrderType); // FIXME: note in [1]
		final String salesOrderType;
		if (LogonUtil.isReturnPalletOnly(deliveryTypeCd)) {	// AUART (sales order type) is specified for return orders in LOGON-6761 / LRQ 018.2
			final SareaEntity salesArea = order.getSarea();
			if (salesArea != null && salesArea.getSorg() != null) {
				salesOrderType = salesArea.getSorg().getSalesdocReturnAuart();
			}
			else {
				final String errMsg = "couldn't determine AUART (sales order type) of return order," +
						" sales area and/or sales organisation is null. orderNumber: " + order.getOrderNumber(); 
				LOG.error(errMsg);
				throw new LogonException(errMsg, Reason.RES_ERROR_UNSPECIFIED);
			}
		}
		else {
			salesOrderType = "";
		}
		salesDocument.setSalesOrderType(salesOrderType);

		String deliveryDate = null;
		Date deliveryToDat = order.getDeliveryToDat();
		if (deliveryToDat != null) {
			deliveryDate = dateFormat.format(deliveryToDat);
		}
		salesDocument.setDeliveryDate(deliveryDate);

		String loadingDate = null;
		LoadEntity relevantLoadForSalesDocument = getRelevantLoadForSalesDocument(order);
		if (relevantLoadForSalesDocument != null) {
			Date loadingTs = relevantLoadForSalesDocument.getLoadingTs();
			if (loadingTs != null) {
				LoadingpointEntity loadingpointEntity = OrderUtils.getLoadingpointFromOrder(order);
				loadingTs = convertDateToShippingTimeZone(loadingpointEntity, loadingTs);
				loadingDate = dateFormat.format(loadingTs);
			}
		}
			
		salesDocument.setLoadingDate(loadingDate);

		String serviceRenderedDate = null;
		String realDeliveryTime = null;
		Date actualDeliveryDat = order.getActualDeliveryDat();
		if (actualDeliveryDat != null) {
			serviceRenderedDate = dateFormat.format(actualDeliveryDat);
			realDeliveryTime = timeFormat.format(actualDeliveryDat);
		}
		salesDocument.setServiceRenderedDate(serviceRenderedDate);
		salesDocument.setRealDeliveryTime(realDeliveryTime);

		String shiptoSapBpLangCd = null;
		List<PartnerInformationDTO> partnerInformationList = new ArrayList<PartnerInformationDTO>(); // FIXME:
																										// open
																										// item
																										// in
																										// [1]
		SapBpEntity[] partners = new SapBpEntity[] { order.getSoldtoSapBp(), order.getShiptoSapBp(),
				order.getBilltoSapBp(), order.getPayerSapBp() };
		String[] partnerFunctions = new String[] { PartnerInformationDTO.PARTNER_FUNCTION_SOLD_TO,
				PartnerInformationDTO.PARTNER_FUNCTION_SHIP_TO, PartnerInformationDTO.PARTNER_FUNCTION_BILL_TO,
				PartnerInformationDTO.PARTNER_FUNCTION_PAYER };
		for (int i = 0; i < partners.length; i++) {
			SapBpEntity partner = partners[i];
			String partnerFunction = partnerFunctions[i];

			PartnerInformationDTO partnerInformation = new PartnerInformationDTO();
			partnerInformation.setPartnerFunction(partnerFunction);

			if (partner != null) {
				
				/* 
				 * LOGON-4950
				 * Prevent sending invented sold-to / bill-to / payer data to SAP in transfer deliveries and shipment document interface
				 */
				boolean partnerIsSoldTo = PartnerInformationDTO.PARTNER_FUNCTION_SOLD_TO.equals(partnerFunction);
				boolean partnerIsBillTo = PartnerInformationDTO.PARTNER_FUNCTION_BILL_TO.equals(partnerFunction);
				boolean partnerIsPayer = PartnerInformationDTO.PARTNER_FUNCTION_PAYER.equals(partnerFunction);
				
				if(partnerNeedsFilteringOut(partner, order, partnerIsSoldTo, partnerIsBillTo, partnerIsPayer)) {
					continue;
				}
				
				partnerInformation.setPartnerNumber(partner.getSapIfcId());

				if (PartnerInformationDTO.PARTNER_FUNCTION_SOLD_TO.equals(partnerFunction)
						&& Boolean.TRUE.equals(partner.getTreatAsOnetimeFlag())) {
					setPartnerInformation(partnerInformation, order.getOneTimeSoldtoName(),
							order.getOneTimeSoldtoStreetHouseno(), order.getOneTimeSoldtoCity(),
							order.getOneTimeSoldtoPostalCode(), order.getOneTimeSoldtoCountry(),
							order.getOneTimeSoldtoVatNo());
				}

				if (PartnerInformationDTO.PARTNER_FUNCTION_BILL_TO.equals(partnerFunction)
						&& Boolean.TRUE.equals(partner.getTreatAsOnetimeFlag())) {
					if (order.getSoldtoSapBp().getSapBpId().equals(partner.getSapBpId())) {
						setPartnerInformation(partnerInformation, order.getOneTimeSoldtoName(),
								order.getOneTimeSoldtoStreetHouseno(), order.getOneTimeSoldtoCity(),
								order.getOneTimeSoldtoPostalCode(), order.getOneTimeSoldtoCountry(),
								order.getOneTimeSoldtoVatNo());
					}
				}

				if (PartnerInformationDTO.PARTNER_FUNCTION_PAYER.equals(partnerFunction)
						&& Boolean.TRUE.equals(partner.getTreatAsOnetimeFlag())) {
					if (order.getSoldtoSapBp().getSapBpId().equals(partner.getSapBpId())) {
						setPartnerInformation(partnerInformation, order.getOneTimeSoldtoName(),
								order.getOneTimeSoldtoStreetHouseno(), order.getOneTimeSoldtoCity(),
								order.getOneTimeSoldtoPostalCode(), order.getOneTimeSoldtoCountry(),
								order.getOneTimeSoldtoVatNo());
					}
				}

				if (PartnerInformationDTO.PARTNER_FUNCTION_SHIP_TO.equals(partnerFunction)
						&& Boolean.TRUE.equals(partner.getTreatAsOnetimeFlag())) {
					setPartnerInformation(partnerInformation, order.getLnfSiteName(), 
							order.getLnfSiteStreetHouseNo(), order.getLnfSiteCity(), 
							order.getLnfSitePostalCode(), order.getLnfSiteCountry(), 
							order.getLnfSiteVatNo());
				}

				if (PartnerInformationDTO.PARTNER_FUNCTION_SHIP_TO.equals(partnerFunction)) {
					LangBsEntity shiptoLang = partner.getLang();
					if (shiptoLang != null) {
						shiptoSapBpLangCd = shiptoLang.getLangCd().toUpperCase();
						if (Constant.LANGUAGE_CODE_GERMAN.toUpperCase().equals(shiptoSapBpLangCd)) {
							shiptoSapBpLangCd = TextIdentificationDTO.LANGUAGE_ISO_GERMAN;
						}
					}
				}

				String transportationZone = null;
				TrspZoneBsEntity trspZone = order.getDeliveryTrspZone();
				if (trspZone != null) {
					transportationZone = trspZone.getTrspZoneCd();
				}
				partnerInformation.setTransportationZone(transportationZone);
			}

			partnerInformationList.add(partnerInformation);
		}

		CarrierEntity forwardingAgent = order.getCommCarrier();
		if (forwardingAgent != null) {
			SapVendorEntity sapVendor = forwardingAgent.getSapVendor();
			if (sapVendor != null) {

				PartnerInformationDTO partnerInformation = new PartnerInformationDTO();
				partnerInformation = new PartnerInformationDTO();
				partnerInformation.setPartnerFunction(PartnerInformationDTO.PARTNER_FUNCTION_FORWARDING_AGENT);
				partnerInformation.setPartnerNumber(sapVendor.getSapIfcId());
				partnerInformationList.add(partnerInformation);
			}
		}
		CarrierEntity executingCarrier = order.getExecCarrier();
		if (executingCarrier != null) {
			SapVendorEntity sapVendor = executingCarrier.getSapVendor();
			if (sapVendor != null) {

				PartnerInformationDTO partnerInformation = new PartnerInformationDTO();
				partnerInformation.setPartnerFunction(PartnerInformationDTO.PARTNER_FUNCTION_CARRIER);
				partnerInformation.setPartnerNumber(sapVendor.getSapIfcId());

				partnerInformationList.add(partnerInformation);
			}
		}

		List<OrderTpSapVendorEntity> otsvList = getOrderTpSapVendorList(order.getOrderId());
		
		for (OrderTpSapVendorEntity otsv : otsvList) {
			SapVendorEntity sapVendor = otsv.getTpSapVendor().getSapVendor();
			SapTpSapVendorPfBsEntity sapTpSapVendorPfBsEntity = otsv.getTpSapVendor().getSapTpSapVendorPf();
			
			PartnerInformationDTO partnerInformation = new PartnerInformationDTO();
			partnerInformation.setPartnerFunction(sapTpSapVendorPfBsEntity.getSapTpSapVendorPfCd());
			partnerInformation.setPartnerNumber(sapVendor.getSapIfcId());

			partnerInformationList.add(partnerInformation);
		}
		
		salesDocument.setPartnerInformationList(partnerInformationList);
		salesDocument.setSapContract(sapContractNo);

		final String externalDeliveryNumber = getExternalDeliveryNumber(order);
		final String dispatchDeliveryNumber = getDispatchDeliveryNumber(order, isCreditNote, creditNote);
		
		salesDocument.setExternalDeliveryNumber(externalDeliveryNumber);
		salesDocument.setDispatchDeliveryNumber(dispatchDeliveryNumber);

		if (isCreditNote) {
			salesDocument.setCancelledDocument(externalDeliveryNumber);
		}

		String customerPurchaseOrder = null;
		CustorderEntity custorder = order.getCustorder();
		if (custorder != null) {
			customerPurchaseOrder = custorder.getCustorderCustrefNo();
		}
		salesDocument.setCustomerPurchaseOrder(customerPurchaseOrder);

		salesDocument.setPurchaseOrderType(""); // always empty // FIXME: open item in [1]
		salesDocument.setLogisticExternalInvoiceNumber("");
		boolean isExportInvoiceContrType = order.getOrderNumberSeqUsage() != null
				&& Constant.EXPORT_INVOICE_SEQ_USAGE_CD.equals(order.getOrderNumberSeqUsage().getSeqUsageCd());
		
		if (isExportInvoiceContrType) {
			salesDocument.setLogisticExternalInvoiceNumber(StringUtils.defaultString(
					order.getOrderNumberFromSeqUsage(), 
					getOrderNumber(order)));
		}
		salesDocument.setSapDelivery(""); // always empty

		String incoterms = null;
		SapContractEntity sapContract = order.getSapContract();
		if (sapContract != null) {
			incoterms = sapContract.getIncotermsCd();
		}
		salesDocument.setIncoterms(incoterms);

		List<TextIdentificationDTO> textIdentificationList = new ArrayList<TextIdentificationDTO>();
		TextIdentificationDTO textIdentification = new TextIdentificationDTO();
		textIdentification.setTextID(TextIdentificationDTO.TEXT_ID_Z003);
		textIdentification.setLanguageISO(shiptoSapBpLangCd);

		List<TextsDTO> textsList = new ArrayList<TextsDTO>();
		String noteDelivery = order.getNoteDelivery();
		if (StringUtils.isNotEmpty(noteDelivery)) {
			List<String> splittedStringList = splitAndStoreInList(noteDelivery, TextsDTO.TEXT_LINE_MAX_LENGTH);
			for (String splittedString : splittedStringList) {
				TextsDTO texts = new TextsDTO();
				texts.setTextLine(splittedString);
				textsList.add(texts);
			}
		}
					
		textIdentification.setTextsList(textsList);
		textIdentificationList.add(textIdentification);

		if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
			OrderItemEntity item = order.getOrderItems().get(0);
			LoadEntity load = item.getLoad();
			
			if (load != null && load.getWsCarrier() != null) {
				TextIdentificationDTO carrierTextId = new TextIdentificationDTO();
				carrierTextId.setTextID(TextIdentificationDTO.TEXT_ID_Z009);
				carrierTextId.setLanguageISO(shiptoSapBpLangCd);
				TextsDTO carrier = new TextsDTO();
				carrier.setTextLine(load.getWsCarrier());
				ArrayList<TextsDTO> texts = new ArrayList<TextsDTO>();
				texts.add(carrier);
				carrierTextId.setTextsList(texts);
				textIdentificationList.add(carrierTextId);
			}
			
			if (load != null && load.getWsTruckPlateNo() != null) {
				TextIdentificationDTO truckTextId = new TextIdentificationDTO();
				truckTextId.setTextID(TextIdentificationDTO.TEXT_ID_Z010);
				truckTextId.setLanguageISO(shiptoSapBpLangCd);
				TextsDTO truckPlateNo = new TextsDTO();
				truckPlateNo.setTextLine(load.getWsTruckPlateNo());
				ArrayList<TextsDTO> texts = new ArrayList<TextsDTO>();
				texts.add(truckPlateNo);
				truckTextId.setTextsList(texts);
				textIdentificationList.add(truckTextId);
			}
		}
		
		if (order.getEtranOutbound() != null) {
			TextIdentificationDTO etranTextIdentification = new TextIdentificationDTO();
			etranTextIdentification.setLanguageISO(shiptoSapBpLangCd);
			etranTextIdentification.setTextID(TextIdentificationDTO.TEXT_ID_Z013);

			List<TextsDTO> etranTextList = new ArrayList<TextsDTO>();
			TextsDTO etranOutboundText = new TextsDTO();
			
			etranOutboundText.setTextLine(order.getEtranOutbound());
			etranTextList.add(etranOutboundText);
			etranTextIdentification.setTextsList(etranTextList);
			textIdentificationList.add(etranTextIdentification);
		}
		
		AssignmentEntity assignment = loadAssignment(order.getOrderId());
		if (assignment != null && assignment.getLdsDriverName() != null) {
			TextIdentificationDTO driverNameTextIdentification = new TextIdentificationDTO();
			driverNameTextIdentification.setLanguageISO(shiptoSapBpLangCd);
			driverNameTextIdentification.setTextID(TextIdentificationDTO.TEXT_ID_Z011);
			TextsDTO driverNameText = new TextsDTO();
			driverNameText.setTextLine(assignment.getLdsDriverName());

			List<TextsDTO> texts = new ArrayList<TextsDTO>();
			texts.add(driverNameText);

			driverNameTextIdentification.setTextsList(texts);
			textIdentificationList.add(driverNameTextIdentification);
		}

		if (assignment != null && assignment.getLdsAttorneyName() != null) {
			TextIdentificationDTO attorneyNameTextIdentification = new TextIdentificationDTO();
			attorneyNameTextIdentification.setLanguageISO(shiptoSapBpLangCd);
			attorneyNameTextIdentification.setTextID(TextIdentificationDTO.TEXT_ID_Z012);
			
			String fieldData = assignment.getLdsAttorneyName(); // length of 64
			if (assignment.getLdsAttorneyStart() != null) {
				// document_header_text_identification has a max length of 70
				// we have to allow space for the whitepsace + 8 length date
				if (fieldData.length() > (70 - 9)) { 
					fieldData = fieldData.substring(0, 61); // first index is inclusive, second index is exclusive
				}
				fieldData +=  " " + dateFormat.format(assignment.getLdsAttorneyStart());
			}
			TextsDTO text = new TextsDTO();
			text.setTextLine(fieldData);
			List<TextsDTO> texts = new ArrayList<TextsDTO>();
			texts.add(text);
			
			attorneyNameTextIdentification.setTextsList(texts);
			textIdentificationList.add(attorneyNameTextIdentification);
		}
		
		salesDocument.setTextIdentificationList(textIdentificationList);

		ShippingpointEntity shippingpoint = order.getShippingpoint();
		String plant = getShippingpointPlantCd(order);

		Boolean freeDeliveryFlag = order.getFreeDeliveryFlag();
		String freeOfChargeIndicator = Boolean.TRUE.equals(freeDeliveryFlag) ? SalesDocumentItemDTO.FREE_OF_CHARGE_INDICATOR_TRUE
				: SalesDocumentItemDTO.FREE_OF_CHARGE_INDICATOR_FALSE;

		String originalDocumentForReturns = null;
		String originalItem = null;
		if (isCreditNote) {
			OrderItemEntity creditNoteOrderItem = creditNote.getOrderItem();
			if (creditNoteOrderItem != null) {
				originalItem = getOrderItemNo(creditNoteOrderItem);

				OrderEntity creditNoteOrder = creditNoteOrderItem.getOrder();
				if (creditNoteOrder != null) {
					originalDocumentForReturns = getExternalDeliveryNumber(creditNoteOrder);
				}
			}
		}
		
		String meansOfTransportId = null;
		if (order.getDeliveredTruck() != null) {
			meansOfTransportId = order.getDeliveredTruck().getTruckPlateNo();
		}
		else if (!LogonUtil.isReturnPickupOrder(deliveryTypeCd)) {	// return pickup orders doesn't have load
			for (OrderItemEntity orderItem : materialItems) {
				if (orderItem.getLoad().getWsTruckPlateNo() != null) {
					meansOfTransportId = orderItem.getLoad().getWsTruckPlateNo();
					break;
				}
			}
		}

		/*
		 * Create a SalesDocumentItemDTO for each order item. For an order
		 * that was not previously sent to SAP, and has an order item with a
		 * return amount, SAP has to know about it, so treat it as a CN, ie.
		 * add a virtual item with delivered quantity set as the return
		 * quantity. (Ticket ADN76973.)
		 *
		 * As part of the Advance Payment (AP) extension (LOGON-6624), we first expand
		 * all order items which point to an Advance Payment contract item and replace them
		 * with "virtual" order items. Furthermore, since we are expanding the list with "virtual"
		 * items we reset the order item position (no) on each order item. We do this by
		 * setting the SENT_ORDER_ITEM_NO on each order item. For non-Advance Payment
		 * order items, we need this value set anyway so that it gets persisted to the DB
		 * when our session context closes. For the "virtual" order items, we collect
		 * these values in the end and set them in the special table TLGNC_ORDER_ITEM_CONTRACT_ITEM
		 */
		List<SalesDocumentItemDTO> salesDocumentItemList = new ArrayList<SalesDocumentItemDTO>();

		CreateSalesDocumentItemParams csdiParams = new CreateSalesDocumentItemParams();
		csdiParams.setPlant(plant);
		csdiParams.setShippingpoint(shippingpoint);
		csdiParams.setFreeOfChargeIndicator(freeOfChargeIndicator);
		csdiParams.setMeansOfTransportId(meansOfTransportId);
		
		/*
		 * In case of a credit note, only the order item the credit note is based
		 * on will be sent. See LOGON-6488.
		 */
		final List<OrderItemEntity> relevantOrderItems;
		if (isCreditNote) {
			if(creditNoteList != null && !creditNoteList.isEmpty()) {
				relevantOrderItems = new ArrayList<OrderItemEntity>();
				for(CreditNoteEntity creditNoteListItem : creditNoteList) {
					relevantOrderItems.addAll(expandOrderItemForCreditNote(creditNoteListItem.getOrderItem()));
				}
			} else {
				relevantOrderItems = expandOrderItemForCreditNote(creditNote.getOrderItem());
			}
		}
		else {
			relevantOrderItems = expandQuantityControlledOrderItems(sortedOrderItems);
		}
		
		for (OrderItemEntity orderItem : relevantOrderItems) {
			csdiParams.setOrderItem(orderItem);
			ItemType itemType;

			if (isCreditNote) {
				itemType = ItemType.CREDIT_NOTE_ITEM;
				csdiParams.setOriginalDocumentForReturns(originalDocumentForReturns);
				csdiParams.setOriginalItem(originalItem);
			}
			else if (orderItem.getReturnedQuantity() == null ||
					orderItem.getReturnedQuantity().equals(BigDecimal.ZERO)) {
				itemType = ItemType.STANDARD_ITEM;
				csdiParams.setOriginalDocumentForReturns(null);
				csdiParams.setOriginalItem(null);
			}
			else if (orderItem.getReturnQuantHandling() != null && orderItem.getReturnQuantHandling().getReturnQuantHandlingCd().equals(Constant.RETURN_QUANT_HANDLING_NO_ADJUSTMENT)){
				itemType = ItemType.REAL_ITEM_WITH_RETURN_QUANTITY_NO_ADJUSTMENT;
				csdiParams.setOriginalDocumentForReturns(null);
				csdiParams.setOriginalItem(null);
			}
			else {
				itemType = ItemType.REAL_ITEM_WITH_RETURN_QUANTITY;
				csdiParams.setOriginalDocumentForReturns(null);
				csdiParams.setOriginalItem(null);
			}

			csdiParams.setItemType(itemType);
			csdiParams.setIsQuantityControlledContract(isQuantityControlledContractItem(orderItem));

			ShippingpointEntity returnShippingpoint = getReturnShippingpoint(orderItem);
			if (returnShippingpoint != null && returnShippingpoint.getPlant() != null) {
				csdiParams.setReturnPlant(returnShippingpoint.getPlant().getPlantCd());
				csdiParams.setReturnShippingpoint(returnShippingpoint);
			}
			else {
				csdiParams.setReturnPlant(null);
				csdiParams.setReturnShippingpoint(null);
			}
			
			SalesDocumentItemDTO salesDocumentItem = createSalesDocumentItem(csdiParams);
			salesDocumentItemList.add(salesDocumentItem);

			if (itemType == ItemType.REAL_ITEM_WITH_RETURN_QUANTITY) {
				itemType = ItemType.VIRTUAL_RETURN_ITEM;
				csdiParams.setItemType(itemType);
				salesDocumentItem = createSalesDocumentItem(csdiParams);
				salesDocumentItemList.add(salesDocumentItem);
			}
		}
		salesDocument.setSalesDocumentItemList(salesDocumentItemList);

		//--- Create new TLGNC_ORDER_ITEM_CONTRACT_ITEM entities for Advance Payment virtual items ---//
		//--- Updated total sent quantity on the group leader for all order items ---//
		if (!isCreditNote) {
			updateOrderItemSentPosition(relevantOrderItems);
			updateTotalSentQuantityOnOrderItems(sortedOrderItems);
		}

		return result = salesDocument;
	}

	public void sendSalesDocument(SalesDocumentDTO salesDocument) {

		Long startTime = new Date().getTime();
		LogonToSap.sendSalesDocument(getConfigBean(), salesDocument);
		Long endTime = new Date().getTime();
		LOG.debug("Sending took" + (endTime-startTime)+ "ms" + salesDocument.getSalesDocumentNumber());
	}

	/**
	 * Returns an outbound {@link TransferDeliveryDocumentDTO} transfer delivery
	 * document. There are a few fields which differ for the inbound and
	 * outbound documents, which are set within the function body.
	 * 
	 * Specializes a transferdeliverydocument constructed in
	 * {@link #getTransferDeliveryDocumentCommon(OrderEntity)} to become an
	 * outbound transfer delivery document.
	 * 
	 * Business background: transfer documents are only sent in case of STPO
	 * (stock transfer purchase order) and SA (scheduling agreement) contracts.
	 * (For these the tlgnr_sap_contract_type.contract_type_is_stpo_flag is on).
	 * 
	 * In transfer cases there is an issuing part, and a receiving part in SAP
	 * for the order.
	 * 
	 * Issueing plant (outbound): the shippingpoint's plant Receiving (inbound)
	 * plant: the shipto's plant
	 * 
	 */
	public TransferDeliveryDocumentDTO getTransferDeliveryDocumentOutbound(OrderEntity order, CreditNoteEntity creditNote) {
		// [[
		TransferDeliveryDocumentDTO result = null;
		Object[][] params = { { "order", order }, { "creditNote", creditNote } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getTransferDeliveryDocumentOutbound", params);
		}
		// ]]
		try {

			final TransferDeliveryDocumentDTO outboundTransferDeliveryDocument = getTransferDeliveryDocumentCommon(order,
					creditNote, true);
			final boolean isCreditNote = creditNote != null;

			if (isCreditNote) {
				outboundTransferDeliveryDocument.setFunction(TransferDeliveryDocumentDTO.FUNCTION_OUTBOUND_TRANSFER_RETURN);
			}
			else {
				outboundTransferDeliveryDocument.setFunction(TransferDeliveryDocumentDTO.FUNCTION_OUTBOUND_DELIVERY);
			}

			outboundTransferDeliveryDocument.setDeliveryType(TransferDeliveryDocumentDTO.DELIVERY_TYPE_OUTBOUND_DELIVERY);

			//Route
			if (isTransportRelevant(order, outboundTransferDeliveryDocument.getFunction())
					&& !isPickupOrScheduledPickup(order)) {
				if (isOwnFleet(order)) {
					outboundTransferDeliveryDocument.setRoute(TransferDeliveryDocumentDTO.ROUTE_INTERNAL_SUBCONTRACTED_FLEET);
				}
				else {
					outboundTransferDeliveryDocument.setRoute(TransferDeliveryDocumentDTO.ROUTE_EXTERNAL_SUBCONTRACTED_FLEET);
				}
			}
			else {
				outboundTransferDeliveryDocument.setRoute(TransferDeliveryDocumentDTO.ROUTE_NO_TRANSPORT_RELEVANT);
			}
			
			String plant = getShippingpointPlantCd(order);

			for (TransferDeliveryItemDTO transferDeliveryItem : outboundTransferDeliveryDocument.getTransferDeliveryItemList()) {
				transferDeliveryItem.setPlant(plant);
			}

			String partnerNumberOfSender = getPartnerNumberOfSender();
			outboundTransferDeliveryDocument.setPartnerNumberOfSender(partnerNumberOfSender);
			
			return result = outboundTransferDeliveryDocument;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	/**
	 * Returns an inbound {@link TransferDeliveryDocumentDTO} transfer delivery
	 * document. There are a few fields which differ for the inbound and
	 * outbound documents, which are set within the function body.
	 * 
	 * Specializes a transferdeliverydocument constructed in
	 * {@link #getTransferDeliveryDocumentCommon(OrderEntity)} to become an
	 * inbound transfer delivery document.
	 * 
	 * Business background: transfer documents are only sent in case of STPO
	 * (stock transfer purchase order) and SA (scheduling agreement) contracts.
	 * (For these the tlgnr_sap_contract_type.contract_type_is_stpo_flag is on).
	 * 
	 * In transfer cases there is an issuing part, and a receiving part in SAP
	 * for the order.
	 * 
	 * Issueing plant (outbound): the shippingpoint's plant
	 * Receiving plant (inbound): the shipto's plant
	 */
	public TransferDeliveryDocumentDTO getTransferDeliveryDocumentInbound(OrderEntity order, CreditNoteEntity creditNote) {
		// [[
		TransferDeliveryDocumentDTO result = null;
		Object[][] params = { { "order", order }, { "creditNote", creditNote } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getTransferDeliveryDocumentInbound", params);
		}
		// ]]
		try {

			final TransferDeliveryDocumentDTO inboundTransferDeliveryDocument = getTransferDeliveryDocumentCommon(order,
					creditNote, false);
			final boolean isCreditNote = creditNote != null;

			if (isCreditNote) {
				inboundTransferDeliveryDocument.setFunction(TransferDeliveryDocumentDTO.FUNCTION_INBOUND_TRANSFER_RETURN);
			}
			else {
				inboundTransferDeliveryDocument.setFunction(TransferDeliveryDocumentDTO.FUNCTION_INBOUND_DELIVERY);
			}

			inboundTransferDeliveryDocument.setDeliveryType(TransferDeliveryDocumentDTO.DELIVERY_TYPE_INBOUND_DELIVERY);

			/*
			 * "LF" Vendor (tlgns_sap_contract.vendor_sap_no). We decided not to
			 * check contract type (whether it is STPO or SA).
			 */
			SapContractEntity sapContract = order.getSapContract();
			String vendorSapNo = sapContract.getVendorSapNo();

			if (StringUtils.isNotEmpty(vendorSapNo)) {
				List<HeaderPartnerInformationDTO> headerPartnerInformationList = inboundTransferDeliveryDocument.getHeaderPartnerInformationList();

				HeaderPartnerInformationDTO headerPartnerInformation = new HeaderPartnerInformationDTO();
				headerPartnerInformation.setQualifierForPartnerFunction(HeaderPartnerInformationDTO.QUALIFIER_FOR_PARTNER_FUNCTION_VENDOR);
				headerPartnerInformation.setPartnerNoSAP(vendorSapNo);
				headerPartnerInformationList.add(headerPartnerInformation);
			}
			
			//Route
			if (isTransportRelevant(order, inboundTransferDeliveryDocument.getFunction())
					&& isPickupOrScheduledPickup(order)) {
				if (isOwnFleet(order)) {
					inboundTransferDeliveryDocument.setRoute(TransferDeliveryDocumentDTO.ROUTE_INTERNAL_SUBCONTRACTED_FLEET);
				}
				else {
					inboundTransferDeliveryDocument.setRoute(TransferDeliveryDocumentDTO.ROUTE_EXTERNAL_SUBCONTRACTED_FLEET);
				}
			}
			else {
				inboundTransferDeliveryDocument.setRoute(TransferDeliveryDocumentDTO.ROUTE_NO_TRANSPORT_RELEVANT);
			}			

			DaoProvider daoProvider = getDaoProvider();
			ReadOnlyDao<ShiptoEntity> shiptoDao = daoProvider.getShiptoEntityDao();
			Filter<ShiptoEntity> shiptoFilter = shiptoDao.createFilter();
			SapBpEntity shiptoSapBusinessPartner = order.getShiptoSapBp();
			Long sapBusinessPartnerId = shiptoSapBusinessPartner.getSapBpId();
			shiptoFilter.addExpression(
					Restrictions.eq(ShiptoEntity.ATTR_SHIPTO_SAP_BP_ID, sapBusinessPartnerId)
			);
			ShiptoEntity shipTo = shiptoDao.findSingle(shiptoFilter);
			PlantBsEntity shipToPlant = shipTo.getPlant();
			for (TransferDeliveryItemDTO transferDeliveryItem : inboundTransferDeliveryDocument.getTransferDeliveryItemList()) {
				Authorizer.disableNextAccessRestriction();

				if (shipToPlant != null) {
					String shipToPlantCode = shipToPlant.getPlantCd();
					transferDeliveryItem.setPlant(shipToPlantCode);
				}
				else {
					LOG.error("No plant_cd is specified for the shipto. /transfer_delivery_creation/item_general_data/WERKS field is going to be left empty in the inbound transfer delivery document. [ShiptoId="
							+ shipTo.getShiptoId()
							+ ";ShiptoSapId="
							+ shipTo.getShiptoSapBp().getSapId()
							+ ";orderNr="
							+ order.getOrderNumber() + ";orderId=" + order.getOrderId() + "]");
				}
			}

			String partnetNumberOfSender = getPartnerNumberOfSender();
			inboundTransferDeliveryDocument.setPartnerNumberOfSender(partnetNumberOfSender);

			return result = inboundTransferDeliveryDocument;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}


	/**
	 * 
	 * Returns a {@link TransferDeliveryDocumentDTO} transfer delivery document,
	 * with all the non-specialized fields set (common for both in and outbound
	 * transfer delivery document types). As a later step the returned transfer
	 * delivery document is going to be specialized to become inbound or
	 * outbound transfer delivery document by setting specialized fields.
	 * 
	 * @param order
	 *          the order for which the common fields will be determined
	 * @param creditNote
	 * 			{@link CreditNoteEntity}
	 * @param isInboundTransfer
	 * 			<code>true</code> If LFART=NL (outbound transfer delivery),
	 * 			otherwise <code>false</code>
	 * @return
	 */
	private TransferDeliveryDocumentDTO getTransferDeliveryDocumentCommon(OrderEntity order,
			CreditNoteEntity creditNote, boolean isOutboundTransfer) {

		validateShippingPoint(order);

		final boolean isCreditNote = creditNote != null;
		final TransferDeliveryDocumentDTO transferDeliveryDocument = new TransferDeliveryDocumentDTO();

		String[] salesOrganizationAndDistributionChannelAndDivision = getSalesOrganizationAndDistributionChannelAndDivision(order);
		transferDeliveryDocument.setSalesOrganization(salesOrganizationAndDistributionChannelAndDivision[SALES_ORGANIZATION]);
		transferDeliveryDocument.setDistributionChannel(salesOrganizationAndDistributionChannelAndDivision[DISTRIBUTION_CHANNEL]);
		transferDeliveryDocument.setDivision(salesOrganizationAndDistributionChannelAndDivision[DIVISION]);

		String sapShippingpointNo = getSapShippingpointNo(order);
		transferDeliveryDocument.setShippingPointReceivingPoint(sapShippingpointNo);

		String sapContractNo = getSapContractNo(order);
		transferDeliveryDocument.setStpoNumber(sapContractNo);

		final String externalDeliveryNumber = getExternalDeliveryNumber(order);
		final String dispatchDeliveryNumber = getDispatchDeliveryNumber(order, isCreditNote, creditNote);
		
		transferDeliveryDocument.setExternalDeliveryNumber(externalDeliveryNumber);
		transferDeliveryDocument.setDispatchDeliveryNumber(dispatchDeliveryNumber);

		String spIndicator = getSpIndicator(order);
		transferDeliveryDocument.setSpIndicator(spIndicator);

		List<HeaderQualifierExtraDataDTO> headerQualifierExtraDataList = new ArrayList<HeaderQualifierExtraDataDTO>();

		if (order.getDeliveredTrailer() != null) {
			HeaderQualifierExtraDataDTO headerQualifierExtraData = new HeaderQualifierExtraDataDTO();
			
			headerQualifierExtraData.setQualifierType(QualifierExtraDataDTO.QUALIFIER_TYPE_TRAILER_INFORMATION);
			headerQualifierExtraData.setDataValue1(order.getDeliveredTrailer().getTrailerPlateNo());
			headerQualifierExtraData.setDataValue2(order.getDeliveredTrailer().getSapVehicleNo());
			
			headerQualifierExtraDataList.add(headerQualifierExtraData);
		}

		if (isCreditNote) {
			HeaderQualifierExtraDataDTO headerQualifierExtraData = new HeaderQualifierExtraDataDTO();
			headerQualifierExtraData.setQualifierType(HeaderQualifierExtraDataDTO.QUALIFIER_TYPE_RETURNED_ORDER_NO);
			headerQualifierExtraData.setDataValue1(externalDeliveryNumber);
			
			headerQualifierExtraDataList.add(headerQualifierExtraData);
		}

		transferDeliveryDocument.setHeaderQualifierExtraDataList(headerQualifierExtraDataList);

		List<HeaderTextIdentificationDTO> headerTextIdentificationList = new ArrayList<HeaderTextIdentificationDTO>();
		HeaderTextIdentificationDTO headerTextIdentification = new HeaderTextIdentificationDTO();
		headerTextIdentification.setTextID(HeaderTextIdentificationDTO.TEXT_ID_ZS01);

		String shiptoSapBpLangCd = null, shiptoSapBpLangCdToSend = null;
		SapBpEntity shiptoSapBp = order.getShiptoSapBp();
		LangBsEntity shiptoLang = shiptoSapBp.getLang();
		if (shiptoLang != null) {
			shiptoSapBpLangCd = shiptoLang.getLangCd();
			shiptoSapBpLangCdToSend = shiptoSapBpLangCd.toUpperCase();
			if (Constant.LANGUAGE_CODE_GERMAN.toUpperCase().equals(shiptoSapBpLangCdToSend)) {
				shiptoSapBpLangCdToSend = HeaderTextIdentificationDTO.LANGUAGE_KEY_GERMAN;
			}
		}
		headerTextIdentification.setLanguageKey(shiptoSapBpLangCdToSend);

		List<HeaderTextsDTO> headerTextsList = new ArrayList<HeaderTextsDTO>();
		String noteDelivery = order.getNoteDelivery();
		if (StringUtils.isNotEmpty(noteDelivery)) {
			List<String> splittedStringList = splitAndStoreInList(noteDelivery, HeaderTextsDTO.TEXT_LINE_MAX_LENGTH);
			for (String splittedString : splittedStringList) {
				HeaderTextsDTO headerTexts = new HeaderTextsDTO();
				headerTexts.setTextLine(splittedString);
				headerTextsList.add(headerTexts);
			}
		}

		headerTextIdentification.setHeaderTextsList(headerTextsList);

		headerTextIdentificationList.add(headerTextIdentification);
		transferDeliveryDocument.setHeaderTextIdentificationList(headerTextIdentificationList);

		List<HeaderPartnerInformationDTO> headerPartnerInformationList = new ArrayList<HeaderPartnerInformationDTO>();
		SapBpEntity[] partners = new SapBpEntity[] { order.getSoldtoSapBp(), order.getShiptoSapBp(), order.getBilltoSapBp(),
				order.getPayerSapBp() };
		String[] qualifierForPartnerFunctions = new String[] {
				HeaderPartnerInformationDTO.QUALIFIER_FOR_PARTNER_FUNCTION_SOLD_TO,
				HeaderPartnerInformationDTO.QUALIFIER_FOR_PARTNER_FUNCTION_SHIP_TO,
				HeaderPartnerInformationDTO.QUALIFIER_FOR_PARTNER_FUNCTION_BILL_TO,
				HeaderPartnerInformationDTO.QUALIFIER_FOR_PARTNER_FUNCTION_PAYER };
		for (int i = 0; i < partners.length; i++) {
			SapBpEntity partner = partners[i];
			String qualifierForPartnerFunction = qualifierForPartnerFunctions[i];
			if (partner != null) {
				
				/* 
				 * LOGON-4950
				 * Prevent sending invented sold-to / bill-to / payer data to SAP in transfer deliveries and shipment document interface
				 */
				boolean partnerIsSoldTo = HeaderPartnerInformationDTO.QUALIFIER_FOR_PARTNER_FUNCTION_SOLD_TO.equals(qualifierForPartnerFunction);
				boolean partnerIsBillTo = HeaderPartnerInformationDTO.QUALIFIER_FOR_PARTNER_FUNCTION_BILL_TO.equals(qualifierForPartnerFunction);
				boolean partnerIsPayer = HeaderPartnerInformationDTO.QUALIFIER_FOR_PARTNER_FUNCTION_PAYER.equals(qualifierForPartnerFunction);
				
				if(partnerNeedsFilteringOut(partner, order, partnerIsSoldTo, partnerIsBillTo, partnerIsPayer)) {
					continue;
				}
				
				HeaderPartnerInformationDTO headerPartnerInformation = new HeaderPartnerInformationDTO();
				headerPartnerInformation.setQualifierForPartnerFunction(qualifierForPartnerFunction);
				headerPartnerInformation.setPartnerNoSAP(partner.getSapIfcId());
				headerPartnerInformationList.add(headerPartnerInformation);
			}
		}
		CarrierEntity commercialCarrier = order.getCommCarrier();
		if (commercialCarrier != null) {
			SapVendorEntity sapVendor = commercialCarrier.getSapVendor();
			if (sapVendor != null) {
				HeaderPartnerInformationDTO headerPartnerInformation = new HeaderPartnerInformationDTO();
				headerPartnerInformation.setQualifierForPartnerFunction(HeaderPartnerInformationDTO.QUALIFIER_FOR_PARTNER_FUNCTION_FORWARDING_AGENT);
				headerPartnerInformation.setPartnerNoSAP(sapVendor.getSapIfcId());
				headerPartnerInformationList.add(headerPartnerInformation);
			}
		}
		CarrierEntity executingCarrier = order.getExecCarrier();
		if (executingCarrier != null) {
			SapVendorEntity sapVendor = executingCarrier.getSapVendor();
			if (sapVendor != null) {
				HeaderPartnerInformationDTO headerPartnerInformation = new HeaderPartnerInformationDTO();
				headerPartnerInformation.setQualifierForPartnerFunction(HeaderPartnerInformationDTO.QUALIFIER_FOR_PARTNER_FUNCTION_CARRIER);
				headerPartnerInformation.setPartnerNoSAP(sapVendor.getSapIfcId());
				headerPartnerInformationList.add(headerPartnerInformation);
			}
		}
		
		List<OrderTpSapVendorEntity> otsvList = getOrderTpSapVendorList(order.getOrderId());
		
		for (OrderTpSapVendorEntity otsv : otsvList) {
			SapVendorEntity sapVendor = otsv.getTpSapVendor().getSapVendor();
			SapTpSapVendorPfBsEntity sapTpSapVendorPfBsEntity = otsv.getTpSapVendor().getSapTpSapVendorPf();

			HeaderPartnerInformationDTO headerPartnerInformation = new HeaderPartnerInformationDTO();
			headerPartnerInformation.setQualifierForPartnerFunction(sapTpSapVendorPfBsEntity.getSapTpSapVendorPfCd());
			headerPartnerInformation.setPartnerNoSAP(sapVendor.getSapIfcId());
			headerPartnerInformationList.add(headerPartnerInformation);
		}
		
		transferDeliveryDocument.setHeaderPartnerInformationList(headerPartnerInformationList);

		String realDeliveryDate = null;
		String realDeliveryTime = null;
		String serviceRenderedDate = null;
		Date actualDeliveryDat = order.getActualDeliveryDat();
		if (actualDeliveryDat != null) {
			realDeliveryDate = dateFormat.format(actualDeliveryDat);
			realDeliveryTime = timeFormat.format(actualDeliveryDat);
			serviceRenderedDate = dateFormat.format(actualDeliveryDat);
		}
		transferDeliveryDocument.setRealDeliveryDate(realDeliveryDate);
		transferDeliveryDocument.setRealDeliveryTime(realDeliveryTime); // FIXME: open item in [3]

		transferDeliveryDocument.setServiceRenderedDate(serviceRenderedDate);

		String incotermsPart1 = null;
		SapContractEntity sapContract = order.getSapContract();
		if (sapContract != null) {
			incotermsPart1 = sapContract.getIncotermsCd();
		}
		transferDeliveryDocument.setIncotermsPart1(incotermsPart1);

		final String shippingConditions = getShippingConditions(order);
		transferDeliveryDocument.setShippingConditions(shippingConditions);

		transferDeliveryDocument.setMeansOfTransportType(""); // FIXME: open item in [3]

		final VehicleFinder vehicleFinder = new VehicleFinder(order, true, true, false);
		final String meansOfTransportID = vehicleFinder.getSapVehicleNo();
		final String licensePlate = vehicleFinder.getLicensePlateNo();
		
		transferDeliveryDocument.setMeansOfTransportID(meansOfTransportID);
		transferDeliveryDocument.setLicensePlate(licensePlate);

		String travelTime = null;
		Long transportDurationMin = order.getTransportDurationMin();
		if (transportDurationMin != null) {
			if ( transportDurationMin <= TransferDeliveryDocumentDTO.TRAVEL_TIME_MAX) {				
				travelTime = transportDurationMin.toString();
			}
			else {
				travelTime = TransferDeliveryDocumentDTO.TRAVEL_TIME_MAX.toString();
				if (LOG.isDebugEnabled()) {
					LOG.debug("Transport duration is "  + transportDurationMin + " sending the possible maximum " + travelTime );
				}
			}
			transferDeliveryDocument.setTravelTime(travelTime);
		}

		ShippingpointEntity shippingpoint = getShippingpoint(order);
		final String shippingType = getShippingType(order);
		transferDeliveryDocument.setShippingType(shippingType);

		List<TransferDeliveryItemDTO> transferDeliveryItemList = new ArrayList<TransferDeliveryItemDTO>();

		final List<OrderItemEntity> orderItems = order.getOrderItems();
		
		/*
		 * In case of a credit note, only the order item the credit note is based
		 * on will be sent. See LOGON-6488.
		 */
		final List<OrderItemEntity> relevantOrderItems;
		if (isCreditNote) {
			relevantOrderItems = Collections.singletonList(creditNote.getOrderItem());
		}
		else {
			relevantOrderItems = orderItems;
		}
		
		for (OrderItemEntity orderItem : relevantOrderItems) {
			TransferDeliveryItemDTO transferDeliveryItem = new TransferDeliveryItemDTO();

			String itemNumberOfTheDocument = getOrderItemNo(orderItem);
			transferDeliveryItem.setItemNumberOfTheDocument(itemNumberOfTheDocument);

			String itemNumberOfSTPO = null;
			SapContractItemEntity sapContractItem = orderItem.getSapContractItem();
			if (sapContractItem != null) {
				Long contractItemNo = sapContractItem.getContractItemNo();
				if (contractItemNo != null) {
					itemNumberOfSTPO = formatContractItemNo(contractItemNo);
				}
			}
			transferDeliveryItem.setItemNumberOfSTPO(itemNumberOfSTPO);

			String materialNumber = getSapMaterialIfcNo(orderItem);
			transferDeliveryItem.setMaterialNumber(materialNumber);

			String shortTextForSalesOrderItem = null;
			SapMaterialEntity sapMaterial = orderItem.getSapMaterial();
			if (sapMaterial != null) {
				SapMaterialDescriptionEntity sapMaterialDescription = null;
				ReadOnlyDao<SapMaterialDescriptionEntity> sapMaterialDescriptionDao = getDaoProvider().getSapMaterialDescriptionEntityDao();
				FilterImpl<SapMaterialDescriptionEntity> sapMaterialDescriptionFilter = new FilterImpl<SapMaterialDescriptionEntity>();
				sapMaterialDescriptionFilter.addExpression(Restrictions.eq("sapMaterialDescriptionPK.sapMaterialId",
						sapMaterial.getSapMaterialId()));
				sapMaterialDescriptionFilter.addExpression(Restrictions.eq("sapMaterialDescriptionPK.langCd",
						shiptoSapBpLangCd));
				Authorizer.disableNextAccessRestriction();
				sapMaterialDescription = sapMaterialDescriptionDao.findSingle(sapMaterialDescriptionFilter);
				if (sapMaterialDescription != null) {
					shortTextForSalesOrderItem = sapMaterialDescription.getTxt();
				}
			}
			transferDeliveryItem.setShortTextForSalesOrderItem(shortTextForSalesOrderItem);

			if (isOutboundTransfer) {
				final String storageLocation = sapStorageLocation(orderItem, shippingpoint);
				transferDeliveryItem.setStorageLocation(storageLocation);
			}

			String productionPlant = null;
			PlantBsEntity orderItemPlant = orderItem.getProductionPlant();
			if (orderItemPlant != null) {
				productionPlant = orderItemPlant.getPlantCd();
			}
			transferDeliveryItem.setProductionPlant(productionPlant);

			final ItemType itemType;
			if (isCreditNote) {
				itemType = ItemType.CREDIT_NOTE_ITEM;
			}
			else if (orderItem.getReturnQuantHandling() != null && orderItem.getReturnQuantHandling().getReturnQuantHandlingCd().equals(Constant.RETURN_QUANT_HANDLING_NO_ADJUSTMENT)){
				itemType = ItemType.REAL_ITEM_WITH_RETURN_QUANTITY_NO_ADJUSTMENT;
			}
			else {
				itemType = ItemType.STANDARD_ITEM;
			}
			
			DeliveredQtyInSalesUomAndTons deliveredQtyData = getDeliveredQtyInSalesUomAndTons(
					orderItem, itemType);
			
			String deliveredQtyInSalesUomStr = deliveredQtyData.getDeliveredQtyInSalesUomStr();
			String salesUomStr = deliveredQtyData.getSalesUomStr();
			String deliveredQtyInTonsStr = deliveredQtyData.getDeliveredQtyInTonsStr();
			String weightUnitStr = deliveredQtyData.getWeightUnitStr();
			
			transferDeliveryItem.setDeliveredQtyInSalesUom(deliveredQtyInSalesUomStr);
			transferDeliveryItem.setSalesUom(salesUomStr);
			transferDeliveryItem.setDeliveredQtyInTons(deliveredQtyInTonsStr);
			transferDeliveryItem.setWeightUnit(weightUnitStr);
			
			// LOGON-4516 Runtime Error while sending Transfer Delivery Document
			// -> ZTRATY always set to "".
			// transferDeliveryItem.setMeansOfTransportType(meansOfTransportID);
			transferDeliveryItem.setMeansOfTransportType("");

			transferDeliveryItemList.add(transferDeliveryItem);
		}
		transferDeliveryDocument.setTransferDeliveryItemList(transferDeliveryItemList);
		return transferDeliveryDocument;
	}
	
	public void sendTransferDeliveryDocument(TransferDeliveryDocumentDTO transferDeliveryDocument) {

		LogonToSap.sendTransferDeliveryDocument(getConfigBean(), transferDeliveryDocument);
	}

	private List<String> splitAndStoreInList(String source, int splitLength) {
		List<String> textList = new ArrayList<String>();
		int start = 0;
		int fullLength = source.length();
		while (start + splitLength <= fullLength) {
			String splitted = source.substring(start, start + splitLength);
			start += splitLength;
			textList.add(splitted);
		}
		if (start != fullLength) {
			// add the last one, too
			String splitted = source.substring(start);
			textList.add(splitted);
		}
		return textList;
	}

	private String getOrderNumber(OrderEntity order) {
		String orderNumberStr = null;
		Long orderNumber = order.getOrderNumber();
		if (orderNumber != null) {
			orderNumberStr = orderNumber.toString();
		}
		return orderNumberStr;
	}

	private String getOrderItemNo(OrderItemEntity orderItem) {
		String orderItemNoStr = null;
		Long orderItemNo = orderItem.getOrderItemNo();
		if (orderItemNo != null) {
			orderItemNoStr = orderItemNo.toString();
		}
		return orderItemNoStr;
	}

	private String getSentOrderItemNo(OrderItemEntity orderItem) {
		String orderItemNoStr = null;
		Long orderItemNo = orderItem.getSentOrderItemNo();
		if (orderItemNo != null) {
			orderItemNoStr = orderItemNo.toString();
		}
		return orderItemNoStr;
	}

	private boolean isQuantityControlledContractItem(OrderItemEntity orderItem) {
		SapContractItemEntity contractItem = orderItem.getSapContractItem();

		// not all order items belong to a contract
		if (contractItem == null) {
			return false;
		}

		SapContractEntity contract = contractItem.getSapContract();
		boolean quantityControlledContract = LogonUtil.isQuantityControlledContract(contract);
		return quantityControlledContract;
	}

	private String getSapMaterialIfcNo(OrderItemEntity orderItem) {
		String sapMaterialIfcNo = null;
		SapMaterialEntity sapMaterial = orderItem.getSapMaterial();
		if (sapMaterial != null) {
			sapMaterialIfcNo = sapMaterial.getSapMaterialIfcNo();
		}
		return sapMaterialIfcNo;
	}

	private String getSapContractNo(OrderEntity order) {
		SapContractEntity sapContract = order.getSapContract();
		String sapContractNo = null;
		if (sapContract != null) {
			sapContractNo = sapContract.getSapContractNo();
		}
		return sapContractNo;
	}

	private String getSpIndicator(OrderEntity order) {
		if (order == null || order.getSpi() == null) {
			return null;
		}
		return order.getSpi().getOrderSpiCd();
	}

	private String getShippingConditions(OrderEntity order) {
		String shippingConditions = null;

		ShippingpointEntity shippingpoint = getShippingpointForShippingCond(order);
		if (shippingpoint != null && shippingpoint.getSapShippingCond() != null) {
			SapShippingCondBsEntity sapShippingCond = shippingpoint.getSapShippingCond();
			shippingConditions = sapShippingCond.getSapShippingCondCd();
		}

		return shippingConditions;
	}
	
	private String getShippingType(OrderEntity order) {
		String shippingType = null;
		
		ShippingpointEntity shippingpoint = getShippingpointForShippingCond(order);
		if (shippingpoint != null && shippingpoint.getSapShippingCond() != null) {
			SapShippingCondBsEntity sapShippingCond = shippingpoint.getSapShippingCond();
			shippingType = sapShippingCond.getSendToSapShippingType();
		}
		
		return shippingType;
	}

	private String getSapShippingpointNo(OrderEntity order) {
		String sapShippingpointNo = null;

		ShippingpointEntity shippingpoint = getShippingpoint(order);
		if (shippingpoint != null) {
			sapShippingpointNo = shippingpoint.getSapShippingpointNo();
		}

		return sapShippingpointNo;
	}

	private String getShippingpointPlantCd(OrderEntity order) {
		String plantCd = null;

		ShippingpointEntity shippingpoint = getShippingpoint(order);
		if (shippingpoint != null) {
			PlantBsEntity plant = shippingpoint.getPlant();
			if (plant != null) {
				plantCd = plant.getPlantCd();
			}
		}

		return plantCd;
	}
	
	/**
	 * Loads an assignment
	 * @param orderId for which we want to get an assignment
	 * @adnlog.exclude return
	 */
	public AssignmentEntity loadAssignment(Long orderId) {

		final ReadOnlyDao<AssignmentPartEntity> assDao = getDaoProvider().getAssignmentPartEntityDao();
		final Filter<AssignmentPartEntity> filter = assDao.createFilter();
		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ORDER_ID, orderId));
		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT + "."
				+ AssignmentEntity.ATTR_ACTIVE_FLAG, true));
		final AssignmentPartEntity assPart = assDao.findSingle(filter);

		if (assPart != null) {
			return assPart.getAssignment();
		}

		return null;

	}

	private ShippingpointEntity getShippingpoint(OrderEntity order) {
		ShippingpointEntity shippingpoint = order.getShippingpoint();

		if (shippingpoint != null && Boolean.FALSE.equals(shippingpoint.getKnownAlsoInSapFlag())) {
			if (shippingpoint.getSendSapShippingpoint() != null) {
				shippingpoint = shippingpoint.getSendSapShippingpoint();
			}
			else {
				SapBpEntity shiptoSapBp = order.getShiptoSapBp();
				ShiptoEntity shipto = null;
				Long shiptoSapBpId = shiptoSapBp == null ? null : shiptoSapBp.getSapBpId();
				if (shiptoSapBpId != null) {
					ReadOnlyDao<ShiptoEntity> shiptoDao = getDaoProvider().getShiptoEntityDao();
					Filter<ShiptoEntity> shiptoFilter = new FilterImpl<ShiptoEntity>();
					shiptoFilter.addExpression(Restrictions.eq(ShiptoEntity.ATTR_SHIPTO_SAP_BP_ID, shiptoSapBpId));
					Authorizer.disableNextAccessRestriction();
					shipto = shiptoDao.findSingle(shiptoFilter);
				}
				if (shipto != null) {
					shippingpoint = shipto.getRailChainShippingpoint();
					if (shippingpoint != null && Boolean.FALSE.equals(shippingpoint.getKnownAlsoInSapFlag())) {
						if (shippingpoint.getSendSapShippingpoint() != null) {
							shippingpoint = shippingpoint.getSendSapShippingpoint();
						}
					}
				}
			}
		}

		return shippingpoint;
	}
	
	private ShippingpointEntity getReturnShippingpoint(final OrderItemEntity orderItem) {
		
		ShippingpointEntity returnShippingpoint = orderItem.getReturnShippingpoint();
		if (returnShippingpoint != null) {
			if (Boolean.TRUE.equals(returnShippingpoint.getKnownAlsoInSapFlag())) {
				return returnShippingpoint;
			}
			else {
				returnShippingpoint = returnShippingpoint.getSendSapShippingpoint();
				if (returnShippingpoint != null) {
					return returnShippingpoint;
				}	
			}
		}
		
		return null;
	}
	
	private ShippingpointEntity getShippingpointForShippingCond(OrderEntity order) {
		
		ShippingpointEntity sp = order.getShippingpoint();
		if (sp == null) {
			return sp;
		}
		
		if (Boolean.TRUE.equals(sp.getKnownAlsoInSapFlag()) || sp.getSendSapShippingpoint() != null) {
			return sp;
		}
		
		final SapBpEntity shiptoSapBp = order.getShiptoSapBp();
		if (shiptoSapBp == null) {
			return null;
		}
		
		final Long shiptoSapBpId = shiptoSapBp.getSapBpId();
		final ReadOnlyDao<ShiptoEntity> dao = getDaoProvider().getShiptoEntityDao();
		final Filter<ShiptoEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(ShiptoEntity.ATTR_SHIPTO_SAP_BP_ID, shiptoSapBpId));
		Authorizer.disableNextAccessRestriction();
		final ShiptoEntity shipto = dao.findSingle(filter);
		if (shipto != null) {
			sp = shipto.getRailChainShippingpoint();
		}
		
		return sp;
	}
	
	private String getLastEventTsOfBizevent(Long orderId, Long bizeventTypeCd) {
		String eventTsStr = null;
		ReadOnlyDao<BizeventEntity> bizeventDao = getDaoProvider().getBizeventEntityDao();
		FilterImpl<BizeventEntity> bizeventFilter = new FilterImpl<BizeventEntity>();
		bizeventFilter.addExpression(Restrictions.eq(BizeventEntity.ATTR_ORDER_ID, orderId));
		bizeventFilter.addExpression(Restrictions.eq(BizeventEntity.ATTR_BIZEVENT_TYPE_CD, bizeventTypeCd));
		bizeventFilter.addExpression(Order.desc(BizeventEntity.ATTR_EVENT_TS));
		Authorizer.disableNextAccessRestriction();
		List<BizeventEntity> bizeventList = bizeventDao.find(bizeventFilter);
		if (bizeventList.size() > 0) {
			BizeventEntity bizevent = bizeventList.get(0);
			if (bizevent != null) {
				Date eventTs = bizevent.getEventTs();
				if (eventTs != null) {
					eventTsStr = dateFormat.format(eventTs);
				}
			}
		}
		return eventTsStr;
	}

	public UomBsEntity getSalesUom(OrderEntity order, OrderItemEntity orderItem) {
		UomBsEntity salesUom = null;
		SareaEntity sarea = order.getSarea();

		UomBsEntity deliveredUom = orderItem.getDeliveredUom();
		SapMaterialEntity sapMaterial = orderItem.getSapMaterial();
		if (sapMaterial != null) {
			salesUom = unitOfMeasurementConverterService.getSalesUom( sapMaterial, sarea);
			if (salesUom == null) {
				salesUom = sapMaterial.getBaseUom();
			}
		}
		if (salesUom == null) {
			salesUom = deliveredUom;
		}

		return salesUom;
	}



	/**
	 * 
	 * 
	 * @param orderItem
	 * @param itemType
	 *            should always be {@link ItemType#STANDARD_ITEM} unless it is
	 *            for the {@link SalesDocumentItemDTO}, in which case specify
	 *            the correct item type, so that real items and virtual items
	 *            for returned quantities will be correctly set up.
	 * @return
	 */
	private DeliveredQtyInSalesUomAndTons getDeliveredQtyInSalesUomAndTons(
			OrderItemEntity orderItem, ItemType itemType) {
		// [[
		DeliveredQtyInSalesUomAndTons result = null;
		Object[][] params = {{"orderItem", orderItem},
							{"itemType", itemType}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getDeliveredQtyInSalesUomAndTons", params);
		}
		// ]]
		try {
			String deliveredQtyInSalesUomStr = null;
			String salesUomStr = null;
			String deliveredQtyInTonsStr = null;
			String weightUnitStr = null;
			
			OrderEntity order = orderItem.getOrder();
			UomBsEntity salesUom = getSalesUom(order, orderItem); // Should never be null!
			SapMaterialEntity sapMaterial = orderItem.getSapMaterial(); // Not nullable!
			UomBsEntity baseUom = sapMaterial.getBaseUom();
			
			BigDecimal deliveredQtyInBaseUom;
			
			switch (itemType) {
			case STANDARD_ITEM:
				// Send delivered quantity
				deliveredQtyInBaseUom = orderItem.getDeliveredQuantity();
				break;
			case REAL_ITEM_WITH_RETURN_QUANTITY:
			case REAL_ITEM_WITH_RETURN_QUANTITY_NO_ADJUSTMENT:
					deliveredQtyInBaseUom = getDeliveredPlusReturnedQuantityInBaseUom(orderItem, order);
					break;
			case CREDIT_NOTE_ITEM:
			case VIRTUAL_RETURN_ITEM: {
				// Send returned quantity (as delivered quantity)
				BigDecimal returnedQuantity = orderItem.getReturnedQuantity();

				Long toBaseUomNumerator = orderItem.getLrUomNumerator();
				Long toBaseUomDenominator = orderItem.getLrUomDenominator();

				if (toBaseUomNumerator == null || toBaseUomDenominator == null) {
					LOG.error("Invalid LR numerator(s) (null). order item: " + order.getOrderNumber() + "_" + orderItem.getOrderItemNo());
				}

				final BigDecimal returnedQtyInBaseUom;
				try {
					returnedQtyInBaseUom = unitOfMeasurementConverterService.convert(returnedQuantity, toBaseUomNumerator, toBaseUomDenominator, true);
				}
				catch (LogonConversionException e) {
					LOG.error("LogonConversionException occurred when converting returned quantity to base UoM. order item: " + order.getOrderNumber() + "_" + orderItem.getOrderItemNo());
					throw new LogonException(e);
				}

				if (returnedQtyInBaseUom == null) {
					deliveredQtyInBaseUom = BigDecimal.ZERO;
				}
				else {
					deliveredQtyInBaseUom = returnedQtyInBaseUom;
				}

				break;
			}
			default:
				throw new LogonException(Reason.RES_ERROR_IMPL);
			}

			if (deliveredQtyInBaseUom == null) {
				String errMsg = "delivered quantity in base UoM is null. order item: " + order.getOrderNumber() + "_" + orderItem.getOrderItemNo();
				LOG.error(errMsg);
				throw new LogonException(errMsg, Reason.RES_IMPL_BUG);
			}

			deliveredQtyInBaseUom = deliveredQtyInBaseUom.abs(); // Should never be null.
			
			// *** Delivered quantity in sales UoM
			BigDecimal deliveredQtyInSalesUom;
			
			try {
				UomConversionResult ucr = unitOfMeasurementConverterService.convert(deliveredQtyInBaseUom,
						baseUom, salesUom, sapMaterial, null, null, false);
				
				deliveredQtyInSalesUom = ucr.getQuantity();
			}
			catch (LogonConversionException e) {
				LOG.warn("Cannot convert delivered or returned quantity from base UoM "
						+ baseUom.getUomCd() + " to sales UoM " + salesUom.getUomCd()
						+ " (sapMaterialId = " + sapMaterial.getSapMaterialId() + ")"
						+ " order item: " + order.getOrderNumber() + "_" + orderItem.getOrderItemNo()
						+ ". Message: " + e.getMessage());
				
				deliveredQtyInSalesUom = null;
			}
			
			if (deliveredQtyInSalesUom != null) {
				deliveredQtyInSalesUomStr = deliveredQtyInSalesUom.toPlainString();
				/*
				 * At this point, the resulting "deliveredQtyInSalesUom" should already have
				 * a maximum of 4 decimal points. Make sure we do not exceed the SAP field length
				 * of 15 characters. The explicit exception helps to faster identify the problem.
				 */
				if (deliveredQtyInSalesUomStr.length() > SAP_DOCS_QUANTITY_MAX_LENGTH) {
					/* Possible solutions if you hit this exception:
					 * - Decrease the number of digits (see scale factor above)
					 * - Change the sales unit of measurement in SAP to something that produces
					 *   a shorter string (e.g. tons instead of kilograms)
					 * - Longer term: Discuss with business, what really the max size and precision
					 *   requirements for this value are.
					 */
					final String msg = String.format("The quantity string '%s' is too large for "
							+ "the SAP sales document. " + "The maximum length is set to be %d.",
							deliveredQtyInSalesUomStr, SAP_DOCS_QUANTITY_MAX_LENGTH);
					LOG.error(msg);
					throw new RuntimeException(msg);
				}
			}
			
			// *** Sales UoM
			if (salesUom != null) {
				salesUomStr = salesUom.getUomCd();
				salesUomStr = convertLogonUomToSapUom(salesUomStr, order, orderItem);
			}
			
			// *** Delivered quantity in tons
			BigDecimal deliveredQtyInTons;
			UomBsEntity uomTons = referentialDataService.findBsEntity(UomBsEntity.class, Constant.UOM_TONS);
			
			try {
				UomConversionResult ucr = unitOfMeasurementConverterService.convert(deliveredQtyInBaseUom,
						baseUom, uomTons, sapMaterial, null, null, false);
				
				deliveredQtyInTons = ucr.getQuantity();
			}
			catch (LogonConversionException e) {
				LOG.error("Cannot convert delivered or returned quantity from base UoM ("
						+ baseUom.getUomCd() + ") to tons. order item: " + order.getOrderNumber() + "_" + orderItem.getOrderItemNo());
				
				deliveredQtyInTons = null;
			}
			
			if (deliveredQtyInTons != null) {
				deliveredQtyInTonsStr = deliveredQtyInTons.toPlainString();
				
				// *** Weight unit
				weightUnitStr = Constant.UOM_TONS;
				weightUnitStr = convertLogonUomToSapUom(weightUnitStr, order, orderItem);
			}
			
			return result = new DeliveredQtyInSalesUomAndTons(deliveredQtyInSalesUomStr,
					salesUomStr, deliveredQtyInTonsStr, weightUnitStr);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	private BigDecimal getDeliveredPlusReturnedQuantityInBaseUom(OrderItemEntity orderItem, OrderEntity order) {
		BigDecimal deliveredQtyInBaseUom;// Send delivered quantity + returned quantity (as delivered quantity)
		BigDecimal deliveredQuantity = orderItem.getDeliveredQuantity();
		BigDecimal returnedQuantity = orderItem.getReturnedQuantity();

		Long toBaseUomNumerator = orderItem.getLrUomNumerator();
		Long toBaseUomDenominator = orderItem.getLrUomDenominator();

		if (toBaseUomNumerator == null || toBaseUomDenominator == null) {
			LOG.error("Invalid LR numerator(s) (null). order item: " + order.getOrderNumber() + "_" + orderItem.getOrderItemNo());
		}

		final BigDecimal returnedQtyInBaseUom;
		try {
			returnedQtyInBaseUom = unitOfMeasurementConverterService.convert(returnedQuantity, toBaseUomNumerator, toBaseUomDenominator, true);
		}
		catch (LogonConversionException e) {
			LOG.error("LogonConversionException occurred when converting returned quantity to base UoM. order item: " + order.getOrderNumber() + "_" + orderItem.getOrderItemNo());
			throw new LogonException(e);
		}

		if (deliveredQuantity == null) {
			deliveredQtyInBaseUom = returnedQtyInBaseUom;
		} else {
			deliveredQtyInBaseUom = deliveredQuantity.add(returnedQtyInBaseUom);
		}
		return deliveredQtyInBaseUom;
	}

	private static OrderItemEntity getFirstBagOrBulkOrderItem(OrderEntity order) {
		// [[
		OrderItemEntity result = null;
		Object[][] params = { { "order", order } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SapOutgoingDataServiceBean.class, "getFirstBagOrBulkOrderItem", params);
		}
		// ]]
		try {
			OrderItemEntity firstBagOrBulkOrderItem = null;

			final List<OrderItemEntity> orderItems = order.getOrderItems();
			for (OrderItemEntity orderItem : orderItems) {
				if (LogonPackageHandlingUtil.isBag(orderItem) || LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(orderItem)) {
					if (firstBagOrBulkOrderItem == null
							|| orderItem.getOrderItemNo() < firstBagOrBulkOrderItem.getOrderItemNo()) {
						firstBagOrBulkOrderItem = orderItem;
					}
				}
			}

			return result = firstBagOrBulkOrderItem;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	@AdnTrace
	private OrderItemEntity getFirstBulkOrderItem(List<OrderItemEntity> orderItems) {
		OrderItemEntity firstBulkOrderItem = null;

		for (OrderItemEntity orderItem : orderItems) {
			if (LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(orderItem)) {
				if (firstBulkOrderItem == null || orderItem.getOrderItemNo() < firstBulkOrderItem.getOrderItemNo()) {
					firstBulkOrderItem = orderItem;
				}
			}
		}

		return firstBulkOrderItem;
	}
	
	/**
	 * If the order contains no bulk orderitems return the latest load
	 * @param order for the sales document
	 * @return the load which will provide data for the sales document
	 */
	@AdnTrace
	private LoadEntity getRelevantLoadForSalesDocument(OrderEntity order) {
		List<OrderItemEntity> orderItems = order.getOrderItems();
		OrderItemEntity firstBulkOrderItem = getFirstBulkOrderItem(orderItems);
				
		if (firstBulkOrderItem != null) {
			return firstBulkOrderItem.getLoad();
		}
		else {
			return getLatestLoad(order);
		}
	}

	private String[] getSalesOrganizationAndDistributionChannelAndDivision(OrderEntity order) {
		String salesOrganization = null;
		String distributionChannel = null;
		String division = null;
		SareaEntity sarea = order.getSarea();
		if (sarea != null) {
			SorgBsEntity sorg = sarea.getSorg();
			if (sorg != null) {
				salesOrganization = sorg.getSorgCd();
			}
			distributionChannel = sarea.getDistributionChannel();
			division = sarea.getDivision().getDivisionCd();
		}
		return new String[] { salesOrganization, distributionChannel, division };
	}

	/**
	 * Find latest load (check LOADING_TS)
	 * 
	 * @param order
	 * @return LoadEntity
	 */
	private LoadEntity getLatestLoad(OrderEntity order) {
		// [[
		LoadEntity result = null;
		Object[][] params = { { "order", order } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getLatestLoad", params);
		}
		// ]]
		try {
			LoadEntity loadTypeLoad = null;
			final List<LoadEntity> loadList = new ArrayList<LoadEntity>();

			final List<OrderItemEntity> orderItems = (List<OrderItemEntity>) LogonPackageHandlingUtil.getMaterialItems(order);
			for (OrderItemEntity oie : orderItems) {
				if (oie.getLoad() != null) {
					loadList.add(oie.getLoad());

					// set the first item to compare
					if (loadTypeLoad == null) {
						loadTypeLoad = oie.getLoad();
					}
				}
			}

			for (LoadEntity load : loadList) {
				if (load.getLoadingTs() != null) {
					// in case first selected load has LoadingTs null
					if (loadTypeLoad.getLoadingTs() == null) {
						loadTypeLoad = load;
					}
					else if (load.getLoadingTs().compareTo(loadTypeLoad.getLoadingTs()) > 0) {
						loadTypeLoad = load;
					}
				}
			}
			return result = loadTypeLoad;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private void setPartnerInformation(PartnerInformationDTO partnerInformation, String name, String streetAndHouseNumber,
			String city, String postalCode, String countryKey, String oneTimeSoldtoVatNo) {
		String name1 = null;
		String name2 = null;
		String name3 = null;
		if (name != null) {
			if (name.length() > PartnerInformationDTO.NAME1_MAX_LENGTH) {
				name1 = name.substring(0, PartnerInformationDTO.NAME1_MAX_LENGTH);
				int name1AndName2MaxLength = PartnerInformationDTO.NAME1_MAX_LENGTH + PartnerInformationDTO.NAME2_MAX_LENGTH;
				if (name.length() > name1AndName2MaxLength) {
					name2 = name.substring(PartnerInformationDTO.NAME1_MAX_LENGTH, name1AndName2MaxLength);
					name3 = name.substring(name1AndName2MaxLength);
				}
				else {
					name2 = name.substring(PartnerInformationDTO.NAME1_MAX_LENGTH);
				}
			}
			else {
				name1 = name;
			}
		}
		partnerInformation.setName1(name1);
		partnerInformation.setName2(name2);
		partnerInformation.setName3(name3);

		String streetAndHouseNumber1 = null;
		String streetAndHouseNumber2 = null;
		if (streetAndHouseNumber != null) {
			if (streetAndHouseNumber.length() > PartnerInformationDTO.STREET_AND_HOUSE_NUMBER1_MAX_LENGTH) {
				streetAndHouseNumber1 = streetAndHouseNumber.substring(0,
						PartnerInformationDTO.STREET_AND_HOUSE_NUMBER1_MAX_LENGTH);
				streetAndHouseNumber2 = streetAndHouseNumber.substring(PartnerInformationDTO.STREET_AND_HOUSE_NUMBER1_MAX_LENGTH);
			}
			else {
				streetAndHouseNumber1 = streetAndHouseNumber;
			}
		}
		partnerInformation.setStreetAndHouseNumber1(streetAndHouseNumber1);
		partnerInformation.setStreetAndHouseNumber2(streetAndHouseNumber2);

		partnerInformation.setCity(city);
		partnerInformation.setPostalCode(postalCode);
		partnerInformation.setCountryKey(countryKey);

		if (oneTimeSoldtoVatNo != null) {
			partnerInformation.setOneTimeSoldtoVatNo(oneTimeSoldtoVatNo);
		}
	}
	
	public void setPartnerAddressInformationForPricingCallPartner(PricingCallDTOPartner partner,
			String name, String streetAndHouseNumber, String city, String postalCode,
			String country) {
		
		String name1 = null;
		String name2 = null;
		String name3 = null;
		if (name != null) {
			if (name.length() > PartnerInformationDTO.NAME1_MAX_LENGTH) {
				name1 = name.substring(0, PartnerInformationDTO.NAME1_MAX_LENGTH);
				int name1AndName2MaxLength = PartnerInformationDTO.NAME1_MAX_LENGTH + PartnerInformationDTO.NAME2_MAX_LENGTH;
				if (name.length() > name1AndName2MaxLength) {
					name2 = name.substring(PartnerInformationDTO.NAME1_MAX_LENGTH, name1AndName2MaxLength);
					name3 = name.substring(name1AndName2MaxLength);
				}
				else {
					name2 = name.substring(PartnerInformationDTO.NAME1_MAX_LENGTH);
				}
			}
			else {
				name1 = name;
			}
		}
		
		partner.setName1(name1);
		partner.setName2(name2);
		partner.setName3(name3);

		String street = null;
		if (streetAndHouseNumber != null) {
			if (streetAndHouseNumber.length() > PartnerInformationDTO.STREET_AND_HOUSE_NUMBER1_MAX_LENGTH) {
				street = streetAndHouseNumber.substring(0, PartnerInformationDTO.STREET_AND_HOUSE_NUMBER1_MAX_LENGTH);
			}
			else {
				street = streetAndHouseNumber;
			}
		}
		partner.setStreet(street);

		partner.setCity(city);
		partner.setPostalCode(postalCode);
		partner.setCountry(country);

	}

	private TrailerEntity getTrailerOfTruck(Date actualDeliveryDat, Long truckId) {
		TrailerEntity trailerOfTruck = null;
		ReadOnlyDao<TruckTrailerHistoryEntity> truckTrailerHistoryDao = getDaoProvider().getTruckTrailerHistoryEntityDao();
		FilterImpl<TruckTrailerHistoryEntity> truckTrailerHistoryFilter = new FilterImpl<TruckTrailerHistoryEntity>();
		truckTrailerHistoryFilter.addExpression(Restrictions.eq(TruckTrailerHistoryEntity.ATTR_TRUCK_ID, truckId));
		truckTrailerHistoryFilter.addExpression(Restrictions.le(TruckTrailerHistoryEntity.ATTR_ASSIGNMENT_TS,
				actualDeliveryDat));
		truckTrailerHistoryFilter.addExpression(Order.desc(TruckTrailerHistoryEntity.ATTR_ASSIGNMENT_TS));
		Authorizer.disableNextAccessRestriction();
		List<TruckTrailerHistoryEntity> truckTrailerHistoryList = truckTrailerHistoryDao.find(truckTrailerHistoryFilter);
		if (truckTrailerHistoryList.size() > 0) {
			TruckTrailerHistoryEntity truckTrailerHistory = truckTrailerHistoryList.get(0);
			if (truckTrailerHistory != null) {
				trailerOfTruck = truckTrailerHistory.getTrailer();
			}
		}
		return trailerOfTruck;
	}

	private String convertLogonUomToSapUom(String logonUom) {
		Authorizer.disableNextAccessRestriction();
		final UomBsEntity uom = referentialDataService.findBsEntity(UomBsEntity.class, logonUom);
		if (uom != null) {
			return StringUtils.defaultString(uom.getSendToSap(), uom.getUomCd());
		}
		else {
			return null;
		}
	}
	
	private String convertLogonUomToSapUom(String logonUom, OrderEntity order, OrderItemEntity orderItem) {
		final String sapUom = convertLogonUomToSapUom(logonUom);
		
		if (sapUom == null) {
			final StringBuilder orderInfo = new StringBuilder();
			orderInfo.append("No UoM mapping in tlgnr_uom_bs for UoM = ");
			orderInfo.append(logonUom);
			orderInfo.append(" (");
			orderInfo.append(String.format("OrderId = %d, OrderNumber = %s",
					order.getOrderId(),
					getOrderNumber(order)));
			if (orderItem != null) {
				orderInfo.append(String.format(", OrderItemId = %d, OrderItemNo = %s",
						orderItem.getOrderItemId(),
						getOrderItemNo(orderItem)));
			}
			orderInfo.append(')');
			LOG.info(orderInfo.toString());
		}
		
		return sapUom;
	}
	
	private String convertLogonUomToSapUom(String logonUom, GoodsMovementEntity goodsMovement) {
		final String sapUom = convertLogonUomToSapUom(logonUom);
		
		if (sapUom == null) {
			final StringBuilder orderInfo = new StringBuilder();
			orderInfo.append("No UoM mapping in tlgnr_uom_bs for UoM = ");
			orderInfo.append(logonUom);
			orderInfo.append(" (");
			orderInfo.append(String.format("GoodsMovemenetId = %d",
					goodsMovement.getGoodsMovementId()));
			orderInfo.append(')');
			LOG.info(orderInfo.toString());
		}
		
		return sapUom;
	}
	
	private String formatContractItemNo(Long contractItemNo) {
		String formattedContractItemNo = String.format("%06d", contractItemNo);
		return formattedContractItemNo;
	}

	private ShipmentCentralAddressDTO createShipmentCentralAddress(String qualifierForPartnerFunction, SapVendorEntity sapVendor) {
		ShipmentCentralAddressDTO shipmentCentralAddress = new ShipmentCentralAddressDTO();
		shipmentCentralAddress.setQualifierForPartnerFunction(qualifierForPartnerFunction);

		String partnerNoSap = null;
		String languageKey = null;
		String nameOrAddressLine = null;
		String houseNumberAndStreet = null;
		String postalCode = null;
		String townOrCity = null;
		String countryIndicator = null;
		String regionState = null;
		if (sapVendor != null) {
			partnerNoSap = sapVendor.getSapIfcId();
			LangBsEntity lang = sapVendor.getLang();
			if (lang != null) {
				languageKey = lang.getLangCd();
			}
			nameOrAddressLine = sapVendor.getName1();
			houseNumberAndStreet = StringUtils.defaultString(sapVendor.getStreet()) + " "
					+ StringUtils.defaultString(sapVendor.getHouseNumber());
			postalCode = sapVendor.getPostalCode();
			townOrCity = sapVendor.getCity();
			countryIndicator = sapVendor.getCountry() != null ? sapVendor.getCountry().getSapCountryCd() : "";
			regionState = sapVendor.getRegion();
		}
		shipmentCentralAddress.setPartnerNoSap(partnerNoSap);
		shipmentCentralAddress.setLanguageKey(languageKey);
		shipmentCentralAddress.setNameOrAddressLine(nameOrAddressLine);
		shipmentCentralAddress.setHouseNumberAndStreet(houseNumberAndStreet);
		shipmentCentralAddress.setPostalCode(postalCode);
		shipmentCentralAddress.setTownOrCity(townOrCity);
		shipmentCentralAddress.setCountryIndicator(countryIndicator);
		shipmentCentralAddress.setRegionState(regionState);

		return shipmentCentralAddress;
	}
	
	/**
	 * 
	 * @param orderItem
	 * @param onlyForReturn
	 */
	private SalesDocumentItemDTO createSalesDocumentItem(CreateSalesDocumentItemParams csdiParams) {
		// [[
		SalesDocumentItemDTO result = null;
		Object[][] params = { { "csdiParams", csdiParams } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createSalesDocumentItem", params);
		}
		// ]]
		try {
			final ItemType itemType = csdiParams.getItemType();
			final OrderItemEntity orderItem = csdiParams.getOrderItem();
			
			final SalesDocumentItemDTO salesDocumentItem = new SalesDocumentItemDTO();
			
			final String itemNumber;
			final String returnIndicator;
			
			switch (itemType) {
			case STANDARD_ITEM:
				itemNumber = getSentOrderItemNo(orderItem);
				
				BigDecimal deliveredQuantity = orderItem.getDeliveredQuantity();
				if (deliveredQuantity != null) {
					returnIndicator = (deliveredQuantity.signum() == -1 ? SalesDocumentItemDTO.RETURN_INDICATOR_TRUE
							: SalesDocumentItemDTO.RETURN_INDICATOR_FALSE);
				}
				else {
					returnIndicator = null;
				}
				
				break;
			case REAL_ITEM_WITH_RETURN_QUANTITY:
				if (csdiParams.isQuantityControlledContract()) {
					// LOGON-6624: Advance Payments are an exception here - they must contain the POSEX value
					itemNumber = getSentOrderItemNo(orderItem);
				}
				else {
					itemNumber = null;
				}
				returnIndicator = SalesDocumentItemDTO.RETURN_INDICATOR_FALSE;
				break;
			case VIRTUAL_RETURN_ITEM:
				if (csdiParams.isQuantityControlledContract()) {
					// LOGON-6624: Advance Payments are an exception here - they must contain the POSEX value
					itemNumber = getSentOrderItemNo(orderItem);
				}
				else {
					itemNumber = null;
				}
				returnIndicator = SalesDocumentItemDTO.RETURN_INDICATOR_TRUE;
				break;
			case CREDIT_NOTE_ITEM:
				itemNumber = null;
				returnIndicator = SalesDocumentItemDTO.RETURN_INDICATOR_TRUE;
				break;
				case REAL_ITEM_WITH_RETURN_QUANTITY_NO_ADJUSTMENT:
					itemNumber = getSentOrderItemNo(orderItem);
					returnIndicator = SalesDocumentItemDTO.RETURN_INDICATOR_FALSE;
					break;
			default:
				throw new LogonException("Unknown item type", Reason.RES_ERROR_IMPL);
			}
			
			salesDocumentItem.setItemNumber(itemNumber);
			salesDocumentItem.setReturnIndicator(returnIndicator);
			
			DeliveredQtyInSalesUomAndTons deliveredQtyData = getDeliveredQtyInSalesUomAndTons(
					orderItem, itemType);
			
			String deliveredQtyInSalesUomStr = deliveredQtyData.getDeliveredQtyInSalesUomStr();
			String salesUomStr = deliveredQtyData.getSalesUomStr();
			String deliveredQtyInTonsStr = deliveredQtyData.getDeliveredQtyInTonsStr();
			String weightUnitStr = deliveredQtyData.getWeightUnitStr();
			
			salesDocumentItem.setDeliveredQtyInSalesUom(deliveredQtyInSalesUomStr);
			salesDocumentItem.setSalesUom(salesUomStr);
			salesDocumentItem.setDeliveredQtyInTons(deliveredQtyInTonsStr);
			salesDocumentItem.setWeightUnit(weightUnitStr);
			
			final String plant;
			final ShippingpointEntity shippingpoint;
			
			String sapShippingpointNo;
			if (SalesDocumentItemDTO.RETURN_INDICATOR_TRUE.equals(returnIndicator)
					&& csdiParams.getReturnPlant() != null
					&& csdiParams.getReturnShippingpoint() != null) {
				
				plant = csdiParams.getReturnPlant();
				shippingpoint = csdiParams.getReturnShippingpoint();
				sapShippingpointNo = shippingpoint != null ? shippingpoint.getSapShippingpointNo() : null;
			}
			else {
				plant = csdiParams.getPlant();
				shippingpoint = csdiParams.getShippingpoint();
				sapShippingpointNo = getSapShippingpointNo(orderItem.getOrder());
			}
			
			salesDocumentItem.setPlant(plant);

			final String storageLocation = sapStorageLocation(orderItem, shippingpoint);
			salesDocumentItem.setStorageLocation(storageLocation);

			salesDocumentItem.setShippingPointReceivingPoint(sapShippingpointNo);

			final String freeOfChargeIndicator = csdiParams.getFreeOfChargeIndicator();
			salesDocumentItem.setFreeOfChargeIndicator(freeOfChargeIndicator);

			String meansOfTransportId = csdiParams.getMeansOfTransportId();
			// the spec now states that the first 4 characters are taken (ZPTRATY is only 4 characters)
			if (meansOfTransportId != null && meansOfTransportId.length() > 4) {
				meansOfTransportId = meansOfTransportId.substring(0, 4);
			}
			salesDocumentItem.setMeansOfTransportType(meansOfTransportId);

			String itemSapContractNo = null;
			String itemFromContract = null;
			SapContractEntity sapContract = null;
			SapContractItemEntity sapContractItem = orderItem.getSapContractItem();
			
			if (sapContractItem != null) {
				sapContract = sapContractItem.getSapContract();
				itemSapContractNo = sapContract.getSapContractNo();
				Long contractItemNo = sapContractItem.getContractItemNo();
				if (contractItemNo != null) {
					itemFromContract = formatContractItemNo(contractItemNo);
				}
			}
			salesDocumentItem.setContract(itemSapContractNo);
			salesDocumentItem.setItemFromContract(itemFromContract);

			final String originalDocumentForReturns = csdiParams.getOriginalDocumentForReturns();
			salesDocumentItem.setOriginalDocumentForReturns(originalDocumentForReturns);
			
			final String originalItem = csdiParams.getOriginalItem();
			salesDocumentItem.setOriginalItem(originalItem);

			String materialNumber = getSapMaterialIfcNo(orderItem);
			salesDocumentItem.setMaterialNumber(materialNumber);

			String materialDescription = null;
			PlantBsEntity orderItemPlant = orderItem.getProductionPlant();
			SapMaterialEntity sapMaterial = orderItem.getSapMaterial();
			if (sapMaterial != null && orderItemPlant != null) {
				ReadOnlyDao<MatPlantEntity> matPlantDao = getDaoProvider().getMatPlantEntityDao();
				Filter<MatPlantEntity> matPlantFilter = new FilterImpl<MatPlantEntity>();
				matPlantFilter.addExpression(Restrictions.eq(MatPlantEntity.ATTR_SAP_MATERIAL_ID,
						sapMaterial.getSapMaterialId()));
				matPlantFilter.addExpression(Restrictions.eq(MatPlantEntity.ATTR_PLANT_CD, orderItemPlant.getPlantCd()));
				Authorizer.disableNextAccessRestriction();
				MatPlantEntity matPlant = matPlantDao.findSingle(matPlantFilter);
				materialDescription = (matPlant != null) ? matPlant.getBrandSalesName() : "";
			}
			salesDocumentItem.setMaterialDescription(materialDescription);

			// If cash sales we send the net price
			if (sapContract != null) {
				SareaEntity sarea = orderItem.getOrder().getSarea();
				if (orderService.isCashSales(sapContract, sarea)) {
					InvoiceBean invoiceBean = orderService.getInvoiceBean(orderItem);
					if (invoiceBean != null) {
						BigDecimal netPrice = invoiceBean.getNetPrice();
						if (netPrice != null) {
							salesDocumentItem.setNetPrice(netPrice.toPlainString());
						}
					}
				}
			}

			return result = salesDocumentItem;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	/**
	 * Returns the SAP storage location for the order item. This will be used in the
	 * sales document, transfer document, and shipment document that are sent to SAP.
	 * This method takes into account goods movement and chooses between actual and
	 * virtual (= in transit) storage location.
	 * @param orderItem The item for which to determine the storage location
	 * @param sp Originating shipping point of the order (in normal/forward case) or the return shipping point in case of return item.
	 * @return The SAP storage location of the order item. Can be null if there is no
	 * storage location defined in the DB (the corresponding DB fields are nullable).
	 */
	private String sapStorageLocation(OrderItemEntity orderItem, ShippingpointEntity sp) {
		final String sloc;

		if (!canHaveStorageLocation(orderItem)
				|| isServiceMaterialItem(orderItem) 
				|| LogonUtil.isReturnPickupOrder(orderItem.getOrder().getDeliveryType().getDeliveryTypeCd())) {
			sloc = null;
		}
		else {
			sloc = goodsMovementService.getStorageLocationForSapDocuments(orderItem, sp);
		}
		return sloc;
	}

	
	/**
	 * Returns the value of the CanHaveStorageLocationFlag for the material belonging to the orderItem.
	 * It the flag is false, this means that any storage location info shouldn't be considered, when
	 * sending to SAP (must be empty).
	 * 
	 * @param orderItem
	 * @return
	 */
	private boolean canHaveStorageLocation(OrderItemEntity orderItem) {
		// [[
		boolean result = false;
		Object[][] params = {{"orderItem", orderItem}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "canHaveStorageLocation", params);
		}
		// ]]
		try {
			if (orderItem == null) {
				throw new IllegalArgumentException("orderItem must not be null");
			}
			SapMaterialEntity sapMaterial = orderItem.getSapMaterial();
			MaterialPackageBsEntity materialPackageEntity = sapMaterial.getMaterialPackage();
			return result = materialPackageEntity.getCanHaveStorageLocationFlag();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Checks if the order item has a service material
	 * @param orderItem an order item
	 * @return true if the logon package type code of the order item is "SERVICE", false otherwise.
	 */
	boolean isServiceMaterialItem(OrderItemEntity orderItem)
	{
		if (orderItem == null) {
			throw new IllegalArgumentException("orderItem must not be null");
		}
		
		final SapMaterialEntity sapMaterial = orderItem.getSapMaterial(); // not nullable
		final MaterialPackageBsEntity materialPackageType = sapMaterial.getMaterialPackage(); // not nullable
		final PackageTypeBsEntity packageType = materialPackageType.getLogonPackageType(); // nullable!
		
		if (packageType == null) {
			final String materialPackageCd = sapMaterial.getMaterialPackage().getMaterialPackageCd();
			LOG.warn("No logon package type defined for SAP material type '{}'.", new Object[] { materialPackageCd });
			return false;
		}
		else if (packageType.getPackageTypeCd() == Constant.PACKAGE_TYPE_SERVICE) {
			return true;
		}
		else {
			return false;
		}
	}

	// 2-Dec-2014/nhattan: Implemented according to /share/doc/crh/logon_2_2/spec/tech/latest/ifc_sap_send/released/20141120_Specification_Interface_DeliveryDocument_OUT_LRQ_041_V1_40.pdf
	private final String getDispatchDeliveryNumber(OrderEntity order, boolean isCreditNote, CreditNoteEntity creditNote) {
		
		String dispatchDeliveryNumber = getOrderNumber(order);
		
		if (isCreditNote) {
			final OrderItemEntity orderItem = creditNote.getOrderItem();
			final String sentOrderItemNo = getOrderItemNo(orderItem);
			dispatchDeliveryNumber = dispatchDeliveryNumber + RETURN_INFIX + sentOrderItemNo;
		}
		
		return dispatchDeliveryNumber;
	}

	// 2-Dec-2014/nhattan: Implemented according to /share/doc/crh/logon_2_2/spec/tech/latest/ifc_sap_send/released/20141120_Specification_Interface_DeliveryDocument_OUT_LRQ_041_V1_40.pdf
	// TODO Oct 30, 2014/hosszub: Can this method be public?
	/**
	 * Returns the LIFEX number, otherwise known as the ExternalDeliveryNumber of an order.  
	 * @param order
	 * @return
	 */
	protected final String getExternalDeliveryNumber(OrderEntity order) {
		
		final String sequential = order.getOrderNumberFromSeqUsage();
		final String lds = order.getLdsDeliveryNoteNo();
		final String logon = getOrderNumber(order);
		
		final String externalDeliveryNumber;
		if (StringUtils.isNotEmpty(lds) && order.getSarea().getDelnoteUsesWsNumberFlag()) {
			externalDeliveryNumber = lds;
		}
		else if (StringUtils.isNotEmpty(sequential)) {
			externalDeliveryNumber = sequential;
		}
		else {
			externalDeliveryNumber = logon;
		}
		
		return externalDeliveryNumber;
	}
	
	/**
	 * Used to distinguish between 3 use cases of return quantity handling. We
	 * have:
	 * 
	 * <pre>
	 * Standard:                a) L = 25 , R = --,         D = 25
	 * Return before SenttoSAP: b) L = 25 , R = 10, no CN,  D = 15
	 * Return after SenttoSAP:  c) L = 25 , R = 10, [x]CN,  D = 15
	 * </pre>
	 * 
	 * Depending on the use cases we should:
	 * 
	 * <pre>
	 * a) send 1 item, D=25
	 * b) when sending to SAP, treat as CN: 1 item D=25 + 1 line item recognizable as D=credit 10
	 * c) Order 1: originally sent 1 item D=25 + later: Order 2: a credit note order with 1 item D=credit 10
	 * d) when sending to SAP, treat as if there would be no returns at all because external system sends the credit to SAP:
	 * 1 item D=25
	 * </pre>
	 * 
	 * TODO: We should make a distinction on use cases rather than item types!
	 * (There can be more item types in the future, and it will make the code
	 * harder to maintain.)
	 */
	public enum ItemType {
		/**
		 * Use case a): Normal item with no return quantity.
		 */
		STANDARD_ITEM,
		/**
		 * Use case c): Credit note item.
		 */
		CREDIT_NOTE_ITEM,
		/**
		 * Use case b) Real item with return quantity re-added to the delivered
		 * quantity, but the return quantity will be sent as a CN, see
		 * {@link #VIRTUAL_RETURN_ITEM}.
		 */
		REAL_ITEM_WITH_RETURN_QUANTITY,
		/**
		 * Use case b) Virtual item for the return quantity.
		 * 
		 * @see {@link #REAL_ITEM_WITH_RETURN_QUANTITY}
		 */
		VIRTUAL_RETURN_ITEM,
		/**
		 * Use case d) Real item with return quantity re-added to the delivered
		 * quantity, but the return quantity will not be sent at all, as an external  system
		 * already sent to SAP.
		 */REAL_ITEM_WITH_RETURN_QUANTITY_NO_ADJUSTMENT

	}

	public static class CreateSalesDocumentItemParams {

		private OrderItemEntity orderItem;

		private String plant;

		private String returnPlant;

		private ShippingpointEntity shippingpoint;
		
		private ShippingpointEntity returnShippingpoint;

		private String freeOfChargeIndicator;

		private String meansOfTransportId;

		private String originalDocumentForReturns;

		private String originalItem;

		private ItemType itemType;

		private boolean belongsToQuantityControlledContract;

		public OrderItemEntity getOrderItem() {
			return orderItem;
		}

		public void setOrderItem(OrderItemEntity orderItem) {
			this.orderItem = orderItem;
		}

		public String getPlant() {
			return plant;
		}

		public void setPlant(String plant) {
			this.plant = plant;
		}
		
		public String getReturnPlant() {
			return returnPlant;
		}

		public void setReturnPlant(String returnPlant) {
			this.returnPlant = returnPlant;
		}
		
		public ShippingpointEntity getShippingpoint() {
			return shippingpoint;
		}

		public void setShippingpoint(ShippingpointEntity shippingpoint) {
			this.shippingpoint = shippingpoint;
		}
		
		public ShippingpointEntity getReturnShippingpoint() {
			return returnShippingpoint;
		}

		public void setReturnShippingpoint(ShippingpointEntity returnShippingpoint) {
			this.returnShippingpoint = returnShippingpoint;
		}

		public String getFreeOfChargeIndicator() {
			return freeOfChargeIndicator;
		}

		public void setFreeOfChargeIndicator(String freeOfChargeIndicator) {
			this.freeOfChargeIndicator = freeOfChargeIndicator;
		}

		public String getMeansOfTransportId() {
			return meansOfTransportId;
		}

		public void setMeansOfTransportId(String meansOfTransportId) {
			this.meansOfTransportId = meansOfTransportId;
		}

		public String getOriginalDocumentForReturns() {
			return originalDocumentForReturns;
		}

		public void setOriginalDocumentForReturns(String originalDocumentForReturns) {
			this.originalDocumentForReturns = originalDocumentForReturns;
		}

		public String getOriginalItem() {
			return originalItem;
		}

		public void setOriginalItem(String originalItem) {
			this.originalItem = originalItem;
		}

		public ItemType getItemType() {
			return itemType;
		}

		public void setItemType(ItemType itemType) {
			this.itemType = itemType;
		}

		public boolean isQuantityControlledContract() {
			return belongsToQuantityControlledContract;
		}

		public void setIsQuantityControlledContract(boolean belongsToQuantityControlledContract) {
			this.belongsToQuantityControlledContract = belongsToQuantityControlledContract;
		}
	}

	private final static class VehicleFinder {

		private final TruckEntity truck;
		private final WagonEntity wagon;
		private final ContainerEntity container;
		private final SimpleVehicleEntity simpleVehicle;

		public VehicleFinder(OrderEntity order, boolean includeContainer, boolean includeLoad, boolean required) {

			if (LogonUtil.isReturnDistributionDest(order.getDistributionDest().getDistributionDestCd()) 
					|| LogonUtil.isRailRepairOrder(order.getDeliveryType().getDeliveryTypeCd(), order.getDistributionDest().getDistributionDestCd())) {
				container = null;
				truck = null;
				wagon = order.getDeliveredWagon();
				simpleVehicle = null;
			}
			else {
				//
				final OrderItemEntity firstBagOrBulkOrderItem = getFirstBagOrBulkOrderItem(order);

				LoadEntity tmpLoad = null;
				if (firstBagOrBulkOrderItem != null) {
					if (LogonPackageHandlingUtil.isPallet(firstBagOrBulkOrderItem) && LogonUtil.isReturnPickupOrder(order.getDeliveryType().getDeliveryTypeCd())) {
						tmpLoad = null;
						required = false;
					}
					else {
						tmpLoad = firstBagOrBulkOrderItem.getLoad();
					}
				}
				else {	// service only order (LRQ 070.3 / LOGON-4743)
					tmpLoad = null;
					required = false;
				}
	
				final LoadEntity load = tmpLoad;
				
				// If the order status is delivered
				if ((order.getDeliveredTruck() != null) || (order.getDeliveredWagon() != null) 
						|| (order.getDeliveredSimpleVehicle() != null)) {
					container = null;
					truck = order.getDeliveredTruck();
					wagon = order.getDeliveredWagon();
					simpleVehicle = order.getDeliveredSimpleVehicle();
				}
				// If the order status is loaded
				else if (includeLoad && load != null) {
					if (includeContainer) {
						container = load.getContainer();
					}
					else {
						container = null;					
					}
					truck = load.getTruck();
					wagon = load.getWagon();
					simpleVehicle = load.getSimpleVehicle();				
				}
				else {
					container = null;
					truck = null;
					wagon = null;
					simpleVehicle = null;				
				}
			}

			if (truck == null && wagon == null && container == null && simpleVehicle == null && required) {
				throw new LogonException("Cannot find license plate number for order id = '" + order.getOrderId()
						+ "', order number = '" + order.getOrderNumber() + "'", Reason.RES_ERROR_IMPL);
			}
		}

		public String getSapVehicleNo() {
			if (truck != null) {
				return truck.getSapVehicleNo();
			}
			else if (wagon != null) {
				return wagon.getSapVehicleNo();
			} 
			else if (simpleVehicle != null) {
				return simpleVehicle.getSapVehicleNo();
			}
			else {
				return null;
			}
		}

		public String getLicensePlateNo() {
			if (container != null) {
				return container.getContainerNo();
			}
			else if (truck != null) {
				return truck.getTruckPlateNo();
			}
			else if (wagon != null) {
				return wagon.getWagonNo();
			} 
			else if (simpleVehicle != null) {
				return simpleVehicle.getVehicleRegNo();
			}
			else {
				return null;
			}
		}
	}

	private boolean isTransportRelevant(OrderEntity order, String function) {
		
		final DeliveryTypeBsEntity deliveryTypeBs = order.getDeliveryType();
		final Long deliveryType = deliveryTypeBs.getDeliveryTypeCd();
		
		// check service delivery
		if (deliveryType == Constant.DELIVERY_TYPE_SERVICE) {
			return false;
		}
		
		// check MESCOD (function)
		if (function.equals(SalesDocumentDTO.FUNCTION_RET)
				|| function.equals(TransferDeliveryDocumentDTO.FUNCTION_INBOUND_TRANSFER_RETURN)
				|| function.equals(TransferDeliveryDocumentDTO.FUNCTION_OUTBOUND_TRANSFER_RETURN)) {
			return false;
		}
		
		return true;
	}
	
	private boolean isPickupOrScheduledPickup(OrderEntity order) {
		
		final DeliveryTypeBsEntity deliveryTypeBs = order.getDeliveryType();
		final Long deliveryType = deliveryTypeBs.getDeliveryTypeCd();
		
		if ((deliveryType == Constant.DELIVERY_TYPE_PICKUP
					|| deliveryType == Constant.DELIVERY_TYPE_BLOCKTRAIN_PICKUP
					|| deliveryType == Constant.DELIVERY_TYPE_SHIP_PICKUP
					|| deliveryType == Constant.DELIVERY_TYPE_RETURN_PICKUP)
			 || ( LogonUtil.isScheduledPickupOrder(order))){
			return true;
		}
		
		return false;
	}
	
	private boolean isOwnFleet(OrderEntity order) {
		
		final TruckEntity truck = order.getDeliveredTruck();
		final SimpleVehicleEntity simpleVehicle = order.getDeliveredSimpleVehicle();
		final CarrierEntity carrier = order.getCommCarrier();
		
		CompanycodeBsEntity vehicleCompanycode = null;
		if (truck != null) {
			if (truck.getSapVehicleIsInternalFlag() != null && truck.getSapVehicleIsInternalFlag()) {
				final String sapVehicleNo = truck.getSapVehicleNo();
				if (sapVehicleNo != null) {
					final ReadOnlyDao<SapVehicleEntity> dao = getDaoProvider().getSapVehicleEntityDao();
					final Filter<SapVehicleEntity> f = dao.createFilter();
					f.addExpression(Restrictions.eq(SapVehicleEntity.ATTR_SAP_VEHICLE_NO, sapVehicleNo));
					
					try {
						final SapVehicleEntity sapVehicle = dao.findSingle(f);
						if (sapVehicle != null) {
							vehicleCompanycode = sapVehicle.getCompanycode();
						}
						else {
							throw new LogonException("Cannot determine fleet type for order "
									+ order.getOrderNumber().toString()
									+ " because the vehicle's company code cannot be determined "
									+ "(no sap vehicle with sap vehicle no "
									+ sapVehicleNo
									+ ").",
									Reason.RES_ERROR_UNSPECIFIED);
						}
					}
					catch (NonUniqueResultException e) {
						throw new LogonException("Cannot determine fleet type for order "
								+ order.getOrderNumber().toString()
								+ " because the vehicle's company code cannot be determined "
								+ "(multiple sap vehicles with sap vehicle no "
								+ sapVehicleNo
								+ ").",
								e);
					}
				}
				else {
					throw new LogonException("Cannot determine fleet type for order "
							+ order.getOrderNumber().toString()
							+ " because the vehicle's company code cannot be determined "
							+ "(sap vehicle no field empty of delivering truck).",
							Reason.RES_ERROR_UNSPECIFIED);
				}
			}
			else {
				return false;
			}
		}
		else if (simpleVehicle != null) {
			if (simpleVehicle.getSapVehicleIsInternalFlag() != null && simpleVehicle.getSapVehicleIsInternalFlag()) {
				vehicleCompanycode = simpleVehicle.getCompanycode();
			}
			else {
				return false;
			}
		}
		else if (carrier != null) {
			final SapVendorEntity vendor = carrier.getSapVendor();
			return vendor.getVnIsInternal();
		}
		
		if (vehicleCompanycode == null) {
			throw new LogonException("Cannot determine fleet type for order "
					+ order.getOrderNumber().toString()
					+ " because the vehicle's company code cannot be determined.",
					Reason.RES_ERROR_UNSPECIFIED);
		}
		
		final CompanycodeBsEntity orderCompanycode = order.getSarea().getSorg().getCompanycode();
		if (orderCompanycode.getCompanycodeCd().equals(vehicleCompanycode.getCompanycodeCd())) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * As part of the Advance Payment (AP) extension (LOGON-6624) and Restricted Quantities enhancement, we need
	 * to expand all order items which point to quantity controlled (QC) contract item. This QC contract item
	 * is a virtual item which represents a set of member contract items. As part of the expansion, the QC order
	 * item is replaced with N "virtual" order items each pointing to a member contract item in the QC group.
	 *
	 * Furthermore, since we are expanding the list with "virtual" items we reset the order item position
	 * on each order item. We do this by setting the SENT_ORDER_ITEM_NO on each order item. For non-Quantity Controlled
	 * order items, we need this value set anyway so that it gets persisted to the DB when our session context
	 * closes. For the "virtual" order items, we collect these values in the end and set them in the special
	 * table TLGNC_ORDER_ITEM_CONTRACT_ITEM
	 *
	 * Note that all virtual order items created are detached from the current session. This is fine
	 * since we are not persisting them anyway, but rather, we just need them to make the sending of
	 * the documents to SAP work (SAP requires a 1-1 mapping between order item and contract item).
	 * Expanding here allows us to keep the rest of the document sending/generation code more or less
	 * untouched.
	 */
	private List<OrderItemEntity> expandQuantityControlledOrderItems(List<OrderItemEntity> sortedItemsByPosition) {
		List<OrderItemEntity> result = new ArrayList<OrderItemEntity>();
		try {
			boolean virtualItemsAdded = false;

			//--- Create the virtual items if necessary ---//
			for (OrderItemEntity orderItem : sortedItemsByPosition) {
				if (isQuantityControlledContractItem(orderItem)) {
					List<OrderItemEntity> virtualOrdertems = expandQuantityControlledOrderItem(orderItem);
					result.addAll(virtualOrdertems);

					virtualItemsAdded = true;

					if (LOG.isDebugEnabled()) {
						LOG.debug(String.format(
								"Order Item [id: %s, d.qty: %s, r.qty: %s, ci.id: %s] => ",
								orderItem.getOrderItemId(),
								orderItem.getDeliveredQuantity(),
								orderItem.getReturnedQuantity(),
								orderItem.getSapContractItem().getSapContractItemId()));

						for (OrderItemEntity virtual: virtualOrdertems) {
							LOG.debug(String.format(
									"    Virtual Order Item[id: %s, d.qty: %s, r.qty: %s, ci.id: %s]",
									virtual.getOrderItemId(),
									virtual.getDeliveredQuantity(),
									virtual.getReturnedQuantity(),
									virtual.getSapContractItem().getSapContractItemId()));
						}
					}
				}
				else {
					result.add(orderItem);
				}
			}

			//--- Adjust position numbers if virtual items were added ---//
			if (virtualItemsAdded) {
				long position = 0L;
				for (OrderItemEntity orderItem : result) {
					position += 10L;
					orderItem.setSentOrderItemNo(position);
				}
			}
			else {
				// in this case, take the order's current item-no and use it as the outgoing item no
				// since our order items are still attached to the current session, they will be
				// saved on close
				for (OrderItemEntity orderItem : result) {
					orderItem.setSentOrderItemNo(orderItem.getOrderItemNo());
				}
			}
		}
		catch (LogonException exc) {
			throw exc;
		}
		catch (Exception exc) {
			LOG.error("Creating virtual order items failed", exc);
			throw new LogonException(exc, Reason.RES_ERROR_IMPL);
		}

		return result;
	}

	/**
	 * This method takes a Quantity Controlled (Advance Payment or Restricted Quantity) order item and
	 * creates N "virtual" order items each pointing to a valid SAP contract item.
	 *
	 * The algorithm depends on the view VLGNC_SENT_CONTRACT_ITEM_QTY which computes the remaining quantity
	 * for a set of SAP contract items using the information in TLGNC_ORDER_ITEM_CONTRACT_ITEM and
	 * TLGNS_SAP_CONTRACT_ITEM.
	 */
	private List<OrderItemEntity> expandQuantityControlledOrderItem(OrderItemEntity orderItem) {
		//--- Our list of virtual order items ---//
		List<OrderItemEntity> orderItems;

		boolean inReplayMode = true;

		final SapContractItemEntity apContractItemGroupLeader = orderItem.getSapContractItem();

		//--- Setup our base quantities for computations ---//
		BigDecimal orderItemReturnedQty = getReturnedQuantityInContractUoM(
				orderItem,
				apContractItemGroupLeader);

		BigDecimal orderItemDeliveredQty = getDeliveredQuantityInContractUoM(
				orderItem,
				apContractItemGroupLeader);

		//--- Pass 1: Distribute the delivered quantity across contract item members ---//
		orderItems = mapQuantityControlledOrderItems(Collections.singletonList(orderItem));
		if (orderItems.isEmpty()) {
			inReplayMode = false;
			orderItems = distributeQuantityOnOrderItem(orderItem, orderItemDeliveredQty);
		}

		//--- Pass 2: Distribute the returned quantity from youngest to oldest ---//
		if (orderItemReturnedQty.compareTo(BigDecimal.ZERO) > 0) {
			// we will modify this array as part of this process
			ArrayList<OrderItemEntity> reversedOrderItems = new ArrayList<OrderItemEntity>(orderItems);
			Collections.reverse(reversedOrderItems);

			while (orderItemReturnedQty.compareTo(BigDecimal.ZERO) > 0) {
				if (reversedOrderItems.isEmpty()) {
					String msg = String.format(
							"No remaining order items to distribute returned quantity: %s_%s, order: %s_%s",
							apContractItemGroupLeader.getSapContractItemId(),
							apContractItemGroupLeader.getContractItemNo(),
							orderItem.getOrder().getOrderNumber(),
							orderItem.getOrderItemNo());

					LOG.error(msg);
					throw new LogonException(msg, Reason.RES_IMPL_BUG);
				}

				Iterator<OrderItemEntity> it = reversedOrderItems.iterator();
				while (it.hasNext()) {
					OrderItemEntity virtualOrderItem = it.next();

					// all quantites are expressed in the UoM of the contract item
					virtualOrderItem.setReturnedUom(apContractItemGroupLeader.getTargetQuantityUom());

					// NOTE: we always subtract the entity's returned quantity from its
					// current delivered quantity. The reason is that this is the
					// general contract throughout the application.

					BigDecimal deliveredQty = virtualOrderItem.getDeliveredQuantity();

					if (orderItemReturnedQty.compareTo(deliveredQty) > 0) {
						// we return the entire used quantity here and continue distributing
						virtualOrderItem.setReturnedQuantity(deliveredQty);

						// if we are replaying a sent order item, the delivery quantity already reflects
						// the amount which was returned the last time so we do not subtract again from
						// this total
						if (! inReplayMode) {
							virtualOrderItem.setDeliveredQuantity(BigDecimal.ZERO);
						}

						orderItemReturnedQty = orderItemReturnedQty.subtract(virtualOrderItem.getReturnedQuantity());
						it.remove(); // nothing remaining here
					}
					else {
						// ok, we are returning less than we delivered - we have it covered by this item
						virtualOrderItem.setReturnedQuantity(orderItemReturnedQty);

						// if we are replaying a sent order item, the delivery quantity already reflects
						// the amount which was returned the last time so we do not subtract again from
						// this total
						if (! inReplayMode) {
							virtualOrderItem.setDeliveredQuantity(deliveredQty.subtract(orderItemReturnedQty));
						}

						orderItemReturnedQty = BigDecimal.ZERO;
						break;
					}
				}
			}
		}

		//--- Results ---//
		return orderItems;
	}

	/**
	 * Computes the distribution of the given quantity across all members of the given item's SAP contract item
	 * and returns a list of "virtual" order item entities each pointing to a valid, SAP contract item member.
	 *
	 * The given <em>orderItem</em> is assumed to be linked with a quantity controlled SAP contract item. That
	 * is, the item's SAP contract item is either an Advance Payment or Quantity-Restricted group leader. Using
	 * this group leader, we retrieve the list of member contract items that still have a remaining quantity
	 * and attempt to distribute <em>quantityToDistribute</em> across the items.
	 *
	 * Note that this method does not support REPLAY in general - it will always create a new set of items
	 * based on the information returned by the view VLGNC_SENT_CONTRACT_ITEM_QTY. The method
	 * {@link #mapQuantityControlledOrderItems(java.util.List)} can be used to first read in the list of
	 * sent items and only if that list is empty, should this method be called.
	 *
	 * @param orderItem the order item whose SAP contract item is an AP group leader
	 * @param quantityToDistribute is the quantity that we need to distribute over a set of member contract items
	 *
	 * @return a List of OrderItemEntity instances each pointing to a valid SAP contract member item. The
	 * 	returned OrderItemEntity instances are in a sense "virtual" since they are created dynamically (
	 * 	or read in from the TLGNC_ORDER_ITEM_CONTRACT_ITEM table in case of a replay) but do not really exist
	 * 	in our system otherwise.
	 */
	public List<OrderItemEntity> distributeQuantityOnOrderItem(
			OrderItemEntity orderItem,
			BigDecimal quantityToDistribute) {

		//--- Our list of virtual order items ---//
		List<OrderItemEntity> orderItems = new ArrayList<OrderItemEntity>();

		final SapContractItemEntity apContractItemGroupLeader = orderItem.getSapContractItem();

		BigDecimal orderItemReturnedQty = getReturnedQuantityInContractUoM(
				orderItem,
				apContractItemGroupLeader);

		// the returned qty is already subtracted from the delivered qty so we need to add it back in
		quantityToDistribute = quantityToDistribute.add(orderItemReturnedQty);

		ReadOnlyDao<SentContractItemQtyEntity> dao =
				getDaoProvider().getSentContractItemQtyEntityDao();

		Filter<SentContractItemQtyEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(
				SentContractItemQtyEntity.ATTR_AP_GROUP_LEADER_ID,
				apContractItemGroupLeader.getSapContractItemId()));
		filter.addExpression(Order.asc(SentContractItemQtyEntity.ATTR_CONTRACT_ITEM_NO));

		// items are returned ordered from oldest to youngest
		List<SentContractItemQtyEntity> sentItemsWithQuantities = dao.find(filter);

		// actual contact item entities (the view only returns the ids)
		// TODO 2013-Sep-20/tito: there must be a better way of doing this - the view generated
		// code should return the materialized value. Possible?
		Map<Long, SapContractItemEntity> contractItemsById = new HashMap<Long, SapContractItemEntity>();
		Map<Long, SapContractItemEntity> existingContractItems = findContractItemsBySentContractItemQty(sentItemsWithQuantities);
		if (!existingContractItems.isEmpty()) {
			contractItemsById.putAll(existingContractItems);
		}

		// And this is our virtual mapping as outlined in the specification.
		OrderItemEntity virtualOrderItem = null;

		//--- Distribute the delivered quantity from oldest to youngest ---//
		while (quantityToDistribute.compareTo(BigDecimal.ZERO) > 0) {
			if (sentItemsWithQuantities.isEmpty()) {
				String msg = String.format(
						"No remaining quantities for distributing delivered quantity: "
								+ "contract-item: %s_%s, order: %s_%s",
						apContractItemGroupLeader.getSapContractItemId(),
						apContractItemGroupLeader.getContractItemNo(),
						orderItem.getOrder().getOrderNumber(),
						orderItem.getOrderItemNo());

				LOG.warn(msg);

				// Find the youngest contract item and put the excess quantity on that one
				SapContractItemEntity youngestContractItem;
				if (virtualOrderItem == null) {
					ReadOnlyDao<SapContractItemEntity> ciDao = 
							getDaoProvider().getSapContractItemEntityDao();

					Filter<SapContractItemEntity> ciFilter = ciDao.createFilter();
					ciFilter.addExpression(Restrictions.eq(
							SapContractItemEntity.ATTR_AP_GROUP_LEADER_ID,
							apContractItemGroupLeader.getSapContractItemId()));
					ciFilter.addExpression(Order.desc(SapContractItemEntity.ATTR_CONTRACT_ITEM_NO));

					List<SapContractItemEntity> contractItems = ciDao.findNRecs(0, 1, ciFilter);
					if (contractItems.isEmpty()) {
						LOG.error("There is no contract item having this group leader: " + apContractItemGroupLeader.getSapContractItemId());
						throw new LogonException(Reason.RES_ERROR_IMPL);
					}
					else {
						youngestContractItem = contractItems.get(0);
						contractItemsById.put(youngestContractItem.getSapContractItemId(), 
								youngestContractItem);
					}
				}
				else {
					youngestContractItem = virtualOrderItem.getSapContractItem();
				}

				SentContractItemQtyEntity sentItemQuantity = new SentContractItemQtyEntity();
				sentItemQuantity.setSapContractItemId(youngestContractItem.getSapContractItemId());
				sentItemQuantity.setRemainingQuantity(quantityToDistribute);
				sentItemsWithQuantities.add(sentItemQuantity);
			}

			Iterator<SentContractItemQtyEntity> it = sentItemsWithQuantities.iterator();
			while (it.hasNext()) {
				SentContractItemQtyEntity entity = it.next();

				BigDecimal remainingQty = entity.getRemainingQuantity();
				if (remainingQty.compareTo(BigDecimal.ZERO) <= 0) {
					it.remove();  // nothing remaining
					continue;
				}

				SapContractItemEntity contractItem =
						contractItemsById.get(entity.getSapContractItemId());

				try {
					virtualOrderItem = (OrderItemEntity) BeanUtils.cloneBean(orderItem);
				}
				catch (Exception exc) {
					LOG.error("Creating virtual order items failed", exc);
					throw new LogonException(exc, Reason.RES_ERROR_IMPL);
				}

				virtualOrderItem.setSapContractItem(contractItem);

				// all quantites are expressed in the UoM of the contract item
				virtualOrderItem.setDeliveredUom(apContractItemGroupLeader.getTargetQuantityUom());

				orderItems.add(virtualOrderItem);

				if (quantityToDistribute.compareTo(remainingQty) > 0) {
					// we use up all of this contract item's quantity and continue distributing
					virtualOrderItem.setDeliveredQuantity(remainingQty);

					quantityToDistribute = quantityToDistribute.subtract(remainingQty);
					it.remove(); // nothing remaining here
				}
				else {
					// the current contract item satisfies our entire quantity needs
					virtualOrderItem.setDeliveredQuantity(quantityToDistribute);
					quantityToDistribute = BigDecimal.ZERO;
					break;
				}
			}
		}

		//--- SANITY CHECK: we have to have at least 1 virtual item since our original
		// 				 	order item pointed to the advance payment group leader
		if (orderItems.isEmpty()) {
			String msg = String.format(
					"No virtual order items created for: order %s_%s",
					orderItem.getOrder().getOrderNumber(),
					orderItem.getOrderItemNo());

			LOG.error(msg);
			throw new LogonException(msg, Reason.RES_IMPL_BUG);
		}

		return orderItems;
	}

	/**
	 * For each quantity controlled (i.e. Advance Payment, Quantity Restricted) order item we replace
	 * with the set of order items -> contract items created when sending the Sales document to SAP. That is,
	 * we require that this mapping exist in the TLGNC_ORDER_ITEM_CONTRACT_ITEM table.
	 *
	 * For non-quantity controlled order items, no mapping takes place and the order item in question is
	 * merely returned.
	 *
	 * @param orderItems a List of OrderItemEntity instances which can be a mix of quantity-controlled and
	 *                   non-quantity controlled items
	 *
	 * @return a List of OrderItemEntity instances which either represent a virtual order item entity
	 * 		   from the TLGNC_ORDER_ITEM_CONTRACT_ITEM mapping table or the order item passed in as part
	 * 		   of <em>orderItems</em> if it does not correspond to a quantity controlled item. All items
	 * 		   are sorted by the SENT_ITEM_NO in ascending order (i.e. from OLDEST to YOUNGEST)
	 */
	public List<OrderItemEntity> mapQuantityControlledOrderItems(List<OrderItemEntity> orderItems) {
		//--- Container for resulting ordered items sorted by SENT_ORDER_NO ---//
		List<OrderItemEntity> results = new ArrayList<OrderItemEntity>();

		//--- 2 step processs: Move over all non-Advance Payment order items directly to our
		//	  results list (which we will sort later) and save all Advance Payment order item ids
		//	  to query the TLGNC_ORDER_ITEM_CONTRACT_ITEM table ---//
		List<Long> apOrderItemIds = new ArrayList<Long>();

		for (OrderItemEntity orderItem : orderItems) {
			if (isQuantityControlledContractItem(orderItem)) {
				apOrderItemIds.add(orderItem.getOrderItemId());
			}
			else {
				results.add(orderItem);
			}
		}

		//--- Retrieve the advance payment order items already sent out ---//
		if (!apOrderItemIds.isEmpty()) {
			ReadOnlyDao<OrderItemContractItemEntity> dao =
					getDaoProvider().getOrderItemContractItemEntityDao();
			
			final Filter<OrderItemContractItemEntity> f = dao.createFilter();
			f.addExpression(DbUtils.createSplittedInRestrictionOfIds(
					OrderItemContractItemEntity.ATTR_ORDER_ITEM_ID,
					apOrderItemIds));

			List<OrderItemContractItemEntity> entities = dao.find(f);
			for (OrderItemContractItemEntity entity : entities) {
				// Create a new virtual order item mapped to the correct SAP contract item
				try {
					OrderItemEntity virtualOrderItem =
							(OrderItemEntity) BeanUtils.cloneBean(entity.getOrderItem());

					SapContractItemEntity contractItem = entity.getSapContractItem();

					virtualOrderItem.setSentOrderItemNo(entity.getSentOrderItemNo());
					virtualOrderItem.setSapContractItem(contractItem);

					results.add(virtualOrderItem);
				}
				catch (Exception exc) {
					LOG.error("Creating virtual order items failed", exc);
					throw new LogonException(exc, Reason.RES_ERROR_IMPL);
				}
			}

			//--- Now sort by sent_order_no in ascending order
			//
			//	  All order items have to have the SENT_ORDER_NO set - this is ensured by the patch
			//	  script as well as updateOrderItemSentPosition() ---//
			Collections.sort(results, new Comparator<OrderItemEntity>() {
				public int compare(OrderItemEntity o1, OrderItemEntity o2) {
					return o1.getSentOrderItemNo().compareTo(o2.getSentOrderItemNo());
				}
			});
		}

		return results;
	}

	/**
	 * For Advance Payment and Restricted Quantity order items we create a new entry in the
	 * TLGNC_ORDER_ITEM_CONTRACT_ITEM table. For non-Advance Payment order items, we save the
	 * current SENT_ORDER_NO.
	 */
	private void updateOrderItemSentPosition(List<OrderItemEntity> orderItemDtos) {
		ReadWriteDao<OrderItemContractItemEntity> oiCiDao =
				getDaoProvider().getOrderItemContractItemEntityDao();

		ReadWriteDao<OrderItemEntity> oiDao =
				getDaoProvider().getOrderItemEntityDao();

		for (OrderItemEntity dto: orderItemDtos) {
			if (!isQuantityControlledContractItem(dto)) {
				// update new SENT_ORDER_NO value
				oiDao.merge(dto, false);
			}
			else {
				// we use findById here since the OrderItemEntities are in the session cache
				OrderItemEntity orderItem = oiDao.findById(dto.getOrderItemId());

				OrderItemContractItemEntity entity = new OrderItemContractItemEntity();
				entity.setOrderItem(orderItem);
				entity.setSapContractItem(dto.getSapContractItem());
				entity.setSentOrderItemNo(dto.getSentOrderItemNo());
				entity.setDeliveredQuantity(dto.getDeliveredQuantity());
				entity.setDeletedFlag(Boolean.FALSE);

				oiCiDao.persist(entity, false);
			}
		}
	}

	private List<OrderItemEntity> expandOrderItemForCreditNote(OrderItemEntity orderItem) {
		if (!isQuantityControlledContractItem(orderItem)) {
			return Collections.singletonList(orderItem);
		}

		try {
			SapContractItemEntity apGroupLeader = orderItem.getSapContractItem();
			
			//--- Setup our base quantities for computations ---//
			BigDecimal creditNoteReturnedQtyInContractUom = getReturnedQuantityInContractUoM (
					orderItem, apGroupLeader);
			
			//--- Retrieve list of virtual order items sent to SAP from YOUNGEST to OLDEST ---//
			ReadWriteDao<OrderItemContractItemEntity> dao = getDaoProvider().getOrderItemContractItemEntityDao();

			Filter<OrderItemContractItemEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(
					OrderItemContractItemEntity.ATTR_ORDER_ITEM, orderItem));
			filter.addExpression(Order.desc(OrderItemContractItemEntity.ATTR_CTL_CRE_TS));

			List<OrderItemContractItemEntity> items = dao.find(filter);

			//--- Our list of virtual order items from youngest to oldest ---//
			List<OrderItemEntity> orderItems = new ArrayList<OrderItemEntity>();

			while (creditNoteReturnedQtyInContractUom.compareTo(BigDecimal.ZERO) > 0) {
				if (items.isEmpty()) {
					String msg = String.format(
							"No remaining items to distribute credit note returned quantity: %s_%s, order: %s_%s",
							apGroupLeader.getSapContractItemId(),
							apGroupLeader.getContractItemNo(),
							orderItem.getOrder().getOrderNumber(),
							orderItem.getOrderItemNo());

					LOG.error(msg);
					throw new LogonException(msg, Reason.RES_IMPL_BUG);
				}

				Iterator<OrderItemContractItemEntity> it = items.iterator();
				while (it.hasNext()) {
					OrderItemContractItemEntity entity = it.next();

					// Create a new virtual order item mapped to the correct SAP contract item
					OrderItemEntity virtualOrderItem =
							(OrderItemEntity) BeanUtils.cloneBean(entity.getOrderItem());
					orderItems.add(virtualOrderItem);

					virtualOrderItem.setSentOrderItemNo(entity.getSentOrderItemNo());
					virtualOrderItem.setSapContractItem(entity.getSapContractItem());

					BigDecimal deliveredQtyInContractUom = entity.getDeliveredQuantity();

					if (creditNoteReturnedQtyInContractUom.compareTo(deliveredQtyInContractUom) > 0) {
						// we return the entire used quantity here and continue distributing
						BigDecimal deliveredQuantityInReturnedUom = convertReturnedQuantityFromContractUoM(virtualOrderItem, apGroupLeader, deliveredQtyInContractUom);
						virtualOrderItem.setReturnedQuantity(deliveredQuantityInReturnedUom);
						virtualOrderItem.setDeliveredQuantity(BigDecimal.ZERO);

						// update our DB entity to reflect the decrease in delivered qty
						entity.setDeliveredQuantity(BigDecimal.ZERO);

						creditNoteReturnedQtyInContractUom = creditNoteReturnedQtyInContractUom.subtract(deliveredQtyInContractUom);
						it.remove(); // nothing remaining here
					}
					else {
						// ok, we are returning less than we delivered - we have it covered by this item			
						BigDecimal creditNoteReturnedQtyInReturnedUom = convertReturnedQuantityFromContractUoM(virtualOrderItem, apGroupLeader, creditNoteReturnedQtyInContractUom);
						virtualOrderItem.setReturnedQuantity(creditNoteReturnedQtyInReturnedUom);
						virtualOrderItem.setDeliveredQuantity(deliveredQtyInContractUom.subtract(creditNoteReturnedQtyInContractUom));

						// update our DB entity to reflect the decrease in delivered qty
						entity.setDeliveredQuantity(deliveredQtyInContractUom.subtract(creditNoteReturnedQtyInContractUom));

						creditNoteReturnedQtyInContractUom = BigDecimal.ZERO;
						break;
					}
				}
			}

			//--- SANITY CHECK: we have to have at least 1 virtual item since our original
			// 				 	order item pointed to the advance payment group leader
			if (orderItems.isEmpty()) {
				String msg = String.format(
						"No virtual order items created for credit note order %s_%s",
						orderItem.getOrder().getOrderNumber(),
						orderItem.getOrderItemNo());

				LOG.error(msg);
				throw new LogonException(msg, Reason.RES_IMPL_BUG);
			}

			//--- Results ---//
			return orderItems;
		}
		catch (LogonException exc) {
			throw exc;
		}
		catch (Exception exc) {
			LOG.error("Creating virtual order items failed", exc);
			throw new LogonException(exc, Reason.RES_ERROR_IMPL);
		}
	}

	// LOGON-6624: LRQ 303.0 Advance Payment  / Restricted Quantity Contracts
	// now that the order is transmitted, we need to update the TOTAL_SENT_QTY on each contract
	// item thereby reducing the overall available or open quantity.
	private void updateTotalSentQuantityOnOrderItems(List<OrderItemEntity> orderItems) {
		for (OrderItemEntity orderItem : orderItems) {
			SapContractItemEntity contractItem = orderItem.getSapContractItem();

			// service material order items, for example, do not have a contract
			if (contractItem != null && contractItem.getIsApGroupLeader()) {
				BigDecimal totalSentQty = contractItem.getTotalSentQuantity();
				if (totalSentQty == null) {
					totalSentQty = BigDecimal.ZERO;
				}

				BigDecimal convertedDeliveredQty =
						unitOfMeasurementConverterService.convertOrderItemQtyToContractItemUoM(
								orderItem,
								orderItem.getDeliveredQuantity(),
								orderItem.getDeliveredUom(),
								contractItem);

				BigDecimal newTotalSentQty = totalSentQty.add(convertedDeliveredQty);
				contractItem.setTotalSentQuantity(newTotalSentQty);
			}
		}
	}

	/**
	 * Find all SAP contract items for all quantity controlled order items.
	 *
	 * @return Map keyed by SAP contract item id, value is the contract item entity. This method never
	 * 		returns null, but can return an immutable empty map.
	 */
	private Map<Long, SapContractItemEntity> findContractItemsBySentContractItemQty(List<SentContractItemQtyEntity> 
			sentItemsWithQuantities) {
		
		if (sentItemsWithQuantities.isEmpty()) {
			return Collections.<Long, SapContractItemEntity>emptyMap();
		}
		
		//--- Retrieve all SAP Contract Items (we need the actual member entities) ---//
		final Set<Long> contractItemIds = new HashSet<Long>();
		for (SentContractItemQtyEntity dto: sentItemsWithQuantities) {
			contractItemIds.add(dto.getSapContractItemId());
		}
		
		//--- Lookup contract items ---//
		ReadOnlyDao<SapContractItemEntity> dao =
				getDaoProvider().getSapContractItemEntityDao();

		Filter<SapContractItemEntity> filter = dao.createFilter();
		filter.addExpression(DbUtils.createSplittedInRestrictionOfIds(
				SapContractItemEntity.ATTR_SAP_CONTRACT_ITEM_ID,
				new ArrayList<Long>(contractItemIds)));

		List<SapContractItemEntity> contractItems = dao.find(filter);

		//--- Assemble results for caller ---//
		Map<Long, SapContractItemEntity> result = new HashMap<Long, SapContractItemEntity>();
		for (SapContractItemEntity contractItem: contractItems) {
			result.put(contractItem.getSapContractItemId(), contractItem);
		}

		return result;
	}

	private BigDecimal getDeliveredQuantityInContractUoM(OrderItemEntity orderItem, SapContractItemEntity contractItem) {
		BigDecimal deliveredQty = orderItem.getDeliveredQuantity();
		if (deliveredQty == null) {
			String msg = "delivered quantity in base UoM is null. order item: "
					+ orderItem.getOrder().getOrderNumber() + "_" + orderItem.getOrderItemNo();

			LOG.error(msg);
			throw new LogonException(msg, Reason.RES_IMPL_BUG);
		}

		return unitOfMeasurementConverterService.convertOrderItemQtyToContractItemUoM(
				orderItem,
				deliveredQty,
				orderItem.getDeliveredUom(),
				contractItem);
	}

	private BigDecimal getReturnedQuantityInContractUoM(OrderItemEntity orderItem, SapContractItemEntity contractItem) {
		BigDecimal returnedQuantity = orderItem.getReturnedQuantity();
		if (returnedQuantity == null) {
			return BigDecimal.ZERO;
		}

		return unitOfMeasurementConverterService.convertOrderItemQtyToContractItemUoM(
				orderItem,
				returnedQuantity,
				orderItem.getReturnedUom(),
				contractItem);
	}
	
	private BigDecimal convertReturnedQuantityFromContractUoM(OrderItemEntity orderItem, SapContractItemEntity contractItem, BigDecimal quantity) {
		// [[
		BigDecimal result = null;
		Object[][] params = {{"orderItem", orderItem},
							{"contractItem", contractItem},
							{"quantity", quantity}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "convertReturnedQuantityFromContractUoM", params);
		}
		// ]]
		UomBsEntity fromUom = null;
		UomBsEntity toUom = null;
		try {
			if (quantity == null) {
				return result = BigDecimal.ZERO;
			}
			
			fromUom = contractItem.getTargetQuantityUom();
			toUom = orderItem.getReturnedUom();
			UomBsEntity materialBaseUom = orderItem.getSapMaterial().getBaseUom();
			
			Long loadedReturnedNumerator = orderItem.getLrUomNumerator();
			Long loadedReturnedDenumerator = orderItem.getLrUomDenominator();
			
			if (materialBaseUom.getUomCd().equals(fromUom.getUomCd()) && loadedReturnedNumerator != null && loadedReturnedDenumerator != null) {
				return result = unitOfMeasurementConverterService.convert(
						quantity,
						loadedReturnedNumerator,
						loadedReturnedDenumerator,
						false);
			}
			else {
				return result = unitOfMeasurementConverterService.convertQuantityFromContractItemUoM(
						orderItem,
						contractItem,
						quantity,
						toUom);
			}
		}
		catch (LogonConversionException e) {
			String msg = String.format(
					"Converting %s from UoM '%s' to UoM '%s' failed for order item: %s_%s",
					quantity,
					fromUom.getUomCd(),
					toUom.getUomCd(),
					orderItem.getOrder().getOrderNumber(),
					orderItem.getOrderItemNo());
		
			throw new LogonException(msg, e);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * In the WSDL it is the SNDPRN field.
	 * It tells SAP from which cluster the document was coming.
	 * 
	 * @return "LOGON_0X" where X = wsclient.sndprn from the config table.
	 */
	private String getPartnerNumberOfSender() {
		int partnerNumberOfSender = getConfigBean().getWsclientSndprn();
		StringBuilder sb = new StringBuilder(SNDPRN_PREFIX);
		sb.append(partnerNumberOfSender);
		String sndPrn = sb.toString();
		return sndPrn;
	}

	private List<OrderTpSapVendorEntity> getOrderTpSapVendorList(Long orderId) {
		ReadWriteDao<OrderTpSapVendorEntity> otsvDao = getDaoProvider().getOrderTpSapVendorEntityDao();
		Filter<OrderTpSapVendorEntity> filter = new FilterImpl<OrderTpSapVendorEntity>();
		filter.addExpression(Restrictions.eq(OrderTpSapVendorEntity.ATTR_ORDER_ID, orderId));
		filter.addExpression(Restrictions.eq(OrderTpSapVendorEntity.ATTR_TP_SAP_VENDOR + "." + TpSapVendorEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
		return otsvDao.find(filter);		
	}
	
	/**
	 * @param firstOrder
	 * @param realStart
	 * @return
	 */
	private Date convertDateToShippingTimeZone(LoadingpointEntity loadingpointEntity, Date realStart) {
		if(loadingpointEntity != null){
			Long weightSystemLpCd = loadingpointEntity.getWeighSystemLp().getWeighSystemLpCd();
			if (weightSystemLpCd == Constant.WEIGH_SYSTEM_LP_LOGON || weightSystemLpCd == Constant.WEIGH_SYSTEM_LP_WS) {
				ShippingpointEntity shippingpointEntity = loadingpointEntity.getShippingpoint();
				if(shippingpointEntity != null){
					String shippingTimeZone = shippingpointEntity.getTimezone().getTimezoneCd();
					if (shippingTimeZone != null) {
						DateTime convertedDateTime = convertDateToDataTimeInTimezone(realStart, shippingTimeZone);
						realStart = convertedDateTime.toLocalDateTime().toDate();
					}
				}
			}
		}
		return realStart;
	}
	
	private DateTime convertDateToDataTimeInTimezone(Date from, String toTz) {
		String appDefaultTimeZoneCd = DateTimeZoneFactory.getDefaultTimeZoneId();
		DateTimeZone fromDateTimeZone = DateTimeZoneFactory.forID(appDefaultTimeZoneCd);
		DateTimeZone targetTimeZone = DateTimeZoneFactory.forID(toTz);
		DateTime fromDate = new DateTime(from, fromDateTimeZone);
		DateTime toDate = fromDate.withZone(targetTimeZone);

		return toDate;
	}

	private void validateShippingPoint(OrderEntity order) {
		if (getShippingpoint(order) == null) {
			throw new LogonException("Misconfiguration: could not get Sap Shippingpoint number for order: " + order.getOrderNumber() + ".",
					Reason.RES_ERROR_SAP_SHIPPINGPOINT_MISCONFIGURED);
		}
	}

}


