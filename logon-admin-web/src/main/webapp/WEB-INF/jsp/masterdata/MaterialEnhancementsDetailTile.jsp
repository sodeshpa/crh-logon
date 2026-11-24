<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ include file="../includeTLDs.jsp" %>
<%@ page import="com.crh.logon.constants.Constant" %>
<%@ page import="com.crh.logon.admin.web.actions.masterdata.MaterialEnhancementsDetailAction" %>
<%@ page import="com.crh.logon.admin.web.actions.masterdata.MaterialEnhancementsDetailEntryAction" %>
<%@ page import="com.crh.logon.admin.web.forms.masterdata.MaterialEnhancementsDetailForm" %>
<%@ page import="com.crh.logon.admin.web.beans.MaterialEnhancementsDetailBean" %>
<%@ page import="com.crh.logon.persistence.entities.common.MatPlantEntity" %>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>

<c:set var="form" value="${sessionScope.admin_MaterialEnhancementsDetailForm}"/>
<c:set var="formBean" value="${sessionScope.admin_MaterialEnhancementsDetailForm.materialEnhancementsDetails}"/>

<c:set var="formType"><%= MaterialEnhancementsDetailBean.FORM_TYPE_NEW %></c:set>
<c:set var="matPlantId" value=""/>
<c:if test="${not empty param['materialEnhancementsDetails.matPlantId']}">
	<c:set var="formType"><%= MaterialEnhancementsDetailBean.FORM_TYPE_EXISTING %></c:set>
	<c:set var="matPlantId" value="${param['materialEnhancementsDetails.matPlantId']}"/>
</c:if>

<script type="text/javascript" src="<crh-common:url value='/dwr/interface/MaterialEnhancementsAjaxService.js'/>"></script>
<script type="text/javascript">

