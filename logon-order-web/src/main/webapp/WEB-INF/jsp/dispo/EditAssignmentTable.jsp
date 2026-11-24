<%@ page
	import="com.crh.logon.web.utils.Authorization"
%>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>
<%@ include file="../includeTLDs.jsp" %>
<%-- LOGON-6102: Show date/time for simple vehicles (and only time for trucks) --%>
<%--  LOGON-6664 orders across midnight are always displayed in full date format --%>
<%--<c:choose>
	<c:when test="${order_dispo_AssignmentEditForm.showDates == true}">
		<c:set var="datePattern" value="dd.MM.yy HH:mm" scope="request"/>
	</c:when>
	<c:when test="${order_dispo_AssignmentEditForm.acrossMidnight == true}">
		<c:set var="datePattern" value="dd.MM.yy HH:mm" scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="datePattern" value="HH:mm" scope="request"/>
	</c:otherwise>
</c:choose>--%>

<!--  <%-- Struts needs this, fond no better way to do it --%> 
<html:form action="dispo/AssignmentEdit.do" onsubmit="return false;" styleId="AssignmentEditForm">
-->

<c:set var="zebra" value="1"/>

<c:set var="isTimeFieldsDisabled">true</c:set>
<crh-common:authorize function="<%= Authorization.DISPATCHING_INT_ORD_W %>">
<c:set var="isTimeFieldsDisabled">false</c:set>
</crh-common:authorize>


