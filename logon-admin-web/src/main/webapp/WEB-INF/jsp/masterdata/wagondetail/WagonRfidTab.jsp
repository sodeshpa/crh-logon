<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@ page import="com.crh.logon.web.forms.RFIDPopupForm" %>
<%@page import="com.crh.logon.admin.web.actions.masterdata.WagonDetailAction"%>
<jsp:include page="TabBase.jsp"></jsp:include>
<html:hidden property="activeTab" value="<%=WagonDetailAction.FORWARD_RFID_TAB%>"/>

                          <div class="subformlist" id="<%=RFIDPopupForm.NAME_OF_HTML_DIV_TO_RELOAD%>" style="clear:both;font-size:12px; margin:0px 4px 0px 4px;height:90%">
<jsp:include page="../../AdminRfidsTile.jsp"/>    
                          </div>
                          <div align="right">
                            <input onclick="javascript:showPopup('AdminRFIDPopup.do');" type="button" class="SmallButtonStyle" value=" <fmt:message key="buttons.add"/> ">
                          </div>
