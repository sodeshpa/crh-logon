/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.masterdata;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.crh.logon.admin.web.actions.masterdata.WagonDetailAction;
import com.crh.logon.admin.web.beans.WagonDetailBuildBean;
import com.crh.logon.admin.web.beans.WagonDetailMainBean;
import com.crh.logon.admin.web.beans.WagonDetailTypeBean;
import com.crh.logon.admin.web.beans.WagonDetailWheelsetBean;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.admin.web.beans.WagonDetailControlBean;
import com.crh.logon.admin.web.beans.WagonDetailCurrentOrderBean;
import com.crh.logon.admin.web.beans.WagonDetailLoadBean;
import com.crh.logon.admin.web.beans.WagonDetailMaintenanceBean;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.WagonNumberUtils;
import com.crh.logon.web.forms.AbstractActionForm;
import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.utils.ServiceLocatorProvider;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_WagonDetailForm"
 */
@AdnTrace
public class WagonDetailForm  extends AbstractActionForm {

	private static final long serialVersionUID = 1L;
	
	private static final Set monthSet = new  HashSet();
	
	//map containing the DISTINCT wagon ids + numbers coming from the wagon search list
	private Map<Long,String> wagonIdMap;
	
	static {
		monthSet.add(new Integer(0));
		monthSet.add(new Integer(1));
		monthSet.add(new Integer(2));
		monthSet.add(new Integer(3));
		monthSet.add(new Integer(4));
		monthSet.add(new Integer(5));
		monthSet.add(new Integer(6));
		monthSet.add(new Integer(7));
		monthSet.add(new Integer(8));
		monthSet.add(new Integer(9));
		monthSet.add(new Integer(10));
		monthSet.add(new Integer(11));
		monthSet.add(new Integer(12));
	}
	
	public static final String formName = "admin_WagonDetailForm";
	private String formAction;
	private String selectedTab;
	private String activeTab;
	private String requestedInfo;
	
	private Long wagonId;
	private WagonDetailMainBean wagonDetailMain;
	private WagonDetailLoadBean wagonDetailLoad;
	private WagonDetailControlBean wagonDetailControl;
	private WagonDetailTypeBean wagonDetailType;
	private WagonDetailBuildBean wagonDetailBuild;
	private WagonDetailWheelsetBean wagonDetailWheelset;
	private WagonDetailMaintenanceBean wagonDetailMaintenance;
	private WagonDetailCurrentOrderBean orderBean;
	
	private String inputWagonNo = "";
	
	private RfidobjectEntity rfidObject;
	
	// used to determine which tab to return after activating the reset button
	private String resetToTab;
	
	// used to print an error message if a file upload had been failed
	private Boolean lastUploadFailed = false;
	private String lastUnsupportedFileExtension = "";

	
	public WagonDetailForm() {
		init();
	}
	
	public void init() {
		clear();
	}
	
	public void clear() {
		formAction = null;
		activeTab = WagonDetailAction.FORWARD_LOAD_TAB;
		selectedTab = WagonDetailAction.FORWARD_LOAD_TAB;
		requestedInfo = null;
		wagonId = null;
		wagonDetailMain = null;
		wagonDetailLoad = null;
		wagonDetailControl = null;
		wagonDetailType = null;
		wagonDetailBuild = null;
		wagonDetailWheelset = null;
		wagonDetailMaintenance = null;
		orderBean = null;
		rfidObject = null;
		inputWagonNo = null;
		formAction = null;
	}
	
