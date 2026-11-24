<%@ include file="../includeTLDs.jsp" %>

<script type="text/javascript" src="<crh-common:url value='../js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/dwr/interface/PDAAdminAjaxService.js'/>"></script>

<script type="text/javascript">
	
	function submitWithAction(action){
		form = Ext.getDom('admin_PDAAdminForm');
		Ext.query("input[name=inputAction]", form)[0].value = action;
		form.submit();
	}

	function modifyPDADevice(pdaDeviceId){
		form = Ext.getDom('admin_PDAAdminForm');
		Ext.query("input[name=inputAction]", form)[0].value = 'modify';
		Ext.query("input[name=pdaDeviceId]", form)[0].value = pdaDeviceId;
		
		form.submit();
	}

	Ext.onReady(function(){

		// --- Filters ---
		<jsp:include page="PDAAdminFiltersInc.jsp"/>
		

	    // trigger the data store load
		// --- Result list ---
		<jsp:include page="PDAAdminSearchResultGridInc.jsp"/>
	     		
	    searchArea.searchPDAs({});
	    
	    
		Ext.get('buttonSearch').on('click', function() {
			searchArea.searchPDAs({
				carrierId: carrierCombo.getValue()
			});	
		});

	   
	});

	
</script>
<div class="customer_max">
<html:form action="/pdaadmin/PDAAdmin.do" styleId="admin_PDAAdminForm">
<html:hidden property="inputAction" value=""/>
<html:hidden property="pdaDeviceId" value=""/>
 <!-- beginn Border-Table   -->
    <table cellpadding="0" cellspacing="0" border="0" width="100%" style="padding:0px; margin:0px;">
    	<tr>
    		<td>
    		</td>
    		<td>
    			<jsp:include page="../SubTab10Tile.jsp"/> 
				<table class="big_form2">
			    	<tr class="classicblue">
      					<th class="classicblue">
      						<fmt:message key="tabitem.admin"/> / <fmt:message key="tabsubitem.10.1"/> / <fmt:message key="tabsubitem.10.1.3"/>
      					</th>
      				</tr>
					<tr>
						<td>
							<jsp:include page="../Sub1Tab10Tile.jsp"/>
							<input type="hidden" name="carrierNr" id="carrierNr" value=""/>
							<table id="searchTable" width="100%" class="classicblue" style="margin-top:4px">
							<tr>
								<td>
									<div id="search">
										<table>
											<tr>
												<td>
														<table cellpadding="0" cellspacing="0">
															<tbody>
															<tr>
																<td>
																	<span class="label" style="margin-left: 15px"><nobr>Carrier:</nobr></span>
																</td>
																<td>
																<td>
																<td>
																	<input type="text" value="" id="carrierId" style="width: 120px"/>						
																</td>
															</tr>
															</tbody>
														</table>
												</td>
												<td class="buttons right">
													<input id="buttonNew" type="button" class="ViewButtonStyle" value="<fmt:message key="pdaadmin.buttons.new"/>" style="margin-top: 3px" onClick="submitWithAction('new');"/>
													<input id="buttonSearch" type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.search"/>" style="margin-top: 3px"/>
												</td>
											</tr>
										</table>
									</div>
								</td>
							</tr>
							</table>
				<div id="divSearchResults" style="margin-top: 10px"/>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	</html:form>
		