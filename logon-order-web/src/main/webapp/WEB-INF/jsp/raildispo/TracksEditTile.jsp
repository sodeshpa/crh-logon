<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>
<%-- BEGIN $RCSfile: TracksEditTile.jsp,v $ --%>
<%@ include file="../includeTLDs.jsp" %>
<%-- Struts needs this fake form tag, fond no better way to do it. --%> 
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page" value="${order_raildispo_DispoForm}"/>

<div style="overflow-x:hidden;overflow-y:auto;border-left:none;border-top:none;border-right:none;" id="raildispoEditTracksList" class="border">
<table class="classic" style="border:none">
<c:forEach var="track" varStatus="status" items="${order_raildispo_DispoForm.tracks}">
<tr class="zebra<c:out value="${status.count % 2}"/>">
<td>
	<html:checkbox property="selectedTrackCheckbox[${status.count - 1}]" value="true" onclick="setSelectedTrackCheckboxes()"></html:checkbox>
</td>
<td><c:out value="${track.trackName}"/></td>
<td><nobr><c:out value="${track.trackNameTx}"/></nobr></td>
</tr>
</c:forEach>
</table>
</div>
<script type="text/javascript">
	var el = Ext.get("raildispoEditTracksList");
	if (el.getHeight() > 300){
		el.setHeight(300);
	}
</script>

<div class="buttons right" style="vertical-align: top; width:280px">
	<input type="button" class="SmallButtonStyle" value="<fmt:message
					key="dispo.button.check" />"
					onclick="javascript:setAllCheckboxes(true, 'DivTracksEdit');setSelectedTrackCheckboxes();tracksList.collapse()">
	<input type="button" class="SmallButtonStyle" value="<fmt:message
					key="dispo.button.uncheck" />"
					onclick="javascript:setAllCheckboxes(false, 'DivTracksEdit');setSelectedTrackCheckboxes()">
</div> 
 