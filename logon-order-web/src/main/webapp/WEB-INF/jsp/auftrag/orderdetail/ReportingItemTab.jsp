<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page
	import="com.crh.logon.order.web.actions.auftrag.OrderDetailAction"%>
<%@page
	import="com.crh.logon.persistence.entities.common.OrderItemEntity"%>
	<%@page import="com.crh.logon.web.utils.Authorization"%>

<%----------------------------------------------------------------------------------------------%>
<%--------------------START AUTHORIZATION DRIVEN DISABLED SETTINGS -----------------------------%>
<%----------------------------------------------------------------------------------------------%>
<c:set var="isReportingDisabled">true</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_REPORT_W %>">
		<c:set var="isReportingDisabled">false</c:set>	
</crh-common:authorize>
<c:set var="isSlFlagDisabled">true</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_REPORT_SERVICE_LEVEL_FLAG %>">
		<c:set var="isSlFlagDisabled">false</c:set>	
</crh-common:authorize>
<c:set var="isRatingCmntCrhDisabled">true</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_REPORT_RATING_COMMNT_CRH %>">
		<c:set var="isRatingCmntCrhDisabled">false</c:set>
</crh-common:authorize>
<c:set var="isRatingCmntCarrDisabled">true</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_REPORT_RATING_COMMNT_CARR %>">
		<c:set var="isRatingCmntCarrDisabled">false</c:set>	
</crh-common:authorize>
<c:set var="isCommentDispatchingDisabled">true</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_REPORT_COMMENT_DISPATCHING %>">
		<c:set var="isCommentDispatchingDisabled">false</c:set>	
</crh-common:authorize>
<c:set var="isCommentDriverDisabled">true</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_REPORT_COMMENT_DRIVER%>">
		<c:set var="isCommentDriverDisabled">false</c:set>	
</crh-common:authorize>
<c:set var="isCommentShippingDisabled">true</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_REPORT_COMMENT_SHIPPING %>">
		<c:set var="isCommentShippingDisabled">false</c:set>	
</crh-common:authorize>
<%----------------------------------------------------------------------------------------------%>
<%--------------------END AUTHORIZATION DRIVEN DISABLED SETTINGS -------------------------------%>
<%----------------------------------------------------------------------------------------------%> 

<!--<html:form action="auftrag/OrderDetail.do"  onsubmit="return false;" >-->
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page"
	value="${order_auftrag_OrderDetailForm}" />
	
