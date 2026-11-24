<%@ include file="../includeTLDs.jsp" %>
<html:form action="dispokarte/Dispokarte.do" styleId="ShipToForm" onsubmit="return false;">
<html:hidden property="formAction" value=""/>
<%@ taglib uri="http://com.crh.logon/tags/order-taglib.tld" prefix="crh-order" %>
<%@page import="com.crh.logon.order.web.forms.dispokarte.DispokarteForm"%>
<%@page import="com.crh.logon.order.taglib.DispokarteInvalidAttributeMarkerTag"%>
<%@page import="com.crh.logon.constants.ShiptoSalesAreaError"%>
    <%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>
<%@page import="com.crh.logon.persistence.entities.common.ShiptoSareaEntity" %>
	<%@ page import="com.crh.logon.persistence.entities.common.SareaEntity" %>

<c:set var="reloadTabFn">javascript:tabChoose('TabSalesArea<c:out value="${order_dispokarte_DispokarteForm.currSalesAreaIndex}"/>')</c:set>

<script type="text/javascript">
	
	function modifyRestrictions(params){
		addFormElements(params, "ShipToForm");
		var diva = Ext.get('DivList');
		diva.load({
			url: 'Dispokarte.do', 
			params: params,
			text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
			scripts: true
		});
	}

	function changeDeliveryType(params){
		addFormElements(params, "ShipToForm");
		var diva = Ext.get('DivList');
		diva.load({
			url: 'Dispokarte.do', 
			params: params,
			text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
			scripts: true
		});
	}
	

  	var applyNdLeadtimeChkUntilMi;
  	var applySdLeadtimeChkFromMi;
  	var h1EarlyOrderLimit;
  	var h2EarliestDeliveryFrom;
  	var sareaH1EarlyOrderLimit;
  	var sareaH2EarliestDeliveryFrom;
	
  	function reloadDropdown() {
  		applyNdLeadtimeChkUntilMi = new Ext.form.TimeField({
  			id: 'applyNdLeadtimeChkUntilMi',
  	    	increment: 15,
  	    	format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
  	    	applyTo: 'applyNdLeadtimeChkUntilMi',
  	    	width: 67
  		});
  		applySdLeadtimeChkFromMi = new Ext.form.TimeField({
  			id: 'applySdLeadtimeChkFromMi',
  	    	increment: 15,
  	    	format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
  	    	applyTo: 'applySdLeadtimeChkFromMi',
  	    	width: 67
  		});
		h1EarlyOrderLimit = new Ext.form.TimeField({
			id: 'h1EarlyOrderLimit',
			increment: 15,
			format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
			applyTo: 'h1EarlyOrderLimit',
			width: 67
		});
		h2EarliestDeliveryFrom = new Ext.form.TimeField({
			id: 'h2EarliestDeliveryFrom',
			increment: 15,
			format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
			applyTo: 'h2EarliestDeliveryFrom',
			width: 67
		});
		sareaH1EarlyOrderLimit = new Ext.form.TimeField({
			id: 'sareaH1EarlyOrderLimit',
			increment: 15,
			format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
			applyTo: 'sareaH1EarlyOrderLimit',
			disabled : true,
			width: 67
		});
		sareaH2EarliestDeliveryFrom = new Ext.form.TimeField({
			id: 'sareaH2EarliestDeliveryFrom',
			increment: 15,
			format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
			applyTo: 'sareaH2EarliestDeliveryFrom',
			disabled : true,
			width: 67
		});

	}
  	
  	function selectAndDisplayBlockTypeOnSalesArea(centralBlock, salesAreaBlock) {
  		if (centralBlock === 'true') {
  			var centralOrderBlock = '<c:out value="${order_dispokarte_DispokarteForm.soldTo.centralOrderBlock}"/>';
  			var centralDeliveryBlock = '<c:out value="${order_dispokarte_DispokarteForm.soldTo.centralDeliveryBlock}"/>';
  			var centralPostingBlock= '<c:out value="${order_dispokarte_DispokarteForm.soldTo.centralPostingBlock}"/>';

  			displayBlockTypeOnSalesArea(centralOrderBlock, centralDeliveryBlock, centralPostingBlock);
  		}
  		else if (salesAreaBlock === 'true'){
  			var customerOrderBlock = '<c:out value="${order_dispokarte_DispokarteForm.soldToSapBpSarea.customerOrderBlock}"/>';
  			var customerDeliveryBlock = '<c:out value="${order_dispokarte_DispokarteForm.soldToSapBpSarea.customerDeliveryBlock}"/>';
  			var customerPostingBlock = '<c:out value="${order_dispokarte_DispokarteForm.soldToSapBpSarea.customerPostingBlock}"/>';
  			
  			displayBlockTypeOnSalesArea(customerOrderBlock, customerDeliveryBlock, customerPostingBlock);
  		}
  	}
  	
  	function displayBlockTypeOnSalesArea(orderBlock, deliveryBlock, postingBlock) {
  		if(orderBlock === 'true') {
  			Ext.get('orderBlock').addClass('activeSalesAreaBlock');
  		}
  		if(deliveryBlock === 'true') {
  			Ext.get('deliveryBlock').addClass('activeSalesAreaBlock');
  		}
  		if(postingBlock === 'true') {
  			Ext.get('postingBlock').addClass('activeSalesAreaBlock');
  		}
  	}
  	
