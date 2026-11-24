<%@ include file="../includeTLDs.jsp"%>
<%@ page import="com.crh.logon.constants.Constant"%>
<%@ page import="com.crh.logon.order.web.forms.auftrag.LiveKarteForm"%>

<c:set var="INVALID_OBJECT_ID">
	<%=Constant.INVALID_OBJECT_ID%>
</c:set>

<c:set var="org.apache.struts.taglib.html.BEAN" scope="page"
	value="${order_auftrag_LiveKarteForm}" />

<table class="noborder" cellpadding="0" cellspacing="0" border="0"
	style="margin-top: 4px;">
	<colgroup>
		<col width="2%">
		<col width="1%">
		<col width="40%">
		<col width="42%">
		<col width="1%">
		<col width="15%">
		<col width="11%">
	</colgroup>
	<tr>
		<td valign="top"
			style="white-space: nowrap; border: 0px; padding-right: 4px; vertical-align: top;">&nbsp;</td>
		<td class="formtitle" style="vertical-align: middle;">&nbsp;</td>
		<td colspan="5" style="vertical-align: middle;"><span
			class="content" style="vertical-align: middle;"> <a
			onclick="updateFilters(<%=LiveKarteForm.DATE_TODAY_SELECTION%>)"
			href="#"><fmt:message key="auftragbearbeitung.texte.heute" /></a> |
		<a onclick="updateFilters(<%=LiveKarteForm.DATE_TOMORROW_SELECTION%>)"
			href="#"><fmt:message key="auftragbearbeitung.texte.morgen" /></a> |
		<a
			onclick="updateFilters(<%=LiveKarteForm.DATE_THIS_WEEK_SELECTION%>)"
			href="#"><fmt:message key="auftragbearbeitung.texte.w" /></a> | <a
			onclick="updateFilters(<%=LiveKarteForm.DATE_THIS_MONTH_SELECTION%>)"
			href="#"><fmt:message key="auftragbearbeitung.texte.m" /></a> | <a
			onclick="updateFilters(<%=LiveKarteForm.DATE_THIS_YEAR_SELECTION%>)"
			href="#"><fmt:message key="auftragbearbeitung.texte.YTD" /></a></span></td>
	</tr>

	<tr>
		<td valign="top"
			style="white-space: nowrap; border: 0px; padding-right: 4px; vertical-align: top;"><span
			class="formtitle"><fmt:message
			key="auftragbearbeitung.texte.datum" />:</span></td>
		<td style="vertical-align: middle;"><a href="#"
			onclick="updateFilters(<%=LiveKarteForm.DATE_ONE_DAY_BACKWARD_SELECTION%>)"><img
			src="<crh-common:url value='/images/arrow_left_blue.gif'/>" alt="vortag" width="16"
			height="16" border="0"></a></td>
		<td><html:text style="width: 100%;" styleId="searchFromDateId"
			property="searchFromDate" size="4" maxlength="20"
			onchange="updateFilters(${INVALID_OBJECT_ID})" /></td>
		<td><html:text style="width: 100%;" styleId="searchToDateId"
			property="searchToDate" size="4" maxlength="20"
			onchange="updateFilters(${INVALID_OBJECT_ID})" /></td>
		<td style="vertical-align: middle;"><a href="#"
			onclick="updateFilters(<%=LiveKarteForm.DATE_ONE_DAY_FORWARD_SELECTION%>)"><img
			src="<crh-common:url value='/images/arrow_right_blue.gif'/>" alt="n&auml;chster Tag"
			width="16" height="16" border="0"></a>
		<td align="right"><span class="formtitle"
			style="white-space: nowrap;"><fmt:message key="livemap.label.refresh" />:</span></td>
		<td>
		<html:checkbox styleId="refreshObjectsId"
			property="refreshFlag" 
			onclick="reloadMapObjects();" style="border: 0px none; margin: 0px; padding: 0px;" />
		 </td>
	</tr>
	</td>
	</tr>
</table>


