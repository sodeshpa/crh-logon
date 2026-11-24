<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true" %>

<%@ include file="../includeTLDs.jsp" %>
<%@ page import="com.crh.logon.admin.web.actions.masterdata.ShippingPointCreatePopupAction" %>
<%@ page import="com.crh.logon.admin.web.forms.masterdata.ShippingPointCreatePopupForm" %>
<%@ page import="com.crh.logon.persistence.entities.common.ShippingpointEntity" %>

<c:set var="formBean" value="${sessionScope.admin_ShippingPointCreatePopupForm.shippingPointCreateBean}"/>
<c:set var="plantCd" value="${formBean.plantCd}"/>
<c:set var="plantName" value="${formBean.plantName}"/>

<script type="text/javascript">
	Ext.get('buttonSave').on('click', function() {
		var shippingPointCreatePopupDiv = Ext.get('ShippingPointCreatePopupDiv');
		var submitParams = {};
		storeValueAsHiddenField('ShippingPointCreatePopupForm', 'createFormAction', '<%= ShippingPointCreatePopupAction.FORWARD_SAVE %>');
		addFormElements(submitParams, "<%= ShippingPointCreatePopupForm.SESSION_SHIPPING_POINT_CREATE_POPUP_FORM_NAME %>");

		shippingPointCreatePopupDiv.load( {
			url: '<c:url value="ShippingPointCreatePopup.do"/>',
			params: submitParams,
			text: '<fmt:message key="extjs.loading"/>',
			scripts: true
		} );
	} );
	
	Ext.get('buttonCancel').on('click', function() {
		hidePopup();
	} );
</script>

<div id="ShippingPointCreatePopupDiv">
	<html:form action="ShippingPointCreatePopup.do" onsubmit="return false;" styleId="ShippingPointCreatePopupForm">
		<html:hidden property="createFormAction" value="" />
		<html:hidden property="shippingPointCreateBean.plantCd" value="${plantCd}" />
		<html:hidden property="shippingPointCreateBean.plantName" value="${plantName}" />
		<table class="classicblue" style="border:none; width:auto">
			<tr>
				<th colspan="2">
					<fmt:message key="shippingpoint.popup.create.title"/>
				</th>
			</tr>
			<tr>
				<td class="popupcontent">
					<table width="100%">
						<tr>
							<td class="label right">
								<fmt:message key="shippingpoint.field.plantCode" />:
							</td>
							<td>
								<c:out value="${plantCd}" />
							</td>
						</tr>
						<tr>
							<td class="label right">
								<fmt:message key="shippingpoint.field.plants" />:
							</td>
							<td>
								<c:out value="${plantName}" />
							</td>
						</tr>
						<tr>
							<td class="label right">
								<fmt:message key="shippingpoint.field.sapShippingpointNo" />:
							</td>
							<td>
								<html:text styleId="shippingPointCreateBean.sapShippingpointNo" property="shippingPointCreateBean.sapShippingpointNo"
										value="${formBean.sapShippingpointNo}" maxlength="<%= String.valueOf(ShippingpointEntity.DBSIZE_SAP_SHIPPINGPOINT_NO) %>" style="width: 95%;" />
							</td>
						</tr>
						<tr>
							<td class="label right">
								<fmt:message key="shippingpoint.field.shippingpointName" />:
							</td>
							<td>
								<html:text styleId="shippingPointCreateBean.shippingpointName" property="shippingPointCreateBean.shippingpointName"
										value="${formBean.shippingpointName}" maxlength="<%= String.valueOf(ShippingpointEntity.DBSIZE_SHIPPINGPOINT_NAME) %>" style="width: 95%;" />
							</td>
						</tr>
						<tr>
							<td colspan="2" class="buttons right">
								<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.save"/>" id="buttonSave" />
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.cancel"/>" id="buttonCancel" />  
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</html:form>
</div>
