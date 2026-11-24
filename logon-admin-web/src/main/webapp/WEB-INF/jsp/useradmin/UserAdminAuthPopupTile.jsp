<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: UserAdminAuthPopupTile.jsp,v $ --%>
<%@ page session="true"%>

<%@ include file="../includeTLDs.jsp"%>
<script type="text/javascript">
onPopupReady(function() {
	
	el = document.getElementById('selectedRoleIds');
	if (!el) {
		el = document.getElementById('selectedSoldToIds');
	}
	if (!el) {
		el = document.getElementById('selectedShipToIds');
	}
	if (!el) {
		el = document.getElementById('selectedPlantIds');
	}
	if (!el) {
		el = document.getElementById('selectedShippingPointIds');
	}
	if (!el) {
		el = document.getElementById('selectedPoolIds');
	}
	if (!el) {
		el = document.getElementById('selectedCarrierIds');
	}
	if (!el) {
		el = document.getElementById('selectedSareaIds');
	}
	if (!el) {
		el = document.getElementById('selectedSgroupIds');
	}
	if (!el) {
		el = document.getElementById('selectedCompCodeIds');
	}
	if (!el) {
		el = document.getElementById('selectedPurchOrgIds');
	}
	
	if (el) {
		el.focus();
	}
});

function saveAuth(id) {
	var el = Ext.getDom(id);

	if (el.selectedIndex == -1) {
		hidePopup();
		return;
	}

	var a = {
		"save" :"save"
	};
	addFormElements(a, "UserAdminAuthPopupForm");

	showPopup('UserAdminAuthPopup.do',
			'<fmt:message key="shippingpoint.text.exjsloading"/>', a);
}

</script>


<html:form action="/useradmin/UserAdminAuthPopup.do" styleId="UserAdminAuthPopupForm">
<table class="classicblue" style="border:none; width:auto" id="user-auth-table">
<tr>
	<th colspan="2">
		<c:if test="${admin_UserAdminAuthPopupForm.restrictionType == 0}">
			<fmt:message key="useradmin.texte.assign.customers"/>
		</c:if>
		<c:if test="${admin_UserAdminAuthPopupForm.restrictionType == 1}">
			<fmt:message key="useradmin.texte.assign.shipto"/>
		</c:if>
		<c:if test="${admin_UserAdminAuthPopupForm.restrictionType == 2}">
			<fmt:message key="useradmin.texte.assign.plants"/>
		</c:if>
		<c:if test="${admin_UserAdminAuthPopupForm.restrictionType == 3}">
			<fmt:message key="useradmin.texte.assign.shippingpoints"/>
		</c:if>
		<c:if test="${admin_UserAdminAuthPopupForm.restrictionType == 4}">
			<fmt:message key="useradmin.texte.assign.pools"/>
		</c:if>
		<c:if test="${admin_UserAdminAuthPopupForm.restrictionType == 5}">
			<fmt:message key="useradmin.texte.assign.carriers"/>
		</c:if>
		<c:if test="${admin_UserAdminAuthPopupForm.restrictionType == 6}">
			<fmt:message key="useradmin.texte.assign.sareas"/>
		</c:if>
		<c:if test="${admin_UserAdminAuthPopupForm.restrictionType == 7}">
			<fmt:message key="useradmin.texte.assign.sgroups"/>
		</c:if>
		<c:if test="${admin_UserAdminAuthPopupForm.restrictionType == 8}">
			<fmt:message key="useradmin.texte.assign.roles"/>
		</c:if>
		<c:if test="${admin_UserAdminAuthPopupForm.restrictionType == 9}">
			<fmt:message key="useradmin.texte.assign.compcode"/>
		</c:if>
		<c:if test="${admin_UserAdminAuthPopupForm.restrictionType == 10}">
			<fmt:message key="useradmin.texte.assign.purchorg"/>
		</c:if>
	</th>
