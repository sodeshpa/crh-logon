<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: OrderSpecialTile.jsp,v $ --%>

<%@ page session="true"%>
<%@ include file="../includeTLDs.jsp"%>
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="fmtx"%>


<%@page
	import="com.crh.logon.order.web.forms.auftrag.OrderSpecialForm"%>
<%@page
	import="com.crh.logon.persistence.entities.common.OrderEntity"%>
<%@page
	import="com.crh.logon.persistence.entities.common.OrderItemEntity"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page
	import="com.crh.logon.persistence.entities.common.OrderRepEntity"%>
<%@page
	import="com.crh.logon.order.web.actions.auftrag.OrderSpecialAction"%><c:set
	var="orderSpecialUrl">
	/../order/auftrag/OrderSpecial.do
</c:set>
<c:set	var="SELECTION_MATERIAL_LOAD_CURRENT"><%=OrderSpecialForm.SELECTION_MATERIAL_LOAD_CURRENT %></c:set>
<c:set	var="SELECTION_MATERIAL_LOAD_PREVIOUS"><%=OrderSpecialForm.SELECTION_MATERIAL_LOAD_PREVIOUS %></c:set>
<c:set	var="SELECTION_MATERIAL_LOAD_EMPTY"><%=OrderSpecialForm.SELECTION_MATERIAL_LOAD_EMPTY %></c:set>
<c:set	var="SELECTION_MATERIAL_LOAD_ANY"><%=OrderSpecialForm.SELECTION_MATERIAL_LOAD_ANY %></c:set>

<%-- DWR services --%>
<script type="text/javascript"
	src="<crh-common:url value='/dwr/interface/OrderAjaxService.js'/>"></script>
<script type="text/javascript"
	src="<crh-common:url value='/dwr/interface/OrderSpecialAjaxService.js'/>"></script>

<script type="text/javascript"
	src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>

