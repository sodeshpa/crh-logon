<%--
 Copyright (c) : AdNovum Informatik AG
 Version Number: $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: OpenOrdersForCustorder.jsp,v $ --%>

<%@ include file="../includeTLDs.jsp" %>

<%-- <%@page import="org.apache.commons.lang.StringUtils"%> --%>
<%@page import="com.crh.logon.order.web.forms.bestellung.BestellungForm"%>

<c:set var="form" scope="page" value="${order_bestellung_BestellungForm}" />

<table style="width: 1230px">
	<tr>
		<td style="padding-top: 0px">
			<span class="labelblue">
				<fmt:message key="order.list.open"/>
			</span>
			<table class="classicblue" style="width: 1230px; margin-top: 4px">
				<tbody>
					<tr>						
						<th rowspan="2" style="width: 60px">
							<fmt:message key="fields.order.number.short"/>
						</th>
						<th>
							<fmt:message key="bestellung.orderingPerson"/>
						</th>
						<th rowspan="2">
							<fmt:message key="fields.produkt.produkt"/><br/>
							<fmt:message key="fields.konditionen.produktionswerk"/>
						</th> 
						<th rowspan="2" style="text-align: right">
							#
						</th>
						<th rowspan="2">
							<fmt:message key="fields.uom.uom"/><br/>
							<fmt:message key="fields.defaults.kundensilo"/>
						</th> 
						<th colspan="2">
							<fmt:message key="fields.order.delivery.time"/>
						</th> 
						<th rowspan="2">
							<fmt:message key="fields.konditionen.liefertyp"/>
							<br/>
						</th> 
						<th rowspan="2">
							<fmt:message key="fields.defaults.versandstelle"/>
							<br/>
							<fmt:message key="fields.defaults.ladestrasse"/>
						</th>
						<th>
							<fmt:message key="fields.pool.pool"/>
						</th>
						<th rowspan="2" style="width: 105px">
							<fmt:message key="fields.billto.billto"/>
							/
							<fmt:message key="fields.payer.payer"/>
							<br/>
							<fmt:message key="dispokarte.texte.releaseSAP"/>
						</th>
					</tr>
					<tr>
						<td class="inner_cell_gray">
							<fmt:message key="fields.order.status"/>
						</td>
						<td class="inner_cell_gray">
							<fmt:message key="fields.time.from"/>
						</td>
						<td class="inner_cell_gray">
							<fmt:message key="fields.time.until"/>
						</td>
						<td class="inner_cell_gray">
							<fmt:message key="fields.carrier.commercial"/> /<br/>
							<fmt:message key="fields.carrier.executing"/>
						</td>
					</tr>

					<%-- List of the open single-delivery orders --%>
					<c:set var="rowCnt" value="${0}"/>
					<c:forEach var="order" varStatus="status" items="${form.openOrdersForCustorder}">
						<c:if test="${prevOrderId == order.orderId}">
							<c:set var="sameOrderId" value="true"/>
						</c:if>
						<c:if test="${prevOrderId != order.orderId}">
							<c:set var="sameOrderId" value="false"/>
							<c:set var="rowCnt" value="${rowCnt + 1}"/>
							<c:set var="prevOrderId" value="${order.orderId}"/>
						</c:if>
						<tr class="innerZebra<c:out value="${rowCnt % 2}"/>">
							<td>
								<c:if test="${not sameOrderId}">
									<a href="javascript:showPopup('<c:url
											value="/auftrag/OrderDetailEntry.do"/>?orderid=<c:out
											value="${order.orderId}"/>')">
										<c:out value="${order.orderNumber}"/>
									</a>
								</c:if>
							</td>
							<td>
								<nobr>
									<c:choose>
										<c:when test="${empty order.contactPersonTxt}">
											-
										</c:when>
										<c:otherwise>
											<c:out value="${order.contactPersonTxt}"/>
										</c:otherwise>
									</c:choose>
									<br/>
									<c:out value="${order.statusTxt}"/>
								</nobr>
							</td>
							<td>
								<nobr>
									<c:out value="${order.materialTxt}"/>
									<br/>
									<c:out value="${order.productionPlantName}"/>
								</nobr>
							</td>
							<td style="text-align: right">
								<c:out value="${order.orderedQuantity}"/>
							</td>
							<td>
								<c:out value="${order.unitTxt}"/>
								<br/>
								<c:out value="${order.siloName}"/>
							</td>
							<td>
								<c:out value="${order.deliveryFromDate}"/><br/>
								<c:out value="${order.deliveryFromTime}"/>
							</td>
							<td>
								<c:out value="${order.deliveryToDate}"/><br/>
								<c:out value="${order.deliveryToTime}"/>
							</td>
							<td>
								<nobr>
									<c:out value="${order.deliveryTypeTxt}"/>
									<br/>
									<c:out value="${order.supplyPlantName}"/>
								</nobr>
							</td>
							<td>
								<nobr>
									<c:out value="${order.shippingpointName}"/>
									<br/>
									<c:out value="${order.loadingpointName}"/>
								</nobr>
							</td>
							<td>
								<nobr>
									<c:out value="${order.poolName}"/>
									<br/>
									<c:out value="${order.commCarrierName}"/>
									<br/>
									<c:out value="${order.execCarrierName}"/>
								</nobr>
							</td>
							<td align="center">
								<c:out value="${order.bpName}"/>
								<br/>
								<c:out value="${order.payerBpName}"/>
								<br/>
								<input type="checkbox" disabled="true"
										<c:if test="${order.releaseSapFlag}">checked</c:if>/>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</td>
	</tr>
</table>
