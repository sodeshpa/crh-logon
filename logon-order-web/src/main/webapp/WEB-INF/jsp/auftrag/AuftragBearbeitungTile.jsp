<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: AuftragBearbeitungTile.jsp,v $ --%>

<%@ page session="true" %>
<%@ page import="com.crh.logon.order.web.actions.auftrag.AuftragBearbeitungAction" %>
<%@ page import="com.crh.logon.order.web.forms.auftrag.AuftragBearbeitungForm"%>
<%@ include file="../includeTLDs.jsp" %>
<%@ page import="com.crh.logon.constants.Constant" %>
<%@page import="com.crh.logon.persistence.entities.common.LoadEntity"%>
<%@page import="com.crh.logon.persistence.entities.common.OrderItemEntity"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<c:if test="false">
    <%-- for preview in MyEclipse --%>
    <link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>

<script type="text/javascript">

var shippingHeaders = {
		loadable: '<fmt:message key="auftragbearbeitung.texte.ordercolumn.loadable"/>',
		truckid: '<fmt:message key="auftragbearbeitung.texte.ordercolumn.truckid"/>',
		containerName: '<fmt:message key="auftragbearbeitung.texte.ordercolumn.container"/>',
		truckPlateNo: '<fmt:message key="auftragbearbeitung.texte.ordercolumn.trucknr"/>',
		driverName: '<fmt:message key="auftragbearbeitung.texte.ordercolumn.chaffeur"/>',
		poolName: '<fmt:message key="auftragbearbeitung.texte.ordercolumn.pool"/>',
		commCarrierName: '<fmt:message key="auftragbearbeitung.texte.ordercolumn.transporteur.comm"/>',
		execCarrierName: '<fmt:message key="auftragbearbeitung.texte.ordercolumn.transporteur.exec"/>',
		loadingPlannedStart: '<fmt:message key="auftragbearbeitung.texte.ordercolumn.loading.planned"/>',
		loadingRealStart: '<fmt:message key="auftragbearbeitung.texte.ordercolumn.loading.calculated"/>',
		materialTxt: '<fmt:message key="auftragbearbeitung.texte.ordercolumn.product"/>',
		materialNo: '<fmt:message key="auftragbearbeitung.texte.ordercolumn.product.no"/>',
		productionPlantName: '<fmt:message key="auftragbearbeitung.texte.ordercolumn.productionplant"/>',
		loadingpointName: '<fmt:message key="auftragbearbeitung.texte.ordercolumn.loadingpoint"/>',
		loadOrderId: '<fmt:message key="auftragbearbeitung.texte.ordercolumn.parallel"/>',
		additionalLoadInfo: '<fmt:message key="auftragbearbeitung.texte.ordercolumn.additional.load.info"/>',
		tareWeightT: '<fmt:message key="auftragbearbeitung.texte.ordercolumn.taralkw"/>',
		tareMeasure: '<fmt:message key="auftragbearbeitung.texte.ordercolumn.tarameasure"/>',
		netQty: '<fmt:message key="auftragbearbeitung.texte.ordercolumn.netQty"/>',
		grossWeight: '<fmt:message key="auftragbearbeitung.texte.ordercolumn.bruttomeasure"/>',
		chamberNumber: '<fmt:message key="auftragbearbeitung.texte.ordercolumn.chambernumber"/>',
		orderedQuantity: '<fmt:message key="auftragbearbeitung.texte.ordercolumn.quantity"/>',
		orderNumber: '<fmt:message key="auftragbearbeitung.texte.ordercolumn.ordernr"/>',
		statusTxt: '<fmt:message key="auftragbearbeitung.texte.ordercolumn.orderstatus"/>',
		soldtoBpName: '<fmt:message key="auftragbearbeitung.texte.ordercolumn.customer"/>',
		shiptoBpName: '<fmt:message key="auftragbearbeitung.texte.ordercolumn.shipto"/>'
};
var UOM_PCE = '<%=Constant.UOM_PCE %>';
var UOM_LKW = '<%=Constant.UOM_LKW %>';
var UOM_CONTAINER = '<%=Constant.UOM_CONTAINER %>';
var UOM_MTQ = '<%=Constant.UOM_MTQ %>';
var UOM_MTQ_TXT = '<%=Constant.UOM_MTQ_TXT %>';
var UOM_TONS = '<%=Constant.UOM_TONS %>';

var SAP_MATERIAL_NO_LANDFILL_PLANNING = '<%=Constant.SAP_MATERIAL_NO_LANDFILL_PLANNING%>';
var SAP_MATERIAL_NO_AGG_PLANNING = '<%=Constant.SAP_MATERIAL_NO_AGG_PLANNING%>';

