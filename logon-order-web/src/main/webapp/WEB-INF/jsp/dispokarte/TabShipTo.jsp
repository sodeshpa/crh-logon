<%@ include file="../includeTLDs.jsp" %>
<%@ taglib uri="http://com.crh.logon/tags/common-taglib.tld" prefix="crh-common" %>
<%@ page import="com.crh.logon.taglib.fallbacklogic.DefaultFallBackLogicTag" %>
<%@page import="com.crh.logon.order.web.forms.dispokarte.DispokarteForm"%>
<%@page import="com.crh.logon.order.web.ajax.OrderAjaxService"%>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>

<%@page import="com.crh.logon.constants.Constant"%><script type="text/javascript">
var isCoordKnown = false;
<c:set var="countryOfBp" value="${order_dispokarte_DispokarteForm.countryForShipto}"/>
<c:set var="shiptoAddress" value="${countryOfBp}, ${order_dispokarte_DispokarteForm.shipTo.shiptoSapBp.postalCode} ${order_dispokarte_DispokarteForm.shipTo.shiptoSapBp.city} ${order_dispokarte_DispokarteForm.shipTo.shiptoSapBp.street} ${order_dispokarte_DispokarteForm.shipTo.shiptoSapBp.houseNumber}"/>
<c:if test="${order_dispokarte_DispokarteForm.validShiptoCoords}">
	isCoordKnown = true;
</c:if>
</script>

<c:set var="TAB_SHIPTO_TYPE_ROAD" >
	<%=DispokarteForm.TAB_SHIPTO_TYPE_ROAD%>
</c:set>
<c:set var="TAB_SHIPTO_TYPE_RAIL" >
	<%=DispokarteForm.TAB_SHIPTO_TYPE_RAIL%>
</c:set>


<script type="text/javascript" src="<crh-common:url value='/js/dispokarte-map.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/logon-gmap-common.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/dispokarte.js'/>"></script>

<script type="text/javascript">

