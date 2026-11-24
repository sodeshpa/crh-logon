/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.taglib.fallbacklogic;

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.DefaultFallbackLogic;
import com.crh.logon.business.services.utils.DefaultFallbackLogic.LevelOneFallbackStructure;
import com.crh.logon.business.services.utils.DefaultFallbackLogic.LevelTwoFallbackStructure;
import com.crh.logon.business.services.utils.OrderTakingEditorDto;
import com.crh.logon.business.services.utils.OrderTakingEditorOrderItemDto;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.ConfigBeanProvider;
import com.crh.logon.utils.ConfigBean;

/**
 * Main part of defaultFallback tag. Sets the values, which will be used for rendering the checkbox 
 * and the inputField.
 * @author AdNovum Informatik AG
 */
public class DefaultFallBackLogicTag extends TagSupport { 

	private static final String C_NAME = 
		DefaultFallBackLogicTag.class.getName();

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(C_NAME);

	public static final String PROPERTY_DEFAULT_SHIPTO_UNLOADING_TIME_MIN_CEM = "shiptoUnloadingCem";
	public static final String PROPERTY_DEFAULT_SHIPTO_UNLOADING_TIME_MIN_AGG = "shiptoUnloadingAgg";	
	public static final String PROPERTY_DEFAULT_SHIPTO_LOADING_TIME_MIN_LF = "shiptoLoadingLf";
	
	public static final String PROPERTY_DEFAULT_SILO_UNLOADING_TIME_MIN = "siloUnloading";
	public static final String PROPERTY_DEFAULT_LOADINGPOINT_LOADING_TIME_MIN = "loadingPointLoading";
	
	/**
	 * This case is used on Call center heading, when the user enters the unloading time of the newly created order.
	 */
	public static final String PROPERTY_DEFAULT_ORDER_CREATION_UNLOADING_TIME_MIN = "orderCreationUnloading";
	
	public static final String PROPERTY_DEFAULT_ORDER_CREATION_LOADING_TIME_MIN = "orderCreationLoading";

	private static final LoadingpointEntity LOADINGPOINT_NOT_IN_LIST = null;

	//input parameters
	private String objectName = null;
	private String objectName2 = null;	
	private String propertyName = null;
	private String propertyInputFieldName = null;
	private String propertyCheckboxName = null;

	private String propertyValue = null;
	private Object propertyFallbackValues = null;
	private boolean changeDisabled = false;
	private boolean readOnly = false;
	


