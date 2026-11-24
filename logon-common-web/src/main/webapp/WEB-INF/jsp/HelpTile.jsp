<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: HelpTile.jsp,v $ --%>

<%@ page session="true" %>

<%@ include file="includeTLDs.jsp" %>

<script type="text/javascript">
Ext.onReady( function() {

	Ext.get('buttonClose').on('click', function() {
		hidePopup();
	} );

} );
</script>

<div id="HelpPopupDiv">
<form action="" styleId="HelpPopupForm">
	
	<!-- begin Border-Table -->
	<table class="big_form2" cellpadding="0" cellspacing="0" border="0"
		style="padding: 0px; margin: 0px; width: auto;">
		<tr><td>
			<table class="classicblue" style="border: none; margin-top: 10px; width: 100%;">
				<tr>
					<th class="classicblue left" style="vertical-align: middle;">
						<fmt:message key="application.help.title.external" />
					</th>
				</tr>
				<tr>
					<td class="label left">
						<fmt:message key="application.help.text.external" />
					</td>
				</tr>
			</table>
		</td></tr>
		<tr><td>
			<table class="classicblue" style="border: none; margin-top: 10px; width: 100%;">
				<tr>
					<th class="classicblue left" style="vertical-align: middle;">
						<fmt:message key="application.help.title.internal" />
					</th>
				</tr>
				<tr>
					<td class="label left">
						<fmt:message key="application.help.text.internal" />
					</td>
				</tr>
			</table>
		</td></tr>
		<tr><td>
			<table style="border: none; margin-top: 10px; width: 100%;">
				<tr><td class="buttons right">
					<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.close" />" id="buttonClose">
				</td></tr>
			</table>
		</td></tr>
	</table>

</form>
</div>

<%-- END $RCSfile: HelpTile.jsp,v $ --%>
