<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.admin.web.actions.masterdata.WagonDetailAction"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@ page import="com.crh.logon.constants.Constant"%>
<%@ page import="java.text.DecimalFormat" %>
<%
DecimalFormat df = new DecimalFormat( "'+'0.0;'-'0.0" );
df.setMaximumFractionDigits(2);
DecimalFormat df2 = new DecimalFormat( "0.0" );
df2.setMaximumFractionDigits(2);

%>
<%@page import="com.crh.logon.persistence.entities.common.WagonEntity"%><c:set var="org.apache.struts.taglib.html.BEAN" scope="page" value="${admin_WagonDetailForm}" />
<%@ page import="com.crh.logon.persistence.entities.views.WagonDetailLoadListEntity" %>
<%@ page import="com.crh.logon.admin.web.forms.masterdata.WagonDetailForm" %>
<%@ page import="com.crh.logon.admin.web.beans.WagonDetailMainBean" %>
<jsp:include page="TabBase.jsp"></jsp:include>
<html:hidden property="activeTab" value="<%=WagonDetailAction.FORWARD_LOAD_TAB%>"/>
<c:set var="readonly" value="${true}"/>
<crh-common:authorize function="<%= Authorization.RAILCAR_DETAIL_BASE_W %>">
	<c:set var="readonly" value="${false}"/>
</crh-common:authorize>

            <c:set var="datePattern" value="dd/MM/yyyy" scope="request"/> 
			<c:set var="timePattern" value="HH:mm" scope="request"/> 
		
	<%-- -------------------------------------------------------------------------------------------------------------%>
	<%-- -----------------------------------------------LOAD TABLE----------------- -----------------------------------%>
	<%-- -------------------------------------------------------------------------------------------------------------%>
	<table style="height:100%;width:100%">
	<tr>
	<td>
		<table class="classicblue" style="width:100%">
	<thead>
        <tr>
            <th><fmt:message key="wagondetail.loadtab.product"/></th>
            <th><fmt:message key="wagondetail.loadtab.plant"/></th>
            <th>&#916;&nbsp;<fmt:message key="wagondetail.loadtab.tons.brackets"/></th> 
            <th><fmt:message key="wagondetail.loadtab.tarameasure"/>&nbsp;<fmt:message key="wagondetail.loadtab.tons.brackets"/></th> 
            <th><fmt:message key="wagondetail.loadtab.tara"/>&nbsp;<fmt:message key="wagondetail.loadtab.tons.brackets"/></th>  
            <th><fmt:message key="wagondetail.loadtab.nettoweight"/></th>
            <th><fmt:message key="wagondetail.loadtab.bruttomeasure"/>&nbsp;<fmt:message key="wagondetail.loadtab.tons.brackets"/></th>
            <th><fmt:message key="wagondetail.loadtab.loadedat"/></th>
            <th><fmt:message key="wagondetail.loadtab.container"/></th>
            <th><fmt:message key="wagondetail.loadtab.quantity"/></th> 
        	<th><fmt:message key="wagondetail.loadtab.orderno"/></th>
		</tr>
        </thead>
        <tbody>
	<c:set var="zebracount" value="0"/>
	<c:if test="${!empty admin_WagonDetailForm.wagonDetailMain.loadList }">
	<c:forEach var="order" items="${admin_WagonDetailForm.wagonDetailMain.loadList}"> 
			<c:set var="zebracount" value="${(zebracount + 1)%2}"/>
		<tr class="zebra<c:out value="${zebracount}" />">
		    <td><c:out value="${order.materialTxt}"/></td>
		    <td>
				<c:out value="${order.plantName}"/>
            </td>
            <td>
			<c:set var="delta" value="${order.delta}"/> 
			<c:if test="${!empty delta }">
            	<%=df.format(pageContext.getAttribute("delta"))%>
            </c:if>
            </td>
			<td> 
	            <c:out value="${order.wsOriginalTareMeasureT}"/>
            </td>
			<td> 
	            <c:out value="${order.tareQuantity}"/>
            </td>            
			<td>
            <logic:present name="order" property="netQuantity">
				<c:out value="${order.netQuantity}"/> <c:out value="${order.loadUnitTxt}"/>
            </logic:present>
            </td>
            <td>
			<c:set var="gross" value="${order.grossWeightT}"/> 
			<c:if test="${!empty gross }">
            	<%=df2.format(pageContext.getAttribute("gross"))%>
            </c:if>         
            </td>
			<td>
				<%
					// OK, this is an ugly hack, but there is no simple way to inject ourselves
					// into this call and we need to format the data properly using the user's
					// current time.
					WagonDetailForm form = (WagonDetailForm) session.getAttribute("admin_WagonDetailForm");
					WagonDetailMainBean mainBean = form.getWagonDetailMain();

					WagonDetailLoadListEntity order = (WagonDetailLoadListEntity)pageContext.getAttribute("order");
					String loadingTimestamp = mainBean.getTimeZone().fmtAsDayMonthYearHourMinute(order.getLoadingTs());
					out.println(loadingTimestamp);
				%>
			</td>
            <td>
            	<c:out value="${order.containerName}"/>
            </td>
           <td>
           <c:if test="${!empty order.orderedQuantity}">
           <c:out value="${order.orderedQuantity}"/>&nbsp;<c:out value="${order.orderedUnitTxt}"/>  
           (<c:out value="${order.orderedMeasure}"/>&nbsp;<fmt:message key="wagondetail.loadtab.tons"/>)
           </c:if>
	         </td>
			<td>
			<c:if test="${!empty order.orderId}">
			<%-- This is only shown because of bahnwagen freimeldung, who does not have the right for this, he must not see as a link--%>
        	<crh-common:authorize function="<%= Authorization.RAILCARS_FREIMELDEN %>">
        		<a href="javascript:showPopup('<c:url value="../../logon/order/auftrag/OrderDetailEntry.do"/>?orderid=<c:out value="${order.orderId}"/>')">
			</crh-common:authorize>
        		<c:out value="${order.orderNumberWithItemNo}"/>
			<crh-common:authorize function="<%= Authorization.RAILCARS_FREIMELDEN %>">
        		</a>
			</crh-common:authorize>
			</c:if>
			</td>
		</tr>
	</c:forEach>
	</c:if>
	</tbody>
	</table>
	</td>
	</tr>
</table>
				
