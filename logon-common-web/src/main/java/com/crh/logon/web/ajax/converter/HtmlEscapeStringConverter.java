/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.ajax.converter;

import org.directwebremoting.convert.StringConverter;
import org.directwebremoting.dwrp.SimpleOutboundVariable;
import org.directwebremoting.extend.MarshallException;
import org.directwebremoting.extend.OutboundContext;
import org.directwebremoting.extend.OutboundVariable;
import org.directwebremoting.util.JavascriptUtil;
import org.springframework.web.util.HtmlUtils;

/**
 * @author AdNovum Informatik AG
 * 
 * This is an DWR type converter. Escape all HTML and JavaScript specific characters. 
 */
public class HtmlEscapeStringConverter extends StringConverter {
	@Override
	public OutboundVariable convertOutbound(Object data, OutboundContext outctx)
			throws MarshallException {
		String escaped = HtmlUtils.htmlEscape(JavascriptUtil.escapeJavaScript(data.toString()));
		return new SimpleOutboundVariable('"' + escaped + '"', outctx, true);
	}
}
