<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page
	import="com.crh.logon.persistence.entities.common.OrderItemEntity"%>
<%@page
	import="com.crh.logon.order.web.actions.auftrag.OrderDetailAction"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>


<%@page
	import="com.crh.logon.persistence.entities.common.OrderRepEntity"%>
<%@page import="com.crh.logon.order.web.forms.auftrag.RepTabBean"%>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>
<c:set
        var="orderDetailUrl">
    <crh-common:contextDependentRedirect
            root="order">/auftrag/OrderDetail.do
</c:set>

<script type="text/javascript">

<c:set var="fullAccess">false</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_REPAIR_MAINT_FULL_ACCESS %>">
<c:set var="fullAccess">true</c:set>
</crh-common:authorize>

function clearFileUpload(name, type){
	<%--- Why this is so implemented: setFile of the bean throws exception for empty file input--%>
	fileField = document.getElementsByName(name)[0];
	parentNod = fileField.parentNode;
	tmpForm = document.createElement("form");
	tmpForm.id="replaceThisUploadField" + type;
	tmpForm.name="replaceThisUploadField" + type;
	parentNod.replaceChild(tmpForm,fileField);
	tmpForm.appendChild(fileField);
	tmpForm.reset();
	parentNod.replaceChild(fileField,tmpForm);
	fileField.id="replaceThisUploadField"+type;
	fileField.name="replaceThisUploadField"+type;
}

function removeOrderRepAttachment(id,type){
<c:if test="${!fullAccess && order_auftrag_OrderDetailForm.repBean.documentationAcceptFlag }">	
	if (type == '<%=Constant.ORDER_REP_ATTACHMENT_TYPE_DOCUMENTATION %>') {
		return false;
	}
</c:if> 	
<c:if test="${!fullAccess && order_auftrag_OrderDetailForm.repBean.offerAcceptFlag }">
	if (type == '<%=Constant.ORDER_REP_ATTACHMENT_TYPE_OFFER %>' ) {
		return false;
	}
</c:if> 
<c:if test="${!fullAccess && order_auftrag_OrderDetailForm.repBean.invoicePaidFlag }">	
	if (type == '<%=Constant.ORDER_REP_ATTACHMENT_TYPE_INVOICE %>') {
		return false;
	}
</c:if>		
<c:if test="${!fullAccess}">
	if (type == '<%=Constant.ORDER_REP_ATTACHMENT_TYPE_EMAIL %>') {
		return false;
	}	
</c:if>
	//save with a form
    form = Ext.getDom('OrderDetailForm');
	p = {'formAction' :'<%=OrderDetailAction.FORM_ACTION_REMOVE_REP_ORDER_ATT_FILE%>',
		 'selectedTab' :'<%=OrderDetailAction.FORWARD_REP_MAINT_TAB%>',
		 'uploadBean.orderRepAttId': id,
		 'uploadBean.type':type
		};
	addFormElements(p, form);
	var divName = getAttachmentDivName(type);
		
	var diva = Ext.get(divName);
	diva.load( {
		url :'<c:url value="${orderDetailUrl}"/>',
		params : p,
				text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
				scripts :true
			});
}

function reloadAttachmentDiv(p,type){
	var divName = getAttachmentDivName(type);
	var diva = Ext.get(divName);
	diva.load( {
		url :'<c:url value="${orderDetailUrl}"/>',
		params : p,
				text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
				scripts :true
			});
	return true;
}

function getAttachmentDivName(type) {
	if (type == '<%=Constant.ORDER_REP_ATTACHMENT_TYPE_DOCUMENTATION %>') {
		divName="documentationAttachments";
	}
	else if (type == '<%=Constant.ORDER_REP_ATTACHMENT_TYPE_OFFER %>' ) {
		divName="offerAttachments";
	}
	else if (type == '<%=Constant.ORDER_REP_ATTACHMENT_TYPE_INVOICE %>') {
		divName="invoiceAttachments";
	}
	else if (type == '<%=Constant.ORDER_REP_ATTACHMENT_TYPE_EMAIL %>') {
		divName="emailAttachments";
	}
	return divName;
}

