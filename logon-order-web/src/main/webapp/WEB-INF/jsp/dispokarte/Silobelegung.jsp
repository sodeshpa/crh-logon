<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: Silobelegung.jsp,v $ --%>

<%@ include file="../includeTLDs.jsp" %>
<%@page import="com.crh.logon.web.utils.Authorization"%>

<c:choose>
	<c:when test="${param.referrer == 'order'}">
		<c:set var="targetForm" value="${order_bestellung_BestellungForm}"/>
	</c:when>
	<c:otherwise>
		<c:set var="targetForm" value="${order_dispokarte_DispokarteForm}"/>
	</c:otherwise>
</c:choose>

<script type="text/javascript">
	function deleteSilobelegung(id){
		var diva = Ext.get('DivSilobelegung');
		diva.load({
			url: '../dispokarte/Silobelegung.do', 
			params: {'delete': '','id': id, 'referrer': '<c:out value="${param.referrer}"/>'},
			text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
			scripts: true
		});
	}

	function presetRfIdForm(id) {
		OrderAjaxService.selectSiloByIndex(id, '<c:out value="${param.referrer}"/>', function() {
			showPopup('../OrderRFIDPopup.do');
		});
	}

	<c:if test="${param.referrer == 'order' && param.initialLoad != true}">
		var selectedSilo = editorList[0].siloCombo.getValue();
		editorList[0].siloCombo.store.loadData(<c:out value="${order_bestellung_BestellungForm.json['siloList']}" escapeXml="false"/>);
		editorList[0].siloCombo.preselectValue(selectedSilo, true);
	</c:if>

</script>

<table class="classic" style="border:none">
<c:set var="index" value="0"/>
<c:forEach var="silo" varStatus="status" items="${targetForm.shipTo.silos}">
<c:if test="${silo.activeFlag}">
<c:if test="${!empty silo.shipto}">
    <c:set var="index" value="${index + 1 }"/>
	<tr class="zebra<c:out value="${index % 2}"/>">
	<td><c:out value="${silo.siloName}"/></td>
	<td>
		<c:if test="${ not empty silo.sapMaterial.sapMaterialId }">
			<c:out value="${targetForm.siloMaterialDescriptionMap[silo.sapMaterial.sapMaterialId].txt}"/> (<c:out value="${silo.sapMaterial.sapMaterialNo}"/>)
		</c:if>	
	</td>
	<td>
	<c:if test="${not empty silo.maxVolumeTons }">
		<c:out value="${silo.maxVolumeTons}"/> t 
	</c:if>
	<c:if test="${ not empty silo.maxVolumeTons }">
		<c:if test="${not empty silo.maxVolumeM3 }">
		/
		</c:if>
	</c:if> 
	<c:if test="${not empty silo.maxVolumeM3 }">
		<c:out value="${silo.maxVolumeM3}"/> m3</td>
	</c:if>
	
	<td style="width:80px;overflow: hidden;"><c:out value="${silo.siloComment}"/></td>
	
	<td valign="middle" style="width:1px"><nobr>
		<crh-common:authorize function="<%= Authorization.MASTER_DATA_SHIPTO_CARD_W %>">
			<a href="javascript:deleteSilobelegung(<c:out value="${status.count-1}"/>)"> 
			<c:if test="${!silo.portableSiloFlag}">
				<img alt="delete" src="<crh-common:url value='/images/delete2.png'/>"/>
			</c:if>
			<c:if test="${silo.portableSiloFlag}">
				<img alt="detach" src="<crh-common:url value='/images/delete2.png'/>"/>
			</c:if></a>
		</crh-common:authorize>
		<a href="javascript:showPopup('../dispokarte/SilobelegungEntry.do?referrer=<c:out value="${param.referrer}"/>&action=edit&id=<c:out value="${status.count-1}"/>','')" ><img src="<crh-common:url value='../images/silk/pencil.png'/>"/></a>
		<a href="javascript:presetRfIdForm('<c:out value="${status.count-1}"/>')" >RFIDs</a>
		</nobr>
	</td>
	</tr>
	</c:if>
</c:if>
</c:forEach>
</table>