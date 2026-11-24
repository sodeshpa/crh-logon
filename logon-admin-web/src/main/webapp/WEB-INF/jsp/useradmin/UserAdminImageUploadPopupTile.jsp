<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: UserAdminImageUploadPopupTile.jsp,v $ --%>
<%@ page session="true"%>

<%@ include file="../includeTLDs.jsp"%>
<script type="text/javascript">

function upload() {

	var form = Ext.getDom('UserAdminImageUploadPopupForm');
	Ext.query("input[name=inputAction]", form)[0].value = 'upload';
	Ext.Ajax.request({
		url :'UserAdminImageUploadPopup.do',
		form: 'UserAdminImageUploadPopupForm',
		method: 'POST',
		isUpload: true,
		params: { type : '<c:out value="${param.type}"/>' },
        success: function(result, request) {
			hidePopup();
			useradmin.submitWithActionToDiv('imageUpload', 'admin_UserAdminFormUserAdmin', '');
		}
	});}

</script>


<html:form action="/useradmin/UserAdminImageUploadPopup.do" enctype="multipart/form-data" method="post" onsubmit="return false;" styleId="UserAdminImageUploadPopupForm">
<html:hidden property="inputAction" value=""/>
<table class="classicblue" style="border:none; width:auto" id="user-extaccess-pwd-table">
<tr>
	<th colspan="2">
		<fmt:message key="useradmin.popup.imageupload.title"/>
	</th>
</tr>
<tr>
	<td class="popupcontent">
	<table width="100%">
		<tr>
			<td class="left"><nobr><fmt:message
				key="useradmin.field.imagename" /></nobr></td>
			<td><nobr><html:file property="image"/></nobr></td>
		</tr>
			<tr>
			<td colspan="2" class="buttons right">
				<input id="uploadCancel" type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.cancel"/>" onclick="hidePopup();"/>   				
				<input id="uploadSave" type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.upload"/>" onclick="upload()"/>
			</td>
		</tr>
	</table>
	</td>
</table>

</html:form>
