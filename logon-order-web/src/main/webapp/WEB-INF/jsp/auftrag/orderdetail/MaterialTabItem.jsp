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

<c:set var="orderDetailUrl">
	/../order/auftrag/OrderDetail.do
</c:set>

<!--<html:form action="auftrag/OrderDetail.do"  onsubmit="return false;" >-->
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page"
		value="${order_auftrag_OrderDetailForm}" />
<c:set var="form" scope="page" value="${order_auftrag_OrderDetailForm}" />
<c:set var="selectedOrderItemBean" scope="page"
		value="${form.materialBean.orderItemList[form.materialBean.selOrderItemIndex]}" />

<script type="text/javascript">
function deleteItem(orderItemId) {
	document.getElementById(orderItemId+'_OrderedQuantity').value = 0;
};
</script>


<c:set var="RETURN_QUANT_HANDLING_CREDIT_NOTE_NEEDED"><%=Constant.RETURN_QUANT_HANDLING_CREDIT_NOTE_NEEDED%></c:set>
<c:set var="MATERIAL_PACKAGE_BAG"><%=Constant.MATERIAL_PACKAGE_BAG%></c:set>

<c:set var="PACKAGE_TYPE_SERVICE"><%=Constant.PACKAGE_TYPE_SERVICE%></c:set>
<c:set var="PACKAGE_TYPE_BAG"><%=Constant.PACKAGE_TYPE_BBAG%></c:set>

<c:set var="ORDER_STATUS_DELIVERED"><%=Constant.ORDER_STATUS_DELIVERED%></c:set>
<c:set var="ORDER_STATUS_FINISHED"><%=Constant.ORDER_STATUS_FINISHED%></c:set>
<c:set var="ORDER_STATUS_LOADING"><%=Constant.ORDER_STATUS_LOADING%></c:set>

<%----------------------------------------------------------------------------------------------%>
<%--------------------START AUTHORIZATION DRIVEN DISABLED SETTINGS -----------------------------%>
<%----------------------------------------------------------------------------------------------%>
<c:set var="isQuantityUomChangeDisabled">true</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_MATERIAL_QUANTITY_UOM %>">
<c:set var="isQuantityUomChangeDisabled">false</c:set>
</crh-common:authorize>

<c:set var="isAdjustmentWeightsDisabled">true</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_ADJUSTMENT_WEIGHTS %>">
<c:set var="isAdjustmentWeightsDisabled">false</c:set>
</crh-common:authorize>

<c:set var="isCreditNoteCreateDisabled">true</c:set>
<crh-common:authorize function="<%= Authorization.CREDIT_NOTE_CREATE %>">
<c:set var="isCreditNoteCreateDisabled">false</c:set>
</crh-common:authorize>

<c:set var="isOrdCorrectionWieghtsAndQtyDisabled">true</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_CORRECTION_WEIGHTS_AND_QUANTITIES%>">
<c:set var="isOrdCorrectionWieghtsAndQtyDisabled">false</c:set>
</crh-common:authorize>

<c:set var="isUnapprovedReturnViewAuth">false</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_MATERIAL_UNAPP_RETURN_Q%>">
<c:set var="isUnapprovedReturnViewAuth">true</c:set>
</crh-common:authorize>


<%----------------------------------------------------------------------------------------------%>
<%--------------------END AUTHORIZATION DRIVEN DISABLED SETTINGS -------------------------------%>
<%----------------------------------------------------------------------------------------------%>



<!-- Begin scrollform -->
<c:choose>
	<c:when test="${form.materialTabOpened}">
		<div class="subformlist" style="height: 85px; margin-top: 0px; width: 98%;">
	</c:when>
	<c:otherwise>
		<div class="subformlist" style="height: 254px; margin: 0px; width: 98%;">
	</c:otherwise>
