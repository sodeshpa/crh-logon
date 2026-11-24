<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"
	import="com.crh.logon.web.utils.Authorization"
%>

<%@ include file="../includeTLDs.jsp" %>

<%@page import="com.crh.logon.constants.Constant"%>

<c:set var="SIMPLE_VEHICLE_PRIORITY_MAX"><%=Constant.SIMPLE_VEHICLE_PRIORITY_MAX%></c:set>

<c:set var="ORDER_STATUS_RELEASED"><%=Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER%></c:set>
<c:set var="ORDER_STATUS_LOADED"><%=Constant.ORDER_STATUS_LOADED%></c:set>
<c:set var="ORDER_STATUS_SHIPPING"><%=Constant.ORDER_STATUS_SHIPPING%></c:set>
<c:set var="ORDER_STATUS_DELIVERED"><%=Constant.ORDER_STATUS_DELIVERED%></c:set>

<%-- 
	If there are no pools to choose from (eg. because of data authorization),
	the screen could not be used.
	(Note: A warning message describes the situation for the user.)
 --%>
<c:if test="${!empty order_simpledispo_SimpleDispoForm.pools}">


<%-- Authorization helper variables for the page --%>
<c:set var="canAccessOrderDetail" value="false"/>
<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_R %>">
    <c:set var="canAccessOrderDetail" value="true"/>
</crh-common:authorize>

<c:set var="canDispatchWrite" value="false"/>
<crh-common:authorize function="<%= Authorization.SIMPLE_VEHICLE_DISPATCHING_W %>">
	<c:set var="canDispatchWrite" value="true"/>
</crh-common:authorize>


<script type="text/javascript">

var texts = {
		loading: '<fmt:message key="contadmin.text.loading"/>',
		materialNr: '<fmt:message key="simpledispo.texts.materialnr"/>',
		materialDesc: '<fmt:message key="simpledispo.ordertable.columns.material"/>',
		orderedQty: '<fmt:message key="simpledispo.texts.orderedqty"/>'
};

var gmapEnabled = false;
var icons = {};

Ext.onReady(function() {
	
	document.body.style.overflow = "hidden";
	
	// Ext quicktips
	Ext.QuickTips.init();
	Ext.apply(Ext.QuickTips.getQuickTip(), {
	    maxWidth: 400,
	    dismissDelay: 0
	});
	var qt = Ext.QuickTips.getQuickTip();
	
	qt.on('show', function (){
		if (!this.activeTarget) {return};
		if (!this.activeTarget.el) {return};
		var clist = Ext.query("table[class='materialTable']");
		for(var i=0;i<clist.length;i++){
			Ext.get(clist[i]).remove();
		}
		
		//material info popup -> ajax call to get data			
		if(this.activeTarget.el.className == 'custorderMaterialInfo'){
			var materialInfoEl = this.activeTarget.el;
			var qt = this;
			var deliveryorderNr = qt.activeTarget.el.getAttribute('deliveryorderNr');
			SimpleDispoAjaxService.getDetailedMaterialInfo(deliveryorderNr, function(orderItemOrderedQtyWrapperList) { 
				buildOrderMaterialInfo(qt, orderItemOrderedQtyWrapperList);
			});
		}
	});
	qt.on('hide', function () {
		var clist = Ext.query("table[class='materialTable']");
		for(var i=0;i<clist.length;i++){
			Ext.get(clist[i]).remove();
		}
	});

	var carrierStore = new Ext.data.SimpleStore({
		fields: ['id', 'name', 'checked'],
	    data : [ 
	<c:forEach var="carrier" varStatus="status" items="${order_simpledispo_SimpleDispoForm.carriers}">
		<c:set var="contains" value="false" />
		<c:forEach var="item" items="${order_simpledispo_SimpleDispoForm.selectedCarriers}">
		  <c:if test="${item eq carrier.carrierId}">
		    <c:set var="contains" value="true" />
		  </c:if>
		</c:forEach>
			[
				'<c:out value="${carrier.carrierId}"/>',
				'<c:out value="${carrier.carrierName}"/>',
				<c:out value="${contains}"/>
			]
		<c:if test="${!status.last}">,</c:if>
	</c:forEach>
	    ]
	});

	var carrierCheckCombo = new Ext.ux.CheckComboBox({
		store: carrierStore,
	    displayField: 'name',
	    checkedField: 'checked',
	    valueField: 'id',
	    typeAhead: true,
	    mode: 'local',
	    triggerAction: 'all',
	    emptyText:'none',
	    noneLabel: 'none',
	    selectOnFocus: true,
	    width: 180,
	    listWidth: 200
	});
	carrierCheckCombo.applyToMarkup('carriersField');
	
	carrierCheckCombo.initList();
	
	carrierCheckCombo.on('change', function(combo, record) {
		var checkedCarriers = combo.getCheckedValues();
		SimpleDispoAjaxService.setSelectedCarriers(checkedCarriers, function() {
			// Refresh the page after the selected carriers are successfully set 
			document.getElementById("inputAction").value=actions.SELECT_CARRIER;
			Ext.get('order_simpledispo_SimpleDispoForm').dom.submit();
		});
	});
	
	/* Resize the Scheduling and Orders section on window resize. */
	window.onresize = resizeTables;
	function resizeTables() {
		var dispotable = Ext.get('vehiclecontainer');
		var ordertable = Ext.get('orderlistcontainer');
		
		var heightFactor = 0.5;
		var tablesheight = document.body.clientHeight - dispotable.getTop() - 60;
		dispotable.setHeight(Math.round(tablesheight * heightFactor));
		ordertable.setHeight(Math.round(tablesheight * (1-heightFactor)) - 15);
	}

	/* For the draggable resize functionality between the Scheduling and Orders sections. */
	var split = new Ext.SplitBar("tableresize", "vehiclecontainer",
            Ext.SplitBar.VERTICAL);
	split.setAdapter(new Ext.SplitBar.AbsoluteLayoutAdapter("container"));
	split.minSize = 200;
	split.maxSize = 600;
	split.animate = false;
	
	GMapAPILoader.load(true);
	
	resizeTables();
});

