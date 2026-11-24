<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>

<%@ include file="../includeTLDs.jsp"%>
<%@ page import="com.crh.logon.constants.Constant"%>
<%@ page import="com.crh.logon.admin.web.actions.masterdata.MaterialEnhancementsAction" %>

<body>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.grid.PagingToolbar.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.data.OriginalRecordStore.js'/>"></script>
<script type="text/javascript">

<c:set var="form" scope="page" value="${admin_MaterialEnhancementsForm}" />

var texts = {
	loading: '<fmt:message key="extjs.loading"/>',
	select: '<fmt:message key="extjs.select"/>',
	pagerDisplayMsg: '<fmt:message key="extjsgrid.pager.displayMsg"/>',
	pagerEmptyMsg: '<fmt:message key="materialenhancements.pager.emptyMsg"/>',
	autoadd1PerNOfUom: '<fmt:message key="materialenhancements.grid.autoadd1PerNOfUom"/>',
	autoaddNUom: '<fmt:message key="materialenhancements.grid.autoaddNUom"/>'
};

var matplanttexts = {
	gridtitle: '<fmt:message key="materialenhancements.grid.gridtitle"/>',
	error: '<fmt:message key="materialenhancements.grid.error"/>',
	functions: '<fmt:message key="materialenhancements.grid.functions"/>',
	plantName: '<fmt:message key="materialenhancements.grid.plantName"/>',
	materialDesc: '<fmt:message key="materialenhancements.grid.materialDesc"/>',
	sapMaterialNo: '<fmt:message key="materialenhancements.grid.materialNo"/>',
	brandName: '<fmt:message key="materialenhancements.grid.brandName"/>',
	productLineText1: '<fmt:message key="materialenhancements.grid.productLineText1"/>',
	productLineText2: '<fmt:message key="materialenhancements.grid.productLineText2"/>',
	logo1z1: '<fmt:message key="materialenhancements.grid.logo1z1"/>',
	logo1z2: '<fmt:message key="materialenhancements.grid.logo1z2"/>',
	nationalNorm: '<fmt:message key="materialenhancements.grid.nationalNorm"/>',
	autoaddMaterialDesc: '<fmt:message key="materialenhancements.grid.autoaddMaterialDesc"/>'
};

var matnormtexts = {
	gridtitle: '<fmt:message key="sapmatplantnorm.grid.gridtitle"/>',
	plant: '<fmt:message key="sapmatplantnorm.grid.column.plant"/>',
	material: '<fmt:message key="sapmatplantnorm.grid.column.material"/>',
	materialno: '<fmt:message key="sapmatplantnorm.grid.column.materialno"/>',
	norm: '<fmt:message key="sapmatplantnorm.grid.column.norm"/>',
	language: '<fmt:message key="sapmatplantnorm.grid.column.language"/> (SAP)',
	destCountry: '<fmt:message key="sapmatplantnorm.grid.column.country"/>',
	sorg: '<fmt:message key="sapmatplantnorm.grid.column.sorg"/>',
	logo: '<fmt:message key="sapmatplantnorm.grid.column.logo"/>',
	zcode1: '<fmt:message key="sapmatplantnorm.grid.column.zcode1"/>',
	zcode2: '<fmt:message key="sapmatplantnorm.grid.column.zcode2"/>',
	text1: '<fmt:message key="sapmatplantnorm.grid.column.text1"/>',
	text2: '<fmt:message key="sapmatplantnorm.grid.column.text2"/>',
	text3: '<fmt:message key="sapmatplantnorm.grid.column.text3"/>',
	deleted: '<fmt:message key="sapmatplantnorm.grid.column.deleted"/>'
};

function imgIconEdit() {
	return '<img src="<crh-common:url value='images/silk/pencil.png'/>" />';
}

function imgIconDelete() {
	return '<img src="<crh-common:url value='images/delete2.png'/>" />';
}

function errorRenderer(val, meta, rec) {
	var result = '<div style="float: left; width: 20px;">';
	if (rec.data.updateBlockedByHjid != null && rec.data.updateBlockedByHjid != '') {
		var title = "<bean:message key="materialenhancements.grid.error.popup" arg0="' + rec.data.updateBlockedByHjid  + '"/>";
		result += '<img src="' + crhCommonUrl('./images/silk/error.png') + '" title="' + title + '"/>';
	}
	result += '</div>';
	return result;	
}

