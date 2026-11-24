/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.masterdata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.forms.masterdata.ZoneMapForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.TrspZonePolygonEntity;
import com.crh.logon.persistence.entities.refdata.TrspZoneBsEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_ZoneMapForm" path="/ZoneMap" scope="session"
 *  validate="true" input="ZoneMap.def"
 *  
 * @struts.action-forward name="zoneMapEntry" path="/ZoneMapEntry.do"
 * @struts.action-forward name="input" path="ZoneMapTile.def"
 * @struts.action-forward name="addPolygon" path="ZoneMapPolygon.def"
 * 
 */
public class ZoneMapAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ZoneMapAction.class);
	public static final String FORWARD_SAVE = "save";
	public static final String FORWARD_CANCEL = "cancel";
	public static final String FORWARD_ADD = "addPolygon";
	public static final String FORWARD_REMOVE = "removePolygon";
	public static final String RESULT_SUCCESS = "success";
	public static final String RESULT_FAILURE = "failure";
	
	
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
			String forwardName = "input";
			// Get the struts form.
			helpForm = (ZoneMapForm) form;
		
			
			
			if (FORWARD_CANCEL.equals(helpForm.getFormCancel())) {
				//reset
				helpForm.setFormCancel("");
				forwardName = "zoneMapEntry";
			}
			else if (FORWARD_ADD.equals(helpForm.getFormAction())) {
				if (logDebugEnabled) {
					LOG.debug("Action: add polygon button clicked!");
				}
				
				TrspZonePolygonEntity polygon = new TrspZonePolygonEntity();
				//all ids less than 0 are new ones! We need to have ids because of polygon objects on the map.
				long newZoneId = helpForm.getNewZoneId() - 1;
				helpForm.setNewZoneId(newZoneId);
				polygon.setTrspZonePolygonId(new Long(newZoneId));
				polygon.setTrspZone(new TrspZoneBsEntity());
				//default color is white
				polygon.setColorRgb("FFFFFF");
				polygon.setZIndex(new Long(Constant.INVALID_OBJECT_ID));
				helpForm.getPolygonList().add(polygon);
				
				//set selected polygon id
				helpForm.setSelectedPolygonId(newZoneId);
				
				forwardName = FORWARD_ADD;
			}
			else if (FORWARD_SAVE.equals(helpForm.getFormAction())) {
				if (logDebugEnabled) {
					LOG.debug("Action: save polygon button clicked!");
				}
				
				// form validation is called from here because it takes ages to reload google map!
				helpForm.validateZoneMap(request);
				
				helpForm.setFormResult(RESULT_FAILURE);
				if ((MessageUtils.getActionErrors(request) == null) || (MessageUtils.getActionErrors(request).size() == 0)) {
					
					//set zindex to null if user did not select
					for (Iterator it = helpForm.getPolygonList().iterator(); it.hasNext();) {
						TrspZonePolygonEntity polygon = (TrspZonePolygonEntity) it.next();
						if ((polygon.getZIndex() != null) && (Constant.INVALID_OBJECT_ID == polygon.getZIndex())) {
							polygon.setZIndex(null);
						}
					}
					
					//udate poygon list
					List<TrspZonePolygonEntity> polygonList = getZoneMapService().updateTrspZonePolygons(helpForm.getPolygonList(), helpForm.getRemovedPolygonList(), helpForm.getTrspZoneVertexMap());
					
					//set all null zIndex values to -1
					for (TrspZonePolygonEntity polygon : polygonList) {
						if (polygon.getZIndex() == null) {
							polygon.setZIndex(new Long(Constant.INVALID_OBJECT_ID));
						}
					}
					
					helpForm.setPolygonList(polygonList);
					helpForm.setRemovedPolygonList(new ArrayList<TrspZonePolygonEntity>());
					helpForm.setFormResult(RESULT_SUCCESS);
					MessageUtils.addActionMessage(request, "zonemap.message.success.save");
				}
			
				
				//set selected polygon id
				if (!helpForm.getPolygonList().isEmpty()) {
					TrspZonePolygonEntity pl = (TrspZonePolygonEntity) helpForm.getPolygonList().get(0); 
					helpForm.setSelectedPolygonId(pl.getTrspZonePolygonId());
				}
				
				forwardName = FORWARD_ADD;
			}
			else if (FORWARD_REMOVE.equals(helpForm.getFormAction())) {
				String indexStr = request.getParameter("index");
				if (logDebugEnabled) {
					LOG.debug("Action: remove polygon button clicked! Index = " + indexStr);
				}
			
				int index = StringUtils.toIntValue(indexStr);
				
				TrspZonePolygonEntity ploygon = (TrspZonePolygonEntity) helpForm.getPolygonList().remove(index);
				if (ploygon.getTrspZonePolygonId() >= 0) {
					helpForm.getRemovedPolygonList().add(ploygon);
				}
				
				//set selected polygon id
				if (!helpForm.getPolygonList().isEmpty()) {
					TrspZonePolygonEntity pl = (TrspZonePolygonEntity) helpForm.getPolygonList().get(0); 
					helpForm.setSelectedPolygonId(pl.getTrspZonePolygonId());
				}
				
				forwardName = FORWARD_ADD;
			}
			
			
			helpForm.setFormAction("");
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