</tr>
<tr>
	<td class="popupcontent">
	<table width="100%">
		<c:if test="${admin_UserAdminAuthPopupForm.restrictionType == 8}">
				<table cellspacing="0" cellpadding="0" style="width: 100%;"
					class="noborder">
					<tbody>
						<tr>
							<td>
							<html:select styleId="selectedRoleIds" multiple="true" size="7" property="selectedRoleIds" style=" height: 130px">
								<c:forEach var="entry" varStatus="status"
									items="${admin_UserAdminForm.selectableRoleMap}">
									<html:option value="${entry.key}">
										<label <c:out value='${entry.value.roleDescr}'/>"><c:out
												value='${entry.value.roleName}' /></label>
									</html:option>
								</c:forEach>
							</html:select>
							<div align="right">
								<input type="button" value='<fmt:message key="useradmin.texte.selectNone"/>' class="SmallButtonStyle" onclick="useradmin.selectDeselectAllOptions(selectedRoleIds, false)" />
								<input type="button" value='<fmt:message key="useradmin.texte.selectAll"/>' class="SmallButtonStyle" onclick="useradmin.selectDeselectAllOptions(selectedRoleIds, true)" />
								<input type="button" value='<fmt:message key="buttons.cancel"/>' class="SmallButtonStyle" onclick="hidePopup();" />
								<input type="button" value='<fmt:message key="buttons.hinzufuegen"/>' class="SmallButtonStyle" onclick="saveAuth(selectedRoleIds);" />
							</div>
							</td></tr>
					</tbody>
				</table>
			</c:if>
			<c:if test="${admin_UserAdminAuthPopupForm.restrictionType == 0}">
				<table cellspacing="0" cellpadding="0" style="width: 100%;" class="noborder">
					
					<tbody>
						<tr>
							<td>
							<html:select styleId="selectedSoldToIds" multiple="true" size="7" property="selectedSoldToIds" style=" height: 130px">
								<c:forEach var="entry" varStatus="status"
									items="${admin_UserAdminForm.selectableSoldToShipToSoldToMap}">
									<html:option value="${entry.key}">
										<c:out value="${entry.value.name}" />
									</html:option>
								</c:forEach>
							</html:select>
							<div align="right">
								<input type="button" value='<fmt:message key="useradmin.texte.selectNone"/>' class="SmallButtonStyle" onclick="useradmin.selectDeselectAllOptions(selectedSoldToIds, false)" />
								<input type="button" value='<fmt:message key="useradmin.texte.selectAll"/>' class="SmallButtonStyle" onclick="useradmin.selectDeselectAllOptions(selectedSoldToIds, true)" />
								<input type="button" value='<fmt:message key="buttons.cancel"/>' class="SmallButtonStyle" onclick="hidePopup();" />
								<input type="button" value='<fmt:message key="buttons.hinzufuegen"/>' class="SmallButtonStyle" onclick="saveAuth(selectedSoldToIds);" />
							</div>
							</td>
						</tr>
					</tbody>
				</table>
				</c:if>
				
				<c:if test="${admin_UserAdminAuthPopupForm.restrictionType == 1}">
				<table cellspacing="0" cellpadding="0" style="width: 100%;"
					class="noborder">
					
					<tbody>
						<tr>
							
							
							<td>
							<html:select styleId="selectedShipToIds" multiple="true" size="7" property="selectedShipToIds" style=" height: 130px">
								<c:forEach var="entry" varStatus="status"
									items="${admin_UserAdminForm.selectableSoldToShipToShipToMap}">
									<html:option value="${entry.key}">
										<c:out value="${entry.value.name}" />
									</html:option>
								</c:forEach>
							</html:select>
							<div align="right">
								<input type="button" value='<fmt:message key="useradmin.texte.selectNone"/>' class="SmallButtonStyle" onclick="useradmin.selectDeselectAllOptions(selectedShipToIds, false)" />
								<input type="button" value='<fmt:message key="useradmin.texte.selectAll"/>' class="SmallButtonStyle" onclick="useradmin.selectDeselectAllOptions(selectedShipToIds, true)" />
								<input type="button" value='<fmt:message key="buttons.cancel"/>' class="SmallButtonStyle" onclick="hidePopup();" />
								<input type="button" value='<fmt:message key="buttons.hinzufuegen"/>' class="SmallButtonStyle" onclick="saveAuth(selectedShipToIds);" />
							</div>
							</td></tr>
					</tbody>
				</table>
			</c:if>
			<c:if test="${admin_UserAdminAuthPopupForm.restrictionType == 2}">
				<table cellspacing="0" cellpadding="0" style="width: 100%;"
	class="noborder">
	
	<tbody>
		<tr>
			
			
			<td>
			<html:select styleId="selectedPlantIds" multiple="true" size="7" property="selectedPlantIds" style=" height: 130px">
				<c:forEach var="entry" varStatus="status"
					items="${admin_UserAdminForm.selectablePlantShippingPointPlantMap}">
					<html:option value="${entry.key}">
						<c:out value="${entry.value.name}" /> (<c:out value="${entry.value.value}" />)
					</html:option>
				</c:forEach>
			</html:select>
			<div align="right">
				<input type="button" value='<fmt:message key="useradmin.texte.selectNone"/>' class="SmallButtonStyle" onclick="useradmin.selectDeselectAllOptions(selectedPlantIds, false)" />
				<input type="button" value='<fmt:message key="useradmin.texte.selectAll"/>' class="SmallButtonStyle" onclick="useradmin.selectDeselectAllOptions(selectedPlantIds, true)" />
				<input type="button" value='<fmt:message key="buttons.cancel"/>' class="SmallButtonStyle" onclick="hidePopup();" />
				<input type="button" value='<fmt:message key="buttons.hinzufuegen"/>' class="SmallButtonStyle" onclick="saveAuth(selectedPlantIds);" />
			</div>
			</td></tr>
	</tbody>
