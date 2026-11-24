<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: ShippingPoint.jsp,v $ --%>
<%@ include file="../includeTLDs.jsp"%>
<%@ page import="com.crh.logon.admin.web.actions.masterdata.ShippingPointAction" %>
<%@ page import="com.crh.logon.constants.Constant" %>
<%@ page import="com.crh.logon.persistence.entities.common.MatShippingpointEntity" %>
<%@ page import="com.crh.logon.persistence.entities.common.ShippingpointEntity" %>
<%@ page import="com.crh.logon.web.utils.MessageUtils" %>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>

<c:set var="shippingPointUrl">
    /../admin/ShippingPoint.do
</c:set>

<script type="text/javascript" src="<crh-common:url value='/js/masterdata/shipping-point-map.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/logon-gmap-common.js'/>"></script>
<script type="text/javascript">
    var texts = {
        exjsloading: '<fmt:message key="shippingpoint.text.exjsloading"/>',
        messageBoxDelete: '<fmt:message key="shippingpoint.messagebox.delete"/>',
        messageBoxCancel: '<fmt:message key="shippingpoint.messagebox.cancel"/>',
        messageBoxConfirm: '<fmt:message key="shippingpoint.messagebox.confirm"/>',
        messageBoxText: '<fmt:message key="shippingpoint.messagebox.text"/>'
    };

    function searchRailwayStation() {
        showPopup('RailwayStationPopup.do');
    }

    function editSendSapSp() {
        showPopup('ShippingPointSendSapSpPopupEntry.do?shippingpointId=' + Ext.getDom('sendSapSpShippingpointId').value);
    }

    function deleteSendSapSp() {
        Ext.getDom('sendSapSpShippingpointId').value = '';
        Ext.getDom('sendSapSpShippingpointName').value = '';
        Ext.getDom('sendSapSpShippingpointNo').value = '';
        updateSpanSendSapSp();
    }

    function updateSpanSendSapSp() {
        var spanSendSapSpValue = '';

        var sendSapSpShippingpointIdValue = Ext.getDom('sendSapSpShippingpointId').value;
        if (sendSapSpShippingpointIdValue != null && sendSapSpShippingpointIdValue != "") {
            var sendSapSpShippingpointNameValue = Ext.getDom('sendSapSpShippingpointName').value;
            var sendSapSpShippingpointNoValue = Ext.getDom('sendSapSpShippingpointNo').value;
            spanSendSapSpValue = sendSapSpShippingpointNameValue + " (" + sendSapSpShippingpointNoValue + ")";
            Ext.get('sendSapSpDeleteIcon').setVisible(true, false);
        }
        else {
            Ext.get('sendSapSpDeleteIcon').setVisible(false, false);
        }

        Ext.get('spanSendSapSp').update(spanSendSapSpValue);
    }

    updateSpanSendSapSp();

    var futureValidFromDat;

    function initForm() {
        futureValidFromDat = new Ext.form.DateField({
            id: 'futureValidFromDat',
            format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
	    	altFormats: '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>',
	    	applyTo: 'futureValidFromDat',
	    	value: '<c:out value="${admin_ShippingPointForm.futureValidFromDat}" />',
	        allowBlank: true,
		    disabled: false,
	    	width: 100
		} );
	}
	
	initForm();

	function storageLocationChange(checkbox, textbox) {
		var useDefaultStorageLocation = Ext.getDom(checkbox).checked;
		Ext.getDom(textbox).readOnly = useDefaultStorageLocation;
		if (useDefaultStorageLocation) {
			Ext.getDom(textbox).value = Ext.getDom('shippingPoint.storageLocation').value;
		}
	}

	function defaultStorageLocationChanged() {
		var defaultStorageLocation = Ext.getDom('shippingPoint.storageLocation').value;

		var i = 0;
		while (Ext.getDom('storageLocationBeanList[' + i + '].storageLocationIsDefault') != null) {
			if (Ext.getDom('storageLocationBeanList[' + i + '].storageLocationIsDefault').checked) {
				Ext.getDom('storageLocationBeanList[' + i + '].storageLocation').value = defaultStorageLocation;
			}
			i++;
		}
	}

	function changeLdsType(ldsType) {

		var diva = Ext.get('serverDiv');
		diva.load( {
			url :'<c:url value="${shippingPointUrl}"/>',
			params : {
				'formAction' : '<%=ShippingPointAction.FORWARD_LOAD_LDS_SERVER%>',
				'ldsType'    : ldsType
			},
			text : texts.exjsloading,
			scripts : true
		});
	}

</script>
<script type="text/javascript" src="<crh-common:url value='/js/masterdata/ShippingPoint.js'/>"></script>

