/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.dispokarte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.forms.dispokarte.DispokarteForm;
import com.crh.logon.order.web.utils.comparator.BilltoComparator;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.order.web.forms.dispokarte.BilltoSelectionRhythmForm;
import com.crh.logon.persistence.entities.common.ShiptoBilltoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrBtrulEntity;
import com.crh.logon.persistence.entities.refdata.BilltoRuletypeBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.web.utils.Authorization;

/**
 * @author AdNovum Informatik AG
 *
 * @struts.action name="order_dispokarte_BilltoSelectionRhythmForm"
 *  path="/dispokarte/BilltoSelectionRhythmAction" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="continue" path="BilltoSelectionRhythmTile.def"
 * @struts.action-forward name="close" path="BilltoSelectionRhythmCloseTile.def"
 * * 
 */
public class BilltoSelectionRhythmAction extends OrderActionSupport {

	private static final int RULE_TYPE_MONTH = 0;
	private static final int RULE_TYPE_DAY_OF_WEEK = 1;
	private static final int RULE_TYPE_DAY_OF_MONTH = 2;
	
	private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(BilltoSelectionRhythmAction.class);

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
				String forwardName = "continue";
				BilltoSelectionRhythmForm helpForm = (BilltoSelectionRhythmForm) form;
				DispokarteForm dispoForm = (DispokarteForm) request.getSession().getAttribute(DispokarteForm.FORM_NAME);
				
				if (request.getParameter("save") != null) {
					
					if ( helpForm.getCurrentShiptoSareaRestr().getRules() == null) {
						helpForm.getCurrentShiptoSareaRestr().setRules(new ArrayList());
					}
					
					List billtoList = dispoForm.getCurrSalesAreaBean().getShiptoSarea().getBilltos();
					Collections.sort(billtoList, new BilltoComparator());
			//		if ( helpForm.TAB_MONTHLY.equals( helpForm.getTab()) ) {
						List monthList = helpForm.getMonthCheckboxList();
						ListIterator monthIt = monthList.listIterator();
						
						handleRulePersisting(
								monthIt, 
								helpForm, 
								billtoList, 
								BilltoSelectionRhythmForm.NUM_OF_MONTHS, 
								BilltoSelectionRhythmForm.RULE_TYPE_MONTH);
				//	}
				//					else if ( helpForm.TAB_WEEKLY.equals( helpForm.getTab()) ) {
						List weekList = helpForm.getWeekCheckboxList();
						ListIterator weekIt = weekList.listIterator();
						
						handleRulePersisting(
								weekIt, 
								helpForm, 
								billtoList, 
								BilltoSelectionRhythmForm.NUM_OF_DAYS_IN_WEEK, 
								BilltoSelectionRhythmForm.RULE_TYPE_DAYS_OF_WEEK);
	//				}
		//			else if ( helpForm.TAB_DAYOFMONTH.equals( helpForm.getTab()) ) {
						List daysOfMonthList = helpForm.getDayOfMonthCheckboxList();;
						ListIterator daysOfMonthIt = daysOfMonthList.listIterator();
						
						handleRulePersisting(
								daysOfMonthIt, 
								helpForm, 
								billtoList, 
								BilltoSelectionRhythmForm.NUM_OF_DAYS_IN_MONTH, 
								BilltoSelectionRhythmForm.RULE_TYPE_DAYS_IN_MONTH);
			//		}
					
					if ( BilltoSelectionRhythmForm.TAB_MONTHLY.equals( helpForm.getTab()) ) {
						helpForm.getCurrentShiptoSareaRestr().setBilltoRuletype(createBilltoRuleType(RULE_TYPE_MONTH));
					}
					else if ( BilltoSelectionRhythmForm.TAB_WEEKLY.equals( helpForm.getTab()) ) {
						helpForm.getCurrentShiptoSareaRestr().setBilltoRuletype(createBilltoRuleType(RULE_TYPE_DAY_OF_WEEK));
					}
					else if ( BilltoSelectionRhythmForm.TAB_DAYOFMONTH.equals( helpForm.getTab()) ) {
						helpForm.getCurrentShiptoSareaRestr().setBilltoRuletype(createBilltoRuleType(RULE_TYPE_DAY_OF_MONTH));
					}
					
					// remove the rules from the DB, which were there before but now their checkbox has been unchecked. 
					
					/*Iterator removeIt = helpForm.getExistingRulesMap().keySet().iterator();
					ArrayList listToRemove = new ArrayList();
					
					while ( removeIt.hasNext() ) {
						listToRemove.add( helpForm.getExistingRulesMap().get( removeIt.next() ) );				
					}*/
						//String key = (String) removeIt.next();
						//ShiptoVkoRestrBtRuleEntity ruleToRemove = (ShiptoVkoRestrBtRuleEntity) helpForm.getExistingRulesMap().get(key);
						
				//						ShiptoSareaRestrBulkEntity restr = helpForm.getCurrentShiptoSareaRestrBulk();
				//						List rules = restr.getRules();
				//						
				//						ListIterator rulesIt = rules.listIterator();
				//
				//						List indexToRemove = new ArrayList();
				//						int rulesIndex = -1;
				//						while ( rulesIt.hasNext()) {
				//							rulesIndex++;
				//							ShiptoSareaRestrBtrulEntity rule = (ShiptoSareaRestrBtrulEntity) rulesIt.next();
				//							String ruleKey = rule.getBilltoSapBp().getSapBpId().toString() + '-' + rule.getBilltoRuletype().getBilltoRuletypeCd().toString() + '-' + rule.getValue().toString();
				//							if ( helpForm.getExistingRulesMap().containsKey(ruleKey) ) {
				//								indexToRemove.add(new Integer(rulesIndex));
				//							}
				//						}
				//						
				//						//rules.remove( listToRemove );
				//						
				//						ListIterator removeIt = indexToRemove.listIterator();
				//						int correction = 0;
				//						while (removeIt.hasNext()) {
				//							rules.remove( ((Integer) removeIt.next()).intValue() + correction-- );
				//						}
						
						//dispoService.removeRestrictionRule(ruleToRemove);
					//}
					
					forwardName = "close";
				}
				
				
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
		
