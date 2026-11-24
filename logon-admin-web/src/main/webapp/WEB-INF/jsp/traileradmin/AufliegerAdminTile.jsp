<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: AufliegerAdminTile.jsp,v $ --%>
<%-- Does not use, but is involved in functionality:
	AufliegerCarrierTile - this is what one sees for the first time (single search field with carrrier data) from here one reaches AufliegerAdminTile
	
	Does use these tiles:
	AufliegerData1Tile - div called masterdata
	AufliegerData2Tile - div called masterdata2, contains LKW assignment
	AufliegerData3Tile - div called masterdata2, contains the map
	RFIDPopupTile - div called RFIFPopupForm.DIV_NAME
	
	
	AufliegerAdminTile includes:	
	_________________________________________________________________________________
	|          |					 |						|						|
	|          |  AufliegerData1Tile |  RFIDPopupTile		|    AufliegerData3Tile | 
	|          |					 |						|						|
	_________________________________________________________________________________
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%-- <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> --%>
<%@ include file="../includeTLDs.jsp" %>
<%@page import="com.crh.logon.admin.web.actions.traileradmin.TrailerAdminAction"%>
<%@page import="com.crh.logon.admin.web.forms.traileradmin.TrailerAdminForm"%>
<%@ page import="com.crh.logon.admin.web.forms.AdminRFIDPopupForm" %>
<%@page import="com.crh.logon.persistence.entities.common.TrailerEntity"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@ page session="true" %>
<%@page import="com.crh.logon.web.utils.Authorization"%>


<c:if test="false">
    <%-- for preview in MyEclipse --%>
    <link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>

<c:set var="isAddNewTrailerAvailable">false</c:set>
<crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_PLANNING_TRUCK_CREATE %>">
<c:set var="isAddNewTrailerAvailable">true</c:set>
</crh-common:authorize>
<crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_GENERIC_TRUCK_CREATE %>">
<c:set var="isAddNewTrailerAvailable">true</c:set>
</crh-common:authorize>

<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/dwr/interface/AdminRFIDPopupAjaxService.js'/>"></script>
<script type="text/javascript">
var forwards = {
		UPDATE_CHAMBERS: '<%=TrailerAdminAction.FORWARD_UPDATE_CHAMBERS %>',
		SELECT_CARRIER: '<%=TrailerAdminAction.FORWARD_SELECT_CARRIER %>',
		LOAD_TRAILER: '<%=TrailerAdminAction.FORWARD_LOAD_TRAILER %>',
		SAVE_NEW: '<%=TrailerAdminAction.FORWARD_SAVE_NEW %>',
		SAVE: '<%=TrailerAdminAction.FORWARD_SAVE %>',
		RESET: '<%=TrailerAdminAction.FORWARD_RESET %>',
		CREATE_TRAILER: '<%=TrailerAdminAction.FORWARD_CREATE_TRAILER %>',
		SHOW_ONLY_ACTIVE_TRAILERS: '<%=TrailerAdminAction.FORWARD_SHOW_ONLY_ACTIVE_TRAILERS %>'
};

var texts = {
	commonConfirm: '<fmt:message key="confirm.trim.textarealength"/>',
	commentField: '<fmt:message key="roadmasterdata.fields.comment"/>'
};
var lengths = {		
		commentLength: '<%=TrailerEntity.DBSIZE_TRAILER_COMMENT + ""%>'
};

var tkategoryContainer = <%=Constant.TRAILER_TKATEGORIE_CONTAINER%>;
var defaultMapCenter = 
{  
	lat : <%=Constant.MAP_CENTER_LAT%>,
	lng : <%=Constant.MAP_CENTER_LNG%>,
	zoomLevel : <%=Constant.MAP_CENTER_ZOOM_LEVEL%>
};

function filterTrailers() { 
	document.getElementById("inputAction").value=forwards.SHOW_ONLY_ACTIVE_TRAILERS;
	Ext.get('admin_TrailerAdminForm').dom.submit();
} 
</script>
<script type="text/javascript" src="<crh-common:url value='/js/traileradmin/AufliegerAdminTile.js'/>"></script>
<div class="customer_max">
    <!-- beginn Border-Table   -->
