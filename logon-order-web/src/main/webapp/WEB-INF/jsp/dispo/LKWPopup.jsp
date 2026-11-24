<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: LKWPopup.jsp,v $ --%>
<%-- This popup is shown when on the Dispatich tab one clicks on tha name of a truck.--%>
<%@ page session="true"%>
<%@page import="com.crh.logon.persistence.entities.common.TruckUnavailabilityEntity"%>
<%@page import="com.crh.logon.constants.Constant"%>

<%@ include file="../includeTLDs.jsp"%>
<script type="text/javascript">
onPopupReady(function() {

	var el = document.getElementById('buttonAdd');
	if (el) {
		el.focus();
	}
});

var defaultMapCenterLat = <%=Constant.MAP_CENTER_LAT%>;
var defaultMapCenterLng = <%=Constant.MAP_CENTER_LNG%>;
var defaultMapCenterZoomLevel = <%=Constant.MAP_CENTER_ZOOM_LEVEL%>;

function LKWPopupInfo(){
	this.errMsgs = new Array();
	this.errMsgs[0] = "<fmt:message key="lkwpopup.error.eventrequired"/>";
	this.errMsgs[1] = "<fmt:message key="lkwpopup.error.timerequired"/>";
	this.errMsgs[2] =  "<fmt:message key="lkwpopup.error.daterequired"/>";
	this.extJsLoading = '<fmt:message key="shippingpoint.text.exjsloading"/>';
	this.lng = '<c:out value="${order_dispo_LKWPopupForm.truck.homebaseCoordLon}"/>';
    this.lat = '<c:out value="${order_dispo_LKWPopupForm.truck.homebaseCoordLat}"/>';
    this.deleteConfirm = "<fmt:message key="lkwpopup.comfirm.delete"/>";
}
var lkwPopupInfo = new LKWPopupInfo();
Ext.onReady(function(){
	load_lkwpopup_map(lkwPopupInfo.lat, lkwPopupInfo.lng);

});
</script>


<html:form action="/dispo/LKWPopup.do" onsubmit="return false;" styleId="LKWPopupForm">
<c:set var="datePattern" value="dd.MM.yy" scope="request"/> 
<c:set var="timePattern" value="HH:mm" scope="request"/>
<c:set var="SIMPLE_VEHICLE_PRIORITY_MAX"><%=Constant.SIMPLE_VEHICLE_PRIORITY_MAX%></c:set>
<table class="classicblue" style="border:none; width:auto">
<tr>
	<th colspan="2">
		<fmt:message key="lkwpopup.title"/>
	</th>
