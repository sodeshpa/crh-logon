
<%@ include file="../includeTLDs.jsp" %>
<%@page import="com.crh.logon.web.forms.RFIDPopupForm" %>
<%@page import="com.crh.logon.admin.web.forms.useradmin.UserAdminForm"%>
<%@page import="com.crh.logon.persistence.entities.admin.UserEntity"%>
<%@page import="com.crh.logon.admin.web.forms.useradmin.UserAdminExtLoginPwdPopupForm"%>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>
<script type="text/javascript">

// --- Common properties for all date-fields ---
Ext.form.DateField.prototype.enableKeyEvents = true;
Ext.form.DateField.prototype.onKeyPress = function (event, field) {
    if (event.getKey() == 13) {
        this.onTriggerClick();
    }
}

var texts = {
    alertRfid: '<fmt:message key="res.rfidobject.label.not.unique.userpda"/>'
};

Ext.onReady(function () {

    if (document.getElementById('searchCustomerByName') != null && document.getElementById('searchCustomerById') != null) {
        var customerRecordType = Ext.data.Record.create([
            {name: 'soldtoSapBpId', mapping: 'soldtoSapBpId'},
            {name: 'soldtoSapId', mapping: 'soldtoSapId'},
            {name: 'soldtoName', mapping: 'soldtoName'},
        ]);

        var customerListByIdStore = new Ext.data.Store({
            proxy: new Ext.data.DWRProxyForPager(UserAdminAjaxService.fetchSoldToListById, true),
            reader: new Ext.data.ListRangeReader({id: 'soldtoSapId', totalProperty: 'totalSize'}, customerRecordType),
            remoteSort: true
        });

        var customerListByNameStore = new Ext.data.Store({
            proxy: new Ext.data.DWRProxyForPager(UserAdminAjaxService.fetchSoldToListByName, true),
            reader: new Ext.data.ListRangeReader({id: 'soldtoName', totalProperty: 'totalSize'}, customerRecordType),
            remoteSort: true
        });


        // Custom rendering Template
        var resultTpl = new Ext.XTemplate(
                '<table class="classic"><thead><tr>',
                '<th width="100"><fmt:message key="fields.kunde.sapbpid"/></th>',
                '<th width="100"><fmt:message key="fields.kunde.sapid"/></th>',
                '<th width="300"><fmt:message key="fields.kunde.name"/></th>',
                '</tr></thead><tbody>',
                '<tpl for="."><tr  class="listitem zebra{[xindex % 2]}">',
                '<td>{soldtoSapBpId}</td>',
                '<td>{soldtoSapId}</td>',
                '<td>{soldtoName}</td>',
                '</tr></tpl>',
                '</tbody></table>'
        );

        var searchById = new Ext.form.ComboBox({
            store: customerListByIdStore,
            displayField: 'soldtoSapId',
            typeAhead: false,
            loadingText: '<fmt:message key="dispokarte.texte.shiptosearching"/>',
            maxHeight: 800,
            minChars: 1,
            pageSize: 20,
            hideTrigger: true,
            tpl: resultTpl,
            applyTo: 'searchCustomerById',
            itemSelector: 'tr.listitem',
            onSelect: function (record) {
                Ext.get('searchCustomerById').dom.value = record.data.soldtoSapId;
                Ext.get('searchCustomerByName').dom.value = record.data.soldtoName;
                searchById.collapse();
                useradmin.submitWithActionToDiv('selectSoldTo', 'admin_UserAdminFormUserAdmin', '');
            }
        });

        var searchByName = new Ext.form.ComboBox({
            store: customerListByNameStore,
            displayField: 'soldtoName',
            typeAhead: false,
            loadingText: '<fmt:message key="dispokarte.texte.shiptosearching"/>',
            maxHeight: 800,
            minChars: 1,
            pageSize: 20,
            hideTrigger: true,
            tpl: resultTpl,
            applyTo: 'searchCustomerByName',
            itemSelector: 'tr.listitem',
            onSelect: function (record) {
                Ext.get('searchCustomerById').dom.value = record.data.soldtoSapId;
                Ext.get('searchCustomerByName').dom.value = record.data.soldtoName;
                searchByName.collapse();
                useradmin.submitWithActionToDiv('selectSoldTo', 'admin_UserAdminFormUserAdmin', '');
            }
        });
        Ext.get("searchCustomerById").setTop("0px");
        Ext.get("searchCustomerByName").setTop("0px");
    }

// ---------- VENDOR ---------------//


    if (document.getElementById('searchVendorByName') != null && document.getElementById('searchVendorById') != null) {
        var vendorRecordType = Ext.data.Record.create([
            {name: 'sapVendorId', mapping: 'sapVendorId'},
            {name: 'sapId', mapping: 'sapId'},
            {name: 'name1', mapping: 'name1'},
            {name: 'displayName', mapping: 'displayName'}
        ]);

        var vendorListByIdStore = new Ext.data.Store({
            proxy: new Ext.data.DWRProxyForPager(UserAdminAjaxService.fetchVendorListById, true),
            reader: new Ext.data.ListRangeReader({id: 'sapVendorId', totalProperty: 'totalSize'}, vendorRecordType),
            remoteSort: true
        });

        var vendorListByNameStore = new Ext.data.Store({
            proxy: new Ext.data.DWRProxyForPager(UserAdminAjaxService.fetchVendorListByName, true),
            reader: new Ext.data.ListRangeReader({id: 'displayName', totalProperty: 'totalSize'}, vendorRecordType),
            remoteSort: true
        });

        // Custom rendering Template
        var resultTpl = new Ext.XTemplate(
                '<table class="classic"><thead><tr>',
                '<th width="100"><fmt:message key="fields.vendor.id"/></th>',
                '<th width="100"><fmt:message key="fields.carrier.commercial"/></th>',
                '<th width="100"><fmt:message key="fields.vendor.sapid"/></th>',
                '<th width="300"><fmt:message key="fields.vendor.name"/></th>',
                '</tr></thead><tbody>',
                '<tpl for="."><tr  class="listitem zebra{[xindex % 2]}">',
                '<td>{sapVendorId}</td>',
                '<td>{displayName}</td>',
                '<td>{sapId}</td>',
                '<td>{name1}</td>',
                '</tr></tpl>',
                '</tbody></table>'
        );

        var searchById = new Ext.form.ComboBox({
            store: vendorListByIdStore,
            valueField: 'sapId',
            displayField: 'sapId',
            typeAhead: false,
            loadingText: '<fmt:message key="dispokarte.texte.shiptosearching"/>',
            maxHeight: 800,
            minChars: 1,
            pageSize: 20,
            hideTrigger: true,
            tpl: resultTpl,
            applyTo: 'searchVendorById',
            itemSelector: 'tr.listitem',
            onSelect: function (record) {
                Ext.get('searchVendorById').dom.value = record.data.sapId;
                Ext.get('searchVendorByName').dom.value = record.data.displayName;
                searchById.collapse();
                document.getElementsByName('driverFlag')[0].disabled = true; // disable driverFlag checkbox, otherwise the ajax calls are going to conflict
                useradmin.submitWithActionToDiv('selectVendor', 'admin_UserAdminFormUserAdmin', '');
            }
        });

        var searchByName = new Ext.form.ComboBox({
            store: vendorListByNameStore,
            displayField: 'name1',
            typeAhead: false,
            loadingText: '<fmt:message key="dispokarte.texte.shiptosearching"/>',
            maxHeight: 800,
            minChars: 1,
            pageSize: 20,
            hideTrigger: true,
            tpl: resultTpl,
            applyTo: 'searchVendorByName',
            itemSelector: 'tr.listitem',
            onSelect: function (record) {
                Ext.get('searchVendorById').dom.value = record.data.sapId;
                Ext.get('searchVendorByName').dom.value = record.data.displayName;
                searchByName.collapse();
                document.getElementsByName('driverFlag')[0].disabled = true; // disable driverFlag checkbox, otherwise the ajax calls are going to conflict
                useradmin.submitWithActionToDiv('selectVendor', 'admin_UserAdminFormUserAdmin', '');
            }
        });
        Ext.get("searchVendorById").setTop("0px");
        Ext.get("searchVendorByName").setTop("0px");
    }


    if (
            document.getElementById('userDetailsPanel_pdaPinValidToDatStr') != null &&
                    document.getElementById('userDetailsPanel_pdaPinValidToTimeStr') != null
            ) {

        var pdaPinValidToDat = new Ext.form.DateField({
            id: 'pdaPinValidToDat',
            format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
		altFormats: '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>',
		applyTo: 'userDetailsPanel_pdaPinValidToDatStr',
		allowBlank: false,
		selectOnFocus: true
	});
	
	var pdaPinValidToTime = new Ext.form.TimeField({
		id: 'pdaPinValidToTime',
		increment: 15,
		format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
		applyTo: 'userDetailsPanel_pdaPinValidToTimeStr',
		allowBlank: false,
		selectOnFocus: true
	});

	}
	
	if (
		document.getElementById('certifiedFromDat') != null && 
		document.getElementById('certifiedToDat') != null
	) {
		var certifiedFromDat = new Ext.form.DateField({
			id: 'certifiedFromDat',
			format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
			altFormats: '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>',
			applyTo: 'certifiedFromDat',
			allowBlank: false,
			selectOnFocus: true
		});
		
		var delToDat = new Ext.form.DateField({
			id: 'certifiedToDat',
			format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
			altFormats: '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>',
			applyTo: 'certifiedToDat',
			allowBlank: false,
			selectOnFocus: true
		});	
	}
	var deliveryFromToValidator = function() {
		//if (Ext.getCmp('delFromDat').getValue() <= Ext.getCmp('delToDat').getValue()) {
		//	if (Ext.getCmp('delFromDat').getValue() < Ext.getCmp('delToDat').getValue()) {
		//		return true;
		//	}
		//	else if (Ext.getCmp('delFromTime').getValue() <= Ext.getCmp('delToTime').getValue()) {
				return true;
		//	}
		//}
		//return false;
	}

});