var TARE_MEASURE_MAX_SIZE = '<%=LoadEntity.DBSIZE_TARE_WEIGHT_T %>';
var NET_WEIGHT_MAX_SIZE = '<%=LoadEntity.DBSIZE_NET_QUANTITY %>';
var LOADED_QUANTITY_MAX_SIZE = '<%=OrderItemEntity.DBSIZE_LOADED_QUANTITY %>';
var LOADED_UOM_CD_MAX_SIZE = '<%=OrderItemEntity.DBSIZE_LOADED_UOM_CD %>';

var CHAMBER_TITLE = '<fmt:message key="auftragbearbeitung.texte.chamber"/>';

var LOADABLE_ICON_IMAGE_URL = '<crh-common:url value="/images/active.png"/>';

var GENERATE_PDF_IMAGE_URL = '<crh-common:url value="/images/img_pdf_icon.png"/>';
var GENERATE_PDF_TITLE = '<fmt:message key="auftragbearbeitung.alt.generatepdf"/>';

var RESET_ROW_IMAGE_URL = '<crh-common:url value="/images/deactivated.png"/>';
var RESET_ROW_TITLE = '<fmt:message key="auftragbearbeitung.alt.resetrow"/>';

var SAVE_ROW_IMAGE_URL = '<crh-common:url value="/images/active.png"/>';
var SAVE_ROW_TITLE = '<fmt:message key="auftragbearbeitung.alt.saverow"/>';

var CALENDAR_IMAGE_URL = '<crh-common:url value="/images/calendar.gif"/>';
var CALENDAR_TITLE = '<fmt:message key="auftragbearbeitung.alt.set.loaded.as.planned"/>';

var LOAD_CONTAINER_IMAGE_URL = '<crh-common:url value="/images/img_container_green.png"/>';
var LOAD_CONTAINER_TITLE = '<fmt:message key="auftragbearbeitung.alt.loadcontainer"/>';

var CANCEL_WEIGHT_SYSTEM_IMAGE_URL = '<crh-common:url value="/images/delete.gif"/>';
var CANCEL_WEIGHT_SYSTEM_TITLE = '<fmt:message key="auftragbearbeitung.alt.cancelonweighsystem"/>';

var TRANSMIT_TO_WEIGHT_SYSTEM_IMAGE_URL = '<crh-common:url value="/images/balance.png"/>';
var TRANSMIT_TO_WEIGHT_SYSTEM_TITLE = '<fmt:message key="auftragbearbeitung.button.transmitweighsystem"/>';

var CANCEL_LOADING_IMAGE_URL = '<crh-common:url value="/images/delete2.png"/>';
var CANCEL_LOADING_TITLE = '<fmt:message key="auftragbearbeitung.alt.cancelloading"/>';

var DOWNLOAD_PDF_TITLE = '<fmt:message key="auftragbearbeitung.alt.viewpdf"/>';

var ORDER_NOTES_TITLE = '<fmt:message key="auftragbearbeitung.texte.titlenotes"/>:';

var GENERATE_PDF_PARAMETER = '<%=AuftragBearbeitungAction.ACTION_GENERATE_PDF %>';

var GRID_DISPLAY_MESSAGE = '<fmt:message key="auftragbearbeitung.texte.grid.displaymsg"/>';
var GRID_EMPTY_MESSAGE = '<fmt:message key="auftragbearbeitung.texte.grid.emptymsg"/>';

var MAX_ROW_COUNT = <%=AuftragBearbeitungAction.maxRowNumber %>;

</script>

<script type="text/javascript"
		src="<crh-common:url value="/js/logonPackageHandlingHelper.js"/>"></script>
		
<script type="text/javascript" src="<crh-common:url value='/dwr/interface/OrderAjaxService.js'/>"></script>
<script type="text/javascript">

logonPackageHandlingHelper.init('<%=Constant.PACKAGE_TYPE_BULK_POWDER%>', '<%=Constant.PACKAGE_TYPE_BULK_DUMPED%>', '<%=Constant.PACKAGE_TYPE_BBAG%>', '<%=Constant.PACKAGE_TYPE_SERVICE%>');