</table>
</c:if>
<c:if test="${admin_UserAdminAuthPopupForm.restrictionType == 3}">
<table cellspacing="0" cellpadding="0" style="width: 100%;"
	class="noborder">
	
	<tbody>
		<tr>
			
			
			<td>
			<html:select styleId="selectedShippingPointIds" multiple="true" size="7" property="selectedShippingPointIds" style="height: 130px">
				<c:forEach var="entry" varStatus="status"
					items="${admin_UserAdminForm.selectablePlantShippingPointShippingPointMap}">
					<html:option value="${entry.key}">
						<c:out value="${entry.value.name}" />
					</html:option>
				</c:forEach>
			</html:select>
			<div align="right">
				<input type="button" value='<fmt:message key="useradmin.texte.selectNone"/>' class="SmallButtonStyle" onclick="useradmin.selectDeselectAllOptions(selectedShippingPointIds, false)" />
				<input type="button" value='<fmt:message key="useradmin.texte.selectAll"/>' class="SmallButtonStyle" onclick="useradmin.selectDeselectAllOptions(selectedShippingPointIds, true)" />
				<input type="button" value='<fmt:message key="buttons.cancel"/>' class="SmallButtonStyle" onclick="hidePopup();" />
				<input type="button" value='<fmt:message key="buttons.hinzufuegen"/>' class="SmallButtonStyle" onclick="saveAuth(selectedShippingPointIds);" />
			</div>
			</td>
		</tr>
	</tbody>
</table>
</c:if>
<c:if test="${admin_UserAdminAuthPopupForm.restrictionType == 4}">
				<table cellspacing="0" cellpadding="0" style="width: 100%;"
	class="noborder">
	
	<tbody>
		<tr>
			
			
			<td>
			<html:select styleId="selectedPoolIds" multiple="true" size="7" property="selectedPoolIds" style=" height: 130px">
				<c:forEach var="entry" varStatus="status"
					items="${admin_UserAdminForm.selectablePoolMap}">
					<html:option value="${entry.key}">
						<c:out value="${entry.value.name}" />
					</html:option>
				</c:forEach>
			</html:select>
			<div align="right">
				<input type="button" value='<fmt:message key="useradmin.texte.selectNone"/>' class="SmallButtonStyle" onclick="useradmin.selectDeselectAllOptions(selectedPoolIds, false)" />
				<input type="button" value='<fmt:message key="useradmin.texte.selectAll"/>' class="SmallButtonStyle" onclick="useradmin.selectDeselectAllOptions(selectedPoolIds, true)" />
				<input type="button" value='<fmt:message key="buttons.cancel"/>' class="SmallButtonStyle" onclick="hidePopup();" />
				<input type="button" value='<fmt:message key="buttons.hinzufuegen"/>' class="SmallButtonStyle" onclick="saveAuth(selectedPoolIds);" />
			</div>
			</td>
		</tr>
	</tbody>
