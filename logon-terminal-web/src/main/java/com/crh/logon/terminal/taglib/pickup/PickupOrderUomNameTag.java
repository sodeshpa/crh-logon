/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.terminal.taglib.pickup;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.terminal.web.forms.pickup.PickupForm;
import com.crh.logon.web.utils.ServiceLocatorProvider;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * Used to determine whether functionality is available for a given order.
 * @author AdNovum Informatik AG
 */
public class PickupOrderUomNameTag extends TagSupport { 
	
	private static final String C_NAME = 
		PickupOrderUomNameTag.class.getName();

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(C_NAME);
	
	private String uomCd;
	
	public int doStartTag() throws JspException {
		// [[
		int result = 0;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "doStartTag");
		}
		// ]]
		try {
			// TODO (emoke Mar 3, 2010):optimize later maybe
			PickupForm form = (PickupForm) pageContext.getSession().getAttribute(PickupForm.FORM_NAME);
			if (form.getUomCdToUomTxMap() == null ){
				form.setUomCdToUomTxMap(new HashMap());
			}
			if (form.getUomCdToUomTxMap().containsKey(uomCd)) {
				Object obj = form.getUomCdToUomTxMap().get(uomCd);
				try {
					pageContext.getOut().write(((UomTxEntity)obj).getTxt());
				}
				catch (IOException e) {
					LOG.error("Error in " + PickupOrderUomNameTag.C_NAME);	
				}
			}
			else{
				UomTxEntity desc = (UomTxEntity) (ServiceLocatorProvider.getServiceLocator().getRefDataGenericsService().findTxEntity(UomTxEntity.class, pageContext.getRequest().getLocale(), uomCd));
				form.getUomCdToUomTxMap().put(uomCd,desc);
				try {
					if (desc != null) {						
						pageContext.getOut().write(desc.getTxt());
					}
				}
				catch (IOException e) {
					LOG.error("Error in " + PickupOrderUomNameTag.C_NAME);
				}
			}
			
			return result = EVAL_BODY_INCLUDE;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public String getUomCd() {
		return uomCd;
	}

	public void setUomCd(String uomCd) {
		this.uomCd = uomCd;
	}



}
