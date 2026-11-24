

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page import="com.crh.logon.order.web.actions.auftrag.OrderDetailAction"%>

<c:set var="orderDetailUrl">
	/../order/auftrag/OrderDetail.do
</c:set>

<c:set var="CUSTORDER_STATUS_FINISHED"><%=Constant.CUSTORDER_STATUS_FINISHED%></c:set>
<c:set var="PACKAGE_TYPE_BULK_POWDER"><%=Constant.PACKAGE_TYPE_BULK_POWDER%></c:set>
<c:set var="form" scope="page" value="${order_auftrag_OrderDetailForm}" />
<c:set var="order" scope="page" value="${form.order}"/>
<%-- <c:set var="bestellform" scope="page" value="${order_bestellung_BestellungForm}" /> --%>

<script type="text/javascript">

<c:if test="${showCloseCustorderPopup}">
	<jsp:include page="../../bestellung/CustOrderConf.jsp"></jsp:include>
</c:if>

<c:if test="${showInitRecalcPopup}">
	<jsp:include page="../../auftrag/orderdetail/CustOrderExactQtyPopup.jsp"></jsp:include>
</c:if>

function initiateSetCustorderToFinishedManuallyOrdDet() {
	form = Ext.getDom('OrderDetailForm');

	var p = {};
	p.formAction = '<%=OrderDetailAction.FORM_ACTION_INITIATE_CLOSE_CUSTOMER_ORDER%>';
	addFormElements(p, form);
	 
	var diva = Ext.get('TabsDiv');
	diva.load({
		url :'<c:url value="${orderDetailUrl}"/>',
		params : p,
		text :'<fmt:message key="extjs.loading"/>',
		scripts :true
	});
}

function confirmSetCustorderToFinishedManuallyOrdDet(custorderId) {
	if (typeof(setToReloadIfOrderDetailClosed)=='function') {
		setToReloadIfOrderDetailClosed(true);
	}

	form = Ext.getDom('OrderDetailForm');

	var p = {};
	p.formAction = '<%=OrderDetailAction.FORM_ACTION_CONFIRM_CLOSE_CUSTOMER_ORDER%>';
	p.custorderId = custorderId;
	addFormElements(p, form);
	 
	var diva = Ext.get('TabsDiv');
	diva.load({
		url :'<c:url value="${orderDetailUrl}"/>',
		params : p,
		text :'<fmt:message key="extjs.loading"/>',
		scripts :true
	});
}

function addServiceToCustOrder(action) {
	// TODO
}

function generateCollPdf() {
    form = Ext.getDom('OrderDetailForm');
	p = {'formAction' :'<%=OrderDetailAction.FORWARD_GENERATE_COLL_PDF%>'};
	addFormElements(p, form);

	var diva = Ext.get('collDelNoteDiv');
	diva.load( {
		url :'<c:url value="${orderDetailUrl}"/>',
		params : p,
		text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
		scripts :true
		});
}

</script>

<c:set var="org.apache.struts.taglib.html.BEAN" scope="page"
	value="${order_auftrag_OrderDetailForm}" />

<table style="width: 100%;">
	<!--<html:form action="auftrag/OrderDetail.do"  onsubmit="return false;" >-->
	<tr>
		<!-- Customer Order -->
		<td colspan='3'>
			<span class="labelblue" style="float: left;">
				<fmt:message key="orderdetail.custorder.label.custorder.nr" />
				<c:out value="${form.custOrderTabBean.custorderNumber}" />&nbsp;
				(<c:out value="${form.custOrderTabBean.deliveryFrom}" />&nbsp;-&nbsp;<c:out value="${form.custOrderTabBean.deliveryTo}" />)&nbsp;
				<c:out value="${form.custOrderTabBean.custorderStatus}" />
			</span>
		</td>
	</tr>
	<tr>
		<td>
			<span class="label" style="float: left;">
				<fmt:message key="orderdetail.custorder.label.exactCustOrderQty" />:&nbsp;
			</span>
			<c:set var="exactCustOrderQtyFlagDiv"><fmt:message key="orderdetail.custorder.label.exactCustOrderQty" /></c:set>
			<div id="exactCustOrderQtyFlagDiv">
				<html:checkbox property="custOrderTabBean.exactCustOrderQty" onclick="exactCustOrderFlagClicked(this);" 
				disabled="${!form.custOrderTabBean.exactCustOrderQtyEditable 
						|| form.order.custorder.custorderStatus.custorderStatusCd == CUSTORDER_STATUS_FINISHED}"/>
			</div>
		</td>
		<td>
			<span class="label" style="float: left;">
				<fmt:message key="orderdetail.custorder.label.exactSingleOrderQty" />:&nbsp;
			</span>
			<c:set var="exactSingleOrderQty"><fmt:message key="orderdetail.custorder.label.exactSingleOrderQty" /></c:set>
				<html:checkbox property="custOrderTabBean.exactSingleOrderQty" onclick="exactSingleOrderFlagClicked(this);" 
				disabled="${!form.custOrderTabBean.exactSingleOrderQtyEditable 
						|| form.order.custorder.custorderStatus.custorderStatusCd == CUSTORDER_STATUS_FINISHED}" />
			</div>
		</td>

		<%-- FINISH CUSTOMER ORDER BUTTON --%>
		<td class="buttons right">
			<input type="button" 
				<c:choose>
					<c:when test="${form.custOrderTabBean.custorderEntity.custorderStatus.custorderStatusCd == CUSTORDER_STATUS_FINISHED}">
						class="ViewButtonStyleDisabled"
					</c:when>
					<c:otherwise>
						class="ViewButtonStyle"
					</c:otherwise>
				</c:choose>
			value="<fmt:message key="orderdetail.custorder.button.closeCustomerOrder"/>"
			onClick="initiateSetCustorderToFinishedManuallyOrdDet()">
		</td>
	</tr>
