/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.simplevehicleadmin;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.forms.simplevehicleadmin.SimpleVehicleAdminForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.TrailerAdminService;
import com.crh.logon.constants.Constant;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_SimpleVehicleAdminForm"
 *  path="/simplevehicleadmin/SimpleVehicleAdminEntry" scope="session"
 *  validate="false" input="/simplevehicleadmin/SimpleVehicleAdmin.def"
 *
 * @struts.action-forward name="success" path="/simplevehicleadmin/SimpleVehicleAdmin.def"
 */
public class SimpleVehicleAdminEntryAction extends ActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(SimpleVehicleAdminEntryAction.class);

	public ActionForward execute(
		ActionMapping mapping, ActionForm form,
		HttpServletRequest request,	HttpServletResponse response)
		throws IOException, ServletException
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
			Authorization.authorize(Authorization.MASTER_DATA_ROAD_SIMPLE_VEHICLE);			
			SimpleVehicleAdminForm helpForm = (SimpleVehicleAdminForm) form;
			helpForm.clear();
			setSelectedTab("masterdata", request);
			setSelectedSub1Tab("road", request);
			setSelectedSub2Tab("simplevehicle", request);
			String forwardName = "success";
			TrailerAdminService  service = getServiceLocator().getTrailerAdminService();
			List carrierList = service.fetchCarriers();
			helpForm.setCarrierList(carrierList);			
						
			populateSimpleVehiclePriorities(helpForm);
			populateValidCounts(helpForm);
			
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
		
	/**
	 * Populates the simpleVehiclePriorities array of SimpleVehicleAdminForm.
	 * 
	 * @param simpleVehicleAdminForm - the form instance
	 */
	private void populateSimpleVehiclePriorities(SimpleVehicleAdminForm simpleVehicleAdminForm) {
		// TODO 2012-Nov-13/awe: this is 100% identical to trucks (using same constants seams reasonable) - move it to some common class?
		
		/* 
		 * Priority has inverted meaning. 
		 * The minimum priority value (99) is the higher number, and the maximum (1) is the lower. 
		 * */
		int min = (int)Constant.TRUCK_PRIORITY_MIN;
		int max = (int)Constant.TRUCK_PRIORITY_MAX;

		// We have to include the "inactive" value besides 1-99.
		int finalSize = min + 1;
		long[] priorities = new long[finalSize];

		priorities[0] = Constant.TRUCK_PRIORITY_TRUCK_INACTIVE;

		for (int i = max; i <= min; i++) {
			priorities[i] = i;
		}

		simpleVehicleAdminForm.setSimpleVehiclePriorities(priorities);
	}
	
	
	private long[] createArray(int min, int max) {
		long[] data = new long[max - min + 1];
		for (int ii = 0; ii < data.length; ++ii) {
			data[ii] = ii + min;
		}
		return data;
	}
	
	/**
	 * Populates the validUnitCounts and validNrChambers arrays of SimpleVehicleAdminForm.
	 * 
	 * @param simpleVehicleAdminForm - the form instance
	 */
	private void populateValidCounts(SimpleVehicleAdminForm simpleVehicleAdminForm) {
		long[] chambers = createArray((int)Constant.SIMPLE_VEHICLE_NR_CHAMBERS_MIN, 
				(int)Constant.SIMPLE_VEHICLE_NR_CHAMBERS_MAX);
		simpleVehicleAdminForm.setValidNrChambers(chambers);
		long[] units = createArray((int)Constant.SIMPLE_VEHICLE_UNIT_COUNT_MIN, 
				(int)Constant.SIMPLE_VEHICLE_UNIT_COUNT_MAX);
		simpleVehicleAdminForm.setValidUnitCounts(units);
	}

}
