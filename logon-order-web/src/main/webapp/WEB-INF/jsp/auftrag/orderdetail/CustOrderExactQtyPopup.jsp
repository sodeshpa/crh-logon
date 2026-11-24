<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.order.web.actions.auftrag.OrderDetailAction"%>
	
<c:set var="tobeCreatedOrders" value="${requestScope.resultWrapper.tobeCreatedOrders}" />
<c:set var="tobeDeletedOrders" value="${requestScope.resultWrapper.tobeDeletedOrders}" />
<c:set var="tobeEditedOrders" value="${requestScope.resultWrapper.tobeEditedOrders}" />

Ext.MessageBox.buttonText.yes = '<fmt:message key="extjsgrid.list.buttonYes"/>';
Ext.MessageBox.buttonText.no = '<fmt:message key="extjsgrid.list.buttonNo"/>';
Ext.MessageBox.confirm('<fmt:message key="orderdetail.custorder.exactqtyPopup.title" />',
  		'<fmt:message key="orderdetail.custorder.exactqtyPopup.explanation" /><br /><br />'
   		
	   	<c:if test="${not empty tobeEditedOrders}" >
	   	+ '<fmt:message key="orderdetail.custorder.exactqtyPopup.editingOrders"/><br />'
   		+ '<table cellpadding="2"><tr>'
   			<c:forEach var="wrapper" items="${tobeEditedOrders}">
		   		+ '<tr><td><c:out value="${wrapper.order.orderNumber}" /> | </td><td width="3px"></td><td><c:out value="${wrapper.oldOrderedQty}" /> <c:out value="${wrapper.oldOrderedUom}" /> -> <c:out value="${wrapper.newOrderedQty}"/> <c:out value="${wrapper.newOrderedUom}" /></td></tr>'
		   	</c:forEach>
   		+ '</tr></table>'
   		+ '<br />'
	   	</c:if>
   		
   		<c:if test="${not empty tobeCreatedOrders}" >
   		+ '<fmt:message key="orderdetail.custorder.exactqtyPopup.toBeCreatedOrders"/><br />'
   		+ '<table cellpadding="2"><tr>'
   			<c:forEach var="order" items="${tobeCreatedOrders}">
		   		+ '<tr><td>1x </td><td width="3px"></td><td><c:out value="${order.orderItems[0].orderedQuantity}" /> <c:out value="${order.orderItems[0].orderedUom.uomCd}" /></td></tr>'
		   	</c:forEach>
   		+ '</tr></table>'
   		+ '<br />'
	   	</c:if>
   		
   		<c:if test="${not empty tobeDeletedOrders}" >
   		+ '<fmt:message key="orderdetail.custorder.exactqtyPopup.toBeDeletedOrders"/><br />'
   		+ '<table cellpadding="2"><tr>'
   			<c:forEach var="order" items="${tobeDeletedOrders}">
		   		+ '<tr><td><c:out value="${order.orderNumber}" /> | </td><td width="3px"></td><td><c:out value="${order.orderItems[0].orderedQuantity}" /> <c:out value="${order.orderItems[0].orderedUom.uomCd}" /></td></tr>'
		   	</c:forEach>
   		+ '</tr></table>'
   		+ '<br />'
	   	</c:if>
	   	
   		+ '<fmt:message key="orderdetail.custorder.exactqtyPopup.question"/>'
   		,
   		function(btn) {
   			if (btn == 'yes') {
   				exactCustOrderFlagSwiched = false;
				exactSingleOrderFlagSwiched = false;
				saveCustorder('<%=OrderDetailAction.FORWARD_SAVE_CUST_ORDER_CONFIRMED%>');
   			}
   			else {
				if (exactCustOrderFlagSwiched) {
	 				exactCustOrderFlagChecked = !exactCustOrderFlagChecked;
	 				exactCustOrderFlagSwiched = false;
 				}
 				if (exactSingleOrderFlagSwiched) {
	 				exactSingleOrderFlagChecked = !exactSingleOrderFlagChecked;
	 				exactSingleOrderFlagSwiched = false;
 				}
   			}
   		});	
