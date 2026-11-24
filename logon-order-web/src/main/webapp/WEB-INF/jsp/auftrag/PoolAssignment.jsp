<%@ page session="true" %>

<%@ include file="../includeTLDs.jsp" %>

<script type="text/javascript" src="<crh-common:url value='/dwr/interface/ManualPoolAssignmentAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.grid.CheckColumn.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.valid.ValidationMessages.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.grid.EditableList.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.grid.PagingToolbar.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.data.OriginalRecordStore.js'/>"></script>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>
<script type="text/javascript">
    var defaultDateFormat = '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>';
var texts = {
		orderNumber: '<fmt:message key="fields.order.number"/>', 
		soldtoName: '<fmt:message key="poolassignment.soldto"/>',
		shiptoName: '<fmt:message key="poolassignment.shipto"/>',
		materialTxt: '<fmt:message key="poolassignment.materialTxt"/>',
		deliveryTypeTxt: '<fmt:message key="poolassignment.deliveryTypeTxt"/>',
		shiptoOrt: '<fmt:message key="fields.shipto.ort"/>',
		shippingpointName: '<fmt:message key="dispo.texte.shippingpoint"/>',
		deliveryFrom: '<fmt:message key="poolassignment.deliveryFromDat"/>',
		deliveryTo: '<fmt:message key="poolassignment.deliveryToDat"/>',
		autoPoolAssignmentFlag: '<fmt:message key="dispokarte.texte.autoPool"/>',
		poolName: '<fmt:message key="fields.pool.pool"/>',
		autoCarrierAssignmentFlag: '<fmt:message key="dispokarte.texte.autoCarrier"/>',
		carrierName: '<fmt:message key="fields.carrier.commercial"/>',
		pagerDisplayMsg: '<fmt:message key="extjsgrid.pager.displayMsg"/>',
		pagerEmptyMsg: '<fmt:message key="extjsgrid.pager.emptyMsg"/>',
		listPageChangeTitle: '<fmt:message key="extjsgrid.list.pageChangeTitle"/>',
		listPageChangeMessage: '<fmt:message key="extjsgrid.list.pageChangeMessage"/>',
		listMaskMessage: '<fmt:message key="extjsgrid.list.maskMessage"/>',
		listNoChangesMessage: '<fmt:message key="extjsgrid.list.noChangesMessage"/>',
		listButtonYes: '<fmt:message key="extjsgrid.list.buttonYes"/>',
		listButtonNo: '<fmt:message key="extjsgrid.list.buttonNo"/>',
		listButtonCancel: '<fmt:message key="extjsgrid.list.buttonCancel"/>',
		gridtitle: '<fmt:message key="poolassignment.gridtitle"/>',
		buttonSave: '<fmt:message key="extjsgrid.button.save"/>',
		buttonReset: '<fmt:message key="extjsgrid.button.reset"/>',
		shippingpoint: '<fmt:message key="dispo.texte.shippingpoint"/>'
};	
var orderDetailBaseUrl = '<c:url value="/auftrag/OrderDetailEntry.do"/>';
</script>
  
<script type="text/javascript" src="<crh-common:url value='/js/auftrag/PoolAssignment.js'/>"></script>

<jsp:include page="../SubTab7Tile.jsp"/>   

<!-- Begin table 3 rows -->
<table class="big_form2">
      
      <tr class="classicblue">
      <th class="classicblue"><fmt:message key="tabitem.monitoring"/> / <fmt:message key="tabsubitem.7.2"/></th>
      </tr>
      <tr>
    
      <td>
			<jsp:include page="../Sub2Tab7Tile.jsp"/>   
			<div id="divSearchResults"><img height="1px" width="1px" src="<crh-common:url value='/images/default/s.gif'/>"/></div>
      </td>
      </tr>
</table>
