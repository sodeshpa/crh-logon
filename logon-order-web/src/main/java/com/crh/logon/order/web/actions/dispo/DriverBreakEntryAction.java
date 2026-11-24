/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.dispo;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.forms.dispo.DriverBreakForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.order.web.forms.dispo.DispoForm;
import com.crh.logon.persistence.entities.common.DriverBreakEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_dispo_DriverBreakForm" 
 *  path="/dispo/DriverBreakEntry" scope="session"
 *  validate="true" input="DriverBreakEditTile.def"
 *
  * @struts.action-forward name="edit" path="DriverBreakEditTile.def"
 */
public class DriverBreakEntryAction extends OrderActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(DriverBreakEntryAction.class);

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
			Authorization.authorize(Authorization.DISPATCHING_W);
		    // Get the struts form(s).
			DriverBreakForm helpForm = (DriverBreakForm) form;
			DispoForm dispoForm = (DispoForm) request.getSession().getAttribute("order_dispo_DispoForm");
			
		    String forwardName = "edit";
		    
		    DriverBreakEntity driverBreak;
		    if (helpForm.getDriverBreakId() != null){
		    	driverBreak = getServiceLocator().getDispoService()
		    		.fetchDriverBreak(helpForm.getDriverBreakId());
		    }
		    else {
		    	driverBreak = new DriverBreakEntity();
		    	DriverEntity driver =  getServiceLocator().getDispoService()
	    			.fetchDriver(helpForm.getDriverId());
		    	driverBreak.setDriver(driver);
		    	driverBreak.setIsLunchBreak(helpForm.getIsLunchBreak());
		    	driverBreak.setIsMopBreak(Boolean.FALSE);
		    	ConfigBean config = getConfigBean();
				Calendar c = Calendar.getInstance();
				c.setTime(dispoForm.getDate());
				c.add(Calendar.MINUTE, config.getAppDefaultDispoDriverLunchBreakStart());
		    	driverBreak.setStartDat(c.getTime());
				c.setTime(dispoForm.getDate());
				c.add(Calendar.MINUTE, config.getAppDefaultDispoDriverLunchBreakEnd());				
		    	driverBreak.setEndDat(c.getTime());
		    }
		    
		    helpForm.setDriverBreak(driverBreak);
		    helpForm.setBreakDay(dispoForm.getDate());
		    
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
}
