<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: UserAdminType.jsp,v $ --%>
  
<%@ include file="../includeTLDs.jsp" %>
<%@page import="com.crh.logon.constants.Constant"%>
<html:form action="/useradmin/UserAdmin.do" styleId="admin_UserAdminFormUserAdminType">
<html:hidden property="inputAction" value=""/>
<table>
	<tbody>
	<tr>
		<%@ include file="UserAdminTypeInc.jspf" %>
		<td>
			<c:if test="${not empty admin_UserAdminForm.allowedUserTypeList}">
				<div align="left" style="float:left; vertical-align:bottom; padding:0px 0px 0px 10px; width:auto;"><input type="button" tabindex="10" class="ViewButtonStyle" value="Ok" onClick="useradmin.submitWithActionToDiv('selectUserType', 'admin_UserAdminFormUserAdminType', '')" >		</div>
			</c:if>
		</td>
	</tr>
</table>
</html:form>
