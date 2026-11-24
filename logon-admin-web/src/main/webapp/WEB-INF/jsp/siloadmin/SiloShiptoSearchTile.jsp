<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: SiloShiptoSearchTile.jsp,v $ --%>
<%@ page session="true" %>
<%@ include file="../includeTLDs.jsp" %>
<c:if test="false">
    <%-- for preview in MyEclipse --%>
    <link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>
<script type="text/javascript">
onPopupReady(function() {
	
	var el = document.getElementById('search');
	if (el) {
		el.focus();
	}
});
        
Ext.onReady(function(){

	initSiloShiptoSearch();
});

</script>
<!--  <%-- Struts needs this, fond no better way to do it --%> <html:form action="siloadmin/SiloAdmin.do" styleId="admin_SiloAdminForm">-->

<table>

  <colgroup>
  <col width="10%">
  <col width="70%">
  <col width="20%">
  </colgroup>
  	<tr>
		<td class='label'><nobr><fmt:message key="dispokarte.texte.shipto"/>:</nobr></td>
	    <td>
	   		<input  type="text" name="searchtext" value="" size="50" maxlength="100" class="textfield" id="search" style="width:100%"/>
	    </td>
		<td class="buttons">
		<div align="right" style="float:left; vertical-align:bottom; padding:0px 0px 0px 10px; width:auto;">
		<input onclick="hidePopup();" type="submit" class="ViewButtonStyle" name="select" value="<fmt:message key="buttons.cancel"/>"/>
		</div>
		</td>
    </tr>
</table>
<!--</html:form>-->
<%-- END $RCSfile: SiloShiptoSearchTile.jsp,v $ --%>
