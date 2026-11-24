<%@ include file="../includeTLDs.jsp" %>
<%@ taglib uri="http://com.crh.logon/tags/order-taglib.tld" prefix="crh-order" %>

<c:set var="form" scope="page" value="${order_dispokarte_DispokarteForm}" />

<c:if test="${form.contractItemData.dataAvailable}">
	<table style="width: 100%">
		<tr>
			<td style="padding-top: 0px">
				<span class="labelblue">
					<fmt:message key="sapContractItemData.panel.title">
						<fmt:param value="${form.contractItemData.contractItemNo}"/>
						<fmt:param value="${form.contractItemData.materialTxt}"/>
					</fmt:message>
				</span>
				<table class="classicblue" style="width: 100%; margin-top: 4px">
					<tbody>
						<tr>
							<th colspan="3">
								<fmt:message key="sapContractItemData.panel.table.header"/>
							</th>
						</tr>
						<tr class="zebra0">
							<td>
								<span style="white-space: nowrap;">
									<fmt:message key="sapContractItemData.panel.table.sumitemqtydelivered"/>
								</span>
							</td>
							<td>
								<span style="white-space: nowrap;">
									<c:out value="${form.contractItemData.sumDeliveries}"/>
									<c:out value="${form.contractItemData.uomTxt}"/>
								</span>
							</td>
							<td>
								<c:out value="${form.contractItemData.deliveries}"/>
							</td>
						</tr>
						<tr class="zebra1">
							<td>
								<span style="white-space: nowrap;">
									<fmt:message key="sapContractItemData.panel.table.sumitemqtyordered"/>
								</span>
							</td>
							<td>
								<span style="white-space: nowrap;">
									<c:out value="${form.contractItemData.sumNonDeliveries}"/>
									<c:out value="${form.contractItemData.uomTxt}"/>
								</span>
							</td>
							<td>
								<c:out value="${form.contractItemData.nonDeliveries}"/>
							</td>
						</tr>
						<tr class="zebra0">
							<td>
								<span style="white-space: nowrap;">
									<fmt:message key="sapContractItemData.panel.table.orderitemsnotconvertible"/>
								</span>
							</td>
							<td>
								&nbsp;
							</td>
							<td>
								<c:out value="${form.contractItemData.conversionFailures}"/>
							</td>
						</tr>
						<c:if test="${form.contractItemData.hasGroupLeader}">
							<tr class="zebra1">
								<td>
									<span style="white-space: nowrap;">
										<fmt:message key="sapContractItemData.panel.table.groupleadertargetquantity"/>
									</span>
								</td>
								<td>
									<span style="white-space: nowrap;">
										<c:out value="${form.contractItemData.targetQuantity}"/>
										<c:out value="${form.contractItemData.groupLeaderUomTxt}"/>
									</span>
								</td>
								<td>
								</td>
							</tr>
							<tr class="zebra0">
								<td>
									<span style="white-space: nowrap;">
										<fmt:message key="sapContractItemData.panel.table.groupleadertotalsentquantity"/>
									</span>
								</td>
								<td>
									<span style="white-space: nowrap;">
										<c:out value="${form.contractItemData.totalSentQuantity}"/>
										<c:out value="${form.contractItemData.groupLeaderUomTxt}"/>
									</span>
								</td>
								<td>
								</td>
							</tr>
							<tr class="zebra1">
								<td>
									<span style="white-space: nowrap;">
										<fmt:message key="sapContractItemData.panel.table.groupleadersumitemquantityordered"/>
									</span>
								</td>
								<td>
									<span style="white-space: nowrap;">
										<c:out value="${form.contractItemData.sumItemQuantityOrdered}"/>
										<c:out value="${form.contractItemData.groupLeaderUomTxt}"/>
									</span>
								</td>
								<td>
								</td>
							</tr>
							<tr class="zebra0">
								<td>
									<span style="white-space: nowrap;">
										<fmt:message key="sapContractItemData.panel.table.groupleadersumitemquantityloaded"/>
									</span>
								</td>
								<td>
									<span style="white-space: nowrap;">
										<c:out value="${form.contractItemData.sumItemQuantityLoaded}"/>
										<c:out value="${form.contractItemData.groupLeaderUomTxt}"/>
									</span>
								</td>
								<td>
								</td>
							</tr>
							<tr class="zebra1">
								<td>
									<span style="white-space: nowrap;">
										<fmt:message key="sapContractItemData.panel.table.groupleadersumitemquantitydelivered"/>
									</span>
								</td>
								<td>
									<span style="white-space: nowrap;">
										<c:out value="${form.contractItemData.sumItemQuantityDelivered}"/>
										<c:out value="${form.contractItemData.groupLeaderUomTxt}"/>
									</span>
								</td>
								<td>
								</td>
							</tr>
						</c:if>
					</tbody>
				</table>
			</td>
		</tr>
	</table>
	<c:if test="${form.contractItemData.contractQuantityRestricted}">
		<table style="width: 100%">
			<tr>
				<td style="padding-top: 0px">
					<span class="labelblue">
						<fmt:message key="sapContractItemData.quantityrestriectedpanel.title"/>
					</span>
					<table class="classicblue" style="width: 100%; margin-top: 4px">
						<tbody>
							<tr>
								<th>
									<fmt:message key="sapContractItemData.quantityrestriectedpanel.table.header.ordernumber"/>
								</th>
								<th>
									<fmt:message key="sapContractItemData.quantityrestriectedpanel.table.header.orderitemnumber"/>
								</th>
								<th>
									<fmt:message key="sapContractItemData.quantityrestriectedpanel.table.header.deliveredquantity"/>
								</th>
								<th>
									<fmt:message key="sapContractItemData.quantityrestriectedpanel.table.header.sentorderitemnumber"/>
								</th>
								<th>
									<fmt:message key="sapContractItemData.quantityrestriectedpanel.table.header.deletedflag"/>
								</th>
							</tr>
							
							<c:forEach var="contractItemOrderItem" items="${form.contractItemData.contractItemOrderItemList}">
								<tr class="zebra0">
									<td>
										<span style="white-space: nowrap;">
											<c:out value="${contractItemOrderItem.orderNumber}"/>
										</span>
									</td>
									<td>
										<span style="white-space: nowrap;">
											<c:out value="${contractItemOrderItem.orderItemNo}"/>
										</span>
									</td>
									<td>
										<span style="white-space: nowrap;">
											<c:out value="${contractItemOrderItem.deliveredQuantity}"/>
										</span>
									</td>
									<td>
										<span style="white-space: nowrap;">
											<c:out value="${contractItemOrderItem.sentOrderItemNo}"/>
										</span>
									</td>
									<td>
										<span style="white-space: nowrap;">
											<c:out value="${contractItemOrderItem.deletedFlag}"/>
										</span>
									</td>
								</tr>							
							</c:forEach>
						</tbody>
					</table>
				</td>
			</tr>
		</table>
	</c:if>
</c:if>