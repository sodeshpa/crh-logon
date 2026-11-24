/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.store;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.crh.logon.business.services.beans.UomConversionResult;
import com.crh.logon.business.services.localinterfaces.UomConverterServiceLocalInterface;
import com.crh.logon.business.services.pdftools.layout.DeliveryNoteData;
import com.crh.logon.business.services.pdftools.layout.DeliveryNoteLabel;
import com.crh.logon.business.services.pdftools.layout.colldelnote.OrderItemTotalDTO;
import com.crh.logon.business.services.utils.TimeZone;
import org.apache.commons.lang.StringUtils;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.pdftools.layout.OrderItemDTO;
import com.crh.logon.business.services.utils.LogonStatusUtils;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.constants.Constant;
import com.crh.logon.pdftool.labelprovider.DTOMarker;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.SorgBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.BeanFactory;
import com.crh.logon.utils.LogonConversionException;
import com.crh.logon.utils.text.Transliterator;

/**
 * Provides the labels and data for the delivery note from the input order.
 * @author AdNovum Informatik AG
 */
@Named(value="collDeliveryNotePdfDataStore")
public class CollDeliveryNotePdfDataStore extends DeliveryNotePdfDataStore {
	
	@Inject
	UomConverterServiceLocalInterface unitOfMeasurementConverterService;
	
	public CollDeliveryNotePdfDataStore() {
		super();
	}
	
	public static CollDeliveryNotePdfDataStore getCollDeliveryNotePdfDataStore(DaoProvider daoProvider, Locale locale, CustorderEntity order, Transliterator transliterator) {
		CollDeliveryNotePdfDataStore store = BeanFactory.createBean("collDeliveryNotePdfDataStore", CollDeliveryNotePdfDataStore.class);
		
		store.setDaoProvider(daoProvider);
		store.initStore(locale, transliterator);
		
		if (order != null) {
			store.initializeData(order);
		}
		return store;
	}

	private static final JcanLogger LOG = JcanLoggerFactory
			.getLogger(CollDeliveryNotePdfDataStore.class);
	
	public static final int ROUNDING_SCALE = 2;
	
	private List<OrderItemTotalDTO> orderItemTotalDTOs;
	
	/* (non-Javadoc)
	 * @see com.crh.logon.business.services.utils.pdftools.DeliveryNotePdfTextStore#initializeLabels()
	 */
	@Override
	protected void initializeLabels() {		
		super.initializeLabels();
		if(locale != null) {
			addLabel(DeliveryNoteLabel.ORDER_NUMBER, Message.lookup(locale, "pdf.coll.orderno"));
		}
	}
	