<script type="text/javascript">
function updateEmptyMaterial(){
	if (!document.getElementsByName("materialLoad")[<c:out value="${SELECTION_MATERIAL_LOAD_EMPTY }"/>-1].checked) {
		return false;
	}
	getComboValues();
	a = {'formAction' :'<%=OrderSpecialAction.FORM_ACTION_CHANGE_EMPTY_MATERIAL%>'
	};
	addFormElements(a, "OrderSpecialForm"); <%-- add form data --%>
	Ext.Ajax.request({
		url :'<c:url value="${orderSpecialUrl}"/>',
	   params: a,
	   success: function(response){
	    eval(response.responseText);
	   }
	});
}

	var custWithoutOrderToday = false;
	var materialCombo;
	var prodPlantCombo;
	var deliveryFromDat;
	var deliveryFromTime;
	var distDestCombo;
	var plantCombo;
	var shpointCombo;
	var carrierCombo;
	
	onPopupReady( function() {

		// --- Filters ---
		<jsp:include page="OrderSpecialShpitoSearchInc.jsp"/>
		<jsp:include page="OrderSpecialTileInc.jsp"/>
		
		checkTransmitEnabled();
		changeLoad(<c:out value="${order_auftrag_OrderSpecialForm.materialLoad }"/>);
		
	});

	function checkTransmitEnabled(){
		var button = Ext.getDom('SpecialSaveAndTransmitButton');
		if (deliveryFromDat.getValue().clearTime(true).getTime() == (new Date()).clearTime(false).getTime() &&
				(Ext.getDom('plantRailwayStationCd').value == Ext.getDom('origPlantRailwayStationCd').value)) {
			button.className = 'ViewButtonStyle';
			button.disabled = false;			
		}
		else {
			button.className = 'ViewButtonStyleDisabled';
			button.disabled = true;			
		}
	}

	function changeLoad(value) {

		if (value == <c:out value="${SELECTION_MATERIAL_LOAD_CURRENT }"/>) {
			materialCombo.setValue(document.getElementById('currLoadMatDesc').value);
			prodPlantCombo.setValue(document.getElementById('currLoadPlantName').value);
			document.getElementById('quantity').value = document.getElementById('currLoadQty').value;
			document.getElementById('quantityUom').value = '<%=Constant.UOM_TONS%>';
			
			materialCombo.setDisabled(true);
			prodPlantCombo.setDisabled(true);
			document.getElementById('quantity').disabled = true;
			document.getElementById('quantityUom').disabled = true;
		}
		else if (value == <c:out value="${SELECTION_MATERIAL_LOAD_PREVIOUS }"/>) {
			materialCombo.setValue(document.getElementById('preLoadMatDesc').value);
			prodPlantCombo.setValue(document.getElementById('preLoadPlantName').value);
			document.getElementById('quantity').value = document.getElementById('preLoadQty').value;
			document.getElementById('quantityUom').value = '<%=Constant.UOM_TONS%>';
			
			prodPlantCombo.setDisabled(true);
			materialCombo.setDisabled(true);
			document.getElementById('quantity').disabled = false;
			document.getElementById('quantityUom').disabled = true;

		}
		if (value == <c:out value="${SELECTION_MATERIAL_LOAD_EMPTY }"/>) {
			<jsp:include page="OrderSpecialEmptyMaterialOptionInc.jsp"/>
		}
		else if (value == <c:out value="${SELECTION_MATERIAL_LOAD_ANY }"/>) {
			materialCombo.setValue("");
			prodPlantCombo.setValue("");
			document.getElementById('quantity').value = '';
			document.getElementById('quantityUom').value = '-1';
			
			materialCombo.setDisabled(false);
			prodPlantCombo.setDisabled(false);
			document.getElementById('quantity').disabled = false;
			document.getElementById('quantityUom').disabled = false;
		}
	}

	function getComboValues() {

		document.getElementById('distDestCd').value = distDestCombo.getValue();
		document.getElementById('plantCd').value = plantCombo.getValue();
		document.getElementById('shippingpointId').value = shpointCombo.getValue();
		document.getElementById('prodPlantCd').value = prodPlantCombo.getValue();
		document.getElementById('sapMaterialId').value = materialCombo.getValue();
		document.getElementById('carrierId').value = carrierCombo.getValue();
		
	}
	function saveOrder(action) {

		var button = Ext.getDom('SpecialCancelButton');
		button.className = 'ViewButtonStyleDisabled';
		button.disabled = true;
		button = Ext.getDom('SpecialSaveButton');
		button.className = 'ViewButtonStyleDisabled';
		button.disabled = true;
		button = Ext.getDom('SpecialSaveAndTransmitButton');
		button.className = 'ViewButtonStyleDisabled';
		button.disabled = true;
		
		getComboValues();
		
		form = Ext.getDom('OrderSpecialForm');
     	p = {'formAction' : action};
	 	addFormElements(p, form);
		
		var diva = Ext.get('saveDiv');
		diva.load( {
			url :'<c:url value="${orderSpecialUrl}"/>',
			params : p,
			text :'<fmt:message key="orderspecial.message.saving"/>',
			scripts :true
		});
		//orderUpdated = true;
	}

