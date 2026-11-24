<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: RFIDAdminTile.jsp,v $ --%>
<%@ page session="true" %>
<%@ include file="../includeTLDs.jsp" %>
<c:if test="false">
    <%-- for preview in MyEclipse --%>
    <link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>

<script type="text/javascript" src="<crh-common:url value='/dwr/interface/RFIDAdminAjaxService.js'/>"></script>

<script type="text/javascript">
function modifyFilter(params){
	addFormElements(params, "admin_RFIDAdminForm");
	var diva = Ext.get('rfidDiv');
	diva.load({
		url: 'RFIDAdmin.do', 
		params: params,
		text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
		scripts: true
	});
}

function submitWithAction(action){
	form = Ext.getDom('admin_RFIDAdminForm');
	Ext.query("input[name=formAction]", form)[0].value = action;
	form.submit();
}

function modifyRFIDTag(rfidtagId){
	form = Ext.getDom('admin_RFIDAdminForm');
	Ext.query("input[name=inputAction]", form)[0].value = 'modify';
	Ext.query("input[name=rfidtagId]", form)[0].value = rfidtagId;
	
	form.submit();
}

Ext.onReady(function(){

	// --- Filters ---
	<jsp:include page="RFIDAdminFiltersInc.jsp"/>
	

    // trigger the data store load
	// --- Result list ---
	<jsp:include page="RFIDAdminSearchResultGridInc.jsp"/>
     		
    searchArea.searchRFIDTags({});
    
    
	Ext.get('buttonSearch').on('click', function() {
		searchArea.searchRFIDTags({
			//TODO get filter values
			rfidIdentity: Ext.get('rfidIdentity').getValue(),
			name: Ext.get('name').getValue(),
			objectLabel: Ext.get('objectLabel').getValue(),
			rfidobjectTypeCd: Ext.get('rfidObjectType').getValue(),
			activeFlag: true
		});	
	});

   
});

</script>
<html:form action="/rfidadmin/RFIDAdmin.do" styleId="admin_RFIDAdminForm">
<html:hidden property="inputAction" value=""/>
<html:hidden property="rfidtagId" value=""/>
 <!-- beginn Border-Table   -->
    <table width="100%" cellpadding="0" cellspacing="0" border="0" style="padding:0px; margin:0px;">
    	<tr>
    		<td>
    			<jsp:include page="../SubTab9Tile.jsp"/> 
				<table class="big_form2">
				<tr>
				<td>
      				<jsp:include page="../Sub3Tab9Tile.jsp"/>
      				</td>
      				</tr>
			    	<tr class="classicblue">
      					<th class="classicblue">
      						<fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.3"/>/ <fmt:message key="tabsubitem.9.3.1"/>
      					</th>
      				</tr>
      				<tr>
      				<td>
      				
      					<table id="searchTable" cellpadding="0" cellspacing="0" class="big_form2" style="margin: 0px; border-collapse: collapse;">
      					
							<tr>
								<td class="border">
									<div id="search">
										<table>
											<tr>
												<td>
														<table cellpadding="0" cellspacing="0">
															<tbody>
															<tr>
																
																<td>
																	<span class="label" style="margin-left: 15px"><nobr><fmt:message key="rfidadmin.header.rfidIdentity"/>:</nobr></span>
																</td>
																<td>
																	<html:text styleId="rfidIdentity" property="rfidIdentityFilter" style="width: 120px"/>						
																</td>
																<td>
																	<span class="label" style="margin-left: 15px"><nobr><fmt:message key="rfidadmin.header.name"/>:</nobr></span>
																</td>
																<td>
																	<html:text styleId="name" property="nameFilter" style="width: 120px"/>						
																</td>
																<td>
																	<span class="label" style="margin-left: 15px"><nobr><fmt:message key="rfidadmin.header.objectLabel"/>:</nobr></span>
																</td>
																<td>
																	<html:text styleId="objectLabel" property="objectLabelFilter" style="width: 120px"/>						
																</td>
																
																<td>
																<span class="label" style="margin-left: 15px"><nobr><fmt:message key="rfidadmin.header.rfidobjectTypeCd"/>:</nobr></span>
																</td>
																<td>
																<html:select styleId="rfidObjectType" property="rfidObjectTypeFilter" style="width:150px;">
																	<html:option value="-1">-</html:option>
																	<c:forEach var="rfidObjectType" varStatus="st" items="${admin_RFIDAdminForm.rfidObjectTypeMap}">
																		<html:option value="${rfidObjectType.key}"><c:out value="${rfidObjectType.value}"/></html:option>
																	</c:forEach> 
																</html:select>
																</td>
															</tr>
															
																
															</tbody>
														</table>
												</td>
												</tr>
												<tr>
												<td class="buttons right">
													<input id="buttonNew" name="new" type="submit" class="ViewButtonStyle" value="New RFID tag" style="margin-top: 3px"/>
													<input id="buttonSearch" name="search" type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.search"/>" style="margin-top: 3px"/>
													<input id="labelCleanup" name="labelCleanup" type="submit" class="ViewButtonStyle" value="<fmt:message key="rfidadmin.buttons.labelcleanup"/>" style="margin-top: 3px"/>
												</td>
											</tr>
										</table>
									</div>
								</td>
							</tr>
							</table>
      				
      				</td>
      				</tr>
					<tr>
						<td>
							<div id="divSearchResults" style="margin-top: 10px"/>
								
<%-- 								<table width="800" class="classicblue" style="margin-top:4px">
			<tr>
				<th>
					<fmt:message key="rfidadmin.header.rfidIdentity"/>
					<br>
				</th>
				<th>
					<fmt:message key="rfidadmin.header.name"/>
					<br>
				</th>
				<th>
					<fmt:message key="rfidadmin.header.objectLabel"/>
					<br>
				</th>
				<th>
					<fmt:message key="rfidadmin.header.rfidobjectTypeCd"/>
					<br>
				</th>
				<th>
					<fmt:message key="rfidadmin.header.activeFlag"/>
				</th>
			</tr>
			<c:set var="zebracount" value="0"/>
			<c:forEach var="rfid" varStatus="status" items="${admin_RFIDAdminForm.rfidList}">
				<c:set var="zebracount" value="${(zebracount + 1)%2}"/>
				<tr class="zebra<c:out value="${zebracount}" />">
					<td>
						<c:out value="${rfid.rfidIdentity}"/>
					</td>
					<td>
						<c:out value="${rfid.name}"/>
					</td>
					<td>
						<c:out value="${rfid.objectLabel}"/>
					</td>
					<td>
						<c:out value="${rfid.rfidObjectType}"/>
					</td>
					<td>
						<c:choose>
	      	    		<c:when test="${rfid.activeFlag == true}">
	      	    			<img width="16" height="16" alt="activated" src="../images/active.png"/>
	      	    		</c:when>
	      	    		<c:otherwise>
	      	    			<img width="16" height="16" alt="activated" src="../images/deactivated.png"/>
	      	    		</c:otherwise>
	      	    		</c:choose>
					</td>
				</tr>
			</c:forEach>
		</table>
					--%>			
								
							</div>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	</html:form>