	public int doStartTag() throws JspException {
		Object obj = null;
		Object obj2 = null;
		changeDisabled = false;
		
		ConfigBean configBean = ConfigBeanProvider.getConfigBean();
		DefaultFallbackLogic defaultFallbackLogic = new DefaultFallbackLogic(configBean);
		
		if (propertyName.equals(PROPERTY_DEFAULT_SHIPTO_UNLOADING_TIME_MIN_CEM)){
			if ( StringUtils.notEmpty(objectName )) {
				obj = pageContext.getAttribute(objectName);
			}
			if ( obj instanceof ShiptoEntity) {
				ShiptoEntity entity = (ShiptoEntity) obj;
				
				//make it local, never modify the object itself, it is only for detetermining the defaults!
				ShiptoEntity localEnt = new ShiptoEntity();
				localEnt.setUnloadingTimeCemMin(entity.getUnloadingTimeCemMin());
				
				setChangeDisabled(localEnt.getUnloadingTimeCemMin() == null);
				
				Long value = defaultFallbackLogic.getShiptoUnloadingtimeCemValue(localEnt);
				setPropertyValue(value != null ? value.toString() : "");
				setPropertyFallbackValues(defaultFallbackLogic.getShiptoUnloadingtimeCem(localEnt));
				localEnt = null;
			}
			else{
				LOG.error("Unsupported object given to DefaultFallbackLogicTag");
				throw new LogonException();
			}
		}
		if (propertyName.equals(PROPERTY_DEFAULT_SHIPTO_UNLOADING_TIME_MIN_AGG)){
			if ( StringUtils.notEmpty(objectName )) {			
				obj = pageContext.getAttribute(objectName);
			}
			if ( obj instanceof ShiptoEntity) {
				ShiptoEntity entity = (ShiptoEntity) obj;
				
				//make it local, never modify the object itself, it is only for detetermining the defaults!
				ShiptoEntity localEnt = new ShiptoEntity();
				localEnt.setUnloadingTimeAggMin(entity.getUnloadingTimeAggMin());
				
				setChangeDisabled(localEnt.getUnloadingTimeAggMin() == null);
				Long value = defaultFallbackLogic.getShiptoUnloadingtimeAggValue(entity);
				setPropertyValue(value != null ? value.toString() : "");
				setPropertyFallbackValues(defaultFallbackLogic.getShiptoUnloadingtimeAgg(localEnt));
				localEnt = null;
			}
			else{
				LOG.error("Unsupported object given to DefaultFallbackLogicTag");
				throw new LogonException();
			}
		}
		if (propertyName.equals(PROPERTY_DEFAULT_SHIPTO_LOADING_TIME_MIN_LF)){
			if ( StringUtils.notEmpty(objectName )) {			
				obj = pageContext.getAttribute(objectName);
			}
			if ( obj instanceof ShiptoEntity) {
				ShiptoEntity entity = (ShiptoEntity) obj;
				
				//make it local, never modify the object itself, it is only for detetermining the defaults!
				ShiptoEntity localEnt = new ShiptoEntity();
				localEnt.setLoadingTimeLfMin(entity.getLoadingTimeLfMin());
				
				setChangeDisabled(localEnt.getLoadingTimeLfMin() == null);
				Long value = defaultFallbackLogic.getShiptoLoadingtimeLfValue(entity);
				setPropertyValue(value != null ? value.toString() : "");
				setPropertyFallbackValues(defaultFallbackLogic.getShiptoLoadingtimeLf(localEnt));
				localEnt = null;
			}
			else{
				LOG.error("Unsupported object given to DefaultFallbackLogicTag");
				throw new LogonException();
			}
		}		
		if (propertyName.equals(PROPERTY_DEFAULT_SILO_UNLOADING_TIME_MIN)){
			if ( StringUtils.notEmpty(objectName )) {			
				obj = pageContext.getAttribute(objectName);
			}			
			if ( obj instanceof ShiptoSiloEntity) {
				ShiptoSiloEntity entity = (ShiptoSiloEntity) obj;
				
				//copy the meaningful values of the entity, make it local
				ShiptoSiloEntity localEntity = new ShiptoSiloEntity();
				localEntity.setUnloadingTime(entity.getUnloadingTime());
				ShiptoEntity localShent = new ShiptoEntity();
				if (entity.getShipto() != null ){
					localShent.setUnloadingTimeCemMin(entity.getShipto().getUnloadingTimeCemMin());
					localShent.setUnloadingTimeAggMin(entity.getShipto().getUnloadingTimeAggMin());
					localEntity.setSapMaterial(entity.getSapMaterial());
				}
				else{
					localShent.setUnloadingTimeCemMin(null);
					localShent.setUnloadingTimeAggMin(null);
				}
				localEntity.setShipto(localShent);

				if (StringUtils.notEmpty(propertyValue)) {
					Long value = null;
					try{			
						value = new Long(propertyValue);
						entity.setUnloadingTime(value);
						setChangeDisabled(false);
					}
					catch (Exception e){
						setChangeDisabled(false);
					}
				}
				else{
					Long value = defaultFallbackLogic.getShiptoSiloUnloadingTimeValue(localEntity);
					setPropertyValue(value != null ? value.toString() : "");				
					setChangeDisabled(localEntity.getUnloadingTime() == null);
				}
				setPropertyFallbackValues(defaultFallbackLogic.getShiptoSiloUnloadingTime(localEntity));
				localEntity = null;
				localShent = null;	
			}
			else{
				LOG.error("Unsupported object given to DefaultFallbackLogicTag");
				throw new LogonException();
			}
		}
		else if (propertyName.equals(PROPERTY_DEFAULT_LOADINGPOINT_LOADING_TIME_MIN)) {			
			LoadingpointEntity localEnt = new LoadingpointEntity();
			if (StringUtils.notEmpty(propertyValue)) {
				Long value = null;
				try{			
					value = new Long(propertyValue);
					localEnt.setLoadingTime(value);
					setChangeDisabled(false);
				}
				catch (Exception e){
					setChangeDisabled(false);
				}
			}
			else{
				Long value = defaultFallbackLogic.getLoadingpointLoadingtimeValue(localEnt);
				setPropertyValue(value != null ? value.toString() : "");				
				setChangeDisabled( true );
			}
			setPropertyFallbackValues(defaultFallbackLogic.getLoadingpointLoadingtime(localEnt));
			localEnt = null;
		}
		else if (propertyName.equals(PROPERTY_DEFAULT_ORDER_CREATION_UNLOADING_TIME_MIN)) {			
			if (StringUtils.notEmpty(objectName) && StringUtils.notEmpty(objectName2)) {
				obj = pageContext.getAttribute(objectName);
				obj2 = pageContext.getAttribute(objectName2);
			}
			if ( obj instanceof ShiptoEntity && obj2 instanceof List<?>) {
				ShiptoEntity shipto = (ShiptoEntity) obj;
				List<OrderTakingEditorDto> editorList = (List<OrderTakingEditorDto>)obj2;
				
				OrderTakingEditorDto firstEditor = editorList.get(0);
				if (firstEditor != null && firstEditor.getOrderItems() != null && firstEditor.getOrderItems().get(0) != null) {
					OrderTakingEditorOrderItemDto firstOrderItemEditor = firstEditor.getOrderItems().get(0);
					OrderItemEntity firstOrderItem = firstOrderItemEditor.getOrderItem();
					
					//make it local, never modify the object itself, it is only for detetermining the defaults!
					OrderItemEntity localOrderItem = new OrderItemEntity();
					localOrderItem.setSapMaterial(firstOrderItem.getSapMaterial());
					localOrderItem.setShiptoSilo(firstOrderItem.getShiptoSilo());
					
					ShiptoEntity localShipto = new ShiptoEntity();
					localShipto.setUnloadingTimeCemMin(shipto.getUnloadingTimeCemMin());
					localShipto.setUnloadingTimeAggMin(shipto.getUnloadingTimeAggMin());
					localShipto.setLoadingTimeLfMin(shipto.getLoadingTimeLfMin());
					
					Long value = defaultFallbackLogic.getPlannedUnloadingTimeValue(localOrderItem, localShipto);					
					setPropertyValue(value != null ? value.toString() : "");
					setPropertyFallbackValues(defaultFallbackLogic.getPlannedUnloadingTime(localOrderItem, localShipto));
					localOrderItem = null;
					localShipto = null;
				}
				else {
					Long value = 0l;
					setPropertyValue(value != null ? value.toString() : "");
					setChangeDisabled( true );
				}
			}
			else{
				LOG.error("Unsupported object given to DefaultFallbackLogicTag");
				throw new LogonException();
			}
		}
		else if (propertyName.equals(PROPERTY_DEFAULT_ORDER_CREATION_LOADING_TIME_MIN)) {
			setChangeDisabled(true);
			if (StringUtils.notEmpty(objectName) && StringUtils.notEmpty(objectName2)) {
				obj = pageContext.getAttribute(objectName);
				obj2 = pageContext.getAttribute(objectName2);
			}
			if (obj2 instanceof List<?>) {
				List<OrderTakingEditorDto> editorList = (List<OrderTakingEditorDto>) obj2;

				Long value = 0l;

				OrderTakingEditorDto firstEditor = editorList.get(0);
				if (firstEditor != null && firstEditor.getOrderItems() != null && firstEditor.getOrderItems().get(0) != null) {

					OrderTakingEditorOrderItemDto firstOrderItemEditor = firstEditor.getOrderItems().get(0);
					if (firstOrderItemEditor.getOrderItem() != null && firstOrderItemEditor.getOrderItem().getLoadingpoint() != null) {						

						LoadingpointEntity loadingPoint = firstOrderItemEditor.getOrderItem().getLoadingpoint();

						value = defaultFallbackLogic.getLoadingpointLoadingtimeValue(loadingPoint);
						setPropertyFallbackValues(defaultFallbackLogic.getLoadingpointLoadingtime(loadingPoint));
					}
				}

				setPropertyValue(value != null ? value.toString() : "");
			}
			else {
				LOG.error("Unsupported object given to DefaultFallbackLogicTag");
				throw new LogonException();
			}
		}
		return EVAL_BODY_INCLUDE;
	}

