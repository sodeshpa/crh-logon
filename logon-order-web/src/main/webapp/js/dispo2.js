var SELECTION_ASSIGNMENT = 1;
var SELECTION_RETURN = 2;
var SELECTION_PRE = 3;

var ERROR_NONE = 0;
var ERROR_ROUTE_CONFLICT = 1;
var ERROR_LOAD = 2;
var ERROR_TRUCK_UNAVAILABLE = 3;
var ERROR_ASSIGNMENT_NOT_TRANSMITTED = 4;
var ERROR_ASSIGNMENT_NOT_ACKED = 5;
var ERROR_OVER_MIDNIGHT = 6;
var ERROR_WAGON_NOT_ARRIVED = 7;

var ASSIGNMENT_PART_TYPE_PRE_LOAD = 0;
var ASSIGNMENT_PART_TYPE_LOAD = 1;
var ASSIGNMENT_PART_TYPE_TO = 2;
var ASSIGNMENT_PART_TYPE_UNLOAD = 3;
var ASSIGNMENT_PART_TYPE_FROM = 4;
var ASSIGNMENT_PART_TYPE_FINISHED = 5;

var ASSIGNMENT_STATUS_NEW                = 0;
var ASSIGNMENT_STATUS_PRE_LOAD           = 1;
var ASSIGNMENT_STATUS_LOADING            = 2;
var ASSIGNMENT_STATUS_SHIPPING           = 3;
var ASSIGNMENT_STATUS_UNLOADING          = 4;
var ASSIGNMENT_STATUS_RETURNING          = 5;
var ASSIGNMENT_STATUS_FINISHED           = 6;
var ASSIGNMENT_STATUS_CANCELLED          = 7;
var ASSIGNMENT_STATUS_PRESCHEDULING_DONE = 8;

var ASSIGNMENT_PART_STATUS_NEW = 0;
var ASSIGNMENT_PART_STATUS_TRANSMITTED_ACK = 2;

var ORDER_STATUS_TRANSMITTED_TO_POOL         = 2;
var ORDER_STATUS_CONFIRMED_BY_POOL           = 3;
var ORDER_STATUS_TRANSMITTED_TO_CARRIER      = 4;
var ORDER_STATUS_CONFIRMED_BY_CARRIER        = 5;
var ORDER_STATUS_SCHEDULED                   = 6;
var ORDER_STATUS_RELEASED                    = 7;
var ORDER_STATUS_CONFIRMED_BY_DRIVER         = 8;
var ORDER_STATUS_ARRIVED_AT_LOADINGPOINT     = 9;
var ORDER_STATUS_LOADING                     = 10;
var ORDER_STATUS_LOADED                      = 11;
var ORDER_STATUS_SHIPPING                    = 12;
var ORDER_STATUS_ARRIVED_AT_DELIVERY_POINT   = 13;
var ORDER_STATUS_UNLOADING                   = 14;
var ORDER_STATUS_DELIVERED                   = 15;
var ORDER_STATUS_FINISHED                    = 16;
var ORDER_STATUS_DELETED                     = 17;
var ORDER_STATUS_TRANSMITTED_TO_RAIL_COMPANY = 18;

var ASSIGNMENT_TYPE_INTERNAL = 0;
var ASSIGNMENT_TYPE_EXTERNAL = 1;

var PART_TYPES = [ 'pre', 'load', 'to', 'unload', 'from', 'finished' ];

var MINUTES_OF_HOUR = 60;
var MINUTES_OF_DAY = MINUTES_OF_HOUR * 24;

var DELIVERY_STATUS_FLAG_PLANNED = 'Pl';
var DELIVERY_STATUS_FLAG_CONFIRMED = 'Cf';
var DELIVERY_STATUS_FLAG_TRANSMITTED = 'Tr';
var DELIVERY_STATUS_FLAG_DISPATCHED_OR_BEYOND = 'As';

var DELIVERY_TYPE_FLAG_EXTERNAL = 'Ext';
var DELIVERY_TYPE_FLAG_ONTIME = 'Ot';
var DELIVERY_TYPE_FLAG_LOCKED = 'Lo';
var DELIVERY_TYPE_FLAG_PRESCHEDULED = 'Prescheduled';
var DELIVERY_TYPE_FLAG_ONHOLD = 'OnHold';
var DELIVERY_TYPE_FLAG_EXTERNAL_LOCKED = 'ExtLocked';

/** Stores the assignments for later re-drawing. */
var allAssignments = [];

var DEFAULT_TRUCK_HEIGHT = 39;
var DEFAULT_DROP_TARGET_HEIGHT = DEFAULT_TRUCK_HEIGHT - 2;//-2px border top and bottom

function nvl(values) {
	var i;
	for (i = 0; i < values.length; i++) {
		if (values[i]) {
			return values[i];
		}
	}
	return null;
}

function findParentAssignment(domEl) {
	el = domEl;
	while (el != el.parentNode) {
		if (el.assignmentObject !== undefined) {
			return el;
		}
		el = el.parentNode;
	}
	return null;
}

function trimMinutes(minutes) {
	if (minutes < 0) {
		return 0;
	}
	if (minutes >= MINUTES_OF_DAY) {
		return MINUTES_OF_DAY - 1;
	}
	return minutes;
}

function dateYMDcompare(d1, d2) {
	var d1year = d1.getUTCFullYear();
	var d2year = d2.getUTCFullYear();
	if (d1year < d2year) {
		return -1;
	} else if (d1year > d2year) {
		return 1;
	}
	var d1month = d1.getUTCMonth();
	var d2month = d2.getUTCMonth();
	if (d1month < d2month) {
		return -1;
	} else if (d1month > d2month) {
		return 1;
	}
	var d1day = d1.getUTCDate();
	var d2day = d2.getUTCDate();
	if (d1day < d2day) {
		return -1;
	} else if (d1day > d2day) {
		return 1;
	}
	return 0;
}

function dateToMinutes(date) {
	if (date === null) {
		return null;
	}
	var cmp = dateYMDcompare(currentDispoDate, date);
	if (cmp === 0) {
		return date.getUTCMinutes() + MINUTES_OF_HOUR * date.getUTCHours();
	} else if (cmp == -1) {
		return MINUTES_OF_DAY;
	} else {
		return 0;
	}
}

function addMinutes(time_minutes, minutes) {
	var res = time_minutes + minutes;
	if (res < 0) {
		return 0;
	}
	if (res > MINUTES_OF_DAY) {
		return MINUTES_OF_DAY;
	}
	return res;
}

function formatHour(hour, addminutes) {
	if (hour < 10) {
		return '0' + hour + ((addminutes) ? ':00' : '');
	}
	return hour + ((addminutes) ? ':00' : '');
}

function format2Digits(part) {
	if (part < 10) {
		return '0' + part;
	}
	return part;
}

function formatDeliveryDate(date) {
	// the ExtJs.Date.format uses local time for formatting, but we want UTC.
	// the getUTCMonth uses zero indexing (0-11) so that's why it is needed to be increased.
	var hm = format2Digits(date.getUTCHours()) + ':' + format2Digits(date.getUTCMinutes());
	var dm = '(' + format2Digits(date.getUTCDate()) + '.' + format2Digits(date.getUTCMonth()+1) + ')'; 
	return hm + ' ' + dm;
}

function getDispoDateMorning() {
	var morning = clearTime(getCurrentDispoDateAsUTC());
	morning.setUTCHours(0);
	morning.setUTCMinutes(0);
	return morning;
}

function getDispoDateMidnight() {
	var midnight = clearTime(getCurrentDispoDateAsUTC());
	midnight.setUTCHours(23);
	midnight.setUTCMinutes(59);
	return midnight;
}

function coordsEqual(lat1, lon1, lat2, lon2) {
	if (((lat1 === null) || (lon1 === null))
			&& ((lat2 === null) || (lon2 === null))) {
		return true;
	}
	if ((lat1 !== null) && (lon1 !== null) && (lat2 !== null)
			&& (lon2 !== null) && gmapEnabled) {
		var R = 6378137; // earth's mean radius in meters (this was a
		// parameter in V2)
		var rad = function(x) {
			return x * Math.PI / 180;
		};
		var dLat = rad(lat2 - lat1);
		var dLong = rad(lon2 - lon1);

		var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(rad(lat1))
				* Math.cos(rad(lat2)) * Math.sin(dLong / 2)
				* Math.sin(dLong / 2);
		var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		var d = R * c;

		return d < 1000;
	}
	return false;
}

function isPlanningTruck(truck) {
	return truck.sapVehicleCharCd == planningTruckCharacteristicCd;
}

/** Decides if the given assignment is scheduled on a planning truck. */
function isAssignmentOnPlanningTruck(assignment) {
	return assignment.sapVehicleCharCd == planningTruckCharacteristicCd;
}

function packageTypeMatchesTransportCategory(packageTypeCode, camion) {
	// Assumption: it has already been checked that if the truck has
	// transport type trailer then the trailer id is not null.
	var truckTrspCatCode = camion.truckTransportCatCd;
	var trailerTrspCatCode = camion.trailerTrspCatCd;

	if (truckTrspCatCode === TRUCK_TRANSPORT_TYPE_TRAILER) {
		return packageTypeMatchesTrailerTrspCatMap(packageTypeCode,
				trailerTrspCatCode);
	} else {
		return packageTypeMatchesTruckTrspCatMap(packageTypeCode,
				truckTrspCatCode);
	}
}

function isAssignmentOnPlanningTruck(assignment) {
	return assignment.sapVehicleCharCd == planningTruckCharacteristicCd;
}

function appendNotes(text, note) {
	note = note ? note : '';
	if (!text || (text === '')) {
		return note;
	}
	if (note === '') {
		return text;
	}
	return text + '<br>' + note;
}

/**
 * This function is used to determine if is the given truck matches for the
 * order by truck type.
 * 
 * @param truck
 *            DispoTruckListEntity inctance
 * @param typeList
 *            List<DispoTruckTypeListEntity> - list of truck types associated
 *            with the order.
 */
function findTruckTypePrio(truck, typeList) {
	for ( var i = 0; i < typeList.length; i++) {
		if (truck.truckTypeCd == typeList[i].truckTypeCd) {
			return typeList[i];
		}
	}
	return null;
}

function buildAssignmentPopup(data) {

	var description = '';
	var shippingpt = null;
	for ( var i = 0; i < data.parts.length - 1; i++) {
		if ((data.parts[i].assignmentPart.assignmentPartTypeCd == 1)
				&& (shippingpt === null)) {
			shippingpt = nvl([
					data.parts[i].assignmentPart.shiptoName,
					data.parts[i].assignmentPart.shippingpointName,
					data.parts[i].assignmentPart.siteName,
					data.parts[i].assignmentPart.coordLon ? (data.parts[i].assignmentPart.coordLon
							+ "," + data.parts[i].assignmentPart.coordLat)
							: '' ]);
		}
		if (data.parts[i].assignmentPart.assignmentPartTypeCd == 3) {
			var preschedulingInfo = '';
			if (data.isPreschedulingRequired) {
				if (data.isPreschedulingDone) {
					preschedulingInfo = ',&nbsp;' + texts.PreschedulingDone;
				} else {
					preschedulingInfo = ',&nbsp;' + texts.PreschedulingRequired;
				}
			}

			description += (data.parts[i].assignmentPart.orderNumber ? data.parts[i].assignmentPart.orderNumber
					: texts.Extern)
					+ "&nbsp;&nbsp;&nbsp;"
					+ (data.parts[i].assignmentPart.orderStatusTxt ? data.parts[i].assignmentPart.orderStatusTxt
							: '')
					+ preschedulingInfo
					+ "<br>&nbsp;&nbsp;&nbsp;";
			if (shippingpt !== null) {
				description += shippingpt;
				if (data.parts[i].distinctWagonNumbers.length > 0) {
					description += "&nbsp;&nbsp;&nbsp;" + texts.Railcar
							+ "&nbsp;:";
					var comma = '';
					for ( var j = 0; j < data.parts[i].distinctWagonNumbers.length; j++) {
						description += comma
								+ data.parts[i].distinctWagonNumbers[j];
						comma = ',&nbsp;';
					}
				}
				description += '<br>&nbsp;&nbsp;&nbsp;';
			}
			if (data.parts[i].assignmentPart.deliveryFromDat
					&& data.parts[i].assignmentPart.deliveryToDat) {
				description += texts.Time
						+ ":&nbsp;"
						+ formatDeliveryDate(data.parts[i].assignmentPart.deliveryFromDat)
						+ '&nbsp;-&nbsp;'
						+ formatDeliveryDate(data.parts[i].assignmentPart.deliveryToDat)
						+ "<br>&nbsp;&nbsp;&nbsp;";
			}
			var shiptoCity = nvl([ data.parts[i].assignmentPart.shiptoCity,
					data.parts[i].assignmentPart.siteCity ]);
			description += nvl([
					data.parts[i].assignmentPart.shiptoName,
					data.parts[i].assignmentPart.shippingpointName,
					data.parts[i].assignmentPart.siteName,
					data.parts[i].assignmentPart.coordLon ? (data.parts[i].assignmentPart.coordLon
							+ "," + data.parts[i].assignmentPart.coordLat)
							: '' ])
					+ (shiptoCity ? (', ' + shiptoCity) : '') + '<br>';
			if (data.parts[i].assignmentPart.orderId) {
				for ( var k = 0; k < data.parts[i].orderItems.length; k++) {
					description += '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'
							+ data.parts[i].orderItems[k].materialName
							+ '&nbsp;('
							+ data.parts[i].orderItems[k].orderedQuantity
							+ '&nbsp;' + data.parts[i].orderItems[k].uomName
							+ ')<br>';
					if (data.parts[i].orderItems[k].loadOrderItemId) {
						description += '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'
								+ texts.LoadOrderItem
								+ ": "
								+ data.parts[i].orderItems[k].loadOrderItemId
								+ '<br>';
					}
					separator = '<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;';
				}
				data.parts[i].assignmentPart.carrierDispatchingNote = null;
				description += buildNotesPopup(data.parts[i].assignmentPart,
						'&nbsp;&nbsp;&nbsp;');
			} else {
				description += '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'
						+ nvl([ data.assignment.materialName, '-' ])
						+ '&nbsp;( '
						+ nvl([ data.assignment.materialQuantity, '-' ]) + ' '
						+ nvl([ data.assignment.materialUom, '' ]) + ' )<br>';
			}
			description += buildFreightCostInfo(data);
		}
	}
	if (data.assignment.loadOrderId) {
		description += texts.Verladeauftrag + ': '
				+ data.assignment.loadOrderId + '<br>';
	}
	return description;
}

function buildNotesOrder(prefix, orderData) {
	var popup = {
		noteText : '',
		declineAndNoteText : ''
	};

	if (!prefix) {
		prefix = "";
	}
	var carrierNotes = "";
	var driverNotes = "";
	var declineNotes = "";
	if (orderData) {
		if (orderData.order.carrierDeclineNote) {
			declineNotes += orderData.order.carrierDeclineNote;
		}
		if (orderData.order.noteDispatching) {
			carrierNotes += orderData.order.noteDispatching;
		}
		if (orderData.order.noteTruckDriver) {
			driverNotes += orderData.order.noteTruckDriver;
		}
	}

	var pretag = 'div';
	if (Ext.isIE) {
		pretag = 'pre';
	}
	var ret = '<table class="disponotestable">';
	if ((carrierNotes !== '') || (driverNotes !== '') || (declineNotes !== '')) {
		if (declineNotes !== '') {
			ret += '<tr><td style="width:100px"><b>'
					+ prefix
					+ texts.DeclineCarrier
					+ '</b></nobr></td><td style="width:200px;max-width:200px;"><'
					+ pretag + ' class="pretext">' + declineNotes + '</'
					+ pretag + '></td></tr>';
		}
		if (carrierNotes !== '') {
			ret += '<tr><td style="width:100px"><nobr><b>'
					+ prefix
					+ texts.Dispatcher
					+ '</b></nobr></td><td style="width:200px;max-width:200px;"><'
					+ pretag + ' class="pretext">' + carrierNotes + '</'
					+ pretag + ' ></nobr></td></tr>';
		}
		if (driverNotes !== '') {
			ret += '<tr><td style="width:100px"><b>'
					+ prefix
					+ texts.Driver
					+ '</b></nobr></td><td style="width:200px;max-width:200px;"><'
					+ pretag + ' class="pretext">' + driverNotes + '</'
					+ pretag + '></td></tr>';
		}
		ret += '</table>';
	}
	if (declineNotes !== '') {
		popup.declineAndNoteText = ret;
	} else if ((carrierNotes !== '') || (driverNotes !== '')) {
		popup.noteText = ret;
	}

	return popup;
}

function buildFreightCostInfo(orderWrapper) {
	var freightCost = null;
	var freightCurrency = '';

	var commCarrierFound = !Ext.isEmpty(orderWrapper.commCarrierId)
			&& currentCarriers.findIndex('carrierId',
					orderWrapper.commCarrierId) != '-1';

	if (orderWrapper.isPreschedulingRequired && commCarrierFound) {
		freightCost = roundToDecimalAndTrimEndZeros(orderWrapper.freightCost, 2);
		freightCurrency = orderWrapper.freightCurrency;
	}

	return freightCost !== null ? (texts.Freightcost + ': ' + freightCost + ' ' + freightCurrency)
			: '';
}

function buildNotesPopup(data, prefix, orderData) {
	if (!prefix) {
		prefix = "";
	}
	var carrierNotes = "";
	var driverNotes;
	if (data.carrierDispatchingNote) {
		carrierNotes = data.carrierDispatchingNote;
	} else if (orderData) {
		if (orderData.externalOrder && orderData.order.noteTruckDriver) {
			driverNotes = orderData.order.noteTruckDriver;
		}
		if (orderData.order.noteDispatching) {
			carrierNotes = orderData.order.noteDispatching;
		}
	}

	if (!driverNotes) {
		driverNotes = data.carrierTruckDriverNote ? data.carrierTruckDriverNote
				: "";
	}
	var deliveryNotes = data.carrierDeliveryNote ? data.carrierDeliveryNote
			: "";
	var custNotes = data.custWeborderNote ? data.custWeborderNote : "";
	var pretag = 'div';
	if (Ext.isIE) {
		pretag = 'pre';
	}
	if ((carrierNotes !== '') || (driverNotes !== '') || (deliveryNotes !== '')
			|| (custNotes !== '')) {
		var ret = '<table class="disponotestable">';
		if (carrierNotes !== '') {
			ret += '<tr><td style="width:100px"><nobr><b>'
					+ prefix
					+ texts.Dispatcher
					+ '</b></nobr></td><td style="width:200px;max-width:200px;"><'
					+ pretag + ' class="pretext">' + carrierNotes + '</'
					+ pretag + ' ></nobr></td></tr>';
		}
		if (driverNotes !== '') {
			ret += '<tr><td style="width:100px"><b>'
					+ prefix
					+ texts.Driver
					+ '</b></nobr></td><td style="width:200px;max-width:200px;"><'
					+ pretag + ' class="pretext">' + driverNotes + '</'
					+ pretag + '></td></tr>';
		}
		if (deliveryNotes !== '') {
			ret += '<tr><td style="width:100px"><nobr><b>'
					+ prefix
					+ texts.DeliveryNote
					+ '</b></nobr></td><td style="width:200px;max-width:200px;"><'
					+ pretag + ' class="pretext">' + deliveryNotes + '</'
					+ pretag + '></td></tr>';
		}
		if (custNotes !== '') {
			ret += '<tr><td style="width:100px"><nobr><b>'
					+ prefix
					+ texts.CustNote
					+ '</b></nobr></td><td style="width:200px;max-width:200px;"><'
					+ pretag + ' class="pretext">' + custNotes + '</' + pretag
					+ '></td></tr>';
		}
		ret += '</table>';
		return ret;
	} else {
		return '';
	}
}

function checkIsExternalLocked(data) {
	if (data.assignment.assignmentTypeCd == 1
			&& !authUnrestrictedExtOrderDispatching) {
		return true;
	}
	return false;
}

function calculateMaxTimeUntilLoadingBar(orderWrapper) {
	var m = Math.floor((orderWrapper.order.deliveryToDat.getTime() - getRealCurrentDate()
			.getTime())
			/ (60 * 1000))
			- (orderWrapper.loadingTime + orderWrapper.order.transportDurationMin);
	var maxTimeUntilLoadingStr = '';
	var styleClass;
	if (m < 0) {
		maxTimeUntilLoadingStr += '-';
		m *= -1;
		styleClass = 'maxTimeUntilLoadingWarn';
	} else {
		styleClass = 'maxTimeUntilLoading';
	}
	var d = Math.floor(m / (60 * 24));
	var h = Math.floor((m - (d * 60 * 24)) / 60);
	if (d !== 0) {
		maxTimeUntilLoadingStr = maxTimeUntilLoadingStr + d + 'd ';
	}
	if ((d !== 0) || (h !== 0)) {
		maxTimeUntilLoadingStr = maxTimeUntilLoadingStr + h + 'h ';
		m = m - (d * 60 * 24) - (h * 60);
	}
	maxTimeUntilLoadingStr = maxTimeUntilLoadingStr + m + 'min';

	maxTimeUntilLoadingStr = '<span class="' + styleClass + '">'
			+ maxTimeUntilLoadingStr + '</span>';

	return maxTimeUntilLoadingStr;
}

function buildDistancePopup(data) {

	var transportDistance;
	var transportUom = '';
	if (data.order.transportDistanceM < 1000) {
		transportUom = 'm';
		transportDistance = data.order.transportDistanceM;
	} else {
		transportDistance = data.order.transportDistanceM / 1000;
		transportUom = 'km';
	}

	transportDistance = Math.round(transportDistance);

	var popupContent = '<table><tr><td><b>' + texts.LatestLoadingTime
			+ ':</b>&nbsp;' + calculateMaxTimeUntilLoadingBar(data)
			+ '</td></tr>' + '<tr><td><b>' + texts.Distance + ':</b>&nbsp;'
			+ transportDistance + transportUom + '&nbsp;</td></tr>'
			+ '<tr><td><b>' + texts.LoadingTime + ':</b>&nbsp;'
			+ data.loadingTime + 'min&nbsp;<b>' + texts.DrivingTime
			+ ':</b>&nbsp;' + data.order.transportDurationMin + 'min&nbsp;<b>'
			+ texts.UnloadingTime + ':</b>&nbsp;'
			+ data.order.plannedUnloadingTimeMin + 'min</td></tr></table>';

	return popupContent;
}

/**
 * Creates Schipping Point and Loading Point info HTML for the quicktip over an
 * unscheduled order.
 * 
 * Loading Point is not mandatory to fill on order creation -> it could be null.
 * In case the order is bimodal road part, we use the wagon Nr as loadingpoint
 * (See: LOGON-6299).
 */
function buildShippingPLoadingPPopup(data) {
	var resultHTML = '<table><tr><td><b>' + texts.Shippingpoint + ":</b>&nbsp;"
			+ data.order.shippingpointName + "</td></tr>";

	// External orders don't have loading points
	if (!data.externalOrder) {
		// Use the first order item to get the loading point
		var firstOrderItem = data.items[0];

		var loadingPoint = null;
		// In case the order is bimodal road part, we use the wagon Nr as
		// loadingpoint (See: LOGON-6299).
		if (data.logonOrder.deliveryTypeCd == DELIVERY_TYPE_BIMODAL_ROAD) {
			loadingPoint = data.wagonNumbers[firstOrderItem.orderItemId];
		} else if (firstOrderItem.loadingpointName !== null) {
			loadingPoint = firstOrderItem.loadingpointName;
		}

		if (loadingPoint !== null && loadingPoint !== undefined) {
			resultHTML += '<tr><td><b>' + texts.Loadingpoint + ":</b>&nbsp;"
					+ loadingPoint + "</td></tr>";
		}
	}
	resultHTML += '</table>';

	return resultHTML;
}

function buildLNFPopup(data) {
	var lnfSiteNameString = data.order.lnfSiteName;
	var lnfSiteStreetHouseNoString = data.order.lnfSiteStreetHouseNo;
	var lnfSiteCountryString = data.order.lnfSiteCountry;
	var lnfSitePostalCodeString = data.order.lnfSitePostalCode;
	var lnfSiteCityString = data.order.lnfSiteCity;

	if (lnfSiteNameString === null && lnfSiteStreetHouseNoString === null
			&& lnfSiteCountryString === null
			&& lnfSitePostalCodeString === null && lnfSiteCityString === null) {
		return "";
	}

	if (lnfSiteNameString === null) {
		lnfSiteNameString = "";
	}
	if (lnfSiteStreetHouseNoString === null) {
		lnfSiteStreetHouseNoString = "";
	}
	if (lnfSiteCountryString === null) {
		lnfSiteCountryString = "";
	}
	if (lnfSitePostalCodeString === null) {
		lnfSitePostalCodeString = "";
	}
	if (lnfSiteCityString === null) {
		lnfSiteCityString = "";
	}
	return ' <b> ' + texts.LnfAddress + ':&nbsp;' + lnfSiteNameString + '/'
			+ lnfSiteStreetHouseNoString + '/' + lnfSiteCountryString
			+ '&nbsp;' + lnfSitePostalCodeString + '&nbsp;' + lnfSiteCityString
			+ '</b>';
}

function buildItemsLine(items) {
	var res = '';
	var i;
	var comma = '';
	for (i = 0; i < items.length; i++) {
		res += comma + items[i].materialName + '&nbsp;('
				+ items[i].orderedQuantity + '&nbsp;' + items[i].uomName + ')';
		comma = ',&nbsp;';
	}

	return res;
}

/**
 * LOGON-6654: Enhanced scrolling areas in dispo screen
 * Register onMouseWheel event safely across browsers
 */
function registerOnMouseWheelCrossBrowser(extJsScrollable, eventCallback) {
	var mousewheelevt=(/Firefox/i.test(navigator.userAgent)) ? "DOMMouseScroll" : "mousewheel" //FF doesn't recognize mousewheel as of FF3.x
	if (document.attachEvent) // IE
		extJsScrollable.dom.attachEvent("on"+mousewheelevt, eventCallback);
	else if (document.addEventListener) //WC3 browsers
		extJsScrollable.dom.addEventListener(mousewheelevt, eventCallback, false);
}
/**
 * LOGON-6654: Enhanced scrolling areas in dispo screen
 * Extract scrolling parameters from mousewheel movement safely across browsers
 */
function extractScrollParametersFromMouseWheel(e) {
	var scrollAmount = 40;
	if(e.wheelDelta > 0 || e.detail < 0) {
		return { direction: 'up', amount: scrollAmount };
	}
	else if(e.wheelDelta < 0 || e.detail > 0) {
		return { direction: 'down', amount: scrollAmount };
	}
}


/**
 * Decides if the plant of the give truck is real or virtual.
 *
 * LOGON-4999 Implement Truck Home Plant related changes: Distinction
 * real/virtual plant: decide on the fact, whether plant_bs.coord_lat/coord_lon
 * are set or not. (for now, at least)
 *
 * @param truck -
 *            the DispoTruckListEntity object which has plant info in it
 */
function isPlantReal(truck) {
	return truck.truck.coordLat !== null && truck.truck.coordLon !== null;
}

var orderList = null;
var dispo = null;
var currentCarriers = new Ext.util.MixedCollection();

function CurrentTimeIndicator(parent1, parent2, hourWidth) {

	this.timeToLength = function(time) {
		return Math.round(hourWidth * time / 60);
	};

	this.draw = function() {
		var currmin = dateToMinutes(getRealCurrentDate());
		
		list = Ext.select('div[class="currTimeIndicator"]', true, parent1);
		list.remove();
		list = Ext.select('div[class="currTimeIndicator"]', true, parent2);
		list.remove();
		var height = Math.round(parent1.firstChild.offsetHeight * 3 / 4);
		domTree = {
			tag : 'div',
			cls : 'currTimeIndicator',
			style : 'left:' + (this.timeToLength(currmin) - 1)
					+ "px;height:8px;top:" + height + "px"
		};
		Ext.DomHelper.append(parent1, domTree);
		domTree = {
			tag : 'div',
			cls : 'currTimeIndicator',
			style : 'left:' + (this.timeToLength(currmin) - 1) + "px;height:"
					+ parent2.firstChild.firstChild.offsetHeight + "px;top:0px"
		};
		Ext.DomHelper.append(parent2, domTree);
	};

	this.draw();
}

