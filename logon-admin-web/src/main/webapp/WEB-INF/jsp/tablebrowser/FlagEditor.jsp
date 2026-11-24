<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<!-- BEGIN $RCSfile: FlagEditor.jsp,v $ -->

<%@ include file="../includeTLDs.jsp" %>

<html:select size="1" property="value[${valueNr}]"
	value="${value.webValue}">
	<c:if test='${nullable}'>
		<html:option value="">-</html:option>
	</c:if>
	<html:option value="0" />
	<html:option value="1" />
</html:select>