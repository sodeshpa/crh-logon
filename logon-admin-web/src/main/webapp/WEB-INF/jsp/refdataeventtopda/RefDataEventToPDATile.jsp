<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: RefDataEventToPDATile.jsp,v $ --%>
<%@ page session="true" %>
<%@ include file="../includeTLDs.jsp" %>

<%@page import="com.crh.logon.admin.web.forms.refdataeventtopda.RefDataEventToPDAForm"%><c:if test="false">
    <%-- for preview in MyEclipse --%>
    <link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>

<script type="text/javascript" src="<crh-common:url value='/dwr/interface/RFIDAdminAjaxService.js'/>"></script>

<script type="text/javascript">

function submitWithAction(action){
	form = Ext.getDom('admin_RFIDAdminForm');
	Ext.query("input[name=formAction]", form)[0].value = action;
	form.submit();
}

function sendSingleRefDataEntityToComServer(type, id) {
	form = Ext.getDom('admin_RefDataEventToPDAForm');
	Ext.query("input[name=inputAction]", form)[0].value = "sendSingle";
	Ext.getDom('entityType').value = type;
	Ext.getDom('entityId').value = id;
	form.submit();
}

function sendRefDataEntitySetToComServer(type) {
	form = Ext.getDom('admin_RefDataEventToPDAForm');
	Ext.query("input[name=inputAction]", form)[0].value = "sendSet";
	Ext.getDom('entityType').value = type;
	form.submit();
}

</script>
<html:form action="/refdataeventtopda/RefDataEventToPDA.do" styleId="admin_RefDataEventToPDAForm">
<html:hidden property="inputAction" value=""/>
<html:hidden property="entityType" styleId="entityType" value=""/>
<html:hidden property="entityId" styleId="entityId" value=""/>

 <!-- beginn Border-Table   -->
    <table width="100%" cellpadding="0" cellspacing="0" border="0" style="padding:0px; margin:0px;">
    	<tr>
    		<td>
    			<jsp:include page="../SubTab9Tile.jsp"/> 
				<table class="big_form2">
				<tr>
				<td>
      				<jsp:include page="../Sub3Tab9Tile.jsp"/>
      				</td>
      				</tr>
			    	<tr class="classicblue">
      					<th class="classicblue">
      						<fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.3"/>/ <fmt:message key="tabsubitem.9.3.3"/>
      					</th>
      				</tr>
      				<tr>
      				<td>
      				
			<table class="classicblue" cellpadding="0" cellspacing="0" border="0" width="100%" style="padding: 0px; margin: 0px;">
      		<tr>
				<td style="border-top">
					<span class="subtitle1"> <fmt:message key="refdataeventtopda.title.refdataentities"/></span>
				</td>
			</tr>
			
			
			<tr>
			
			<td width="25%">
			<div style="margin: 4px 0px;" class="subform">
				<table cellspacing="0" cellpadding="0" style="width: 100%;"
	class="noborder">
	
	<tbody>
		<tr>
			<td><span class="label"><fmt:message key="refdataeventtopda.texte.lang"/> </span>
			<div style="font-size: 12px; margin-top: 4px; height: 120px;"
				class="subformlist">
			<table cellspacing="0" cellpadding="0" border="0"
				style="width: 100%;" class="noborder">
				<colgroup>
					<col width="99%" />
					<col width="1%" />
				</colgroup>
				<tbody>
					<c:forEach var="entry" varStatus="status"
						items="${admin_RefDataEventToPDAForm.langs}">
						<tr class='zebra<c:out value="${status.count % 2}"/>'>
							<td>
								<c:out value="${entry.txt}" /> (<c:out value="${entry.langTxPK.lang1Cd}"/>)
							</td>
						</tr> 
					</c:forEach>
				</tbody>
			</table>
			</div>
			<div align="right">
			<input type="button" value='<fmt:message key="refdataeventtopda.texte.sendall"/>' class="SmallButtonStyle" onclick="sendRefDataEntitySetToComServer(<%=RefDataEventToPDAForm.ENTITY_LANG %>)" />
			</div>
		</tr>
	</tbody>