	/**
	 * @param custOrder
	 */
	private void initializeData(CustorderEntity custOrder) {
		
		ReadWriteDao<OrderEntity> orderDao = daoProvider.getOrderEntityDao();
		Filter<OrderEntity> ordFilter = new FilterImpl<OrderEntity>();
		ordFilter.addExpression(Restrictions.eq(OrderEntity.ATTR_CUSTORDER, custOrder));
		List<OrderEntity> orders = orderDao.find(ordFilter);
		
		
		SareaEntity sareaBs = custOrder.getSarea();
		SorgBsEntity sorgBs = null;
		
		if (sareaBs != null) {
			sorgBs = sareaBs.getSorg();
			setVisibilitySettings(sareaBs);
		}
		
		if (sorgBs != null) {
			setDestAddress(sorgBs);
		}
		
		// order number
		String logonOrderNumber = (custOrder.getCustorderNumber() != null ? custOrder.getCustorderNumber().toString() : "");
		addData(DeliveryNoteData.ORDER_NR, logonOrderNumber);
		
		//time of the generation of this delivery note
		TimeZone deliveryPlantTZ = getTimeZoneForDeliveryPlant(custOrder.getShippingpoint());
		String delNoteVersionStr = deliveryPlantTZ.fmtAsDayMonthYearHourMinuteWithOffset(new Date());
		addData(DeliveryNoteData.DELIVERY_NOTE_DATE, delNoteVersionStr);
		
		// shipping point data
		ShippingpointEntity shippingpoint = custOrder.getShippingpoint();
		if (shippingpoint != null) {
			addData(DeliveryNoteData.SHIPPING_POINT, shippingpoint.getShippingpointName());
			PlantBsEntity plant = shippingpoint.getPlant();
			if (plant != null) {				
				addData(DeliveryNoteData.PLANT, plant.getPlantName());
			}
			
		}

		setDeliveryWindow(custOrder.getShiptoSapBp(), custOrder.getDeliveryFromDat(), custOrder.getDeliveryToDat());

		// customer reference number + SAP contract number
		String custRefNo = custOrder != null ? custOrder.getCustorderCustrefNo() : null;
		SapContractEntity sapContract = custOrder.getSapContract();
		String contractNo = sapContract != null ? sapContract.getSapContractNo() : null;
		
		// delivery type
		DeliveryTypeTxEntity deliveryTypeTxEntity = referentialDataService.findTxEntity(DeliveryTypeTxEntity.class, locale, custOrder.getDeliveryType().getDeliveryTypeCd());
		if (deliveryTypeTxEntity != null) {
			addData(DeliveryNoteData.DELIVERY_TYPE, deliveryTypeTxEntity.getTxt());
		}
		// incoterm
		addData(DeliveryNoteData.INCOTERM, sapContract != null ? sapContract.getIncotermsCd() : null);
		if (custRefNo != null || contractNo != null) {
			StringBuilder custRefNoAndContractNoTitleSb = new StringBuilder();
			StringBuilder custRefNoAndContractNoDataSb = new StringBuilder();
		
			if (custRefNo != null) {
				custRefNoAndContractNoTitleSb.append(formatText("pdf.deliverynote.custrefno", locale, null, null, null, null, null));
				custRefNoAndContractNoDataSb.append(custRefNo);
			}
			if (contractNo != null) {
				if (custRefNo != null) {
					custRefNoAndContractNoTitleSb.append(" / ");
					custRefNoAndContractNoDataSb.append(" / ");
				}
				
				contractNo = StringUtils.stripStart(contractNo, "0");
				custRefNoAndContractNoTitleSb.append(formatText("pdf.deliverynote.contractno", locale, null, null, null, null, null));
				custRefNoAndContractNoDataSb.append(contractNo);
				
				custRefNoAndContractNoTitleSb.append(':');
			}
			addData(DeliveryNoteData.CUST_REF_CONTRACT_NUMBER_TILE, custRefNoAndContractNoTitleSb.toString());
			addData(DeliveryNoteData.CUST_REF_CONTRACT_NUMBER_DATA, custRefNoAndContractNoDataSb.toString());
		}
		
		//set order specific data, distance, endload time
		Date latestLoadingTs = null;
		Date latestDeliveryTs = null;
		Long waitingTimeMins = Long.valueOf(0);
		Long distance = null;
		boolean distanceValid = true;
		for (OrderEntity orderEntity : orders) {
			// order closed, delivery time can be set
			Date deliveryDat = orderEntity.getActualDeliveryDat();
			if (deliveryDat != null && (latestDeliveryTs == null || deliveryDat.after(latestDeliveryTs))) {
				latestDeliveryTs = deliveryDat;
			}
			
			// distance
			if (distance == null) {				
				distance = orderEntity.getCommercialDistanceM();
			}
			if ( orderEntity.getCommercialDistanceM() != null && distanceValid) {
				distanceValid = distance.equals(orderEntity.getCommercialDistanceM());
			}
			
			// waiting time
			if (orderEntity.getExtraCostWaitingTimeMin() != null) {
				waitingTimeMins = waitingTimeMins + orderEntity.getExtraCostWaitingTimeMin();
			}
			
			// latest loading
			Iterator<?> iterator = orderEntity.getOrderItems().iterator();
			while ( iterator.hasNext() ) {
				OrderItemEntity item = (OrderItemEntity) iterator.next();
				LoadEntity load = item.getLoad();
				if (load != null && load.getLoadingTs() != null){
					Date loadingTs = item.getLoad().getLoadingTs();
					if( latestLoadingTs == null || loadingTs.after(latestLoadingTs)){
						latestLoadingTs = loadingTs;
					}
				}
			}
		}
		
		// waiting time
		if (waitingTimeMins.compareTo(Long.valueOf(0)) != 0) {			
			Long waitingTimeHours = waitingTimeMins / 60;
			waitingTimeMins = waitingTimeMins % 60;
			addData(DeliveryNoteData.WAITING_TIME, waitingTimeHours + ":" + waitingTimeMins);
		}
		
		if (distanceValid && distance != null) {
			addData(DeliveryNoteData.DISTANCE, Long.toString(distance / 1000));
		}
		
		if(latestLoadingTs != null){
			addData(DeliveryNoteData.LOAD_DATE, deliveryPlantTZ.fmtAsDayMonthYearHourMinuteWithOffset(latestLoadingTs));
		}
		if (latestDeliveryTs != null) {			
			addData(DeliveryNoteData.DELIVERY_DATE, deliveryPlantTZ.fmtAsDayMonthYearHourMinuteWithOffset(latestDeliveryTs));
		}
		
		OrderEntity firstOrder = (OrderEntity) custOrder.getOrders().get(0);
		
		// one time solto and shipto data
		final List<String> oneTimeSoldTo = new ArrayList<String>();
		oneTimeSoldTo.add(firstOrder.getOneTimeSoldtoName());
		oneTimeSoldTo.add(firstOrder.getOneTimeSoldtoStreetHouseno());

		final StringBuilder oneTimeCountryZipCity = new StringBuilder();
		oneTimeCountryZipCity.append(StringUtils.defaultString(firstOrder.getOneTimeSoldtoCountry()));
		oneTimeCountryZipCity.append(' ');
		oneTimeCountryZipCity.append(StringUtils.defaultString(firstOrder.getOneTimeSoldtoPostalCode()));
		oneTimeCountryZipCity.append(' ');
		oneTimeCountryZipCity.append(StringUtils.defaultString(firstOrder.getOneTimeSoldtoCity()));
		oneTimeSoldTo.add(oneTimeCountryZipCity.toString());
		oneTimeSoldTo.add(firstOrder.getOneTimeSoldtoVatNo());
		
		final List<String> oneTimeShipTo = new ArrayList<String>();
		oneTimeShipTo.add(firstOrder.getLnfSiteName());
		oneTimeShipTo.add(firstOrder.getLnfSiteStreetHouseNo());
		
		final StringBuilder lnfCountryZipCity = new StringBuilder();
		lnfCountryZipCity.append(StringUtils.defaultString(firstOrder.getLnfSiteCountry()));
		lnfCountryZipCity.append(' ');
		lnfCountryZipCity.append(StringUtils.defaultString(firstOrder.getLnfSitePostalCode()));
		lnfCountryZipCity.append(' ');
		lnfCountryZipCity.append(StringUtils.defaultString(firstOrder.getLnfSiteCity()));
		oneTimeShipTo.add(lnfCountryZipCity.toString());
		
		setAddresses(custOrder.getSoldtoSapBp(), custOrder.getShiptoSapBp(), custOrder.getBilltoSapBp(), custOrder.getPayerSapBp(), 
				custOrder.getCommCarrier(), null, oneTimeSoldTo, oneTimeShipTo, null);

		// remarks
		addData(DeliveryNoteData.COMMENT, com.crh.logon.utils.StringUtils.nvl(custOrder.getNoteDelivery()));
		
		// codes for message lookups
		String distributionChannel = null;
		String divisionCd = null;
		if (sareaBs != null) {
			distributionChannel = sareaBs.getDistributionChannel();
			if (sareaBs.getDivision() != null) {
				divisionCd = sareaBs.getDivision().getDivisionCd();
			}
		}
		
		// footer
		String footerTitle = formatText("pdf.collective_deliverynote.footer.title", locale, null, custOrder.getDeliveryType(), sorgBs, distributionChannel, divisionCd);
		String footerText = formatText("pdf.collective_deliverynote.footer.text", locale, null, custOrder.getDeliveryType(), sorgBs, distributionChannel, divisionCd);
		addData(DeliveryNoteData.FOOTER_TITLE, footerTitle);
		addData(DeliveryNoteData.FOOTER_TEXT, footerText);
		
		// create order table dtos
		orderItemDTOs = createOrdeItemDtos(custOrder);
		
		// create total table dtos
		orderItemTotalDTOs = createTotalDTOs(orderItemDTOs);
		
		// add total net weight and rides
		addData(DeliveryNoteData.RIDES, String.valueOf(orders.size()));
		BigDecimal weightTotal = BigDecimal.ZERO;
		String  uom = null;
		UomBsEntity tonsUom = referentialDataService.findBsEntity(UomBsEntity.class, Constant.UOM_TONS);
		for (OrderEntity order : orders) {
			// LOGON-5630: Changed from checking ActualDeliveryDat to check if loaded (and beyond)
			// If loaded or beyond set the latest available qty
			if (LogonStatusUtils.isLoadedOrBeyond(order.getOrderStatus())) {
				for (Object object : order.getOrderItems()) {	
					OrderItemEntity orderItem = (OrderItemEntity) object;
					// only phyiscal, bag or bulk
					Long pkgCd = orderItem.getSapMaterial().getMaterialPackage().getLogonPackageType().getPackageTypeCd();
					if (!BULK_PWDR_PKG_CD.equals(pkgCd) &&
							!BULK_DMPD_PKG_CD.equals(pkgCd) &&
							!BAG_PKG_CD.equals(pkgCd)) 
					{
						continue;
					}
					BigDecimal weight = getItemWeightInTons(unitOfMeasurementConverterService, orderItem, tonsUom);
					// cannot convert weight to tons, return null
					if (weight == null) {
						weightTotal = null;
						break;
					}
					weightTotal = weightTotal.add(weight);
				}
				if (weightTotal == null) {
					uom = "";
					break;
				}
				uom = Constant.UOM_TONS_TXT;
			}
		}
		
		
		addData(DeliveryNoteData.NET_TOTAL_WEIGHT, (weightTotal != null)
				? weightTotal.setScale(ROUNDING_SCALE, Constant.LOGON_ROUNDING_MODE).toPlainString()
				: getLabel(DeliveryNoteLabel.NOT_AVAILABLE));
		addData(DeliveryNoteData.NET_TOTAL_UOM, uom);
	}
	