<jsp:include page="../SubTab9Tile.jsp"/>   

<!-- Begin table 3 rows -->
<table class="big_form2">
      
      <tr class="classicblue">
      <th height="21" colspan="4" class="classicblue"><fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.5"/></th>
      </tr>
      <tr>
    
      <td>
            <!-- Beginn Submenu 2-2-1-1 -->
<jsp:include page="../Sub5Tab9Tile.jsp"/>   
		
              
              
              <html:form action="traileradmin/TrailerAdmin.do" styleId="admin_TrailerAdminForm">
    <input type="hidden" name="inputAction" id="inputAction" value=""/>
	<input type="hidden" name="trailerId" id="trailerId" value='<c:out value="${admin_TrailerAdminForm.trailerId}"/>'/>
       		  
       		  <div class="subform" style="border-top:0px;">
       		    

                <!-- Beginn Search Table -->
                  <table cellpadding="0" cellspacing="0" border="0" class="big_form2" style="padding:0px;">
                  	<colgroup>
                    <col width="20%">
                    <col width="80%">
                    </colgroup>
                          
                            <tr>
                              <td valign="top" style="border:0px;"><span class="labelblue"><fmt:message key="roadmasterdata.fields.carrierchoose"/></span><br>
                              
                                  <table class="noborder" cellpadding="0" cellspacing="0" border="0">
                                      <tr>
                                          <td valign="bottom" style="white-space:nowrap;border:0px; padding-right: 4px;"><p class="formtitle"><fmt:message key="roadmasterdata.fields.choose"/></p></td>
                                          <td style="white-space:nowrap;border:0px;"><span class="content" style="border:0px;">
					<html:select onchange="selectCarrier()" styleId="carrierId" property="carrierId" style="margin:0px; padding:0px; width:auto;">
						<c:forEach var="entry" items="${admin_TrailerAdminForm.carrierList}">
						<html:option value="${entry.carrierId}"><c:out value="${entry.displayName}"/></html:option>						
						</c:forEach>
					</html:select>
                                          </span>
                                          </td>
                                      </tr>
                                  </table>  
                                                            
                               </td>
                              <td style="border:0px;"><span class="labelblue"><fmt:message key="roadmasterdata.fields.address"/></span><br>
                              <c:out value="${admin_TrailerAdminForm.carrier.sapVendor.name1}"/><br>
                              <c:out value="${admin_TrailerAdminForm.carrier.sapVendor.street}"/> <c:out value="${admin_TrailerAdminForm.carrier.sapVendor.houseNumber}"/><br>
                              <c:out value="${admin_TrailerAdminForm.carrier.sapVendor.city}"/>
                              </td>
                            </tr>
                  </table>
                  
                <c:if test = "${!empty admin_TrailerAdminForm.trailer}">
                <table class="classicblue" style="width:100%;height:100%;">
                  <colgroup>
                  <col width="13%">
                  <col width="29%">
                  <col width="29%">
                  <col width="29%">
                  </colgroup>
              <tr>
                <th height="21" colspan="4"><fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.5"/> / <fmt:message key="tabsubitem.9.5.4"/></th>
              </tr>
              <tr>
              <%--------------------------------------------------------------%>
              <%------------------- Column 1 - Trailer list-------------------%>
              <%--------------------------------------------------------------%>
                <td>
                <p class="subtitle1"><fmt:message key="roadmasterdata.fields.trailer"/></p>
                
                    <!-- beginn Fahrzeug liste -->
                        <html:checkbox styleId="showOnlyActiveTrailers" property="showOnlyActiveTrailers" onclick="filterTrailers()"/>
                        <fmt:message key="roadmasterdata.label.show.only.active.trailers"/>
                      <div class="subformlist" style="font-size:12px; margin-top:4px; height:600px;">
                      <table cellpadding="0" cellspacing="0" border="0"  style="width:100%;">
                        <colgroup>
                        <col width="99%">
                        <col width="1%">
                        </colgroup>
                            <tbody>
	<c:set var="zebracount" value="1"/>
	<c:forEach var="entry" items="${admin_TrailerAdminForm.trailerList}">
		<c:set var="zebracount" value="${(zebracount + 1)%2}"/>
                              <tr  <c:if test="${entry.trailerId eq admin_TrailerAdminForm.trailerId }">class="selected"</c:if><c:if test="${entry.trailerId ne admin_TrailerAdminForm.trailerId }"> class="zebra<c:out value="${zebracount}" />" </c:if> >
                                <td><a href="#" onclick='selectTrailer(<c:out value="${entry.trailerId}"/>)'><c:out value="${entry.trailerName}"/></a></td>
                              </tr>
	</c:forEach>                   
                              
                            </tbody>
                      </table>
                    </div>
                </td>
              <%--------------------------------------------------------------%>
              <%------------------- Column 2 - Trailer main data--------------%>
              <%--------------------------------------------------------------%>                
                <td>
                	<p class="subtitle1"><fmt:message key="roadmasterdata.label.masterdata"/></p>
                    <div class="subform" style="margin:4px 0px 4px 0px;padding:0px;height:92%">
    <jsp:include page="Auflieger1Tile.jsp"/>
                  </div>
                  </td>
              <%--------------------------------------------------------------%>
              <%------------------- Column 3 - history list and comment-------%>
              <%--------------------------------------------------------------%>                  
                <td><p class="subtitle1"><fmt:message key="roadmasterdata.fields.trailerassignment"/></p>
                    <div class="subform" style="height:92%">
							<div class="subformlist"  style="height:125px">
							<table class="noborder" cellpadding="0" cellspacing="0"
								border="0" style="width: 100%;">
								<colgroup>
									<col width="100%">
								</colgroup>
								<tbody>
									<c:set var="zebracount" value="1" />
									<c:forEach var="entry"
										items="${admin_TrailerAdminForm.historyList}">
										<c:set var="zebracount" value="${(zebracount + 1)%2}" />
										<tr class="zebra<c:out value="${zebracount}" />">
 											<td><c:out value="${entry.truck.truckName}" /> | 
                            <crh-common:formatDateWithUserTimezone date="${entry.assignmentTs}"></crh-common:formatDateWithUserTimezone>
											</td> 
										</tr>
									</c:forEach>
								</tbody>
							</table>
							</div>
							<br>
                          <div style="margin:4px 0px 4px 0px;padding:4px;width:100%;"> <span class="label"><fmt:message key="roadmasterdata.fields.container"/></span><br>