function upload(type) {
	var form = Ext.getDom('OrderDetailForm');
	fileField = document.getElementById('replaceThisUploadField'+type);
	fileField.name='uploadBean.file';
	fileField.id='uploadBean.file';
	
	Ext.Ajax.request({
		url :'<c:url value="${orderDetailUrl}"/>',
		form: 'OrderDetailForm',
		method: 'POST',
		isUpload: true,
		 params:{type : type, 'uploadBean.type' : type, 'formAction' :'<%=OrderDetailAction.FORM_ACTION_UPLOAD_REP_ORDER_ATT_FILE%>'},
        success: function(result, request) {
		    clearFileUpload('uploadBean.file', type);
			p = {'formAction' :'<%=OrderDetailAction.FORM_ACTION_REP_TAB_RELOAD_ATTACHMENT_DIV%>',type: type};
			reloadAttachmentDiv(p,type);
		}
	});

}

Ext.onReady(function(){
	
	//------- Offer accept date -------//
	var offerAcceptDat = new Ext.form.DateField({
		id: 'orderDetailOfferAcceptDat',
    	format: '<%=DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale())%>',
    	altFormats: '<%=DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale())%>',
    	applyTo: 'offerAcceptDate',
    	<c:if test="${!fullAccess && order_auftrag_OrderDetailForm.repBean.offerAcceptFlag }">
    	disabled:true,
    	</c:if>    	
    	width: 100
	});

	var offerAcceptTime = new Ext.form.TimeField({
		id: 'orderDetailOfferAcceptTime',
		// TODO (valdas Feb 19, 2010): do we need to add it?
    	//minValue: '<c:out value="${cust_WebBestellungForm.dummyFields.delTimeMin}"/>',
	    //maxValue: '<c:out value="${cust_WebBestellungForm.dummyFields.delTimeMax}"/>',
    	increment: 15,
    	format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
    	applyTo: 'offerAcceptTime',
    	<c:if test="${!fullAccess && order_auftrag_OrderDetailForm.repBean.offerAcceptFlag }">
    	disabled:true,
    	</c:if>    	
    	width: 70
    });
	
	//------- Invoice date -------//
	var invoiceDat = new Ext.form.DateField({
		id: 'orderDetailInvoiceDat',
    	format: '<%=DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale())%>',
    	altFormats: '<%=DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale())%>',
		applyTo :'invoiceDate',
		<c:if test="${!fullAccess && order_auftrag_OrderDetailForm.repBean.invoicePaidFlag }">
		disabled:true,
		</c:if>
		width :100
	});

	var invoiceTime = new Ext.form.TimeField( {
		id :'orderDetailInvoiceTime',
		// TODO (valdas Feb 19, 2010): do we need to add it?
		//minValue: '<c:out value="${cust_WebBestellungForm.dummyFields.delTimeMin}"/>',
		//maxValue: '<c:out value="${cust_WebBestellungForm.dummyFields.delTimeMax}"/>',
		increment :15,
		format :'<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
		applyTo :'invoiceTime',
		<c:if test="${!fullAccess && order_auftrag_OrderDetailForm.repBean.invoicePaidFlag }">
		disabled:true,
		</c:if>
		width :70
	});

	//------- Enter plant date -------//
	var enterPlantDat = new Ext.form.DateField({
		id: 'orderDetailEnterPlantDat',
    	format: '<%=DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale())%>',
    	altFormats: '<%=DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale())%>',
		applyTo :'enterPlantDate',
		<c:if test="${!fullAccess && order_auftrag_OrderDetailForm.repBean.documentationAcceptFlag }">
		disabled:true,
		</c:if>				
		width :100
	});

	var enterPlantTime = new Ext.form.TimeField( {
		id :'orderDetailEnterPlantTime',
		increment :15,
		format :'<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
		applyTo :'enterPlantTime',
		<c:if test="${!fullAccess && order_auftrag_OrderDetailForm.repBean.documentationAcceptFlag }">
		disabled:true,
		</c:if>			
		width :70
	});

	//------- Leave plant date -------//
	var leavePlantDat = new Ext.form.DateField({
		id: 'orderDetailLeavePlantDat',
    	format: '<%=DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale())%>',
    	altFormats: '<%=DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale())%>',
		applyTo :'leavePlantDate',
		<c:if test="${!fullAccess && order_auftrag_OrderDetailForm.repBean.documentationAcceptFlag }">
		disabled:true,
		</c:if>			
		width :100
	});

	var leavePlantTime = new Ext.form.TimeField( {
		id :'orderDetailLeavePlantTime',
		increment :15,
		format :'<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
		applyTo :'leavePlantTime',
		<c:if test="${!fullAccess && order_auftrag_OrderDetailForm.repBean.documentationAcceptFlag }">
		disabled:true,
		</c:if>			
		width :70
	});	


		// --- Common properties for all filter-comboboxes ---
		Ext.form.ComboBox.prototype.shadow = false;
		Ext.form.ComboBox.prototype.emptyText = "Select...";
		Ext.form.ComboBox.prototype.minChars = 1;
		Ext.form.ComboBox.prototype.allowBlank = true;
		Ext.form.ComboBox.prototype.forceSelection = true;
		Ext.menu.Menu.prototype.shadow = false;

		
		<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_REPAIR_MAINT_FULL_ACCESS %>">
		
		// --- Data record and DataStore definitions ---
		var damageCodeRec = Ext.data.Record.create([
		    {name: "damagecodeCd", mapping: "damagecodeCd"},
		    {name: "txt", mapping: "txt"},
		    {name: "damagecodeTxt", mapping: "damagecodeTxt"}
		]);

		var template = new Ext.XTemplate(
				'<tpl for="."><div class="x-combo-list-item">{damagecodeTxt} <span class="dynlabel">({txt})</span></div></tpl>',
		{
		    compiled: true,
		    disableFormats: true
		}
		);
		
	    var damageCodeTagStore = new Ext.data.Store({
	        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { OrderDetailAjaxService.fetchDamageCodeList(skip, count, sort, query, callback) }, true),
			reader: new Ext.data.ListRangeReader({id:'damagecodeCd', totalProperty:'totalSize'}, damageCodeRec)
	    });

		// --- damageCode Tag Comboboxes
		damageCodeTagCombo = new Ext.ux.TwinCombo({
	    	id: 'repBean.damageCode',
	        applyTo: 'damageCodeDummy',
	        store:  damageCodeTagStore,
	        displayField: 'txt',
	        valueField: 'damagecodeCd',
	        pageSize: 20,
	        width: 220,
	        listWidth: 330,
	        typeAhead: false,
	        triggerAction: 'all' ,
	        selectOnFocus: true,
	        tpl:template
		});

		damageCodeTagCombo.on('select', function(combo, record) {
			var buttSave = document.getElementById("addDamageCdButton");
	    	buttSave.disabled = false;
		});
		</crh-common:authorize>
});

