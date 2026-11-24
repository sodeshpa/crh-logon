<%@ include file="../includeTLDs.jsp" %>
<%@ taglib uri="http://com.crh.logon/tags/admin-taglib.tld" prefix="crh-admin" %>
<%@ page import="com.crh.logon.admin.web.utils.SimpleVehicleAdminChangeLogic" %>
<%@page import="com.crh.logon.admin.web.forms.simplevehicleadmin.SimpleVehicleAdminForm"%>
<%@page import="com.crh.logon.persistence.entities.common.SimpleVehicleEntity"%>
<%@page import="com.crh.logon.persistence.entities.refdata.SimpleVehicleTypeTxEntity"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>

<c:set var="TRUCK_PRIORITY_TRUCK_INACTIVE"><%=Constant.TRUCK_PRIORITY_TRUCK_INACTIVE%></c:set>

<table border="0" cellspacing="0" cellpadding="0" width="100%">   
<%-- See details in main tile: SimpleVehicleStammdatenTile --%>
	
                            <colgroup>
                            <col width="45%">
                            <col width="55%">
                            </colgroup>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.simplevehicleid"/></td>
                              <td class="content"  style="white-space:nowrap;border:0px;">
                              <crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_SIMPLE_VEHICLE_CREATE %>"><input style="width:100%" type="text" name="inputVehicleName" id="inputVehicleName" value='</crh-common:authorize><c:out value="${admin_SimpleVehicleAdminForm.inputVehicleName}"/><crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_SIMPLE_VEHICLE_CREATE %>">' maxlength="30"/></crh-common:authorize>
                              </td>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.vehicleregno"/></td>
                              <td class="content"  style="white-space:nowrap;border:0px;">
                              <crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_SIMPLE_VEHICLE_CREATE %>"><input style="width:100%" type="text" name="inputVehicleRegNo" id="inputVehicleRegNo" value='</crh-common:authorize><c:out value="${admin_SimpleVehicleAdminForm.inputVehicleRegNo}"/><crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_SIMPLE_VEHICLE_CREATE %>">' maxlength="20"<crh-admin:disableSimpleVehicleChange formKey="admin_SimpleVehicleAdminForm" functionality="<%=SimpleVehicleAdminChangeLogic.FUNCTIONALITY_SET_REG_NO%>">disabled=true</crh-admin:disableSimpleVehicleChange>/></crh-common:authorize>
                              </td>
                            </tr>
							<tr>
								<td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.sapnumber"/></td>
								<td class="content">
									<input style="width:100%" type="text" name="inputSapVehicleNo" id="inputSapVehicleNo" value='<c:out value="${admin_SimpleVehicleAdminForm.inputSapVehicleNo}"/>' maxlength="<%=String.valueOf(SimpleVehicleEntity.DBSIZE_SAP_VEHICLE_NO)%>" <crh-admin:disableSimpleVehicleChange formKey="admin_SimpleVehicleAdminForm" functionality="<%=SimpleVehicleAdminChangeLogic.FUNCTIONALITY_SET_SAP_NO%>">disabled=true</crh-admin:disableSimpleVehicleChange>/>
								</td>
							</tr>
							<tr>
								<td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.saptype"/></td>
								<td class="content">
									<input style="width:100%" type="text" name="inputSapVehicleTypeCd" id="inputSapVehicleTypeCd" value='<c:out value="${admin_SimpleVehicleAdminForm.inputSapVehicleTypeCd}"/>' maxlength="<%=String.valueOf(SimpleVehicleEntity.DBSIZE_SAP_VEHICLE_TYPE_CD)%>" <crh-admin:disableSimpleVehicleChange formKey="admin_SimpleVehicleAdminForm" functionality="<%=SimpleVehicleAdminChangeLogic.FUNCTIONALITY_SET_SAP_TYPE%>">disabled=true</crh-admin:disableSimpleVehicleChange>/>
								</td>
							</tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.priority"/></td>
                              <td class="content"  style="border:0px;">
                            	<html:select size="1" style="width: 45px;" property="simpleVehiclePriority">
									<c:forEach var="prio" items="${admin_SimpleVehicleAdminForm.simpleVehiclePriorities}">
										<html:option value="${prio}">
											<c:choose> 
												<c:when test="${prio eq TRUCK_PRIORITY_TRUCK_INACTIVE}">
													<fmt:message key="dispo.texte.simplevehicles.simplevehicleinactive"/>
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
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.status"/></td>
                              <td class="content"  style="border:0px;"><input type="checkbox" value="true" name="inputStatus" id="inputStatus" <c:if test="${admin_SimpleVehicleAdminForm.inputStatus}">checked</c:if>/></td>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.internal"/></td>
                              <td class="content"  style="border:0px;"><input type="checkbox" value="true" name="inputInternal" id="inputInternal" <c:if test="${admin_SimpleVehicleAdminForm.inputInternal}">checked</c:if>/></td>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.companycode"/></td>
                              <td class="content">
                                <input style="width:100%" type="text" name="inputCompanycodeCd" id="inputCompanycodeCd" value='<c:out value="${admin_SimpleVehicleAdminForm.inputCompanycodeCd}"/>' maxlength="<%=String.valueOf(SimpleVehicleEntity.DBSIZE_COMPANYCODE_CD)%>" <crh-admin:disableSimpleVehicleChange formKey="admin_SimpleVehicleAdminForm" functionality="<%=SimpleVehicleAdminChangeLogic.FUNCTIONALITY_SET_COMPANYCODE%>">disabled=true</crh-admin:disableSimpleVehicleChange>/>
                              </td>
                            </tr>
                            <tr>
  								<td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.simplevehicletype"/></td>
                             	<td class="content" style="border:0px;">   
				                    <div id="simpleVehicletypeselect">                         
				                        <select onchange="" name="inputSimpleVehicleTypeCd" id="inputSimpleVehicleTypeCd" property="inputSimpleVehicleTypeCd" <crh-admin:disableSimpleVehicleChange formKey="admin_SimpleVehicleAdminForm" functionality="<%=SimpleVehicleAdminChangeLogic.FUNCTIONALITY_CHANGE_VEHICLE_TYPE%>">disabled=true</crh-admin:disableSimpleVehicleChange>>
											<c:forEach var="simpleVehicleTypeEntity" varStatus="st" items="${admin_SimpleVehicleAdminForm.simpleVehicleTypeMap}">
												<option <c:if test="${simpleVehicleTypeEntity.key eq admin_SimpleVehicleAdminForm.inputSimpleVehicleTypeCd}" >selected</c:if> value="<c:out value="${simpleVehicleTypeEntity.key}"/>"><c:out value="${simpleVehicleTypeEntity.value.txt}"/></option>
											</c:forEach>
										</select>
									</div>
				                 </td>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.volumen"/></td>
                              <td class="content" style="white-space:nowrap;border:0px;"><input style="width:100%" type="text" name="inputVolumeM3" id="inputVolumeM3" value='<c:out value="${admin_SimpleVehicleAdminForm.inputVolumeM3}"/>'  maxlength="6" <crh-admin:disableSimpleVehicleChange formKey="admin_SimpleVehicleAdminForm" functionality="<%=SimpleVehicleAdminChangeLogic.FUNCTIONALITY_CHANGE_VOLUME%>">disabled=true</crh-admin:disableSimpleVehicleChange>/>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.branding"/></td>
                              <td class="content" style="white-space:nowrap;border:0px;">
								<select name="inputBranding" id="inputBranding">
									  <option value=""><fmt:message key="option.notappl"/></option>
	                                  <option <c:if test="${admin_SimpleVehicleAdminForm.inputBranding eq 'false'}">selected=true</c:if> value="false"><fmt:message key="option.no"/></option>
	                                  <option <c:if test="${admin_SimpleVehicleAdminForm.inputBranding eq 'true'}">selected=true</c:if> value="true"><fmt:message key="option.yes"/></option>
								</select>
							</td>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="border:0px;"><fmt:message key="roadmasterdata.fields.maxmeasure"/></td>
                              <td class="content" style="white-space:nowrap;border:0px;">
                              	<input style="width:100%" type="text" name="inputMaxMeasure" id="inputMaxMeasure" value='<c:out value="${admin_SimpleVehicleAdminForm.inputMaxMeasure}"/>' maxlength="13" <crh-admin:disableSimpleVehicleChange formKey="admin_SimpleVehicleAdminForm" functionality="<%=SimpleVehicleAdminChangeLogic.FUNCTIONALITY_SET_MAX_WEIGHT%>">disabled=true</crh-admin:disableSimpleVehicleChange>/>
                              </td>                 
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.tarameasure"/></td>
                              <td class="content" style="white-space:nowrap;border:0px;">
                              	<input style="width:100%" type="text" name="inputTaraMeasure" id="inputTaraMeasure" value='<c:out value="${admin_SimpleVehicleAdminForm.inputTaraMeasure}"/>' maxlength="13" <crh-admin:disableSimpleVehicleChange formKey="admin_SimpleVehicleAdminForm" functionality="<%=SimpleVehicleAdminChangeLogic.FUNCTIONALITY_SET_TARE_WEIGHT%>">disabled=true</crh-admin:disableSimpleVehicleChange>/>
                              </td>                            
                            </tr>                            
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.unitcount"/></td>
                              <td class="content" style="white-space:nowrap;border:0px;">
								<select style="width:45px;" onchange="renderTypeSelect()" name="inputUnitCount" <crh-admin:disableSimpleVehicleChange formKey="admin_SimpleVehicleAdminForm" functionality="<%=SimpleVehicleAdminChangeLogic.FUNCTIONALITY_SET_UNIT_COUNT%>">disabled=true</crh-admin:disableSimpleVehicleChange>  id="inputUnitCount" >
									<c:forEach var="count" items="${admin_SimpleVehicleAdminForm.validUnitCounts}">
										<option <c:if test="${admin_SimpleVehicleAdminForm.inputUnitCount eq count}">selected=true</c:if> value="<c:out value="${count}"/>"><c:out value="${count}"/></option>
									</c:forEach>	
								</select>
							  </td>	
                            </tr>                             
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.nrchambers"/></td>
                              <td class="content" style="white-space:nowrap;border:0px;">
								<select style="width:45px;" onchange="renderTypeSelect()" name="inputNrChambers" <crh-admin:disableSimpleVehicleChange formKey="admin_SimpleVehicleAdminForm" functionality="<%=SimpleVehicleAdminChangeLogic.FUNCTIONALITY_SET_NR_CHAMBERS%>">disabled=true</crh-admin:disableSimpleVehicleChange>  id="inputNrChambers" >
									<c:forEach var="nrChambers" items="${admin_SimpleVehicleAdminForm.validNrChambers}">
										<option <c:if test="${admin_SimpleVehicleAdminForm.inputNrChambers eq nrChambers}">selected=true</c:if> value="<c:out value="${nrChambers}"/>"><c:out value="${nrChambers}"/></option>
									</c:forEach>	
								</select>
							  </td>	
                            </tr>                                                                                           
                          </table>    
           				<c:if test="${admin_SimpleVehicleAdminForm.simpleVehicleLoaded || admin_SimpleVehicleAdminForm.simpleVehicleAssigned || admin_SimpleVehicleAdminForm.simpleVehicleInSession}">
                        	  <br>&nbsp;
             			</c:if> 
                         <c:if test="${admin_SimpleVehicleAdminForm.simpleVehicleLoaded}">
                         	<fmt:message key="roadmasterdata.fields.loaded"/>
                         </c:if> 
                         <c:if test="${admin_SimpleVehicleAdminForm.simpleVehicleAssigned}">
                         	<fmt:message key="roadmasterdata.fields.assigned"/>
                         </c:if>
                         <c:if test="${admin_SimpleVehicleAdminForm.simpleVehicleInSession}">
                         	<fmt:message key="roadmasterdata.fields.insession"/>
                         </c:if>