	/**
	 * @param custOrder
	 */
	private List<OrderItemDTO> createOrdeItemDtos(CustorderEntity custOrder) {
		ReadWriteDao<OrderEntity> orderDao = daoProvider.getOrderEntityDao();
		Filter<OrderEntity> filter = new FilterImpl<OrderEntity>();
		filter.addExpression(Restrictions.eq(OrderEntity.ATTR_CUSTORDER, custOrder));
		List<OrderEntity> orders = orderDao.find(filter);
		List<OrderItemDTO> itemDTOs = new ArrayList<OrderItemDTO>();
		
		for (OrderEntity order : orders) {		
			String deliveryTimeString = getDeliveryTimeString(order);
			TimeZone deliveryPlantTZ = getTimeZoneForDeliveryPlant(order.getShippingpoint());
			for (Object object : order.getOrderItems()) {
				OrderItemEntity orderItem = (OrderItemEntity) object;
				OrderItemDTO dto = buildOrderItemDTO(orderItem, deliveryTimeString, deliveryPlantTZ);
				// don't count order weight into total, which is not yet loaded
				dto.setNotLoaded(!LogonStatusUtils.isLoadedOrBeyond(order.getOrderStatus()));
				itemDTOs.add(dto);
			}
		}
		
		// sort the return list. Put non-delivered orders at the end of the list
		Collections.sort(itemDTOs, new Comparator<OrderItemDTO>() {

			public int compare(OrderItemDTO o1, OrderItemDTO o2) {
				return o1.getDeliveryTime() == null && o2.getDeliveryTime() != null ? 1 : -1;
			}
		});
		return itemDTOs;
	}
	
