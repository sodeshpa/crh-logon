<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: EditSilobelegung.jsp,v $ --%>

<%@ include file="../includeTLDs.jsp" %>
<%@ page import="com.crh.logon.taglib.fallbacklogic.DefaultFallBackLogicTag" %>
<%@page import="com.crh.logon.order.web.forms.dispokarte.SilobelegungEditForm"%>
<%@page import="com.crh.logon.persistence.entities.common.ShiptoSiloEntity"%>
<%@page import="com.crh.logon.persistence.entities.refdata.SiloTypeTxEntity"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>

<c:choose>
	<c:when test="${param.referrer == 'order'}">
		<c:set var="okButtonMsgKey" value="buttons.save"/>
	</c:when>
	<c:otherwise>
		<c:set var="okButtonMsgKey" value="buttons.ok"/>
	</c:otherwise>
</c:choose>

<script type="text/javascript">
	onPopupReady(function() {
	
		var el = document.getElementById('siloName');
		if (el) {
			el.focus();
		}
	});

	function saveSilobelegung(){
		var bool = checkCommentLength("fldSilobelegungBemerkung", '<fmt:message key="confirm.trim.textarealength"/>','<fmt:message key="fields.silobelegung.bemerkung"/>', <%=ShiptoSiloEntity.DBSIZE_SILO_COMMENT + ""%>);
		if (!bool){
			return false;
		}
		a = {"save": "", "id": <c:out value="${order_dispokarte_SilobelegungEditForm.id}"/>, 'referrer': '<c:out value="${order_dispokarte_SilobelegungEditForm.referrer}"/>'};
		addFormElements(a, "SilobelegungForm");
	
		showPopup('../dispokarte/Silobelegung.do', null, a);
	}
</script>

<html:form action="dispokarte/Silobelegung.do" onsubmit="return false;" styleId="SilobelegungForm">
<html:hidden property="referrer"/>

<table class="classicblue" style="border:none; width:auto">
<tr>
	<th colspan="2">
		<fmt:message key="dispokarte.texte.silobelegung"/>
	</th>