var texts = {		
		ambiguousaddress: '<fmt:message key="dispokarte.warnings.ambiguous.address"/>',
		coordsretrieved: '<fmt:message key="dispokarte.success.coordsretrieved"/>',
		missingaddress: '<fmt:message key="dispokarte.warnings.missing.address"/>',
		addresserror: '<fmt:message key="dispokarte.warnings.address.error"/>'
};

	Ext.onReady(function(){
	
		var openingTime1From = new Ext.form.TimeField({
			id: 'open1From',
	    	increment: 15,
	    	format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
	    	applyTo: 'open1From',
	    	width: 67
		});
		
		var openingTime2From = new Ext.form.TimeField({
			id: 'open2From',
	    	increment: 15,
	    	format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
	    	applyTo: 'open2From',
	    	width: 67
		});
		
		var openingTime1To = new Ext.form.TimeField({
			id: 'open1To',
	    	increment: 15,
	    	format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
	    	applyTo: 'open1To',
	    	width: 67
		});
		
		var openingTime2To = new Ext.form.TimeField({
			id: 'open2To',
	    	increment: 15,
	    	format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
	    	applyTo: 'open2To',
	    	width: 67
		});
	});
	
	function setupPriority(type, idx) {
		var radiobuttonId = null; 
		var prioField = null;
		
		if ( 'truck' == type ) {
			prioField =  'truckPrio';
			radiobuttonId = 'truckAllowanceTrue';
		}
		else {
			prioField =  'wagonPrio';
			radiobuttonId = 'wagonAllowanceTrue';
		}
		
		var radiobuttonTrue = document.getElementById(radiobuttonId + idx);
		var radiobutton = radiobuttonTrue.checked ? true : false;
		
		var prio = document.getElementById(prioField + idx);
		
		if (prio.value == '' || prio.value == '-' || parseInt(prio.value) < 0) {
			if ( radiobutton ) {
				prio.disabled = false;
				prio.value = "10";
			}
			else {
				prio.disabled = true;
				prio.value = "-";
			}
		}
		else if ( parseInt(prio.value) > 0 && parseInt(prio.value) < 100 ){
			if ( radiobutton ) {
				prio.disabled = false;
				prio.value = "10";
			}
			else {
				prio.disabled = true;
				prio.value = "-";			
			}
		}
		else {
			prio.disabled = false;
		}
	}
	
	function shiptoTypeChanged(){
		var shiptoType = document.getElementById('shiptoTypeDD');
		if (!shiptoType) {
			return;
		}
		
		var plantMatFlowDivs = Ext.get('plantMatFlowDivs');
		if (!plantMatFlowDivs) {
			return;
		}
		
		var parameters = { shiptoType: shiptoType.value, shiptoTypeChanged : true };
		plantMatFlowDivs.load({
			url: 'Dispokarte.do', 
			params: parameters,
			text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
			scripts: true
		});
	}
	
	function modifyMatChainPlant(){
		var matChainPlant = document.getElementById('matChainPlantDD');
		if (!matChainPlant) {
			return;
		}
		
		var plantMatFlowDivs = Ext.get('plantMatFlowDivs');
		if (!plantMatFlowDivs) {
			return;
		}

		var parameters = { modifyMatChainPlant: matChainPlant.value };
		plantMatFlowDivs.load({
			url: 'Dispokarte.do', 
			params: parameters,
			text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
			scripts: true
		});
	}
	
	function modifyRailChainPlant(){
		var diva = Ext.get('railChainDiv');
		var selectedPlant = document.getElementById('railChainPlantDD').value;
		params = { modifyRailChainPlant: selectedPlant};
		diva.load({
			url: 'Dispokarte.do', 
			params: params,
			text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
			scripts: true
		});
	}

	function tabShiptoToChoose(tab){
		<%-- Switch Tab --%>

		if (tab == '<c:out value="${TAB_SHIPTO_TYPE_ROAD}"/>') {
			if ( document.getElementById("TabShipToTypeRoad") != null) {
				document.getElementById("TabShipToTypeRoad").className = "highlight";
			}
			if ( document.getElementById("TabShipToTypeRail") != null) {
				document.getElementById("TabShipToTypeRail").className = "";
			}
		}
		else {
			if ( document.getElementById("TabShipToTypeRoad") != null) {
				document.getElementById("TabShipToTypeRoad").className = "";
			}
			if ( document.getElementById("TabShipToTypeRail") != null) {
				document.getElementById("TabShipToTypeRail").className = "highlight";
			}
		}

		
		a = {"setShiptoTypeTab":"", "shiptoTypeTab": tab};
		addFormElements(a, "ShipToForm"); <%-- add form data --%>
		var diva = Ext.get('ShiptoTypeTabsDiv');
			diva.load({
				url: 'Dispokarte.do', 
				params: a,
				text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
				scripts: true,
				callback: function(el){
					onLoadTab();
				}						
			});

	}
</script>


<html:form action="dispokarte/Dispokarte.do" styleId="ShipToForm">
<html:hidden property="formAction" value=""/>
<table cellpadding="0" class="big_form2" cellspacing="0" style="margin: 0px; border-collapse: collapse; padding: 1px;">
  <tbody>
  	<tr>
  		<td class="border" style="padding: 2px 2px 2px 2px; width: 29%;"> <%-- left column--%>
  		<span class="label">	
  			<fmt:message key="dispokarte.texte.oeffnungszeiten"/>:
  		</span>
<%-- �ffnungszeiten --%>  		
	<table cellspacing="0" cellpadding="0">
		<tr>
			<td style="padding: 0px 0px 0px 0px;">
				<html:text style="width:65px;" property="open1From" styleId="open1From"/>
			</td>
			<td style="padding: 2px 2px 0px 2px;">
				&nbsp;-&nbsp;
			</td>
			<td style="padding: 2px 2px 0px 2px;">
				<html:text style="width:65px;" property="open1To" styleId="open1To"/>
			</td>
			<td style="padding: 2px 2px 0px 2px;">
				&nbsp;/&nbsp;
			</td>
			<td style="padding: 2px 2px 0px 2px;">
				<html:text style="width:65px;" property="open2From" styleId="open2From"/>
			</td>
			<td style="padding: 2px 2px 0px 2px;">
				&nbsp;-&nbsp;
			</td>
			<td style="padding: 2px 2px 0px 2px;">
				<html:text style="width:65px;" property="open2To" styleId="open2To"/>		
			</td>
		</tr>
	</table>
	<c:if test="${order_dispokarte_DispokarteForm.displayShipToOpeningHoursTimezone}">
		<p style="margin-bottom: 3px; margin-top: 3px">
			<span class="label">
				<fmt:message key="application.label.timezone"/>:&nbsp;
				<c:out value="${order_dispokarte_DispokarteForm.shipToTimeZone.formattedAsTimeZoneOnly}"/>
			</span>
		</p>
	</c:if>
