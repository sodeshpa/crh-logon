<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://com.crh.logon/tags/fmt.tld" prefix="fmt" %>
<%@ taglib uri="http://com.crh.logon/tags/common-taglib.tld" prefix="crh-common" %>
<fmt:setBundle basename="LitDict"/>

<jsp:include page="/Page_header.jsp">
    <jsp:param name="reloadInactiveSession" value="true"/>
</jsp:include>

<script type="text/javascript" src="<crh-common:url value="/js/adapter/ext/ext-base.js"/>"></script>
<script type="text/javascript" src="<crh-common:url value="/js/ext-all.js"/>"></script>
<script type="text/javascript" src="<crh-common:url value="/js/logon-password.js"/>"></script>

<%-- BEGIN $RCSfile: $ --%>
        <tr>
            <c:choose>
                <c:when test="${param.showError eq 'true'}">
                    <td>
                        <div class="error">
                            <fmt:message key="errors.login" />
                            <br/>
                            <a href="<c:url value='/Request_reset_token.jsp'/>">
                                <fmt:message key="errors.login.passwordForgot" />
                            </a>
                        </div>
                    </td>
                </c:when>
                <c:otherwise>
                    &nbsp;
                </c:otherwise>
            </c:choose>
        </tr>
        <tr>
            <td align="center">
                <form name="LoginForm" method="post" action="j_security_check" autocomplete="on">
                    <table width="250px" height="100" align="center" cellpadding="3" cellspacing="1" border="0" class="border">
                        <tr>
                            <td class="frmTitel" colspan="2"><fmt:message key="application.loginform.title" />:</td>
                        </tr>
                        <tr>
                            <td class="frmLabel" nowrap=""><fmt:message key="application.loginform.user" /></td>
                            <td
                                    class="frmInput"><input type="text" name="j_username" style="width: 100%" value="" autocomplete="on"></td>
                        </tr>
                        <tr>
                            <td class="frmLabel" nowrap=""><fmt:message key="application.loginform.password" /></td>
                            <td class="frmInput"><input type="password" name="j_password" style="width: 100%" value=""></td>
                        </tr>
                    </table>
                    <br>
                    <table width="20%" align="center" cellpadding="0" cellspacing="1" border="0">
                        <tr>
                            <td width="100%" class="frmSubmit">
                                <input class="submit" type="submit" name="submit" value="<fmt:message key="application.loginform.button.login" />">
                            </td>
                            <td width="100%" class="frmSubmit">
                                <input class="submit" type="reset" value="<fmt:message key="application.loginform.button.cancel" />"/>
                            </td>
                        </tr>
                    </table>
                </form>
            </td>
            <td height="100%"></td>
        </tr>
<%-- END $RCSfile: $ --%>

<jsp:include page="/Page_footer.jsp"/>