</table>
			</div>
			</td>
			
			<td width="25%">
			<div style="margin: 4px 0px;" class="subform">
				<table cellspacing="0" cellpadding="0" style="width: 100%;"
	class="noborder">
	
	<tbody>
		<tr>
			<td><span class="label"><fmt:message key="refdataeventtopda.texte.func"/> </span>
			<div style="font-size: 12px; margin-top: 4px; height: 120px;"
				class="subformlist">
			<table cellspacing="0" cellpadding="0" border="0"
				style="width: 100%;" class="noborder">
				<colgroup>
					<col width="99%" />
					<col width="1%" />
				</colgroup>
				<tbody>
					<c:forEach var="entry" varStatus="status"
						items="${admin_RefDataEventToPDAForm.functs}">
						<tr class='zebra<c:out value="${status.count % 2}"/>'>
							<td>
								<c:out value="${entry.funcName}" />
							</td>
						</tr> 
					</c:forEach>
				</tbody>
			</table>
			</div>
			<div align="right">
			<input type="button" value='<fmt:message key="refdataeventtopda.texte.sendall"/>' class="SmallButtonStyle" onclick="sendRefDataEntitySetToComServer(<%=RefDataEventToPDAForm.ENTITY_FUNC %>)" />
			</div>
		</tr>
	</tbody>
</table>
			</div>
			</td>
			
			</tr>
			<tr>
			
			<td width="25%">
			<div style="margin: 4px 0px;" class="subform">
				<table cellspacing="0" cellpadding="0" style="width: 100%;"
	class="noborder">
	
	<tbody>
		<tr>
			<td><span class="label"><fmt:message key="refdataeventtopda.texte.truckmaintenance"/> </span>
			<div style="font-size: 12px; margin-top: 4px; height: 120px;"
				class="subformlist">
			<table cellspacing="0" cellpadding="0" border="0"
				style="width: 100%;" class="noborder">
				<colgroup>
					<col width="99%" />
					<col width="1%" />
				</colgroup>
				<tbody>
					<c:forEach var="entry" varStatus="status"
						items="${admin_RefDataEventToPDAForm.truckMaintenances}">
						<tr class='zebra<c:out value="${status.count % 2}"/>'>
							<td>
								<c:out value="${entry.txt}" />(<c:out value="${entry.truckMaintenanceTxPK.langCd}"/>)
							</td>
						</tr> 
					</c:forEach>
				</tbody>
			</table>
			</div>
			<div align="right">
			<input type="button" value='<fmt:message key="refdataeventtopda.texte.sendall"/>' class="SmallButtonStyle" onclick="sendRefDataEntitySetToComServer(<%=RefDataEventToPDAForm.ENTITY_TRUCK_MAINTENANCE %>)" />
			</div>
		</tr>
	</tbody>
</table>
			</div>
			</td>
			
			
			<td width="25%">
			<div style="margin: 4px 0px;" class="subform">
				<table cellspacing="0" cellpadding="0" style="width: 100%;"
	class="noborder">
	
	<tbody>
		<tr>
			<td><span class="label"><fmt:message key="refdataeventtopda.texte.pdaerrormessage"/> </span>
			<div style="font-size: 12px; margin-top: 4px; height: 120px;"
				class="subformlist">
			<table cellspacing="0" cellpadding="0" border="0"
				style="width: 100%;" class="noborder">
				<colgroup>
					<col width="99%" />
					<col width="1%" />
				</colgroup>
				<tbody>
					<c:forEach var="entry" varStatus="status"
						items="${admin_RefDataEventToPDAForm.pdaErrorMessages}">
						<tr class='zebra<c:out value="${status.count % 2}"/>'>
							<td>
								<c:out value="${entry.txt}" /> (<c:out value="${entry.pdaErrorMessageTxPK.langCd}"/>)
							</td>
						</tr> 
					</c:forEach>
				</tbody>
			</table>
			</div>
			<div align="right">
			<input type="button" value='<fmt:message key="refdataeventtopda.texte.sendall"/>' class="SmallButtonStyle" onclick="sendRefDataEntitySetToComServer(<%=RefDataEventToPDAForm.ENTITY_PDA_ERROR_MESSAGE %>)" />
			</div>
		</tr>
	</tbody>
