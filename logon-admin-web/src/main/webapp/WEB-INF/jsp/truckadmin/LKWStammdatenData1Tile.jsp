<%@ include file="../includeTLDs.jsp" %>
<%@ taglib uri="http://com.crh.logon/tags/admin-taglib.tld" prefix="crh-admin" %>
<%@ page import="com.crh.logon.admin.web.utils.TruckAdminChangeLogic" %>
<%@page import="com.crh.logon.admin.web.forms.truckadmin.TruckAdminForm"%>
<%@page import="com.crh.logon.persistence.entities.common.TruckEntity"%>
<%@page import="com.crh.logon.persistence.entities.refdata.TruckTypeTxEntity"%>
<%@page import="com.crh.logon.persistence.entities.refdata.TruckTransportCatTxEntity"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>

<c:set var="VEHICLE_CHARACTERISTIC_INDIVIDUAL"><%=Constant.VEHICLE_CHARACTERISTIC_INDIVIDUAL%></c:set>
<c:set var="VEHICLE_CHARACTERISTIC_GENERIC"><%=Constant.VEHICLE_CHARACTERISTIC_GENERIC%></c:set>
<c:set var="VEHICLE_CHARACTERISTIC_PLANNING"><%=Constant.VEHICLE_CHARACTERISTIC_PLANNING%></c:set>

<c:set var="TRUCK_PRIORITY_TRUCK_INACTIVE"><%=Constant.TRUCK_PRIORITY_TRUCK_INACTIVE%></c:set>