<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_REPAIR_MAINT_FULL_ACCESS %>">
function addDamageCd() {
		var damageCd = damageCodeTagCombo.getValue();
		p = {'formAction' :'<%=OrderDetailAction.FORM_ACTION_ADD_DAMAGECODE%>',
				 'selectedTab' :'<%=OrderDetailAction.FORWARD_REP_MAINT_TAB%>',
				 'repBean.damageCode': damageCd
		};
		form = Ext.getDom('OrderDetailForm');	
		var divp = Ext.get('damageCodeDiv');
		divp.load({
			url :'<c:url value="${orderDetailUrl}"/>',
			text: '<fmt:message key="shippingpoint.text.exjsloading"/>',
			scripts: true,
			params: p,
			callback:function(){
				damageCodeTagCombo.clearValue();			
				var buttSave = document.getElementById("addDamageCdButton");
		    	buttSave.disabled = true;
				}
		});
}

function removeDamageCd(damageCd) {
	p = {'formAction' :'<%=OrderDetailAction.FORM_ACTION_REMOVE_DAMAGECODE%>',
			 'selectedTab' :'<%=OrderDetailAction.FORWARD_REP_MAINT_TAB%>',
			 'repBean.damageCode': damageCd
	};
	form = Ext.getDom('OrderDetailForm');	
	var divp = Ext.get('damageCodeDiv');
	divp.load({
		url :'<c:url value="${orderDetailUrl}"/>',
		text: '<fmt:message key="shippingpoint.text.exjsloading"/>',
		scripts: true,
		params: p
	});
}

function sendEmail() {

	var aParams = getAuthParams();
    form = Ext.getDom('OrderDetailForm');
	 p = {'formAction' :'<%=OrderDetailAction.FORWARD_SEND_EMAIL%>',"authParams":aParams};
	 addFormElements(p, form);
	 
	var diva = Ext.get('saveDiv');
	diva.load( {
		url :'<c:url value="${orderDetailUrl}"/>',
		params : p,
		text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
		scripts :true
	});
			
}

</crh-common:authorize>

</script>

<!--<html:form action="auftrag/OrderDetail.do"   method="post" enctype="multipart/form-data" onsubmit="return false;" >-->
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page"
	value="${order_auftrag_OrderDetailForm}" />
		
