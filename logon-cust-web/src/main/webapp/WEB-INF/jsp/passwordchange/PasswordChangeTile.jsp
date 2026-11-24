<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: PasswordChangeTile.jsp,v $ --%>
<%@ page session="true"%>


<%@ include file="../includeTLDs.jsp"%>
<%@page import="com.crh.logon.web.beans.PwdPolicyBean"%>
<link rel="stylesheet" type="text/css" href="<crh-common:url value="/css/crh_style.css"/>">
<script type="text/javascript" src="<crh-common:url value="/js/logon-password.js"/>"></script>

<script type="text/javascript">
	function togglePasswordPolicy() {
		var pwdPolicyButton = document.getElementById('pwdPolicyButton');
		if (pwdPolicyButton) {
			var buttonClass = pwdPolicyButton.className;
			if (buttonClass == 'SmallButtonStyleDown') {
				showPasswordPolicy(true);
			}
			else {
				showPasswordPolicy(false);
			}
		}
	}
	
	function showPasswordPolicy(isVisible) {
		var pwdPolicyButton = document.getElementById('pwdPolicyButton');
		var pwdPolicyInfo = document.getElementById('pwdPolicyInfo');
		
		if (isVisible == true) {
			if (pwdPolicyButton) {
				pwdPolicyButton.className = 'SmallButtonStyleUp';
			}
			if (pwdPolicyInfo) {
				pwdPolicyInfo.className = 'box PwdPolicyInfoVisible';
			}
		}
		else {		
			if (pwdPolicyButton) {
				pwdPolicyButton.className = 'SmallButtonStyleDown';
			}
			if (pwdPolicyInfo) {
				pwdPolicyInfo.className = 'box PwdPolicyInfoHidden';
			}
		}
	}
</script>

<html:form action="passwordchange/PasswordChange.do" styleId="cust_PasswordChangeForm">

<div class="customer_max">
<table class="classicblue" cellpadding="0" cellspacing="0" border="0" align="center">
      <tr>	  
          <th><fmt:message key="passwordchange.title"/></th>
      </tr>
      <tr>
      <td class="popupcontent">         
           
           <!-- Beginn Table UserID... -->
           <table cellpadding="0" cellspacing="4" border="0" style="margin-top:10px;">
              <tbody>
                <tr>
                  <td valign="bottom" style="padding-left:0px;"><p class="formtitle"><fmt:message key="passwordchange.field.userid"/></p></td>
                  <td width="100%" style="padding:0px; margin:0px; width:100%; text-align:left;"><c:out value="${cust_PasswordChangeForm.extLogin}" /></td>
                </tr>
                <tr>
                  <td valign="bottom" style="padding-left:0px; white-space:nowrap;">
                  	<p class="formtitle">
                  		<fmt:message key="passwordchange.field.oldpwd"/>
                  	</p>
                  </td>
                  <td width="100%" style="padding:0px; margin:0px; width:100%; text-align:right;">
                  	<html:password size="50" maxlength="100" property="extLoginPwdOld" styleId="extLoginPwdOld"/>
                  </td>
                </tr>
                <tr>
                  <td valign="bottom" style="padding-left:0px; white-space:nowrap;">
                  	<p class="formtitle">
	            	  <fmt:message key="passwordchange.field.newpwd"/>
                  	</p>
                  </td>
                  <td width="100%" style="padding:0px; margin:0px; width:100%; text-align:right;"><html:password size="50" maxlength="100" property="extLoginPwd" styleId="extLoginPwd"/></td>
                </tr>
                <tr>
                  <td valign="bottom" style="padding-left:0px; white-space:nowrap;">
                  	<p class="formtitle">
                  	  <fmt:message key="passwordchange.field.repeatenewpwd"/>
                  	</p>
                  </td>
                  <td width="100%" style="padding:0px; margin:0px; width:100%; text-align:right;"><html:password size="50" maxlength="100" property="extLoginPwd2" styleId="extLoginPwd2"/></td>
                </tr>
                <tr>
                  <td class="left buttons">
					<c:choose>
						<c:when test="${cust_PasswordChangeForm.extLoginPwdPolicyVisible}">
							<input id="pwdPolicyButton" type="button" class="SmallButtonStyleUp" value="<fmt:message key="useradmin.popup.extpwd.button.pwdpolicy"/>" onclick="togglePasswordPolicy();"/>
						</c:when>
						<c:otherwise>
							<input id="pwdPolicyButton" type="button" class="SmallButtonStyleDown" value="<fmt:message key="useradmin.popup.extpwd.button.pwdpolicy"/>" onclick="togglePasswordPolicy();"/>
						</c:otherwise>
					</c:choose>
				  </td>
                  <td class="right buttons">
                  	<input type="submit" id="savePwdChange" name="save" class="ViewButtonStyle" style="margin-bottom:4px;" value="<fmt:message key="buttons.change"/>">
                  </td>
                </tr>
				<tr>
					<td colspan="2">
						<c:choose>
							<c:when test="${cust_PasswordChangeForm.extLoginPwdPolicyVisible}">
								<div id="pwdPolicyInfo" class="box PwdPolicyInfoVisible">
							</c:when>
							<c:otherwise>
								<div id="pwdPolicyInfo" class="box PwdPolicyInfoHidden">
							</c:otherwise>
						</c:choose>
							<table>
								<c:forEach var="extPwdPolicy" items="${cust_PasswordChangeForm.extLoginPwdPolicies}">
									<tr>
										<td class="left">
											<c:choose>
												<c:when test="${extPwdPolicy.valid == null}">
													<img class="ImgRightSpace" width="14" height="14" alt="info" src="<crh-common:url value='/images/information.png'/>"/>
												</c:when>
												<c:when test="${extPwdPolicy.valid == true}">
													<img class="ImgRightSpace" width="14" height="14" alt="ok" src="<crh-common:url value='/images/active.png'/>"/>
												</c:when>
												<c:otherwise>
													<img class="ImgRightSpace" width="14" height="14" alt="nok" src="<crh-common:url value='/images/delete.gif'/>"/>
												</c:otherwise>
											</c:choose>
										</td>
										<td class="left">
											<c:set var="minPolicyKey"><%=PwdPolicyBean.EXT_PWD_POLICY_MIN_SPECIAL %></c:set>
											<fmt:message key="${extPwdPolicy.key}">
												<fmt:param value="${extPwdPolicy.expected}"/>
												<fmt:param value="${extPwdPolicy.actual}"/>
												<c:if test="${extPwdPolicy.key == minPolicyKey}">
													<fmt:param value="${cust_PasswordChangeForm.specialSetOfChars}"/>
												</c:if>
											</fmt:message>
										</td>
									</tr>
								</c:forEach>
							</table>
						</div>
					</td>
				</tr>
              </tbody>
            </table>
            <!-- End Table UserID... -->
            
        </td>
    	</tr>
	</table>
</div>
</html:form>
<script type="text/javascript">
	document.getElementById("cust_PasswordChangeForm").setAttribute("autocomplete", "off");
	//document.getElementById("cust_PasswordChangeForm").autocomplete = "off";
	//alert(document.getElementById("cust_PasswordChangeForm").autocomplete);
</script>