</script>
<script>
	Ext.onReady(function(){
		reloadDropdown();
		selectAndDisplayBlockTypeOnSalesArea(
				'<c:out value="${order_dispokarte_DispokarteForm.customerCentralBlocked}"/>',
				'<c:out value="${order_dispokarte_DispokarteForm.customerSalesAreaBlocked}" />');
	});
</script>
<table cellpadding="0" class="big_form2" style="border-spacing: 0px; padding: 0px;">
  <tbody>
  	<tr>
  		<td width="100%" class="subnaviborder" style="padding-bottom:0px">
<table>
	<tr>
		<td style="vertical-align:top">
			<p class="label" style="margin-top: 8px">
				<fmt:message key="dispokarte.texte.moeglicheHaendler"/>
			</p>
			<div class="scrollist" id="DivHaendler" style="height: 70px; width:250px;">
				<table class="classic" style="border:none">
				<% int activeBilltoIdx = 0; %>
				<c:forEach var="billto" varStatus="status" items="${order_dispokarte_DispokarteForm.currSalesAreaBean.billtos}">
					<tr class="zebra<c:out value="${status.count % 2}"/>">
						<td valign="middle" style="width:1px">
							<html:checkbox disabled="${billto.billtoMarkedForDeletion eq 1}" property="currSalesAreaBean.shiptoBillto[${status.count - 1}]" value="true" onclick="modifyRestrictions({billtosChanged:''})"/>
						</td> 	
						<td style="text-overflow: ellipsis; overflow: hidden; white-space: nowrap; display: block; width: 214px;">
						<c:if test="${billto.billtoMarkedForDeletion eq 1}"><del></c:if>
						<crh-order:dispokarteInvalidAttributeMarker screen="<%=DispokarteInvalidAttributeMarkerTag.DISPOKARTE %>" attributeObject="<%=((DispokarteForm)request.getSession().getAttribute("order_dispokarte_DispokarteForm")).getCurrSalesAreaBean().getBilltos().get(activeBilltoIdx++) %>" checkForErrorCode="<%=ShiptoSalesAreaError.SHIPTO_BILLTO%>">
							<c:out value="${billto.billtoName} ${billto.billtoName2} (${billto.billtoNr})"/> 
						</crh-order:dispokarteInvalidAttributeMarker>
						<c:if test="${billto.billtoMarkedForDeletion eq 1}"></del></c:if>
						</td>
					</tr>
					<c:set var="lastStatusCount" value="${status.count}" />
				</c:forEach>
				</tr>
			</table>
			</div>  
			<div style="width:250px">
			<table class="classic" style="border:none">
				<tr class="zebra<c:out value="${(lastStatusCount + 1) % 2}"/>">
					<td valign="middle" style="width:1px">
						<html:checkbox property="currSalesAreaBean.shiptoSarea.freeDeliveryFlag" styleId="currSalesAreaBean.shiptoSarea.freeDeliveryFlag" value="true" onclick="${reloadTabFn}"/>
					</td>
					<td>
						<fmt:message key="dispokarte.texte.freedelivery"/>
					</td>
				</tr>
			</table>
			</div>
		</td>
		<td style="vertical-align:top">
			<table>
				<tr>
					<td>
						<p class="label" style="margin-top: 2px">
							<fmt:message key="dispokarte.texte.salesOffice"/>
						</p>
					</td>
					<td>
				<crh-order:dispokarteInvalidAttributeMarker screen="<%=DispokarteInvalidAttributeMarkerTag.DISPOKARTE %>" attributeObject="<%=((DispokarteForm)request.getSession().getAttribute("order_dispokarte_DispokarteForm")).getCurrSalesAreaBean().getShiptoSarea() %>" checkForErrorCode="<%=ShiptoSalesAreaError.SAP_BP_SOFFICE %>">
						<c:out value="${order_dispokarte_DispokarteForm.currSalesAreaBean.sapBpSarea.sofficeCd}"/>
						/
						<c:out value="${order_dispokarte_DispokarteForm.currSalesAreaBean.sapBpSarea.sofficeName}"/>
				</crh-order:dispokarteInvalidAttributeMarker>
					</td>
					<td>
					</td>
					<td>
				</tr>
				<tr>
					<td>
						<p class="label" style="margin-top: 2px">
							<fmt:message key="dispokarte.texte.salesGroup"/>
						</p>
					</td>
					<td>
				<crh-order:dispokarteInvalidAttributeMarker screen="<%=DispokarteInvalidAttributeMarkerTag.DISPOKARTE %>" attributeObject="<%=((DispokarteForm)request.getSession().getAttribute("order_dispokarte_DispokarteForm")).getCurrSalesAreaBean().getShiptoSarea() %>" checkForErrorCode="<%=ShiptoSalesAreaError.SALESGROUP %>">
						<c:out value="${order_dispokarte_DispokarteForm.currSalesAreaBean.sapBpSarea.sgroupCd}"/>
						/
						<c:out value="${order_dispokarte_DispokarteForm.currSalesAreaBean.sapBpSarea.sgroupName}"/>
				</crh-order:dispokarteInvalidAttributeMarker>
					</td>
					<td>
					</td>
					<td>
					</td>
				</tr>
				<tr>
					<td>
						<p class="label" style="margin-top: 2px">
							<fmt:message key="dispokarte.texte.distributionDest"/>
						</p>
					</td>
					<td>