<%-- Ansprechpartner --%>  		
<table cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td class="label" style="padding:0px;padding-top:8px">
			<span style="margin-top: 4px"><fmt:message key="dispokarte.texte.ansprechpartner"/></span>
		</td>
		<td class="buttons right" style="vertical-align: top">
 			<input type="button" class="SmallButtonStyle" value="<fmt:message key="buttons.hinzufuegen"/>" onclick="showPopup('AnsprechpartnerEntry.do?referrer=dispokarte&action=add','')"/> <%-- for Ansprechpartner --%>   		
		</td>
	</tr>
</table>
<div class="scrollist" id="DivAnsprechpartner" style="height: 70px;">
	<jsp:include page="Ansprechpartner.jsp">
		<jsp:param name="referrer" value="dispokarte"/>
	</jsp:include>
</div>
<table cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td class="label" style="padding:0px;padding-top:4px">
<%-- Silobelegung --%>
			<span style="margin-top: 4px"><fmt:message key="dispokarte.texte.silobelegung"/></span>
		</td>
		<td class="buttons right" style="vertical-align: top">
			<input type="button" class="SmallButtonStyle" value="<fmt:message key="buttons.hinzufuegen"/>" onclick="showPopup('SilobelegungEntry.do?referrer=dispokarte&action=add','')"/> <%-- for Silobelegung --%>
		</td>
	</tr>
</table>
<div class="scrollist" id="DivSilobelegung" style="height: 70px;">
	<jsp:include page="Silobelegung.jsp">
		<jsp:param name="referrer" value="dispokarte"/>
	</jsp:include>
</div>
<table style="width: 100%; padding: 4px;">
	<tr>
		<td style="padding: 0px 0px 0px 0px;">&nbsp;</td>
		<td class="label" style="padding: 0px 0px 0px 0px;"><nobr><fmt:message key="dispokarte.texte.printdelivnotecem"/></nobr></td>		
		<td class="label" style="padding: 0px 0px 0px 0px;"><nobr><fmt:message key="dispokarte.texte.printdelivnoteagglf"/></nobr></td>		
	</tr>
	<tr>
		<td class="label">
			<fmt:message key="dispokarte.texte.printsingledeliverynote"/>			
		</td>
		<td title="<fmt:message key="dispokarte.texte.printdelivnotecem.tooltip"/>">
			<html:checkbox property="shipTo.paperSingleDelnoteFlagCem" value="true"/>
			<c:set target="${order_dispokarte_DispokarteForm.shipTo}" property="paperSingleDelnoteFlagCem" value="false"/>
		</td>
		<td title="<fmt:message key="dispokarte.texte.printdelivnoteagg.tooltip"/>">
			<html:checkbox property="shipTo.paperSingleDelnoteFlagAgg" value="true"/>
			<c:set target="${order_dispokarte_DispokarteForm.shipTo}" property="paperSingleDelnoteFlagAgg" value="false"/>
		</td>
	</tr>
	<tr>
		<td class="label">
			<fmt:message key="dispokarte.texte.printcollectivedeliverynote"/>			
		</td>
		<td title="<fmt:message key="dispokarte.texte.printcolldelivnotecem.tooltip"/>">
			<html:checkbox property="shipTo.paperCollDelnoteFlagCem" value="true"/>
			<c:set target="${order_dispokarte_DispokarteForm.shipTo}" property="paperCollDelnoteFlagCem" value="false"/>
		</td>
		<td title="<fmt:message key="dispokarte.texte.printcolldelivnoteagg.tooltip"/>">
			<html:checkbox property="shipTo.paperCollDelnoteFlagAgg" value="true"/>
			<c:set target="${order_dispokarte_DispokarteForm.shipTo}" property="paperCollDelnoteFlagAgg" value="false"/>		
		</td>
	</tr>
