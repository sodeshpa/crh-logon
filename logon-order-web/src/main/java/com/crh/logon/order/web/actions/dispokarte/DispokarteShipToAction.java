/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.dispokarte;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJBException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.forms.dispokarte.DispokarteForm;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.ordercreation.DeliverySettingShiptoMap;
import com.crh.logon.business.services.interfaces.DispokarteService;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.refdata.LangTxEntity;
import com.crh.logon.persistence.entities.refdata.SapCountryTxEntity;
import com.crh.logon.persistence.entities.refdata.ShiptoTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.ShiptoTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.SignatureTypeTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_dispokarte_DispokarteForm"
 *  path="/dispokarte/DispokarteShipTo" scope="session"
 *  validate="true" input="DispokarteShipToTile.def"
 *
 * @struts.action-forward name="retry" path="DispokarteShipToTile.def"
 * @struts.action-forward name="select" path="/dispokarte/DispokarteEntry.do"
 */
public class DispokarteShipToAction extends OrderActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(DispokarteShipToAction.class);

	public ActionForward execute(
		ActionMapping mapping, ActionForm form,
		HttpServletRequest request,	HttpServletResponse response)
		throws IOException, ServletException, EJBException
	{
		// [[
		ActionForward result = null;
		Object[][] params = {{"mapping", mapping},
							{"form", form},
							{"request", request},
							{"response", response}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "execute", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.MASTER_DATA_SHIPTO_CARD_R);
			DispokarteForm helpForm;
			String forwardName = "select";
			
			setSelectedTab("dispokarte", request);
			
		    // Get the struts form.
		    helpForm = (DispokarteForm) form;

		    ShiptoEntity newShipto = null;
		    
		    String soldtoSapId = helpForm.getSoldToId();
		    String shiptoSapId = helpForm.getShipToId();
		    
		    if ( (StringUtils.isEmpty(soldtoSapId) || StringUtils.isEmpty(shiptoSapId)) ) {		    	
		    	return result = mapping.findForward(forwardName);
		    }
			
		    DispokarteService dispokarteService = getServiceLocator().getDispokarteService();
		    newShipto = dispokarteService.fetchAndInitializeShiptoBySapId(shiptoSapId);
		    SapBpEntity shiptoSapBp;

		    if (newShipto == null) {
		    	Authorization.authorize(Authorization.MASTER_DATA_SHIPTO_CARD_W);
		    	newShipto = dispokarteService.createShipto(shiptoSapId, soldtoSapId);
		    	shiptoSapBp = newShipto.getShiptoSapBp();
		    }
		    else {
		    	/*
		    	 * If tlgns_sap_bp.treat_as_onetime_flag has been set from 0 to 1 in SAP, we should respect
		    	 * that and set Ship To type to LNF, but with a warning to the user.
		    	 */
		    	shiptoSapBp = newShipto.getShiptoSapBp();
		    	ShiptoTypeBsEntity shiptoType = newShipto.getShiptoType();
		    	Long shiptoTypeCd = shiptoType.getShiptoTypeCd();

		    	if (shiptoSapBp.getTreatAsOnetimeFlag()
		    			&& shiptoTypeCd != Constant.SHIPTO_TYPE_LOCATION_NOT_FIXED) {
		    		shiptoType = getRefDataGenericsService().findBsEntity(ShiptoTypeBsEntity.class, 
		    				Constant.SHIPTO_TYPE_LOCATION_NOT_FIXED);
		    		newShipto.setShiptoType(shiptoType);
					MessageUtils.addActionWarning(request, "dispokarte.warnings.shipto.type.updated");
		    	}
		    }
		    if (shiptoSapBp.getLang() != null) {
		    	LangTxEntity langEntity = getServiceLocator().getRefDataGenericsService().findLangTxEntity(
		    			request.getLocale(), shiptoSapBp.getLang().getLangCd());
		    	helpForm.setLanguage(langEntity.getTxt());
		    }
		    
		    SapBpEntity soldto = dispokarteService.fetchSoldtoSapBpBySapId(soldtoSapId);
		    helpForm.setSoldTo(soldto);
		    
		    loadSapCountryTxEntity(helpForm, request.getLocale(), soldto.getCountry().getSapCountryCd());
		    
		    List<ShiptoSareaEntity> shiptoSalesAreas = dispokarteService.updateShiptoSalesAreas(newShipto, soldto, true);
		    
		    /**
		     * This structure contains a shipto in itself. As thi shipto and the sales areas are passed into
		     * a service, the reference newShipto and the shiptoMap.getShipto() are not the same!
		     */
			DeliverySettingShiptoMap shiptoMap = getServiceLocator().getDeliverySettingService().initDeliverySettingShiptoMapForMasterDataShipto(soldto, request.getLocale(), shiptoSalesAreas);
			shiptoMap.validateSalesAreas();

            helpForm.setShiptoMap(shiptoMap);

			TimeZone shipToTZ = getServiceLocator().getTimeZoneService().loadForShipTo(newShipto, getUserLocale());
			helpForm.setShipToTimeZone(shipToTZ);

			helpForm.setUserTimeZone(getUserTimezone());
		    
		    if ( helpForm.getShipToTypeList() == null) {
		    	helpForm.setShipToTypeList(getServiceLocator().getRefDataGenericsService().findTxEntityList(ShiptoTypeTxEntity.class, request.getLocale()));
		    }
		    if ( helpForm.getSignatureTypeList() == null) {
		    	helpForm.setSignatureTypeList(getServiceLocator().getRefDataGenericsService().findTxEntityList(SignatureTypeTxEntity.class, request.getLocale()));
		    }
		    if ( helpForm.getDistributionDestTxList() == null) {
		    	helpForm.setDistributionDestTxList(getServiceLocator().getDispokarteService().fetchDistributionDestTxEntity(request.getLocale()));
		    }		    
		    
			return result = mapping.findForward(forwardName);
    	}
		catch (EJBException ex) {
			final Reason reason = ExceptionUtil.getLogonExceptionReason(ex, null);
			if (reason != null) {
				MessageUtils.addActionWarning(request, reason.getReason());
				return result = mapping.findForward("retry");
			}
			else {
				throw ex;
			}
		}
		catch (LogonException e) {
			if (Reason.RES_STRUTS_VALIDATION.equals(e.getReason())) {
				return result = mapping.findForward("retry");
			}
			throw e;
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
	 * Use the users locale as the source of language so the country given back will be something that the user can read.
	 * This is actually used for the google coordinates lookup, but to google maps api it scarecly matter in what language you give the country
	 */
	private void loadSapCountryTxEntity(DispokarteForm helpForm, Locale locale, String sapBpCountryCd) {
		SapCountryTxEntity country = getRefDataGenericsService().findTxEntity(SapCountryTxEntity.class, locale, sapBpCountryCd);
		if (country != null) {
			helpForm.setCountryForShipto(country.getTxt());				
		} else {
			// if we don't find the country tx for the locale the fall back to the 2 char code. it's better than nothing.
			helpForm.setCountryForShipto(sapBpCountryCd);
		}
	}
}