function DispoGeneral(startHour, endHour, alltrucks, showCurrTime, timegrid) {

	this.init = function(alltrucks) {
		var trucks = [];
		var planningtrucks = [];

		for ( var i = 0; i < alltrucks.length; i++) {
			var truck = alltrucks[i];

			if (!isPlanningTruck(truck.truck)) {
				trucks.push(truck);
			} else {
				planningtrucks.push(truck);
			}
		}
		this.trucks = {
			'realtrucks' : trucks,
			'planningtrucks' : planningtrucks
		};
	};

	this.init(alltrucks);
	this.disporeal = new Dispo(Ext.getDom('dispocontainer'), startHour,
			startHour + hourSpan, this.trucks.realtrucks, showCurrTime,
			realDispoOverrides);
	this.dispoplanning = new Dispo(Ext.getDom('dispoplanningcontainer'),
			startHour, startHour + hourSpan, this.trucks.planningtrucks,
			showCurrTime, planningDispoOverrides);

	/** Containes the list of trucks for later re-drawing */
	this.alltrucks = alltrucks;

	this.timegrid = timegrid;
	this.draggingInProgress = false;

	this.selection = [];

	this.resetAllSelection = function() {
		for (i = 0; i < this.selection.length; i++) {
			selectedEl = Ext.get("ai" + this.selection[i]);
			if (selectedEl !== null) {
				selectedEl.dom.parentNode.assignmentObject.resetSelection(i);
			} else {
				this.removeSelection(this.selection[i]);
			}
		}
		this.selection = [];
	};

	this.setAllSelection = function() {
		for (i = 0; i < this.selection.length; i++) {
			var selectedEl = Ext.get("ai" + this.selection[i]);
			if (selectedEl !== null) {
				selectedEl.dom.parentNode.assignmentObject.setSelection(i);
			} else {
				this.removeSelection(this.selection[i]);
			}
		}
	};

	this.setSelection = function(id) {
		this.resetAllSelection();
		this.selection[0] = id;
		var ai = Ext.get("ai" + id);
		if (ai) {
			var selectedEl = Ext.get("ai" + id).dom.parentNode;
			selectedEl.assignmentObject.setSelection();
			if (this.onSelectionChange) {
				this.onSelectionChange();
			}
		}
	};
	
	this.selectAllFromTruckForRelease = function(domOfOrderRow) {		
		this.resetAllSelection();
		var list = Ext.query(
				"div[class='auftragOuter'],div[class='auftragOuter ']", domOfOrderRow);
		var j = 0;
		for (i = 0; i < list.length; i++) {
			var l = Ext.DomQuery.select("div[class^='auftragInner']", Ext
					.getDom(list[i].id));
			var parent = findParentAssignment(Ext.getDom(l[0]));
			if (parent.assignmentObject.canTransmit()
					&& !parent.assignment.isNotAuthorized) {
				this.selection[j] = parent.assignment.assignment.assignmentId;
				parent.assignmentObject.setSelection();
				j = j + 1;
				if (j == maxNrOfSelectableAssignments) {
					break;
				}
			}
		}
		if (this.onSelectionChange) {
			this.onSelectionChange();
		}
	};

	this.selectAllForTransmit = function() {
		this.resetAllSelection();
		var list = Ext.query(
				"div[class='auftragOuter'],div[class='auftragOuter ']", Ext
						.getDom(this.idPrefix + 'Table'));
		var j = 0;
		for (i = 0; i < list.length; i++) {
			var l = Ext.DomQuery.select("div[class^='auftragInner']", Ext
					.getDom(list[i].id));
			var parent = findParentAssignment(Ext.getDom(l[0]));
			if (parent.assignmentObject.canTransmit()
					&& !parent.assignment.isNotAuthorized) {
				this.selection[j] = parent.assignment.assignment.assignmentId;
				parent.assignmentObject.setSelection();
				j = j + 1;
				if (j == maxNrOfSelectableAssignments) {
					break;
				}
			}
		}
		if (this.onSelectionChange) {
			this.onSelectionChange();
		}
	};

	this.removeSelection = function(id) {
		this.selection = [];
		if (this.onSelectionChange !== undefined) {
			this.onSelectionChange();
		}
	};

	this.selectionEmpty = function() {
		return this.selection.length === 0;
	};

	this.selectionSingle = function() {
		return this.selection.length == 1;
	};

	this.traverseSelection = function(callback) {
		for (i = 0; i < this.selection.length; i++) {
			selectedEl = Ext.get("ai" + this.selection[i]).dom.parentNode.assignmentObject;
			if (!callback(selectedEl)) {
				return false;
			}
		}
		return true;

	};

	this.canTransmitSelection = function() {
		if (this.selectionEmpty()) {
			return false;
		}
		return this.traverseSelection(function(el) {
			return el.canTransmit();
		});

	};

	this.canSelectForTransmission = function() {
		if (!carrierList.singleCarrierSelected) {
			return false;
		}
		return true;
	};

	this.canCancelSelection = function() {
		if (!this.selectionSingle()) {
			return false;
		}
		return this.traverseSelection(function(el) {
			return el.canCancel();
		});
	};

	this.canSetAtShiptoSelection = function() {
		if (!this.selectionSingle()) {
			return false;
		}
		return this.traverseSelection(function(el) {
			return el.canSetAtShipto();
		});
	};

	this.canSetUnloadedSelection = function() {
		if (!this.selectionSingle()) {
			return false;
		}
		return this.traverseSelection(function(el) {
			return el.canSetUnloaded();
		});
	};

	this.canSetReturnedSelection = function() {
		if (!this.selectionSingle()) {
			return false;
		}
		return this.traverseSelection(function(el, type) {
			return el.canSetReturned(type);
		});
	};

	this.canLoadAtCarrierStock = function() {
		if (!this.selectionSingle()) {
			return false;
		}
		return this.traverseSelection(function(el, type) {
			return el.canLoadAtCarrierStock(type);
		});
	};

	this.canEnterShippingpoint = function() {
		if (!this.selectionSingle()) {
			return false;
		}
		return this.traverseSelection(function(el, type) {
			return el.canEnterShippingpoint(type);
		});
	};

	this.canRerouteSelection = function() {
		if (!this.selectionSingle()) {
			return false;
		}
		return this.traverseSelection(function(el, type) {
			return el.canRerouteSelection(type);
		});
	};
	
	this.redrawTimeIndicators = function() {
		this.disporeal.currentTimeIndicator.draw();
		this.dispoplanning.currentTimeIndicator.draw();
	};

	this.enableCamionsAfterDD = function() {
		this.disporeal.enableCamionsAfterDD();
		this.dispoplanning.enableCamionsAfterDD();
	};

	this.setAssignments = function(assignments) {
		/*
		 * This function is called on a whole screen refresh, so we override
		 * allAssignments array with the assignments array.
		 */
		allAssignments = assignments;

		var realAssignments = [];
		var planningAssignments = [];
		for (i = 0; i < assignments.length; i++) {
			if (this.disporeal.getAssignmentTruckId(assignments[i])) {
				realAssignments.push(assignments[i]);
			}
			if (this.dispoplanning.getAssignmentTruckId(assignments[i])) {
				planningAssignments.push(assignments[i]);
			}
		}
		this.disporeal.setAssignments(realAssignments);
		this.dispoplanning.setAssignments(planningAssignments);
		this.setAllSelection();
	};

	/** Updates the allAssignments array with the given newAssignments. */
	this.updateAllAssignments = function(newAssignments) {
		for ( var j = 0; j < newAssignments.length; j++) {
			var newAssignment = newAssignments[j];

			// Update existing but changed assignments
			var i = 0;
			for (; i < allAssignments.length; i++) {
				var oldAssignment = allAssignments[i];

				if (oldAssignment.assignment.assignmentId == newAssignment.assignment.assignmentId) {
					allAssignments[i] = newAssignment;
					break;
				}
			}

			// Add the new assignment
			if (i == allAssignments.length) {
				allAssignments.push(newAssignment);
			}
		}
	};

	/**
	 * Deletes all assignments form the allAssignments array which has ID-s from
	 * assignmentIdsToDelete array.
	 */
	this.deleteAssignmentFromAllAssignments = function(assignmentIdsToDelete) {
		for ( var j = 0; j < assignmentIdsToDelete.length; j++) {
			var assignmentIdToDelete = assignmentIdsToDelete[j];

			for ( var i = 0; i < allAssignments.length; i++) {
				var oldAssignment = allAssignments[i];

				if (oldAssignment.assignment.assignmentId == assignmentIdToDelete) {
					// Remove the element from the allAssignments array
					allAssignments.splice(i, 1);
					break;
				}
			}
		}
	}

	this.setAssignmentsForOneLine = function(assignments) {
		/*
		 * This function is called on a differential refresh (only refresh the
		 * affected assignments), so "assignments" array is don't contain all
		 * the assignments should be displayed on the screen.
		 */
		this.updateAllAssignments(assignments);

		var realAssignments = [];
		var planningAssignments = [];
		for (i = 0; i < assignments.length; i++) {
			if (this.disporeal.getAssignmentTruckId(assignments[i])) {
				realAssignments.push(assignments[i]);
			}
			if (this.dispoplanning.getAssignmentTruckId(assignments[i])) {
				planningAssignments.push(assignments[i]);
			}
		}
		this.disporeal.setAssignmentsForOneLine(realAssignments);
		this.dispoplanning.setAssignmentsForOneLine(planningAssignments);
		this.setAllSelection();
	};

	this.disableCamionsForDD = function(drg) {
		this.disporeal.disableCamionsForDD(drg);
		this.dispoplanning.disableCamionsForDD(drg);
	};
	
	this.removeSingleTruck = function(truckId, index) {
		this.alltrucks.splice(index, 1);
		var trcTruck = Ext.getDom('trc' + truckId);
		if(trcTruck) {
			Ext.removeNode(trcTruck);			
		}
		var trhcTruck = Ext.getDom('trhc' + truckId);
		if(trhcTruck) {
			Ext.removeNode(trhcTruck);
		}
	};

	this.getPrevTruck = function(index, planning) {
		var i;
		for (i = index-1; i >= 0; i--) {
			var prevTruck = this.alltrucks[i];									
			if(prevTruck && isPlanningTruck(prevTruck.truck) == planning) {
				return prevTruck;
			}
		}
		return null;
	};
	
	this.addSingleTruck = function(truck, index) {
		this.alltrucks.splice(index, 0, truck);
		var planning = isPlanningTruck(truck.truck);
		var prevTruck = null;
		if(index > 0) {
			prevTruck = this.getPrevTruck(index, planning);
		}				
		if(planning) {
			this.dispoplanning.createTruckElement(truck, prevTruck);			
		} else {
			this.disporeal.createTruckElement(truck, prevTruck);
		}
	};

	this.updateTrucks = function(trucks) {
		// TODO (paksyd Apr 25, 2013): Update "this.alltrucks" array here with
		// "trucks" array!

		this.disporeal.updateTrucks(trucks);
		this.dispoplanning.updateTrucks(trucks);
	};

	this.getPrevAssignment = function(camion, leftCoord) {
		camion = Ext.get(camion);
		prevleft = 0;
		prev = null;
		if (camion.hasClass("camion")) {
			clist = Ext.query("div[class='auftragOuter']", camion.dom);
			for (j = 0; j < clist.length; j++) {
				otherleft = clist[j].offsetLeft;

				if ((otherleft > prevleft) && (leftCoord > otherleft)) {
					prev = clist[j];
					prevleft = otherleft;
				}
			}
		}
		return prev;
	};

	this.getNextAssignment = function(camion, leftCoord) {
		camion = Ext.get(camion);
		nextleft = 10000;
		next = null;
		if (camion.hasClass("camion")) {
			clist = Ext.query("div[class='auftragOuter']", camion.dom);
			for (j = 0; j < clist.length; j++) {
				otherleft = clist[j].offsetLeft;

				if ((otherleft < nextleft) && (leftCoord < otherleft)) {
					next = clist[j];
					nextleft = otherleft;
				}
			}
		}
		return next;
	};

}
function Dispo(parent, startHour, endHour, trucks, showCurrTime, overrides) {

	Ext.apply(this, overrides);
	var dispoObject = Ext.getDom(parent);
	dispoObject.idPrefix = this.idPrefix;
	dispoObject.planning = this.planning;

	this.timeToLength = function(time) {
		return Math.round((this.hourWidth * time) / 60);
	};

	this.durationToLength = function(duration) {
		return Math.round((this.hourWidth * duration) / 60);
	};

	this.buildTruckDriverContent = function(truck) {
		var driverName = truck.truck.driverFirstname + ' '
				+ truck.truck.driverName;

		var truckLink = truck.truck.truckName;

		var truckError = '';
		var msgTruckError = '';
		if ((truck.truck.truckTypeCd == 5) && (truck.truck.trailerId === null)) {
			msgTruckError += errortexts.noTrailer;
		}
		if (!truck.compositionValid) {
			if (msgTruckError.length > 0) {
				msgTruckError += ' ';
			}
			msgTruckError += ' ' + errortexts.incompleteComposition;
		}
		if (msgTruckError !== '') {
			truckError = "<img src='"
					+ crhCommonUrl('../images/warning2.png')
					+ "' style='margin-left:5px' ext:qtip='" + msgTruckError
					+ "' ext:qclass='dispoqtip dispoerrorqtip'></img>&ensp;";
		}
		var driver = (truck.truck.driverName === null) ? '&nbsp'
				: ('<nobr> <span style="width:145px;overflow:hidden;display:table-cell;max-width:145px">'
						+ driverName + '</span></nobr>');
		if (authcarrier) {
			truckLink = '<a href="#" onclick=' + "'"
					+ 'showPopup("LKWPopupEntry.do?truckId='
					+ truck.truck.truckId + '","")' + "' title='" + truckLink + "'>" + truckLink
					+ '</a>';
		}
		// the sumFreightCost span is overwritten when the assignments are known
		// see SumFreightCostSpanUpdate function
        var text = '<nobr><span style="width:145px;overflow:hidden;display:table-cell;max-width:145px;">'
				+ truckError + truckLink + '</span>' + '</nobr><br>' + driver;
		return text;

    };
    var releaseAllButtonNextId = 0;
    this.buildTypeStatusContent = function(truck) {

        var mail = '';
        var pause = '';
        var pdaText = '';
        var mailColor = 'white';
        var homePlant;
        var truckTypeText = truck.truck.truckTypeTxt;
        var truckStatus = truck.truck.assignStatus;
        var releaseAll = '';
        if (truck.hasNewMessage) {
            mailColor = 'yellow';
        }
        if (truck.hasNewError) {
            mailColor = 'red';
        }
        
        //Insert releaseAllButton, overwrite right click event
        var releaseAllButtonId = "releaseAllBtn"+releaseAllButtonNextId;
        releaseAll ="<input type='button' class='SmallButtonStyle' id='"+releaseAllButtonId+"' onclick='selectBeforeReleaseBasedOnTruck(this)' oncontextmenu='javascript:selectAndReleaseBasedOnTruck(this);return false;' />";        
        releaseAllButtonNextId += 1;

        if(truckStatus){
            truckTypeText = truckTypeText + "/" + truckStatus;
        }

		if (truck.truck.driverName !== null) {
			if (authcarrier) {
				pause = '<a href="javascript:showPopup('
						+ "'"
						+ 'DriverBreakEntry.do'
						+ "'"
						+ ', null, {driverId:'
						+ truck.truck.driverId
						+ '})"><img src="'
						+ crhCommonUrl('../images/pause.png')
						+ '" style="margin-bottom:3px;margin-left:3px;"  ext:qtip="'
						+ texts.NewBreak + '...'
						+ '" ext:qclass="dispoqtip dispoinfoqtip"></a>';
			}
		}
		
		var priority = truck.truck.priority + '&nbsp';
		
		if (!truck.truck.pdaAvailableFlag) {
			pdaText = '<br>'+ priority +'<span style="color:#ADAAAA">'
					+ '<img src="../images/PDA_no.gif" style="margin-bottom:3px;margin-left:3px;" height="12" width="12" '
					+ 'ext:qtip="' + texts.NoPda
					+ '" ext:qclass="dispoqtip dispoinfoqtip"></span>';
		} else if (truck.truck.pdaSessionId) {
			var truckLink = truck.truck.truckName;
			if (authcarrier) {
				mail = '<a href="javascript:showPopup('
						+ "'"
						+ 'MessagesEditEntry.do'
						+ "'"
						+ ', null, {driverId:'
						+ truck.truck.driverId
						+ ', canSend: '
						+ ((truck.truck.pdaSessionId !== null) && truck.truck.pdaAvailableFlag)
						+ '})"><img src="../images/img_email_'
						+ mailColor
						+ '.gif" style="margin-bottom:3px;margin-left:3px;"  ext:qtip="'
						+ texts.Messages + '...'
						+ '" ext:qclass="dispoqtip dispoinfoqtip"></a>';

			}
			if (truck.truck.pdaSessionStatusCd == 2) {
				pdaText = '<br>'+ priority +'<span style="color:#448844">'
						+ '<img src="../images/PDA_ready.gif" style="margin-bottom:3px;margin-left:3px;" height="12" width="12" '
						+ 'ext:qtip="' + texts.PdaOnline
						+ '" ext:qclass="dispoqtip dispoinfoqtip"></span>';
			} else {
				pdaText = '<br>'+ priority +'<span style="color:#F3721D">'
						+ '<img src="../images/PDA_online.gif" style="margin-bottom:3px;margin-left:3px;" height="12" width="12"  '
						+ 'ext:qtip="' + texts.PdaOnline
						+ '" ext:qclass="dispoqtip dispoinfoqtip"></span>';
			}
		} else {
			pdaText = '<br>'+ priority +'<span style="color:#EE6666">'
					+ '<img src="../images/PDA_offline.gif" style="margin-bottom:3px;margin-left:3px;" height="12" width="12"  '
					+ 'ext:qtip="' + texts.PdaOffline
					+ '" ext:qclass="dispoqtip dispoinfoqtip"></span>';
		}
		return '<nobr><span style="width:100px;overflow:hidden;display:table-cell;max-width:100px;">'
				+ truckTypeText
				+ ' </span></nobr>'
				+ pdaText + releaseAll
				+ pause + mail;
	};

	this.buildLoadQtyContent = function(truck) {
		var popupcontent = '';
		var cellcontent = '';
		var i;
		var nr = 0;
		var load;
		var text = '';
		for (i = 0; i < truck.loads.length; i++) {
			load = truck.loads[i];
			text = load.materialDescription + '&nbsp;' + texts.TruckLoading;
			if (load.netQuantity) {
				text = load.materialDescription + '&nbsp;' + load.netQuantity
						+ '&nbsp;' + load.uomTxt;
			}
			if (nr > 0) {
				popupcontent += '<br>';
			}
			popupcontent += text;
			nr++;
		}
		for (i = 0; i < truck.loads.length; i++) {
			load = truck.loads[i];
			text = load.materialDescription;
			if (load.netQuantity) {
				text = load.materialDescription
						+ ((nr == 1) ? '<br>' : '&nbsp;') + load.netQuantity
						+ '&nbsp;' + load.uomTxt;
			}
			if (load.assignmentId === null) {
				cellcontent += "<nobr style='display:table-row'><span style='display:table-cell;width:106px;max-width:106px;overflow:hidden;text-align:left' ext:qclass='dispoqtip dispoinfoqtip'  ext:qtip='"
						+ popupcontent
						+ "'>"
						+ text
						+ "</span><img src='"
						+ crhCommonUrl('../images/warning2.png')
						+ "' ext:qtip='"
						+ errortexts.noAssignmentForLoad
						+ "' ext:qclass='dispoqtip dispoerrorqtip' style='display:table-cell;text-align:right;width:16px;vertical-align:middle;margin:2px;'></img></nobr>";
			} else {
				cellcontent += "<nobr style='display:block; text-align:left'><span style='width:120px;max-width:120px;text-align:left;'><a href='javascript:dispo.setSelection("
						+ load.assignmentId
						+ ")' ext:qclass='dispoqtip dispoinfoqtip'  ext:qtip='"
						+ popupcontent + "'>" + text + "</a></span></nobr>";
			}
		}

		if (cellcontent === "") {
			cellcontent = "&nbsp;";
		}
		return '<div style="width:126px;height:38px;overflow:hidden;margin-left:2px">'
				+ cellcontent + '</div>';
	};

	this.buildPlTruckContent = function(truck) {
		var mailColor = 'white';
		if (truck.hasNewMessage) {
			mailColor = 'yellow';
		}
		if (truck.hasNewError) {
			mailColor = 'red';
		}
		var driverPopups = '';
		var truckLink = truck.truck.truckName;
		if (authcarrier) {
			driverPopups = '<a href="javascript:showPopup('
					+ "'"
					+ 'DriverBreakEntry.do'
					+ "'"
					+ ', null, {driverId:'
					+ truck.truck.driverId
					+ '})"><img src="'
					+ crhCommonUrl('../images/pause.png')
					+ '" style="margin-bottom:3px;margin-left:3px;"  ext:qtip="'
					+ texts.NewBreak
					+ '...'
					+ '" ext:qclass="dispoqtip dispoinfoqtip"></a>'
					+ '<a href="javascript:showPopup('
					+ "'"
					+ 'MessagesEditEntry.do'
					+ "'"
					+ ', null, {driverId:'
					+ truck.truck.driverId
					+ ', canSend: '
					+ ((truck.truck.pdaSessionId !== null) && truck.truck.pdaAvailableFlag)
					+ '})"><img src="../images/img_email_'
					+ mailColor
					+ '.gif" style="margin-bottom:3px;margin-left:3px;"  ext:qtip="'
					+ texts.Messages + '...'
					+ '" ext:qclass="dispoqtip dispoinfoqtip"></a>';
			truckLink = '<a href="#" onclick=' + "'"
					+ 'showPopup("LKWPopupEntry.do?truckId='
					+ truck.truck.truckId + '","")' + "' title='" + truckLink + "'>" + truckLink
					+ '</a>';
		}
		var truckError = '';
		var msgTruckError = '';
		if ((truck.truck.truckTypeCd == 5) && (truck.truck.trailerId === null)) {
			msgTruckError += errortexts.noTrailer;
		}
		if (!truck.compositionValid) {
			if (msgTruckError.length > 0) {
				msgTruckError += ' ';
			}
			msgTruckError += ' ' + errortexts.incompleteComposition;
		}
		if (msgTruckError !== '') {
			truckError = "<img src='"
					+ crhCommonUrl('../images/warning2.png')
					+ "' style='margin-left:5px' ext:qtip='" + msgTruckError
					+ "' ext:qclass='dispoqtip dispoerrorqtip'></img>&ensp;";
		}
		var text = '<nobr><span style="width:146px;overflow:hidden;display:table-cell;max-width:146px;">'
				+ truckError + truckLink + '</span>' + '</nobr><br>';
		return text;

	};

	this.buildPlTypeContent = function(truck) {
		return this.buildFixwidthtext(96, truck.truck.truckTypeTxt) + '<br/>';
	};

	this.buildPlCarrierContent = function(truck) {
		var carrierDisplayName = truck.truck.carrierDisplayName;
		// the sumFreightCost span is overwritten when the assignments are known
		// see SumFreightCostSpanUpdate function
		return this.buildFixwidthtext(126, carrierDisplayName)
				+ '<br><nobr><span id="sumFreightCost' + truck.truck.truckId
				+ '" name="sumFreightCost' + truck.truck.truckId
				+ '"></span></nobr>';
	};

	this.buildFixwidthtext = function(width, text) {
		return "<nobr><span style='width:"
				+ width
				+ "px;position:relative;overflow:hidden;display:block;float:left;max-width:"
				+ width
				+ "px;' ext:qclass='dispoqtip dispoinfoqtip'  ext:qtip='"
				+ text + "'>" + text + "</span></nobr>";
	};

	this.createTd = function(children, style) {
		var td = new Ext.Element(document.createElement("td"));
		td.applyStyles(style);
		td.dom.innerHTML = children;
		return td;
	}

	this.createHourCell = function(id, style) {
		var td = new Ext.Element(document.createElement("td"));

		td.dom.className = "hour";
		td.dom.id = id;
		td.applyStyles(style);
		td.innerHTML = "&nbsp;";

		return td;
	}

	this.createRow = function(id, tableBody) {
		var tr = new Ext.Element(document.createElement("tr"));
		tr.dom.id = id;
		tr.appendTo(tableBody);

		return tr;
	}

	this.createDomTree = function(id, style) {
		var div = new Ext.Element(document.createElement("div"));
		div.dom.className = "camion";
		div.dom.id = id;
		div.applyStyles(style);

		var childDiv = new Ext.Element(document.createElement("div"));
		childDiv.addClass('ddselect');
		div.appendChild(childDiv);

		return div;
	}

	this.createDomTable = function(id, style) {
		var div = new Ext.Element(document.createElement("div"));
		div.dom.className = "dispotablehead";
		div.dom.id = id + 'Titletrucks';
		// div.dom.setAttribute("style", style);
		div.applyStyles(style);

		var table = new Ext.Element(document.createElement("table"));
		table.dom.id = id + 'trucksTable';
		table.cellpadding = 0;
		table.cellspacing = 0;
		table.applyStyles('width:500px');
		table.dom.className = "dispotable";
		div.appendChild(table);

		var tbody = new Ext.Element(document.createElement("tbody"));
		table.appendChild(tbody);

		return tbody;
	}

	this.createHourTable = function(id, style, innerStyle, tableStyle) {
		var div = new Ext.Element(document.createElement("div"));
		div.dom.className = "dispotablehead";
		div.dom.id = id + 'Div';
		div.applyStyles(style);

		var innerDiv = new Ext.Element(document.createElement("div"));
		innerDiv.applyStyles(innerStyle);

		div.appendChild(innerDiv);

		var table = new Ext.Element(document.createElement("table"));
		table.dom.id = id + 'Table';
		table.cellpadding = 0;
		table.cellspacing = 0;
		table.applyStyles(tableStyle);
		table.dom.className = "dispotable";
		innerDiv.appendChild(table);

		var tbody = new Ext.Element(document.createElement("tbody"));
		table.appendChild(tbody);

		return tbody;
	}

	this.draw = function() {
		dispoDebug("Dispo.draw (" + dispoObject.planning + ")");

		var headerHeight = dispoObject.planning ? 20 : 30;
		var headings = [];
		var hourwidth = Math.round((Ext.get(parent).getWidth() - 500 - 17)
				/ (endHour - startHour));
		this.hourWidth = hourwidth;

		Ext.dd.DragDropMgr.lock();
		var titlecaption;
		if (!dispoObject.planning) {
			titlecaption = {
				tag : 'div',
				cls : 'dispotablehead',
				style : 'width:500px',
				children : [ {
					tag : 'table',
					cls : 'dispotable',
					style : 'width:500px',
					children : [ {
						tag : 'thead',
						children : [ {
							tag : 'tr',
							style : 'height:' + headerHeight + 'px',
							children : [
									{
										tag : 'th',
										html : '<nobr>' + texts.VehicleName
												+ '<br>' + texts.Driver
												+ '</nobr>',
										style : 'width:210px',
										id : this.idPrefix + 'TitleCell'
									},
									{
										tag : 'th',
										html : '<nobr>' + texts.TruckTypeStatus + '<br>'
												+ texts.TruckPrio + '/' + texts.PDAState + '</nobr>',
										style : 'width:120px'
									},
									{
										tag : 'th',
										html : '<nobr>' + texts.Load + '<br>'
												+ texts.NetWeight + '</nobr>',
										style : 'width:190px'
									} ]
						} ]
					} ]
				} ]
			};
		} else {
			titlecaption = {
				tag : 'div',
				cls : 'dispotablehead',
				style : 'width:500px',
				children : [ {
					tag : 'table',
					cls : 'dispotable',
					style : 'width:500px',
					children : [ {
						tag : 'thead',
						children : [ {
							tag : 'tr',
							style : 'height:' + headerHeight + 'px',
							children : [
									{
										tag : 'th',
										html : '<nobr>' + texts.PlanningTrucks
												+ '</nobr>',
										style : 'width:210px',
										id : this.idPrefix + 'TitleCell'
									},
									{
										tag : 'th',
										html : '<nobr>' + texts.Type
												+ '</nobr>',
										style : 'width:100px'
									},
									{
										tag : 'th',
										html : '<nobr>' + texts.Carrier
												+ '</nobr>',
										style : 'width:190px'
									} ]
						} ]
					} ]
				} ]
			};
		}

		Ext.DomHelper.overwrite(parent, titlecaption);
		dispoDebug("Dispo.draw.overwrite (parent, " + parent.childNodes.length
				+ "," + dispoObject.planning + ")");
		var hourtextwidth = Ext.util.TextMetrics.measure(this.idPrefix
				+ 'TitleCell', '00:00').width;

		for (i = 0; i < 24; i++) {
			headings[i] = {
				tag : 'th',
				html : formatHour(i, hourtextwidth < hourwidth - 4),
				style : 'width:' + hourwidth + 'px;',
				id : "hhour" + (i)
			};
		}

		var titlehours = {
			tag : 'div',
			style : 'overflow-y:visible;z-index:12;width:'
					+ (parent.offsetWidth - 500 - 17) + 'px;left:500px;',
			cls : 'dispotablehead',
			id : this.idPrefix + 'Titlehours',
			children : [ {
				tag : 'div',
				style : 'width:' + (hourwidth * 24) + 'px',
				children : [ {
					tag : 'table',
					cls : 'dispotable',
					style : 'width:' + (hourwidth * 24) + 'px',
					children : [ {
						tag : 'thead',
						children : [ {
							tag : 'tr',
							style : 'height:' + headerHeight + 'px',
							children : [ headings ]
						} ]
					} ]
				} ]
			} ]
		};

		hoursdiv = Ext.DomHelper.append(parent, titlehours);
		dispoDebug("Dispo.draw.appendTitlehours (" + dispoObject.planning + ")");
		var rows = [];
		var hourrows = [];
		var cnt = 0;
		var i;
		var truckId;

		var domTableBodyHeight = (parent.offsetHeight - headerHeight - 17 > 0 ? (parent.offsetHeight
				- headerHeight - 17)
				: 0);
		var domTableBody = this.createDomTable(this.idPrefix, 'top:'
				+ (headerHeight + 1) + 'px; left:0px; width:500px; height:'
				+ (domTableBodyHeight) + 'px');
		var domTable = domTableBody.parent().parent();

		var hourTableBodyHeight = (parent.offsetHeight - headerHeight > 0 ? (parent.offsetHeight - headerHeight)
				: 0);
		var hourTableBodyWidth = (parent.offsetWidth - 500 > 0 ? (parent.offsetWidth - 500)
				: 0);

		var hourTableBody = this.createHourTable(this.idPrefix, 'top:'
				+ (headerHeight + 1)
				+ 'px; left:500px; overflow:scroll; width:'
				+ (hourTableBodyWidth) + 'px; height:' + (hourTableBodyHeight)
				+ 'px', 'width:' + (hourwidth * 24) + 'px', 'width:'
				+ (hourwidth * 24) + 'px');
		var hourTable = hourTableBody.parent().parent().parent();

		for (i = 0; i < trucks.length; i++) {
			var cellcnt = 0;
			truckId = trucks[i].truck.truckId;

			var truckRow = this.createRow('trhc' + truckId, domTableBody);

			
			this.buildNewTruckRow(trucks[i], truckRow, dispoObject.planning);
			
			var hourcells = [];
			hourrows[cnt] = this.createRow('trc' + truckId, hourTableBody);
			for ( var k = 0; k < 24; k++) {
				hourcells[k] = this.createHourCell('cc' + truckId + '_' + k,
						'width:' + hourwidth + 'px;');
				hourrows[cnt].appendChild(hourcells[k]);
			}

			var hourcell = hourcells[0];
			// this div acts as a container for objects taht are placed on the
			// camion's timeline
			// it is of course a drag&drop target
			var width = Math.floor(hourwidth * 24) - 4;

			domTree = this.createDomTree('cd' + truckId, "width:" + width
					+ "px");
			var el = hourcell.insertFirst(domTree);

			el.dom.camion = trucks[i].truck;
			el.dom.isBag = trucks[i].isBag;
			el.dom.isNotAuthorized = trucks[i].isNotAuthorized;
			el.dom.sumFreightCostSpan = new SumFreightCostSpan(truckId);
			el.dom.row = el.dom.parentNode.parentNode;
			el.dom.hrow = truckRow.dom;// Ext.getDom('trhc' + truckId);
			el.dom.loads = trucks[i].loads;
			el.dom.dd = el.initDDTarget("truck" + truckId);

			// calculate th width of one hour from the left offset of the first
			// and the last cell in the row
			// there is no better way of dealing with rounding and differences
			// between browser rendering engines.
			// note: this is a floating point value
			el.dom.hourWidth = hourwidth;
			el.dom.hourCell = hourcell;

			el.dom.unavailabilities = trucks[i].truckUnavailabilities;
			el.dom.breaks = trucks[i].driverBreaks;
			el.dom.mopBreaks = trucks[i].mopBreaks;
			el.dom.jams = trucks[i].trafficJams;

			cnt++;
		}
		dispoDebug("Dispo.draw.createTruckList (" + dispoObject.planning + ")");
		// LOGON-6654 save truckTable for later adding of scrolling
		var truckTable = domTable.appendTo(parent);

		dispoDebug("Dispo.draw.createDomTable (" + dispoObject.planning + ")");
		var dispodiv = hourTable.appendTo(parent);
		
		// LOGON-6654 onmousewheel callback for truckTable 
		function routeScrollToDispoDiv(e) {
			var params = extractScrollParametersFromMouseWheel(e);
			if(params) {
				dispodiv.scroll(params.direction, params.amount, false);
			}
		}
		// LOGON-6654 safely register onmousewheel for truckTable
		registerOnMouseWheelCrossBrowser(truckTable, routeScrollToDispoDiv);
		
		dispoDebug("Dispo.draw.createHourTable (" + dispoObject.planning + ")");

		sumFreightCostSpanUpdate.trucksLoaded = true;

		dispodiv.dom.onscroll = function(a, b) {
			// We do not set scrollLeft property if the element is not displayed
			// yet.
			var dispoTitlehours = Ext.getDom('dispoTitlehours');
			if (dispoTitlehours !== null) {
				dispoTitlehours.scrollLeft = this.scrollLeft;
			}
			var dispoplanningTitlehours = Ext.getDom('dispoplanningTitlehours');
			if (dispoplanningTitlehours !== null) {
				dispoplanningTitlehours.scrollLeft = this.scrollLeft;
			}
			Ext.getDom(Ext.getDom(parent).idPrefix + 'Titletrucks').scrollTop = this.scrollTop;

			var orderDiv = Ext.getDom('orderDiv');
			if (orderDiv) {
				orderDiv.scrollLeft = this.scrollLeft;
			}
			if (dispo) {
				dispo.dispoplanning.oldScrollLeft = this.scrollLeft;
				// If this is invoked when the browser gets resized,
				// this.parentNode will be null in IE.
				if (this.parentNode !== null && this.parentNode.planning) {
					dispo.dispoplanning.oldScrollTop = this.scrollTop;
				} else {
					dispo.disporeal.oldScrollTop = this.scrollTop;
				}
				if (dispo.dispoplanning.draggingInProgress) {
					dispo.dispoplanning.draggingInProgress.roundXTicks();
				}
				dispo.disporeal.oldScrollLeft = this.scrollLeft;
				if (dispo.disporeal.draggingInProgress) {
					dispo.disporeal.draggingInProgress.roundXTicks();
				}
			}
		};

		dispoDebug("Dispo.draw.onscroll (" + dispoObject.planning + ")");
		if (trucks.length === 0) {
			domTree = {
				tag : 'div',
				style : 'width:'
						+ Ext.getDom(this.idPrefix + 'Titlehours').scrollWidth
						+ 'px; height:50px'
			};
			Ext.DomHelper.append(dispodiv, domTree);
			dispoDebug("Dispo.draw.domTree appended (" + dispoObject.planning
					+ ")");
		}

		dispoDebug("Dispo.draw.domTree append (" + dispoObject.planning + ")");
		if (dispo) {
			if (this.planning) {
				this.oldScrollLeft = dispo.dispoplanning.oldScrollLeft
						/ dispo.dispoplanning.hourWidth * hourwidth;
				this.oldScrollTop = dispo.dispoplanning.oldScrollTop;
			} else {
				this.oldScrollLeft = dispo.disporeal.oldScrollLeft
						/ dispo.disporeal.hourWidth * hourwidth;
				this.oldScrollTop = dispo.disporeal.oldScrollTop;
			}
		} else {
			this.oldScrollLeft = startHour * hourwidth;
			this.oldScrollTop = 0;
		}
		dispoDebug("Dispo.draw.dispo (" + dispoObject.planning + ")");

		hoursdiv.scrollLeft = this.oldScrollLeft;
		domTable.scrollTop = this.oldScrollTop;

		dispodiv.scrollLeft = this.oldScrollLeft;
		dispodiv.scrollTop = this.oldScrollTop;
		dispoDebug("Dispo.draw.changeScroll (" + dispoObject.planning + ")");
		Ext.dd.ScrollManager.unregister(this.idPrefix + 'Div');
		dispodiv.parent().parent().parent().ddScrollConfig = {
			vthresh : 160,
			hthresh : 200,
			frequency : 200,
			increment : 15,
			animate : false
		};

		Ext.dd.ScrollManager.register(dispodiv);
		dispoDebug("Dispo.draw.scrollManager (" + dispoObject.planning + ")");

		this.currentTimeIndicator = null;
		if (showCurrTime) {
			this.currentTimeIndicator = new CurrentTimeIndicator(hoursdiv,
					dispodiv.dom, hourwidth);
		}

		dispoDebug("Dispo.draw.finished (" + dispoObject.planning + ")");

	};

	this.disableCamionsForDD = function(drg) {
		// drag and drop on a truck
		// + either dragged between trucks (before move assignment)
		// + or dragged horizontally on the same truck (change delivery time)
		// + or moved from below from order list to above (before create
		// assignment)
		var grp;
		/**
		 * Indicates, that at least one of the orders from the dragged
		 * assignment has higher status than dispatched. The above dispatched
		 * statues considered here are: - ORDER_STATUS_TRANSMITTED_TO_DRIVER, -
		 * ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK, -
		 * ORDER_STATUS_ARRIVED_AT_LOADINGPOINT.
		 */
		var aboveDispatched = false;
		var i;
		/**
		 * Boolean variable to indicate if the dragged assignment is based on
		 * external order.
		 */
		var isAssignmentExternal = Ext.isEmpty(drg.order)
				&& Ext.isEmpty(drg.custorderNumber);
		if (drg.order) {
			grp = "order" + drg.id;
		} else {
			grp = "assign" + drg.assignment.assignmentId;
			for (i = 0; i < drg.parts.length; i++) {
				var orderStatusCd = drg.parts[i].assignmentPart.orderStatusCd;
				if (orderStatusCd === ORDER_STATUS_RELEASED 
						|| orderStatusCd === ORDER_STATUS_CONFIRMED_BY_DRIVER
						|| orderStatusCd === ORDER_STATUS_ARRIVED_AT_LOADINGPOINT) {
					aboveDispatched = true;
				}
			}
		}

		var list = Ext.query(".camion", Ext.getDom(this.idPrefix + 'Table'));
		var cnt = 0;
		for (i = 0; i < list.length; i++) {
			var truck = list[i];
			truck.dd.removeFromGroup(grp);

			// marius/2012-10-15: Rule numbering as in
			// /share/doc/crh/logon_2_1/spec/biz/block4/SDS_Logon_block4_LRQ_185.0_and_185.1_Vehicle_Transport_Concept_v0_9_20121010.pdf

			// Rule 1: if the truck is not authorized for the user, it should
			// not be able to dispatch on it
			if (truck.isNotAuthorized) {
				Ext.get(truck.row).addClass("disabled");
				dispoDebug('truck.camion.truckId: ' + truck.camion.truckId
						+ ', not authorized.');
				continue;
			}

			var truckCarrier = currentCarriers.itemAt(currentCarriers
					.findIndex('carrierId', truck.camion.carrierId));

			/*
			 * LRQ 112.6: Auto Changing Carriers Should be possible to change
			 * the comm. carrier of the order by allocating the assignment from
			 * planning truck to another planning truck (from another pool).
			 */
			var canAutoChangeCarrier = authpool && !Ext.isEmpty(drg.assignment)
					&& !aboveDispatched
					&& isAssignmentOnPlanningTruck(drg.assignment)
					&& isPlanningTruck(truck.camion)
					&& !Ext.isEmpty(drg.commCarrierId)
					&& truckCarrier.vnIsCommCarrier;

			dispoDebug('truck.camion.truckId: ' + truck.camion.truckId
					+ ', canAutoChangeCarrier: ' + canAutoChangeCarrier);

			// Rule 2: if it has a planning truck id set, should not be possible
			// to dispatch on planning truck anymore
			if (!canAutoChangeCarrier && !Ext.isEmpty(drg.plannedTruckId)
					&& isPlanningTruck(truck.camion)) {
				dispoDebug('truck.camion.truckId: ' + truck.camion.truckId
						+ ', Rule 2: has a planning truck id set.');
				Ext.get(list[i].row).addClass("disabled");
				continue;
			}

			// Rule 3: no commercial or executing carrier is set, only possible
			// to dispatch on trucks belonging to commercial carriers
			if (!isAssignmentExternal && Ext.isEmpty(drg.commCarrierId)
					&& Ext.isEmpty(drg.execCarrierId)) {
				if (!truckCarrier.vnIsCommCarrier) {
					dispoDebug('truck.camion.truckId: '
							+ truck.camion.truckId
							+ ', Rule 3: no commercial or executing carrier is set.');
					Ext.get(truck.row).addClass("disabled");
					continue;
				}
			}

			// Rule 4/5: only commercial carrier is set
			else if (!canAutoChangeCarrier && !Ext.isEmpty(drg.commCarrierId)
					&& Ext.isEmpty(drg.execCarrierId)) {
				if (Ext.isEmpty(drg.plannedTruckId)) {
					// Rule 4:
					// there is no planning yet crh planner has to plan first
					// on planning trucks of the commercial carrier
					// only those trucks are allowed for which the carrier
					// equals with the commercial carrier id,
					// the only case when it can differ is if the truck is lent
					// to another carrier (dispatching carrier is set for the
					// truck and corresponds with the order's carrier)

					if (truck.camion.carrierId !== drg.commCarrierId) {
						if (!(aboveDispatched === false
								&& !Ext
										.isEmpty(truck.camion.dispatchingCarrierId)
								&& !drg.externalOrder && (truck.camion.dispatchingCarrierId === drg.commCarrierId))) {
							dispoDebug('truck.camion.truckId: '
									+ truck.camion.truckId
									+ ', Rule 4: there is no planning yet.');
							Ext.get(truck.row).addClass("disabled");
							continue;
						}

					}
				} else {

					// Rule 5: the planning has already been done, scheduler
					// will dispatch on any of the partners of the commercia
					// carrier, dispatching carrier functionality is not
					// applicable
					// anymore as the commercial carrier has already planned for
					// a commercial company, it will apply for the executing
					// carrier but for that the executing carrier must be
					// pre-set
					var truckCarrierIsPartner = false;

					var commcarrier = currentCarriers.itemAt(currentCarriers
							.findIndex('carrierId', drg.commCarrierId));
					var execCarrierPartners = new Ext.util.MixedCollection();
					execCarrierPartners.addAll(commcarrier.execCarrierPartners);

					if (execCarrierPartners.findIndex('execCarrierId',
							truck.camion.carrierId) != '-1') {
						truckCarrierIsPartner = true;
					}

					if (!truckCarrierIsPartner) {
						dispoDebug('truck.camion.truckId: '
								+ truck.camion.truckId
								+ ', Rule 5: the planning has already been done.');
						Ext.get(truck.row).addClass("disabled");
						continue;
					}
				}
			} else if (!canAutoChangeCarrier && !Ext.isEmpty(drg.commCarrierId)
					&& !Ext.isEmpty(drg.execCarrierId)) {
				// if executing carrier attribute is set, only those trucks are
				// allowed for which the carrier equals with the executing
				// carrier id,
				// the only case when it can differ is if the truck is lent to
				// another carrier (dispatching carrier is set for the truck and
				// corresponds with the order's executing carrier)

				if (truck.camion.carrierId !== drg.execCarrierId) {
					if (!(aboveDispatched === false
							&& !Ext.isEmpty(truck.camion.dispatchingCarrierId)
							&& !drg.externalOrder && (truck.camion.dispatchingCarrierId === drg.execCarrierId))) {
						dispoDebug('truck.camion.truckId: '
								+ truck.camion.truckId
								+ ', Executing carrier attribute is set.');
						Ext.get(truck.row).addClass("disabled");
						continue;
					}
				}
			}
			/*
			 * Transport category: 1 - Silo, 2 - Container, 3 - Trailer, 4 -
			 * Mixed Goods.
			 */
			// Rule 7: If the truck transport category is trailer, then it must
			// have a trailer attached.
			var trspCat = truck.camion.truckTransportCatCd;
			if (trspCat === TRUCK_TRANSPORT_TYPE_TRAILER) {
				if (truck.camion.trailerId === null) {
					dispoDebug('truck.camion.truckId: ' + truck.camion.truckId
							+ ', Rule 7: Transport category - trailer.');
					Ext.get(truck.row).addClass("disabled");
					continue;
				} else {
					trspCat = truck.camion.trailerTrspCatCd;
				}
			}

			// Rule 8: For container orders the truck/trailer transport category
			// must be 2 (Container).
			if (drg.isContainerOnly && (trspCat !== 2)) {
				dispoDebug('truck.camion.truckId: ' + truck.camion.truckId
						+ ', Rule 8: Container orders.');
				Ext.get(truck.row).addClass("disabled");
				continue;
			}
			// Rule 9 (LOGON-4692): do not check the transport category for
			// External Order nor for assignment based on external order
			if (!drg.externalOrder && !isAssignmentExternal) {
				if (!truck.isBag && (drg.orderCnt > 1)
						&& (truck.camion.nrChambers < drg.orderCnt)) {
					dispoDebug('truck.camion.truckId: '
							+ truck.camion.truckId
							+ ', Truck is not bag and truck chamber count does not match.');
					Ext.get(truck.row).addClass("disabled");
					continue;
				}
				/*
				 * LOGON-4983 Scheduling: Additional Validation Logic based on
				 * order truck type restriction Disable those trucks which truck
				 * type is not "compatible" with the dragged order.
				 * 
				 * LOGON-5992 Scheduling screen - drag&drop an order doesn't
				 * respect restrictions This check applied for unallocated
				 * orders and assignments as well, because not yet released
				 * assignment can be dragged to another truck.
				 */
				if (!isPlanningTruck(truck.camion)
						&& drg.truckTypeList !== undefined
						&& drg.truckTypeList !== null) {
					var truckPrio = findTruckTypePrio(truck.camion,
							drg.truckTypeList);
					if (truckPrio === null) {
						dispoDebug('truck.camion.truckId: '
								+ truck.camion.truckId
								+ ', truck type is restricted.');
						Ext.get(truck.row).addClass("disabled");
						continue;
					}
				}

				/*
				 * LOGON-5043/LOGON-5558: Package type code must match transport
				 * code
				 * 
				 * LOGON-5992 Scheduling screen - drag&drop an order doesn't
				 * respect restrictions This check applied for unallocated
				 * orders and assignments as well, because not yet released
				 * assignment can be dragged to another truck.
				 */
				if (drg.hasOwnProperty("logonPackageTypeCd")) {
					if (!packageTypeMatchesTransportCategory(
							drg.logonPackageTypeCd, truck.camion)) {
						dispoDebug('truck.camion.truckId: '
								+ truck.camion.truckId
								+ ', Package type code must match transport code.');
						Ext.get(truck.row).addClass("disabled");
						continue;
					}
				}
			}

			truck.dd.addToGroup(grp);
			cnt++;
		}
		var groups = {};
		groups[grp] = true;
		Ext.dd.DragDropMgr.refreshCache(groups);
	};

	this.enableCamionsAfterDD = function() {
		list = Ext.query(".camion", Ext.getDom(this.idPrefix + 'Table'));
		for (i = 0; i < list.length; i++) {
			Ext.get(list[i].row).removeClass("disabled");
		}
	};

	this.resetScrollToOld = function() {
		Ext.getDom(this.idPrefix + 'Titlehours').scrollLeft = this.oldScrollLeft;
		Ext.getDom(this.idPrefix + 'Titletrucks').scrollTop = this.oldScrollTop;
		dispodiv = Ext.getDom(this.idPrefix + 'Div');
		dispodiv.scrollLeft = this.oldScrollLeft;
		dispodiv.scrollTop = this.oldScrollTop;
	};

	this.setAssignments = function(assignments) {

		Ext.dd.DragDropMgr.unlock();

		var clist = Ext.query(".camion", Ext.getDom(this.idPrefix + 'Table'));
		for (i = 0; i < clist.length; i++) {
			clist[i].style.height = DEFAULT_DROP_TARGET_HEIGHT;
			clist[i].row.style.height = DEFAULT_TRUCK_HEIGHT;
			clist[i].hrow.style.height = DEFAULT_TRUCK_HEIGHT;
			clist[i].assignmentList = [];
			Ext.DomHelper.overwrite(clist[i], {
				tag : 'div',
				cls : 'ddselect'
			});
		}

		for (i = 0; i < assignments.length; i++) {
			this.drawAssignment(assignments[i]);
		}

		if (this.planning) {
			sumFreightCostSpanUpdate.assignmentsLoaded = true;
			sumFreightCostSpanUpdate.refreshSumFreightCosts();
		}

		this.drawBreaksJamsUnavailabilities();
		this.resetScrollToOld();
		if (this.currentTimeIndicator !== null) {
			this.currentTimeIndicator.draw();
		}

	};

	this.setAssignmentsForOneLine = function(assignments) {

		Ext.dd.DragDropMgr.unlock();
		
		var i;
		for (i = 0; i < assignments.length; i++) {
			var assignment = assignments[i];
			var camion = Ext.get("cd" + this.getAssignmentTruckId(assignment));
			if (camion !== null) {
				this.resetCamion(camion);
			}
		}

		for (i = 0; i < assignments.length; i++) {
			this.drawAssignment(assignments[i]);
		}

		if (this.planning) {
			sumFreightCostSpanUpdate.assignmentsLoaded = true;
			sumFreightCostSpanUpdate.refreshSumFreightCosts();
		}

		this.drawBreaksJamsUnavailabilities();
		this.resetScrollToOld();
		this.enableCamionsAfterDD();
		if (this.currentTimeIndicator !== null) {
			this.currentTimeIndicator.draw();
		}

	};

	this.drawBreaksJamsUnavailabilities = function() {
		var j;
		var clist = Ext.query(".camion", Ext.getDom(this.idPrefix + 'Table'));
		for ( var i = 0; i < clist.length; i++) {
			var un = clist[i].unavailabilities;
			for (j = 0; j < un.length; j++) {
				var from = dateToMinutes(un[j].startDat);
				var to = dateToMinutes(un[j].endDat);
				var domTree = {
					tag : 'div',
					cls : 'truckUnavail',
					html : "<nobr>" + texts.unavailable + ":&nbsp;"
							+ un[j].description + "</nobr>",
					style : 'left:' + this.timeToLength(from) + 'px;width:'
							+ this.durationToLength(to - from) + 'px; height:'
							+ (clist[i].clientHeight + 2) + 'px'
				};
				var unEl = Ext.DomHelper.insertFirst(clist[i], domTree);
				unEl.truckUnavailability = un[j];
			}

			var brks = clist[i].breaks;
			var brk;
			for (j = 0; j < brks.length; j++) {
				brk = new DriverBreak(this, clist[i], brks[j]);
			}

			brks = clist[i].mopBreaks;
			for (j = 0; j < brks.length; j++) {
				brk = new DriverBreak(this, clist[i], brks[j]);
			}

			var jams = clist[i].jams;
			for (j = 0; j < jams.length; j++) {
				var jam = new TrafficJam(this, clist[i], jams[j]);
			}
		}
	}

	this.drawUnavailabilities = function() {
		var clist = Ext.query(".camion", Ext.getDom(this.idPrefix + 'Table'));
		for ( var i = 0; i < clist.length; i++) {
			var un = clist[i].unavailabilities;
			for ( var j = 0; j < un.length; j++) {
				var from = dateToMinutes(un[j].startDat);
				var to = dateToMinutes(un[j].endDat);
				var domTree = {
					tag : 'div',
					cls : 'truckUnavail',
					html : "<nobr>" + texts.unavailable + ":&nbsp;"
							+ un[j].description + "</nobr>",
					style : 'left:' + this.timeToLength(from) + 'px;width:'
							+ this.durationToLength(to - from) + 'px; height:'
							+ (clist[i].clientHeight + 2) + 'px'
				};
				var unEl = Ext.DomHelper.insertFirst(clist[i], domTree);
				unEl.truckUnavailability = un[j];
			}
		}
	};

	this.drawAssignment = function(assignment) {
		el = Ext.get("cd" + this.getAssignmentTruckId(assignment));
		if (el !== null) {
			new DispoAuftrag(el.dom, assignment, this.idPrefix);
		}
	};

	this.drawBreaks = function() {
		var clist = Ext.query(".camion", Ext.getDom(this.idPrefix + 'Table'));
		for ( var i = 0; i < clist.length; i++) {
			var brks = clist[i].breaks;
			var j;
			var brk;
			for (j = 0; j < brks.length; j++) {
				brk = new DriverBreak(this, clist[i], brks[j]);
			}
			brks = clist[i].mopBreaks;
			for (j = 0; j < brks.length; j++) {
				brk = new DriverBreak(this, clist[i], brks[j]);
			}
		}
	};

	this.drawJams = function() {
		var clist = Ext.query(".camion", Ext.getDom(this.idPrefix + 'Table'));
		for ( var i = 0; i < clist.length; i++) {
			var jams = clist[i].jams;
			for ( var j = 0; j < jams.length; j++) {
				var jam = new TrafficJam(this, clist[i], jams[j]);
			}
		}
	};

	this.moveTruckElement = function(truck, prevTruck, truckRow, hourrow) {
		
		var prevTrHcElement = null;
		var prevTrcElement = null;
		if(prevTruck) {
			var cs = Ext.query("tr[id='trhc" + prevTruck.truck.truckId + "']", Ext.getDom(this.idPrefix + 'trucksTable'));
			if (cs.length > 0) {
				prevTrHcElement = new Ext.Element(cs[0]);			
			}			
			cs = Ext.query("tr[id='trc" + prevTruck.truck.truckId + "']", Ext.getDom(this.idPrefix + 'Table'));
			if (cs.length > 0) {
				prevTrcElement = new Ext.Element(cs[0]);			
			}			
		}		
		
		var domTable = Ext.query("tbody",Ext.getDom(this.idPrefix + 'trucksTable'));
		var domTableBody = new Ext.Element(domTable[0]);			
		if(prevTrHcElement) {
			truckRow.appendTo(domTableBody);
			truckRow.insertAfter(prevTrHcElement);			
		} else {
			domTableBody.insertFirst(truckRow);
		}
		
		var hourTable = Ext.query("tbody",Ext.getDom(this.idPrefix + 'Table'));
		var hourTableBody = new Ext.Element(hourTable[0]);
		if(prevTrcElement) {
			hourrow.appendTo(hourTableBody);
			hourrow.insertAfter(prevTrcElement);			
		} else {
			hourTableBody.insertFirst(hourrow);
		}		
	};

	this.buildNewTruckRow = function(truck, truckRow, planning) {
		if(planning) {
			truckRow.appendChild(this.createTd(this.buildPlTruckContent(truck), 'width:210px;padding-left:2px;padding-right:2px;'));
			truckRow.appendChild(this.createTd(this.buildPlTypeContent(truck), 'width:100px;padding-left:2px;padding-right:2px;'));
			truckRow.appendChild(this.createTd(this.buildPlCarrierContent(truck), 'width:190px;padding-left:2px;padding-right:2px;'));
		} else {
			truckRow.appendChild(this.createTd(this.buildTruckDriverContent(truck), 'width:210px'));
			truckRow.appendChild(this.createTd(this.buildTypeStatusContent(truck), 'width:120px'));
			truckRow.appendChild(this.createTd(this.buildLoadQtyContent(truck), 'width:190px'));
		}
	};
	
	this.createTruckElement = function(truck, prevTruck) {			
		var truckId = truck.truck.truckId;

		var truckRow = this.createTruckRow('trhc' + truckId);

		var hourwidth = Math.round((Ext.get(parent).getWidth() - 500 - 17)
				/ (endHour - startHour));
		this.hourwidth = hourwidth;
		
		this.buildNewTruckRow(truck, truckRow, dispoObject.planning);
		
		var hourcells = [];
		var hourrow = this.createTruckRow('trc' + truckId);
		
		this.moveTruckElement(truck, prevTruck, truckRow, hourrow);
		
		for ( var k = 0; k < 24; k++) {
			hourcells[k] = this.createHourCell('cc' + truckId + '_' + k, 'width:' + this.hourwidth + 'px;');
			hourrow.appendChild(hourcells[k]);
		}

		var hourcell = hourcells[0];
		// this div acts as a container for objects taht are placed on the
		// camion's timeline
		// it is of course a drag&drop target
		var width = Math.floor(this.hourwidth * 24) - 4;

		var domTree = this.createDomTree('cd' + truckId, "width:" + width + "px");
		var el = hourcell.insertFirst(domTree);

		el.dom.camion = truck.truck;
		el.dom.isBag = truck.isBag;
		el.dom.isNotAuthorized = truck.isNotAuthorized;
		el.dom.sumFreightCostSpan = new SumFreightCostSpan(truckId);
		el.dom.row = el.dom.parentNode.parentNode;
		el.dom.hrow = truckRow.dom;// Ext.getDom('trhc' + truckId);
		el.dom.loads = truck.loads;
		el.dom.dd = el.initDDTarget("truck" + truckId);

		// calculate th width of one hour from the left offset of the first
		// and the last cell in the row
		// there is no better way of dealing with rounding and differences
		// between browser rendering engines.
		// note: this is a floating point value
		el.dom.hourWidth = this.hourwidth;
		el.dom.hourCell = hourcell;

		el.dom.unavailabilities = truck.truckUnavailabilities;
		el.dom.breaks = truck.driverBreaks;
		el.dom.mopBreaks = truck.mopBreaks;
		el.dom.jams = truck.trafficJams;		
		
	};

	this.createTruckRow = function(id) {
		var tr = new Ext.Element(document.createElement("tr"));
		tr.dom.id = id;		
		return tr;
	};

	this.updateTruckElement = function(truck, truckElement) {
			truckElement.camion = truck.truck;
			truckElement.isBag = truck.isBag;
			truckElement.loads = truck.loads;

			truckElement.unavailabilities = truck.truckUnavailabilities;
			truckElement.breaks = truck.driverBreaks;
			truckElement.mopBreaks = truck.mopBreaks;
			truckElement.jams = truck.trafficJams;

			var planning = isPlanningTruck(truck.truck);
			if (planning && this.planning) {
				truckElement.hrow.childNodes[0].innerHTML = this.buildPlTruckContent(truck);
				truckElement.hrow.childNodes[1].innerHTML = this.buildPlTypeContent(truck);
				truckElement.hrow.childNodes[2].innerHTML = this.buildPlCarrierContent(truck);
			} else {
				truckElement.hrow.childNodes[0].innerHTML = this.buildTruckDriverContent(truck);
				truckElement.hrow.childNodes[1].innerHTML = this.buildTypeStatusContent(truck);
				truckElement.hrow.childNodes[2].innerHTML = this.buildLoadQtyContent(truck);
			}
	};
	
	this.updateTrucks = function(trucks) {
		for (i = 0; i < trucks.length; i++) {
			var truck = trucks[i];
			
			var camion = Ext.get("cd" + truck.truck.truckId);
			if (camion !== null) {
				
				this.updateTruckElement(truck, camion.dom);
				this.resetCamion(camion);
			}
			this.resetTruckHeight(truck.truck.truckId);
		}
	};
	
	this.resetTruckHeight = function(truckId) {
		var trctruckObj = Ext.get('trc' + truckId);
		if (trctruckObj) {
			trctruckObj.setStyle("height", DEFAULT_TRUCK_HEIGHT);
			trctruckObj.first().setStyle("height", DEFAULT_TRUCK_HEIGHT);
			trctruckObj.first().first().setStyle("height", DEFAULT_TRUCK_HEIGHT);
		}
		
		
		var trhctruckObj = Ext.get('trhc' + truckId);
		if (trhctruckObj) {
			trhctruckObj.setStyle("height", DEFAULT_TRUCK_HEIGHT);
			trhctruckObj.first().setStyle("height", DEFAULT_TRUCK_HEIGHT);
		}
	}
	
	this.resetCamion = function(camion) {
		camion.dom.style.height = DEFAULT_DROP_TARGET_HEIGHT;
		camion.dom.row.style.height = DEFAULT_TRUCK_HEIGHT;
		camion.dom.hrow.style.height = DEFAULT_TRUCK_HEIGHT;
		camion.dom.assignmentList = [];

		Ext.DomHelper.overwrite(camion, {
			tag : 'div',
			cls : 'ddselect'
		});
	}

	this.draw();
}

