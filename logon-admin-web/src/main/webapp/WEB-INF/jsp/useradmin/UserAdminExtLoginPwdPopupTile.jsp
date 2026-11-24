<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: UserAdminExtLoginPwdPopupTile.jsp,v $ --%>
<%@ page session="true"%>

<%@ include file="../includeTLDs.jsp"%>
<%@page import="com.crh.logon.admin.web.forms.useradmin.UserAdminExtLoginPwdPopupForm"%>
<%@page import="com.crh.logon.web.beans.PwdPolicyBean"%>
<c:set var="FORMTYPE_EXTPWD"><%=UserAdminExtLoginPwdPopupForm.FORMTYPE_EXTPWD %></c:set>
<c:set var="FORMTYPE_PDAPIN"><%=UserAdminExtLoginPwdPopupForm.FORMTYPE_PDAPIN %></c:set>

<script type="text/javascript">

	function saveExtLoginPwd() {

		a = {
			"save" :"save"
		};
		addFormElements(a, "UserAdminExtLoginPwdPopupForm");

		showPopup('UserAdminExtLoginPwdPopup.do',
				'<fmt:message key="shippingpoint.text.exjsloading"/>', a);
	}

	onPopupReady(function() {

		var el = document.getElementById('extLoginPwd');
		if (el) {
			el.focus();
		}
	});
	
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
<script type="text/javascript" src="<crh-common:url value="/js/logon-password.js"/>"></script>


<html:form action="/useradmin/UserAdminExtLoginPwdPopup.do" onsubmit="return false;" styleId="UserAdminExtLoginPwdPopupForm">
<table class="classicblue" style="border:none; width:auto" id="user-extaccess-pwd-table">
<tr>
	<th>
		<c:if test="${admin_UserAdminExtLoginPwdPopupForm.formType == FORMTYPE_EXTPWD}">
			<fmt:message key="useradmin.popup.extpwd.title"/>
		</c:if>
		<c:if test="${admin_UserAdminExtLoginPwdPopupForm.formType == FORMTYPE_PDAPIN}">
			<fmt:message key="useradmin.popup.pdapin.title"/>
		</c:if>
	</th>
</tr>
<tr>
	<td class="popupcontent">
	<table style="width: 240px">
		<c:if test="${admin_UserAdminExtLoginPwdPopupForm.formType == FORMTYPE_EXTPWD}">
			<c:if test="${admin_UserAdminForm.userEntity.extLoginPwdLockedTs != null}">
				<tr>
					<td colspan="3">
						<c:if test="${not admin_UserAdminExtLoginPwdPopupForm.self}">
							<div class="warn">
								<fmt:message key="application.ext.login.pwd.locked.admin" />
							</div>
						</c:if>
						<c:if test="${admin_UserAdminExtLoginPwdPopupForm.self}">
							<div class="error">
								<fmt:message key="application.ext.login.pwd.locked.self" />
							</div>
						</c:if>
					</td>
				</tr>
			</c:if>
		</c:if>
		<c:if test="${admin_UserAdminExtLoginPwdPopupForm.self}">
			<tr>
				<td class="left" style="vertical-align: middle">
					<nobr>
						<c:if test="${admin_UserAdminExtLoginPwdPopupForm.formType == FORMTYPE_EXTPWD}">
							<fmt:message key="useradmin.field.pwd.old" />
						</c:if>
						<c:if test="${admin_UserAdminExtLoginPwdPopupForm.formType == FORMTYPE_PDAPIN}">
							<fmt:message key="useradmin.field.pdaPin.old"/>
						</c:if>
					</nobr>
				</td>
				<td colspan="2">
					<nobr><input id="extLoginPwdOld" name="extLoginPwdOld" type="password"/></nobr>
				</td>
			</tr>
		</c:if>
		<tr>
			<td class="left" style="vertical-align: middle">
				<nobr>
					<c:if test="${admin_UserAdminExtLoginPwdPopupForm.formType == FORMTYPE_EXTPWD}">
						<fmt:message key="useradmin.field.pwd.new" />
					</c:if>
					<c:if test="${admin_UserAdminExtLoginPwdPopupForm.formType == FORMTYPE_PDAPIN}">
						<fmt:message key="useradmin.field.pdaPin"/>
					</c:if>
				</nobr>
			</td>
			<td colspan="2">
				<nobr><input id="extLoginPwdNew" name="extLoginPwdNew" type="password"/></nobr>
			</td>
		</tr>
		<tr>
			<td class="left" style="vertical-align: middle">
				<nobr>
					<c:if test="${admin_UserAdminExtLoginPwdPopupForm.formType == FORMTYPE_EXTPWD}">
						<fmt:message key="useradmin.field.pwd.new" />
					</c:if>
					<c:if test="${admin_UserAdminExtLoginPwdPopupForm.formType == FORMTYPE_PDAPIN}">
						<fmt:message key="useradmin.field.pdaPin"/>
					</c:if>
				</nobr>
			</td>
			<td colspan="2">
				<nobr><input id="extLoginPwdNew2" name="extLoginPwdNew2" type="password"/></nobr>
			</td>
		</tr>
		<tr>
			<td class="left buttons">
				<c:if test="${admin_UserAdminExtLoginPwdPopupForm.formType == FORMTYPE_EXTPWD}">
					<c:choose>
						<c:when test="${admin_UserAdminExtLoginPwdPopupForm.extLoginPwdPolicyVisible}">
							<input id="pwdPolicyButton" type="button" class="SmallButtonStyleUp" value="<fmt:message key="useradmin.popup.extpwd.button.pwdpolicy"/>" onclick="togglePasswordPolicy();"/>
						</c:when>
						<c:otherwise>
							<input id="pwdPolicyButton" type="button" class="SmallButtonStyleDown" value="<fmt:message key="useradmin.popup.extpwd.button.pwdpolicy"/>" onclick="togglePasswordPolicy();"/>
						</c:otherwise>
					</c:choose>
				</c:if>
			</td>
			<td colspan="2" class="buttons">
				<input id="pwdCancel" type="button" style="float: left" class="ViewButtonStyle" value="<fmt:message key="buttons.cancel"/>" onclick="hidePopup(); useradmin.submitWithActionToDiv('cancelExternalAccess','admin_userAdminFormUserAdmin');"/>   				
				<input id="pwdSave" type="button" style="float: right" class="ViewButtonStyle" value="<fmt:message key="buttons.ok"/>" onclick="saveExtLoginPwd()"/>
			</td>
		</tr>
		<c:if test="${admin_UserAdminExtLoginPwdPopupForm.formType == FORMTYPE_EXTPWD}">
			<tr>
				<td colspan="3">
					<c:choose>
						<c:when test="${admin_UserAdminExtLoginPwdPopupForm.extLoginPwdPolicyVisible}">
							<div id="pwdPolicyInfo" class="box PwdPolicyInfoVisible">
						</c:when>
						<c:otherwise>
							<div id="pwdPolicyInfo" class="box PwdPolicyInfoHidden">
						</c:otherwise>
					</c:choose>
					<table>
						<c:forEach var="extPwdPolicy" items="${admin_UserAdminExtLoginPwdPopupForm.extLoginPwdPolicies}">
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
											<fmt:param value="${admin_UserAdminExtLoginPwdPopupForm.specialSetOfChars}"/>
										</c:if>
									</fmt:message>
								</td>
							</tr>
						</c:forEach>
					</table>
					</div>
				</td>
			</tr>
		</c:if>
	</table>
	</td>
</tr>
</table>

</html:form>
