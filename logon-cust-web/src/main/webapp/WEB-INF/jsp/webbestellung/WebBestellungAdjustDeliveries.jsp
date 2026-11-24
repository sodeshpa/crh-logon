<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: WebBestellungAdjustDeliveries.jsp,v $ --%>

<%@ page session="true"%>
<%@ include file="../includeTLDs.jsp"%>

<%@page import="com.crh.logon.constants.Constant"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>
<%@page import="com.crh.logon.web.utils.ConfigBeanProvider"%>


<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.grid.CheckColumn.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.valid.ValidationMessages.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.grid.EditableList.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.grid.PagingToolbar.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.data.OriginalRecordStore.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.grid.LogonEditorGridPanel.js'/>"></script>


<script type="text/javascript">
	//# sourceURL=WebBestellungAdjustDeliveries.jsp.js

	var jsonSplittedOrders = <c:out value="${cust_WebBestellungForm.jsonSplittedOrders}" escapeXml="false"/>;
	var dateFormat = '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>';
	var altDateFormat = '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>';
	var splittedRecords = [];
	var confirmSaveButtonText = '<fmt:message key="extjsgrid.list.buttonSave"/>';
	var confirmCancelButtonText = '<fmt:message key="extjsgrid.list.buttonCancel"/>';
	var cancelButtonLabel = '<fmt:message key="buttons.cancel" />';
	var saveButtonLabel = '<fmt:message key="bestellung.custorder.popup2.button.savedeliveryorders" />';
	var qtySumCheckWarning1 = '<fmt:message key="bestellung.custorder.popup2.text.qtySumCheckWarning1"/>';
	var qtySumCheckWarning2 = '<fmt:message key="bestellung.custorder.popup2.text.qtySumCheckWarning2"/>';
	var qtySumCheckWarning3 = '<fmt:message key="bestellung.custorder.popup2.text.qtySumCheckWarning3"/>';
	var qtySumCheckWarning4 = '<fmt:message key="bestellung.custorder.popup2.text.qtySumCheckWarning4"/>';
	var qtySumCheckWarningTitle = '<fmt:message key="bestellung.custorder.popup2.text.qtySumCheckWarningTitle"/>';
	var custorderMaterialType = '<c:out value="${cust_WebBestellungForm.materialType}" />';
	var custorderOnHoldEnabled = <c:out value="${cust_WebBestellungForm.onHoldOrderAndLockedFlag}"/>;

	// retrive the warning message, used to validate the deliveridate
	var deliveryDateWarningMsg = '<fmt:message key="bestellung.error.invalid.delivery.earlier.later"/>';
	var editorLocatorMsg = '<fmt:message key="bestellung.error.editorlocator"/>';
	var maxSDO = '<%= ConfigBeanProvider.getConfigBean().getAppDefaultCustorderMaxNumOfOrders() %>';
	var vehicleMaxLoadInOrderedUom = <c:out value="${cust_WebBestellungForm.vehicleMaxLoadInOrderedUomForAdjustDeliveries}" escapeXml="false"/>;
	var orderedUomCd = '<c:out value="${cust_WebBestellungForm.orderedUomCdForAdjustDeliveries}" escapeXml="false"/>';



	Ext.onReady(function() {
		Ext.get('sdoTableWrapper').setHeight(Ext.getDom('sdoiTableWrapper').clientHeight);
		// -- Synchronizing the header height and vertical scroll position of the two tables --
		var sdoTableHeaderCell = Ext.get('sdoTableHeaderCell');
		var sdoiTableHeaderCell = Ext.get('sdoiTableHeaderCell');
		if (sdoTableHeaderCell.getHeight() != sdoiTableHeaderCell.getHeight()) {
			sdoTableHeaderCell.setHeight(Math.max(sdoTableHeaderCell.getHeight(), sdoiTableHeaderCell.getHeight()));
			sdoiTableHeaderCell.setHeight(Math.max(sdoTableHeaderCell.getHeight(), sdoiTableHeaderCell.getHeight()));
		}
		Ext.get('sdoiTableWrapper').on('scroll', function() {
			Ext.get('sdoTableWrapper').scrollTo('top', this.getScroll().top);
		});
	});

</script>


<script type="text/javascript" src="<crh-common:url value='/js/WebAdjustDeliveries.js'/>"></script>

