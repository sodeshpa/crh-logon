<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: ZoneMapPolygon.jsp,v $ --%>
<%@ page session="true"%>

<%@ include file="../includeTLDs.jsp"%>
<%@page
	import="com.crh.logon.admin.web.actions.masterdata.ZoneMapAction"%>

<c:set var="org.apache.struts.taglib.html.BEAN" scope="page"
	value="${admin_ZoneMapForm}" />
<c:set var="zIndexTitle">
	<fmt:message key='zonemap.text.zindex' />
</c:set>

<table class="classic" style="border: none; width: 400px">
	<c:forEach var="polygon" varStatus="status"
		items="${admin_ZoneMapForm.polygonList}">
		<tr class="zebra<c:out value="${status.count % 2}"/>">
			<td width="10"><c:choose>
				<c:when
					test='${admin_ZoneMapForm.selectedPolygonId == polygon.trspZonePolygonId}'>
					<c:set var="checkedAttr" value=" checked " />
				</c:when>
				<c:otherwise>
					<c:set var="checkedAttr" value="" />
				</c:otherwise>
			</c:choose> <input type="radio" id="selectedPolygonId" name="selectedPolygonId"
				value="<c:out value="${polygon.trspZonePolygonId}"/>"
				<c:out value="${checkedAttr}"/>
				onclick="setSelectedPolygon(this.value)" /></td>

			<td><input style="width: 1px" type="text"
				id="zoneCdIdDummy<c:out value="${polygon.trspZonePolygonId}"/>"
				value="" /> <html:hidden
				property="polygonZone[${status.count-1}]"
				styleId="hiddenzoneCdId${polygon.trspZonePolygonId}" /></td>
			<td><input type="text" size="2" style="width: 18px;"
				title="<fmt:message key="zonemap.text.zindex"/>"
				id="zIndexDummy<c:out value="${polygon.trspZonePolygonId}"/>" /> <html:hidden
				property="polygonList[${status.count-1}].ZIndex"
				styleId="hiddenzIndex${polygon.trspZonePolygonId}" /></td>
			<td>
			<div id="divColorId<c:out value="${polygon.trspZonePolygonId}"/>"></div>
			<html:hidden styleId="hiddencolorId${polygon.trspZonePolygonId}"
				property="polygonList[${status.count-1}].colorRgb" /> <input
				type="hidden" id="hiddenPolygonId<c:out value="${status.count-1}"/>"
				value="<c:out value="${polygon.trspZonePolygonId}"/>" /></td>

			<td valign="middle" style="width: 1px"><nobr> <a
				href="javascript:removePolygon(<c:out value="${status.count-1}"/>,<c:out value="${polygon.trspZonePolygonId}"/>)"
				title="<fmt:message key="buttons.delete"/>"><img
				src="<crh-common:url value='/images/delete2.png' />" /></a> </nobr></td>
		</tr>
	</c:forEach>
</table>

<script type="text/javascript">
    polygonListSize = <c:out value="${admin_ZoneMapForm.polygonListSize}"/>;
    
    //set new added polygon as selected (init values)
	if (polygonAction == '<%=ZoneMapAction.FORWARD_ADD%>') {
	
	    var selPolId = <c:out value="${admin_ZoneMapForm.selectedPolygonId}"/>;
	    polygonDataMap[selPolId] = new Array();
	    polygonMap[selPolId] = drawPolygon(new Array(), '#FFFFFF');
	    setSelectedPolygon(selPolId);
	    initPolygonList();
	}
	else if (polygonAction == '<%=ZoneMapAction.FORWARD_REMOVE%>') {
		var selPolId = <c:out value="${admin_ZoneMapForm.selectedPolygonId}"/>;
		setSelectedPolygon(selPolId);
		initPolygonList();
	}
	else if (polygonAction == '<%=ZoneMapAction.FORWARD_SAVE%>') {
		var selPolId = <c:out value="${admin_ZoneMapForm.selectedPolygonId}"/>;
		setSelectedPolygon(selPolId);
		initPolygonList();

		if ('<%=ZoneMapAction.RESULT_SUCCESS%>' == '<c:out value="${admin_ZoneMapForm.formResult}"/>') {
			loadMap();
			loadAllPolygons();
		}
	}

	//reset
	polygonAction = '';	
</script>
<%-- END $RCSfile: ZoneMapPolygon.jsp,v $ --%>