function addDamageCd() {
		var damageCd = damageCodeTagCombo.getValue();
		p = {'formAction' :'<%=OrderSpecialAction.FORM_ACTION_ADD_DAMAGECODE%>',
				 'damageCode': damageCd
		};
		form = Ext.getDom('OrderDetailForm');	
		var divp = Ext.get('damageCodeDiv');
		divp.load({
			url :'<c:url value="${orderSpecialUrl}"/>',
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
	p = {'formAction' :'<%=OrderSpecialAction.FORM_ACTION_REMOVE_DAMAGECODE%>',
			 'damageCode': damageCd
	};
	form = Ext.getDom('OrderSpecialForm');	
	var divp = Ext.get('damageCodeDiv');
	divp.load({
		url :'<c:url value="${orderSpecialUrl}"/>',
		text: '<fmt:message key="shippingpoint.text.exjsloading"/>',
		scripts: true,
		params: p
	});
}

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

function removeOrderRepAttachment(index){
	
	//save with a form
    form = Ext.getDom('OrderDetailForm');
	p = {'formAction' :'<%=OrderSpecialAction.FORM_ACTION_REMOVE_REP_ORDER_ATT_FILE%>',
		 'uploadBean.index': index
		};
	addFormElements(p, form);
		
	var diva = Ext.get('emailAttachments');
	diva.load( {
		url :'<c:url value="${orderSpecialUrl}"/>',
		params : p,
				text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
				scripts :true					
			});
}

function upload(type) {
	var form = Ext.getDom('OrderSpecialForm');
	fileField = document.getElementById('replaceThisUploadField'+type);
	fileField.name='uploadBean.file';
	fileField.id='uploadBean.file';
	Ext.Ajax.request({
		url :'<c:url value="${orderSpecialUrl}"/>',
		form: 'OrderSpecialForm',
		method: 'POST',
		isUpload: true,
		 params:{type : '<c:out value="${param.type}"/>', 'uploadBean.type' : type, 'formAction' :'<%=OrderSpecialAction.FORM_ACTION_UPLOAD_REP_ORDER_ATT_FILE%>'},
        success: function(result, request) {
		    clearFileUpload('uploadBean.file', type);
				p = {'formAction' :'<%=OrderSpecialAction.FORM_ACTION_SHOW_ATTACHMENTS%>'
						};
			var diva = Ext.get('emailAttachments');
			diva.load( {
				url :'<c:url value="${orderSpecialUrl}"/>',
				params : p,
						text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
						scripts :true
					});
				
		}
	});

}
</script>




<div id="OrderSpecialDiv" class="customer_max" style="width: 820px;">

<html:form action="auftrag/OrderSpecial.do" styleId="OrderSpecialForm"
	style="margin-top: 0px;">

	<html:hidden property="distDestCd" styleId="distDestCd" />
	<html:hidden property="plantCd" styleId="plantCd" />
	<html:hidden property="plantName" styleId="plantName" />
	<html:hidden property="plantRailwayStationCd" styleId="plantRailwayStationCd" />
	<html:hidden property="origPlantRailwayStationCd" styleId="origPlantRailwayStationCd" />
	<html:hidden property="shippingpointId" styleId="shippingpointId" />
	<html:hidden property="shippingpointName" styleId="shippingpointName" />
	<html:hidden property="prodPlantCd" styleId="prodPlantCd" />
	<html:hidden property="sapMaterialId" styleId="sapMaterialId" />
	<html:hidden property="carrierId" styleId="carrierId" />
	<html:hidden property="carrierDisplayName" styleId="carrierDisplayName" />
				
	<html:hidden property="osShiptoSapBpId" styleId="osShiptoSapBpId" />
	<html:hidden property="osBilltoSapBpId" styleId="osBilltoSapBpId" />
	<html:hidden property="osSoldtoSapBpId" styleId="osSoldtoSapBpId" />
	<html:hidden property="osSareaId" styleId="osSareaId" />

	<html:hidden property="currLoadMatDesc" styleId="currLoadMatDesc" />
	<html:hidden property="currLoadPlantName" styleId="currLoadPlantName" />
	<html:hidden property="currLoadQty" styleId="currLoadQty" />
	<html:hidden property="preLoadMatDesc" styleId="preLoadMatDesc" />
	<html:hidden property="preLoadPlantName" styleId="preLoadPlantName" />
	<html:hidden property="preLoadQty" styleId="preLoadQty" />
	<html:hidden property="emptyWagonMatDesc" styleId="emptyWagonMatDesc" />
	<html:hidden property="defaultPlant.plantName" styleId="defaultPlant.plantName" />


	<!-- Begin table 2 rows -->
	<table class="big_form2" style="width: 100%">
		<colgroup>
			<col width="50%">
			<col width="50%">
		</colgroup>
		<tr>
			<th colspan="2" class="classicblue">
			<div id="pageTitleDiv">
			<div style="font-size: 14px;"><fmt:message
				key="orderspecial.title" /> <c:out
				value="${order_auftrag_OrderSpecialForm.wagonNo}" /></div>
			</div>
			</th>
		</tr>
		
		<tr>
			<td colspan="2">
			<table class="classic" style="width: 100%">
				<colgroup>
					<col width="20%">
					<col width="15%">
					<col width="32%">
					<col width="33%">
				</colgroup>
				<tr>
					<td>
						<span class='label'><fmt:message key="orderspecial.label.location" /></span>
						<c:out value="${order_auftrag_OrderSpecialForm.wagon.lastKnownRailwayStation.railwayStationName}" />
					</td>
					<td colspan="3">
						<span class='label'><fmt:message key="orderspecial.label.return.to" />:</span>
						&nbsp;
						<c:out value="${order_auftrag_OrderSpecialForm.wagon.returnShiptoSapBp.railwayStation.railwayStationName}" /> (<c:out value="${order_auftrag_OrderSpecialForm.wagon.returnShiptoSapBp.name1}" /> - <c:out value="${order_auftrag_OrderSpecialForm.wagon.returnShiptoSapBp.sapId}" />)
					</td>					
				</tr>		
				<tr>
					<td><span class="label"><nobr><fmt:message
						key="orderspecial.label.dist.dest" /></nobr></span> <input type="text"
						id="distDestCdId" /></td>
					<td><span class="label"><nobr><fmt:message
						key="orderspecial.label.del.from.date" /></nobr></span>
					<html:text styleClass="textfield"
						styleId="orderSpecialDelDatId" property="orderSpecialDelDat" /></div>
					</td>
					<td><span class="label"><nobr><fmt:message
						key="orderspecial.label.plant" /></nobr></span>
						 <input type="text" id="plantCdId" /></td>

					<td><span class="label"><nobr><fmt:message
						key="orderspecial.label.shpoint" /></nobr></span><input type="text"
						id="shpointId" /></td>
				</tr>
				<tr>
					<td id="searchCell" colspan="4"><span class="label"><nobr><fmt:message
						key="orderspecial.label.shipto.sarea" /></nobr></span> <input type="text"
						name="searchtext" value="" size="50" id="search"></td>
				</tr>

				<tr>
					<td colspan="2">

					<table cellpadding="0" cellspacing="0">
						<colgroup>
							<col width="30%">
							<col width="70%">

						</colgroup>
						<tbody>

							<tr>
								<td><span class="label"><nobr><fmt:message
									key="orderspecial.label.carrier" /></nobr></span></td>
								<td><input type="text" id="carrierInputId" /></td>
							</tr>
							<tr>
								<td><span class="label"><nobr><fmt:message
									key="orderspecial.label.billto" /></nobr></span></td>
								<td><input type="text" id="billto" /></td>
							</tr>
							<tr>
								<td><span class="label"><nobr><fmt:message
									key="orderspecial.label.material.load" /></nobr></span></td>
								<td><input type="radio" name="materialLoad" value="<c:out value="${SELECTION_MATERIAL_LOAD_CURRENT }"/>"
									onclick="changeLoad(this.value)" 
									<c:if test="${empty order_auftrag_OrderSpecialForm.currLoadPlantName}">DISABLED </c:if> > <nobr><fmt:message
									key="orderspecial.text.matload.current" />: <c:out
									value="${order_auftrag_OrderSpecialForm.currLoadQty}" /> <c:if
									test="${not empty order_auftrag_OrderSpecialForm.currLoadQty}">
									<%=Constant.UOM_TONS_TXT%>
								</c:if> <c:out
									value="${order_auftrag_OrderSpecialForm.currLoadMatDesc}" /></nobr><br />
								<input type="radio" name="materialLoad" value="<c:out value="${SELECTION_MATERIAL_LOAD_PREVIOUS }"/>"
									onclick="changeLoad(this.value)"
									<c:if test="${empty order_auftrag_OrderSpecialForm.preLoadPlantName}">DISABLED </c:if>> <nobr><fmt:message
									key="orderspecial.text.matload.previous" />:<c:out
									value="${order_auftrag_OrderSpecialForm.preLoadQty}" /> <c:if
									test="${not empty order_auftrag_OrderSpecialForm.preLoadQty}">
									<%=Constant.UOM_TONS_TXT%>
								</c:if> <c:out value="${order_auftrag_OrderSpecialForm.preLoadMatDesc}" /></nobr><br />
								<input type="radio" name="materialLoad" value="<c:out value="${SELECTION_MATERIAL_LOAD_EMPTY }"/>"
								<c:if test="${order_auftrag_OrderSpecialForm.materialLoad eq SELECTION_MATERIAL_LOAD_EMPTY}">checked</c:if>
									onclick="changeLoad(this.value)"> <nobr id="emptyMaterialText"><c:out
									value="${order_auftrag_OrderSpecialForm.emptyWagonMatDesc}" /></nobr><br />
								<input type="radio" name="materialLoad" value="<c:out value="${SELECTION_MATERIAL_LOAD_ANY }"/>"
									onclick="changeLoad(this.value)"
									<c:if test="${order_auftrag_OrderSpecialForm.wagon.wagonType.wagonTrspCat.wagonTrspCatCd == 2}">DISABLED </c:if> > <nobr><fmt:message
									key="orderspecial.text.matload.any" /></nobr><br />
								</td>
							</tr>
							<tr>
								<td><span class="label"><nobr><fmt:message
									key="orderspecial.label.prod.plant.mat" /></nobr></span></td>
								<td>
								<div style="float: left;"><input type="text"
									id="prodPlantCdId" /></div>
								<div style="float: left; margin-left: 2px;"><input
									type="text" id="sapMatId" /></div>
								</td>
							</tr>
							<tr>
								<td><span class="label"><nobr><fmt:message
									key="orderspecial.label.quantity" /></nobr></span></td>
								<td><html:text styleClass="textfield" style="width: 180px;"
									property="quantity" styleId="quantity"
									maxlength="<%=OrderItemEntity.DBSIZE_ORDERED_QUANTITY + ""%>"
									disabled="true" /> <html:select styleClass="textfield"
									size="1" style="width: 80px;" property="quantityUom"
									styleId="quantityUom" disabled="true">
									<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">-</html:option>
									<c:forEach var="entity" varStatus="statusUom"
										items="${order_auftrag_OrderSpecialForm.uomBsEntityMap}">
										<html:option value="${entity.key}">
											<c:out value="${entity.key}" />
										</html:option>
									</c:forEach>
								</html:select></td>
								</td>
							</tr>
							<tr>
								<td><span class="label"><fmt:message
									key="orderspecial.label.cont.person" /></span></td>
								<td><html:text styleClass="textfield"
									styleId="contactPerson" style="width:262px;"
									property="contactPerson"
									maxlength="<%=OrderEntity.DBSIZE_CONTACT_PERSON_TXT + ""%>" /></td>
							</tr>

						</tbody>
					</table>


					</td>
					<td colspan="2">

					<div id="maintTypeDiv"><jsp:include
						page="OrderSpecialMaintTypeList.jsp"></jsp:include></div>
					</td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td>
			<table class="classic" cellpadding="0" cellspacing="0">
				<colgroup>
					<col width="30%">
					<col width="70%">
				</colgroup>
				<tbody>
					<tr>
						<td><span class="label"><nobr><fmt:message
							key="orderspecial.label.email.send" /></nobr></span></td>
						<td><html:checkbox property="emailSendFlag" value="true" />
						</td>
					</tr>
					<tr>
						<td><span class="label"><nobr><fmt:message
							key="orderspecial.label.email.to" /></nobr></span></td>
						<td><html:text styleClass="textfield"
							styleId="emailTo" style="width:100%;" property="emailTo"
							maxlength="<%=OrderRepEntity.DBSIZE_EMAIL_TO_ADRESSES + ""%>" /></td>
					</tr>
					<tr>
						<td><span class="label"><nobr><fmt:message
							key="orderspecial.label.email.cc" /></nobr></span></td>
						<td><html:text styleClass="textfield"
							styleId="emailCc" style="width:100%;" property="emailCc"
							maxlength="<%=OrderRepEntity.DBSIZE_EMAIL_CC_ADRESSES + ""%>" /></td>
					</tr>
					<tr>
						<td><span class="label"><nobr><fmt:message
							key="orderspecial.label.email.bcc" /></nobr></span></td>
						<td><html:text styleClass="textfield"
							styleId="emailBcc" style="width:100%;" property="emailBcc"
							maxlength="<%=OrderRepEntity.DBSIZE_EMAIL_BCC_ADRESSES + ""%>" /></td>
					</tr>
					<tr>
						<td colspan="2"><span class="label"><nobr><fmt:message
							key="orderspecial.label.email.text" /></nobr></span></td>
					</tr>
					<tr>
						<td colspan="2"><html:textarea styleClass="textfield"
							styleId="emailText" rows="8" style="width:100%;"
							property="emailText" /></td>
					</tr>
				</tbody>
			</table>

			</td>
			<td>

			<table class="classic" cellpadding="0" cellspacing="0">
				<colgroup>
					<col width="30%">
					<col width="70%">

				</colgroup>
				<tbody>
					<tr>
						<td><span class="label"><nobr><fmt:message
							key="orderdetail.repair.label.offer.desired" /></nobr></span></td>
						<td><html:checkbox property="offerDesiredFlag" value="true" /></td>
					</tr>
				</tbody>
			</table>
<%--------------------------------------------------------------------------------------------------------------%>
<%---------------------------------------- UPLOAD EMAIL--------------------------------------------------------------%>
<%--------------------------------------------------------------------------------------------------------------%>	
		<div class="subform" style="margin-top:4px;width:98%""><span class="labelblue"><fmt:message
				key="orderdetail.repair.label.email.upload.title" /> </span> 
									<nobr>
									<input type="file" id="replaceThisUploadField<%=Constant.ORDER_REP_ATTACHMENT_TYPE_EMAIL %>"/>
									<input type="button" value='<fmt:message key="buttons.upload"/>' class="ViewButtonStyle"  id="uploadOffer" onclick="upload(<%=Constant.ORDER_REP_ATTACHMENT_TYPE_EMAIL %>);" /></nobr>
									
			<div class="scrollist" id="emailAttachments" style="margin-top:4px;height: 50px;">
				<jsp:include page="OrderSpecialRepAttachmentTile.jsp"></jsp:include>
          	</div>
        </div>
        <br>
<%----------------------------------------------------------------------------------------------------------%>
<%---------------------------------------- DAMAGECODE-----------------------------------------------------%>
<%-------------------------------------------------------------------------------------------------------------%>
<div class="subform" style="width:98%">
<span class="labelblue"><fmt:message
				key="orderdetail.repair.label.damagecode.title" /></span>
                          <div class="subformlist" id="damageCodeDiv" style="height:40px;clear:both;font-size:12px;">
		<jsp:include page="OrderSpecialDamageCodeTile.jsp"></jsp:include>
		</div>
		<div style="margin-top:4px;width:98%">
		<table class="noborder" style="vertical-align: top">
		<tr>
		<td>
				<input type="text" id="damageCodeDummy"  value="" />
				</td>
				<td>
					<input id="addDamageCdButton" disabled type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.hinzufuegen"/>" onclick="addDamageCd()"/>
				</td>
				</tr>
			</table>
		</div>
</div>           			
					
			</td>
			</tr>

		<tr>
			<td align="right">
			<div id="saveDiv"></div>
			</td>
			<td align="right"><input type="button" class="ViewButtonStyle"
				value="<fmt:message key="orderspecial.button.cancel"/>"
				onClick="hidePopup()" id="SpecialCancelButton"> <input type="button"
				class="ViewButtonStyle"
				value="<fmt:message key="orderspecial.button.save"/>"
				onClick="saveOrder('<%=OrderSpecialAction.FORWARD_SAVE_ORDER%>')"
				id="SpecialSaveButton">
			<input type="button" class="ViewButtonStyle"
				value="<fmt:message key="orderspecial.button.save.transmit"/>"
				onClick="saveOrder('<%=OrderSpecialAction.FORWARD_SAVE_TRANSMIT_ORDER%>')"
				id='SpecialSaveAndTransmitButton'></td>
		</tr>

	</table>
</html:form></div>

<%-- END $RCSfile: OrderSpecialTile.jsp,v $ --%>
