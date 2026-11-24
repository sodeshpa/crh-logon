<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%><%-- 
BEGIN $RCSfile: BimodalRailcarMonitoringResultsPartTile.jsp,v $ 
--%>
<%@ taglib uri="http://com.crh.logon/tags/order-taglib.tld" prefix="crh-order" %>
<%@ include file="../includeTLDs.jsp" %>
<%@page import="com.crh.logon.persistence.entities.views.BimodalRailOrderEntity" %>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@page import="com.crh.logon.constants.Constant"%>

<c:set var="showOrderLink">false</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_R %>">
<c:set var="showOrderLink">true</c:set>
</crh-common:authorize>

<c:set var="ORDER_STATUS_SHIPPING"><%=Constant.ORDER_STATUS_SHIPPING%></c:set>
<c:set var="ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT"><%=Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT%></c:set>
<c:set var="ORDER_STATUS_UNLOADING"><%=Constant.ORDER_STATUS_UNLOADING%></c:set>
<c:set var="ORDER_STATUS_DELIVERED"><%=Constant.ORDER_STATUS_DELIVERED%></c:set>
<c:set var="ORDER_STATUS_FINISHED"><%=Constant.ORDER_STATUS_FINISHED%></c:set>

	<table class="classicblue" style="width:100%;margin-top:10px;">
		<tr>
        	<th>
        		<fmt:message key="bimodalrailcarmonitoring.header.railway.Station"/>
        	</th>
        	<th>
        		<a onclick='setOrderBy("<%=BimodalRailOrderEntity.ATTR_ORDER_NUMBER%>")'><fmt:message key="bimodalrailcarmonitoring.header.order.number"/></a>
        	</th>        	
        	<th>
        		<a onclick='setOrderBy("<%=BimodalRailOrderEntity.ATTR_ORDER_STATUS_TXT%>")'><fmt:message key="bimodalrailcarmonitoring.header.order.status"/></a>
        	</th>        	
            <th>
            	<a onclick='setOrderBy("<%=BimodalRailOrderEntity.ATTR_WAGON_NO%>")'><fmt:message key="bimodalrailcarmonitoring.header.railcar.no"/></a>
            </th> 
            <th>
            	<a onclick='setOrderBy("<%=BimodalRailOrderEntity.ATTR_WAGON_TYPE%>")'><fmt:message key="bimodalrailcarmonitoring.header.railcar.type"/></a>
            </th> 
            <th>
				<a onclick='setOrderBy("<%=BimodalRailOrderEntity.ATTR_PRODUCTION_PLANT_NAME%>")'><fmt:message key="bimodalrailcarmonitoring.header.production.plant"/></a>
            </th> 
            <th>
            	<a onclick='setOrderBy("<%=BimodalRailOrderEntity.ATTR_SAP_MATERIAL_DESC%>")'><fmt:message key="bimodalrailcarmonitoring.header.material"/> / <fmt:message key="bimodalrailcarmonitoring.header.net.weight"/>&nbsp;<fmt:message key="bimodalrailcarmonitoring.header.tons.brackets"/></a>
            </th>
            <th>
            	<a onclick='setOrderBy("<%=BimodalRailOrderEntity.ATTR_ACTUAL_DELIVERY_DAT%>")'><fmt:message key="bimodalrailcarmonitoring.header.arrived"/></a>
            </th>
            <th>
            	<a onclick='setOrderBy("<%=BimodalRailOrderEntity.ATTR_WAGON_IDLE_TIME_MIN%>")'><fmt:message key="bimodalrailcarmonitoring.header.railcar.idle.time"/></a> 
            </th>
            <th title="<fmt:message key="bimodalrailcarmonitoring.header.number.of.dependent.orders.tooltip"/>">
            	<fmt:message key="bimodalrailcarmonitoring.header.number.of.dependent.orders"/>
            </th>
            <th title="<fmt:message key="bimodalrailcarmonitoring.header.remaining.quantity.tooltip"/>">
            	<fmt:message key="bimodalrailcarmonitoring.header.remaining.quantity"/>&nbsp;<fmt:message key="bimodalrailcarmonitoring.header.tons.brackets"/>
            </th>
            <th>
            	<fmt:message key="bimodalrailcarmonitoring.header.dedicated.shipto"/>
            </th>            
      	  </tr>
      	  <tr>
	<c:set var="zebracount" value="0"/>      	  
		<logic:iterate id="result" name="order_BimodalRailcarMonitoringForm" property="resultList"  indexId="i">
		
		<c:set var="leftShippingpoint">false</c:set>
		<c:if test="${(result.entity.orderStatusCd == ORDER_STATUS_SHIPPING) ||
            (result.entity.orderStatusCd == ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT) ||
            (result.entity.orderStatusCd == ORDER_STATUS_UNLOADING) ||
            (result.entity.orderStatusCd == ORDER_STATUS_DELIVERED) ||
            (result.entity.orderStatusCd == ORDER_STATUS_FINISHED)
            }">
			<c:set var="leftShippingpoint">true</c:set>
    	</c:if>
		<c:set var="zebracount" value="${(zebracount + 1)%2}"/>

			<tr class="zebra<c:out value="${zebracount}" />">
			<crh-order:bimodalMonitoringResultRowSpan itemIndex="<%=(Integer)pageContext.getAttribute("i") %>" printTdTag="true">
        		<c:out value="${result.entity.railwayStationName}"/>
			</crh-order:bimodalMonitoringResultRowSpan>
			<td>
			<c:choose>
				<c:when test="${showOrderLink}">
					<a href="javascript:showPopup('/logon/order/auftrag/OrderDetailEntry.do?orderid=<c:out value="${result.entity.orderId}"/>')"><c:out value="${result.entity.orderNumber}"/></a>
				</c:when>
				<c:otherwise>			
        			<c:out value="${result.entity.orderNumber}"/></a>
				</c:otherwise>
			</c:choose>
			</td>
			<c:choose>
				<c:when test="${leftShippingpoint}">
					<td>
				</c:when>
				<c:otherwise>
					<td style="background-color:#ccffcc">
				</c:otherwise>
			</c:choose>
            	<c:out value="${result.entity.orderStatusTxt}"/>
            </td>
            <td>
            	<c:out value="${result.entity.wagonNo}"/>
            </td>
            <td>
				<c:out value="${result.entity.wagonType}"/>
            </td>
            <td>
        		<c:out value="${result.entity.productionPlantName}"/>
            </td> 
      	    <td>
      	    	<c:out value="${result.entity.sapMaterialDesc}"/>&nbsp;(<c:out value="${result.railQty}"/>) 
      	    </td>
      	    <td>
				<c:out value="${result.actualDelivery}"/>
      	    </td>
      	    <td>
				<c:out value="${result.idleTime}"/>
      	    </td>
      	    <td>
				<c:out value="${result.depTruckOrderDelivery}"/>
      	    </td>
      	    <td>
				<c:out value="${result.remainingLoad}"/>
      	    </td>
      	    <td style="width: 240px;max-width: 240px">
				<c:out value="${result.dedicatedShiptoNames}"/>
      	    </td>      	    
        </tr>
        </logic:iterate>
        </table>