</table>
			</div>
			</td>
			
			</tr>
			<tr>
			
			<td width="25%">
			<div style="margin: 4px 0px;" class="subform">
				<table cellspacing="0" cellpadding="0" style="width: 100%;"
	class="noborder">
	
	<tbody>
		<tr>
			<td><span class="label"><fmt:message key="refdataeventtopda.texte.pdamarketinfo"/> </span>
			<div style="font-size: 12px; margin-top: 4px; height: 120px;"
				class="subformlist">
			<table cellspacing="0" cellpadding="0" border="0"
				style="width: 100%;" class="noborder">
				<colgroup>
					<col width="99%" />
					<col width="1%" />
				</colgroup>
				<tbody>
					<c:forEach var="entry" varStatus="status"
						items="${admin_RefDataEventToPDAForm.pdaMarketInfos}">
						<tr class='zebra<c:out value="${status.count % 2}"/>'>
							<td>
								<c:out value="${entry.txt}" /> (<c:out value="${entry.pdaMarketinfoTxPK.langCd}"/>)
							</td>
						</tr> 
					</c:forEach> 
				</tbody>
			</table>
			</div>
			<div align="right">
			<input type="button" value='<fmt:message key="refdataeventtopda.texte.sendall"/>' class="SmallButtonStyle" onclick="sendRefDataEntitySetToComServer(<%=RefDataEventToPDAForm.ENTITY_PDA_MARKET_INFO %>)" />
			</div>
		</tr>
	</tbody>
</table>
			</div>
			</td>
			
			
			<td width="25%">
			<div style="margin: 4px 0px;" class="subform">
				<table cellspacing="0" cellpadding="0" style="width: 100%;"
	class="noborder">
	
	<tbody>
		<tr>
			<td><span class="label"><fmt:message key="refdataeventtopda.texte.currency"/> </span>
			<div style="font-size: 12px; margin-top: 4px; height: 120px;"
				class="subformlist">
			<table cellspacing="0" cellpadding="0" border="0"
				style="width: 100%;" class="noborder">
				<colgroup>
					<col width="99%" />
					<col width="1%" />
				</colgroup>
				<tbody>
					<c:forEach var="entry" varStatus="status"
						items="${admin_RefDataEventToPDAForm.currencies}">
						<tr class='zebra<c:out value="${status.count % 2}"/>'>
							<td>
								<c:out value="${entry.txt}" /> (<c:out value="${entry.currencyTxPK.langCd}"/>)
							</td>
						</tr> 
					</c:forEach> 
				</tbody>
			</table>
			</div>
			<div align="right">
			<input type="button" value='<fmt:message key="refdataeventtopda.texte.sendall"/>' class="SmallButtonStyle" onclick="sendRefDataEntitySetToComServer(<%=RefDataEventToPDAForm.ENTITY_CURRENCY %>)" />
			</div>
		</tr>
	</tbody>
</table>
			</div>
			</td>
			
			</tr>
			<tr>
			
			<td width="25%">
			<div style="margin: 4px 0px;" class="subform">
				<table cellspacing="0" cellpadding="0" style="width: 100%;"
	class="noborder">
	
	<tbody>
		<tr>
			<td><span class="label"><fmt:message key="refdataeventtopda.texte.fueltype"/> </span>
			<div style="font-size: 12px; margin-top: 4px; height: 120px;"
				class="subformlist">
			<table cellspacing="0" cellpadding="0" border="0"
				style="width: 100%;" class="noborder">
				<colgroup>
					<col width="99%" />
					<col width="1%" />
				</colgroup>
				<tbody>
					<c:forEach var="entry" varStatus="status"
						items="${admin_RefDataEventToPDAForm.fuelTypes}">
						<tr class='zebra<c:out value="${status.count % 2}"/>'>
							<td>
								<c:out value="${entry.txt}" /> (<c:out value="${entry.fuelTypeTxPK.langCd}"/>)
							</td>
						</tr> 
					</c:forEach> 
				</tbody>
			</table>
			</div>
			<div align="right">
			<input type="button" value='<fmt:message key="refdataeventtopda.texte.sendall"/>' class="SmallButtonStyle" onclick="sendRefDataEntitySetToComServer(<%=RefDataEventToPDAForm.ENTITY_FUEL_TYPE %>)" />
			</div>
		</tr>
	</tbody>
