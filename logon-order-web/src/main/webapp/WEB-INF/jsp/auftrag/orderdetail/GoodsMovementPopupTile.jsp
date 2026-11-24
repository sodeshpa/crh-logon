<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: GoodsMovementPopupTile.jsp,v $ --%>

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@ page import="com.crh.logon.constants.Constant"%>
<%@ page import="com.crh.logon.order.web.ajax.OrderDetailAjaxService"%>

<script type="text/javascript">
    function closeGoodsMovementsPopup() {
            
        var logonPopup2 = Ext.get('logonPopup2');
        hidePopup(logonPopup2);
    }
</script>

<c:set var="form" scope="page" value="${order_auftrag_OrderDetailForm}" />

<table class="classicblue" style="border:none; width:auto">
	<tr>
		<th>
			<fmt:message key="orderdetail.material.label.goodsmovementinfo" />
		</th>
	</tr>
	<tr>
		<td class="popupcontent">
			<table width="100%">
				<thead>
					<tr>
						<th><fmt:message key="orderdetail.material.goodsmovement.column.movements"/></th>
						<th><fmt:message key="orderdetail.material.goodsmovement.column.id"/></th>
						<th><fmt:message key="orderdetail.material.goodsmovement.column.netQuantity"/></th>
						<th><fmt:message key="orderdetail.material.goodsmovement.column.uomCd"/></th>
						<th><fmt:message key="orderdetail.material.goodsmovement.column.fromPlant"/></th>
						<th><fmt:message key="orderdetail.material.goodsmovement.column.fromStorageLoc"/></th>
						<th><fmt:message key="orderdetail.material.goodsmovement.column.toPlant"/></th>
						<th><fmt:message key="orderdetail.material.goodsmovement.column.toStorageLoc"/></th>
						<th><fmt:message key="orderdetail.material.goodsmovement.column.movementType"/></th>
						<th><fmt:message key="orderdetail.material.goodsmovement.column.date"/></th>
						<th><fmt:message key="orderdetail.material.goodsmovement.column.reason"/></th>
					</tr>
				</thead>
				<c:forEach var="bean" varStatus="status" items="${form.materialBean.goodsMovementBeans}">
					<tr>
						<td><c:out value="${bean.movement}"/></td>
						<td><c:out value="${bean.entity.goodsMovementId}"/></td>
						<td><c:out value="${bean.entity.netQuantity}"/></td>
						<td><c:out value="${bean.entity.netQuantityUom.uomCd}"/></td>
						<td><c:out value="${bean.fromPlant}"/></td>
						<td><c:out value="${bean.entity.fromStorageLoc}"/></td>
						<td><c:out value="${bean.deliveryPlant}"/></td>
						<td><c:out value="${bean.entity.toStorageLoc}"/></td>
						<td><c:out value="${bean.entity.movementType}"/></td>
						<td><c:out value="${bean.date}"/></td>
						<td><c:out value="${bean.reason}"/></td>
					</tr>
				</c:forEach>  
			</table>
		</td>
	</tr>
	<tr>
		<td class="buttons right">
			<nobr>
				<input type="button" class="ViewButtonStyle" onClick="closeGoodsMovementsPopup()"
				value="<fmt:message key="buttons.close"/>">
			</nobr>
		</td>
	</tr>
</table>

<script type="text/javascript">
    Ext.get('logonPopup2').center();
    Ext.get('logonPopup2').show();
</script>

<%-- END $RCSfile: GoodsMovementPopupTile.jsp,v $ --%>