</table>
<c:set var="orderConfTooltip"><fmt:message key="dispokarte.texte.order.conf" /></c:set>
<c:set var="loadConfTooltip"><fmt:message key="dispokarte.texte.loadorder.conf" /></c:set>
<c:set var="delivnoteTooltip"><fmt:message key="dispokarte.texte.deliv.note" /></c:set>
<table style="table-layout:fixed">
         <colgroup>
         	<col width="10%">
            <col width="7%">
            <col width="7%">
            <col width="7%">
            <col width="59%">
          </colgroup>
	<tr>
		<td align="right"> </td>
		<td class="label"><fmt:message key="bestellung.order.confirm.abbrev"/></td>
		<td class="label"><fmt:message key="bestellung.load.confirm.abbrev"/></td>
		<td class="label"><fmt:message key="bestellung.delivery.note.abbrev"/></td>
		<td class="label"> </td>
	</tr>
	<tr>
		<td class="label" align="right"><fmt:message key="dispokarte.texte.fax"/>:</td>
		<td><html:checkbox property="shipTo.orderConfFaxFlag" title="${orderConfTooltip}" value="true"/></td>
		<c:set target="${order_dispokarte_DispokarteForm.shipTo}" property="orderConfFaxFlag" value="false"/>
		<td><html:checkbox property="shipTo.loadConfFaxFlag" title="${loadConfTooltip}" value="true"/></td>
		<c:set target="${order_dispokarte_DispokarteForm.shipTo}" property="loadConfFaxFlag" value="false"/>
		<td><html:checkbox property="shipTo.faxDelivnoteFlag" title="${delivnoteTooltip}" value="true"/></td>
		<c:set target="${order_dispokarte_DispokarteForm.shipTo}" property="faxDelivnoteFlag" value="false"/>
		<td><html:text property="shipTo.orderConfFaxNumber" maxlength="80" style="width:100%" /></td>
	</tr>
	<tr>
		<td class="label" align="right"><fmt:message key="dispokarte.texte.mail"/>:</td>
		<td><html:checkbox property="shipTo.orderConfEmailFlag" title="${orderConfTooltip}" value="true"/></td>
		<c:set target="${order_dispokarte_DispokarteForm.shipTo}" property="orderConfEmailFlag" value="false"/>
		<td><html:checkbox property="shipTo.loadConfEmailFlag" title="${loadConfTooltip}" value="true"/></td>
		<c:set target="${order_dispokarte_DispokarteForm.shipTo}" property="loadConfEmailFlag" value="false"/>
		<td><html:checkbox property="shipTo.emailDelivnoteFlag" title="${delivnoteTooltip}" value="true"/></td>
		<c:set target="${order_dispokarte_DispokarteForm.shipTo}" property="emailDelivnoteFlag" value="false"/>
		<td><html:text property="shipTo.orderConfEmailAddresses" style="width:100%"/></td>
	</tr>
	<tr>
		<td class="label" align="right"><fmt:message key="dispokarte.texte.sms"/>:</td>
		<td><html:checkbox property="shipTo.orderConfSmsFlag" title="${orderConfTooltip}" value="true"/></td>
		<c:set target="${order_dispokarte_DispokarteForm.shipTo}"property="orderConfSmsFlag" value="false"/>
		<td><html:checkbox property="shipTo.loadConfSmsFlag" title="${loadConfTooltip}" value="true"/></td>
		<c:set target="${order_dispokarte_DispokarteForm.shipTo}" property="loadConfSmsFlag" value="false"/>
		<td></td>
		<c:set target="${order_dispokarte_DispokarteForm.shipTo}" property="faxDelivnoteFlag" value="false"/>
		<td><html:text property="shipTo.orderConfSmsNumber" maxlength="80" style="width:100%"/></td>
	</tr>
	<tr>
		<td colspan="3" class="label" align="right">
			<fmt:message key="dispokarte.texte.sparche"/>
		</td>
		<td>
			<html:text property="language" style="width:120px;" readonly="true" disabled="true"/>
		</td>
	</tr>
	
