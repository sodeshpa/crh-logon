/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.dispokarte;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.forms.dispokarte.BilltoSelectionRhythmForm;
import com.crh.logon.order.web.forms.dispokarte.DispokarteForm;
import com.crh.logon.order.web.forms.dispokarte.SalesAreaBean;
import com.crh.logon.order.web.utils.comparator.BilltoComparator;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.persistence.entities.common.ShiptoBilltoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrBtrulEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.web.utils.Authorization;

/**
 * @author AdNovum Informatik AG
 *
 * @struts.action name="order_dispokarte_BilltoSelectionRhythmForm"
 *  path="/dispokarte/BilltoSelectionRhythmEntryAction" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="BilltoSelectionRhythmTile.def"
 *
 */
public class BilltoSelectionRhythmEntryAction extends OrderActionSupport {
    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(BilltoSelectionRhythmEntryAction.class);

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
			Authorization.authorize(Authorization.MASTER_DATA_SHIPTO_CARD_R);
			BilltoSelectionRhythmForm helpForm = (BilltoSelectionRhythmForm) form;
			DispokarteForm dispoForm = (DispokarteForm) request.getSession().getAttribute(DispokarteForm.FORM_NAME);
			
			helpForm.setExistingRulesMap(new HashMap());
			int index = new Integer(request.getParameter("id")).intValue();
	    	helpForm.setId(index);
			
			Map billtoIndexMap = new HashMap();
			
			SalesAreaBean currSalesAreaBean = dispoForm.getCurrSalesAreaBean();
			ShiptoSareaEntity shiptoSarea = currSalesAreaBean.getShiptoSarea();
			List<ShiptoBilltoEntity> billtos = shiptoSarea.getBilltos();
			if (billtos != null && billtos.size() > 0) {
				helpForm.initLists(billtos.size());
				
				Collections.sort(billtos, new BilltoComparator() );
				ListIterator billtoIt = billtos.listIterator();
				
				int billtoIndex = -1;
				while ( billtoIt.hasNext() ) {
					billtoIndex++;
					ShiptoBilltoEntity billto = (ShiptoBilltoEntity) billtoIt.next();
					billtoIndexMap.put(billto.getBilltoSapBp().getSapBpId(), new Integer(billtoIndex) );
					
				}
			}
			
			ShiptoSareaRestrEntity restr = ((DeliverySettingDTO) currSalesAreaBean.getDeliverySettingForIndex(index)).getShiptoSareaRestr();
			helpForm.setCurrentShiptoSareaRestr(restr);
			List rules = restr.getRules();
			
			if ( restr.getBilltoRuletype() != null) {
				if ( new Long(0).equals(restr.getBilltoRuletype().getBilltoRuletypeCd())) {
					helpForm.setTab(BilltoSelectionRhythmForm.TAB_MONTHLY);
				}
				else if ( new Long(1).equals(restr.getBilltoRuletype().getBilltoRuletypeCd())) {
					helpForm.setTab(BilltoSelectionRhythmForm.TAB_WEEKLY);
				}
				else if ( new Long(2).equals(restr.getBilltoRuletype().getBilltoRuletypeCd())) {
					helpForm.setTab(BilltoSelectionRhythmForm.TAB_DAYOFMONTH);
				}
			}
			else {
				helpForm.setTab(BilltoSelectionRhythmForm.TAB_MONTHLY);
			}
			
			if ( rules == null) {
				return result = mapping.findForward("success");
			}
			
			ListIterator rulesIt = rules.listIterator();
			
			// check relevant checboxes according to the rules and
			// build a map of these existing rules, so at save we can now which rules are already in DB
			
			while ( rulesIt.hasNext()) {
				ShiptoSareaRestrBtrulEntity rule = (ShiptoSareaRestrBtrulEntity) rulesIt.next();
				
				Long sapBpId = rule.getBilltoSapBp().getSapBpId();
				Object object = billtoIndexMap.get(sapBpId);
				
				if (object != null) {
					Integer intIndex =  (Integer) object;
					int ruleBilltoIndex = intIndex.intValue();
			
					Long ruleType = rule.getBilltoRuletype().getBilltoRuletypeCd();
					Long ruleValue = rule.getValue();
			
					List checkboxList;
					int multiplier;
			
					if ( ruleType.intValue() == 0) {
						checkboxList = helpForm.getMonthCheckboxList();
						multiplier = BilltoSelectionRhythmForm.NUM_OF_MONTHS;
					}
					else if ( ruleType.intValue() == 1 ) {
						checkboxList = helpForm.getWeekCheckboxList();
						multiplier = BilltoSelectionRhythmForm.NUM_OF_DAYS_IN_WEEK;
					}
					else {
						checkboxList = helpForm.getDayOfMonthCheckboxList();
						multiplier = BilltoSelectionRhythmForm.NUM_OF_DAYS_IN_MONTH;
					}
					checkboxList.set(ruleBilltoIndex * multiplier + ruleValue.intValue(), "true");
			
					String key = sapBpId.toString() + '-' + rule.getBilltoRuletype().getBilltoRuletypeCd().toString() + '-' + rule.getValue().toString();
					helpForm.getExistingRulesMap().put(key, rule);
				}
			}
			
			return result = mapping.findForward("success");
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
