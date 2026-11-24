/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.masterdata;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.utils.time.DateTimeParser;
import com.crh.logon.web.utils.ConfigBeanProvider;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.beans.StorageLocationBean;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.RailwayStationEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.refdata.RfidobjectTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.ShipppointTypeBsEntity;
import com.crh.logon.utils.StringUtils;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_ShippingPointForm"
 */
public class ShippingPointForm extends ActionForm {

	private static final long serialVersionUID = -8910104051337644556L;
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ShippingPointForm.class);
    public static final String SESSION_SHPOINTFORM_NAME = "admin_ShippingPointForm";

    private RfidobjectTypeBsEntity rfidType;
    
	private String formAction;
	
	// shippingPoint part
	private ShippingpointEntity shippingPoint;
	private String sendSapSpShippingpointId;
	private String sendSapSpShippingpointName;
	private String sendSapSpShippingpointNo;
	private String varHandlingCostMat;
	private String futureVarHandlingCostMat;
	private String futureValidFromDat;
	// FIXME 16-Jul-2012/stich: remove weighSysTypeSPList entirely?
	private List weighSysTypeSPList;
	private List packageTypeSPList;
	
	private Long weighSystemSpCd;
	private boolean ldsServerRequired;

	//dropdowns
	private List packageTypeList;
	private List spTypeList;
	private List sapShippingCondList;
	private List delivNotePrintTypeList;
	private List materialList;
	private List printerList;
	private List weighSystemSpList;
	private List weighSystemLpList;
	private List ldsServerList;
	private Long railwayStationId;
	
	private Map currencyBsEntityMap;
	private String shippingPointCurrency;
	private Map uomBsEntityMap;
	private String shippingPointUom;

	private boolean isLoadingpointAddingAllowed = true;
	
	//tabs of loading points
	private List<LoadingPointBean> loadingPointWrapperList;
	private int currLoadingPointIndex;
	private Set unassignedRfidSet;
	private Set assignedRfidSet;

	//railway station
	private RailwayStationEntity operationStation;
	private RailwayStationEntity commStation;
	
	private String railArrivalWarningMin;
	
	private Map shippingPointTypeMap;
	
	private List storageLocationBeanList;

	private Locale userLocale;
	private List<TimeZone> timeZoneList;
	private TimeZone selectedTimeZone;


	/**
	 * Default constructor.
	 */
	public ShippingPointForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "ShippingPointForm");
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
	 * fields on the web form.
	 */
	private void init() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			packageTypeList = new ArrayList();
			spTypeList = new ArrayList();
			sapShippingCondList = new ArrayList();
			delivNotePrintTypeList = new ArrayList();
			materialList = new ArrayList();
			printerList = new ArrayList();
			weighSystemSpList = new ArrayList();
			weighSystemLpList = new ArrayList();
			ldsServerList = new ArrayList();
			
			currencyBsEntityMap = new HashMap();
			uomBsEntityMap = new HashMap();
			
			weighSysTypeSPList = new ArrayList();
			packageTypeSPList = new ArrayList();
			
			loadingPointWrapperList = new ArrayList<LoadingPointBean>();
			unassignedRfidSet = new HashSet();
			assignedRfidSet = new HashSet();
			isLoadingpointAddingAllowed = true;
			railArrivalWarningMin = null;
			shippingPointTypeMap = new HashMap();
			
			storageLocationBeanList = new ArrayList();
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
	 * Clears all the input fields on the web form, discards any existing
	 * search results, and resets any existing pages. Called in response to
	 * clicking the reset button on the web form.
	 */
	public void clear() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "clear");
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
	 * Override struts method.
	 * Note: this is not associated with or invoked by the reset button in the
	 * web form, but rather is called on every request before the struts action
	 * method gets control and, more importantly, before the request parameters
	 * are automatically copied into the form. The primary use is to reset
	 * checkboxes and struts multiboxes and the like for which the browser only
	 * sends the form parameters when they are set.
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// [[
		Object[][] params = {{"mapping", mapping},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "reset", params);
		}
		// ]]
		try {
			
			if (!loadingPointWrapperList.isEmpty()) {
			
				LoadingPointBean lpBean = loadingPointWrapperList.get(currLoadingPointIndex);
				lpBean.setLoadingTimeFlag(false);
			
				//reset fields only when another active tab is select
				if ((request.getParameter("addtab") == null)
						&& (request.getParameter("removetab") == null)
						&& lpBean.getLpEntity().getActiveFlag()) {
			
					lpBean.getLpEntity().setMatRestrictions(new ArrayList());
			
					//reset all drop down lists
					lpBean.getLpEntity().getPackageType().setPackageTypeCd(new Long(-1));
					lpBean.getLpEntity().getWeighSystemLp().setWeighSystemLpCd(Constant.WEIGH_SYSTEM_LP_LOGON);
					lpBean.getLpEntity().getDelivNotePrintType().setDelivNotePrintTypeCd(new Long(-1));
					if (lpBean.getLpEntity().getPrinter() != null) {
						lpBean.getLpEntity().getPrinter().setPrinterCd(new Long(-1));
					}
				}
			}
			
			//reset shipping point check boxes and drop downs
			if (shippingPoint != null) {
				shippingPoint.setCoordFixFlag(Boolean.FALSE);
				shippingPoint.setFvlMixShiptoPerWagonFlag(Boolean.FALSE);
				shippingPoint.setKnownAlsoInSapFlag(Boolean.FALSE);
			}
			
			for (int i = 0; i < storageLocationBeanList.size(); i++) {
				StorageLocationBean storageLocationBean = (StorageLocationBean)storageLocationBeanList.get(i);
				storageLocationBean.setStorageLocationIsDefault(Boolean.FALSE);
			}
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
	 * Perform form-specific validation of input parameters.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		// [[
		ActionErrors result = null;
		Object[][] params = {{"mapping", mapping},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validate", params);
		}
		// ]]
		try {
			
			ActionErrors errors = super.validate(mapping, request);
			if (errors == null) {
				errors = new ActionErrors();
			}
			if (loadingPointWrapperList != null && !loadingPointWrapperList.isEmpty()) {
				LoadingPointBean lpBean = loadingPointWrapperList.get(currLoadingPointIndex);
				if (lpBean.isLoadingTimeFlag() == true){
					lpBean.getLpEntity().setLoadingTime(null);
					lpBean.setLoadingTime(null);
				}
			}			
			
			
			if ("save".equals(formAction)) {
				
				//check if shippingpoint is any of the 'rail' types -> in this case railway station is mandatory
				if (Boolean.TRUE.equals( shippingPoint.getShipppointType().getIsRailFlag() )) {
			
					if ( (getOperationStation() == null) && ((request.getParameter("railwayStationId") == null)
							|| Constant.INVALID_OBJECT_ID_STR.equals(request.getParameter("railwayStationId")))) {
			
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
								"shippingpoint.errors.fieldEmpty.Bahnhof"));
					}
				}
			
				if ((shippingPoint.getCoordLat() != null)
						&& (shippingPoint.getCoordLat().toString().length() > ShippingpointEntity.DBSIZE_COORD_LAT)) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
							"shippingpoint.errors.fieldInputTooLong.CoordLat",
							new String[] { ShippingpointEntity.DBSIZE_COORD_LAT + "" }));
				}
			
				if ((shippingPoint.getCoordLon() != null)
						&& (shippingPoint.getCoordLon().toString().length() > ShippingpointEntity.DBSIZE_COORD_LON)) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
							"shippingpoint.errors.fieldInputTooLong.CoordLon",
							new String[] { ShippingpointEntity.DBSIZE_COORD_LON + "" }));
				}
			
				if ((shippingPoint.getGeofenceRadius() != null)
						&& (shippingPoint.getGeofenceRadius() == 0)) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
							"shippingpoint.errors.fieldWrongFormat.Radius"));
				}
			
				if (StringUtils.nvl(shippingPoint.getShippingpointComment()).length() > ShippingpointEntity.DBSIZE_SHIPPINGPOINT_COMMENT) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
							"shippingpoint.errors.fieldInputTooLong.Comment",
							new String[] { ShippingpointEntity.DBSIZE_SHIPPINGPOINT_COMMENT + "" }));
				}
	
				// ADN51108
				if ( getShippingPoint().getShipppointType().getShipppointTypeCd() == Constant.SHIPPINGPOINT_TYPE_FVL_BIMODAL_TRUCK ) {
					if ( railArrivalWarningMin == null ) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("shippingpoint.errors.fieldEmpty.railArrivalWarningMin"));
					}
					else {
						try {
							Long value = Long.parseLong(railArrivalWarningMin);
							if ( value.longValue() < 1 ) {
								errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("shippingpoint.errors.fieldEmpty.railArrivalWarningMin"));
							}
						}
						catch ( NumberFormatException e) {
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
							"shippingpoint.errors.fieldWrongFormat", Message.lookup(request.getLocale(), "shippingpoint.field.railArrivalWarningMin")));
						}
					}
				}
				
				// validate loading points
				for (int i = 0; i < loadingPointWrapperList.size(); i++) {
					LoadingPointBean lpb = loadingPointWrapperList.get(i);
					
					if (!lpb.getLpEntity().getActiveFlag()) {
						// skip deleted loading points
						continue;
					}
					
					if (StringUtils.nvl(lpb.getLpEntity().getLoadingpointName()).length() > LoadingpointEntity.DBSIZE_LOADINGPOINT_NAME) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
								"shippingpoint.errors.fieldInputTooLong.lpName",
								new String[] { lpb.getLpEntity().getLoadingpointName(), LoadingpointEntity.DBSIZE_LOADINGPOINT_NAME + ""}));
					}
			
					if (StringUtils.notEmpty(lpb.getLpEntity().getLoadingpointObjectLabel())) {
						
						if (StringUtils.nvl(lpb.getLpEntity().getLoadingpointObjectLabel()).length() > LoadingpointEntity.DBSIZE_LOADINGPOINT_OBJECT_LABEL) {
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
									"shippingpoint.errors.fieldInputTooLong.object.label",
									new String[] { lpb.getLpEntity().getLoadingpointName(), LoadingpointEntity.DBSIZE_LOADINGPOINT_OBJECT_LABEL + ""}));
						}
					}
					else {
						if (StringUtils.empty(lpb.getLpEntity().getLoadingpointName())) {
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
							"shippingpoint.errors.fieldEmpty.object.label",
							new String[] { lpb.getLpEntity().getLoadingpointName()}));
						}
						else {
							//set loading point name
							lpb.getLpEntity().setLoadingpointObjectLabel(lpb.getLpEntity().getLoadingpointName());
						}
					}
					
					
					
					
					if (StringUtils.nvl(lpb.getLpEntity().getLoadingpointComment()).length() > LoadingpointEntity.DBSIZE_LOADINGPOINT_COMMENT) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
								"shippingpoint.errors.fieldInputTooLong.lpComment",
								new String[] { lpb.getLpEntity().getLoadingpointName(), LoadingpointEntity.DBSIZE_LOADINGPOINT_COMMENT + "" }));
					}
			
					if (StringUtils.nvl(lpb.getLpEntity().getWeighSystemLoadingpointNo()).length() > LoadingpointEntity.DBSIZE_WEIGH_SYSTEM_LOADINGPOINT_NO) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
								"shippingpoint.errors.fieldInputTooLong.weighSystemLpNo",
								new String[] { lpb.getLpEntity().getLoadingpointName(), LoadingpointEntity.DBSIZE_WEIGH_SYSTEM_LOADINGPOINT_NO + "" }));
					}
					if (StringUtils.notEmpty(lpb.getGrossWeightLimitKg())) {
						try {
							double grossTons = Double.parseDouble(lpb.getGrossWeightLimitKg().trim());
							grossTons = grossTons * 1000;
							lpb.getLpEntity().setGrossWeightLimitKg((long) grossTons);
						}
						catch (NumberFormatException e) {
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
									"shippingpoint.errors.fieldWrongFormat.lpGrossWeightLimitKg",
									new String[] { lpb.getLpEntity().getLoadingpointName() }));
						}
					}
					else {
						lpb.getLpEntity().setGrossWeightLimitKg(null);
					}
					
					if (StringUtils.notEmpty(lpb.getLeaveLpDelayMin())) {
						try {
							double leaveLpDelayMin = Double.parseDouble(lpb.getLeaveLpDelayMin().trim());							
							lpb.getLpEntity().setLeaveLpDelayMin((long) leaveLpDelayMin);
						}
						catch (NumberFormatException e) {
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
									"shippingpoint.errors.fieldWrongFormat.leaveLpDelayMin",
									new String[] { lpb.getLpEntity().getLoadingpointName() }));
						}
					}
					else {
						lpb.getLpEntity().setLeaveLpDelayMin(null);
					}
			
					if (lpb.isLoadingTimeFlag() == true){
						lpb.getLpEntity().setLoadingTime(null);
						lpb.setLoadingTime(null);					
					}
					else{						
						if (StringUtils.notEmpty(lpb.getLoadingTime())) {
							
							try {
								Long lTime = Long.valueOf(lpb.getLoadingTime());
								lpb.getLpEntity().setLoadingTime(lTime);
							}
							catch (NumberFormatException e) {
								errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
										"shippingpoint.errors.fieldWrongFormat.lpLoadingTime",
										new String[] { lpb.getLpEntity().getLoadingpointName() }));
							}
						}
						else {
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
									"shippingpoint.errors.fieldWrongFormat.lpLoadingTime",
									new String[] { lpb.getLpEntity().getLoadingpointName() }));
						}
					}
					
								
			
					if (lpb.getPackageTypeCd().longValue() == Constant.INVALID_OBJECT_ID) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
								"shippingpoint.errors.fieldEmpty.packageType",
								new String[] { lpb.getLpEntity().getLoadingpointName() }));
					}
			
					if (lpb.getDelivNotePrintTypeCd().longValue() == Constant.INVALID_OBJECT_ID) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
								"shippingpoint.errors.fieldEmpty.delivNotePrintType",
								new String[] { lpb.getLpEntity().getLoadingpointName() }));
					}
					else if (lpb.getDelivNotePrintTypeCd().longValue() == Constant.DELIV_NOTE_PRINT_TYPE_LOGON){
						if ((lpb.getPrinterCd() == null) || (lpb.getPrinterCd() == Constant.INVALID_OBJECT_ID)) {
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
									"shippingpoint.errors.fieldEmpty.delivNotePrinter",
									new String[] { lpb.getLpEntity().getLoadingpointName() }));
						}
					}
					
					if (lpb.getDelivNotePrintTypeCd().longValue() == Constant.DELIV_NOTE_PRINT_TYPE_LOGON) {
						String delivNoteCopiesLogon = lpb.getDelivNoteCopiesLogon();
						if (StringUtils.empty(delivNoteCopiesLogon)) {
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
									"shippingpoint.errors.fieldEmpty.delivNoteCopies",
									new String[] { lpb.getLpEntity().getLoadingpointName() }));
						}
						else {
							try {
								Long.valueOf(delivNoteCopiesLogon);
							}
							catch (NumberFormatException e) {
								errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
										"shippingpoint.errors.fieldWrongFormat.lpDelivNoteCopies",
										new String[] { lpb.getLpEntity().getLoadingpointName() }));
							}
						}
					}
				}
				
				if (Constant.INVALID_OBJECT_ID_STR.equals(shippingPoint.getLdsServer())) {
					if (ldsServerRequired) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
								"shippingpoint.errors.fieldEmpty.ldsServer",
								new String[] { ShippingpointEntity.DBSIZE_LDS_SERVER + "" }));
					}
					else {
						shippingPoint.setLdsServer(null);
					}
				}

				String varHandlingCostMat = request.getParameter("varHandlingCostMat");
				if (StringUtils.notEmpty(varHandlingCostMat)) {
					try {
						new BigDecimal(varHandlingCostMat.replace(',', '.'));
					}
					catch (NumberFormatException e) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("shippingpoint.errors.fieldWrongFormat.varHandlingCostMat"));
					}
				}
				
				String futureVarHandlingCostMat = request.getParameter("futureVarHandlingCostMat");
				if (StringUtils.notEmpty(futureVarHandlingCostMat)) {
					try {
						new BigDecimal(futureVarHandlingCostMat.replace(',', '.'));
					}
					catch (NumberFormatException e) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("shippingpoint.errors.fieldWrongFormat.futureVarHandlingCostMat"));
					}
				}

				String futureValidFromDatStr = request.getParameter("futureValidFromDat");
				if (StringUtils.notEmpty(futureValidFromDatStr)) {
					try {
                        DateTimeParser.parseLogonDayMonthYear(request.getLocale(), futureValidFromDatStr);
					}
					catch (ParseException e) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("shippingpoint.errors.fieldWrongFormat.futureValidFromDat"));
					}
				}
				
				if ((StringUtils.notEmpty(varHandlingCostMat) || StringUtils.notEmpty(futureVarHandlingCostMat)) && Constant.INVALID_OBJECT_ID_STR.equals(request.getParameter("shippingPointCurrency"))) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("shippingpoint.errors.fieldEmpty.currency"));
				}

				if ((StringUtils.notEmpty(varHandlingCostMat) || StringUtils.notEmpty(futureVarHandlingCostMat) || !Constant.INVALID_OBJECT_ID_STR.equals(request.getParameter("shippingPointCurrency"))) && Constant.INVALID_OBJECT_ID_STR.equals(request.getParameter("shippingPointUom"))) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("shippingpoint.errors.fieldEmpty.uom"));
				}
			}
			
			//reset form action
			if (!errors.isEmpty()) {
				formAction = "";
			}
			return result = errors;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public List getPrinterList() {
		return printerList;
	}

	public void setPrinterList(List printerList) {
		this.printerList = printerList;
	}
	
	public String getFormAction() {
		return formAction;
	}

	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}

	public ShippingpointEntity getShippingPoint() {
		return shippingPoint;
	}

	public void setShippingPoint(ShippingpointEntity shippingPoint) {
		this.shippingPoint = shippingPoint;
		if (shippingPoint != null && LogonUtil.isLoadingpointPreventedForShippingpointType(shippingPoint.getShipppointType().getShipppointTypeCd())){
			isLoadingpointAddingAllowed = false;
		}
		else{
			isLoadingpointAddingAllowed = true;
		}

		if (shippingPoint != null && shippingPoint.getTimezone() != null) {
			setSelectedTimeZoneCd(shippingPoint.getTimezone().getTimezoneCd());
		}
		else {
			setSelectedTimeZoneCd(ConfigBeanProvider.getConfigBean().getAppDefaultTimezone());
		}
	}

	public String getSendSapSpShippingpointId() {
		return sendSapSpShippingpointId;
	}

	public void setSendSapSpShippingpointId(String sendSapSpShippingpointId) {
		this.sendSapSpShippingpointId = sendSapSpShippingpointId;
	}

	public String getSendSapSpShippingpointName() {
		return sendSapSpShippingpointName;
	}

	public void setSendSapSpShippingpointName(String sendSapSpShippingpointName) {
		this.sendSapSpShippingpointName = sendSapSpShippingpointName;
	}

	public String getSendSapSpShippingpointNo() {
		return sendSapSpShippingpointNo;
	}

	public void setSendSapSpShippingpointNo(String sendSapSpShippingpointNo) {
		this.sendSapSpShippingpointNo = sendSapSpShippingpointNo;
	}

	public String getVarHandlingCostMat() {
		return varHandlingCostMat;
	}

	public void setVarHandlingCostMat(String varHandlingCostMat) {
		this.varHandlingCostMat = varHandlingCostMat;
	}

	public String getFutureVarHandlingCostMat() {
		return futureVarHandlingCostMat;
	}

	public void setFutureVarHandlingCostMat(String futureVarHandlingCostMat) {
		this.futureVarHandlingCostMat = futureVarHandlingCostMat;
	}

	public String getFutureValidFromDat() {
		return futureValidFromDat;
	}

	public void setFutureValidFromDat(String futureValidFromDat) {
		this.futureValidFromDat = futureValidFromDat;
	}

	public List getPackageTypeList() {
		return packageTypeList;
	}

	public void setPackageTypeList(List packageTypeList) {
		this.packageTypeList = packageTypeList;
	}

	public List getSpTypeList() {
		return spTypeList;
	}

	public void setSpTypeList(List spTypeList) {
		this.spTypeList = spTypeList;
	}

	public List getSapShippingCondList() {
		return sapShippingCondList;
	}

	public void setSapShippingCondList(List sapShippingCondList) {
		this.sapShippingCondList = sapShippingCondList;
	}

	public int getCurrLoadingPointIndex() {
		return currLoadingPointIndex;
	}

	public void setCurrLoadingPointIndex(int currLoadingPointIndex) {
		this.currLoadingPointIndex = currLoadingPointIndex;
	}
	
	public List getWeighSysTypeSPList() {
		return weighSysTypeSPList;
	}

	public void setWeighSysTypeSPList(List weighSysTypeSPList) {
		this.weighSysTypeSPList = weighSysTypeSPList;
	}

	public List getPackageTypeSPList() {
		return packageTypeSPList;
	}

	public void setPackageTypeSPList(List packageTypeSPList) {
		this.packageTypeSPList = packageTypeSPList;
	}

	public Long getWeighSystemSpCd() {
		return weighSystemSpCd;
	}

	public void setWeighSystemSpCd(Long weighSystemSpCd) {
		this.weighSystemSpCd = weighSystemSpCd;
	}

	public boolean isLdsServerRequired() {
		return ldsServerRequired;
	}

	public void setLdsServerRequired(boolean ldsServerRequired) {
		this.ldsServerRequired = ldsServerRequired;
	}

	public List getDelivNotePrintTypeList() {
		return delivNotePrintTypeList;
	}

	public void setDelivNotePrintTypeList(List delivNotePrintTypeList) {
		this.delivNotePrintTypeList = delivNotePrintTypeList;
	}

	public List getMaterialList() {
		return materialList;
	}

	public void setMaterialList(List materialList) {
		this.materialList = materialList;
	}

	public List<LoadingPointBean> getLoadingPointWrapperList() {
		return loadingPointWrapperList;
	}

	public void setLoadingPointWrapperList(List<LoadingPointBean> loadingPointWrapperList) {
		this.loadingPointWrapperList = loadingPointWrapperList;
	}

	public Long getRailwayStationId() {
		return railwayStationId;
	}

	public void setRailwayStationId(Long railwayStationId) {
		this.railwayStationId = railwayStationId;
	}

	public List getWeighSystemSpList() {
		return weighSystemSpList;
	}

	public void setWeighSystemSpList(List weighSystemSpList) {
		this.weighSystemSpList = weighSystemSpList;
	}

	public List getWeighSystemLpList() {
		return weighSystemLpList;
	}

	public void setWeighSystemLpList(List weighSystemLpList) {
		this.weighSystemLpList = weighSystemLpList;
	}

	public List getLdsServerList() {
		return ldsServerList;
	}

	public void setLdsServerList(List ldsServerList) {
		this.ldsServerList = ldsServerList;
	}

	public Map getCurrencyBsEntityMap() {
		return currencyBsEntityMap;
	}

	public void setCurrencyBsEntityMap(Map currencyBsEntityMap) {
		this.currencyBsEntityMap = currencyBsEntityMap;
	}

	public String getShippingPointCurrency() {
		return shippingPointCurrency;
	}

	public void setShippingPointCurrency(String shippingPointCurrency) {
		this.shippingPointCurrency = shippingPointCurrency;
	}

	public Map getUomBsEntityMap() {
		return uomBsEntityMap;
	}

	public void setUomBsEntityMap(Map uomBsEntityMap) {
		this.uomBsEntityMap = uomBsEntityMap;
	}

	public String getShippingPointUom() {
		return shippingPointUom;
	}

	public void setShippingPointUom(String shippingPointUom) {
		this.shippingPointUom = shippingPointUom;
	}

	public Set getUnassignedRfidSet() {
		return unassignedRfidSet;
	}

	public void setUnassignedRfidSet(Set unassignedRfidSet) {
		this.unassignedRfidSet = unassignedRfidSet;
	}

	public Set getAssignedRfidSet() {
		return assignedRfidSet;
	}

	public void setAssignedRfidSet(Set assignedRfidSet) {
		this.assignedRfidSet = assignedRfidSet;
	}
	
	/**
	 * checks if shipping point list has active shipping points  
	 * @return true if there is at least one active shipping point
	 */
	public boolean getEmptyShippingPoint() {
		// [[
		boolean result = false;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getEmptyShippingPoint");
		}
		// ]]
		try {
			boolean emptyShippingPoint = false;
			
			for (LoadingPointBean lpb: loadingPointWrapperList) {
				if (lpb.getLpEntity().getActiveFlag()) {
					emptyShippingPoint = true;
					return result = emptyShippingPoint;
				}
			}
			return result = emptyShippingPoint;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public Boolean getLdPointMaterial() {
		return true;
	}
	
	public List getStorageLocationBeanList() {
		return storageLocationBeanList;
	}

	public void setStorageLocationBeanList(List storageLocationBeanList) {
		this.storageLocationBeanList = storageLocationBeanList;
	}

	public RfidobjectTypeBsEntity getRfidType() {
		return rfidType;
	}

	public void setRfidType(RfidobjectTypeBsEntity rfidType) {
		this.rfidType = rfidType;
	}

	public RailwayStationEntity getOperationStation() {
		return operationStation;
	}

	public void setOperationStation(RailwayStationEntity operationStation) {
		this.operationStation = operationStation;
	}

	public RailwayStationEntity getCommStation() {
		return commStation;
	}

	public void setCommStation(RailwayStationEntity commStation) {
		this.commStation = commStation;
	}

	public boolean isLoadingpointAddingAllowed() {
		return isLoadingpointAddingAllowed;
	}

	public void setLoadingpointAddingAllowed(boolean isLoadingpointAddingAllowed) {
		this.isLoadingpointAddingAllowed = isLoadingpointAddingAllowed;
	}

	public String getRailArrivalWarningMin() {
		return railArrivalWarningMin;
	}

	public void setRailArrivalWarningMin(String railArrivalWarningMin) {
		this.railArrivalWarningMin = railArrivalWarningMin;
	}
	
	public boolean isFVLShippinpoint() {
		if ( getShippingPoint().getShipppointType().getShipppointTypeCd() == Constant.SHIPPINGPOINT_TYPE_FVL_BIMODAL_TRUCK ) {
			return true;
		}
		return false;
	}

	public Long getShippingPointType() {
		return getShippingPoint().getShipppointType().getShipppointTypeCd();
	}

	public void setShippingPointType(Long shippingPointType) {
		getShippingPoint().setShipppointType((ShipppointTypeBsEntity) shippingPointTypeMap.get(shippingPointType));
	}

	public Map getShippingPointTypeMap() {
		return shippingPointTypeMap;
	}

	public void setShippingPointTypeMap(Map shippingPointTypeMap) {
		this.shippingPointTypeMap = shippingPointTypeMap;
	}

	public void setTimeZoneList(List<TimeZone> timeZones) {
		this.timeZoneList = timeZones;
	}

	public void setUserLocale(Locale userLocale) {
		this.userLocale = userLocale;
	}

	public List<TimeZone> getTimeZoneList() {
		return this.timeZoneList;
	}

	public String getSelectedTimeZoneCd() {
		return this.selectedTimeZone.getTimezoneCd();
	}

	public void setSelectedTimeZoneCd(String selectedTimeZoneCd) {
		this.selectedTimeZone = new TimeZone(selectedTimeZoneCd, userLocale);
	}

	public TimeZone getTimeZone() {
		return selectedTimeZone;
	}

}