<html:hidden property="assignmentPartChanged" styleId="assignmentPartChanged"/>
<table style='width: 880px'>
			<tr>
				<td colspan=3>
						<table class="classicblue">
							<tr>
								<th width="20">
									&nbsp;
								</th>
								<th style='width: 50px' width="50">
									<fmt:message key="editassignment.texte.type"/>
								</th>
								<th style='width: 100px'>
									<fmt:message key="editassignment.texte.arrivaltime"/>
								</td>
								<th style='width: 90px'>
									<fmt:message key="editassignment.texte.departuretime"/>
								</th>
								<th style='width: 140px'>
									<fmt:message key="editassignment.texte.material"/>
								</th>
								<th style='width: 160px'>
									<fmt:message key="editassignment.texte.location"/>
								</th>
								<th style='width: 60px'>
									<fmt:message key="editassignment.texte.distance"/>
								</th>
								<th style='width: 90px'>
									<fmt:message key="editassignment.texte.ordernumber"/>
								</th>						
								<th style='width: 120px'>
									&nbsp;
								</th>
							</tr>
							<c:if test="${order_dispo_AssignmentEditForm.tourPartList != null}">
								
								<c:if test="${!order_dispo_AssignmentEditForm.hasPreLoading}">
									<tr class="zebra<c:out value='${zebra}'/>">
										<td>
										</td>
										<td>
											<fmt:message key="editassignment.type.pre"/>
										</td>
										<td colspan="6">&nbsp;</td>
										<td>
											<input type="button" class="SmallButtonStyle" value="<fmt:message key="editassignment.button.create"/>" onclick="submitPage({'createAssignmentPart': 'pre'})"/>
										</td>
									</tr>
								</c:if>
								
								<c:forEach var="tourPart" varStatus="status" items="${order_dispo_AssignmentEditForm.tourPartList}">
								<c:set target="${order_dispo_AssignmentEditForm}" property="currentIdx" value="${status.count-1}"/>
								
					<%-- block of hidden fields for providing info for time calculating javascript functions about he tour parts --%>			
								
								<html:hidden property="activeCoordLon[${status.count-1}]" styleId="${status.count-1}activeCoordLon" />
								<html:hidden property="activeCoordLat[${status.count-1}]" styleId="${status.count-1}activeCoordLat" />
								<html:hidden property="activeLocationType[${status.count-1}]" styleId="${status.count-1}activeLocationType" />
								
								<%-- fixed[x] is set to true when a real time is set for this entry --%>
								<c:if test="${order_dispo_AssignmentEditForm.realDepartureForCurrentIdx == null}">
									<html:hidden property="fixed[${status.count-1}]" styleId="${status.count-1}fixed" value="false" />
								</c:if>
								<c:if test="${order_dispo_AssignmentEditForm.realDepartureForCurrentIdx != null}">
									<html:hidden property="fixed[${status.count-1}]" styleId="${status.count-1}fixed" value="true" />
								</c:if>
								
								<%-- the timeModifier hidden fields are updated by javascript when 'Calc. other times' is invoked --%>
								<html:hidden property="timeModifier[${status.count-1}]" styleId="${status.count-1}timeModifier" value="0"/>
	
					<%-- end of block of hidden fields --%>
								<c:set var="zebra" value='${(zebra + 1) % 2 }'/>
								<tr class="zebra<c:out value='${zebra}'/>">
									<td>
										<c:set var="isChecked" value=""/>
										<c:if test="${status.count - 1 == order_dispo_AssignmentEditForm.selectedPartId}">
											<c:set var="isChecked" value="checked"/>
										</c:if>
										<input type="radio" id="selectedAssignmentPartSeqNo" name="selectedAssignmentPartSeqNo" value="<c:out value="${status.count-1}"/>" onclick="submitPage({'setSelectedPart':'<c:out value="${status.count-1}"/>'})" <c:out value="${isChecked}"/> />
									</td>
									<td>
										<c:if test="${tourPart.type == 0}">
											<fmt:message key="editassignment.tourpartType.pre"/>
										</c:if>
										<c:if test="${tourPart.type == 1}">
											<fmt:message key="editassignment.tourpartType.load"/>
										</c:if>
										<c:if test="${tourPart.type == 2}">
											<fmt:message key="editassignment.tourpartType.delivery"/>
										</c:if>
										<c:if test="${tourPart.type == 3}">
											<fmt:message key="editassignment.tourpartType.return"/>
										</c:if>
									</td>
									<td>
										<c:out value="${tourPart.plannedArrivalFmt}"/>
										<c:if test="${tourPart.plannedArrivalFmt == null}">
											-
										</c:if>
										/
										<c:if test="${tourPart.realArrivalFmt == null}">
											-
										</c:if>
										<c:out value="${tourPart.realArrivalFmt}"/>
										&nbsp;
										<c:out value="${tourPart.arrivalUTCOffset}"/>
									</td>
									<td>
										<c:out value="${tourPart.plannedDepartureFmt}"/>
										<c:if test="${tourPart.plannedDepartureFmt == null}">
											-
										</c:if>
										/
										<c:if test="${tourPart.realDepartureFmt == null}">
											-
										</c:if>
										<c:out value="${tourPart.realDepartureFmt}"/>
										<c:out value="${tourPart.departureUTCOffset}"/>
										&nbsp;
									</td>
									<td>
										<c:forEach var="material" varStatus="matStatus" items="${order_dispo_AssignmentEditForm.materialsForCurrentIdx}">
											<c:if test="${matStatus.count > 1}">
												<br>
											</c:if>
											<c:out value="${material}"/>
										</c:forEach>&nbsp;
									</td>
									<td>
										<c:out value="${order_dispo_AssignmentEditForm.locationForCurrentIdx}"/>
										&nbsp;
									</td>
									<td>
										<c:if test="${not empty tourPart.distance}">
											<fmt:formatNumber type="number" maxFractionDigits="1" value="${tourPart.distance}" /> km
										</c:if>
									</td>
									<td>
										<c:if test="${tourPart.order != null}">
											<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_R %>">
													<a href="javascript:submitPage({'showOrderDetail': true, 'orderId': <c:out value='${tourPart.order.orderId}'/>})">
											</crh-common:authorize>
											<c:out value="${tourPart.order.orderNumber}"/>
											<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_R %>">
												</a>
											</crh-common:authorize>
										</c:if>
										<c:if test="${tourPart.extOrder != null}">
											<%-- TODO authorization for the 'a' tag --%>
											<crh-common:authorize function="<%= Authorization.PUBLIC_FUNCTION %>">
											</crh-common:authorize>
													<a href="javascript:submitPage({'showExternalOrder': true, 'extOrderId': <c:out value='${tourPart.extOrder.extOrderId}'/>})">
											<c:out value="${tourPart.extOrder.extOrderNumber}"/>
											<crh-common:authorize function="<%= Authorization.PUBLIC_FUNCTION %>">
											</crh-common:authorize>
												</a>
										</c:if>
										&nbsp;
									</td>
									<td>
										<table cellpadding="0" cellspacing="0" width="100%">
											<tr>
												<td width="100%" style="padding:0px">
												<c:if test="${order_dispo_AssignmentEditForm.hasPreLoading && tourPart.type == 0}">
													<input type="button" class="SmallButtonStyle" value="<fmt:message key="buttons.delete"/>" onclick="submitPage({'deleteAssignmentPart': '<c:out value="${status.count-1}"/>'})"/>
												</c:if>
												<c:if test="${order_dispo_AssignmentEditForm.hasReturn && tourPart.type == 3}">
													<input type="button" class="SmallButtonStyle" value="<fmt:message key="buttons.delete"/>" onclick="submitPage({'deleteAssignmentPart': '<c:out value="${status.count-1}"/>'})"/>
												</c:if>
	 											<c:if test="${order_dispo_AssignmentEditForm.currentIdxMovableUp}">  
													<input type="button" class="SmallButtonStyleUp" value="<fmt:message key="buttons.up"/>" onclick="submitPage({'moveAssignmentPart': 'up','partIdx':'<c:out value="${status.count-1}"/>'})"/>
												</c:if> 
												<c:if test="${order_dispo_AssignmentEditForm.currentIdxMovableDown}">  
													<input type="button" class="SmallButtonStyleDown" value="<fmt:message key="buttons.down"/>" onclick="submitPage({'moveAssignmentPart': 'down','partIdx':'<c:out value="${status.count-1}"/>'})"/>
												</c:if>  
												</td>
											</tr>
										</table>
									</td>
								</tr>
								</c:forEach>
								
								<c:if test="${!order_dispo_AssignmentEditForm.hasReturn}">
									<tr>
										<td>
											&nbsp;
										</td>
										<td>
											<fmt:message key="editassignment.type.return"/>
										</td>
										<td colspan=6>&nbsp;</td>
										<td>
											<input type="button" class="SmallButtonStyle" value="<fmt:message key="editassignment.button.create"/>" onclick="submitPage({'createAssignmentPart': 'return'})"/>
										</td>
									</tr>
								</c:if>
							</c:if>
						</table>
				</td>
			</tr>
			<c:if test="${order_dispo_AssignmentEditForm.selectedPartId > -1}">
			<%--  edit part --%>
			
			<%--  define which location radio is checked --%>

			<tr>
				<td style='width: 33%'>
				<table>
					<c:if test="${ order_dispo_AssignmentEditForm.selectedTourPart.type == 0 
									|| order_dispo_AssignmentEditForm.selectedTourPart.type== 3}">
						<tr>
							<td width='1' style='width: 20px'>
								<html:radio property="locationType" value="Homebase" styleId="homebasePosition" onclick="Ext.getDom('assignmentPartChanged').value = 'true'; changeActiveLocation(${order_dispo_AssignmentEditForm.externalOrder});"></html:radio>						
							</td>
							<td class='label'>
								<fmt:message key="dispo.texte.homebase"/>
							</td>
						</tr>
					</c:if>
					<tr>
						<td width='1'>
							<html:radio property="locationType" value="Sp" styleId="spPosition" onclick="Ext.getDom('assignmentPartChanged').value = 'true'; changeActiveLocation(${order_dispo_AssignmentEditForm.externalOrder});"></html:radio>						
						</td>
						<td class='label'>
							<fmt:message key="dispo.texte.shippingpt"/>
						</td>
					</tr> 
					<tr>
						<td width='1'>
							&nbsp;
						</td>
						<td class='label'>
							<html:hidden property="assignmentPartShippingpointId" styleId="shippingpointSearchHidden" />
							<input type="text" id="shippingpointSearch" style='width:200px' value='<c:out value="${order_dispo_AssignmentEditForm.selectedTourPart.shippingpoint.shippingpointName}"/>' onchange="Ext.getDom('assignmentPartChanged').value = 'true'">
						</td>
					</tr>
					<tr>
						<td width='1'>
							<html:radio property="locationType" value="Shipto" styleId="shiptoPosition" onclick="Ext.getDom('assignmentPartChanged').value = 'true'; changeActiveLocation(${order_dispo_AssignmentEditForm.externalOrder});"></html:radio>						
						</td>
						<td class='label'>
							<fmt:message key="dispo.texte.shipto" />
						</td>
					</tr>
					<tr>
						<td width='1'>
							&nbsp;
						</td>
						<td>
							<html:hidden property="assignmentPartShiptoSapNr" styleId="shiptoSearchHidden"/>
							<input type="text" id="shiptoSearch" style='width:200px'  value='<c:out value="${order_dispo_AssignmentEditForm.selectedTourPart.shipto.shiptoSapBp.name1}"/>' onchange="Ext.getDom('assignmentPartChanged').value = 'true'">
							<c:if test="${order_dispo_AssignmentEditForm.selectedTourPart.lnfShiptoFlag}">
								<br><div id="lnfFlag"><fmt:message key="dispo.texte.lnf"/></div>
							</c:if>
						</td>
					</tr>
					</table>
				</td>
				<td style='width: 33%'>
					<table style="border-spacing: 0px;">
						<tr>
							<td width='1'>
								<html:radio property="locationType" value="Coords" styleId="coordsPosition" onclick="Ext.getDom('assignmentPartChanged').value = 'true'; changeActiveLocation(${order_dispo_AssignmentEditForm.externalOrder});"></html:radio>						
							</td>
							<td colspan='2' class='label'>
								<fmt:message key="dispo.texte.coords"/>
							</td>
						</tr>
						<tr>
							<td width='1' rowspan='6'>
								&nbsp;
							</td>
							<td colspan='2' style='padding-bottom:10px;'>
								<nobr>
									<fmt:message key="dispo.texte.lon"/>:
									<html:text property="assignmentPartCoordLon" styleClass='textfield' style="margin-right: 8px;width:50px" styleId='lon' 
									onblur="setCoords('${order_dispo_AssignmentEditForm.selectedPartId}', {lon:Ext.getDom('lon').value, lat:Ext.getDom('lat').value})"/>
									<fmt:message key="dispo.texte.lat"/>:
									<html:text property="assignmentPartCoordLat" styleClass='textfield' style="width:50px" styleId='lat' 
									onblur="setCoords('${order_dispo_AssignmentEditForm.selectedPartId}', {lon:Ext.getDom('lon').value, lat:Ext.getDom('lat').value})"/>
								</nobr>
							</td>
						</tr>
						<c:if test='${ order_dispo_AssignmentEditForm.selectedTourPart.type == 1 
									|| order_dispo_AssignmentEditForm.selectedTourPart.type == 2}'>
						<tr>
							<td>
								<fmt:message key="dispo.texte.tositename"/>:
							</td>
							<td style="padding-left: 0px;">
								<html:text property="siteName" styleClass='textfield' style="width:150px" styleId='SiteName'/>
							</td>
						</tr>
						<tr>
							<td>
								<fmt:message key="dispo.texte.siteload"/>:
							</td>
							<td style="padding-left: 0px;">
								<html:text property="siteLoadUnload" styleClass='textfield' style="width:150px" styleId='SiteLoadUnload'/>								
							</td>
						</tr>
						<tr>
							<td>
								<fmt:message key="dispo.texte.contact"/>:
							</td>
							<td style="padding-left: 0px;">
								<html:text property="siteContactPersonTxt" styleClass='textfield' style="width:150px" styleId='SiteContactPersonTxt'/>								
							</td>
						</tr>
						<tr>
							<td>
								<fmt:message key="dispo.texte.street"/>:
							</td>
							<td style="padding-left: 0px;">
								<html:text property="siteStreetHouseNo" styleClass='textfield' style="width:150px" styleId='SiteStreetHouseNo'/>								
							</td>
						</tr>
						<tr>
							<td>
								<fmt:message key="dispo.texte.country"/>/<fmt:message key="dispo.texte.postalcode"/>/<fmt:message key="dispo.texte.city"/>:
							</td>
							<td style="padding-left: 0px;">
								<html:text property="siteCountry" styleClass='textfield' style="width:22px" styleId='SiteCountry'/>
								<html:text property="sitePostalCode" styleClass='textfield' style="width:50px" styleId='SitePostalCode'/>
								<html:text property="siteCity" styleClass='textfield' style="width:70px" styleId='SiteCity'/>								
							</td>
						</tr>
						</c:if>
					</table>
				</td>
				<td style='width: 33%'>
					<table style='width: 180px'>
						<c:if test="${order_dispo_AssignmentEditForm.selectedTourPart.type != 0}">					
							<tr>
								<td colspan='5' class='label'>
									<fmt:message key="dispo.texte.arrival"/>
									&nbsp;
									<c:out value="${order_dispo_AssignmentEditForm.selectedTourPart.arrivalUTCOffset}"/>
								</td>
							</tr>					
							<tr>
								<td width='1'>
									&nbsp;
								</td>
								<td width='1' class='right'>
									<fmt:message key="dispo.texte.planned"/>:
								</td>
								<td width='1'>
									<html:text styleClass='textfield' style='width:65px' styleId="plannedArrivalDate" property="assignmentPartPlannedArrivalDate" />
									<html:text styleClass='textfield' style='width:50px' styleId='plannedArrival' property="assignmentPartPlannedArrivalTime" onchange="Ext.getDom('assignmentPartChanged').value = 'true'"/>
								</td>
								<td width='1' class='right'>
									<fmt:message key="dispo.texte.is"/>:
								</td>
								<td width='1'>
									<html:text styleClass='textfield' style='width:65px' styleId="realArrivalDate" property="assignmentPartRealArrivalDate" />
									<html:text styleClass='textfield' style='width:50px' styleId='realArrival' property="assignmentPartRealArrivalTime"/>
								</td>
							</tr>	
						</c:if>
						<c:if test="${order_dispo_AssignmentEditForm.selectedTourPart.type != 3}">					
							<tr>
								<td colspan='5' class='label'>
									<fmt:message key="dispo.texte.departure"/>
									&nbsp;
									<c:out value="${order_dispo_AssignmentEditForm.selectedTourPart.departureUTCOffset}"/>
								</td>
							</tr>					
							<tr>
								<td width='1'>
									&nbsp;
								</td>
								<td width='1' class='right'>
									<fmt:message key="dispo.texte.planned"/>:
								</td>
								<td width='1'>
									<html:text styleClass='textfield' style='width:65px' styleId="plannedDepartureDate" property="assignmentPartPlannedDepartureDate" />
									<html:text styleClass='textfield' style='width:50px' styleId='plannedDeparture' property="assignmentPartPlannedDepartureTime" onchange="Ext.getDom('assignmentPartChanged').value = 'true'"/>
								</td>
								<td width='1' class='right'>
									<fmt:message key="dispo.texte.is"/>:
								</td>
								<td width='1'>
									<html:text styleClass='textfield' style='width:65px' styleId="realDepartureDate" property="assignmentPartRealDepartureDate" />
									<html:text styleClass='textfield' style='width:50px' styleId='realDeparture' property="assignmentPartRealDepartureTime"/>
								</td>
							</tr>	
						</c:if>
						<tr>
							<td colspan='5' class='label'>
								<fmt:message key="dispo.texte.distance"/>
							</td>
						</tr>					
						<tr>
							<td width='1'>
								&nbsp;
							</td>
							<td width='1' class='right'>
								<fmt:message key="dispo.texte.planned"/>:
							</td>
							<td width='1'>
								<html:text styleClass='textfield' style='width:50px' styleId='distance' property="assignmentPartDistance" onchange="Ext.getDom('assignmentPartChanged').value = 'true'"/>
							</td>
						</tr>	
						<tr>
							<td colspan="5">
								<input type="button" class="SmallButtonStyle" value="<fmt:message key="editassignment.button.calculate"/>" onclick="calculateTimes(<c:out value="${order_dispo_AssignmentEditForm.selectedPartId}"/>, true, true);"/>
							</td>
						</tr>
						<tr>
							<td colspan="5">
								<c:if test="${order_dispo_AssignmentEditForm.externalOrder && order_dispo_AssignmentEditForm.selectedTourPart.type==2 }">					
									<table style='width: 160px'>
										<tr>
											<td class='label' width='90'>
												<fmt:message key="dispo.texte.material"/>
											</td>
											<td class='label' width='35'>
												<fmt:message key="dispo.texte.quantity"/>
											</td>
											<td class='label' width='35'>
												<fmt:message key="dispo.texte.uom"/>
											</td>								
										</tr>
										<tr>
											<td>
												<html:text property="externalMaterial" style="width:80px;" maxlength="40" styleClass='textfield' />
											</td>
											<td>
												<html:text property="externalQuantity" style="width:30px;" maxlength="12" styleClass='textfield' />
											</td>
											<td>
												<html:select property="externalUom" style="width:100px;">
													<html:option value="-1">-</html:option>
													<c:forEach var="uom" varStatus="uomStatus" items="${order_dispo_AssignmentEditForm.uomList}">
														<html:option value="${uom.uomTxPK.uomBs.uomCd}"><c:out value="${uom.txt}"/></html:option>
													</c:forEach>
												</html:select>
											</td>
										</tr>						
									</table>
								</c:if>							
							</td>
						</tr>
					</table>
				</td>
			</tr>
			</c:if>
		</table>
