<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@page import="com.crh.logon. web.utils.Authorization"%>
<%@page import="com.crh.logon.persistence.entities.common.OrderEntity"%>
<%@page import="com.crh.logon.persistence.entities.common.TruckEntity"%>
<%@page import="com.crh.logon.persistence.entities.common.LoadEntity"%>

<c:set var="org.apache.struts.taglib.html.BEAN" scope="page"
	value="${order_auftrag_OrderDetailForm}" />

<c:set var="form" scope="page" value="${order_auftrag_OrderDetailForm}" />

<c:set var="DELIVERY_TYPE_ROAD_DIRECT"><%=Constant.DELIVERY_TYPE_ROAD_DIRECT%></c:set>
<c:set var="DELIVERY_TYPE_RAIL_DIRECT"><%=Constant.DELIVERY_TYPE_RAIL_DIRECT%></c:set>
<c:set var="DELIVERY_TYPE_BIMODAL_ROAD"><%=Constant.DELIVERY_TYPE_BIMODAL_ROAD%></c:set>
<c:set var="DELIVERY_TYPE_BIMODAL_RAIL"><%=Constant.DELIVERY_TYPE_BIMODAL_RAIL%></c:set>
<c:set var="DELIVERY_TYPE_PICKUP"><%=Constant.DELIVERY_TYPE_PICKUP%></c:set>
<c:set var="UOM_TONS_TXT"><%=Constant.UOM_TONS_TXT%></c:set>


<c:set var="ATTR_ORDER_ID"><%=OrderEntity.ATTR_ORDER_ID%></c:set>
<c:set var="ATTR_ORDER_NUMBER"><%=OrderEntity.ATTR_ORDER_NUMBER%></c:set>
<c:set var="ATTR_TRUCK_NAME"><%=TruckEntity.ATTR_TRUCK_NAME%></c:set>
<c:set var="ATTR_NET_QUANTITY"><%=LoadEntity.ATTR_NET_QUANTITY%></c:set>

<%----------------------------------------------------------------------------------------------%>
<%--------------------START AUTHORIZATION DRIVEN DISABLED SETTINGS -----------------------------%>
<%----------------------------------------------------------------------------------------------%>

<c:set var="isCustomsSerialNoDisabled">true</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_MATERIAL_CUSTOMS_SERIAL_NO%>">
<c:set var="isCustomsSerialNoDisabled">false</c:set>
</crh-common:authorize>

<c:set var="isDelNoteDisabled">true</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_MATERIAL_DELIVERY_NOTE%>">
<c:set var="isDelNoteDisabled">false</c:set>
</crh-common:authorize>

<c:set var="isAdjustmentWeightsDisabled">true</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_ADJUSTMENT_WEIGHTS %>">
<c:set var="isAdjustmentWeightsDisabled">false</c:set>
</crh-common:authorize>

<c:set var="isOrdCorrectionWieghtsAndQtyDisabled">true</c:set>
<crh-common:authorize function="<%= Authorization.ORDER_CORRECTION_WEIGHTS_AND_QUANTITIES%>">
<c:set var="isOrdCorrectionWieghtsAndQtyDisabled">false</c:set>
</crh-common:authorize>

<c:set var="inputProdPlantDis" value="true" />
<crh-common:authorize function="<%=Authorization.ORDER_DETAIL_MATERIAL_W%>">
	<c:set var="inputProdPlantDis" value="false" />
</crh-common:authorize>

<c:set var="transmittedToDriver">
	<c:out value="${form.transmittedToDriver}" />
</c:set>

<c:set var="isDispatched">
	<c:out value="${form.dispatchedFlag}" />
</c:set>

<c:set var="selectedOrderItem" scope="page"
		value="${form.materialBean.orderItemList[form.materialBean.selOrderItemIndex]}" />

<c:set var="prodPlantCd" value="${selectedOrderItem.plantCd}" /> 
<c:if test="${prodPlantCd=='-1'}">
	<c:set var="prodPlantCd" value="" />
