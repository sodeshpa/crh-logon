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

<table cellspacing="0" cellpadding="0" border="0" width="100%" style="border: 0px none;">
	<tbody>
		<tr>
			<td style="padding-left:0px; padding-right: 0px;">
				<div class="textfielddisabled_h" style="float: left; width: 250px;" id="payerStreet"
					title="<c:out value="${order_auftrag_OrderDetailForm.materialBean.payerStreet}" />">
					<c:out value="${order_auftrag_OrderDetailForm.materialBean.payerStreet}" />&nbsp;
				</div>
				<div class="textfielddisabled_h" style="float: right; margin-left: 4px; width: 35px;" 
					title="<c:out value="${order_auftrag_OrderDetailForm.materialBean.payerHouseNumber}" />">
					<c:out value="${order_auftrag_OrderDetailForm.materialBean.payerHouseNumber}"/>&nbsp;
				</div>
			</td>
		</tr>
		<tr>
			<td style="padding-left: 0px; padding-right: 0px;">
				<div class="textfielddisabled_h" style="float: left; width: 35px;" id="payerCountry"
					title="<c:out value="${order_auftrag_OrderDetailForm.materialBean.payerCountry}" />">
					<c:out value="${order_auftrag_OrderDetailForm.materialBean.payerCountry}" />&nbsp;
				</div>
				<div class="textfielddisabled_h" style="float: left; margin-left: 4px; width: 50px;" id="payerPostalCode"
					title="<c:out value="${order_auftrag_OrderDetailForm.materialBean.payerPostalCode}" />">
					<c:out value="${order_auftrag_OrderDetailForm.materialBean.payerPostalCode}" />&nbsp;
				</div>
				<div class="textfielddisabled_h" style="float: right; margin-left: 4px; width: 195px;" id="payerCity"
					title="<c:out value="${order_auftrag_OrderDetailForm.materialBean.payerCity}" />">
					<c:out value="${order_auftrag_OrderDetailForm.materialBean.payerCity}" />&nbsp;
				</div>
			</td>
		</tr>
	</tbody>
</table>

<html:hidden property="materialBean.payerSapId" styleId="payerSapId" />