<!--  
</html:form>
-->

<script type="text/javascript">

	var controls = [];
	var map;
	var coords = [];
	var locked = {
		<c:forEach var="tourPart" varStatus="status" items="${order_dispo_AssignmentEditForm.tourPartList}">
			<c:if test="${status.count-1 > 0}">
				,
			</c:if>
			'<c:out value="${status.count-1}"/>':false
		</c:forEach>
		};
	var loadingTime = <c:out value="${order_dispo_AssignmentEditForm.loadingTime}"/>; 
	var unloadingTime = <c:out value="${order_dispo_AssignmentEditForm.unloadingTime}"/>;
	var loadingText = '<fmt:message key="dispo.texte.shippingpointsearching"/>';
	var orderStatusCd = null;
	<c:if test="${order_dispo_AssignmentEditForm.selectedTourPart.order != null}">
		orderStatusCd = <c:out value="${order_dispo_AssignmentEditForm.selectedTourPart.order.orderStatus.orderStatusCd}"/>;
	</c:if>

	var numOfCallbacksInvoked = 0;
	var selectedPartId = <c:out value="${order_dispo_AssignmentEditForm.selectedPartId}" />;

	var numOfTourParts = 0;;
	// tourPartType[] contains the type of each tour part. It is used at distance calculation 
	var tourPartType = [];
	
	// tourPartShiptoId[] and tourPartShippingpointId contains sapId for the relevant location for each tour part
	var tourPartShiptoId = [];
	var tourPartShippingpointId = [];
	var tourPartLnfFlag = [];
	
	<c:forEach var="tourPart" varStatus="status" items="${order_dispo_AssignmentEditForm.tourPartList}">
		<c:if test="${tourPart.shipto != null}">
			tourPartShiptoId[<c:out value="${status.count-1}"/>]= '<c:out value="${tourPart.shipto.shiptoSapBp.sapId}"/>';
			tourPartLnfFlag[<c:out value="${status.count-1}"/>] = <c:out value="${tourPart.lnfShiptoFlag}"/>;
		</c:if>
		<c:if test="${tourPart.shippingpoint != null}">
			tourPartShippingpointId[<c:out value="${status.count-1}"/>]= '<c:out value="${tourPart.shippingpoint.shippingpointId}"/>';
		</c:if>
		tourPartType[<c:out value="${status.count-1}"/>]= '<c:out value="${tourPart.type}"/>';
		numOfTourParts++;
	</c:forEach>
	
	controls['carrierDispatchingNote'] = new Ext.form.TextArea({
	    maxLength: 250,
	    applyTo : 'carrierDispatchingNote'
	});	
	
	controls['carrierTruckDriverNote'] = new Ext.form.TextArea({
	    maxLength: 250,
	    applyTo : 'carrierTruckDriverNote'
	});	
	
	addMap("<c:out value="${order_dispo_AssignmentEditForm.selectedPartId}"/>");
	
	initShippingpointDropdown('<c:out value="${order_dispo_AssignmentEditForm.selectedPartId}"/>',{lat: '<c:out value="${order_dispo_AssignmentEditForm.selectedTourPart.shippingpoint.coordLat}"/>', lon:'<c:out value="${order_dispo_AssignmentEditForm.selectedTourPart.shippingpoint.coordLon}"/>'});
	initShiptoDropdown('<c:out value="${order_dispo_AssignmentEditForm.selectedPartId}"/>',{lat: '<c:out value="${order_dispo_AssignmentEditForm.selectedTourPart.shipto.coordLat}"/>', lon:'<c:out value="${order_dispo_AssignmentEditForm.selectedTourPart.shipto.coordLon}"/>'});

	if (Ext.get('SiteStreetHouseNo')) {
		Ext.get('SiteStreetHouseNo').on('blur', function(){findCoords(Ext.getDom('SiteStreetHouseNo').value + ',' + Ext.getDom('SitePostalCode').value + ' ' + Ext.getDom('SiteCity').value + ',' + Ext.getDom('SiteCountry').value,'<c:out value="${order_dispo_AssignmentEditForm.selectedPartId}"/>')});
		Ext.get('SiteCountry').on('blur', function(){findCoords(Ext.getDom('SiteStreetHouseNo').value + ',' + Ext.getDom('SitePostalCode').value + ' ' + Ext.getDom('SiteCity').value + ',' + Ext.getDom('SiteCountry').value,'<c:out value="${order_dispo_AssignmentEditForm.selectedPartId}"/>')});
		Ext.get('SitePostalCode').on('blur', function(){findCoords(Ext.getDom('SiteStreetHouseNo').value + ',' + Ext.getDom('SitePostalCode').value + ' ' + Ext.getDom('SiteCity').value + ',' + Ext.getDom('SiteCountry').value,'<c:out value="${order_dispo_AssignmentEditForm.selectedPartId}"/>')});
		Ext.get('SiteCity').on('blur', function(){findCoords(Ext.getDom('SiteStreetHouseNo').value + ',' + Ext.getDom('SitePostalCode').value + ' ' + Ext.getDom('SiteCity').value + ',' + Ext.getDom('SiteCountry').value,'<c:out value="${order_dispo_AssignmentEditForm.selectedPartId}"/>')}); 
	}


	
	if (Ext.get('plannedArrival')) {

		controls['plannedArrival'] = new Ext.form.TimeField({
		    increment: 30,
		    applyTo : 'plannedArrival',
		    disabled: <c:out value="${isTimeFieldsDisabled}"/>,
		    format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>'
		});
		
		
		controls['plannedArrival'].on('change', function(event){
			submitPage({'setSelectedPart':'<c:out value="${order_dispo_AssignmentEditForm.selectedPartId}"/>'});
		});
			
		controls['realArrival'] = new Ext.form.TimeField({
		    increment: 30,
		    applyTo : 'realArrival',
		    disabled: <c:out value="${isTimeFieldsDisabled}"/>,
		    format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>'
		});

	}
	
	if (Ext.get('plannedArrivalDate'))
	{
		controls['plannedArrivalDate'] = new Ext.form.DateField({
			format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
			altFormats: '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>',
			applyTo: 'plannedArrivalDate',
		    disabled: <c:out value="${isTimeFieldsDisabled}"/>
		});
		
		controls['plannedArrivalDate'].on('change', function(event){
			submitPage({'setSelectedPart':'<c:out value="${order_dispo_AssignmentEditForm.selectedPartId}"/>'});
		});
		
		controls['realArrivalDate'] = new Ext.form.DateField({
			format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
			altFormats: '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>',
			applyTo: 'realArrivalDate',
		    disabled: <c:out value="${isTimeFieldsDisabled}"/>
		});
		
	}
	
	if (Ext.get('plannedDeparture')){

		controls['plannedDeparture'] = new Ext.form.TimeField({
		    increment: 30,
		    applyTo : 'plannedDeparture',
		    disabled: <c:out value="${isTimeFieldsDisabled}"/>,
		    format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>'
		});
		
		
		controls['plannedDeparture'].on('change', function(event){
			submitPage({'setSelectedPart':'<c:out value="${order_dispo_AssignmentEditForm.selectedPartId}"/>'});
		});
		
					
		controls['realDeparture'] = new Ext.form.TimeField({
		    increment: 30,
		    applyTo : 'realDeparture',
		    disabled: <c:out value="${isTimeFieldsDisabled}"/>,
		    format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>'
		});
		
	}
	
	if (Ext.get('plannedDepartureDate'))
	{
		controls['plannedDepartureDate'] = new Ext.form.DateField({
			format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
			altFormats: '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>',
			applyTo: 'plannedDepartureDate',
		    disabled: <c:out value="${isTimeFieldsDisabled}"/>
		});
		
		controls['plannedDepartureDate'].on('change', function(event){
			submitPage({'setSelectedPart':'<c:out value="${order_dispo_AssignmentEditForm.selectedPartId}"/>'});
		});
		
		controls['realDepartureDate'] = new Ext.form.DateField({
			format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
			altFormats: '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>',
			applyTo: 'realDepartureDate',
		    disabled: <c:out value="${isTimeFieldsDisabled}"/>
		});
		
	}
	
	if (Ext.get('distance')){

		controls['distance'] = new Ext.form.NumberField ({
		    applyTo : 'distance',
		    disabled: <c:out value="${isTimeFieldsDisabled}"/>,
		    allowBlank: false,
			allowNegative: false,
			allowDecimals: true,
			decimalPrecision: 1,
			selectOnFocus: true,
			enableKeyEvents: true,
			style: 'textDis-align: right'
		});
		
		controls['distance'].on('change', function(event){
			submitPage({'setSelectedPart':'<c:out value="${order_dispo_AssignmentEditForm.selectedPartId}"/>'});
		});
					
	}

	// If the real time is set, all other fields are locked 
	if (( Ext.getDom('realDeparture') && Ext.getDom('realDeparture').value != null && Ext.getDom('realDeparture').value != "" ) ||
	 	( Ext.getDom('realArrival') && Ext.getDom('realArrival').value != null && Ext.getDom('realArrival').value != "" )){
		if (controls['plannedDeparture'] && Ext.getDom('realDeparture').value != null && Ext.getDom('realDeparture').value != "" ) { controls['plannedDeparture'].disable(); }
		if (controls['plannedDepartureDate'] && Ext.getDom('realDepartureDate').value != null && Ext.getDom('realDepartureDate').value != "" ) { controls['plannedDepartureDate'].disable(); }
		if (controls['plannedArrival'] && Ext.getDom('realArrival').value != null && Ext.getDom('realArrival').value != "") { controls['plannedArrival'].disable(); }
		if (controls['plannedArrivalDate'] && Ext.getDom('realArrivalDate').value != null && Ext.getDom('realArrivalDate').value != "") { controls['plannedArrivalDate'].disable(); }
		controls['shippingpointSearch'].disable();
		controls['shiptoSearch'].disable();
		if ( Ext.getDom('homebasePosition') != null ) {
			Ext.getDom('homebasePosition').disabled = true;
		}
		Ext.getDom('spPosition').disabled = true;
		Ext.getDom('shiptoPosition').disabled = true;
		Ext.getDom('coordsPosition').disabled = true;
		if (Ext.getDom('SiteName')) {
			Ext.getDom('SiteName').disabled = true;
			Ext.getDom('SiteLoadUnload').disabled = true;
			Ext.getDom('SiteContactPersonTxt').disabled = true;
			Ext.getDom('SiteStreetHouseNo').disabled = true;
			Ext.getDom('SiteCountry').disabled = true;
			Ext.getDom('SitePostalCode').disabled = true;
			Ext.getDom('SiteCity').disabled = true;
		}
		Ext.getDom('lon').disabled = true;
		Ext.getDom('lat').disabled = true;
		Ext.getDom('<c:out value="${order_dispo_AssignmentEditForm.selectedPartId}"/>' + 'fixed').value = 'true';
	}

	<c:if test="${ !order_dispo_AssignmentEditForm.externalOrder && (order_dispo_AssignmentEditForm.selectedTourPart.type == 1 || order_dispo_AssignmentEditForm.selectedTourPart.type == 2)}">
		disableLocationRadiobuttons();
	</c:if>

	setActiveLocation(<c:out value="${order_dispo_AssignmentEditForm.externalOrder}"/>);
	
	function initShippingpointDropdown(prefix, c){
	
		var shippingpointRecordType = Ext.data.Record.create([
		    {name: 'id', mapping: 'shippingpointId'},    
		    {name: 'shippingpointName', mapping: 'shippingpointName'},    
		    {name: 'coordLat', mapping: 'coordLat'},    
		    {name: 'coordLon', mapping: 'coordLon'}
		]);
		
		var shippingpointListStore = new Ext.data.Store({
	    	proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { DispoAjaxService.fetchShippingpointData(skip, count, query, callback) }, true),
	    	reader: new Ext.data.ListRangeReader({id:'id', totalProperty:'totalSize'}, shippingpointRecordType),
	    	remoteSort: true
		});
		
	    // Custom rendering Template
	    var resultTpl = new Ext.XTemplate(
	    	'<table class="classic"><tbody>',
	        '<tpl for="."><tr  class="listitem zebra{[xindex % 2]}">',
	        '<td>{shippingpointName}</td>',
	        '</tr></tpl>',
	        '</tbody></table>'
	    );
	    
	    var search = new Ext.form.ComboBox({
	     	store: shippingpointListStore,
	     	displayField:'shippingpointName',
	        typeAhead: false,
	        loadingText: loadingText,
	        maxHeight:800,
	        minChars:0,
	        pageSize:10,
	        forceSelection: true,
	        selectOnFocus : true,
	        triggerAction: 'all',
	        hideTrigger:false,
	        tpl: resultTpl,
	        applyTo: 'shippingpointSearch',
	        itemSelector: 'tr.listitem'
	    });

	    search.prefix = prefix;
	    search.coords = c;
	    search.on('select', function(cb, record, index){
        	Ext.getDom(cb.initialConfig.applyTo + 'Hidden').value = record.data.id;
        	cb.coords ={lat: record.data.coordLat, lon: record.data.coordLon};
        	Ext.getDom(prefix + 'activeCoordLon').value = record.data.coordLon; 
        	Ext.getDom(prefix + 'activeCoordLat').value = record.data.coordLat;
        	coords[cb.prefix] = cb.coords;
        	setMapMarker(prefix);
	        calcTimesCurrPart();        	
	   	});
	    
	    controls['shippingpointSearch'] = search;
	    
	};

	function initShiptoDropdown(prefix, c){
	
		var kundeRecordType = Ext.data.Record.create([
		    {name: 'kunde_id', mapping: 'soldtoNr'},    
		    {name: 'kunde_name', mapping: 'soldtoName'},    
		    {name: 'id', mapping: 'shiptoNr'},    
		    {name: 'name', mapping: 'shiptoName'},
		    {name: 'postalCode', mapping: 'postalCode'},
		    {name: 'city', mapping: 'city'},
		    {name: 'coordLat', mapping: 'coordLat'},    
		    {name: 'coordLon', mapping: 'coordLon'}	    
		]);
		
		var shipToKundeListStore = new Ext.data.Store( {
	    	proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { DispoAjaxService.fetchShipToKundeList(skip, count, query, callback) }, true),
	    	reader: new Ext.data.ListRangeReader({id:'id', totalProperty:'totalSize'}, kundeRecordType),
	    	remoteSort: true
		});
	
	    // Custom rendering Template
	    var resultTpl = new Ext.XTemplate(
	    	'<table class="classic"><thead><tr>',
	    	'<th width="100"><fmt:message key="fields.kunde.id"/></th>',
	    	'<th width="300"><fmt:message key="fields.kunde.name"/></th>',
	    	'<th width="100"><fmt:message key="fields.shipto.id"/></th>',
	    	'<th width="300"><fmt:message key="fields.shipto.name"/></th>',
	    	'<th width="100"><fmt:message key="fields.shipto.plz"/></th>',
	    	'<th width="200"><fmt:message key="fields.shipto.ort"/></th>',
	    	'</tr></thead><tbody>',
	        '<tpl for="."><tr  class="listitem zebra{[xindex % 2]}">',
	        '<td>{kunde_id}</td>',
	        '<td>{kunde_name}</td>',
	        '<td>{id}</td>',
	        '<td>{name}</td>',
	        '<td>{postalCode}</td>',
	        '<td>{city}</td>',
	        '</tr></tpl>',
	        '</tbody></table>'
	    );
	    
	    var search = new Ext.form.ComboBox({
	        store: shipToKundeListStore,
	        displayField:'name',
	        typeAhead: false,
	        loadingText: loadingText,
	        maxHeight:800,
	        minChars:0,
	        pageSize:10,
	        forceSelection: true,
	        selectOnFocus : true,
	        triggerAction: 'all',
	        hideTrigger:false,
	        tpl: resultTpl,
	        applyTo: 'shiptoSearch',
	        itemSelector: 'tr.listitem',
	        listWidth: 800
	    });
	    search.coords = c;
	    search.on('select', function(cb, record, index){
        	Ext.getDom(cb.initialConfig.applyTo + 'Hidden').value = record.data.id;
        	cb.coords ={lat: record.data.coordLat, lon: record.data.coordLon}
	       	Ext.getDom(prefix + 'activeCoordLon').value = record.data.coordLon; 
	        Ext.getDom(prefix + 'activeCoordLat').value = record.data.coordLat;
	        calcTimesCurrPart();        	
        	setMapMarker(prefix);
	        //calculateFrom();
	    });
	    controls['shiptoSearch'] = search;
	};
	
	function changeActiveLocation(isExternalOrder) {
		setActiveLocation(isExternalOrder);
		calcTimesCurrPart();
	}
	
	function calcTimesCurrPart(){
	    <c:if test="${order_dispo_AssignmentEditForm.selectedTourPart.type == 0}">
			calculateTimes(<c:out value="${order_dispo_AssignmentEditForm.selectedPartId + 1}"/>, true, false);
		</c:if>        	
        <c:if test="${order_dispo_AssignmentEditForm.selectedTourPart.type != 0}">
        	/* First item (create) */
			<c:if test="${order_dispo_AssignmentEditForm.selectedPartId == 0}">
				calculateTimes(0, false, true);
			</c:if>
			/* Last item (return) */
			<c:if test="${order_dispo_AssignmentEditForm.selectedPartId != 0}">
				calculateTimes(<c:out value="${order_dispo_AssignmentEditForm.selectedPartId - 1}"/>, false, true);
			</c:if>
		</c:if>  		
	}
	
	function setActiveLocation(isExternalOrder) {
		if (Ext.getDom('spPosition').checked <c:if test='${!order_dispo_AssignmentEditForm.selectedTourPart.isBagOrderLoadPart}'>&& !Ext.getDom('spPosition').disabled</c:if>) {
			controls['shippingpointSearch'].enable();
		}
		else {
			controls['shippingpointSearch'].disable();
		}
		if (Ext.getDom('shiptoPosition').checked && !Ext.getDom('shiptoPosition').disabled) {
			controls['shiptoSearch'].enable();
		}
		else {
			controls['shiptoSearch'].disable();
		}
		if ((Ext.getDom('coordsPosition').checked && !Ext.getDom('coordsPosition').disabled) ||
			Ext.getDom('lnfFlag')) {
			Ext.getDom('lon').disabled = false;
			Ext.getDom('lat').disabled = false;
			if (Ext.getDom('SiteName')){
				Ext.getDom('SiteName').disabled = false;
				Ext.getDom('SiteLoadUnload').disabled = false;
				Ext.getDom('SiteContactPersonTxt').disabled = false;
				Ext.getDom('SiteStreetHouseNo').disabled = false;
				Ext.getDom('SiteCountry').disabled = false;
				Ext.getDom('SitePostalCode').disabled = false;
				Ext.getDom('SiteCity').disabled = false;
			}
		}
		else {
			Ext.getDom('lon').disabled = true;
			Ext.getDom('lat').disabled = true;
			if (Ext.getDom('SiteName')) {
				Ext.getDom('SiteName').disabled = true;
				Ext.getDom('SiteLoadUnload').disabled = true;
				Ext.getDom('SiteContactPersonTxt').disabled = true;
				Ext.getDom('SiteStreetHouseNo').disabled = true;
				Ext.getDom('SiteCountry').disabled = true;
				Ext.getDom('SitePostalCode').disabled = true;
				Ext.getDom('SiteCity').disabled = true;
			}
			
		}

	};
	
	function disableLocationRadiobuttons() {
		if ( Ext.getDom('homebasePosition') != null ) {
			Ext.getDom('homebasePosition').disabled = true;
		}
		Ext.getDom('spPosition').disabled = true;
		Ext.getDom('shiptoPosition').disabled = true;
		Ext.getDom('coordsPosition').disabled = true;		
	}
	
	function calculateTimes(fixTimeSeqId, down, up) {
		
		// first have to calculate the number of expected callbacks as the last one will submit the page
		var numOfCallbacks = 0;
		var backIdx = fixTimeSeqId - 1;
		if (down) {
			while (backIdx > -1) {
				if (Ext.getDom(backIdx + 'fixed').value == 'true' ) {
					// reached a fix time 
					break;
				}
				numOfCallbacks++;
				backIdx--;
			}
		}
		var forwardIdx = fixTimeSeqId + 1;
		if (up) {
			while (forwardIdx < numOfTourParts) {
				if (Ext.getDom(forwardIdx + 'fixed').value == 'true' ) {
					// reached a fix time 
					break;
				}
				numOfCallbacks++;
				forwardIdx++;
			}
		}
		// ----------------- end of calculating callback numbers  ------------------------------------

		if (down) {
			backIdx = fixTimeSeqId - 1;
			
			// calculate duration from the selected (fix) tour part backward
			while (backIdx > -1) {
				if (Ext.getDom(backIdx + 'fixed').value == 'true' ) {
					// reached a fix time, stop duration calculation 
					break;
				}
				var fromLon = Ext.getDom(backIdx + 'activeCoordLon').value;
				var fromLat = Ext.getDom(backIdx + 'activeCoordLat').value;
				var incIdx = backIdx + 1;
				var toLon = Ext.getDom( incIdx + 'activeCoordLon').value;
				var toLat = Ext.getDom( incIdx + 'activeCoordLat').value;
				var fromShipto = null;
				if ( tourPartType[backIdx] == 2) {
					// if there are multiple 'delivery' tour parts then need to calculate shipto-shipto distance instaed of shippingpoint-shipto 
					var fromShipto = tourPartShiptoId[backIdx];
				}
				var context = {	idx: backIdx,
						shippingpoint: tourPartShippingpointId[backIdx],
						fromShipto: fromShipto, 
						shipto: tourPartShiptoId[backIdx+1],
						fixIdx: fixTimeSeqId,
						numOfCallbacks: numOfCallbacks,
						up:up,
						down:down};
				// calculate duration
				if (fromLat && fromLon && toLat && toLon) {
					updateRouteExistingMap(fromLat, fromLon, toLat, toLon, durCallBack, context);
				}  
				else {
					numOfCallbacksInvoked++;
				}
				backIdx--;
			}
		}
		
		if (up) {
			// calculate duration from the selected (fix) tour part forward
			forwardIdx = fixTimeSeqId + 1;
			while (forwardIdx < numOfTourParts) {								
				if (Ext.getDom(forwardIdx + 'fixed').value == 'true' ) {
					// reached a fix time 
					break;
				}
				var prevIdx = forwardIdx - 1;
				var fromLon = Ext.getDom(prevIdx + 'activeCoordLon').value;
				var fromLat = Ext.getDom(prevIdx + 'activeCoordLat').value;
				var toLon = Ext.getDom( forwardIdx + 'activeCoordLon').value;
				var toLat = Ext.getDom( forwardIdx + 'activeCoordLat').value;
								
				var fromShipto = null;
				if ( tourPartType[prevIdx] == 2) {
					// if there are multiple 'delivery' tour parts then need to calculate shipto-shipto distance instaed of shippingpoint-shipto
					var fromShipto = tourPartShiptoId[prevIdx];
				}
				var context = {	idx: forwardIdx,
						shippingpoint: tourPartShippingpointId[prevIdx],
						fromShipto: fromShipto, 
						shipto: tourPartShiptoId[forwardIdx],
						fixIdx: fixTimeSeqId,
						numOfCallbacks: numOfCallbacks,
						up:up,
						down:down};
				// calculate duration
				if (fromLat && fromLon && toLat && toLon) {
					updateRouteExistingMap(fromLat, fromLon, toLat, toLon, durCallBack, context);
				}  
				else {
					numOfCallbacksInvoked++;
				}
		
				forwardIdx++;
			}
		}
	}
	
	// this function is called back by the gmap's duration calculation
	function durCallBack(duration, context, distance) {

		// setting the 'timeModifier' hidden field for the specific tour part
		Ext.getDom( context.idx + 'timeModifier').value = duration;
		numOfCallbacksInvoked++;
		// context.numOfCallbacks contains the number of expected callbacks -> the last one will submit the page		
		if (numOfCallbacksInvoked == context.numOfCallbacks) {
			submitPage({'calculateTime': '', 'baseId' : context.fixIdx, 'calcUp':context.up, 'calcDown':context.down});
		}
	}
	
	<c:if test="${order_dispo_AssignmentEditForm.recalculateIdx != null}">
		calculateTimes(<c:out value="${order_dispo_AssignmentEditForm.recalculateIdx}"/>, true, true);
	</c:if>
</script>