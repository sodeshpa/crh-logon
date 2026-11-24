/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.externalsiteadmin;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.forms.externalsiteadmin.ExternalSiteAdminForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.ExternalSiteService;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.ExtSiteEntity;
import com.crh.logon.persistence.entities.sap.SapVendorEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_ExternalSiteAdminForm"
 *  path="/externalsiteadmin/ExternalSiteAdmin" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="/externalsiteadmin/ExternalSite.def"
 */
public class ExternalSiteAdminAction extends ActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ExternalSiteAdminAction.class);
	public static final String FORWARD_SELECT_CARRIER = "selectCarrier";
	public static final String FORWARD_SAVE = "save";
	public static final String FORWARD_SELECT_SITE = "selectSite";
	public static final String FORWARD_RESET = "reset";
	public static final String FORWARD_ADD_SITE = "addSite";


	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
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
			// FIXME (szilagyi Jul 30, 2010): 
			Authorization.authorize(Authorization.MASTER_DATA_ROAD_EXT_SITE_R);
			
			setSelectedTab("masterdata", request);
			setSelectedSub1Tab("road", request);
			setSelectedSub2Tab("externalsite", request);
			String forwardName = "success";
			
			ExternalSiteAdminForm externalSiteAdminForm = (ExternalSiteAdminForm) form;
		 	ExternalSiteService externalSiteAdminService = getServiceLocator().getExternalSiteService();
			String inputAction = externalSiteAdminForm.getInputAction();
		    
			if (inputAction.equals(FORWARD_SELECT_CARRIER)){
				externalSiteAdminForm.clearFields();
				
				if (externalSiteAdminForm.getCarrierId() == Constant.INVALID_OBJECT_ID){
					externalSiteAdminForm.setCarrierName("");
					externalSiteAdminForm.setCarrierStreet("");
					externalSiteAdminForm.setCarrierCity("");
				} else {
					CarrierEntity carrier = (CarrierEntity) externalSiteAdminForm.getCarrierMap().get(
							externalSiteAdminForm.getCarrierId());
	
					//set vendor attributes
					SapVendorEntity vendor = carrier.getSapVendor();
					externalSiteAdminForm.setCarrierName(StringUtils.nvl(vendor.getName1()));
					externalSiteAdminForm.setCarrierStreet(StringUtils.nvl(vendor.getStreet()) + " "
							+ StringUtils.nvl(vendor.getHouseNumber()));
					externalSiteAdminForm.setCarrierCity(StringUtils.nvl(vendor.getCity()));
				}	
				
				setSiteMap(externalSiteAdminForm, externalSiteAdminService);
			}
			else if (inputAction.equals(FORWARD_SAVE)){
				
				Authorization.authorize(Authorization.MASTER_DATA_ROAD_EXT_SITE_W);
				
				LOG.debug("Action: save external site");
				
				Long carrierId = externalSiteAdminForm.getCarrierId();
				String siteName = externalSiteAdminForm.getSiteName();
				Double coordLat = externalSiteAdminForm.getCoordLat();
				Double coordLon = externalSiteAdminForm.getCoordLon();
				Long geofenceRadiusM = externalSiteAdminForm.getRadius();
				Long loadTimeMin = externalSiteAdminForm.getLoadTimeMin();
				
				if ( StringUtils.empty(siteName) ){
					MessageUtils.addActionError(request, "extsite.errors.required.name");
				}
				if ( coordLat == null || coordLat == 0.0 ||  coordLon == null || coordLon == 0.0 ){
					MessageUtils.addActionError(request, "extsite.errors.required.coordinates");
				}
				if ( geofenceRadiusM == null || geofenceRadiusM == 0L ){
					MessageUtils.addActionError(request, "extsite.errors.required.geofence");
				}
				if ( loadTimeMin == null || loadTimeMin == 0L ){
					MessageUtils.addActionError(request, "extsite.errors.required.siteload_unload.duration");
				}
	
				if (noErrorWarningPresent(request)) {
					ExtSiteEntity extSite = new ExtSiteEntity();
					Map carrierMap = externalSiteAdminForm.getCarrierMap();
					CarrierEntity carrier = (CarrierEntity) carrierMap.get(carrierId);
					
					if (externalSiteAdminForm.getSiteId() != null && externalSiteAdminForm.getSiteId() != 0L){
						extSite = (ExtSiteEntity) externalSiteAdminForm.getSiteMap().get(externalSiteAdminForm.getSiteId());
						LOG.debug("Update existing external site");
					}
					
					boolean activeFlag = externalSiteAdminForm.isActiveFlag();
					String siteLoadingPoint = externalSiteAdminForm.getSiteLoadingPoint();
					String siteContactPerson = externalSiteAdminForm.getSiteContactPerson();
					String siteStreeHouseNo = externalSiteAdminForm.getSiteStreeHouseNo();
					String siteCountry = externalSiteAdminForm.getSiteCountry();
					String sitePostalCode = externalSiteAdminForm.getSitePostalCode();
					String siteCity = externalSiteAdminForm.getSiteCity();
					String truckDriverNote = externalSiteAdminForm.getTruckDriverNote();
					
					extSite.setCarrier(carrier);
					extSite.setSiteName(siteName);
					extSite.setActiveFlag(activeFlag);
					extSite.setSiteLoadUnload(siteLoadingPoint);
					extSite.setSiteContactPersonTxt(siteContactPerson);
					extSite.setSiteStreetHouseNo(siteStreeHouseNo);
					extSite.setSiteCountry(siteCountry);
					extSite.setSitePostalCode(sitePostalCode);
					extSite.setSiteCity(siteCity);
					extSite.setCoordLat(coordLat);
					extSite.setCoordLon(coordLon);
					extSite.setGeofenceRadiusM(geofenceRadiusM);
					extSite.setLoadUnloadTimeMin(loadTimeMin);
					extSite.setCarrierTruckDriverNote(truckDriverNote);
					
					extSite = externalSiteAdminService.saveSite(extSite);
					if ((extSite != null) && (extSite.getExtSiteId() != null)) {
						MessageUtils.addActionMessage(request, "extsite.message.success.save");
						LOG.debug("The external site was saved successfully");
						
						externalSiteAdminForm.getSiteMap().put(extSite.getExtSiteId(), extSite);
						externalSiteAdminForm.setSiteId(extSite.getExtSiteId());
					}
					
				}
				
			}
			else if (inputAction.equals(FORWARD_SELECT_SITE)){
				setFormFields(externalSiteAdminForm);
			}
			else if(inputAction.equals(FORWARD_RESET)){
				if (externalSiteAdminForm.getSiteId() != null && externalSiteAdminForm.getSiteId() != 0L){
					setFormFields(externalSiteAdminForm);
				} else {
					externalSiteAdminForm.resetFields();
				}
			}
			else if(inputAction.equals(FORWARD_ADD_SITE)){
				externalSiteAdminForm.clearFields();
			}
			
			return result = mapping.findForward(forwardName);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private void setSiteMap(ExternalSiteAdminForm externalSiteAdminForm, ExternalSiteService externalSiteAdminService) {
		long carrierId = externalSiteAdminForm.getCarrierId();
		List siteList = externalSiteAdminService.fetchSiteForCarrier(carrierId);
		
		Map siteMap = new LinkedHashMap();
		siteMap = buildSiteMap(siteList);
		externalSiteAdminForm.setSiteMap(siteMap);
	}
	
	
	//Convert list to map.
	private Map buildSiteMap ( List siteList ) {
		Map materialMap = new LinkedHashMap();
		for (Iterator it = siteList.iterator(); it.hasNext();) {
			ExtSiteEntity site = (ExtSiteEntity) it.next();
			materialMap.put(site.getExtSiteId(), site);
			LOG.debug(" site name: " + site.getSiteName() );
		}
		return materialMap;
	}
	
	private void setFormFields( ExternalSiteAdminForm externalSiteAdminForm){
		// [[
		Object[][] params = {{"externalSiteAdminForm", externalSiteAdminForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setFormFields", params);
		}
		// ]]
		try {
			long siteId = externalSiteAdminForm.getSiteId();
			Map siteMap = externalSiteAdminForm.getSiteMap();
			ExtSiteEntity extSite = (ExtSiteEntity) siteMap.get(siteId);
			
			externalSiteAdminForm.setSiteName(extSite.getSiteName());
			externalSiteAdminForm.setActiveFlag(extSite.getActiveFlag());
			externalSiteAdminForm.setSiteLoadingPoint(extSite.getSiteLoadUnload());
			externalSiteAdminForm.setSiteStreeHouseNo(extSite.getSiteStreetHouseNo());
			externalSiteAdminForm.setSitePostalCode(extSite.getSitePostalCode());
			externalSiteAdminForm.setSiteCity(extSite.getSiteCity());
			externalSiteAdminForm.setSiteCountry(extSite.getSiteCountry());
			externalSiteAdminForm.setSiteContactPerson(extSite.getSiteContactPersonTxt());
			externalSiteAdminForm.setRadius(extSite.getGeofenceRadiusM());
			externalSiteAdminForm.setCoordLat(extSite.getCoordLat());
			externalSiteAdminForm.setCoordLon(extSite.getCoordLon());
			externalSiteAdminForm.setLoadTimeMin(extSite.getLoadUnloadTimeMin());
			externalSiteAdminForm.setTruckDriverNote(extSite.getCarrierTruckDriverNote());
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

}