/**
 * Calls DispoAjaxService.changeTruckPriority to change the trucks priority and
 * refreshes the truck list.
 */
function setTruckPriority(truckId, priority) {
	DispoAjaxService.changeTruckPriority(truckId, priority, refreshTrucks);
}

function saveDriverBreakChanges(driverBreakEl) {
	if (driverBreakEl.parentNode) {
		var startMinutes = Math.round(driverBreakEl.offsetLeft * 60
				/ driverBreakEl.parentNode.hourWidth);
		startMinutes = Math.floor(Math
				.round(startMinutes / 60 * dispo.timegrid)
				* 60 / dispo.timegrid); // rounding to grid

		var duration = Math.round(driverBreakEl.offsetWidth * 60
				/ driverBreakEl.parentNode.hourWidth);
		duration = Math.floor(Math.round(duration / 60 * dispo.timegrid) * 60
				/ dispo.timegrid); // rounding to grid

		DispoAjaxService
				.saveDriverBreak(driverBreakEl.driverBreak.driverBreakId,
						driverBreakEl.parentNode.camion.driverId, startMinutes,
						duration, driverBreakEl.driverBreak.isLunchBreak,
						refreshTrucks);
	} else {
		// page is not in a consistent state, cancel change
		refreshTrucks();
	}
}

planningDispoOverrides = {
	idPrefix : 'dispoplanning',
	planning : true,
	getAssignmentTruckId : function(assignment) {
		if (isAssignmentOnPlanningTruck(assignment.assignment)) {
			return assignment.assignment.truckId;
		} else if (assignment.plannedTruckId
				&& (assignment.plannedTruckId != assignment.assignment.truckId)) {
			return assignment.plannedTruckId;
		}
		return null;
	}
};

realDispoOverrides = {
	idPrefix : 'dispo',
	planning : false,
	getAssignmentTruckId : function(assignment) {
		if (!isAssignmentOnPlanningTruck(assignment.assignment)) {
			return assignment.assignment.truckId;
		}
		return null;
	}
};

driverBreakMoveHandleOverrides = {
	roundXTicks : function() {
		var hourWidth = this.getEl().handledEl.parentNode.hourWidth;
		this.xTickSize = hourWidth / dispo.timegrid;
		newTicks = [];
		var dispodiv = Ext.get(Ext.get(this.getEl()).findParentNode(
				'[class="dispotablehead"]'));
		var scrollLeft = dispodiv.dom.scrollLeft;
		var offset = dispodiv.getLeft();
		var tickTime = offset - scrollLeft % this.xTickSize + 3;
		while (tickTime <= offset + dispodiv.getWidth()) {
			newTicks[newTicks.length] = tickTime;
			tickTime += this.xTickSize;
		}
		this.xTicks = newTicks;
	},
	onInvalidDrop : function() {
		saveDriverBreakChanges(this.getEl().handledEl);
		dispo.draggingInProgress = false;
	},
	onDrag : function() {
		this.getEl().handledEl.style.left = this.getEl().offsetLeft - 3;
	},
	startDrag : function(x, y) {
		dispo.draggingInProgress = true;
		this.resetConstraints();
		this.roundXTicks();
	}
};

driverBreakLeftHandleOverrides = {
	roundXTicks : function() {
		var hourWidth = this.getEl().handledEl.parentNode.hourWidth;
		this.xTickSize = hourWidth / dispo.timegrid;
		newTicks = [];
		var dispodiv = Ext.get(Ext.get(this.getEl()).findParentNode(
				'[class="dispotablehead"]'));
		var scrollLeft = dispodiv.dom.scrollLeft;
		var offset = dispodiv.getLeft();
		var tickTime = offset - scrollLeft % this.xTickSize;
		while (tickTime <= offset + dispodiv.getWidth()) {
			newTicks[newTicks.length] = tickTime;
			tickTime += this.xTickSize;
		}
		this.xTicks = newTicks;
	},
	onInvalidDrop : function() {
		saveDriverBreakChanges(this.getEl().handledEl);
		dispo.draggingInProgress = false;
	},
	onDrag : function() {
		var handle = this.getEl();
		var el = handle.handledEl;
		el.style.width = el.offsetWidth + el.offsetLeft - handle.offsetLeft;
		el.style.left = handle.offsetLeft;
	},
	startDrag : function(x, y) {
		dispo.draggingInProgress = true;
		this.resetConstraints();
		this.roundXTicks();
	}
};

