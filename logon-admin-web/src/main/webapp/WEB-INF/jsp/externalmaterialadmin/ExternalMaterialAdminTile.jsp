<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: ExternalMaterialAdminTile.jsp,v $ --%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page	import="com.crh.logon.admin.web.actions.externalmaterialadmin.ExternalMaterialAdminAction"%>
<%@page	import="com.crh.logon.persistence.entities.common.ExtMaterialEntity"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@ include file="../includeTLDs.jsp"%>
<script type="text/javascript">
var forwards = {
		SELECT_CARRIER: '<%=ExternalMaterialAdminAction.FORWARD_SELECT_CARRIER%>',
		SAVE: '<%=ExternalMaterialAdminAction.FORWARD_SAVE%>',
		SELECT_MATERIAL: '<%=ExternalMaterialAdminAction.FORWARD_SELECT_MATERIAL%>',
		ADD_MATERIAL: '<%=ExternalMaterialAdminAction.FORWARD_ADD_MATERIAL%>',
		RESET: '<%=ExternalMaterialAdminAction.FORWARD_RESET%>'	
	};

	
	function selectCarrier(){
		document.getElementById("inputAction").value=forwards.SELECT_CARRIER;
		Ext.get('admin_ExternalMaterialAdminForm').dom.submit();
	}

	function saveMaterial(){
		var materialNameLength = <%=ExtMaterialEntity.DBSIZE_MATERIAL_NAME + ""%>;
		var boolName = checkCommentLength("materialNameId", '<fmt:message key="confirm.trim.textarealength"/>','<fmt:message key="extmat.label.name"/>', materialNameLength);
		if (!boolName){
			return false;
		}

		var materialCommentLength = <%=ExtMaterialEntity.DBSIZE_MATERIAL_COMMENT + ""%>;
		var boolComment = checkCommentLength("inputCommentId", '<fmt:message key="confirm.trim.textarealength"/>','<fmt:message key="extmat.label.comment"/>', materialCommentLength);
		if (!boolComment){
			return false;
		}
		document.getElementById("inputAction").value=forwards.SAVE;
		Ext.get('admin_ExternalMaterialAdminForm').dom.submit();
			
	}	

	function selectMaterial(id){
		document.getElementById("materialId").value = id;
		document.getElementById("inputAction").value = forwards.SELECT_MATERIAL;
	    Ext.get('admin_ExternalMaterialAdminForm').dom.submit();
	}	
	
	function addMaterial(){
		document.getElementById("inputAction").value = forwards.ADD_MATERIAL;
	    Ext.get('admin_ExternalMaterialAdminForm').dom.submit();
	}

	function resetFields(){
		document.getElementById("inputAction").value = forwards.RESET;
	    Ext.get('admin_ExternalMaterialAdminForm').dom.submit();
	}
	
	
</script>


