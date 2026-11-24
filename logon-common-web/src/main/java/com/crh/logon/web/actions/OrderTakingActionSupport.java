/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.actions;

import java.util.ArrayList;
import java.util.Date;

import com.crh.logon.constants.Constant;
import com.crh.logon.web.utils.ServiceLocatorProvider;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.CustorderItemEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.PersonEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.refdata.OrderCreationTypeBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;

/**
 * Superclass of all order taking screens, functions are used from all of them, or there has to be one
 * for each order taking screen.
 * 
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 */
public class OrderTakingActionSupport extends ActionSupport {

    protected CustorderEntity createCustorder(SapBpEntity soldTo,
			ShiptoEntity shipto) {
		
		CustorderEntity custorderEntity = new CustorderEntity(); // pickup order
		
		PersonEntity creator =  getUser().getPerson();
		
		custorderEntity.setShiptoSapBp(shipto.getShiptoSapBp());
		
		// BB
		custorderEntity.setOrderConfFaxFlag(BooleanUtils.isTrue(shipto.getOrderConfFaxFlag()) && !StringUtils.isEmpty(shipto.getOrderConfFaxNumber()));
		custorderEntity.setOrderConfFaxNumber(shipto.getOrderConfFaxNumber());
		custorderEntity.setOrderConfEmailFlag(BooleanUtils.isTrue(shipto.getOrderConfEmailFlag())  && !StringUtils.isEmpty(shipto.getOrderConfEmailAddresses()));
		custorderEntity.setOrderConfEmailAdresses(shipto.getOrderConfEmailAddresses());
		custorderEntity.setOrderConfSmsFlag(BooleanUtils.isTrue(shipto.getOrderConfSmsFlag()) && !StringUtils.isEmpty(shipto.getOrderConfSmsNumber()));
		custorderEntity.setOrderConfSmsNumber(shipto.getOrderConfSmsNumber());

		custorderEntity.setLoadConfFaxFlag(BooleanUtils.isTrue(shipto.getLoadConfFaxFlag()) && !StringUtils.isEmpty(shipto.getOrderConfFaxNumber()));
		custorderEntity.setLoadConfEmailFlag(BooleanUtils.isTrue(shipto.getLoadConfEmailFlag())  && !StringUtils.isEmpty(shipto.getOrderConfEmailAddresses()));
		custorderEntity.setLoadConfSmsFlag(BooleanUtils.isTrue(shipto.getLoadConfSmsFlag()) && !StringUtils.isEmpty(shipto.getOrderConfSmsNumber()));

		// LS
		custorderEntity.setDelivnoteFaxFlag(BooleanUtils.isTrue(shipto.getFaxDelivnoteFlag()) && !StringUtils.isEmpty(shipto.getOrderConfFaxNumber()));
		custorderEntity.setDelivnoteEmailFlag(BooleanUtils.isTrue(shipto.getEmailDelivnoteFlag()) && !StringUtils.isEmpty(shipto.getOrderConfEmailAddresses()));
		
		custorderEntity.setSoldtoSapBp(soldTo);
		
		custorderEntity.setCustorderItems(new ArrayList<CustorderItemEntity>());
				
		String contactPersonTxt = (creator.getFirstname() != null ? creator.getFirstname() + " " : "") + (creator.getName() != null ? creator.getName(): "");  
		custorderEntity.setContactPersonTxt(contactPersonTxt);
		return custorderEntity;
	}
	
    /**
	 * Initializing customer order attributes which are immutable on web order.
	 * @param shipto
	 * @return
	 */
	protected CustorderEntity customInitWebCustorder(ShiptoEntity shipto,CustorderEntity custOrder) {
		
		custOrder.setOrderCreationType(getServiceLocator().getRefDataGenericsService().findBsEntity(OrderCreationTypeBsEntity.class, Constant.ORDER_CREATION_TYPE_WEB_ORDER));
	    
		custOrder.setDelivnoteSinglePaperFlag(BooleanUtils.isTrue(shipto.getPaperSingleDelnoteFlagCem()) );
		custOrder.setDelivnoteCollPaperFlag(BooleanUtils.isTrue(shipto.getPaperCollDelnoteFlagCem()) );
		
	    // Comments
	    custOrder.setNoteOrderMgmt(shipto.getNoteOrderMgmt());
	    custOrder.setNoteDelivery(shipto.getNoteDelivery());
	    custOrder.setNoteDispatching(shipto.getNoteDispatching());
	    custOrder.setNoteTruckDriver(shipto.getNoteTruckDriver());
//	    custOrder.setNoteRailwayDisp(shipto.getNoteRailwayDisp());
		
		custOrder.setOnHoldOrderAndLockedFlag(Boolean.FALSE);
		custOrder.setFreeDeliveryFlag(Boolean.FALSE);
		
		
		return custOrder;
	}
	
	/**
	 * Initializing customer order attributes which are immutable on pickup order.
	 * @param custorderEntity
	 * @param loadingpoint
	 * @return
	 */
	protected CustorderEntity customInitPickupCustorder(CustorderEntity custorderEntity, LoadingpointEntity loadingpoint) {
		
		RefDataGenericsService refData = ServiceLocatorProvider.getServiceLocator().getRefDataGenericsService();
		custorderEntity.setOrderCreationType(refData.findBsEntity(OrderCreationTypeBsEntity.class, Constant.ORDER_CREATION_TYPE_PICKUP));
		
		//this flag has an effect in endLoad, it is responsible for being printed for sure at endload
		custorderEntity.setDelivnoteSinglePaperFlag( Boolean.TRUE );
		custorderEntity.setDelivnoteCollPaperFlag( Boolean.FALSE );
		
		// Comments
		custorderEntity.setNoteOrderMgmt(null);
		custorderEntity.setNoteDelivery(null);
		custorderEntity.setNoteDispatching(null);
		custorderEntity.setNoteTruckDriver(null);
		
		Date now = new Date();
		custorderEntity.setDeliveryFromDat(now);
		custorderEntity.setDeliveryToDat(now);
		
		ShippingpointEntity shippingpointEntity = loadingpoint.getShippingpoint();
		custorderEntity.setShippingpoint(shippingpointEntity);
		
		return custorderEntity;
	}

	protected TimeZone createTimeZoneForShipTo(ShiptoEntity shipto) {
		return getTimeZoneService().loadForShipTo(shipto, getUserLocale());
	}
}