</c:if>

<c:set var="isTareGrossNetDisabled">true</c:set>
<c:if test="${selectedOrderItem.tareNetWeightsAreNotNullFlag
		&& selectedOrderItem.orderItemAndLoadRel11Flag
		&& selectedOrderItem.convFactors.fromNetQtyToLoadedUom != null
		&& selectedOrderItem.convFactors.fromNetQtyToTons != null}">
	<c:choose>
		<c:when test="${form.materialBean.sentToSapIsNullFlag}">
			<c:if test="${!isAdjustmentWeightsDisabled}">
				<c:set var="isTareGrossNetDisabled">false</c:set>
			</c:if>
		</c:when>
		<c:otherwise>
	    	<c:if test="${!isOrdCorrectionWieghtsAndQtyDisabled}">
				<c:set var="isTareGrossNetDisabled">false</c:set>
			</c:if>
		</c:otherwise>
	</c:choose>
</c:if>


<%----------------------------------------------------------------------------------------------%>
<%--------------------END AUTHORIZATION DRIVEN DISABLED SETTINGS -------------------------------%>
<%----------------------------------------------------------------------------------------------%>

<!-- begin split table 50/50  part 2 -->
<table style="width: 100%;">
	<colgroup>
		<col width="50%" />
		<col width="50%" />
	</colgroup>
	<tbody>
		<tr>
			<td>

			<!-- MATERIAL DELIVERY NOTE INFO -->
			<div class="subform" style="width:305px; margin-left:4px;"><span class="label"><fmt:message
							key="orderdetail.material.label.lsinfo" /><br />
			</span>
			<table cellspacing="0" cellpadding="0" border="0" width="97%"
				style="table-layout:fixed;border: 0px none;" class="classic">
				<colgroup>
					<col width="29%" />
					<col width="71%" />
				</colgroup>
				<tbody>
					<tr>
						<td valign="bottom"><span class="formtitle"><fmt:message
							key="orderdetail.material.field.materialnr" /></span></td>
						<td><span
							class="textfielddisabled_h" style="float: left; width: 80px;"> <c:out
							value="${selectedOrderItem.sapMaterialNo}" />&nbsp;
						</span>
						<span class="textfielddisabled_h" style="float: right; margin-left: 4px; width: 120px;"> <c:out
							value="${selectedOrderItem.materialPackageCd}" />&nbsp;
						</span></td>
					</tr>
					<tr>
						<td valign="bottom"><span class="formtitle"><fmt:message
							key="orderdetail.material.field.material.norm" /></span></td>
						<td><span class="textfielddisabled"> <c:out
							value="${selectedOrderItem.materialNorm}" />&nbsp;
						</span></td>
					</tr>
					<tr>
						<td valign="bottom"><span class="formtitle"><fmt:message
							key="orderdetail.material.field.prod.plant" /></span></td>
						<td>
						<div style="float: left;"><html:text styleClass="textfield"
							style="width:45px;"
							property="materialBean.orderItemList[${form.materialBean.selOrderItemIndex}].plantCd"
							value="${prodPlantCd}"
							disabled="true" styleId="prodPlantCodeId" />
						</div>
						<div style="float: right;">
							<html:select
								styleClass="textfield" size="1" style="width:155px;"
								property="materialBean.orderItemList[${form.materialBean.selOrderItemIndex}].plantCd"
								onchange="javascript:changeProdPlant(this.options[this.selectedIndex].value)"
								disabled="${inputProdPlantDis}">
								<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">-</html:option>
								<c:forEach var="entity" varStatus="status"
									items="${selectedOrderItem.prodPlantList}">
									<html:option value="${entity.plantCd}">
										<c:out value="${entity.plantName}" />
									</html:option>
								</c:forEach>
							</html:select>
						</div></td>
					</tr>
					<tr>
						<td valign="bottom"><span class="formtitle"><fmt:message
							key="orderdetail.material.field.material.norm.desc" /></span></td>
						<td><span class="textfielddisabled"> <c:out
							value="${selectedOrderItem.materialCENorm}" />&nbsp;
						</span></td>
					</tr>
					<tr>
						<td valign="bottom"><span class="formtitle"><fmt:message
							key="orderdetail.material.field.mix.design.id" /></span></td>
						<td>
							<html:select
								styleClass="textfield" size="1" style="width:210px;"
								property="materialBean.orderItemList[${form.materialBean.selOrderItemIndex}].mixDesignId"
								disabled="${transmittedToDriver}"
								onchange="javascript:updateAggRmxCodeField(this.options[this.selectedIndex].value)">
								<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">-</html:option>
								<c:forEach var="item" varStatus="status"
									items="${selectedOrderItem.fireqMatMixdesignList}">
									<html:option value="${item.mixDesignId}">
										<c:out value="${item.mixDesignCd}"/>
									</html:option>
								</c:forEach>
							</html:select>
						</td>
					</tr>
					<tr>
						<td valign="bottom"><span class="formtitle"><fmt:message
							key="orderdetail.material.field.agg.rmx.code" /></span></td>
						<td>
						<html:text styleClass="textfielddisabled"
							style="width:100%;"
							property="materialBean.orderItemList[${form.materialBean.selOrderItemIndex}].aggRmxCode"
							disabled="true" styleId="aggRmxCodeField" />
					</tr>
					<tr>
						<td valign="bottom"><span class="formtitle"><fmt:message
							key="orderdetail.material.field.certification" /></span></td>
						<td><span
							class="textfielddisabled_h" style="float: left; width: 80px;"> <c:out
							value="${selectedOrderItem.certification}" />&nbsp;
						</span><span
							class="textfielddisabled_h" style="float: right; margin-left: 4px; width: 120px;"> <c:out
							value="${selectedOrderItem.cpdNumber}" />&nbsp;
						</span></td>
					</tr>
				</tbody>
			</table>
			</div>

			<!-- CONTAINER / SILO -->
			<div style="width:305px; margin-top:4px; margin-left:4px;" class="subform">
			<table cellspacing="0" cellpadding="0" border="0" width="97%"
				style="border: 0px none;" class="classic">
				<colgroup>
					<col width="34%" />
					<col width="33%" />
					<col width="33%" />
				</colgroup>
				<tbody>
					<tr>
						<td valign="bottom"><span class="formtitle"><fmt:message
							key="orderdetail.material.field.container" /></span></td>
						<td valign="bottom"><span class="textfielddisabled"> <c:out
							value="${selectedOrderItem.containerNo}" />&nbsp;
						</span></td>
						<td valign="bottom"><span class="textfielddisabled"> <c:out
							value="${selectedOrderItem.containerName}" />&nbsp;
						</span></td>
					</tr>
					<tr>
						<td valign="bottom"><span class="formtitle"><fmt:message
							key="orderdetail.material.field.silo" /></span></td>
						<td valign="bottom" colspan="2"><span
							class="textfielddisabled"> <c:out
							value="${selectedOrderItem.siloName}" />&nbsp;
						</span></td>
					</tr>
					<tr>
						<td valign="bottom"><span class="formtitle"><fmt:message
							key="orderdetail.material.field.silo.verification" /></span></td>
						<td valign="bottom" colspan="2"><span
							class="textfielddisabled"> <c:out
							value="${selectedOrderItem.siloVerificationType}" />&nbsp;
						</span></td>
					</tr>
				</tbody>
			</table>
			</div>

			</td>
			<td style="vertical-align: top;">

			<!-- WEIGHING -->
			<div class="subform" style="width:305px;"">
			<table cellspacing="0" cellpadding="0" border="0" width="100%"
				style="border: 0px none;" class="classic">
				<colgroup>
					<col width="40%" />
					<col width="25%" />
					<col width="30%" />
				</colgroup>
				<tbody>
					<tr>
						<td><span class="label"><fmt:message
								key="orderdetail.material.label.weighing" /></span></td>
						<td colspan="2" align="left">
						<c:if test="${isSpecialDetailDisabled}" >
						<span class="formtitle" title='<fmt:message key="orderdetail.material.label.loadid" />'> <c:out
							value="${selectedOrderItem.loadId}" /><c:out
							value="${selectedOrderItem.parallelVerladId}" />
						</span>
						</c:if>
						</td>
					</tr>
					
					<c:if test="${(form.order.deliveryType.deliveryTypeCd == DELIVERY_TYPE_ROAD_DIRECT)  ||
									(form.order.deliveryType.deliveryTypeCd == DELIVERY_TYPE_RAIL_DIRECT) ||
									(form.order.deliveryType.deliveryTypeCd == DELIVERY_TYPE_BIMODAL_RAIL) ||
									(form.order.deliveryType.deliveryTypeCd == DELIVERY_TYPE_PICKUP)}">
					<tr>
						<td valign="bottom" class="formtitle"
							style="white-space: nowrap; border: 0px;"><fmt:message
							key="orderdetail.main.field.ldpoint" /></td>
						<td colspan="2">
						<div id="LPointDiv"
							style="border: 0px; padding: 0px; width: 100%;"><jsp:include
							page="LPoint.jsp"></jsp:include></div>
						</td>
					</tr>
						<c:if test="${not empty selectedOrderItem.carrierStock}"> 
						<tr>
							<td valign="bottom" class="formtitle"
								style="white-space: nowrap; border: 0px;"><fmt:message
								key="orderdetail.material.field.carrier.stock" /></td>
							<td colspan="2">
								<c:out value="${selectedOrderItem.carrierStock}" />
							</td>
						</tr>
						</c:if>
					</c:if>

					<tr>
						<td colspan="2">
							<span class="formtitle">
								<fmt:message key="orderdetail.material.field.tare.weight" />
							</span> 
							[<c:out value="${UOM_TONS_TXT}" />]
						</td>
						<td>
							<html:text styleId="inputTareMeasure"
									property="materialBean.orderItemList[${form.materialBean.selOrderItemIndex}].tareWeightQuantity"
									onchange="onInputTareMeasureChange(this, this.value, ${selectedOrderItem.orderItemId}, convFactors)"
									styleClass="textfield" style="width: 100%;"
									maxlength="<%=LoadEntity.DBSIZE_TARE_WEIGHT_T + ""%>"
									disabled="${isTareGrossNetDisabled || form.bagOrderFlag}" />
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<span class="formtitle">
								<fmt:message key="orderdetail.material.field.gross.weight" />
							</span>
							[<c:out value="${UOM_TONS_TXT}" />]
						</td>
						<td>
							<html:text styleId="inputGrossMeasure"
									property="materialBean.orderItemList[${form.materialBean.selOrderItemIndex}].grossWeightQuantity"
									onchange="onInputGrossMeasureChange(this, this.value, ${selectedOrderItem.orderItemId}, convFactors)"
									styleClass="textfield" style="width: 100%;"
									maxlength="<%=LoadEntity.DBSIZE_NET_QUANTITY + ""%>"
									disabled="${isTareGrossNetDisabled || form.bagOrderFlag}" />
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<span class="formtitle">
								<fmt:message key="orderdetail.material.field.net.weight" />
							</span>
							[<c:out value="${selectedOrderItem.netQuantityUom}" />]
						</td>
						<td>
							<html:text styleId="inputNetMeasure"
									property="materialBean.orderItemList[${form.materialBean.selOrderItemIndex}].netWeightQuantity"
									onchange="onInputNetMeasureChange(this, this.value, ${selectedOrderItem.orderItemId}, convFactors)"
									styleClass="textfield" style="width: 100%;"
									maxlength="<%=LoadEntity.DBSIZE_NET_QUANTITY + ""%>"
									disabled="${isTareGrossNetDisabled}" />
						</td>
					</tr>
					<tr>
						<c:if test="${form.materialBean.goodsMovementPresent}">
						<td align="right" colspan="3">
							<a href="javascript:showGoodsMovementPopup()"><img src="<crh-common:url value='/images/information.gif'/>"/></a>
							<span class="formtitle"><fmt:message key="orderdetail.material.label.goodsmovementinfo" /></span>
						</td>
						</c:if>
					</tr>
				</tbody>
			</table>
			</div>
			<!-- End Weighting --> 
			
			<!-- Begin Bi-Modal Truck -->
			<c:if test="${(form.order.deliveryType.deliveryTypeCd == DELIVERY_TYPE_BIMODAL_ROAD)}">
				<div style="width:305px; margin-top:4px;" class="subform"><span
					class="label"><fmt:message
					key="orderdetail.material.label.bimodal.rail" /><br />
				</span> 
				<table cellspacing="0" cellpadding="0" border="0" width="97%"
				style="border: 0px none;" class="classic">
				<colgroup>
					<col width="50%" />
					<col width="50%" />
				</colgroup>
				<tbody>
					<tr>
						<td valign="bottom"><span class="formtitle"><fmt:message
							key="orderdetail.material.field.order.no" /></font></span></td>
						<td valign="bottom">
						<c:if test="${selectedOrderItem.authorizedBimodalTruckOrder&&!selectedOrderItem.archivedBimodalRailOrder}">
						<a href="javascript:reloadOrder('<c:out value="${selectedOrderItem.bimodalTruckOrderId}" />');">
						</c:if>
						<c:out value="${selectedOrderItem.bimodalTruckOrderNo}" />&nbsp;
						<c:if test="${selectedOrderItem.authorizedBimodalTruckOrder}">
						</a>
						</c:if>
						<c:if test="${!empty selectedOrderItem.bimodalTruckNetWeight }">
							(<c:out value="${selectedOrderItem.bimodalTruckNetWeight }"/>)
						</c:if>
						</td>
					</tr>
					<tr>
						<td valign="bottom" style="border: 0px none; white-space: nowrap;"
							class="formtitle"><fmt:message
							key="orderdetail.material.field.wagon.no" /></td>
						<td valign="bottom"><span class="textfielddisabled"> <c:out
							value="${selectedOrderItem.bimodalTruckWagonNo}" />&nbsp;
						</span></td>
					</tr>
				</tbody>
			</table>
				</div>
			</c:if>
			<!-- end Bi-Modal Truck -->	

			<!-- Begin Bi-Modal Rail -->
			<c:if test="${(form.order.deliveryType.deliveryTypeCd == DELIVERY_TYPE_BIMODAL_RAIL)}">
				<div style="width:305px; margin-top:4px;" class="subform"><span
					class="label"><fmt:message
					key="orderdetail.material.label.bimodal.truck" /> </span>
				<div style="font-size: 12px; margin-top: 4px; height: 64px;"
					class="subformlist">
				<table cellspacing="0" cellpadding="0" border="0"
					style="width: 100%;" class="noborder">
					<colgroup>
						<col width="15%" />
						<col width="30%" />
						<col width="10%" />
						<col width="45%" />
					</colgroup>
					<tbody>
						<c:set var="counter" value="1" />
						<c:forEach var="bimodalTruckOrderItem" varStatus="status" items="${selectedOrderItem.bimodalTruckOrderItems}">
							<tr class="zebra<c:out value="${counter % 1}" />">
								<td valign="bottom">
									<c:if test="${bimodalTruckOrderItem.authorized}">
										<a href="javascript:reloadOrder('<c:out value="${bimodalTruckOrderItem.orderId}" />');">
									</c:if>
									<c:out value="${bimodalTruckOrderItem.orderNumber}" />&nbsp;
									<c:if test="${bimodalTruckOrderItem.authorized}">
										</a>
									</c:if>
								</td>							
								<td valign="bottom">						
									<span title="<c:out value="${bimodalTruckOrderItem.weightRepresentation}" />" style="width:65px;" class="textfielddisabled_h">									
										<c:out value="${bimodalTruckOrderItem.weightRepresentation}" />
									</span>
								</td>
								<td valign="bottom">
								<fmt:message key="orderdetail.material.label.truck" />
								</td>
								<td valign="bottom">
									<span class="textfielddisabled_h">
										<c:out value="${bimodalTruckOrderItem.truckName}" />&nbsp;
									</span>
								</td>
							</tr>
							<c:set var="counter" value="${counter + 1}" />
						</c:forEach>
					</tbody>
				</table>
				</div>
				</div>
			
			</c:if>
			<!-- end Bi-Modal Rail -->	

			<!-- Delivery note -->
			<div style="width:305px; margin-top:4px;" class="subform"><span
				class="label"><fmt:message
				key="orderdetail.material.label.del.note" /> </span> 
				<c:if test="${!isMaterialDisabled && !isDelNoteDisabled}" >
					<a href="javascript:generatePdf();" ><img src="<crh-common:url value='/images/img_pdf_create.png'/>" alt="<fmt:message key="auftragbearbeitung.alt.generatepdf"/>"> </a>
				</c:if>
			<div style="font-size: 12px; margin-top: 4px; height: 44px;"
				class="subformlist">
			<table cellspacing="0" cellpadding="0" border="0"
				style="width: 100%;" class="noborder">
				<colgroup>
					<col width="1%" />
					<col width="99%" />
				</colgroup>
				<tbody>
					<c:set var="counter" value="1" />
					<c:forEach var="entity" varStatus="_status"
						items="${form.materialBean.deliveryNoteMap}">
						<tr class="zebra<c:out value="${counter % 2}" />">
							<td valign="middle"><a
								href="<%=request.getContextPath() %>/blob?keyValue=<c:out value="${entity.key}" />&entity=OutputdocEntity&keyName=outputdocId"
								target="_blank"><img src="<crh-common:url value='/images/img_pdf_icon.png'/>"
								width="16" height="16" alt="pdf"></a></td>
							<td><c:out value="${entity.value}" /></td>
						</tr>
						<c:set var="counter" value="${counter + 1}" />
					</c:forEach>

				</tbody>
			</table>
			</div>
			</div>
			
			<!-- Reference numbers -->
			<div style="width:305px; margin-top:4px;" class="subform">
				<span
				class="label"><fmt:message
				key="orderdetail.material.label.referencenumbers" /> </span>
				
					<table cellspacing="0" cellpadding="0" border="0"
						style="width: 100%; border: 0px none;" class="classic">
						<colgroup>
							<col width="40%" />
							<col width="60%" />
						</colgroup>
						<tr>
							<td>
								<fmt:message
									key="orderdetail.material.label.referencenumbers.sequentialno" />	
							</td>
							<td>
								<c:set var="ordernumberReadonly" value="${true}"/>
								<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_MATERIAL_LDS_ORDERNO %>">
									<c:set var="ordernumberReadonly" value="${false}"/>
								</crh-common:authorize>
								<html:text styleId="orderNumberFromSeqUsage"
									property="orderBean.orderNumberFromSeqUsage"
									styleClass="textfield" style="width: 100%;"
									disabled="${ordernumberReadonly}" />
								
							</td>
						</tr>
						<tr>
							<td>
								<fmt:message
									key="orderdetail.material.label.referencenumbers.ldsorderno" />
							</td>
							<td>
							<span class="textfielddisabled_h"><c:out value="${form.orderBean.ldsDeliveryNoteNo}"/><c:if test="${empty form.orderBean.ldsDeliveryNoteNo}">&nbsp;</c:if></span>
							</td>
						</tr>
					</table>
			</div>
			
			</td>
		</tr>
	</tbody>
</table>





