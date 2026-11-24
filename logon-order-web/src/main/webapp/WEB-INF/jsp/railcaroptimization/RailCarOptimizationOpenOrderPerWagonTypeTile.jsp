<%@ include file="../includeTLDs.jsp" %>

		<%-- Open orders popup per type --%>
		var prefix = "";

		var pretag = 'div';
		if (Ext.isIE){
			pretag = 'pre';
		}
<logic:iterate id="wagonTypeCd" name="order_RailCarOptimizationForm" property="selectedWagonTypes"  indexId="i">

		var element = Ext.get("openOrdersPopup" + <c:out value="${wagonTypeCd}"/>);
		if (!Ext.isEmpty(element)){		
			var arr=[];
			var ret = '<table class="disponotestable">';
			<logic:iterate id="order" name="order_RailCarOptimizationForm" property="openOrders"  indexId="i">
				<c:if test="${!empty order_RailCarOptimizationForm.shiptoIdsForWagonTypes[wagonTypeCd][order.shiptoId]}">
				ret += '<tr><td style="width:80px"><nobr><b>' + prefix + '<c:out value="${order.orderNumber}"/>' + 
				'</b></nobr></td><td style="width:250px;max-width:250px;"><nobr><' + pretag + ' class="pretext">' +
				'<fmt:formatNumber type="number" value="${1/order.countWagonTypes}"/> / <c:out value="${order.shiptoName}"/> / <c:out value="${order.orderedQuantity}"/> / <c:out value="${order.orderedUomCd}"/> / <c:out value="${order.materialDescription}"/>' + '</' + pretag + ' ></nobr></td></tr>'
				</c:if>
			</logic:iterate>
			ret += '</table>';
		
			arr['ext:qtip'] = ret;
			arr['ext:qclass']= 'dispoqtip dispoinfoqtip';
			element.set(arr, arr);
		}
</logic:iterate>
	
