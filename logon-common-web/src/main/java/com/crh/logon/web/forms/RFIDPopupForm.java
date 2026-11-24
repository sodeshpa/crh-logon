/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.forms;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.rfidadministration.parameters.RFIDDataCache;
import com.crh.logon.business.services.utils.RFIDBean;
import com.crh.logon.business.services.utils.RFIDBean.AbstractRFIDBean;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.common.RfidtagEntity;

/**
 *	Form of RFIDPopupTile, which contains dropdown with all available rfidTags.
 *	It is all about attaching rfIdTags to rfIdObjects. Supposed that an rfIdObject is
 *	set to the session with the name SESSION_RFIDOBJECT_NAME. The selected rfidTag is removed
 *	from that object back to the list in case we remove it on RFIDsTile. The div named DIV_NAME
 *	is going to be reloaded each case of add/remove.
 *
 * WARNING! This form, and all the actions does not care about the RFIDObject Type, if you use it, you must 
 * take care of the proper setting of that. It's easily solvable with the help of RFIDServiceBean's 
 * functions.
 *	
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 */
@AdnTrace
public class RFIDPopupForm extends ActionForm {
	public Long selectedRfidTagId;

	public RfidobjectEntity selectedRfidObject;
	private static final long serialVersionUID = -8910104051337644556L;
	public static final String NAME_OF_HTML_DIV_TO_RELOAD = "rfids";
	protected static final JcanLogger LOG = JcanLoggerFactory.getLogger(RFIDPopupForm.class);

	private static final RfidobjectEntity NO_RFID_OBJECT_SELECTED = null;

	public RfidobjectEntity getRfObject() {
		return selectedRfidObject;
	}

	public void setRfObject(RfidobjectEntity rfObject) {
		this.selectedRfidObject = rfObject;
	}

	public void setRfObject(AbstractRFIDBean rfidSelection) {
		this.selectedRfidObject = rfidSelection.getRfidObject();
	}

	/**
	 * Default constructor.
	 */
	public RFIDPopupForm() {
		init();
	}

	/**
	 * Initializes the form. This has the effect of clearing all the input
	 * fields on the web form.
	 */
	protected void init() {
		this.selectedRfidObject = null;
		this.selectedRfidTagId = null;
	}
	
	public Long getRfidTagId() {
		return selectedRfidTagId;
	}

	public void setRfidTagId(Long rfidTagId) {
		this.selectedRfidTagId = rfidTagId;
	}
	
	public static void initForm(HttpSession session, AbstractRFIDBean rfidObjectSelection) {
		resetRfidDataCache(session);
		session.setAttribute(RFIDBean.SESSION_KEY, rfidObjectSelection);
	}

	public static void resetForm(HttpSession session) {
		resetRfidDataCache(session);
		session.removeAttribute(RFIDBean.SESSION_KEY);
	}

	public List<RfidtagEntity> getAssignedRfids() {
		if (selectedRfidObject == NO_RFID_OBJECT_SELECTED) {
			return Collections.<RfidtagEntity> emptyList();
		}
		return selectedRfidObject.getRfids();
	}

	/*
	 * TODO (armin Nov 3, 2014): Make the rfid temp storage have its boundaries within its
	 * parent forms so no optimistic locking exception can happen when an rfidtag is removed from and rfid object
	 * and it is added to a different rfid object withing the same form without persisting data.
	 * Currently the boundaries are rfidobject based to avoid rfids leaking to different forms.
	 */
	public static RFIDDataCache resetRfidDataCache(HttpSession session) {
		RFIDDataCache rfidDataCache = new RFIDDataCache();
		session.setAttribute(RFIDDataCache.SESSION_KEY, rfidDataCache);
		return rfidDataCache;
	}
	
	public static RFIDDataCache getRFIDDataCache(HttpSession session) {
		RFIDDataCache rfidDataCache = (RFIDDataCache) session.getAttribute(RFIDDataCache.SESSION_KEY);
		if (rfidDataCache == null) {
			rfidDataCache = resetRfidDataCache(session);
		}
		
		return rfidDataCache;
	}

}
