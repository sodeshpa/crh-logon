<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>
<%@ include file="../includeTLDs.jsp" %>
<%@ page import="com.crh.logon.constants.Constant" %>
<%@ page import="com.crh.logon.admin.web.forms.masterdata.SalesAreaDetailForm" %>
<%@ page import="com.crh.logon.persistence.entities.common.SareaEntity" %>
<%@ page import="com.crh.logon.persistence.entities.views.SareaDisplayEntity" %>
<%@ page import="com.crh.logon.admin.web.actions.masterdata.SalesAreaDetailAction" %>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>

<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.grid.PagingToolbar.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.data.OriginalRecordStore.js'/>"></script>
<c:set var="displayEntity" value="${admin_SalesAreaDetailForm.displayEntity}"/>

<script>
Ext.onReady( function() {

	var openingTime1From = new Ext.form.TimeField({
		id: 'shiptoOpen1From',
    	increment: 15,
    	format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
    	applyTo: 'shiptoOpen1From',
    	width: 67
	});
	
	var openingTime1To = new Ext.form.TimeField({
		id: 'shiptoOpen1To',
    	increment: 15,
    	format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
    	applyTo: 'shiptoOpen1To',
    	width: 67
	});
	
	var openingTime2From = new Ext.form.TimeField({
		id: 'shiptoOpen2From',
    	increment: 15,
    	format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
    	applyTo: 'shiptoOpen2From',
    	width: 67
	});
	
	var openingTime2To = new Ext.form.TimeField({
		id: 'shiptoOpen2To',
    	increment: 15,
    	format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
    	applyTo: 'shiptoOpen2To',
    	width: 67
	});

	var allowBimodalCreationTill = new Ext.form.TimeField({
		id: 'allowBimodalCreationTillMi',
    	increment: 15,
    	format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
    	applyTo: 'allowBimodalCreationTillMi',
    	width: 67
	});
		
	var allowSdWebOrderUntilMi = new Ext.form.TimeField({
		id: 'allowSdWebOrderUntilMi',
    	increment: 15,
    	format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
    	applyTo: 'allowSdWebOrderUntilMi',
    	width: 67
	});

	var h1EarlyOrderLimit = new Ext.form.TimeField({
		id: 'h1EarlyOrderLimit',
		increment: 15,
		format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
		applyTo: 'h1EarlyOrderLimit',
		width: 67
	});

	var h2EarliestDeliveryFrom = new Ext.form.TimeField({
		id: 'h2EarliestDeliveryFrom',
		increment: 15,
		format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
		applyTo: 'h2EarliestDeliveryFrom',
		width: 67
	});

	var allowNdWebOrderUntilMi = new Ext.form.TimeField({
		id: 'allowNdWebOrderUntilMi',
    	increment: 15,
    	format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
    	applyTo: 'allowNdWebOrderUntilMi',
    	width: 67
	});

	var applySdLeadtimeChkFromMi = new Ext.form.TimeField({
		id: 'applySdLeadtimeChkFromMi',
    	increment: 15,
    	format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
    	applyTo: 'applySdLeadtimeChkFromMi',
    	width: 67
	});
	
	var applyNdLeadtimeChkUntilMi = new Ext.form.TimeField({
		id: 'applyNdLeadtimeChkUntilMi',
    	increment: 15,
    	format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
    	applyTo: 'applyNdLeadtimeChkUntilMi',
    	width: 67
	});
	
	Ext.get('buttonSave').on('click', function() {
		var salesAreaDetailDiv = Ext.get('salesAreaDetailDiv');

		var submitParams = {};

		storeValueAsHiddenField('admin_SalesAreaDetailForm', 'formAction', 'save');
		addFormElements(submitParams, "<%= SalesAreaDetailForm.SESSION_SALES_AREA_DETAIL_FORM_NAME %>");
		
		salesAreaDetailDiv.load( {
			url: '<c:url value="SalesAreaDetail.do"/>',
			params: submitParams,
			text: '<fmt:message key="extjs.loading"/>',
			scripts: true
		} );
	} );

	Ext.get('buttonCancel').on('click', function() {
		hidePopup();
	} );
	
	var tabs = new Ext.TabPanel({
	    renderTo: 'pdf_tabs',
	    activeTab: 0,
	    autoWidth: false,
	    items:[
	        {contentEl:'pdf_tab_general', title:'<fmt:message key="salesarea.text.delivnotetabs.general"/>'},
	        {contentEl:'pdf_tab_cs', title:'<fmt:message key="salesarea.text.delivnotetabs.cashsales"/>'},
	        {contentEl:'pdf_tab_exp_inv', title:'<fmt:message key="salesarea.text.delivnotetabs.exportinvoice"/>'}
	    ],
	    listeners: {
	    	afterlayout: function(tab, layout) {
	    		if (tab.getActiveTab()) {
					tab.getActiveTab().body.setHeight('');
				}
	    	}
	    }
	});
} );


