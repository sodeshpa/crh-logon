<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: FooterTile.jsp,v $ --%>

<%@ page session="true" %>

<%@ include file="includeTLDs.jsp" %>
<c:if test="false">
	<%-- for preview in MyEclipse --%>
	<link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>

<!-- FOOTER -->
<logic:present name="DISPLAY_SNOOP_BUTTON">
<table>
	<tr>
		<td>
			<html:form action="main/ToggleSnoopDisplay.do">
				<html:submit style="font-family:geneva,arial,helvetica,sans-serif; font-size:14px; color:#000; background-color:#DDD; border:1px solid #FDD; padding:0px 0px;">Toggle Snoop</html:submit>
			</html:form>
		</td>
	</tr>
</table>
</logic:present>

<logic:present name="SNOOP_FLAG" scope="session">
	<espace:snoop scopes="all" format="table" tableClass="classicblue" headerClass="labelblue" textClass="zebra1" />
</logic:present>

<%-- END $RCSfile: FooterTile.jsp,v $ --%>
