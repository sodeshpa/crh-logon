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

import com.crh.logon.business.services.utils.BimodalRailOrderWrapper;
import com.crh.logon.order.web.forms.bimodalrailcarmonitoring.BimodalRailcarMonitoringForm;

/**
 * Used for rendering rowspan attribute in normal html tables.
 * 
 * If according to certain attribute (here railway station id) two rows which are next to each other are identical
 * no td tag will be rendered for the second, third... etc. but at the first one a rowspan= number
 * of next identical will be shown, and therefore the table rendered will show the same attribute in
 * a common column for the identical rows. 
 * 
 * @author AdNovum Informatik AG
 */
public class BimodalRailcarMonitoringRowSpanTag extends TagSupport { 
	
	private static final String C_NAME = 
		BimodalRailcarMonitoringRowSpanTag.class.getName();

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(C_NAME);
	
	private static final String SKIP_INDEX_LIST = "skipRailwayStationList";
	
	private int itemIndex;
	
	private String printInTd = "";
	
	private boolean printTdTag = true;
	
	public int doStartTag() throws JspException {
		int rowSpan = 1;
		int result = 0;
		BimodalRailcarMonitoringForm form = (BimodalRailcarMonitoringForm) pageContext.getSession().getAttribute(BimodalRailcarMonitoringForm.SESSION_FORM_NAME);
		if (form == null){
			startColumn(rowSpan);
			return EVAL_BODY_INCLUDE;
		}
		List <BimodalRailOrderWrapper> resultList = form.getResultList();		


		Object obj = pageContext.getAttribute(SKIP_INDEX_LIST);
		BimodalRailOrderWrapper terminalItem = (BimodalRailOrderWrapper) resultList.get(itemIndex);

		List list = null;
		if (obj != null){
			list = (List) obj;
		} else{
			list= new ArrayList();
			pageContext.setAttribute(SKIP_INDEX_LIST, list);
		}
		if (list.contains(Integer.valueOf(itemIndex))){				
			//do not print anything
			return SKIP_BODY;
		}				
		if (printTdTag){				
			while ( itemIndex + 1 < resultList.size() && resultList.get(itemIndex + 1) != null && ((BimodalRailOrderWrapper) resultList.get(itemIndex + 1)).getEntity().getRailwayStationId() != null &&((BimodalRailOrderWrapper) resultList.get(itemIndex + 1)).getEntity().getRailwayStationId().equals(terminalItem.getEntity().getRailwayStationId() )) {
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
			pageContext.getOut().write("<td rowspan=\"" + Integer.toString(rowSpan ) + "\" " + (printInTd != null ? printInTd : "" ) + (rowSpan != 1 ? " style=\"vertical-align:middle;\"" : "") + ">");
		}
		catch (IOException e) {
			LOG.error("Error in " + BimodalRailcarMonitoringRowSpanTag.C_NAME);
		}
	}
	
	public int doEndTag(){
		int i = EVAL_PAGE;
		try {
			Object obj = null;
			obj = pageContext.getAttribute(SKIP_INDEX_LIST);
			if (obj != null) {				
				List list = (List) obj;
				if (printTdTag && list != null && !list.contains(Integer.valueOf(itemIndex))) {				
					pageContext.getOut().write("</td>");
				}
			}
		}
		catch (IOException e) {
			LOG.error("Error in " + BimodalRailcarMonitoringRowSpanTag.C_NAME);
		}
		return i;
	}


	public void setItemIndex(Integer itemIndex) {
		if (itemIndex == null) {
			this.itemIndex = 0;
		}
		else{			
			this.itemIndex = Integer.valueOf(itemIndex).intValue();
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
