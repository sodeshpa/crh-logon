/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.order.web.forms.auftrag.AuftragBearbeitungForm;
import com.crh.logon.persistence.entities.views.OrdItemAssignmtLoadEntity;

/**
 * Used to determine whether functionality is available for a given order.
 * @author AdNovum Informatik AG
 */
public class OrderSimpleChangeRowSpan extends TagSupport { 
	
	private static final String C_NAME = 
		OrderSimpleChangeRowSpan.class.getName();

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(C_NAME);
	private static final String SKIP_INDEX_LIST_ASSIGNMENT_COLUMN = "skipAssignmentList";
	private static final String SKIP_INDEX_LIST_ORDER_COLUMN = "skipOrderList";
	private static final String SKIP_INDEX_LIST_LOAD_COLUMN = "skipLoadList";
	  
	private String checkIdentityAccording;
	
	private int terminalItemIndex;
	
	private String printInTd = "";
	
	private boolean printTdTag = true;
	
	public int doStartTag() throws JspException {
		int rowSpan = 1;
		int result = 0;
		AuftragBearbeitungForm form = (AuftragBearbeitungForm) pageContext.getSession().getAttribute("order_auftrag_AuftragBearbeitungForm");
		if (form == null){
			startColumn(rowSpan);
			return EVAL_BODY_INCLUDE;
		}
		List resultList = form.getOrderList();		


		if (checkIdentityAccording.equals(OrdItemAssignmtLoadEntity.ATTR_ASSIGNMENT_ID)){
			OrdItemAssignmtLoadEntity terminalItem = (OrdItemAssignmtLoadEntity) resultList.get(terminalItemIndex);
			if (terminalItem.getAssignmentId() == null) {
				if (printTdTag){				
					startColumn(rowSpan);
				}
				return EVAL_BODY_INCLUDE;
			}
			
			Object obj = pageContext.getAttribute(SKIP_INDEX_LIST_ASSIGNMENT_COLUMN);
			List list = null;
			if (obj != null){
				 list = (List) obj;
			} else{
				list= new ArrayList();
				pageContext.setAttribute(SKIP_INDEX_LIST_ASSIGNMENT_COLUMN, list);
			}

			if (list.contains(Integer.valueOf(terminalItemIndex))){				
				//do not print anything
				return SKIP_BODY;
			}
			if (printTdTag){
				while ( terminalItemIndex + 1 < resultList.size() && resultList.get(terminalItemIndex + 1) != null && ((OrdItemAssignmtLoadEntity) resultList.get(terminalItemIndex + 1)).getAssignmentId() != null &&((OrdItemAssignmtLoadEntity) resultList.get(terminalItemIndex + 1)).getAssignmentId().equals(terminalItem.getAssignmentId() )) {
					rowSpan++;
					terminalItemIndex++;
					list.add(Integer.valueOf(terminalItemIndex));
				}
				startColumn(rowSpan);
			}
		}
		if (checkIdentityAccording.equals(OrdItemAssignmtLoadEntity.ATTR_LOAD_ID)){
			OrdItemAssignmtLoadEntity terminalItem = (OrdItemAssignmtLoadEntity) resultList.get(terminalItemIndex);
			if (terminalItem.getLoadId() == null) {
				if (printTdTag){				
					startColumn(rowSpan);
				}
				return EVAL_BODY_INCLUDE;
			}
			
			Object obj = pageContext.getAttribute(SKIP_INDEX_LIST_LOAD_COLUMN);
			List list = null;
			if (obj != null){
				 list = (List) obj;
			} else{
				list= new ArrayList();
				pageContext.setAttribute(SKIP_INDEX_LIST_LOAD_COLUMN, list);
			}

			if (list.contains(Integer.valueOf(terminalItemIndex))){				
				//do not print anything
				return SKIP_BODY;
			}
			if (printTdTag){
				while ( terminalItemIndex + 1 < resultList.size() && resultList.get(terminalItemIndex + 1) != null && ((OrdItemAssignmtLoadEntity) resultList.get(terminalItemIndex + 1)).getLoadId() != null &&((OrdItemAssignmtLoadEntity) resultList.get(terminalItemIndex + 1)).getLoadId().equals(terminalItem.getLoadId() )) {
					rowSpan++;
					terminalItemIndex++;
					list.add(Integer.valueOf(terminalItemIndex));
				}
				startColumn(rowSpan);
			}
		}		
		else if (checkIdentityAccording.equals(OrdItemAssignmtLoadEntity.ATTR_ORDER_ID)){
			Object obj = pageContext.getAttribute(SKIP_INDEX_LIST_ORDER_COLUMN);
			OrdItemAssignmtLoadEntity terminalItem = (OrdItemAssignmtLoadEntity) resultList.get(terminalItemIndex);

			List list = null;
			if (obj != null){
				list = (List) obj;
			} else{
				list= new ArrayList();
				pageContext.setAttribute(SKIP_INDEX_LIST_ORDER_COLUMN, list);
			}
			if (list.contains(Integer.valueOf(terminalItemIndex))){				
				//do not print anything
				return SKIP_BODY;
			}				
			if (printTdTag){				
				while ( terminalItemIndex + 1 < resultList.size() && resultList.get(terminalItemIndex + 1) != null && ((OrdItemAssignmtLoadEntity) resultList.get(terminalItemIndex + 1)).getOrderId() != null &&((OrdItemAssignmtLoadEntity) resultList.get(terminalItemIndex + 1)).getOrderId().equals(terminalItem.getOrderId() )) {
					rowSpan++;
					terminalItemIndex++;
					list.add(Integer.valueOf(terminalItemIndex));

				}
				startColumn(rowSpan);
			}
		}		
		return result = EVAL_BODY_INCLUDE;
	}

