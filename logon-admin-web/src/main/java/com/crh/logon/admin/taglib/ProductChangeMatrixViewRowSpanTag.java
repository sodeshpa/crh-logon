/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.jsp.tagext.TagSupport;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.admin.web.forms.masterdata.ProductChangeMatrixViewForm;
import com.crh.logon.persistence.entities.views.MaterialCompatEntity;

/**
 * Used to determine whether functionality is available for a given order.
 *
 * @author AdNovum Informatik AG
 */
public class ProductChangeMatrixViewRowSpanTag extends TagSupport {

	private static final String C_NAME = ProductChangeMatrixViewRowSpanTag.class.getName();

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(C_NAME);

	private static final String SKIP_INDEX_LIST = "skipList";

	private int itemIndex;

	private boolean printTdTag = true;

	@Override
	public int doStartTag() {
		int rowSpan = 1;
		ProductChangeMatrixViewForm form = (ProductChangeMatrixViewForm) pageContext.getSession()
				.getAttribute(ProductChangeMatrixViewForm.SESSION_PRODUCT_CHANGE_MATRIX_VIEW_FORM_NAME);
		if (form == null) {
			startColumn(rowSpan);
			return EVAL_BODY_INCLUDE;
		}
		List resultList = form.getCompats();


		MaterialCompatEntity compat = (MaterialCompatEntity) resultList.get(itemIndex);

		Object obj = pageContext.getAttribute(SKIP_INDEX_LIST);
		List list;
		if (obj != null) {
			list = (List) obj;
		}
		else {
			list = new ArrayList();
			pageContext.setAttribute(SKIP_INDEX_LIST, list);
		}

		if (list.contains(itemIndex)) {
			//do not print anything
			return SKIP_BODY;
		}
		if (printTdTag) {
			while (itemIndex + 1 < resultList.size() && resultList.get(itemIndex + 1) != null
					&& ((MaterialCompatEntity) resultList.get(itemIndex + 1)).getFromSapMaterialId() != null
					&& ((MaterialCompatEntity) resultList.get(itemIndex + 1)).getFromSapMaterialId()
					.equals(compat.getFromSapMaterialId())) {
				rowSpan++;
				itemIndex++;
				list.add(itemIndex);
			}
			startColumn(rowSpan);
		}
		return EVAL_BODY_INCLUDE;
	}

	private void startColumn(int rowSpan) {
		try {
			pageContext.getOut()
					.write("<td rowspan=\"" + rowSpan + "\"  " + (rowSpan != 1 ? "style=\"vertical-align:middle;\"" : "") + ">");
		}
		catch (IOException e) {
			LOG.error("Error in " + ProductChangeMatrixViewRowSpanTag.C_NAME, e);
		}
	}

	@Override
	public int doEndTag() {
		try {
			pageContext.getOut().write("</td>");
		}
		catch (IOException e) {
			LOG.error("Error in " + ProductChangeMatrixViewRowSpanTag.C_NAME, e);
		}
		return EVAL_PAGE;
	}

	public void setItemIndex(Integer itemIndex) {
		if (itemIndex == null) {
			this.itemIndex = 0;
		}
		else {
			this.itemIndex = itemIndex;
		}
	}

	public void setPrintTdTag(Boolean printTdTag) {
		if (printTdTag == null) {
			this.printTdTag = false;
		}
		else {
			this.printTdTag = printTdTag;
		}
	}
}