GMapAPILoader.onAPIReady(function(){
	icons.stIcon = new google.maps.MarkerImage("/logon/admin/imagename?keyValue=livemap_shipto.png",
			  new google.maps.Size(21, 21),
			  new google.maps.Point(0,0),
			  new google.maps.Point(11,11));
	
	icons.spIcon = new google.maps.MarkerImage("/logon/admin/imagename?keyValue=livemap_ordermap_shipping_point.png",
			  new google.maps.Size(23, 24),
			  new google.maps.Point(0,0),
			  new google.maps.Point(12,12));

	icons.coordIcon = new google.maps.MarkerImage("/logon/admin/imagename?keyValue=livemap_non_geo_event.png",
			  new google.maps.Size(10, 10),
			  new google.maps.Point(0,0),
			  new google.maps.Point(5,5));

	icons.selectIcon = new google.maps.MarkerImage("/logon/admin/imagename?keyValue=livemap_non_geo_event.png",
			  new google.maps.Size(35, 35),
			  new google.maps.Point(0,0),
			  new google.maps.Point(18,18));

	icons.truckIcon = new google.maps.MarkerImage("/logon/admin/imagename?keyValue=livemap_truck_partially_loaded_inside_geofence.png",
			  new google.maps.Size(23, 23),
			  new google.maps.Point(0,0),
			  new google.maps.Point(2, 23));
	
	gmapEnabled = true;
});



