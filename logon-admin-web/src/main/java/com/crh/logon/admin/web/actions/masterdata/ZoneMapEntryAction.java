/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.masterdata;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.masterdata.ZoneMapForm;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.TrspZonePolygonEntity;
import com.crh.logon.persistence.entities.refdata.TrspZoneBsEntity;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_ZoneMapForm" path="/ZoneMapEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="ZoneMapTile.def"
 * @struts.action-forward name="failure" path="ZoneMapTile.def"
 */
public class ZoneMapEntryAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ZoneMapEntryAction.class);

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
			Authorization.authorize(Authorization.MASTER_DATA_GLOBAL_ZONEMAP_BAG);
			ZoneMapForm helpForm;
			String forwardName = "success";
			setSelectedTab("masterdata", request);
			setSelectedSub1Tab("global", request);
			setSelectedSub2Tab("zonemap", request);
			
			// Get the struts form.
			helpForm = (ZoneMapForm) form;
			helpForm.clear();
			
			final List<TrspZoneBsEntity> zoneList = getRefDataGenericsService().findBsEntityList(TrspZoneBsEntity.class, 
					false, TrspZoneBsEntity.ATTR_TRSP_ZONE_CD, true);
			helpForm.setTrspZoneList(zoneList);
			Map zoneMap = listToMap(zoneList);
			helpForm.setTrspZoneMap(zoneMap);
			
			//get polygon list
			List<TrspZonePolygonEntity> polygonList = getZoneMapService().loadTrspZonePolygons();
			
			//set all null zIndex values to -1 (form validation will reset them back)
			for (TrspZonePolygonEntity polygon : polygonList) {
				if (polygon.getZIndex() == null) {
					polygon.setZIndex(new Long(Constant.INVALID_OBJECT_ID));
				}
			}
			
			//set selected polygon id
			if (!polygonList.isEmpty()) {
				TrspZonePolygonEntity ploygon = polygonList.get(0);
				helpForm.setSelectedPolygonId(ploygon.getTrspZonePolygonId());
			}
			
			helpForm.setPolygonList(polygonList);
			
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

	private Map listToMap(List<TrspZoneBsEntity> list) {
		// [[
		Map result = null;
		Object[][] params = {{"list", list}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "listToMap", params);
		}
		// ]]
		try {
			final Map<String, TrspZoneBsEntity> map = new HashMap<String, TrspZoneBsEntity>();
			for (TrspZoneBsEntity zone : list) {
				map.put(zone.getTrspZoneCd(), zone);
			}
			return result = map;
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