<%----------------------------------------------------------------------------------------------------------%>
<%----------------------------------------EMAIL DIV----------------------------------------------------------------%>
<%-------------------------------------------------------------------------------------------------------------%>	
<div class="subform" style="width:98%;margin:4px;padding:0px;">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">

				<tr>
				<td>


<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<colgroup>
		<col width="50%">
		<col width="50%">
	</colgroup>
	<tbody>
	<tr>
		<td rowspan="3">
<div class="subform" style="width:98%"">
<span class="labelblue"><fmt:message
				key="orderdetail.repair.label.email.title" /> </span>
		<table class="classic" cellpadding="0" cellspacing="0">
			<colgroup>
				<col width="30%">
				<col width="70%">
			</colgroup>
			<tbody>
				<tr>
					<td><span class="label"><nobr><fmt:message
						key="orderspecial.label.email.to" /></nobr></span></td>
					<td><html:text styleClass="textfield" styleId="emailTo"
						style="width:100%;" property="repBean.emailTo"
						disabled="${!fullAccess}"
						maxlength="<%=OrderRepEntity.DBSIZE_EMAIL_TO_ADRESSES + ""%>" />
						</td>
				</tr>
				<tr>
					<td><span class="label"><nobr><fmt:message
						key="orderspecial.label.email.cc" /></nobr></span></td>
					<td><html:text styleClass="textfield" styleId="emailCc"
						style="width:100%;" property="repBean.emailCc"
						disabled="${!fullAccess}"
						maxlength="<%=OrderRepEntity.DBSIZE_EMAIL_CC_ADRESSES + ""%>" /></td>
				</tr>
				<tr>
					<td><span class="label"><nobr><fmt:message
						key="orderspecial.label.email.bcc" /></nobr></span></td>
					<td><html:text styleClass="textfield" styleId="emailBcc"
						style="width:100%;" property="repBean.emailBcc"
						disabled="${!fullAccess}"
						maxlength="<%=OrderRepEntity.DBSIZE_EMAIL_BCC_ADRESSES + ""%>" /></td>
				</tr>
				<tr>
					<td colspan="2"><span class="label"><nobr><fmt:message
						key="orderspecial.label.email.text" /></nobr></span></td>
				</tr>
				<tr>
					<td colspan="2"><html:textarea styleClass="textfield"
						styleId="emailText" rows="11" style="width:100%;"
						disabled="${!fullAccess}"
						property="repBean.emailText" /></td>
				</tr>
				<tr>
					<td colspan="2">
					<table  cellpadding="0" cellspacing="0" width="100%">
							<tr>
								<td align="left"><span class="label"><nobr><fmt:message
									key="orderspecial.label.email.send.last" />: <c:out
									value="${order_auftrag_OrderDetailForm.repBean.emailLastSent}" /></nobr></span></td>
									</tr>
							<tr>
								<td style="text-align:right">
								<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_REPAIR_MAINT_FULL_ACCESS %>">
								<input type="button" class="ViewButtonStyle"
									value="<fmt:message key="orderdetail.repair.button.send.email"/>"
									onClick="sendEmail()">
									</crh-common:authorize>
									</td>
							</tr>
					</table>
					</td>
				</tr>
			</tbody>
		</table>
</div>
		</td>
		<td>
<div class="subform" style="width:98%"">
<span class="labelblue"><fmt:message
				key="orderdetail.repair.label.maintenancetype.title" /> </span>		
		<div class="scrollist" style="height: 100px;">
		<table class="classic" style="border: none">

			<c:set var="counter" value="1" />
			<c:forEach var="entity" varStatus="status"
				items="${order_auftrag_OrderDetailForm.repBean.maintTypeTxEntityMap}">
				<tr class="zebra<c:out value="${counter % 2}"/>">
					<td valign="middle" style="width: 1px"><html:checkbox
						property="repBean.maintTypeBs[${status.count - 1}]" value="true"
						disabled="${!fullAccess}" /></td>
					<td><c:out
						value="${entity.value.maintTypeTxPK.maintTypeBs.maintTypeCd}" /></td>
					<td><c:out value="${entity.value.txt}" /></td>
				</tr>
				<c:set var="counter" value="${counter + 1}" />

			</c:forEach>
		</table>
		</div>
</div>
		</td>
	</tr>
	<tr>
	<td>