<table border="0" cellspacing="0" cellpadding="0" width="99%">
	<colgroup>
		<col width="49%">
		<col width="1%">
		<col width="50%">
	</colgroup>
	<tr>
		<td>
		<div class="subform"><span class="labelblue"><fmt:message key="orderdetail.rep.label.process" /></span><br>
		<table border="0" cellspacing="0" cellpadding="0" width="98%">
			<colgroup>
				<col width="30%">
				<col width="34%">
				<col width="34%">
				<col width="2%">
			</colgroup>
			<thead>
				<tr>
					<th style="white-space: nowrap; border: 0px;">&nbsp;</th>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.plan" /></span></td>	
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.actual" /></span></td>	
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.ab" /></span></td>							
				</tr>
			</thead>
			<tbody>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.quantity" /></span></td>	
					<td valign="bottom"><span class="textfielddisabled_h" style="width: 77px;" title="<c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList[order_auftrag_OrderDetailForm.reportingBean.selOrderItemIndex].quantityPlan}" />"> <c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList[order_auftrag_OrderDetailForm.reportingBean.selOrderItemIndex].quantityPlan}" />&nbsp; </span></td>						
					<td valign="bottom"><span class="textfielddisabled_h" style="width: 77px;" title="<c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList[order_auftrag_OrderDetailForm.reportingBean.selOrderItemIndex].quantityActual}" />"> <c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList[order_auftrag_OrderDetailForm.reportingBean.selOrderItemIndex].quantityActual}" />&nbsp; </span></td>	
				
					<td style="white-space: nowrap; border: 0px; text-align:right;"><html:checkbox
						property="reportingBean.orderItemFBeanList[${order_auftrag_OrderDetailForm.reportingBean.selOrderItemIndex}].quantityFlag" disabled="true" /></td>
				</tr>
				<tr> 
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.delivery.type" /></span></td>	
					<td valign="bottom"><span class="textfielddisabled_h" style="width: 77px;" title="<c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList[order_auftrag_OrderDetailForm.reportingBean.selOrderItemIndex].deliveryTypePlan}" />"> <c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList[order_auftrag_OrderDetailForm.reportingBean.selOrderItemIndex].deliveryTypePlan}" />&nbsp; </span></td>						
					<td valign="bottom"><span class="textfielddisabled_h" style="width: 77px;" title="<c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList[order_auftrag_OrderDetailForm.reportingBean.selOrderItemIndex].deliveryTypeActual}" />"> <c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList[order_auftrag_OrderDetailForm.reportingBean.selOrderItemIndex].deliveryTypeActual}" />&nbsp; </span></td>	
					<td style="white-space: nowrap; border: 0px; text-align:right;"><html:checkbox
						property="reportingBean.orderItemFBeanList[${order_auftrag_OrderDetailForm.reportingBean.selOrderItemIndex}].deliveryTypeFlag" disabled="true" /></td>
				</tr>
			</tbody>
		</table>
		<table border="0" cellspacing="0" cellpadding="0" width="98%">
			<colgroup>
				<col width="98%">
				<col width="2%">
			</colgroup>
			<tbody>					
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.based.on.dispocard" /></span></td>				
					<td style="white-space: nowrap; border: 0px; text-align:right;"><html:checkbox
						property="reportingBean.orderItemFBeanList[${order_auftrag_OrderDetailForm.reportingBean.selOrderItemIndex}].basedOnDispocard" disabled="true" /></td>
				</tr>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.pda.delivery" /></span></td>	
					<td style="white-space: nowrap; border: 0px;"><html:checkbox
						property="reportingBean.noPdaDeliveryFlag" disabled="true" /></td>
				</tr>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.loaded.by.lds" /></span></td>	
					<td style="white-space: nowrap; border: 0px; text-align:right;"><html:checkbox
						property="reportingBean.orderItemFBeanList[${order_auftrag_OrderDetailForm.reportingBean.selOrderItemIndex}].loadedByLds" disabled="true" /></td>
				</tr>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.truck.rerouting" /></span></td>				
					<td style="white-space: nowrap; border: 0px;"><html:checkbox
						property="reportingBean.truckReroutingFlag" disabled="true" /></td>
				</tr>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.short.term.order" /></span></td>
					<td style="white-space: nowrap; border: 0px;"><html:checkbox
						property="reportingBean.shortTermOrderFlag" disabled="true" /></td>
				</tr>
			</tbody>
		</table>
		<table border="0" cellspacing="0" cellpadding="0" width="98%">
			<colgroup>
				<col width="65%">
				<col width="30%">
				<col width="5%">
			</colgroup>
			<tbody>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.order.mng" /></span></td>
					<td valign="bottom"><span class="textfielddisabled" style="text-align:right;"> <c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.reactionTimeOrderMng}" />&nbsp; </span></td>		
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.min" /></span></td>
				</tr>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.pool.mng" /></span></td>
					<td valign="bottom"><span class="textfielddisabled" style="text-align:right;"> <c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.reactionTimePoolMng}" />&nbsp; </span></td>		
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.min" /></span></td>	
				</tr>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.dispatcher" /></span></td>
					<td valign="bottom"><span class="textfielddisabled" style="text-align:right;"> <c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.reactionTimeDispatcher}" />&nbsp; </span></td>		
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.min" /></span></td>
				</tr>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.truck.driver" /></span></td>
					<td valign="bottom"><span class="textfielddisabled" style="text-align:right;"> <c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.reactionTimeTruckDriver}" />&nbsp; </span></td>		
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.min" /></span></td>	
				</tr>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.time.window" /></span></td>
					<td valign="bottom"><span class="textfielddisabled" style="text-align:right;"> <c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.reactionDelivTimeWindow}" />&nbsp; </span></td>		
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.min" /></span></td>	
				</tr>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.laoding.time" /></span></td>
					<td valign="bottom"><span class="textfielddisabled" style="text-align:right;"> <c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.loadingTime}" />&nbsp; </span></td>		
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.min" /></span></td>
				</tr> 
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.driving.time" /></span></td>
					<td valign="bottom"><span class="textfielddisabled" style="text-align:right;"> <c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.actualDrivingTime}" />&nbsp; </span></td>		
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.min" /></span></td>	
				</tr>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.unlaoding.time" /></span></td>
					<td valign="bottom"><span class="textfielddisabled" style="text-align:right;"> <c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.unloadingTime}" />&nbsp; </span></td>		
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.min" /></span></td>
				</tr>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.ship.point" /></span></td>
					<td valign="bottom"><span class="textfielddisabled" style="text-align:right;"> <c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.cycleTimeShipPoint}" />&nbsp; </span></td>		
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.min" /></span></td>
				</tr>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.shipto" /></span></td>
					<td valign="bottom"><span class="textfielddisabled" style="text-align:right;"> <c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.cycleTimeShipto}" />&nbsp; </span></td>		
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.min" /></span></td>	
				</tr>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.weighing.type" /></span></td>
					<td colspan="2" valign="bottom"><span class="textfielddisabled" > <c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList[order_auftrag_OrderDetailForm.reportingBean.selOrderItemIndex].weighingType}" />&nbsp; </span></td>						
				</tr>
			</tbody>
		</table>

	</div>
	</td>
	<td>
	<c:set var="lc">
		<c:out value="${order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList[order_auftrag_OrderDetailForm.reportingBean.selOrderItemIndex].lc}" />
	</c:set>
	<div class="subform"><span class="labelblue"><fmt:message key="orderdetail.rep.label.payment" /></span><br>
	<table border="0" cellspacing="0" cellpadding="0" width="98%">
		<colgroup>
			<col width="35%">
			<col width="20%">
			<col width="25%">
			<col width="20%">
		</colgroup>
		<tbody>
			<tr>
				<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.net.weight" /></span></td>		
				<td valign="bottom"><span class="textfielddisabled" style="text-align:right;"> <c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList[order_auftrag_OrderDetailForm.reportingBean.selOrderItemIndex].netWeight}" />&nbsp; </span></td>	
				<td valign="bottom"><span class="formtitle"><c:out value="${lc}" />/<fmt:message key="orderdetail.rep.field.tons" /></span></td>
				<td valign="bottom"><span class="textfielddisabled" style="text-align:right;"> <c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList[order_auftrag_OrderDetailForm.reportingBean.selOrderItemIndex].netWeightLC}" />&nbsp; </span></td>						
			</tr>
			<tr>
				<td valign="bottom"><span class="formtitle"><fmt:message 
						key="orderdetail.rep.field.distance" /></span></td>	
				<td valign="bottom"><span class="textfielddisabled" style="text-align:right;"> <c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList[order_auftrag_OrderDetailForm.reportingBean.selOrderItemIndex].distance}" />&nbsp; </span></td>	
				<td valign="bottom"><span class="formtitle"><c:out value="${lc}" />/<fmt:message key="orderdetail.rep.field.km" /></span></td>
				<td valign="bottom"><span class="textfielddisabled" style="text-align:right;"> <c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList[order_auftrag_OrderDetailForm.reportingBean.selOrderItemIndex].distanceLC}" />&nbsp; </span></td>	
			</tr>
			<tr>
				<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.capacity" /></span></td>	
				<td valign="bottom"><span class="textfielddisabled" style="text-align:right;"> <c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList[order_auftrag_OrderDetailForm.reportingBean.selOrderItemIndex].quantity}" />&nbsp; </span></td>	
				<td valign="bottom"><span class="formtitle"><c:out value="${lc}" />/<fmt:message key="orderdetail.rep.field.tkm" /></span></td>
				<td valign="bottom"><span class="textfielddisabled" style="text-align:right;"> <c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.orderItemFBeanList[order_auftrag_OrderDetailForm.reportingBean.selOrderItemIndex].quantityLC}" />&nbsp; </span></td>	
			</tr>
		</tbody>
	</table>

	</div>


	<div class="subform" style="margin-top: 4px;">
	<table border="0" cellspacing="0" cellpadding="0" width="98%">
		<colgroup>
			<col width="17%">
			<col width="32%">
			<col width="17%">
			<col width="32%">
			<col width="2%">
		</colgroup>
		<thead>
			<tr>
				<th style="white-space: nowrap; border: 0px;">
					<span class="labelblue">
						<fmt:message key="orderdetail.rep.label.service.level" />
					</span>
				</th>
			</tr>
		</thead>
		<tbody>
			<tr>			
				<td valign="bottom"><span class="formtitle">
						<fmt:message key="orderdetail.rep.field.end.load" /></span></td>									
				<td valign="bottom"><span class="textfielddisabled_h" style="width: 90px;" title="<c:out value="${order_auftrag_OrderDetailForm.reportingBean.endLoad}" />">
						<c:out value="${order_auftrag_OrderDetailForm.reportingBean.endLoad}" />&nbsp;</span></td>						
				<td valign="bottom"><span class="formtitle">
						<fmt:message key="orderdetail.rep.field.delivery.to" /></span></td>												
				<td colspan="2" valign="bottom"><span class="textfielddisabled_h" style="width: 90px;" title="<c:out value="${order_auftrag_OrderDetailForm.reportingBean.deliveryTo}" />">
						<c:out value="${order_auftrag_OrderDetailForm.reportingBean.deliveryTo}" />&nbsp; </span></td>
			</tr>			
			<tr>			
				<td valign="bottom"><span class="formtitle">
						<fmt:message key="orderdetail.rep.field.enter.st" /></span></td>									
				<td valign="bottom"><span class="textfielddisabled_h" style="width: 90px;" title="<c:out value="${order_auftrag_OrderDetailForm.reportingBean.enterSt}" />">
						<c:out value="${order_auftrag_OrderDetailForm.reportingBean.enterSt}" />&nbsp;</span></td>						
				<td valign="bottom"><span class="formtitle">
						<fmt:message key="orderdetail.rep.field.on.time" /></span></td>												
				<td colspan="2" valign="bottom"><html:checkbox
						property="reportingBean.onTimeFlag" disabled="true" /></td>
			</tr>
			<tr>
				<td valign="bottom" class="label"
					style="white-space: nowrap; border: 0px;">&nbsp;</td>
				<td colspan="2" valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.relevant" /></span></td>	
				<td valign="bottom" style="white-space: nowrap; border: 0px;"><html:checkbox
					disabled="${isSlFlagDisabled}"
					property="reportingBean.relevantServiceLevelFlag" /></td>
			</tr>
			<tr>
				<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.note.crh" /></span></td>
				<td colspan="4" valign="bottom" style="white-space:nowrap; border: 0px;"><html:textarea
				disabled="${isRatingCmntCrhDisabled}"
					property="reportingBean.noteCrh" rows="2"
					style="width: 100%;"></html:textarea></td>
			</tr>
			<tr>
				<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.note.carrier" /></span></td>	
				<td colspan="4" valign="bottom" style="white-space:nowrap; border: 0px;"><html:textarea
				disabled="${isRatingCmntCarrDisabled}"
					property="reportingBean.noteCarrier" rows="2"
					style="width: 100%;"></html:textarea></td>
			</tr>
		</tbody>
	</table>

	</div>


	<div class="subform" style="margin-top: 4px;"><span class="labelblue"><fmt:message key="orderdetail.rep.label.region" /></span><br>
	<table border="0" cellspacing="0" cellpadding="0" width="98%">
		<colgroup>
			<col width="30%">
			<col width="70%">
		</colgroup>
		<tbody>
			<tr>
				<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.sales.district" /></span></td>		
				<td valign="bottom"><span class="textfielddisabled"> <c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.salesDistrict}" />&nbsp; </span></td>	
			</tr>   
			<tr>
				<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.sales.office" /></span></td>		
				<td valign="bottom"><span class="textfielddisabled"> <c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.salesOffice}" />&nbsp; </span></td>	
			</tr>   
			<tr>
				<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.sales.group" /></span></td>		
				<td valign="bottom"><span class="textfielddisabled"> <c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.salesGroup}" />&nbsp; </span></td>	
			</tr>  
			<tr>
				<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.zone" /></span></td>		
				<td valign="bottom"><span class="textfielddisabled"> <c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.zone}" />&nbsp; </span></td>	
			</tr>  		
			<tr>
				<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.region" /></span></td>		
				<td valign="bottom"><span class="textfielddisabled"> <c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.region}" />&nbsp; </span></td>	
			</tr>  		
			<tr>
				<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.rep.field.language" /></span></td>		
				<td valign="bottom"><span class="textfielddisabled"> <c:out
						value="${order_auftrag_OrderDetailForm.reportingBean.language}" />&nbsp; </span></td>	
			</tr>  				
		</tbody>
	</table>

	</div>
	</td>
	</tr>
