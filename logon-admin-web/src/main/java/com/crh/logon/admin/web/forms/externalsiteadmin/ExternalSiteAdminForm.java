/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.externalsiteadmin;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * 
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_ExternalSiteAdminForm"
 */
public class ExternalSiteAdminForm extends ActionForm {



	public static final String formName = "admin_ExternalSiteAdminForm";
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ExternalSiteAdminForm.class);

	private String inputAction;
	private Map carrierMap;
	private Long carrierId;
	private String carrierName;
	private String carrierStreet;
	private String carrierCity;
	private Map siteMap;
	private Long siteId;
	private boolean activeFlag;
	private String siteName;
	private String siteLoadingPoint;
	private String siteContactPerson;
	private String siteStreeHouseNo;
	private String siteCountry;
	private String sitePostalCode;
	private String siteCity;
	private Double coordLat;
	private Double coordLon;
	private Long radius;
	private Long loadTimeMin;
	private String truckDriverNote;
	

	
	
	

	public String getInputAction() {
		return inputAction;
	}

	public void setInputAction(String inputAction) {
		this.inputAction = inputAction;
	}

	public Map getCarrierMap() {
		return carrierMap;
	}

	public void setCarrierMap(Map carrierMap) {
		this.carrierMap = carrierMap;
	}

	public Long getCarrierId() {
		return carrierId;
	}

	public void setCarrierId(Long carrierId) {
		this.carrierId = carrierId;
	}

	public String getCarrierName() {
		return carrierName;
	}

	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}
	
		public String getCarrierStreet() {
		return carrierStreet;
	}

	public void setCarrierStreet(String carrierStreet) {
		this.carrierStreet = carrierStreet;
	}

	public String getCarrierCity() {
		return carrierCity;
	}

	public void setCarrierCity(String carrierCity) {
		this.carrierCity = carrierCity;
	}

	public Map getSiteMap() {
		return siteMap;
	}

	public void setSiteMap(Map siteMap) {
		this.siteMap = siteMap;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public boolean isActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(boolean activeFlag) {
		this.activeFlag = activeFlag;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getSiteLoadingPoint() {
		return siteLoadingPoint;
	}

	public void setSiteLoadingPoint(String siteLoadingPoint) {
		this.siteLoadingPoint = siteLoadingPoint;
	}

	public String getSiteContactPerson() {
		return siteContactPerson;
	}

	public void setSiteContactPerson(String siteContactPerson) {
		this.siteContactPerson = siteContactPerson;
	}

	public String getSiteStreeHouseNo() {
		return siteStreeHouseNo;
	}

	public void setSiteStreeHouseNo(String siteStreeHouseNo) {
		this.siteStreeHouseNo = siteStreeHouseNo;
	}

	public String getSiteCountry() {
		return siteCountry;
	}

	public void setSiteCountry(String siteCountry) {
		this.siteCountry = siteCountry;
	}

	public String getSitePostalCode() {
		return sitePostalCode;
	}

	public void setSitePostalCode(String sitePostalCode) {
		this.sitePostalCode = sitePostalCode;
	}

	public String getSiteCity() {
		return siteCity;
	}

	public void setSiteCity(String siteCity) {
		this.siteCity = siteCity;
	}

	public Double getCoordLat() {
		return coordLat;
	}

	public void setCoordLat(Double coordLat) {
		this.coordLat = coordLat;
	}

	public Double getCoordLon() {
		return coordLon;
	}

	public void setCoordLon(Double coordLon) {
		this.coordLon = coordLon;
	}

	public Long getRadius() {
		return radius;
	}

	public void setRadius(Long radius) {
		this.radius = radius;
	}

	public Long getLoadTimeMin() {
		return loadTimeMin;
	}

	public void setLoadTimeMin(Long loadTimeMin) {
		this.loadTimeMin = loadTimeMin;
	}

	public String getTruckDriverNote() {
		return truckDriverNote;
	}

	public void setTruckDriverNote(String truckDriverNote) {
		this.truckDriverNote = truckDriverNote;
	}

	/**
	 * Default constructor.
	 */
	public ExternalSiteAdminForm() {
		
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "ExternalSiteAdminForm");
		}
		// ]]
		try {
			init();
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
	 * Initializes the form. This has the effect of clearing all the input
	 * fields on the ordermgr.web form.
	 */
	private void init() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			
			activeFlag = true;
			inputAction = null;
			carrierMap = null;
			carrierId = null;
			carrierName = null;
			siteMap = null;
			siteId = null;
			siteName = null;
			siteLoadingPoint = null;
			siteContactPerson = null;
			siteStreeHouseNo = null;
			siteCountry = null;
			sitePostalCode = null;
			siteCity = null;
			coordLat = null;
			coordLon = null;
			radius = null;
			loadTimeMin = null;
			truckDriverNote = null;
			
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
     * Override struts method.
     * Note: is called on every request before the struts action
     * method gets control and, more importantly, before the request parameters
     * are automatically copied into the form. The primary use is to reset
     * checkboxes and struts multiboxes and the like for which the browser only
     * sends the form parameters when they are set.
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
		activeFlag = false;
	}

	public void resetFields() {
		activeFlag = true;
		siteName = null;
		siteLoadingPoint = null;
		siteContactPerson = null;
		siteStreeHouseNo = null;
		siteCountry = null;
		sitePostalCode = null;
		siteCity = null;
		coordLat = null;
		coordLon = null;
		radius = null;
		loadTimeMin = null;
		truckDriverNote = null;
	}
	
	public void clearFields() {
		activeFlag = true;
		siteId = null;
		siteName = null;
		siteLoadingPoint = null;
		siteContactPerson = null;
		siteStreeHouseNo = null;
		siteCountry = null;
		sitePostalCode = null;
		siteCity = null;
		coordLat = null;
		coordLon = null;
		radius = null;
		loadTimeMin = null;
		truckDriverNote = null;
	}

	
	public void clear() {
		init();
	}



}