</script>

	<html:form action="simpledispo/SimpleDispo.do" styleId='order_simpledispo_SimpleDispoForm'>
		<input type="hidden" name="inputAction" id="inputAction" value=""/>
		<input type="hidden" name="simpleVehicleId" id="simpleVehicleId" value=""/>
		<input type="hidden" name="assignmentId" id="assignmentId" value=""/>
		<input type="hidden" name="orderId" id="orderId" value=""/>
		<input type="hidden" name="priority" id="priority" value=""/>
		
		<table class="big_form2" style="border-spacing: 0px; padding: 0px; width:auto;">
			<tbody>
				<tr>
					<td class="label" style="vertical-align: bottom;">
						<fmt:message key="dispo.texte.pools" />
					</td>
					<td class="label" style="vertical-align: bottom; width:100%;">
						<fmt:message key="dispo.texte.carriers" />
					</td>	
				</tr>
				<tr>
					<td style="padding-top: 0px;">
						<html:select property="selectedPool" onchange="setSelectedPool()" styleClass="x-form-text, x-form-field" style="width: auto;">
							<c:forEach var="pool" varStatus="status" items="${order_simpledispo_SimpleDispoForm.pools}">
								<html:option value="${status.count - 1}"><c:out value="${pool.name}"/></html:option>
							</c:forEach>
						</html:select>
					</td>
					<td style="vertical-align:top; padding-top: 0px;">
						<input type="text" id="carriersField"/>
			 		</td>
				</tr>
			</tbody>
		</table>
	</html:form>
	
	<div class="label" style="margin-top: 8px">
		<fmt:message key="dispo.texte.disposition"/>
	</div>
	
	<div id="vehiclecontainer" class="simpledispocontainer">
		<table class="dispotable simpledispotable">
			<thead>
				<tr style="height:30px">
					<th>
						<nobr>
							<fmt:message key="simpledispo.vehicletable.columns.vehiclename"/><br/>
							<fmt:message key="simpledispo.vehicletable.columns.vehicleregno"/>
						</nobr>
					</th>
					<th>
						<nobr>
							<fmt:message key="simpledispo.vehicletable.columns.type"/><br/>
							<fmt:message key="simpledispo.vehicletable.columns.priority"/>
						</nobr>
					</th>
					<th>
						<nobr>
							<fmt:message key="simpledispo.vehicletable.columns.tareweight"/><br/>
							<fmt:message key="simpledispo.vehicletable.columns.maxgross"/>
						</nobr>
					</th>
					<th>
						<nobr>
							<fmt:message key="simpledispo.vehicletable.columns.carrier"/><br/>
							<fmt:message key="simpledispo.vehicletable.columns.status"/>
						</nobr>
					</th>
					<th>
						<nobr>
							<fmt:message key="simpledispo.vehicletable.columns.assignment.start"/><br/>
							<fmt:message key="simpledispo.vehicletable.columns.assignment.end"/>
						</nobr>
					</th>
					<th>
						<nobr>
							<fmt:message key="simpledispo.vehicletable.columns.action"/>
						</nobr>
					</th>
					<th style="width: 50%;">
						<nobr>
							<fmt:message key="simpledispo.vehicletable.columns.tour.assignment"/>
						</nobr>
					</th>
				</tr>
			</thead>
			<tbody>
			
				<c:forEach var="vehicle" items="${order_simpledispo_SimpleDispoForm.vehicles}">
					<tr>
						<td>
							<nobr>
								<c:out value="${vehicle.vehicleName}"/>
								<br/>
								<c:out value="${vehicle.vehicleRegNo}"/>
							</nobr>
						</td>
						<td>
							<nobr>
								<c:out value="${vehicle.vehicleType}"/>
								<br/>
                            	<select class="truckprio_dropdown" 
                            		onchange="changeTruckPriority('<c:out value="${vehicle.vehicleId}"/>', this.value)"
                            		<c:if test="${!canDispatchWrite}">disabled </c:if>>
                            		
									<c:forEach var="prio" items="${order_simpledispo_SimpleDispoForm.truckPriorities}">
										<option <c:if test="${prio eq vehicle.priority}">selected </c:if> value="<c:out value="${prio}"/>">
											<c:choose>
												<c:when test="${prio eq SIMPLE_VEHICLE_PRIORITY_MAX}">
													<fmt:message key="dispo.texte.trucks.truckinactive"/>
												</c:when>
												<c:otherwise>
													<c:out value="${prio}"/>
												</c:otherwise>
											</c:choose>
										</option>
									</c:forEach>
								</select>
							</nobr>
						</td>
						<td>
							<nobr>
								<c:out value="${vehicle.tareWeight}"/>&nbsp;<fmt:message key="simpledispo.texts.tons"/>
								<br/>
								<c:choose>
									<c:when test="${!empty vehicle.maxGrossWeight}">
										<c:out value="${vehicle.maxGrossWeight}"/>&nbsp;<fmt:message key="simpledispo.texts.tons"/>
									</c:when>
									<c:otherwise>-</c:otherwise>
								</c:choose>
							</nobr>
						</td>
						<td>
							<nobr>
								<c:out value="${vehicle.carrierName}"/>
								<br/>
								<c:out value="${vehicle.assignmentStatus}"/>
							</nobr>
						</td>
						<td>
							<c:choose>
								<c:when test="${!empty vehicle.assignmentId}">
									<c:out value="${vehicle.assignmentStart}"/>
									<br/>
									<c:out value="${vehicle.assignmentEnd}"/>
								</c:when>
								<c:otherwise>-</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:choose>
								<c:when test="${canDispatchWrite and !empty vehicle.assignmentId}">
									<c:choose>
										<c:when test="${vehicle.released}">
											<a href="javascript:showLoadDialog('<c:out value="${vehicle.vehicleId}"/>');">
												<fmt:message key="simpledispo.vehicletable.actions.load"/>
											</a>
										</c:when>
										<c:otherwise>
											<a href="javascript:releaseAssignment('<c:out value="${vehicle.assignmentId}"/>');">
												<fmt:message key="simpledispo.vehicletable.actions.release"/>
											</a>
										</c:otherwise>
									</c:choose>
									<span> | </span>
									<a href="javascript:showAssignmentEditDialog('<c:out value="${vehicle.assignmentId}"/>');">
										<fmt:message key="simpledispo.vehicletable.actions.edit"/>
									</a>
									
									<br/>
									
									<%-- If the vehicle has one order scheduled only, then no need for the popup, deallocate directly. --%>
									<c:set var="ordercount" value="0"/>
									<c:forEach var="part" items="${vehicle.parts}">
										<c:set var="ordercount" value="${ordercount+1}"/>
									</c:forEach>
									<c:choose>
										<c:when test="${ordercount eq 1}">
											<c:set var="deallocate">javascript:deallocateOrder('<c:out value="${vehicle.vehicleId}"/>');</c:set>
										</c:when>
										<c:otherwise>
											<c:set var="deallocate">javascript:showDeallocateDialog('<c:out value="${vehicle.vehicleId}"/>');</c:set>
										</c:otherwise>
									</c:choose>
									
									<a href="<c:out value="${deallocate}"/>">
										<fmt:message key="simpledispo.vehicletable.actions.deallocate"/>
									</a>
								</c:when>
								<c:otherwise>-</c:otherwise>
							</c:choose>
						</td>
						<td>
							<%-- 
								LOGON-6103: Allow unloading of orders only in the order defined by the assignment-parts
								"Show the unload link/icon (arrow) only for the first order to be unloaded according to the assignment. 
								So at all times maximum one unloading-icon is visible for a tour."
								 FIXME (paksyd 05.12.2012): This check is business related logic, it should be done in business level, not in jsp!
							 --%>
							<c:set var="show_unload" value="${true}"/>
							<c:forEach varStatus="status" var="part" items="${vehicle.parts}">
								<c:set var="is_released" value="${(ORDER_STATUS_RELEASED <= part.orderStatusCd) and (part.orderStatusCd < ORDER_STATUS_LOADED)}"/>
								<c:set var="is_loaded" value="${(ORDER_STATUS_LOADED <= part.orderStatusCd) and (part.orderStatusCd < ORDER_STATUS_SHIPPING)}"/>
								<c:set var="is_shipping" value="${(ORDER_STATUS_SHIPPING <= part.orderStatusCd) and (part.orderStatusCd < ORDER_STATUS_DELIVERED)}"/>
								<c:set var="is_delivered" value="${ORDER_STATUS_DELIVERED <= part.orderStatusCd}"/>	
							
								<div class="assigned_order <c:if test="${is_released}">released</c:if> <c:if test="${is_loaded}">loaded</c:if> <c:if test="${is_shipping}">shipping</c:if> <c:if test="${is_delivered}">delivered</c:if>">
									<p><c:out value="${part.orderNumber}"/></p>
									<p><c:out value="${part.shiptoName}"/></p>
									
									<c:if test="${canDispatchWrite and (is_loaded or is_shipping) and show_unload}">
										<a class="unloadlink" 
											href="javascript:showUnloadDialog('<c:out value="${part.orderId}"/>');" 
											title="<fmt:message key="simpledispo.vehicletable.actions.unload"/>">
											<img alt="<fmt:message key="simpledispo.vehicletable.actions.unload"/>" src="../images/arrow_unload.gif"/>
										</a>
										<c:set var="show_unload" value="${false}"/>
									</c:if>
								</div>
							</c:forEach>
							<div class="clearfix"></div>
						</td>
					</tr>
				</c:forEach>
				
			</tbody>
		</table>
	</div>	

	<div id="tableresize" style="position:relative;background-color:transparent;width:100%; height:3px; z-index:30; cursor: row-resize; text-align:center;font-size: 1px">
		<img style='background-color:#999; width: 300px;height:1px;'></img>
	</div>
	
	<div class="label" style="margin-top: 8px">
		<fmt:message key="dispo.texte.order"/>
	</div>
	
	<div id="orderlistcontainer" class="simpledispocontainer">
		<table class="dispotable simpledispotable">
			<thead>
				<tr style="height:30px">
					<th>
						<nobr>
							<fmt:message key="simpledispo.ordertable.columns.customerorder"/><br/>
							<fmt:message key="simpledispo.ordertable.columns.delivery"/>
						</nobr>
					</th>
					<th>
						<nobr>
							<fmt:message key="simpledispo.ordertable.columns.remark"/>
						</nobr>
					</th>
					<th>
						<nobr>
							<fmt:message key="simpledispo.ordertable.columns.deliverytype"/>
						</nobr>
					</th>
					<th>
						<nobr>
							<fmt:message key="simpledispo.ordertable.columns.material"/><br/>
							<fmt:message key="simpledispo.ordertable.columns.orderedqty"/>
						</nobr>
					</th>
					<th>
						<nobr>
							<fmt:message key="simpledispo.ordertable.columns.deliveryfromto1"/><br/>
							<fmt:message key="simpledispo.ordertable.columns.deliveryfromto2"/>
						</nobr>
					</th>
					<th>
						<nobr>
							<fmt:message key="simpledispo.ordertable.columns.soldto"/><br/>
							<fmt:message key="simpledispo.ordertable.columns.shipto"/>
						</nobr>
					</th>
					<th>
						<nobr>
							<fmt:message key="simpledispo.ordertable.columns.carrier"/>
						</nobr>
					</th>
					<th>
						<nobr>
							<fmt:message key="simpledispo.ordertable.columns.assignto"/>
						</nobr>
					</th>
				</tr>
			</thead>
			
			<tbody>
				<c:forEach items="${order_simpledispo_SimpleDispoForm.orders}" var="orderdata">
					<tr>
	    				<td>
							<nobr>
								<c:out value="${orderdata.order.custorderNumber}"/>
								<br/>
								<c:choose>
									<c:when test="${canAccessOrderDetail}">
										<a href="javascript:showOrderDetailPopup('<c:out value="${orderdata.order.orderId}"/>');">
											<c:out value="${orderdata.order.orderNumber}"/>
										</a>
									</c:when>
									<c:otherwise>
										<c:out value="${orderdata.order.orderNumber}"/>
									</c:otherwise>
								</c:choose> 
								
							</nobr>
						</td>
						<td>
							<c:choose>
								<c:when test="${!empty orderdata.order.noteDispatching}">
									<c:out value="${orderdata.order.noteDispatching}"/>
								</c:when>
								<c:otherwise>-</c:otherwise>
							</c:choose>
						</td>
						<td>
							<nobr>
								<c:out value="${orderdata.order.packageType}"/>
							</nobr>
						</td>
						<td>
							<div class = "custorderMaterialInfo"
									style = "position:relative;z-index:12;overflow:hidden;white-space:nowrap;"
									ext:qtip = " "
									ext:qclass = "dispoqtip dispoinfoqtip" 
									deliveryorderNr="<c:out value="${orderdata.order.orderNumber}"/>" >
								<c:out value="${orderdata.order.matDesc}"/>
								<br/>
								<c:out value="${orderdata.order.orderedQuantity}"/><span>&nbsp;</span>
								<c:out value="${orderdata.order.orderedUomTxt}"/>
							</div>
						</td>
						<td>
							<nobr>
								<c:out value="${orderdata.orderDeliveryFrom}"/>
								<br/>
								<c:out value="${orderdata.orderDeliveryTo}"/>
							</nobr>
						</td>
						<td>
							<nobr>
								<c:out value="${orderdata.order.soldtoName}"/>
								<br/>
								<c:out value="${orderdata.order.shiptoName}"/>
							</nobr>
						</td>
						<td>
							<nobr>
								<c:out value="${orderdata.order.carrierName}"/>
							</nobr>
						</td>
						<td>
							<select class="vehicleselect" 
									onchange="assignOrderToVehicle('<c:out value="${orderdata.order.orderId}"/>', this.value);"
									<c:if test="${!canDispatchWrite}">disabled</c:if> >
								<option value=""><fmt:message key="dummy.select"/></option>
								<c:forEach items="${orderdata.vehicles}" var="vehicle">
									<option value="<c:out value="${vehicle.simpleVehicleId}"/>">
										<c:out value="${vehicle.vehicleName}"/>
									</option>
								</c:forEach>
							</select>
						</td>	
	    			</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</c:if>
  
