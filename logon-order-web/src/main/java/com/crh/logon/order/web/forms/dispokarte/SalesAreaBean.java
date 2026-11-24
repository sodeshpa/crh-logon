/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.dispokarte;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.crh.logon.business.ordercreation.DeliveryOptionConfigurationDTO;
import com.crh.logon.business.ordercreation.DeliverySettingSalesAreaMap;
import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.business.services.utils.LeadTimeValidationBaseBean;
import com.crh.logon.constants.Constant;
import com.crh.logon.dto.DistanceValidationEditGuiBean;
import com.crh.logon.order.web.utils.DeliveryOptionConfigurationWrapper;
import com.crh.logon.order.web.utils.comparator.BilltoComparator;
import com.crh.logon.order.web.utils.comparator.DeliverySettingWrapperComparator;
import com.crh.logon.persistence.entities.common.ShiptoBilltoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.refdata.BilltoRuletypeBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.views.BpSareaSofficeSgroupEntity;
import com.crh.logon.persistence.entities.views.SapSoldBillSareaSrchEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.web.utils.JsonUtils;
import flexjson.JSONSerializer;

/**
 * @author AdNovum Informatik AG
 */
public class SalesAreaBean implements Serializable {

	private static final long serialVersionUID = 1L;

    public static final String DEFAULT_SCOPE_ALL = "0";
    public static final String DEFAULT_SCOPE_BULK = "1";	
    public static final String DEFAULT_SCOPE_BAG = "2";
        
    private ShiptoSareaEntity shiptoSarea;
    private LeadTimeValidationBaseBean leadTimeValidationBaseBean;
	private List <SapSoldBillSareaSrchEntity> billtos;
	private HashMap <Long, SapBpEntity> shiptoBilltosMap;
	private HashMap <Long, ShiptoSiloEntity> shiptoSilosMap;
	private List<ShiptoSiloEntity> shiptoSilos;
	
	private DeliverySettingSalesAreaMap salesAreaMap;
	
	List<DeliverySettingDTO> newDeliverySettings;

	private DistanceValidationEditGuiBean distanceValidationGuiBeanShipto;
	private DistanceValidationEditGuiBean distanceValidationGuiBeanSarea;


	public List<DeliverySettingDTO> getNewDeliverySettingList() {
		return newDeliverySettings;
	}

	public void setNewDeliverySettingList(List<DeliverySettingDTO> newDeliverySettingList) {
		this.newDeliverySettings = newDeliverySettingList;
	}

	private BpSareaSofficeSgroupEntity sapBpSarea;
	
	private BilltoRuletypeBsEntity defaultRuleType;
	
	public SalesAreaBean(){
		newDeliverySettings = new ArrayList<DeliverySettingDTO>();
		billtos = new LinkedList <SapSoldBillSareaSrchEntity>();
		reset();
	}
	
	public void reset() {
		shiptoBilltosMap = new HashMap <Long, SapBpEntity> ();
		shiptoSilosMap = new HashMap<Long, ShiptoSiloEntity>();
		if (shiptoSarea != null) {
			shiptoSarea.setFreeDeliveryFlag(false);
			shiptoSarea.setReleaseSapOptionalFlag(false);
			if (shiptoSarea.getBilltos() != null) {
			    shiptoSarea.getBilltos().clear();
			}
		}
	}

	/**
	 * This list is supposed to contain all the business partners defined by SAP partner functions
	 * for the soldto as possible billtos. The shiptobilltos can only be a subset of these business partners.
	 * 
	 * @param billto
	 */
	public void addBillto(SapSoldBillSareaSrchEntity billto) {
		billtos.add(billto);
		Collections.sort(billtos, new BilltoComparator());
	}
	
	/**
	 * This is supposed to return all the possible billtos according to the soldto billto partner 
	 * functions from SAP.
	 * 
	 * @return
	 */
	public List <SapSoldBillSareaSrchEntity> getBilltos() {
		return billtos;
	}
	