	public Long getFirstAvailableDefaultValue(){
		Long val = null;
		if (propertyName.equals(PROPERTY_DEFAULT_SHIPTO_UNLOADING_TIME_MIN_CEM)){			
			LevelOneFallbackStructure values = (LevelOneFallbackStructure) getPropertyFallbackValues();
			val = values.getLevelOneDefaultValue();
		}
		else if (propertyName.equals(PROPERTY_DEFAULT_SHIPTO_UNLOADING_TIME_MIN_AGG)){			
			LevelOneFallbackStructure values = (LevelOneFallbackStructure) getPropertyFallbackValues();
			val = values.getLevelOneDefaultValue();
		}
		else if (propertyName.equals(PROPERTY_DEFAULT_SHIPTO_LOADING_TIME_MIN_LF)){			
			LevelOneFallbackStructure values = (LevelOneFallbackStructure) getPropertyFallbackValues();
			val = values.getLevelOneDefaultValue();
		}
		else if (propertyName.equals(PROPERTY_DEFAULT_LOADINGPOINT_LOADING_TIME_MIN)){
			LevelOneFallbackStructure values = (LevelOneFallbackStructure) getPropertyFallbackValues();
			val = values.getLevelOneDefaultValue();
		}
		else if (propertyName.equals(PROPERTY_DEFAULT_SILO_UNLOADING_TIME_MIN)){			
			LevelTwoFallbackStructure values = (LevelTwoFallbackStructure) getPropertyFallbackValues();
			if (values.getLevelOneDefaultValue() != null ){
				val = values.getLevelOneDefaultValue();
			}
			else if (values.getLevelTwoDefaultValue() != null ) {
				val = values.getLevelTwoDefaultValue();
			}
			else{
				return val = null;
			}
		}
		else if (propertyName.equals(PROPERTY_DEFAULT_ORDER_CREATION_LOADING_TIME_MIN)) {
			LevelOneFallbackStructure values = (LevelOneFallbackStructure) getPropertyFallbackValues();
			val = values.getLevelOneDefaultValue();
		}
		else if (propertyName.equals(PROPERTY_DEFAULT_ORDER_CREATION_UNLOADING_TIME_MIN)) {
			LevelTwoFallbackStructure values = (LevelTwoFallbackStructure) getPropertyFallbackValues();
			if (values.getLevelOneDefaultValue() != null ){
				val = values.getLevelOneDefaultValue();
			}
			else if (values.getLevelTwoDefaultValue() != null ) {
				val = values.getLevelTwoDefaultValue();
			}
			else{
				return val = null;
			}
		}		
		return val;
	}
	
