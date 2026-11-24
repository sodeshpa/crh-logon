<%@ page session="true" %>

<%@ include file="../includeTLDs.jsp" %>

<script type="text/javascript">
	function setEventOptions(eventClassName) {
		var diva = Ext.get('eventOptionsDiv');
		var eventClassName = document.getElementById('eventTypeSelect').options[document.getElementById('eventTypeSelect').selectedIndex].value;
		var assignmentId = document.getElementById('assignmentId').value;
		var truckId = document.getElementById('truckId').value;
		diva.load( {
			url :'ManualPDATrigger.do',
			params : {
				'formAction': 'getPDAEventOptions',
				'eventClassName' :eventClassName,
				'assignmentId' :assignmentId,
				'truckId' :truckId  
			},
			scripts :true
		});
	}
</script>


<c:if test="false">
    <%-- for preview in MyEclipse --%>
    <link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>


<c:set var="pdaForm" value="${sessionScope.admin_ManualPDATriggerForm}"/>

<jsp:include page="../SubTab11Tile.jsp" />

<!-- Begin table 3 rows -->
<table class="big_form2">

	<tr class="classicblue">
		<th class="classicblue"><fmt:message key="tabitem.test" />
		/ <fmt:message key="tabsubitem.11.2" /></th>
	</tr>
	<tr>

		<td><!-- beginn Border-Table   -->

<html:form action="test/ManualPDATrigger.do">
	<table width="100%">
		<tr>
			<td style="vertical-align: top;" width="25%">
				<table class="classicblue" style="width:100%">
					<tr>
						<td class="label left" width="20%">
							Event type
						</td>
						<td>
							<html:select size="1" property="eventType" styleId="eventTypeSelect">
								<c:forEach var="eventType" items="${pdaForm.eventTypes}">
									<html:option value="${eventType.name}"><c:out value="${eventType.simpleName}"/></html:option>
								</c:forEach>
							</html:select>
						</td>
					</tr>
					<tr>
						<td class="label left" width="20%">
							assignmentId
						</td>
						<td>
							<html:text styleId="assignmentId" name="assignmentId" property="assignmentId" maxlength="10" size="10" styleClass="textfield" value="${pdaForm.assignmentId}"/>
						</td>
					</tr>
					<tr>
						<td class="label left" width="20%">
							truckId
						</td>
						<td>
							<html:text styleId="truckId" name="truckId" property="truckId" maxlength="10" size="10" styleClass="textfield" value="${pdaForm.truckId}"/>
						</td>
					</tr>
					<tr>
						<td class="buttons" colspan="2">
							<input type="button" onclick="setEventOptions()" class="ViewButtonStyle" value="get"/>
							<html:submit value="submit" property="formAction" styleClass="ViewButtonStyle" ></html:submit>
						</td>
					</tr>
				</table>
			</td>
			<td style="vertical-align: top;">
				<div id="eventOptionsDiv">
					<jsp:include page="PDAEventOptions.jsp"></jsp:include>
				</div>
			</td>
		</tr>
	</table>				
</html:form>
</td>
</tr>
</table>