	/**
	 * Resets the possible billtos (according to SAP soldto-billto partner functions).
	 * This is supposed to be the backing list of shipto billtos: if a business partner 
	 * is not in this list, there should not be a shiptobillto for that either.
	 */
	public void resetBilltos() {
		billtos = new LinkedList <SapSoldBillSareaSrchEntity>();
	}

	/**
	 * Builds up a map with the shipto billto partners. These are those which will be checked on
	 * the GUI on the sales area tab upper section.
	 * 
	 * @param shiptoBilltos
	 */
	public void addShiptoBilltos(List <ShiptoBilltoEntity> shiptoBilltos) {
		ArrayList <SapBpEntity> newBilltoList = new ArrayList <SapBpEntity>();
		if (shiptoBilltos != null) {
			Iterator i = shiptoBilltos.iterator();
			while (i.hasNext()) {
				/*SapSoldBillSareaSrchEntity billto = (SapSoldBillSareaSrchEntity) i.next();
				shiptoBilltosMap.put(billto.getBilltoSapBpId(), billto);
				newBilltoList.add(billto);*/
				SapBpEntity billtoSapBp = ((ShiptoBilltoEntity)i.next()).getBilltoSapBp();
				shiptoBilltosMap.put(billtoSapBp.getSapBpId(), billtoSapBp);
				newBilltoList.add(billtoSapBp);
				
			}
		}
	}
	
	public void addShiptoSilos(List <ShiptoSiloEntity> shiptoSilos) {
		ArrayList <ShiptoSiloEntity> newSiloList = new ArrayList <ShiptoSiloEntity>();
		shiptoSilosMap = new HashMap();
		if (shiptoSilos != null) {
			Iterator i = shiptoSilos.iterator();
			while (i.hasNext()) {
				ShiptoSiloEntity silo = ((ShiptoSiloEntity)i.next());
				shiptoSilosMap.put(silo.getShiptoSiloId(), silo);
				newSiloList.add(silo);
			}
		}
		this.shiptoSilos = newSiloList;
	}
	
	public List<ShiptoSiloEntity> getShiptoSilos() {
		return shiptoSilos;
	}
	
	/**
	 * Returns true if a business partner which is allowed to be checked on screen (has a soldto+billto partner function
	 * from SAP) and it is also signed to be added to the shipto billto list.
	 * @param idx
	 * @return
	 */
	public boolean getShiptoBillto(int idx){
		if (idx < 0){
			return false;
		}
		return shiptoBilltosMap.containsKey(((SapSoldBillSareaSrchEntity)billtos.get(idx)).getBilltoSapBpId());
	}
	
	/**
	 * Setter, if on the GUI a business partner is marked to be added to the shipto billtos.
	 * 
	 * @param idx
	 * @param value
	 */
	public void setShiptoBillto(int idx, boolean value) {
		if ((idx >= 0) && value) {
			SapSoldBillSareaSrchEntity billto = (SapSoldBillSareaSrchEntity)billtos.get(idx);
			ShiptoBilltoEntity shiptoBillto = new ShiptoBilltoEntity();

			// a dummy billtoSapBp is created here -> important to load the original one from DB before persisting!
			SapBpEntity newBilltoSapBp = new SapBpEntity();
			newBilltoSapBp.setName1(billto.getBilltoName());
			newBilltoSapBp.setSapId(billto.getBilltoNr());
			newBilltoSapBp.setSapBpId(billto.getBilltoSapBpId());
			shiptoBillto.setBilltoSapBp(newBilltoSapBp);
			newBilltoSapBp.setMarkedForDeletion(Constant.MARKED_FOR_DELETION_FALSE);
			shiptoBillto.setShiptoSarea(shiptoSarea);
			if (shiptoSarea.getBilltos() == null) {
			    shiptoSarea.setBilltos(new ArrayList());
			}
			shiptoSarea.getBilltos().add(shiptoBillto);
			shiptoBilltosMap.put(billto.getBilltoSapBpId(), newBilltoSapBp);
			Collections.sort(shiptoSarea.getBilltos(), new BilltoComparator());
		}
	}