<%----------------------------------------------------------------------------------------------------------%>
<%---------------------------------------- DAMAGECODE-----------------------------------------------------%>
<%-------------------------------------------------------------------------------------------------------------%>
<div class="subform" style="width:98%">
<span class="labelblue"><fmt:message
				key="orderdetail.repair.label.damagecode.title" /></span>
                          <div class="subformlist" id="damageCodeDiv" style="height:40px;clear:both;font-size:12px;">
		<jsp:include page="RepTabDamageCodeTile.jsp"></jsp:include>
		</div>
		<div style="margin-top:4px;width:98%">
		<table class="noborder" style="vertical-align: top">
		<tr>
		<td>
			<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_REPAIR_MAINT_FULL_ACCESS %>">
					<input type="text" id="damageCodeDummy"  value="" />
			</crh-common:authorize>
				</td>
				<td>
				<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_REPAIR_MAINT_FULL_ACCESS %>">
					<input id="addDamageCdButton" disabled type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.hinzufuegen"/>" onclick="addDamageCd()"/>
				</crh-common:authorize>
				</td>
				</tr>
			</table>
		</div>
</div>

	</td>
	</tr>
	<tr>
	<td>
<%----------------------------------------------------------------------------------------------------------%>
<%---------------------------------------- ATTACHMENT E-MAIL-------------------------------------------------%>
<%----------------------------------------------------------------------------------------------------------%>

		<div class="subform" style="width:98%""><span class="labelblue"><fmt:message
				key="orderdetail.repair.label.email.upload.title" /> </span> 
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_REPAIR_MAINT_FULL_ACCESS %>">
									<nobr>
									<input type="file" id="replaceThisUploadField<%=Constant.ORDER_REP_ATTACHMENT_TYPE_EMAIL %>"/>
									<input type="button" value='<fmt:message key="buttons.upload"/>' class="ViewButtonStyle"  onclick="upload(<%=Constant.ORDER_REP_ATTACHMENT_TYPE_EMAIL %>);" /></nobr>
									</crh-common:authorize>
									
		<div id="emailAttachments" class="scrollist" style="margin-top:4px;height: 40px;">
					<c:set var="attachmentTypeDiv" scope="request"><%=Constant.ORDER_REP_ATTACHMENT_TYPE_EMAIL %></c:set>
					<jsp:include page="OrderDetailAttachmentDivTile.jsp"></jsp:include>
                        </div>                          
           </div>
           	
	</td>
	</tr>	
	</tbody>
	</table>
	
	</td>
	</tr>
	</table>
	</div>
<div class="subform" style="width:98%;margin:4px;padding:0px;">	
			<table border="0" cellspacing="0" cellpadding="0" width="100%">

				<tr>
				<td>

<%----------------------------------------------------------------------------------------------------------%>
<%---------------------------------------- OFFER DIV----------------------------------------------------------------%>
<%-------------------------------------------------------------------------------------------------------------%>		

	<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<colgroup>
				<col width="60%">
				<col width="40%">

			</colgroup>
			<tbody>
	<tr>
		<td>
		
		<div class="subform" style="width:98%""><span class="labelblue"><fmt:message
			key="orderdetail.repair.label.offer" /></span>
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<colgroup>
				<col width="30%">
				<col width="70%">

			</colgroup>
			<tbody>
				<tr>
					<td><span class="label"><nobr><fmt:message
						key="orderdetail.repair.label.offer.desired" /></nobr></span></td>
					<td><html:checkbox property="repBean.offerDesiredFlag" disabled="${!fullAccess}"
						value="true" /></td>
				</tr>
				<tr>
					<td><span class="label"><nobr><fmt:message
						key="orderdetail.repair.label.datum" /></nobr></span></td>
					<td>
					<div style="float: left;"><html:text styleClass="textfield"
						styleId="offerAcceptDate" property="repBean.offerAcceptDate" /></div>
					<div style="float: left; margin-left: 2px;"><html:text
						styleClass="textfield" styleId="offerAcceptTime"
						property="repBean.offerAcceptTime" /></div>
					</td>
				</tr>
				<tr>
					<td><span class="label"><nobr><fmt:message
						key="orderdetail.repair.label.offer.amount" /></nobr></span></td>
					<td><html:text styleClass="textfield" styleId="offerAmount" disabled="${!fullAccess && order_auftrag_OrderDetailForm.repBean.offerAcceptFlag }"
						style="width:80px;" property="repBean.offerAmount"
						maxlength="<%=OrderRepEntity.DBSIZE_OFFER_AMOUNT + ""%>" /> <html:select
						styleClass="textfield" size="1" style="width: 70px;"
						property="repBean.offerAmountCur" styleId="offerAmountCur" disabled="${!fullAccess && order_auftrag_OrderDetailForm.repBean.offerAcceptFlag }">
						<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">-</html:option>
						<c:forEach var="entity" varStatus="status"
							items="${order_auftrag_OrderDetailForm.repBean.currencyBsEntityMap}">
							<html:option value="${entity.key}">
								<c:out value="${entity.key}" />
							</html:option>
						</c:forEach>
					</html:select></td>
				</tr>
				<tr>
					<td><span class="label"><nobr><fmt:message
						key="orderdetail.repair.label.offer.accept" /></nobr></span></td>
					<td><html:checkbox property="repBean.offerAcceptFlag" disabled="${!fullAccess}"
						value="true" /></td>					
				</tr>				
				</tbody>
		</table>
		</div>
		</td>
		<td>
		
		<span class="labelblue">
		
				<div class="subform" style="width:98%"><span class="labelblue"><fmt:message
				key="orderdetail.repair.label.remarks.offer.title" /> </span> 
