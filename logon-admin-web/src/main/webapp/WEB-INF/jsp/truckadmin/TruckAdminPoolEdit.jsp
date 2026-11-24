<%@ page session="true"%>

<%@ include file="../includeTLDs.jsp"%>
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="fmtx"%>
<%@ page import="com.crh.logon.admin.web.ajax.PoolAdminAjaxService"%>


<%@page import="com.crh.logon.admin.web.actions.truckadmin.PoolAdminAction"%><c:if test="false">
	<%-- for preview in MyEclipse --%>
	<link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>

<html:form action="/truckadmin/PoolAdmin.do" styleId="admin_PoolAdminForm">

<html:hidden property="inputAction" value=""/>
<table>
<tr>
	<td class="left"><nobr><fmt:message
		key="pooladmin.field.logonname" /></nobr></td>
	<td>
	<nobr>
		<html:text style="width:130px;"	property="currentEditedForwardingAgentBean.logonName"/>
	<td class="left"><nobr><fmt:message
		key="pooladmin.field.coordinates" /></nobr></td>
	<td>
<td>
<div class="scrollist" id="DivPools" style="height: 70px; width:250px;">
				<table class="classic" style="border:none">
				<c:forEach var="pool" varStatus="status" items="${admin_PoolAdminForm.poolList}">
					<tr class="zebra<c:out value="${status.count % 2}"/>">
						<td valign="middle" style="width:1px">
							<html:checkbox property="currentEditedForwardingAgentBean.selectedPools" value="true" onchange=""/>
						</td> 
					
						<td><c:out value="${pool.name}"/></td>
					</tr>
					<c:set var="lastStatusCount" value="${status.count}" />
				</c:forEach>
				</tr>
			</table>
			</div>
</td>
</tr>
</table>
</html:form>