<div class="customer_max"><!-- beginn Border-Table   --> 
	<jsp:include page="../SubTab9Tile.jsp" /> <!-- Begin table 3 rows -->
	<html:form action="externalmaterialadmin/ExternalMaterialAdmin.do" styleId="admin_ExternalMaterialAdminForm">
		<table class="big_form2" border="0">
			<tr class="classicblue">
				<th height="21" colspan="4" class="classicblue"><fmt:message
					key="tabitem.masterdata" />  <fmt:message key="tabsubitem.9.5" /></th>
			</tr>
			<tr>
				<td>
					<!-- Beginn Submenu 2-2-1-1 --> <jsp:include page="../Sub5Tab9Tile.jsp" /> 
					<input type="hidden" name="inputAction" id="inputAction" value="" />
					<input type="hidden" name="materialId" id="materialId" value="<c:out value="${admin_ExternalMaterialAdminForm.materialId}"/>" />
			
					<%--------------------------------------------------------------%>
					<%---------------------  Carrier list  -------------------------%>
					<%--------------------------------------------------------------%>
					<div class="subform" style="border-top: 0px;"><!-- Beginn Search Table -->
						<table class="big_form2" border="0">
							<tr>
								<td valign="top" style="border: 0px;">
									<span class="labelblue">
										<fmt:message key="extmat.label.carrier" />
									</span>
									<table cellpadding="0" cellspacing="0" border="0">
										<tr>
											<td valign="bottom"
												style="white-space: nowrap; border: 0px; padding-right: 4px;">
											</td>
											<td style="white-space: nowrap; border: 0px;">
												<span class="content" style="border: 0px;"> 
													<html:select onchange="selectCarrier()" styleId="carrierId" property="carrierId" style="margin:0px; padding:0px; width:auto;">
														<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">
															<fmt:message key="extmat.label.carrierchoose" />
														</html:option>
														<c:forEach var="entry"
															items="${admin_ExternalMaterialAdminForm.carrierMap}">
															<html:option value="${entry.key}">
																<c:out value="${entry.value.displayName}" />
															</html:option>
														</c:forEach>
													</html:select>
												</span>
											</td>
											<td style="border: 0px;"><span class="labelblue"><fmt:message
												key="extmat.label.address" /></span><br>
												<c:out value="${admin_ExternalMaterialAdminForm.carrierName}" /> <br>
												<c:out value="${admin_ExternalMaterialAdminForm.carrierStreet}" /> <br>
												<c:out value="${admin_ExternalMaterialAdminForm.carrierCity}" />
											</td>
										</tr>
									</table>
								</td>

							<%---------------- if ( carrierId != null )  ----------------------------%>
							<c:if test="${admin_ExternalMaterialAdminForm.carrierId != null}">

	

						<table class="classicblue" style="width: 100%;">
							<tr>
								<th height="21" colspan="4"><fmt:message key="tabitem.masterdata" />
								/ <fmt:message key="tabsubitem.9.5" /> / <fmt:message
									key="tabsubitem.9.5.8" /></th>
							</tr>
													
							<%--------------------------------------------------------------%>
							<%------------------- Column 1 - material list-------------------%>
							<%--------------------------------------------------------------%>
							<tr>
								<td>
									<table border="0" width="100%">
										<tr>
											<td width="20%">
												<span class="subtitle1"> <fmt:message key="extmat.label.material" /> </span>
												<div class="scrollist"
													style="margin: 4px 0px 4px 0px; height: 410px; width: 100%;">
													<table cellpadding="0" cellspacing="0" border="0"
														style="border: medium none; width: 100%;">
														<tbody>
															<c:set var="zebracount" value="1" />
															<c:if test="${empty admin_ExternalMaterialAdminForm.materialMap}">
																<td>
																	<a href="#">
																		<fmt:message key="extmat.label.nomaterial" />
																	</a>
																</td>
															</c:if>
															<c:if test="${!empty admin_ExternalMaterialAdminForm.materialMap}">
																<c:forEach var="entry"
																	items="${admin_ExternalMaterialAdminForm.materialMap}">
																	<c:set var="zebracount" value="${(zebracount + 1)%2}" />
																	<tr
																		<c:if test="${entry.key eq admin_ExternalMaterialAdminForm.materialId }">class="selected"</c:if>
																		<c:if test="${entry.key ne admin_ExternalMaterialAdminForm.materialId }"> class="zebra<c:out value="${zebracount}" />" </c:if>>
																		<td><a href="#"
																			onclick='selectMaterial(<c:out value="${entry.key}"/>)'><c:out
																			value="${entry.value.materialName}" /></a></td>
												
																	</tr>
																</c:forEach>
															</c:if>
														</tbody>
													</table>
												</div>
											</td>
											
											
											
											<td width="80%">
												<span class="subtitle1"> <fmt:message key="extmat.label.masterdata" /> </span>
												<div class="scrollist"
													style="margin: 4px 0px 4px 0px; height: 410px; width: 100%;">
												<table width="30%" >
													<%--------------------------------------------------------------%>
													<%----------------------  name field ---------------------------%>
													<%--------------------------------------------------------------%>
													<tr>
														<td width="30%">
															<span class="label"> <fmt:message key="extmat.label.name" /> </span>
														</td>
														<td width="70%">
															<html:text style="width:100%;" styleId="materialNameId" property="materialName" 
																maxlength="<%=ExtMaterialEntity.DBSIZE_MATERIAL_NAME + ""%>" />
														</td>
													</tr>				
													<%--------------------------------------------------------------%>
													<%---------------------   active checkbox ----------------------%>
													<%--------------------------------------------------------------%>
													<tr>				
														<td>
															<span class="label"> <fmt:message key="extmat.label.activeFlag" /> </span> 
														</td>
														<td class="content" style="white-space: nowrap; border: 0px;">
															<html:checkbox property="inputActive"/>
														</td>
													</tr>				
													<%--------------------------------------------------------------%>
													<%--------------------  comment field --------------------------%>
													<%--------------------------------------------------------------%>
													<tr>				
														<td>
															<div style="width: 50%;">
																<span class="label"><fmt:message
																	key="extmat.label.comment" />
																</span>
																<c:set var="textarealength"><%=ExtMaterialEntity.DBSIZE_MATERIAL_COMMENT + ""%></c:set>
															</div>
														</td>
														<td>					
															<html:textarea onkeypress="return checkMaxLength(event, this, ${textarealength})" styleId="inputCommentId" property="inputComment"
																	rows="4" style="width: 98%;">
															</html:textarea>
														</td>
													</tr>			
												</table>
												</div>
											</td>
				
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td>
									<table width="100%">
										<tr>
											<td>
											<div align="left">
												<input onclick="javascript:addMaterial();" type="button"
													class="SmallButtonStyle"
													value=" <fmt:message key="buttons.add"/> ">
											</div>
											</td>
											<td>
												<div align="right">
													<table>
														<tr>
															<td>
																<div align="right">
																	<input id="resetButton" name="resetButton"
																		onclick="javascript:resetFields();" type="button"
																		class="ViewButtonStyle" style="margin-bottom: 4px;"
																		value="<fmt:message key="buttons.reset"/>">
																</div>
															</td>
															<td>
																<div align="left">
																	<input id="saveButton" name="saveButton"
																		onclick="javascript:saveMaterial();" type="button"
																		class="ViewButtonStyle" style="margin-bottom: 4px;"
																		value="<fmt:message key="buttons.save"/>">
																</div>
															</td>
														</tr>
													</table>
												</div>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							</table>

							</c:if>
							<%----------------END:[ if ( carrierId != null ) ] --------------------------%>
							</tr>
						</table>
					</div>
				</td>
			</tr>
		</table>
	</html:form>
</div>
<!-- End Border Table -->