</table>

<table border="0" cellspacing="0" cellpadding="0" width="98%">
	<colgroup>
		<col width="31%">
		<col width="2%">
		<col width="31%">
		<col width="2%">
		<col width="34%">
	</colgroup>
	<tr>
		<td>
		<div class="subform"><span class="labelblue"><fmt:message key="orderdetail.rep.field.note.disposition" /></span><br>
		<html:textarea disabled="true" property="reportingBean.noteDisposition" rows="2"
			style="width: 100%;"></html:textarea></div>
		</td>
		<td style="white-space: nowrap; border: 0px;">&nbsp;</td>
		<td>
		<div class="subform"><span class="labelblue"><fmt:message key="orderdetail.rep.field.note.driver" /></span><br>
		<html:textarea disabled="true" property="reportingBean.noteDriver" rows="2"
			style="width: 100%;"></html:textarea></div>
		</td>
		<td style="white-space: nowrap; border: 0px;">&nbsp;</td>
		<td>
		<div class="subform"><span class="labelblue"><fmt:message key="orderdetail.rep.field.note.shipping" /></span><br>
		<html:textarea disabled="true" property="reportingBean.noteShipping" rows="2"
			style="width: 100%;"></html:textarea></div>
		</td>
	</tr>
</table>
<!--</html:form>-->