<table border="0" cellspacing="0" cellpadding="0" width="100%">   
<%-- See details in main tile: LKWStammdatenTile --%>
	
                            <colgroup>
                            <col width="45%">
                            <col width="52%">
                            <col width="22%">
                            </colgroup>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.truckid"/></td>
                              <td colspan="2" class="content"  style="white-space:nowrap;border:0px;">
                              <crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_TRUCK_UPDATE %>"><input style="width:100%" type="text" name="inputTruckName" id="inputTruckName" value='</crh-common:authorize><c:out value="${admin_TruckAdminForm.inputTruckName}"/><crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_TRUCK_UPDATE %>">' maxlength="30"/></crh-common:authorize>
                              </td>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.plateno"/></td>
                              <td colspan="2" class="content"  style="white-space:nowrap;border:0px;">
                              <crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_TRUCK_UPDATE %>"><input style="width:100%" type="text" name="inputPlateNr" id="inputPlateNr" value='</crh-common:authorize><c:out value="${admin_TruckAdminForm.inputPlateNr}"/><crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_TRUCK_UPDATE %>">' maxlength="20"<crh-admin:disableTruckChange formKey="admin_TruckAdminForm" functionality="<%=TruckAdminChangeLogic.FUNCTIONALITY_SET_PLATENO%>">disabled=true</crh-admin:disableTruckChange>/></crh-common:authorize>
                              </td>
                            </tr>
							<tr>
								<td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.sapnumber"/></td>
								<td colspan="2" class="content">
									<input style="width:100%" type="text" name="inputSapVehicleNo" id="inputSapVehicleNo" value='<c:out value="${admin_TruckAdminForm.inputSapVehicleNo}"/>' maxlength="<%=String.valueOf(TruckEntity.DBSIZE_SAP_VEHICLE_NO)%>" <crh-admin:disableTruckChange formKey="admin_TruckAdminForm" functionality="<%=TruckAdminChangeLogic.FUNCTIONALITY_SET_SAP_NO%>">disabled=true</crh-admin:disableTruckChange>/>
								</td>
							</tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.priority"/></td>
                              <td colspan="2" class="content"  style="border:0px;">
                            	<html:select size="1" style="width: 45px;" property="truckPriority">
									<c:forEach var="prio" items="${admin_TruckAdminForm.truckPriorities}">
										<html:option value="${prio}">
											<c:choose> 
												<c:when test="${prio eq TRUCK_PRIORITY_TRUCK_INACTIVE}">
													<fmt:message key="dispo.texte.trucks.truckinactive"/>
												</c:when>
												<c:otherwise>
													<c:out value="${prio}"/>
												</c:otherwise>
											</c:choose>
										</html:option>
									</c:forEach>					
								</html:select>
                              </td>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.status"/><c:if test="${admin_TruckAdminForm.characteristic ne VEHICLE_CHARACTERISTIC_PLANNING and not admin_TruckAdminForm.inputStatus}"> (<fmt:message key="roadmasterdata.fields.statusdisabledfromsap"/>)</c:if></td>
                              <td colspan="2" class="content"  style="border:0px;"><input type="checkbox" value="true" name="inputStatus" id="inputStatus" <c:if test="${admin_TruckAdminForm.inputStatus}">checked</c:if> <c:if test="${admin_TruckAdminForm.inputStatusDisabled}">disabled </c:if>/></td>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.internal"/></td>
                              <td colspan="2" class="content"  style="border:0px;"><input type="checkbox" value="true" name="inputInternalFlag" id="inputInternalFlag" <c:if test="${admin_TruckAdminForm.inputInternalFlag}">checked</c:if> disabled /></td>
                            </tr>
                            <tr>
                            	<td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.label.characteristic"/></td>
								<td colspan="2" class="content">
									<select disabled="true" style="width:100%" name="characteristic" id="characteristic" property="characteristic" formKey="admin_TruckAdminForm">
										<option <c:if test="${admin_TruckAdminForm.characteristic eq VEHICLE_CHARACTERISTIC_INDIVIDUAL}">selected </c:if> value="<%=Constant.VEHICLE_CHARACTERISTIC_INDIVIDUAL%>"><fmt:message key="roadmasterdata.characteristic.truck.individual"/></option>
										<option <c:if test="${admin_TruckAdminForm.characteristic eq VEHICLE_CHARACTERISTIC_GENERIC}">selected </c:if> value="<%=Constant.VEHICLE_CHARACTERISTIC_GENERIC%>"><fmt:message key="roadmasterdata.characteristic.truck.generic"/></option>
										<option <c:if test="${admin_TruckAdminForm.characteristic eq VEHICLE_CHARACTERISTIC_PLANNING}">selected </c:if> value="<%=Constant.VEHICLE_CHARACTERISTIC_PLANNING%>"><fmt:message key="roadmasterdata.characteristic.truck.planning"/></option>
									</select>
								<td>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.tkategorie"/></td>
                              <td colspan="2" class="content">
				<select onchange="renderTypeSelect()" name="inputTkategorie" id="inputTkategorie" property="inputTkategorie" <crh-admin:disableTruckChange formKey="admin_TruckAdminForm" functionality="<%=TruckAdminChangeLogic.FUNCTIONALITY_CHANGE_KATEGORIE%>">disabled=true</crh-admin:disableTruckChange>>
				<%-- Would be good to simplify this. --%>				
					<c:forEach var="entry" items="${admin_TruckAdminForm.truckTkategorieList}">
					<%String entryTruckTypeCd = ((TruckTransportCatTxEntity)pageContext.getAttribute("entry")).getTruckTransportCatTxPK().getTruckTransportCatBs().getTruckTransportCatCd().toString();%>
					<option value="<%=entryTruckTypeCd%>" <%= (
							(((TruckAdminForm)session.getAttribute("admin_TruckAdminForm")).getInputTkategorie() != null &&  
							entryTruckTypeCd.equals(((TruckAdminForm)session.getAttribute("admin_TruckAdminForm")).getInputTkategorie().toString() ))
							) ? "selected" : ""%> ><c:out value="${entry.txt}"/></option>
					</c:forEach>
					</select>
                              </td>
                            </tr>
                            <tr>
  		<td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.trucktype"/></td>
                             <td colspan="2" class="content" style="border:0px;">   
                    <div id="trucktypeselect">                         
                             <select onchange="" name="inputLkwType" id="inputLkwType" property="inputLkwType" <crh-admin:disableTruckChange formKey="admin_TruckAdminForm" functionality="<%=TruckAdminChangeLogic.FUNCTIONALITY_CHANGE_TRUCK_TYPE%>">disabled=true</crh-admin:disableTruckChange>>
						<c:forEach var="truckTypeEntity" varStatus="st" items="${admin_TruckAdminForm.truckTypeMap}">
							<option <c:if test="${truckTypeEntity.key eq admin_TruckAdminForm.inputLkwType}" >selected</c:if> value="<c:out value="${truckTypeEntity.key}"/>"><c:out value="${truckTypeEntity.value.txt}"/></option>
						</c:forEach>

					</select>
					</div>
                            </td>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.manufacturer"/></td>
                              <td colspan="2" class="content"><span class="content" style="border:0px;">
                                <input style="width:100%" type="text" name="inputManufacturer" id="inputManufacturer" maxlength="20" value='<c:out value="${admin_TruckAdminForm.inputManufacturer}"/>'/>
                              </td>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.axisnumber"/></td>
                              <td colspan="2" class="content">
                              	 <crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_TRUCK_UPDATE %>"><input style="width:100%" type="text" name="inputAxisNumber" id="inputAxisNumber" value='<c:out value="${admin_TruckAdminForm.inputAxisNumber}"/>'  maxlength="10" <crh-admin:disableTruckChange formKey="admin_TruckAdminForm" functionality="<%=TruckAdminChangeLogic.FUNCTIONALITY_SET_NUMAXLES%>">disabled=true</crh-admin:disableTruckChange>/></crh-common:authorize>
                              </td>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.euronorm"/></td>
                              <td colspan="2" class="content" style="white-space:nowrap;border:0px;"><span class="content" style="border:0px;">
                                <input style="width:100%" type="text" name="inputEuroNorm" id="inputEuroNorm" value='<c:out value="${admin_TruckAdminForm.inputEuroNorm}"/>'  maxlength="20"/>
                              </span></td>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><span class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.dustfilter"/></span></td>
                              <td colspan="2" class="content" style="white-space:nowrap;border:0px;"><span class="content" style="white-space:nowrap;border:0px;">
							<select name="inputDustfilter" id="inputDustfilter" <crh-admin:disableTruckChange formKey="admin_TruckAdminForm" functionality="<%=TruckAdminChangeLogic.FUNCTIONALITY_CHANGE_DUSTFILTER%>">disabled=true</crh-admin:disableTruckChange> />
								  <option value=""><fmt:message key="option.notappl"/></option>                                
                                  <option <c:if test="${admin_TruckAdminForm.inputDustfilter eq 'false'}">selected=true</c:if> value="false"><fmt:message key="option.no"/></option>
                                  <option <c:if test="${admin_TruckAdminForm.inputDustfilter eq 'true'}">selected=true</c:if> value="true"><fmt:message key="option.yes"/></option>
							</select>
                              </span></td>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.volumen"/></td>
                              <td colspan="2" class="content" style="white-space:nowrap;border:0px;"><input style="width:100%" type="text" name="inputVolumeM3" id="inputVolumeM3" value='<c:out value="${admin_TruckAdminForm.inputVolumeM3}"/>'  maxlength="6" <crh-admin:disableTruckChange formKey="admin_TruckAdminForm" functionality="<%=TruckAdminChangeLogic.FUNCTIONALITY_CHANGE_VOLUMEN%>">disabled=true</crh-admin:disableTruckChange>/>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.branding"/></td>
                              <td colspan="2" class="content" style="white-space:nowrap;border:0px;">
							<select name="inputBranding" id="inputBranding" />
								  <option value=""><fmt:message key="option.notappl"/></option>
                                  <option <c:if test="${admin_TruckAdminForm.inputBranding eq 'false'}">selected=true</c:if> value="false"><fmt:message key="option.no"/></option>
                                  <option <c:if test="${admin_TruckAdminForm.inputBranding eq 'true'}">selected=true</c:if> value="true"><fmt:message key="option.yes"/></option>
							</select>
							</td>
                            </tr>
                            <tr>
                            <td>
                            &nbsp;
                            </td>
                            </tr>            
                            <tr>
                            <td>
                            </td>
                            <td class="label">
	                            <fmt:message key="tabsubitem.9.5.3"/>
                            </td>
                            <td class="label">
                            	<fmt:message key="tabsubitem.9.5.4"/>
                            </td>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="border:0px;"><fmt:message key="roadmasterdata.fields.maxmeasure"/></td>
                              <td class="content" >
                              	<input style="width:100%" type="text" name="inputMaxMeasure" id="inputMaxMeasure" value='<c:out value="${admin_TruckAdminForm.inputMaxMeasure}"/>' maxlength="13" <crh-admin:disableTruckChange formKey="admin_TruckAdminForm" functionality="<%=TruckAdminChangeLogic.FUNCTIONALITY_SET_MAX_WEIGHT%>">disabled=true</crh-admin:disableTruckChange>/>
                              <td id="trailerMaxMeasure" align="center">	
							<c:choose>
							<c:when test="${!empty admin_TruckAdminForm.selectedTrailer.maxGrossWeightT}">	
							<c:out value="${admin_TruckAdminForm.selectedTrailer.maxGrossWeightT }"/>	
							</c:when>
							<c:otherwise>
							-
							</c:otherwise>   
							</c:choose>		                             
                              </td>                                                            
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.tarameasure"/></td>
                              <td class="content">
                              	<input style="width:100%" type="text" name="inputTaraMeasure" id="inputTaraMeasure" value='<c:out value="${admin_TruckAdminForm.inputTaraMeasure}"/>' maxlength="13" <crh-admin:disableTruckChange formKey="admin_TruckAdminForm" functionality="<%=TruckAdminChangeLogic.FUNCTIONALITY_SET_TARE_WEIGHT%>">disabled=true</crh-admin:disableTruckChange>/>
                              <td id="trailerTaraMeasure" align="center">
							<c:choose>
							<c:when test="${!empty admin_TruckAdminForm.selectedTrailer.tareWeightT}">	
							<c:out value="${admin_TruckAdminForm.selectedTrailer.tareWeightT }"/>	
							</c:when>
							<c:otherwise>
							-
							</c:otherwise> 
							</c:choose>		                               
                              </td>                              
                            </tr>                            
                           
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.nrchambers"/></td>
                              <td class="content" style="white-space:nowrap;border:0px;">
							<select style="width:100%" onchange="renderTypeSelect()" name="inputNrChambers" <crh-admin:disableTruckChange formKey="admin_TruckAdminForm" functionality="<%=TruckAdminChangeLogic.FUNCTIONALITY_SET_CHAMBERS%>">disabled=true</crh-admin:disableTruckChange>  id="inputNrChambers" />
                                  <option <c:if test="${admin_TruckAdminForm.inputNrChambers eq '0'}">selected=true</c:if> value="0">0</option>
                                  <option <c:if test="${admin_TruckAdminForm.inputNrChambers eq '1'}">selected=true</c:if> value="1">1</option>
                                  <option <c:if test="${admin_TruckAdminForm.inputNrChambers eq '2'}">selected=true</c:if> value="2">2</option>
							</select>
							</td>	
							<td id="trailerChamberNr" align="center">
							<c:choose>
							<c:when test="${!empty admin_TruckAdminForm.selectedTrailer.nrChambers}">	
							<c:out value="${admin_TruckAdminForm.selectedTrailer.nrChambers }"/>	
							</c:when>
							<c:otherwise>
							-
							</c:otherwise>
							</c:choose>			
							</td>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.chamber1T"/></td>
                              <td class="content"><input style="width:100%" <crh-admin:disableTruckChange formKey="admin_TruckAdminForm" functionality="<%=TruckAdminChangeLogic.FUNCTIONALITY_SET_CHAMBER_1_WEIGHT%>">disabled=true</crh-admin:disableTruckChange>  type="text" name="inputMaxNetWeightChamber1T" id="inputMaxNetWeightChamber1T" value='<c:out value="${admin_TruckAdminForm.inputMaxNetWeightChamber1T}"/>' maxlength='13'/>
							</td>					
							<td id="trailerChamber1T" align="center">							
							<c:choose>
							<c:when test="${!empty admin_TruckAdminForm.selectedTrailer.maxNetWeightChamber1T}">	
							<c:out value="${admin_TruckAdminForm.selectedTrailer.maxNetWeightChamber1T }"/>	
							</c:when>
							<c:otherwise>
							-
							</c:otherwise>
							</c:choose>                              
                              </td>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.chamber2T"/></td>
                              <td class="content"><input style="width:100%" <crh-admin:disableTruckChange formKey="admin_TruckAdminForm" functionality="<%=TruckAdminChangeLogic.FUNCTIONALITY_SET_CHAMBER_2_WEIGHT%>">disabled=true</crh-admin:disableTruckChange> type="text" name="inputMaxNetWeightChamber2T" id="inputMaxNetWeightChamber2T" value='<c:out value="${admin_TruckAdminForm.inputMaxNetWeightChamber2T}"/>' maxlength='13'/>
							</td>						
							<td id="trailerChamber2T" align="center">							
							<c:choose>
							<c:when test="${!empty admin_TruckAdminForm.selectedTrailer.maxNetWeightChamber2T}">	
							<c:out value="${admin_TruckAdminForm.selectedTrailer.maxNetWeightChamber2T }"/>	
							</c:when>
							<c:otherwise>
							-
							</c:otherwise>
							</c:choose> 							
                              </td>
                            </tr>        
							<tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="truckadmin.fields.dispatchingcarrier"/></td>
                              <td class="content">

					<html:select styleId="inputDispatchingCarrierId" property="inputDispatchingCarrierId" style="margin:0px; padding:0px; width:auto;">
					<html:option value="<%=Constant.INVALID_OBJECT_ID_STR %>">Select carrier...</html:option>											
						<c:forEach var="entry" items="${admin_TruckAdminForm.carrierList}">
						<html:option value="${entry.carrierId}"><c:out value="${entry.displayName}"/></html:option>						
						</c:forEach>
					</html:select>
					                            
							</td>						
                            </tr>
					<crh-common:authorize function="<%= Authorization.ITRACK_AVAILABILITY %>">
							<tr>
								<td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.iTrackAvailable"/></td>
								<td colspan="2" class="content"  style="border:0px;"><input type="checkbox" value="true" name="iTrackAvailableFlag" id="iTrackAvailableFlag" <c:if test="${admin_TruckAdminForm.iTrackAvailableFlag}">checked</c:if> /></td>
							</tr>
					</crh-common:authorize>
                          </table>
           				<c:if test="${admin_TruckAdminForm.truckLoaded || admin_TruckAdminForm.truckAssigned || admin_TruckAdminForm.truckInSession}">
                        	  <br>&nbsp;
             			</c:if> 
                         <c:if test="${admin_TruckAdminForm.truckLoaded}">
                         	<fmt:message key="roadmasterdata.fields.loaded"/>
                         </c:if> 
                         <c:if test="${admin_TruckAdminForm.truckAssigned}">
                         	<fmt:message key="roadmasterdata.fields.assigned"/>
                         </c:if>
                         <c:if test="${admin_TruckAdminForm.truckInSession}">
                         	<fmt:message key="roadmasterdata.fields.insession"/>
                         </c:if>