</c:choose>
<table cellpadding="0" cellspacing="0" style="border: 0px; width: 100%;">
	<c:choose>
		<c:when test="${form.materialTabOpened}">
			<colgroup>
				<col width="2%">
				<col width="4%">
				<col width="20%">
				<col width="8%">
				<col width="10%">
				<col width="8%">
				<col width="10%">
				<col width="8%">
				<col width="9%">
				<col width="1%">
				<col width="4%">
				<col width="8%">
				<col width="6%">
				<col width="2%">
			</colgroup>
		</c:when>
		<c:otherwise>
			<colgroup>
				<col width="2%">
				<col width="4%">
				<col width="38%">
				<col width="8%">
				<col width="10%">
				<col width="8%">
				<col width="10%">
				<col width="8%">
				<col width="10%">
				<col width="2%">
			</colgroup>
		</c:otherwise>
    </c:choose>
	<tbody>
		<tr>
		<c:choose>
			<c:when test="${form.materialTabOpened}">
				<td>&nbsp;</td>
				<td class="formtitle"><fmt:message
					key="orderdetail.material.field.position" /></td>
				<td class="formtitle"><fmt:message
					key="orderdetail.material.field.material" /></td>
				<td colspan="2" class="formtitle"><fmt:message
					key="orderdetail.material.field.order.quantity" /></td>
				<td colspan="2" class="formtitle"><fmt:message
					key="orderdetail.material.field.net.weight" /></td>
				<td colspan="3" class="formtitle"><fmt:message
					key="orderdetail.material.field.return.quantity" /></td>
				<td class="formtitle"><fmt:message
					key="orderdetail.material.field.credit.note" /></td> 
				<td colspan="3" class="formtitle"><fmt:message
					key="orderdetail.material.field.delivered.quantity" /></td>
			</c:when>
			<c:otherwise>
				<c:if test="${form.materialBean.hasServiceMaterial}">
					<td>&nbsp;</td>
					<td class="formtitle"><fmt:message
						key="orderdetail.material.field.position" /></td>
					<td class="formtitle"><fmt:message
						key="orderdetail.material.field.material" /></td>
					<td colspan="2" class="formtitle"><fmt:message
						key="orderdetail.material.field.order.quantity" /></td>
					<td colspan="2" class="formtitle"><fmt:message
						key="orderdetail.material.field.net.weight" /></td>
					<td colspan="3" class="formtitle"><fmt:message
						key="orderdetail.material.field.return.quantity" /></td>
				</c:if>
			</c:otherwise>
		</c:choose>
		</tr>
		
		<c:forEach var="orderItemBean" varStatus="status"
			items="${form.materialBean.orderItemList}">
		<c:if test="${(form.materialTabOpened ) || (not form.materialTabOpened && orderItemBean.serviceMaterial)}">
		
<%----------------------------------------------------------------------------------------------%>
<%--------------------START PER ORDER ITEM DISABLED SETTINGS -----------------------------------%>
<%----------------------------------------------------------------------------------------------%>
		
<c:set var="isOrderedQuantityDisabled">true</c:set>
<c:if test="${!isQuantityUomChangeDisabled && !orderItemBean.isRailReturnRepair && (orderItemBean.isNew || !form.dispatchedFlag)}"> 
	<c:set var="isOrderedQuantityDisabled">false</c:set>
</c:if>

<c:set var="isLoadedQuantityDisabled">true</c:set>
<c:choose>
	<c:when test="${form.materialBean.sentToSapIsNullFlag}">
		<c:if test="${!isAdjustmentWeightsDisabled 
						&& orderItemBean.tareNetWeightsAreNotNullFlag 
						&& !orderItemBean.orderItemAndLoadRel11Flag}"> 
			<c:set var="isLoadedQuantityDisabled">false</c:set>
		</c:if>
	</c:when>
	<c:otherwise>
    	<c:if test="${!isOrdCorrectionWieghtsAndQtyDisabled 
						&& orderItemBean.tareNetWeightsAreNotNullFlag 
						&& !orderItemBean.orderItemAndLoadRel11Flag}"> 
			<c:set var="isLoadedQuantityDisabled">false</c:set>
		</c:if>
	</c:otherwise>
</c:choose>

<c:set var="isReturnedQuantityDisabled">false</c:set>
<c:if test="${orderItemBean.isReturnedQuantityDisabled || isAdjustmentWeightsDisabled}">
	<c:set var="isReturnedQuantityDisabled">true</c:set>