	public int doEndTag(){
		int i = EVAL_PAGE;
		setObjectName(null);
		setObjectName2(null);
		setPropertyName(null);
		setPropertyInputFieldName(null);
		setPropertyCheckboxName(null);
		setPropertyValue(null);
		setPropertyFallbackValues(null);
		setChangeDisabled(true);
		setReadOnly(false);
		return i;
	}

	public String getObjectName() {
		return objectName;
	}


	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	

	public String getObjectName2() {
		return objectName2;
	}
	
	
	public void setObjectName2(String objectName2) {
		this.objectName2 = objectName2;
	}
	

	public String getPropertyName() {
		return propertyName;
	}
	

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}


	public String getPropertyInputFieldName() {
		return propertyInputFieldName;
	}



	public void setPropertyInputFieldName(String propertyInputFieldName) {
		this.propertyInputFieldName = propertyInputFieldName;
	}


	public String getPropertyCheckboxName() {
		return propertyCheckboxName;
	}



	public void setPropertyCheckboxName(String propertyCheckboxName) {
		this.propertyCheckboxName = propertyCheckboxName;
	}



	public String getPropertyValue() {
		return propertyValue;
	}



	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}



	public boolean isChangeDisabled() {
		return changeDisabled;
	}



	public void setChangeDisabled(boolean changeDisabled) {
		this.changeDisabled = changeDisabled;
	}



	public Object getPropertyFallbackValues() {
		return propertyFallbackValues;
	}



	public void setPropertyFallbackValues(Object propertyFallbackValues) {
		this.propertyFallbackValues = propertyFallbackValues;
	}
	

	public boolean isReadOnly() {
		return readOnly;
	}
	

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}


}