driverBreakRightHandleOverrides = {
	roundXTicks : function() {
		var hourWidth = this.getEl().handledEl.parentNode.hourWidth;
		this.xTickSize = hourWidth / dispo.timegrid;
		newTicks = [];
		var dispodiv = Ext.get(Ext.get(this.getEl()).findParentNode(
				'[class="dispotablehead"]'));
		var scrollLeft = dispodiv.dom.scrollLeft;
		var offset = dispodiv.getLeft();
		var tickTime = offset - scrollLeft % this.xTickSize;
		while (tickTime <= offset + dispodiv.getWidth()) {
			newTicks[newTicks.length] = tickTime;
			tickTime += this.xTickSize;
		}
		this.xTicks = newTicks;
	},
	onInvalidDrop : function() {
		saveDriverBreakChanges(this.getEl().handledEl);
		dispo.draggingInProgress = false;
	},
	onDrag : function() {
		var handle = this.getEl();
		var el = handle.handledEl;
		el.style.width = handle.offsetLeft - el.offsetLeft;
	},
	startDrag : function(x, y) {
		dispo.draggingInProgress = this;
		this.resetConstraints();
		this.roundXTicks();
	}
};

function DriverBreak(parent, truck, driverBreak) {
	this.draw = function() {
		var from = dateToMinutes(driverBreak.startDat);
		var to = (driverBreak.endDat.getFullYear() == 9999) ? null
				: dateToMinutes(driverBreak.endDat);
		if (from == to) {
			return;
		}
		var domTree;
		var brkEl;
		if (driverBreak.isMopBreak) {
			var duration;
			if (to === null) {
				if (dateYMDcompare(currentDispoDate, driverBreak.startDat) === 0) {
					duration = dateToMinutes(getRealCurrentDate()) - from;
				} else {
					duration = 0;
				}
			} else {
				duration = to - from;
			}
			domTree = {
				tag : 'div',
				cls : 'mopBreak',
				html : "&nbsp;",
				style : 'left:' + parent.timeToLength(from) + 'px;width:'
						+ parent.durationToLength(duration) + 'px; height:'
						+ (truck.clientHeight + 2) + 'px'
			};
			brkEl = Ext.DomHelper.insertFirst(truck, domTree);
			brkEl.driverBreak = driverBreak;
		} else {
			var text = driverBreak.description ? driverBreak.description : "";
			if (driverBreak.isLunchBreak) {
				text = texts.LunchBreak + " " + text;
			}
			domTree = {
				tag : 'div',
				cls : 'driverBreak',
				style : 'left:' + parent.timeToLength(from) + 'px;width:'
						+ parent.durationToLength(to - from) + 'px; height:'
						+ (truck.clientHeight + 2) + 'px'
			};
			brkEl = Ext.DomHelper.insertFirst(truck, domTree);
			brkEl.driverBreak = driverBreak;
			domTree = {
				tag : 'div',
				cls : 'driverBreakHandle',
				style : 'cursor:e-resize;left:' + parent.timeToLength(from)
						+ 'px;width:3px; height:' + (truck.clientHeight + 2)
						+ 'px'
			};
			brkEl.handleLeft = Ext.DomHelper.insertFirst(truck, domTree);
			brkEl.handleLeft.handledEl = brkEl;
			domTree = {
				tag : 'div',
				cls : 'driverBreakHandle',
				style : 'cursor:e-resize;left:'
						+ (parent.timeToLength(from)
								+ parent.durationToLength(to - from) - 3)
						+ 'px;width:3px; height:' + (truck.clientHeight + 2)
						+ 'px'
			};
			brkEl.handleRight = Ext.DomHelper.insertFirst(truck, domTree);
			brkEl.handleRight.handledEl = brkEl;

			var tip = texts.Break + ': ' + text.replace(/"/g, "&quot;");
			domTree = {
				tag : 'div',
				cls : 'driverBreakHandle',
				html : text,
				'ext:qtip' : tip,
				'ext:qclass' : 'dispoqtip dispoinfoqtip',
				style : 'left:' + (parent.timeToLength(from) + 3) + 'px;width:'
						+ (parent.durationToLength(to - from) - 6)
						+ 'px; height:' + (truck.clientHeight + 2) + 'px'
			};
			brkEl.handleBody = Ext.DomHelper.insertFirst(truck, domTree);
			brkEl.handleBody.handledEl = brkEl;

			var dd = Ext.get(brkEl.handleBody).initDD(
					"breakb" + driverBreak.driverBreakId, null,
					driverBreakMoveHandleOverrides);
			dd.setYConstraint(0, 0, 0);
			dd.isTarget = false;
			dd = Ext.get(brkEl.handleLeft).initDD(
					"breakl" + driverBreak.driverBreakId, null,
					driverBreakLeftHandleOverrides);
			dd.setXConstraint(brkEl.offsetLeft, brkEl.offsetWidth - 3, 0);
			dd.setYConstraint(0, 0, 0);
			dd.isTarget = false;
			dd = Ext.get(brkEl.handleRight).initDD(
					"breakr" + driverBreak.driverBreakId, null,
					driverBreakRightHandleOverrides);
			dd.setXConstraint(brkEl.offsetWidth - 3, truck.offsetWidth
					- brkEl.offsetWidth - brkEl.offsetLeft, 0);
			dd.setYConstraint(0, 0, 0);
			dd.isTarget = false;

			Ext
					.get(brkEl.handleBody)
					.on(
							"dblclick",
							function(e, target) {
								var params = target.handledEl.driverBreak.driverBreakId ? {
									driverBreakId : target.handledEl.driverBreak.driverBreakId
								}
										: {
											driverId : target.handledEl.parentNode.camion.driverId
										};
								params.isLunchBreak = target.handledEl.driverBreak.isLunchBreak;
								showPopup('DriverBreakEntry.do', null, params);
							});
		}
	};

	this.draw();
}

function TrafficJam(parent, truck, trafficJam) {
	this.draw = function() {
		var from = dateToMinutes(trafficJam.startDat);
		var to = (trafficJam.endDat.getFullYear() == 9999) ? null
				: dateToMinutes(trafficJam.endDat);
		if (from == to) {
			return;
		}
		var duration;
		if (to === null) {
			if (dateYMDcompare(currentDispoDate, trafficJam.startDat) === 0) {
				duration = dateToMinutes(getRealCurrentDate()) - from;
			} else {
				duration = 0;
			}
		} else {
			duration = to - from;
		}
		var domTree = {
			tag : 'div',
			cls : 'trafficJam',
			html : "&nbsp;",
			style : 'left:' + parent.timeToLength(from) + 'px;width:'
					+ parent.durationToLength(duration) + 'px; height:'
					+ (truck.clientHeight + 2) + 'px'
		};
		var tjEl = Ext.DomHelper.insertFirst(truck, domTree);
		tjEl.trafficJam = trafficJam;
	};
	this.draw();
}

dispoAuftragOverrides = {
	roundXTicks : function() {
		this.xTickSize = this.hourWidth / dispo.timegrid;
		var dispodiv = Ext.get(this.idPrefix + 'Div');
		var scrollLeft = dispodiv.dom.scrollLeft;
		var offset = dispodiv.getLeft();

		this.minX = offset;
		if (this.leftLimit > scrollLeft) {
			this.minX += this.leftLimit - scrollLeft;
		}
		this.maxX = offset + dispodiv.getWidth() - this.assWidth;
		if (this.rightLimit - scrollLeft + offset < this.maxX) {
			this.maxX = this.rightLimit - scrollLeft + offset;
		}

		if (this.maxX < this.minX) {
			this.minX = this.maxX;
		}

		var roundTime = Math.floor(this.deliveryTime / this.xTickSize)
				* this.xTickSize - scrollLeft + offset - this.deliveryOffset
				- 1;

		while ((roundTime - this.xTickSize) >= this.minX) {
			roundTime -= this.xTickSize;
		}
		var newTicks = [];
		while (roundTime <= this.maxX) {
			newTicks[newTicks.length] = roundTime;
			roundTime += this.xTickSize;
		}
		if (newTicks.length === 0) {
			newTicks[0] = this.minX;
		}
		this.xTicks = newTicks;
	},
	onInvalidDrop : function(e) {
		var el = Ext.get(this.getEl());
		if ((el.parentNode) && (el.parentNode.assignment)) {
			el = el.parentNode;
		}
		if (!el.dom.assignment) {
			return;
		} // check object type
		this.delMinutes = null;
		if (e.getPageY() > this.ordertop) {
			cancelAssignments([ el.dom.assignment.assignment.assignmentId ],
					null);
		} else {
			refreshAssignments();
		}
		el.remove();
		dispo.enableCamionsAfterDD();
		dispo.draggingInProgress = false;
	},
	onAssignmentNotMoved: function() {
		var el = Ext.get(this.getEl());
		var truckId = el.dom.assignment.assignment.truckId;
		if ((el.parentNode) && (el.parentNode.assignment)) {
			el = el.parentNode;
		}
		redrawAssignmentsOfTruck(truckId);
		//remove hover class
		var camionObj = Ext.get("cd" + truckId);
		if (camionObj) {
			camionObj.removeClass("ddhover");
		}
		el.remove();
		dispo.enableCamionsAfterDD();
		dispo.draggingInProgress = false;
	},
	onDragDrop : function(e, id) {
		if (e.getPageY() > this.orderListTop) {
			this.onInvalidDrop(e);
			return;
		}
		var valid = checkValidDropPlace(id, this.ordertop);
		if (!valid) {
			return;
		}

		var el = this.getEl();
		if (!el.assignment) {
			return;
		} // check object type
		
		Ext.get(el).remove();
		var del = Ext.get(id);
		if (del.hasClass("camion")) {
			if (((del.getTop() + del.getHeight()) > this.top) && (del.getTop() < this.dispobottom)) {
				
				/*
				 * In case the assignment not moved to another truck and not moved horizontally either
				 * there is no sense to invoke DispoAjaxService.moveAssignment().
				 * To handle this case we take this behaviour as invalid drop.
				 */
				var assignmentOriginalTruckId = el.assignment.assignment.truckId;
				var draggedOverTruckId = del.dom.camion.truckId;
				var isAssignmentMovedToAnotherTruck = (assignmentOriginalTruckId !== draggedOverTruckId);
				
				var assignmentNotMovedHorizontally = !this.delMinutes;
				if (!isAssignmentMovedToAnotherTruck && assignmentNotMovedHorizontally) {
					this.onAssignmentNotMoved();
					return;
				}
				
				del.removeClass("ddhover");
				var parts = el.assignment.parts;
				if (this.dragOverAssignment) {
					var doparts = this.dragOverAssignment.assignment.parts;
					var orderId = null;
					for ( var i = 0; i < doparts.length; i++) {
						if (doparts[i].assignmentPart.orderId
								&& (doparts[i].realStartMin < this.delMinutes)) {
							orderId = doparts[i].assignmentPart.orderId;
						}
					}
					DispoAjaxService
							.mergeAssignments(
									el.assignment.assignment.assignmentId,
									this.dragOverAssignment.assignment.assignment.assignmentId,
									orderId, updateTruckListOneLine);
					// Delete the merging assignments from "cache"
					var assignmentIDsToDelete = [
							el.assignment.assignment.assignmentId,
							this.dragOverAssignment.assignment.assignment.assignmentId ];
					dispo
							.deleteAssignmentFromAllAssignments(assignmentIDsToDelete);
				} else {
					DispoAjaxService
							.moveAssignment(
									del.dom.camion.truckId,
									el.assignment.assignment.assignmentId,
									parts[this.partNr].assignmentPart.orderId,
									this.delMinutes,
									parts[this.partNr].assignmentPart.extOrderId,
									function(truckAndAssignmentDifference) {
										
										var truckModification = truckAndAssignmentDifference.truckModification;
										var assignmentModification = null;
										if (truckAndAssignmentDifference.assignmentModification) {
											assignmentModification = truckAndAssignmentDifference.assignmentModification.assignments;
										}
										if(truckModification) {
											var trucks = truckModification.newTrucks;
											// Remove the yellow delivery window
											// from the affected trucks
											for (i = 0; i < trucks.length; i++) {
												var truckId = trucks[i].object.truck.truckId;
												var truckObj = Ext.getDom('cd' + truckId);
												//there are real assignments rendered as shadow assignments planning trucks
												//and these real assignments are not returned by the list so the screen must be
												//refreshed therefore
												if (truckHasPrescheduledAssignmentOrPlanningTruck(truckId, assignmentModification)){
													refreshTrucks();
													return;
												}
												
												if (truckObj != null) {
													/*
													 * Erase the assignmentList
													 * array of the truck object
													 * This is used on the freight
													 * cost label creation. If not
													 * erased the freight cost lable
													 * will be displayed for the
													 * original and the new truck as
													 * well.
													 */
													truckObj.assignmentList = [];
													// Remove all child elements of
													// the truck row
													while (truckObj.firstChild) {
														truckObj
																.removeChild(truckObj.firstChild);
													}
													// Re-create the ddselect div
													// within the truck object
													Ext.DomHelper.overwrite(
															truckObj, {
																tag : 'div',
																cls : 'ddselect'
															});
												}
											}
										}
										updateTruckListOneLine(truckAndAssignmentDifference);
									});
				}
				dispo.enableCamionsAfterDD();
				dispo.draggingInProgress = false;
				return;
			}
		}
		this.delMinutes = null;
		refreshOrders();
		refreshAssignments();
		dispo.enableCamionsAfterDD();
		dispo.draggingInProgress = false;
	},
	onDrag : function(e) {
		if(this.dragOverAssignment && (!this.assList || this.assList.length === 0)) {
			for (var i = 0; i < this.dragOverAssignment.childNodes.length; i++) {
				var childNode = this.dragOverAssignment.childNodes[i];
				if(childNode.tagName == 'DIV' && childNode.style.cssText
					&& childNode.style.cssText.indexOf('blue;') > -1) {
					Ext.get(childNode).remove();
				}
			}
		}
		this.dragOverAssignment = null;
		var el = this.getEl();
		var dispodiv = Ext.get(this.idPrefix + 'Div');
		var scrollLeft = dispodiv.dom.scrollLeft;
		var offset = dispodiv.getLeft();
		this.delMinutes = Math
				.round((el.offsetLeft + scrollLeft - offset + this.deliveryOffset)
						* 60 / this.hourWidth);
		this.delMinutes = Math.floor(Math.round(this.delMinutes / 60
				* dispo.timegrid)
				* 60 / dispo.timegrid);
		
		//LOGON-6203, if you drag and drop an overflow assignment, then it jumps 
		//immediately as much to the left as necessary to get it wholly within the screen 
		//--> it causes the wrong delMinutes
		//so, if the delMinutes is outside of delivery time, set it to deliveryFromDat
		if (this.deliveryFromMinutes && (this.delMinutes < this.deliveryFromMinutes)) {
			this.delMinutes = this.deliveryFromMinutes;
		}
		
		if (this.assList) {
			var assignmentWrapper = el.assignment;
			for ( var j = 0; j < this.assList.length; j++) {
				var otherAssignment = this.assList[j].assignment;
				var freechambers = otherAssignment.assignment.nrChambers
						- otherAssignment.orderCnt;
				if (((otherAssignment.assignment.assignStatusCd === ASSIGNMENT_STATUS_NEW)
						|| (otherAssignment.assignment.assignStatusCd === ASSIGNMENT_STATUS_PRE_LOAD) 
						|| (otherAssignment.assignment.assignStatusCd === ASSIGNMENT_STATUS_LOADING))
						&& (otherAssignment.assignment.assignmentId != assignmentWrapper.assignment.assignmentId)
						&& ((assignmentWrapper.orderCnt <= freechambers) || (assignmentWrapper.isBag))) {
					if ((this.delMinutes > Math.min(
							otherAssignment.parts[0].realStartMin,
							otherAssignment.parts[0].plannedStartMin))
							&& (this.delMinutes < Math
									.max(
											otherAssignment.parts[otherAssignment.parts.length - 1].realStartMin,
											otherAssignment.parts[otherAssignment.parts.length - 1].plannedStartMin))) {
						
						if (otherAssignment.isReturnPalletReverse === true 
								&& assignmentWrapper.isReturnPalletReverse === true) {
							firstAssignmentPart = otherAssignment.parts[otherAssignment.lastUnloadIndex];
							secondAssignmentPart = assignmentWrapper.parts[assignmentWrapper.lastUnloadIndex];
						}
						else {
							firstAssignmentPart = otherAssignment.parts[0];
							secondAssignmentPart = assignmentWrapper.parts[0];
						}
						
						var canMerge = true;
						if (!el.assignmentObject.haveSameLocation(
								firstAssignmentPart, secondAssignmentPart)) {
							canMerge = false;
						}
						else if (assignmentWrapper.assignment.assignmentTypeCd != otherAssignment.assignment.assignmentTypeCd) {
							canMerge = false;
						}
						else if (assignmentWrapper.isLandfill || otherAssignment.isLandfill) {
							canMerge = false;
						}
						else if ( (assignmentWrapper.isReturnPalletReverse && !otherAssignment.isReturnPalletReverse)
								|| (!assignmentWrapper.isReturnPalletReverse && otherAssignment.isReturnPalletReverse)) {
							canMerge = false;
						} 
						else if ((assignmentWrapper.orderCnt > freechambers)
								&& ((assignmentWrapper.isBag && !otherAssignment.isBag)
										|| (!assignmentWrapper.isBag && otherAssignment.isBag))) {
							canMerge = false;
						}
						
						if (canMerge) {
							this.dragOverAssignment = this.assList[j];
						} else {
							if (this.assList[j].childNodes[2]) {
								Ext.get(this.assList[j].childNodes[2]).remove();
							}
						}
					} else {
						if (this.assList[j].childNodes[2]) {
							Ext.get(this.assList[j].childNodes[2]).remove();
						}
					}
				}
			}
			if (this.dragOverAssignment) {
				if (!this.dragOverAssignment.childNodes[2]) {
					var selBorder = {
						tag : 'div',
						style : 'border:2px solid blue; width:100%;height:35px; z-index:50; position:relative; top:-10px; left:-2px;'
					};
					Ext.DomHelper.append(Ext.get(this.dragOverAssignment), selBorder);
				}
			}
		}
	},
	onDragEnter : function(e, id) {
		el = Ext.get(id);
		if (el.hasClass("camion")) {
			el.addClass("ddhover");

			if (this.assListTruck != el.dom.camion.truckId) {
				for ( var j = 0; j < el.dom.assignmentList.length; j++) {
					if (el.dom.assignmentList[j].childNodes[2]) {
						Ext.get(el.dom.assignmentList[j].childNodes[2])
								.remove();
					}
				}

				this.assListTruck = el.dom.camion.truckId;
				this.assList = el.dom.assignmentList;
			}
		}
	},
	onDragOut : function(e, id) {
		el = Ext.get(id);
		if (el.hasClass("camion")) {
			el.removeClass("ddhover");

			if (this.assListTruck == el.dom.camion.truckId) {
				for ( var j = 0; j < el.dom.assignmentList.length; j++) {
					if (el.dom.assignmentList[j].childNodes[2]) {
						Ext.get(el.dom.assignmentList[j].childNodes[2])
								.remove();
					}
				}
				this.assListTruck = null;
				this.assList = null;
			}			
		}
	},
	startDrag : function(x, y) {
		var el = this.getEl();
		var horizontalOnly = false;

		if (!el.assignment) {
			return;
		} // check el object type

		var parts = el.assignment.parts;
		if (this.DDM.currentTarget.partNr) {
			this.partNr = this.DDM.currentTarget.partNr;
			horizontalOnly = true;
		} else {
			for ( var i = 0; i < parts.length; i++) {
				if (parts[i].assignmentPart.assignmentPartTypeCd == 3) {
					this.partNr = i;
					break;
				}
			}
		}

		dispo.draggingInProgress = this;

		Ext.get(el).applyStyles({
			'z-index' : 20
		});
		Ext.get(el).appendTo(document.body);
		this.planning = Ext.get(this.idPrefix + 'Div').dom.parentNode.planning;
		this.dispodiv = Ext.get('dispoDiv');
		this.dispoplanningdiv = Ext.get('dispoplanningDiv');

		// Default: None of the Real or Plannning trucks hidden, or only Real
		// trucks hidden.
		this.top = this.dispoplanningdiv.getTop();
		if (hidePlanningTrucks) {
			this.top = this.dispodiv.getTop();
		}
		this.dispobottom = this.top + this.dispodiv.getHeight()
				+ this.dispoplanningdiv.getHeight();
		this.ordertop = Ext.get('orderDiv').getTop();
		this.bottom = this.ordertop + Ext.get('orderDiv').getHeight();
		this.orderListTop = Ext.get("orderlistcontainer").getTop();
		this.setDragElPos(x, y);
		if (horizontalOnly) {
			this.setYConstraint(0, 0, 0);
		} else {
			this.setYConstraint(y - this.top, this.bottom - y, 0);
		}
		this.resetConstraints();

		this.deliveryTime = el.assignmentObject
				.durationToLength(parts[this.partNr].plannedStartMin);
		this.deliveryOffset = el.assignmentObject
				.durationToLength(parts[this.partNr].plannedStartMin
						- parts[0].plannedStartMin);
		
		if (el.assignment && el.assignment.assignment) {
			var assignment = el.assignment.assignment;
			var deliveryFrom = parts[this.partNr].assignmentPart.deliveryFromDat;
			if (assignment.assignmentTypeCd == ASSIGNMENT_PART_TYPE_LOAD) {
				deliveryFrom = getDispoDateMorning();
			}
			this.deliveryFromMinutes = dateToMinutes(deliveryFrom);
		}

		this.roundXTicks();

		dispo.disableCamionsForDD(el.assignment);
	}
};

dispoOrderOverrides = {
	roundXTicks : function() {
		/*
		 * Since the planning and real the truck section could be hided also,
		 * there could be a case in which we don't want to calculate wrong
		 * values.
		 */
		if (hidePlanningTrucks && hideRealTrucks) {
			return;
		}

		this.xTickSize = this.hourWidth / dispo.timegrid;

		// only the sizes are required from dispodiv, what are the same than the
		// dispoplanningdiv
		var dispodiv;
		// LOGON-5624: We use to the visible truck div instead of just dispoDiv
		// because it could be hidden.
		if (!hideRealTrucks) {
			dispodiv = Ext.get('dispoDiv');
		}
		if (!hidePlanningTrucks) {
			dispodiv = Ext.get('dispoplanningDiv');
		}

		var scrollLeft = dispodiv.dom.scrollLeft;
		var offset = dispodiv.getLeft();
		this.minX = offset;
		if (this.leftLimit > scrollLeft) {
			this.minX += this.leftLimit - scrollLeft;
		}
		this.maxX = offset + dispodiv.getWidth() - this.assWidth;
		if (this.rightLimit - scrollLeft + offset < this.maxX) {
			this.maxX = this.rightLimit - scrollLeft + offset;
		}

		if (this.maxX < this.minX) {
			this.minX = this.maxX;
		}

		var roundTime = Math.floor(this.deliveryTime / this.xTickSize)
				* this.xTickSize - scrollLeft + offset - this.deliveryOffset;
		while ((roundTime - this.xTickSize) >= this.minX) {
			roundTime -= this.xTickSize;
		}
		newTicks = [];
		while (roundTime <= this.maxX) {
			newTicks[newTicks.length] = roundTime;
			roundTime += this.xTickSize;
		}
		if (newTicks.length === 0) {
			newTicks[0] = this.minX;
		}
		this.xTicks = newTicks;
	},
	onInvalidDrop : function(e) {
		if (!this.order) {
			return;
		} // check object type
		Ext.get(this.getEl()).remove();
		refreshOrders();
		dispo.enableCamionsAfterDD();
		dispo.draggingInProgress = false;
	},
	onDragDrop : function(e, id) {
		if (!this.order) {
			return;
		} // check object type
		if (e.getPageY() > this.ordertop) {
			this.onInvalidDrop(e);
			return;
		}
		
		var valid = checkValidDropPlace(id, this.ordertop);
		if (!valid) {
			this.onInvalidDrop(e);
			return;
		}

		var el = this.getEl();
		var elOffset = el.offsetLeft;
		Ext.get(el).remove();
		var del = Ext.get(id);
		if (del.hasClass("camion")) {
			del.removeClass("ddhover");
			// FIXME (paksyd Apr 16, 2013): Possible bug! When real dispo is
			// hidden we should get the planning div!
			var dispodiv = Ext.get('dispoDiv'); // only the sizes are required
			// from dispodiv, what are the
			// same than the
			// dispoplanningdiv
			var scrollLeft = dispodiv.dom.scrollLeft;
			var offset = dispodiv.getLeft();
			var delOffset = this.order.order.transportDurationMin
					+ this.order.loadingTime;
			var delMinutes = Math.round((elOffset + scrollLeft - offset) * 60
					/ this.hourWidth)
					+ delOffset;
			delMinutes = Math.floor(Math
					.round(delMinutes / 60 * dispo.timegrid)
					* 60 / dispo.timegrid);
			var lnfCoordKnown = (!Ext.isEmpty(this.order.order.lnfCoordLat)
					&& !Ext.isEmpty(this.order.order.lnfCoordLon) && (this.order.order.lnfCoordLat !== 0 || this.order.order.lnfCoordLon !== 0));
			var shiptoCoordKnown = (!Ext
					.isEmpty(this.order.order.shiptoCoordlat) && !Ext
					.isEmpty(this.order.order.shiptoCoordlon));
			// case carrier stock
			if (!this.order.externalOrder && this.order.isBag
					&& !Ext.isEmpty(this.order.order.bagStockCoordLat)
					&& !Ext.isEmpty(this.order.order.bagStockCoordLon)
					&& (lnfCoordKnown || shiptoCoordKnown)
					&& (del.dom.camion.carrierId == this.order.commCarrierId)) {

				// in case a bag stock has to be used,
				// add a listener to the load of the values

				// in case not, call the ajax function anyway, but with
				// parameters which will cause a call for a standard route
				// between the two points and the distance, duration will
				// come from there

				var callLoadAtCarrierStockDel = callLoadAtCarrierStock
						.createDelegate(this);
				callLoadAtCarrierStockDel(del, delMinutes - delOffset);
			} else {
				// normal case
				// del.update('');
				DispoAjaxService.createAssignment(del.dom.camion.truckId,
						this.order.order.orderNumber, delMinutes - delOffset,
						this.order.loadingTime,
						this.order.order.transportDurationMin,
						this.order.order.plannedUnloadingTimeMin,
						this.order.externalOrder, function(truckAndAssignmentDifference) {
							refreshOrders();
							//there are real assignments rendered as shadow assignments planning trucks
							//and these real assignments are not returned by the list so the screen must be
							//refreshed therefore
							var assignmentModification = null;
							if (truckAndAssignmentDifference.assignmentModification) {
								assignmentModification = truckAndAssignmentDifference.assignmentModification.assignments;
							}
							if (truckHasPrescheduledAssignmentOrPlanningTruck(del.dom.camion.truckId, assignmentModification)){
								refreshTrucks();
							}
							else {
								updateTruckListOneLine(truckAndAssignmentDifference);
							}
						});
			}

			dispo.enableCamionsAfterDD();
			dispo.draggingInProgress = false;
			return;
		}
		refreshOrders();
		refreshAssignments();
		dispo.enableCamionsAfterDD();
		dispo.draggingInProgress = false;
	},
	onDragEnter : function(e, id) {
		el = Ext.get(id);
		if (el.hasClass("camion")) {
			prev = null;
			if (!Ext.get(el.dom.row).hasClass("disabled")) {
				el.addClass("ddhover");
				orderLeft = this.orderObj.iel.getLeft() - el.getLeft() + 500;
				prev = dispo.getPrevAssignment(el, orderLeft);
				next = dispo.getNextAssignment(el, orderLeft);
				if (next !== null) {
					next.assignmentObject.setErrorMark(this.oldParent);
				}
			}
			this.orderObj.setErrorMark(prev);
		}
	},
	onDragOut : function(e, id) {
		el = Ext.get(id);
		if (el.hasClass("camion")) {
			el.removeClass("ddhover");
			next = null;
			if (!Ext.get(el.dom.row).hasClass("disabled")) {
				orderLeft = this.orderObj.iel.getLeft() - el.getLeft() + 500;
				next = dispo.getNextAssignment(el, orderLeft);
				if (next !== null) {
					nextLeft = next.offsetLeft;
					prev = dispo.getPrevAssignment(el, nextLeft);
					next.assignmentObject.setErrorMark(prev);
				}
			}
		}
	},
	startDrag : function(x, y) {
		if (!this.getEl().parentNode) {
			return;
		} // check object type
		if (!this.getEl().parentNode.order) {
			return;
		} // check object type
		dispo.draggingInProgress = this;
		this.resetConstraints();
		Ext.get(this.getEl()).applyStyles({
			'z-index' : 20
		});
		dispo.disableCamionsForDD(this.getEl().parentNode.order);
		this.orderObj = this.getEl().parentNode.orderObject;
		this.order = this.getEl().parentNode.order;
		this.oldParent = this.getEl().parentNode;
		this.ordertop = Ext.get('orderlistcontainer').getTop();
		Ext.get(this.getEl()).appendTo(document.body);
		this.setDragElPos(x, y);
		// the top of the dispo area is needed here. the planning area is on the
		// top of this...
		var top = Ext.get('dispoplanningDiv').getTop();
		var bottom = Ext.get('orderDiv').getTop()
				+ Ext.get('orderDiv').getHeight();
		this.setYConstraint(y - top, bottom - y, 0);
		this.roundXTicks();
	}
};

var lastMouseOverAssignmentId = null;
var currMouseOverAssignmentId = null;

function callLoadAtCarrierStock(del, startMinute) {
	DispoAjaxService.createCarrierStockAssignment(del.dom.camion.truckId,
			this.order.order.orderNumber, startMinute, this.order.loadingTime,
			this.order.order.plannedUnloadingTimeMin,
			(this.order.order.orderId === null), function() {
				refreshOrders();
				refreshAssignments();
			});
}

function checkValidDropPlace(id, ordertop) {
	var grandmaTop = 0;
	var grandmaBottom = ordertop;

	// getting planning or real dispoDiv
	var dispoDiv = Ext.get(id).findParentNode('#dispoDiv');
	if (!dispoDiv) {
		dispoDiv = Ext.get(id).findParentNode('#dispoplanningDiv');
	}
	if (dispoDiv) {
		grandmaTop = Ext.get(dispoDiv).getTop();
		grandmaBottom = Ext.get(dispoDiv).getBottom();
	}

	// getting top of truck in which the order wants to be dropped
	var truckTop = Ext.get(id).getTop();

	// truck must be on the div (ui) it belongs to logically
	if (truckTop < grandmaTop || truckTop > grandmaBottom) {
		return false;
	}

	return true;
}

function auftragMouseoverHandler(e, target) {
	var ass = target.parentNode.parentNode.assignment;
	lastMouseOverAssignmentId = currMouseOverAssignmentId;
	currMouseOverAssignmentId = ass.assignment.assignmentId;
	if (currMouseOverAssignmentId
			&& (currMouseOverAssignmentId == lastMouseOverAssignmentId)) {
		return;
	}
	for ( var i = 0; i < ass.parts.length; i++) {
		if (ass.parts[i].assignmentPart.orderId) {
			var span = Ext.get('span'
					+ ass.parts[i].assignmentPart.assignmentPartId);
			if (span) {
				span.addClass('auftragSpanSelected');
			}
		}
	}
}

function auftragMouseoutHandler(e, target) {
	if (currMouseOverAssignmentId == lastMouseOverAssignmentId) {
		return;
	}
	lastMouseOverAssignmentId = null;
	currMouseOverAssignmentId = null;
	var ass = target.parentNode.parentNode.assignment;
	for ( var i = 0; i < ass.parts.length; i++) {
		if (ass.parts[i].assignmentPart.orderId) {
			var span = Ext.get('span'
					+ ass.parts[i].assignmentPart.assignmentPartId);
			if (span) {
				span.removeClass('auftragSpanSelected');
			}
		}
	}
}

function DispoAuftrag(parent, data, idPrefix) {

	this.timeToLength = function(time) {
		return Math.round((parent.hourWidth * time) / 60);
	};

	this.durationToLength = function(duration) {
		return Math.round((parent.hourWidth * duration) / 60);
	};

	this.createBalkenPart = function(width, className, balken, balkencnt) {
		if (width > 1) {
			balken[balkencnt++] = {
				tag : 'div',
				'cls' : className,
				style : 'width:' + width + 'px'
			};
		} else if (width == 1) {
			balken[balkencnt++] = {
				tag : 'div',
				'cls' : className,
				style : 'border-left:none;border-right:none;width:' + width
						+ 'px'
			};
		}
		return balkencnt;
	};

	this.findNextUnloadAssignmentPart = function(currentPosition, parts) {
		var lengthOfParts = parts.length;
		var result;
		var j;

		for (j = currentPosition + 1; j < lengthOfParts; j++) {
			result = j;
			if (j < lengthOfParts - 1) { 
					var nextPart = parts[j + 1];
					var nextAssignmentPart = nextPart.assignmentPart;
					var nextAssignmentPartTypeCd = nextAssignmentPart.assignmentPartTypeCd;
		
					if (nextAssignmentPartTypeCd == ASSIGNMENT_PART_TYPE_UNLOAD) {
						return result;
					}
			}
		}
		return result;
	};
	
	this.applyBreakDifferences = function(parts){
		var lengthOfParts = parts.length;
		var firstPart = parts[0];
		var secondPart = parts[1];
		var breaks = parent.breaks;
		var lengthOfBreaks = breaks.length;
		var j;
		var i;
		
		for (i = 0; i < lengthOfBreaks; i++) {
			var currentBreak = breaks[i];
			var breakStartMinutes = dateToMinutes(currentBreak.startDat);
			var breakEndMinutes = dateToMinutes(currentBreak.endDat);
			var diff = 0;

			if ((firstPart.plannedStartMin <= breakEndMinutes)
					&& (secondPart.plannedStartMin > breakStartMinutes)) {
				diff = breakEndMinutes - firstPart.plannedStartMin;
				firstPart.plannedStartMin += diff;
			}

			for (j = 0; j < lengthOfParts - 1; j++) {
				var currentPart = parts[j];
				var nextPart = parts[j + 1];
				if ((diff === 0) 
						&& (currentPart.plannedStartMin <= breakEndMinutes)
						&& (nextPart.plannedStartMin > breakStartMinutes)) {
					diff = breakEndMinutes - breakStartMinutes;
				}
				nextPart.plannedStartMin += diff;
			}
		}
	};
	
	/**
	 * Determines the suffix of the div class for the dispo colors.
	 * For example on time, on hold, external orders have different colors then the other orders.
	 */
	this.chooseApplicableStatusFlag = function(assignment, assignmentPart, isReal, isOnTimeOrder) {
		
		var statusFlag = DELIVERY_STATUS_FLAG_PLANNED;
		
		var assignTrStatusCd = assignment.assignTrStatusCd;
		
		if (assignTrStatusCd == ASSIGN_STATUS_TR_TRANSMITTED) {
			var assignPartStatusCd = assignmentPart.assignPartStatusCd; 
			if (assignPartStatusCd == ASSIGNMENT_PART_STATUS_TRANSMITTED_ACK) {
				statusFlag = DELIVERY_STATUS_FLAG_CONFIRMED;
			} else {
				statusFlag = DELIVERY_STATUS_FLAG_TRANSMITTED;
			}
		}
		else if (isOnTimeOrder) {
			statusFlag = DELIVERY_STATUS_FLAG_DISPATCHED_OR_BEYOND;
		}
		
		var assignmentTypeCd = assignment.assignmentTypeCd;
		
		if (assignmentTypeCd == ASSIGNMENT_TYPE_EXTERNAL) {
			statusFlag += DELIVERY_TYPE_FLAG_EXTERNAL;
		} else if (isOnTimeOrder) {
			statusFlag = DELIVERY_TYPE_FLAG_ONTIME + statusFlag;
		}

		if (this.locked) {
			statusFlag = DELIVERY_TYPE_FLAG_LOCKED;
		}
		
		if (!isReal) {
			statusFlag = DELIVERY_TYPE_FLAG_PRESCHEDULED;
		} else if (this.isExternalLocked) {
			statusFlag = DELIVERY_TYPE_FLAG_EXTERNAL_LOCKED;
		} else if (data.isOnHoldOrderAndLockedFlag) {
			statusFlag = DELIVERY_TYPE_FLAG_ONHOLD;
		}

		return statusFlag;
	};
	
	this.createMaterialDescription = function(assignment, part) {
		var separator = '';
		var materialDescription = '<nobr>';
		var assignmentPart = part.assignmentPart;
		var j;
		if (assignmentPart.orderId) {
			var orderItems = part.orderItems;
			for (j = 0; j < orderItems.length; j++) {
				var currentOrderItem = orderItems[j];
				
				materialDescription += separator
						+ currentOrderItem.materialName
						+ '&nbsp;('
						+ currentOrderItem.orderedQuantity
						+ '&nbsp;'
						+ currentOrderItem.uomName
						+ ')';
				separator = ',&nbsp;';
			}
		} else {
			materialDescription += nvl([ assignment.materialName, '-' ])
									+ '&nbsp;( '
									+ nvl([ assignment.materialQuantity,
									'-' ])
									+ nvl([ assignment.materialUom, ' ' ])
									+ ' )';
		}
		materialDescription += '</nobr>';
		
		return materialDescription;
	};
	
	this.draw = function() {

		var errorText = "";
		var errorType = ERROR_NONE;

		var i;
		var j;		
		var parts = data.parts;
		var lengthOfParts = parts.length;
		var firstPart = parts[0];
		var firstAssignmentPart = firstPart.assignmentPart;
		var secondPart = parts[1];
		var lastPart = parts[lengthOfParts - 1];
		var assignment = data.assignment;
		var unloadIndex = data.firstUnloadIndex;
		var highestAssignmentPartStatus = data.highestAssignmentPartStatus;
		var highestOrderStatus = data.highestOrderStatus;
		var isOnTimeOrder = data.isOnTimeOrder;
		
		// Setting the plannedStartMin attribute on the assignment parts for
		// drawing
		var currentPart;
		var currentAssignmentPart;
		for (i = 0; i < lengthOfParts; i++) {
			currentPart = parts[i];
			currentAssignmentPart = currentPart.assignmentPart;
			currentPart.plannedStartMin = dateToMinutes(currentAssignmentPart.plannedStart);
		}

		this.applyBreakDifferences(parts);

		var now = dateToMinutes(getRealCurrentDate());
		var nowIsLaterDay = false;
		
		if ((assignment.assignStatusCd === ASSIGNMENT_STATUS_NEW)
				&& (assignment.assignTrStatusCd === 0)) {
			now = firstPart.plannedStartMin;
		} 
		else if (now >= MINUTES_OF_DAY) {
			now = MINUTES_OF_DAY;
			nowIsLaterDay = true;
		}
		
		if (firstAssignmentPart.realStart === null) {
			firstPart.realStartMin = Math.max(firstPart.plannedStartMin, now);
		} else {
			firstPart.realStartMin = dateToMinutes(firstAssignmentPart.realStart);
		}
		

		var previousPart;
		for (i = 1; i < lengthOfParts; i++) {
			currentPart = parts[i];
			previousPart = parts[i - 1];
			currentAssignmentPart = currentPart.assignmentPart;
			if (currentAssignmentPart.realStart === null) {
						var minutes =	previousPart.realStartMin 
										+ currentPart.plannedStartMin 
										- previousPart.plannedStartMin;
						var trimmedMinutes = trimMinutes(minutes);
						currentPart.realStartMin = Math.max(trimmedMinutes, now);
						if ((currentPart.realStartMin == now) && nowIsLaterDay) {
										errorType = ERROR_OVER_MIDNIGHT;
										errorText = errortexts.overMidnight;
				}
			} else {
				currentPart.realStartMin = dateToMinutes(currentAssignmentPart.realStart);
			}
		}

		var innerbasetime = Math.min(firstPart.plannedStartMin, firstPart.realStartMin);
		var innerduration = Math.max(lastPart.plannedStartMin, lastPart.realStartMin)
									- innerbasetime;

		var outerbasetime = 0;
		var outerendtime = MINUTES_OF_DAY;

		this.isExternalLocked = checkIsExternalLocked(data);
		this.locked = (assignment.poolId != assignment.truckPoolId && assignment.poolId !== null)
						|| this.isExternalLocked;

		// this case is when the assignment is shown on the planning truck,
		// which is in fact not the assignment itself, just a duplicate of the
		// normal assignment - initialization will be skipped
		var isReal = (parent.camion.truckId == assignment.truckId);
		// cases when no details and no popup is supposed to appear on the
		// assignment
		var showScheduleDetails = !this.isExternalLocked;
		var showAssignmentPopup = !this.isExternalLocked;

		var sollbar = [];
		var sollcnt = 0;
		if (firstPart.plannedStartMin > innerbasetime) {
			var firstPartWith = this.durationToLength(firstPart.plannedStartMin - innerbasetime)
			sollbar[sollcnt++] = {
				tag : 'div',
				'cls' : 'balkenPart',
				style : 'width:' + firstPartWith + 'px'
			};
		}
		var istbar = [];
		var istcnt = 0;
		if (firstPart.realStartMin > innerbasetime) {
			var firstPartWidth = this.durationToLength(firstPart.realStartMin - innerbasetime)
			istbar[istcnt++] = {
				tag : 'div',
				'cls' : 'balkenPart',
				style : 'width:' + firstPartWidth + 'px'
			};
		}

		// this flag is used later at truckscheduling info in the customer order
		// header
		data.isLate = false;
		var deliveryToDat;
		var deliveryTo;

		var nextPart;
		var currentAssignmentPartTypeCd;
		
		for (i = 0; i < lengthOfParts - 1; i++) {
			currentPart = parts[i];
			nextPart = parts[i + 1];
			currentAssignmentPart = currentPart.assignmentPart;
			currentAssignmentPartTypeCd = currentAssignmentPart.assignmentPartTypeCd;
			
			var statusAppend = this.chooseApplicableStatusFlag(assignment, currentAssignmentPart, isReal, isOnTimeOrder);
					
			var sollStatusAppend = statusAppend;
			var istStatusAppend = statusAppend;

			if (currentAssignmentPartTypeCd == ASSIGNMENT_PART_TYPE_UNLOAD) {
				deliveryTo = currentAssignmentPart.deliveryToDat;

				if (deliveryTo !== null) {
					var m = Math.floor((deliveryTo.getTime() - getRealCurrentDate().getTime()) / (60 * 1000))
							- (currentPart.plannedStartMin - firstPart.plannedStartMin);
					if (m < 0) {
						data.isLate = true;
					}
				}
				if (assignment.assignmentTypeCd == ASSIGNMENT_TYPE_EXTERNAL) {
					deliveryFrom = getDispoDateMorning();
					deliveryTo = getDispoDateMidnight();
				}
				if ((deliveryTo !== null) && dateToMinutes(deliveryTo) < currentPart.realStartMin) {
					istStatusAppend = "Er";
					if (istcnt > 0) {
						istbar[istcnt - 1].cls = PART_TYPES[ASSIGNMENT_PART_TYPE_TO] + istStatusAppend;
						data.isLate = true;
					}
				}
				if ((deliveryTo !== null) && dateToMinutes(deliveryTo) < currentPart.plannedStartMin) {
					sollStatusAppend = "Er";
					if (sollcnt > 0) {
						sollbar[sollcnt - 1].cls = PART_TYPES[ASSIGNMENT_PART_TYPE_TO] + sollStatusAppend;
						data.isLate = true;
					}
				}
			}
			
			var plannedDurationLength = this.durationToLength(nextPart.plannedStartMin - currentPart.plannedStartMin);
			
			var assignmentPartTypeName = PART_TYPES[currentAssignmentPartTypeCd];
			
			sollcnt = this.createBalkenPart(plannedDurationLength, 
											assignmentPartTypeName + sollStatusAppend, 
											sollbar, 
											sollcnt);

			if (assignment.assignTrStatusCd === 0) {
				if ((firstPart.plannedStartMin - dateToMinutes(getRealCurrentDate())) < 15) {
					errorText = errortexts.notTransmitted;
					errorType = ERROR_ASSIGNMENT_NOT_TRANSMITTED;
				}
			} else {
				if ((assignment.assignStatusCd === ASSIGNMENT_STATUS_NEW)
						&& ((firstPart.plannedStartMin - dateToMinutes(getRealCurrentDate())) < 15)
						&& (firstAssignmentPart.assignPartStatusCd === 0)
						&& (parent.camion.pdaSessionId !== null)
						&& (parent.camion.pdaAvailableFlag)) {
					errorText = errortexts.notAcked;
					errorType = ERROR_ASSIGNMENT_NOT_ACKED;
				}
			}

			var realDurationLength = this.durationToLength(nextPart.realStartMin - currentPart.realStartMin);

			istcnt = this.createBalkenPart(realDurationLength,
											assignmentPartTypeName + istStatusAppend,
											istbar, 
											istcnt);
		}

		var collisions = [];
		var clist = Ext.query("div[class='auftragOuter']", parent);

		var asswidth = this.durationToLength(innerduration);
		var assleft = this.durationToLength(innerbasetime);

		// position and width of the sollBar
		var assSollWidth = this.durationToLength(lastPart.plannedStartMin
						- firstPart.plannedStartMin);
		var assSollLeft = this.durationToLength(firstPart.plannedStartMin);

		// position and width of the istBar
		var assIstWidth = this.durationToLength(lastPart.realStartMin
						- firstPart.realStartMin);
		var assIstLeft = this.durationToLength(firstPart.realStartMin);

		if (assIstWidth === 0) {
			assIstWidth = asswidth;
			assIstLeft = assleft;
		}

		for (j = 0; j < clist.length; j++) {
			otherIstLeft = clist[j].assIstLeft;
			otherIstWidth = clist[j].assIstWidth;
			otherSollLeft = clist[j].assSollLeft;
			otherSollWidth = clist[j].assSollWidth;

			if ((assIstLeft <= otherIstLeft + otherIstWidth)
					&& (assIstLeft + assIstWidth >= otherIstLeft)
					|| (assSollLeft <= otherSollLeft + otherSollWidth)
					&& (assSollLeft + assSollWidth >= otherSollLeft)) {
				collisions[collisions.length] = clist[j].offsetTop - 8;
			}
		}
		var asstop = 1;
		while (collisions.indexOf(asstop) > -1) {
			asstop += 40;
		}
		if (parent.clientHeight < (asstop + 36)) {
			parent.style.height = asstop + 36;
			parent.row.style.height = asstop + DEFAULT_TRUCK_HEIGHT;
			parent.hrow.style.height = asstop + DEFAULT_TRUCK_HEIGHT;
			
			// 6-Jan-2015/nhattan: LOGON-9050 IE10, wrong display if there is several assignments for one truck
			parent.row.children[0].style.height = asstop + DEFAULT_DROP_TARGET_HEIGHT;
			parent.hrow.children[0].style.height = asstop + DEFAULT_DROP_TARGET_HEIGHT;
		}
		
		var auftragbalken = {
			tag : 'div',
			cls : 'auftragInner',
			style : "width:100%;left:0px",
			custordernr : data.custorderNumber,
			children : [ {
				tag : 'div',
				cls : 'auftragSollIst',
				children : sollbar
			}, {
				tag : 'div',
				cls : 'auftragSollIst',
				children : istbar
			}, {
				tag : 'img',
				src : crhCommonUrl('../images/warning2.png'),
				cls : 'auftragError',
				'ext:qtip' : errorText,
				'ext:qclass' : 'dispoqtip dispoerrorqtip'
			}, {
				tag : 'div',
				id : "dispoBarOverlay_" + assignment.assignmentId,
				cls : 'dispoBarOverlay',
				style : "width:100%;left:0px"
			} ],
			id : isReal ? "ai" + assignment.assignmentId : "pl"
					+ assignment.assignmentId
		};

		var span;

		if (showScheduleDetails) {
			var loadPartCount = data.countOfLoadAssignmentParts;
			var unloadPartCount = data.countOfUnloadAssignmentParts;
			var descriptionstartAp = data.firstLoadIndex;
			var latestFinishedPart = parts[data.indexOfFinishedAssignmentPartAfterLatestUnload];
			var latestFinishedAssignmentPart = latestFinishedPart.assignmentPart;
			var assignmentPartPlantCd = '';
			var assignmentPartReturningPlantCd = latestFinishedAssignmentPart.plantCd;
			var assignmentPartReturningPlantCdDisplayPosition = data.lastUnloadIndex;

/*			for (i = lengthOfParts - 1; i > 0; i--) {
					var assignmentPart = parts[i];
					var assignmentPartTypeCd = assignmentPart.assignmentPartTypeCd;
					if (assignmentPartTypeCd == ASSIGNMENT_PART_TYPE_FINISHED) {
								assignmentPartReturningPlantCd = assignmentPart.plantCd;
					}
					else if (assignmentPartTypeCd == ASSIGNMENT_PART_TYPE_UNLOAD) {
								// this is the last load. If more than one load objects the
								// returning plant must displayed only with the last load.
								assignmentPartReturningPlantCdDisplayPosition = i;
								break;
					}
			}*/
			

			for (i = 0; i < lengthOfParts - 1; i++) {
				currentPart = parts[i];
				currentAssignmentPart = currentPart.assignmentPart;
				currentAssignmentPartTypeCd = currentAssignmentPart.assignmentPartTypeCd;

				if (currentAssignmentPartTypeCd == ASSIGNMENT_PART_TYPE_LOAD ) {
					assignmentPartPlantCd = currentAssignmentPart.plantCd;
				}
				else if (currentAssignmentPartTypeCd == ASSIGNMENT_PART_TYPE_UNLOAD) {
					auftragbalken.children[auftragbalken.children.length] = {
						tag : 'div',
						cls : 'auftragDeliveryTimeSoll',
						style : 'left:'
								+ (this
										.durationToLength(currentPart.plannedStartMin
												- innerbasetime) - 1) + 'px'
					};
					auftragbalken.children[auftragbalken.children.length] = {
						tag : 'div',
						cls : 'auftragDeliveryTimeIst',
						style : 'left:'
								+ (this
										.durationToLength(currentPart.realStartMin
												- innerbasetime) - 1) + 'px'
					};

					var sollIstConnectStart = currentPart.plannedStartMin;
					var sollIstConnectLength = currentPart.realStartMin - currentPart.plannedStartMin;

					if (sollIstConnectLength < 0) {
						sollIstConnectLength = -1 * sollIstConnectLength;
						sollIstConnectStart = currentPart.realStartMin;
					}

					auftragbalken.children[auftragbalken.children.length] = {
						tag : 'div',
						cls : 'auftragDeliveryTimeSollIstConnect',
						style : 'left:'
								+ (this.durationToLength(sollIstConnectStart
										- innerbasetime) - 1)
								+ 'px;'
								+ 'width:'
								+ (this.durationToLength(sollIstConnectLength) + 3)
								+ 'px'
					};

					if ((loadPartCount > 1) || (unloadPartCount > 1) || (descriptionstartAp < 0)) {
						descriptionstartAp = i - 1;
					}

					var nextDescPart = this.findNextUnloadAssignmentPart(i, parts);
					var descriptionstartApPart = parts[descriptionstartAp]; //FIXME 17-Jul-2013/fecnek: Rename this wrong variable name to a good one
					var nextDescPartPart = parts[nextDescPart]; //FIXME 17-Jul-2013/fecnek: Rename this wrong variable name to a good one

					var descriptionwidthSoll = this.durationToLength(nextDescPartPart.plannedStartMin
																		- descriptionstartApPart.plannedStartMin);

					var descriptionwidthIst;

					var descriptionleftSoll = this.durationToLength(descriptionstartApPart.plannedStartMin - innerbasetime) + 1;
					var descriptionleftIst = 2;

					if (descriptionstartApPart.assignmentPart.realStart !== null) {
						if (descriptionstartApPart.realStartMin < MINUTES_OF_DAY) {
							descriptionleftIst = this.durationToLength(descriptionstartApPart.realStartMin
														- innerbasetime) + 1;
							descriptionwidthIst = this.durationToLength(nextDescPartPart.realStartMin
														- descriptionstartApPart.realStartMin);
						} else {
							descriptionleftIst = this.durationToLength(descriptionstartApPart.plannedStartMin
														- innerbasetime) + 1;
							descriptionwidthIst = this.durationToLength(MINUTES_OF_DAY
														- descriptionstartApPart.plannedStartMin);
						}
					} else {
						if (assignment.assignTrStatusCd === ASSIGNMENT_PART_STATUS_NEW) {
							// Not yet transmitted to a truck
							descriptionleftIst = descriptionleftSoll;
							descriptionwidthIst = descriptionwidthSoll;
						} else {
							var dateNow = getRealCurrentDate();
							var cmp = dateYMDcompare(currentDispoDate, dateNow);
							if (cmp === 0 
									&& (assignment.assignStatusCd > ASSIGNMENT_PART_STATUS_NEW 
									|| (assignment.assignStatusCd === ASSIGNMENT_PART_STATUS_NEW 
									&& assignment.assignTrStatusCd == ASSIGN_STATUS_TR_TRANSMITTED))) {
								/*
								 * Only count css left value, when it cannot be
								 * negative. Negative left value causes the
								 * plant code to appear way left than the order.
								 * If more orders in the same row, the
								 * descriptions are displayed on each other. See
								 * "LOGON-5320 Scheduling Screen: Visualization
								 * of the plant codes (2nd line of an order) is
								 * wrong"
								 */
								var nowToMinutes = dateToMinutes(dateNow);
								if (nowToMinutes >= innerbasetime) {
									descriptionleftIst = this.durationToLength(nowToMinutes - innerbasetime) + 4;
								}
								descriptionwidthIst = descriptionwidthSoll;
							} else {
								descriptionleftIst = this.durationToLength(descriptionstartApPart.plannedStartMin
																				- innerbasetime) + 1;
								descriptionwidthIst = this.durationToLength(MINUTES_OF_DAY - descriptionstartApPart.plannedStartMin);
							}
						}
					}

					var shiptoCity = nvl([ currentAssignmentPart.shiptoCity, currentAssignmentPart.siteCity ]);
					
					var coordinatesOfCurrentAssignmentPart;
					if (currentAssignmentPart.coordLon) {
							coordinatesOfCurrentAssignmentPart = currentAssignmentPart.coordLon + ","  + currentAssignmentPart.coordLat;
					}
					else {
							coordinatesOfCurrentAssignmentPart = '';
					}
					
					var descriptionSoll = nvl([
							currentAssignmentPart.shiptoName,
							currentAssignmentPart.shippingpointName,
							currentAssignmentPart.siteName,
							coordinatesOfCurrentAssignmentPart])
							+ (shiptoCity ? (', ' + shiptoCity) : '');
					descriptionSoll = '<nobr>' + descriptionSoll + '</nobr>';
					
					var descriptionIst;
					var plantDesc = [];
					if (showMaterialInfo != 1) {
						descriptionIst = this.createMaterialDescription(assignment, currentPart);
					} else {
						if (assignmentPartPlantCd) {
							plantDesc[plantDesc.length] = {
								tag : 'span',
								cls : 'plantCodeLeft',
								html : assignmentPartPlantCd
							};

							assignmentPartPlantCd = '';
						}

						if (assignmentPartReturningPlantCd
								&& assignmentPartReturningPlantCdDisplayPosition == i) {
							plantDesc[plantDesc.length] = {
								tag : 'span',
								cls : 'plantCodeRight',
								html : assignmentPartReturningPlantCd
							};

							assignmentPartReturningPlantCd = '';
						}
						plantDesc[plantDesc.length] = {
							tag : 'span',
							cls : 'plantCode clear',
							html : '&nbsp;'
						};

					}

					auftragbalken.children[auftragbalken.children.length] = {
						tag : 'div',
						cls : 'auftragInnerSollIst',
						html : descriptionSoll,
						style : 'z-index:1;left:' + descriptionleftSoll
								+ 'px; width:' + descriptionwidthSoll
								+ 'px;top: 0px; height:14px;'
					};

					if (showMaterialInfo != 1) {
						auftragbalken.children[auftragbalken.children.length] = {
							tag : 'div',
							cls : 'auftragInnerSollIst',
							html : descriptionIst,
							style : 'z-index:1;left:' + descriptionleftIst
									+ 'px; width:' + descriptionwidthIst
									+ 'px;top:15px; height:14px;'
						};
					} else {
						auftragbalken.children[auftragbalken.children.length] = {
							tag : 'div',
							cls : 'auftragInnerSollIst',
							html : descriptionIst,
							style : 'z-index:1;left:' + descriptionleftIst
									+ 'px; width:' + descriptionwidthIst
									+ 'px;top:15px; height:14px;',
							children : plantDesc
						};
					}

					auftragbalken.children[auftragbalken.children.length] = {
						tag : 'div',
						id : isReal ? 'hdraghandle'
								+ currentAssignmentPart.orderId
								: 'readonlyhraghandle',
						cls : 'hDragHandle',
						style : 'left:'
								+ (this
										.durationToLength(currentPart.plannedStartMin
												- innerbasetime) - 15) + 'px'
					};

					var deliveryFrom = currentAssignmentPart.deliveryFromDat;
					deliveryTo = currentAssignmentPart.deliveryToDat;

					if (assignment.assignmentTypeCd == 1) {
						deliveryFrom = getDispoDateMorning();
						deliveryTo = getDispoDateMidnight();
					}
					var spanleft = this.durationToLength(dateToMinutes(deliveryFrom));
					var spanwidth = this.durationToLength(dateToMinutes(deliveryTo) - dateToMinutes(deliveryFrom));
					if (assignment.assignmentTypeCd === 0) {
						var spanbalken = {
							tag : 'div',
							cls : 'auftragSpan',
							style : "width:" + spanwidth + "px;left:"
									+ spanleft + "px; top:" + (asstop - 2)
									+ "px;",
							id : isReal ? 'span'+ currentAssignmentPart.assignmentPartId
									: 'readonlyspan' + currentAssignmentPart.assignmentPartId
						};
						span = Ext.DomHelper.append(parent, spanbalken);
					}

					outerbasetime = Math.max(outerbasetime,
											trimMinutes(dateToMinutes(deliveryFrom) 
														- currentPart.plannedStartMin
														+ firstPart.plannedStartMin));
					outerendtime = Math.min(outerendtime,
											trimMinutes(dateToMinutes(deliveryTo)
														- currentPart.plannedStartMin
														+ firstPart.plannedStartMin));
				}
			}
		}

		outerduration = outerendtime - outerbasetime;

		var domTree = {
			tag : 'div',
			cls : 'auftragOuter',
			style : "width:" + (asswidth + 4) + "px;left:" + assleft
					+ "px;top:" + (asstop + 8) + "px",
			children : auftragbalken
		};

		this.el = Ext.get(Ext.DomHelper.append(parent, domTree));

		parent.assignmentList[parent.assignmentList.length] = this.el.dom;

		this.el.dom.span = span;
		this.el.dom.assignment = data;
		this.el.dom.assignmentObject = this;
		this.el.dom.assSollLeft = assSollLeft;
		this.el.dom.assIstLeft = assIstLeft;
		this.el.dom.assSollWidth = assSollWidth;
		this.el.dom.assIstWidth = assIstWidth;

		if (!isReal) {
			// skip adding drag and drop handles, mouseoverlisteners,errors,
			// doubleclick listener, single click listener, etc.
			// if this attribute is false, this means that the assignment is
			// only a duplicate of another, rendered on a planning truck also
			return;
		}

		if (showScheduleDetails) {
			if (errorType != ERROR_NONE) {
				var err = Ext.query("img[class='auftragError']", this.el.dom)[0];
				err.style.visibility = 'visible';
				err.errorType = errorType;
			}
		}

		this.iel = Ext.get("ai" + assignment.assignmentId);

		lastMouseOverAssignmentId = null;
		currMouseOverAssignmentId = null;

		var oel;

		if (showAssignmentPopup) {
			var popup = buildAssignmentPopup(data) + buildNotesPopup(assignment);
			popup = popup.replace(/"/g, "&quot;");
			popup = "<div style='width:310px; max-width:310px;'>" + popup + '</div>';

			oel = Ext.query("div[class='dispoBarOverlay']", this.iel.dom)[0];
			oel.setAttribute("ext:qtip", popup);
			oel.setAttribute("ext:qclass", 'dispoqtip dispoinfoqtip');
			oel.setAttribute("isLate", data.isLate);
		}

		oel = Ext.get(Ext.query("div[class='dispoBarOverlay']", this.iel.dom)[0]);
		oel.on('mouseover', auftragMouseoverHandler);
		oel.on('mouseout', auftragMouseoutHandler);

		var del = Ext.query("div[class='hDragHandle']", this.iel.dom);
		for (i = 0; i < del.length; i++) {
			Ext.get(del[i]).on('mouseover', auftragMouseoverHandler);
			Ext.get(del[i]).on('mouseout', auftragMouseoutHandler);
		}

		this.startAcceptPlanning = isAssignmentOnPlanningTruck(data.assignment)
										&& authcarrier 
										&& (authpool || highestOrderStatus == 7);
		this.normalScheduling = authcarrier 
									&& (!isAssignmentOnPlanningTruck(data.assignment) 
											|| !data.isPreschedulingRequired);
		this.planningScheduling = (authpool 
									&& authcarrier 
									&& isAssignmentOnPlanningTruck(data.assignment));

		// this is here when the assignment can be initialized for drag and drop
		var isAssignmentDraggable = highestAssignmentPartStatus === 0 
										&& highestOrderStatus < 7
										&& assignment.assignStatusCd === ASSIGNMENT_STATUS_NEW 
										&& assignment.assignTrStatusCd === 0;
		var isSchedulingAssignment = this.planningScheduling || this.normalScheduling;
		
		if (!this.locked && (this.startAcceptPlanning 
								|| (isAssignmentDraggable && isSchedulingAssignment) ) ) {
			this.dd = this.el.initDD("assign" + assignment.assignmentId, null, dispoAuftragOverrides);
			this.dd.constrainTo(Ext.get(idPrefix + "Table"), null, false);
			this.dd.leftLimit = this.durationToLength(outerbasetime);
			this.dd.rightLimit = this.durationToLength(outerendtime);
			this.dd.assWidth = asswidth;
			this.dd.isTarget = false;
			this.dd.hourWidth = parent.hourWidth;
			// idPrefix is holding the value on which part of the dispo area is
			// the assignment placed. (dispo or planning)
			this.dd.idPrefix = idPrefix;
			if (innerduration != outerduration) {
				this.iel.addClass("movelr");
				for (i = 0; i < lengthOfParts - 1; i++) {
					currentAssignmentPart = parts[i];
					if (currentAssignmentPart.assignmentPartTypeCd == ASSIGNMENT_PART_TYPE_UNLOAD) {
						Ext.getDom('hdraghandle' + currentAssignmentPart.orderId).partNr = i;
					}
				}
			}
		}
		this.isClickable = false;
		if (this.planningScheduling || this.normalScheduling
				|| this.startAcceptPlanning) {
			this.iel.on("click", this.auftragOnClickHandler);
			this.isClickable = true;
		}
		if (!this.locked && (this.planningScheduling || this.normalScheduling)) {
			this.iel.on("dblclick", this.auftragOnDbClickHandler);
		}
		// show error markers and provide possibility to resolve the problem
		if (!this.isExternalLocked) {
			this.setErrorMark(dispo.getPrevAssignment(parent,
					this.el.dom.offsetLeft + this.iel.dom.offsetLeft), parent);
		}
	};

	this.auftragOnDbClickHandler = function(e, target) {
		if (target.className == 'auftragError') {
			return; // don't show if doubleclick is on error mark, as it might
			// interfere with conflict resolution
		}
		if (dispo.selection[0] !== null) {
			showPopup('AssignmentEditEntry.do', null, {
				assignmentId : dispo.selection[0]
			});
		}
	};

	this.auftragOnClickHandler = function(e, target) {
		if (target.className == 'selectionBorder') {
			return; // already selected
		}
		if (target.className == 'auftragError') {
			var assData = target.parentNode.parentNode.assignment;
			//Handle onholderOrders
			if(assData.isOnHoldOrderAndLockedFlag === true) {
				return;
			}
			if (target.errorResolving) {
				return;
			}
			switch (target.errorType) {
			case ERROR_ROUTE_CONFLICT:
				var prev = dispo.getPrevAssignment(parent,
						target.parentNode.offsetLeft
								+ target.parentNode.parentNode.offsetLeft);
				if (prev !== null) {
					var prevFData = prev.assignment.parts[prev.assignment.parts.length - 1].assignmentPart;
					var prevUData = prev.assignment.parts[prev.assignment.parts.length - 2].assignmentPart;
					var sData = assData.parts[0].assignmentPart;

					if (!prev.assignmentObject.locked
							&& (prevFData.realStart === null)
							&& (prevUData.realStart === null)
							&& (sData.assignmentPartTypeCd == 1)) {
						target.errorResolving = true;
						DispoAjaxService.setPrevAssignmentReturnShippingpoint(
								prev.assignment.assignment.assignmentId,
								assData.assignment.assignmentId,
								refreshAssignments);
					}
					// TODO: special case if return assignment is not finished
					else if (!target.parentNode.parentNode.assignmentObject.locked
							&& (sData.realStart === null)) {
						target.errorResolving = true;
						if (sData.assignmentPartTypeCd === 0) {
							sData = assData.parts[1].assignmentPart;
						}

						DispoAjaxService.setAssignmentPrePosition(
								prev.assignment.assignment.assignmentId,
								assData.assignment.assignmentId,
								refreshAssignments);

					} else {
						if (errortexts && errortexts.cantResolveRouteConflict) {
							Logon.message
									.error(errortexts.cantResolveRouteConflict);
						}
					}
				}
				break;
			case ERROR_LOAD:
				var load = null;
				var i;
				if (assData.assignment.assignmentTypeCd === 0) {
					for (i = 0; i < parent.loads.length; i++) {
						load = parent.loads[i];
						break;
					}

					if (load && !load.loadTime) {
						var item = null;
						var order = null;
						for (i = 0; i < assData.parts.length; i++) {
							if (assData.parts[i].assignmentPart.orderId) {
								if (assData.parts[i].orderItems) {
									if ((order !== null)
											&& (order != assData.parts[i].assignmentPart.orderId)) {
										item = null;
										break;
									}
									order = assData.parts[i].assignmentPart.orderId;

									if (assData.parts[i].orderItems.length > 1) {
										item = null;
										break;
									}
									item = assData.parts[i].orderItems[0];
								}
							}
						}
						if (item !== null) {
							if ((load.sapMaterialId == item.sapMaterialId)
									&& (load.productionPlantCd == item.productionPlantCd)) {
								target.errorResolving = true;
								DispoAjaxService.takeoverLoad(
										assData.assignment.assignmentId,
										refreshAssignments);
							}
							break;
						}
					}
				}

				if (assData.assignment.assignTrStatusCd === 0) {
					for (i = 0; i < parent.loads.length; i++) {
						load = parent.loads[i];
						break;
					}
					if (!load) {
						break;
					}
					var otherAssDom = Ext.getDom('ai' + load.assignmentId);
					if (otherAssDom) {
						var otherAssData = otherAssDom.parentNode.assignment;
						var diff = otherAssData.parts[otherAssData.parts.length - 1].plannedStartMin
								- assData.parts[0].plannedStartMin;
						for (i = 0; i < assData.parts.length; i++) {
							if (assData.parts[i].assignmentPart.assignmentPartTypeCd == 3) {
								target.errorResolving = true;
								DispoAjaxService
										.setAssignmentDeliveryTimeMinutes(
												assData.assignment.assignmentId,
												assData.parts[i].assignmentPart.orderId,
												assData.parts[i].plannedStartMin
														+ diff + 2,
												refreshAssignments);
								break;
							}
						}
					} else {
						if (errortexts && errortexts.cantResolveLoadConflict) {
							Logon.message
									.error(errortexts.cantResolveLoadConflict);
						}
					}
				}
				break;
			case ERROR_ASSIGNMENT_NOT_TRANSMITTED:
				target.errorResolving = true;
				DispoAjaxService
						.transmitToDriver([ assData.assignment.assignmentId ],
								refreshAssignments);
				break;
			}
			return; // don't change selection
		}
		dispo
				.setSelection(findParentAssignment(target).assignment.assignment.assignmentId);
	};

	this.resetSelection = function() {
		list = Ext.query("div[class='selectionBorder']", this.iel.dom);
		if (list.length > 0) {
			Ext.get(list[0]).remove();
		}

		this.iel.dom.className = "auftragInner";

	};

	this.setSelection = function() {
		if (this.locked) {
			return;
		}
		selectionBorder = {
			tag : 'div',
			cls : 'selectionBorder',
			style : "width:" + this.iel.dom.clientWidth + "px;left:"
					+ this.iel.dom.clientLeft + "px"
		};
		Ext.DomHelper.append(this.iel, selectionBorder);

		this.iel.dom.className = "auftragInnerSelected";

	};

	this.canCancel = function() {
		if (this.locked) {
			return false;
		}
		var ass = this.el.dom.assignment;
		for ( var i = 0; i < ass.parts.length; i++) {
			if (ass.assignment.assignmentTypeCd == ASSIGNMENT_TYPE_EXTERNAL) {
				if (ass.parts[i].assignmentPart.assignmentPartTypeCd == ASSIGNMENT_PART_TYPE_FROM
						&& ass.parts[i].assignmentPart.realStart === null) {
					return true;
				}
			} else {
				/*
				 * LOGON-5629 LRQ 073.2 StartLoading does not Block Deallocation
				 * Functionality Check if the order is loading, and disable
				 * deallocation.
				 */
				if (ass.parts[i].assignmentPart.orderStatusCd !== null
						&& ass.parts[i].assignmentPart.orderStatusCd < ORDER_STATUS_DELIVERED
						&& ass.parts[i].assignmentPart.orderStatusCd != ORDER_STATUS_LOADING) {
					return true;
				}
			}
		}

		return false;
	};

	this.canTransmit = function() {
		// if it is not clickable at all, then transmit cannot happen either
		if (!this.isClickable) {
			return false;
		}
		return !this.locked
				&& !this.el.dom.assignment.isOnHoldOrderAndLockedFlag
				&& this.el.dom.assignment.assignment.assignTrStatusCd === 0;
	};

	this.canSetAtShipto = function() {
		if (this.locked) {
			return false;
		}
		var ass = this.el.dom.assignment;
		for ( var i = 0; i < ass.parts.length; i++) {
			if (ass.assignment.assignmentTypeCd === 0) {
				if ((ass.parts[i].assignmentPart.orderStatusCd === ORDER_STATUS_ARRIVED_AT_DELIVERY_POINT)
						|| (ass.parts[i].assignmentPart.orderStatusCd === ORDER_STATUS_UNLOADING)) {
					return false;
				}
				if ((ass.parts[i].assignmentPart.orderStatusCd === ORDER_STATUS_LOADED)
						|| (ass.parts[i].assignmentPart.orderStatusCd === ORDER_STATUS_SHIPPING)) {
					return true;
				}
			} else {
				if ((ass.parts[i].assignmentPart.assignmentPartTypeCd == 3)
						&& (ass.parts[i].assignmentPart.realStart === null)
						&& (ass.assignment.assignTrStatusCd == 1)) {
					return true;
				}
			}
		}
		return false;
	};

	this.canSetUnloaded = function() {
		if (this.locked) {
			return false;
		}
		var ass = this.el.dom.assignment;
		for ( var i = 0; i < ass.parts.length; i++) {
			if (ass.assignment.assignmentTypeCd === 0) {
				if ((ass.parts[i].assignmentPart.orderStatusCd === ORDER_STATUS_ARRIVED_AT_DELIVERY_POINT)
						|| (ass.parts[i].assignmentPart.orderStatusCd === ORDER_STATUS_UNLOADING)) {
					return true;
				}
			} else {
				if ((ass.parts[i].assignmentPart.assignmentPartTypeCd == 3)
						&& (ass.parts[i].assignmentPart.realStart !== null)
						&& (ass.parts[i + 1].assignmentPart.realStart === null)) {
					return true;
				}
			}
		}
		return false;
	};

	this.canSetReturned = function() {
		if (this.locked) {
			return false;
		}
		var ass = this.el.dom.assignment;
		var hasReturn = false;
		var unfinished = false;
		for ( var i = 0; i < ass.parts.length; i++) {
			if (ass.parts[i].assignmentPart.assignmentPartTypeCd != 5) {
				if (ass.parts[i].assignmentPart.realStart === null) {
					return false;
				}
			}
			if (ass.parts[i].assignmentPart.assignmentPartTypeCd == 4) {
				hasReturn = true;
			}
			if (ass.parts[i].assignmentPart.assignmentPartTypeCd == 5) {
				unfinished = (ass.parts[i].assignmentPart.realStart === null);
			}
		}
		return hasReturn && unfinished;

	};

	this.canLoadAtCarrierStock = function() {
		if (this.locked) {
			return false;
		}
		var ass = this.el.dom.assignment;
		if (!ass.isBag) {
			return false;
		}
		if (ass.assignment.bagStockShippingpointId === null) {
			return false;
		}
		var c = Ext.get('cd' + ass.assignment.truckId);
		if (c.dom.camion.pdaAvailableFlag) {
			return false;
		}
		var canLoad = false;
		var aboveDispatched = false;
		for ( var i = 0; i < ass.parts.length; i++) {
			if ((ass.parts[i].assignmentPart.orderStatusCd === ORDER_STATUS_RELEASED)
					|| (ass.parts[i].assignmentPart.orderStatusCd === ORDER_STATUS_CONFIRMED_BY_DRIVER)
					|| (ass.parts[i].assignmentPart.orderStatusCd === ORDER_STATUS_ARRIVED_AT_LOADINGPOINT)) {
				aboveDispatched = true;
			}
			if (ass.parts[i].assignmentPart.assignmentPartTypeCd == 1
					&& ass.parts[i].assignmentPart.realStart === null) {
				canLoad = true;
			}
		}
		return aboveDispatched && canLoad;

	};

	this.canEnterShippingpoint = function() {
		if (this.locked) {
			return false;
		}
		var ass = this.el.dom.assignment;

		if (isAssignmentOnPlanningTruck(ass.assignment)) {
			return false;
		}
		// var c = Ext.get('cd'+ass.assignment.truckId);
		// if ( c.dom.camion.pdaAvailableFlag ) {
		// return false;
		// }

		for ( var i = 0; i < ass.parts.length; i++) {
			if ((ass.parts[i].assignmentPart.orderStatusCd === ORDER_STATUS_RELEASED)
					|| (ass.parts[i].assignmentPart.orderStatusCd === ORDER_STATUS_CONFIRMED_BY_DRIVER)) {
				return true;
			}
		}
		return false;

	};

	this.canRerouteSelection = function() {
		if (this.locked) {
			return false;
		}
		var ass = this.el.dom.assignment;

		if (isAssignmentOnPlanningTruck(ass.assignment)) {
			return false;
		}

		return true;
	};

	this.haveSameLocation = function(ap1, ap2) {
		return (ap1.assignmentPart.shippingpointId == ap2.assignmentPart.shippingpointId)
				&& (ap1.assignmentPart.shiptoId == ap2.assignmentPart.shiptoId)
				&& coordsEqual(ap1.assignmentPart.coordLat,
						ap1.assignmentPart.coordLon,
						ap2.assignmentPart.coordLat,
						ap2.assignmentPart.coordLon);
	};

	this.setErrorMark = function(prev, truck) {
		var error = false;
		var errorText = "";
		var errorType = ERROR_NONE;
		var i;
		if (prev) {
			if (prev.order === undefined) {
				error = !this
						.haveSameLocation(
								prev.assignment.parts[prev.assignment.parts.length - 1],
								data.parts[0]);
			} else {
				error = (prev.order.shippingpointId != data.parts[0].assignmentPart.shippinpointId);
			}
			if (error) {
				errorText = errortexts.routeConflict;
				errorType = ERROR_ROUTE_CONFLICT;
			}
		}
		if (truck) {
			if (data.assignment.assignStatusCd < ASSIGNMENT_STATUS_RETURNING) {
				for (i = 0; i < truck.loads.length; i++) {
					var load = truck.loads[i];
					if ((load.assignmentId != data.assignment.assignmentId)
							&& (load.sapMaterialId !== null)
							&& ((load.assignmentId === null) || (load.loadTime > data.parts[0].assignmentPart.plannedStart))) {
						error = true;
						errorText = errortexts.wrongLoad;
						errorType = ERROR_LOAD;
					}
				}
			}
			if ((errorType == ERROR_NONE) && truck.unavailabilities) {
				for (i = 0; i < truck.unavailabilities.length; i++) {
					var un = truck.unavailabilities[i];
					var start = nvl([ data.parts[0].assignmentPart.realStart,
							data.parts[0].assignmentPart.realStart ]);
					if ((un.startDat.getTime() < data.parts[data.parts.length - 1].assignmentPart.plannedStart
							.getTime())
							&& (un.endDat.getTime() > data.parts[0].assignmentPart.plannedStart
									.getTime())) {
						error = true;
						errorText = errortexts.truckUnavailable;
						errorType = ERROR_TRUCK_UNAVAILABLE;
						break;
					}
				}
			}
		}
		if (dispodateIsToday() && (data.assignment.assignTrStatusCd == 1)) {
			var arrtime = null;
			var psLoad = null;
			for (i = 0; i < data.parts.length; i++) {
				if (data.parts[i].assignmentPart.assignmentPartTypeCd == 1) {
					if ((psLoad === null)
							|| (psLoad.getTime() > data.parts[i].assignmentPart.plannedStart
									.getTime())) {
						psLoad = data.parts[i].assignmentPart.plannedStart;
						if (data.parts[i].assignmentPart.railArrivalWarningMin) {
							arrtime = psLoad
									.add(
											Date.MINUTE,
											-data.parts[i].assignmentPart.railArrivalWarningMin);
						}
					}
				}
			}
			if (arrtime && (arrtime.getTime() < (getRealCurrentDate()).getTime())) {
				for (i = 0; i < data.parts.length; i++) {
					if (data.parts[i].assignmentPart.deliveryTypeCd == 4) {

						for ( var j = 0; j < data.parts[i].orderItems.length; j++) {
							var cd = data.parts[i].orderItems[j].brOrderStatusCd;
							if (cd === null) {
								error = true;
								errorText = errortexts.wagonNotArrived;
								errorType = ERROR_WAGON_NOT_ARRIVED;
								break;
							} else if ((cd < 13) || (cd > 16)) {
								error = true;
								errorText = errortexts.wagonNotArrived;
								errorType = ERROR_WAGON_NOT_ARRIVED;
								break;
							}
						}

					}
				}
			}
		}

		var err = Ext.query("img[class='auftragError']", this.iel.dom)[0];
		if (error) {
			err.style.visibility = 'visible';
			err.setAttribute("ext:qtip", errorText);
			err.errorType = errorType;
		}
	};

	this.draw();
}

function setOrderPoolOrCommCarrier(ordersListDivId, combinedPoolCommCarrierId) {
	if (combinedPoolCommCarrierId == "-") {
		return;
	}
	var commCarrSel = Ext.get(ordersListDivId);
	var orderslist = commCarrSel.dom.innerHTML;
	if (combinedPoolCommCarrierId.indexOf("pool") != -1) {
		var poolId = combinedPoolCommCarrierId.replace("pool", "");
		DispoAjaxService.setOrderPool(orderslist, poolId, refreshOrders);
	} else {
		DispoAjaxService.setOrderCommCarrier(orderslist,
				combinedPoolCommCarrierId, refreshOrders);
	}
}
function setOrderExecCarrier(ordersListDivId, execCarrierId) {
	var commCarrSel = Ext.get(ordersListDivId);
	var orderslist = commCarrSel.dom.innerHTML;
	DispoAjaxService.setOrderExecCarrier(orderslist, execCarrierId,
			refreshOrders);
}

// keep outside to avoid collapse on reload
var nonCollapsedOrders = [];
function OrderList(parent, startHour, endHour, custorders) {

	// Collapse or expand group of like deliveries
	// Assumes that both the number of the order and the id of the order
	// are set as attributes on the element where the event originates.
	this.toggleDeliveryCollapsed = function(elem) {
		var orderId = elem.getAttribute('data-orderid');
		var found = false;
		ordersOfGroup = this.getOrdersOfGroup(orderId);
		for ( var i = 0; i < nonCollapsedOrders.length; i++) {
			if (nonCollapsedOrders[i] == orderId) {
				nonCollapsedOrders.splice(i, 1);
				nonCollapsedOrders = substract(nonCollapsedOrders, ordersOfGroup);
				found = true;
				break;
			}
		}
		if (!found) {
			nonCollapsedOrders.push(orderId);
			//LOGON-6991: when expanding a group, add all orders in group to unCollapsedOrders so that newly formed group will be expanded.
			nonCollapsedOrders = merge(nonCollapsedOrders, ordersOfGroup);
		}

		this.draw();
	}
	
	//-------------------------------------

	function areOrdersIdentical(order1, order2) {
		var currentOrder = order2.order;
		var prevOrder = order1.order;
		// Checking the attributes of the previous and the current
		// orders if these are identical
		var isInternalOrder = !order2.externalOrder;
		var isDeliveryFromToIdentical = currentOrder.deliveryFromDat
				.getTime() == prevOrder.deliveryFromDat.getTime()
				&& currentOrder.deliveryToDat.getTime() == prevOrder.deliveryToDat
						.getTime();
		var isPoolIdentical = (currentOrder.poolId === null && prevOrder.poolId === null)
				|| (currentOrder.poolId == prevOrder.poolId);
		var isCommCarrierIdentical = (currentOrder.commCarrierId === null && prevOrder.commCarrierId === null)
				|| (currentOrder.commCarrierId == prevOrder.commCarrierId);
		var isExecCarrierIdentical = (currentOrder.execCarrierId === null && prevOrder.execCarrierId === null)
				|| (currentOrder.execCarrierId == prevOrder.execCarrierId);
		var isShippingpointIdentical = (currentOrder.shippingpointId === null && prevOrder.shippingpointId === null)
				|| (currentOrder.shippingpointId == prevOrder.shippingpointId);
		var isOrderStatusCodeIdentical = (currentOrder.orderStatusCd === null && prevOrder.orderStatusCd === null)
				|| (currentOrder.orderStatusCd === prevOrder.orderStatusCd);

		// Check if both orders have the same number of the
		// orderItems
		var currentOrderItems = order2.items;
		var prevOrderItems = order1.items;
		var isOrderItemCountIdentical = currentOrderItems.length == prevOrderItems.length;

		// Check if both orders have the same materials (assume
		// order is the same if identical)
		var areOrderItemMaterialsIdentical = false;
		if (isOrderItemCountIdentical) {
			areOrderItemMaterialsIdentical = true;
			for (ii = 0; ii < currentOrderItems.length; ii++) {
				if ((currentOrderItems[ii].sapMaterialId !== prevOrderItems[ii].sapMaterialId)
						|| (currentOrderItems[ii].uomName !== prevOrderItems[ii].uomName)
						|| (currentOrderItems[ii].orderedQuantity !== prevOrderItems[ii].orderedQuantity)) {
					areOrderItemMaterialsIdentical = false;
					break;
				}
			}
		}

		isOrdersIdentical = isInternalOrder
				&& isDeliveryFromToIdentical && isPoolIdentical
				&& isCommCarrierIdentical && isExecCarrierIdentical
				&& isShippingpointIdentical
				&& isOrderItemCountIdentical
				&& areOrderItemMaterialsIdentical
				&& isOrderStatusCodeIdentical;
		
		return isOrdersIdentical;
	}
	
	this.getSameOrders = function(custorderIdx, orderIdx) {
		var custorder = this.custorders[custorderIdx];
		var order = custorder.orderWrappers[orderIdx];
		var result = []
		for(var i = orderIdx + 1; i < custorder.orderWrappers.length; i++) {
			order2 = custorder.orderWrappers[i];
			if(areOrdersIdentical(order, order2)) {
				result.push(order2.id);
			} else {
				break;
			}
		}
		return result;
	}
	
	this.getOrdersOfGroup = function(orderId) {
		var custorderIdx = 0;
		var orderIdx = 0;
		var found = false;
		for(custorderIdx = 0; custorderIdx < this.custorders.length; custorderIdx++) {
			wrapOrders = this.custorders[custorderIdx].orderWrappers;
			if(wrapOrders) {
				for(orderIdx = 0; orderIdx < wrapOrders.length; orderIdx++) {
					if(wrapOrders[orderIdx].id == orderId) {
						found = true;
						break;
					}
				}
				if(found) {
					break;
				}
			}
		}
		return this.getSameOrders(custorderIdx, orderIdx);
	}

	//---------------------------------------------

	/** Stores the list of orders for later re-drawing the orders section. */
	this.custorders = custorders;

	this.selection = orderList ? orderList.selection : null;

	this.wrapOrderTitle = function(line1, line2, line1tt, line2tt, elwidth) {
		var escapedLine1 = line1 ? line1.replace(/"/g, "&quot;") : '';
		var escapedLine2 = line2 ? line2.replace(/"/g, "&quot;") : '';
		var escapedLine1tt = line1tt ? line1tt.replace(/"/g, "&quot;")
				: escapedLine1;
		var escapedLine2tt = line2tt ? line2tt.replace(/"/g, "&quot;")
				: escapedLine2;
		tm1 = Ext.util.TextMetrics.measure('ohour0', line1);
		tm2 = Ext.util.TextMetrics.measure('ohour0', line2);
		ret = [
				{
					tag : 'div',
					style : 'position:relative;width:'
							+ Math.min(elwidth - 2, tm1.width)
							+ 'px;height:15px;overflow:hidden;left:'
							+ Math.floor((elwidth - 2 - Math.min(elwidth - 2,
									tm1.width)) / 2) + 'px',
					children : [ {
						tag : 'nobr',
						style : 'position:absolute;left:0px',
						'ext:qtip' : escapedLine1tt,
						'ext:qclass' : 'dispoqtip dispoinfoqtip',
						html : line1
					} ]
				},
				{
					tag : 'div',
					style : 'position:relative;width:'
							+ Math.min(elwidth - 2, tm2.width)
							+ 'px;height:15px;overflow:hidden;left:'
							+ Math.floor((elwidth - 2 - Math.min(elwidth - 2,
									tm2.width)) / 2) + 'px',
					children : [ {
						tag : 'nobr',
						style : 'position:absolute;left:0px',
						'ext:qtip' : escapedLine2tt,
						'ext:qclass' : 'dispoqtip dispoinfoqtip',
						html : line2
					} ]
				} ];
		return ret;
	};

	this.buildCommCarrierOptions = function(orderCarrierId, options) {
		var initialSize = options.length;
		var j = 0;
		var commCarrierPresent = false;
		for ( var i = 0; i < currentCarriers.items.length; i++) {
			if (!Ext.isEmpty(orderCarrierId)
					&& currentCarriers.items[i].carrierId == orderCarrierId) {
				options[initialSize + j] = {
					tag : 'option',
					value : currentCarriers.items[i].carrierId,
					html : currentCarriers.items[i].carrierName
				};
				options[initialSize + j].selected = "selected";
				commCarrierPresent = true;
				j++;
			} else if (currentCarriers.items[i].vnIsCommCarrier) {
				options[initialSize + j] = {
					tag : 'option',
					value : currentCarriers.items[i].carrierId,
					html : currentCarriers.items[i].carrierName
				};
				j++;
			}
			if (!Ext.isEmpty(orderCarrierId) && !commCarrierPresent) {
				options[initialSize + j] = {
					tag : 'option',
					value : orderCarrierId,
					html : 'Invalid ' + orderCarrierId
				};
				options[initialSize + j].selected = "selected";
			}
		}
		return options;
	};

	this.buildExecCarrierOptions = function(orderExecCarrierId, options,
			orderCommCarrierId) {
		var initialSize = options.length;
		var j = 0;
		var execCarrierPresent = false;
		var commcarrier = currentCarriers.itemAt(currentCarriers.findIndex(
				'carrierId', orderCommCarrierId));
		for ( var i = 0; i < commcarrier.execCarrierPartners.length; i++) {
			options[initialSize + j] = {
				tag : 'option',
				value : commcarrier.execCarrierPartners[i].execCarrierId,
				html : commcarrier.execCarrierPartners[i].execCarrierDisplayName
			};
			if (!Ext.isEmpty(orderExecCarrierId)
					&& commcarrier.execCarrierPartners[i].execCarrierId == orderExecCarrierId) {
				options[initialSize + j].selected = "selected";
				execCarrierPresent = true;
			}
			j++;
		}
		// if the executing carrier is invalid already, it should render that it
		// is set, but invalid
		if (!Ext.isEmpty(orderExecCarrierId) && !execCarrierPresent) {
			options[initialSize + j] = {
				tag : 'option',
				value : orderExecCarrierId,
				html : 'Invalid ' + orderExecCarrierId
			};
			options[initialSize + j].selected = "selected";
		}
		return options;
	};

	this.buildPoolOptions = function(orderPoolId, orderCarrierId, options) {
		var initialSize = options.length;
		for ( var i = 0; i < currentPoolOptions.length; i++) {
			options[initialSize + i] = {
				tag : 'option',
				value : "pool" + currentPoolOptions[i].poolId,
				html : currentPoolOptions[i].poolName
			};
			if (Ext.isEmpty(orderCarrierId)
					&& currentPoolOptions[i].poolId == orderPoolId) {
				options[initialSize + i].selected = "selected";
			}
		}
		return options;
	};

	this.buildLoadingpoints = function(order) {
		var res = '';
		var comma = '';
		var i;
		if (order.order.deliveryTypeCd == 4) {
			res = texts.Railcar + "&nbsp;:";
			for (i = 0; i < order.distinctWagonNumbers.length; i++) {
				res += comma + order.distinctWagonNumbers[i];
				comma = ',&nbsp;';
			}
		} else {
			var lplist = {};
			for (i = 0; i < order.items.length; i++) {
				var name = order.items[i].loadingpointName;
				if (name) {
					if (!lplist[name]) {
						res += comma + name;
						comma = ',&nbsp;';
						lplist[name] = true;
					}
				}
			}
		}
		return res;
	};

	/** Creates the truck type info object for display in the quicktip. */
	this.buildTruckTypeInfo = function(truckTypeList) {
		var tableRows = [];

		for ( var i = 0; i < truckTypeList.length; i++) {
			var truckType = truckTypeList[i];
			var truckTypeName = truckType.truckTypeNameTx;
			var priority = truckType.evaluationPriority;

			tableRows[i] = {
				tag : 'tr',
				children : [ {
					tag : 'td',
					html : truckTypeName
				} ]
			};
		}

		var table = {
			tag : 'table',
			cls : 'truckTypeTable',
			children : [ {
				tag : 'thead',
				children : [ {
					tag : 'tr',
					children : [ {
						tag : 'th',
						html : cusOrdTexts.truckTypes
					} ]
				}, {
					tag : 'body',
					children : tableRows
				} ]
			} ]
		};

		return table;
	};

	this.durationToLength = function(duration) {
		var d = Math.round(this.hourwidth * duration / 60);
		return d;
	};

	this.draw = function() {
		var headings = [];
		var hourwidth = Math.round((Ext.get(parent).getWidth() - 500 - 17)
				/ (endHour - startHour));
		this.hourwidth = hourwidth;

		var overlapdivwidth = 'width:' + (parent.offsetWidth - 17)
				+ 'px;max-width:' + (parent.offsetWidth - 17)
				+ 'px;overflow:hidden';

		var titlecaption = {
			tag : 'div',
			cls : 'dispotablehead',
			style : 'width:500px;overflow:visible;',
			children : [ {
				tag : 'table',
				cls : 'dispotable',
				style : 'width:500px',
				children : [ {
					tag : 'thead',
					children : [
							{
								tag : 'tr',
								cls : 'custOrder',
								children : [
										{
											tag : 'th',
											style : 'max-width:90px;width:90px;border:none;overflow-x:visible;text-align:left',
											id : 'ordersTitleCell',
											children : [ {
												tag : 'div',
												style : 'position:relative;z-index:12;background:#888888;'
														+ overlapdivwidth,
												children : [ {
													tag : 'table',
													cls : 'dispotable',
													style : 'width:500px',
													children : [ {
														tag : 'thead',
														children : [ {
															tag : 'tr',
															cls : 'custOrder',
															children : [
																	{
																		tag : 'th',
																		html : '<nobr>'
																				+ cusOrdTexts.custOrdNr
																				+ '</nobr>',
																		style : 'max-width:90px;width:90px',
																		id : 'ordersTitleCell'
																	},
																	{
																		tag : 'th',
																		html : '<nobr>'
																				+ cusOrdTexts.soldTo
																				+ '</nobr>',
																		style : 'max-width:130px;width:130px'
																	},
																	{
																		tag : 'th',
																		html : '<nobr>'
																				+ cusOrdTexts.shiptoCityAddress
																				+ '</nobr>',
																		style : 'max-width:280px;width:280px',
																		colspan : '2'
																	},
																	{
																		tag : 'th',
																		html : '<nobr>'
																				+ cusOrdTexts.material
																				+ '</nobr>',
																		style : 'max-width:80px;width:80px'
																	},
																	{
																		tag : 'th',
																		html : '<nobr>'
																				+ cusOrdTexts.ordSched
																				+ '</nobr>',
																		style : 'max-width:80px;width:80px'
																	},
																	{
																		tag : 'th',
																		html : '<nobr>'
																				+ cusOrdTexts.truckTypes
																				+ '</nobr>',
																		style : 'max-width:80px;width:80px'
																	},
																	{
																		tag : 'th',
																		html : '<nobr>'
																				+ cusOrdTexts.qtyDlvr
																				+ '</nobr>',
																		style : 'max-width:80px;width:80px'
																	},
																	{
																		tag : 'th',
																		html : '<nobr>'
																				+ cusOrdTexts.loaded
																				+ '</nobr>',
																		style : 'max-width:80px;width:80px'
																	},
																	{
																		tag : 'th',
																		html : '<nobr>'
																				+ cusOrdTexts.open
																				+ '</nobr>',
																		style : 'max-width:80px;width:80px'
																	},
																	{
																		tag : 'th',
																		html : '<nobr>'
																				+ cusOrdTexts.lastDl
																				+ '</nobr>',
																		style : 'max-width:80px;width:80px'
																	},
																	{
																		tag : 'th',
																		html : '<nobr>'
																				+ cusOrdTexts.tol
																				+ '</nobr>',
																		style : 'max-width:80px;width:80px'
																	} ]
														} ]
													} ]
												} ]
											} ]
										},
										{
											tag : 'th',
											html : '<nobr></nobr>',
											style : 'max-width:130px;width:130px;border:none;'
										},
										{
											tag : 'th',
											html : '<nobr></nobr>',
											style : 'max-width:140px;width:140px;border:none;'
										},
										{
											tag : 'th',
											html : '<nobr></nobr>',
											style : 'max-width:140px;width:140px;border:none;'
										} ]
							},
							{
								tag : 'tr',
								cls : 'custOrder',
								children : [
										{
											tag : 'th',
											html : '<nobr>'
													+ cusOrdTexts.shipto
													+ '</nobr>',
											style : 'max-width:90px;width:90px',
											id : 'ordersTitleCell'
										},
										{
											tag : 'th',
											html : '<nobr>'
													+ cusOrdTexts.shippingPoint
													+ '</nobr>',
											style : 'max-width:130px;width:130px'
										},
										{
											tag : 'th',
											html : '<nobr>'
													+ cusOrdTexts.commCarr
													+ '</nobr>',
											style : 'max-width:140px;width:140px'
										},
										{
											tag : 'th',
											html : '<nobr>'
													+ cusOrdTexts.opCarr
													+ '</nobr>',
											style : 'max-width:140px;width:140px'
										} ]
							} ]
				} ]
			} ]
		};

		Ext.DomHelper.overwrite(parent, titlecaption);

		var hourtextwidth = Ext.util.TextMetrics.measure('ordersTitleCell',
				'00:00').width;
		for (i = 0; i < 24; i++) {
			headings[i] = {
				tag : 'th',
				html : formatHour(i, hourtextwidth < hourwidth - 4),
				style : 'width:' + hourwidth + 'px;',
				id : "ohour" + (i)
			};
		}

		var titlehours = {
			tag : 'div',
			style : 'overflow-y:visible;width:'
					+ (parent.offsetWidth - 500 - 17)
					+ 'px;left:500px;top:20px;',
			cls : 'dispotablehead',
			id : 'orderTitlehours',
			children : [ {
				tag : 'div',
				style : 'width:' + (hourwidth * 24) + 'px',
				children : [ {
					tag : 'table',
					cls : 'dispotable',
					style : 'width:' + (hourwidth * 24) + 'px',
					children : [ {
						tag : 'thead',
						children : [ {
							tag : 'tr',
							cls : 'custOrder',
							children : [ headings ]
						} ]
					} ]
				} ]
			} ]
		};

		var hoursdiv = Ext.DomHelper.append(parent, titlehours);

		var rows = [];
		var hourrows = [];
		var cnt = 0;

		var custordNr = '';
		var i;
		var k;
		var extOrder;

		for (i = 0; i < custorders.length; i++) {
			var hourcells = [];
			var houcellshidden = [];
			for (k = 0; k < 24; k++) {
				hourcells[k] = {
					tag : 'td',
					cls : 'hour',
					html : '&nbsp;',
					id : 'oc' + i + '_' + k,
					style : 'width:' + hourwidth + 'px;'
				};
				houcellshidden[k] = {
					tag : 'td',
					cls : 'hour',
					html : '&nbsp;',
					style : 'width:' + hourwidth + 'px;'
				};
			}
			var custordNrDisp = '-';
			var soldTo = '-';
			var shiptoCityAddress = '-';
			var shippingPoint = '';
			var material = '-';
			var tolerance = '-';
			var lastDelivery = '-';
			var truckTypes = '-';

			if (custorders[i].custorderViewEntity !== null) {
				custordNr = custorders[i].custorderViewEntity.custorderNumber;
				custordNrDisp = custordNr;
				soldTo = custorders[i].custorderViewEntity.soldtoName;
				shiptoCityAddress = custorders[i].custorderViewEntity.shiptoCityAddress;
				shippingPoint = custorders[i].custorderViewEntity.shippingpointName;
				var materialName = custorders[i].custorderViewEntity.materialName;
				if (custorders[i].custorderViewEntity.exactQuantityOrderTolerance) {
					tolerance = custorders[i].custorderViewEntity.exactQuantityOrderTolerance
							+ ' kg';
				}

				if (custorders[i].orderWrappers.length > 0) {
					var orderWrappers = custorders[i].orderWrappers;
					var lastOrderWrapper = orderWrappers[orderWrappers.length - 1];

					// Creating the truck type quicktip based on the last single
					// delivery order
					var truckTypeList = lastOrderWrapper.truckTypeList;

					var truckTypePopup = Ext.util.Format
							.htmlEncode(Ext.DomHelper.markup(this
									.buildTruckTypeInfo(truckTypeList)));
					var truckTypeLabel = truckTypeList.length == 1 ? truckTypeList[0].truckTypeNameTx
							: cusOrdTexts.multiple;

					truckTypes = '<div ext:qtip="' + truckTypePopup
							+ '" ext:qclass="dispoqtip dispoinfoqtip">'
							+ truckTypeLabel + '</div>';

					// Calculating last delivery
					if (custorders[i].custorderViewEntity.exactCustorderQtyFlag) {
						var firstItem = lastOrderWrapper.items[0];
						lastDelivery = firstItem.orderedQuantity + '&nbsp;'
								+ firstItem.uomName;
					}
				}

				if (custorders[i].sumQuantities === null) {
					// if sumQuantities is null info must visible only on popup,
					// because more than one or no material info is reachable
					materialName = '-';
				}
				material = {
					tag : 'div',
					html : materialName,
					style : 'position:relative;z-index:12;width:80px;height:17px;overflow:hidden;white-space:nowrap',
					'ext:qtip' : 'Material',
					'ext:qclass' : 'dispoqtip dispoinfoqtip',
					'custorderNr' : custordNr,
					'lastDelivery' : lastDelivery,
					cls : 'custorderMaterialInfo'
				};
			} else {
				// In case the order group holds external orders we display the
				// properties on eachs order line.
				if (custorders[i].orderWrappers[0].externalOrder) {
					extOrder = custorders[i].orderWrappers[0].extOrder;
					// The External orders are grouped by ShipTo so it makes
					// sense to use ShipTo ID for ID.
					custordNr = extOrder.shiptoId;
					shiptoCityAddress = extOrder.shiptoName;
				}
			}

			var schedOrderedQuantity = '-';
			var loadedQuantityInBaseUom = '-';
			var deliveredQuantityInBaseUom = '-';
			var openQuantityInBaseUom = '-';
			var custorderHeadClass = custorders[i].custorderHeadClass;

			if (custorders[i].sumQuantities !== null) {
				var sumQuantities = custorders[i].sumQuantities;

				var orderedQuantity = (sumQuantities.orderedQuantity !== null) ? sumQuantities.orderedQuantity
						: 0;
				var scheduledQuantityInBaseUom = (sumQuantities.scheduledQuantityInBaseUom !== null) ? sumQuantities.scheduledQuantityInBaseUom
						: 0;
				loadedQuantityInBaseUom = (sumQuantities.loadedQuantityInBaseUom !== null) ? sumQuantities.loadedQuantityInBaseUom
						: 0;
				deliveredQuantityInBaseUom = (sumQuantities.deliveredQuantityInBaseUom !== null) ? sumQuantities.deliveredQuantityInBaseUom
						: 0;
				openQuantityInBaseUom = (sumQuantities.openQuantityInBaseUom !== null) ? sumQuantities.openQuantityInBaseUom
						: 0;

				orderedQuantity = orderedQuantity + ' '
						+ sumQuantities.orderedUomTxt;
				scheduledQuantityInBaseUom = scheduledQuantityInBaseUom + ' '
						+ sumQuantities.baseUomTxt;
				loadedQuantityInBaseUom = loadedQuantityInBaseUom + ' '
						+ sumQuantities.baseUomTxt;
				deliveredQuantityInBaseUom = deliveredQuantityInBaseUom + ' '
						+ sumQuantities.baseUomTxt;
				openQuantityInBaseUom = openQuantityInBaseUom + ' '
						+ sumQuantities.baseUomTxt;

				schedOrderedQuantity = orderedQuantity + ' ('
						+ scheduledQuantityInBaseUom + ')';
			}

			var carrDivTop = 'top:2px;';

			var custorderrow = {
				tag : 'tr',
				cls : custorderHeadClass,
				id : 'trha' + i,
				style : 'height:21px;',
				custordernr : custordNr,
				children : [
				/* custOrdNr */{
					tag : 'td',
					html : '<nobr>' + custordNrDisp + '</nobr>',
					style : 'max-width:90px;width:90px',
					onclick : 'pingCustomerOrders(' + custordNr + ')'
				},
				/* soldTo */{
					tag : 'td',
					html : '<nobr>' + soldTo + '</nobr>',
					style : 'max-width:130px;width:130px'
				},
				/* shiptoCityAddress */{
					tag : 'td',
					html : '<nobr>' + shiptoCityAddress + '</nobr>',
					style : 'max-width:280px;width:280px',
					colspan : '2'
				},
				/* material */{
					tag : 'td',
					children : material,
					style : 'max-width:80px;width:80px'
				},
				/*
				 * orderedQuantity AND scheduledQuantityInBaseUom
				 */{
					tag : 'td',
					html : '<nobr>' + schedOrderedQuantity + '</nobr>',
					style : 'max-width:80px;width:80px'
				},
				/* truckTypes */{
					tag : 'td',
					html : truckTypes,
					style : 'max-width:80px;width:80px'
				},
				/* qtyDlvr */{
					tag : 'td',
					html : '<nobr>' + deliveredQuantityInBaseUom + '</nobr>',
					style : 'max-width:80px;width:80px'
				},
				/* loaded */{
					tag : 'td',
					html : '<nobr>' + loadedQuantityInBaseUom + '</nobr>',
					style : 'max-width:80px;width:80px'
				},
				/* openQuantityInBaseUom */{
					tag : 'td',
					html : '<nobr>' + openQuantityInBaseUom + '</nobr>',
					style : 'max-width:80px;width:80px'
				},
				/* lastDl */{
					tag : 'td',
					html : '<nobr>' + lastDelivery + '</nobr>',
					style : 'max-width:80px;width:80px'
				},
				/* tolerance */{
					tag : 'td',
					html : '<nobr>' + tolerance + '</nobr>',
					style : 'max-width:80px;width:80px'
				}, {
					tag : 'td',
					html : '&nbsp;'
				} ]
			};

			rows[cnt] = {
				tag : 'tr',
				cls : custorderHeadClass,
				id : 'trha' + i,
				style : 'max-height:21px;height:21px;overflow:visible',
				children : [
						{
							tag : 'td',
							style : 'position:relative;max-height:21px;height:21px;max-width:90px;width:90px;overflow:visible;border:none;text-align:left;vertical-align:top',
							children : [ {
								tag : 'div',
								style : 'position:relative; height:10px; width:10px; top:-1px; overflow:visible',
								children : [ {
									tag : 'div',
									style : 'position:relative;z-index:12;border:0px;left:0px;height:22px;'
											+ overlapdivwidth,
									children : [ {
										tag : 'table',
										cls : 'dispotable',
										style : 'position:absolute;width:500px;border:0px;height:21px;width:100%',
										children : [ {
											tag : 'tbody',
											children : custorderrow
										} ]
									} ]
								} ]
							} ]
						}, {
							tag : 'td',
							html : '<nobr></nobr>',
							style : 'max-width:130px;width:130px;border:none'
						}, {
							tag : 'td',
							html : '<nobr></nobr>',
							style : 'max-width:140px;width:140px;border:none'
						}, {
							tag : 'td',
							html : '<nobr></nobr>',
							style : 'max-width:140px;width:140px;border:none'
						}, {
							tag : 'td',
							html : '<nobr></nobr>',
							style : 'border:none'
						} ]
			};

			hourrows[cnt] = {
				tag : 'tr',
				id : 'tra' + i,
				style : 'height:21px;',
				children : houcellshidden
			};
			cnt++;

			rows[cnt] = {
				tag : 'tr',
				cls : 'order',
				id : 'trhb' + i,
				style : 'height:30px;vertical-align:text-top;',
				children : [
						{
							tag : 'td',
							html : '<div id="orderid_' + custordNr
									+ '" class="orderNrDiv"></div>',
							style : 'max-width:90px;width:90px'
						},
						/* shippingPoint */{
							tag : 'td',
							html : '<div id="shippingpointDiv_' + custordNr
									+ '" class="shippingpointDiv"><nobr>'
									+ shippingPoint + '</nobr></div>',
							style : 'max-width:130px;width:130px'
						},
						/* commCarr */{
							tag : 'td',
							html : '<div id="commCarrDivId_'
									+ custordNr
									+ '"  class="commCarrDiv" style="width:140px;height:17px;position:relative;-moz-box-sizing:border-box;text-align:left;'
									+ carrDivTop + '"></div>',
							style : 'max-width:140px;width:140px'
						},
						/* opCarr */{
							tag : 'td',
							html : '<div id="execCarrDivId_'
									+ custordNr
									+ '" style="width:140px;height:17px;position:relative;-moz-box-sizing:border-box;text-align:left;'
									+ carrDivTop + '"></div>',
							style : 'max-width:140px;width:140px'
						}, {
							tag : 'td',
							html : '<nobr></nobr>',
							style : 'border-top:none'
						} ]
			};
			hourrows[cnt] = {
				tag : 'tr',
				cls : 'order',
				id : 'trb' + i,
				style : 'height:30px;',
				children : hourcells
			};
			cnt++;

			// }
		}

		var orderTitleordersWith = (parent.offsetWidth - 17) + "px";
		if (hideCustorderHeaders || Ext.isIE) {
			orderTitleordersWith = "500px";
		}

		var toverflow = 'overflow:hidden;';
		if (Ext.isIE) {
			toverflow = 'overflow-x:visible;z-index:100;';
		}

		var domTable = {
			tag : 'div',
			cls : 'dispotablehead',
			id : 'orderTitleorders',
			style : toverflow + 'top:40px; left:0px; width:'
					+ orderTitleordersWith + '; height:'
					+ (parent.offsetHeight - 40 - 17) + 'px',
			children : [ {
				tag : 'div',
				style : 'padding-bottom:2px;',
				children : [ {
					id : 'ordertitlesTable',
					cls : 'dispotable',
					tag : 'table',
					style : 'width:100%',
					cellpadding : 0,
					cellspacing : 0,
					children : [ {
						tag : 'body',
						children : rows
					} ]
				} ]
			} ]
		};
		// LOGON-6654 save reference to this element to allow scrolling orders on it
		var orderTitleorders = Ext.DomHelper.append(parent, domTable, /* returnExtJsElem not DOM = */ true);

		var fftop = 'top:-1px;';
		if (Ext.isIE) {
			fftop = '';
		}
		domTable = {
			tag : 'div',
			cls : 'dispotablehead',
			id : 'orderDiv',
			style : 'top:40px; left:501px; overflow:scroll; width:'
					+ (parent.offsetWidth - 500) + 'px; height:'
					+ (parent.offsetHeight - 40) + 'px',
			children : [ {
				tag : 'div',
				style : 'width:' + (hourwidth * 24) + 'px',
				children : [ {
					id : 'orderTable',
					cls : 'dispotable',
					tag : 'table',
					style : fftop + 'width:' + (hourwidth * 24) + 'px',
					cellpadding : 0,
					cellspacing : 0,
					children : [ {
						tag : 'body',
						children : hourrows
					} ]
				} ]
			} ]
		};

		var orderdiv = Ext.DomHelper.append(parent, domTable);

		if (custorders.length === 0) {
			domTree = {
				tag : 'div',
				style : 'width:' + Ext.getDom('orderTitlehours').scrollWidth
						+ 'px; height:50px'
			};
			Ext.DomHelper.append(orderdiv, domTree);
		}

		orderdiv.onscroll = function(a, b) {
			Ext.getDom('orderTitlehours').scrollLeft = this.scrollLeft;
			Ext.getDom('orderTitleorders').scrollTop = this.scrollTop;
			var dispoDiv = Ext.getDom('dispoDiv');
			var dispoPlanningDiv = Ext.getDom('dispoplanningDiv');
			if (dispoDiv) {
				dispoDiv.scrollLeft = this.scrollLeft;
			}
			if (dispoPlanningDiv) {
				dispoPlanningDiv.scrollLeft = this.scrollLeft;
			}
			if (orderList) {
				orderList.oldScrollLeft = this.scrollLeft;
				orderList.oldScrollTop = this.scrollTop;
			}
		};
		// LOGON-6654 obtain ExtJS reference to orderDiv to trigger scrolling
		var scrollOrderDivReference = Ext.get('orderDiv');
		// LOGON-6654 onmousewheel callback for orderTitles
		function routeScrollToOrderDiv(e) {
			var params = extractScrollParametersFromMouseWheel(e);
			if(params) {
				scrollOrderDivReference.scroll(params.direction, params.amount, false);
			}
		}
		// LOGON-6654 safely register onmousewheel for orderTitles
		registerOnMouseWheelCrossBrowser(orderTitleorders, routeScrollToOrderDiv);

		this.oldScrollLeft = orderList ? orderList.oldScrollLeft : startHour
				* hourwidth;
		this.oldScrollTop = orderList ? orderList.oldScrollTop : 0;
		Ext.getDom('orderTitlehours').scrollLeft = this.oldScrollLeft;
		Ext.getDom('orderTitleorders').scrollTop = this.oldScrollTop;
		orderdiv.scrollLeft = this.oldScrollLeft;
		orderdiv.scrollTop = this.oldScrollTop;

		var oldSelection = this.selection;
		this.selection = null;

		var tbl = Ext.get('orderTable');
		var tabwidth = tbl.getWidth() - 2;
		var tableft = tbl.getLeft();

		for ( var j = 0; j < custorders.length; j++) {
			var custorderViewEntity = custorders[j].custorderViewEntity;
			/**
			 * If the order is external, this object holds the order data to
			 * display.
			 */
			if (custorderViewEntity !== null) {
				custordNr = custorderViewEntity.custorderNumber;
				/**
				* If the order is not external we have to display the shipto name
				* instead of customer order nr in this column.
				*/
				var shipToName = custorderViewEntity.shiptoName;
				var shipToNameObject = {
						tag : 'nobr',
						html : shipToName,
						title : shipToName
				};
				Ext.DomHelper.append(Ext.get("orderid_" + custordNr),
								shipToNameObject);
				
			} else {
				for (k = 0; k < custorders[j].orderWrappers.length; k++) {
					var orderWrapper = custorders[j].orderWrappers[k];
					if (orderWrapper.externalOrder) {
						// The order is external order
						extOrder = orderWrapper.extOrder;
						custordNr = extOrder.shiptoId;
						// Write the external order number name to the ordernr
						// cell
						var orderNrObject = {
							tag : 'div',
							html : extOrder.orderNumber
						};
						Ext.DomHelper.append(Ext.get("orderid_" + custordNr),
								orderNrObject);
						// Write the shippingpoint to the appropriate cell
						var shippingPointObject = {
							tag : 'div',
							html : extOrder.shippingpointName,
							title : extOrder.shippingpointName,
							style : 'overflow:hidden;width: 130px;white-space:nowrap;'
						};
						Ext.DomHelper.append(Ext.get("shippingpointDiv_"
								+ custordNr), shippingPointObject);
					}
				}
			}
			var orders = custorders[j].orderWrappers;
			hourcell = Ext.get("oc" + j + "_0");

			// this div acts as a container for objects that are placed on the
			// camion's timeline
			// it is of course a drag&drop target
			var orderrowId = 'orderrow' + j;
			var domTree = {
				tag : 'div',
				id : orderrowId,
				cls : 'orderrow',
				style : "width:" + (hourwidth * 24 - 4) + "px"
			};
			var el = Ext.DomHelper.insertFirst(hourcell, domTree);

			// calculate th width of one hour from the left offset of the first
			// and the last cell in the row
			// there is no better way of dealing with rounding and differences
			// between browser rendering engines.
			// note: this is a floating point value
			el.hourWidth = hourwidth;
			el.hourCell = hourcell;
			var auftragOffset = 24;
			var rowHeight = auftragOffset;
			if (orders.length > 0) {
				rowHeight = auftragOffset * orders.length;
			}
			// extfmu
			// don't set initial height to rowHeight times numberOfOrders, since
			// we don't know how
			// many of these orders we are going to draw - only set to
			// auftragOffset
			Ext.get("trhb" + j).setHeight(auftragOffset);
			Ext.get("trb" + j).setHeight(auftragOffset);
			Ext.get(orderrowId).setHeight(auftragOffset);

			var ordersPlaced = [ [] ];
			var x;
			// extfmu countIdentifier is used to identify the elements where the
			// number of collapsed orders are counted
			var countIdentifier;
			// extfmu combinedPoolAndCommCarrierOptionsIdentifier is used to
			// identify the commercial carrier select box that must be disabled
			// in case of collapsing
			var combinedPoolAndCommCarrierOptionsIdentifier;
			// paksyd combinedPoolAndExecCarrierOptionsIdentifier is used to
			// identify the executive carrier select box that must be disabled
			// in case of collapsing
			var combinedPoolAndExecCarrierOptionsIdentifier;
			// extfmu ordersDrawn: number of orders actually drawn, is used to
			// determine total row height
			var ordersDrawn = 0;
			// The index of the latest "order group" we group the identical
			// orders into these groups.
			var orderGroupIndex = 0;
			var execCarrierSelect;
			var ii = 0;
			var isOrdersIdentical = false;
			for (i = 0; i < orders.length; i++) {
				x = 0;
				var y = 0;
				var currOrderDom = new Order(el, orders[i], this.selection);
				// extfmu hide CurrentOrderCss: will be set to display:none for
				// every style of the collapsed order's components
				var hideCurrentOrderCss = '';
				var supressFirstInSet = false;
				// extfmu convenience flag to determine if we should count up
				// number of orders drawn
				// When true, display:none will be set to for every collapsed
				// order
				var doHidingCurrentOrderCss = false;
				if (i >= 1) {

					isOrdersIdentical = areOrdersIdentical(orders[i - 1], orders[i]);
					
					if (isOrdersIdentical) {
						// goto first-in-set and check if it is expanded or
						// collapsed
						// if it is set to expanded, we just act as if the
						// orders
						// weren't identical
						var prevOrderId = orders[i - 1].id;
						var odiv = Ext.get('ord' + prevOrderId);
						if (!odiv.hasClass('first-in-set')) {
							odiv = odiv.prev('div.first-in-set');
							var idStr = odiv.dom.getAttribute('id');
							prevOrderId = idStr.substring(3);
						}
						var doExpand = false;
						for (ii = 0; ii < nonCollapsedOrders.length; ii++) {
							if (nonCollapsedOrders[ii] == prevOrderId) {
								doExpand = true;
								supressFirstInSet = true;
							}
						}
						isOrdersIdentical = !doExpand;
					}
					if (isOrdersIdentical) {
						hideCurrentOrderCss = 'display: none;';
						doHidingCurrentOrderCss = true;
						// extfmu update collapsed count
						var prevCounterValue = parseInt(document
								.getElementById(countIdentifier).innerHTML, 10);
						document.getElementById(countIdentifier).innerHTML = prevCounterValue + 1;
						// if not the first, disable the pool-carrier select
						// boxes
						if (ordersDrawn > 0) {
							var commCarrierSelect = document
									.getElementById(combinedPoolAndCommCarrierOptionsIdentifier);
							if (commCarrierSelect !== null) {
								commCarrierSelect.disabled = true;	
							}
							execCarrierSelect = document
									.getElementById(combinedPoolAndExecCarrierOptionsIdentifier);
							if (execCarrierSelect !== null) {
								execCarrierSelect.disabled = true;	
							}
							
						}
					} else {
						// Creating a new group of orders
						orderGroupIndex++;
						// hide prev + if count only 1
						var cntr = document.getElementById(countIdentifier);
						if (cntr != null && cntr.value == 1) {
							var img = Ext.get(countIdentifier).next('img');
							if (img && !img.hasClass('is-expanded')) {
								img.dom.src = '../images/0.gif';
							}
						}
					}
				}
				// extfmu update counter identifier, pool identifier and number
				// of orders drawn
				countIdentifier = "inpCnt_" + custordNr + "_" + orderGroupIndex;
				combinedPoolAndCommCarrierOptionsIdentifier = "poolCommCarrierIdent_"
						+ custordNr + "_" + orderGroupIndex;
				combinedPoolAndExecCarrierOptionsIdentifier = "poolExecCarrierIdent_"
						+ custordNr + "_" + orderGroupIndex;
				if (!doHidingCurrentOrderCss) {
					ordersDrawn++;

					while (y <= ordersPlaced[x].length) {
						if (this.conflicts(ordersPlaced[x][y], currOrderDom)) {
							x = x + 1;
							y = 0;
							if (ordersPlaced[x] === null
									|| ordersPlaced[x] === undefined) {
								ordersPlaced[x] = [];
							}
						} else {
							y++;
						}
					}
				}
				if (orders[i] == oldSelection) {
					this.selection = oldSelection;
				}
				ordersPlaced[x][y] = currOrderDom;
				currOrderDom.draw(auftragOffset * x, doHidingCurrentOrderCss,
						supressFirstInSet);
				var ordersListDivId = 'ordersListDiv_' + custordNr + '_' + x;
				// extfmu: carrier selects and pool
				if (y == 1 || !isOrdersIdentical) {
					var text1 = !Ext.isEmpty(orders[i].order.commCarrierName) ? orders[i].order.commCarrierName
							: texts.NoCarrier;
					var text2 = !Ext.isEmpty(orders[i].order.execCarrierName) ? orders[i].order.execCarrierName
							: texts.NoCarrier;

					// var tdText = this.wrapOrderTitle(text1, text2, text1,
					// text2, 100);

					if (!orders[i].externalOrder
							&& ((orders[i].order.orderStatusCd === ORDER_STATUS_TRANSMITTED_TO_POOL)
									|| (orders[i].order.orderStatusCd === ORDER_STATUS_CONFIRMED_BY_POOL)
									|| (orders[i].order.orderStatusCd === ORDER_STATUS_TRANSMITTED_TO_CARRIER)
									|| (orders[i].order.orderStatusCd === ORDER_STATUS_CONFIRMED_BY_CARRIER))) {

						var combinedPoolAndCarrierOptions = [];
						// 1.add the poool options
						this.buildPoolOptions(orders[i].order.poolId,
								orders[i].order.commCarrierId,
								combinedPoolAndCarrierOptions);
						// 2.add the separator between pool and carrier options
						combinedPoolAndCarrierOptions.push({
							tag : 'option',
							value : '-',
							html : texts.LineBetweenPoolCarriers
						});
						// 3a. if it is only associated with the pool, add all
						// carriers available
						if ((orders[i].order.orderStatusCd === ORDER_STATUS_TRANSMITTED_TO_POOL)
								|| (orders[i].order.orderStatusCd === ORDER_STATUS_CONFIRMED_BY_POOL)) {
							combinedPoolAndCarrierOptions.push([ {
								tag : 'option',
								value : '',
								html : texts.NoCarrier,
								selected : 'selected'
							} ]);
							this.buildCommCarrierOptions(
									orders[i].order.commCarrierId,
									combinedPoolAndCarrierOptions);
						}
						// 3b. if the order was already transmitted, only add
						// the carrier of the order
						else if (!Ext.isEmpty(orders[i].order.commCarrierId)) {
							combinedPoolAndCarrierOptions.push([ {
								tag : 'option',
								value : '\'' + orders[i].order.commCarrierId
										+ '\'',
								html : orders[i].order.commCarrierName,
								selected : 'selected'
							} ]);
						}
						var selectComm = [ {
							tag : 'select',
							id : combinedPoolAndCommCarrierOptionsIdentifier,
							style : 'font-size:9px;width:90px;height:18px;',
							size : 1,
							name : 'carriers' + orders[i].id,
							children : combinedPoolAndCarrierOptions,
							onchange : 'setOrderPoolOrCommCarrier(\''
									+ ordersListDivId + '\', this.value)'
						} ];
						if (!authpool) {
							selectComm[0].disabled = 'disabled';
						}
						combinedPoolAndCarrierSelect = [ {
							tag : 'div',
							style : 'position:absolute;-moz-box-sizing:border-box;width:138px;height:18px;overflow:hidden;text-align:center;top:'
									+ auftragOffset
									* x
									+ 'px;'
									+ hideCurrentOrderCss,
							children : [ selectComm ]
						} ];

						Ext.DomHelper.append(Ext.get("commCarrDivId_"
								+ custordNr), combinedPoolAndCarrierSelect);
					} else if (orders[i].externalOrder) {
						// Write the carrier name to the comCarrier cell
						var carrierObject = {
							tag : 'div',
							html : extOrder.commCarrierName,
							style : 'text-align:center'
						};
						Ext.DomHelper.append(Ext.get("commCarrDivId_"
								+ custordNr), carrierObject);
					}

					// if the commercial carrier is not selectable, there should
					// be no possibility to change the executing carrier!
					var commCarrierFound = false;

					if (!Ext.isEmpty(orders[i].order.commCarrierId)
							&& currentCarriers.findIndex('carrierId',
									orders[i].order.commCarrierId) != '-1') {
						commCarrierFound = true;
					}

					var selectExec = '';
					var counterHtml = '<input readonly size="3" style="visibility: hidden;"/>&nbsp;<img src="../images/0.gif" width="10" height="10" style="margin:4px;"></img>';
					if (!doHidingCurrentOrderCss && !supressFirstInSet) {
						var imgSrc = '../images/plain_plus_2.png';
						var imgClass = 'collapsible';
						for (ii = 0; ii < nonCollapsedOrders.length; ii++) {
							if (orders[i].id == nonCollapsedOrders[ii]) {
								imgClass += ' is-expanded';
								imgSrc = '../images/plain_minus_2.png';
								break;
							}
						}
						counterHtml = '<div style="float: right;"><div id="'
								+ countIdentifier
								+ '" style="line-height: 18px; height: 18px; width: 20px; font-size: 10; display: inline;">1</div>&nbsp;<img height="10" width="10" class="'
								+ imgClass
								+ '" src="'
								+ imgSrc
								+ '" style="margin-right: 4px; margin-top: 4px; margin-bottom: 4px;display: inline;" data-orderid="'
								+ orders[i].id + '"></img></div>';
					}
					if (authcarrier
							&& commCarrierFound
							&& !orders[i].externalOrder
							&& ((orders[i].order.orderStatusCd === ORDER_STATUS_TRANSMITTED_TO_POOL)
									|| (orders[i].order.orderStatusCd === ORDER_STATUS_CONFIRMED_BY_POOL)
									|| (orders[i].order.orderStatusCd === ORDER_STATUS_TRANSMITTED_TO_CARRIER) 
									|| (orders[i].order.orderStatusCd === ORDER_STATUS_CONFIRMED_BY_CARRIER))) {

						var execCarrierOptions = [];
						execCarrierOptions.push([ {
							tag : 'option',
							value : '',
							html : texts.NoCarrier,
							selected : 'selected'
						} ]);
						if (orders[i].order.orderStatusCd === ORDER_STATUS_CONFIRMED_BY_CARRIER
								|| orders[i].order.orderStatusCd === ORDER_STATUS_TRANSMITTED_TO_CARRIER) {
							this.buildExecCarrierOptions(
									orders[i].order.execCarrierId,
									execCarrierOptions,
									orders[i].order.commCarrierId);
							selectExec = [ {
								tag : 'select',
								id : combinedPoolAndExecCarrierOptionsIdentifier,
								style : 'font-size:9px;width:90px;height:18px',
								size : 1,
								name : 'carriers' + orders[i].id,
								children : execCarrierOptions,
								onchange : 'setOrderExecCarrier(\''
										+ ordersListDivId + '\', this.value)'
							} ];
						} else {
							execCarrierOptions = [];
							execCarrierOptions.push([ {
								tag : 'option',
								value : text2,
								html : text2,
								selected : 'selected'
							} ]);
							selectExec = [ {
								tag : 'select',
								id : combinedPoolAndExecCarrierOptionsIdentifier,
								style : 'font-size:9px;width:90px;height:18px;',
								disabled : 'disabled',
								size : 1,
								name : 'carriers' + orders[i].id,
								children : execCarrierOptions
							} ];
						}
					} else if (orders[i].externalOrder) {
						// There is no exec carrier for external orders.
						selectExec = '-';
						counterHtml = '';
					}

					var paddingForNonFirstInSet = supressFirstInSet ? 'padding-left: 4px;' : 'padding-left: 4px;';

					execCarrierDiv = [ {
									tag : 'div',
									style : 'float:left',
									children : [ selectExec ]
					} ];

					execCarrierSelect = [ {
									tag : 'div',
									style : 'position:absolute;-moz-box-sizing:border-box;width:138px;height:17px;overflow:hidden;text-align:center;top:'
											+ auftragOffset * x + 'px;'
											+ hideCurrentOrderCss + paddingForNonFirstInSet,
									children : [ execCarrierDiv, counterHtml ]
					} ];
				
					var ordersListDiv = [ {
						tag : 'div',
						id : ordersListDivId,
						style : 'position:absolute;visibility:hidden',
						html : orders[i].order.orderNumber
					} ];
					
					var execCarrDiv = Ext.get("execCarrDivId_" + custordNr);
					Ext.DomHelper.append(execCarrDiv, execCarrierSelect);
					Ext.DomHelper.append(execCarrDiv, ordersListDiv);
				} else {
					// if more then one orders are in one row, the carrier
					// selection must change all in the row.
					if (y > 1) {
						var commCarrSel = Ext.get(ordersListDivId);
						var orderslist = commCarrSel.dom.innerHTML;
						orderslist = orderslist + ','
								+ orders[i].order.orderNumber;
						commCarrSel.dom.innerHTML = orderslist;
					}
				}
			}
			// hide prev + if count only 1
			var cntrElement = document.getElementById(countIdentifier);
			if (cntrElement != null && cntrElement.innerHTML === "1") {
				var imgExp = Ext.get(countIdentifier).next('img');
				if (imgExp && !imgExp.hasClass('is-expanded')) {
					imgExp.dom.src = '../images/0.gif';
				}
			}
			if (orders.length > 0) {
				// rowHeight = auftragOffset * ordersPlaced.length;
				rowHeight = auftragOffset * ordersDrawn;
			}
			Ext.get("trhb" + j).setHeight(rowHeight);
			Ext.get("trb" + j).setHeight(rowHeight);
			Ext.get(orderrowId).setHeight(rowHeight);

			if (custorders[j].custorderViewEntity !== null) {
				// show custorder delivery window
				var custOrdDeliveryFromMin = dateToMinutes(custorders[j].custorderViewEntity.deliveryFromDat);
				var custOrdDeliveryToMin = dateToMinutes(custorders[j].custorderViewEntity.deliveryToDat);
				var custOrderDivLeft = this
						.durationToLength(custOrdDeliveryFromMin);
				var custOrderDivWidth = this
						.durationToLength(custOrdDeliveryToMin
								- custOrdDeliveryFromMin);
				rowHeight = rowHeight - 1;
				var custOrderDiv = {
					tag : 'div',
					cls : 'custorderSpan',
					style : 'overflow:hidden;width:' + custOrderDivWidth
							+ 'px;left:' + custOrderDivLeft + 'px;height:'
							+ rowHeight + 'px;',
					children : {
						tag : 'div',
						style : 'width:' + custOrderDivWidth + 'px;height:'
								+ rowHeight + 'px;',
						cls : 'custorderSpanChild'
					}
				};
				Ext.DomHelper.append(Ext.get(orderrowId), custOrderDiv);
			}

			// Hiding Custorder headers if needed
			if (hideCustorderHeaders) {
				document.getElementById("trha" + j).style.display = "none";
				document.getElementById("tra" + j).style.display = "none";
			}

		}
		sumFreightCostSpanUpdate.plannedOrdersLoaded = true;
		sumFreightCostSpanUpdate.refreshSumFreightCosts();
		var imgs = Ext.select('img.collapsible');
		var that = this;
		imgs.addListener('click', function(evt, t, o) {
			var imgSrc = t.src;
			if (imgSrc.indexOf('0.gif') == -1) {
				that.toggleDeliveryCollapsed(t);
			}			
		});

	};

	// if placedOrder Balken conflicts with currOrd Balken then true.
	this.conflicts = function(placedOrder, currOrd) {
		// placedOrder currOrd
		if (placedOrder === null || placedOrder === undefined
				|| currOrd === null) {
			return false;
		}
		if (placedOrder.commCarrierId != currOrd.commCarrierId
				|| placedOrder.execCarrierId != currOrd.execCarrierId) {
			return true;
		}

		return true;
	};

	this.canTransmitSelection = function() {
		if (this.selection === null) {
			return false;
		}
		orderNode = Ext.getDom('ord' + this.selection.id);
		if (orderNode) {
			return orderNode.orderObject.canTransmit();
		}
		return false;
	};

	this.canAcceptSelection = function() {
		if (this.selection === null) {
			return false;
		}
		orderNode = Ext.getDom('ord' + this.selection.id);
		if (orderNode) {
			return orderNode.orderObject.canAccept();
		}
		return false;
	};

	this.canCancelSelection = function() {
		if (this.selection === null) {
			return false;
		}
		orderNode = Ext.getDom('ord' + this.selection.id);
		if (orderNode) {
			return orderNode.orderObject.canCancel();
		}
		return false;
	};

	this.draw();
}

// replaces the sumFreightCost + truckId sum freight cost spans in case there
// are prescheduled orders assigned to the truck
// also a popup is rendered showing which assignments need still prescheduling,
// and which are already done
function SumFreightCostSpan(truckId) {

	this.init = function(truckId) {
		this.isUsed = false;
		this.truckId = truckId;
		this.assignmentFreightCostFloat = 0;
		this.assignmentOrderNumbers = "";
		this.assignmentFreightCurrency = "";
		this.plannedOrderFreightCostFloat = 0;
		this.plannedOrderNumbers = "";
		this.plannedFreightCurrency = "";
	};

	this.draw = function() {
		currency = this.plannedFreightCurrency !== '' ? this.plannedFreightCurrency
				: this.assignmentFreightCurrency;
		var truck = Ext.get("cd" + this.truckId);
		if (!truck) {
			return;
		}

		var freightCostFloat = this.assignmentFreightCostFloat
				+ this.plannedOrderFreightCostFloat;

		var freightCostSpan = null;
		var sumFreightCostsWidth = null;

		if (!isNaN(freightCostFloat)) {
			var freightText = roundToDecimalAndTrimEndZeros(freightCostFloat, 2)
					+ ' ' + currency;
			sumFreightCostsWidth = Ext.util.TextMetrics.measure(
					'dispoplanningTitleCell', freightText).width;
			orderNumbers = this.assignmentOrderNumbers + '<br>'
					+ this.plannedOrderNumbers;

			freightCostSpan = {
				tag : 'span',
				style : "overflow:hidden;display:table-cell;width:"
						+ sumFreightCostsWidth
						+ ";text-align:right;padding-left:3;"
						+ (isPlanningTruck(truck.dom.camion) ? 'color:#0000FF;'
								: ''),
				html : freightText,
				"ext:qtip" : texts.TotalFreightCostPerTruck + ": "
						+ freightText + '<br><br>' + texts.Orders + '<br>'
						+ orderNumbers,
				"ext:qclass" : "dispoqtip dispoinfoqtip"
			};
		} else {
			sumFreightCostsWidth = 18;
			orderNumbers = this.assignmentOrderNumbers + '<br>'
					+ this.plannedOrderNumbers;

			freightCostSpan = {
				tag : 'span',
				style : "overflow:hidden;display:table-cell;width:"
						+ sumFreightCostsWidth
						+ ";text-align:right;padding-left:3;"
						+ (isPlanningTruck(truck.dom.camion) ? 'color:#0000FF;'
								: ''),
				html : ""
			};

			freightCostSpan.children = {
				tag : 'img',
				src : crhCommonUrl('../images/information.png'),
				style : 'visibility:visible',
				"ext:qtip" : texts.PlanningInfoOnTruck + '<br><br>'
						+ texts.Orders + '<br>' + orderNumbers,
				"ext:qclass" : "dispoqtip dispoinfoqtip"
			};
		}

		var dom = Ext.getDom("sumFreightCost" + this.truckId);

		Ext.DomHelper.overwrite(dom, freightCostSpan);

		this.init(this.truckId);
	};

	this.init(truckId);
}

// overwrites the spans named 'sumFreightCost' + truck.truck.truckId each per
// truck. The popup contains information about the planned orders
// and the planned revenues. The information is only visible on planning trucks.
function SumFreightCostSpanUpdate() {

	this.assignmentsLoaded = false;
	this.plannedOrdersLoaded = false;
	this.trucksLoaded = false;

	this.refreshSumFreightCosts = function() {
		if (!this.assignmentsLoaded || !this.plannedOrdersLoaded
				|| !this.trucksLoaded) {
			return;
		}

		var i;
		var truckDiv;
		var truck;

		var clist = Ext.query(".camion", Ext.getDom('dispoplanningTable'));
		for (i = 0; i < clist.length; i++) {
			truckDiv = clist[i];
			truck = truckDiv.camion;
			if (!isPlanningTruck(truck)) {
				continue;
			}
			var truckId = truck.truckId;
			for ( var j = 0; j < truckDiv.assignmentList.length; j++) {
				var assignment = truckDiv.assignmentList[j].assignment;
				var parts = assignment.parts;
				for ( var k = 0; k < parts.length; k++) {
					if (parts[k].assignmentPart.assignmentPartTypeCd == 3) {
						sumFreightCostSpanUpdate.addCostSpanInfoToTruck(
								truckId, null, assignment, parts[k]);
					}
				}
			}
		}

		// add costs from the bottom (order/not yet assigned) list to the trucks
		// where they were planned
		var olist = Ext.query("div[class='orderOuter']", Ext
				.getDom('orderlistcontainer'));
		for (i = 0; i < olist.length; i++) {
			var orderDiv = olist[i];
			var order = orderDiv.order;
			if (order.plannedTruckId) {
				sumFreightCostSpanUpdate.addCostSpanInfoToTruck(
						order.plannedTruckId, order, null, null);
			}
		}

		// add costs to the trucks getting the values from the assignments
		clist = Ext.query(".camion", Ext.getDom('dispoplanningTable'));
		for (i = 0; i < clist.length; i++) {
			truckDiv = clist[i];
			truck = truckDiv.camion;
			if (isPlanningTruck(truck) && truckDiv.sumFreightCostSpan.isUsed) {
				truckDiv.sumFreightCostSpan.draw();
			}
		}
	};

	// marks the specified truckId to refresh it's sumFreightCostSpan when all
	// the used data is loaded (assignments, orders)
	this.addCostSpanInfoToTruck = function(truckId, order, assignment,
			assignmentPart) {

		var truck = Ext.get('cd' + truckId);
		if (truck === undefined || truck === null) {
			return;
		}

		var newSpan = truck.dom.sumFreightCostSpan;

		newSpan.isUsed = true;

		var fcColor = "";
		var orderNumbers = "";
		if (order) {
			if (order.isPreschedulingDone) {
				fcColor = '#A2C4A2';
			}
			orderNumbers = "<font color=" + fcColor + ">"
					+ order.order.orderNumber;
			if (order.isPreschedulingRequired) {
				orderNumbers += " ("
						+ roundToDecimalAndTrimEndZeros(order.freightCost, 2)
						+ " " + order.freightCurrency + ") "
						+ order.plannerUnloadPlannedStart;
			}
			newSpan.plannedOrderFreightCostFloat += parseFloat(order.freightCost);
			newSpan.plannedOrderNumbers += '<br>' + orderNumbers;
			newSpan.plannedFreightCurrency = order.freightCurrency;
			orderNumbers += "</font>";
		}
		if (assignment) {
			if (assignment.isPreschedulingDone) {
				fcColor = '#448844';
			} else if (assignment.isPreschedulingRequired) {
				fcColor = '#FF0000';
			}
			orderNumbers = "<font color=" + fcColor + ">"
					+ assignmentPart.assignmentPart.orderNumber;
			if (assignment.isPreschedulingRequired) {
				orderNumbers += " ("
						+ roundToDecimalAndTrimEndZeros(assignment.freightCost,
								2) + " " + assignment.freightCurrency + ") "
						+ assignmentPart.plannerUnloadPlannedStart;
			}
			orderNumbers += "</font>";
			newSpan.assignmentFreightCostFloat += parseFloat(assignment.freightCost);
			newSpan.assignmentOrderNumbers += '<br>' + orderNumbers;
			newSpan.assignmentFreightCurrency = assignment.freightCurrency;
		}
	};

}

function Order(parent, data, selection) {

	this.hourWidth = parent.hourWidth;

	this.createBalkenPart = function(width, cls, balken, balkencnt) {
		if (width > 1) {
			balken[balkencnt++] = {
				tag : 'div',
				'cls' : cls,
				style : 'width:' + width + 'px'
			};
		} else if (width == 1) {
			balken[balkencnt++] = {
				tag : 'div',
				'cls' : cls,
				style : 'border-left:none;border-right:none;width:' + width
						+ 'px'
			};
		}
		return balkencnt;
	};

	this.timeToLength = function(time) {
		return Math.round(this.hourWidth * time / 60) - 2;
	};

	this.durationToLength = function(duration) {
		return Math.round(this.hourWidth * duration / 60);
	};

	this.init = function() {
		this.spanstart = trimMinutes(dateToMinutes(data.order.deliveryFromDat)
				- data.order.transportDurationMin - data.loadingTime);

		if (dateYMDcompare(currentDispoDate, data.order.deliveryFromDat) !== 0) {
			this.spanstart = 0;
		}

		this.spanend = trimMinutes(dateToMinutes(data.order.deliveryToDat)
				+ data.order.transportDurationMin
				+ data.order.plannedUnloadingTimeMin);

		if (dateYMDcompare(currentDispoDate, data.order.deliveryToDat) !== 0) {
			this.spanend = 60 * 24;
		}

		this.commCarrierId = data.order.commCarrierId;
		this.execCarrierId = data.order.execCarrierId;
	};

	this.draw = function(divTop, hideCurrentOrder, supressFirstInSet) {

		var spanstart = this.spanstart;
		var spanend = this.spanend;

		var spanwidth = spanend - spanstart;

		var orderduration = trimMinutes(data.order.transportDurationMin * 2
				+ data.loadingTime + data.order.plannedUnloadingTimeMin);

		this.startMinute = spanstart
				+ Math.round((spanwidth - orderduration) / 2);

		var statusAppend = DELIVERY_STATUS_FLAG_PLANNED;
		if (data.order.orderStatusCd === ORDER_STATUS_TRANSMITTED_TO_CARRIER) {
			statusAppend = DELIVERY_STATUS_FLAG_TRANSMITTED;
		} else if (data.order.orderStatusCd === ORDER_STATUS_CONFIRMED_BY_CARRIER) {
			statusAppend = DELIVERY_STATUS_FLAG_CONFIRMED;
		}
		if (data.order.onHoldOrderAndLockedFlag) {
			statusAppend = DELIVERY_TYPE_FLAG_LOCKED;
		} else if (data.externalOrder) {
			statusAppend += DELIVERY_TYPE_FLAG_EXTERNAL;
		} else if (data.order.deliveryFromDat.toUTCString() === data.order.deliveryToDat.toUTCString()) {
			statusAppend = DELIVERY_TYPE_FLAG_ONTIME + statusAppend;
		}

		var durationparts = [];
		var durationcnt = 0;
		
		var isLandfillOrReturnPallet = data.landfill || data.emptyPallet;
		
		durationcnt = this.createBalkenPart(this
					.durationToLength(data.loadingTime), 'load' + statusAppend,
					durationparts, durationcnt);
		
		durationcnt = this.createBalkenPart(this
				.durationToLength(data.order.transportDurationMin), 'to'
				+ statusAppend, durationparts, durationcnt);
		
		durationcnt = this.createBalkenPart(this
				.durationToLength(data.order.plannedUnloadingTimeMin), 'unload'
				+ statusAppend, durationparts, durationcnt);
		
		if (!isLandfillOrReturnPallet) {
			durationcnt = this.createBalkenPart(this
					.durationToLength(data.order.transportDurationMin), 'from'
					+ statusAppend, durationparts, durationcnt);
		}

		var description = data.order.orderNumber + '&nbsp;'
				+ buildItemsLine(data.items);
		var popup = description + '<br>' + texts.Time + ":&nbsp;"
				+ formatDeliveryDate(data.order.deliveryFromDat)
				+ '&nbsp;-&nbsp;'
				+ formatDeliveryDate(data.order.deliveryToDat) + "<br>"
				+ buildShippingPLoadingPPopup(data) + buildLNFPopup(data)
				+ buildDistancePopup(data)
				+ buildNotesPopup(data.order, "", data) + "<br>"
				+ buildFreightCostInfo(data);

		popup = popup.replace(/"/g, "&quot;");
		var descriptionBar = {
			tag : 'div',
			cls : 'orderCaption',
			html : description,
			'ext:qtip' : popup,
			'ext:qclass' : 'dispoqtip dispoinfoqtip'
		};

		var barleft = this.durationToLength((spanwidth - orderduration) / 2);
		var innerbarWidth = this.durationToLength(orderduration) + 4;

		var innerbar = {
			tag : 'div',
			cls : 'orderInner',
			style : 'width:' + innerbarWidth + 'px;left:' + barleft + 'px',
			children : durationparts
		};
		durationparts[0].children = descriptionBar;
		innerbar.children[durationcnt++] = {
			tag : 'img',
			src : crhCommonUrl('../images/warning2.png'),
			cls : 'orderError'
		};
		var hideCurrentOrderCss = '';
		if (hideCurrentOrder === true) {
			hideCurrentOrderCss = 'display: none;'
		}
		var domTree = {
			tag : 'div',
			cls : (hideCurrentOrder || supressFirstInSet ? '' : 'first-in-set ')
					+ 'orderOuter'
					+ (((selection !== null) && (data.id == selection.id)) ? "Selected"
							: ""),
			style : "width:" + this.durationToLength(spanwidth) + "px;left:"
			// + this.timeToLength(spanstart) + "px;" + hideCurrentOrderCss,
			+ this.timeToLength(spanstart) + "px; top: " + divTop + "px;"
					+ hideCurrentOrderCss,
			id : 'ord' + data.id,
			children : [ innerbar ]
		};

		// Creating icon after the order with carrier notes popup
		var carrierNotes = buildNotesOrder("", data);
		if (carrierNotes.declineAndNoteText !== '') {
			carrierNotes.declineAndNoteText = carrierNotes.declineAndNoteText
					.replace(/"/g, "&quot;");
			carrierNotes.declineAndNoteText = carrierNotes.declineAndNoteText
					.replace(/'/g, "&quot;");
			domTree.children[1] = {
				tag : 'img',
				src : crhCommonUrl('../images/exclamation.gif'),
				'ext:qtip' : carrierNotes.declineAndNoteText,
				cls : 'orderIcon',
				onclick : 'resetCarrierDeclineNote(' + data.order.orderId
						+ ');',
				'ext:qclass' : 'dispoqtip dispoinfoqtip',
				style : 'left:' + (innerbarWidth + barleft) + 'px'
			};
		} else if (carrierNotes.noteText !== '') {
			carrierNotes.noteText = carrierNotes.noteText.replace(/"/g,
					"&quot;");
			carrierNotes.noteText = carrierNotes.noteText.replace(/'/g,
					"&quot;");
			domTree.children[1] = {
				tag : 'img',
				src : crhCommonUrl('../images/error.png'),
				'ext:qtip' : carrierNotes.noteText,
				cls : 'orderIcon',
				'ext:qclass' : 'dispoqtip dispoinfoqtip',
				style : 'left:' + (innerbarWidth + barleft) + 'px'
			};
		}

		var dom = Ext.DomHelper.append(parent, domTree);
		dom.order = data;
		dom.orderObject = this;
		this.el = Ext.get(dom);
		this.iel = Ext.get(dom.firstChild);
		if (authcarrier && (!data.isBag || (data.order.deliveryTypeCd != 5)) // &&
		// ((!data.isPreschedulingRequired && authprescheduleuse) ||
		// (data.isPreschedulingRequired && authpreschedulewrite) )
		) {

			var dd = this.iel.initDD("order" + data.id, null,
					dispoOrderOverrides);
			dd.hourWidth = this.hourWidth;
			dd.deliveryOffset = this.durationToLength(data.loadingTime)
					+ this.durationToLength(data.order.transportDurationMin);
			dd.deliveryTime = this.durationToLength(this.startMinute)
					+ dd.deliveryOffset;

			dd.leftLimit = this.timeToLength(spanstart);
			dd.assWidth = this.durationToLength(orderduration);
			dd.rightLimit = this.timeToLength(spanend) - dd.assWidth;
		}
		this.iel.on("click", function() {
			var list = Ext.query("div[class='orderOuterSelected']", Ext
					.getDom("orderDiv"));
			for ( var i = 0; i < list.length; i++) {
				list[i].className = "orderOuter";
			}
			if (this.dom.parentNode) {
				orderList.selection = this.dom.parentNode.order;
				this.dom.parentNode.className = "orderOuterSelected";
			}

			if (orderList.onSelectionChange) {
				orderList.onSelectionChange();
			}
		});

		this.iel.on("dblclick", function(e, target) {
			var order = this.dom.parentNode.order;
			if (authorderdetail && !order.externalOrder) {
				showPopup("../auftrag/OrderDetailEntry.do", null, {
					orderid : order.order.orderId
				});
			} else if (true && order.externalOrder) {
				showPopup("../dispo/ExternalOrderEntry.do", null, {
					edit : '',
					extorderid : order.order.extOrderId
				});
			}
		});

	};

	this.setErrorMark = function(prev) {
		var error = false;
		if (prev !== null) {
			error = (prev.assignment.finishShippingpointId != data.shippingpointId);
		}
		var err = Ext.query("img[class='orderError']", this.iel.dom)[0];
		err.style.visibility = error ? 'visible' : 'hidden';
	};

	this.canTransmit = function() {
		return data.order.commCarrierId
				&& ((data.order.orderStatusCd === ORDER_STATUS_TRANSMITTED_TO_POOL) 
				|| (data.order.orderStatusCd === ORDER_STATUS_CONFIRMED_BY_POOL));
	};

	this.canAccept = function() {
		return (authpool && (data.order.orderStatusCd === ORDER_STATUS_TRANSMITTED_TO_POOL))
				|| (authcarrier && (data.order.orderStatusCd === ORDER_STATUS_TRANSMITTED_TO_CARRIER));
	};

	this.canCancel = function() {
		return ((!data.externalOrder) && (
				(authpool && (
					(data.order.orderStatusCd === ORDER_STATUS_TRANSMITTED_TO_POOL) 
					|| (data.order.orderStatusCd === ORDER_STATUS_CONFIRMED_BY_POOL))
				) || 
				(authcarrier && (
					(data.order.orderStatusCd === ORDER_STATUS_TRANSMITTED_TO_CARRIER) 
					|| (data.order.orderStatusCd === ORDER_STATUS_CONFIRMED_BY_CARRIER)
				))));
	};

	this.init();
}

function pingCustomerOrders(selectedParameter) {
	var reset = false;
	if (selectedParameter === null || selectedParameter === undefined) {
		if (selectedCustorder === null || selectedCustorder === undefined) {
			return;
		}
	} else {
		if (selectedCustorder !== null
				&& selectedCustorder == selectedParameter) {
			// reset
			selectedCustorder = null;
			reset = true;
		} else {
			selectedCustorder = selectedParameter;
		}
	}

	var list = Ext.query("div[class*=auftragSollIst]");
	var i;
	var custordernr = '';
	for (i = 0; i < list.length; i++) {
		custordernr = list[i].offsetParent.getAttribute('custordernr');
		if (reset || (custordernr !== null && custordernr == selectedCustorder)) {
			list[i].className = 'auftragSollIst';
		} else {
			list[i].className = 'auftragSollIstPing'; // gray out all that not
			// related to selected
			// custordernr
		}

	}

	list = Ext.query("tr[class*=ustOrderHead]");
	for (i = 0; i < list.length; i++) {
		custordernr = list[i].getAttribute('custordernr');
		if (custordernr !== null && selectedCustorder !== null
				&& custordernr == selectedCustorder && !reset) {
			list[i].className = list[i].className.replace("custOrderHead",
					"pingCustOrderHead");
		} else {
			list[i].className = list[i].className.replace("pingCustOrderHead",
					"custOrderHead");
		}
	}

	list = Ext.query("div[class*=uftragDeliveryTime]");
	for (i = 0; i < list.length; i++) {
		custordernr = list[i].offsetParent.getAttribute('custordernr');
		if (reset
				|| (custordernr !== null && selectedCustorder !== null && custordernr == selectedCustorder)) {
			list[i].className = list[i].className.replace(
					"pingAuftragDeliveryTime", "auftragDeliveryTime");
		} else {
			// gray out all that not related to selected custordernr
			list[i].className = list[i].className.replace(
					"auftragDeliveryTime", "pingAuftragDeliveryTime");
		}
	}
}

/** 
 * This will be called when we got the ajax response from server.
 *
 */
function getOrderNumbersToConfirmCallback(orderNumbersToConfirm, assignmentIds, orders) {
	if (orders == null && orderNumbersToConfirm.length === 0) {
		cancelAssignments(assignmentIds);
		return;
	}
	if (orders != null && orders.length > 0 && orderNumbersToConfirm.length === 0) {
		cancelAssignments(assignmentIds, null, orders);
		return;
	}
	
	var truckId = getTruckIdByAssignment(assignmentIds[0]);
	var truckName = getTruckNameById(truckId);
	
	var messageBody = createDeallocationConfirmationMsg(orderNumbersToConfirm, truckName);
	showConfirmationDialog(
		texts.dispoDeallocationConfirmTitle,
		messageBody,
		texts.optionYes,
		texts.optionNo,
		function() {
			cancelAssignments(assignmentIds, null, orderNumbersToConfirm);
		}
	);
}

function confirmCancelAssignments(assignmentIds, dialogText, orders) {
	if(orders != null && orders.length === 0) {
		return;
	}
	var groupedOrderNumbers = getGroupedOrderNumbersOfAssignments(assignmentIds, orders);
	var orderNumbersNotToConfirmYet     = groupedOrderNumbers[0];
	var orderNumbersToConfirmAlready    = groupedOrderNumbers[1];
	if (orderNumbersNotToConfirmYet.length !== 0) {
		DispoAjaxService.getOrderNumbersToConfirm(
			orderNumbersNotToConfirmYet,
			function (orderNumbersToConfirmReturnedByServer) {
				var orderNumbersToConfirm = orderNumbersToConfirmAlready.concat(
					orderNumbersToConfirmReturnedByServer
				);
				getOrderNumbersToConfirmCallback(orderNumbersToConfirm, assignmentIds, orders);
			}
		);
	}
	else {
		getOrderNumbersToConfirmCallback(orderNumbersToConfirmAlready, assignmentIds, orders);
	}
}

/**
 * Collects the order numbers from screen belonging to the given assignment IDs
 * 
 * Sorts them into two groups:
 * - order numbers which are already to be confirmed
 * - order numbers which are not to be confirmed yet
 * 
 * @param assignmentIds
 */
function getGroupedOrderNumbersOfAssignments(assignmentIds, orders) {
	var orderNumbersNotToConfirmYet = [];
	var orderNumbersToConfirmAlready = [];
	
	for (var assignmentIndex = 0; assignmentIndex < assignmentIds.length; assignmentIndex++) {
		var assignmentId = assignmentIds[assignmentIndex];
		var assignment = Ext.getDom("ai" + assignmentId).parentNode.assignment;
		for (var assignmentPartIndex = 0; assignmentPartIndex < assignment.parts.length; assignmentPartIndex++) {
			var assignmentPartWrapper = assignment.parts[assignmentPartIndex];
			var assignmentPart = assignmentPartWrapper.assignmentPart;
			if (!assignmentPart.orderNumberInStr) {
				continue;
			}
			
			if(orders != null) {
				var foundOrderNumber = false;
				for (var orderIndex = 0; orderIndex < orders.length; orderIndex++) {
					if(orders[orderIndex] == assignmentPart.orderNumberInStr) {
						foundOrderNumber = true;
						break;
					}
				}
				if(!foundOrderNumber) {
					continue;
				}
			}
			
			if (assignmentPart.orderStatusCd === ORDER_STATUS_ARRIVED_AT_LOADINGPOINT) {
				orderNumbersToConfirmAlready.push(assignmentPart.orderNumberInStr);
			}
			else {
				orderNumbersNotToConfirmYet.push(assignmentPart.orderNumberInStr);
			}
		}
	}
	
	var groupedOrderNumbers = [];
	groupedOrderNumbers.push(orderNumbersNotToConfirmYet);
	groupedOrderNumbers.push(orderNumbersToConfirmAlready);
	return groupedOrderNumbers;
}