<% 
TrailerAdminForm form = ((TrailerAdminForm) request.getSession().getAttribute(TrailerAdminForm.formName));
pageContext.setAttribute("containerList", form.getContainerList()); 
%>
					<html:select property="inputContainerSelect"  styleId="inputContainerSelect" style="width:100%; margin:4px 0px 4px 0px;padding:4px;" >
						<option value=""><fmt:message key="option.select"/>.</option>
						<html:options collection="containerList" property="containerId" labelProperty="containerName"/>
					</html:select>	
<script type="text/javascript">					
	var container = document.getElementById("inputContainerSelect");
	container.disabled=false;
	<%if (form.getTrailer().getCurrentTruck()!= null &&form.getTrailer().getCurrentTruck().getPdaAvailableFlag().equals(Boolean.TRUE)){
		%>container.disabled=true;<%
	}
	%>
</script>
                          </div>							

							<!-- beginn special title with button aligned to the right -->
                      <div style="margin-bottom:0px; padding-left:4px; padding-bottom:4px;"> <span class="label">RFID:</span><br>
                      </div>
                      <!-- beginn RFID -->
                          <div class="subformlist" id="<%=AdminRFIDPopupForm.NAME_OF_HTML_DIV_TO_RELOAD%>" style="clear:both;font-size:12px; margin:0px 4px 0px 4px;">
