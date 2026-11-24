<%@ page session="true" %>

<%@ include file="../includeTLDs.jsp" %>

<c:if test="false">
	<%-- for preview in MyEclipse --%>
	<link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>

<%@page import="com.crh.logon.web.utils.ConfigBeanProvider"%>
<%@page import="com.crh.logon.admin.web.forms.test.ManualFaxSmsTriggerForm"%>

<script type="text/javascript" src="<crh-common:url value='/dwr/interface/TestAjaxService.js'/>"></script>

<jsp:include page="../SubTab11Tile.jsp" />

<c:set var="fsform" value="${sessionScope.admin_ManualFaxSmsTriggerForm}" />

<script type="text/javascript">

	var availableFaxProviders = <c:out value="${fsform.availableFaxProvidersAsJSArray}" escapeXml="false"/>;
	var availableSmsProviders = <c:out value="${fsform.availableSmsProvidersAsJSArray}" escapeXml="false"/>;

	function switchFaxOrSms(isFax) {
		$('documentIdRow').style.display= (isFax) ? "" : "none";
		
		var serviceProviderArray = (isFax) ? availableFaxProviders : availableSmsProviders;
		var serviceProviderSelect = document.getElementById('serviceProviderSelect');
		serviceProviderSelect.options.length = 0;
		
		serviceProviderSelect.options[0] = new Option("---", "", true, false);
		for (i=0; i<serviceProviderArray.length; i++) {
			serviceProviderSelect.options[serviceProviderSelect.options.length] = new Option(serviceProviderArray[i], serviceProviderArray[i], false, false);
		}
	}
</script>

<!-- Begin table 3 rows -->
<table class="big_form2">

	<tr class="classicblue">
		<th class="classicblue"><fmt:message key="tabitem.test" />
		/ <fmt:message key="tabsubitem.11.5" /></th>
	</tr>
	<tr>

		<td><!-- begin Border-Table   --> 
		<html:form action="test/ManualFaxSmsTrigger.do">
				<table width="100%">
					<tr style="width:40%;" >
						<td style="vertical-align: top;" width="25%">
							<table class="classicblue" style="width: 100%">
								<tr>
									<td class="label left" width="20%">
										Service provider
									</td>
									<td>
										<html:select size="1" property="serviceProvider" styleId="serviceProviderSelect">
											<c:forEach var="availableServiceProvider" items="${fsform.availableServiceProviders}">
												<html:option value="${availableServiceProvider}"><c:out value="${availableServiceProvider}"/></html:option>
											</c:forEach>
										</html:select>
									</td>
								</tr>
								<tr>
									<td class="label left" width="20%">number:</td>
									<td>
										<html:text property="number" styleClass="textfield" />
									</td>
								</tr>
								<tr>
									<td class="label left" width="20%">message:</td>
									<td>
									<html:text property="message" styleClass="textfield" />
									</td>
								</tr>
								<tr>
									<td class="label left" width="20%">orderNo:</td>
									<td>
										<html:text property="orderNo" styleClass="textfield" />
									</td>
								</tr>
								<tr id="documentIdRow" <c:if test="${not fsform.fax}">style="display: none;"</c:if>>
									<td class="label left" width="20%">documentId:</td>
									<td>
										<html:text property="documentId" styleClass="textfield" />
									</td>
								</tr>
								<tr>
									<td class="label left" width="20%">is it a fax?</td>
									<td>
										<html:checkbox property="fax" onchange="switchFaxOrSms(this.checked)" styleClass="textfield" />
									</td>
								</tr>
								<tr>
									<td class="buttons" colspan="2">
										<html:submit value="Call provider" styleClass="ViewButtonStyle"/>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
		</html:form>
		</td>
</tr>
</table>
