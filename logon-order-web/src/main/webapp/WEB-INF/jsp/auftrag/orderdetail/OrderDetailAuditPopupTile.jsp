<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>
<%@ include file="../../includeTLDs.jsp"%>
<%@ taglib uri="http://com.crh.logon/tags/order-taglib.tld" prefix="crh-order" %>
<%@page import="com.crh.logon.order.taglib.BizeventAuditValuePrintTag"%>

<form id="CancelForm" action="#">
<table class="classicblue" style="border: medium none; width: 1000px;">
<tbody>
	<tr>
		<th><fmt:message key="orderdetail.audit.popup.title"/> - <c:out value="${order_auftrag_OrderDetailForm.order.orderNumber}" /></th>
	</tr>
	<tr>
	<td class="popupcontent">
		<table class="big_form2" style="width: 100%">
			<thead>
			<tr>
                                <th>
                                	<fmt:message key="orderdetail.audit.popup.eventTs"/>
                                </th>
                                <th>
									<fmt:message key="orderdetail.audit.popup.initiator"/>
                                </th>                                                                                            
                                <th>
									<fmt:message key="orderdetail.audit.popup.action"/>
                                </th>                              
                                <th>
									<fmt:message key="orderdetail.audit.popup.oldValue"/>
                                </th>
                                <th>
                                	<fmt:message key="orderdetail.audit.popup.newValue"/>
                                </th>
			</tr>
			</thead>
                            <tbody>
	<c:set var="zebracount" value="0"/>
	<%int i = 0; %>
	<c:forEach var="entry" varStatus="status" items="${bizeventAuditList}">
	<c:if test="${entry.oldValueDisplay ne entry.newValueDisplay}">
		<c:set var="zebracount" value="${(zebracount + 1)%2}"/>	
                              <tr class="zebra<c:out value="${zebracount}" />" >
                                <td>
                                    <c:out value="${entry.eventTime}"/>
                                </td>
                                <td>
									<c:out value="${entry.entity.initiator}"/>
                                </td>                                                                                            
                                <td>
									<c:out value="${entry.entity.txt}"/>
                                </td>                     
	<td>           
								<crh-order:bizeventAuditValuePrint fieldName="<%=BizeventAuditValuePrintTag.OLD_VALUE %>" itemIndex="<%=i %>"/>
	</td>
	<td>
								<crh-order:bizeventAuditValuePrint fieldName="<%=BizeventAuditValuePrintTag.NEW_VALUE %>" itemIndex="<%=i %>"/>
	</td>								
                              </tr>	
	</c:if>
	<%i++; %>
	</c:forEach>                              
                            </tbody>
                          </table>
                          </td>
</tr>
<tr>
<td class="buttons right">
<input type="button" id="inputDialogCancel" value="<fmt:message key="buttons.cancel"/>" class="ViewButtonStyle"/>
</td>
</tr>
</tbody>
</table>
</form>                             