</tr>
<tr>
	<td class="popupcontent">
	<table width="100%">
		<tr>
			<td class="label right"><fmt:message key="fields.silobelegung.name"/></td>
		<c:if test="${not order_dispokarte_SilobelegungEditForm.silo.portableSiloFlag}">
			<td><html:text style="width:200px;" property="silo.siloName" styleId="siloName" maxlength="<%=ShiptoSiloEntity.DBSIZE_SILO_NAME + ""%>" /> </td>
		</c:if>
		<c:if test="${order_dispokarte_SilobelegungEditForm.silo.portableSiloFlag}">
			<td><c:out value="${order_dispokarte_SilobelegungEditForm.silo.siloName}"/></td>
		</c:if>		
		</tr>
		<tr>
			<td class="label right"><fmt:message key="fields.silobelegung.produkt"/></td>
			<td>
				<html:select size="1" style="width:200px;" property="materialIdx">
					<html:option value="-1">-</html:option>
					<c:forEach var="sapMaterial" varStatus="status" items="${order_dispokarte_SilobelegungEditForm.materials}">
						<html:option value="${status.count-1}"><c:out value="${sapMaterial.txt}"/> (<c:out value="${sapMaterial.sapMaterialNo}"/>)</html:option>
					</c:forEach>					
				</html:select> 
			</td>
		</tr>
		<tr>
			<td class="label right"><fmt:message key="fields.silobelegung.maxvolumetons"/></td>
		<c:if test="${not order_dispokarte_SilobelegungEditForm.silo.portableSiloFlag}">
			<td><html:text style="width:200px;" property="maxVolumeTons" styleId="fldSilobelegungGroesse" maxlength="<%=ShiptoSiloEntity.DBSIZE_MAX_VOLUME_TONS + ""%>"/> </td>
		</c:if>
		<c:if test="${order_dispokarte_SilobelegungEditForm.silo.portableSiloFlag}">
			<td><c:out value="${order_dispokarte_SilobelegungEditForm.silo.maxVolumeTons}"/></td>
		</c:if>
		</tr>
		<tr>
			<td class="label right"><fmt:message key="fields.silobelegung.maxvolumemeter"/></td>
		<c:if test="${not order_dispokarte_SilobelegungEditForm.silo.portableSiloFlag}">
			<td><html:text style="width:200px;" property="maxVolumeMeter" styleId="fldSilobelegungGroesse" maxlength="<%=ShiptoSiloEntity.DBSIZE_MAX_VOLUME_M3 + ""%>"/> </td>
		</c:if>
		<c:if test="${order_dispokarte_SilobelegungEditForm.silo.portableSiloFlag}">
			<td><c:out value="${order_dispokarte_SilobelegungEditForm.silo.maxVolumeM3}"/></td>
		</c:if>						
		</tr>
		<tr>
			<td class="label right"><fmt:message key="fields.silobelegung.bemerkung"/></td>
		<c:if test="${not order_dispokarte_SilobelegungEditForm.silo.portableSiloFlag}">
			<c:set var="textarealength"><%=ShiptoSiloEntity.DBSIZE_SILO_COMMENT + ""%></c:set>
			<td><html:textarea onkeypress="return checkMaxLength(event, this, ${textarealength})" style="width:200px; height: 50px;" property="silo.siloComment" styleId="fldSilobelegungBemerkung"/> </td>
		</c:if>
		<c:if test="${order_dispokarte_SilobelegungEditForm.silo.portableSiloFlag}">
			<td><c:out value="${order_dispokarte_SilobelegungEditForm.silo.siloComment}"/></td>
		</c:if>		
		</tr>
			<tr>
			    <td class="label right"><fmt:message key="fields.silobelegung.silotype"/></td>
			    <c:if test="${not order_dispokarte_SilobelegungEditForm.silo.portableSiloFlag}">
			    <td>			
		<select name="siloTypeCd" id="siloTypeCd" property="siloTypeCd"  style="width:200px;">
					<c:forEach var="entry" items="${order_dispokarte_SilobelegungEditForm.siloTypes}">
					<%String entrySiloTypeCd = ((SiloTypeTxEntity)pageContext.getAttribute("entry")).getSiloTypeTxPK().getSiloTypeBs().getSiloTypeCd().toString();%>
					<option value="<%=entrySiloTypeCd%>" <%=( 
							((SilobelegungEditForm)session.getAttribute("order_dispokarte_SilobelegungEditForm")).getSiloTypeCd() != null &&  
							entrySiloTypeCd.equals(((SilobelegungEditForm)session.getAttribute("order_dispokarte_SilobelegungEditForm")).getSiloTypeCd().toString() )
							) ? "selected" : ""%> ><c:out value="${entry.txt}"/></option>
					</c:forEach></select>
				</td>
				</c:if>
				<c:if test="${order_dispokarte_SilobelegungEditForm.silo.portableSiloFlag}">
				<td><%-- Later simplify this--%>			
					<c:forEach var="entry" items="${order_dispokarte_SilobelegungEditForm.siloTypes}">
					<%String entrySiloTypeCd2 = ((SiloTypeTxEntity)pageContext.getAttribute("entry")).getSiloTypeTxPK().getSiloTypeBs().getSiloTypeCd().toString();
							if ( ((SilobelegungEditForm)session.getAttribute("order_dispokarte_SilobelegungEditForm")).getSiloTypeCd() != null && 
									entrySiloTypeCd2.equals(((SilobelegungEditForm)session.getAttribute("order_dispokarte_SilobelegungEditForm")).getSiloTypeCd().toString() ) ) {%> 
									 <c:out value="${entry.txt}"/>
							<%}%> 
					</c:forEach>
				</td>
				</c:if>
		</tr>
		<tr>		
		<td class="label right" align="right"><nobr><fmt:message key="dispokarte.texte.entladezeit"/></nobr></td>
		<td>
	<table><tr>
	<%pageContext.setAttribute("silo", ((SilobelegungEditForm)request.getSession().getAttribute("order_dispokarte_SilobelegungEditForm")).getSilo()); %>
		<c:if test="${not order_dispokarte_SilobelegungEditForm.silo.portableSiloFlag}">
	<crh-common:defaultFallback propertyName="<%=DefaultFallBackLogicTag.PROPERTY_DEFAULT_SILO_UNLOADING_TIME_MIN %>" propertyInputFieldName="silo.unloadingTime" propertyCheckboxName = "unloadingTimeCheckbox" objectName="silo">
			<td>
			<crh-common:defaultFallbackInputField style="width:50px;"></crh-common:defaultFallbackInputField>
			</td>
			<td class="label right" width="1"><nobr><fmt:message key="dispokarte.texte.entladezeitflag"/>:</nobr></td>			
			<td>
				<crh-common:defaultFallbackCheckbox></crh-common:defaultFallbackCheckbox>
			</td>
	</crh-common:defaultFallback>
		</c:if>
		<c:if test="${order_dispokarte_SilobelegungEditForm.silo.portableSiloFlag}">
	<crh-common:defaultFallback propertyName="<%=DefaultFallBackLogicTag.PROPERTY_DEFAULT_SILO_UNLOADING_TIME_MIN %>" propertyInputFieldName="silo.unloadingTime" propertyCheckboxName = "unloadingTimeCheckbox" objectName="silo" readOnly="true">
			<td>
			<crh-common:defaultFallbackInputField style="width:50px;"></crh-common:defaultFallbackInputField>
			</td>
			<td class="label right" width="1"><nobr><fmt:message key="dispokarte.texte.entladezeitflag"/></nobr></td>			
			<td>
				<crh-common:defaultFallbackCheckbox></crh-common:defaultFallbackCheckbox>
			</td>
	</crh-common:defaultFallback>
		</c:if>
	</td></tr></table>
			
	</tr>
		<tr>
			<td colspan="2" class="buttons right">
				<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.cancel"/>" onclick="hidePopup()"/>
				<crh-common:authorize function="<%= Authorization.MASTER_DATA_SHIPTO_CARD_W %>">
					<input type="button" class="ViewButtonStyle" value="<fmt:message key="${okButtonMsgKey}"/>" onclick="saveSilobelegung()"/>
				</crh-common:authorize>
			</td>
		</tr>
		
	</table>
	</td>
</table>

</html:form>