	public ShiptoSareaEntity getShiptoSarea() {
		return shiptoSarea;
	}

	public void setShiptoSarea(ShiptoSareaEntity shiptoSarea) {
		this.shiptoSarea = shiptoSarea;
	}


	public ShiptoSareaRestrEntity getRestrictionForSettingByIndex(int restrIdx) {
		final DeliverySettingDTO deliverySettingDTO = getRestrictionDeliverySettings().get(restrIdx);
		ShiptoSareaRestrEntity restr = deliverySettingDTO.getShiptoSareaRestr();
		return restr;
	}
	
	public DeliverySettingDTO getWrappedDeliverySettingForIdx(int idx) {
		final DeliverySettingDTO deliverySettingDTO = getRestrictionDeliverySettings().get(idx);
		return (DeliverySettingDTO) deliverySettingDTO;
	}
	
	/**
	 * @param restrIdx
	 * @return
	 */
	public DeliveryOptionConfigurationDTO getDeliverySettingOptionConfiguration(int restrIdx) {
		DeliverySettingDTO setting = getDeliverySettingForIndex(restrIdx);
		
        DeliveryOptionConfigurationDTO options = DeliveryOptionConfigurationWrapper.getDeliveryOptionConfiguration(getSalesAreaMap(), setting);
		return options;
	}
	
	
	/**
	 * @param restrIdx
	 * @return
	 */
	public DeliverySettingDTO getDeliverySettingForIndex(int restrIdx) {
		final DeliverySettingDTO deliverySettingDTO = getRestrictionDeliverySettings().get(restrIdx);
		return deliverySettingDTO;
	}
	
	public BpSareaSofficeSgroupEntity getSapBpSarea() {
		return sapBpSarea;
	}

	public void setSapBpSarea(BpSareaSofficeSgroupEntity sapBpSarea) {
		this.sapBpSarea = sapBpSarea;
	}

	public BilltoRuletypeBsEntity getDefaultRuleType() {
		return defaultRuleType;
	}

	public void setDefaultRuleType(BilltoRuletypeBsEntity defaultRuleType) {
		this.defaultRuleType = defaultRuleType;
	}


	public String getProductListJSON(){
		
			final JSONSerializer jsonSerializer = JsonUtils.getJsonSerializer();
			jsonSerializer.exclude(DeliverySettingDTO.excludeEntities);

			return jsonSerializer.serialize(salesAreaMap.getBasicDeliverySettings());
	}
	
	public String getServiceOnlyProductListJSON() {
		List<DeliverySettingDTO> result = new ArrayList<DeliverySettingDTO>();

		final JSONSerializer jsonSerializer = JsonUtils.getJsonSerializer();
		jsonSerializer.exclude(DeliverySettingDTO.excludeEntities);

		if (salesAreaMap.getServiceDeliverySettings() != null) {
			for (DeliverySettingDTO deliverySetting : salesAreaMap.getServiceDeliverySettings().getPossibleDeliverySettings()) {
				final int deliverySettingType = deliverySetting.getDeliverySettingType();
				if (deliverySettingType == DeliverySettingDTO.DELIVERY_SETTING_BASED_ON_CONTRACT) {
					result.add(deliverySetting);
				}
			}
		}
		return jsonSerializer.serialize(result);
	}

	
	public String getRestrictionDeliverySettingsJSON() {
		
		return JsonUtils.getDeliverySettingJSONSerializer().serialize(getRestrictionDeliverySettings());
	}
	
