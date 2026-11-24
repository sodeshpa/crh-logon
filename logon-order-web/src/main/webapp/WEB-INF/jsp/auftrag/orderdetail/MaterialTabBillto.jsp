<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.constants.Constant"%>

<c:set var="org.apache.struts.taglib.html.BEAN" scope="page"
	value="${order_auftrag_OrderDetailForm}" />

<table cellspacing="0" cellpadding="0" border="0" width="100%"
	style="border: 0px none;">
	<tbody>
		<tr>
			<td style="padding-left:0px; padding-right: 0px;">
			<div class="textfielddisabled_h" style="float: left; width: 250px;" id="billtoStreet"
				title="<c:out value="${order_auftrag_OrderDetailForm.materialBean.billtoStreet}" />">
				<c:out value="${order_auftrag_OrderDetailForm.materialBean.billtoStreet}" />&nbsp;
			</div>
			<div class="textfielddisabled_h" style="float: right; margin-left: 4px; width: 35px;"
				title="<c:out value="${order_auftrag_OrderDetailForm.materialBean.billtoHouseNumber}" />">
				<c:out value="${order_auftrag_OrderDetailForm.materialBean.billtoHouseNumber}" />&nbsp;
			</div>
			</td>
		</tr>
		<tr>
			<td style="padding-left:0px; padding-right: 0px;">
			<div class="textfielddisabled_h" style="float: left; width: 35px;" id="billtoCountry"
				title="<c:out value="${order_auftrag_OrderDetailForm.materialBean.billtoCountry}" />">
				<c:out value="${order_auftrag_OrderDetailForm.materialBean.billtoCountry}" />&nbsp;
			</div>
			<div class="textfielddisabled_h" style="float: left; margin-left: 4px; width: 50px;" id="billtoPostalCode"
				title="<c:out value="${order_auftrag_OrderDetailForm.materialBean.billtoPostalCode}" />">
				<c:out value="${order_auftrag_OrderDetailForm.materialBean.billtoPostalCode}" />&nbsp;</div>
			<div class="textfielddisabled_h" style="float: right; margin-left: 4px; width: 195px;" id="billtoCity"
				title="<c:out value="${order_auftrag_OrderDetailForm.materialBean.billtoCity}" />">
				<c:out value="${order_auftrag_OrderDetailForm.materialBean.billtoCity}" />&nbsp;
			</div>
			</td>
		</tr>
	</tbody>
</table> 

<html:hidden property="materialBean.billtoSapId" styleId="billtoSapId" />