</table>


<table style="width: 100%">
<%----------------------------------------%>
<%-- OPEN CUSTOMER ORDER QUANTITY TABLE --%>
<%----------------------------------------%>
	<tr>
		<td style="padding-top: 0px">
			<table class="classicblue" style="width: 100%; margin-top: 4px">
				<tbody>
					<tr>
						<th rowspan="2" style="width: 40%">
							<fmt:message key="orderdetail.custorder.label.material"/><br/>
						</th>
						<th colspan="5" style="width: 60%">
							<fmt:message key="orderdetail.custorder.label.quantity.quantity"/><br/>
						</th>
					</tr>
					<tr>
						<td class="cell_gray" style="width: 15%">
							<fmt:message key="orderdetail.custorder.label.quantity.ordered"/><br/>
						</td>
						<td class="cell_gray" style="width: 11%">
							<fmt:message key="orderdetail.custorder.label.quantity.scheduled"/><br/>
						</td>
						<td class="cell_gray" style="width: 11%">
							<fmt:message key="orderdetail.custorder.label.quantity.loaded"/><br/>
						</td>
						<td class="cell_gray" style="width: 11%">
							<fmt:message key="orderdetail.custorder.label.quantity.delivered"/><br/>
						</td>
						<td class="cell_gray" style="width: 11%">
							<fmt:message key="orderdetail.custorder.label.quantity.todeliver"/><br/>
						</td>
					</tr>

					<%-- List of the open single-delivery orders --%>
					<c:set var="rowCnt" value="${0}"/>
					<c:forEach var="material" varStatus="status" items="${form.custOrderTabBean.materialOnlyMap}">
						<tr class="zebra<c:out value="${rowCnt % 2}" />">
							<td valign="bottom">
								<c:out value="${material.value.materialTxt}" />
							</td>
							<td valign="bottom" style="text-align: left">
								<html:text styleClass='textfield' style='width: 50px' styleId="orderedQuantity"
									property="custOrderTabBean.custorderItemsQtyBySapMatIdMap(${material.value.sapMaterialId})" 
									disabled="${form.order.custorder.custorderStatus.custorderStatusCd == CUSTORDER_STATUS_FINISHED 
											|| !form.custOrderTabBean.exactCustOrderQtyEditable}"
								/>
								<c:out value="${material.value.orderedUomTxt}" />&nbsp;
							</td>
							<td valign="bottom" style="text-align: right">
								<c:out value="${material.value.scheduledQuantityInBaseUom}" />&nbsp; 
								<c:out value="${material.value.baseUomTxt}" />&nbsp;</td>
							<td valign="bottom" style="text-align: right">
								<c:out value="${material.value.loadedQuantityInBaseUom}" />&nbsp; 
								<c:out value="${material.value.baseUomTxt}" />&nbsp;</td>
							<td valign="bottom" style="text-align: right">
								<c:out value="${material.value.deliveredQuantityInBaseUom}" />&nbsp; 
								<c:out value="${material.value.baseUomTxt}" />&nbsp;</td>
							<td valign="bottom" style="text-align: right">
								<c:out value="${material.value.openQuantityInBaseUom}" />&nbsp;
								<c:out value="${material.value.baseUomTxt}" />&nbsp;</td>
						</tr>
						<c:set var="rowCnt" value="${rowCnt + 1}" />
					</c:forEach>
				</tbody>
			</table>
		</td>
	</tr>

	<%-- SAVE AND RELOAD CUSTORDER BUTTON --%>
	<tr>
		<td class="buttons right">
			<input type="button" class="ViewButtonStyle" onClick="saveCustorder('<%=OrderDetailAction.FORWARD_SAVE_CUST_ORDER%>')"
				value="<fmt:message key="orderdetail.custorder.button.saveAndReload"/>">
		</td>
	</tr>

	<%-- ADD SERVICE MATERIAL BUTTON --%>
	<%-- LOGON-5118: out of scope for the moment --%>
	<%--
	<tr>
		<td class="buttons right"><nobr>
				<input type="button" class="ViewButtonStyle"
					value="<fmt:message key="orderdetail.custorder.button.addServiceMaterial"/>"
					onClick="addServiceToCustOrder()">
			</nobr>
		</td>
	</tr>
	--%>

	<%-----------------------------%>
	<%-- SERVICE ONLY DELIVERIES --%>
	<%-----------------------------%>
	<c:set var="serviceOnlyOrderAvailable" value="false" />
	<c:forEach var="order" items="${form.custOrderTabBean.orders}">
		<c:if test="${order.serviceMaterialOnlyOrder}" >
			<c:set var="serviceOnlyOrderAvailable" value="true" />
		</c:if>
	</c:forEach>
	<c:if test="${serviceOnlyOrderAvailable}" >
		<tr>
			<td style="padding-top: 0px">
				<table class="classicblue" style="width: 100%; margin-top: 4px">
					<tbody>
						<tr>
							<th style="width: 10%">
								<fmt:message key="orderdetail.custorder.label.deliveryNo"/><br/>
							</th>
							<th style="width: 35%">
								<fmt:message key="orderdetail.custorder.label.material"/><br/>
							</th>
							<th style="width: 10%">
								<fmt:message key="orderdetail.custorder.label.delivery.date"/><br/>
							</th>
							<th style="width: 10%">
								<fmt:message key="orderdetail.custorder.label.delivery.time"/><br/>
							</th>
							<th style="width: 10%">
								<fmt:message key="orderdetail.custorder.label.quantity.quantity"/><br/>
							</th>
							<th style="width: 25%">
								<fmt:message key="orderdetail.custorder.label.status"/><br/>
							</th>
						</tr>

						<%-- List of the open single-delivery orders --%>
						<c:set var="rowCnt" value="${0}"/>
						<c:forEach var="order" varStatus="status" items="${form.custOrderTabBean.orders}">
							<c:if test="${order.serviceMaterialOnlyOrder}" >
								<tr class="zebra<c:out value="${rowCnt % 2}" />">
									<td style="text-align: left">
										<a href="javascript:showPopup('<c:url value="/auftrag/OrderDetailEntry.do"/>?orderid=<c:out value="${order.orderId}"/>')"><c:out value="${order.orderNumber}"/></a>
									</td>
									<td>
										<c:forEach var="item" varStatus="status" items="${order.materials}">
											<c:out value="${item.materialDesc}" />
											<br />
										</c:forEach>
									</td>
									<td style="text-align: left">
										<c:out value="${order.actualDeliveryDate}"/>&nbsp;
									</td>
									<td style="text-align: left">
										<c:out value="${order.actualDeliveryTime}"/>&nbsp;
									</td>
									<td style="text-align: left">
										<c:forEach var="item" varStatus="status" items="${order.materials}">
											<c:out value="${item.orderedQuantity}" />&nbsp;
											<c:out value="${item.orderedUom}" />
											<br />
										</c:forEach>
									</td>
									<td style="text-align: right">
										<c:out value="${order.orderStatus}"/>&nbsp;
									</td>
								</tr>
								<c:set var="rowCnt" value="${rowCnt + 1}" />
							</c:if>
						</c:forEach>
					</tbody>
				</table>
			</td>
		</tr>
	</c:if>

