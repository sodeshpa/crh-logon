<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>
<%@ page import="com.crh.logon.order.web.actions.itrack.ITrackReportingEntryAction" %>
<%@ page import="com.crh.logon.web.utils.Authorization" %>


<%@ page session="true" %>

<%@ include file="../includeTLDs.jsp" %>


<script type="text/javascript" src="<crh-common:url value='/dwr/interface/ITrackReportingAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/itrackReporting.js'/>"></script>
<table class="classicblue" cellpadding="0" cellspacing="0" border="0" width="100%" style="padding: 0px; margin: 0px;">
	<tbody>
	<tr>
		<td style="border-top">
			<span class="subtitle1">ITrack Reporting</span>
		</td>
	</tr>


	<tr>
		<td>
			<fmt:message key="itrackreporting.filters.end.time.from"/>
			<input type="text" id="fromDatePicker"/>
		</td>
		<td>
			<fmt:message key="itrackreporting.filters.end.time.to"/>
			<input type="text" id="toDatePicker"/>
		</td>
	</tr>
	<tr>
		<td width="15%">
			<div id="DivSareaSelection" style="margin: 4px 0px;" class="subform">
				<table cellspacing="0" cellpadding="0" style="width: 100%;" class="noborder">
					<tbody>
					<tr>
						<td><span class="label"><fmt:message key="itrackreporting.filters.sales.area"/></span>
							<div style="font-size: 12px; margin-top: 4px; height: 120px;" class="subformlist">
								<table cellspacing="0" cellpadding="0" border="0" style="width: 100%;" class="noborder">
									<tbody>

									<tr class="zebra0">
										<td><fmt:message key="itrack.reporting.selectiontext.all"/></td>
									</tr>
									</tbody>
								</table>
							</div>
							<br>
							<input type="text" id="sareaCombo"/>
							<input type="button" id="addSareaButton" class="SmallButtonStyle"
								   value="<fmt:message key="itrack.reporting.button.add"/>"/>
							<input type="button" id="clearSareaSelectionButton" class="SmallButtonStyle"
								   value="<fmt:message key="itrack.reporting.button.clearselection"/>"/>
						</td>
					</tr>
					</tbody>
				</table>

			</div>
		</td>

		<td width="15%">
			<div id="DivShippingPointSelection" style="margin: 4px 0px;" class="subform">

				<table cellspacing="0" cellpadding="0" style="width: 100%;" class="noborder">

					<tbody>
					<tr>
						<td><span class="label"><fmt:message key="itrackreporting.filters.shipping.point"/></span> <!-- beginn
						customer/ship-to selection list: div with
						scrollbars -->
							<div style="font-size: 12px; margin-top: 4px; height: 120px;" class="subformlist">
								<table cellspacing="0" cellpadding="0" border="0" style="width: 100%;" class="noborder">
									<colgroup>
										<col width="99%">
										<col width="1%">
									</colgroup>
									<tbody>

									<tr class="zebra0">
										<td><fmt:message key="itrack.reporting.selectiontext.all"/></td>
									</tr>


									</tbody>
								</table>
							</div>
							<br>
							<input type="text" id="shippingpointCombo"/>
							<input type="button" id="addShippingPointButton" class="SmallButtonStyle"
								   value="<fmt:message key="itrack.reporting.button.add"/>"/>
							<input type="button" id="clearShippingPointSelectionButton" class="SmallButtonStyle"
								   value="<fmt:message key="itrack.reporting.button.clearselection"/>"/>
						</td>
					</tr>
					</tbody>
				</table>
			</div>
		</td>

		<td width="15%">
			<div id="DivCommCarrierSelection" style="margin: 4px 0px;" class="subform">
				<table cellspacing="0" cellpadding="0" style="width: 100%;" class="noborder">
					<tbody>
					<tr>
						<td><span class="label"><fmt:message key="itrackreporting.filters.comm.carrier"/></span> <!-- beginn
						customer/ship-to selection list: div with
						scrollbars -->
							<div style="font-size: 12px; margin-top: 4px; height: 120px;" class="subformlist">
								<table cellspacing="0" cellpadding="0" border="0" style="width: 100%;" class="noborder">
									<colgroup>
										<col width="99%">
										<col width="1%">
									</colgroup>
									<tbody>

									<tr class="zebra0">
										<td><fmt:message key="itrack.reporting.selectiontext.all"/></td>
									</tr>


									</tbody>
								</table>
							</div>
							<br>
							<input type="text" id="commCarrierCombo"/>
							<input type="button" id="addCommCarrierButton" class="SmallButtonStyle"
								   value="<fmt:message key="itrack.reporting.button.add"/>"/>
							<input type="button" id="clearCommCarrierSelectionButton" class="SmallButtonStyle"
								   value="<fmt:message key="itrack.reporting.button.clearselection"/>"/>
						</td>
					</tr>
					</tbody>
				</table>
			</div>
		</td>
		<td width="15%">
			<div id="DivExecCarrierSelection" style="margin: 4px 0px;" class="subform">

				<table cellspacing="0" cellpadding="0" style="width: 100%;" class="noborder">
					<tbody>
					<tr>
						<td><span class="label"><fmt:message key="itrackreporting.filters.exec.carrier"/></span>
							<div style="font-size: 12px; margin-top: 4px; height: 120px;" class="subformlist">
								<table cellspacing="0" cellpadding="0" border="0" style="width: 100%;" class="noborder">
									<colgroup>
										<col width="99%">
										<col width="1%">
									</colgroup>
									<tbody>

									<tr class="zebra0">
										<td><fmt:message key="itrack.reporting.selectiontext.all"/></td>
									</tr>


									</tbody>
								</table>
							</div>
							<br>
							<input type="text" id="execCarrierCombo"/>
							<input type="button" id="addExecCarrierButton" class="SmallButtonStyle"
								   value="<fmt:message key="itrack.reporting.button.add"/>"/>
							<input type="button" id="clearExecCarrierSelectionButton" class="SmallButtonStyle"
								   value="<fmt:message key="itrack.reporting.button.clearselection"/>"/>
						</td>
					</tr>
					</tbody>
				</table>
			</div>
		</td>

		<td width="15%">
			<div id="DivSoldtoSapBpSelection" style="margin: 4px 0px;" class="subform">
				<table cellspacing="0" cellpadding="0" style="width: 100%;" class="noborder">
					<tbody>
					<tr>
						<td><span class="label"><fmt:message key="itrackreporting.filters.sold.to"/></span>
							<div style="font-size: 12px; margin-top: 4px; height: 120px;" class="subformlist">
								<table cellspacing="0" cellpadding="0" border="0" style="width: 100%;" class="noborder">
									<colgroup>
										<col width="99%">
										<col width="1%">
									</colgroup>
									<tbody>

									<tr class="zebra0">
										<td><fmt:message key="itrack.reporting.selectiontext.all"/></td>
									</tr>


									</tbody>
								</table>
							</div>
							<br>
							<input type="text" id="soldtoCombo"/>
							<input type="button" id="addSoldtoSapBpButton" class="SmallButtonStyle"
								   value="<fmt:message key="itrack.reporting.button.add"/>"/>
							<input type="button" id="clearSoldtoSapBpSelectionButton" class="SmallButtonStyle"
								   value="<fmt:message key="itrack.reporting.button.clearselection"/>"/>
						</td>
					</tr>
					</tbody>
				</table>
			</div>
		</td>


		<td width="15%">
			<div id="DivShiptoSapBpSelection" style="margin: 4px 0px;" class="subform">


				<table cellspacing="0" cellpadding="0" style="width: 100%;" class="noborder">

					<tbody>
					<tr>
						<td><span class="label"><fmt:message key="itrackreporting.filters.ship.to"/></span>
							<div style="font-size: 12px; margin-top: 4px; height: 120px;" class="subformlist">


								<table cellspacing="0" cellpadding="0" border="0" style="width: 100%;" class="noborder">
									<colgroup>
										<col width="99%">
										<col width="1%">
									</colgroup>
									<tbody>

									<tr class="zebra0">
										<td><fmt:message key="itrack.reporting.selectiontext.all"/></td>
									</tr>


									</tbody>
								</table>
							</div>
							<br>
							<input type="text" id="shiptoCombo"/>
							<input type="button" id="addShiptoSapBpButton" class="SmallButtonStyle"
								   value="<fmt:message key="itrack.reporting.button.add"/>"/>
							<input type="button" id="clearShiptoSapBpSelectionButton" class="SmallButtonStyle"
								   value="<fmt:message key="itrack.reporting.button.clearselection"/>"/>
						</td>
					</tr>
					</tbody>
				</table>
			</div>
		</td>


	</tr>

	<tr>
		<td class="buttons right" colspan="6">
			<div class="buttons">
				<input type="button" id="exportXLSButton" class="ViewButtonStyle" value="<fmt:message
				key="itrackreporting.button.report.export"/>">
			</div>
		</td>
	</tr>
	</tbody>
</table>
<script type="text/javascript">
	Ext.onReady(function () {
		ITrackReporting.reportFunctions(
			{
				'dateFormat': '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
				'dateAltFormats': '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>'
			}
		);
	});
</script>