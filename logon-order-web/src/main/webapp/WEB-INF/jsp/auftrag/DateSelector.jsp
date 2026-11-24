<%@ page import="com.crh.logon.order.web.forms.auftrag.AuftragBearbeitungForm" %>
<%@ include file="../includeTLDs.jsp" %>
      <table class="noborder" cellpadding="0" cellspacing="0" border="0" style="margin-top:4px;">
                            <colgroup>
                                <col width="2%">
                                <col width="1%">
                                <col width="48%">
                                <col width="48%">
                                <col width="1%">
                            </colgroup>
                              <tr>
                                <td valign="top" style="white-space:nowrap;border:0px; padding-right: 4px; vertical-align:top;">&nbsp;</td>
                                <td class="formtitle" style="vertical-align:middle;">&nbsp;</td>
                                <td colspan="3" style="vertical-align:middle;"><span class="content" style="vertical-align:middle;">
                                	<a onclick="setSelectedDate(<%=AuftragBearbeitungForm.DATE_TODAY_SELECTION %>)" href="#"><fmt:message key="auftragbearbeitung.texte.heute"/></a> | 
                                	<a onclick="setSelectedDate(<%=AuftragBearbeitungForm.DATE_TOMORROW_SELECTION %>)" href="#"><fmt:message key="auftragbearbeitung.texte.morgen"/></a> | 
                                	<a onclick="setSelectedDate(<%=AuftragBearbeitungForm.DATE_THIS_WEEK_SELECTION%>)" href="#"><fmt:message key="auftragbearbeitung.texte.w"/></a> | 
                                	<a onclick="setSelectedDate(<%=AuftragBearbeitungForm.DATE_THIS_MONTH_SELECTION%>)" href="#"><fmt:message key="auftragbearbeitung.texte.m"/></a> | 
                                	<a onclick="setSelectedDate(<%=AuftragBearbeitungForm.DATE_THIS_YEAR_SELECTION%>)" href="#"><fmt:message key="auftragbearbeitung.texte.YTD"/></a></span></td>
                    </tr>
                              <tr>
                                <td valign="top" style="white-space:nowrap;border:0px; padding-right: 4px; vertical-align:top;"><span class="formtitle"><fmt:message key="auftragbearbeitung.texte.datum"/>:</span></td>
                                <td style="vertical-align:middle;"><a href="#" onclick="setSelectedDate(<%=AuftragBearbeitungForm.DATE_ONE_DAY_BACKWARD_SELECTION%>)">
                                <img src="<crh-common:url value='/images/arrow_left_blue.gif'/>" title="<fmt:message key="auftragbearbeitung.texte.previousday"/>" alt="<fmt:message key="auftragbearbeitung.texte.previousday"/>" width="16" height="16" border="0"></a></td>
                                <td><input onchange="setSelectedDate(<%=AuftragBearbeitungForm.DATE_CUSTOM_SELECTION%>);" name="searchFromDate" id="searchFromDate" property="searchFromDate" value='<c:out value="${order_auftrag_AuftragBearbeitungForm.searchFromDate}"/>' type="text" class="textfield" style="width:100%;" size="4" maxlength="20"></td>
								<td ><input onchange="setSelectedDate(<%=AuftragBearbeitungForm.DATE_CUSTOM_SELECTION%>);" name="searchToDate" id="searchToDate" property="searchToDate" value='<c:out value="${order_auftrag_AuftragBearbeitungForm.searchToDate}"/>' type="text" class="textfield" style="width:100%;" size="4" maxlength="20"></td>
								<td style="vertical-align:middle;" ><a href="#" onclick="setSelectedDate(<%=AuftragBearbeitungForm.DATE_ONE_DAY_FORWARD_SELECTION%>)">
								<img src="<crh-common:url value='/images/arrow_right_blue.gif'/>" title="<fmt:message key="auftragbearbeitung.texte.nextday"/>" alt="<fmt:message key="auftragbearbeitung.texte.nextday"/>" width="16" height="16" border="0"></a></td>
                              </tr>
                    </table>
              