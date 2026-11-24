<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page import="com.crh.logon.order.web.actions.auftrag.OrderDetailAction"%>
<%@page import="com.crh.logon.persistence.entities.common.OrderItemEntity"%>
<%@page import="com.crh.logon.persistence.entities.common.OrderEntity"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>

<c:set var="orderDetailUrl">
	/../order/auftrag/OrderDetail.do
</c:set>

<!--<html:form action="auftrag/OrderDetail.do"  onsubmit="return false;" >-->
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page"
		value="${order_auftrag_OrderDetailForm}" />
<c:set var="form" scope="page" value="${order_auftrag_OrderDetailForm}" />

<script type="text/javascript">

	function selectExtraCost(extraCost) {
		var waitingTime = document.getElementsByName('servicesExtraCostBean.extraCostWaitingTimeMin')[0].value
		p = {'formAction' :'<%=OrderDetailAction.FORM_ACTION_ADD_EXTRA_COST%>',
			'servicesExtraCostBean.extraCost': extraCost,
			'servicesExtraCostBean.extraCostWaitingTimeMin': waitingTime
		};
		var divp = Ext.get('extraCostDiv');
		//the div only contains the current jsp
		
		divp.load({
			url :'<c:url value="${orderDetailUrl}"/>',
			text: '<fmt:message key="shippingpoint.text.exjsloading"/>',
			scripts: true,
			params: p
		});
	}

	function removeExtraCost(extraCost) {
		var waitingTime = document.getElementsByName('servicesExtraCostBean.extraCostWaitingTimeMin')[0].value
		p = {'formAction' :'<%=OrderDetailAction.FORM_ACTION_REMOVE_EXTRA_COST%>',
			'servicesExtraCostBean.extraCost': extraCost,
			'servicesExtraCostBean.extraCostWaitingTimeMin': waitingTime
		};
		var divp = Ext.get('extraCostDiv');
		//the div only contains the current jsp
		
		divp.load({
			url :'<c:url value="${orderDetailUrl}"/>',
			text: '<fmt:message key="shippingpoint.text.exjsloading"/>',
			scripts: true,
			params: p
		});
	}

	function initTypeAheadDropdown() {
		var productList = <c:out value="${form.servicesExtraCostBean.extraCostsJSON}" escapeXml="false"/>;
		
		var extraCostStore = new Ext.data.SimpleStore({
		    fields: ['sapOrderExtraCostCd','txt'],
		    data: productList
		});

		var extraCostTemplate = new Ext.XTemplate(	    
	    	'<table class="classicblue" style="width:100%"><thead><tr>',
	    	'<th><fmt:message key="orderdetail.extra.cost.code"/></th>',
	    	'<th><fmt:message key="orderdetail.extra.cost.description"/></th>',	    	
	    	'</tr></thead><tbody>',
	        '<tpl for=".">',
		        	'<tr class="x-combo-list-item zebra{[xindex % 2]}">',
		        		'<td>{sapOrderExtraCostCd}</td>',
		        		'<td>{txt}</td>',
			        '</tr>', 
	        '</tpl>',
	        '</tbody></table>',
	        {        			        
	            // XTemplate configuration:
	            compiled: true
	        }	        
	    );

		var extraCostCombo = new Ext.form.ComboBox({
		    id: 'inputExtraCostDummy',
		    applyTo: 'inputExtraCostDummy',
		    fieldLabel: 'Country',
		    //hiddenName: 'ddi_country', //-> <input id="ddi_country" type="hidden" name="ddi_country" value="">
		    emptyText: '<fmt:message key="orderdetail.extra.cost.select"/>', 
		    store: extraCostStore,
		    displayField: 'txt',
		    valueField: 'sapOrderExtraCostCd',
		    selectOnFocus: true,
		    mode: 'local',
		    typeAhead: true,
		    editable: true,
		    triggerAction: 'all',
		    tpl:extraCostTemplate,
		    width: 200,
		    listWidth: 400
		});

		extraCostCombo.on('select', function(){
			selectExtraCost(extraCostCombo.getValue());
		});
	}
	
	    
	Ext.onReady(function() {
		var extraCostFull = <c:out value="${form.servicesExtraCostBean.extraCostFull}"/>;

		if (!extraCostFull) {
			initTypeAheadDropdown();
		}
	});
</script>
<c:set var="isExtraCostFull">
	<c:out value="${form.servicesExtraCostBean.extraCostFull}"/>
</c:set>
<c:set var="isExtraCostNotEmpty">
	<c:out value="${not form.servicesExtraCostBean.extraCostEmpty}"/>
</c:set>

<c:set var="isExtraCostEditable">false</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_S_EC_MOD_BEFORE_SAP %>">
	<c:set var="isExtraCostEditable" value="${form.materialBean.sentToSapIsNullFlag}"/>
</crh-common:authorize>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_S_EC_MOD_AFTER_SAP %>">
	<c:set var="isExtraCostEditable" value="true"/>
</crh-common:authorize>

<span class="labelblue"><fmt:message key="orderdetail.extra.cost.title" /></span><br/>
<div class="subformlist" style="width: 98%; height:270px; margin: 0px;" >
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<colgroup>
			<col width="25%">
			<col width="70%">
			<col width="5%">
		</colgroup>
		<tbody>
			<tr>
				<td>
					<span class="label">
						<fmt:message key="orderdetail.extra.cost.watingtime" />:&nbsp;
					</span>
				</td>
				<c:set var="maxlength">
					<%= OrderEntity.DBSIZE_EXTRA_COST_WAITING_TIME_MIN + "" %>
				</c:set>
				<td colspan="2">
					<html:text styleClass='textfield' style='width: 50px'
						property="servicesExtraCostBean.extraCostWaitingTimeMin" maxlength="${maxlength}" />
				</td>											
			</tr>
			<tr>
				<td colspan="3">
					&nbsp;
				</td>
			</tr>
			<c:if test="${isExtraCostNotEmpty}">
				<tr>
					<td class="formtitle"><fmt:message
						key="orderdetail.extra.cost.code" /></td>
					<td colspan="2" class="formtitle"><fmt:message
						key="orderdetail.extra.cost.description" /></td>
				</tr>
			</c:if>
			<c:forEach var="secbean" varStatus="status"
				items="${form.servicesExtraCostBean.activeExtraCostList}">
				<tr>
					<td>
						<span class="textfielddisabled"> <c:out
							value="${secbean.sapOrderExtraCostCd}" />&nbsp;
						</span>
					</td>
					<td>
						<span class="textfielddisabled"> <c:out
							value="${form.servicesExtraCostBean.extraCostMap[secbean.sapOrderExtraCostCd].txt}" />&nbsp;
						</span>
					</td>
					<td>
						<c:if test="${isExtraCostEditable}">
							<a href="javascript:removeExtraCost('<c:out value="${secbean.sapOrderExtraCostCd}" />')" >
								<img alt="Delete" src="<crh-common:url value='/images/delete2.png'/>"/>
							</a>
						</c:if>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
<c:if test="${isExtraCostFull}">
	<span class="label">
		<fmt:message key="orderdetail.extra.cost.note" />
	</span>		
</c:if>	
<c:if test="${not isExtraCostFull and isExtraCostEditable}">
	<input type="text" id="inputExtraCostDummy" value=""/>
</c:if>

<!--</html:form>-->