	/**
	 * 
	 */
	private void startColumn(int rowSpan) {
		try {
			pageContext.getOut().write("<td rowspan=\"" + Integer.toString(rowSpan ) + "\" " + (printInTd != null ? printInTd : "" ) + (rowSpan != 1 ? " style=\"vertical-align:middle;\"" : "") + ">");
		}
		catch (IOException e) {
			LOG.error("Error in " + OrderSimpleChangeRowSpan.C_NAME);
		}
	}
	
	public int doEndTag(){
		//it is not even dispatched, no chance to be dispatched together with the next row
		int i = EVAL_PAGE;
		try {
			Object obj = null;
			if (checkIdentityAccording.equals(OrdItemAssignmtLoadEntity.ATTR_LOAD_ID)){
				
				obj = pageContext.getAttribute(SKIP_INDEX_LIST_ORDER_COLUMN);
			}
			else if (checkIdentityAccording.equals(OrdItemAssignmtLoadEntity.ATTR_ORDER_ID)){
				
				obj = pageContext.getAttribute(SKIP_INDEX_LIST_LOAD_COLUMN);
			}
			if (checkIdentityAccording.equals(OrdItemAssignmtLoadEntity.ATTR_ASSIGNMENT_ID)){
				obj = pageContext.getAttribute(SKIP_INDEX_LIST_ASSIGNMENT_COLUMN);
			}
			if (obj != null) {				
				List list = (List) obj;
				if (printTdTag && list != null && !list.contains(Integer.valueOf(terminalItemIndex))) {				
					pageContext.getOut().write("</td>");
				}
			}
		}
		catch (IOException e) {
			LOG.error("Error in " + OrderSimpleChangeRowSpan.C_NAME);
		}
		return i;
	}

	public void setCheckIdentityAccording(String checkIdentityAccording) {
		this.checkIdentityAccording = checkIdentityAccording;
	}

	public void setTerminalItemIndex(Integer terminalItemIndex) {
		if (terminalItemIndex == null) {
			this.terminalItemIndex = 0;
		}
		else{			
			this.terminalItemIndex = Integer.valueOf(terminalItemIndex).intValue();
		}
	}

	public void setPrintTdTag(Boolean printTdTag) {
		if (printTdTag == null) {
			this.printTdTag = false;
		}
		else{			
			this.printTdTag = Boolean.valueOf(printTdTag).booleanValue();
		}
	}

	public void setPrintInTd(String printInTd) {
		this.printInTd = printInTd;
	}
}