<html:form action="webbestellung/WebBestellung.do"  styleId="singleDeliveryOrderSplisdoTableForm">

	<div id="SingleDeliveryOrderTileDiv" class="customer_max" style="padding: 0px; width: 1200px;">

		<table class="big_form2 classicblue" style="overflow: auto; width: 1200px; padding: 2px; border-spacing: 0px; border: none;">
			<tr>
				<th><fmt:message key="bestellung.custorder.popup2.title" /></th>
			</tr>

				<c:set var="ordersListFromCustOrders" value="${cust_WebBestellungForm.splittedOrderDatas}" />

				<tr>
					<td>

						<div class="sdoBorderDiv">

							<div class="sdoTableWrapper" id="sdoTableWrapper">

								<!-- SDO table content -->
								<table class="classicblue sdoTable" style="border: none; border-spacing: 0px; border-collapse: collapse">

									<!-- Header -->
									<tr class="header">
										<th id="sdoTableHeaderCell" >
											&nbsp;
										</th>
										<th style="vertical-align: middle;">
											<fmt:message key="bestellung.custorder.popup2.headers.no"/>
										</th>
										<th style="vertical-align: middle;">
											<fmt:message key="bestellung.custorder.popup2.headers.datefrom"/>
											<c:if test="${displayShipToTimezone}">
												<c:out value="${shipToTimeZoneFmtOffset}"/>
											</c:if>
										</th>
										<th style="vertical-align: middle;">
											<fmt:message key="bestellung.custorder.popup2.headers.dateto"/>
											<c:if test="${displayShipToTimezone}">
												<c:out value="${shipToTimeZoneFmtOffset}"/>
											</c:if>
										</th>
										<th style="vertical-align: middle;">
											<fmt:message key="bestellung.custorder.popup2.headers.timefrom"/>
											<c:if test="${displayShipToTimezone}">
												<c:out value="${shipToTimeZoneFmtOffset}"/>
											</c:if>
										</th>
										<th style="vertical-align: middle;">
											<fmt:message key="bestellung.custorder.popup2.headers.timeto"/>
											<c:if test="${displayShipToTimezone}">
												<c:out value="${shipToTimeZoneFmtOffset}"/>
											</c:if>
										</th>
										<th style="vertical-align: middle; padding: 0px">
											<!-- OnHold -->
											<table cellpadding="0" cellspacing="0" style="padding: 0px; margin:0px; border-spacing: 0px">
												<tr>
													<th style="border: none; vertical-align: middle;">
														<input type="checkbox" name="editor_0_header.onHold"
																id="editor_0_header.onHold" />
													</th>
													<th style="border: none; vertical-align: middle;">
														<nobr>
															<fmt:message key="bestellung.custorder.popup2.headers.onhold"/>
														</nobr>
													</th>
												</tr>
											</table>
										</th>
									</tr>

									<c:set var="splittedOrderRowCnt" value="${0}"/>

									<c:forEach var="splittedOrder" varStatus="splittedOrderStatus" items="${ordersListFromCustOrders}">

										<c:set var="splittedOrderRowCnt" value="${splittedOrderRowCnt + 1}"/>

										<!-- Body -->
										<tr class="zebra<c:out value="${splittedOrderRowCnt % 2}"/>">
											<td style="vertical-align: middle; text-align: center">
												<!-- TODO: don't display delete png when there is only one record -->
												<a onclick="javascript:deleteSplittedOrderData(0, <c:out value="${splittedOrder.index}" />)"
														id="editor_0_deleteButton_<c:out value="${splittedOrder.index}" />"
														style="display:none;">
													<img src="<crh-common:url value='/images/delete2.png'/>" />
												</a>
											</td>
											<td style="vertical-align: middle; text-align: center">
												<c:out value="${splittedOrderStatus.count}" />.
											</td>
											<td style="width: 90px">
												<!-- Delivery From Date -->
												<input type="text"
														style="width: 65px;"
														id="editor_0_ordersListFromCustOrders[<c:out value="${splittedOrderStatus.count-1}"/>].deliveryFromDatStr"
														name="editor_0_ordersListFromCustOrders[<c:out value="${splittedOrderStatus.count-1}"/>].deliveryFromDatStr"
														value="<c:out value="${splittedOrder.deliveryFromDatStr}" />"/>
											</td>
											<td style="width: 90px">
												<!-- Delivery To Date -->
												<input type="text"
														style="width: 65px;"
														id="editor_0_ordersListFromCustOrders[<c:out value="${splittedOrderStatus.count-1}"/>].deliveryToDatStr"
														name="editor_0_ordersListFromCustOrders[<c:out value="${splittedOrderStatus.count-1}"/>].deliveryToDatStr"
														value="<c:out value="${splittedOrder.deliveryToDatStr}" />"/>
											</td>
											<td style="width: 90px">
												<!-- Delivery From Time -->
												<input type="text"
														style="width: 65px;"
														id="editor_0_ordersListFromCustOrders[<c:out value="${splittedOrderStatus.count-1}"/>].deliveryFromTime"
														name="editor_0_ordersListFromCustOrders[<c:out value="${splittedOrderStatus.count-1}"/>].deliveryFromTime"
														value="<c:out value="${splittedOrder.deliveryFromTime}" />"/>
											</td>
											<td style="width: 90px">
												<!-- Delivery To Time -->
												<input type="text"
														style="width: 65px;"
														id="editor_0_ordersListFromCustOrders[<c:out value="${splittedOrderStatus.count-1}"/>].deliveryToTime"
														name="editor_0_ordersListFromCustOrders[<c:out value="${splittedOrderStatus.count-1}"/>].deliveryToTime"
														value="<c:out value="${splittedOrder.deliveryToTime}" />"/>
											</td>
											<td style="text-align: center; vertical-align: middle;" title="<fmt:message key="bestellung.custorder.popup2.headers.onhold"/>">
												<!-- OnHold -->
												<input type="checkbox"
														name="editor_0_ordersListFromCustOrders[<c:out value="${splittedOrderStatus.count-1}"/>].onHold"
														id="editor_0_ordersListFromCustOrders[<c:out value="${splittedOrderStatus.count-1}"/>].onHold"
														value="false" />
											</td>
										</tr>

									</c:forEach>

									<!-- height of this two rows must be in synch with the spacer tr -->
									<tr>
										<td style="height: 20px; vertical-align: middle; text-align: center;">
											<a onclick="javascript:addSplittedOrderData(0)"
													id="editor_0_addButton"
													style="display:none;">
												<img src="<crh-common:url value='/images/add.png'/>" />
											</a>
										</td>
										<td colspan="7">&nbsp;</td>
									</tr>
									<tr>
										<td style="height: 20px;" colspan="8">&nbsp;</td>
									</tr>
								</table>

							</div>

							<div class="sdoiTableWrapper" id="sdoiTableWrapper">

								<!-- SDOI table content -->
								<table class="classicblue sdoiTable" style="border: none; border-collapse: collapse;">

									<!-- Header -->
									<tr id="sdoiTableHeader" class="header">


										<c:forEach var="materialColumn" varStatus="materialColumnStatus" items="${cust_WebBestellungForm.materialColumns}">
											<th id="sdoiTableHeaderCell" colspan="2" style="vertical-align: middle;border-left: none" nowrap="nowrap">
											<span>
												<c:out value="${materialColumn.materialDescription.txt}" />
											</span>
												<br />
												<fmt:message key="bestellung.custorder.popup2.headers.quantity" />
												&nbsp;
												<span id="sumCurrentOrderedQtyInBase_<c:out value="${materialColumn.material.sapMaterialId}" />">
												<c:out value="${'' + materialColumn.orderedQuantity}" />
											</span>
												/
												<span id="sumOriginalOrderedQtyInBase_<c:out value="${materialColumn.material.sapMaterialId}" />">
												<c:out value="${'' + materialColumn.orderedQuantity}" />
											</span>
												<input id="sumOrderedQtyMaterial_<c:out value="${materialColumn.material.sapMaterialId}" />" type="hidden" value="<c:out value="${materialColumn.materialDescription.txt}" />">
												<input id="sumOrderedQtyMaterialUom_<c:out value="${materialColumn.material.sapMaterialId}" />" type="hidden" value="<c:out value="${materialColumn.material.baseUom.uomCd}" />">
												&nbsp;
												<c:out value="${materialColumn.material.baseUom.uomCd}" />
											</th>
										</c:forEach>

									</tr>

									<c:set var="splittedOrderRowCnt" value="${0}"/>

									<c:forEach var="splittedOrder" varStatus="splittedOrderStatus" items="${ordersListFromCustOrders}">

										<c:set var="splittedOrderRowCnt" value="${splittedOrderRowCnt + 1}"/>
										<c:set var="splittedOrderItemDatas" value="${splittedOrder.splittedOrderItemDatas}" />

										<!-- Body -->
										<tr class="zebra<c:out value="${splittedOrderRowCnt % 2}"/>">

											<c:forEach var="materialColumn" varStatus="materialColumnStatus" items="${cust_WebBestellungForm.materialColumns}">

												<c:set var="alreadyFound" value="false" />

												<c:forEach var="splittedOrderItemData" varStatus="splittedOrderItemDatasStatus" items="${splittedOrderItemDatas}">


													<c:choose>
														<c:when test="${materialColumn.materialDescription.sapMaterialDescriptionPK.sapMaterialId == splittedOrderItemData.orderItem.sapMaterial.sapMaterialId}">

															<td style="text-align: center;border-left: none;">
																<!-- Ordered Quantity -->
																<input type="text"
																		name="ordersListFromCustOrders[<c:out value="${splittedOrderStatus.count-1}"/>].splittedOrderItemData[<c:out value="${splittedOrderItemDatasStatus.count-1}"/>].sapMatId_<c:out value="${splittedOrderItemData.sapMaterialId}"/>.orderedQty"
																		id="ordersListFromCustOrders[<c:out value="${splittedOrderStatus.count-1}"/>].splittedOrderItemData[<c:out value="${splittedOrderItemDatasStatus.count-1}"/>].sapMatId_<c:out value="${splittedOrderItemData.sapMaterialId}"/>.orderedQty"
																		value="<c:out value="${splittedOrderItemData.orderItem.orderedQuantity}" />"
																		style="width: 40px;"/>
															</td>
															<td style="text-align: left;">
																<c:out value="${splittedOrderItemData.orderedUom}" />
															</td>
														</c:when>
														<c:otherwise>

															<c:if test="${alreadyFound == false}">

																<c:set var="alreadyFound" value="true" />

																<c:set var="matExistsInSDO" value="false" />

																<c:forEach var="innerSplittedOrderItemData" varStatus="innnerSplittedOrderItemDatasStatus" items="${splittedOrderItemDatas}">

																	<c:if test="${materialColumn.materialDescription.sapMaterialDescriptionPK.sapMaterialId == innerSplittedOrderItemData.orderItem.sapMaterial.sapMaterialId}">
																		<c:set var="matExistsInSDO" value="true" />
																	</c:if>

																</c:forEach>

																<c:if test="${matExistsInSDO == false}">
																	<td style="border-left: none">&nbsp;</td>
																	<td>&nbsp;</td>
																</c:if>

															</c:if>

														</c:otherwise>
													</c:choose>
												</c:forEach>
											</c:forEach>

										</tr>
									</c:forEach>
									<tr>

										<td class="right" style="vertical-align: middle">
											<div id="editor_<c:out value="${orderTakingEditorStatus.index}"/>_buttonPanel" >
												<input type="button" class="ViewButtonStyle" value="<fmt:message
												key="buttons.cancel" />" id="buttonCancel">
												<input type="button" class="ViewButtonStyle" value="<fmt:message key="bestellung.custorder.popup2.button.savedeliveryorders" />" id="buttonSave">
											</div>
										</td>
									</tr>

								</table>


							</div>

						</div>

					</td>
				</tr>


		</table>

	</div>