	public List<DeliverySettingDTO> getRestrictionDeliverySettings() {
		List<DeliverySettingDTO> allRestrictionDeliverySettings = salesAreaMap == null ? new ArrayList<DeliverySettingDTO>() : salesAreaMap.getRestrictionDeliverySettings();
		if(allRestrictionDeliverySettings != null) {
			List<DeliverySettingDTO> newDeliverySettingList = getNewDeliverySettingList();
			if(newDeliverySettingList != null) {
				allRestrictionDeliverySettings.addAll(newDeliverySettingList);
			}
			Collections.sort(allRestrictionDeliverySettings, new DeliverySettingWrapperComparator());
		}
		return allRestrictionDeliverySettings;
	}
	

	public DeliverySettingSalesAreaMap getSalesAreaMap() {
		return salesAreaMap;
	}

	public void setSalesAreaMap(DeliverySettingSalesAreaMap salesAreaMap) {
		this.salesAreaMap = salesAreaMap;
	}


	/*
	 * Lead time validation fields
	 */

	public String getApplyNdLeadtimeChkUntilMi() {
		String applyNdLeadtimeChkUntilMi = "";
		
		if (shiptoSarea.getApplyNdLeadtimeChkUntilMi() != null) {
			applyNdLeadtimeChkUntilMi = DateTimeUtils.formatHoursMinutes( 
					DateTimeUtils.getHoursMinutes(shiptoSarea.getApplyNdLeadtimeChkUntilMi()));
		}
		return applyNdLeadtimeChkUntilMi;
	}

	public void setApplyNdLeadtimeChkUntilMi(String applyNdLeadtimeChkUntilMi) {
		if (StringUtils.empty(applyNdLeadtimeChkUntilMi)) {
			shiptoSarea.setApplyNdLeadtimeChkUntilMi(null);
		}
		else {
			Long minAfterMidnight = null;
			try {
				Date parseHoursMinutes = DateTimeUtils.parseHoursMinutes(applyNdLeadtimeChkUntilMi);
				minAfterMidnight = DateTimeUtils.getMinAfterMidnight(parseHoursMinutes);
			}
			catch (ParseException e) {
				minAfterMidnight = null;
			}
			shiptoSarea.setApplyNdLeadtimeChkUntilMi(minAfterMidnight);
		}
	}
	
	public String getApplySdLeadtimeChkFromMi() {
		String applySdLeadtimeChkFromMi = "";
		
		if (shiptoSarea.getApplySdLeadtimeChkFromMi() != null) {
			applySdLeadtimeChkFromMi = DateTimeUtils.formatHoursMinutes( 
					DateTimeUtils.getHoursMinutes(shiptoSarea.getApplySdLeadtimeChkFromMi()));
		}
		return applySdLeadtimeChkFromMi;
	}

	public void setApplySdLeadtimeChkFromMi(String applySdLeadtimeChkFromMi) {
		if (StringUtils.empty(applySdLeadtimeChkFromMi)) {
			shiptoSarea.setApplySdLeadtimeChkFromMi(null);
		}
		else {
			Long minAfterMidnight = null;
			try {
				Date parseHoursMinutes = DateTimeUtils.parseHoursMinutes(applySdLeadtimeChkFromMi);
				minAfterMidnight = DateTimeUtils.getMinAfterMidnight(parseHoursMinutes);
			}
			catch (ParseException e) {
				minAfterMidnight = null;
			}
			shiptoSarea.setApplySdLeadtimeChkFromMi(minAfterMidnight);
		}
	}

	public DistanceValidationEditGuiBean getDistanceValidationGuiBeanShipto() {
		return distanceValidationGuiBeanShipto;
	}

	public void setDistanceValidationGuiBeanShipto(DistanceValidationEditGuiBean distanceValidationGuiBeanShipto) {
		this.distanceValidationGuiBeanShipto = distanceValidationGuiBeanShipto;
	}

	public DistanceValidationEditGuiBean getDistanceValidationGuiBeanSarea() {
		return distanceValidationGuiBeanSarea;
	}

	public void setDistanceValidationGuiBeanSarea(DistanceValidationEditGuiBean distanceValidationGuiBeanSarea) {
		this.distanceValidationGuiBeanSarea = distanceValidationGuiBeanSarea;
	}
}
