<%@ include file="../includeTLDs.jsp"%>
<%@ page import="com.crh.logon.persistence.entities.common.LoadingpointEntity"%>
<%@ page import="com.crh.logon.taglib.fallbacklogic.DefaultFallBackLogicTag" %>
<%@ page import="com.crh.logon.admin.web.forms.masterdata.ShippingPointForm"%>
<%@ page import="com.crh.logon.admin.web.forms.masterdata.LoadingPointBean"%>
<%@ page import="com.crh.logon.web.forms.RFIDPopupForm" %>
<%@ page import="com.crh.logon.constants.Constant"%>

<script type="text/javascript" src="<crh-common:url value='/dwr/interface/AdminRFIDPopupAjaxService.js'/>"></script>

<c:set var="form" scope="page" value="${admin_ShippingPointForm}" />
<c:set var="currLoadingPoint" value="${form.loadingPointWrapperList[form.currLoadingPointIndex]}" />

<script type="text/javascript">
	function updateWeighSystemNoField(weighSystemLpCd) {
		var weighSystemLpNoDisabled = true;
		
		if (weighSystemLpCd == constants.WEIGH_SYSTEM_LP_WS) {
			weighSystemLpNoDisabled = false;
		}
		
		Ext.getDom('weighSystemLpNo').disabled = weighSystemLpNoDisabled;
	}

	Ext.onReady(function() {
		var weighSystemLp = Ext.getDom('weighSystemLp');
		
		if (weighSystemLp) {
			updateWeighSystemNoField(weighSystemLp.options[weighSystemLp.selectedIndex].value);
		}
	});
</script>


