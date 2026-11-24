/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.masterdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.TrspZonePolygonEntity;
import com.crh.logon.persistence.entities.common.TrspZoneVertexEntity;
import com.crh.logon.persistence.entities.refdata.TrspZoneBsEntity;
import com.crh.logon.web.utils.MessageUtils;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_ZoneMapForm"
 */
public class ZoneMapForm extends ActionForm {

	private static final long serialVersionUID = -8910104051337644556L;
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ZoneMapForm.class);
    public static final String SESSION_ZONEMAPFORM_NAME = "admin_ZoneMapForm";
    
	private String formAction;
	private String formCancel;
	private String formResult;
	private List<TrspZonePolygonEntity> polygonList;
	private List<TrspZonePolygonEntity> removedPolygonList;
	private Map<String, List<TrspZoneVertexEntity>>  trspZoneVertexMap;
	//private List initTrspZoneVertexList;
	
	private List<TrspZoneBsEntity> trspZoneList;
	private Map<String, TrspZoneBsEntity> trspZoneMap;
	private long newZoneId;
	private long selectedPolygonId;

	
	public String getFormCancel() {
		return formCancel;
	}

	public void setFormCancel(String formCancel) {
		this.formCancel = formCancel;
	}
	
	public Map<String, List<TrspZoneVertexEntity>> getTrspZoneVertexMap() {
		return trspZoneVertexMap;
	}

	public void setTrspZoneVertexMap(Map<String, List<TrspZoneVertexEntity>> trspZoneVertexMap) {
		this.trspZoneVertexMap = trspZoneVertexMap;
	}
	
	public long getSelectedPolygonId() {
		return selectedPolygonId;
	}

	public void setSelectedPolygonId(long selectedPolygonId) {
		this.selectedPolygonId = selectedPolygonId;
	}
	
	public List<TrspZonePolygonEntity> getRemovedPolygonList() {
		return removedPolygonList;
	}

	public void setRemovedPolygonList(List<TrspZonePolygonEntity> removedPolygonList) {
		this.removedPolygonList = removedPolygonList;
	}
	
	public Map<String, TrspZoneBsEntity> getTrspZoneMap() {
		return trspZoneMap;
	}

	public void setTrspZoneMap(Map<String, TrspZoneBsEntity> trspZoneMap) {
		this.trspZoneMap = trspZoneMap;
	}
	
	public long getNewZoneId() {
		return newZoneId;
	}

	public void setNewZoneId(long newZoneId) {
		this.newZoneId = newZoneId;
	}
	
	public List<TrspZoneBsEntity> getTrspZoneList() {
		return trspZoneList;
	}

	public void setTrspZoneList(List<TrspZoneBsEntity> trspZoneList) {
		this.trspZoneList = trspZoneList;
	}
	
	public List<TrspZonePolygonEntity> getPolygonList() {
		return polygonList;
	}

	public void setPolygonList(List<TrspZonePolygonEntity> polygonList) {
		this.polygonList = polygonList;
	}
	
	public int getPolygonListSize() {
		if (polygonList == null) {
			return 0;
		}
		return polygonList.size();
	}

	/**
	 * Default constructor.
	 */
	public ZoneMapForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "ZoneMapForm");
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
			formAction = "";
			formCancel = "";
			formResult = "";
			newZoneId = 0;
			polygonList = new ArrayList<TrspZonePolygonEntity>();
			removedPolygonList = new ArrayList<TrspZonePolygonEntity>();
			trspZoneList = new ArrayList();
			trspZoneMap = new HashMap();
			trspZoneVertexMap = new HashMap<String, List<TrspZoneVertexEntity>>();
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
			
			if (logDebugEnabled) {
				LOG.debug("Reset checkboxes!");
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

	public void validateZoneMap(HttpServletRequest request) {
		// [[
		Object[][] params = {{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validateZoneMap", params);
		}
		// ]]
		try {
			
			for (Iterator it = polygonList.iterator(); it.hasNext();) {
				TrspZonePolygonEntity polygon = (TrspZonePolygonEntity) it.next();
				
				if (polygon.getTrspZone() == null || StringUtils.isEmpty(polygon.getTrspZone().getTrspZoneCd())) {
					MessageUtils.addActionError(request, "zonemap.errors.format.zone");
				} 
				if (StringUtils.defaultString(polygon.getColorRgb()).length() != TrspZonePolygonEntity.DBSIZE_COLOR_RGB) {
					MessageUtils.addActionError(request, "zonemap.errors.format.color");
				}
				if (polygon.getZIndex() == null || polygon.getZIndex().equals(Long.valueOf(Constant.INVALID_OBJECT_ID))) {
					MessageUtils.addActionError(request, "error.field.required", MessageUtils.getMessage(request, "zonemap.text.zindex"));
				}
			
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
	

	public String getFormAction() {
		return formAction;
	}

	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}

	public String getFormResult() {
		return formResult;
	}

	public void setFormResult(String formResult) {
		this.formResult = formResult;
	}
	
	
	public void setPolygonZone( int index, String trspZoneCd ) {
		if ( StringUtils.isNotEmpty(trspZoneCd) ){			
			TrspZoneBsEntity trspZone = (TrspZoneBsEntity)trspZoneMap.get(trspZoneCd);
			getPolygonList().get(index).setTrspZone(trspZone);
		}
	}
	
	public String getPolygonZone( int index ) {
		return getPolygonList().get(index).getTrspZone().getTrspZoneCd();
	}
}