</tr>
<tr>
<td class="popupcontent">
	<table width="100%">
	<tr>
		<td class="border">
			<table width="100%">
				<div id="map" style="width:100%;height:250px;overflow:hidden" class="border">map</div>                 
			</table>
		</td>
	

		<td style="padding:0px" rowspan="2">
		<table class="classicblue" style="width:800px">
       		<thead>
	       		<tr>
	         		<th width="32%"><fmt:message key="lkwpopup.breakstable.header.event"/></th>
	       		  	<th width="18%"><fmt:message key="lkwpopup.breakstable.header.datumbeginn"/></th>
		          	<th width="12%"><fmt:message key="lkwpopup.breakstable.header.uhrzeitbeginn"/></th>
	         		<th width="18%"><fmt:message key="lkwpopup.breakstable.header.datumende"/></th>
	         		<th width="12%"><fmt:message key="lkwpopup.breakstable.header.uhrzeitende"/></th>
	   				<th width="1%"><nobr> <img src="<crh-common:url value='/images/edit.png'/>" /><img src="<crh-common:url value='/images/delete2.png'/>" /></nobr></th>
				</tr>
       		</thead>
   			<tbody id="unavailabilities">
				<c:set var="zebracount" value="1"/>
				<c:forEach var="entry" items="${order_dispo_LKWPopupForm.truckUnavailabilities}">
				<c:set var="zebracount" value="${(zebracount + 1)%2}"/>
				<tr class="zebra<c:out value="${zebracount}" />">
					<td><c:out value="${entry.description}"/></td>
					<td><fmt:formatDate value="${entry.startDat}" type="date" pattern="${datePattern}"/></td>
					<td><fmt:formatDate value="${entry.startDat}" type="time" pattern="${timePattern}"/></td>
					<td><fmt:formatDate value="${entry.endDat}" type="date" pattern="${datePattern}"/></td>
					<td><fmt:formatDate value="${entry.endDat}" type="time" pattern="${timePattern}"/></td>
					<td valign="middle">
						<nobr> 
							<a onclick='javascript:editRow(this.parentNode.parentNode.parentNode,"<c:out value="${entry.truckUnavailabilityId}"/>","<c:out value="${entry.description}"/>","<fmt:formatDate value="${entry.startDat}" type="date" pattern="${datePattern}"/>","<fmt:formatDate value="${entry.startDat}" type="date" pattern="${timePattern}"/>","<fmt:formatDate value="${entry.endDat}" type="date" pattern="${datePattern}"/>","<fmt:formatDate value="${entry.endDat}" type="date" pattern="${timePattern}"/>" )'>
								<img src="<crh-common:url value='/images/edit.png'/>" />
							</a> 
							<a onclick="javascript:deleteTruckUnAvailability(<c:out value="${entry.truckUnavailabilityId}"/>)">
								<img src="<crh-common:url value='/images/delete2.png'/>" />
							</a>
						</nobr>
					</td>					
				</tr>
				</c:forEach>
			</tbody>
		</table>
		</td>
	</tr>
	<tr>
	<td class="border">
		<table width="400px">
          	<tr>
          		<td class="label left"><fmt:message key="lkwpopup.details.lkwid"/></td>
          		<td><c:out value="${order_dispo_LKWPopupForm.truck.truckName}"/> <c:if test="${!empty order_dispo_LKWPopupForm.truck.truckPlateNo}">(<c:out value="${order_dispo_LKWPopupForm.truck.truckPlateNo}"/>)</c:if></td>
          	</tr>
            <tr>
            	<td class="label left"><fmt:message key="lkwpopup.details.lkwcategory"/></td>
            	<td><c:out value="${order_dispo_LKWPopupForm.trspCat.txt}"/></td>            	
            </tr>          	                      
            <tr>
            	<td class="label left"><fmt:message key="lkwpopup.details.lkwtyp"/></td>
            	<td><c:out value="${order_dispo_LKWPopupForm.type.txt}"/></td>            	
            </tr>
            <tr>
            	<td class="label left"><fmt:message key="lkwpopup.details.lkwprio"/></td>
            	<td>
            		<select class="truckprio_dropdown" onchange="setTruckPriority('<c:out value="${order_dispo_LKWPopupForm.truck.truckId}"/>', this.value)" >
                            		
						<c:forEach var="prio" items="${order_dispo_LKWPopupForm.truckPriorities}">
							<option <c:if test="${prio eq order_dispo_LKWPopupForm.truckPriority}">selected </c:if> value="<c:out value="${prio}"/>">
								<c:choose>
									<c:when test="${prio eq SIMPLE_VEHICLE_PRIORITY_MAX}">
										<fmt:message key="dispo.texte.trucks.truckinactive"/>
									</c:when>
									<c:otherwise>
										<c:out value="${prio}"/>
									</c:otherwise>
								</c:choose>
							</option>
						</c:forEach>
					</select>
            	</td>            	
            </tr>
            <tr>
            	<td class="label left"><fmt:message key="lkwpopup.details.maxmeasure"/></td>
		        <td><fmt:formatNumber value="${order_dispo_LKWPopupForm.compositionGross}" maxIntegerDigits="8" maxFractionDigits="4"/> <fmt:message key="lkwpopup.details.tons"/></td>
            </tr>                                                
            <tr>
            	<td class="label left"><fmt:message key="lkwpopup.details.taragewicht"/></td>
	            <td><c:out value="${order_dispo_LKWPopupForm.compositionTare}"/></td>
            </tr>
            <tr>
            	<td class="label left"><fmt:message key="lkwpopup.details.chambernumber"/></td>
	            <td><c:out value="${order_dispo_LKWPopupForm.compositionNrChambers}"/></td>
            </tr>            
            <tr>
            	<td class="label left"><fmt:message key="lkwpopup.details.linkauflieger"/></td>
            	<td><c:out value="${order_dispo_LKWPopupForm.trailer.trailerName}"/> <c:if test="${!empty order_dispo_LKWPopupForm.trailer.trailerPlateNo}">(<c:out value="${order_dispo_LKWPopupForm.trailer.trailerPlateNo}"/>)</c:if></td>
            </tr>
            <tr>
            	<td class="label left"><fmt:message key="lkwpopup.details.trailercategory"/></td>
            	<td><c:out value="${order_dispo_LKWPopupForm.trailerTrspCat.txt}"/></td>            	
            </tr>
            <tr>
            	<td class="label left"><fmt:message key="lkwpopup.details.currcontainer"/></td>
            	<td><c:out value="${order_dispo_LKWPopupForm.container.containerName}"/> <c:if test="${!empty order_dispo_LKWPopupForm.container.containerNo}">(<c:out value="${order_dispo_LKWPopupForm.container.containerNo}"/>)</c:if></td>            	
            </tr>
            <tr>
            <td colspan="2">&nbsp;</td>
            </tr>                    
            <tr>
            	<td class="label left"><fmt:message key="lkwpopup.details.bemerkung"/></td>
	            <td><c:out value="${order_dispo_LKWPopupForm.truck.truckComment}"/></td>
            </tr>
		</table>
		</td>
	</tr>		
		<tr>
			<td colspan="2"class="buttons right" style="vertical-align: top">
				<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.cancel"/>" onclick='closePopup();'/>   				
				<input id="buttonAdd" type="button" class="ViewButtonStyle" value="<fmt:message key="lkwpopup.button.add"/>" onclick="addGeneralRow();"/>								
				<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.reset"/>" onclick='javascript:showPopup("LKWPopupEntry.do?truckId=<c:out value="${order_dispo_LKWPopupForm.truck.truckId}"/>","");'/>
				<input id="buttonSave" type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.save"/>" onclick="saveTruckUnAvailability()"/>  
			</td>
		</tr>
	</table>
</td>
</tr>
</table>

</html:form>