<c:if
	test="${(not empty form.loadingPointWrapperList) && (form.emptyShippingPoint == true)}">



	<table border="0" cellpadding="0" cellspacing="0"
		style="margin-top: 10px; width: 100%;">
		<tbody>
			<tr>
				<td width="100%">
				<div id="LoadingStreetTabs" style="margin-top: 10px;">
				<ul id="subnavigation">
					<c:forEach var="loadingPointList" varStatus="status"
						items="${form.loadingPointWrapperList}">
						<c:if test="${loadingPointList.lpEntity.activeFlag==true}">
							<li><a
								href="javascript:setCurrentLPoint(<c:out value="${status.count - 1}"/>)"
								id='TabLS<c:out value="${status.count - 1}"/>'
								<c:if test="${form.currLoadingPointIndex==(status.count - 1)}"> class="highlight"</c:if>>
							<span class="outer"><span class="inner"><c:out
								value="${loadingPointList.lpEntity.loadingpointName}" /> </span></span></a></li>
						</c:if>
					</c:forEach>
				</ul>
				</div>
				</td>
			</tr>
			<tr>
				<td><!--<html:form action="ShippingPoint.do" onsubmit="return false;" >-->
				<table class="big_form2" style="width: 100%;">
					<tbody>
						<tr>
							<td style="width: 25%;">
							<table class="classic" style="border: none; width: 100%">
								<tbody>
									<tr>
										<td class="label left"><fmt:message
											key="shippingpoint.lpoint.field.name" /></td>
										<td style="text-align: right;"><html:hidden
											property="loadingPointWrapperList[${form.currLoadingPointIndex}].lpEntity.loadingpointId" />

										<html:text style="width: 200px;"
											property="loadingPointWrapperList[${form.currLoadingPointIndex}].lpEntity.loadingpointName"
											maxlength="<%= LoadingpointEntity.DBSIZE_LOADINGPOINT_NAME + "" %>" styleId="loadingpointName"/>

										</td>
									</tr>
									<tr>
										<td class="label left"><fmt:message
											key="shippingpoint.lpoint.field.packageType" /></td>
										<td style="text-align: right;"><html:select size="1"
											style="width:200px;"
											onchange="javascript:loadMaterials(this.options[this.selectedIndex].value)"
											property="loadingPointWrapperList[${form.currLoadingPointIndex}].packageTypeCd">
											<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">-</html:option>
											<c:forEach var="packTypeList" varStatus="status"
												items="${form.packageTypeList}">
												<html:option value="${packTypeList.packageTypeTxPK.packageTypeBs.packageTypeCd}">
													<c:out value="${packTypeList.txt}" />
												</html:option>
											</c:forEach>
										</html:select></td>
									</tr>
									<tr>
										<td class="label left"><fmt:message
											key="shippingpoint.lpoint.field.weighingType" /></td>
										<td style="text-align: right;"><nobr>
											<html:select size="1"
											style="width:200px;" styleId="weighSystemLp"
											onchange="javascript:updateWeighSystemNoField(this.options[this.selectedIndex].value)"
											property="loadingPointWrapperList[${form.currLoadingPointIndex}].weighSystemLpCd">
												<c:forEach var="element" varStatus="status"
													items="${form.weighSystemLpList}">
													<html:option value="${element.weighSystemLpTxPK.weighSystemLpBs.weighSystemLpCd}">
														<c:out value="${element.txt}" />
													</html:option>
												</c:forEach>
											</html:select>
										</nobr></td>
									</tr>
									<tr>
										<td class="label left"><nobr> 
										<fmt:message key="shippingpoint.lpoint.field.weighSystemLpNo" />
										</nobr></td>
										<td style="text-align: right;"><nobr><html:text
											style="width: 200px;" styleId="weighSystemLpNo"
											disabled="true"
											property="loadingPointWrapperList[${form.currLoadingPointIndex}].lpEntity.weighSystemLoadingpointNo"
											maxlength="<%= LoadingpointEntity.DBSIZE_WEIGH_SYSTEM_LOADINGPOINT_NO + "" %>" /></nobr></td>
									</tr>
									<tr>
										<td class="label left" colspan="2"
											style="padding-bottom: -5px"><nobr><fmt:message
											key="shippingpoint.lpoint.field.parameter" /></nobr></td>
									</tr>
									<tr>
										<td colspan="2" class="left">
										<table class="classic" style="border: none">
											<tr>
												<td class="label left"><nobr><fmt:message key="shippingpoint.lpoint.field.loadingTime" /></nobr></td>
												<td><nobr>
	<%		
	ShippingPointForm form = ((ShippingPointForm)request.getSession().getAttribute("admin_ShippingPointForm"));
	int currIndex = form.getCurrLoadingPointIndex();
		if (form.getCurrLoadingPointIndex() >= 0 && form.getCurrLoadingPointIndex() < form.getLoadingPointWrapperList().size()){			
			LoadingPointBean lpBean = ((LoadingPointBean) form.getLoadingPointWrapperList().get(form.getCurrLoadingPointIndex()));
			String lTime = lpBean.getLoadingTime();
			pageContext.setAttribute("loadingTime", lTime);
		}
	%>  										
			<crh-common:defaultFallback propertyValue="<%=(String)pageContext.getAttribute("loadingTime") %>"
				 propertyName="<%=DefaultFallBackLogicTag.PROPERTY_DEFAULT_LOADINGPOINT_LOADING_TIME_MIN%>" propertyInputFieldName="<%="loadingPointWrapperList["+ currIndex + "].loadingTime"%>" propertyCheckboxName="<%="loadingPointWrapperList["+ currIndex + "].loadingTimeFlag"%>">											
								<crh-common:defaultFallbackInputField style="width:130px;"/>&nbsp;&nbsp;
								<crh-common:defaultFallbackCheckbox/>
			</crh-common:defaultFallback>
												</nobr>
												</td>
											</tr>
											<tr>
												<td class="label left"><nobr><fmt:message
													key="shippingpoint.lpoint.field.gross" /></nobr></td>
												<td><nobr><html:text style="width:130px;"
													property="loadingPointWrapperList[${form.currLoadingPointIndex}].grossWeightLimitKg" /></nobr></td>
											</tr>
											
											<tr>
												<td class="label left"><nobr><fmt:message
														key="shippingpoint.lpoint.leave.delay" /></nobr></td>
												<td>
												<nobr>
												<html:text style="width:130px;" property="loadingPointWrapperList[${form.currLoadingPointIndex}].leaveLpDelayMin" />
												</nobr>
												</td>
											</tr>
											
										</table>
										</td>
									</tr>
									<tr>
										<td class="label left" colspan="2" style="padding-bottom: 0px"><nobr><fmt:message
											key="shippingpoint.lpoint.field.desc" /></nobr></td>
									</tr>
									<tr>
										<td class="left" colspan="2"><html:textarea
											property="loadingPointWrapperList[${form.currLoadingPointIndex}].lpEntity.loadingpointComment"
											rows="3" style="width: 100%;"></html:textarea></td>
									</tr>
								</tbody>
							</table>
							</td>
							<td style="width: 25%;">
							<table class="classic" style="border: none; width: 100%">
								<tbody>
									<tr>
										<td class="label left"><fmt:message
											key="shippingpoint.lpoint.field.object.label" /></td>
										<td style="text-align: right;">
										<html:text style="width: 200px;"
											property="loadingPointWrapperList[${form.currLoadingPointIndex}].lpEntity.loadingpointObjectLabel"
											maxlength="<%= LoadingpointEntity.DBSIZE_LOADINGPOINT_OBJECT_LABEL + "" %>" styleId="loadingpointObjectLabel"/>

										</td>
									</tr>
									<tr>
										<td class="label left"><fmt:message
											key="shippingpoint.lpoint.field.delivNote" /></td>
										<td style="text-align: right;"><nobr><html:select size="1"
											style="width: 200px;"
											onchange="javascript:selectDeliveryNote(this.options[this.selectedIndex].value)"
											property="loadingPointWrapperList[${form.currLoadingPointIndex}].delivNotePrintTypeCd">
											<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">-</html:option>
											<c:forEach var="delivNotePrintTypeList" varStatus="status"
												items="${form.delivNotePrintTypeList}">
												<html:option
													value="${delivNotePrintTypeList.delivNotePrintTypeTxPK.delivNotePrintTypeBs.delivNotePrintTypeCd}">
													<c:out value="${delivNotePrintTypeList.txt}" />
												</html:option>
											</c:forEach>
										</html:select></nobr></td>
									</tr>
									<tr>
										<td class="label left"><nobr><fmt:message
													key="shippingpoint.lpoint.field.printer" /></nobr></td>
										<td style="text-align: right;"><nobr><c:set
													var="DELIV_NOTE_PRINT_TYPE_LOGON">
													<%=Constant.DELIV_NOTE_PRINT_TYPE_LOGON%>
												</c:set> <c:set var="enabledPrinter">
													<c:choose>
														<c:when
															test="${currLoadingPoint.delivNotePrintTypeCd == DELIV_NOTE_PRINT_TYPE_LOGON}">false</c:when>
														<c:otherwise>true</c:otherwise>
													</c:choose>
												</c:set> 
													<html:select size="1"
														style="width: 200px;" styleId="printerCd" disabled="${enabledPrinter}"
														property="loadingPointWrapperList[${form.currLoadingPointIndex}].printerCd">
														<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">-</html:option>
														<c:forEach var="printerList" varStatus="status"
															items="${form.printerList}">
															<html:option value="${printerList.printerCd}">
																<c:out value="${printerList.printerName}" />
															</html:option>
														</c:forEach>
													</html:select>
										</nobr></td>
									</tr>
									<tr>
										<td class="label left"><nobr><fmt:message key="shippingpoint.lpoint.field.delivNoteCopies" /></nobr></td>
										<td style="text-align: right;"><nobr>
											<html:text styleId="delivNoteCopiesLogon" property="loadingPointWrapperList[${form.currLoadingPointIndex}].delivNoteCopiesLogon"
												 disabled="${enabledPrinter}" style="width: 200px;" />
										</nobr></td>
									</tr>
								</tbody>
							</table>
							</td>
							<td style="width: 25%;">
							<p class="label margintop8"><fmt:message
								key="shippingpoint.lpoint.field.rfid" /></p>
                          <!-- beginn RFID -->
                          <div class="subformlist" id="<%=RFIDPopupForm.NAME_OF_HTML_DIV_TO_RELOAD%>" style="clear:both;font-size:12px; margin:0px 0px 0px 0px;">
<jsp:include page="../AdminRfidsTile.jsp"/>    
                          </div>
                          <div align="right">
                            <input onclick="javascript:updateObjectLabelShowRFIDPopup();" type="button" class="SmallButtonStyle" value=" <fmt:message key="buttons.add"/> ">
                          </div>
                          <!-- End RFID -->							
							</td>

							<td style="width: 25%;">
							<p class="label margintop8"><fmt:message
								key="shippingpoint.lpoint.field.products" /></p>

							<div class="scrollist" id="materialsDiv" style="height: 250px;">
							<jsp:include page="Materials.jsp"></jsp:include></div>
							<p class="buttons right" style="vertical-align: top"><input
								type="button" class="SmallButtonStyle"
								value="<fmt:message
								key="shippingpoint.button.check" />"
								onclick="javascript:checkAllMaterials()"> <input
								type="button" class="SmallButtonStyle"
								value="<fmt:message
								key="shippingpoint.button.uncheck" />"
								onclick="javascript:uncheckAllMaterials()"></p>
							</td>
						</tr>
					</tbody>
				</table>
				<!--</html:form>--></td>
			</tr>
		</tbody>
	</table>
</c:if>