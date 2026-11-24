<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.order.web.actions.auftrag.OrderDetailAction"%>
<%@page import="com.crh.logon.persistence.entities.views.BizeventOrderLogEntity"%>
<c:set var="orderDetailUrl">
	/../order/auftrag/OrderDetail.do
</c:set>
<script type="text/javascript">

function showAuditPopup(){

		Ext.Ajax.request({
			url :'<c:url value="${orderDetailUrl}"/>',
			params : {'formAction' :'<%=OrderDetailAction.FORM_ACTION_LOAD_ORDER_AUDIT%>' },
			success: function(response){
				showSecondaryPopupHtml.createDelegate(this,[response.responseText])();
			}
		});
}
Ext.onReady(function(){
	
	var DEFAULT_PAGE_SIZE = 25;

	// --- Data record and store definitions ---
	var orderDetailRecord = Ext.data.Record.create([
		{name: 'compositeId', mapping: 'entity.compositeId', type: 'float'},
		{name: '<%=BizeventOrderLogEntity.ATTR_EVENT_TS%>', mapping: 'eventTs'},
		{name: '<%=BizeventOrderLogEntity.ATTR_BIZEVENT_TYPE_TXT%>', mapping: 'entity.bizeventTypeTxt'},
		{name: 'description', mapping: 'description'},
		{name: '<%=BizeventOrderLogEntity.ATTR_INITIATOR%>', mapping: 'initiator'},
		{name: 'additionalInfo', mapping: 'additionalInfo'},
	]);

    var orderDetailStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(
        	function(skip, count, sort, query, callback) {
        		OrderDetailAjaxService.fetchBizeventList(
        			skip, count, sort, query, callback)
        	}, 
        	true
        ),
		reader: new Ext.data.ListRangeReader(
			{id: 'compositeId', totalProperty:'totalSize'}, 
			orderDetailRecord
		),
		remoteSort: true
    });

    // ------ Toolbars ---------
    var pagingBar = new Ext.ux.grid.PagingToolbar({
    	id:				'orderDetailPager',
		store:			orderDetailStore,
        pageSize:		DEFAULT_PAGE_SIZE,
        displayInfo:	true,
        displayMsg:		'<fmt:message key="creditnote.pager.displayMsg"/>',
        emptyMsg:		'<fmt:message key="creditnote.pager.emptyMsg"/>'
    });

	var cnGrid = new Ext.grid.GridPanel({
		id: 'orderLogGrid',
		el: 'orderLogDiv',
		width: Ext.get('orderLogDiv').getWidth(),
		autoHeight: true,
		viewConfig: {autoFill: true, forceFit: true},
		stripeRows: true,
		enableColumnHide: false,
		title: '<fmt:message key="orderdetail.log.order.log"/>',
		frame: true,
		loadMask: true,
		bbar: pagingBar,
		store: orderDetailStore,
		columns: [
				{header: '<fmt:message key="orderdetail.log.field.date"/>', sortable: true, dataIndex: '<%=BizeventOrderLogEntity.ATTR_EVENT_TS%>'},
				{header: '<fmt:message key="orderdetail.log.field.event"/>', sortable: true, dataIndex: '<%=BizeventOrderLogEntity.ATTR_BIZEVENT_TYPE_TXT%>'},
				{header: '<fmt:message key="orderdetail.log.field.comment"/>', sortable: false, dataIndex: 'description',
						renderer: function(v, p, record){
							return '<span title = "'+ (record.data.description != null ? record.data.description.replace(/<br>/g,'&#13;') : '') +'">' + (record.data.description != null ? record.data.description : '') + '</span>';
						}
				},
				{header: '<fmt:message key="orderdetail.log.field.additionalinfo"/>', sortable: false, dataIndex: 'additionalInfo',
						renderer: function(v, p, record){
							return '<span title = "'+ (record.data.additionalInfo != null ? record.data.additionalInfo.replace(/<br>/g,'&#13;') : '') +'">' + (record.data.additionalInfo != null ? record.data.additionalInfo : '') + '</span>';
						}
				},
				{header: '<fmt:message key="orderdetail.log.field.initiator"/>', sortable: true, dataIndex: '<%=BizeventOrderLogEntity.ATTR_INITIATOR%>'}],
		selModel: new Ext.grid.RowSelectionModel({singleSelect:true})
	});

	cnGrid.render(); // render the grid
	orderDetailStore.load({params:{start: 0, limit: DEFAULT_PAGE_SIZE, sort: '', query: ''}});
});

</script>


<div id="orderLogDiv"><img height="1px" width="1px" src="<crh-common:url value='/images/default/s.gif'/>"/>

</div>
<table  border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
	<td  class="buttons right"><br>
						<input type="button" class="ViewButtonStyle"
							value="<fmt:message key="orderdetail.audit.popup.button"/>"
							onClick="showAuditPopup();">
	</td>
		</tr>
</table>
