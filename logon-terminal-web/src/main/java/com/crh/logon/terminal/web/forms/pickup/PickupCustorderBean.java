/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.terminal.web.forms.pickup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.ordercreation.DeliveryOptionConfigurationDTO;
import com.crh.logon.business.ordercreation.DeliverySettingFilterLogic;
import com.crh.logon.business.ordercreation.DeliverySettingProductMap;
import com.crh.logon.business.ordercreation.DeliverySettingSalesAreaMap;
import com.crh.logon.business.ordercreation.DeliverySettingShiptoMap;
import com.crh.logon.business.ordercreation.dto.AbstractDeliverySetting;
import com.crh.logon.business.ordercreation.dto.CustorderItemDeliverySetting;
import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.business.ordercreation.dto.OrderItemDeliverySetting;
import com.crh.logon.business.services.interfaces.DispokarteService;
import com.crh.logon.business.services.utils.CustorderUtils;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.ShiptoSalesAreaError;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.CustorderItemEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoBilltoEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.DistributionDestBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.web.utils.ServiceLocatorProvider;

/**
 * Pickup order is an order type, which is created/edited  on touchscreen terminals by CUSTOMERS. This bean is only for orders with BAG
 * products.
 * Bag pickup orders AUTOMATICALLY go through the workflow steps: if they are created on call center or weborder, they will get
 * right after creation into status transmitted to carrier, and when the customer
 * clicks print delivery note, the step dispatching to the artificial pickup bag truck, loading will automatically be executed, and they go right in status delivered.
 * In case the order is created on the touchscreen the order goes through the whole workflow due to a single click on button
 * print delivery note.
 *
 * The bean's purpose is to store, and manage it's OrderEntity, and the itemBeanList, which represent a list of
 * OrderItemEntities.
 * The available products are defined on the dispokarte on the sales area tabs, available materials must have there
 * an order condition setting (ShiptoSareaRestriction in the db).
 * In the beginning if the itemBeanList is
 * empty, all products should be available from all sales areas, but when the first product is added to the orderBean,
 * only those products should be possible to add to the list, which are from the sales area defined by the first product (restriction)m
 * and which are possible to combine with the first material restriction.
 * 
 * There is two possibilities to create a bean: 
 * 1. to create it from the terminal application which means, the order is going to be created according to this bean
 * 2. if it is created from an existing order coming from weborder or call center. In this case the order items
 * may not have a restriction in the background, but the order already exists in the db!
 * 
 * Materials can only be added if they have a valid restriction in the background, that holds for both cases: also when the order
 * exists already in the db, and also if it does not.
 * 
 * @author AdNovum Informatik AG
 */
