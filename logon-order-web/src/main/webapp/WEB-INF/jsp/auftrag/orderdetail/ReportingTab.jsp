<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page
	import="com.crh.logon.order.web.actions.auftrag.OrderDetailAction"%>
<%@page
	import="com.crh.logon.persistence.entities.common.OrderItemEntity"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<c:set var="orderDetailUrl">
	/../order/auftrag/OrderDetail.do
</c:set>

<%----------------------------------------------------------------------------------------------%>
<%--------------------START AUTHORIZATION DRIVEN DISABLED SETTINGS -----------------------------%>
<%----------------------------------------------------------------------------------------------%>
<c:set var="isReportingDisabled">true</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_REPORT_W %>">
		<c:set var="isReportingDisabled">false</c:set>	
</crh-common:authorize>
 
<%----------------------------------------------------------------------------------------------%>
<%--------------------END AUTHORIZATION DRIVEN DISABLED SETTINGS -------------------------------%>
<%----------------------------------------------------------------------------------------------%> 

<script type="text/javascript">
	function loadRepItem(matIndex) {

	     form = Ext.getDom('OrderDetailForm');
		 p = {'formAction' :'<%=OrderDetailAction.FORWARD_REPORTING_ITEM%>',
					'matIndex' :matIndex
				};
		 addFormElements(p, form);
		 
		
		var diva = Ext.get('ReportingItemDiv');
		diva.load( {
				url :'<c:url value="${orderDetailUrl}"/>',
				params : p,
				text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
				scripts :true
				});
	}

</script>
<!--<html:form action="auftrag/OrderDetail.do"  onsubmit="return false;" >-->
<c:if test="${empty order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList}">
	</br>
	<span class="formtitle"><fmt:message
		key="orderdetail.rep.label.no.rep" /></span>
</c:if>

<c:if test="${not empty order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList}"> 
<!-- Beginn scrollform -->
<div class="subformlist"
	style="height: 80px; margin-top: 0px; width: 98%;">
<table cellpadding="0" cellspacing="0" style="border: 0px; width: 100%;">
	<colgroup>
		<col width="2%">
		<col width="5%">
		<col width="20%">
		<col width="8%">
		<col width="10%">
		<col width="8%">
		<col width="10%">
		<col width="8%">
		<col width="10%">
		<col width="4%">
		<col width="8%">
		<col width="7%">
	</colgroup>
	<tbody>
		<tr>
			<td>&nbsp;</td>
			<td class="formtitle"><fmt:message
				key="orderdetail.material.field.position" /></td>
			<td class="formtitle"><fmt:message
				key="orderdetail.material.field.material" /></td>
			<td colspan="2" class="formtitle"><fmt:message
				key="orderdetail.material.field.order.quantity" /></td>
			<td colspan="2" class="formtitle"><fmt:message
				key="orderdetail.material.field.net.weight" /></td>
			<td colspan="2" class="formtitle"><fmt:message
				key="orderdetail.material.field.return.quantity" /></td>
			<td class="formtitle"><fmt:message
				key="orderdetail.material.field.credit.note" /></td> 
			<td colspan="2" class="formtitle"><fmt:message
				key="orderdetail.material.field.delivered.quantity" /></td>
		</tr>
		<c:forEach var="entity" varStatus="status"
			items="${order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList}">

			<tr>
				<td valign="top"><input type="radio" name="radio_material"
					onclick="javascript:loadRepItem('<c:out value="${status.count-1}"/>')"
					<c:if test="${order_auftrag_OrderDetailForm.reportingBean.selOrderItemIndex == status.count-1}" > checked="checked" </c:if> />

				</td>
				<td><span class="textfielddisabled"> <c:out
					value="${order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList[status.count-1].orderItemNo}" />&nbsp;
				</span></td>
				<td><span class="textfielddisabled" style="width: 90%;">
				<c:out
					value="${order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList[status.count-1].sapMaterialName}" />&nbsp;
				</span></td>
				<td><span class="textfielddisabled"> <c:out
					value="${order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList[status.count-1].orderedQuantity}" />&nbsp;
				</span></td>
				<td><span class="textfielddisabled" style="width: 90%;">
				<c:out
					value="${order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList[status.count-1].orderedUom}" />&nbsp;
				</span></td>
				<td><span class="textfielddisabled" style="width: 90%;">
				<c:out
					value="${order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList[status.count-1].loadedQuantity}" />&nbsp;
				</span></td>
				<td><span class="textfielddisabled" style="width: 80%;">
				<c:out
					value="${order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList[status.count-1].loadedUom}" />&nbsp;
				</span></td>

				<td><span class="textfielddisabled" style="width: 90%;">
				<c:out
					value="${order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList[status.count-1].returnedQuantity}" />&nbsp;
				</span></td>
				<td><span class="textfielddisabled" style="width: 80%;">
				<c:out
					value="${order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList[status.count-1].returnedUom}" />&nbsp;
				</span></td>
				<td><html:checkbox
					property="reportingBean.orderItemFBeanList[${status.count-1}].creditNote"
					disabled="true" /></td>
				<td><span class="textfielddisabled" style="width: 90%;">
				<c:out
					value="${order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList[status.count-1].deliveredQuantity}" />&nbsp;
				</span></td>
				<td><span class="textfielddisabled" style="width: 80%;">
				<c:out
					value="${order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList[status.count-1].deliveredUom}" />&nbsp;
				</span></td>
			</tr>
		</c:forEach>
	</tbody>
</table>
</div>
<!-- End scrollform -->


<!-- height: 250; -->
<div id="ReportingItemDiv" class="scrollist" 
	style="border: 0px; padding: 0px; width: 100%; height: auto;"><jsp:include
	page="ReportingItemTab.jsp"></jsp:include></div>

</c:if>  

<!--</html:form>-->