</c:if>

<c:set var="orderHasBeenDelivered">false</c:set>
<c:if test="${(form.order.orderStatus.orderStatusCd == ORDER_STATUS_DELIVERED) ||
            (form.order.orderStatus.orderStatusCd == ORDER_STATUS_FINISHED)}">
    <c:set var="orderHasBeenDelivered">true</c:set>
</c:if>

<c:choose>
	<c:when test="${orderHasBeenDelivered && form.materialBean.sentToSapIsNullFlag}">
		<c:if test="${!isAdjustmentWeightsDisabled && orderItemBean.loadedQuantity != null && !orderItemBean.isReturnedQuantityDisabled}"> 
			<c:set var="isReturnedQuantityDisabled">false</c:set>
		</c:if>
	</c:when>
	<c:otherwise>
		<c:if test="${!isAdjustmentWeightsDisabled && orderHasBeenDelivered && !isCreditNoteCreateDisabled && !orderItemBean.isCreditNoteDisabled
				&& orderItemBean.creditNoteFlag}">
			<c:set var="isReturnedQuantityDisabled">false</c:set>
		</c:if>
	</c:otherwise>
</c:choose>

<c:set var="isCreditNoteDisabled">true</c:set>
<c:if test="${!form.materialBean.sentToSapIsNullFlag
		&& !isCreditNoteCreateDisabled && !orderItemBean.isCreditNoteDisabled}">
	<c:set var="isCreditNoteDisabled">false</c:set>
</c:if>
<c:set var="isSelected">false</c:set>
<c:if test="${form.materialBean.selOrderItemIndex == status.count-1}" >
	<c:set var="isSelected">true</c:set>
</c:if>
<%-----------------------------------------------------------------------------------------------------------------%>
<%--------------------END PER ORDER ITEM DISABLED SETTINGS --------------------------------------------------------%>
<%-----------------------------------------------------------------------------------------------------------------%>
<%--WHEN RENDERING THE PAGE THESE EDITABILITY RULES WILL ONLY AFFECT THE SELECTED ROW, OTHERWISE USED--------------%>
<%-- AS PARAMETER TO SEND INTO THE JAVASCRIPT TO ENABLE DISABLE FIELDS WHEN CLICKING THE RADIO BUTTON--------------%>
<%-----------------------------------------------------------------------------------------------------------------%>

<c:set var="returnQuantHandling" value="${orderItemBean.returnQuantHandling}" />
<c:if test="${orderItemBean.returnQuantHandling == null}" >
	<c:set var="returnQuantHandling">null</c:set>
</c:if>

<c:set var="isEditablePlanningItem">false</c:set>
<c:if test="${not empty form.planningMaterialReplacementList and not orderItemBean.serviceMaterial
		and form.planningMaterialStillEditable}" >
	<c:set var="isEditablePlanningItem">true</c:set>