<html:form action="ShippingPoint.do" styleId="ShippingPointForm">
	<html:hidden property="formAction" value="" />
	<html:hidden styleId="sendSapSpShippingpointId" property="sendSapSpShippingpointId" value="${admin_ShippingPointForm.sendSapSpShippingpointId}" />
	<html:hidden styleId="sendSapSpShippingpointName" property="sendSapSpShippingpointName" value="${admin_ShippingPointForm.sendSapSpShippingpointName}" />
	<html:hidden styleId="sendSapSpShippingpointNo" property="sendSapSpShippingpointNo" value="${admin_ShippingPointForm.sendSapSpShippingpointNo}" />

	<!-- beginn Border-Table   -->
	<table class="big_form2" cellpadding="0" cellspacing="0" border="0"
		width="100%" style="padding: 0px; margin: 0px;">

		<tr>
			<!-- Begin content Lieferschein Informationen -->
			<td class="border" style="width: 30%;"><!-- Main form -->

			<table class="classic"
				style="border: none; margin-top: 10px; width: 100%">
				<tbody>
					<tr>
						<td colspan="5" class="border">
							<table>
								<tr>
									<td>
										<table class="classic" style="border: none; width: 100%">
											<tbody>
												<tr>
													<td class="label left">
														<nobr>
															<fmt:message key="shippingpoint.field.plants" />
														</nobr>
													</td>
													<td>
														<c:out value="${admin_ShippingPointForm.shippingPoint.plant.plantName}" /> (<c:out value="${admin_ShippingPointForm.shippingPoint.plant.plantCd}" />)
													</td>
												</tr>
												<tr>
													<td class="label left">
														<nobr>	
															<fmt:message key="shippingpoint.field.shippingpoints" />
															&nbsp;&nbsp;
															<html:text styleId="shippingPoint.sapShippingpointNo" property="shippingPoint.sapShippingpointNo"
																maxlength="<%= String.valueOf(ShippingpointEntity.DBSIZE_SAP_SHIPPINGPOINT_NO) %>" style="width: 50px;" />
														</nobr>
													</td>
													<td>
														<html:text styleId="shippingPoint.shippingpointName" property="shippingPoint.shippingpointName"
															maxlength="<%= String.valueOf(ShippingpointEntity.DBSIZE_SHIPPINGPOINT_NAME) %>" style="width: 150px;" />
													</td>
												</tr>
												<tr>
													<td class="label left">
														<nobr>	
															<fmt:message key="shippingpoint.field.sendSapSp" />
														</nobr>
													</td>
													<td>
														<span id="spanSendSapSp"></span>
														&nbsp;&nbsp;
														<a href="javascript:editSendSapSp();"><img src="<crh-common:url value='images/silk/pencil.png'/>" /></a>
														&nbsp;&nbsp;
														<a id="sendSapSpDeleteIcon" href="javascript:deleteSendSapSp();"><img src="<crh-common:url value='images/delete2.png'/>" /></a>
													</td>
												</tr>
												<tr>
													<td class="label left">
														<nobr>	
															<fmt:message key="shippingpoint.field.sapShippingpoint" />
														</nobr>
													</td>
													<td>
														<html:checkbox property="shippingPoint.knownAlsoInSapFlag" styleId="shippingPoint.knownAlsoInSapFlag" />
													</td>
												</tr>
												<tr>
													<td class="label left">
														<nobr>	
															<fmt:message key="shippingpoint.field.mixShiptoPerWagonFlag" />
														</nobr>
													</td>
													<td>
														<html:checkbox property="shippingPoint.fvlMixShiptoPerWagonFlag" styleId="fvlMixShiptoPerWagonFlag" disabled="${!admin_ShippingPointForm.FVLShippinpoint}" />
													</td>
												</tr>
												<tr>
													<td class="label left">
														<nobr>	
															<fmt:message key="shippingpoint.field.railArrivalWarningMin" />
														</nobr>
													</td>
													<td>
														<html:text style="width:50px;" property="railArrivalWarningMin" maxlength="5" styleId="railArrivalWarningMin" disabled="${!admin_ShippingPointForm.FVLShippinpoint}"/>
													</td>
												</tr>
											</tbody>
										</table>
									</td>
									<td>
										<div id="railwayStationDiv">
											<jsp:include page="RailwayStation.jsp"></jsp:include>									
										</div>
										<table>
											<tr>
												<td class="label left" width="110" style="vertical-align: middle;">
													<fmt:message key="shippingpoint.field.railloadingpoint"/>
												</td>
												<td colspan="2" style="vertical-align: middle;">
													<html:text style="width:50px;" property="shippingPoint.railwayStationLoadingPoint" maxlength="4" />
													&nbsp;&nbsp;&nbsp;
													<a href="javascript:searchRailwayStation()">
														<img src="<crh-common:url value='images/silk/pencil.png'/>" style="vertical-align: top;"/>
													</a>
												</td>
											</tr>
											<tr>
												<td class="label left" width="110">
													<fmt:message key="shippingpoint.field.sbbCustomerNumber"/>
												</td>
												<td colspan="2">
													<html:text styleId="shippingPoint.sbbCustomerNumber" property="shippingPoint.sbbCustomerNumber"
														maxlength="<%= String.valueOf(ShippingpointEntity.DBSIZE_SBB_CUSTOMER_NUMBER) %>" style="width: 130px;" />
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>

					<tr>
						<td class="border">
						<table class="classic" style="border: none">
							<tr>
								<td class="label left"><nobr><fmt:message
									key="shippingpoint.field.packageTypePoss" /></nobr></td>
							</tr>
							<tr>
								<td>
								<div class="scrollist" id="packageTypeDiv" style="height: 72px;">
								<table class="classic" style="border: none">
									<c:forEach var="packageTypeSP" varStatus="status"
										items="${admin_ShippingPointForm.packageTypeSPList}">
										<tr class="zebra<c:out value="${status.count % 2}"/>">
											<td><c:out value="${packageTypeSP}" /></td>
										</tr>
									</c:forEach>
								</table>
								</div>
								</td>
							</tr>
						</table>
						</td>
						<td style="width: 2px;">&nbsp;</td>
						<td class="border">
							<table class="classic" style="border: none">
								<tr>
									<td class="label left"><nobr><fmt:message
										key="shippingpoint.field.weighingTypePoss" /></nobr></td>
								</tr>
								<tr>
									<td>
										<div class="scrollist" id="weighSysTypeDiv"
											style="height: 72px;">
											<table class="classic" style="border: none">
												<c:forEach var="weighSysTypeSP" varStatus="status"
													items="${admin_ShippingPointForm.weighSysTypeSPList}">
													<tr class="zebra<c:out value="${status.count % 2}"/>">
														<td><c:out value="${weighSysTypeSP}" /></td>
													</tr>
												</c:forEach>
											</table>
										</div>
									</td>
								</tr>
							</table>
						</td>
						<td style="width: 2px;">&nbsp;</td>
						<td class="border">
							<table class="classic" style="border: none; width: 100%">
								<tbody>
									<tr>
										<td class="label left" colspan=2><nobr><fmt:message
											key="shippingpoint.field.spType" /></nobr></td>
									</tr>
									<tr>
										<td colspan=2><html:select size="1" style="width:140px;"
											property="shippingPointType"
											onchange="javascript:selectSPTypeCd()"
											styleId="shippointTypeDD">
											<html:option value="-1">-</html:option>
											<c:forEach var="spTypeList" varStatus="status"
												items="${admin_ShippingPointForm.spTypeList}">
												<html:option value="${spTypeList.shipppointTypeTxPK.shipppointTypeBs.shipppointTypeCd}">
													<c:out value="${spTypeList.txt}" />
												</html:option>
											</c:forEach>
										</html:select></td>
									</tr>
									<tr>
										<td class="label left" colspan=2><nobr><fmt:message
											key="shippingpoint.field.sapShippingCond" /></nobr></td>
									</tr>
									<tr>
										<td colspan=2>
											<html:select size="1" style="width:140px;" property="shippingPoint.sapShippingCond.sapShippingCondCd">
												<html:option value="-1">
													-
												</html:option>
												<c:forEach var="sapShippingCond" varStatus="status" items="${admin_ShippingPointForm.sapShippingCondList}">
												<html:option value="${sapShippingCond.sapShippingCondTxPK.sapShippingCondBs.sapShippingCondCd}">
													<c:out value="${sapShippingCond.txt}" />
												</html:option>
												</c:forEach>
											</html:select>
										</td>
									</tr>
								</tbody>
							</table>
						</td>
					</tr>
					<tr>
						<td class="border" colspan="5">
							<table class="classic" style="border: none; width: 100%">
								<tr>
									<td class="label left">
										<nobr>
											<fmt:message key="shippingpoint.field.varHandlingCostMat" />:
										</nobr>
									</td>
									<td>
										<html:text styleId="varHandlingCostMat" property="varHandlingCostMat"
											style="width: 100px;" />
									</td>
									<td class="label left">
										<nobr>
											<fmt:message key="shippingpoint.field.currency" />:
										</nobr>
									</td>
									<td>
										<html:select styleId="shippingPointCurrency" property="shippingPointCurrency"
											styleClass="textfield" style="width: 150px;">
											<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">-</html:option>
											<html:optionsCollection name="admin_ShippingPointForm" property="currencyBsEntityMap" value="key" label="key"/>
										</html:select>
									</td>
								</tr>
								<tr>
									<td class="label left">
										<nobr>
											<fmt:message key="shippingpoint.field.futureVarHandlingCostMat" />:
										</nobr>
									</td>
									<td>
										<html:text styleId="futureVarHandlingCostMat" property="futureVarHandlingCostMat"
											style="width: 100px;" />
									</td>
									<td class="label left">
										<nobr>
											<fmt:message key="shippingpoint.field.uom" />:
										</nobr>
									</td>
									<td>
										<html:select styleId="shippingPointUom" property="shippingPointUom"
											styleClass="textfield" style="width: 150px;">
											<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">-</html:option>
											<html:optionsCollection name="admin_ShippingPointForm" property="uomBsEntityMap" value="key" label="key"/>
										</html:select>
									</td>
								</tr>
								<tr>
									<td class="label left">
										<nobr>
											<fmt:message key="shippingpoint.field.futureValidFromDat" />:
										</nobr>
									</td>
									<td>
										<html:text styleId="futureValidFromDat" property="futureValidFromDat"
											value="" />
									</td>
								</tr>
							</table> 
						</td>
					</tr>
					<tr>
						<td class="border" colspan="5">
							<table class="classic" style="border: none; width: 100%">
								<tr>
									<td class="label left">
										<nobr>
											<fmt:message key="shippingpoint.field.ldsType" />:
										</nobr>
									</td>
									<td>
										<html:select
											size="1" style="width: 250px;"
											property="weighSystemSpCd" styleId="ldsType"
											onchange="javascript:changeLdsType(this.options[this.selectedIndex].value)">
											<c:forEach var="element" varStatus="status" items="${admin_ShippingPointForm.weighSystemSpList}">
												<html:option value="${element.weighSystemSpTxPK.weighSystemSpBs.weighSystemSpCd}">
													<c:out value="${element.txt}" />
												</html:option>
											</c:forEach>
										</html:select>
									</td>
									<td class="label left">
										<nobr>
											<fmt:message key="shippingpoint.field.ldsServer" />:
										</nobr>
									</td>
									<td><nobr>
										<div id="serverDiv" style="border: 0px; padding: 0px; width: 150px;">
											<jsp:include page="LdsServer.jsp"></jsp:include>
										</div>
									</nobr></td>
								</tr>
							</table> 
						</td>
					</tr>
					<tr>
						<td class="label left" colspan="5"><nobr><fmt:message
							key="shippingpoint.field.desc" /></nobr></td>
					</tr>
					<tr>
						<td class="left" colspan="5"><html:textarea
							property="shippingPoint.shippingpointComment" rows="3"
							style="width: 100%;"></html:textarea></td>
					</tr>
				</tbody>
			</table>

			</td>
			<!-- Main form end -->

			<!-- Beginn Map Standort -->
			<td valign="top" class="border" style="width: 30%;">
				<div id="map" style="width: 450px; height: 400px; margin-top: 8px;overflow:hidden"
					class="border">map</div>
				<br />
				<table width="100%" cellpadding="0" cellspacing="0"
					class="classic" style="border: none">
					<tbody>
						<tr>
							<td class="label left"><fmt:message
								key="shippingpoint.field.coord" /></td>
							<td class="label left"><nobr><fmt:message
								key="shippingpoint.field.radius" /></nobr></td>
						</tr>
						<tr>
							<td class="label right" width="1"><nobr> <span
								class="label"><fmt:message
								key="shippingpoint.field.fix" />:</span> <html:checkbox
								property="shippingPoint.coordFixFlag" styleId="coordFlag" />&nbsp;&nbsp;&nbsp;<html:text
								style="width:125px;" property="shippingPoint.coordLon"
								styleId="coordLon" maxlength="<%= ShippingpointEntity.DBSIZE_COORD_LON + "" %>" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:text
								style="width:125px;" property="shippingPoint.coordLat"
								styleId="coordLat" maxlength="<%= ShippingpointEntity.DBSIZE_COORD_LAT + "" %>" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</nobr></td>
							<td><html:text property="shippingPoint.geofenceRadius" style="width:50px;" styleId="radius" /></td>
						</tr>
						<tr>
							<td class="label right" colspan="2">
								<nobr>
									<span class="label">
										<fmt:message key="application.label.timezone" />:&nbsp;&nbsp;&nbsp;
									</span>
								</nobr>
								<html:select styleId="applicationLang" size="1"
											 style="width:300px;"
											 property="selectedTimeZoneCd">
									<c:forEach var="tz" varStatus="status"
											   items="${admin_ShippingPointForm.timeZoneList}">
										<html:option value="${tz.timezoneCd}">
											<c:out value="${tz.formattedAsTimeZoneOnly}" />
										</html:option>
									</c:forEach>
								</html:select>
							</td>
						</tr>
					</tbody>
				</table>
			</td>
			<td rowspan="2" class="border" style="width: 30%;">
				<table class="classic" style="border: none; width: 100%">
					<tr>
						<td class="label left">
							<nobr>
								<fmt:message key="shippingpoint.field.stockableMaterial" />:
							</nobr>
						</td>
						<td style="text-align: right;">
							<html:text styleId="shippingPoint.storageLocation" property="shippingPoint.storageLocation"
								maxlength="<%= String.valueOf(ShippingpointEntity.DBSIZE_STORAGE_LOCATION) %>" style="width: 50px;"
								onchange="defaultStorageLocationChanged();" />
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<div class="scrollist" id="materialDiv" style="height: 450px; width: 100%;">
								<table class="classic" style="border: none;">
									<c:forEach var="storageLocationBean" varStatus="status" items="${admin_ShippingPointForm.storageLocationBeanList}">
										<c:set var="counter" value="${status.count - 1}" />
										<tr class="zebra<c:out value="${status.count % 2}"/>" style="vertical-align: middle;">
											<td class="left" style="vertical-align: middle;">
												<c:out value="${storageLocationBean.sapMaterialPlantDesc.display}" />
											</td>
											<td style="width: 80px; text-align: right; vertical-align: middle;">
												<html:checkbox property="storageLocationBeanList[${counter}].storageLocationIsDefault" styleId="storageLocationBeanList[${counter}].storageLocationIsDefault"
													disabled="${storageLocationBean.storageLocationIsDefaultDisabled}"
													title="<%= MessageUtils.getMessage(request, request.getLocale(), "shippingpoint.text.storageLocationIsDefaultTooltip") %>"
													onchange="storageLocationChange('storageLocationBeanList[${counter}].storageLocationIsDefault', 'storageLocationBeanList[${counter}].storageLocation');" />
												<html:text styleId="storageLocationBeanList[${counter}].storageLocation" property="storageLocationBeanList[${counter}].storageLocation"
													maxlength="<%= String.valueOf(MatShippingpointEntity.DBSIZE_STORAGE_LOCATION) %>" style="width: 50px;"
													readonly="${storageLocationBean.storageLocationReadOnly}" />
											</td>
										</tr>
									</c:forEach>
								</table>
							</div>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>

	<br>

	<nobr>
		&nbsp;
		<c:if test="${admin_ShippingPointForm.loadingpointAddingAllowed}">
			<a href="javascript:addLoadingPoint()">
			<img src="<crh-common:url value='images/add.png'/>"
				title="<%= MessageUtils.getMessage(request, request.getLocale(), "loadingpoint.button.tooltip.create") %>"
			/></a>&nbsp;&nbsp;
		</c:if>
		<c:if test="${!admin_ShippingPointForm.loadingpointAddingAllowed}">
			<a><img src="<crh-common:url value='images/add-disabled.png'/>" />
			</a>&nbsp;&nbsp;
		</c:if>
		<a href="javascript:removeLoadingPoint()">
		<img src="<crh-common:url value='images/delete2.png'/>"
			title="<%= MessageUtils.getMessage(request, request.getLocale(), "loadingpoint.button.tooltip.delete") %>"
		/>
		</a> 
	</nobr>

	<div id="DivLoadingStreet"><c:if
		test="${not empty admin_ShippingPointForm.loadingPointWrapperList}">
		<jsp:include page="LoadingStreet.jsp"></jsp:include></c:if></div>

</html:form>


<br>
<html:form action="ShippingPoint.do" onsubmit="return false;">
	<table class="big_form2" cellpadding="0" cellspacing="0" border="0"
		width="100%" style="padding: 0px; margin: 0px;">

		<tr>
			<td class="buttons right"><input type="button"
				class="ViewButtonStyle"
				value="<fmt:message key="shippingpoint.button.cancel"/>"
				onClick="submitWithAction('cancel')"> <input type="button"
				class="ViewButtonStyle"
				value="<fmt:message key="shippingpoint.button.save"/>"
				onClick="window.scrollTo(0,0);submitWithAction('save')"></td>

		</tr>
	</table>
</html:form>
