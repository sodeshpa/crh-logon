<%@ page session="true"%>
<%@ include file="../includeTLDs.jsp"%>

<c:set var="deleteOrderMap" value="${requestScope.deleteOrderMap}" />
<c:set var="manualDeallocateOrderNumberList" value="${requestScope.manualDeallocateOrderNumberList}" />
<c:set var="custorderId" value="${requestScope.custorderId}" />
<c:set var="sourceAction" value="${requestScope.sourceAction}" />

<c:choose>
	<%-- Handle if deleteOrderIdList is empty (can close custorder- yes/no)--%>
	<c:when test="${empty deleteOrderMap && not empty manualDeallocateOrderNumberList}" >
		Ext.MessageBox.alert('<fmt:message key="orderdetail.custorder.button.closeCustomerOrder.popup.cannotClose"/>',''
				+ '<fmt:message key="orderdetail.custorder.button.closeCustomerOrder.popup.deallocateFirst"/>:<br />'
				<c:forEach var="orderNo" items="${manualDeallocateOrderNumberList}">
				+ '<c:out value="${orderNo}" /> <br />'
				</c:forEach>
				);	
	</c:when>
	<c:otherwise>
		Ext.MessageBox.buttonText.yes = '<fmt:message key="extjsgrid.list.buttonYes"/>';
		Ext.MessageBox.buttonText.no = '<fmt:message key="extjsgrid.list.buttonNo"/>';
		Ext.MessageBox.confirm('<fmt:message key="orderdetail.custorder.button.closeCustomerOrder.popup.closing"/>',''
				<c:if test="${not empty manualDeallocateOrderNumberList}" >
					+ '<fmt:message key="orderdetail.custorder.button.closeCustomerOrder.popup.deallocateFirst"/>:<br />'
					<c:forEach var="orderNo" items="${manualDeallocateOrderNumberList}">
					+ '<c:out value="${orderNo}" /><br />'
					</c:forEach>
					+ '<br />'
				</c:if>
				<c:if test="${not empty deleteOrderMap}" >
					+ '<fmt:message key="orderdetail.custorder.button.closeCustomerOrder.popup.deleteAutomatically"/>:<br /><br />'
					<c:forEach var="order" items="${deleteOrderMap}">
					+ '<c:out value="${order.value.orderNumber}" /> <br />'
						<c:forEach var="item" items="${order.value.orderItems}">
						+ '- <c:out value="${item.orderedQuantity}" /> <c:out value="${item.orderedUom.uomCd}" /> <br />'
						</c:forEach>
						+ '<br />'
					</c:forEach>
				</c:if>
				<c:choose>
					<c:when test="${not empty deleteOrderMap}" >
						+ '<fmt:message key="orderdetail.custorder.button.closeCustomerOrder.popup.askDeleteAutomatically"/><br />'
					</c:when>
					<c:otherwise>
						+ '<fmt:message key="orderdetail.custorder.button.closeCustomerOrder.popup.askCloseCustorder"/><br />'	
					</c:otherwise>
				</c:choose>
				,
	    		function(btn) {
	    			if (btn == 'yes') {
	    				<c:choose>
		    				<c:when test="${sourceAction == 'OrderDetail'}">	    				
		    					confirmSetCustorderToFinishedManuallyOrdDet(<c:out value="${custorderId}"/>);
		    				</c:when>
		    				<c:when test="${sourceAction == 'Bestellung'}">	    				
		    					confirmSetCustorderToFinishedManuallyBest(<c:out value="${custorderId}"/>);
		    				</c:when>
	    				</c:choose>
	    			}
	    		});	
	</c:otherwise>
</c:choose>