var actions = {
	TRANSMIT_TO_WEIGH_SYSTEM: '<%=AuftragBearbeitungAction.ACTION_TRANSMIT_TO_WEIGH_SYSTEM%>',
	SELECT_LOADINGPOINT: '<%=AuftragBearbeitungAction.ACTION_SELECT_LOADINGPOINT%>',
	LOAD_SHIPPINGPOINTS: '<%=AuftragBearbeitungAction.ACTION_LOAD_SHIPPINGPOINTS%>',
	SAVE_MEASURE_DATA: '<%=AuftragBearbeitungAction.ACTION_SAVE_MEASURE_DATA%>',
	SET_LOADED_AS_PLANNED: '<%=AuftragBearbeitungAction.ACTION_SET_LOADED_AS_PLANNED%>',
	SELECT_DATE_INTERVAL: '<%=AuftragBearbeitungAction.ACTION_SELECT_DATE_INTERVAL%>',
	GET_STATISTIC: '<%=AuftragBearbeitungAction.ACTION_GET_STATISTIC%>',
	GET_STATISTIC2: '<%=AuftragBearbeitungAction.ACTION_GET_STATISTIC2%>',
	LOAD_ORDERS: '<%=AuftragBearbeitungAction.ACTION_LOAD_ORDERS%>',
	CHANGE_LOADINGPOINT: '<%=AuftragBearbeitungAction.ACTION_CHANGE_LOADINGPOINT%>',
	GENERATE_PDF: '<%=AuftragBearbeitungAction.ACTION_GENERATE_PDF%>',
	CANCEL_ON_WEIGH_SYSTEM:'<%=AuftragBearbeitungAction.ACTION_CANCEL_ON_WEIGH_SYSTEM%>',
	CHANGE_CONTAINER: '<%=AuftragBearbeitungAction.ACTION_CHANGE_CONTAINER%>',
	ACTION_CANCEL_LOADING: '<%=AuftragBearbeitungAction.ACTION_CANCEL_LOADING%>',
	ACTION_EDIT_LDS_INFORMATION: '<%=AuftragBearbeitungAction.ACTION_EDIT_LDS_INFORMATION%>'
};

var constants = {
		ORDER_ITEM_ID:'<%=AuftragBearbeitungForm.REQUEST_ORDER_ITEM_ID%>'
};

var texts = {
	extjsProcessing: '<fmt:message key="extjs.processing"/>',
	extjsLoading: '<fmt:message key="extjs.loading"/>'
};

Ext.onReady(function() {
	loadShippingPoints();
	initializeOrderList();
});
 
</script>
<crh-common:authorize function="<%= Authorization.SHIPPING_W %>">
<script type="text/javascript" src="<crh-common:url value='/js/auftrag/AuftragBearbeitungTileW.js'/>"></script>
</crh-common:authorize>
<script type="text/javascript" src="<crh-common:url value='/js/auftrag/AuftragBearbeitungTile.js'/>"></script>

		<html:form styleId = "order_auftrag_AuftragBearbeitungForm" action="auftrag/AuftragBearbeitung.do">