public class PickupCustorderBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private final DispokarteService dispokarteService;
	
	private Map<String, PickupCustorderItemBean> addedDeliverySettingMap = new HashMap<String, PickupCustorderItemBean>();
	private Map<String, DeliverySettingDTO> validDeliverySettings;
	
	private CustorderEntity custorderEntity;
	private List<PickupCustorderItemBean> custorderItemBeanList;
	private List<PickupPalletOrderItemBean> palletOrderItemBeanList;
	
	LoadingpointEntity loadingpointEntity;
	ShiptoSareaEntity shiptoSarea;
	
	List<ShiptoSareaEntity> shiptoSalesAreas;
	
	private DeliverySettingShiptoMap shiptoMap;
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PickupCustorderBean.class);
	
	/**
	 * Creates a new bean with a new orderEntity. This order does not exist yet in the db, will have an empty itemBeanList in the beginning.
	 * @param shiptoSalesAreas
	 * @param loadingpointEntity
	 * @param custorder TODO
	 * @param shiptoMap 
	 * @param serviceLocator TODO
	 */
	public PickupCustorderBean(List shiptoSalesAreas, LoadingpointEntity loadingpointEntity, CustorderEntity custorder, DeliverySettingShiptoMap shiptoMap) {
		super();
		// [[
		Object[][] params = {{"shiptoSalesAreas", shiptoSalesAreas},
							{"loadingpointEntity", loadingpointEntity},
							{"custorder", custorder},
							{"shiptoMap", shiptoMap}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "PickupCustorderBean", params);
		}
		// ]]
		try {
			custorderEntity = custorder;
			this.loadingpointEntity = loadingpointEntity;
			
			dispokarteService = ServiceLocatorProvider.getServiceLocator().getDispokarteService();
			
			this.shiptoMap = shiptoMap;
			this.shiptoSalesAreas = shiptoSalesAreas;
			
			setSareaAndRelatedData(null);
			
			custorderItemBeanList = new ArrayList<PickupCustorderItemBean>();
			palletOrderItemBeanList = new ArrayList<PickupPalletOrderItemBean>();
			
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
	 * Create the bean according to an EXISTING order.
	 * @param pickupOrder
	 * @param shippingpointEntity
	 * @param loadingPointEntity
	 */
	public PickupCustorderBean(CustorderEntity pickupOrder, ShippingpointEntity shippingpointEntity, LoadingpointEntity loadingPointEntity) {
		if ( ! pickupOrder.getShippingpoint().getShippingpointId().equals(shippingpointEntity.getShippingpointId() )) {
			throw new LogonException();
		}
		dispokarteService = ServiceLocatorProvider.getServiceLocator().getDispokarteService();
		ShiptoEntity shipto = dispokarteService.fetchAndInitializeShiptoBySapId(pickupOrder.getShiptoSapBp().getSapId());

		List shiptoSalesAreas = dispokarteService.fetchShiptoSareas(shipto, pickupOrder.getSoldtoSapBp().getSapBpId(), true);
		this.shiptoSalesAreas = new ArrayList();
		this.custorderEntity = pickupOrder;
		
		this.shiptoMap = ServiceLocatorProvider.getServiceLocator().getDeliverySettingService().initDeliverySettingShiptoMapForPickupTerminal(pickupOrder.getSoldtoSapBp(), Locale.ENGLISH, shiptoSalesAreas);
		shiptoMap.validateSalesAreas();

		/**
		 * Only add to the valid sales areas in case it is valid!
		 */
		for ( Object sarea: shiptoSalesAreas ) {
			if ( !shiptoMap.getInvalidMap().containsKey((ShiptoSareaEntity) sarea) ) {
				this.shiptoSalesAreas.add((ShiptoSareaEntity) sarea);
			}
		}
		this.addedDeliverySettingMap = new HashMap();
		this.validDeliverySettings = new HashMap();
		
		this.custorderItemBeanList = new ArrayList<PickupCustorderItemBean>();
		this.palletOrderItemBeanList = new ArrayList<PickupPalletOrderItemBean>();
		this.loadingpointEntity = loadingPointEntity;
		
		ShiptoSareaEntity shiptoSarea = getSelectedShiptoSarea();
		if (shiptoSarea == null) {
			throw new LogonException();
		}
		
		shiptoMap.validateSalesAreas();
		
		setSareaAndRelatedData(shiptoSarea);

		Iterator<CustorderItemEntity> it2 = pickupOrder.getCustorderItems().iterator();
		while (it2.hasNext()) {
			CustorderItemEntity item = it2.next();

			CustorderItemDeliverySetting itemSetting =  new CustorderItemDeliverySetting(item.getCustorder(), item);
			DeliverySettingDTO setting = matchDeliverySetting(shiptoSarea,
					itemSetting);

			PickupCustorderItemBean itemBean = new PickupCustorderItemBean(setting, item);
			updateCustorderBean(itemBean);

		}
		
		List<OrderEntity> orders = pickupOrder.getOrders();
		for (OrderEntity order : orders ) {
			for (OrderItemEntity orderItem : order.getOrderItems()) {
				//only for pallets!
				if (LogonPackageHandlingUtil.isPallet(orderItem)) {
					OrderItemDeliverySetting orderItemSetting =  new OrderItemDeliverySetting(orderItem.getOrder(), orderItem);
					
					DeliverySettingDTO deliverySettingByCompositeIdentifier = matchDeliverySetting(shiptoSarea, orderItemSetting);
					PickupPalletOrderItemBean palletItemBean = new PickupPalletOrderItemBean(deliverySettingByCompositeIdentifier, orderItem);
					getPalletOrderItemBeanList().add(palletItemBean);
				}
			}
		}
	}

	private DeliverySettingDTO matchDeliverySetting(
			ShiptoSareaEntity shiptoSarea,
			AbstractDeliverySetting itemSetting) {
		final DeliverySettingSalesAreaMap salesAreaMap = shiptoMap.getSalesAreaMapForSareaId(shiptoSarea.getSarea().getSareaId());
		//the basic list contains restriction based settings, and the problem with that is that a sap material can be there multiple times, the composite id contains the restriction if as well, we need to match only by the meaningful attributes
		DeliverySettingDTO setting = salesAreaMap.matchDeliverySetting(itemSetting);
		return setting;
	}

	public CustorderEntity getCustorderEntity() {
		return custorderEntity;
	}

	public void setCustorderEntity(CustorderEntity pickupOrder) {
		this.custorderEntity = pickupOrder;
	}
	
	public int getCustorderItemBeanListSize(){
		return custorderItemBeanList.size();
	}

	public List<PickupCustorderItemBean> getCustorderItemBeanList() {
		return custorderItemBeanList;
	}

	public void setCustorderItemBeanList(List<PickupCustorderItemBean> itemBeanList) {
		this.custorderItemBeanList = itemBeanList;
	}


	/**
	 * Adds an OrderItemEntity to the order and updates the bean. 
	 * @param restrictionId
	 * @param orderBean
	 */
	public void addCustorderItem(PickupCustorderItemBean custorderItemBean) {

		List<CustorderItemEntity> orderItems = custorderEntity.getCustorderItems();
		CustorderItemEntity itemEntity = custorderItemBean.getItemEntity();

		for(CustorderItemEntity o : orderItems){
			if(o.getSapMaterial().getSapMaterialId().equals(itemEntity.getSapMaterial().getSapMaterialId())){
				//element alredy exists
				return;
			}
		}
		
		updateCustorderBean(custorderItemBean);
		
		if (!orderItems.contains(itemEntity)) {			
			orderItems.add(itemEntity);
			itemEntity.setCustorder(custorderEntity);
		}
	}

	/**
	 * The restriction of the itembean is either known - that means that it
	 * still has a valid restriction in the background, or is null which means that we are in edit mode where the items
	 * are not at all guaranteed to have a restriction in the background since call center and webbestellung create the items
	 * independent of the settings in dispokarte.
	 * As far the terminal application uses the restrictions as a base, if a restriction is already used to add an item, it cannot
	 * be used anymore, therefore the structures containing the still available valid restrictions need to be updated, if an item has been added
	 * according to a valid restriction.
	 * @param custorderItemBean
	 */
	private void updateCustorderBean(PickupCustorderItemBean custorderItemBean) {
		DeliverySettingDTO setting = custorderItemBean.getDeliverySetting();
		if (setting == null ) {
			//add it, it is not based on restrictions, most lihely coming from callcenter
			custorderItemBeanList.add(custorderItemBean);	
			return;
		}
		
		String compositeId = setting.getCompositeIdentifier();
		if (!addedDeliverySettingMap.containsKey(compositeId)) {
			setting = validDeliverySettings.get(compositeId);
			//reduce here the valid restrictions to those, which are in fact available in the first shipto sarea
			if (shiptoSarea == null) {
				setSareaAndRelatedData(setting.getShiptoSarea());
				setBilltoSapBp(setting);
				
				CustorderUtils.setupCustorderAttributes(custorderEntity, setting);
			}
			
			if (custorderEntity.getSapContract() == null && setting.getSapContractItem() != null) {
				custorderEntity.setSapContract(setting.getSapContractItem().getSapContract());
			}
			
			if (custorderEntity.getDeliveryType() == null) {
				boolean isReturnPickup = false;
				
				if (setting.getDeliveryType() != null) {
					if (LogonUtil.isReturnPalletOnly(setting.getDeliveryType().getDeliveryTypeCd())) {
						isReturnPickup = true;
					}
				}
				
				final DeliveryTypeBsEntity deliveryType;
				if (isReturnPickup) {
					deliveryType = dispokarteService.fetchDeliveryType(Constant.DELIVERY_TYPE_RETURN_PICKUP);
				}
				else {
					deliveryType = dispokarteService.fetchDeliveryType(Constant.DELIVERY_TYPE_PICKUP);
				}
				
				custorderEntity.setDeliveryType(deliveryType);
			}
			
			custorderItemBeanList.add(custorderItemBean);
			addedDeliverySettingMap.put(compositeId, custorderItemBean);
			//use one material only once, this can happen if multiple restriction are set in the dispocard for the same material 
			Long sapMaterialId = custorderItemBean.getDeliverySetting().getSapMaterial().getSapMaterialId();
			for(Entry<String,DeliverySettingDTO> r : validDeliverySettings.entrySet()){
				if (!addedDeliverySettingMap.containsKey(r.getKey()) && r.getValue().getSapMaterial().getSapMaterialId().equals(sapMaterialId)) {
					addedDeliverySettingMap.put(r.getKey(), custorderItemBean);	
				}
			}

		}
	}
	
	public List getBilltoList() {
		
		if (!custorderItemBeanList.isEmpty() ) {
			final DeliverySettingSalesAreaMap salesAreaMapForSareaId = shiptoMap.getSalesAreaMapForSareaId(custorderItemBeanList.get(0).getDeliverySetting().getShiptoSarea().getSarea().getSareaId());
			
			final DeliveryOptionConfigurationDTO deliveryOptionConfiguration = DeliverySettingFilterLogic.getDeliveryOptionConfiguration(custorderItemBeanList.get(0).getDeliverySetting(), salesAreaMapForSareaId);

			return deliveryOptionConfiguration.getBilltoList();
		}
		return new ArrayList();
	}

	public SapBpEntity getShiptoSapBp() {
		return custorderEntity.getShiptoSapBp();
	}
	
	public SapBpEntity getSoldtoSapBp() {
		return custorderEntity.getSoldtoSapBp();
	}
	
	/**
	 * Do not let billto pre-set in case it is invalid.
	 * @param deliverySetting
	 */
	public void setBilltoSapBp(DeliverySettingDTO deliverySetting){
		SapBpEntity billtoSapBp = deliverySetting.getBilltoSapBp();
		ShiptoSareaEntity sarea = deliverySetting.getShiptoSarea();
		final DeliverySettingSalesAreaMap salesAreaMap = shiptoMap.getSalesAreaMapForSareaId(sarea.getSarea().getSareaId());
		final Map<Object, List<ShiptoSalesAreaError>> invalidObjectMap = salesAreaMap.getInvalidMap();
		for (Map.Entry<Object, List<ShiptoSalesAreaError>> entry : invalidObjectMap.entrySet() ) {
			if ( entry instanceof ShiptoBilltoEntity && ((ShiptoBilltoEntity) entry.getKey()).getBilltoSapBp().getSapBpId().equals(billtoSapBp.getSapBpId())) {
				return;
			}
		}			
		setBilltoSapBp(billtoSapBp);
		
	}
	
	/**
	 *	 
	 * @param shiptoSarea
	 */
	private void setSareaAndRelatedData(ShiptoSareaEntity shiptoSarea){
		this.shiptoSarea = shiptoSarea;
		if (shiptoSarea != null) {			
			SareaEntity sarea = shiptoSarea.getSarea();
			custorderEntity.setSarea(sarea);
			
			DistributionDestBsEntity destEntity = ServiceLocatorProvider.getServiceLocator().getRefDataGenericsService().findBsEntity(DistributionDestBsEntity.class, Constant.DISTRIBUTION_DEST_OUTBOUND);
			custorderEntity.setDistributionDest(destEntity);
			

			DeliverySettingSalesAreaMap salesAreaMap = shiptoMap.getSalesAreaMapForSareaId(shiptoSarea.getSarea().getSareaId());
			final DeliverySettingProductMap productMap = salesAreaMap.getMaterialRestrictionDeliverySettings();
			final DeliverySettingProductMap serviceMap = salesAreaMap.getServiceRestrictionDeliverySettings();

			validDeliverySettings = new HashMap();
			for (DeliverySettingDTO setting : productMap.getPossibleDeliverySettings() ) {
				boolean isSettingValid = checkValidity(setting);
				if ( isSettingValid ) {					
					validDeliverySettings.put(setting.getCompositeIdentifier(), setting);
				}
			}
			for (DeliverySettingDTO setting : serviceMap.getPossibleDeliverySettings() ) {
				boolean isSettingValid = checkValidity(setting);
				if ( isSettingValid ) {					
					validDeliverySettings.put(setting.getCompositeIdentifier(), setting);
				}
			}
		}
		else{
			custorderEntity.setSarea(null);
			custorderEntity.setDistributionDest(null);
			validDeliverySettings = new HashMap();
			for (DeliverySettingDTO setting : shiptoMap.getMaterialRestrictionDeliverySettingList() ) {
				boolean isSettingValid = checkValidity(setting);
				if ( isSettingValid ) {					
					validDeliverySettings.put(setting.getCompositeIdentifier(), setting);
				}
			}
			for (DeliverySettingDTO setting : shiptoMap.getServiceRestrictionDeliverySettingList() ) {
				boolean isSettingValid = checkValidity(setting);
				if ( isSettingValid ) {					
					validDeliverySettings.put(setting.getCompositeIdentifier(), setting);
				}
			}
			
		}
	}
	
	/**
	 * Postfiltering possible pickup order materials for a specific Shippingpoint and Loadingpoint pair.
	 *  
	 * @param setting
	 * @return
	 */
	private boolean checkValidity(DeliverySettingDTO setting) {
		boolean isSettingValid = true;
		//shippintpoints other than this.custorderEntity.getShippingpoint() are invalid
		if ( setting.getShippingpoint() == null || !setting.getShippingpoint().getShippingpointId().equals(custorderEntity.getShippingpoint().getShippingpointId()) ) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Product " + setting.getSapMaterial().getSapMaterialNo() + " has no or not proper Shippingpoint set.");
			}
			isSettingValid = false;				
		}
		//loadingpoints other then null OR this.loadingpointEntity are invalid
		if (setting.getLoadingpoint() != null && 
				!setting.getLoadingpoint().getLoadingpointId().equals(loadingpointEntity.getLoadingpointId()) ) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Product " + setting.getSapMaterial().getSapMaterialNo() + " is not properly set up on the dispokarte or not proper lp set.");
			}
			isSettingValid = false;
		}
		return isSettingValid;
	}
	
	public SapBpEntity getBilltoSapBp(){
		return custorderEntity.getBilltoSapBp();
	}
	
	public void setBilltoSapBp(SapBpEntity billto){
		custorderEntity.setBilltoSapBp(billto);
	}

	/**
	 * Still filters the valid restrictions. Valid restrictions may not be present together in the same order, because
	 * certain order attributes must be the same for them. (For example not possible to add free of charge and not free of charge
	 * items together.)
	 * @return
	 */
	public Map<String, DeliverySettingDTO> getValidDeliverySettings() {
		
		Map<String, DeliverySettingDTO> lRestrictions = new HashMap<String, DeliverySettingDTO>();
		lRestrictions.putAll(validDeliverySettings);
		Iterator<Map.Entry<String, DeliverySettingDTO>> it = lRestrictions.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, DeliverySettingDTO> entry = it.next();
			DeliverySettingDTO deliverySetting = entry.getValue();
			if (custorderEntity.getFreeDeliveryFlag() != null && !deliverySetting.getFreeDeliveryFlag().equals(custorderEntity.getFreeDeliveryFlag())) {
				it.remove();
			}
			if (custorderEntity.getSapContract() != null && deliverySetting.getSapContractItem() != null
					&& !deliverySetting.getSapContractItem().getSapContract().getSapContractId().equals(custorderEntity.getSapContract().getSapContractId())) {
				it.remove();
			}
		}
		return lRestrictions;
	}

	public Map <String, PickupCustorderItemBean> getAddedDeliverySettingMap() {
		return addedDeliverySettingMap;
	}
	
	private ShiptoSareaEntity getSelectedShiptoSarea(){
		SareaEntity sarea = custorderEntity.getSarea();
		Iterator it = shiptoSalesAreas.iterator();
		ShiptoSareaEntity selectedSarea = null;
		while (it.hasNext()) {
			ShiptoSareaEntity shiptoSarea = (ShiptoSareaEntity) it.next();
			if (shiptoSarea.getSarea().getSareaId().equals(sarea.getSareaId())) {
				selectedSarea = shiptoSarea;
				return selectedSarea;
			}
		}
		return null;
	}

	public List getShiptoSalesAreas() {
		return shiptoSalesAreas;
	}
	
	
	public List<PickupPalletOrderItemBean> getPalletOrderItemBeanList() {
		return palletOrderItemBeanList;
	}

	public void setPalletBeanList(List<PickupPalletOrderItemBean> palletOrderItemBeanList) {
		this.palletOrderItemBeanList = palletOrderItemBeanList;
	}
}