</script>

<% pageContext.setAttribute("currentTS", "" + System.currentTimeMillis()); %>

<c:if test="${admin_UserAdminForm.searchState == null}">
<c:choose>
	<c:when test="${admin_UserAdminForm.userEntity.userId == null}"><c:set var="modifyState" scope="page" value="false"/>
		</c:when>
	<c:otherwise><c:set var="modifyState" scope="page" value="true"/></c:otherwise>
</c:choose>
<%
UserAdminForm uaForm = ((UserAdminForm)request.getSession().getAttribute("admin_UserAdminForm"));
 %>
<html:form action="/useradmin/UserAdmin.do" styleId="admin_UserAdminFormUserAdmin">

<html:hidden property="inputAction" value=""/>
	<!-- beginn Border-Table   -->
	<table class="classicblue" cellpadding="0" cellspacing="0" border="0"
		width="100%" style="padding: 0px; margin: 0px;">
		<th colspan="3"><fmt:message key="useradmin.texte.detailinformation" /> <c:out value="${admin_UserAdminForm.userEntity.person.firstname}"/> <c:out value="${admin_UserAdminForm.userEntity.person.name}"/></th>
	<table class="classicblue" cellpadding="0" cellspacing="0" border="0" width="100%" style="padding: 0px; margin: 0px;">
		<tr>
			<td>
				<table id="user-details-table">
					<tbody>						
					<tr>
						<td valign="bottom" colspan="2">
							<p class="subtitle1"><fmt:message key="useradmin.texte.userInfo"/></p>
						</td>
					</tr>
						<tr>
							<td class="left"><nobr><fmt:message
								key="useradmin.field.active" /></nobr></td>
							

							<td>
								<nobr>
									<html:checkbox
										 styleId="userDetailsPanel_aktiv"
										property="userEntity.person.activeFlag" disabled="true"/>
								</nobr>
							</td>
						</tr>
						<tr>
							<td class="left"><nobr><fmt:message
								key="useradmin.field.firstName" /></nobr></td>
							<td>
							<nobr>
								<c:out value="${admin_UserAdminForm.userEntity.person.firstname}"/>
						</tr>
						<tr>
							<td class="left"><nobr><fmt:message
								key="useradmin.field.name" /></nobr></td>
								
							<td>
								<c:out value="${admin_UserAdminForm.userEntity.person.name}"/>
							</td>
						</tr>
						<tr>
						<td class="left"><nobr><fmt:message key="useradmin.field.type" /></nobr></td>
						<td>
							<c:out value="${admin_UserAdminForm.userType}"/>
						</td>
						</tr>
						<tr>
							<td class="left"><nobr><fmt:message
								key="useradmin.field.ldapHolspace"/></nobr></td>
							<td>
							<nobr>
								<c:out value="${admin_UserAdminForm.userEntity.ldapHolspace}"/>
							</td>
						</tr>
						<tr>
						<td class="left"><nobr><fmt:message
								key="useradmin.field.email" />
						<td>
								<c:out value="${admin_UserAdminForm.userEntity.person.email}"/>
							</td>
						</tr>
						<tr>
							<td class="left"><nobr><fmt:message
								key="useradmin.field.phoneNr" /></nobr></td>
							<td><nobr><c:out value="${userEntity.person.phoneNr}" /></nobr></td>
						</tr>
						<tr>
							<td class="left"><nobr><fmt:message
								key="useradmin.field.mobileNr" /></nobr></td>
							<td><nobr><c:out value="${userEntity.person.mobileNr}" /></nobr></td>
						</tr>
						<tr>
							<td class="left"><nobr><fmt:message
								key="useradmin.field.signatureValidFlag" /></nobr>
							</td>
							<td class="left"><html:checkbox
										 styleId="userDetailsPanel_signatureValidFlag"
										property="userEntity.signatureValidFlag" disabled="true"/>
							</td> 
						</tr>
						<tr>
							<td class="left"><nobr><fmt:message
								key="useradmin.field.signiture" /></nobr></td>
								<td><nobr><c:out value="${admin_UserAdminForm.userEntity.signature.signatureId}"/></nobr></td>
						</tr>
						<tr>
							<td class="left"><nobr><fmt:message
								key="useradmin.field.photoImage" /></nobr></td>
								<td><nobr><c:out value="${admin_UserAdminForm.userEntity.photoImage.imageId}"/></nobr></td>
						</tr>
						<tr>
						<td class="left"><nobr><fmt:message
								key="useradmin.field.applicationLang" /></nobr>
							</td>
							<td>
							<html:select styleId="applicationLang" size="1" style="width:200px;" property="userEntity.applicationLang.langCd"
										disabled="true">
								<c:forEach var="userLanguage" varStatus="status"
									items="${admin_UserAdminForm.userLanguageList}">
									<html:option value="${userLanguage.langTxPK.langBs.langCd}">
									<c:out value="${userLanguage.txt}" />
								</html:option>
								</c:forEach>
							</html:select>
							</td>
						</tr>
						<tr>
							<td class="left"><nobr><fmt:message key="application.label.timezone" /></nobr>
							</td>
							<td>
								<html:select styleId="applicationLang" size="1"
											 style="width:300px;"
											 property="selectedTimeZoneCd" disabled="true">
									<c:forEach var="tz" varStatus="status"
											   items="${admin_UserAdminForm.timeZoneList}">
										<html:option value="${tz.timezoneCd}">
											<c:out value="${tz.formattedAsTimeZoneOnly}" />
										</html:option>
									</c:forEach>
								</html:select>
							</td>
						</tr>
						<c:if test="${admin_UserAdminForm.userEntity.userType.userTypeCd == 1 }">
						
							<tr>
							<td class="left"><nobr><fmt:message
								key="useradmin.field.customerNr"/></nobr></td>
							<td>
								<c:out value="${admin_UserAdminForm.userEntity.sapBp.sapId}" />
							</td>
						</tr>
						<tr>
							<td class="left"><nobr><fmt:message
								key="useradmin.field.customerName" /></nobr></td>
							<td>
								<c:out value="${admin_UserAdminForm.customerName}" />
							</td>
						</tr>
						</c:if>
						<c:if test="${(admin_UserAdminForm.userEntity.userType.userTypeCd == 2) || (admin_UserAdminForm.userEntity.userType.userTypeCd == 3)}">
									
												
							<tr>
							<td class="left"><nobr><fmt:message
								key="useradmin.field.vendorNr"/></nobr></td>
							<td>
								<c:out value="${admin_UserAdminForm.userEntity.sapVendor.sapId}" />
							</td>
						</tr>
						<tr>
							<td class="left"><nobr><fmt:message
								key="useradmin.field.vendorName" /></nobr></td>
							<td>
								<c:out value="${admin_UserAdminForm.carrierVendor.displayName}" /> (<c:out value="${admin_UserAdminForm.userEntity.sapVendor.name1}"/>)
							</td>
						</tr>
						</c:if>
						<tr>
							<td class="left"><nobr><fmt:message
								key="useradmin.field.driverFlag" /></nobr></td>
							<td>
							<nobr>
								<html:checkbox
								 styleId="userDetailsPanel_driverFlag"
								property="driverFlag" disabled="true"/>
							</nobr>&nbsp;&nbsp;
							</td> 
						</tr>
						
						<%-- BEGIN DRIVER DETAILS--%>
						<c:if test="${admin_UserAdminForm.driverFlag == true}">
						<br/>
						<tr>
							<td valign="bottom" colspan="2">
								<p class="subtitle1"><fmt:message key="useradmin.texte.driverinfo"/></p>
							</td>
						</tr>
							<tr>
								<td class="left"><nobr><fmt:message
									key="useradmin.field.currentTruckId" /></nobr></td>
								<td>
										<c:out value="${admin_UserAdminForm.driverEntity.currentTruck.truckId}" />
								</td>
							</tr>
							
							
							<tr>
								<td class="left"><nobr><fmt:message
									key="useradmin.field.certifiedFromDat" /></nobr></td>
								<td>
									<c:out value="${admin_UserAdminForm.certifiedFromDat}"/>
								</td>
							</tr>
							<tr>
								<td class="left"><nobr><fmt:message
									key="useradmin.field.certifiedToDat" /></nobr></td>
								<td>
									<c:out value="${admin_UserAdminForm.certifiedToDat}"/>
								</td>
							</tr>
							<tr>
								<td class="left"><nobr><fmt:message
									key="useradmin.field.driverComment" /></nobr></td>
								<td>
								<c:out value="${admin_UserAdminForm.driverEntity.driverComment}"/>
								</td>
							</tr>
					</c:if>
					<%-- END DRIVER DETAILS--%>
					</tbody>
				</table>
			</td>
		
			<td>
			<div id="user-externalaccess-div" style="width:100%">
				<table>
					<tbody>
						<tr>
							<td valign="bottom" colspan="2">
							<p class="subtitle1"><fmt:message key="useradmin.texte.extAccess" /></p>
							</td>
						</tr>
						<tr>
							<td class="left"><nobr><fmt:message
								key="useradmin.field.active" /></nobr>
							</td>
							<td>
								<nobr>
									<html:checkbox property="userEntity.extLoginActiveFlag" styleId="userExternalAccessPanel_extAktiv" 
									onclick="useradmin.handleExternalAccess(this.checked);" disabled="true"/>
								</nobr>
							</td>
						</tr>
						<tr>
							<td class="left"><fmt:message
								key="useradmin.field.login" />
							</td>
							<td>
								<c:out value="${admin_UserAdminForm.userEntity.extLogin}"/>
							</td>
						</tr>
						<tr>
							<td class="left"><nobr><fmt:message
								key="useradmin.field.pwd" /></nobr></td>
							<td><input disabled="disabled" style="width:130px;" type="password" value="•••••"/>
							<input type="image" src="<crh-common:url value='/images/lock_edit.png'/>" onClick="useradmin.pwdChange(false, <%= UserAdminExtLoginPwdPopupForm.FORMTYPE_EXTPWD %> );return false;"/></td>
						</tr>
						<tr>
							<td class="left"><fmt:message key="useradmin.field.pwd.validto" />
							</td>
							<td>
								<c:out value="${admin_UserAdminForm.extPwdValidToStr}" escapeXml="false" />
							</td>
						</tr>
						<tr>
							<td class="left"><fmt:message key="useradmin.field.pwd.lockeddate" />
							</td>
							<td>
								<c:out value="${admin_UserAdminForm.extPwdLockedOnStr}" escapeXml="false" />
							</td>
						</tr>
						<tr>
							<td class="left"><fmt:message key="useradmin.field.pwd.lastreset" />
							</td>
							<td>
								<c:out value="${admin_UserAdminForm.extPwdResetOnStr}" escapeXml="false" />
							</td>
						</tr>
					</tbody>
				</table>
			</div>
			<br/><br/></br>
			<div id="user-pdaaccess-div" style="width:100%">
				<table>
					<tbody>
						<tr>
							<td valign="bottom" colspan="2">
							<p class="subtitle1"><fmt:message key="useradmin.texte.pdaAccess" /></p>
							</td>
						</tr>
						<tr>
							<td class="left"><nobr><fmt:message
								key="useradmin.field.pdaUserNumber" /></nobr></td>
							<td>
								<nobr>
									<c:out value="${admin_UserAdminForm.userEntity.pdaUserNo}"/>
								</nobr>
							</td>
						</tr>
						<tr>
							<td class="left"><nobr><fmt:message
								key="useradmin.field.pickupbarcodepass" /></nobr></td>
							<td>
							<nobr>
							<c:out value="${admin_UserAdminForm.userEntity.pickupBarcodePasscode}"/>
							</nobr>
							</td> 
						</tr>							
						<tr>						
							<td class="left"><nobr><fmt:message
								key="useradmin.field.pdaPinValidToDat" /></nobr></td>
							<td>
								<nobr>
									<c:out value="${admin_UserAdminForm.pdaPinValidToDatStr}"/>&nbsp;
									<c:out value="${admin_UserAdminForm.pdaPinValidToTimeStr}"/>
								</nobr>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
			</td>
			<td valign="bottom" width="300">
			<c:if test="${not empty admin_UserAdminForm.roleList}">
				<div id="DivRoleSelection" style="height: 280px;">
				
				<table cellspacing="0" cellpadding="0" style="width: 100%;" class="noborder">
					<tbody>
						<tr>
							<td valign="bottom" colspan="2">
							<p class="subtitle1"><fmt:message key="useradmin.texte.assigned.roles"/></p>
							</td>
						</tr>
						<tr>
							<td>
							<div style="font-size: 12px; margin-top: 4px; height: 270px;"
								class="subformlist">
							<table cellspacing="0" cellpadding="0" border="0"
								style="width: 100%;" class="noborder">
								<tbody>
									<c:forEach var="entry" varStatus="status"
										items="${admin_UserAdminForm.selectedRoleMap}">
										<tr class='zebra<c:out value="${status.count % 2}"/>'>
											
											<td>
											<label title="<c:out value='${entry.value.roleDescr}'/>"><c:out
												value='${entry.value.roleName}' /></label>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
							</div>
						</tr>
					</tbody>
				</table>
				</div>			
			</c:if>
			</td>
			</tr>
			</table>
			
			
			
		
		<%--------------------%>
		<%-- AUTHORIZATIONS --%>
		<%--------------------%>
		<table class="classicblue" cellpadding="0" cellspacing="0" border="0" width="100%" style="padding: 0px; margin: 0px;">
			<tr>
				<td style="border-top">
					<span class="subtitle1"><fmt:message key="useradmin.texte.authorization"/></span>
				</td>
			</tr>
			
			<tr>
			
			
			<%-- COLUMN 1 --%>
				<td width="15%">
					<div id="DivCompCodeSareaSelection" style="margin: 4px 0px;" class="subform">
						<table cellspacing="0" cellpadding="0" style="width: 100%;" class="noborder">
							<tbody>
								<tr>
									<td><span class="label"><fmt:message key="useradmin.texte.assigned.compcode"/></span>
									<div style="font-size: 12px; margin-top: 4px; height: 120px;" class="subformlist">
									<table cellspacing="0" cellpadding="0" border="0" style="width: 100%;" class="noborder">
										<tbody>
										<c:if test="${empty admin_UserAdminForm.selectedCompCodeSareaCompCodeMap}">
												<tr class='zebra<c:out value="${status.count % 2}"/>'>
													<td>*</td>
												</tr>
											</c:if>
											<c:forEach var="entry" varStatus="status"
												items="${admin_UserAdminForm.selectedCompCodeSareaCompCodeMap}">
												<tr class='zebra<c:out value="${status.count % 2}"/>'>
													<td>
														<c:out value="${entry.value.name}" />
													</td>
													<td valign="middle" style="width: 1px">
														<nobr> 
															<c:if test="${entry.value.canRemove == true}">
																<img src="<crh-common:url value='/images/delete2.png'/>" onClick='useradmin.removeCompCode("<c:out value="${entry.key}"/>")'/>
															</c:if>
														</nobr>
													</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
									</div>
								</tr>
							</tbody>
						</table>
						<table cellspacing="0" cellpadding="0" style="width: 100%;" class="noborder">
							<tbody>
								<tr>
									<td><span class="label"><fmt:message key="useradmin.texte.assigned.sareas"/></span>
									<div style="font-size: 12px; margin-top: 4px; height: 120px;" class="subformlist">
									<table cellspacing="0" cellpadding="0" border="0"
										style="width: 100%;" class="noborder">
										<tbody>
											<c:if test="${empty admin_UserAdminForm.selectedCompCodeSareaSareaMap}">
												<tr class='zebra<c:out value="${status.count % 2}"/>'>
													<td>*</td>
												</tr>
											</c:if>
											<c:forEach var="entry" varStatus="status" items="${admin_UserAdminForm.selectedCompCodeSareaSareaMap}">
												<tr class='zebra<c:out value="${status.count % 2}"/>'>
													<td>
														<c:out value="${entry.value.name}" />
													</td>
													<td valign="middle" style="width: 1px">
														<nobr> 
															<c:if test="${entry.value.canRemove == true}">
																<img src="<crh-common:url value='/images/delete2.png'/>" onClick='useradmin.removeSarea("<c:out value="${entry.key}"/>")'/>
															</c:if>
														</nobr>
													</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
									</div>
								</tr>
							</tbody>
						</table>
					</div>
				</td>
				
				
				<%-- COLUMN 2 --%>
				
				<td width="15%">
					<div id="DivPlantShippingPointSelection" style="margin: 4px 0px;" class="subform">
						<table cellspacing="0" cellpadding="0" style="width: 100%;" class="noborder">
							
							<tbody>
								<tr>
									<td><span class="label"><fmt:message key="useradmin.texte.assigned.plants"/></span>
									<div style="font-size: 12px; margin-top: 4px; height: 120px;"
										class="subformlist">
									<table cellspacing="0" cellpadding="0" border="0"
										style="width: 100%;" class="noborder">
										<colgroup>
											<col width="99%" />
											<col width="1%" />
										</colgroup>
										<tbody>
											<c:if test="${empty admin_UserAdminForm.selectedPlantShippingPointPlantMap}">
												<tr class='zebra<c:out value="${status.count % 2}"/>'>
													<td>*</td>
												</tr>
											</c:if>
											<c:forEach var="entry" varStatus="status"
												items="${admin_UserAdminForm.selectedPlantShippingPointPlantMap}">
												<tr class='zebra<c:out value="${status.count % 2}"/>'>
													<td>
														<c:out value="${entry.value.name}" /> (<c:out value="${entry.value.value}" />)
													</td>
													<td valign="middle" style="width: 1px"><nobr> 
														<c:if test="${entry.value.canRemove == true}">
															<img src="<crh-common:url value='/images/delete2.png'/>" onClick='useradmin.removePlant("<c:out value='${entry.key}'/>");'/> </nobr>
														</c:if>
													</td>
												</tr> 
											</c:forEach>
										</tbody>
									</table>
									</div>									
								</tr>
							</tbody>
						</table>
						
						<table cellspacing="0" cellpadding="0" style="width: 100%;" class="noborder">
							<tbody>
								<tr>
									<td><span class="label"><fmt:message key="useradmin.texte.assigned.shippingpoints"/> </span> <!-- beginn customer/ship-to selection list: div with scrollbars -->
									<div style="font-size: 12px; margin-top: 4px; height: 120px;"
										class="subformlist">
									<table cellspacing="0" cellpadding="0" border="0"
										style="width: 100%;" class="noborder">
										<colgroup>
											<col width="99%" />
											<col width="1%" />
										</colgroup>
										<tbody>
										<c:if test="${empty admin_UserAdminForm.selectedPlantShippingPointShippingPointMap}">
												<tr class='zebra<c:out value="${status.count % 2}"/>'>
													<td>*</td>
												</tr>
											</c:if>
											<c:forEach var="entry" varStatus="status"
												items="${admin_UserAdminForm.selectedPlantShippingPointShippingPointMap}">
												<tr class='zebra<c:out value="${status.count % 2}"/>'>
													<td>
														<c:out value="${entry.value.name}" />
													</td>
														 <td valign="middle" style="width: 1px">
															<nobr> 
													<c:if test="${entry.value.canRemove == true}">
															<img src="<crh-common:url value='/images/delete2.png'/>" onClick='useradmin.removeShippingPoint("<c:out value="${entry.key}"/>");'/>
													</c:if>
															</nobr>
														 </td>
												</tr>
											</c:forEach>
										</tbody> 
									</table>
									</div>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
				</td>
				
				
				<%-- COLUMN 3 --%>
				
				<td width="15%">
					<div id="DivPoolCarrierSelection" style="margin: 4px 0px;" class="subform">
						<table cellspacing="0" cellpadding="0" style="width: 100%;" class="noborder">
							<tbody>
								<tr>
									<td><span class="label"><fmt:message key="useradmin.texte.assigned.pools"/></span> <!-- beginn customer/ship-to selection list: div with scrollbars -->
									<div style="font-size: 12px; margin-top: 4px; height: 120px;" class="subformlist">
									<table cellspacing="0" cellpadding="0" border="0"
										style="width: 100%;" class="noborder">
										<colgroup>
											<col width="99%" />
											<col width="1%" />
										</colgroup>
										<tbody>
										<c:if test="${empty admin_UserAdminForm.selectedPoolMap}">
												<tr class='zebra<c:out value="${status.count % 2}"/>'>
													<td>*</td>
												</tr>
											</c:if>
											<c:forEach var="entry" varStatus="status"
												items="${admin_UserAdminForm.selectedPoolMap}">
												<tr class='zebra<c:out value="${status.count % 2}"/>'>
													<td>
														<c:out value="${entry.value.name}" />
													</td>
													<td valign="middle" style="width: 1px"> 
														<nobr>
														<c:if test="${entry.value.canRemove == true}">
															  <img src="<crh-common:url value='/images/delete2.png'/>" onClick='useradmin.removePool(<c:out value="${entry.key}"/>)'/>
														</c:if>
														</nobr>
													</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
									</div>
									</td>
								</tr>
							</tbody>
						</table>
						
						<table cellspacing="0" cellpadding="0" style="width: 100%;"
							class="noborder">
							
							<tbody>
								<tr>
									<td><span class="label"><fmt:message key="useradmin.texte.assigned.carrier"/></span> <!-- beginn customer/ship-to selection list: div with scrollbars -->
									<div style="font-size: 12px; margin-top: 4px; height: 120px;"
										class="subformlist">
									<table cellspacing="0" cellpadding="0" border="0"
										style="width: 100%;" class="noborder">
										<colgroup>
											<col width="99%" />
											<col width="1%" />
										</colgroup>
										<tbody>
										<c:if test="${empty admin_UserAdminForm.selectedCarrierMap}">
												<tr class='zebra<c:out value="${status.count % 2}"/>'>
													<td>*</td>
												</tr>
											</c:if>
											<c:forEach var="entry" varStatus="status"
												items="${admin_UserAdminForm.selectedCarrierMap}">
												<tr class='zebra<c:out value="${status.count % 2}"/>'>
													<td>
														<c:out value="${entry.value.name}" />
													</td>
													<td valign="middle" style="width: 1px">
														<nobr> 
															<c:if test="${entry.value.canRemove == true}">
																<img src="<crh-common:url value='/images/delete2.png'/>" onClick='useradmin.removeCarrier(<c:out value="${entry.key}"/>)'/>
															</c:if>
													   </nobr>
												   </td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
									</div>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
				</td>
				
				
				<%-- COLUMN 4 --%>
				
				<td width="15%">
					<div id="DivCustomerShipToSelection" style="margin: 4px 0px;" class="subform">
						<table cellspacing="0" cellpadding="0" style="width: 100%;" class="noborder">
							<tbody>
								<tr>
									<td><span class="label"><fmt:message key="useradmin.texte.assigned.customers"/></span> <!-- beginn customer/ship-to selection list: div with scrollbars -->
									<div style="font-size: 12px; margin-top: 4px; height: 120px;"
										class="subformlist">
									<table cellspacing="0" cellpadding="0" border="0"
										style="width: 100%;" class="noborder">
										<colgroup>
											<col width="99%" />
											<col width="1%" />
										</colgroup>
										<tbody>
										<c:if test="${empty admin_UserAdminForm.selectedSoldToShipToSoldToMap}">
												<tr class='zebra<c:out value="${status.count % 2}"/>'>
													<td>*</td>
												</tr>
										</c:if>
											<c:forEach var="entry" varStatus="status"
												items="${admin_UserAdminForm.selectedSoldToShipToSoldToMap}">
												<tr class='zebra<c:out value="${status.count % 2}"/>'>
													<td>
														<c:out value="${entry.value.name}" />
													</td>
													<td valign="middle" style="width: 1px">
														<nobr> 
															<c:if test="${entry.value.canRemove == true}">
																<img src="<crh-common:url value='/images/delete2.png'/>" onClick='useradmin.removeCustomer("<c:out value="${entry.key}"/>")'/>
															</c:if>
														</nobr>
													</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
									</div>
									</td>
								</tr>
							</tbody>
						</table>
						
						<table cellspacing="0" cellpadding="0" style="width: 100%;"
							class="noborder">
							
							<tbody>
								<tr>
									<td><span class="label"><fmt:message key="useradmin.texte.assigned.shipto"/></span> <!-- beginn customer/ship-to selection list: div with scrollbars -->
									<div style="font-size: 12px; margin-top: 4px; height: 120px;"
										class="subformlist">
										
										
									<table cellspacing="0" cellpadding="0" border="0"
										style="width: 100%;" class="noborder">
										<colgroup>
											<col width="99%" />
											<col width="1%" />
										</colgroup>
										<tbody>
										<c:if test="${empty admin_UserAdminForm.selectedSoldToShipToShipToMap}">
												<tr class='zebra<c:out value="${status.count % 2}"/>'>
													<td>*</td>
												</tr>
										</c:if>
											<c:forEach var="entry" varStatus="status"
												items="${admin_UserAdminForm.selectedSoldToShipToShipToMap}">
												<tr class='zebra<c:out value="${status.count % 2}"/>'>
													<td>
														<c:out value="${entry.value.name}" />
													</td>
													<td valign="middle" style="width: 1px">
														<nobr> 
															<c:if test="${entry.value.canRemove == true}">
																<img src="<crh-common:url value='/images/delete2.png'/>" onClick='useradmin.removeShipTo("<c:out value="${entry.key}"/>");'/>
															</c:if>	
														</nobr>
													</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
									</div>
								</tr>
							</tbody>
						</table>
					</div>
				</td>
				
				
				<%-- COLUMN 5 --%>
				
				<td width="15%">
					<div id="DivSgroupPurchOrgSelection" style="margin: 4px 0px;" class="subform">
						<table cellspacing="0" cellpadding="0" style="width: 100%;" class="noborder">
							<tbody>
								<tr>
									<td><span class="label"><fmt:message key="useradmin.texte.assigned.sgroups"/></span>
									<div style="font-size: 12px; margin-top: 4px; height: 120px;"
										class="subformlist">
									<table cellspacing="0" cellpadding="0" border="0"
										style="width: 100%;" class="noborder">
										<colgroup>
											<col width="99%" />
											<col width="1%" />
										</colgroup>
										<tbody>
										<c:if test="${empty admin_UserAdminForm.selectedSgroupMap}">
												<tr class='zebra<c:out value="${status.count % 2}"/>'>
													<td>*</td>
												</tr>
											</c:if>
											<c:forEach var="entry" varStatus="status"
												items="${admin_UserAdminForm.selectedSgroupMap}">
												<tr class='zebra<c:out value="${status.count % 2}"/>'>
													<td>
														<c:out value="${entry.value.name}" />
													</td>
													<td valign="middle" style="width: 1px">
                                                        <nobr>
                                                            <c:if test="${entry.value.canRemove == true}"> 
                                                                <img src="<crh-common:url value='/images/delete2.png'/>" onClick='useradmin.removeSgroup("<c:out value='${entry.key}'/>")'/>
                                                            </c:if>
                                                        </nobr>
                                                    </td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
									</div>
									</td>
								</tr>
							</tbody>
						</table>
						
						<table cellspacing="0" cellpadding="0" style="width: 100%;"
							class="noborder">
							<tbody>
								<tr>
									<td><span class="label"><fmt:message key="useradmin.texte.assigned.purchorg"/></span>
									<div style="font-size: 12px; margin-top: 4px; height: 120px;"
										class="subformlist">
									<table cellspacing="0" cellpadding="0" border="0"
										style="width: 100%;" class="noborder">
										<colgroup>
											<col width="99%" />
											<col width="1%" />
										</colgroup>
										<tbody>
										<c:if test="${empty admin_UserAdminForm.selectedPurchOrgMap}">
												<tr class='zebra<c:out value="${status.count % 2}"/>'>
													<td>*</td>
												</tr>
											</c:if>
											<c:forEach var="entry" varStatus="status"
												items="${admin_UserAdminForm.selectedPurchOrgMap}">
												<tr class='zebra<c:out value="${status.count % 2}"/>'>
													<td>
														<c:out value="${entry.value.name}" />
													</td>
													<td valign="middle" style="width: 1px">
                                                        <nobr>
                                                            <c:if test="${entry.value.canRemove == true}"> 
                                                                <img src="<crh-common:url value='/images/delete2.png'/>" onClick='useradmin.removePurchOrg("<c:out value='${entry.key}'/>")'/>
                                                            </c:if>
                                                        </nobr>
                                                    </td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
									</div>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
				</td>
			</tr>
			
			
			<tr>
				<td colspan="4">
					<pre style="font-family: Arial, Helvetica, sans-serif"><c:out value="${admin_UserAdminForm.authorizationHelpInfoText}" escapeXml="false" /></pre>
				</td>
				<td class="buttons right">	
					<input type="button" id="cancelUser" name="cancelUser" class="ViewButtonStyle" value='<fmt:message key="buttons.cancel"/>' onClick="cancel();" />
					<c:choose>
						<c:when test="${!modifyState}">
							<input type="button"  class="ViewButtonStyle" value='<fmt:message key="buttons.save"/>' onClick="useradmin.submitWithActionToDiv('saveUserAdding', 'admin_UserAdminFormUserAdmin', '')" tabindex="0"/>
						</c:when>
						<c:otherwise>
							<input type="button"  class="ViewButtonStyle" value='<fmt:message key="buttons.save"/>' onClick="useradmin.submitWithActionToDiv('saveUserModification', 'admin_UserAdminFormUserAdmin', '')" tabindex="0"/>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</table>
	</table>
</html:form>
</c:if>