</table>
</c:if>
<c:if test="${admin_UserAdminAuthPopupForm.restrictionType == 5}">
<table cellspacing="0" cellpadding="0" style="width: 100%;"
	class="noborder">
	
	<tbody>
		<tr>
			
			
			<td>
			<html:select styleId="selectedCarrierIds" multiple="true" size="7" property="selectedCarrierIds" style=" height: 130px">
				<c:forEach var="entry" varStatus="status"
					items="${admin_UserAdminForm.selectableCarrierMap}">
					<html:option value="${entry.key}">
						<c:out value="${entry.value.name}" />
					</html:option>
				</c:forEach>
			</html:select>
			<div align="right">
				<input type="button" value='<fmt:message key="useradmin.texte.selectNone"/>' class="SmallButtonStyle" onclick="useradmin.selectDeselectAllOptions(selectedCarrierIds, false)" />
				<input type="button" value='<fmt:message key="useradmin.texte.selectAll"/>' class="SmallButtonStyle" onclick="useradmin.selectDeselectAllOptions(selectedCarrierIds, true)" />
				<input type="button" value='<fmt:message key="buttons.cancel"/>' class="SmallButtonStyle" onclick="hidePopup();" />
				<input type="button" value='<fmt:message key="buttons.hinzufuegen"/>' class="SmallButtonStyle" onclick="saveAuth(selectedCarrierIds);" />
			</div>
			</td>
		</tr>
	</tbody>
</table>
</c:if>
<c:if test="${admin_UserAdminAuthPopupForm.restrictionType == 6}">
					<table cellspacing="0" cellpadding="0" style="width: 100%;"
	class="noborder">
	
	<tbody>
		<tr>
			
			
			<td>
			<html:select styleId="selectedSareaIds" multiple="true" size="7" property="selectedSareaIds" style=" height: 130px">
				<c:forEach var="entry" varStatus="status"
					items="${admin_UserAdminForm.selectableCompCodeSareaSareaMap}">
					<html:option value="${entry.key}">
						<c:out value="${entry.value.name}" />
					</html:option>
				</c:forEach>
			</html:select>
			<div align="right">
				<input type="button" value='<fmt:message key="useradmin.texte.selectNone"/>' class="SmallButtonStyle" onclick="useradmin.selectDeselectAllOptions(selectedSareaIds, false)" />
				<input type="button" value='<fmt:message key="useradmin.texte.selectAll"/>' class="SmallButtonStyle" onclick="useradmin.selectDeselectAllOptions(selectedSareaIds, true)" />
				<input type="button" value='<fmt:message key="buttons.cancel"/>' class="SmallButtonStyle" onclick="hidePopup();" />
				<input type="button" value='<fmt:message key="buttons.hinzufuegen"/>' class="SmallButtonStyle" onclick="saveAuth(selectedSareaIds);" />
			</div>
			</td>
		</tr>
	</tbody>