	/**
	 * @param itemoDTOs
	 */
	private List<OrderItemTotalDTO> createTotalDTOs(List<OrderItemDTO> itemoDTOs) {
		Map<String, OrderItemTotalDTO> dtos = new HashMap<String, OrderItemTotalDTO>();
		for (OrderItemDTO dto : itemoDTOs) {
			BigDecimal weight = OrderItemDTO.getPkgDependantWeight(dto);
			// do not include orders which haven't been loaded
			if (dto.isNotLoaded() || weight == null) {
				continue;
			}
			if (dtos.containsKey(dto.getSapNr())) {
				// increase the net weight
				OrderItemTotalDTO totalDto = dtos.get(dto.getSapNr());
				totalDto.setNetWeight(totalDto.getNetWeight().add(weight));
			} else {
				boolean isPhyisical = 
						Long.valueOf(Constant.PACKAGE_TYPE_BBAG).compareTo(dto.getPkgType()) == 0 || 
						Long.valueOf(Constant.PACKAGE_TYPE_BULK_POWDER).compareTo(dto.getPkgType()) == 0 ||
						Long.valueOf(Constant.PACKAGE_TYPE_BULK_DUMPED).compareTo(dto.getPkgType()) == 0;
				dtos.put(dto.getSapNr(), new OrderItemTotalDTO(dto.getSapNr(), 
						dto.getMaterialName(), weight, OrderItemDTO.getPkgDependantUom(dto), isPhyisical));
			}
		}
		return new ArrayList<OrderItemTotalDTO>(dtos.values());
	}
	