Ext.onReady( function() {
	Ext.get('buttonSave').on('click', function() {
		var materialEnhancementsDetailDiv = Ext.get('MaterialEnhancementsDetailDiv');

		<c:if test="${formType == 'new'}">
		var submitParams = {'materialEnhancementsDetails.plantCd': detailPlantsCombo.getValue(),
			'materialEnhancementsDetails.sapMaterialId': detailMaterialsCombo.getValue(),
			'materialEnhancementsDetails.autoaddSapMaterialId': detailAutoaddMaterialsCombo.getValue()};
		</c:if>
		<c:if test="${formType == 'existing'}">	
		var submitParams = {'materialEnhancementsDetails.plantCd': '<c:out value="${formBean.plantCd}" />',
			'materialEnhancementsDetails.sapMaterialId': '<c:out value="${formBean.sapMaterialId}" />',
			'materialEnhancementsDetails.autoaddSapMaterialId': detailAutoaddMaterialsCombo.getValue()};
		</c:if>
		storeValueAsHiddenField('MaterialEnhancementsDetailForm', 'formAction', '<%= MaterialEnhancementsDetailAction.FORWARD_SAVE %>');
		addFormElements(submitParams, "<%= MaterialEnhancementsDetailForm.SESSION_MATERIAL_ENHANCEMENTS_DETAIL_FORM_NAME %>");

		materialEnhancementsDetailDiv.load( {
			url: '<c:url value="MaterialEnhancementsDetail.do"/>',
			params: submitParams,
			text: '<fmt:message key="extjs.loading"/>',
			scripts: true
		} );
	} );

	Ext.get('buttonCancel').on('click', function() {
		hidePopup();
	} );

	function maskDiv(divId) {

		var div = Ext.get(divId);

		if (div) {
			var items = div.select('.ext-el-mask').elements;
			if (items.length > 0 && items[0]) {
				items[0].style = items[0].style + ";background-color:''";
			}
		}

	}

	var win = new Ext.Window({
        modal       : true,
        applyTo		: 'template_chromat_reduction_edit',
        layout		: 'fit',
        shadow      : false,
        frame		: false,
        closable    : false, 
        closeAction	: 'hide'});
    
	Ext.get('link_edit_chromat_reduction_text').on('click', function() {
		win.show();
		maskDiv('MaterialEnhancementsDetailForm');
	});
	Ext.get('button_close_chromat_reduction_edit').on('click', function() {
		win.hide();
	});
    
	var futureValidFromDat;

	var detailAutoaddMaterialStore;
	var detailMaterialRecordType = Ext.data.Record.create( [ {
		name: "display",
		mapping: "display",
		type: "string"
	}, {
		name: "value",
		mapping: "sapMaterialId",
		type: "string"
	} ]);
	detailAutoaddMaterialStore = new Ext.data.Store({
	    proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { MaterialEnhancementsAjaxService.fetchAutoaddMaterialList(skip, count, sort, [ query ], callback) }, true),
		reader: new Ext.data.ListRangeReader({id: 'sapMaterialId', totalProperty: 'totalSize'}, detailMaterialRecordType)
	});
	detailAutoaddMaterialsCombo = new Ext.ux.TwinCombo( {
		id: 'materialEnhancementsDetails.autoaddSapMaterialCombo',
	    applyTo: 'materialEnhancementsDetails.autoaddSapMaterialId',
	    store: detailAutoaddMaterialStore,
	    displayField: 'display',
	    valueField: 'value',
	    pageSize: 20,
	    width: 200,
	    listWidth: 280,
	    typeAhead: false,
	    triggerAction: 'all',
		minChars: 2,
	    selectOnFocus: true   
	} );
	detailAutoaddMaterialStore.load({params: {start: 0, limit: detailAutoaddMaterialsCombo.pageSize, query: '<c:out value="${formBean.autoaddMaterialDescTxt}" />'} });



	var detailAutoaddUomStore;
	var detailUomRecordType = Ext.data.Record.create( [ {
		name: "name",
		mapping: "uomCd",
		type: "string"
	}, {
		name: "value",
		mapping: "uomCd",
		type: "string"
	} ]);
	
	detailAutoaddUomStore = new Ext.data.Store( {
			proxy: new Ext.data.DWRProxy(MaterialEnhancementsAjaxService.fetchUomList),
			reader: new Ext.data.ObjectReader( {
				id: "uomCd"
			}, detailUomRecordType),
			remoteSort: true
	} );
		
	var detailAutoaddUomsCombo = new Ext.form.ComboBox( {
		id: 'materialEnhancementsDetails.autoaddUomCd',
	    applyTo: 'materialEnhancementsDetails.autoaddUomCd',
	    store: detailAutoaddUomStore,
	    displayField: 'name',
	    valueField: 'value',
	    mode: 'local',
	    width: 180,
	    listWidth: 250,
	    typeAhead: false,
	    triggerAction: 'all',
	    selectOnFocus: true,
	    disabled: false
	} );
	detailAutoaddUomStore.load();
	
<c:if test="${formType == 'new'}">
	var detailPlantRecordType;
	var detailPlantStore;
	var detailMaterialStore;
	var detailPlantsCombo;
	var detailMaterialsCombo;
	var firstLoadDetailPlantStore = true;

	function initFormHeader() {
		detailPlantRecordType = Ext.data.Record.create( [ {
			name: "name",
			mapping: "name",
			type: "string"
		}, {
			name: "value",
			mapping: "value",
			type: "string"
		} ]);

		detailPlantStore = new Ext.data.Store( {
			proxy: new Ext.data.DWRProxy(MaterialEnhancementsAjaxService.fetchPlantsList),
			reader: new Ext.data.ObjectReader( {
				id: "value"
			}, detailPlantRecordType),
			remoteSort: true
		} );

		detailPlantStore.load();

		detailMaterialStore = new Ext.data.Store({
		    proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { MaterialEnhancementsAjaxService.fetchNotProducedMaterialsList(skip, count, sort, [ detailPlantsCombo.getValue(), query ], callback) }, true),
			reader: new Ext.data.ListRangeReader({id: 'sapMaterialId', totalProperty: 'totalSize'}, detailMaterialRecordType)
		});

		detailPlantsCombo = new Ext.form.ComboBox( {
			store: detailPlantStore,
			displayField: 'name',
			valueField: 'value',
		    typeAhead: false,
			minChars: 2,
			forceSelection: true,
			triggerAction: 'all',
			emptyText: '',
			selectOnFocus: true,
			applyTo: 'materialEnhancementsDetails.plantName',
			loadingText: '<fmt:message key="extjs.loading"/>'
		} );

		detailMaterialsCombo = new Ext.ux.TwinCombo( {
			id: 'materialEnhancementsDetails.sapMaterialDescTxt',
		    applyTo: 'materialEnhancementsDetails.sapMaterialDescTxt',
		    store: detailMaterialStore,
		    displayField: 'display',
		    valueField: 'value',
		    pageSize: 20,
		    width: 250,
		    listWidth: 250,
		    typeAhead: false,
		    triggerAction: 'all',
		    selectOnFocus: true,
		    disabled: true
		} );

		detailPlantsCombo.on('select', function(record) {
			detailMaterialsCombo.clearValue();
			detailMaterialsCombo.reset();
			detailMaterialStore.reload({params: {start: 0, limit: detailMaterialsCombo.pageSize, query: ""} });
			detailMaterialsCombo.setDisabled(false);

			} );

		detailPlantStore.addListener('load', function() {
			if (firstLoadDetailPlantStore) {
				detailPlantsCombo.setValue('<c:out value="${formBean.plantCd}" />');
				if (detailPlantsCombo.getValue() != "") {
					detailMaterialsCombo.setDisabled(false);
				}
				var receivedData = {
					name: '<c:out value="${formBean.sapMaterialDescTxt}" />',
					value: '<c:out value="${formBean.sapMaterialId}" />'
				};
				var receivedDataRecord = new detailMaterialStore.recordType(receivedData, '<c:out value="${formBean.sapMaterialId}" />');
				detailMaterialStore.insert(0, receivedDataRecord);
				detailMaterialsCombo.preselectValue('<c:out value="${formBean.sapMaterialId}" />', false);
				firstLoadDetailPlantStore = false;
			}
    	} );
	}
	
	initFormHeader();	
