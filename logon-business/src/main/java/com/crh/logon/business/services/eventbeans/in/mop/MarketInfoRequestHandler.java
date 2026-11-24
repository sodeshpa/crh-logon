/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.services.eventbeans.in.mop;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.PDFGeneratorServiceLocalInterface;
import org.apache.commons.lang.StringUtils;

import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.localinterfaces.EmailServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonEventException;
import com.crh.logon.business.services.utils.MailPropertiesConfiguration;
import com.crh.logon.constants.Constant;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.MarketInfoRequest;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.PdaMessageEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.refdata.PdaMessageTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.SgroupBsEntity;
import com.crh.logon.persistence.entities.refdata.SofficeBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpSareaEntity;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class MarketInfoRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(MarketInfoRequestHandler.class);

    private static final String MAILADDRESS_SEPARATOR = ",";
    private static String FIELD_MESSAGE = "message";
    
	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;	
	@EJB private PDFGeneratorServiceLocalInterface pdfGeneratorService;
	@EJB private EmailServiceLocalInterface emailService;


    
    protected void validate(LogonEventRequest eventBean) {
    	
    	MarketInfoRequest bean = (MarketInfoRequest)eventBean;
    	validateMandatory(FIELD_MESSAGE, bean.getMessage());
    }
        
    protected LogonEventResponse perform(LogonEventRequest eventBean) {
		// [[
		LogonEventResponse result = null;
		Object[][] params = {{"eventBean", eventBean}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "perform", params);
		}
		// ]]
		try {
            MarketInfoRequest bean = (MarketInfoRequest)eventBean;
            
            DriverEntity driver = getCurrentDriver();
            if (driver == null) {
                
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "driver not found"));
            }			
            PdaMessageEntity msg = new PdaMessageEntity();
            msg.setPdaUser(driver.getUser());
            msg.setMessage(bean.getMessage());
            PdaMessageTypeBsEntity pdaMessageType = referentialDataService.findBsEntity(PdaMessageTypeBsEntity.class, Constant.PDA_MESSAGE_TYPE_MARKET_INFO);
            msg.setPdaMessageType(pdaMessageType);
            msg.setReceiverAckFlag(Boolean.FALSE);
            ShiptoEntity shipto = null;
            OrderEntity order = null;
            Locale locale = Locale.GERMAN;
            if (bean.getAssignmentPartId() != null) {
            	AssignmentPartEntity ap = getAssignmentPart(bean.getAssignmentPartId());
            	if (ap.getShipto() != null) {
            		shipto = ap.getShipto();
            	}
            	if (ap.getOrder() != null) {
            		order = ap.getOrder();
            		if (shipto != null) {
	            		Filter<ShiptoEntity> filter = new FilterImpl<ShiptoEntity>();
	            		filter.addExpression(Restrictions.eq(ShiptoEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
	            		filter.addExpression(Restrictions.eq(ShiptoEntity.ATTR_SHIPTO_SAP_BP, ap.getOrder().getShiptoSapBp()));
	            		shipto = getDaoProvider().getShiptoEntityDao().findSingle(filter);
            		}
            	}
            }
            
            String salesDistrict = null;
            SofficeBsEntity salesOffice = null;
            SgroupBsEntity salesGroup = null;
            Collection<SapBpSareaEntity> sareas = null;
            if (shipto != null) {
            	locale = new Locale(shipto.getShiptoSapBp().getLang().getLangCd());
	            Filter<SapBpSareaEntity> filter = new FilterImpl<SapBpSareaEntity>();
	            filter.addExpression(Restrictions.eq(SapBpSareaEntity.ATTR_SAP_BP, shipto.getShiptoSapBp()));
	            if (order != null) {
	            	filter.addExpression(Restrictions.eq(SapBpSareaEntity.ATTR_SAREA, order.getSarea()));
	            }
	            sareas = getDaoProvider().getSapBpSareaEntityDao().find(filter);
	            if (!sareas.isEmpty()) {
	            	for (SapBpSareaEntity sa : sareas) {
	            		if ( (salesDistrict == sa.getSalesDistrict()) || 
	            				((salesDistrict != null) && (sa.getSalesDistrict() != null) && (salesDistrict.equals(sa.getSalesDistrict()))) ) {
	            			salesDistrict = sa.getSalesDistrict();
	            		}
	            		else {
	            			salesDistrict = null;
	            			break;
	            		}
	            	}
	            	for (SapBpSareaEntity sa : sareas) {
	            		if ( (salesOffice == sa.getSoffice()) || 
	            				((salesOffice != null) && (sa.getSoffice() != null) && (salesOffice.getSofficeCd().equals(sa.getSoffice().getSofficeCd()))) ) {
	            			salesOffice = sa.getSoffice();
	            		}
	            		else {
	            			salesOffice = null;
	            			break;
	            		}
	            	}
	            	for (SapBpSareaEntity sa : sareas) {
	            		if ( (salesGroup == sa.getSgroup()) || 
	            				((salesGroup != null) && (sa.getSgroup() != null) && (salesGroup.getSgroupCd().equals(sa.getSgroup().getSgroupCd()))) ) {
	            			salesGroup = sa.getSgroup();
	            		}
	            		else {
	            			salesGroup = null;
	            			break;
	            		}
	            	}
	            }
            }
            // # 0=PDA User first name, 1=PDA user name, 2=Carrier display name
            String subject = pdfGeneratorService.formatText("marketinfo.email.subject", locale, new Object[] {
	           		driver.getUser().getPerson().getFirstname(),
	           		driver.getUser().getPerson().getName(),
	           		driver.getCurrentTruck().getCarrier().getDisplayName() });
	            
			// # 0=MARKET_MESSAGE_TEXT, 1=PDA User first name, 2=PDA user name, 3=Carrier display name
			// # 4=Truck ID, 5=Truck Plate Number, 6=Date/Time, 7=gmap base uri, 8=place of message lat, 9=lon, 
			// # 10=mobile phone PDA User, 11=Order no, 12=SoldTo Name1, 13=SoldTo Name2, 14=ShipTo Name1, 
			// # 15=ShipTo Name2, 16=PLZ 17=Ort, 
			// # 18=country 19=region, 20=Sales District, 21=Sales Office, 22=Sales Group 
            String message = pdfGeneratorService.formatText("marketinfo.email.message", locale, new Object[] {
            		bean.getMessage(),
            		StringUtils.defaultString(driver.getUser().getPerson().getFirstname()),
            		StringUtils.defaultString(driver.getUser().getPerson().getName()),
            		driver.getCurrentTruck().getCarrier().getDisplayName(),
            		driver.getCurrentTruck().getTruckName(),
            		driver.getCurrentTruck().getTruckPlateNo(),
            		bean.getEventTime(),
            		getConfigBean().getGmapApiBaseUrl(),
            		bean.getGeoY().toString(),
            		bean.getGeoX().toString(),
            		StringUtils.defaultString(driver.getUser().getPerson().getMobileNr(), "-"),
            		(order != null ? order.getOrderNumber().toString() : "-"),
            		(order != null ? StringUtils.defaultString(order.getSoldtoSapBp().getName1()) : "-"),
            		(order != null ? StringUtils.defaultString(order.getSoldtoSapBp().getName2()) : "-"),
            		(shipto != null ? StringUtils.defaultString(shipto.getShiptoSapBp().getName1()) : "-"),
            		(shipto != null ? StringUtils.defaultString(shipto.getShiptoSapBp().getName2()) : "-"),
            		(shipto != null ? StringUtils.defaultString(shipto.getShiptoSapBp().getPostalCode()) : "-"),
            		(shipto != null ? StringUtils.defaultString(shipto.getShiptoSapBp().getCity()) : "-"),
            		(shipto != null ? StringUtils.defaultString(shipto.getShiptoSapBp().getCountry().getSapCountryCd()) : "-"),
            		(shipto != null ? StringUtils.defaultString(shipto.getShiptoSapBp().getRegion()) : "-"),
            		(salesDistrict != null ? salesDistrict : "-"),
            		(salesOffice != null ? salesOffice.getSofficeName() : "-"),
            		(salesGroup != null ? salesGroup.getSgroupName() : "-") });
	            
            final Collection<String> mailTos = new HashSet<String>();
        	if (sareas != null) {    		
        		for (SapBpSareaEntity sarea : sareas) {
                    if (sarea.getSgroup() != null) {
                        String sGroupAddresses = sarea.getSgroup().getEmailAddresses();
                        if (sGroupAddresses != null) {
                            Collections.addAll(mailTos, sGroupAddresses.split(MAILADDRESS_SEPARATOR));
                        }
                    }
                }
        	}
        	
        	final Collection<String> mailCcs = new HashSet<String>();
        	final String additionalAddressed = getConfigBean().getMarketmessageSalesadminEmail();
			if (additionalAddressed != null) {
			    Collections.addAll(mailCcs, additionalAddressed.split(MAILADDRESS_SEPARATOR));
			}
			
            mailCcs.removeAll(mailTos);
            
            
            if ( ((mailTos != null) && (!mailTos.isEmpty())) || ((mailCcs != null) && (!mailCcs.isEmpty())) ) {
            	
            	final String[] to = mailTos.toArray(new String[mailTos.size()]);
            	final String[] cc = mailCcs.toArray(new String[mailCcs.size()]);
            	
            	emailService.send(
            			to, cc, null,
            			subject, message,
            			new MailPropertiesConfiguration(order));
            }
            getDaoProvider().getPdaMessageEntityDao().persist(msg);
            
            return result = createLogonEventResponse(MopResponseCode.OK);
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