function deleteMatPlant(matPlantId) {
	Ext.MessageBox.buttonText.yes = '<fmt:message key="materialenhancements.messagebox.delete"/>';
	Ext.MessageBox.buttonText.no = '<fmt:message key="materialenhancements.messagebox.cancel"/>';

	Ext.MessageBox.confirm('<fmt:message key="materialenhancements.messagebox.confirm"/>',
		'<fmt:message key="materialenhancements.messagebox.text"/>',
		function(btn) {
			if (btn == 'yes') {
				Ext.Ajax.request( {
					url :'<c:url value="MaterialEnhancements.do"/>',
					method: 'POST',
					params: {'formAction': '<%= MaterialEnhancementsAction.FORWARD_DELETE %>',
						'matPlantId': matPlantId},
			        success: function(response) {
						eval(response.responseText);
						Ext.get('buttonSearch').dom.click();

						if (materialStore.lastOptions){
							materialStore.reload(materialStore.lastOptions);
						}
					}
				} );
			}
		} );
}
</script>
<script type="text/javascript" src="<crh-common:url value='/dwr/interface/MaterialEnhancementsAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/masterdata/MaterialEnhancementsTile.js'/>"></script>

<jsp:include page="../SubTab9Tile.jsp"/>

<!-- Begin table 3 rows -->
<table class="big_form2">
      
      <tr class="classicblue">
      <th class="classicblue"><fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.4"/></th>
      </tr>
      <tr>
    
      <td>
            <!-- Begin Submenu -->
<jsp:include page="../Sub4Tab9Tile.jsp"/>

<table class="big_form2" id="searchTable" cellpadding="0" cellspacing="0" border="0"
	style="padding: 0px; margin: 0px; margin-top: 20px;">
	<colgroup>
		<col width="10%">
		<col width="25%">
		<col width="10%">
		<col width="25%">
		<col width="20%">
		<col width="10%">
	</colgroup>
	<tr>
		<td valign="bottom"
			style="white-space: nowrap; border: 0px; padding-right: 4px; vertical-align: middle;"
			class="label"><fmt:message key="materialenhancements.text.plant" />:</td>
		<td style="border: 0px;"><input type="text" name="plantsCombo"
			value="" id="plantsCombo" style="width: 95%"></td>
		<td valign="bottom"
			style="white-space: nowrap; border: 0px; padding-right: 4px; vertical-align: middle;"
			class="label"><fmt:message
			key="materialenhancements.text.material" />:</td>
		<td style="border: 0px;"><input type="text" name="materialsCombo"
			value="" id="materialsCombo" style="width: 95%"></td>
		<td style="border: 0px; vertical-align: middle;"><input type="button" class="ViewButtonStyle"
			value="<fmt:message key="buttons.search"/>"
			id="buttonSearch"></td>
		<td valign="bottom"
			style="white-space: nowrap; border: 0px; padding-right: 4px; vertical-align: middle;"
			class="label"><a href="javascript:showPopup('<c:url value="MaterialEnhancementsDetailEntry.do"/>')"><img
			src="<crh-common:url value='images/add.png'/>" />&nbsp;
			<fmt:message key="materialenhancements.text.creatematplantentry" />
			</a></td>
	</tr>
</table>

<div id="divMaterialEnhancementsSearchResults"></div>

<table class="big_form2" id="searchTable2" cellpadding="0" cellspacing="0" border="0"
	style="padding: 0px; margin: 0px; margin-top: 20px;">
	<colgroup>
		<col width="5%">
		<col width="10%">
		<col width="5%">
		<col width="10%">
		<col width="5%">
		<col width="10%">
		<col width="55%">
	</colgroup>
	<tr>
		<td valign="bottom" style="white-space: nowrap; border: 0px; padding-right: 4px; vertical-align: middle;" class="label">
			<fmt:message key="sapmatplantnorm.text.norm" />:
		</td>
		<td style="border: 0px;">
			<input type="text" name="norm" value="" id="norm" style="width: 95%">
		</td>
		<td valign="bottom" style="white-space: nowrap; border: 0px; padding-right: 4px; vertical-align: middle;" class="label">
			<fmt:message key="sapmatplantnorm.text.language" /> (SAP):
		</td>
		<td style="border: 0px;">
			<select id="lang">
				<option value=""><fmt:message key="sapmatplantnorm.text.selectAll" /></option>
				<c:forEach var="entity" items="${form.sapLanguageMap}"> 
					<option value="<c:out value="${entity.key}" />" ><c:out value="${entity.value}" /> (<c:out value="${entity.key}" />)</option>
				</c:forEach>
			</select>
		</td>
		<td valign="bottom" style="white-space: nowrap; border: 0px; padding-right: 4px; vertical-align: middle;" class="label">
			<fmt:message key="sapmatplantnorm.text.sorg" />:
		</td>
		<td style="border: 0px;">
			<input type="text" name="sorg" value="" id="sorg" style="width: 95%">
		</td>
		<td style="border: 0px; vertical-align: middle;">
			<input type="button" class="ViewButtonStyle" value="<fmt:message key="sapmatplantnorm.button.filter"/>" id="buttonFilter">
		</td>
	</tr>
</table>

<div id="divSapMaterialPlantNormsSearchResults"></div>

</td>
</tr>
</table>
</body>
</html>