<html:select
		property="currSalesAreaBean.shiptoSarea.distributionDest.distributionDestCd" style="width:150px;"
		styleId="currSalesAreaBean.shiptoSarea.distributionDest.distributionDestCd">
		<c:forEach var="distributionDestTx" varStatus="distDestStatus"
			items="${order_dispokarte_DispokarteForm.distributionDestTxList}">
			<html:option value="${distributionDestTx.distributionDestTxPK.distributionDestBs.distributionDestCd}">
				<c:out value="${distributionDestTx.distributionDestTxPK.distributionDestBs.distributionDestCd}"/> - <c:out value="${ distributionDestTx.txt}" />
			</html:option>
		</c:forEach>
	</html:select>
					</td>
					<c:choose>
      					<c:when test="${order_dispokarte_DispokarteForm.customerCentralBlocked}">							
      						<td>
								<p class="label" style="margin-top: 2px">
									<fmt:message key="dispokarte.text.centralBlock"/>
								</p>
							</td>
							<td>
								<div id="salesAreaBlockTypes">
									<span id="orderBlock">Order</span> / <span id="deliveryBlock"> Delivery </span> / <span id="postingBlock">Posting</span>
								</div>
							</td>
      					</c:when>
      					<c:when test="${order_dispokarte_DispokarteForm.customerSalesAreaBlocked}">							
							<td>
								<p class="label" style="margin-top: 2px">
									<fmt:message key="dispokarte.text.salesAreaBlock"/>
								</p>
							</td>
							<td>
								<div id="salesAreaBlockTypes">
									<span id="orderBlock">Order</span> / <span id="deliveryBlock"> Delivery </span> / <span id="postingBlock">Posting</span>
								</div>
							</td>
      					</c:when>
      					<c:otherwise>
      					</c:otherwise>
					</c:choose>      					
				</tr>				
				<tr>
					<td>
						<p class="label" style="margin-top: 2px">
							<fmt:message key="dispokarte.texte.holdback"/>
						</p>
					</td>
					<td>
						<html:select property="currSalesAreaBean.shiptoSarea.releaseSapOptionalFlag" style="width:150px;" onchange="${reloadTabFn}" styleId="currSalesAreaBean.shiptoSarea.releaseSapOptionalFlag" >
							<html:option value="true"><fmt:message key="dispokarte.texte.optional"/></html:option>
							<html:option value="false"><fmt:message key="dispokarte.texte.automatic"/></html:option>
						</html:select>
					</td>
				</tr>
			</table>
		</td>
		<td>
			<table>
				<tr>
					<td>
						<p class="label" style="margin-top: 2px">
							<fmt:message key="salesarea.text.dbltActiveFlag" />:
						</p>
					</td>
					<td>
						<fmt:message key="salesarea.text.active" />  <html:checkbox
							property="currSalesAreaBean.distanceValidationGuiBeanShipto.dbltActiveFlag"
							styleId="currSalesAreaBean.distanceValidationGuiBeanShipto.dbltActiveFlag"  value="true"/>
							<%--struts trick, set the value here to false otherwise not possibe to uncheck the checkbox--%>
							<c:set target="${order_dispokarte_DispokarteForm.currSalesAreaBean.distanceValidationGuiBeanShipto}" property="dbltActiveFlag" value="false"/>
					</td>
					<td>
						<p class="label" style="margin-top: 2px">
							<fmt:message key="shiptosarea.text.selesarea.setting" />:
						</p>
					</td>
				</tr>
				<tr>
					<td>
						<p class="label" style="margin-top: 2px">
							<fmt:message key="salesarea.text.l1" />:
						</p>
					</td>
					<td>
						<html:text styleId="currSalesAreaBean.distanceValidationGuiBeanShipto.dbltDistanceL1" property="currSalesAreaBean.distanceValidationGuiBeanShipto.dbltDistanceL1"
								size="<%= String.valueOf(ShiptoSareaEntity.DBSIZE_DBLT_DISTANCE_L1) %>" maxlength="<%=
								String.valueOf(ShiptoSareaEntity.DBSIZE_DBLT_DISTANCE_L1) %>"/>
					</td>
					<td>
						<html:text disabled="true" styleId="currSalesAreaBean.distanceValidationGuiBeanSarea.dbltDistanceL1"
								property="currSalesAreaBean.distanceValidationGuiBeanSarea.dbltDistanceL1"
								size="<%= String.valueOf(SareaEntity.DBSIZE_DBLT_DISTANCE_L1) %>" maxlength="<%=
								String.valueOf(SareaEntity.DBSIZE_DBLT_DISTANCE_L1) %>"/>
					</td>
				</tr>
				<tr>
					<td>
						<p class="label" style="margin-top: 2px">
							<fmt:message key="salesarea.text.l2" />:
						</p>
					</td>
					<td>
						<html:text styleId="currSalesAreaBean.distanceValidationGuiBeanShipto.dbltDistanceL2"
								property="currSalesAreaBean.distanceValidationGuiBeanShipto.dbltDistanceL2"
								size="<%= String.valueOf(ShiptoSareaEntity.DBSIZE_DBLT_DISTANCE_L2) %>" maxlength="<%=
								String.valueOf(ShiptoSareaEntity.DBSIZE_DBLT_DISTANCE_L2) %>"/>
					</td>
					<td>
						<html:text disabled="true" styleId="currSalesAreaBean.distanceValidationGuiBeanSarea.dbltDistanceL2"
								property="currSalesAreaBean.distanceValidationGuiBeanSarea.dbltDistanceL2"
								size="<%= String.valueOf(SareaEntity.DBSIZE_DBLT_DISTANCE_L2) %>" maxlength="<%=
								String.valueOf(SareaEntity.DBSIZE_DBLT_DISTANCE_L2) %>"/>
					</td>
				</tr>
				<tr>
					<td>
						<p class="label" style="margin-top: 2px">
							<fmt:message key="salesarea.text.h1" />:
						</p>
					</td>
					<td>
						<html:text style="width:65px;" property="currSalesAreaBean.distanceValidationGuiBeanShipto.h1EarlyOrderLimit" styleId="h1EarlyOrderLimit"/>
					</td>
					<td>
						<html:text style="width:65px;" property="currSalesAreaBean.distanceValidationGuiBeanSarea.h1EarlyOrderLimit"
								styleId="sareaH1EarlyOrderLimit"/>
					</td>
				</tr>
				<tr>
					<td>
						<p class="label" style="margin-top: 2px">
							<fmt:message key="salesarea.text.h2" />:
						</p>
					</td>
					<td>
						<html:text style="width:65px;" property="currSalesAreaBean.distanceValidationGuiBeanShipto.h2EarliestDeliveryFrom" styleId="h2EarliestDeliveryFrom"/>
					</td>
					<td>
						<html:text style="width:65px;" property="currSalesAreaBean.distanceValidationGuiBeanSarea.h2EarliestDeliveryFrom"
								styleId="sareaH2EarliestDeliveryFrom"/>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<p class="label" style="margin-top: 2px">
							<fmt:message key="salesarea.text.offsets" />:
						</p>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						D1: <html:text styleId="currSalesAreaBean.distanceValidationGuiBeanShipto.dbltOffsetD1"
								property="currSalesAreaBean.distanceValidationGuiBeanShipto.dbltOffsetD1"
								size="2" maxlength="2"/>
						D2: <html:text styleId="currSalesAreaBean.distanceValidationGuiBeanShipto.dbltOffsetD2"
								property="currSalesAreaBean.distanceValidationGuiBeanShipto.dbltOffsetD2"
								size="2" maxlength="2"/>
						D3: <html:text styleId="currSalesAreaBean.distanceValidationGuiBeanShipto.dbltOffsetD3"
								property="currSalesAreaBean.distanceValidationGuiBeanShipto.dbltOffsetD3"
								size="2" maxlength="2"/>
						D4: <html:text styleId="currSalesAreaBean.distanceValidationGuiBeanShipto.dbltOffsetD4"
								property="currSalesAreaBean.distanceValidationGuiBeanShipto.dbltOffsetD4"
								size="2" maxlength="2"/>
						D5: <html:text styleId="currSalesAreaBean.distanceValidationGuiBeanShipto.dbltOffsetD5"
								property="currSalesAreaBean.distanceValidationGuiBeanShipto.dbltOffsetD5"
								size="2" maxlength="2"/>
						D6: <html:text styleId="currSalesAreaBean.distanceValidationGuiBeanShipto.dbltOffsetD6"
								property="currSalesAreaBean.distanceValidationGuiBeanShipto.dbltOffsetD6"
								size="2" maxlength="2"/>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						D1: <html:text disabled="true" styleId="currSalesAreaBean.distanceValidationGuiBeanSarea.dbltOffsetD1"
							property="currSalesAreaBean.distanceValidationGuiBeanSarea.dbltOffsetD1"
							size="2" maxlength="2"/>
						D2: <html:text disabled="true" styleId="currSalesAreaBean.distanceValidationGuiBeanSarea.dbltOffsetD2"
							property="currSalesAreaBean.distanceValidationGuiBeanSarea.dbltOffsetD2"
							size="2" maxlength="2"/>
						D3: <html:text disabled="true" styleId="currSalesAreaBean.distanceValidationGuiBeanSarea.dbltOffsetD3"
							property="currSalesAreaBean.distanceValidationGuiBeanSarea.dbltOffsetD3"
							size="2" maxlength="2"/>
						D4: <html:text disabled="true" styleId="currSalesAreaBean.distanceValidationGuiBeanSarea.dbltOffsetD4"
							property="currSalesAreaBean.distanceValidationGuiBeanSarea.dbltOffsetD4"
							size="2" maxlength="2"/>
						D5: <html:text disabled="true" styleId="currSalesAreaBean.distanceValidationGuiBeanSarea.dbltOffsetD5"
							property="currSalesAreaBean.distanceValidationGuiBeanSarea.dbltOffsetD5"
							size="2" maxlength="2"/>
						D6: <html:text disabled="true" styleId="currSalesAreaBean.distanceValidationGuiBeanSarea.dbltOffsetD6"
							property="currSalesAreaBean.distanceValidationGuiBeanSarea.dbltOffsetD6"
							size="2" maxlength="2"/>
					</td>
				</tr>
			</table>
		</td>
		<td style="vertical-align:top">
			<table>
				<tr>
					<td>
						<p class="label" style="margin-top: 2px">
							<fmt:message key="salesarea.text.sdltAndNdltActive" />:
						</p>
					</td>
					<td>
						<fmt:message key="salesarea.text.active" />  <html:checkbox property="currSalesAreaBean.shiptoSarea.sdltAndNdltActive"
							styleId="currSalesAreaBean.shiptoSarea.sdltAndNdltActive" value="true"/>
							<%--struts trick, set the value here to false otherwise not possibe to uncheck the checkbox--%>
						<c:set target="${order_dispokarte_DispokarteForm.currSalesAreaBean.shiptoSarea}" property="sdltAndNdltActive" value="false"/>
					</td>
				</tr>
				<tr>
					<td>
						<p class="label" style="margin-top: 2px">
							<fmt:message key="salesarea.text.applySdLeadtimeChkFromMi" />:
						</p>
					</td>
					<td>
							<html:text style="width:65px;" property="currSalesAreaBean.applySdLeadtimeChkFromMi" styleId="applySdLeadtimeChkFromMi"/>
					</td>
				</tr>
				<tr>
					<td>
						<p class="label" style="margin-top: 2px">
							<fmt:message key="salesarea.text.applyNdLeadtimeChkUntilMi" />:
						</p>
					</td>
					<td>
						<html:text style="width:65px;" property="currSalesAreaBean.applyNdLeadtimeChkUntilMi" styleId="applyNdLeadtimeChkUntilMi"/>
					</td>
				</tr>
			</table>
		</td>
	<tr>
</table>


		</td>
	</tr>
	<tr>
		<td  class="border" style="padding-top:0px; border-top:none;" >
	  		<div id="DivList">				
				<jsp:include page="ListLose.jsp"/>
	  		</div>

		</td>
	</tr>
  </tbody>
</table>
</html:form>