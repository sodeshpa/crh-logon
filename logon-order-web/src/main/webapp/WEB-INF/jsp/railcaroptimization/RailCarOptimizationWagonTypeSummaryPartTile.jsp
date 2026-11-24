
<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: RailCarOptimizationWagonTypeSummaryPartTile.jsp,v $ --%>

<%@ page session="true" %>
<%@ include file="../includeTLDs.jsp" %>

<script type="text/javascript">
	function initOpenOrdersPopup(){

		<%-- Open orders summary, not per type, but for all --%>
		var prefix = "";

		var pretag = 'div';
		if (Ext.isIE){
			pretag = 'pre';
		}
		
		var element = Ext.get("openOrdersPopup");
		var arr=[];
		var ret = '<table class="disponotestable">';
		<logic:iterate id="order" name="order_RailCarOptimizationForm" property="openOrders"  indexId="i">
			ret += '<tr><td style="width:80px"><nobr><b>' + prefix + '<c:out value="${order.orderNumber}"/>' + 
				'</b></nobr></td><td style="width:250px;max-width:250px;"><nobr><' + pretag + ' class="pretext">' +
				'<c:out value="${order.shiptoName}"/> / <c:out value="${order.orderedQuantity}"/> / <c:out value="${order.orderedUomCd}"/> / <c:out value="${order.materialDescription}"/>' + '</' + pretag + ' ></nobr></td></tr>'
		</logic:iterate>
		ret += '</table>';
		
		arr['ext:qtip'] = ret;
		arr['ext:qclass']= 'dispoqtip dispoinfoqtip';
		element.set(arr, arr);
	}

	function initOpenOrdersPopupPerWagonType(){
		<jsp:include page="RailCarOptimizationOpenOrderPerWagonTypeTile.jsp"/>
	}
</script>	

<c:if test="false">
    <%-- for preview in MyEclipse --%>
    <link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>

<table class="classic">
	<tr>
		<th><fmt:message key="railcaroptimization.texte.Position" /></th>
		<th><fmt:message key="railcaroptimization.texte.RailcarType" /></th>
		<th><fmt:message key="railcaroptimization.texte.Incoming" /></th>
		<th><fmt:message key="railcaroptimization.texte.Empty" /></th>
		<th><fmt:message key="railcaroptimization.texte.Loaded" /></th>
		<th><fmt:message key="railcaroptimization.texte.Available" /></th>
		<th><fmt:message key="railcaroptimization.texte.OpenOrders" /></th>
		<th><fmt:message key="railcaroptimization.texte.Difference" /></th>
	</tr>
	<c:set var="zebracount" value="0"/>
	
	<c:set var="totalloaded" value="0"/>
	<c:set var="totalincoming" value="0"/>
	<c:set var="totalempty" value="0"/>
	<c:set var="totalopenorders" value="0"/>
	
	<c:set var="zebracount" value="0"/>
	<logic:iterate id="wagonType" name="order_RailCarOptimizationForm" property="selectedWagonTypes"  indexId="i">
	
	<c:set var="zebracount" value="${(zebracount + 1)%2}"/>
	<tr class="zebra<c:out value="${zebracount}" />">
	<td class="zebradark">
		<c:out value="${i+1}"/>
	</td>			
	<td class="zebradark"><c:out value="${order_RailCarOptimizationForm.wagonTypeMap[wagonType].wagonTypeName}"/></td>
	<td>
		<c:set var="count"><c:out value="${order_RailCarOptimizationForm.typeInfoBean.incomingMap[wagonType]}"/></c:set>
		<c:choose><c:when test="${empty count}">0</c:when><c:otherwise><c:out value="${count}"/><c:set var="totalincoming" value="${(totalincoming + count)}"/></c:otherwise></c:choose>
	</td>
	<td>
		<c:set var="count"><c:out value="${order_RailCarOptimizationForm.typeInfoBean.emptyMap[wagonType]}"/></c:set>
		<c:choose><c:when test="${empty count}">0</c:when><c:otherwise><c:out value="${count}"/><c:set var="totalempty" value="${(totalempty + count)}"/></c:otherwise></c:choose>	
	</td>
	<td>
		<c:set var="count"><c:out value="${order_RailCarOptimizationForm.typeInfoBean.loadedMap[wagonType]}"/></c:set>
		<c:choose><c:when test="${empty count}">0</c:when><c:otherwise><c:out value="${count}"/><c:set var="totalloaded" value="${(totalloaded + count)}"/></c:otherwise></c:choose>	
	</td>
	<td  class="zebradark">
		<c:set var="availablecount"><c:out value="${order_RailCarOptimizationForm.typeInfoBean.availableMap[wagonType]}"/></c:set>
		<c:choose><c:when test="${empty availablecount}">0</c:when><c:otherwise><c:out value="${availablecount}"/></c:otherwise></c:choose>	
	</td>
	<td  class="zebradark">	
		<c:set var="priorityfactor"><c:out value="${order_RailCarOptimizationForm.typeInfoBean.typePriorityMap[wagonType]}"/></c:set>
		<c:choose><c:when test="${empty priorityfactor}">0</c:when><c:otherwise>
		<div id = "openOrdersPopup<c:out value="${wagonType}"/>" ><c:out value="${priorityfactor}"/></div>
		</c:otherwise></c:choose>	
	</td>
	<td  class="zebradark">
		<fmt:formatNumber type="number" value="${availablecount - priorityfactor}"/>
	</td>
	</tr>
	</logic:iterate>
	<tr>
	<th>&nbsp;<c:set var="totalopenorders" value="${(order_RailCarOptimizationForm.openOrderCount)}"/>
	</th>
	<th>
		<fmt:message key="railcaroptimization.texte.Total" />
	</th>
	<th>
		<fmt:formatNumber type="number" value="${totalincoming}"/>
	</th>
	<th>
		<fmt:formatNumber type="number" value="${totalempty}"/>
	</th>
	<th>
		<fmt:formatNumber type="number" value="${totalloaded}"/>	
	</th>
	<th>
		<fmt:formatNumber type="number" value="${totalincoming + totalempty + totalloaded}"/>	
	</th>
	<th>
		<div id = "openOrdersPopup" ><fmt:formatNumber type="number" value="${totalopenorders}" /></div>	
	</th>
	<th>
		<fmt:formatNumber type="number" value="${totalincoming + totalempty + totalloaded - totalopenorders}"/>
	</th>	
	</tr>
</table>

<script type="text/javascript">
	initOpenOrdersPopup();
	initOpenOrdersPopupPerWagonType();
</script>
