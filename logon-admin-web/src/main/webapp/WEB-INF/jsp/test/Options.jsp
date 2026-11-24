<%@ page session="true" %>

<%@ include file="../includeTLDs.jsp" %>

<c:set var="wsform" value="${sessionScope.admin_ManualWebserviceTriggerForm}"/>

<table class="classicblue" style="width: 100%;">
	<tr>
		<td style="width: 50%;">
			<h2>Request</h2>
			<html:textarea styleId="webServiceOptions.request" name="admin_ManualWebserviceTriggerForm" property="webServiceOptions.request" styleClass="textfield" rows="50">
			</html:textarea>
		</td>
		<td>
			<h2 style="display: inline;">Response</h2></h1><span style="font-size: x-small;">&nbsp;(note: SAP services do not send a response)</span>
			<html:textarea styleId="webServiceOptions.response" name="admin_ManualWebserviceTriggerForm" property="webServiceOptions.response" styleClass="textfield" rows="50">
			</html:textarea>
		</td>
	</tr>
</table>
<script type="text/javascript">
	var field = document.getElementById('webServiceOptions.request');
	if (field) {
		field.setAttribute('spellcheck', false);
	}
	field = document.getElementById('webServiceOptions.response');
	if (field) {
		field.setAttribute('spellcheck', false);
	}
</script>
