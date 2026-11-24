/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.terminal.taglib.bagterminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.crh.logon.persistence.entities.views.BagTerminalEntity;
import com.crh.logon.terminal.web.forms.BagTerminalOrderDTO;
import com.crh.logon.terminal.web.forms.bagterminal.BagTerminalForm;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * Prints out the body in a td tag in case the order id of the current BagTerminalEntity does not correspond with the previous entity in the list. 
 * Skips the body in case the id is the same. 
 * @author AdNovum Informatik AG
 */
public class BagTerminalOrderRowSpan extends TagSupport { 
	
	private static final String C_NAME = 
		BagTerminalOrderRowSpan.class.getName();

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(C_NAME);
	private static final String SKIP_INDEX_LIST_ORDER_COLUMN = "skipAssignmentList";
	
	private boolean printTdTag = true;
	private String printInTd = "";
	
	public int doStartTag() throws JspException {
		int rowSpan = 1;
		HttpSession session = pageContext.getSession();
		BagTerminalForm form = (BagTerminalForm) session.getAttribute(BagTerminalForm.FORM_NAME);
		if (form == null) {
			startColumn(rowSpan);
			return EVAL_BODY_INCLUDE;
		}
		List<BagTerminalOrderDTO> resultList = form.getOrderDTOList();
		int terminalItemIndex = (Integer)pageContext.getAttribute("i");
		
		BagTerminalOrderDTO firstBagTerminalOrderDTO = resultList.get(terminalItemIndex);
		BagTerminalEntity referenceBagTerminalOrder = firstBagTerminalOrderDTO.getTerminalOrderEntity();

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
			Long referenceAssignmentId = referenceBagTerminalOrder.getAssignmentId();
			while (isAssignmentIdTheSame(resultList, terminalItemIndex, referenceAssignmentId)) {
				rowSpan++;
				terminalItemIndex++;
				list.add(Integer.valueOf(terminalItemIndex));
			}
			startColumn(rowSpan);
		}
		
		return EVAL_BODY_INCLUDE;
	}

	private boolean isAssignmentIdTheSame(List<BagTerminalOrderDTO> resultList,
			int terminalItemIndex, Long referenceAssignmentId) {
		int nextIndex = terminalItemIndex + 1;
		if (nextIndex >= resultList.size()) {
			return false;
		}
		BagTerminalOrderDTO nextBagTerminalOrderDTO = resultList.get(nextIndex);
		if (nextBagTerminalOrderDTO == null) {
			return false;
		}
		BagTerminalEntity nextBagTerminalOrderEntity = nextBagTerminalOrderDTO.getTerminalOrderEntity();
		Long nextAssignmentId = nextBagTerminalOrderEntity.getAssignmentId();
		
		return nextAssignmentId != null && nextAssignmentId.equals(referenceAssignmentId);
	}

	/**
	 * 
	 */
	private void startColumn(int rowSpan) {
		try {
			pageContext.getOut().write("<td rowspan=\"" + Integer.toString(rowSpan ) + "\"  " + printInTd  + ">");
		}
		catch (IOException e) {
			LOG.error("Error in " + BagTerminalOrderRowSpan.C_NAME);
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
			LOG.error("Error in " + BagTerminalOrderRowSpan.C_NAME);
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