</table>
  		</td>
  		<td class="border" style="padding: 0px; width: 30%;">
			<table>
				<c:if test="${not order_dispokarte_DispokarteForm.validShiptoCoords}">
				<tr>
					<td class="label" style="border-bottom: 1px solid #CCCCCC;padding-left:0px;padding-right:2px;">
						<nobr><fmt:message key="dispokarte.texte.shipto.address"/></nobr>
					</td>
					<td colspan="3" align="left" style="border-bottom: 1px solid #CCCCCC;padding-left:0px;padding-right:2px;">
						<input id="shiptoAddressField" style="width: 110px" class="textfield" value="<c:out value="${shiptoAddress}"/>"/>
					</td>
					<td colspan="3" style="border-bottom: 1px solid #CCCCCC;padding-left:0px;padding-right:0px">
						<input id="shiptoAddressSearchBtn" style="width: 80%" type="button" value="Set address" class="ViewButtonStyle" onClick="javascript:setShiptoAddress(document.getElementById('shiptoAddressField').value)"/>
					</td>
				</tr>
				</c:if>			
				<tr>
					<td colspan="7">
				<c:choose>
					<c:when test="${not order_dispokarte_DispokarteForm.validShiptoCoords}">
						<div id="map" style="width:100%;height:315px;overflow:hidden"></div>
					</c:when>
					<c:otherwise>
						<div id="map" style="width:100%;height:350px;margin-top: 8px;overflow:hidden"></div>
					</c:otherwise>
				</c:choose>					
					</td>
				</tr>
				<tr>	
					<td class="label" width="120">
						<fmt:message key="dispokarte.texte.koordinaten"/>:
					</td>
					<td  width="135" colspan="3" align="left">
						<nobr>
							<html:text style="width:60px;" property="shipTo.coordLon" styleId="FldLngKoord"/>
							<html:text style="width:60px;" property="shipTo.coordLat" styleId="FldLatKoord"/>
						</nobr>
					</td>
					<td class="label" title="<fmt:message key="dispokarte.texte.fix"/>" align="left">
						<html:checkbox property="shipTo.coordFixFlag" value="true"/>						
					</td>
					<c:set target="${order_dispokarte_DispokarteForm.shipTo}" property="coordFixFlag" value="false"/>
					<td class="label right" width="1" style="padding: 0px 0px 0px 0px;"><nobr><fmt:message key="dispokarte.texte.geofence"/>:</nobr></td>
					<td style="padding: 0px 0px 0px 0px;"><html:text property="shipTo.geofenceRadius" style="width:50px;" styleId="FldRadius"/></td>	
				</tr>
				<tr>
					<td colspan="7">
						<!-- Truck loading/unloading defaults -->
						<table>
							<tr>
								<td style="padding: 0px 0px 0px 0px;">&nbsp;</td>
								<td class="label" style="padding: 0px 0px 0px 0px;"><nobr><fmt:message key="dispokarte.texte.materialtype.cem"/></nobr></td>
								<td style="padding: 0px 0px 0px 0px;">&nbsp;</td>
								<td class="label" style="padding: 0px 0px 0px 0px;"><nobr><fmt:message key="dispokarte.texte.materialtype.agg"/></nobr></td>
								<td style="padding: 0px 0px 0px 0px;">&nbsp;</td>
							</tr>
							<tr>
								<%pageContext.setAttribute("shipTo", ((DispokarteForm)request.getSession().getAttribute("order_dispokarte_DispokarteForm")).getShipTo()); %>
								<!-- Truck unloading time CEM -->
								<crh-common:defaultFallback propertyName="<%=DefaultFallBackLogicTag.PROPERTY_DEFAULT_SHIPTO_UNLOADING_TIME_MIN_CEM %>" propertyInputFieldName="shipTo.unloadingTimeCemMin" propertyCheckboxName="unloadingTimeCemCheckbox" objectName="shipTo">
									<td class="label" style="padding: 0px 0px 0px 0px;"><nobr><fmt:message key="dispokarte.texte.entladezeit"/>:</nobr></td>
									<td width="50" style="padding: 0px 0px 0px 0px;">
										<crh-common:defaultFallbackInputField style="width:50px;"></crh-common:defaultFallbackInputField>
									</td>
									<td title="<fmt:message key="dispokarte.texte.entladezeitflag"/>" align="left" width="20" style="padding: 0px 0px 0px 0px;">
										<crh-common:defaultFallbackCheckbox></crh-common:defaultFallbackCheckbox>
									</td>  
								</crh-common:defaultFallback>
								<c:set target="${order_dispokarte_DispokarteForm.shipTo}" property="unloadingTimeCemMin" value=""/>
								
								<!-- Truck unloading time AGG -->
								<crh-common:defaultFallback propertyName="<%=DefaultFallBackLogicTag.PROPERTY_DEFAULT_SHIPTO_UNLOADING_TIME_MIN_AGG %>" propertyInputFieldName="shipTo.unloadingTimeAggMin" propertyCheckboxName="unloadingTimeAggCheckbox" objectName="shipTo">
									<td width="50" style="padding: 0px 0px 0px 0px;">
										<crh-common:defaultFallbackInputField style="width:50px;"></crh-common:defaultFallbackInputField>
									</td>
									<td title="<fmt:message key="dispokarte.texte.entladezeitflag"/>" align="left" width="20" style="padding: 0px 0px 0px 0px;">
										<crh-common:defaultFallbackCheckbox></crh-common:defaultFallbackCheckbox>
									</td>
								</crh-common:defaultFallback>
								<c:set target="${order_dispokarte_DispokarteForm.shipTo}" property="unloadingTimeAggMin" value=""/>
							</tr>
							<tr>
								<!-- Truck loading times -->
								<crh-common:defaultFallback propertyName="<%=DefaultFallBackLogicTag.PROPERTY_DEFAULT_SHIPTO_LOADING_TIME_MIN_LF %>" propertyInputFieldName="shipTo.loadingTimeLfMin" propertyCheckboxName="loadingTimeLfCheckbox" objectName="shipTo">
									<td class="label" style="padding: 0px 0px 0px 0px;"><nobr><fmt:message key="dispokarte.texte.landfillloading"/>:</nobr></td>
									<td width="50" style="padding: 0px 0px 0px 0px;">&nbsp;</td>
									<td align="left" width="20" style="padding: 0px 0px 0px 0px;">&nbsp;</td>  
									<td width="50" style="padding: 0px 0px 0px 0px;">
										<crh-common:defaultFallbackInputField style="width:50px;"></crh-common:defaultFallbackInputField>
									</td>
									<td title="<fmt:message key="dispokarte.texte.entladezeitflag"/>" align="left" width="20" style="padding: 0px 0px 0px 0px;">
										<crh-common:defaultFallbackCheckbox></crh-common:defaultFallbackCheckbox>
									</td>			
								</crh-common:defaultFallback>
								<c:set target="${order_dispokarte_DispokarteForm.shipTo}" property="loadingTimeLfMin" value=""/>
							</tr>
						</table>
					</td>
				</tr>
			</table>
  		</td>
  		<td class="border" style="padding: 2px 4px 2px 4px; width: 40%;">
			<table cellpadding="0" cellspacing="0" style="margin: 0px; border-collapse: collapse; padding: 0px 0px 0px 0px;">
				<tr>
					<td style="width:160px;">
						<span class="label"><fmt:message key="dispokarte.texte.shiptoType" /></span>
						<div style="margin-top: 4px">
							<html:select styleId="shiptoTypeDD" size="1" style="width:120px;"
									property="selectedShipToType"
									onchange="shiptoTypeChanged()"
									disabled="${order_dispokarte_DispokarteForm.treatAsOneTime}">
								<c:forEach var="row" items="${order_dispokarte_DispokarteForm.shipToTypeList}">
									<html:option value="${row.shiptoTypeTxPK.shiptoTypeBs.shiptoTypeCd}">
										<c:out value="${row.txt}" />
									</html:option>
								</c:forEach>
							</html:select>
						</div>
						<div style="margin-top: 6px">
							<html:checkbox value="true" property="shipTo.dailyOrderContactFlag" />
							<c:set target="${order_dispokarte_DispokarteForm.shipTo}" property="dailyOrderContactFlag" value="false" />
							<span><fmt:message key="dispokarte.texte.kundeKontaktieren" /></span>
						</div>
					</td>
					<td style="vertical-align: top">
						<div id="plantMatFlowDivs">
							<div id="plantDiv"><jsp:include page="PlantDropdown.jsp" /></div>
							<div id="matFlowDiv"><jsp:include page="MatFlowDropdown.jsp" /></div>
						</div>
					</td>
				</tr>
			</table>

			<table width="100%" border="0" cellpadding="0" cellspacing="0"
				id="ShiptoTypeTabs" style="margin-top: 4px;">
				<tr>
					<td width="100%" style="padding-left: 4px" class="subnaviborder">
					<ul id="subnavigation">
						<li><a href="javascript:tabShiptoToChoose('<c:out value="${TAB_SHIPTO_TYPE_ROAD}"/>')"
							id='TabShipToTypeRoad'
							<c:if test="${order_dispokarte_DispokarteForm.shiptoTypeTab==TAB_SHIPTO_TYPE_ROAD}">class="highlight"</c:if>>
						<span class="outer"><span class="inner"><fmt:message
							key="dispokarte.texte.shiptotab.road" /></span></span></a></li>
						<c:if test="${not empty order_dispokarte_DispokarteForm.shipTo.shiptoSapBp.railwayStation}" >
							<li><a href="javascript:tabShiptoToChoose('<c:out value="${TAB_SHIPTO_TYPE_RAIL}"/>')"
								id='TabShipToTypeRail'
								<c:if test="${order_dispokarte_DispokarteForm.shiptoTypeTab==TAB_SHIPTO_TYPE_RAIL}">class="highlight"</c:if>>
							<span class="outer"><span class="inner"><fmt:message
								key="dispokarte.texte.shiptotab.rail" /></span></span></a></li>
						</c:if>	
					</ul>
					</td>
				</tr>
			</table>

			<div id="ShiptoTypeTabsDiv" class="border" style="border-top:none;">
				<c:choose>
					<c:when test="${order_dispokarte_DispokarteForm.shiptoTypeTab == TAB_SHIPTO_TYPE_ROAD}">
						<jsp:include page="TabShipToRoad.jsp"/>
					</c:when>
					<c:otherwise>
						<jsp:include page="TabShipToRail.jsp"/>
					</c:otherwise>
				</c:choose>
			</div>
	
  		</td>
	</tr>
	<%-- Bemerkungen --%>
	<tr>
		<td colspan="3">
			<table width="100%">
				<tr>
					<td class="label" width="200">
						<nobr>
							<fmt:message key="dispokarte.texte.auftragsmanagement"/>
						</nobr>
					</td>
					<td class="label" width="200">
						<nobr>
							<fmt:message key="dispokarte.texte.zusatzlieferschein"/>
						</nobr>
					</td>
					<td class="label" width="200">
						<nobr>
							<fmt:message key="dispokarte.texte.poolspediteur"/>
						</nobr>
					</td>
					<td class="label" width="200">
						<nobr>
							<fmt:message key="dispokarte.texte.chauffeur"/>
						</nobr>
					</td>
					<td class="label" width="200"	>
						<nobr>
							<fmt:message key="dispokarte.texte.bahndisposition"/>
						</nobr>
					</td>
				</tr>
				<tr>
					<td>
						<html:textarea cols="30" rows="5" property="shipTo.noteOrderMgmt"></html:textarea>
					</td>
					<td>
						<html:textarea cols="30" rows="5" property="shipTo.noteDelivery"></html:textarea>
					</td>
					<td>
						<html:textarea cols="30" rows="5" property="shipTo.noteDispatching"></html:textarea>
					</td>
					<td>
						<html:textarea cols="30" rows="5" property="shipTo.noteTruckDriver"></html:textarea>
					</td>
					<td>
						<html:textarea cols="30" rows="5" property="shipTo.noteRailwayDisp"></html:textarea>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<%-- end of Bemerkungen --%>
  </tbody>
</table>
</html:form>