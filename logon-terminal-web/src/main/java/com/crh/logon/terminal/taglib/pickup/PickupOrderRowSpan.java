/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.terminal.taglib.pickup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.crh.logon.persistence.entities.views.PickupTerminalEntity;
import com.crh.logon.terminal.web.forms.pickup.PickupForm;
import com.crh.logon.terminal.web.forms.PickupTerminalOrderDTO;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * Prints out the body in a td tag in case the order id of the current PickupTerminalEntity does not correspond with the previous entity in the list. 
 * Skips the body in case the id is the same.
 * @author AdNovum Informatik AG
 */
public class PickupOrderRowSpan extends TagSupport { 
	
	private static final String C_NAME = 
		PickupOrderRowSpan.class.getName();

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(C_NAME);
	private static final String SKIP_INDEX_LIST_ORDER_COLUMN = "skipAssignmentList";
	
	private boolean printTdTag = true;
	private String printInTd = "";
	
	public int doStartTag() throws JspException {
		int rowSpan = 1;
		HttpSession session = pageContext.getSession();
		PickupForm form = (PickupForm) session.getAttribute(PickupForm.FORM_NAME);
		if (form == null) {
			startColumn(rowSpan);
			return EVAL_BODY_INCLUDE;
		}
		List<PickupTerminalOrderDTO> resultList = form.getOrderDTOList();
		int terminalItemIndex = (Integer)pageContext.getAttribute("i");
		
		PickupTerminalOrderDTO referencePickupTerminalOrderDTO = resultList.get(terminalItemIndex);
		PickupTerminalEntity referencePickupTerminalOrder = referencePickupTerminalOrderDTO.getTerminalOrderEntity();

		Object obj = pageContext.getAttribute(SKIP_INDEX_LIST_ORDER_COLUMN);
		List list = null;
		if (obj != null) {
			list = (List) obj;
		}
		else {
			list= new ArrayList();
			pageContext.setAttribute(SKIP_INDEX_LIST_ORDER_COLUMN, list);
		}

		if (list.contains(Integer.valueOf(terminalItemIndex))) {
			//do not print anything
			return SKIP_BODY;
		}
		if (printTdTag) {
			Long referenceOrderId = referencePickupTerminalOrder.getOrderId();
			while (isOrderIdTheSame(resultList, terminalItemIndex, referenceOrderId)) {
				rowSpan++;
				terminalItemIndex++;
				list.add(Integer.valueOf(terminalItemIndex));
			}
			startColumn(rowSpan);
		}
		
		return EVAL_BODY_INCLUDE;
	}

	private boolean isOrderIdTheSame(List<PickupTerminalOrderDTO> resultList,
			int terminalItemIndex, Long referenceOrderId) {
		int nextIndex = terminalItemIndex + 1;
		int size = resultList.size();
		if (nextIndex >= size) {
			return false;
		}
		PickupTerminalOrderDTO nextPickupTerminalOrderDTO = resultList.get(nextIndex);
		if (nextPickupTerminalOrderDTO == null) {
			return false;
		}
		PickupTerminalEntity nextPickupTerminalEntity = nextPickupTerminalOrderDTO.getTerminalOrderEntity();
		Long nextOrderId = nextPickupTerminalEntity.getOrderId();

		return nextOrderId != null
				&& nextOrderId.equals(referenceOrderId);
	}

	/**
	 * 
	 */
	private void startColumn(int rowSpan) {
		try {
			pageContext.getOut().write("<td rowspan=\"" + Integer.toString(rowSpan ) + "\"  " +  printInTd + ">");
		}
		catch (IOException e) {
			LOG.error("Error in " + PickupOrderRowSpan.C_NAME);
		}
	}
	
	public int doEndTag(){
		//it is not even dispatched, no chance to be dispatched together with the next row
		int i = EVAL_PAGE;
		int terminalItemIndex = (Integer)pageContext.getAttribute("i");
		Object obj = pageContext.getAttribute(SKIP_INDEX_LIST_ORDER_COLUMN);
		List list = (List) obj;
		try {
			if (printTdTag && list != null && !list.contains(Integer.valueOf(terminalItemIndex))) {				
				pageContext.getOut().write("</td>");
			}
		}
		catch (IOException e) {
			LOG.error("Error in " + PickupOrderRowSpan.C_NAME);
		}
		return i;
	}

	public void setPrintTdTag(Boolean printTdTag) {
		if (printTdTag == null) {
			this.printTdTag = false;
		}
		else{			
			this.printTdTag = Boolean.valueOf(printTdTag).booleanValue();
		}
	}

	public String getPrintInTd() {
		return printInTd;
	}

	public void setPrintInTd(String printInTd) {
		this.printInTd = printInTd;
	}
}
