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

<c:set var="org.apache.struts.taglib.html.BEAN" scope="page"
	value="${order_auftrag_OrderDetailForm}" />
<c:set var="ERROR"><%=Constant.ERROR%></c:set>

<c:set var="currency" value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.currencyCd}" />

<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td>
		<div class="subform"><span class="labelblue"><fmt:message
			key="orderdetail.com.label.income" /></span><br>
		<table border="0" cellspacing="0" cellpadding="0" width="99%" >
			<colgroup>
				<col width="43%">
				<col width="1%">
				<col width="18%">
				<col width="1%">
				<col width="18%">
				<col width="1%">
				<col width="18%">
			</colgroup>
			<tbody>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td class="label" align="right"><c:out value="${currency}" />/<fmt:message
						key="orderdetail.com.field.tons" /></td>
					<td>&nbsp;</td>
					<td class="label" align="right"><c:out value="${currency}" /></td>
					<td>&nbsp;</td>
					<td class="label" align="right">%</td>
				</tr>
				<tr class="zebra1">
					<td class="label"><fmt:message
						key="orderdetail.com.field.gross.price" /></td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.grossPricePt}" />
					</td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.grossPrice}" />
					</td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.grossPricePer}" />
					</td>
				</tr>
				<tr>
					<td class="label"><fmt:message
						key="orderdetail.com.field.total.discounts" /></td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.discountsPt}" />
					</td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.discounts}" />
					</td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.discountsPer}" />
					</td>
				</tr>
				<tr class="zebra1">
					<td><span class="label"><fmt:message
						key="orderdetail.com.field.net.price" /></span>						
						<span title='<fmt:message key="orderdetail.com.label.netprice.quantity" />'><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.netpriceQuantity}" /></span></td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.netPricePt}" />
					</td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.netPrice}" />
					</td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.netPricePer}" />
					</td>
				</tr>
				<tr>
					<td class="label"><fmt:message
						key="orderdetail.com.field.total.rebates" /></td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.rebatesPt}" />
					</td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.rebates}" />
					</td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.rebatesPer}" />
					</td>
				</tr>
				<tr>
					<td class="label"><fmt:message
						key="orderdetail.com.field.cash.discount" /></td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.cashDiscountPt}" />
					</td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.cashDiscount}" />
					</td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.cashDiscountPer}" />
					</td>
				</tr>
				<tr class="zebra1">
					<td class="label"><fmt:message
						key="orderdetail.com.field.net.sales" /></td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.netSalesEariningPt}" />
					</td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.netSalesEarining}" />
					</td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.netSalesEariningPer}" />
					</td>
				</tr>
			</tbody>
		</table>
		</div>
		</td>
	</tr>
	<tr>
		<td>
		<div class="subform"><span class="labelblue"><fmt:message
			key="orderdetail.com.label.costs" /></span><br>
		<table border="0" cellspacing="0" cellpadding="0" width="99%">
			<colgroup>
				<col width="43%">
				<col width="1%">
				<col width="18%">
				<col width="1%">
				<col width="18%">
				<col width="1%">
				<col width="18%">
			</colgroup>
			<tbody>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td class="label" align="right"><c:out value="${currency}" />/<fmt:message
						key="orderdetail.com.field.tons" /></td>
					<td>&nbsp;</td>
					<td class="label" align="right"><c:out value="${currency}" /></td>
					<td>&nbsp;</td>
					<td class="label" align="right">%</td>
				</tr>
				<tr>
					<td class="label"><fmt:message
						key="orderdetail.com.field.production.costs" /></td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.prodCostPt}" />
					</td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right" 
					<c:if test="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.prodCost == ERROR}" >
					 style="color:red;"
					</c:if>>
					<c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.prodCost}" />
					</td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.prodCostPer}" />
					</td>
				</tr>
				<tr>
					<td colspan="7">
					<hr width="100%">
					</td>
				</tr>
				<tr>
					<td class="label"><fmt:message
						key="orderdetail.com.field.inter.unit.distribution" /> </td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.iudCostPt}" />
					</td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"
					<c:if test="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.iudCost == ERROR}" >
					 style="color:red;"
					</c:if>>
					
					<c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.iudCost}" />
					</td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.iudCostPer}" />
					</td>
				</tr>
				<tr>
					<td class="label"><fmt:message
						key="orderdetail.com.field.shipping.station" /> </td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.handlingCostPt}" />
					</td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"
					<c:if test="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.handlingCost == ERROR}" >
					 style="color:red;"
					</c:if>>
					<c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.handlingCost}" />
					</td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.handlingCostPer}" />
					</td>
				</tr>
				<tr>
					<td class="label"><fmt:message
						key="orderdetail.com.field.outbound.distribution" /></td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.outCostPt}" />
					</td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"
					<c:if test="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.outCost == ERROR}" >
					 style="color:red;"
					</c:if>>
					<c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.outCost}" />
					</td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.outCostPer}" />
					</td>
				</tr>
				<tr>
					<td class="label"><fmt:message
						key="orderdetail.com.field.distribution" /></td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.totalDistCostPt}" />
					</td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.totalDistCost}" />
					</td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.totalDistCostPer}" />
					</td>
				</tr>
				<tr class="zebra1">
					<td class="label"><fmt:message 
						key="orderdetail.com.field.goods.sold" /></td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.totalCostPt}" />
					</td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.totalCost}" />
					</td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.totalCostPer}" />
					</td>
				</tr>
			</tbody>
		</table>
		</div>
		</td>
	</tr>
	<tr>
		<td>
		<div class="subform"><span class="labelblue"><fmt:message
			key="orderdetail.com.label.margin" /></span><br>
		<table border="0" cellspacing="0" cellpadding="0" width="99%">
			<colgroup>
				<col width="43%">
				<col width="1%">
				<col width="18%">
				<col width="1%">
				<col width="18%">
				<col width="1%">
				<col width="18%">
			</colgroup>
			<tbody>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td class="label" align="right"><c:out value="${currency}" />/<fmt:message
						key="orderdetail.com.field.tons" /></td>
					<td>&nbsp;</td>
					<td class="label" align="right"><c:out value="${currency}" /></td>
					<td>&nbsp;</td>
					<td class="label" align="right">%</td>
				</tr>
				<tr class="zebra1">
					<td class="label"><fmt:message
						key="orderdetail.com.field.contribution.margin" /></td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.contribMarginPt}" />
					</td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.contribMargin}" />
					</td>
					<td>&nbsp;</td>
					<td class="formtitle" align="right"><c:out
						value="${order_auftrag_OrderDetailForm.materialBean.orderItemList[order_auftrag_OrderDetailForm.materialBean.selOrderItemIndex].commercialBean.contribMarginPer}" />
					</td>
				</tr>
			</tbody>
		</table>
		</div>
		</td>
	</tr>
</table>