</c:if>

var firstLoadDetailAutoaddMaterialStore = true;
detailAutoaddMaterialStore.addListener('load', function() {
	if (firstLoadDetailAutoaddMaterialStore) {
		detailAutoaddMaterialsCombo.setValue('<c:out value="${formBean.autoaddSapMaterialId}" />');
		detailAutoaddMaterialsCombo.preselectValue('<c:out value="${formBean.autoaddSapMaterialId}" />', false);
		firstLoadDetailAutoaddMaterialStore = false;
	}
} );

	function initForm() {
		futureValidFromDat = new Ext.form.DateField( {
			id: 'materialEnhancementsDetails.futureValidFromDat',
	    	format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
	    	altFormats: '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>',
	    	applyTo: 'materialEnhancementsDetails.futureValidFromDat',
	    	value: '<c:out value="${formBean.futureValidFromDat}" />',
	        allowBlank: true,
		    disabled: false,
	    	width: 180
		} );
	}

	initForm();
} );
</script>

<div id="MaterialEnhancementsDetailDiv">
<html:form action="MaterialEnhancementsDetail.do" styleId="MaterialEnhancementsDetailForm">
	<html:hidden property="formAction" value="" />
	<html:hidden property="materialEnhancementsDetails.matPlantId" value="${matPlantId}" />
	
	<!-- begin Border-Table -->
	<table class="big_form2" cellpadding="0" cellspacing="0" border="0"
		style="padding: 0px; margin: 0px; width: auto;">
		<tr><td>
			<table class="classicblue" style="border: none; margin-top: 10px; width: 100%;">
				<tr>
					<th class="classicblue left" style="vertical-align: middle;">
						<fmt:message key="materialenhancements.text.plant" />:
					</th>
					<th class="classicblue left">
						<c:if test="${formType == 'new'}">
							<html:text styleId="materialEnhancementsDetails.plantName" property="materialEnhancementsDetails.plantName"
								style="width: 95%;" />
						</c:if>
						<c:if test="${formType == 'existing'}">	
							<c:out value="${formBean.plantName}" /> (<c:out value="${formBean.plantCd}" />)
						</c:if>
					</th>
					<th class="classicblue left" style="vertical-align: middle;">
						<fmt:message key="materialenhancements.text.material" />:
					</th>
					<th class="classicblue left">
						<c:if test="${formType == 'new'}">
							<html:text styleId="materialEnhancementsDetails.sapMaterialDescTxt" property="materialEnhancementsDetails.sapMaterialDescTxt"
								value="${formBean.sapMaterialDescTxt}" style="width: 95%;" />
						</c:if>
						<c:if test="${formType == 'existing'}">
							<c:out value="${formBean.sapMaterialDescTxt}" /> (<c:out value="${formBean.sapMaterialNo}" />)
						</c:if>
					</th>
				</tr>
			</table>
		</td></tr>
		<tr><td>
			<table class="classic"
				style="border: none; margin-top: 10px; width: 100%;">
				<tr>
					<td class="label left">
						<fmt:message key="materialenhancements.text.brandSalesName" />:
					</td>
					<td>
						<html:text styleId="materialEnhancementsDetails.brandSalesName" property="materialEnhancementsDetails.brandSalesName"
							value="${formBean.brandSalesName}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_BRAND_SALES_NAME) %>" style="width: 180;" />
					</td>
					<td class="label left">
						<fmt:message key="materialenhancements.text.variableProdCostMat" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.variableProdCostMat" property="materialEnhancementsDetails.variableProdCostMat"
							value="${formBean.variableProdCostMat}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_VARIABLE_PROD_COST_MAT + 1) %>" style="width: 180;" />
					</td>
				</tr>
				<tr>
					<td class="label left">
						<fmt:message key="materialenhancements.text.productLineText1" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.normDesc" property="materialEnhancementsDetails.normDesc"
							value="${formBean.normDesc}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_NORM_DESC) %>" style="width: 180;" />
					</td>
					<td class="label left">
						<fmt:message key="materialenhancements.text.futureVariableProdCostMat" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.futureVariableProdCostMat" property="materialEnhancementsDetails.futureVariableProdCostMat"
							value="${formBean.futureVariableProdCostMat}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_FUTURE_VARIABLE_PROD_COST_MAT + 1) %>" style="width: 180;" />
					</td>
				</tr>
				<tr>
					<td class="label left">
						<fmt:message key="materialenhancements.text.productLineText2" />:
					</td>
					<td>
						<html:text styleId="materialEnhancementsDetails.cpdNumber" property="materialEnhancementsDetails.cpdNumber"
							value="${formBean.cpdNumber}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_CPD_NUMBER) %>" style="width: 180;" />
					</td>
					<td class="label left">
						<fmt:message key="materialenhancements.text.futureValidFromDat" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.futureValidFromDat" property="materialEnhancementsDetails.futureValidFromDat"
							value="" />
					</td>
				</tr>
				<tr>
					<td class="label left">
						<fmt:message key="materialenhancements.text.logoGeneric" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.logoGeneric" property="materialEnhancementsDetails.logoGeneric"
							value="${formBean.logoGeneric}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_LOGO_GENERIC) %>" style="width: 180;" />
					</td>
					<td class="label left">
						<fmt:message key="materialenhancements.text.currency" />:
					</td>
					<td>
						<html:select styleId="materialEnhancementsDetails.currency" property="materialEnhancementsDetails.currency"
							styleClass="textfield" style="width: 180;">
							<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">-</html:option>
							<html:optionsCollection name="admin_MaterialEnhancementsDetailForm" property="materialEnhancementsDetails.currencyBsEntityMap" value="key" label="key"/>
						</html:select>
					</td>
				</tr>
				<tr>
					<td class="label left">
						<fmt:message key="materialenhancements.text.bulkDensityKgpm3" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.bulkDensityKgpm3" property="materialEnhancementsDetails.bulkDensityKgpm3"
							value="${formBean.bulkDensityKgpm3}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_BULK_DENSITY_KGPM3) %>" style="width: 180;" />
					</td>
					<td class="label left">
						<fmt:message key="materialenhancements.text.chromatReductionMonths" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.chromatReductionMonths" property="materialEnhancementsDetails.chromatReductionMonths"
							value="${formBean.chromatReductionMonths}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_CHROMAT_REDUCTION_MONTHS) %>" style="width: 180;" />
					</td>
				</tr>
				<tr>
					<td colspan="2">&nbsp;</td>
					<td class="label left">
						<fmt:message key="materialenhancements.text.materialPlantLanguage" />:
					</td>
					<td valign="bottom" style="white-space: nowrap; border: 0px; padding-right: 4px; vertical-align: middle;" class="label">
						<a id="link_edit_chromat_reduction_text" >
							<img src="<crh-common:url value='/images/silk/pencil.png'/>"/>
						</a>
					</td>
				</tr>
				<tr>
					<td class="label left">
						<fmt:message key="materialenhancements.text.autoaddMaterialDescTxt" />:
					</td>
					<td>
						<input id="materialEnhancementsDetails.autoaddSapMaterialId"/>
						</td>
					<td class="label left">
						<fmt:message key="materialenhancements.text.autoadd1PerNOfUom" />:
					</td>
					<td>
						<html:text styleId="materialEnhancementsDetails.autoadd1PerNOfUom" property="materialEnhancementsDetails.autoadd1PerNOfUom"
								value="${formBean.autoadd1PerNOfUom}" style="width: 95%;" />
					</td>
				</tr>
				<tr>
					<td colspan="2">
					</td>
					<td class="label left">
						<fmt:message key="materialenhancements.text.autoaddNUomCd" />:
					</td>
					<td>
						<html:text styleId="materialEnhancementsDetails.autoaddUomCd" property="materialEnhancementsDetails.autoaddNUomCd"
								value="${formBean.autoaddNUomCd}" style="width: 95%;" />
					</td>
				</tr>
				<tr>
					<td colspan="4">&nbsp;</td>
				</tr>
			</table>
		</td></tr>
		<tr><td>
			<table class="classic"
				style="border: none; margin-top: 10px; width: 100%">
				<tr>
					<td>
						<html:checkbox styleId="materialEnhancementsDetails.prioOverSapMatNormFlag" property="materialEnhancementsDetails.prioOverSapMatNormFlag" />
						<c:set target="${formBean}" property="prioOverSapMatNormFlag" value="false"/>
					</td>
					<td class="label left" colspan="5">
						<fmt:message key="materialenhancements.text.prio.flag" />
					</td>
				</tr>
				<tr>
					<td class="label left">
						<fmt:message key="materialenhancements.text.logo1" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.logo1" property="materialEnhancementsDetails.logo1"
							value="${formBean.logo1}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_LOGO_1) %>" style="width: 180;" />
					</td>
					<td class="label left">
						<fmt:message key="materialenhancements.text.logo2" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.logo2" property="materialEnhancementsDetails.logo2"
							value="${formBean.logo2}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_LOGO_2) %>" style="width: 180;" />
					</td>
					<td class="label left">
						<fmt:message key="materialenhancements.text.logo3" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.logo3" property="materialEnhancementsDetails.logo3"
							value="${formBean.logo3}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_LOGO_3) %>" style="width: 180;" />
					</td>
				</tr>
				<tr>
					<td class="label left">
						<fmt:message key="materialenhancements.text.logo1Z1" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.logo1Z1" property="materialEnhancementsDetails.logo1Z1"
							value="${formBean.logo1Z1}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_LOGO_1_Z1) %>" style="width: 180;" />
					</td>
					<td class="label left">
						<fmt:message key="materialenhancements.text.logo2Z1" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.logo2Z1" property="materialEnhancementsDetails.logo2Z1"
							value="${formBean.logo2Z1}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_LOGO_2_Z1) %>" style="width: 180;" />
					</td>
					<td class="label left">
						<fmt:message key="materialenhancements.text.logo3Z1" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.logo3Z1" property="materialEnhancementsDetails.logo3Z1"
							value="${formBean.logo3Z1}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_LOGO_3_Z1) %>" style="width: 180;" />
					</td>
				</tr>
				<tr>
					<td class="label left">
						<fmt:message key="materialenhancements.text.logo1Z2" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.logo1Z2" property="materialEnhancementsDetails.logo1Z2"
							value="${formBean.logo1Z2}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_LOGO_1_Z2) %>" style="width: 180;" />
					</td>
					<td class="label left">
						<fmt:message key="materialenhancements.text.logo2Z2" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.logo2Z2" property="materialEnhancementsDetails.logo2Z2"
							value="${formBean.logo2Z2}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_LOGO_2_Z2) %>" style="width: 180;" />
					</td>
					<td class="label left">
						<fmt:message key="materialenhancements.text.logo3Z2" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.logo3Z2" property="materialEnhancementsDetails.logo3Z2"
							value="${formBean.logo3Z2}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_LOGO_3_Z2) %>" style="width: 180;" />
					</td>
				</tr>
				<tr>
					<td class="label left">
						<fmt:message key="materialenhancements.text.nationalNorm" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.nationalNorm" property="materialEnhancementsDetails.nationalNorm"
							value="${formBean.nationalNorm}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_NATIONAL_NORM) %>" style="width: 180;" />
					</td>
					<td class="label left">
						<fmt:message key="materialenhancements.text.logo2Z3" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.logo2Z3" property="materialEnhancementsDetails.logo2Z3"
							value="${formBean.logo2Z3}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_LOGO_2_Z3) %>" style="width: 180;" />
					</td>
					<td class="label left">
						<fmt:message key="materialenhancements.text.logo3Z3" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.logo3Z3" property="materialEnhancementsDetails.logo3Z3"
							value="${formBean.logo3Z3}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_LOGO_3_Z3) %>" style="width: 180;" />
					</td>
				</tr>
				<tr>
					<td colspan="6">&nbsp;</td>
				</tr>
				<tr>
					<td class="label left">
						<fmt:message key="materialenhancements.text.logo4" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.logo4" property="materialEnhancementsDetails.logo4"
							value="${formBean.logo4}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_LOGO_4) %>" style="width: 180;" />
					</td>
					<td class="label left">
						<fmt:message key="materialenhancements.text.logo5" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.logo5" property="materialEnhancementsDetails.logo5"
							value="${formBean.logo5}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_LOGO_5) %>" style="width: 180;" />
					</td>
					<td class="label left">
						<fmt:message key="materialenhancements.text.logo6" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.logo6" property="materialEnhancementsDetails.logo6"
							value="${formBean.logo6}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_LOGO_6) %>" style="width: 180;" />
					</td>
				</tr>
				<tr>
					<td class="label left">
						<fmt:message key="materialenhancements.text.logo4Z1" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.logo4Z1" property="materialEnhancementsDetails.logo4Z1"
							value="${formBean.logo4Z1}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_LOGO_4_Z1) %>" style="width: 180;" />
					</td>
					<td class="label left">
						<fmt:message key="materialenhancements.text.logo5Z1" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.logo5Z1" property="materialEnhancementsDetails.logo5Z1"
							value="${formBean.logo5Z1}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_LOGO_5_Z1) %>" style="width: 180;" />
					</td>
					<td class="label left">
						<fmt:message key="materialenhancements.text.logo6Z1" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.logo6Z1" property="materialEnhancementsDetails.logo6Z1"
							value="${formBean.logo6Z1}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_LOGO_6_Z1) %>" style="width: 180;" />
					</td>
				</tr>
				<tr>
					<td class="label left">
						<fmt:message key="materialenhancements.text.logo4Z2" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.logo4Z2" property="materialEnhancementsDetails.logo4Z2"
							value="${formBean.logo4Z2}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_LOGO_4_Z2) %>" style="width: 180;" />
					</td>
					<td class="label left">
						<fmt:message key="materialenhancements.text.logo5Z2" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.logo5Z2" property="materialEnhancementsDetails.logo5Z2"
							value="${formBean.logo5Z2}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_LOGO_5_Z2) %>" style="width: 180;" />
					</td>
					<td class="label left">
						<fmt:message key="materialenhancements.text.logo6Z2" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.logo6Z2" property="materialEnhancementsDetails.logo6Z2"
							value="${formBean.logo6Z2}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_LOGO_6_Z2) %>" style="width: 180;" />
					</td>
				</tr>
				<tr>
					<td class="label left">
						<fmt:message key="materialenhancements.text.logo4Z3" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.logo4Z3" property="materialEnhancementsDetails.logo4Z3"
							value="${formBean.logo4Z3}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_LOGO_4_Z3) %>" style="width: 180;" />
					</td>
					<td class="label left">
						<fmt:message key="materialenhancements.text.logo5Z3" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.logo5Z3" property="materialEnhancementsDetails.logo5Z3"
							value="${formBean.logo5Z3}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_LOGO_5_Z3) %>" style="width: 180;" />
					</td>
					<td class="label left">
						<fmt:message key="materialenhancements.text.logo6Z3" />:
					</td>
					<td><html:text styleId="materialEnhancementsDetails.logo6Z3" property="materialEnhancementsDetails.logo6Z3"
							value="${formBean.logo6Z3}" maxlength="<%= String.valueOf(MatPlantEntity.DBSIZE_LOGO_6_Z3) %>" style="width: 180;" />
					</td>
				</tr>
			</table>
		</td></tr>
		<tr><td>
			<img src="<crh-common:url value='images/delivery_note_legend.gif'/>" />
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
	
	<!-- HTML Template for Chromat Reduction Text Popup 
		 See editChromatReductionText() for actual usage and references 
	-->
	<div id="template_chromat_reduction_edit" class="x-hidden">
		<table class="classicblue" style="border: none; width: auto">
			<tr>
				<th ><fmt:message key="materialenhancements.text.materialPlantLanguage" />
				</th>
				<th><fmt:message
						key="materialenhancements.text.chromatReduction" /></th>
				<th ><fmt:message key="materialenhancements.text.materialPlantLanguage" />
				</th>
				<th><fmt:message
						key="materialenhancements.text.chromatReduction" /></th>
				<th ><fmt:message key="materialenhancements.text.materialPlantLanguage" />
				</th>
				<th><fmt:message
						key="materialenhancements.text.chromatReduction" /></th>
				<th ><fmt:message key="materialenhancements.text.materialPlantLanguage" />
				</th>
				<th><fmt:message
						key="materialenhancements.text.chromatReduction" /></th>
			</tr>
			<c:set var="index">0</c:set>
			<c:set var="rowIndex">0</c:set>
			<c:forEach var="entry" items="${formBean.chromatReductionTexts}">
				<c:set var="index" value="${index + 1 }"/>

				<c:if test="${index % 4 == 1}">
					<c:set var="rowIndex" value="${rowIndex + 1 }"/>
					<tr class="zebra<c:out value="${rowIndex % 2}"/>">
				</c:if>
				
				<td><b><c:out value="${entry.key }" /></b></td>
				<td>
					<c:set var="entry_key"><c:out value="${entry.key }" /></c:set>
					<html:textarea rows="3" cols="30"
						property="materialEnhancementsDetails.chromatReductionText(${entry_key })">
						<c:out value="${entry.value }" />
					</html:textarea>
				</td>

				<c:if test="${index % 4 == 0}">
					</tr>
				</c:if>
			</c:forEach>
			<tr>
				<td colspan="8">
					<table style="border: none; margin-top: 10px; width: 100%;">
						<tr>
							<td class="buttons right" >
								<input id="button_close_chromat_reduction_edit"
									type="button" class="ViewButtonStyle"
									value="<fmt:message key="buttons.close"/>">
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</div>
</html:form>
</div>
