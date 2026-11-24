<%@ include file="../includeTLDs.jsp" %>
<%@ page import="com.crh.logon.admin.web.utils.SimpleVehicleAdminChangeLogic" %>
<%@page import="com.crh.logon.admin.web.forms.simplevehicleadmin.SimpleVehicleAdminForm"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@page import="com.crh.logon.persistence.entities.common.SimpleVehicleEntity"%>

<c:set var="isAddNewSimpleVehicleAvailable">false</c:set>
<crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_SIMPLE_VEHICLE_CREATE %>">
<c:set var="isAddNewSimpleVehicleAvailable">true</c:set>
</crh-common:authorize>
                        
<html:form action="simplevehicleadmin/SimpleVehicleAdmin.do" styleId="admin_SimpleVehicleAdminForm">
    <input type="hidden" name="inputAction" id="inputAction" value=""/>
    <input type="hidden" name="simpleVehicleId" id="simpleVehicleId" value='<c:out value="${admin_SimpleVehicleAdminForm.simpleVehicleId}"/>'/>
<jsp:include page="SimpleVehicleStammdatenCarrierTile.jsp"/>
    <c:if test = "${!empty admin_SimpleVehicleAdminForm.carrier}">              
	<c:if test = "${!empty admin_SimpleVehicleAdminForm.simpleVehicle}">
	
	
	
                <table class="classicblue" style="width:100%;height:100%;">
                  <colgroup>
                  <col width="13%">
                  <col width="87%">
                  </colgroup>
                  <tr>
                    <th height="21" colspan="2" class="classicblue"><fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.5"/> / <fmt:message key="tabsubitem.9.5.10"/></th>
                  </tr>
                  <tr>
                    <td><p class="subtitle1"><fmt:message key="roadmasterdata.label.simplevehicle"/></p>
                        <!-- beginn Fahrzeug liste -->
                        <div id="simpleVehiclelist" name="simpleVehiclelist" class="subformlist" style="font-size:12px; margin-top:4px;height:600px">
<jsp:include page="SimpleVehicleStammdatenSimpleVehicleList.jsp"/>          
                        </div>
                      <!-- End Fahrzeugliste -->
                    </td>
                    <td>
			<table class="classicblue" style="width:100%;height:100%;">
			<tr>
                    <td><p class="subtitle1"><fmt:message key="roadmasterdata.label.masterdata"/></p>
                       <div  id = "masterdata1" name="masterdata1" class="subform" style="margin:4px 0px 4px 0px;padding:0px;height:93%">
							<jsp:include page="SimpleVehicleStammdatenData1Tile.jsp"/>                         
                       </div>
 					</td>
                    <td><p class="subtitle1"><fmt:message key="roadmasterdata.label.map"/></p>
                        <div id="masterdata2" name="masterdata2" class="subform" style="margin:4px 0px 4px 0px;padding:0px;height:93%">
                        	<jsp:include page="SimpleVehicleStammdatenData2Tile.jsp"/>
                        </div>
 					</td>
 					</tr>
			</table>

 					</td>
                  </tr>
                  <tr>
                    <td>
						<c:if test = "${isAddNewSimpleVehicleAvailable}">                  
                      		<div style="vertical-align:top;" align="right">
                        		<input onclick="javascript:addNewSimpleVehicle();" type="button" class="SmallButtonStyle" value=" <fmt:message key="buttons.add"/> " /> 
                        	</div>
						</c:if>
                    </td>
                    <td><div align="right">
<crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_SIMPLE_VEHICLE %>">
<%if (((SimpleVehicleAdminForm)session.getAttribute("admin_SimpleVehicleAdminForm")).getSimpleVehicle() != null) {%>
       <input onclick="javascript:resetSimpleVehicle(<c:out value="${admin_SimpleVehicleAdminForm.simpleVehicleId}"/>)" type="button" class="ViewButtonStyle" style="margin-bottom:4px;" value="<fmt:message key="buttons.reset"/>">
                      &nbsp;&nbsp;
		<input id="saveButton" name="saveButton" onclick="javascript:saveSimpleVehicle(false)" type="button" class="ViewButtonStyle"  style="margin-bottom:4px;" 
			value="<fmt:message key="buttons.save"/>">
<% }
else{%>
<crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_SIMPLE_VEHICLE_CREATE %>">
       <input onclick="javascript:resetSimpleVehicle(<c:out value="${admin_SimpleVehicleAdminForm.simpleVehicleId}"/>)" type="button" class="ViewButtonStyle" style="margin-bottom:4px;" value="<fmt:message key="buttons.reset"/>">
                      &nbsp;&nbsp;
		<input id="saveButton" name="saveButton" onclick="javascript:saveSimpleVehicle(true)" type="button" class="ViewButtonStyle"  style="margin-bottom:4px;" 
			value="<fmt:message key="simplevehicleadmin.buttons.savenew"/>">
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
                <%-----------------------------------NO VEHICLE IS PRESENT -------------------%>
                <%----------------------------------------------------------------------------%>
                
<c:if test = "${empty admin_SimpleVehicleAdminForm.simpleVehicle}">
		<table class="classicblue" style="width:100%;">
			<tr>
				<td>
	       			<c:out value="${admin_SimpleVehicleAdminForm.carrier.displayName}"/> <fmt:message key="simplevehicleadmin.info.nosimplevehicle"/>
	        		<c:if test = "${isAddNewSimpleVehicleAvailable}">
                  		<div align="right">
                     		<input onclick="javascript:addNewSimpleVehicle();" type="button" class="SmallButtonStyle" value=" <fmt:message key="buttons.add"/> "/>
                  		</div>
					</c:if>
				</td>
			</tr>
		</table>    
</c:if>
</c:if>
                
 						</div>
 						<div style="visibility:hidden;width:0px;height:0px" id="changefields"></div>
 						</html:form>
