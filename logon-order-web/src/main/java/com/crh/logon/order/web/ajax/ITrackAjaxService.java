/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.ajax;

import javax.servlet.http.HttpServletRequest;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.itrack.dto.PositionDTO;
import com.crh.logon.business.services.interfaces.ITrackService;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;


/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to 
 * Ajax components. 
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="ITrackAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.order.web.utils.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.Reason"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.itrack.dto.PositionDTO"
 * @adnbus.dwr.convert converter="map" match="java.util.Map"/>
 *
 */
public class ITrackAjaxService extends DwrServiceSupport {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ITrackAjaxService.class);
    
	public PositionDTO getLastPositionOfTheTruck(String truckPlateNo, HttpServletRequest request) {
		Authorization.authorize(Authorization.LIVE_MAP);
		final ITrackService iTrackService = getServiceLocator().getITrackService();
		PositionDTO position =  iTrackService.getLastPosition(truckPlateNo);
		return position;
	}
		
}
