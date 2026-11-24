<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: OngoingWebOrders.jsp,v $ --%>

<%@ include file="../includeTLDs.jsp" %>

<%@page import="com.crh.logon.constants.Constant"%>
<%@page import="java.util.List"%>

<!-- beginn Offene Bestellung -->


<%@page import="com.crh.logon.cust.web.forms.webbestellung.WebBestellungForm"%>
<c:set var="ORDER_STATUS_DELIVERED"><%=Constant.ORDER_STATUS_DELIVERED%></c:set>
<c:set var="ORDER_STATUS_DELETED"><%=Constant.ORDER_STATUS_DELETED%></c:set>
<c:set var="ORDER_STATUS_FINISHED"><%=Constant.ORDER_STATUS_FINISHED%></c:set>
<c:set var="ORDER_STATUS_NEW"><%=Constant.ORDER_STATUS_NEW%></c:set>
<c:set var="DELIVERY_TYPE_PICKUP"><%=Constant.DELIVERY_TYPE_PICKUP%></c:set>
<c:set var="DELIVERY_TYPE_SHIP_PICKUP"><%=Constant.DELIVERY_TYPE_SHIP_PICKUP%></c:set>
<c:set var="DELIVERY_TYPE_BLOCKTRAIN_PICKUP"><%=Constant.DELIVERY_TYPE_BLOCKTRAIN_PICKUP%></c:set>
<c:set var="ORDER_STATUS_DISPATCHED"><%=Constant.ORDER_STATUS_DISPATCHED%></c:set>
<c:set var="ORDER_STATUS_ARRIVED_AT_LOADINGPOINT"><%=Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT%></c:set>
<c:set var="ORDER_STATUS_LOADING"><%=Constant.ORDER_STATUS_LOADING%></c:set>
<c:set var="ORDER_STATUS_LOADED"><%=Constant.ORDER_STATUS_LOADED%></c:set>
<c:set var="ORDER_STATUS_SHIPPING"><%=Constant.ORDER_STATUS_SHIPPING%></c:set>
<c:set var="ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT"><%=Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT%></c:set>
<c:set var="ORDER_STATUS_UNLOADING"><%=Constant.ORDER_STATUS_UNLOADING%></c:set>


<%@page import="com.crh.logon.persistence.entities.common.OrderEntity"%>
<%@page import="com.crh.logon.persistence.entities.views.OrderCreateEntity"%>
<%@ page import="com.crh.logon.business.services.utils.DateTimeAwareOrderWrapper" %>

<table class="big_form2" cellpadding="0" cellspacing="0" style="padding:0px; margin:0px; border-collapse:collapse;">
	<tr style="border-bottom: 2px solid #599ade;">
      <td class="labelblue_bg"><span class="labelbluebig"><fmt:message key="webbestellung.columns.ongoingorders"/></span></td>
    </tr>
    <tr>
      <td>
		<table class="classicblue" style="width:100%; margin-top:4px;">       
		  <%--      <colgroup>
            <col width="1%">
            <col width="12%">
            <col width="15%">
            <col width="2%">
            <col width="4%">
            <col width="10%">
            <col width="10%">
            <col width="12%">
            <col width="10%">
            <col width="10%"
            <col width="19%">
            <col width="1%">
		</colgroup>   --%>
    	<tbody>
<!------------------------------->
<!--------- Table header --------> 
<!------------------------------->   	
	   	<tr>
          	<th><fmt:message key="fields.order.status"/></th>
            <th rowspan="2"><fmt:message key="fields.produkt.produkt"/></th>
            <th rowspan="2">#</th> 
            <th rowspan="2"><fmt:message key="fields.uom.uom"/></th>
            <th colspan="2">
				<fmt:message key="fields.order.delivery.time"/>
			</th> 
      	    <th rowspan="2"><fmt:message key="webbestellung.expectedarrive"/></th>
      	    <th rowspan="2"><fmt:message key="fields.type"/></th>
      	    <th><fmt:message key="dispokarte.texte.rechnungsemp"/></th>
      	    <th><fmt:message key="fields.remarks"/></th>
      	    <th rowspan="2">&nbsp;</th>
        </tr>
        
        <tr>
        	<td class="cell_gray"><fmt:message key="fields.order.number"/></td>
	        <td class="cell_gray"><fmt:message key="fields.time.from" /></td>
			<td class="cell_gray"><fmt:message key="fields.time.until" /></td>
			<td class="cell_gray"><fmt:message key="dispokarte.texte.rechnungsgeber" /></td>
			<td class="cell_gray"><fmt:message key="fields.customerreferencenumber.short" /></td>
		</tr>
        