</table>
</c:if>
<c:if test="${admin_UserAdminAuthPopupForm.restrictionType == 7}">
<table cellspacing="0" cellpadding="0" style="width: 100%;"
	class="noborder">
	
	<tbody>
		<tr>
			
			
			<td>
			<html:select styleId="selectedSgroupIds" multiple="true" size="7" property="selectedSgroupIds" style=" height: 130px">
				<c:forEach var="entry" varStatus="status"
					items="${admin_UserAdminForm.selectableSgroupMap}">
					<html:option value="${entry.key}">
						<c:out value="${entry.value.name}" />
					</html:option>
				</c:forEach>
			</html:select>
			<div align="right">
				<input type="button" value='<fmt:message key="useradmin.texte.selectNone"/>' class="SmallButtonStyle" onclick="useradmin.selectDeselectAllOptions(selectedSgroupIds, false)" />
				<input type="button" value='<fmt:message key="useradmin.texte.selectAll"/>' class="SmallButtonStyle" onclick="useradmin.selectDeselectAllOptions(selectedSgroupIds, true)" />
				<input type="button" value='<fmt:message key="buttons.cancel"/>' class="SmallButtonStyle" onclick="hidePopup();" />
				<input type="button" value='<fmt:message key="buttons.hinzufuegen"/>' class="SmallButtonStyle" onclick="saveAuth(selectedSgroupIds);" />
			</div>
			</td>
		</tr>
	</tbody>
</table>
</c:if>
<c:if test="${admin_UserAdminAuthPopupForm.restrictionType == 9}">
<table cellspacing="0" cellpadding="0" style="width: 100%;"
	class="noborder">
	
	<tbody>
		<tr>
			
			
			<td>
			<html:select styleId="selectedCompCodeIds" multiple="true" size="7" property="selectedCompCodeIds" style=" height: 130px">
				<c:forEach var="entry" varStatus="status"
					items="${admin_UserAdminForm.selectableCompCodeSareaCompCodeMap}">
					<html:option value="${entry.key}">
						<c:out value="${entry.value.name}" />
					</html:option>
				</c:forEach>
			</html:select>
			<div align="right">
				<input type="button" value='<fmt:message key="useradmin.texte.selectNone"/>' class="SmallButtonStyle" onclick="useradmin.selectDeselectAllOptions(selectedCompCodeIds, false)" />
				<input type="button" value='<fmt:message key="useradmin.texte.selectAll"/>' class="SmallButtonStyle" onclick="useradmin.selectDeselectAllOptions(selectedCompCodeIds, true)" />
				<input type="button" value='<fmt:message key="buttons.cancel"/>' class="SmallButtonStyle" onclick="hidePopup();" />
				<input type="button" value='<fmt:message key="buttons.hinzufuegen"/>' class="SmallButtonStyle" onclick="saveAuth(selectedCompCodeIds);" />
			</div>
			</td>
		</tr>
	</tbody>
</table>
</c:if>
<c:if test="${admin_UserAdminAuthPopupForm.restrictionType == 10}">
<table cellspacing="0" cellpadding="0" style="width: 100%;"
	class="noborder">
	
	<tbody>
		<tr>
			
			
			<td>
			<html:select styleId="selectedPurchOrgIds" multiple="true" size="7" property="selectedPurchOrgIds" style=" height: 130px">
				<c:forEach var="entry" varStatus="status"
					items="${admin_UserAdminForm.selectablePurchOrgMap}">
					<html:option value="${entry.key}">
						<c:out value="${entry.value.name}" />
					</html:option>
				</c:forEach>
			</html:select>
			<div align="right">
				<input type="button" value='<fmt:message key="useradmin.texte.selectNone"/>' class="SmallButtonStyle" onclick="useradmin.selectDeselectAllOptions(selectedPurchOrgIds, false)" />
				<input type="button" value='<fmt:message key="useradmin.texte.selectAll"/>' class="SmallButtonStyle" onclick="useradmin.selectDeselectAllOptions(selectedPurchOrgIds, true)" />
				<input type="button" value='<fmt:message key="buttons.cancel"/>' class="SmallButtonStyle" onclick="hidePopup();" />
				<input type="button" value='<fmt:message key="buttons.hinzufuegen"/>' class="SmallButtonStyle" onclick="saveAuth(selectedPurchOrgIds);" />
			</div>
			</td>
		</tr>
	</tbody>
</table>
</c:if>
	</table>
	</td>
</table>

</html:form>