	private void resetBooleans(Object bean) {
		if (bean == null) {
			return;
		}
		try {
			PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(bean);
			for (PropertyDescriptor propertyDescriptor: propertyDescriptors) {
				if ( ((propertyDescriptor.getPropertyType().equals(Boolean.class)) || (propertyDescriptor.getPropertyType().equals(boolean.class))) && (propertyDescriptor.getWriteMethod() != null) ) {
					BeanUtils.setProperty(bean, propertyDescriptor.getName(), Boolean.FALSE);
				}
			}		
		}
		catch (Exception e) {
			throw new LogonException(e);
		}
	}
	
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		String formActionParameter = request.getParameter("formAction");
		if (formActionParameter!= null && (formActionParameter.equals(WagonDetailAction.FORM_ACTION_COPY) || formActionParameter.equals(WagonDetailAction.FORM_ACTION_SAVE_NEW))) {
			return;
		}
		if (!WagonDetailAction.FORM_ACTION_LOAD_TAB.equals(getFormAction())) {
			return;
		}
		if (WagonDetailAction.FORWARD_BUILD_TAB.equals(selectedTab)) {
			resetBooleans(wagonDetailBuild);
		}
		else if (WagonDetailAction.FORWARD_CONTROL_TAB.equals(selectedTab)) {
			resetBooleans(wagonDetailControl);
		}
		else if (WagonDetailAction.FORWARD_LOAD_TAB.equals(selectedTab)) {
			resetBooleans(wagonDetailLoad);
		}
		else if (WagonDetailAction.FORWARD_MAINTENANCE_TAB.equals(selectedTab)) {
			resetBooleans(wagonDetailMaintenance);
		}
		else if (WagonDetailAction.FORWARD_TYPE_TAB.equals(selectedTab)) {
			resetBooleans(wagonDetailType);
		}
		else if (WagonDetailAction.FORWARD_WHEELSET_TAB.equals(selectedTab)) {
			resetBooleans(wagonDetailWheelset);
		}
		resetBooleans(wagonDetailMain);
		resetBooleans(this);
	}
	
	public ActionErrors validateTab(String tab, ActionMapping mapping, HttpServletRequest request) {
		if (WagonDetailAction.FORWARD_LOAD_TAB.equals(tab)) {
			return validateBean(mapping, request, wagonDetailLoad);
		}
		else if (WagonDetailAction.FORWARD_CONTROL_TAB.equals(tab)) {
			return validateBean(mapping, request, wagonDetailControl);
		}
		else if (WagonDetailAction.FORWARD_TYPE_TAB.equals(tab)) {
			return validateBean(mapping, request, wagonDetailType);
		}
		else if (WagonDetailAction.FORWARD_BUILD_TAB.equals(tab)) {
			return validateBean(mapping, request, wagonDetailBuild);
		}
		else if (WagonDetailAction.FORWARD_WHEELSET_TAB.equals(tab)) {
			return validateBean(mapping, request, wagonDetailWheelset);
		}
		return null;
	}
	
	public ActionMessages validateAll(ActionMapping mapping, HttpServletRequest request) {
		validateTab(WagonDetailAction.FORWARD_LOAD_TAB, mapping, request);
		validateTab(WagonDetailAction.FORWARD_MAP_TAB, mapping, request);
		validateTab(WagonDetailAction.FORWARD_CONTROL_TAB, mapping, request);
		validateTab(WagonDetailAction.FORWARD_TYPE_TAB, mapping, request);
		validateTab(WagonDetailAction.FORWARD_BUILD_TAB, mapping, request);
		validateTab(WagonDetailAction.FORWARD_WHEELSET_TAB, mapping, request);
		validateTab(WagonDetailAction.FORWARD_MAINTENANCE_TAB, mapping, request);

		Date arrivalLastKnownStation = getWagonDetailMain().getArrivalLastKnownStationDat();
		if (arrivalLastKnownStation != null && !arrivalLastKnownStation.before(new Date())) {
        	MessageUtils.addActionMessage(request,"wagondetail.error.lastknowndate");
        }
		validateWagonNoUnicity(request);
		validateBean(mapping, request, wagonDetailMain);
		
		//check if month has correct format
		try {
			String wheelYear1 = checkMonth(request, wagonDetailWheelset.getWheelset1NbsYear());
			wagonDetailWheelset.setWheelset1NbsYear(wheelYear1);
			String wheelYear2 = checkMonth(request, wagonDetailWheelset.getWheelset2NbsYear());
			wagonDetailWheelset.setWheelset2NbsYear(wheelYear2);
			String wheelYear3 = checkMonth(request, wagonDetailWheelset.getWheelset3BsYear());
			wagonDetailWheelset.setWheelset3BsYear(wheelYear3);
			String wheelYear4 = checkMonth(request, wagonDetailWheelset.getWheelset4BsYear());
			wagonDetailWheelset.setWheelset4BsYear(wheelYear4);
		} 
		catch (Exception e) {
			MessageUtils.addActionWarning(request, "wagon.error.format.wheelset.month");
		}
		ActionMessages messages = new ActionMessages();
		if (MessageUtils.getActionErrors(request)!= null && !MessageUtils.getActionErrors(request).isEmpty()){			
			messages.add(MessageUtils.getActionErrors(request));
		}
		if (MessageUtils.getActionWarnings(request)!= null && !MessageUtils.getActionWarnings(request).isEmpty()){			
			messages.add(MessageUtils.getActionWarnings(request));
		}
		return messages;
	}

	private String checkMonth(HttpServletRequest request, String yearMonthStr) {
		if (StringUtils.notEmpty(yearMonthStr)){
			BigDecimal yearMonth = new BigDecimal(yearMonthStr);
			BigDecimal truncated = yearMonth.subtract(new BigDecimal("" + yearMonth.intValue()));
			int decimal = truncated.multiply(new BigDecimal("100")).intValue();
			
			if (!monthSet.contains(decimal)) {
				MessageUtils.addActionWarning(request, "wagon.error.format.wheelset.month");
			}
			else {
				
				if (decimal == 0) {
					return yearMonth.intValue() + "";
				}
				else {
					BigDecimal numberDec = new BigDecimal("" + yearMonth.intValue());
					BigDecimal decimalDec = new BigDecimal("" + decimal);
					BigDecimal div = numberDec.add(decimalDec.divide(new BigDecimal("100"), 2, BigDecimal.ROUND_UNNECESSARY));
					return div.toString();
				}
			}
		}
		return yearMonthStr;
	}

	
	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		return super.validate(mapping, request);
	}
	
	public void validateWagonNoUnicity(HttpServletRequest request) {
		if (getInputWagonNo() != null || wagonDetailControl.getWagonNo() != null) {
			//check if it can be converted to a normal wagon no
			String wagonNo = getInputWagonNo() != null ? getInputWagonNo() : wagonDetailControl.getWagonNo();
			String formattedString = WagonNumberUtils.toDisplayNo(wagonNo);
			if (formattedString.equals(wagonNo)) { //if it remained as it was that means that it is either badly formatted, or it is formatted already
				//check if it can be converted to a normal wagon no (it was typed in originally as formatted, but uniqueness will be checked with db NO)
				formattedString = WagonNumberUtils.toDbNo(wagonNo);
				if (formattedString.equals(wagonNo)) {										
					MessageUtils.addActionWarning(request,"wagondetail.error.wagonno.format");
					return;
				}
			}
			else{
				formattedString = wagonNo;
			}
			Long wagonId = null;
			if (getInputWagonNo() == null) {
				//it is not copy
				wagonId = getWagonId();
			}
			boolean uniqueConstraintViolated = ServiceLocatorProvider.getServiceLocator().getWagonDetailService().findWagonWithUniqueParameters(wagonId, formattedString);
			if (uniqueConstraintViolated) {
				MessageUtils.addActionWarning(request,"wagondetail.error.wagonno.notunique");	
			}
		}
		else{
			MessageUtils.addActionWarning(request,"wagondetail.error.wagonno.empty");
		}
	}
	
	public boolean getWagonTypeIsLocked() {
		return ( (!wagonDetailControl.getFromDefaultMaxLoadT().booleanValue()) || (!wagonDetailControl.getFromDefaultVolumeM3().booleanValue()) || (!wagonDetailControl.getFromDefaultLimitCreditNoteT()) );
	}

	public String getFormAction() {
		return formAction;
	}

	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public String getActiveTab() {
		return activeTab;
	}

	public void setActiveTab(String activeTab) {
		this.activeTab = activeTab;
	}

	public String getRequestedInfo() {
		return requestedInfo;
	}

	public void setRequestedInfo(String requestedInfo) {
		this.requestedInfo = requestedInfo;
	}

	public Long getWagonId() {
		return wagonId;
	}

	public void setWagonId(Long wagonId) {
		this.wagonId = wagonId;
	}

	public WagonDetailMainBean getWagonDetailMain() {
		return wagonDetailMain;
	}

	public void setWagonDetailMain(WagonDetailMainBean wagonDetailMain) {
		this.wagonDetailMain = wagonDetailMain;
	}

	public WagonDetailLoadBean getWagonDetailLoad() {
		return wagonDetailLoad;
	}

	public void setWagonDetailLoad(WagonDetailLoadBean wagonDetailLoad) {
		this.wagonDetailLoad = wagonDetailLoad;
	}

	public WagonDetailControlBean getWagonDetailControl() {
		return wagonDetailControl;
	}

	public void setWagonDetailControl(WagonDetailControlBean wagonDetailControl) {
		this.wagonDetailControl = wagonDetailControl;
	}

	public WagonDetailTypeBean getWagonDetailType() {
		return wagonDetailType;
	}

	public void setWagonDetailType(WagonDetailTypeBean wagonDetailType) {
		this.wagonDetailType = wagonDetailType;
		wagonDetailType.addPropertyChangeListener(new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {

				if ( ("wagonTypeCd".equals(evt.getPropertyName())) && (wagonDetailControl != null) ) {
					WagonDetailTypeBean wagonDetailType = (WagonDetailTypeBean)evt.getSource();
					if (!wagonDetailControl.getFromDefaultMaxLoadT().booleanValue()) {
						wagonDetailControl.setMaxGrossWeightT(wagonDetailType.getWagonTypeMaxLoadT());
					}
					if (!wagonDetailControl.getFromDefaultVolumeM3().booleanValue()) {
						wagonDetailControl.setVolumeM3(wagonDetailType.getWagonTypeVolumeM3());
					}
					if (!wagonDetailControl.getFromDefaultLimitCreditNoteT().booleanValue()) {
						wagonDetailControl.setLimitCreditNoteT(wagonDetailType.getWagonTypeLimitCreditNoteT());
					}
				}
			}
		});
	}

	public WagonDetailBuildBean getWagonDetailBuild() {
		return wagonDetailBuild;
	}

	public void setWagonDetailBuild(WagonDetailBuildBean wagonDetailBuild) {
		this.wagonDetailBuild = wagonDetailBuild;
	}

	public WagonDetailWheelsetBean getWagonDetailWheelset() {
		return wagonDetailWheelset;
	}

	public void setWagonDetailWheelset(WagonDetailWheelsetBean wagonDetailWheelset) {
		this.wagonDetailWheelset = wagonDetailWheelset;
	}

	public RfidobjectEntity getRfidObject() {
		return rfidObject;
	}

	public void setRfidObject(RfidobjectEntity rfidObject) {
		this.rfidObject = rfidObject;
	}

	public String getInputWagonNo() {
		return inputWagonNo;
	}

	public void setInputWagonNo(String inputWagonNo) {
		this.inputWagonNo = inputWagonNo;
	}

	public WagonDetailMaintenanceBean getWagonDetailMaintenance() {
		return wagonDetailMaintenance;
	}

	public void setWagonDetailMaintenance(WagonDetailMaintenanceBean wagonDetailMaintenance) {
		this.wagonDetailMaintenance = wagonDetailMaintenance;
	}

	public String getResetToTab() {
		return resetToTab;
	}

	public void setResetToTab(String resetToTab) {
		this.resetToTab = resetToTab;
	}

	public Map<Long, String> getWagonIdMap() {
		return wagonIdMap;
	}

	public void setWagonIdMap(Map<Long, String> wagonIdMap) {
		this.wagonIdMap = wagonIdMap;
	}

	public WagonDetailCurrentOrderBean getOrderBean() {
		return orderBean;
	}

	public void setOrderBean(WagonDetailCurrentOrderBean orderBean) {
		this.orderBean = orderBean;
	}

	public Boolean isLastUploadFailed() {
		return lastUploadFailed;
	}

	public void setLastUploadFailed(Boolean lastUploadFailed) {
		this.lastUploadFailed = lastUploadFailed;
	}

	public String getLastUnsupportedFileExtension() {
		return lastUnsupportedFileExtension;
	}

	public void setLastUnsupportedFileExtension(String lastUnsupportedFileExtension) {
		this.lastUnsupportedFileExtension = lastUnsupportedFileExtension;
	}
	
}
