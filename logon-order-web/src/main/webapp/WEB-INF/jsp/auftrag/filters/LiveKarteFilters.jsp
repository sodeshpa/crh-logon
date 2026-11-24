<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: LiveKarteFilters.jsp,v $ --%>

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>

<%@ page import="com.crh.logon.order.web.forms.auftrag.LiveKarteForm" %>

<c:set var="org.apache.struts.taglib.html.BEAN" scope="page" value="${order_auftrag_LiveKarteForm}"/>

	<table style="width: 100%; padding: 0px; margin: 0px;">
		<colgroup>
			<col width="15%">
			<col width="15%">
			<col width="14%">
			<col width="14%">
			<col width="14%">
			<col width="14%">
			<col width="14%">
		</colgroup>

		<tbody>
			<tr>
				<td class="border"><span class="labelblue"><fmt:message key="livemap.label.plant" /></span>
				<div id="plantSelection" class="subformlist"
					style="font-size: 12px; margin-top: 4px; height: 80px;">
				<table class="compact" cellpadding="0" cellspacing="0" border="0"
					style="width: 100%;">
					<colgroup>
						<col width="1%">
						<col width="99%">
					</colgroup>
					<tbody>
						<c:set var="counter" value="1" />
						<c:forEach var="entry" varStatus="status"
							items="${order_auftrag_LiveKarteForm.plantMap}">
							<c:if test="${entry.value.visible}">
								<tr class="zebra<c:out value="${counter % 2}"/>">
									<td valign="middle" style="width: 1px;"><html:checkbox
										property="plantCheckBoxes[${status.count - 1}]" value="true"
										onclick="refreshFilters()" style="border: 0px none ; margin: 0px; padding: 0px;"/></td>
									<td><c:out value="${entry.value.value}" /></td>
								</tr>
								<c:set var="counter" value="${counter + 1}" />
							</c:if>
						</c:forEach>
						<c:forEach var="entry" varStatus="status"
							items="${order_auftrag_LiveKarteForm.plantMap}">
							<c:if test="${!entry.value.visible}">
								<tr style="visibility: hidden; position: absolute;">
									<td valign="middle" style="width: 1px;"><html:checkbox
										property="plantCheckBoxes[${status.count - 1}]" value="true"
										onclick="refreshFilters()" style="visibility: hidden; position: absolute;"  /></td>
									<td><c:out value="${entry.value.value}" /></td>
								</tr>
							</c:if>
						</c:forEach>
					</tbody>
				</table>
				</div>
				<div align="right"><input type="button"
					onclick="selectAll('plantSelection',false)"
					class="SmallButtonStyle" value=" <fmt:message key="livemap.label.unselect" /> ">&nbsp;&nbsp;<input
					type="button" onclick="selectAll('plantSelection',true)"
					class="SmallButtonStyle" value=" <fmt:message key="livemap.label.select" /> "></div>
				</td>




				<td class="border"><span class="labelblue"><fmt:message key="livemap.label.shpoint" /></span>
				<div id="shPointSelection" class="subformlist"
					style="font-size: 12px; margin-top: 4px; height: 80px;">
				<table class="compact" cellpadding="0" cellspacing="0" border="0"
					style="width: 100%;">
					<colgroup>
						<col width="1%">
						<col width="99%">
					</colgroup>
					<tbody>
						<c:set var="counter" value="1" />
						<c:forEach var="entry" varStatus="status"
							items="${order_auftrag_LiveKarteForm.shPointMap}">
							<c:if test="${entry.value.visible}">
								<tr class="zebra<c:out value="${counter % 2}"/>">
									<td valign="middle" style="width: 1px"><html:checkbox
										property="shPointCheckBoxes[${status.count - 1}]" value="true"
										onclick="refreshFilters()" style="border: 0px none ; margin: 0px; padding: 0px;" /></td>
									<td><c:out value="${entry.value.value}" /></td>
								</tr>
								<c:set var="counter" value="${counter + 1}" />
							</c:if>
						</c:forEach>
						<c:forEach var="entry" varStatus="status"
							items="${order_auftrag_LiveKarteForm.shPointMap}">
							<c:if test="${!entry.value.visible}">
								<tr style="visibility: hidden; position: absolute;">
									<td valign="middle" style="width: 1px"><html:checkbox
										property="shPointCheckBoxes[${status.count - 1}]" value="true"
										onclick="refreshFilters()" style="visibility: hidden; position: absolute;" /></td>
									<td><c:out value="${entry.value.value}" /></td>
								</tr>
							</c:if>
						</c:forEach>

					</tbody>
				</table>
				</div>
				<div align="right"><input type="button"
					onclick="selectAll('shPointSelection',false)"
					class="SmallButtonStyle" value=" <fmt:message key="livemap.label.unselect" /> ">&nbsp;&nbsp;<input
					type="button" onclick="selectAll('shPointSelection',true)"
					class="SmallButtonStyle" value=" <fmt:message key="livemap.label.select" /> "></div>
				</td>

				<td class="border"><span class="labelblue"><fmt:message key="livemap.label.ldpoint" /></span>
				<div id="ldPointSelection" class="subformlist"
					style="font-size: 12px; margin-top: 4px; height: 80px;">
				<table class="compact" cellpadding="0" cellspacing="0" border="0"
					style="width: 100%;">
					<colgroup>
						<col width="1%">
						<col width="99%">
					</colgroup>
					<tbody>
						<c:set var="counter" value="1" />
						<c:forEach var="entry" varStatus="status"
							items="${order_auftrag_LiveKarteForm.ldPointMap}">
							<c:if test="${entry.value.visible}">
								<tr class="zebra<c:out value="${counter % 2}"/>">
									<td valign="middle" style="width: 1px"><html:checkbox
										property="ldPointCheckBoxes[${status.count - 1}]" value="true"
										onclick="refreshFilters()" style="border: 0px none ; margin: 0px; padding: 0px;" /></td>
									<td><c:out value="${entry.value.value}" /></td>
								</tr>
								<c:set var="counter" value="${counter + 1}" />
							</c:if>
						</c:forEach>
						<c:forEach var="entry" varStatus="status"
							items="${order_auftrag_LiveKarteForm.ldPointMap}">
							<c:if test="${!entry.value.visible}">
								<tr style="visibility: hidden; position: absolute;">
									<td valign="middle" style="width: 1px"><html:checkbox
										property="ldPointCheckBoxes[${status.count - 1}]" value="true"
										onclick="refreshFilters()" style="visibility: hidden; position: absolute;" /></td>
									<td><c:out value="${entry.value.value}" /></td>
								</tr>
							</c:if>
						</c:forEach>
					</tbody>
				</table>
				</div>
				<div align="right"><input type="button"
					onclick="selectAll('ldPointSelection',false)"
					class="SmallButtonStyle" value=" <fmt:message key="livemap.label.unselect" /> ">&nbsp;&nbsp;<input
					type="button" onclick="selectAll('ldPointSelection',true)"
					class="SmallButtonStyle" value=" <fmt:message key="livemap.label.select" /> "></div>
				</td>

				<td class="border"><span class="labelblue"><fmt:message key="livemap.label.pool" /></span>
				<div id="poolSelection" class="subformlist"
					style="font-size: 12px; margin-top: 4px; height: 80px;">
				<table class="compact" cellpadding="0" cellspacing="0" border="0"
					style="width: 100%;">
					<colgroup>
						<col width="1%">
						<col width="99%">
					</colgroup>
					<tbody>
						<c:set var="counter" value="1" />
						<c:forEach var="entry" varStatus="status"
							items="${order_auftrag_LiveKarteForm.poolMap}">
							<c:if test="${entry.value.visible}">
								<tr class="zebra<c:out value="${counter % 2}"/>">
									<td valign="middle" style="width: 1px"><html:checkbox
										property="poolCheckBoxes[${status.count - 1}]" value="true"
										onclick="refreshFilters()" style="border: 0px none ; margin: 0px; padding: 0px;" /></td>
									<td><c:out value="${entry.value.value}" /></td>
								</tr>
								<c:set var="counter" value="${counter + 1}" />
							</c:if>
						</c:forEach>
						<c:forEach var="entry" varStatus="status"
							items="${order_auftrag_LiveKarteForm.poolMap}">
							<c:if test="${!entry.value.visible}">
								<tr style="visibility: hidden; position: absolute;">
									<td valign="middle" style="width: 1px"><html:checkbox
										property="poolCheckBoxes[${status.count - 1}]" value="true"
										onclick="refreshFilters()" style="visibility: hidden; position: absolute;" /></td>
									<td><c:out value="${entry.value.value}" /></td>
								</tr>
							</c:if>
						</c:forEach>
					</tbody>
				</table>
				</div>
				<div align="right"><input type="button"
					onclick="selectAll('poolSelection',false)" class="SmallButtonStyle"
					value=" <fmt:message key="livemap.label.unselect" /> ">&nbsp;&nbsp;<input type="button"
					onclick="selectAll('poolSelection',true)" class="SmallButtonStyle"
					value=" <fmt:message key="livemap.label.select" /> "></div>
				</td>

				<td class="border"><span class="labelblue"><fmt:message key="livemap.label.carrier" /></span>
				<div id="carrierSelection" class="subformlist"
					style="font-size: 12px; margin-top: 4px; height: 80px;">
				<table class="compact" cellpadding="0" cellspacing="0" border="0"
					style="width: 100%;">
					<colgroup>
						<col width="1%">
						<col width="99%">
					</colgroup>
					<tbody>
						<c:set var="counter" value="1" />
						<c:forEach var="entry" varStatus="status"
							items="${order_auftrag_LiveKarteForm.carrierMap}">
							<c:if test="${entry.value.visible}">
								<tr class="zebra<c:out value="${counter % 2}"/>">
									<td valign="middle" style="width: 1px"><html:checkbox
										property="carrierCheckBoxes[${status.count - 1}]" value="true"
										onclick="refreshFilters()" style="border: 0px none ; margin: 0px; padding: 0px;" /></td>
									<td><c:out value="${entry.value.value}" /></td>
								</tr>
								<c:set var="counter" value="${counter + 1}" />
							</c:if>
						</c:forEach>
						<c:forEach var="entry" varStatus="status"
							items="${order_auftrag_LiveKarteForm.carrierMap}">
							<c:if test="${!entry.value.visible}">
								<tr style="visibility: hidden; position: absolute;">
									<td valign="middle" style="width: 1px"><html:checkbox
										property="carrierCheckBoxes[${status.count - 1}]" value="true"
										onclick="refreshFilters()" style="visibility: hidden; position: absolute;" /></td>
									<td><c:out value="${entry.value.value}" /></td>
								</tr>
							</c:if>
						</c:forEach>
					</tbody>
				</table>
				</div>
				<div align="right"><input type="button"
					onclick="selectAll('carrierSelection',false)"
					class="SmallButtonStyle" value=" <fmt:message key="livemap.label.unselect" /> ">&nbsp;&nbsp;<input
					type="button" onclick="selectAll('carrierSelection',true)"
					class="SmallButtonStyle" value=" <fmt:message key="livemap.label.select" /> "></div>
				</td>

				<td class="border"><span class="labelblue"><fmt:message key="livemap.label.truck" /></span>
				<div id="truckSelection" class="subformlist"
					style="font-size: 12px; margin-top: 4px; height: 80px;">
				<table class="compact" cellpadding="0" cellspacing="0" border="0"
					style="width: 100%;">
					<colgroup>
						<col width="1%">
						<col width="99%">
					</colgroup>
					<tbody>
						<c:set var="counter" value="1" />
						<c:forEach var="entry" varStatus="status"
							items="${order_auftrag_LiveKarteForm.truckMap}">
							<c:if test="${entry.value.visible}">
								<tr class="zebra<c:out value="${counter % 2}"/>">
									<td valign="middle" style="width: 1px"><html:checkbox
										property="truckCheckBoxes[${status.count - 1}]" value="true"
										onclick="refreshFilters()" style="border: 0px none ; margin: 0px; padding: 0px;" /></td>
									<td><c:out value="${entry.value.value}" /></td>
								</tr>
								<c:set var="counter" value="${counter + 1}" />
							</c:if>
						</c:forEach>
						<c:forEach var="entry" varStatus="status"
							items="${order_auftrag_LiveKarteForm.truckMap}">
							<c:if test="${!entry.value.visible}">
								<tr style="visibility: hidden; position: absolute;">
									<td valign="middle" style="width: 1px"><html:checkbox
										property="truckCheckBoxes[${status.count - 1}]" value="true"
										onclick="refreshFilters()" style="visibility: hidden; position: absolute;" /></td>
									<td><c:out value="${entry.value.value}" /></td>
								</tr>
							</c:if>
						</c:forEach>
					</tbody>
				</table>
				</div>
				<div align="right"><input type="button"
					onclick="selectAll('truckSelection',false)"
					class="SmallButtonStyle" value=" <fmt:message key="livemap.label.unselect" /> ">&nbsp;&nbsp;<input
					type="button" onclick="selectAll('truckSelection',true)"
					class="SmallButtonStyle" value=" <fmt:message key="livemap.label.select" /> "></div>
				</td>
				<td class="border"><span class="labelblue"><fmt:message key="livemap.label.driver" /></span>
				<div id="driverSelection" class="subformlist"
					style="font-size: 12px; margin-top: 4px; height: 80px;">
				<table class="compact" cellpadding="0" cellspacing="0" border="0"
					style="width: 100%;">
					<colgroup>
						<col width="1%">
						<col width="99%">
					</colgroup>
					<tbody>
						<c:set var="counter" value="1" />
						<c:forEach var="entry" varStatus="status"
							items="${order_auftrag_LiveKarteForm.driverMap}">
							<c:if test="${entry.value.visible}">
								<tr class="zebra<c:out value="${counter % 2}"/>">
									<td valign="middle" style="width: 1px"><html:checkbox
										property="driverCheckBoxes[${status.count - 1}]" value="true"
										onclick="refreshFilters()" style="border: 0px none ; margin: 0px; padding: 0px;" /></td>
									<td><c:out value="${entry.value.value}" /></td>
								</tr>
								<c:set var="counter" value="${counter + 1}" />
							</c:if>
						</c:forEach>
						<c:forEach var="entry" varStatus="status"
							items="${order_auftrag_LiveKarteForm.driverMap}">
							<c:if test="${!entry.value.visible}">
								<tr style="visibility: hidden; position: absolute;">
									<td valign="middle" style="width: 1px"><html:checkbox
										property="driverCheckBoxes[${status.count - 1}]" value="true"
										onclick="refreshFilters()" style="visibility: hidden; position: absolute;" /></td>
									<td><c:out value="${entry.value.value}" /></td>
								</tr>
							</c:if>
						</c:forEach>
					</tbody>
				</table>
				</div>
				<div align="right"><input type="button"
					onclick="selectAll('driverSelection',false)"
					class="SmallButtonStyle" value=" <fmt:message key="livemap.label.unselect" /> ">&nbsp;&nbsp;<input
					type="button" onclick="selectAll('driverSelection',true)"
					class="SmallButtonStyle" value=" <fmt:message key="livemap.label.select" /> "></div>
				</td>

			</tr>
			<tr>
				<td class="border"><span class="labelblue"><fmt:message key="livemap.label.sarea" /></span>
				<div id="sareaSelection" class="subformlist"
					style="font-size: 12px; margin-top: 4px; height: 80px;">
				<table class="compact" cellpadding="0" cellspacing="0" border="0"
					style="width: 100%;">
					<colgroup>
						<col width="1%">
						<col width="99%">
					</colgroup>
					<tbody>
						<c:set var="counter" value="1" />
						<c:forEach var="entry" varStatus="status"
							items="${order_auftrag_LiveKarteForm.sareaMap}">
							<c:if test="${entry.value.visible}">
								<tr class="zebra<c:out value="${counter % 2}"/>">
									<td valign="middle" style="width: 1px"><html:checkbox
										property="sareaCheckBoxes[${status.count - 1}]" value="true"
										onclick="refreshFilters()" style="border: 0px none ; margin: 0px; padding: 0px;" /></td>
									<td><c:out value="${entry.value.value}" /></td>
								</tr>
								<c:set var="counter" value="${counter + 1}" />
							</c:if>
						</c:forEach>
						<c:forEach var="entry" varStatus="status"
							items="${order_auftrag_LiveKarteForm.sareaMap}">
							<c:if test="${!entry.value.visible}">
								<tr style="visibility: hidden; position: absolute;">
									<td valign="middle" style="width: 1px"><html:checkbox
										property="sareaCheckBoxes[${status.count - 1}]" value="true"
										onclick="refreshFilters()" style="visibility: hidden; position: absolute;" /></td>
									<td><c:out value="${entry.value.value}" /></td>
								</tr>
							</c:if>
						</c:forEach>
					</tbody>
				</table>
				</div>
				<div align="right"><input type="button"
					onclick="selectAll('sareaSelection',false)"
					class="SmallButtonStyle" value=" <fmt:message key="livemap.label.unselect" /> ">&nbsp;&nbsp;<input
					type="button" onclick="selectAll('sareaSelection',true)"
					class="SmallButtonStyle" value=" <fmt:message key="livemap.label.select" /> "></div>
				</td>

				<td class="border"><span class="labelblue"><fmt:message key="livemap.label.soffice" /></span>
				<div id="sofficeSelection" class="subformlist"
					style="font-size: 12px; margin-top: 4px; height: 80px;">
				<table class="compact" cellpadding="0" cellspacing="0" border="0"
					style="width: 100%;">
					<colgroup>
						<col width="1%">
						<col width="99%">
					</colgroup>
					<tbody>
						<c:set var="counter" value="1" />
						<c:forEach var="entry" varStatus="status"
							items="${order_auftrag_LiveKarteForm.sofficeMap}">
							<c:if test="${entry.value.visible}">
								<tr class="zebra<c:out value="${counter % 2}"/>">
									<td valign="middle" style="width: 1px"><html:checkbox
										property="sofficeCheckBoxes[${status.count - 1}]" value="true"
										onclick="refreshFilters()" style="border: 0px none ; margin: 0px; padding: 0px;" /></td>
									<td><c:out value="${entry.value.value}" /></td>
								</tr>
								<c:set var="counter" value="${counter + 1}" />
							</c:if>
						</c:forEach>
						<c:forEach var="entry" varStatus="status"
							items="${order_auftrag_LiveKarteForm.sofficeMap}">
							<c:if test="${!entry.value.visible}">
								<tr style="visibility: hidden; position: absolute;">
									<td valign="middle" style="width: 1px"><html:checkbox
										property="sofficeCheckBoxes[${status.count - 1}]" value="true"
										onclick="refreshFilters()" style="visibility: hidden; position: absolute;" /></td>
									<td><c:out value="${entry.value.value}" /></td>
								</tr>
							</c:if>
						</c:forEach>
					</tbody>
				</table>
				</div>
				<div align="right"><input type="button"
					onclick="selectAll('sofficeSelection',false)"
					class="SmallButtonStyle" value=" <fmt:message key="livemap.label.unselect" /> ">&nbsp;&nbsp;<input
					type="button" onclick="selectAll('sofficeSelection',true)"
					class="SmallButtonStyle" value=" <fmt:message key="livemap.label.select" /> "></div>
				</td>

				<td class="border"><span class="labelblue"><fmt:message key="livemap.label.sgroup" /></span>
				<div id="sgroupSelection" class="subformlist"
					style="font-size: 12px; margin-top: 4px; height: 80px;">
				<table class="compact" cellpadding="0" cellspacing="0" border="0"
					style="width: 100%;">
					<colgroup>
						<col width="1%">
						<col width="99%">
					</colgroup>
					<tbody>
						<c:set var="counter" value="1" />
						<c:forEach var="entry" varStatus="status"
							items="${order_auftrag_LiveKarteForm.sgroupMap}">
							<c:if test="${entry.value.visible}">
								<tr class="zebra<c:out value="${counter % 2}"/>">
									<td valign="middle" style="width: 1px"><html:checkbox
										property="sgroupCheckBoxes[${status.count - 1}]" value="true"
										onclick="refreshFilters()" style="border: 0px none ; margin: 0px; padding: 0px;" /></td>
									<td><c:out value="${entry.value.value}" /></td>
								</tr>
								<c:set var="counter" value="${counter + 1}" />
							</c:if>
						</c:forEach>
						<c:forEach var="entry" varStatus="status"
							items="${order_auftrag_LiveKarteForm.sgroupMap}">
							<c:if test="${!entry.value.visible}">
								<tr style="visibility: hidden; position: absolute;">
									<td valign="middle" style="width: 1px"><html:checkbox
										property="sgroupCheckBoxes[${status.count - 1}]" value="true"
										onclick="refreshFilters()" style="visibility: hidden; position: absolute;" /></td>
									<td><c:out value="${entry.value.value}" /></td>
								</tr>
							</c:if>
						</c:forEach>
					</tbody>
				</table>
				</div>
				<div align="right"><input type="button"
					onclick="selectAll('sgroupSelection',false)"
					class="SmallButtonStyle" value=" <fmt:message key="livemap.label.unselect" /> ">&nbsp;&nbsp;<input
					type="button" onclick="selectAll('sgroupSelection',true)"
					class="SmallButtonStyle" value=" <fmt:message key="livemap.label.select" /> "></div>
				</td>

				<td class="border"><span class="labelblue"><fmt:message key="livemap.label.soldto" /></span>
				<div id="soldtoSelection" class="subformlist"
					style="font-size: 12px; margin-top: 4px; height: 80px;">
				<table class="compact" cellpadding="0" cellspacing="0" border="0"
					style="width: 100%;">
					<colgroup>
						<col width="1%">
						<col width="99%">
					</colgroup>
					<tbody>
						<c:set var="counter" value="1" />
						<c:forEach var="entry" varStatus="status"
							items="${order_auftrag_LiveKarteForm.soldtoMap}">
							<c:if test="${entry.value.visible}">
								<tr class="zebra<c:out value="${counter % 2}"/>">
									<td valign="middle" style="width: 1px"><html:checkbox
										property="soldtoCheckBoxes[${status.count - 1}]" value="true"
										onclick="refreshFilters()" style="border: 0px none ; margin: 0px; padding: 0px;" /></td>
									<td><c:out value="${entry.value.value}" /></td>
								</tr>
								<c:set var="counter" value="${counter + 1}" />
							</c:if>
						</c:forEach>
						<c:forEach var="entry" varStatus="status"
							items="${order_auftrag_LiveKarteForm.soldtoMap}">
							<c:if test="${!entry.value.visible}">
								<tr style="visibility: hidden; position: absolute;">
									<td valign="middle" style="width: 1px"><html:checkbox
										property="soldtoCheckBoxes[${status.count - 1}]" value="true"
										onclick="refreshFilters()" style="visibility: hidden; position: absolute;" /></td>
									<td><c:out value="${entry.value.value}" /></td>
								</tr>
							</c:if>
						</c:forEach>
					</tbody>
				</table>
				</div>
				<div align="right"><input type="button"
					onclick="selectAll('soldtoSelection',false)"
					class="SmallButtonStyle" value=" <fmt:message key="livemap.label.unselect" /> ">&nbsp;&nbsp;<input
					type="button" onclick="selectAll('soldtoSelection',true)"
					class="SmallButtonStyle" value=" <fmt:message key="livemap.label.select" /> "></div>
				</td>

				<td class="border"><span class="labelblue"><fmt:message key="livemap.label.shipto" /></span>
				<div id="shiptoSelection" class="subformlist"
					style="font-size: 12px; margin-top: 4px; height: 80px;">
				<table class="compact" cellpadding="0" cellspacing="0" border="0"
					style="width: 100%;">
					<colgroup>
						<col width="1%">
						<col width="99%">
					</colgroup>
					<tbody>
						<c:set var="counter" value="1" />
						<c:forEach var="entry" varStatus="status"
							items="${order_auftrag_LiveKarteForm.shiptoMap}">
							<c:if test="${entry.value.visible}">
								<tr class="zebra<c:out value="${counter % 2}"/>">
									<td valign="middle" style="width: 1px"><html:checkbox
										property="shiptoCheckBoxes[${status.count - 1}]" value="true"
										onclick="refreshFilters()" style="border: 0px none ; margin: 0px; padding: 0px;" /></td>
									<td><c:out value="${entry.value.value}" /></td>
								</tr>
								<c:set var="counter" value="${counter + 1}" />
							</c:if>
						</c:forEach>
						<c:forEach var="entry" varStatus="status"
							items="${order_auftrag_LiveKarteForm.shiptoMap}">
							<c:if test="${!entry.value.visible}">
								<tr style="visibility: hidden; position: absolute;">
									<td valign="middle" style="width: 1px"><html:checkbox
										property="shiptoCheckBoxes[${status.count - 1}]" value="true"
										onclick="refreshFilters()" style="visibility: hidden; position: absolute;" /></td>
									<td><c:out value="${entry.value.value}" /></td>
								</tr>
							</c:if>
						</c:forEach>
					</tbody>
				</table>
				</div>
				<div align="right"><input type="button"
					onclick="selectAll('shiptoSelection',false)"
					class="SmallButtonStyle" value=" <fmt:message key="livemap.label.unselect" /> ">&nbsp;&nbsp;<input
					type="button" onclick="selectAll('shiptoSelection',true)"
					class="SmallButtonStyle" value=" <fmt:message key="livemap.label.select" /> "></div>
				</td>

				<td class="border"><span class="labelblue"><fmt:message key="livemap.label.material" /></span>
				<div id="materialSelection" class="subformlist"
					style="font-size: 12px; margin-top: 4px; height: 80px;">
				<table class="compact" cellpadding="0" cellspacing="0" border="0"
					style="width: 100%;">
					<colgroup>
						<col width="1%">
						<col width="99%">
					</colgroup>
					<tbody>
						<c:set var="counter" value="1" />
						<c:forEach var="entry" varStatus="status"
							items="${order_auftrag_LiveKarteForm.materialMap}">
							<c:if test="${entry.value.visible}">
								<tr class="zebra<c:out value="${counter % 2}"/>">
									<td valign="middle" style="width: 1px"><html:checkbox
										property="materialCheckBoxes[${status.count - 1}]"
										value="true" onclick="refreshFilters()" style="border: 0px none ; margin: 0px; padding: 0px;" /></td>
									<td><c:out value="${entry.value.value}" /></td>
								</tr>
								<c:set var="counter" value="${counter + 1}" />
							</c:if>
						</c:forEach>
						<c:forEach var="entry" varStatus="status"
							items="${order_auftrag_LiveKarteForm.materialMap}">
							<c:if test="${!entry.value.visible}">
								<tr style="visibility: hidden; position: absolute;">
									<td valign="middle" style="width: 1px"><html:checkbox
										property="materialCheckBoxes[${status.count - 1}]"
										value="true" onclick="refreshFilters()" style="visibility: hidden; position: absolute;" /></td>
									<td><c:out value="${entry.value.value}" /></td>
								</tr>
							</c:if>
						</c:forEach>
					</tbody>
				</table>
				</div>
				<div align="right"><input type="button"
					onclick="selectAll('materialSelection',false)"
					class="SmallButtonStyle" value=" <fmt:message key="livemap.label.unselect" /> ">&nbsp;&nbsp;<input
					type="button" onclick="selectAll('materialSelection',true)"
					class="SmallButtonStyle" value=" <fmt:message key="livemap.label.select" /> "></div>
				</td>

				<td class="border"><span class="labelblue"><fmt:message key="livemap.label.order.number" /></span>
				<div id="orderNumSelection" class="subformlist"
					style="font-size: 12px; margin-top: 4px; height: 80px;">
				<table class="compact" cellpadding="0" cellspacing="0" border="0"
					style="width: 100%;">
					<colgroup>
						<col width="1%">
						<col width="99%">
					</colgroup>
					<tbody>
						<c:set var="counter" value="1" />
						<c:forEach var="entry" varStatus="status"
							items="${order_auftrag_LiveKarteForm.orderNumMap}">
							<c:if test="${entry.value.visible}">
								<tr class="zebra<c:out value="${counter % 2}"/>">
									<td valign="middle" style="width: 1px"><html:checkbox
										property="orderNumCheckBoxes[${status.count - 1}]"
										value="true" onclick="refreshFilters()" style="border: 0px none ; margin: 0px; padding: 0px;" /></td>
									<td><c:out value="${entry.value.value}" /></td>
								</tr>
								<c:set var="counter" value="${counter + 1}" />
							</c:if>
						</c:forEach>
						<c:forEach var="entry" varStatus="status"
							items="${order_auftrag_LiveKarteForm.orderNumMap}">
							<c:if test="${!entry.value.visible}">
								<tr style="visibility: hidden; position: absolute;">
									<td valign="middle" style="width: 1px"><html:checkbox
										property="orderNumCheckBoxes[${status.count - 1}]"
										value="true" onclick="refreshFilters()" style="visibility: hidden; position: absolute;" /></td>
									<td><c:out value="${entry.value.value}" /></td>
								</tr>
							</c:if>
						</c:forEach>
					</tbody>
				</table>
				</div>
				<div align="right"><input type="button"
					onclick="selectAll('orderNumSelection',false)"
					class="SmallButtonStyle" value=" <fmt:message key="livemap.label.unselect" /> ">&nbsp;&nbsp;<input
					type="button" onclick="selectAll('orderNumSelection',true)"
					class="SmallButtonStyle" value=" <fmt:message key="livemap.label.select" /> "></div>
				</td>

			</tr>
		</tbody>
	</table>





<script type="text/javascript">

	//refresh(load) map objects only the map is loaded itself
	if (typeof(gmap) != "undefined") {
		loadAllMapObjects();
	}

</script>
