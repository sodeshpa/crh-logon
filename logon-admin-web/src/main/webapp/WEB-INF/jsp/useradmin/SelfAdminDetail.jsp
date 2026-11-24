<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: SelfAdminDetail.jsp,v $ --%>

<%@ include file="../includeTLDs.jsp" %>

<%@page import="com.crh.logon.admin.web.forms.useradmin.UserAdminExtLoginPwdPopupForm"%>

<div class="customer_max" id="user-details-div">
    
    <!-- beginn Border-Table   -->
    <table width="100%">
    <tr>
<!--    <td width="10" height="1">&nbsp;</td>-->
    <td>
   			 <!-- Beginn Benutzersuche -->
		<html:form action="/useradmin/SelfAdmin.do" styleId="admin_UserAdminForm">
		<html:hidden property="inputAction" value=""/>
		<html:hidden property="updateUserStatusValue" value=""/>
        <html:hidden property="userEntity.sendDailyReportsFlag"/>
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
							<td><nobr><html:text style="width:200px;" styleId="userDetailsPanel_phoneNr" property="userEntity.person.phoneNr" /></nobr></td>
						</tr>
						<tr>
							<td class="left"><nobr><fmt:message
								key="useradmin.field.mobileNr" /></nobr></td>
							<td><nobr><html:text style="width:200px;" styleId="userDetailsPanel_mobileNr" property="userEntity.person.mobileNr" /></nobr></td>
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
							<html:select styleId="applicationLang" size="1" style="width:200px;" property="userEntity.applicationLang.langCd">
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
											 property="selectedTimeZoneCd">
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
							<input type="image" src="<crh-common:url value='/images/lock_edit.png'/>" onClick="useradmin.pwdChange(true, <%= UserAdminExtLoginPwdPopupForm.FORMTYPE_EXTPWD %> );return false;"/></td>
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
			<table class="classicblue" cellpadding="0" cellspacing="0" border="0" width="100%" style="padding: 0px; margin: 0px;">
			<tr>
				<td style="border-top">
					<span class="subtitle1"><fmt:message key="useradmin.texte.authorization"/></span>
				</td>
			</tr>
		
		
	<tr>
		<td width="15%">
			<div id="DivCompCodeSareaSelection" style="margin: 4px 0px;" class="subform">
				<table cellspacing="0" cellpadding="0" style="width: 100%;" class="noborder">
					<tbody>
						<tr>
							<td><span class="label"><fmt:message key="useradmin.texte.assigned.compcode"/></span> <!-- beginn companycode selection list: div with scrollbars -->
							<div style="font-size: 12px; margin-top: 4px; height: 120px;" class="subformlist">
							<table cellspacing="0" cellpadding="0" border="0" style="width: 100%;" class="noborder">
								<colgroup>
									<col width="99%" />
									<col width="1%" />
								</colgroup>
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
										</tr>
									</c:forEach>
								</tbody>
							</table>
							</div>
							</td>
						</tr>
					</tbody>
				</table>
			<table cellspacing="0" cellpadding="0" style="width: 100%;" class="noborder">
				<tbody>
					<tr>
						<td><span class="label"><fmt:message key="useradmin.texte.assigned.sareas"/></span>
						<div style="font-size: 12px; margin-top: 4px; height: 120px;"
							class="subformlist">
						<table cellspacing="0" cellpadding="0" border="0"
							style="width: 100%;" class="noborder">
							<tbody>
							<c:if test="${empty admin_UserAdminForm.selectedCompCodeSareaSareaMap}">
									<tr class='zebra<c:out value="${status.count % 2}"/>'>
										<td>*</td>
									</tr>
								</c:if>
								<c:forEach var="entry" varStatus="status"
									items="${admin_UserAdminForm.selectedCompCodeSareaSareaMap}">
									<tr class='zebra<c:out value="${status.count % 2}"/>'>
										<td>
											<c:out value="${entry.value.name}" />
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
												<c:out value="${entry.value.name}" />
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
							<td><span class="label"><fmt:message key="useradmin.texte.assigned.shippingpoints"/></span> <!-- beginn customer/ship-to selection list: div with scrollbars -->
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
			
			
		<td width="15%">
			<div id="DivPoolCarrierSelection" style="margin: 4px 0px;" class="subform">
				<table cellspacing="0" cellpadding="0" style="width: 100%;" class="noborder">
					<tbody>
						<tr>
							<td><span class="label"><fmt:message key="useradmin.texte.assigned.pools"/></span> <!-- beginn customer/ship-to selection list: div with scrollbars -->
							<div style="font-size: 12px; margin-top: 4px; height: 120px;"
								class="subformlist">
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
										</tr>
									</c:forEach>
								</tbody>
							</table>
							</div>
							</td>
						</tr>
					</tbody>
				</table>

				<table cellspacing="0" cellpadding="0" style="width: 100%;" class="noborder">
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
							<td><span class="label"><fmt:message key="useradmin.texte.assigned.purchorg"/></span> <!-- beginn purchasing organisation selection list: div with scrollbars -->
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
	</tr>			

	<tr>
		<td class="buttons right" colspan="5">	
			<input type="submit" id="cancelUserSelf" name="cancelUserSelf" class="ViewButtonStyle" value='<fmt:message key="buttons.cancel"/>' />
			<input type="button" class="ViewButtonStyle" value='<fmt:message key="buttons.save"/>' onClick="useradmin.submitWithActionToDivSelf('saveUserModificationSelf', 'admin_UserAdminForm')"/>
		</td>
	</tr>
</table>
</table>
		
</html:form>
		
		
</td>
</tr>
</table>
</div>

<%-- END $RCSfile: SelfAdminDetail.jsp,v $ --%>
