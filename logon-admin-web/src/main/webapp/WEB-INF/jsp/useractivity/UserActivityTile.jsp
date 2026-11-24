
<%@ include file="../includeTLDs.jsp" %>
<%@page import="com.crh.logon.web.utils.Authorization"%>
  
<script type="text/javascript" src="<crh-common:url value='/js/extjshelper/executeUpdate.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/useradmin/useradmin-util.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/dwr/interface/UserActivityAjaxService.js'/>"></script>

<script type="text/javascript"> 
	executeUpdate.init('<fmt:message key="executeupdate.updatedText"/>', '<fmt:message key="executeupdate.exceptionTitle"/>', '<fmt:message key="executeupdate.updateButtonText"/>');
	useradmin.init('<fmt:message key="shippingpoint.text.exjsloading"/>', '<fmt:message key="executeupdate.question.optimisticlock"/>');
	
	var activitySearchArea;
	var lastLoginSearchArea;
	
	function resetSearchFilters() {
		document.getElementsByName("filterFromDate")[0].value = "";
		document.getElementsByName("filterFromTime")[0].value = "";
		document.getElementsByName("filterToDate")[0].value = "";
		document.getElementsByName("filterToTime")[0].value = "";
		document.getElementsByName("filterUserId")[0].value = "";
		document.getElementsByName("filterUserName")[0].value = ""; 
		document.getElementsByName("filterUserHolspace")[0].value = ""; 
		document.getElementsByName("filterUserPdauser")[0].value = "";
		document.getElementsByName("filterExecutorId")[0].value = "";
		document.getElementsByName("filterUserFirstname")[0].value = "";
		document.getElementsByName("filterUserExtlogin")[0].value = "";
		document.getElementsByName("filterIpAddress")[0].value = "";
	};
	
	Ext.onReady(function() {
		
		<c:choose>
			<c:when test="${admin_UserActivityForm.adminView}">
				<jsp:include page="UserActivityAdminSearchGrid.jsp"/>
				lastLoginSearchArea.search();
			</c:when>
			<c:otherwise>
				<jsp:include page="UserActivitySearchGrid.jsp"/>
			</c:otherwise>
		</c:choose>
		
		activitySearchArea.search();
	});
</script>


<div class="customer_max">
	<!-- beginn Border-Table   -->
	<table cellpadding="0" cellspacing="0" border="0" width="100%" style="padding:0px; margin:0px;">
		<tr>
			<td>
				<jsp:include page="../SubTab10Tile.jsp"/>   

				<table class="big_form2">
					<tr class="classicblue">
						<th class="classicblue" colspan="7">
							<fmt:message key="tabitem.admin"/> / <fmt:message key="tabsubitem.10.5"/>
						</th>
					</tr>
					<tr>
						<td>
							<html:form action="/useractivity/UserActivity.do" styleId="admin_UserActivityForm">
								<c:if test="${admin_UserActivityForm.adminView}">
									<span class="labelblue">
										<fmt:message key="useractivity.subform.title.activitysearch"/>
									</span>
									<table class="subform" width="100%">
										<tr>
											<td style="vertical-align:middle;">
												<input id="searchButton" name="searchButton" type="button" class="ViewButtonStyle" style="width:100px;" value="<fmt:message key="useractivity.filter.filterbutton"/>">
											</td>
											<td>
												<fmt:message key="useractivity.filter.fromdate"/>
												<html:text styleId="filterFromDate" property="filterFromDate" style="width:100px;"/>
											</td>
											<td>
												<fmt:message key="useractivity.filter.todate"/>
												<html:text styleId="filterToDate" property="filterToDate" style="width:100px;" />
											</td>
											<td>
												<fmt:message key="useractivity.col.userid"/><br/>
												<html:text styleId="filterUserId" property="filterUserId" styleClass="textField" style="width:130px;"/>
											</td>
											<td>
												<fmt:message key="useractivity.col.username"/><br/>
												<html:text styleId="filterUserName" property="filterUserName" styleClass="textField" style="width:130px;"/>
											</td>
											<td>
												<fmt:message key="useractivity.col.userholspace"/><br/>
												<html:text styleId="filterUserHolspace" property="filterUserHolspace" styleClass="textField" style="width:130px;"/>
											</td>
											<td>
												<fmt:message key="useractivity.col.userpdauser"/><br/>
												<html:text styleId="filterUserPdauser" property="filterUserPdauser" styleClass="textField" style="width:130px;"/>
											</td>
										</tr>
										<tr>
											<td style="vertical-align:middle;">
												<input id="resetFilterButton" name="resetFilterButton" type="button" class="ViewButtonStyle" style="width:100px;" value="<fmt:message key="useractivity.filter.resetfilterbutton"/>">
											</td>
											<td>
												<fmt:message key="useractivity.filter.fromtime"/>
												<html:text styleId="filterFromTime" property="filterFromTime" style="width:100px;"/>
											</td>
											<td>
												<fmt:message key="useractivity.filter.totime"/>
												<html:text styleId="filterToTime" property="filterToTime" style="width:100px;"/>
											</td>
											<td>
												<fmt:message key="useractivity.col.executorid"/><br/>
												<html:text styleId="filterExecutorId" property="filterExecutorId" styleClass="textField" style="width:130px;"/>
											</td>
											<td>
												<fmt:message key="useractivity.col.userfirstname"/><br/>
												<html:text styleId="filterUserFirstname" property="filterUserFirstname" styleClass="textField" style="width:130px;"/>
											</td>
											<td>
												<fmt:message key="useractivity.col.userextlogin"/><br/>
												<html:text styleId="filterUserExtlogin" property="filterUserExtlogin" styleClass="textField" style="width:130px;"/>
											</td>
											<td>
												<fmt:message key="useractivity.col.ipaddress"/><br/>
												<html:text styleId="filterIpAddress" property="filterIpAddress" styleClass="textField" style="width:130px;"/>
											</td>
										</tr>
									</table>
									<br/>
									<span class="labelblue">
										<fmt:message key="useractivity.subform.title.lastlogin"/>
									</span>
									<table class="subform" width="100%">
										<tr>
											<td style="vertical-align:middle; width: 150px;">
												<input id="dormantSearchButton" name="dormantSearchButton" type="button" class="ViewButtonStyle" style="width:120px;" value="<fmt:message key="useractivity.dormantsearch.button"/>">
											</td>
											<td style="float:left;">
												<fmt:message key="useractivity.dormantsearch.lastloginbefore"/>
												<html:text styleId="lastLoginBefore" property="lastLoginBefore" style="width:100px;"/>
											</td>
										</tr>
									</table>
								</c:if>
							</html:form>
						</td>
					</tr>
				</table>
				<table class="big_form2">
					<tr>
						<td>
							<div id="search-results-div" style="width:100%">
							</div>
							<div id="last-login-search-results-div" style="width:100%">
							</div>
						</td>
					</tr>
				</table>
			</td>
  		</tr>
	</table>
</div>

<%-- END $RCSfile: UserActivityTile.jsp,v $ --%>

