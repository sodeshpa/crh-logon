/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.ordercreation.dto.ExecutingCarrierDTO;
import com.crh.logon.business.services.interfaces.BestellungService;
import com.crh.logon.business.services.interfaces.CarrierService;
import com.crh.logon.business.services.interfaces.PoolAdminService;
import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.WagonTrspCatTxEntity;
import com.crh.logon.persistence.entities.refdata.WagonTypeTxEntity;
import com.crh.logon.persistence.entities.views.PoolAllCarrierEntity;
import com.crh.logon.web.utils.CheckboxDropdownEntity;
import com.crh.logon.web.utils.DropdownEntity;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author AdNovum Informatik AG
 */
public class ChartUtils {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ChartUtils.class);
    
    /**
     * Delivery types
     */
    public static final Map<Integer, String> deliveryType2TxtMap;
    static {
    	deliveryType2TxtMap = new HashMap<Integer, String>();
    	deliveryType2TxtMap.put(Constant.CHART_DELIVERY_TYPE_DELIVERED, "chartsearch.dropdown.deliveryType.delivered");
    	deliveryType2TxtMap.put(Constant.CHART_DELIVERY_TYPE_PICKUP, "chartsearch.dropdown.deliveryType.pickup");
    }
    
    /**
     * Product types
     */
    public static final Map<Integer, String> productType2TxtMap;
    static {
    	productType2TxtMap = new HashMap<Integer, String>();
    	productType2TxtMap.put(Constant.PRODUCT_TYPE_BAG, "chartsearch.dropdown.productType.bag");
    	productType2TxtMap.put(Constant.PRODUCT_TYPE_BULK_LANDFILL, "chartsearch.dropdown.productType.bulkLandfill");
    	productType2TxtMap.put(Constant.PRODUCT_TYPE_BULK_AGG, "chartsearch.dropdown.productType.bulkAgg");
    	productType2TxtMap.put(Constant.PRODUCT_TYPE_BULK_CEM, "chartsearch.dropdown.productType.bulkCem");
    }
    
    /**
     * Transport category
     */
    public static final Map<Integer, String> transCat2TxtMap;
    static {
    	transCat2TxtMap = new HashMap<Integer, String>();
    	transCat2TxtMap.put(Constant.CHART_TRANS_CAT_TRUCK, "chartsearch.dropdown.transCat.road");
    	transCat2TxtMap.put(Constant.CHART_TRANS_CAT_RAIL, "chartsearch.dropdown.transCat.rail");
    }
    
    /**
     * Chart type
     */
    public static final Map<Integer, String> chartType2TxtMap;
    static {
    	chartType2TxtMap = new HashMap<Integer, String>();
    	chartType2TxtMap.put(Constant.CHART_TYPE_NR_OF_TRUCKS, "chartsearch.dropdown.chartType.trucks");
    	chartType2TxtMap.put(Constant.CHART_TYPE_NR_OF_LOADINGS, "chartsearch.dropdown.chartType.lodings");
    }

	/**
	 * 
	 * @param bestellungService
	 * @param plantCd
	 * @param locale
	 * @return
	 */
	public static List<CheckboxDropdownEntity> getShippingPointList(
			BestellungService bestellungService, String plantCd, boolean cbChecked, Locale locale) {
		// [[
		List<CheckboxDropdownEntity> result = null;
		Object[][] params = {{"bestellungService", bestellungService},
							{"plantCd", plantCd},
							{"cbChecked", cbChecked + ""},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ChartUtils.class, "getShippingPointList", params);
		}
		// ]]
		try {
			final List<ShippingpointEntity> shippingPointList = bestellungService.getShippingPointListByPlantCd(plantCd, locale);
			
			if (shippingPointList == null || shippingPointList.isEmpty()) {
				return result = new ArrayList<CheckboxDropdownEntity>(); 
			}			
			
			result = new ArrayList<CheckboxDropdownEntity>();
			for (ShippingpointEntity entity : shippingPointList) {				
				CheckboxDropdownEntity dropdownEntity = new CheckboxDropdownEntity(entity.getShippingpointName(), String.valueOf(entity.getShippingpointId()));
				dropdownEntity.setChecked(cbChecked);
				result.add(dropdownEntity);
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
	 * 
	 * @param transCategCdCd
	 * @param locale
	 * @param poolAdminService
	 * @return
	 */
    public static List<CheckboxDropdownEntity> fetchPoolList(String deliveryType, Locale locale,
    		boolean valuesChecked, PoolAdminService poolAdminService) {
		// [[
		List<CheckboxDropdownEntity> result = null;
		Object[][] params = {{"deliveryType", deliveryType},
							{"locale", locale},
							{"valuesChecked", valuesChecked + ""},
							{"poolAdminService", poolAdminService}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ChartUtils.class, "fetchPoolList", params);
		}
		// ]]
		try {
			final List<PoolTxEntity> poolList = poolAdminService.fetchPoolTxEntitiesByDeliveryTypeNotAuthorized(locale,
					Long.valueOf(deliveryType));
			
    		if (poolList == null || poolList.isEmpty()) {
    			return result = null;
    		}
    		
    		result = new ArrayList<CheckboxDropdownEntity>();
       		for (PoolTxEntity entity : poolList) {
    		
    			CheckboxDropdownEntity dropdownEntity = new CheckboxDropdownEntity(entity.getName(), String.valueOf(entity.getPoolTxPK().getPoolId()));
    			dropdownEntity.setChecked(valuesChecked);
    			result.add(dropdownEntity);
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
	 * 
	 * 
	 * @param poolIds
	 * @param carrierService
	 * @param cbSelected
	 * @return
	 */
	public static List<CheckboxDropdownEntity> getCarrierDPEntities(List<Long> poolIds,
			CarrierService carrierService, boolean cbSelected, Locale locale) {
		// [[
		ArrayList<CheckboxDropdownEntity> result = null;
		Object[][] params = {{"poolIds", poolIds},
							{"carrierService", carrierService},
							{"cbSelected", cbSelected + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ChartUtils.class, "getCarrierDPEntities", params);
		}
		// ]]
		try {
			result = new ArrayList<CheckboxDropdownEntity>();
			if (poolIds == null || poolIds.isEmpty()) {
				final List<PoolAllCarrierEntity>  carrierEntityList = carrierService.fetchAllExecutingCarrierList();
				
				if (carrierEntityList == null || carrierEntityList.isEmpty()) {
					return result;
				}
				
				for (PoolAllCarrierEntity entity : carrierEntityList) {
					CheckboxDropdownEntity dropdownEntity = new CheckboxDropdownEntity(entity.getCarrierName(), String.valueOf(entity.getCarrierId()));
					dropdownEntity.setChecked(cbSelected);
	    			result.add(dropdownEntity);
				}
			}
			else {
				List<ExecutingCarrierDTO> distinctCarrierList = carrierService.fetchDistinctExecutingCarrierList(poolIds, locale);
				
				if (distinctCarrierList == null || distinctCarrierList.isEmpty()) {
					return result;
				}

				for (ExecutingCarrierDTO carrier : distinctCarrierList) {
					CheckboxDropdownEntity dropdownEntity = 
							new CheckboxDropdownEntity(carrier.getDisplayName(), String.valueOf(carrier.getCarrierId()));
					dropdownEntity.setChecked(cbSelected);
					result.add(dropdownEntity);
				}
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
	 * 
	 * 
	 * @param refData
	 * @param locale
	 * @return
	 */
	public static List<CheckboxDropdownEntity> getTruckTypeList(RefDataGenericsService refData, Locale locale) {
		// [[
		List<CheckboxDropdownEntity> result = null;
		Object[][] params = {{"refData", refData},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ChartUtils.class, "getTruckTypeList", params);
		}
		// ]]
		try {
			final List<TruckTypeTxEntity> plantBsList = refData.findTxEntityList(TruckTypeTxEntity.class, locale);
			
			if (plantBsList == null || plantBsList.isEmpty()) {
				return result = null; // should never happen
			}
						
			result = new ArrayList<CheckboxDropdownEntity>();
			for (TruckTypeTxEntity entity : plantBsList) {				
				final String truckTypeName = entity.getTxt();
				final String truckTypeCd = String.valueOf(entity.getTruckTypeTxPK().getTruckTypeBs().getTruckTypeCd());
				CheckboxDropdownEntity dropdownEntity = new CheckboxDropdownEntity(truckTypeName, truckTypeCd);
				dropdownEntity.setChecked(true); // by default all pools are selected
				result.add(dropdownEntity);
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
	 * 
	 * @param refData
	 * @return
	 */
	public static List<DropdownEntity> getPlantBsList(RefDataGenericsService refData) {
		// [[
		List<DropdownEntity> result = null;
		Object[][] params = {{"refData", refData}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ChartUtils.class, "getPlantBsList", params);
		}
		// ]]
		try {
			List<PlantBsEntity> plantBsList = refData.findBsEntityList(PlantBsEntity.class);
			
			if (plantBsList == null || plantBsList.isEmpty()) {
				return result = null; // should never happen
			}
			
			
			result = new ArrayList<DropdownEntity>();
			for (PlantBsEntity entity : plantBsList) {
				DropdownEntity dropdownEntity = new DropdownEntity(entity.getPlantName(), entity.getPlantCd()); 
				result.add(dropdownEntity);
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
	 * 
	 * @param request
	 * @return
	 */
	public static List<DropdownEntity> getProductTypeList(HttpServletRequest request) {
		// [[
		List<DropdownEntity> result = null;
		Object[][] params = {{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ChartUtils.class, "getProductTypeList", params);
		}
		// ]]
		try {
			String bagValue = MessageUtils.getMessage(request,
					productType2TxtMap.get(Constant.PRODUCT_TYPE_BAG));
			String bulkLandfillValue = MessageUtils.getMessage(request,
					productType2TxtMap.get(Constant.PRODUCT_TYPE_BULK_LANDFILL));
			String bulkAggValue = MessageUtils.getMessage(request,
					productType2TxtMap.get(Constant.PRODUCT_TYPE_BULK_AGG));
			String bulkCemValue = MessageUtils.getMessage(request,
					productType2TxtMap.get(Constant.PRODUCT_TYPE_BULK_CEM));
			
			result = new ArrayList<DropdownEntity>();
			DropdownEntity bagEntity = new DropdownEntity(bagValue,
					String.valueOf(Constant.PRODUCT_TYPE_BAG));
			DropdownEntity bulkLandfillEntity = new DropdownEntity(
					bulkLandfillValue, String.valueOf(Constant.PRODUCT_TYPE_BULK_LANDFILL));
			DropdownEntity bulkAggEntity = new DropdownEntity(bulkAggValue,
					String.valueOf(Constant.PRODUCT_TYPE_BULK_AGG));
			DropdownEntity bulkCemEntity = new DropdownEntity(bulkCemValue,
					String.valueOf(Constant.PRODUCT_TYPE_BULK_CEM));
			
			result.add(bagEntity);
			result.add(bulkLandfillEntity);
			result.add(bulkAggEntity);
			result.add(bulkCemEntity);
			
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
	 * 
	 * @param request
	 * @return
	 */
	public static List<CheckboxDropdownEntity> getTransCatList(HttpServletRequest request) {
		// [[
		List<CheckboxDropdownEntity> result = null;
		Object[][] params = {{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ChartUtils.class, "getTransCatList", params);
		}
		// ]]
		try {
			String truckValue = MessageUtils.getMessage(request,
					transCat2TxtMap.get(Constant.CHART_TRANS_CAT_TRUCK));
			String railValue = MessageUtils.getMessage(request,
					transCat2TxtMap.get(Constant.CHART_TRANS_CAT_RAIL));
			
			result = new ArrayList<CheckboxDropdownEntity>();
			CheckboxDropdownEntity truckEntity = new CheckboxDropdownEntity(truckValue,
					String.valueOf(Constant.CHART_TRANS_CAT_TRUCK));
			CheckboxDropdownEntity railEntity = new CheckboxDropdownEntity(railValue,
					String.valueOf(Constant.CHART_TRANS_CAT_RAIL));
			
			result.add(truckEntity);
			result.add(railEntity);
			
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
	 * 
	 * @param request
	 * @return
	 */
	public static List<CheckboxDropdownEntity> getDeliveryTypeList(HttpServletRequest request) {
		// [[
		List<CheckboxDropdownEntity> result = null;
		Object[][] params = {{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ChartUtils.class, "getDeliveryTypeList", params);
		}
		// ]]
		try {
			String deliveredValue = MessageUtils.getMessage(request,
					deliveryType2TxtMap.get(Constant.CHART_DELIVERY_TYPE_DELIVERED));
			String pickupValue = MessageUtils.getMessage(request,
					deliveryType2TxtMap.get(Constant.CHART_DELIVERY_TYPE_PICKUP));
			
			result = new ArrayList<CheckboxDropdownEntity>();
			CheckboxDropdownEntity deliveredEntity = new CheckboxDropdownEntity(deliveredValue,
					String.valueOf(Constant.CHART_DELIVERY_TYPE_DELIVERED), true );
			CheckboxDropdownEntity pickupEntity = new CheckboxDropdownEntity(pickupValue,
					String.valueOf(Constant.CHART_DELIVERY_TYPE_PICKUP));
			
			result.add(deliveredEntity);
			result.add(pickupEntity);
			
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
	 * NOTE: NO AUTHORIZATION --> call this function from an AUTHORIZED function.
	 * 
	 * @param request
	 * @return
	 */
	public static List<CheckboxDropdownEntity> getChartTypeList(HttpServletRequest request) {
		// [[
		List<CheckboxDropdownEntity> result = null;
		Object[][] params = {{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ChartUtils.class, "getChartTypeList", params);
		}
		// ]]
		try {
			String trucksValue = MessageUtils.getMessage(request,
					chartType2TxtMap.get(Constant.CHART_TYPE_NR_OF_TRUCKS));
			String loadingsValue = MessageUtils.getMessage(request,
					chartType2TxtMap.get(Constant.CHART_TYPE_NR_OF_LOADINGS));
			
			result = new ArrayList<CheckboxDropdownEntity>();
			CheckboxDropdownEntity trucksEntity = new CheckboxDropdownEntity(trucksValue,
					String.valueOf(Constant.CHART_TYPE_NR_OF_TRUCKS));
			CheckboxDropdownEntity loadingsEntity = new CheckboxDropdownEntity(loadingsValue,
					String.valueOf(Constant.CHART_TYPE_NR_OF_LOADINGS));
			
			result.add(trucksEntity);
			result.add(loadingsEntity);
			
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
	 * NOTE: NO AUTHORIZATION --> call this function from an AUTHORIZED function.
	 * 
	 * @param refData
	 * @param locale
	 * @return
	 */
	public static List<CheckboxDropdownEntity> getWagonTrspCatList(RefDataGenericsService refData,
			Locale locale) {
		// [[
		List<CheckboxDropdownEntity> result = null;
		Object[][] params = {{"refData", refData},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ChartUtils.class, "getWagonTrspCatList", params);
		}
		// ]]
		try {
			final List<WagonTrspCatTxEntity> poolTxList = refData.findTxEntityList(WagonTrspCatTxEntity.class, locale);	
			
			if (poolTxList == null || poolTxList.isEmpty()) {
				return result = null; // should never happen
			}		
			
			result = new ArrayList<CheckboxDropdownEntity>();
			for (WagonTrspCatTxEntity entity : poolTxList) {				
				final String wagonTrspCatName = entity.getTxt();
				final String wagonTrspCatCd = String.valueOf(entity.getWagonTrspCatTxPK().getWagonTrspCatBs().getWagonTrspCatCd());
				final CheckboxDropdownEntity dropdownEntity = new CheckboxDropdownEntity(wagonTrspCatName, wagonTrspCatCd);
				dropdownEntity.setChecked(true); // by default all pools are selected
				result.add(dropdownEntity);
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
	 * NOTE: NO AUTHORIZATION --> call this function from an AUTHORIZED function.
	 * 
	 * @param refData
	 * @param locale
	 * @return
	 */
	public static List<CheckboxDropdownEntity> getWagonTypeTxEntityList(RefDataGenericsService refData,
			boolean valuesChecked, Locale locale) {
		// [[
		List<CheckboxDropdownEntity> result = null;
		Object[][] params = {{"refData", refData},
							{"valuesChecked", valuesChecked + ""},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ChartUtils.class, "getWagonTypeTxEntityList", params);
		}
		// ]]
		try {
			final List<WagonTypeTxEntity> wagonTypeList = refData.findTxEntityList(WagonTypeTxEntity.class, locale);	
			
			if (wagonTypeList == null || wagonTypeList.isEmpty()) {
				return result = null; // should never happen
			}		
			
			result = new ArrayList<CheckboxDropdownEntity>();
			for (WagonTypeTxEntity entity : wagonTypeList) {
				final String wagonTypeName = entity.getTxt();
				final String wagonTypeCode = String.valueOf(entity.getWagonTypeTxPK().getWagonTypeBs().getWagonTypeCd());
				CheckboxDropdownEntity dropdownEntity = new CheckboxDropdownEntity(wagonTypeName, wagonTypeCode);
				dropdownEntity.setChecked(valuesChecked); 
				result.add(dropdownEntity);
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
	
	public static String getSelectedValues(List<CheckboxDropdownEntity> values) {
		// [[
		String result = null;
		Object[][] params = {{"values", values}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ChartUtils.class, "getSelectedValues", params);
		}
		// ]]
		try {
			if (values == null || values.isEmpty()) {
				return result = null;
			}
			
			final StringBuilder builder = new StringBuilder();
			for (Iterator<CheckboxDropdownEntity> iter = values.iterator(); iter.hasNext();) {
				CheckboxDropdownEntity entity = iter.next();
				builder.append(entity.getValue());
				
				if (iter.hasNext()) {
					builder.append(",");
				}
			}
			return result = builder.toString();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
}
