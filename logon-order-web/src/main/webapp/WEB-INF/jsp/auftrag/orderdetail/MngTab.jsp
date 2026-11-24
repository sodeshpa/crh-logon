<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>

<!--<html:form action="auftrag/OrderDetail.do"  onsubmit="return false;" >-->

<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@page import="com.crh.logon.persistence.entities.common.OrderEntity"%>
<c:set var="form" scope="page" value="${order_auftrag_OrderDetailForm}" />
<c:set var="noPdaOrRailEventAccess">false</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_ORDER_MNG_NOPDAORRAILEVENT %>">
<c:set var="noPdaOrRailEventAccess">true</c:set>
</crh-common:authorize>

<table border="0" cellspacing="0" cellpadding="0" width="99%">
	<colgroup>
		<col width="49%">
		<col width="50%">
	</colgroup>
	<tr>
		<td>
		<div class="subform"><span class="labelblue"><fmt:message
			key="orderdetail.mng.label.control" /></span><br>

		<table border="0" cellspacing="0" cellpadding="0" width="98%">
			<colgroup>
				<col width="98%">
				<col width="2%">
			</colgroup>
			<tbody>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.mng.field.release.sap" /></span></td>
					<td valign="bottom"
						style="white-space: nowrap; border: 0px; align: right;"><html:checkbox
						property="mngBean.releaseSapFlag"
						disabled="${order_auftrag_OrderDetailForm.releaseSapFlagDisabled}" /></td>
				</tr>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
							key="orderdetail.mng.field.pda.delivery" /></span></td>
					<td valign="bottom"
							style="white-space: nowrap; border: 0px; align: right;" title="<fmt:message
						key="orderdetail.mng.field.pda.delivery.title"/>"><html:checkbox
							property="mngBean.noPdaOrRailEventsFlag"
							disabled="${!noPdaOrRailEventAccess || order_auftrag_OrderDetailForm.mngBean.disabledNoPdaOrRailEventsFlag}"
					/></td>
				</tr>
			</tbody>
		</table>

		<table border="0" cellspacing="0" cellpadding="0" width="98%">
			<colgroup>
				<col width="40%">
				<col width="60%">
			</colgroup>
			<tbody>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.mng.field.logon.type" /></span></td>
					<td valign="bottom" class="formtitle"><span
						class="textfielddisabled"> <c:out
						value="${order_auftrag_OrderDetailForm.mngBean.orderType}" />&nbsp;
					</span></td>
				</tr>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.mng.field.distribution.dest" /></span></td>
					<td valign="bottom" class="formtitle"><span
						class="textfielddisabled"> <c:out
						value="${order_auftrag_OrderDetailForm.mngBean.distributionDest}" />&nbsp;
					</span></td>
				</tr>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.mng.field.delivery.type" /></span></td>
					<td valign="bottom" class="formtitle"><span
						class="textfielddisabled"> <c:out
						value="${order_auftrag_OrderDetailForm.mngBean.deliveryType}" />&nbsp;
					</span></td>
				</tr>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.mng.field.shipping.cond" /></span></td>
					<td valign="bottom" class="formtitle"><span
						class="textfielddisabled"> <c:out
						value="${order_auftrag_OrderDetailForm.mngBean.shippingCond}" />&nbsp;
					</span></td>
				</tr>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.mng.field.sbb.customer.number" /></span></td>
					<td valign="bottom" class="formtitle"><span
						class="textfielddisabled"> <c:out
						value="${order_auftrag_OrderDetailForm.mngBean.sbbCustomerNumber}" />&nbsp;
					</span></td>
				</tr>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.mng.field.customer.classification" /></span></td>
					<td valign="bottom" class="formtitle"><span
						class="textfielddisabled"> <c:out
						value="${order_auftrag_OrderDetailForm.mngBean.customerClassification}" />&nbsp;
					</span></td>
				</tr>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.mng.field.customer.group" /></span></td>
					<td valign="bottom" class="formtitle"><span
						class="textfielddisabled"> <c:out
						value="${order_auftrag_OrderDetailForm.mngBean.customerGroup}" />&nbsp;
					</span></td>
				</tr>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.mng.field.abs.class" /></span></td>
					<td valign="bottom" class="formtitle"><span
						class="textfielddisabled"> <c:out
						value="${order_auftrag_OrderDetailForm.mngBean.abcClass}" />&nbsp;
					</span></td>
				</tr>
				<tr>
					<td valign="bottom"><font color="red"><span
						class="formtitle"><fmt:message
						key="orderdetail.mng.field.creation.type" /></span></font></td>
					<td valign="bottom" class="formtitle"><span
						class="textfielddisabled"> <c:out
						value="${order_auftrag_OrderDetailForm.mngBean.orderCreationType}" />&nbsp;
					</span></td>
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
						key="orderdetail.mng.field.sap.inventory.managed" /></span></td>
					<td valign="bottom" style="white-space: nowrap; border: 0px;"><html:checkbox
						property="mngBean.sapInventoryManagedFlag" disabled="true" /></td>
				</tr>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.mng.field.customer.order.block" /></span></td>
					<td valign="bottom" style="white-space: nowrap; border: 0px;"><html:checkbox
						property="mngBean.customerOrderBlockFlag" disabled="true" /></td>
				</tr>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.mng.field.customer.delivery.block" /></span></td>
					<td valign="bottom" style="white-space: nowrap; border: 0px;"><html:checkbox
						property="mngBean.customerDeliveryBlockFlag" disabled="true" /></td>
				</tr>
			</tbody>
		</table>

		<table border="0" cellspacing="0" cellpadding="0" width="98%">
			<colgroup>
				<col width="40%">
				<col width="60%">
			</colgroup>
			<tbody>
				<tr>
					<td valign="bottom"><font color="red"><span
						class="formtitle"><fmt:message
						key="orderdetail.mng.field.validation.code" /></span></font></td>
					<td valign="bottom" class="formtitle"><span
						class="textfielddisabled"> <c:out
						value="${order_auftrag_OrderDetailForm.mngBean.orderValidationCode}" />&nbsp;
					</span></td>
				</tr>
			</tbody>
		</table>
		</div>
		</td>
		
		<td style="white-space: nowrap; border: 0px;">
			<div class="subform" style="width:98%">
				<span class="labelblue">
					<fmt:message key="orderdetail.mng.label.charges"/>
				</span>
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<colgroup>
						<col width="96%">
						<col width="4%">
					</colgroup>
					<c:set var="disableCheckbox" value="true" />					
					<c:set var="disableFreeOfCharge" value="${order_auftrag_OrderDetailForm.mngBean.disabledFreeOfCharge}" />
					<crh-common:authorize function="<%=Authorization.ORDER_DETAIL_ORDER_MNG_MIN_QTY_FLAG%>">
						<c:if test="${order_auftrag_OrderDetailForm.mngBean.sentToSap}" >
							<c:set var="disableCheckbox" value="false" />
						</c:if>
					</crh-common:authorize>
					<tr>
						<td valign="bottom"><span class="formtitle">
							<fmt:message key="orderdetail.mng.field.freeOfChargeCust" /></span>
						</td>		
						<td style="white-space: nowrap; border: 0px; vertical-align: bottom; align: center; text-align: center;">
							<html:checkbox property="mngBean.custFreeOfChargeFlag" disabled="${disableFreeOfCharge}" />
						</td>
					</tr> 
					<tr>
						<td valign="bottom"><span class="formtitle">
							<fmt:message key="orderdetail.mng.field.suppressMinCharge" />
						</td>
						<td style="white-space: nowrap; border: 0px; vertical-align: bottom; align: center; text-align: center;">
							<html:checkbox property="mngBean.sapSuppressMinChargeFlag" disabled="${!disableCheckbox}" />
						</td>
					</tr>
				</table>
			</div>
		<br />	
		
		<div class="subform" style="width:98%"><span class="labelblue"><fmt:message
			key="orderdetail.mng.label.del.note" /></span><br>
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
			<colgroup>
				<col width="42%">
				<col width="50%">
				<col width="4%">
				<col width="4%">
			</colgroup>
			<thead>
				<tr>
					<th colspan="2" style="white-space: nowrap; border: 0px;">&nbsp;</th>
					<th valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.mng.field.bb" /></span></th>		
					<th valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.mng.field.ls" /></span></th>	
				</tr> 
			</thead>
			<tbody>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.mng.field.fax" /></span></td>	
					<td valign="bottom"><span class="textfielddisabled"> <c:out
						value="${order_auftrag_OrderDetailForm.mngBean.faxNumber}" />&nbsp; </span></td>	
					<td
						style="white-space: nowrap; border: 0px; vertical-align: bottom; align: center; text-align: center;"><html:checkbox
						property="mngBean.bbFaxFlag" disabled="true" /></td>
					<td
						style="white-space: nowrap; border: 0px; vertical-align: bottom; align: center; text-align: center;"><html:checkbox
						property="mngBean.lsFaxFlag" disabled="true" /></td>
				</tr>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.mng.field.mail" /></span></td>							
					<td valign="bottom"><span class="textfielddisabled"> <c:out
						value="${order_auftrag_OrderDetailForm.mngBean.emailNumber}" />&nbsp; </span></td>	
					<td
						style="white-space: nowrap; border: 0px; vertical-align: bottom; align: center; text-align: center;"><html:checkbox
						property="mngBean.bbEmailFlag" disabled="true" /></td>
					<td
						style="white-space: nowrap; border: 0px; vertical-align: bottom; align: center; text-align: center;"><html:checkbox
						property="mngBean.lsEmailFlag" disabled="true" /></td>
				</tr>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.mng.field.sms" /></span></td>								
					<td valign="bottom"><span class="textfielddisabled"> <c:out
						value="${order_auftrag_OrderDetailForm.mngBean.smsNumber}" />&nbsp; </span></td>	
					<td
						style="white-space: nowrap; border: 0px; vertical-align: bottom; align: center; text-align: center;"><html:checkbox
						property="mngBean.bbSmsFlag" disabled="true" /></td>
					<td style="white-space: nowrap; border: 0px;">&nbsp;</td>
				</tr>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.mng.field.paper" /></span></td>							
					<td valign="bottom"><span class="textfielddisabled"> <c:out
						value="${order_auftrag_OrderDetailForm.mngBean.paperNumber}" />&nbsp; </span></td>	
					<td style="white-space: nowrap; border: 0px;">&nbsp;</td>
					<td style="white-space: nowrap; border: 0px; vertical-align: top align: center; text-align: center;"><html:checkbox
						property="mngBean.lsPaperFlag" disabled="true" /></td>
				</tr>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.mng.field.signature" /></span></td>					
					<td valign="bottom"><span class="textfielddisabled"> <c:out
						value="${order_auftrag_OrderDetailForm.mngBean.contactPersonSignature}" />&nbsp; </span></td>	
					<td style="white-space: nowrap; border: 0px;">&nbsp;</td>
					<td style="white-space: nowrap; border: 0px;">&nbsp;</td>
				</tr>
				<tr>
					<td valign="bottom"><span class="formtitle"><fmt:message
						key="orderdetail.mng.field.print.type" /></span></td>	
					<td valign="bottom"><span class="textfielddisabled"> <c:out
						value="${order_auftrag_OrderDetailForm.mngBean.delNotePrintType}" />&nbsp; </span></td>	
					<td style="white-space: nowrap; border: 0px;">&nbsp;</td>
					<td style="white-space: nowrap; border: 0px;">&nbsp;</td>
				</tr>
			</tbody>
		</table>
		</div>

		</td>
	</tr>


	<tr>
		<td colspan="3">
		<div class="subform"><span class="labelblue"><fmt:message
			key="orderdetail.mng.label.notes" /></span><br>
		<table border="0" cellspacing="0" cellpadding="0" width="98%">

			<tbody>
				<tr>
					<td>
					<div class="subform"><span class="labelblue"><fmt:message
						key="orderdetail.mng.field.note.order.mgmt" /></span><br>
					<html:textarea property="mngBean.noteOrderMgmt"
						style="height:35px;width: 280px;"></html:textarea></div>
					</td>
					<td style="white-space: nowrap; border: 0px;">&nbsp;</td>
					<td>
					<div class="subform"><span class="labelblue"><fmt:message
						key="orderdetail.mng.field.note.dispatching" /></span><br>
					<html:textarea property="mngBean.noteDispatching" 
						style="height:35px;width: 280px;"></html:textarea></div>
					</td>
				</tr> 
				<tr>
					<td>
					<div class="subform"><span class="labelblue"><fmt:message
						key="orderdetail.mng.field.note.client" /></span><br>
					<html:textarea property="mngBean.noteCustWeborder"
						style="height:35px;width: 280px;"></html:textarea></div>
					</td>
					<td style="white-space: nowrap; border: 0px;">&nbsp;</td>
					<td>
					<div class="subform"><span class="labelblue"><fmt:message
						key="orderdetail.mng.field.note.truck.driver" /></span><br>
					<html:textarea property="mngBean.noteTruckDriver" 
						style="height:35px;width: 280px;"></html:textarea></div>
					</td>
				</tr>
				<tr>
					<td>
					<div class="subform"><span class="labelblue"><fmt:message
						key="orderdetail.mng.field.note.delivery" /></span><br>
					<html:textarea property="mngBean.noteDelivery" 
						style="height:35px;width: 280px;"></html:textarea></div>
					</td>
					<td style="white-space: nowrap; border: 0px;">&nbsp;</td>
					<td>
					<div class="subform"><span class="labelblue"><fmt:message
						key="orderdetail.mng.field.note.railway.disp" /></span><br>
					<html:textarea property="mngBean.noteRailwayDisp" 
						style="height:35px;width: 280px;"></html:textarea></div>
					</td>
				</tr>
			</tbody>
		</table>
		</div>
		</td>
	</tr>

</table>
<!--</html:form>-->
