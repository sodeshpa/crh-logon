<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>
<%-- BEGIN $RCSfile: WagonStatusEditTile.jsp,v $ --%>
<%@ include file="../includeTLDs.jsp" %>
<%-- Struts needs this fake form tag, fond no better way to do it. --%> 
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page" value="${order_raildispo_DispoForm}"/>

<div style="overflow-y:none;border-left:none;border-top:none;border-right:none;" id="raildispoEditStatusList" class="border">
<table class="classic" style="border:none">
<tr class="colorarriving">
<td>
	<html:checkbox property="statusIncoming" value="true" onclick="setSelectedStatus()"></html:checkbox>
</td>
<td><fmt:message key="raildispo.texte.status.incoming" /></td>
</tr>
<tr class="colorarrived">
<td>
	<html:checkbox property="statusAtSp" value="true" onclick="setSelectedStatus()"></html:checkbox>
</td>
<td><fmt:message key="raildispo.texte.status.atSp" /></td>
</tr>
<tr class="colorloaded">
<td>
	<html:checkbox property="statusLoaded" value="true" onclick="setSelectedStatus()"></html:checkbox>
</td>
<td><fmt:message key="raildispo.texte.status.loaded" /></td>
</tr>
<tr class="colortransmitted">
<td>
	<html:checkbox property="statusTransmitted" value="true" onclick="setSelectedStatus()"></html:checkbox>
</td>
<td><fmt:message key="raildispo.texte.status.transmitted" /></td>
</tr>
</table>
</div>

<div class="buttons right" style="vertical-align: top; width:200px">
	<input type="button" class="SmallButtonStyle" value="<fmt:message
					key="dispo.button.check" />"
					onclick="javascript:setAllCheckboxes(true, 'DivStatusEdit');setSelectedStatus();statusList.collapse()">
	<input type="button" class="SmallButtonStyle" value="<fmt:message
					key="dispo.button.uncheck" />"
					onclick="javascript:setAllCheckboxes(false, 'DivStatusEdit');setSelectedStatus()">
</div> 
 