</table>
			</div>
			</td>
			
			
			<td width="25%">
			<div style="margin: 4px 0px;" class="subform">
				<table cellspacing="0" cellpadding="0" style="width: 100%;"
	class="noborder">
	
	<tbody>
		<tr>
			<td><span class="label"><fmt:message key="refdataeventtopda.texte.waitingtimereason_shippingpoint"/> </span>
			<div style="font-size: 12px; margin-top: 4px; height: 120px;"
				class="subformlist">
			<table cellspacing="0" cellpadding="0" border="0"
				style="width: 100%;" class="noborder">
				<colgroup>
					<col width="99%" />
					<col width="1%" />
				</colgroup>
				<tbody>
					<c:forEach var="entry" varStatus="status"
						items="${admin_RefDataEventToPDAForm.waitingtimeReasonsShippingpoint}">
						<tr class='zebra<c:out value="${status.count % 2}"/>'>
							<td>
								<c:out value="${entry.txt}" /> (<c:out value="${entry.waitingtimeReasonTxPK.langCd}"/>)
							</td>
						</tr> 
					</c:forEach> 
				</tbody>
			</table>
			</div>
			<div align="right">
			<input type="button" value='<fmt:message key="refdataeventtopda.texte.sendall"/>' class="SmallButtonStyle" onclick="sendRefDataEntitySetToComServer(<%=RefDataEventToPDAForm.ENTITY_WAITINGTIMEREASON_SHIPPINGPOINT %>)" />
			</div>
		</tr>
	</tbody>
</table>

			</div>
			</td>
			
			</tr>
			<tr>
			
			<td width="25%">
			<div style="margin: 4px 0px;" class="subform">

<table cellspacing="0" cellpadding="0" style="width: 100%;"
	class="noborder">
	
	<tbody>
		<tr>
			<td><span class="label"><fmt:message key="refdataeventtopda.texte.waitingtimereason_shipto"/> </span>
			<div style="font-size: 12px; margin-top: 4px; height: 120px;"
				class="subformlist">
			<table cellspacing="0" cellpadding="0" border="0"
				style="width: 100%;" class="noborder">
				<colgroup>
					<col width="99%" />
					<col width="1%" />
				</colgroup>
				<tbody>
					<c:forEach var="entry" varStatus="status"
						items="${admin_RefDataEventToPDAForm.waitingtimeReasonsShipto}">
						<tr class='zebra<c:out value="${status.count % 2}"/>'>
							<td>
								<c:out value="${entry.txt}" /> (<c:out value="${entry.waitingtimeReasonTxPK.langCd}"/>)
							</td>
						</tr> 
					</c:forEach> 
				</tbody>
			</table>
			</div>
			<div align="right">
			<input type="button" value='<fmt:message key="refdataeventtopda.texte.sendall"/>' class="SmallButtonStyle" onclick="sendRefDataEntitySetToComServer(<%=RefDataEventToPDAForm.ENTITY_WAITINGTIMEREASON_SHIPTO %>)" />
			</div>
		</tr>
	</tbody>
