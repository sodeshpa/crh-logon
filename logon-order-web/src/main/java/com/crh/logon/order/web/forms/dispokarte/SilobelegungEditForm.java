/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.dispokarte;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.refdata.SiloTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.SiloTypeTxEntity;
import com.crh.logon.persistence.entities.views.SapMaterialDescEntity;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="order_dispokarte_SilobelegungEditForm"
 */
public class SilobelegungEditForm extends ActionForm {

	private static final int NOTHING_IS_SELECTED = -1;

	private static final long serialVersionUID = -8910104051337644556L;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SilobelegungEditForm.class);

	public static final String REFERRER_DISPOKARTE = "dispokarte";

	public static final String REFERRER_ORDER_TAKING = "order";

	private int selectedSiloIdentifier;

	private String action;

	private String maxVolumeTons;

	private String maxVolumeMeter;

	private boolean unloadingTimeCheckbox;

	private Long siloTypeCd;

	private String referrer;

	private List<SiloTypeTxEntity> siloTypes;

	private Map<String, ShiptoSiloEntity> siloNameMap;

	//TODO: replace with IndexOf when equals is implemented
	private int selectedMaterialIndex;

	// db state
	private ShiptoSiloEntity silo;

	//dropdowns
	private List<SapMaterialDescEntity> materials;

	/**
	 * resets the form. 
	 */
	public void clear() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "clear");
		}
		// ]]
		try {
			selectedMaterialIndex = NOTHING_IS_SELECTED;
			maxVolumeMeter = null;
			maxVolumeTons = null;
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
	 * ordermgr.web form, but rather is called on every request before the struts action
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
			unloadingTimeCheckbox = false;
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
	 * Default constructor.
	 */
	public SilobelegungEditForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "SilobelegungEditForm");
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
	 * fields on the order.web form.
	 */
	private void init() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			selectedSiloIdentifier = NOTHING_IS_SELECTED;
			silo = new ShiptoSiloEntity();
			action = "edit";
			materials = new LinkedList<SapMaterialDescEntity>();
			selectedMaterialIndex = NOTHING_IS_SELECTED;
			maxVolumeMeter = null;
			maxVolumeTons = null;
			siloNameMap = new HashMap<String, ShiptoSiloEntity>();
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

			/* no need to validate if 'delete' button was pressed */
			if (request.getParameter("delete") != null) {
				return result = null;
			}
			if (errors == null) {
				errors = new ActionErrors();
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

	public int getId() {
		return selectedSiloIdentifier;
	}

	public void setId(int id) {
		this.selectedSiloIdentifier = id;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	
	public int getMaterialIdx() {
		return selectedMaterialIndex;
	}

	public void setMaterialIdx(int materialIdx) {
		this.selectedMaterialIndex = materialIdx;
	}
	

	public ShiptoSiloEntity getSilo() {
		return silo;
	}

	public void setSilo(ShiptoSiloEntity silo) {
		this.silo = silo;
		if (silo != null){
			if (silo.getMaxVolumeM3() != null){
				maxVolumeMeter = silo.getMaxVolumeM3().toString();
			}
			if (silo.getMaxVolumeTons() != null){
				maxVolumeTons = silo.getMaxVolumeTons().toString();
			}
			calculateIdx();
		}
	}

	
	public List<SapMaterialDescEntity> getMaterials() {
		return materials;
	}

	public void setMaterials(List<SapMaterialDescEntity> materials) {
		this.materials = materials;
		calculateIdx();
	}

	private void calculateIdx(){
		if ((materials == null) || (silo == null)){
			selectedMaterialIndex = 0;
			return;
		}
		for (int i = 0; i < materials.size(); i++){
			SapMaterialDescEntity m = materials.get(i);
			if ((silo.getSapMaterial() != null) && m.getSapMaterialId().equals(silo.getSapMaterial().getSapMaterialId())){
				selectedMaterialIndex = i;
				return;
			}
		}
	}

	public String getMaxVolumeTons() {
		return maxVolumeTons;
	}

	public void setMaxVolumeTons(String maxVolumeTons) {
		this.maxVolumeTons = maxVolumeTons;
	}

	public String getMaxVolumeMeter() {
		return maxVolumeMeter;
	}

	public void setMaxVolumeMeter(String maxVolumeMeter) {
		this.maxVolumeMeter = maxVolumeMeter;
	}

	public boolean isUnloadingTimeCheckbox() {
		return unloadingTimeCheckbox;
	}

	public void setUnloadingTimeCheckbox(boolean unloadingTimeCheckbox) {
		this.unloadingTimeCheckbox = unloadingTimeCheckbox;
	}

	public List<SiloTypeTxEntity> getSiloTypes() {
		return siloTypes;
	}

	public void setSiloTypes(List<SiloTypeTxEntity> siloTypes) {
		this.siloTypes = siloTypes;
	}

	public Long getSiloTypeCd() {
		return siloTypeCd;
	}

	public void setSiloTypeCd(Long siloTypeCd) {
		this.siloTypeCd = siloTypeCd;
		SiloTypeBsEntity type = findTypeWithCd();
		silo.setSiloType(type);
	}
	
	private SiloTypeBsEntity findTypeWithCd(){
		SiloTypeBsEntity result = null;

		for (SiloTypeTxEntity siloType : getSiloTypes()) {
			if (siloType.getSiloTypeTxPK().getSiloTypeBs().getSiloTypeCd().equals(getSiloTypeCd())){
				result = siloType.getSiloTypeTxPK().getSiloTypeBs();
			}
		}
		return result;
	}

	public String getReferrer() {
		return referrer;
	}

	public void setReferrer(String referrer) {
		this.referrer = referrer;
	}

	public Map<String, ShiptoSiloEntity> getSiloNameMap() {
		return siloNameMap;
	}

	public void setSiloNameMap(Map<String, ShiptoSiloEntity> siloNameMap) {
		this.siloNameMap = siloNameMap;
	}

}