</c:if>
				<c:choose>
					<c:when test="${isEditablePlanningItem}">
						<tr bgcolor="#F6CEAE">
					</c:when>
					<c:otherwise>
						<tr>
					</c:otherwise>
				</c:choose>
				
				<td valign="top"><input type="radio" name="radio_material" value = "<c:out value="${orderItemBean.orderItemId}"/>" onChange="javascript:onChangeMaterialRadio()" 
					onclick="javascript:onClickMaterialRadio('<c:out value="${orderItemBean.orderItemId}"/>', 
					 <c:out value="${isOrderedQuantityDisabled}"/>, 
					 <c:out value="${isLoadedQuantityDisabled}"/>,
					 <c:out value="${isReturnedQuantityDisabled}"/>,
					 <c:out value="${isCreditNoteDisabled}"/>,
					 <c:out value="${orderItemBean.creditNoteFlag}"/>,
					 <c:out value="${returnQuantHandling}"/>);
					 loadOrderItem('<c:out value="${status.count-1}"/>');"
					<c:if test="${isSelected}" > checked="checked" </c:if> />
				</td>
				<td><span class="textfielddisabled"> <c:out
					value="${orderItemBean.orderItemNo}" />&nbsp;
				</span></td>
				<td style="vertical-align: top;">
					<c:choose>
						<c:when test="${isEditablePlanningItem}">
							<html:select styleId="${orderItemBean.orderItemId}_SapMaterialId"
								property="materialBean.orderItemList[${status.count-1}].sapContractItemId"
								styleClass="textfield" size="1" style="width:95%;"
								onchange='javascript:onReplacePlanningMaterial()'>
							<c:forEach var="entity" varStatus="statusUom" items="${form.planningMaterialReplacementList}">
								<html:option value="${entity.sapContractItemId}">
									<c:out value="${entity.sapMaterialName} (${entity.contractItemNo})" />
								</html:option>
							</c:forEach>
							</html:select>
						</c:when>
						<c:otherwise>
							<div style="position: relative;">
								<div style="overflow: hidden; white-space: nowrap; position: absolute; width: 100%;">
									<span class="textfielddisabled" style="width: 95%;">
										<c:out value="${orderItemBean.sapMaterialName}" />
										<c:if test="${orderItemBean.contractItemNo != null}">
											(<c:out value="${orderItemBean.contractItemNo}" />)
										</c:if>
									</span>
								</div>
							</div>
						</c:otherwise>
					</c:choose>
				</td>
				
				<td>
					<html:text styleId="${orderItemBean.orderItemId}_OrderedQuantity"
							property="materialBean.orderItemList[${status.count-1}].orderedQuantity"
							styleClass="textfield" style="width: 100%;"
							maxlength="<%=OrderItemEntity.DBSIZE_ORDERED_QUANTITY + ""%>"
							disabled="${isOrderedQuantityDisabled || !isSelected}" />
				</td>
				<td>
					<c:choose>
						<c:when test="${isOrderedQuantityDisabled}">
							<html:text  styleId="${orderItemBean.orderItemId}_OrderedUom"
								styleClass="textfield" style="width: 95%;"
								property="materialBean.orderItemList[${status.count-1}].orderedUom"
								disabled="true" />
						</c:when>
						<c:otherwise>
							<html:select styleId="${orderItemBean.orderItemId}_OrderedUom"
									property="materialBean.orderItemList[${status.count-1}].orderedUom"
									styleClass="textfield" size="1" style="width:95%;"
									disabled="${isOrderedQuantityDisabled || !isSelected}">
							<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">-</html:option>
							<c:forEach var="entity" varStatus="statusUom"
									items="${orderItemBean.uomList}">
								<html:option value="${entity.uomTxPK.uomBs.uomCd}">
									<c:out value="${entity.uomTxPK.uomBs.uomCd}" />
								</html:option>
							</c:forEach>
							</html:select>
						</c:otherwise>
					</c:choose>
				</td>
				
				<td>
					<html:text styleId="${orderItemBean.orderItemId}_LoadedQuantity"
							property="materialBean.orderItemList[${status.count-1}].loadedQuantity"
							onchange="onLoadedQtyChange(this.value, ${orderItemBean.orderItemId})"
							styleClass="textfield" style="width: 100%;"
							maxlength="<%=OrderItemEntity.DBSIZE_LOADED_QUANTITY + ""%>"
							disabled="${isLoadedQuantityDisabled || !isSelected}" />
				</td>
				<td>
					<c:choose>
						<c:when test="${isLoadedQuantityDisabled}">
							<html:text  styleId="${orderItemBean.orderItemId}_LoadedUom"
								styleClass="textfield" style="width: 95%;"
								property="materialBean.orderItemList[${status.count-1}].loadedUom"
								disabled="true" />
						</c:when>
						<c:otherwise>
							<html:select styleId="${orderItemBean.orderItemId}_LoadedUom"
								property="materialBean.orderItemList[${status.count-1}].loadedUom"
								onchange="onLoadedUomChange(this.value, ${orderItemBean.orderItemId})"
								styleClass="textfield" size="1" style="width:95%;" 
								disabled="${isLoadedQuantityDisabled || !isSelected}">
							<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">-</html:option>
							<c:forEach var="entity" varStatus="statusUom"
									items="${orderItemBean.uomList}">
								<c:if test="${!entity.uomTxPK.uomBs.fuzzyQuantityFlag}">
									<html:option value="${entity.uomTxPK.uomBs.uomCd}">
										<c:out value="${entity.uomTxPK.uomBs.uomCd}" />
									</html:option>
								</c:if>
							</c:forEach>
							</html:select>
						</c:otherwise>
					</c:choose>
				</td>
				<!-- if  credit note needed then check authorization -->
				<c:choose>
					<c:when test="${(orderItemBean.returnQuantHandling == RETURN_QUANT_HANDLING_CREDIT_NOTE_NEEDED) && isUnapprovedReturnViewAuth == false}">
						<td colspan="4">&nbsp;</td>
					</c:when>
					<c:otherwise>
						<!-- See "MaterialTab.jsp" for the "onchange" function definitions. -->
						<td>
							<html:text styleId="${orderItemBean.orderItemId}_ReturnedQuantity"
									property="materialBean.orderItemList[${status.count-1}].returnedQuantity"
									onchange="onReturnedQtyChange(this.value, ${orderItemBean.orderItemId})"
									styleClass="textfield" style="width: 100%;"
									maxlength="<%=OrderItemEntity.DBSIZE_RETURNED_QUANTITY + ""%>"
									disabled="${isReturnedQuantityDisabled || !isSelected}" />
						</td>
						<td>
							<html:text  styleId="${orderItemBean.orderItemId}_ReturnedUom"
									styleClass="textfield" style="width: 95%;"
									property="materialBean.orderItemList[${status.count-1}].returnedUom"
									disabled="true" />
						</td>
						<c:if test="${form.materialTabOpened}">
							<td>
								<span id="<c:out value="${orderItemBean.orderItemId}" />_ReturnReasonButton" <c:if test="${isReturnedQuantityDisabled}">style="visibility:hidden;"</c:if>>
									<a href="javascript:openReturnReasonPopup(<c:out value="${orderItemBean.orderItemId}" />, <c:out value="${orderItemBean.sapMaterialId}" />, <c:out value="${status.count-1}" />)" >
										<img src="<crh-common:url value='/images/silk/pencil.png'/>"/>
									</a>
								</span>
							</td>
							<td>
								<html:checkbox styleId="${orderItemBean.orderItemId}_CreditNoteFlag"
										property="materialBean.orderItemList[${status.count-1}].creditNoteFlag"
										onclick="changeCN('${orderItemBean.orderItemId}', this.checked);"
										disabled="${isCreditNoteDisabled || !isSelected}" />
							</td>
						</c:if>
					</c:otherwise>
				</c:choose>
				
				<c:set var="deliveredQtyVisibility">
					<c:choose>
						<c:when test="${form.materialTabOpened}">inline</c:when>
						<c:otherwise>none</c:otherwise>
					</c:choose>
				</c:set>
				<td>
					<html:text styleId="${orderItemBean.orderItemId}_DeliveredQuantity"
							property="materialBean.orderItemList[${status.count-1}].deliveredQuantity"
							styleClass="textfield" style="width: 100%;display:${deliveredQtyVisibility};"
							disabled="true" />
				</td>
				<td>
					<html:text styleId="${orderItemBean.orderItemId}_DeliveredUom"
							styleClass="textfield" style="width: 95%;display:${deliveredQtyVisibility};"
							property="materialBean.orderItemList[${status.count-1}].deliveredUom"
							disabled="true" />
				</td>
				<td>
					<c:if test="${(orderItemBean.logonPackageTypeCd == PACKAGE_TYPE_BAG
								|| orderItemBean.logonPackageTypeCd == PACKAGE_TYPE_SERVICE)
							&& (!form.loadedFlag)}">
						<a href="javascript:deleteItem(<c:out value="${orderItemBean.orderItemId}" />)" >
							<img alt="Delete" src="<crh-common:url value='/images/delete2.png'/>"/>
						</a>
					</c:if>
				</td>
			</tr>
		</c:if>
		</c:forEach>
	</tbody>
</table>
</div>
<!-- End scrollform -->

<!--</html:form>-->