		private void handleRulePersisting (ListIterator iterator, BilltoSelectionRhythmForm helpForm, List billtoList, int numOfPeriods, int ruleType) {
			int counter = -1;
			while ( iterator.hasNext() ) {
				counter++;
				boolean isSelected = new Boolean((String) iterator.next());
				
				int value = counter % numOfPeriods;
				int bpIndex = counter / numOfPeriods;
				SapBpEntity sapBp = ((ShiptoBilltoEntity) billtoList.get(bpIndex)).getBilltoSapBp();
				String key = sapBp.getSapBpId().toString() + '-' + ruleType + '-' + value;
				if ( isSelected ) {
					if ( ! helpForm.getExistingRulesMap().containsKey(key) ) {
						ShiptoSareaRestrBtrulEntity rule = new ShiptoSareaRestrBtrulEntity();
						rule.setBilltoRuletype(createBilltoRuleType(ruleType));
						rule.setBilltoSapBp(sapBp);
						rule.setValue(new Long(value));
						rule.setShiptoSareaRestr(helpForm.getCurrentShiptoSareaRestr());
						
						helpForm.getCurrentShiptoSareaRestr().getRules().add(rule);
		
						helpForm.getExistingRulesMap().put(key, rule);
					}
				}
				else {
					if ( helpForm.getExistingRulesMap().containsKey(key) ) {										
						helpForm.getExistingRulesMap().remove(key);
						removeFromRules(ruleType, sapBp, value, helpForm.getCurrentShiptoSareaRestr().getRules());
					}
				}
			}
		}

		private void removeFromRules(int ruleType, SapBpEntity sapBp, int value, List rules) {
			if (rules != null && !rules.isEmpty()) {
				Iterator it = rules.iterator();
				while (it.hasNext()) {
					ShiptoSareaRestrBtrulEntity object = (ShiptoSareaRestrBtrulEntity) it.next();
					if (object.getBilltoRuletype().getBilltoRuletypeCd().equals(Long.valueOf(ruleType)) &&
							object.getBilltoSapBp().getSapBpId().equals(sapBp.getSapBpId()) &&
							object.getValue().equals(Long.valueOf(value))) {
						it.remove();
						break;
					}
				}
			}
		}

		private BilltoRuletypeBsEntity createBilltoRuleType (int ruleType) {
			BilltoRuletypeBsEntity ruletype = new BilltoRuletypeBsEntity();
			ruletype.setCtlActCd(true);
			ruletype.setBilltoRuletypeCd(new Long(ruleType));
			return ruletype;
		}
	
}