<%---------------------------%>
<%-- Single Delivery Table --%>
<%---------------------------%>
	<tr>
		<td>
			<span class="labelblue">
				<fmt:message key="orderdetail.custorder.label.deliveries"/>
			</span>
			<div style="font-size: 12px; height: 265px;"
				class="subformlist">
			<table class="classicblue" style="width: 100%; margin-top: 4px">
				<tbody>
					<tr>
						<th rowspan="2" style="width: 5%">
							<fmt:message key="orderdetail.custorder.label.deliveryNo"/><br/>
						</th>
						<th rowspan="2" style="width: 15%">
							<fmt:message key="orderdetail.custorder.label.material"/><br/>
						</th>
						<th colspan="3" style="width: 24%">
							<fmt:message key="orderdetail.custorder.label.delivery.delivery"/><br/>
						</th>
						<th rowspan="2" style="width: 10%">
							<fmt:message key="orderdetail.custorder.label.vehicle"/><br/>
						</th>
						<th colspan="2" style="width: 10%">
							<fmt:message key="orderdetail.custorder.label.quantity.quantity"/><br/>
						</th>
						<th rowspan="2" style="width: 5%">
							<fmt:message key="orderdetail.custorder.label.status"/><br/>
						</th>
					</tr>
					<tr>
						<td class="cell_gray">
							<fmt:message key="orderdetail.custorder.label.delivery.fromDat"/><br/>
						</td>
						<td class="cell_gray">
							<fmt:message key="orderdetail.custorder.label.delivery.toDat"/><br/>
						</td>
						<td class="cell_gray">
							<fmt:message key="orderdetail.custorder.label.delivery.actualDat"/><br/>
						</td>
						<td class="cell_gray">
							<fmt:message key="orderdetail.custorder.label.quantity.planned"/><br/>
						</th>
						<td class="cell_gray">
							<fmt:message key="orderdetail.custorder.label.quantity.loaded"/><br/>
						</td>
					</tr>
					<%-- 
				</tbody>
			</table>
			<div style="font-size: 12px; margin-top: -1px; height: 124px;"
				class="subformlist">
			<table>
				<tbody>
				--%>

						<%-- List of the open single-delivery orders --%>
					<c:set var="rowCnt" value="${0}"/>
					<c:forEach var="order" varStatus="status2" items="${form.custOrderTabBean.orders}">
						<%-- Workaround for JSTL 1.0 (Struts 1.2.9) without JSTL 1.0's EL Functions, e.g. fn:length --%>
						<c:forEach var="item" varStatus="status3" items="${order.materials}">
							<c:set var="matCnt" value="${status3.count}" />
						</c:forEach>
						<c:forEach var="item" varStatus="status3" items="${order.materials}">
						<tr class="zebra<c:out value="${rowCnt % 2}" />">
							<c:if test="${status3.first}">
								<%-- Delivery No. --%>
								<td style="text-align: left" rowspan="<c:out value="${matCnt}" />">
									<a href="javascript:showPopup('<c:url value="/auftrag/OrderDetailEntry.do"/>?orderid=<c:out value="${order.orderId}"/>')"><c:out value="${order.orderNumber}"/></a>
								</td>
							</c:if>
							<%-- Material --%>
							<td>
								<c:out value="${item.materialDesc}" />
							</td>
							<c:if test="${status3.first}">
								<%-- Delivery --%>
								<td style="text-align: left" rowspan="<c:out value="${matCnt}" />">
									<c:out value="${order.deliveryFromDate}"/><br />
									<c:out value="${order.deliveryFromTime}"/>&nbsp;
								</td>
								<td style="text-align: left" rowspan="<c:out value="${matCnt}" />">
									<c:out value="${order.deliveryToDate}"/><br />
									<c:out value="${order.deliveryToTime}"/>&nbsp;
								</td>
								<td style="text-align: left" rowspan="<c:out value="${matCnt}" />">
									<c:out value="${order.actualDeliveryDate}"/><br />
									<c:out value="${order.actualDeliveryTime}"/>&nbsp;
								</td>
								<%-- Vehicle --%>
								<td style="text-align: right" rowspan="<c:out value="${matCnt}" />">
									<c:choose>
										<c:when test="${not empty item.truckPlateNo}">
											<c:out value="${item.truckPlateNo}" />
										</c:when>
										<c:when test="${not empty order.vehicleName}" >
											<c:out value="${order.vehicleName}" />
										</c:when>
										<c:otherwise>
											<c:out value="N/A" />
										</c:otherwise>
									</c:choose>
								</td>
							</c:if>
							<td>
								<c:out value="${item.orderedQuantity}" />&nbsp;
								<c:out value="${item.orderedUom}" />
							</td>
							<td>
								<c:out value="${item.loadedQuantity}" />&nbsp;
								<c:out value="${item.loadedUom}" />
							</td>
							<c:if test="${status3.first}">
								<td style="text-align: left" rowspan="<c:out value="${matCnt}" />">
									<c:out value="${order.orderStatus}"/>&nbsp;
								</td>
							</c:if>
						</c:forEach>
						</tr>
						<c:set var="rowCnt" value="${rowCnt + 1}" />
					</c:forEach>
				</tbody>
			</table>
			</div>
		</td>
	</tr>
</table>


<%------------------------------%>
<%-- COLLECTIVE DELIVERY NOTE --%>
<%------------------------------%>
<table style="width: 100%;">
	<colgroup>
		<col width="50%" />
		<col width="50%" />
	</colgroup>
	<tbody>
		<tr>
		<td>&nbsp;
		</td>
		<td style="float: right">
			<div id="collDelNoteDiv"
				style="border: 0px; padding: 0px; width: 100%;"><jsp:include
				page="CustOrderCollDelNote.jsp"></jsp:include></div>
		</td>
		</tr>
	</tbody>
</table>

<!--</html:form>-->