	private BigDecimal getItemWeightInTons(UomConverterServiceLocalInterface unitOfMeasurementConverterService, OrderItemEntity item, UomBsEntity uomTons) {
		BigDecimal quantity = item.getDeliveredQuantity();
		UomBsEntity uom = item.getDeliveredUom();
		
		if (quantity == null) {
			quantity = item.getLoadedQuantity();
			uom = item.getLoadedUom();
		}
		
		if (quantity == null) {
			return null;
		}
		
		String uomCd = uom.getUomCd();
		
		if (!Constant.UOM_TONS.equals(uomCd)) {
			SapMaterialEntity sapMaterial = item.getSapMaterial(); // Not nullable.
			
			try {
				UomConversionResult ucr = unitOfMeasurementConverterService.convert(quantity, uom,
						uomTons, sapMaterial, null, null, false);
				
				quantity = ucr.getQuantity();
			}
			catch (LogonConversionException e) {
				Long sapMaterialId = sapMaterial.getSapMaterialId();
				
				LOG.warn("Cannot convert loaded or delivered quantity from UoM " + uomCd
						+ " to tons (sapMaterialId = " + sapMaterialId + "): "
						+ e.getMessage());
				
				quantity = null;
			}
		}
		
		return quantity;
	}
	
	/* (non-Javadoc)
	 * @see com.crh.logon.business.services.utils.pdftools.DeliveryNotePdfTextStore#getDTOList(java.lang.Class)
	 */
	@Override
	public <E extends DTOMarker> List<E> getDTOList(Class<E> clazz) {
		List<E> dtoList = super.getDTOList(clazz);
		if (OrderItemTotalDTO.class.equals(clazz)) {
			for (OrderItemTotalDTO dto : orderItemTotalDTOs) {
				dtoList.add(clazz.cast(dto));
			}
		}
		return dtoList;
	}
	
}
