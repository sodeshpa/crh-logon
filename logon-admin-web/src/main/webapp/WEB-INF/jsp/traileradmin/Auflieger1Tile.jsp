<%@ include file="../includeTLDs.jsp" %>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@page import="com.crh.logon.admin.web.forms.traileradmin.TrailerAdminForm"%>
<%@page import="com.crh.logon.persistence.entities.common.TrailerEntity"%>
<%@page import="com.crh.logon.persistence.entities.refdata.TrailerTrspCatTxEntity"%>
<%@page import="com.crh.logon.persistence.entities.refdata.TrailerTypeTxEntity"%>
<%@page import="com.crh.logon.constants.Constant"%>

<c:set var="VEHICLE_CHARACTERISTIC_INDIVIDUAL"><%=Constant.VEHICLE_CHARACTERISTIC_INDIVIDUAL%></c:set>
<c:set var="VEHICLE_CHARACTERISTIC_GENERIC"><%=Constant.VEHICLE_CHARACTERISTIC_GENERIC%></c:set>
<c:set var="VEHICLE_CHARACTERISTIC_PLANNING"><%=Constant.VEHICLE_CHARACTERISTIC_PLANNING%></c:set>

<script>

	function blockFields() {
		
		var characteristic = <c:out value="${admin_TrailerAdminForm.characteristic}"/>

		if (characteristic == <%=Constant.VEHICLE_CHARACTERISTIC_INDIVIDUAL%>) {
			document.getElementById("inputPlateNr").disabled = true;
			document.getElementById("inputTrailerTransportType").disabled = true;
			document.getElementById("inputTrailerType").disabled = true;
			document.getElementById("inputAxisNumber").disabled = true;
			document.getElementById("inputVolumeM3").disabled = true;
			document.getElementById("inputMaxMeasure").disabled = true;
			document.getElementById("inputTaraMeasure").disabled = true;
		}
		else {
			document.getElementById("inputPlateNr").disabled = false;
			document.getElementById("inputTrailerTransportType").disabled = false;
			document.getElementById("inputTrailerType").disabled = false;
			document.getElementById("inputAxisNumber").disabled = false;
			document.getElementById("inputVolumeM3").disabled = false;
			document.getElementById("inputMaxMeasure").disabled = false;
			document.getElementById("inputTaraMeasure").disabled = false;
		}
	}

	Ext.onReady( function() {
		blockFields();
	}); 
</script>

