<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>

<jsp:include page="/Page_header.jsp"/>

<%-- BEGIN $RCSfile: $ --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://com.crh.logon/tags/fmt.tld" prefix="fmt" %>
<fmt:setBundle basename="LitDict"/>

	<tr><td>
		<div class="error"><fmt:message key="application.login.incativity" /></div>
			<table align="center" cellpadding="0" cellspacing="0" border="0" width="100%" style="padding:0px; margin:0px;">
                <tr>    
                    <td align="center">
        				<a href="<c:out value='${pageContext.request.contextPath}'/>">Log in</a>
        			</td>
        		</tr>
            </table>
        </td></tr>
<%-- END $RCSfile: $ --%>

<jsp:include page="/Page_footer.jsp"/>