</table>
		
			</div>
			</td>
			
			<td width="25%">
			<div style="margin: 4px 0px;" class="subform">
		<table cellspacing="0" cellpadding="0" style="width: 100%;"
	class="noborder">
	
	<tbody>
		<tr>
			<td><span class="label"><fmt:message key="refdataeventtopda.texte.rfidobjecttype"/> </span>
			<div style="font-size: 12px; margin-top: 4px; height: 120px;"
				class="subformlist">
			<table cellspacing="0" cellpadding="0" border="0"
				style="width: 100%;" class="noborder">
				<colgroup>
					<col width="99%" />
					<col width="1%" />
				</colgroup>
				<tbody>
					<c:forEach var="entry" varStatus="status"
						items="${admin_RefDataEventToPDAForm.rfidobjectTypes}">
						<tr class='zebra<c:out value="${status.count % 2}"/>'>
							<td>
								<c:out value="${entry.txt}" /> (<c:out value="${entry.rfidobjectTypeTxPK.langCd}"/>)
							</td>
						</tr> 
					</c:forEach> 
				</tbody>
			</table>
			</div>
			<div align="right">
			<input type="button" value='<fmt:message key="refdataeventtopda.texte.sendall"/>' class="SmallButtonStyle" onclick="sendRefDataEntitySetToComServer(<%=RefDataEventToPDAForm.ENTITY_RFIDOBJECT_TYPE %>)" />
			</div>
		</tr>
	</tbody>
</table>
		
			</div>
			</td>
			
			</tr>
			<tr>
			<td width="25%">
			<div style="margin: 4px 0px;" class="subform">
		<table cellspacing="0" cellpadding="0" style="width: 100%;"
	class="noborder">
	
	<tbody>
		<tr>
			<td><span class="label"><fmt:message key="refdataeventtopda.texte.waitingtimeatshippingpoint"/> </span>
			<div style="font-size: 12px; margin-top: 4px; height: 120px;"
				class="subformlist">
			<table cellspacing="0" cellpadding="0" border="0"
				style="width: 100%;" class="noborder">
				<colgroup>
					<col width="99%" />
					<col width="1%" />
				</colgroup>
				<tbody>
						<tr>
							<td>
								<c:out value="${admin_RefDataEventToPDAForm.waitingTimeAtShippingpoint}" />
							</td>
							<td valign="middle" style="width: 1px"><nobr> <label title="Send <c:out value="${admin_RefDataEventToPDAForm.waitingTimeAtShippingpoint}" /> Reference Data Entity to the ComServer" onClick="sendSingleRefDataEntityToComServer(<%=RefDataEventToPDAForm.ENTITY_WAITING_TIME_AT_SHIPPINGPOINT %>, '<c:out value="${admin_RefDataEventToPDAForm.waitingTimeAtShippingpoint}" />')"> <fmt:message key="refdataeventtopda.texte.send"/> </label></nobr></td>
						</tr> 
				</tbody>
			</table>
			</div>
		</tr>
	</tbody>
</table>
		
			</div>
			</td>
<td width="25%">
			<div style="margin: 4px 0px;" class="subform">
		<table cellspacing="0" cellpadding="0" style="width: 100%;"
	class="noborder">
	
	<tbody>
		<tr>
			<td><span class="label"><fmt:message key="refdataeventtopda.texte.waitingtimeatshipto"/> </span>
			<div style="font-size: 12px; margin-top: 4px; height: 120px;"
				class="subformlist">
			<table cellspacing="0" cellpadding="0" border="0"
				style="width: 100%;" class="noborder">
				<colgroup>
					<col width="99%" />
					<col width="1%" />
				</colgroup>
				<tbody>
						<tr>
							<td>
								<c:out value="${admin_RefDataEventToPDAForm.waitingTimeAtShipTo}" />
							</td>
							<td valign="middle" style="width: 1px"><nobr> <label title="Send <c:out value="${admin_RefDataEventToPDAForm.waitingTimeAtShipTo}" /> Reference Data Entity to the ComServer" onClick="sendSingleRefDataEntityToComServer(<%=RefDataEventToPDAForm.ENTITY_WAITING_TIME_AT_SHIPTO %>, '<c:out value="${admin_RefDataEventToPDAForm.waitingTimeAtShipTo}" />')"> <fmt:message key="refdataeventtopda.texte.send"/> </label></nobr></td>
						</tr> 
				</tbody>
			</table>
			</div>
		</tr>
	</tbody>
</table>
			</tr>
			
							</table>
      				
					
				</table>
			</td>
		</tr>
	</table>
	</html:form>
