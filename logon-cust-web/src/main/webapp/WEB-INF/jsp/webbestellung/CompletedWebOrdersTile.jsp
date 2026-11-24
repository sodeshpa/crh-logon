<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: CompletedWebOrdersTile.jsp,v $ --%>
<%@ include file="../includeTLDs.jsp" %>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page import="com.crh.logon.cust.web.forms.webbestellung.WebBestellungForm"%>
<%@page import="java.util.List"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>

<c:set var="ORDER_STATUS_DELETED"><%=Constant.ORDER_STATUS_DELETED%></c:set>
<c:set var="isDeletedOrderVisible">false</c:set>
<crh-common:authorize function="<%= Authorization.WEB_ORDER_VIEW_DELETED_ORDERS %>">
<c:set var="isDeletedOrderVisible">true</c:set>
</crh-common:authorize>

<!-- Beginn Abgeschlossene Bestellungen -->
<table class="big_form2" cellpadding="0" cellspacing="0" style="padding:0px; margin:0px; border-collapse:collapse;">
	<tr style="border-bottom: 2px solid #599ade;">
      <td class="labelblue_bg"><span class="labelbluebig"><fmt:message key="webbestellung.columns.completedorders"/></span></td>
    </tr>
    <tr>
      <td>
            <table class="classicblue" style="width:100%; margin-top:4px;">
<%-- %><colgroup>
            <col width="1%">
            <col width="12%">
            <col width="15%">
            <col width="2%">
            <col width="4%">
            <col width="10%">
            <col width="10%">
            <col width="12%">
            <col width="10%">
            <col width="10%">
            <col width="18%">
		</colgroup> --%>
		
<!------------------------------->
<!--------- Table header --------> 
<!------------------------------->                          
        <tr>        		
                <th rowspan="2"><fmt:message key="fields.order.number"/></th> 
                <th rowspan="2"><fmt:message key="fields.produkt.produkt"/></th>
                <th rowspan="2">#</th> 
                <th rowspan="2"><fmt:message key="fields.uom.uom"/></th> 
                <th colspan="2">
					<fmt:message key="fields.order.delivery.time"/>
				</th> 
      	    	<th rowspan="2"><fmt:message key="webbestellung.arrive"/></th>
      	    	<th rowspan="2"><fmt:message key="fields.type"/></th>
      	    	<th><fmt:message key="dispokarte.texte.rechnungsemp"/></th>
      	    	<th><fmt:message key="fields.remarks"/></th>
        </tr>
        
        <tr>
	        <td class="cell_gray"><fmt:message key="fields.time.from" /></td>
			<td class="cell_gray"><fmt:message key="fields.time.until" /></td>
			<td class="cell_gray"><fmt:message key="dispokarte.texte.rechnungsgeber" /></td>
			<td class="cell_gray"><fmt:message key="fields.customerreferencenumber.short" /></td>
		</tr>

<!------------------------------------>
<!--------- Content of table --------->
<!--------- Completed orders-- ---------> 
<!------------------------------------>
		<c:set var="index" value="0"/>
		<% pageContext.setAttribute("orderCreateList", ((WebBestellungForm)session.getAttribute("cust_WebBestellungForm")).getCompletedOrderList());%>
		<logic:iterate id="order" name="orderCreateList"  indexId="i">
		
		<%-- Deleted orders are not visible for a certain group of users --%>
		<c:if test="${order.orderStatusCd ne ORDER_STATUS_DELETED || isDeletedOrderVisible }">
		
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
          <td class="label">
			<nobr><c:out value="${order.materialTxt}"/></nobr><br>(<c:out value="${order.materialNo}"/>)
          </td>
            <td style="vertical-align:middle;"><c:out value="${order.orderedQuantity}"/></td>
            <td style="vertical-align:middle;"><nobr><c:out value="${order.unitTxt}"/></nobr></td>
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
      	    			(<c:out value="${order.unloadPlannedStartDate}"/>
      	    			<c:out value="${order.unloadPlannedStartTime}"/>)
      	    		</c:when>
      	    		<c:otherwise>
      	    			-
      	    		</c:otherwise>
      	    	</c:choose>
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
        </tr>
        </c:if>
        
		</logic:iterate>
		
	</table>	
		</td>
		</tr>
		</table>
		
