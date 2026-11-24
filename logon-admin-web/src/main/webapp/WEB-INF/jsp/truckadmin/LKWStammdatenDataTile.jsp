<%@ include file="../includeTLDs.jsp" %>
<%@ page import="com.crh.logon.admin.web.utils.TruckAdminChangeLogic" %>
<%@page import="com.crh.logon.admin.web.forms.truckadmin.TruckAdminForm"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@page import="com.crh.logon.persistence.entities.common.TruckEntity"%>

<c:set var="isAddNewTruckAvailable">false</c:set>
<crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_PLANNING_TRUCK_CREATE %>">
<c:set var="isAddNewTruckAvailable">true</c:set>
</crh-common:authorize>
<crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_GENERIC_TRUCK_CREATE %>">
<c:set var="isAddNewTruckAvailable">true</c:set>
</crh-common:authorize>
<script >
function filterTrucks() { 
	document.getElementById("inputAction").value=forwards.SHOW_ONLY_ACTIVE_TRUCKS;
	Ext.get('admin_TruckAdminForm').dom.submit();
} 
</script>                        
<html:form action="truckadmin/TruckAdmin.do" styleId="admin_TruckAdminForm">
    <input type="hidden" name="inputAction" id="inputAction" value=""/>
    <input type="hidden" name="truckId" id="truckId" value='<c:out value="${admin_TruckAdminForm.truckId}"/>'/>
<jsp:include page="LKWStammdatenCarrierTile.jsp"/>
    <c:if test = "${!empty admin_TruckAdminForm.carrier}">              
	<c:if test = "${!empty admin_TruckAdminForm.truck}">
	   
                <table class="classicblue" style="width:100%;height:100%;">
                  <colgroup>
                  <col width="13%">
                  <col width="87%">
                  </colgroup>
                  <tr>
                    <th height="21" colspan="2" class="classicblue"><fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.5"/> / <fmt:message key="tabsubitem.9.5.3"/></th>
                  </tr>
                  <tr>
                    <td><p class="subtitle1"><fmt:message key="roadmasterdata.label.truck"/></p>
                        <!-- beginn Fahrzeug liste -->
                        <html:checkbox styleId="showOnlyActiveTrucks" property="showOnlyActiveTrucks" onclick="filterTrucks()"/>
                        <fmt:message key="roadmasterdata.label.show.only.active.trucks"/>

                        <div id="trucklist" name="trucklist" class="subformlist" style="font-size:12px; margin-top:4px;height:600px">
						<jsp:include page="LKWStammdatenTruckList.jsp"/>          
                        </div>
                      <!-- End Fahrzeugliste -->
                    </td>
                    <td>
			<table class="classicblue" style="width:100%;height:100%;">
			<tr>
                    <td><p class="subtitle1"><fmt:message key="roadmasterdata.label.masterdata"/></p>
                       <div  id = "masterdata1" name="masterdata1" class="subform" style="margin:4px 0px 4px 0px;padding:0px;height:93%">
							<jsp:include page="LKWStammdatenData1Tile.jsp"/>                         
                       </div>
 					</td>
                    <td><p class="subtitle1"><fmt:message key="roadmasterdata.label.truckassignment"/></p>
                        <div id = "masterdata2" name="masterdata2" class="subform" style="margin:4px 0px 4px 0px;padding:0px;height:93%">
                        	<jsp:include page="LKWStammdatenData2Tile.jsp"/>
                        </div>
                      <!-- End Fahrzeugzuordnung -->
                    </td>
                    <td><p class="subtitle1"><fmt:message key="roadmasterdata.label.map"/></p>
                        <div id="masterdata3" name="masterdata3" class="subform" style="margin:4px 0px 4px 0px;padding:0px;height:93%">
                        	<jsp:include page="LKWStammdatenData3Tile.jsp"/>
                        </div>
 					</td>
 					</tr>
			</table>

 					</td>
                  </tr>
                  <tr>
                    <td>
						<c:if test = "${isAddNewTruckAvailable}">                  
                      		<div style="vertical-align:top;" align="right">
                        		<input onclick="javascript:addNewTruck();" type="button" class="SmallButtonStyle" value=" <fmt:message key="buttons.add"/> " /> 
                        	</div>
						</c:if>
                    </td>
                    <td><div align="right">
<crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_TRUCK %>">
<%if (((TruckAdminForm)session.getAttribute("admin_TruckAdminForm")).getTruck() != null) {%>
       <input onclick="javascript:resetTruck(<c:out value="${admin_TruckAdminForm.truckId}"/>)" type="button" class="ViewButtonStyle" style="margin-bottom:4px;" value="<fmt:message key="buttons.reset"/>">
                      &nbsp;&nbsp;
		<input id="saveButton" name="saveButton" onclick="javascript:saveTruck(false)" type="button" class="ViewButtonStyle"  style="margin-bottom:4px;" 
			value="<fmt:message key="buttons.save"/>">
<% }
else{%>
<crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_TRUCK_UPDATE %>">
       <input onclick="javascript:resetTruck(<c:out value="${admin_TruckAdminForm.truckId}"/>)" type="button" class="ViewButtonStyle" style="margin-bottom:4px;" value="<fmt:message key="buttons.reset"/>">
                      &nbsp;&nbsp;
		<input id="saveButton" name="saveButton" onclick="javascript:saveTruck(true)" type="button" class="ViewButtonStyle"  style="margin-bottom:4px;" 
			value="<fmt:message key="truckadmin.buttons.savenew"/>">
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
                <%------------------------------------NO TRUCK IS PRESENT -------------------%>
                <%----------------------------------------------------------------------------%>
                
<c:if test = "${empty admin_TruckAdminForm.truck}">
		<table class="classicblue" style="width:100%;">
			<tr>
				<td>
	       			<c:out value="${admin_TruckAdminForm.carrier.displayName}"/> <fmt:message key="truckadmin.info.notruck"/>
	        		<c:if test = "${isAddNewTruckAvailable}">
                  		<div align="right">
                     		<input onclick="javascript:addNewTruck();" type="button" class="SmallButtonStyle" value=" <fmt:message key="buttons.add"/> "/>
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