</html:form>

<%-- Cannot be moved to external CSS becuase of dynamic width / height --%>
<style type="text/css">

	.sdoBorderDiv {
		width: auto;
		width: 1180px;
		max-height: 608px;
		border: 1px solid #CCC;
		overflow-y: auto;
		background-color: white;
		margin: 5px;
	}

	.sdoBorderDiv tr.header {
		height: 34px;
	}

	.sdoTableWrapper {
		width: 750px;
		max-height: 603px;
		overflow-y: hidden;
		overflow-x: hidden;
		float: left;
		border-right: 1px solid #B5B5B5;
	}

	.sdoiTableWrapper {
		width: auto;
		width: 425px;
		max-height: 603px;
		float: left;
		overflow-x: auto;
		display: inline;
	}

	.sdoTable {
		width: 750px;
	}

	.classicblue.sdoTable td,
	.classicblue.sdoTable th {
		border: 2px solid white;
	}

	.classicblue.sdoiTable td,
	.classicblue.sdoiTable th {
		border: 2px solid white;
	}

	.sdoiTable {
		width: auto;
		width: 425px;
		border-spacing: 0px;
		border-collapse: collapse;
		margin-left: 2px;
	}

	.sdoiTable tr.zebra0 td,
	.sdoiTable tr.zebra1 td,
	.sdoTable tr.zebra0 td,
	.sdoTable tr.zebra1 td {
		height: 28px;
		vertical-align: middle;
	}

</style>


<%-- END $RCSfile: WebBestellungAdjustDeliveries.jsp,v $ --%>
