<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: OpenCustorders.jsp,v $ --%>

<%@ include file="../includeTLDs.jsp" %>

<%@page import="com.crh.logon.order.web.forms.bestellung.BestellungForm"%>

<c:set var="form" scope="page" value="${order_bestellung_BestellungForm}" />

<table style="width: 1230px">
	<tr>
		<td style="padding-top: 0px">
			<span class="labelblue">
				<fmt:message key="custorder.list.open"/>
			</span>
			<table class="classicblue" style="width: 1230px; margin-top: 4px">
				<thead>
					<tr>
						<th>
							<fmt:message key="fields.order.number.short"/>
						</th>
						<th>
							<fmt:message key="bestellung.orderingPerson"/>
						</th>
						<th rowspan="2">
							<fmt:message key="fields.produkt.produkt"/><br/>
							<fmt:message key="fields.konditionen.produktionswerk"/>
						</th>
						<th rowspan="2" style="text-align: right">#</th> 
						<th rowspan="2">
							<fmt:message key="fields.uom.uom"/><br/>
							<fmt:message key="fields.defaults.kundensilo"/>
						</th>
						<th colspan="2">
							<fmt:message key="fields.order.delivery.time"/>
						</th> 
						<th rowspan="2">
							<fmt:message key="fields.konditionen.liefertyp"/><br/>
							<%--
							<fmt:message key="fields.konditionen.lieferwerk"/>
							--%>
						</th>
						<th rowspan="2">
							<fmt:message key="fields.defaults.versandstelle"/><br/>
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
						<td class="cell_gray"><fmt:message key="fields.customerrefnumber.short"/></td>
						<td class="cell_gray"><fmt:message key="fields.order.status"/></td>
						<td class="cell_gray"><fmt:message key="fields.time.from"/></td>
						<td class="cell_gray"><fmt:message key="fields.time.until"/></td>
						<td class="cell_gray"><fmt:message key="fields.carrier.commercial"/></td>
					</tr>
				</thead>
				<tbody>
					<%-- List of the open customer orders --%>
					<%-- Note: currently all customer orders only have 1 bulk material --%>
					<c:set var="rowCnt" value="${0}"/>
					<c:forEach var="custorder" varStatus="status" items="${form.openCustorderList}">
						<c:if test="${prevCustorderId != custorder.custorderId}">
							<c:set var="rowCnt" value="${rowCnt + 1}"/>
 							<c:set var="prevCustorderId" value="${custorder.custorderId}"/>
 						</c:if>
						<tr class="zebra<c:out value="${rowCnt % 2}"/>">
							<td>
								<a href="javascript:CustorderExpander.toggleShowOrdersForCustorder(<c:out
										value="${custorder.custorderId}"/>)">
									<c:out value="${custorder.custorderNumber}"/>
								</a>
								<input id='buttonCustorderExpand<c:out value="${custorder.custorderId}"/>'
										type="button" class="SmallButtonStyleDown" value=''
										onclick="CustorderExpander.toggleShowOrdersForCustorder(<c:out
											value="${custorder.custorderId}"/>)"/>
								<br/>
								<c:out value="${custorder.custorderCustrefNo}"/>
							</td>
							<td>
								<%-- Not using a <nobr> tag (but its CSS equivalent) here allows the image
								     to be aligned to the right without forcing a line-break to always be
								     inserted. --%>
								<span class="white-space: nowrap">
									<c:choose>
										<c:when test="${empty custorder.contactPersonTxt}">
											-
										</c:when>
										<c:otherwise>
											<c:out value="${custorder.contactPersonTxt}"/>
										</c:otherwise>
									</c:choose>
									<br/>
									<c:out value="${custorder.statusTxt}"/>
									<a><img alt="Close"
											title="<fmt:message key="custorder.list.closecustomerorder"/>"
											onclick="initiateSetCustorderToFinishedManuallyBest('<c:out
												value="${custorder.custorderId}"/>', true)"
											src="<crh-common:url value='/images/close_icon.gif'/>"
											style="margin-top: 3px; margin-left: 3px; float: right;"></a>
								</span>
							</td>
							<td>
								<nobr>
									<c:out value="${custorder.materialTxt}"/>
									<br/>
									<c:out value="${order.productionPlantName}"/>
								</nobr>
							</td>
							<td style="text-align: right">
								<c:out value="${custorder.orderedQuantity}"/>
							</td>
							<td>
								<c:out value="${custorder.unitTxt}"/>
								<br/>
								<c:out value="${custorder.siloName}"/>
							</td>
							<td>
								<c:out value="${custorder.deliveryFromDate}"/>
								<br/>
								<c:out value="${custorder.deliveryFromTime}"/>
							 </td>
							<td>
								<c:out value="${custorder.deliveryToDate}"/>
								<br/>
								<c:out value="${custorder.deliveryToTime}"/>
							</td>
							<td>
								<nobr>
									<c:out value="${custorder.deliveryTypeTxt}"/>
									<br/>
									<c:out value="${custorder.supplyPlantName}"/>
								</nobr>
							</td>
							<td>
								<nobr>
									<c:out value="${custorder.shippingpointName}"/>
									<br/>
									<c:out value="${custorder.loadingpointName}"/>
								</nobr>
							</td>
							<td>
								<nobr>
									<c:out value="${custorder.poolName}"/>
									<br/>
									<c:out value="${order.commCarrierName}"/>
								</nobr>
							</td>
							<td align="center">
								<c:out value="${custorder.billtoBpName}"/>
								<br/>
								<c:out value="${custorder.payerBpName}"/>
								<br/>
								<input type="checkbox" disabled="true"
										<c:if test="${custorder.releaseSapFlag}">checked</c:if>/>
							</td>
						</tr>
						<tr class="zebra<c:out value="${rowCnt % 2}"/>"
								id="ordersForCustorderTr<c:out value="${custorder.custorderId}"/>"
								style="display: none;">
							<td colspan="13">
								<div id="ordersForCustorderDiv<c:out value="${custorder.custorderId}"/>">
								</div>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</td>
	</tr>
</table>