<%--------------------------------------------------------------------------------------------------------------%>
<%----------------------------------------REMARKS OFFER-------------------------------------------------------------%>
<%--------------------------------------------------------------------------------------------------------------%>
			<html:textarea styleClass="textfield"
						styleId="offerRemarks" rows="7" style="width:100%;"
						property="repBean.offerRemarks" disabled="${!fullAccess && order_auftrag_OrderDetailForm.repBean.offerAcceptFlag }"/>
						</div>
		</td>
		</tr>
		<tr>
		<td colspan="2">
<%--------------------------------------------------------------------------------------------------------------%>
<%---------------------------------------- UPLOAD OFFER--------------------------------------------------------------%>
<%--------------------------------------------------------------------------------------------------------------%>	
		<div class="subform" style="width:98%""><span class="labelblue"><fmt:message
				key="orderdetail.repair.label.offer.upload.title" /> </span> 
									<nobr>
									<input type="file" id="replaceThisUploadField<%=Constant.ORDER_REP_ATTACHMENT_TYPE_OFFER %>"
									<c:if test="${!fullAccess && order_auftrag_OrderDetailForm.repBean.offerAcceptFlag }">
									disabled
									</c:if> 									 
									/>
									<input type="button" value='<fmt:message key="buttons.upload"/>' class="ViewButtonStyle"  id="uploadOffer" onclick="upload(<%=Constant.ORDER_REP_ATTACHMENT_TYPE_OFFER %>);"
									<c:if test="${!fullAccess && order_auftrag_OrderDetailForm.repBean.offerAcceptFlag }">
									disabled
									</c:if> 
									/></nobr>
									
		<div id="offerAttachments" class="scrollist" style="margin-top:4px;height: 50px;">
					<c:set var="attachmentTypeDiv" scope="request"><%=Constant.ORDER_REP_ATTACHMENT_TYPE_OFFER %></c:set>
					<jsp:include page="OrderDetailAttachmentDivTile.jsp"></jsp:include>
          </div>
           </div>
		</td>
		</tr>
		</tbody>
		</table>
		
		</td>
		</tr>
		</table>
		</div>
<div class="subform" style="width:98%;margin:4px;padding:0px;">	
			<table border="0" cellspacing="0" cellpadding="0" width="100%">		
<%--------------------------------------------------------------------------------------------------------------%>
<%----------------------------------------SECOND DIV-DOCUMENTATION-------------------------------------%>
<%--------------------------------------------------------------------------------------------------------------%>
		
		<tr>
		<td>
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<colgroup>
				<col width="60%">
				<col width="40%">

			</colgroup>
			<tbody>
	<tr>
		<td>
		
		<div class="subform" style="width:98%""><span class="labelblue"><fmt:message
			key="orderdetail.repair.label.doku" /></span><br>
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<colgroup>
				<col width="30%">
				<col width="70%">

			</colgroup>
			<tbody>		
				<tr>
					<td><span class="label"><nobr><fmt:message
						key="orderdetail.repair.label.documentation.accept" /></nobr></span></td>
					<td><html:checkbox property="repBean.documentationAcceptFlag" disabled="${!fullAccess}"
						value="true" /></td>
				</tr>
								
				<tr>
					<td><span class="label"><nobr><fmt:message
						key="orderdetail.repair.label.documentation.enter.plant.date" /></nobr></span></td>
					<td>
					<div style="float: left;"><html:text styleClass="textfield"
						styleId="enterPlantDate" property="repBean.enterPlantDate" /></div>
					<div style="float: left; margin-left: 2px;"><html:text
						styleClass="textfield" styleId="enterPlantTime"
						property="repBean.enterPlantTime" /></div>
					</td>
				</tr>
				<tr>
					<td><span class="label"><nobr><fmt:message
						key="orderdetail.repair.label.documentation.leave.plant.date" /></nobr></span></td>
					<td>
					<div style="float: left;"><html:text styleClass="textfield"
						styleId="leavePlantDate" property="repBean.leavePlantDate" /></div>
					<div style="float: left; margin-left: 2px;"><html:text
						styleClass="textfield" styleId="leavePlantTime"
						property="repBean.leavePlantTime" /></div>
					</td>
				</tr>								
				</tbody>
		</table>
		</div>
		</td>
		<td>
		
		<span class="labelblue">
		
				<div class="subform" style="width:98%"><span class="labelblue"><fmt:message
				key="orderdetail.repair.label.remarks.documentation.title" /> </span> 