<table border="0" cellspacing="0" cellpadding="0" >   
                            <colgroup>
                            <col width="45%">
                            <col width="22%">
                            <col width="52%">
                            </colgroup>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.trailerid"/></td>
                              <td colspan="2" class="content"  style="white-space:nowrap;border:0px;">
                              <crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_TRAILER_UPDATE %>"><input style="width:100%" type="text" name="inputTrailerName" id="inputTrailerName" value='</crh-common:authorize><c:out value="${admin_TrailerAdminForm.inputTrailerName}"/><crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_TRAILER_UPDATE %>">' maxlength="30"/></crh-common:authorize>
                              </td>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.plateno"/></td>
                              <td colspan="2" class="content"  style="white-space:nowrap;border:0px;">
                              <crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_TRAILER_UPDATE %>"><input style="width:100%" type="text" name="inputPlateNr" id="inputPlateNr" value='</crh-common:authorize><c:out value="${admin_TrailerAdminForm.inputPlateNr}"/><crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_TRAILER_UPDATE %>">' maxlength="20"/></crh-common:authorize>
                              </td>
                            </tr>
							<tr>
								<td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.sapnumber"/></td>
								<td colspan="2" class="content">
									<input style="width:100%" type="text" name="inputSapVehicleNo" id="inputSapVehicleNo" property="inputSapVehicleNo" disabled="true" value='<c:out value="${admin_TrailerAdminForm.inputSapVehicleNo}"/>' maxlength="<%= String.valueOf(TrailerEntity.DBSIZE_SAP_VEHICLE_NO) %>">
								</td>
							</tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.status"/><c:if test="${admin_TrailerAdminForm.characteristic ne VEHICLE_CHARACTERISTIC_PLANNING and not admin_TrailerAdminForm.inputStatus}"> (<fmt:message key="roadmasterdata.fields.statusdisabledfromsap"/>)</c:if></td>
                              <td colspan="2" class="content"  style="border:0px;"><input type="checkbox" value="1" name="inputStatus" id="inputStatus" <c:if test="${admin_TrailerAdminForm.inputStatus}">checked</c:if> <c:if test="${admin_TrailerAdminForm.inputStatusDisabled}">disabled</c:if>/></td>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.internal"/></td>
                              <td colspan="2" class="content"  style="border:0px;"><input type="checkbox" value="1" name="inputInternalFlag" id="inputInternalFlag" <c:if test="${admin_TrailerAdminForm.inputInternalFlag}">checked</c:if> disabled /></td>
                            </tr>
                            <tr>
                            	<td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.label.characteristic"/></td>
								<td colspan="2" class="content">
									<select disabled="true" style="width:100%" name="characteristic" id="characteristic" property="characteristic" formKey="admin_TrailerAdminForm">
										<option <c:if test="${admin_TrailerAdminForm.characteristic eq VEHICLE_CHARACTERISTIC_INDIVIDUAL}">selected </c:if> value="<%=Constant.VEHICLE_CHARACTERISTIC_INDIVIDUAL%>"><fmt:message key="roadmasterdata.characteristic.trailer.individual"/></option>
										<option <c:if test="${admin_TrailerAdminForm.characteristic eq VEHICLE_CHARACTERISTIC_GENERIC}">selected </c:if> value="<%=Constant.VEHICLE_CHARACTERISTIC_GENERIC%>"><fmt:message key="roadmasterdata.characteristic.trailer.generic"/></option>
										<option <c:if test="${admin_TrailerAdminForm.characteristic eq VEHICLE_CHARACTERISTIC_PLANNING}">selected </c:if> value="<%=Constant.VEHICLE_CHARACTERISTIC_PLANNING%>"><fmt:message key="roadmasterdata.characteristic.trailer.planning"/>
									</select>
								</td>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.tkategorie"/></td>
                              <td colspan="2" class="content">
                              <select name="inputTrailerTransportType" id="inputTrailerTransportType">
					<c:forEach var="entry" items="${admin_TrailerAdminForm.trailerTransportTypeList}">
					<%String entryTrailerCatCd = ((TrailerTrspCatTxEntity)pageContext.getAttribute("entry")).getTrailerTrspCatTxPK().getTrailerTrspCatBs().getTrailerTrspCatCd().toString();%>
					<option value="<%=entryTrailerCatCd%>" <%=(
							((TrailerAdminForm)session.getAttribute("admin_TrailerAdminForm")).getInputTrailerTransportType() != null &&
							entryTrailerCatCd.equals(((TrailerAdminForm)session.getAttribute("admin_TrailerAdminForm")).getInputTrailerTransportType().toString() )
							) ? "selected" : ""%> ><c:out value="${entry.txt}"/></option>
					</c:forEach>
					</select>
					</td>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.trailertype"/></td>
                              <td colspan="2" class="content" style="border:0px;">