<!------------------------------------>
<!--------- Content of table --------->
<!--------- Ongoing orders-- ---------> 
<!------------------------------------>
		<c:set var="index" value="0"/>
		<% pageContext.setAttribute("orderCreateList", ((WebBestellungForm)session.getAttribute("cust_WebBestellungForm")).getOrderList());%>
		<logic:iterate id="order" name="orderCreateList"  indexId="i">
		<c:if test="${order.orderStatusCd != ORDER_STATUS_FINISHED && order.orderStatusCd != ORDER_STATUS_DELETED}">
        <crh-common:dtOrderWrapperRowspan  printTdTag="false" pageContextOrderList="<%=(List)pageContext.getAttribute("orderCreateList") %>" pageContextIndexName="i" pageContextOrderCreateEntityName="order">
        	<c:set var="index" value="${index + 1 }"/>
        </crh-common:dtOrderWrapperRowspan>
        <tr class="zebra<c:out value="${index % 2}"/>">          
          <crh-common:dtOrderWrapperRowspan pageContextOrderList="<%=(List)pageContext.getAttribute("orderCreateList") %>" pageContextIndexName="i" pageContextOrderCreateEntityName="order" printInTd="style=\"vertical-align:middle;\"">
          <p>
          	<c:out value="${order.statusTxt}"/><br>	  	
  				<a href="javascript:showPopup('/logon/order/auftrag/OrderDetailEntry.do?orderid=<c:out value="${order.orderId}"/>')"><c:out value="${order.orderNumber}"/></a>
            </p>
			</crh-common:dtOrderWrapperRowspan>
			<td class="label" style="vertical-align:middle">
			<nobr><c:out value="${order.materialTxt}"/></nobr><br>(<c:out value="${order.materialNo}"/>)
          </td>
            <td style="vertical-align:middle"><c:out value="${order.orderedQuantity}"/></td>
            <td style="vertical-align:middle"><nobr><c:out value="${order.unitTxt}"/></nobr></td>
          <crh-common:dtOrderWrapperRowspan pageContextOrderList="<%=(List)pageContext.getAttribute("orderCreateList") %>" pageContextIndexName="i" pageContextOrderCreateEntityName="order" printInTd="style=\"vertical-align:middle;\"">
      	    	<c:out value="${order.deliveryFromDate}"/>
      	    	<c:out value="${order.deliveryFromTime}"/>
		   </crh-common:dtOrderWrapperRowspan>
      	             <crh-common:dtOrderWrapperRowspan pageContextOrderList="<%=(List)pageContext.getAttribute("orderCreateList") %>" pageContextIndexName="i" pageContextOrderCreateEntityName="order" printInTd="style=\"vertical-align:middle;\"">
      	    	<c:out value="${order.deliveryToDate}"/>
      	    	<c:out value="${order.deliveryToTime}"/> 	    
			</crh-common:dtOrderWrapperRowspan>
      	              <crh-common:dtOrderWrapperRowspan pageContextOrderList="<%=(List)pageContext.getAttribute("orderCreateList") %>" pageContextIndexName="i" pageContextOrderCreateEntityName="order" printInTd="style=\"vertical-align:middle;\"">
      	    <c:choose>
      	    		<%-- 1st priority is real start date if available --%>
      	    		<c:when test="${order.unloadRealStartDate != null}">
      	    			<%-- ADN43831: grey text if not unloaded by PDA - decision by unload status not really correct? --%>
      	    			<c:if test="${order.unloadStatusCd != 2}"><font color="#CCCCCC"></c:if>
      	    			<c:out value="${order.unloadRealStartDate}"/>
      	    			<c:out value="${order.unloadRealStartTime}"/>
      	    			<c:if test="${order.unloadStatusCd != 2}"></font></c:if>
      	    		</c:when>
      	    		<c:when test="${order.unloadPlannedStartDate != null}">
      	    		<%-- This planned start is not always the planned start unload what is in the db, since it must consider breaks, and the real starting dates of other assignment_parts.--%>
      	    			(<c:out value="${order.unloadPlannedStartDate}"/>
      	    			<c:out value="${order.unloadPlannedStartTime}"/>)
      	    		</c:when>
      	    		<c:otherwise>
      	    			-
      	    		</c:otherwise>
      	    	</c:choose>
				<c:if test="${(order.orderStatusCd eq ORDER_STATUS_ARRIVED_AT_LOADINGPOINT ||
				order.orderStatusCd eq ORDER_STATUS_ARRIVED_AT_LOADINGPOINT ||
				order.orderStatusCd eq ORDER_STATUS_LOADING ||
				order.orderStatusCd eq ORDER_STATUS_LOADED ||
				order.orderStatusCd eq ORDER_STATUS_SHIPPING ||
				order.orderStatusCd eq ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT ||
				order.orderStatusCd eq ORDER_STATUS_UNLOADING) &&
				order.iTrackAvailableFlag}">
						  <img src="<crh-common:url value='/images/information.gif'/>"
								  title="<fmt:message key="webbestellung.itrackavailable.order.mouseover.text"/>"/>
				</c:if>
			</crh-common:dtOrderWrapperRowspan>
      	    <crh-common:dtOrderWrapperRowspan pageContextOrderList="<%=(List)pageContext.getAttribute("orderCreateList") %>" pageContextIndexName="i" pageContextOrderCreateEntityName="order" printInTd="style=\"vertical-align:middle;\"">
      	    <c:choose>
      	    <c:when test="${(order.deliveryTypeCd eq DELIVERY_TYPE_PICKUP) || 
      	    				(order.deliveryTypeCd eq DELIVERY_TYPE_SHIP_PICKUP) || 
      	    				(order.deliveryTypeCd eq DELIVERY_TYPE_BLOCKTRAIN_PICKUP)}">
      	    	<fmt:message key="webbestellung.columns.pickup"/>
      	    </c:when>
      	    <c:otherwise>
      	    	<fmt:message key="webbestellung.columns.deliver"/>
      	    </c:otherwise>
      	    </c:choose>
      	    </crh-common:dtOrderWrapperRowspan>
           <crh-common:dtOrderWrapperRowspan pageContextOrderList="<%=(List)pageContext.getAttribute("orderCreateList") %>" pageContextIndexName="i" pageContextOrderCreateEntityName="order" printInTd="style=\"vertical-align:middle;\"">
      	    <c:out value="${order.bpName}"/>
      	    <br />
      	    <c:out value="${order.payerBpName}"/>
      	    </crh-common:dtOrderWrapperRowspan>
              <crh-common:dtOrderWrapperRowspan pageContextOrderList="<%=(List)pageContext.getAttribute("orderCreateList") %>" pageContextIndexName="i" pageContextOrderCreateEntityName="order" printInTd="style=\"vertical-align:middle;\"">
	      	    <c:out value="${order.custWeborderNote}"/>
	      	    <br />
	      	    <c:out value="${order.custorderCustrefNo}"/>
      	    </crh-common:dtOrderWrapperRowspan>
      	              <crh-common:dtOrderWrapperRowspan pageContextOrderList="<%=(List)pageContext.getAttribute("orderCreateList") %>" pageContextIndexName="i" pageContextOrderCreateEntityName="order" printInTd="style=\"vertical-align:middle;\"">
			<c:if test="${order.deletionAllowed}">
      	        <a><img alt="<fmt:message key="webbestellung.tooltip.delete"/>" onclick='webOrderDeleteOrder("<c:out value="${order.orderId}"/>", "<c:out value="${order.orderNumber}"/>");' src="<crh-common:url value='/images/delete2.png'/>" alt="delete" width="16" height="16"></a>
      	    </c:if>
			</crh-common:dtOrderWrapperRowspan>
        </tr>
        </c:if>
		</logic:iterate>
        
	    </tbody>
		</table>	
	</td>
   </tr>
    <tr>
        <td colspan="16" class="right">&nbsp;</td>
    <tr>
</table>