<jsp:include page="../AdminRfidsTile.jsp"/>    
                          </div>
                          <div align="right">
                            <input id="addRFID" onclick="javascript:updatePlateNoShowRFIDPopup();" type="button" class="SmallButtonStyle" value=" <fmt:message key="buttons.add"/> ">
                          </div>
                      <!-- End RFID -->
                          <div style="padding:4px;width:100%;"> <span class="label"><fmt:message key="roadmasterdata.fields.comment"/></span><br>
                              <textarea id="inputComment" name="inputComment" rows="7" class="textfield" onkeypress="return checkMaxLength(event, this, <%=TrailerEntity.DBSIZE_TRAILER_COMMENT + ""%>)" cols="1" style="width: 100%;"><c:out value="${admin_TrailerAdminForm.inputComment}" /></textarea>
                          </div>
                    </div>
                  <!-- End Fahrzeugzuordnung -->
                </td>
              <%--------------------------------------------------------------%>
              <%------------------- Column 4 - Map-----------------------------%>
              <%--------------------------------------------------------------%>                
                <td>
                <p class="subtitle1"><fmt:message key="roadmasterdata.fields.lastknown"/></p>
                        <div id="masterdata3" name="masterdata3" class="subform" style="margin:4px 0px 4px 0px;padding:0px;height:92%">
                        <jsp:include page="Auflieger3Tile.jsp"/>
                        </div>
				</td>
              </tr>
              <tr>
				<td>
					<c:if test = "${isAddNewTrailerAvailable}">
                  		<div align="right">
                  			<input onclick="javascript:addNewTrailer();" type="button" class="SmallButtonStyle" value=" <fmt:message key="buttons.add"/> " alt="Fahrzeug" />
						</div>
					</c:if>
				</td>
                <td colspan="3">
                <div align="right" style="margin:4px 0px 4px 0px;padding:0px;height:7%">
                    <crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_TRAILER %>">
<%if (((TrailerAdminForm)session.getAttribute("admin_TrailerAdminForm")).getTrailer() != null) {%>
       <input onclick="javascript:resetTrailer()" type="button" class="ViewButtonStyle" style="margin-bottom:4px;" value="<fmt:message key="buttons.reset"/>">
                      &nbsp;&nbsp;
		<input id="saveButton" name="saveButton" onclick="javascript:saveTrailer(false)" type="button" class="ViewButtonStyle"  style="margin-bottom:4px;" 
			value="<fmt:message key="buttons.save"/>">
<% }
else{%>
<crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_TRAILER_UPDATE %>">
		<input onclick="javascript:resetTrailer()" type="button" class="ViewButtonStyle" style="margin-bottom:4px;" value="<fmt:message key="buttons.reset"/>">
                      &nbsp;&nbsp;
		<input id="saveButton" name="saveButton" onclick="javascript:saveTrailer(true)" type="button" class="ViewButtonStyle"  style="margin-bottom:4px;" 
			value="<fmt:message key="traileradmin.buttons.savenew"/>">
</crh-common:authorize>
<% }%>
</crh-common:authorize>
                    
                  </div>
                    <br>
                </td>
              </tr>
            </table>
		</c:if>
                <%----------------------------------------------------------------------------%>
                <%------------------------------------NO TRAILER IS PRESENT -------------------%>
                <%----------------------------------------------------------------------------%>                            
                <c:if test = "${empty admin_TrailerAdminForm.trailer}">
                <table class="classicblue" style="width:100%;"><tr><td>
	                <c:out value="${admin_TrailerAdminForm.carrier.displayName}"/> <fmt:message key="traileradmin.info.notrailer"/>
	                <c:if test = "${isAddNewTrailerAvailable}">                        
                      <div align="right">
                        <input onclick="javascript:addNewTrailer();" type="button" class="SmallButtonStyle" value=" <fmt:message key="buttons.add"/> " />
                        </div>
					</c:if>
					</td>
					</tr>
					</table>    
                </c:if>
            </div>
            
            </html:form>
            

            </td>
      
      
      </tr>
</table>
  <!-- End Border Table -->
</div>
<div style="visibility:hidden;width:0px;height:0px" id="changefields"></div>
<script>
changeFields();
</script>