<select onchange="" name="inputTrailerType" id="inputTrailerType" >
					<c:forEach var="entry" items="${admin_TrailerAdminForm.trailerTypeList}">
					<%String entryTrailerTypeCd = ((TrailerTypeTxEntity)pageContext.getAttribute("entry")).getTrailerTypeTxPK().getTrailerTypeBs().getTrailerTypeCd().toString();%>
					<option value="<%=entryTrailerTypeCd%>" <%=(
							((TrailerAdminForm)session.getAttribute("admin_TrailerAdminForm")).getInputTrailerType() != null &&
							entryTrailerTypeCd.equals(((TrailerAdminForm)session.getAttribute("admin_TrailerAdminForm")).getInputTrailerType().toString() )
							) ? "selected" : ""%> ><c:out value="${entry.txt}"/></option>
					</c:forEach>
					</select>
					</td>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.manufacturer"/></td>
                              <td colspan="2" class="content"><span class="content" style="border:0px;">
                                <input style="width:100%" type="text" name="inputManufacturer" id="inputManufacturer" value='<c:out value="${admin_TrailerAdminForm.inputManufacturer}"/>' maxlength="20"/>
                              </td>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.axisnumber"/></td>
                              <td colspan="2" class="content"><input style="width:100%" type="text" name="inputAxisNumber" id="inputAxisNumber" value='<c:out value="${admin_TrailerAdminForm.inputAxisNumber}"/>'  maxlength="10"/></td>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.volumen"/></td>
                              <td colspan="2" class="content" style="white-space:nowrap;border:0px;"><input style="width:100%" type="text" name="inputVolumeM3" id="inputVolumeM3" maxlength="6" value='<c:out value="${admin_TrailerAdminForm.inputVolumeM3}"/>'/>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.branding"/></td>
                              <td colspan="2" class="content">
							<select name="inputBranding" id="inputBranding" />
								<option value=""><fmt:message key="option.notappl"/></option>
								<option <c:if test="${admin_TrailerAdminForm.inputBranding}">selected</c:if> value="false"><fmt:message key="option.no"/></option>
								<option <c:if test="${admin_TrailerAdminForm.inputBranding}">selected</c:if> value="true"><fmt:message key="option.yes"/></option>
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
                            <td align="center" class="label">
	                            <fmt:message key="tabsubitem.9.5.3"/>
                            </td>
                            <td class="label">
                            	<fmt:message key="tabsubitem.9.5.4"/>
                            </td>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="border:0px;"><fmt:message key="roadmasterdata.fields.maxmeasure"/></td>
                              <td align="center">	
							<c:choose>
							<c:when test="${!empty admin_TrailerAdminForm.trailer.currentTruck.maxGrossWeightT}">	
							<c:out value="${admin_TrailerAdminForm.trailer.currentTruck.maxGrossWeightT }"/>	
							</c:when>
							<c:otherwise>
							-
							</c:otherwise>   
							</c:choose>
                              </td>
                              <td class="content" ><input style="width:100%" type="text" name="inputMaxMeasure" id="inputMaxMeasure" value='<c:out value="${admin_TrailerAdminForm.inputMaxMeasure}"/>'  maxlength="13"/></td>
                            </tr>
                             <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.tarameasure"/></td>
                              <td align="center">	
							<c:choose>
							<c:when test="${!empty admin_TrailerAdminForm.trailer.currentTruck.tareWeightT}">	
							<c:out value="${admin_TrailerAdminForm.trailer.currentTruck.tareWeightT }"/>	
							</c:when>
							<c:otherwise>
							-
							</c:otherwise>   
							</c:choose>
							</td>
                              <td class="content"><input style="width:100%" type="text" name="inputTaraMeasure" id="inputTaraMeasure" value='<c:out value="${admin_TrailerAdminForm.inputTaraMeasure}"/>' maxlength="13"/></td>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.nrchambers"/></td>
                              <td align="center">	
							<c:choose>
							<c:when test="${!empty admin_TrailerAdminForm.trailer.currentTruck.nrChambers}">	
							<c:out value="${admin_TrailerAdminForm.trailer.currentTruck.nrChambers }"/>	
							</c:when>
							<c:otherwise>
							-
							</c:otherwise>   
							</c:choose>
                              </td>                              
                              <td class="content" style="white-space:nowrap;border:0px;">
							<select onchange="updateChambers()" name="inputNrChambers"  id="inputNrChambers" />
                                  <option <c:if test="${admin_TrailerAdminForm.inputNrChambers eq '0'}">selected=true</c:if> value="0">0</option>
                                  <option <c:if test="${admin_TrailerAdminForm.inputNrChambers eq '1'}">selected=true</c:if> value="1">1</option>
                                  <option <c:if test="${admin_TrailerAdminForm.inputNrChambers eq '2'}">selected=true</c:if> value="2">2</option>
							</select>
							</td>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.chamber1T"/></td>
                              <td align="center">	
							<c:choose>
							<c:when test="${!empty admin_TrailerAdminForm.trailer.currentTruck.maxNetWeightChamber1T}">	
							<c:out value="${admin_TrailerAdminForm.trailer.currentTruck.maxNetWeightChamber1T }"/>	
							</c:when>
							<c:otherwise>
							-
							</c:otherwise>   
							</c:choose>
                              </td>                              
                              
                              <td class="content"><input style="width:100%" type="text" name="inputMaxNetWeightChamber1T" id="inputMaxNetWeightChamber1T" value='<c:out value="${admin_TrailerAdminForm.inputMaxNetWeightChamber1T}"/>' maxlength='13'/></td>
                            </tr>
                            <tr>
                              <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.chamber2T"/></td>
                              <td align="center">	
							<c:choose>
							<c:when test="${!empty admin_TrailerAdminForm.trailer.currentTruck.maxNetWeightChamber2T}">	
							<c:out value="${admin_TrailerAdminForm.trailer.currentTruck.maxNetWeightChamber2T }"/>	
							</c:when>
							<c:otherwise>
							-
							</c:otherwise>   
							</c:choose>
                              </td>                                
                              <td class="content"><input style="width:100%" type="text" name="inputMaxNetWeightChamber2T" id="inputMaxNetWeightChamber2T" value='<c:out value="${admin_TrailerAdminForm.inputMaxNetWeightChamber2T}"/>' maxlength='13'/></td>
                            </tr>                                                                               
                          </table>
                          <br>
                      