</script>

<div id="salesAreaDetailDiv">
<html:form action="SalesAreaDetail.do" styleId="admin_SalesAreaDetailForm">
	<html:hidden property="formAction" value="" />
	<table class="big_form2" cellpadding="0" cellspacing="0" border="0"
		style="padding: 0px; margin: 0px; width: auto;">
		<tr><td>
			<table class="classicblue" style="border: none; margin-top: 10px; width: 100%;">
				<tr>
					<th class="classicblue left" style="vertical-align: middle;">
						<fmt:message key="salesarea.text.sareaId" />:
					</th>
					<th class="classicblue left">
						<c:out value="${displayEntity.sareaId}" />
					</th>
					<th class="classicblue left" style="vertical-align: middle;">
						<fmt:message key="salesarea.text.sareaDisplayLongname" />:
					</th>
					<th class="classicblue left">
						<c:out value="${displayEntity.sareaDisplayLongname}" />
					</th>
				</tr>
			</table>
		</td></tr>
		<tr><td>
			<table class="classic" style="border: none; margin-top: 10px; width: 100%;">			
				<tr>
					<td class="label left">
						<fmt:message key="salesarea.text.sareaId" />:
					</td>
					<td>
						<c:out value="${displayEntity.sareaId}" />
					</td>
					<td class="label left">
						<fmt:message key="salesarea.text.sorgCd" />:
					</td>
					<td>
						<c:out value="${displayEntity.sorgCd}" />
					</td>
					<td class="label left">
						<fmt:message key="salesarea.text.distributionChannel" />:
					</td>
					<td>
						<c:out value="${displayEntity.distributionChannel}" />
					</td>
					<td class="label left">
						<fmt:message key="salesarea.text.division" />:
					</td>
					<td>
						<c:out value="${displayEntity.divisionCd}" />
					</td>
					<td class="label left">
						<fmt:message key="salesarea.text.sareaDisplayLongname" />:
					</td>
					<td>
						<html:text styleId="sareaDisplayLongname" property="displayEntity.sareaDisplayLongname" />						
					</td>
				</tr>
			</table>
			<table class="classicblue" style="border: none; margin-top: 10px;">
				<tr>
					<th colspan="7" class="classicblue left">
						<fmt:message key="salesarea.text.title.openingHours" />
					</th>
				</tr>
				<tr>
					<td style="padding: 0px 0px 0px 0px;">
						<html:text style="width:65px;" property="shiptoOpen1From" styleId="shiptoOpen1From"/>
					</td>
					<td style="padding: 5px 2px 0px 2px;">
						&nbsp;-&nbsp;
					</td>
					<td style="padding: 2px 2px 0px 2px;">
						<html:text style="width:65px;" property="shiptoOpen1To" styleId="shiptoOpen1To"/>
					</td>
					<td style="padding: 5px 2px 0px 2px;">
						&nbsp;/&nbsp;
					</td>
					<td style="padding: 2px 2px 0px 2px;">
						<html:text style="width:65px;" property="shiptoOpen2From" styleId="shiptoOpen2From"/>
					</td>
					<td style="padding: 5px 2px 0px 2px;">
						&nbsp;-&nbsp;
					</td>
					<td style="padding: 2px 2px 0px 2px;">
						<html:text style="width:65px;" property="shiptoOpen2To" styleId="shiptoOpen2To"/>		
					</td>
				</tr>
			</table>
			<table class="classicblue" style="border: none; margin-top: 10px; width: 100%;">			
				<tr>
					<th colspan="4" class="classicblue left">
						<fmt:message key="salesarea.text.title.webOrderUi" />
					</th>
				</tr>
				<tr>
					<td class="label left">
						<fmt:message key="salesarea.text.customerComment" />
					</td>
					<td>
						<html:checkbox property="displayEntity.custCommFlag" />					
					</td>
				</tr>
			</table>
			<table class="classicblue" style="border: none; margin-top: 10px; width: 100%;">			
				<tr>
					<th colspan="4" class="classicblue left">
						<fmt:message key="salesarea.text.title.deliveryType" />
					</th>
				</tr>
				<tr>
					<td class="label left">
						<fmt:message key="salesarea.text.transferdocSapDelayTime" />:
					</td>
					<td>
						<html:text styleId="transferdocSapDelayTime" property="displayEntity.delaySendToSapMi" />
					</td>
				</tr>
				<tr>
					<td class="label left">
						<fmt:message key="salesarea.text.forceToSAPFlag" />:
					</td>
					<td>
						<html:checkbox styleId="forceToSAPFlag" property="displayEntity.forceToSapFlag" />
					</td>
				</tr>
				<tr>
					<th colspan="4" class="classicblue left">
						<fmt:message key="salesarea.text.title.emailAddresses" />
					</th>
				</tr>
				<tr>
					<td class="label left">
						<fmt:message key="salesarea.text.mailFrom" />:
					</td>
					<td>						
						<html:text styleId="mailFrom" property="displayEntity.mailFrom" />
					</td>
					<td class="label left">
						<fmt:message key="salesarea.text.mailRailadmin" />:
					</td>
					<td>						
						<html:text styleId="mailRailadmin" property="displayEntity.mailRailadmin" />
					</td>
				</tr>
				<tr>
					<td class="label left">
						<fmt:message key="salesarea.text.mailTestTo" />:
					</td>
					<td>						
						<html:text styleId="mailTestTo" property="displayEntity.mailTestTo" />
					</td>
					<td class="label left">
						<fmt:message key="salesarea.text.mailBcc" />:
					</td>
					<td>						
						<html:text styleId="mailBcc" property="displayEntity.mailBcc" />
					</td>
				</tr>
				<!-- pdf settings section-->
				<tr>
					<th colspan="4" class="classicblue left">
						<fmt:message key="salesarea.text.title.pdf" />
					</th>
				</tr>
				<tr>
					<td colspan="4">
						<div id="pdf_tabs" style="width:850px;"></div>
						<div id="pdf_tab_general" class="x-hide-display">
							<table>
								<tr>
									<td colspan="3" class="label left">
										<fmt:message key="salesarea.text.delnoteUsesWsNumberFlag" />:
									</td>
									<td>
										<html:checkbox styleId="delnoteUsesWsNumberFlag"
													   property="displayEntity.delnoteUsesWsNumberFlag"/>
									</td>
								</tr>
								<tr>
									<td colspan="3" class="label left">
										<fmt:message key="salesarea.text.documentDelTimeWindowFlag" />:
									</td>
									<td>
										<html:checkbox styleId="documentDelTimeWindowFlag"
													   property="displayEntity.documentDelTimeWindowFlag"/>
									</td>
								</tr>
								<tr>
									<td colspan="3" class="label left">
										<fmt:message key="salesarea.text.documentDistanceFlag" />:
									</td>
									<td>
										<html:checkbox styleId="documentDistanceFlag"
													   property="displayEntity.documentDistanceFlag"/>
									</td>
								</tr>
								<tr>
									<td colspan="3" class="label left">
										<fmt:message key="salesarea.text.documentCommCarrierFlag" />:
									</td>
									<td>
										<html:checkbox styleId="documentCommCarrierFlag"
													   property="displayEntity.documentCommCarrierFlag"/>
									</td>
								</tr>
								<tr>
									<td colspan="3" class="label left">
										<fmt:message key="salesarea.text.documentExecCarrierFlag" />:
									</td>
									<td>
										<html:checkbox styleId="documentExecCarrierFlag"
													   property="displayEntity.documentExecCarrierFlag"/>
									</td>
								</tr>
								<tr>
									<td colspan="3" class="label left">
										<fmt:message key="salesarea.text.exportinvoice.showZcode12Flag"/>:
									</td>
									<td>
										<html:checkbox styleId="expShowZcode12Flag"
													   property="displayEntity.expShowZcode12Flag"/>
									</td>
								</tr>
								<tr>
									<td colspan="3" class="label left">
										<fmt:message key="salesarea.text.documentShowShiptoContactFlag"/>:
									</td>
									<td>
										<html:checkbox styleId="dnShowShipToContactFlag"
													   property="displayEntity.dnShowShiptoContactFlag"/>
									</td>
								</tr>
							</table>
							<!-- Eastern European settings -->
							<table style="width: 100%;">
								<tr>
									<th colspan="3" class="classicblue left">
										<fmt:message key="salesarea.text.delivnote.easterneurope.specific.settings" />
									</th>
								</tr>
								<tr>
									<td colspan="3">
										<div id="pdf_EE_settings" class="">
											<table>
												<tr>
													<td colspan="3" class="label left">
														<fmt:message key="salesarea.text.delivnote.show.trailer.plate" />:
													</td>
													<td>
														<html:checkbox styleId="dnShowTrailerFlag"
																	   property="displayEntity.dnShowTrailerFlag"/>
													</td>
												</tr>
												<tr>
													<td colspan="3" class="label left">
														<fmt:message key="salesarea.text.delivnote.show.crh.seals.info" />:
													</td>
													<td>
														<html:checkbox styleId="dnShowSealsInfoFlag"
																	   property="displayEntity.dnShowSealsInfoFlag"/>
													</td>
												</tr>
												<tr>
													<td colspan="3" class="label left">
														<fmt:message key="salesarea.text.delivnote.show.silo.number" />:
													</td>
													<td>
														<html:checkbox styleId="dnShowSiloNoFlag"
																	   property="displayEntity.dnShowSiloNoFlag"/>
													</td>
												</tr>
												<tr>
													<td colspan="3" class="label left">
														<fmt:message key="salesarea.text.delivnote.show.plant.cycle.info" />:
													</td>
													<td>
														<html:checkbox styleId="dnShowPlantCycleFlag"
																	   property="displayEntity.dnShowPlantCycleFlag"/>
													</td>
												</tr>
												<tr>
													<td colspan="3" class="label left">
														<fmt:message key="salesarea.text.delivnote.show.fix.text.in.remark" />:
													</td>
													<td>
														<html:checkbox styleId="dnShowFixText"
																	   property="displayEntity.dnShowFixText"/>
													</td>
												</tr>
												<tr>
													<td colspan="3" class="label left">
														<fmt:message key="salesarea.text.delivnote.show.dispatcher.driver.info" />:
													</td>
													<td>
														<html:checkbox styleId="dnShowDispatcherDriverFlag"
																	   property="displayEntity.dnShowDispatcherDriverFlag"/>
													</td>
												</tr>
												<tr>
													<td colspan="3" class="label left">
														<fmt:message key="salesarea.text.delivnote.show.carrier.crm" />:
													</td>
													<td>
														<html:checkbox styleId="dnShowCarrierCrmFlag"
																	   property="displayEntity.dnShowCarrierCrmFlag"/>
													</td>
												</tr>
											</table>
										</div>						
									</td>
								</tr>
								<!-- End of Eastern European settings -->	
							</table>						
						</div>
						<div id="pdf_tab_cs" class="x-hide-display">
							<table>
								<tr>
									<td colspan="3" class="label left">
										<fmt:message key="salesarea.text.cashsalesinvoice.enableOneTimeCustFlag"/>:
									</td>
									<td>
										<html:checkbox styleId="csEnableOneTimeCustFlag"
													   property="displayEntity.csEnableOneTimeCustFlag"/>
									</td>
								</tr>
								<tr>
									<td colspan="3" class="label left">
										<fmt:message key="salesarea.text.cashsalesinvoice.showPaymentMthFlag"/>:
									</td>
									<td>
										<html:checkbox styleId="csShowPaymentMthFlag"
													   property="displayEntity.csShowPaymentMthFlag"/>
									</td>
								</tr>
								<tr>
									<td colspan="3" class="label left">
										<fmt:message key="salesarea.text.cashsalesinvoice.priceType"/>:
									</td>
									<td>
										<html:select styleId="csPriceType" property="displayEntity.csPriceType">
							    			<html:option value="0">
							    				<fmt:message key="salesarea.text.cashsalesinvoice.priceType.standard"/>
						    				</html:option>
							    			<html:option value="1">
							    				<fmt:message key="salesarea.text.cashsalesinvoice.priceType.bulgarian"/>
							    			</html:option>
							    			<html:option value="2">
							    				<fmt:message key="salesarea.text.cashsalesinvoice.priceType.czech"/>
							    			</html:option>
							    		</html:select>
									</td>
								</tr>
								<tr>
									<td colspan="3" class="label left">
										<fmt:message key="salesarea.text.cashsalesinvoice.showTotalsTextFlag"/>:
									</td>
									<td>
										<html:checkbox styleId="csShowTotalsTextFlag"
													   property="displayEntity.csShowTotalsTextFlag"/>
									</td>
								</tr>
								<tr>
									<td colspan="3" class="label left">
										<fmt:message key="salesarea.text.cashsalesinvoice.showCustTaxCodeFlag"/>:
									</td>
									<td>
										<html:checkbox styleId="csShowCustTaxCodeFlag"
													   property="displayEntity.csShowCustTaxCodeFlag"/>
									</td>
								</tr>
								<tr>
									<td colspan="3" class="label left">
										<fmt:message key="salesarea.text.cashsalesinvoice.showContractCurrencyFlag"/>:
									</td>
									<td>
										<html:checkbox styleId="csShowContractCurrencyFlag"
													   property="displayEntity.csShowContractCurrencyFlag"/>
									</td>
								</tr>
							</table>
						</div>
						<div id="pdf_tab_exp_inv" class="x-hide-display">
							<table>
								<tr>
									<td colspan="3" class="label left">
										<fmt:message key="salesarea.text.exportinvoice.showIssueDatLocFlag"/>:
									</td>
									<td>
										<html:checkbox styleId="expShowIssueDatLocFlag"
													   property="displayEntity.expShowIssueDatLocFlag"/>
									</td>
								</tr>
								<tr>
									<td colspan="3" class="label left">
										<fmt:message key="salesarea.text.exportinvoice.showAdInvoiceNoFlag"/>:
									</td>
									<td>
										<html:checkbox styleId="expShowAdInvoiceNoFlag"
													   property="displayEntity.expShowAdInvoiceNoFlag"/>
									</td>
								</tr>
								<tr>
									<td colspan="3" class="label left">
										<fmt:message key="salesarea.text.exportinvoice.matPriceType"/>:
									</td>
									<td>
										<html:select styleId="expMatPriceType" property="displayEntity.expMatPriceType">
							    			<html:option value="0">
							    				<fmt:message key="salesarea.text.exportinvoice.matPriceType.dontDisplay"/>
						    				</html:option>
							    			<html:option value="1">
							    				<fmt:message key="salesarea.text.exportinvoice.matPriceType.display"/>
							    			</html:option>
							    			<html:option value="2">
							    				<fmt:message key="salesarea.text.exportinvoice.matPriceType.displayAndExtraLine"/>
							    			</html:option>
							    		</html:select>
									</td>
								</tr>
								<tr>
									<td colspan="3" class="label left">
										<fmt:message key="salesarea.text.exportinvoice.showTotalTxtFlag"/>:
									</td>
									<td>
										<html:checkbox styleId="expShowTotalTxtFlag"
													   property="displayEntity.expShowTotalTxtFlag"/>
									</td>
								</tr>
								<tr>
									<td colspan="3" class="label left">
										<fmt:message key="salesarea.text.exportinvoice.showLegalText1Flag"/>:
									</td>
									<td>
										<html:checkbox styleId="expShowLegalText1Flag"
													   property="displayEntity.expShowLegalText1Flag"/>
									</td>
								</tr>
								<tr>
									<td colspan="3" class="label left">
										<fmt:message key="salesarea.text.exportinvoice.showLegalText2Flag"/>:
									</td>
									<td>
										<html:checkbox styleId="expShowLegalText2Flag"
													   property="displayEntity.expShowLegalText2Flag"/>
									</td>
								</tr>
								<tr>
									<td colspan="3" class="label left">
										<fmt:message key="salesarea.text.exportinvoice.showCtrlNoFlag"/>:
									</td>
									<td>
										<html:checkbox styleId="expShowCtrlNoFlag"
													   property="displayEntity.expShowCtrlNoFlag"/>
									</td>
								</tr>
							</table>
						</div>
					</td>
				</tr>
				<!-- EOF pdf settings section -->
				<tr>
					<th colspan="4" class="classicblue left">
						<fmt:message key="salesarea.text.title.miscellaneous" />
					</th>
				</tr>
				<tr>
					<td colspan="3" class="label left">
						<fmt:message key="salesarea.text.mailEncoding" />:
					</td>
					<td>
						<c:out value="${displayEntity.mailEncoding}" />
					</td>
				</tr>
				<tr>
					<td colspan="3" class="label left">
						<fmt:message key="salesarea.text.allowWebOrderOnWeekends" />:
					</td>
					<td>	
						<html:select styleId="allowWebOrderOnWeekend" property="displayEntity.allowWebOrderOnWeekend">
			    			<html:option value="0">
			    				<fmt:message key="salesarea.text.allowWebOrderOnWeekends.disabled"/>
		    				</html:option>
			    			<html:option value="1">
			    				<fmt:message key="salesarea.text.allowWebOrderOnWeekends.saturday"/>
			    			</html:option>
			    			<html:option value="2">
			    				<fmt:message key="salesarea.text.allowWebOrderOnWeekends.saturdayandsunday"/>
			    			</html:option>
			    		</html:select>
					</td>
				</tr>
				<tr>
					<td colspan="3" class="label left">
						<fmt:message key="salesarea.text.allowBimodalCreationUntilMin" />:
					</td>
					<td>						
						<html:text style="width:65px;" styleId="allowBimodalCreationTillMi" property="allowBimodalCreationTillMi"
							size="<%= String.valueOf(SareaEntity.DBSIZE_ALLOW_BIMODAL_CREATION_TILL_MI) %>" maxlength="<%= String.valueOf(SareaEntity.DBSIZE_ALLOW_BIMODAL_CREATION_TILL_MI) %>"/>
					</td>
				</tr>
				<tr>
					<td colspan="3" class="label left">
						<fmt:message key="salesarea.text.shortTermOrderFlagThreshold" />:
					</td>
					<td>						
						<html:text styleId="shortTermOrderThresholdMi" property="shortTermOrderThresholdMi"
							size="<%= String.valueOf(SareaEntity.DBSIZE_SHORT_TERM_ORDER_THRESHOLD_MI) %>" maxlength="<%= String.valueOf(SareaEntity.DBSIZE_SHORT_TERM_ORDER_THRESHOLD_MI) %>"/>
					</td>
				</tr>
				<tr>
					<td colspan="3" class="label left">
						<fmt:message key="salesarea.text.exactQuantityOrderTolerance" />:
					</td>
					<td>						
						<html:text styleId="exactQuantityOrderTolerance" property="exactQuantityOrderTolerance"
							size="<%= String.valueOf(SareaEntity.DBSIZE_EXACT_QUANTITY_ORDER_TOLERANCE) %>" maxlength="<%= String.valueOf(SareaEntity.DBSIZE_EXACT_QUANTITY_ORDER_TOLERANCE) %>"/>
					</td>
				</tr>
				<tr>
					<td colspan="3" class="label left">
						<fmt:message key="salesarea.text.allowPlanningMaterialFlag" />:
					</td>
					<td>
						<html:checkbox styleId="allowPlanningMaterialFlag"
								property="displayEntity.allowPlanningMaterialFlag"/>
					</td>
				</tr>
				<tr>
					<td colspan="3" class="label left">
						<fmt:message key="salesarea.text.enablePricingCall" />:
					</td>
					<td>
						<html:checkbox styleId="enablePricingCall"
								property="displayEntity.enablePricingCallFlag"/>
					</td>
				</tr>
				<tr>
					<td colspan="3" class="label left">
						<fmt:message key="salesarea.text.railReturnSareaFlag" />:
					</td>
					<td>
						<html:checkbox styleId="railReturnSareaFlag"
								property="displayEntity.railReturnSareaFlag"/>
					</td>
				</tr>
				<tr>
					<td colspan="3" class="label left">
						<fmt:message key="salesarea.text.enableDemandPlanningFlag" />:
					</td>
					<td>
						<html:checkbox styleId="enableDemandPlanningFlag"
									   property="displayEntity.enableDemandPlanningFlag"/>
					</td>
				</tr>
				<tr>
					<td colspan="3" class="label left">
						<fmt:message key="salesarea.text.demandPlanLatestUntilH" />:
					</td>
					<td>
						<html:text style="width:65px;" styleId="demandPlanLatestUntilH"
									   property="displayEntity.demandPlanLatestUntilH"/>
					</td>
				</tr>
				<!-- lead time check settings START -->
				<tr>
					<th colspan="4" class="classicblue left">
						<fmt:message key="salesarea.text.title.delivery.to.lead.time.check" />
					</th>
				</tr>
				<tr>
					<td colspan="3" class="label left">
						<fmt:message key="salesarea.text.allowSdWebOrderUntilMi" />:
					</td>
					<td>
						<html:text style="width:65px;" property="allowSdWebOrderUntilMi" styleId="allowSdWebOrderUntilMi"/>
					</td>
				</tr>
				<tr>
					<td colspan="3" class="label left">
						<fmt:message key="salesarea.text.allowNdWebOrderUntilMi" />:
					</td>
					<td>
						<html:text style="width:65px;" property="allowNdWebOrderUntilMi" styleId="allowNdWebOrderUntilMi"/>
					</td>
				</tr>
				<tr>
					<td colspan="3" class="label left">
						<fmt:message key="salesarea.text.applySdLeadtimeChkFromMi" />:
					</td>
					<td>
						<html:text style="width:65px;" property="applySdLeadtimeChkFromMi" styleId="applySdLeadtimeChkFromMi"/>
					</td>
				</tr>
				<tr>
					<td colspan="3" class="label left">
						<fmt:message key="salesarea.text.applyNdLeadtimeChkUntilMi" />:
					</td>
					<td>
						<html:text style="width:65px;" property="applyNdLeadtimeChkUntilMi" styleId="applyNdLeadtimeChkUntilMi"/>
					</td>
				</tr>
				<!-- lead time check settings END -->

				<!-- distance lead check settings START -->
				<tr>
					<th colspan="4" class="classicblue left">
						<fmt:message key="salesarea.text.title.delivery.from.lead.time.check" />
					</th>
				</tr>
				<tr>
					<td colspan="3" class="label left">
						<fmt:message key="salesarea.text.l1" />:
					</td>
					<td>
						<html:text styleId="dbltDistanceL1" property="distanceValidationGuiBean.dbltDistanceL1"
								size="<%= String.valueOf(SareaEntity.DBSIZE_DBLT_DISTANCE_L1) %>" maxlength="<%=
								String.valueOf(SareaEntity.DBSIZE_DBLT_DISTANCE_L1) %>"/>
					</td>
				</tr>
				<tr>
					<td colspan="3" class="label left">
						<fmt:message key="salesarea.text.l2" />:
					</td>
					<td>
						<html:text styleId="dbltDistanceL2" property="distanceValidationGuiBean.dbltDistanceL2"
								size="<%= String.valueOf(SareaEntity.DBSIZE_DBLT_DISTANCE_L2) %>" maxlength="<%=
								String.valueOf(SareaEntity.DBSIZE_DBLT_DISTANCE_L2) %>"/>
					</td>
				</tr>
				<tr>
					<td colspan="3" class="label left">
						<fmt:message key="salesarea.text.h1" />:
					</td>
					<td>
						<html:text style="width:65px;" property="distanceValidationGuiBean.h1EarlyOrderLimit"
								styleId="h1EarlyOrderLimit"/>
					</td>
				</tr>
				<tr>
					<td colspan="3" class="label left">
						<fmt:message key="salesarea.text.h2" />:
					</td>
					<td>
						<html:text style="width:65px;" property="distanceValidationGuiBean.h2EarliestDeliveryFrom"
								styleId="h2EarliestDeliveryFrom"/>
					</td>
				</tr>
				<tr>
					<td colspan="4" class="label left">
						<fmt:message key="salesarea.text.offsets" />:
					</td>
				</tr>
				<tr>
					<td colspan="4">
						D1: <html:text styleId="dbltOffsetD1"
							property="distanceValidationGuiBean.dbltOffsetD1"
							size="2" maxlength="2"/>
						D2: <html:text styleId="dbltOffsetD2"
							property="distanceValidationGuiBean.dbltOffsetD2"
							size="2" maxlength="2"/>
						D3: <html:text styleId="dbltOffsetD3"
							property="distanceValidationGuiBean.dbltOffsetD3"
							size="2" maxlength="2"/>
						D4: <html:text styleId="dbltOffsetD4"
							property="distanceValidationGuiBean.dbltOffsetD4"
							size="2" maxlength="2"/>
						D5: <html:text styleId="dbltOffsetD5"
							property="distanceValidationGuiBean.dbltOffsetD5"
							size="2" maxlength="2"/>
						D6: <html:text styleId="dbltOffsetD6"
							property="distanceValidationGuiBean.dbltOffsetD6"
							size="2" maxlength="2"/>
					</td>
				</tr>
				<!-- distance lead check settings END -->
			</table>
		</td></tr>
		<tr><td>
			<table style="border: none; margin-top: 10px; width: 100%;">
				<tr><td class="buttons right">
					<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.save" />" id="buttonSave">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.cancel" />" id="buttonCancel">
				</td></tr>
			</table>
		</td></tr>
	</table>
</html:form>
</div>