/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.DateTimeAwareOrderWrapper;

/**
 * Prints out the body in a td tag in case the order id of the current OrderCreateEntity does not correspond with the previous entity in the list. 
 * Skips the body in case the id is the same.
 * @author AdNovum Informatik AG
 */
public class OrderRowSpan extends TagSupport { 
	
	private static final String C_NAME = 
		OrderRowSpan.class.getName();

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(C_NAME);
	private static final String SKIP_INDEX_LIST_ORDER_COLUMN = "skipAssignmentList";
	
	private List pageContextOrderList;
	private String pageContextOrderCreateEntityName = "";
	private String pageContextIndexName = "";
	
	private String printInTd = "";
	
	private boolean printTdTag = true;
	
	public int doStartTag() throws JspException {
		int rowSpan = 1;
		int result = 0;

		int itemIndex = (Integer)pageContext.getAttribute(pageContextIndexName);
		
		DateTimeAwareOrderWrapper orderItem = getOrderCreateEntityByIndex(itemIndex);

		Object obj = pageContext.getAttribute(SKIP_INDEX_LIST_ORDER_COLUMN);
		List list = null;
		if (obj != null){
			list = (List) obj;
		} else{
			list= new ArrayList();
			pageContext.setAttribute(SKIP_INDEX_LIST_ORDER_COLUMN, list);
		}

		if (list.contains(Integer.valueOf(itemIndex))){				
			//do not print anything
			return SKIP_BODY;
		}
		if (printTdTag){
			while ( itemIndex + 1 < getPageContentOrderListSize()
					&& getOrderCreateEntityByIndex(itemIndex + 1) != null
					&& (getOrderCreateEntityByIndex(itemIndex + 1)).getOrderId() != null
					&& (getOrderCreateEntityByIndex(itemIndex + 1)).getOrderId().equals(orderItem.getOrderId() )) {
				rowSpan++;
				itemIndex++;
				list.add(Integer.valueOf(itemIndex));
			}
			startColumn(rowSpan);
		}
		
		return result = EVAL_BODY_INCLUDE;
	}

	/**
	 * 
	 */
	private void startColumn(int rowSpan) {
		try {
			pageContext.getOut().write("<td rowspan=\"" + Integer.toString(rowSpan ) + "\"  " +  printInTd  + ">");
		}
		catch (IOException e) {
			LOG.error("Error in " + OrderRowSpan.C_NAME);
		}
	}

	protected DateTimeAwareOrderWrapper getOrderCreateEntityByIndex(int index) {
		return (DateTimeAwareOrderWrapper) getPageContextOrderList().get(index);
	}

	protected int getPageContentOrderListSize() {
		return getPageContextOrderList().size();
	}
	
	public int doEndTag(){
		//it is not even dispatched, no chance to be dispatched together with the next row
		int i = EVAL_PAGE;
		int itemIndex = (Integer)pageContext.getAttribute(pageContextIndexName);
		Object obj = pageContext.getAttribute(SKIP_INDEX_LIST_ORDER_COLUMN);
		List list = (List) obj;
		try {
			if (printTdTag && list != null && !list.contains(Integer.valueOf(itemIndex))) {				
				pageContext.getOut().write("</td>");
			}
		}
		catch (IOException e) {
			LOG.error("Error in " + OrderRowSpan.C_NAME);
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


	public String getPageContextOrderCreateEntityName() {
		return pageContextOrderCreateEntityName;
	}

	public void setPageContextOrderCreateEntityName(String pageContextOrderCreateEntityName) {
		this.pageContextOrderCreateEntityName = pageContextOrderCreateEntityName;
	}

	public String getPageContextIndexName() {
		return pageContextIndexName;
	}

	public void setPageContextIndexName(String pageContextIndexName) {
		this.pageContextIndexName = pageContextIndexName;
	}

	public boolean isPrintTdTag() {
		return printTdTag;
	}

	public void setPrintTdTag(boolean printTdTag) {
		this.printTdTag = printTdTag;
	}

	public List getPageContextOrderList() {
		return pageContextOrderList;
	}

	public void setPageContextOrderList(List pageContextOrderList) {
		this.pageContextOrderList = pageContextOrderList;
	}
}