<div class="customer_max">
    
    <!-- beginn Border-Table   -->
    <table cellpadding="0" cellspacing="0" border="0" width="100%" style="padding:0px; margin:0px;">
    <tr>
    <td>    		
            <table cellpadding="0" cellspacing="0" border="0" class="big_form2" style="margin:0px; border-collapse:collapse;">

              	<tr>
                  <td class="border">
                  <table width="100%">
                                <colgroup>
                                    <col width="20%">
                                    <col width="20%">
                                    <col width="20%">
                                    <col width="20%">
                                    <col width="20%">
                                </colgroup>
                                <tr><td class="border">
                  <span class="labelblue"><fmt:message key="auftragbearbeitung.texte.werk"/></span><br>
          
					<html:select onchange="loadShippingPoints()" styleId="selectedPlantCd" property="selectedPlantCd" style="margin:0px 0px 10px 0px;  padding:0px; width:100%;">
						<c:forEach var="plantAcc" items="${order_auftrag_AuftragBearbeitungForm.selectablePlants}">
						<html:option value="${plantAcc.value}"><c:out value="${plantAcc.name}"/></html:option>												
						</c:forEach>
					</html:select>
					
					 <div id="shippingPointSelection">
					<jsp:include page="ShippingPointSelector.jsp"/>                                            	
						</div>					                 								                          
					<html:select onchange="refreshStatistic()" styleId="selectedTransportCat" property="selectedTransportCat" style="margin:0px 0px 10px 0px;  padding:0px; width:100%;">
						<html:option value="<%=AuftragBearbeitungForm.TRANSPORT_TYPE_BULK_POWDER %>"><fmt:message key="fields.packagetype.bulkpowder"/></html:option>
						<html:option value="<%=AuftragBearbeitungForm.TRANSPORT_TYPE_BULK_DUMPED %>"><fmt:message key="fields.packagetype.bulkdumped"/></html:option>
						<html:option value="<%=AuftragBearbeitungForm.TRANSPORT_TYPE_BAG %>"><fmt:message key="fields.packagetype.bag"/></html:option>
						<html:option value=""><fmt:message key="fields.packagetype.all"/></html:option>
					</html:select>                    
					<input type="button" onclick="loadOrders()" id="orderListSearchButton" class="ViewButtonStyle" value="<fmt:message key="auftragbearbeitung.texte.search"/>"/>   
                    </td>                    
					<td class="border">
                      <table border="0" cellspacing="0" cellpadding="0" style="padding:0px; margin:0px; width:100%;">
                        <tr>
                          <td>
                        <span class="labelblue"><fmt:message key="auftragbearbeitung.texte.status"/></span>
                          <%--td style="padding:0px; margin:0px;"><span class="labelblue"><fmt:message key="auftragbearbeitung.texte.produkt"/></span></td--%>
                          <div id="statusSelection">
   					<html:select multiple="true" size="4" property="selectedStatus" styleId="selectedStatus" style="margin:0px 0px 10px 0px;  padding:0px; width:100%;<%--width:130px; --%>">
						<c:forEach var="entry" items="${order_auftrag_AuftragBearbeitungForm.selectableStatusList}">
							<html:option value="${entry.orderStatusTxPK.orderStatusBs.orderStatusCd}"><c:out value="${entry.txt}"/></html:option>
						</c:forEach>
					</html:select><br>
                              <input type="button" onclick="selectStatusAll(0)" class="SmallButtonStyle" value=" <fmt:message key="auftragbearbeitung.texte.keine"/> ">&nbsp;&nbsp;<input type="button"  onclick="selectStatusAll(100)" class="SmallButtonStyle" value=" <fmt:message key="auftragbearbeitung.texte.alle"/> ">&nbsp;&nbsp;<input type="button"  onclick="selectStatusAll(<%=Constant.ORDER_STATUS_LOADED %>)" class="SmallButtonStyle" value=" <fmt:message key="auftragbearbeitung.texte.notyetloaded"/> ">
                            </div>  
                          </td>
                          </tr>
                          <tr>
                        </tr>
                      </table>
                      </td>
                    
                  <td class="border">
                  <span class="labelblue"><fmt:message key="auftragbearbeitung.texte.filter"/></span>
                      <table border="0" cellspacing="0" cellpadding="0" style="padding:0px; margin:0px; width:100%;">
                       <colgroup>
                                    <col width="20%">
                                    <col width="80%">
						</colgroup>                                    
					<tr>
						<td colspan="2">		                          
                            <div id="dateSelection">
								<jsp:include page="DateSelector.jsp"/><br>                                            	
							</div>		
                  		</td>
                  	</tr>
                    <tr>
                      	<td>                  
                  			<span class="formtitle"><fmt:message key="auftragbearbeitung.texte.filterstring"/>:</span>
                  		</td>
                  		<td>
                  			<input onkeyup="searchOrderBySearchText()" name="searchtext" id ="searchtext" type="text" class="textfield" value="<c:out value="${order_auftrag_AuftragBearbeitungForm.searchtext}"/>" maxlength="20" title="<fmt:message key="auftragbearbeitung.texte.filterstring.mouseover"/>"/>                  			
                  		</td>
					</tr>
					<tr>
                      	<td>                  
                  			<span class="formtitle"><fmt:message key="auftragbearbeitung.texte.ordernofilter"/>:</span>
                  		</td>
                  		<td>
                  			<input onkeyup="searchOrderByNo()" name="searchOrderNo" id ="searchOrderNo" type="text" class="textfield" value="<c:out value="${order_auftrag_AuftragBearbeitungForm.searchOrderNo}"/>" maxlength="20" title="<fmt:message key="auftragbearbeitung.texte.ordernofilter.mouseover"/>"/>                  			
                  		</td>
					</tr>
                  	</table>
                  </td>
					<td class="border">
						<div id="infoStatistic">
							<input type="button" onclick="enableStatistic1()" class="SmallButtonStyle" value="<fmt:message key="auftragbearbeitung.texte.display.shipping.statistics"/>">                                            	
						</div>
					</td>
					<td class="border">
						<div id="infoStatistic2">
							<input type="button" onclick="enableStatistic2()" class="SmallButtonStyle" value="<fmt:message key="auftragbearbeitung.texte.display.material.statistics"/>">
							<div style="text-align:right;" id="updateJS"></div>
						</div>
					</td>
                </tr>
        </table>
		<div id="divShippingSearchResults" style="margin-top: 2px"/>
  </td>
  </tr>
  </table>
  <!-- End Border Table -->
        <input type="hidden" id="selectedOrderBy" name="selectedOrderBy" value="<c:out value="${order_auftrag_AuftragBearbeitungForm.selectedOrderBy}"/>"/>
        <input type="hidden" id="sortAsc" name="sortAsc" value="<c:out value="${order_auftrag_AuftragBearbeitungForm.sortAsc}"/>"/>
  </html:form>
<%-- END $RCSfile: AuftragBearbeitungTile.jsp,v $ --%>