<%--------------------------------------------------------------------------------------------------------------%>
<%----------------------------------------REMARKS DOCUMENTATION-----------------------------------------------------%>
<%--------------------------------------------------------------------------------------------------------------%>
			<html:textarea styleClass="textfield"
					rows="4" style="width:100%;"
						property="repBean.documentationRemarks" disabled="${!fullAccess && order_auftrag_OrderDetailForm.repBean.documentationAcceptFlag }"/>
						</div>
		</td>
		</tr>
		<tr>
		<td colspan="2">
<%--------------------------------------------------------------------------------------------------------------%>
<%---------------------------------------- UPLOAD DOCUMENTATION-------------------------------------------------%>
<%--------------------------------------------------------------------------------------------------------------%>	
		<div class="subform" style="width:98%""><span class="labelblue"><fmt:message
				key="orderdetail.repair.label.offer.upload.title" /> </span> 
									<nobr><input type="file" id="replaceThisUploadField<%=Constant.ORDER_REP_ATTACHMENT_TYPE_DOCUMENTATION %>"
									<c:if test="${!fullAccess && order_auftrag_OrderDetailForm.repBean.documentationAcceptFlag }">
									disabled
									</c:if> 									 
									/>
									<input type="button" value='<fmt:message key="buttons.upload"/>' class="ViewButtonStyle"  id="uploadDocumentation" onclick="upload(<%=Constant.ORDER_REP_ATTACHMENT_TYPE_DOCUMENTATION %>);"
									<c:if test="${!fullAccess && order_auftrag_OrderDetailForm.repBean.documentationAcceptFlag }">
									disabled
									</c:if> 									 
									/></nobr>
									
		<div id="documentationAttachments" class="scrollist" style="margin-top:4px;height: 50px;">
					<c:set var="attachmentTypeDiv" scope="request"><%=Constant.ORDER_REP_ATTACHMENT_TYPE_DOCUMENTATION %></c:set>
					<jsp:include page="OrderDetailAttachmentDivTile.jsp"></jsp:include>
                        </div>
           </div>
		</td>
		</tr>
		</tbody>
		</table>
		</td>
		</tr>
		</table>
		</div>

