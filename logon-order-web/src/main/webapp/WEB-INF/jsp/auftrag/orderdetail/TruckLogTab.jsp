<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>



<!--Ext.get('TabsDiv').getWidth() -->


<!--<html:form action="auftrag/OrderDetail.do"  onsubmit="return false;" >-->

<div id="chart" style="height: 460px"><c:url var="chartUrl"
	value="/truckLogChart.servlet">
	<c:param name="orderId"
		value="${order_auftrag_OrderDetailForm.order.orderId}" />
	<c:param name="width" value="650" />
	<c:param name="height" value="450" />
</c:url> <img id="chartImg" src="<c:out value="${chartUrl}"/>" width="650"
	height="450" /></div>

<!--</html:form>-->