<%--------------------------------------------------------------------------------------------------------------%>
<%----------------------------------------THIRD DIV - INVOICE---------------------------------------------------%>
<%--------------------------------------------------------------------------------------------------------------%>		
<div class="subform" style="width:98%;margin:4px;padding:0px;">	
			<table border="0" cellspacing="0" cellpadding="0" width="100%">		
		
		<tr>
		<td>
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<colgroup>
				<col width="50%">
				<col width="50%">

			</colgroup>
			<tbody>
	<tr>
		<td>
		
		<div class="subform" style="width:98%""><span class="labelblue"><fmt:message
			key="orderdetail.repair.label.invoice" /></span><br>


		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<colgroup>
				<col width="30%">
				<col width="70%">

			</colgroup>
			<tbody>				
				<tr>
					<td><span class="label"><nobr><fmt:message
						key="orderdetail.repair.label.invoice.date" /></nobr></span></td>
					<td>
					<div style="float: left;"><html:text styleClass="textfield"
						styleId="invoiceDate" property="repBean.invoiceDate" disabled="${!fullAccess && order_auftrag_OrderDetailForm.repBean.invoicePaidFlag}"/></div>
					<div style="float: left; margin-left: 2px;"><html:text
						styleClass="textfield" styleId="invoiceTime"
						property="repBean.invoiceTime" /></div>
					</td>
				</tr>
				<tr>
					<td><span class="label"><nobr><fmt:message
						key="orderdetail.repair.label.invoice.no" /></nobr></span></td>
					<td><html:text styleClass="textfield" styleId="invoiceNo" disabled="${!fullAccess && order_auftrag_OrderDetailForm.repBean.invoicePaidFlag}"
						style="width:172px;" property="repBean.invoiceNo"
						maxlength="<%=OrderRepEntity.DBSIZE_INVOICE_NO + ""%>" /></td>
				</tr>
				<tr>
					<td><span class="label"><nobr><fmt:message
						key="orderdetail.repair.label.invoice.amount" /></nobr></span></td>
					<td><html:text styleClass="textfield" styleId="invoiceAmount"
						style="width:80px;" property="repBean.invoiceAmount" disabled="${!fullAccess && order_auftrag_OrderDetailForm.repBean.invoicePaidFlag}"
						maxlength="<%=OrderRepEntity.DBSIZE_INVOICE_AMOUNT + ""%>" /> <html:select
						styleClass="textfield" size="1" style="width: 70px;"
						property="repBean.invoiceAmountCur" styleId="invoiceAmountCur" disabled="${!fullAccess && order_auftrag_OrderDetailForm.repBean.invoicePaidFlag}">
						<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">-</html:option>
						<c:forEach var="entity" varStatus="status"
							items="${order_auftrag_OrderDetailForm.repBean.currencyBsEntityMap}">
							<html:option value="${entity.key}">
								<c:out value="${entity.key}" />
							</html:option>
						</c:forEach>
					</html:select></td>
				</tr>
				<tr>
					<td><span class="label"><nobr><fmt:message
						key="orderdetail.repair.label.invoice.paid" /></nobr></span></td>
					<td><html:checkbox property="repBean.invoicePaidFlag" disabled="${!fullAccess}"
						value="true" /></td>
				</tr>
				
				
			</tbody>
		</table>
		
		</div>
		</td>
		<td>
		
		<span class="labelblue">
		
				<div class="subform" style="width:98%"><span class="labelblue"><fmt:message
				key="orderdetail.repair.label.remarks.invoice.title" /> </span> 
<%--------------------------------------------------------------------------------------------------------------%>
<%----------------------------------------REMARKS INVOICE-----------------------------------------------------------%>
<%--------------------------------------------------------------------------------------------------------------%>
<html:textarea styleClass="textfield"
						styleId="internalRemark" rows="3" style="width:100%;"
						property="repBean.internalRemark" disabled="${!fullAccess && order_auftrag_OrderDetailForm.repBean.invoicePaidFlag}"/>
						</div>
		</td>
		</tr>
		<tr>
		<td colspan="2">
<%--------------------------------------------------------------------------------------------------------------%>
<%---------------------------------------- UPLOAD INVOICE-------------------------------------------------------%>
<%--------------------------------------------------------------------------------------------------------------%>	
		<div class="subform" style="width:98%""><span class="labelblue"><fmt:message
				key="orderdetail.repair.label.invoice.upload.title" /> </span> 
									<nobr><input type="file" id="replaceThisUploadField<%=Constant.ORDER_REP_ATTACHMENT_TYPE_INVOICE %>"
									<c:if test="${!fullAccess && order_auftrag_OrderDetailForm.repBean.invoicePaidFlag }">
									disabled
									</c:if>										 
									/>
									<input type="button" value='<fmt:message key="buttons.upload"/>'id="uploadInvoice" class="ViewButtonStyle"  onclick="upload(<%=Constant.ORDER_REP_ATTACHMENT_TYPE_INVOICE %>);"
									<c:if test="${!fullAccess && order_auftrag_OrderDetailForm.repBean.invoicePaidFlag }">
									disabled
									</c:if>									 
									/></nobr>
									
		<div id="invoiceAttachments" class="scrollist" style="margin-top:4px;height: 50px;">
					<c:set var="attachmentTypeDiv" scope="request"><%=Constant.ORDER_REP_ATTACHMENT_TYPE_INVOICE %></c:set>
					<jsp:include page="OrderDetailAttachmentDivTile.jsp"></jsp:include>
                        </div>
           </div>
		</td>
		</tr>
		</tbody>
		</table>
		</td>
</tr>
</table>
</div>
<!--</html:form>-->
