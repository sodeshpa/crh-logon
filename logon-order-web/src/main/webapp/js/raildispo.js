var taracache = {};
var grosscache = {};
var focus = null;

function setTaraCache(key, value) {
	taracache[key] = value;
}

function setGrossCache(key, value) {
	grosscache[key] = value;
}

function getTaraCache(key) {
	return taracache[key];
}

function getGrossCache(key) {
	return grosscache[key];
}

function saveWeight(control) {
	if (control.className == 'oksigninactive') {
		return;
	}
	// TODO 28-Nov-2012/romek: Next line is not flexible for DOM changes, also
	// ugly, use ExtJS!
	var assignment = control.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.wagon.assignment;
	if (assignment && assignment.assignment.assignTrStatusCd) {
		Logon.message.warning(errortexts.WeightTransmitted);
	}

	var fld = control.parentNode.parentNode.firstChild.firstChild;

	var tares = {};
	var grosses = {};

	while (fld != null) {
		if (fld.tagName == 'INPUT') {
			var orderItemId = fld.getAttribute('orderItemId');
			var gross = parseFloat(fld.value);
			gross = (!isNaN(gross)) ? gross: null;
			grosses[orderItemId] = fld.value;
			var tareField = Ext.getDom("iTare" + fld.getAttribute('loadid'));
			if (tareField) {
				var tare = parseFloat(tareField.value);
				tare = (!isNaN(tare)) ? tare: null;
				tares[orderItemId] = tare;
			}
		}
		fld = fld.nextSibling;
	}

	RailDispoAjaxService.setLoadWeights(tares, grosses, authrailspecial,
			refreshWagons);
}

function nvl(values) {
	var i;
	
	if (values) {
		for (i = 0; i < values.length; i++) {
			if (values[i]) {
				return values[i];
			}
		}
		
		return null;
	}
	else {
		return '';
	}
}

function findTypePrio(wagon, tl) {
	for (var i = 0; i < tl.length; i++) {
		if (wagon.wagon.wagonTypeCd == tl[i].wagonTypeCd) {
			return tl[i];
		}
	}
	return null;
}

function findPart(assignment, typeCd) {
	if (!assignment) {
		return null;
	}
	for (var i = 0; i < assignment.parts.length; i++) {
		if (assignment.parts[i].assignmentPart.assignmentPartTypeCd == typeCd) {
			return assignment.parts[i];
		}
	}
	return null;
}

function buildItemsLine(items, nobr) {
	if (items.length == 1) {
		return items[0].materialName + (nobr ? ' ': '<br>')
				+ items[0].orderedQuantity + ' ' + items[0].uomName;
	}
	var res = '';
	var i;
	var comma = '';
	for (i = 0; i < items.length; i++) {
		res += comma + items[i].materialName + ' ' + items[i].orderedQuantity
				+ ' ' + items[i].uomName;
		comma = ',<br>';
	}

	return res;
}

function buildLoadsLine(loads) {
	var res = '';
	var i;
	var comma = '';
	for (i = 0; i < loads.length; i++) {
		res += comma + loads[i].load.materialDescription;
		comma = ',<br>';
	}

	return res;
}

function checkSaveButtonState(saveid, wagonNo) {
	var saveBtn = Ext.getDom('iSave' + saveid);
	if (saveBtn != null) {
		var fld = saveBtn.parentNode.parentNode.firstChild.firstChild;
		var trackValue = Ext.getDom('track' + wagonNo).value;

		if (trackValue !== '' && trackValue != '-') {
			while (fld != null) {
				if (fld.tagName == 'INPUT') {
					var orig = fld.getAttribute('ovalue');
					if (orig != fld.value) {
						saveBtn.className = 'oksign';
						return;
					}
					var tareField = Ext.getDom("iTare"
							+ fld.getAttribute('loadid'));
					if (tareField
							&& (tareField.getAttribute('ovalue') != tareField.value)) {
						saveBtn.className = 'oksign';
						return;
					}
				}
				fld = fld.nextSibling;
			}
		}
		saveBtn.className = 'oksigninactive';
	}
}

function saveTara(field, saveid, wagonNo) {
	setTaraCache(field.id, field.value);
	checkSaveButtonState(saveid, wagonNo);
}

function saveGross(field, saveid, wagonNo) {
	setGrossCache(field.id, field.value);
	checkSaveButtonState(saveid, wagonNo);
}

function setFocus(field) {
	focus = field.id;
}

function removeFocus(field) {
	if (focus == field.id) {
		focus = null;
	}
}

function validateEtran(event, wagonId) {
	var ETRAN_MAX_SIZE = 8;
	var ENTER_KEY = 13;
	
	if (event && event.keyCode == ENTER_KEY) {
		saveEtran(wagonId);
		return;
	}
	
	var saveEtranField = Ext.get('saveetran' + wagonId);
	var etranReturnField = Ext.get('etranreturn' + wagonId);
	var etranOutboundField = Ext.get('etranoutbound' + wagonId);
	
	var etranReturn = etranReturnField.getValue();
	var etranOutbound = etranOutboundField.getValue();
	var validationSuccess = true;
	
	if (etranReturn.length > ETRAN_MAX_SIZE) {
		etranReturnField.addClass('etranfielderror');
		validationSuccess = false;
	}
	else {
		etranReturnField.removeClass('etranfielderror');
	}
	
	if (etranOutbound.length > ETRAN_MAX_SIZE) {
		etranOutboundField.addClass('etranfielderror');
		validationSuccess = false;
	}
	else {
		etranOutboundField.removeClass('etranfielderror');
	}
	
	if (validationSuccess) {
		saveEtranField.removeClass('oksigninactive');
		saveEtranField.removeClass('noksign');
		saveEtranField.addClass('oksign');
		
		saveEtranField.set({"ext:qtip": ""});
	}
	else {
		saveEtranField.removeClass('oksign');
		saveEtranField.removeClass('oksigninactive');
		saveEtranField.addClass('noksign');
	
		saveEtranField.set({"ext:qtip": errortexts.EtranValidation});
	}
	
	return validationSuccess;
}

function saveEtran(wagonId) {
	if (!validateEtran(null, wagonId)) {
		return;
	}
	
	var etranReturnField = Ext.get('etranreturn' + wagonId);
	var etranOutboundField = Ext.get('etranoutbound' + wagonId);
	var saveEtranField = Ext.get('saveetran' + wagonId);
	
	var wagonRow = etranReturnField.up('.wagon').dom;
	
	var wagonWrapper = wagonRow.wagon;
	var orderDataPartWrapper = findPart(wagonWrapper.assignment, 3);
	
	var orderNumber = orderDataPartWrapper ? orderDataPartWrapper.assignmentPart.orderNumber : null;
	var etranReturn = etranReturnField.getValue();
	var etranOutbound = etranOutboundField.getValue();
	
	function saveSuccess(etranResult) {
		etranReturnField.dom.value = etranResult.etranReturn;
		etranOutboundField.dom.value = etranResult.etranOutbound;
		
		saveEtranField.removeClass('oksign');
		saveEtranField.removeClass('noksign');
		saveEtranField.addClass('oksigninactive');
	}
	
	if (orderNumber) {
		RailDispoAjaxService.saveEtranToOrder(orderNumber, etranReturn, etranOutbound, 
				saveSuccess);
	}
	else {
		RailDispoAjaxService.saveEtranToWagon(wagonId, etranReturn, etranOutbound, 
				saveSuccess);
	}
}

function buildEtranFields(wagonWrapper) {
	if (!isEtranEnabled()) {
		return '';
	}
	
	var etranOutbound = '';
	var etranReturn = '';
	
	var orderDataPartWrapper = findPart(wagonWrapper.assignment, 3);
	
	if (orderDataPartWrapper) {
		var orderDataPart = orderDataPartWrapper.assignmentPart;
		
		if (SPECIAL_ORDER_DIST_CD_LIST.indexOf(orderDataPart.distributionDestCd) > -1) {
			return '';
		}
		
		if (orderDataPart.etranOutbound) {
			etranOutbound = orderDataPart.etranOutbound;
		}
		if (orderDataPart.etranReturn) {
			etranReturn = orderDataPart.etranReturn;
		}
	}
	else {
		var wagon = wagonWrapper.wagon;
		
		if (wagon.etranOutbound) {
			etranOutbound = wagon.etranOutbound;
		}
		if (wagon.etranReturn) {
			etranReturn = wagon.etranReturn;
		}
	}
	
	var wagonId = wagonWrapper.wagon.wagonId;
	var saveId = "saveetran" + wagonId;
	var etranOutbountId = "etranoutbound" + wagonId;
	var etranReturnId = "etranreturn" + wagonId;
	
	var etranOutBoundField = "<input type='text' class='etranoutboundsign'"
		+ "' id='" + etranOutbountId
		+ "' value='" + etranOutbound
		+ "' onkeyup='validateEtran(event, " + wagonId + ")'" +
			" onfocus='setFocus(this)' onblur='removeFocus(this)'/>";
	
	var etranReturnField = "<input type='text' class='etranreturnsign'"
		+ "' id='" + etranReturnId
		+ "' value='" + etranReturn
		+ "' onkeyup='validateEtran(event, " + wagonId + ")'" +
			" onfocus='setFocus(this)' onblur='removeFocus(this)'/>";
	
	var submitButton = "<span"
		+ " ext:qclass='dispoqtip dispoerrorqtip'"
		+ " id='" + saveId
		+ "' class='oksigninactive etransavebutton'"
		+ " onclick='saveEtran(" + wagonId + ")'></span>";
	
	var output = 
		'<span>' +
			'<span class="etranfield">' + 
				etranOutBoundField +
			'</span>' +
			'<span class="etranfield">' + 
				etranReturnField +
			'</span>' +
			submitButton +
		'</span>';
	
	return output;
}

function buildOrderRemarkLabel1() {
	var output = 
		'<span class="ordernumberlabel">' +
			texts.Nr + 
		'</span>';
			
	if (isEtranEnabled()) {
		output += 
			'<span class="orderremarklabelbig">' +
				texts.Etran + 
			'</span>';
	}
		
	return output;
}

function buildOrderRemarkLabel2() {
	var output = 
		'<span class="ordernumberlabel">' +
			texts.Comment + 
		'</span>';
			
	if (isEtranEnabled()) {
		output += 
			'<span class="orderremarklabel">' +
				texts.EtranOutbound + 
			'</span>' + 
			'<span class="orderremarklabelmedium">' +
				texts.EtranReturn + 
			'</span>';
	}

	return output;
}

function buildTareMeasure(wagon, container, orderItem) {
	var value = null;
	var loadId = null;
	var packageTypeCd = null;
	var saveId = '';
	var wagonNo = wagon.wagon.wagonNo;
	if (container) {
		if (container.container.tareQuantity) {
			value = container.container.tareQuantity;
		}
		packageTypeCd = container.container.logonPackageTypeCd;
		loadId = container.container.loadId;
		saveId = 'c' + container.container.containerId;
	}
	else if (wagon) {
		if (wagon.wagon.tareQuantity) {
			value = wagon.wagon.tareQuantity;
		}
		packageTypeCd = wagon.wagon.logonPackageTypeCd;
		loadId = wagon.wagon.loadId;
		saveId = 'w' + wagon.wagon.wagonId;
	}

	var changed = false;
	var up = findPart(wagon.assignment, 3);
	var id = "iTare" + loadId;
	var cValue = getTaraCache(id);
	var oValue = value ? value: '';
	value = oValue;
	if (cValue) {
		if (value != cValue) {
			value = cValue;
			changed = true;
		}
		else {
			setTaraCache(id, null);
		}
	}

	if (authrail && up && up.assignmentPart.orderStatusCd >= 6 && loadId
			&& orderItem && !wagon.isReturning && packageTypeCd
			&& (wagon.wagon.wagonTrspCatCd != 4)
			&& (up.assignmentPart.orderStatusCd <= 11 || authrailspecial)) {
		return [
			"<input type='text' style='width:40px;height:18px;' loadid='" + loadId 
			+ "' fieldid='" + loadId
			+ "' ovalue='" + oValue
			+ "' id='" + id
			+ "' value='" + value
			+ "' onkeyup='saveTara(this, "+ '"' + saveId + '", "' + wagonNo + '"' + 
				")' onfocus='setFocus(this)' onblur='removeFocus(this)'>",
			changed
		];
	}

	if (wagon.wagon.wagonTrspCatCd == 4) {
		value = "&nbsp;";
	}

	return [value, changed];
}

function buildGrossMeasure(wagon, container, up, orderItem, tareChanged, loads) {
	var ret = "";
	var edit = false;
	if (loads && !up) {
		for (var i = 0; i < loads.length; i++) {
			if (i > 0) {
				ret += '<br>';
			}
			if (loads[i].netQuantityInTons != null) {
				ret += loads[i].netQuantityInTons;
			}
			else {
				// FIXME 29-Nov-2012/romek: What would be the proper error
				// handling?
				Logon.message
						.warning('Could not convert net quantity to tons (loadId = '
								+ loads[i].loadId + ').');
				ret += 0;
			}
		}
	}
	var max = ((container || !up) ? 1: up.orderItems.length);
	var saveId = '';
	var okClass = null;
	for (var j = 0; j < max; j++) {
		if (!container && up) {
			orderItem = up.orderItems[j];
		}
		var value = null;
		var packageTypeCd = null;
		var loadId = null;
		var wagonNo = wagon.wagon.wagonNo;
		if (container) {
			if (container.container.netQuantity) {
				if (container.netQuantityInTons != null) {
					// Rounding with 2 decimal digits.
					value = Math
							.round((container.netQuantityInTons + container.container.tareQuantity) * 100) / 100;
				}
				else {
					// FIXME 29-Nov-2012/romek: What would be the proper error
					// handling?
					Logon.message
							.warning('Could not convert net quantity to tons (containerId = '
									+ container.container.containerId + ').');
					value = 0;
				}
			}
			packageTypeCd = container.container.logonPackageTypeCd;
			loadId = container.container.loadId;
			saveId = 'c' + container.container.containerId;
		}
		else if (wagon) {
			if (wagon.wagon.wagonTrspCatCd == 4) {
				if (orderItem) {
					value = orderItem.loadedQuantity;
				}
			}
			else if (wagon.wagon.netQuantity) {
				if (wagon.netQuantityInTons != null) {
					// Rounding with 2 decimal digits.
					value = Math
							.round((wagon.netQuantityInTons + wagon.wagon.tareQuantity) * 100) / 100;
				}
				else {
					// FIXME 29-Nov-2012/romek: What would be the proper error
					// handling?
					Logon.message
							.warning('Could not convert net quantity to tons (wagonId = '
									+ wagon.wagon.wagonId + ').');
					value = 0;
				}
			}
			packageTypeCd = wagon.wagon.logonPackageTypeCd;
			loadId = wagon.wagon.loadId;
			saveId = 'w' + wagon.wagon.wagonId;
		}

		var changed = false;
		var fldid = loadId + (orderItem ? ("_" + orderItem.orderItemId): "");
		var id = "iGross" + fldid;
		var cValue = getGrossCache(id);
		var oValue = value ? value: '';
		value = oValue;
		if (cValue) {
			if (value != cValue) {
				value = cValue;
				changed = true;
			}
			else {
				setGrossCache(id, null);
			}
		}

		var trackDropdown = Ext.getDom('track' + wagonNo);
		var trackValue = '';
		if (trackDropdown != null) {
			trackValue = trackDropdown.value;
		}
		okClass = "oksigninactive";
		if (trackValue !== '' && trackValue != '-' && (tareChanged || changed)) {
			okClass = "oksign";
		}

		if (j > 0) {
			ret += '<br>';
		}

		if (authrail && up && up.assignmentPart.orderStatusCd >= 6
				&& loadId && orderItem && !wagon.isReturning
				&& packageTypeCd 
				&& !logonPackageHandlingHelper.isService(orderItem.logonPackageTypeCd)
				&& (up.assignmentPart.orderStatusCd <= 11 || authrailspecial)) {
			edit = true;
			ret += "<input type='text' style='width:40px;height:18px;margin-left:10px' "
					+ "loadid='" + loadId 
					+ "' fieldid='" + fldid
					+ "' ovalue='" + oValue
					+ "' orderItemId='" + (orderItem ? orderItem.orderItemId: "")
					+ "' id='" + id
					+ "' value='" + value
					+ "' onkeyup='saveGross(this, " + '"' + saveId + '", "' + wagonNo + '"' 
					+ ")' onfocus='setFocus(this)' onblur='removeFocus(this)'>";
		}
		else {
			ret += value;
		}
	}
	if (edit) {
		ret = "<table style='border-collapse: collapse;display:inline;'><tr style='height:0px'><td style='border:none'>"
				+ ret
				+ "</td><td style='border:none'><span id='iSave"
				+ saveId
				+ "' class='"
				+ okClass
				+ "' style='margin-left:10px;width:20px;' onclick='saveWeight(this)'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></td></tr></table>";
	}
	return ret;
}

function buildCell(content) {
	if (content.line1) {
		content.line1 = "<span"
				+ (content.tip1 ? (" ext:qtip='" + content.tip1 + "' ext:qclass='dispoqtip dispoinfoqtip'")
						: "") + ">" + content.line1 + "</span>";
	}
	content.tip2 = nvl([content.tip2, content.tip1]);
	if (content.line2) {
		content.line2 = "<span"
				+ (content.tip2 ? (" ext:qtip='" + content.tip2 + "' ext:qclass='dispoqtip dispoinfoqtip'")
						: "") + ">" + content.line2 + "</span>";
	}
	var res = "<div style='-moz-box-sizing:border-box;overflow:hidden;position:relative;font-size:12px;height:38px;padding-top:1px;"
			+ content.divstyle
			+ "'>"
			+ ((content.action || content.info) ? buildMarkers(content.action,
					content.info): '')
			+ "<nobr>"
			+ (content.error ? buildErrorMarker(content.error.msg,
					content.error.callback): '')
			+ (content.line1 ? content.line1: "")
			+ (content.line2 ? ("<br>" + content.line2): "")
			+ "</nobr>"
			+ "</div>";
	return res;
}

function buildMarkers(action, info) {
	var res = "<div style='-moz-box-sizing:border-box;float:right;font-size:12px;height:38px;width:22px;position:relative;'>"
			+ (action ? buildActionMarker(action) + (info ? '<br>': ''): '')
			+ (info ? buildInfoMarker(info): '') + "</div>";
	return res;
}

function buildErrorMarker(msg, callback) {
	var res = "<div style='-moz-box-sizing:border-box;float:left;font-size:12px;height:38px;width:10px;position:absolute;left:0px;'>"
			+ "<img src='"
			+ crhCommonUrl('../images/warning2.png')
			+ "' style='position:relative; left:2px; top:8px' ext:qclass='dispoqtip dispoerrorqtip' ext:qtip='"
			+ msg
			+ ((authrail && callback) ? "' onclick='" + callback: '')
			+ "'/>" + "</div>"
	return res;
}

function buildInfoMarker(msg) {
	var res = '<img src="'
			+ crhCommonUrl('../images/error.png')
			+ '" style="position:relative; left:0px; top:0px" ext:qclass="dispoqtip dispoinfoqtip" ext:qtip="'
			+ msg + '"/>';
	return res;
}

function buildActionMarker(action) {
	var res = "<img src='"
			+ crhCommonUrl('../images/' + action.icon)
			+ "' style='position:relative; left:0px; top:0px' ext:qclass='dispoqtip dispoinfoqtip' ext:qtip='"
			+ action.tooltip + "' onclick='" + action.callback + "'/>";
	return res;
}

function buildWagonNote(noteDispo, noteSpedi) {
	if (noteDispo || noteSpedi) {

		var res = '';
		if (noteDispo) {
			res += noteDispo;
			if (noteSpedi) {
				res += '<br>';
			}
		}
		if (noteSpedi) {
			res += noteSpedi;
		}
		res = res.replace(/"/g, "&quot;");
		return '<nobr>' + res + '</nobr>';
	}
	return null;
}

function wagonClick(e) {
	var trackinput = e.getTarget("span.trackinput", 5);
	if (trackinput && wagonList.selection[this.dom.id]) {
		return;
	}
	if (e.shiftKey) {
		if (wagonList.selection[this.dom.id]) {
			return;
		}
		var sel = this.dom;
		var newSelection = [];
		// try down
		newSelection[0] = sel.id;
		var current = sel.nextSibling;
		while (current != null) {
			if (wagonList.selection[current.id]) {
				break;
			}
			newSelection[newSelection.length] = current.id;
			current = current.nextSibling;
		}
		if (current == null) {
			// no selection down, try up
			newSelection = [];
			newSelection[0] = sel.id;
			current = sel.previousSibling;
			while (current != null) {
				if (wagonList.selection[current.id]) {
					break;
				}
				newSelection[newSelection.length] = current.id;
				current = current.previousSibling;
			}
		}
		if (current == null) {
			// no selection at all
			newSelection = [sel.id];
		}
		for (var i = 0; i < newSelection.length; i++) {
			wagonList.selection[newSelection[i]] = true;
		}
	}
	else {
		wagonList.selection = {};
		wagonList.selection[this.dom.id] = true;
	}
	wagonList.drawSelection();
	if (wagonList.onSelectionChange) {
		wagonList.onSelectionChange();
	}
}

function showSpecialOrderPopup(wagonId) {
	showPopup('/logon/order/auftrag/OrderSpecialEntry.do?wagonid=' + wagonId);
}

function buildTrackControl(wagon) {
	if (!wagon.isReturning) {
		if (authrail) {
			return "<span class='trackinput'><input type='text' style='width:30px;' value='"
					+ (wagon.wagon.currentPlantTrackCd ? wagon.wagon.trackName
							: '-')
					+ "' id='track"
					+ wagon.wagon.wagonNo
					+ "'><input type='text' style='width:30px;' value='"
					+ (wagon.wagon.currentTrackPositionNo ? wagon.wagon.currentTrackPositionNo
							: '-')
					+ "' id='tpos"
					+ wagon.wagon.wagonNo
					+ "'></span>";
		}
		else {
			return (wagon.wagon.currentPlantTrackCd ? wagon.wagon.trackName
					: '')
					+ '<br>'
					+ (wagon.wagon.currentTrackPositionNo ? wagon.wagon.currentTrackPositionNo
							: '');
		}

	}
	else {
		return '-<br>-';
	}
}

function buildMaterialList(list) {
	if (!list || (list.length === 0)) {
		return '-';
	}
	var res = list[0];
	for (var i = 1; i < list.length; i++) {
		res += '<br>' + list[i];
	}
	return res;
}

function buildMaterialPrioList(wagon, status) {
	if (wagon.assignment) {
		return null;
	}

	var res = '';
	var preloadList = buildPreloadPrioList(wagon);

	if (wagon.possibleOrders.length > 0 || preloadList.length > 0) {

		res = '<table>';

		if (wagon.possibleOrders.length > 0) {
			var prio = wagon.possibleOrders[0].prio;

			var separator = '';
			var rows = "";
			var span = 0;
			for (var i = 0; i < wagon.possibleOrders.length; i++) {
				var newprio = wagon.possibleOrders[i].prio;
				if (prio != newprio) {
					res += '<tr><td rowspan="' + span + '"><b><nobr>'
							+ texts.Prio + '&nbsp;' + prio + '</nobr></b></td>'
							+ rows;
					prio = newprio;
					separator = '';
					span = 0;
					rows = '';
				}
				rows += separator
						+ '<td><nobr>'
						+ buildItemsLine(wagon.possibleOrders[i].order.items,
								true)
						+ '</nobr></td><td style="vertical-align:middle"><nobr>('
						+ texts.Nr + ": "
						+ wagon.possibleOrders[i].order.order.orderNumber
						+ ')</nobr></td></tr>';
				span++;
				separator = '<tr>'

				var orow = Ext.get('tro'
						+ wagon.possibleOrders[i].order.order.orderNumber);
				if (orow) {
					var wtl = orow.dom.order.wagonTypeList;
					if (wtl && (wtl.length > 0)
							&& wtl[0].evaluationPriority == prio) {
						orow.addClass("maxprio");
					}
					else {
						orow.addClass("normalprio");
					}
				}

			}
			res += '<tr><td rowspan="' + span + '"><b>' + texts.Prio + '&nbsp;'
					+ prio + '</b></td>' + rows;

		}

		if (preloadList.length > 0) {
			res += '<tr><td><b>' + texts.Preloads + '</b></td><td>'
					+ preloadList + '</td></tr>';
		}

		res += '</table>';
	}
	return res;
}

function buildPreloadPrioList(wagon) {
	if (wagon.assignment) {
		return '';
	}

	var res = '';

	if (wagon.possiblePreloads.length > 0) {
		res = '<table style="border:0px;margin:0px;" cellspacing=0 cellpadding=0>';
		for (var i = 0; i < wagon.possiblePreloads.length; i++) {
			var p = wagon.possiblePreloads[i];
			res += '<tr><td><b><nobr>' + p.count
					+ '</nobr></b></td><td><nobr>&nbsp;&nbsp;'
					+ p.target.materialName + " " + p.target.loadQuantity + " "
					+ p.target.uomName;
		}
		res += '</table>';
	}
	return res;
}

function initTrackDropdown(wagon) {
	var wagonNo = wagon.wagon.wagonNo;

	var trackRecordType = Ext.data.Record.create([{
		name: 'id',
		mapping: 'plantTrackCd'
	}, {
		name: 'trackName',
		mapping: 'trackName'
	}, {
		name: 'trackNameTx',
		mapping: 'trackNameTx'
	}]);

	var trackListStore = new Ext.data.Store({
		proxy: new Ext.data.DWRProxy(RailDispoAjaxService.fetchTrackList),
		reader: new Ext.data.ObjectReader({
			id: 'id'
		}, trackRecordType),
		remoteSort: true
	});

	// Custom rendering Template
	var resultTpl = new Ext.XTemplate('<table class="classic"><tbody>',
			'<tpl for="."><tr  class="listitem zebra{[xindex % 2]}">',
			'<td>{trackName}</td>', '<td>{trackNameTx}</td>', '</tr></tpl>',
			'</tbody></table>');

	var search = new Ext.form.ComboBox({
		store: trackListStore,
		displayField: 'trackName',
		typeAhead: false,
		loadingText: '',
		maxHeight: 800,
		minChars: 0,
		forceSelection: true,
		selectOnFocus: true,
		triggerAction: 'all',
		hideTrigger: false,
		tpl: resultTpl,
		applyTo: 'track' + wagonNo,
		itemSelector: 'tr.listitem',
		listWidth: 200
	});
	search.wagonNo = wagonNo;
	search.on('select', function(cb, record, index) {
		var list = wagonList.getSelectedWagonNos();
		if (list.length === 0) {
			RailDispoAjaxService.setWagonTrack([cb.wagonNo], record.data.id,
					refreshWagons);
		}
		else {
			RailDispoAjaxService.setWagonTrack(list, record.data.id,
					refreshWagons);
		}
	});
}

function initPosDropdown(wagonNo) {

	var posRecordType = Ext.data.Record.create([{
		name: 'id',
		mapping: 'value'
	}, {
		name: 'label',
		mapping: 'label'
	}]);

	var posProxy = new Ext.data.DWRProxy(function(query, callback) {
		RailDispoAjaxService.fetchPosList(this.wagonNo, callback)
	});
	posProxy.wagonNo = wagonNo;

	var posListStore = new Ext.data.Store({
		proxy: posProxy,
		reader: new Ext.data.ObjectReader({
			id: 'id'
		}, posRecordType),
		remoteSort: true
	});

	// Custom rendering Template
	var resultTpl = new Ext.XTemplate('<table class="classic"><tbody>',
			'<tpl for="."><tr  class="listitem zebra{[xindex % 2]}">',
			'<td>{label}</td>', '</tr></tpl>', '</tbody></table>');

	var search = new Ext.form.ComboBox({
		store: posListStore,
		displayField: 'label',
		typeAhead: false,
		loadingText: '',
		maxHeight: 800,
		minChars: 0,
		forceSelection: true,
		selectOnFocus: true,
		triggerAction: 'all',
		hideTrigger: false,
		tpl: resultTpl,
		applyTo: 'tpos' + wagonNo,
		itemSelector: 'tr.listitem',
		listWidth: 50
	});
	search.wagonNo = wagonNo;
	search.on('select', function(cb, record, index) {
		var list = wagonList.getSelectedWagonNos();
		if (list.length === 0) {
			RailDispoAjaxService.setWagonTrackPos([cb.wagonNo],
					record.data.id, refreshWagons);
		}
		else {
			RailDispoAjaxService.setWagonTrackPos(list, record.data.id,
					refreshWagons);
		}
	});
}

function isTransmitted(up) {
	return !((up.assignmentPart.orderStatusCd == 11)
			|| (up.assignmentPart.orderStatusCd == 10)
			|| (up.assignmentPart.orderStatusCd == 9)
			|| (up.assignmentPart.orderStatusCd == 8)
			|| (up.assignmentPart.orderStatusCd == 7) || (up.assignmentPart.orderStatusCd == 6));
}

function buildOrderNumber(order) {
	var output = "-";
	
	if (order) {
		if (authorderdetail) {
			output = '<a href="javascript:showPopup(' + "'"
					+ '../auftrag/OrderDetailEntry.do' + "'" + ', null, {orderid: '
					+ order.orderId + '})">' + order.orderNumber + '</a>';
		}
		else {
			output = order.orderNumber;
		}
	}
	
	output = 
		'<span class="ordernumberlabel">' + 
			output + 
		'</span>';
	
	return output;
}

function buildCarrierNotes(orderData) {
	var carrierNotes = '';
	if (orderData.order.noteRailwayDisp) {
		carrierNotes = orderData.order.noteRailwayDisp;
	}
	// just remove adding item note, all the information is stored in the order
	// notes
	return carrierNotes;
}

function buildNotesPopup(data, data2, prefix, orderData) {
	if (!prefix) {
		prefix = "";
	}
	var carrierNotes = "";
	if (data.carrierDispatchingNote) {
		carrierNotes = data.carrierDispatchingNote;
	}
	else if (orderData) {
		carrierNotes = buildCarrierNotes(orderData);
	}

	var deliveryNotes = data2.carrierDeliveryNote ? data2.carrierDeliveryNote
			: "";
	var custNotes = data2.custWeborderNote ? data2.custWeborderNote: "";
	if ((carrierNotes !== '') || (deliveryNotes !== '') || (custNotes !== '')) {
		var ret = '<table class="disponotestable">';
		if (carrierNotes !== '') {
			carrierNotes = encodeQuotes(carrierNotes);
			ret += '<tr><td style="width:100px"><nobr><b>' + prefix
					+ texts.Dispatcher
					+ '</b></nobr></td><td style="width:100%"><pre>'
					+ carrierNotes + '</pre></nobr></td></tr>'
		}
		if (deliveryNotes !== '') {
			deliveryNotes = encodeQuotes(deliveryNotes);
			ret += '<tr><td style="width:100px"><nobr><b>' + prefix
					+ texts.DeliveryNote
					+ '</b></nobr></td><td style="width:100%"><pre>'
					+ deliveryNotes + '</pre></td></tr>';
		}
		if (custNotes !== '') {
			custNotes = encodeQuotes(custNotes);
			ret += '<tr><td style="width:100px"><nobr><b>' + prefix
					+ texts.CustNote
					+ '</b></nobr></td><td style="width:100%"><pre>'
					+ custNotes + '</pre></td></tr>';
		}
		ret += '</table>';
		return ret;
	}
	else {
		return '';
	}
}

function adjustStellzeit(target) {
	var ass = Ext.get(target).up('tr').dom.wagon.assignment;
	if (ass) {
		RailDispoAjaxService.adjustStellzeit(ass.assignment.assignmentId);
		refreshWagons();
	}
}

/**
 * Opens the wagon pre-load popup.
 * 
 * @param (String) wagonId - the id of the wagon to preload.
 */
function showPreLoadInputDialog(wagonId) {
	var parameters = {'wagonId': wagonId};
	showPopup('PreLoadWagonEntry.do', null, parameters);
}

function WagonList(parent, wagons) {

	this.selection = wagonList ? wagonList.selection: {};
	this.scrolltop = wagonList ? wagonList.scrolltop: 0;

	this.drawSelection = function() {
		var list = Ext.select("tr", false, Ext.getDom("wagonsTable"));
		list.removeClass("selected");
		var newSel = {};
		for (var sel in this.selection) {
			var el = Ext.getDom(sel);
			if (el) {
				newSel[sel] = true;

				if (el.selecttarget) {
					el = el.selecttarget;
				}
				el = Ext.get(el);
				el.addClass("selected");
				el.dom.firstChild.className = "ddselect";
			}
		}
		this.selection = newSel;
	}

	this.getSelectedAssignments = function() {
		var res = [];
		for (var id in this.selection) {
			var row = Ext.getDom(id);
			if (row && row.wagon.assignment) {
				res[res.length] = row.wagon.assignment;
			}
			else {
				return [];
			}
		}
		return res;
	}

	this.getSelectedWagons = function() {
		var res = [];
		for (var id in this.selection) {
			var row = Ext.getDom(id);
			if (row && row.wagon) {
				res[res.length] = row.wagon;
			}
			else {
				return [];
			}
		}
		return res;
	}

	this.getSelectedAssignmentIds = function() {
		var res = {
			list: [],
			transmitted: false
		};
		for (var id in this.selection) {
			var row = Ext.getDom(id);
			if (row && row.wagon.assignment) {
				res.list[res.list.length] = row.wagon.assignment.assignment.assignmentId;
				if (row.wagon.assignment.assignment.assignTrStatusCd == 1) {
					res.transmitted = true;
				}
			}
			else if (row && row.wagon.isReturning) {
				res.list[res.list.length] = row.wagon.wagon.incomingAssignmentId;
				if (row.wagon.wagon.incomingAssignTrStatusCd == 1) {
					res.transmitted = true;
				}
			}
			else {
				return {
					list: [],
					transmitted: false
				};
			}
		}
		return res;
	}

	this.getSelectedWagonNos = function() {
		var res = [];
		for (var id in this.selection) {
			var row = Ext.getDom(id);
			if (row) {
				res[res.length] = row.wagon.wagon.wagonNo;
			}
			else {
				return [];
			}
		}
		return res;
	}

	this.draw = function() {
		var w = Ext.get(parent).getWidth() - 30;
		var widthOverflow = 0;
		
		var minOrderRemarkWidth = isEtranEnabled() ? 250 : 0;
		var orderRemarkWidth = Math.round(w * 0.1);
		
		if (minOrderRemarkWidth > orderRemarkWidth) {
			widthOverflow += minOrderRemarkWidth - orderRemarkWidth;
			orderRemarkWidth = minOrderRemarkWidth;
		}
		
		var useOverflowStyle = widthOverflow > 0;
		var overFlowStyle = useOverflowStyle ? "text-align: left;" : "";
		
		var columnWidthStyles = [
			'width:' + Math.round(w * 0.08) + 'px',
			'width:' + Math.round(w * 0.05) + 'px',
			'width:' + Math.round(w * 0.02) + 'px',
			'width:' + Math.round(w * 0.06) + 'px',
			'width:' + Math.round(w * 0.06) + 'px',
			'width:' + Math.round(w * 0.12) + 'px',
			'width:' + Math.round(w * 0.06) + 'px',
			'width:' + Math.round(w * 0.07) + 'px',
			'width:' + orderRemarkWidth + 'px',
			'width:' + Math.round(w * 0.08) + 'px',
			'width:' + Math.round(w * 0.09) + 'px',
			'width:' + Math.round(w * 0.05) + 'px',
			overFlowStyle + 'width:' + Math.round(w * 0.14) + 'px'
		];
		
		var columnWidth2And3Style = 'width:' + Math.round(w * 0.08) + 'px';
		var twlast3 = 'width:' + Math.round(w * 0.28) + 'px';

		w += widthOverflow;
		var headerCells = [];

		headerCells[0] = {
			tag: 'th',
			html: buildCell({
				line1: texts.RailcarNr,
				line2: texts.RailcarType,
				divstyle: columnWidthStyles[0]
			}),
			style: columnWidthStyles[0]
		};
		headerCells[1] = {
			tag: 'th',
			html: buildCell({
				line1: texts.Track,
				line2: texts.Position,
				divstyle: columnWidthStyles[1]
			}),
			style: columnWidthStyles[1]
		};
		headerCells[2] = {
			tag: 'th',
			html: buildCell({
				line1: texts.ContainerNr,
				line2: texts.ContainerType,
				divstyle: columnWidth2And3Style
			}),
			style: columnWidth2And3Style,
			colspan: '2'
		};
		headerCells[3] = {
			tag: 'th',
			html: buildCell({
				line1: texts.ArrivalTime,
				line2: texts.LoadingTime,
				divstyle: columnWidthStyles[4]
			}),
			style: columnWidthStyles[4]
		};
		headerCells[4] = {
			tag: 'th',
			html: buildCell({
				line1: texts.LastProduct,
				line2: texts.LoadingStatus,
				divstyle: columnWidthStyles[5]
			}),
			style: columnWidthStyles[5]
		};
		headerCells[5] = {
			tag: 'th',
			html: buildCell({
				line1: texts.TareMeasure,
				divstyle: columnWidthStyles[6]
			}),
			style: columnWidthStyles[6]
		};
		headerCells[6] = {
			tag: 'th',
			html: buildCell({
				line1: texts.RawMeasure,
				divstyle: columnWidthStyles[7]
			}),
			style: columnWidthStyles[7] + ";border-right:3px solid #999"
		};
		headerCells[7] = {
			tag: 'th',
			html: buildCell({
				line1: buildOrderRemarkLabel1(),
				line2: buildOrderRemarkLabel2(),
				divstyle: columnWidthStyles[8]
			}),
			style: columnWidthStyles[8]
		};
		headerCells[8] = {
			tag: 'th',
			html: buildCell({
				line1: texts.Product,
				line2: texts.OrderedQuantity,
				divstyle: columnWidthStyles[9]
			}),
			style: columnWidthStyles[9]
		};
		headerCells[9] = {
			tag: 'th',
			html: buildCell({
				line1: texts.SwitchingTimeDep,
				line2: texts.SwitchingTimeArr,
				divstyle: columnWidthStyles[10]
			}),
			style: columnWidthStyles[10]
		};

		headerCells[10] = {
			tag: 'th',
			html: buildCell({
				line1: texts.Delivery,
				line2: texts.From + '&nbsp;&nbsp;&nbsp;' + texts.To,
				divstyle: columnWidthStyles[11]
			}),
			style: columnWidthStyles[11]
		};

		headerCells[11] = {
			tag: 'th',
			html: buildCell({
				line1: texts.Customer,
				line2: texts.Shipto,
				divstyle: columnWidthStyles[12]
			}),
			style: columnWidthStyles[12]
		};

		var dispoTableHeadDiv = {
			tag: 'div',
			cls: 'dispotablehead',
			style: 'width:' + w + 'px',
			children: [{
				tag: 'table',
				cls: 'dispotable',
				children: [{
					tag: 'thead',
					children: [{
						tag: 'tr',
						children: headerCells
					}]
				}]
			}]
		};

		Ext.DomHelper.overwrite(parent, dispoTableHeadDiv);

		var rows = [];
		var rowCount = 0;

		rows[rowCount++] = {
			tag: 'colgroup',
			children: [{
				tag: 'col',
				style: columnWidthStyles[0]
			},
			{
				tag: 'col',
				style: columnWidthStyles[1]
			},
			{
				tag: 'col',
				style: columnWidthStyles[2]
			},
			{
				tag: 'col',
				style: columnWidthStyles[3]
			},
			{
				tag: 'col',
				style: columnWidthStyles[4]
			},
			{
				tag: 'col',
				style: columnWidthStyles[5]
			},
			{
				tag: 'col',
				style: columnWidthStyles[6]
			},
			{
				tag: 'col',
				style: columnWidthStyles[7] + ";border-right:3px solid #999"
			},
			{
				tag: 'col',
				style: columnWidthStyles[8]
			},
			{
				tag: 'col',
				style: columnWidthStyles[9]
			},
			{
				tag: 'col',
				style: columnWidthStyles[10]
			},
			{
				tag: 'col',
				style: columnWidthStyles[11]
			},
			{
				tag: 'col',
				style: columnWidthStyles[12]
			}]
		};

		for (var wagonIndex = 0; wagonIndex < wagons.length; wagonIndex++) {
			var currentWagonWrapper = wagons[wagonIndex];
			var currentWagon = currentWagonWrapper.wagon;
			var dp = findPart(currentWagonWrapper.assignment, 2);
			var up = findPart(currentWagonWrapper.assignment, 3);
			var height = null;

			var timeerror = null;
			var timecallback = null;
			if (dp
					&& (dp.assignmentPart.plannedStart.getTime() < (new Date())
							.add(Date.MINUTE, 15).getTime())
					&& !isTransmitted(up)) {
				timeerror = errortexts.NotTransmitted;
				timecallback = "adjustStellzeit(this)";
			}
			else if (up
					&& !up.assignmentPart.realStart
					&& (up.assignmentPart.plannedStart < (new Date()).getTime())) {
				timeerror = errortexts.NotArrived;
			}
			else if (up
					&& up.assignmentPart.deliveryToDat.getTime() < up.assignmentPart.plannedStart
							.getTime()) {
				timeerror = errortexts.CantArrive;
			}

			var specialOrderAction = null;
			if (!currentWagonWrapper.assignment) {
				specialOrderAction = {
					tooltip: texts.SpecialOrder,
					icon: 'add.png',
					callback: 'showSpecialOrderPopup('
							+ currentWagon.wagonId + ')'
				};
			}

			var preLoadWagonAction = null;
			if (currentWagonWrapper.preLoadEnabled) {
				preLoadWagonAction = {
					tooltip: texts.PreLoadWagon,
					icon: 'add.png',
					callback: 'showPreLoadInputDialog('
							+ currentWagon.wagonId + ')'
				};
			}

			
			var loadingstate;
			var loadingdate;
			var rowCells = [];
			var netQuantityInTons;
			var cellNumber = 0;
			var status;
			if (currentWagon.wagonTrspCatCd != 2) {

				loadingdate = currentWagon.loadingTs;

				if (loadingdate == null) {
					loadingstate = texts.LoadingStateEmpty;
				}
				else if (currentWagon.netQuantity == null) {
					loadingstate = texts.LoadingStateLoading;
				}
				else {
					if (currentWagonWrapper.netQuantityInTons != null) {
						netQuantityInTons = currentWagonWrapper.netQuantityInTons;
					}
					else {
						// FIXME 29-Nov-2012/romek: What would be the proper
						// error handling?
						Logon.message
								.warning('Could not convert net quantity to tons (wagonId = '
										+ currentWagon.wagonId + ').');
						netQuantityInTons = 0;
					}

					loadingstate = netQuantityInTons + ' t';
				}

				loadingdate = currentWagonWrapper.loadingTSAsDayMonthHourMinute

				status = 'arriving';
				if (!currentWagonWrapper.isReturning) {
					status = 'arrived';
					if (currentWagon.loadId
							&& (currentWagon.loadingTs || (currentWagon.logonPackageTypeCd == null))) {
						status = 'loaded';
					}
					if (up) {
						if (isTransmitted(up)) {
							status = 'transmitted';
						}
					}
				}

				if (up && (up.orderItems.length > 1)) {
					height = 40 + 18 * (up.orderItems.length - 1);
				}
				else if (currentWagonWrapper.loads && (currentWagonWrapper.loads.length > 1)) {
					height = 40 + 18 * (currentWagonWrapper.loads.length - 1);
				}
				//Wagon Type
				rowCells[cellNumber++] = {
					tag: 'td',
					html: buildCell({
						line1: currentWagonWrapper.wagonDisplayNo,
						line2: currentWagon.wagonTypeName,
						divstyle: columnWidthStyles[0],
						info: buildWagonNote(
								currentWagon.wagonNoteDisposition,
								currentWagon.wagonNoteRailwaySpedition),
						action: specialOrderAction
					}),
					style: columnWidthStyles[0]
				};
				// Track position
				rowCells[cellNumber++] = {
					tag: 'td',
					html: buildCell({
						line1: buildTrackControl(currentWagonWrapper),
						line2: '',
						divstyle: columnWidthStyles[1] + ';padding-left:5px'
					}),
					style: columnWidthStyles[1]
				};
				// Container Type
				rowCells[cellNumber++] = {
					tag: 'td',
					html: buildCell({
						line1: '',
						line2: '',
						divstyle: columnWidthStyles[2]
					}),
					style: columnWidthStyles[2] + ';border-right:none'
				};
				rowCells[cellNumber++] = {
					tag: 'td',
					html: buildCell({
						line1: '',
						line2: '',
						divstyle: columnWidthStyles[3]
					}),
					style: columnWidthStyles[3] + ';border-left:none'
				};
				// Arrival Time / Loading Time
				rowCells[cellNumber++] = {
					tag: 'td',
					html: buildCell({
						line1: ((currentWagonWrapper.isReturning) ? currentWagonWrapper.plannedStartAsDayMonthYear
								: currentWagonWrapper.arrivalLastKnownStation),
						line2: loadingdate,
						divstyle: columnWidthStyles[4]
					}),
					style: columnWidthStyles[4]
				};
				var currentmaterial = null;
				if (currentWagon.wagonTrspCatCd == 4) {
					currentmaterial = (currentWagonWrapper.loads) ? buildLoadsLine(currentWagonWrapper.loads)
							: '';
				}
				else {
					currentmaterial = (currentWagon.materialName && currentWagon.tareQuantity) ? currentWagon.materialName
							: (currentWagon.lastMaterialName ? ('('
									+ currentWagon.lastMaterialName + ')')
									: ' - ');
				}
				// (last) material loading status (max. net)
				rowCells[cellNumber++] = {
					tag: 'td',
					html: buildCell({
						line1: currentmaterial,
						line2: loadingstate,
						tip1: currentmaterial,
						divstyle: columnWidthStyles[5]
								+ (height ? (';height: ' + (height - 2) + 'px') : ''),
						action: preLoadWagonAction
					}),
					style: columnWidthStyles[5]
				};
				tare = buildTareMeasure(currentWagonWrapper, null, up ? up.orderItems[0]
						: null);
				// Tare
				rowCells[cellNumber++] = {
					tag: 'td',
					html: buildCell({
						line1: tare[0],
						line2: '(' + currentWagon.tareWeightT + ' t)',
						divstyle: columnWidthStyles[6]
					}),
					style: columnWidthStyles[6]
				};
				// Gross
				rowCells[cellNumber++] = {
					tag: 'td',
					html: buildCell({
						line1: buildGrossMeasure(currentWagonWrapper, null, up,
								up ? up.orderItems[0]: null, tare[1],
								currentWagonWrapper.loads),
						line2: '(' + currentWagon.maxGrossWeightT + ' t)',
						divstyle: columnWidthStyles[7]
								+ (height ? (';height: ' + (height - 2) + 'px')
										: '')
					}),
					style: columnWidthStyles[7]
							+ ";border-right:3px solid #999"
				};
				// Order remark
				rowCells[cellNumber++] = {
					tag: 'td',
					html: buildCell({
						line1: buildOrderNumber(up ? up.assignmentPart : null) + buildEtranFields(currentWagonWrapper),
						line2: currentWagonWrapper.assignment ? currentWagonWrapper.assignment.assignment.carrierDispatchingNote
								: '-',
						divstyle: columnWidthStyles[8],
						tip2: currentWagonWrapper.assignment ? buildNotesPopup(
								currentWagonWrapper.assignment.assignment,
								up.assignmentPart): null
					}),
					style: columnWidthStyles[8]
				};
				if (currentWagonWrapper.assignment) {
					var matmismatch = null;
					if ((currentWagon.sapMaterialId && (up.orderItems[0].sapMaterialId != currentWagon.sapMaterialId))
							|| (currentWagon.productionPlantCd && (up.orderItems[0].productionPlantCd != currentWagon.productionPlantCd))) {
						matmismatch = {
							msg: errortexts.MaterialMismatch,
							callback: null
						};
					}
					if (currentWagonWrapper.possibleMaterialList.length === 0) {
						matmismatch = {
							msg: errortexts.MatCompatMismatch,
							callback: null
						};
					}
					var matline = up ? buildItemsLine(up.orderItems): '';
					// Product Order Quality
					rowCells[cellNumber++] = {
						tag: 'td',
						html: buildCell({
							line1: matline,
							line2: null,
							tip1: matline,
							divstyle: columnWidthStyles[9],
							error: matmismatch
						}),
						style: columnWidthStyles[9]
					};
					rowCells[cellNumber++] = {
						tag: 'td',
						html: buildCell({
							line1: (dp ? dp.plannedStartAsDayMonthHourMinute: '-'),
							line2: (up ? up.plannedStartAsDayMonthHourMinute: '-'),
							divstyle: columnWidthStyles[10],
							error: timeerror ? {
								msg: timeerror,
								callback: timecallback
							}: null
						}),
						style: columnWidthStyles[10]
					};
					rowCells[cellNumber++] = {
						tag: 'td',
						html: buildCell({
							line1: up ? up.deliveryFrom: '-',
							line2: up ? up.deliveryTo: '-',
							divstyle: columnWidthStyles[11]
						}),
						style: columnWidthStyles[11]
					};
					rowCells[cellNumber++] = {
						tag: 'td',
						html: buildCell({
							line1: (up ? up.assignmentPart.orderSoldtoName
									: '-'),
							line2: (up ? up.assignmentPart.shiptoName: '-'),
							divstyle: columnWidthStyles[12]
						}),
						style: columnWidthStyles[12]
					};
				}
				else {
					var prioList = buildMaterialPrioList(currentWagonWrapper, status);
					rowCells[cellNumber++] = {
						tag: 'td',
						html: buildCell({
							line1: buildMaterialList(currentWagonWrapper.possibleMaterialList),
							line2: null,
							divstyle: columnWidthStyles[9]
									+ (prioList ? ";color:#999": ""),
							tip1: prioList
						}),
						style: columnWidthStyles[9]
					};
					rowCells[cellNumber++] = {
						tag: 'td',
						colspan: 3,
						html: buildCell({
							line1: '('
									+ (currentWagonWrapper.isReturning ? (currentWagon.arrRailwayStationNo
											+ ' ' + currentWagon.arrRailwayStationName)
											: (currentWagon.railwayStationNo
													+ ' ' + currentWagon.railwayStationName))
									+ ')',
							divstyle: twlast3
						}),
						style: twlast3
					};
				}

				var tp = findPart(currentWagonWrapper.assignment, 2);

				rows[rowCount++] = {
					tag: 'tr',
					cls: 'wagon ' + status,
					id: 'trw' + currentWagon.wagonNo,
					children: rowCells
				};
				if (height) {
					rows[rowCount - 1].style = 'height: ' + height
							+ 'px;max-height: ' + height + 'px';
					height = null;
				}
			}
			else {
				status = 'arriving';
				var cwTare = currentWagon.tareWeightT;
				if (!currentWagonWrapper.isReturning) {
					status = 'arrived';
					for (var j = 0; j < currentWagonWrapper.containers.length; j++) {
						cwTare += currentWagonWrapper.containers[j].container.tareWeightT;
						if (currentWagonWrapper.containers[j].container.loadId
								&& (currentWagonWrapper.containers[j].container.loadingTs || (currentWagonWrapper.containers[j].container.logonPackageTypeCd == null))) {
							status = 'loaded';
						}
					}
					if (currentWagon.loadId
							&& (currentWagon.logonPackageTypeCd == null)) {
						status = 'loaded';
					}
					if (up) {
						if (isTransmitted(up)) {
							status = 'transmitted';
						}
					}
				}
				for (var containerIndex = 0; 
					 containerIndex < Math.max(currentWagonWrapper.containers.length, 1); 
					 containerIndex++) {
					rowCells = [];
					var currentContainerWrapper = currentWagonWrapper.containers[containerIndex];
					var currentContainer = (currentContainerWrapper) ? 
							currentContainerWrapper.container : null;
					loadingdate = (currentContainerWrapper) ? currentContainer.loadingTs
							: null;

					if (loadingdate == null) {
						loadingstate = texts.LoadingStateEmpty;
					}
					else if (currentContainerWrapper
							&& (currentContainer.netQuantity == null)) {
						loadingstate = texts.LoadingStateLoading;
					}
					else {
						if (currentContainerWrapper) {

							if (currentContainerWrapper.netQuantityInTons != null) {
								netQuantityInTons = currentContainerWrapper.netQuantityInTons;
							}
							else {
								// FIXME 29-Nov-2012/romek: What would be the
								// proper error handling?
								Logon.message
										.warning('Could not convert net quantity to tons '
												+ '(containerId = '
												+ currentContainer.containerId
												+ ').');
								netQuantityInTons = 0;
							}

							loadingstate = netQuantityInTons + ' t';
						}
						else {
							loadingstate = '-';
						}
					}

					if (loadingdate == null) {
						loadingdate = ' - ';
					}
					else {
						loadingdate = currentContainerWrapper.loadingTSAsDayMonthHourMinute
					}

					cellNumber = 0;
					if (containerIndex === 0) {

						rowCells[cellNumber++] = {
							tag: 'td',
							rowspan: currentWagonWrapper.containers.length,
							style: columnWidthStyles[0],
							html: buildCell({
								line1: currentWagonWrapper.wagonDisplayNo,
								line2: currentWagon.wagonTypeName,
								divstyle: columnWidthStyles[0],
								info: buildWagonNote(
										currentWagon.wagonNoteDisposition,
										currentWagon.wagonNoteRailwaySpedition),
								action: specialOrderAction
							})
						};
						rowCells[cellNumber++] = {
							tag: 'td',
							rowspan: currentWagonWrapper.containers.length,
							html: buildCell({
								line1: buildTrackControl(currentWagonWrapper),
								line2: '',
								divstyle: columnWidthStyles[1]
										+ ';padding-left:5px'
							}),
							style: columnWidthStyles[1]
						};

						var wagonicon = "<img src='"
								+ crhCommonUrl('../images/img_container_blue.png')
								+ "' onclick='showPopup("
								+ '"'
								+ "ContainersEditEntry.do?wagonId="
								+ currentWagon.wagonId
								+ '",""'
								+ ")' style='margin-top:8px' ext:qclass='dispoqtip dispoinfoqtip' ext:qtip='"
								+ texts.EditContainers + "' >";

						rowCells[cellNumber++] = {
							tag: 'td',
							rowspan: currentWagonWrapper.containers.length,
							html: buildCell({
								line1: wagonicon,
								line2: '',
								divstyle: columnWidthStyles[2]
										+ ';padding-left:1px;border-right:none;overflow:visible'
							}),
							style: columnWidthStyles[2]
									+ ';border-right:none;overflow-x:visible;'
						};
					}
					var containertip = currentContainerWrapper ? (currentContainer.containerName
							+ '<br>'
							+ currentContainer.containerNo
							+ '<br>' + currentContainer.containerTypeName)
							: '';
					rowCells[cellNumber++] = {
						tag: 'td',
						style: columnWidthStyles[3] + ';border-left:none',
						html: (currentContainerWrapper ? buildCell({
							line1: currentContainer.containerName,
							line2: currentContainer.containerTypeName,
							tip1: containertip,
							tip2: containertip,
							divstyle: columnWidthStyles[3]
						})
								: buildCell({
									line1: null,
									line2: null,
									divstyle: columnWidthStyles[3]
								}))
					};
					rowCells[cellNumber++] = {
						tag: 'td',
						html: buildCell({
							line1: ((currentWagonWrapper.isReturning) ? currentWagonWrapper.plannedStartAsDayMonthHourMinute
									: currentWagonWrapper.arrivalLastKnownStation),
							line2: loadingdate,
							divstyle: columnWidthStyles[4]
						}),
						style: columnWidthStyles[4]
					};
					rowCells[cellNumber++] = {
						tag: 'td',
						html: buildCell({
							line1: currentContainerWrapper ? ((currentContainer.materialName && currentContainer.tareQuantity) ? currentContainer.materialName
									: (currentContainer.lastMaterialName ? ('('
											+ currentContainer.lastMaterialName + ')')
											: '-'))
									: '-',
							line2: loadingstate
									+ (currentContainerWrapper ? (' ('
											+ currentContainer.maxNetWeightT + ' t)')
											: ''),
							divstyle: columnWidthStyles[5]
						}),
						style: columnWidthStyles[5]
					};
					up = findPart(currentWagonWrapper.assignment, 3);
					var orderItem = null;
					if (up) {
						for (l = 0; l < up.orderItems.length; l++) {
							if ((currentContainerWrapper && (up.orderItems[l].loadId == currentContainer.loadId))
									|| (!currentContainerWrapper && (up.orderItems[l].loadId == currentWagon.loadId))) {
								orderItem = up.orderItems[l];
								break;
							}
						}
					}
					var tare = buildTareMeasure(currentWagonWrapper,
							currentContainerWrapper, orderItem);
					rowCells[cellNumber++] = {
						tag: 'td',
						html: buildCell({
							line1: tare[0],
							line2: '(' + Math.round(cwTare * 100) / 100
									+ ' t)',
							divstyle: columnWidthStyles[6]
						}),
						style: columnWidthStyles[6]
					};
					rowCells[cellNumber++] = {
						tag: 'td',
						html: buildCell({
							line1: buildGrossMeasure(currentWagonWrapper,
									currentContainerWrapper, up, orderItem,
									tare[1]),
							line2: '(' + currentWagon.maxGrossWeightT
									+ ' t)',
							divstyle: columnWidthStyles[7]
						}),
						style: columnWidthStyles[7]
								+ ";border-right:3px solid #999"
					};
					if (containerIndex === 0) {
						rowCells[cellNumber++] = {
							tag: 'td',
							rowspan: currentWagonWrapper.containers.length,
							html: buildCell({
								line1: buildOrderNumber(up ? up.assignmentPart : null),
								line2: currentWagonWrapper.assignment ? currentWagonWrapper.assignment.assignment.carrierDispatchingNote
										: '-',
								divstyle: columnWidthStyles[8],
								tip2: currentWagonWrapper.assignment ? buildNotesPopup(
										currentWagonWrapper.assignment.assignment,
										up.assignmentPart): null
							}),
							style: columnWidthStyles[8]
						};
					}
					if (currentWagonWrapper.assignment) {
						rowCells[cellNumber++] = {
							tag: 'td',
							html: orderItem ? buildCell({
								line1: orderItem.materialName,
								line2: orderItem.orderedQuantity + ' '
										+ orderItem.uomName,
								divstyle: columnWidthStyles[9]
							})
									: buildCell({
										line1: '-',
										divstyle: columnWidthStyles[9],
										error: (currentContainerWrapper && (currentContainer.loadCnt > 1)) ? {
											msg: errortexts.MaterialMismatch,
											callback: null
										}
												: null
									}),
							style: columnWidthStyles[9]
						};
						if (containerIndex === 0) {
							rowCells[cellNumber++] = {
								tag: 'td',
								rowspan: currentWagonWrapper.containers.length,
								html: buildCell({
									line1: (dp ? dp.plannedStartAsDayMonthHourMinute
											: '-'),
									line2: (up ? up.plannedStartAsDayMonthHourMinute
											: '-'),
									divstyle: columnWidthStyles[10],
									error: timeerror ? {
										msg: timeerror,
										callback: timecallback
									}: null
								}),
								style: columnWidthStyles[10]
							};
							rowCells[cellNumber++] = {
								tag: 'td',
								rowspan: currentWagonWrapper.containers.length,
								html: buildCell({
									line1: up ? up.deliveryFrom
											: '-',
									line2: up ? up.deliveryTo
											: '-',
									divstyle: columnWidthStyles[11]
								}),
								style: columnWidthStyles[11]
							};
							rowCells[cellNumber++] = {
								tag: 'td',
								rowspan: currentWagonWrapper.containers.length,
								html: buildCell({
									line1: (up ? up.assignmentPart.orderSoldtoName
											: '-'),
									line2: (up ? up.assignmentPart.shiptoName
											: '-'),
									divstyle: columnWidthStyles[12]
								}),
								style: columnWidthStyles[12]
							};
						}
					}
					else {
						var material = currentContainerWrapper ? buildMaterialList(currentContainerWrapper.possibleMaterialList)
								: '-';
						var popup = currentContainerWrapper ? buildMaterialPrioList(
								currentWagonWrapper, status)
								: null;
						rowCells[cellNumber++] = {
							tag: 'td',
							html: buildCell({
								line1: material,
								line2: null,
								divstyle: columnWidthStyles[9]
										+ (popup ? ";color:#999": ""),
								tip1: popup
							}),
							style: columnWidthStyles[9]
						};
						if (containerIndex === 0) {
							rowCells[cellNumber++] = {
								tag: 'td',
								rowspan: currentWagonWrapper.containers.length,
								colspan: 3,
								html: buildCell({
									line1: '('
											+ (currentWagonWrapper.isReturning ? (currentWagon.arrRailwayStationNo
													+ ' ' + currentWagon.arrRailwayStationName)
													: (currentWagon.railwayStationNo
															+ ' ' + currentWagon.railwayStationName))
											+ ')',
									divstyle: twlast3
								}),
								style: twlast3
							};
						}
					}
					rows[rowCount++] = {
						tag: 'tr',
						cls: 'wagon ' + status,
						id: (currentContainerWrapper ? ('trc' + currentContainer.containerNo)
								: ('trw' + currentWagon.wagonNo)),
						children: rowCells
					};
				}
			}
		}

		var domTable = {
			tag: 'div',
			cls: 'dispotablehead',
			id: 'wagonsTable',
			style: 'overflow-y:scroll; top:40px; left:0px;width:' + (w + 18)
					+ 'px;height: ' + (parent.offsetHeight - 40 - 17) + 'px',
			children: [{
				id: 'wagonsTable',
				cls: 'dispotable',
				tag: 'table',
				cellpadding: 0,
				cellspacing: 0,
				children: [{
					tag: 'body',
					children: rows
				}]
			}]
		};

		Ext.DomHelper.append(parent, domTable);

		var wt = Ext.get('wagonsTable');
		wt.dom.scrollTop = this.scrolltop;
		this.scrolltop = wt.dom.scrollTop; // correct this.scrolltop in case
											// the new list is shorter and does
											// not allow so much scrolling
		wt.on('scroll', function(e, o) {
			this.scrolltop = o.scrollTop;
		}, this);

		var tabletop = wt.getTop();
		var row = null;
		var ddselect = null;
		for (var m = 0; m < wagons.length; m++) {
			if (wagons[m].containers.length === 0) {
				row = Ext.get('trw' + wagons[m].wagon.wagonNo);
				row.dom.wagon = wagons[m];
				ddselect = {
					tag: 'div',
					cls: 'ddselect',
					style: 'position:absolute; height: '
							+ (row.getHeight() - 1)
							+ 'px;-moz-box-sizing:border-box;left:1px;top: '
							+ (row.getTop() - tabletop + this.scrolltop)
							+ 'px;width:' + (row.getWidth() - 2) + 'px'
				};
				Ext.DomHelper.insertBefore(row.dom.firstChild.firstChild,
						ddselect);
				if (authrail) {
					row.dom.dd = row.initDDTarget('trw'
							+ wagons[m].wagon.wagonNo);
				}
				row.on("click", wagonClick);
			}
			else {
				var selecttarget = null;
				for (var n = 0; n < wagons[m].containers.length; n++) {
					row = Ext.get('trc'
							+ wagons[m].containers[n].container.containerNo);
					if (n === 0) {
						ddselect = {
							tag: 'div',
							cls: 'ddselect',
							style: 'position:absolute; height: '
									+ (40 * wagons[m].containers.length - 1)
									+ 'px;-moz-box-sizing:border-box;left:1px;top: '
									+ (row.getTop() - tabletop + this.scrolltop)
									+ 'px;width:' + (row.getWidth() - 2) + 'px'
						};
						Ext.DomHelper.insertBefore(
								row.dom.firstChild.firstChild, ddselect);
						selecttarget = row.dom;
					}
					row.dom.selecttarget = selecttarget;
					row.dom.wagon = wagons[m];
					row.dom.container = wagons[m].containers[n];
					if (authrail) {
						row.dom.dd = row
								.initDDTarget('trc'
										+ wagons[m].containers[n].container.containerNo);
					}
					row.on("click", wagonClick);
					checkSaveButtonState('c'
							+ wagons[m].containers[n].container.containerId,
							wagons[m].wagon.wagonNo);
				}
			}
			var trackInput = Ext.get('track' + wagons[m].wagon.wagonNo);
			if (trackInput) {
				initTrackDropdown(wagons[m]);
				initPosDropdown('' + wagons[m].wagon.wagonNo);
				checkSaveButtonState('w' + wagons[m].wagon.wagonId,
						wagons[m].wagon.wagonNo);
			}
		}

		if (focus !== null) {
			var el = Ext.get(focus);
			if (el != null) {
				el.dom.focus();
				if (el.dom.createTextRange) {
					tr = el.dom.createTextRange();
					tr.moveStart('textedit', 1);
					tr.select();
				}
			}
			else {
				focus = null;
			}
		}

	};

	this.beforeDD = function(order) {
		var list = Ext.query("tr[class*='wagon']", Ext.getDom('wagonsTable'));
		for (var i = 0; i < list.length; i++) {
			var wagonrow = list[i];
			if (!wagonrow.wagon) {
				continue;
			}
			if ((wagonrow.wagon.wagon.lastKnownRailwayStationId != order.order.depRailwayStationId)
					&& (wagonrow.wagon.wagon.returnRailwayStationId != order.order.depRailwayStationId)) {
				Ext.get(wagonrow).addClass("disabled");
				continue;
			}
			if (wagonrow.wagon.assignment != null) {
				Ext.get(wagonrow).addClass("disabled");
				continue;
			}
			if (wagonrow.wagon.wagon.wagonTrspCatCd == 2) {
				if (wagonrow.wagon.containers.length < order.materialItems.length) {
					Ext.get(wagonrow).addClass("disabled");
					continue;
				}
				var add = true;
				for (var k = 0; k < order.materialItems.length; k++) {
					if (order.materialItems[k].logonPackageTypeCd
							&& order.materialItems[k].uomCd != "CON"
							&& order.materialItems[k].uomCd != "TO") {
						Ext.get(wagonrow).addClass("disabled");
						add = false;
						break;
					}
					for (var p = 0; p < wagonrow.wagon.containers.length; p++) {
						if (wagonrow.wagon.containers[p].container.sapMaterialId
								&& wagonrow.wagon.containers[p].container.netQuantity
								&& (wagonrow.wagon.containers[p].container.sapMaterialId != order.materialItems[p].sapMaterialId)) {
							Ext.get(wagonrow).addClass("disabled");
							continue;
						}
						if (wagonrow.wagon.containers[p].container.productionPlantCd
								&& wagonrow.wagon.containers[p].container.netQuantity
								&& (wagonrow.wagon.containers[p].container.productionPlantCd != order.materialItems[p].productionPlantCd)) {
							Ext.get(wagonrow).addClass("disabled");
							continue;
						}
					}
				}
				if (add) {
					wagonrow.dd.addToGroup("tro" + order.order.orderNumber);
				}
			}
			else if (wagonrow.wagon.wagon.wagonTrspCatCd == 4) {
				if (!order.isBag) {
					Ext.get(wagonrow).addClass("disabled");
					continue;
				}
				wagonrow.dd.addToGroup("tro" + order.order.orderNumber);
			}
			else {
				if (order.materialItems.length > 1) {
					Ext.get(wagonrow).addClass("disabled");
					continue;
				}
				if (wagonrow.wagon.wagon.sapMaterialId
						&& wagonrow.wagon.wagon.netQuantity
						&& (wagonrow.wagon.wagon.sapMaterialId != order.materialItems[0].sapMaterialId)) {
					Ext.get(wagonrow).addClass("disabled");
					continue;
				}
				if (wagonrow.wagon.wagon.productionPlantCd
						&& wagonrow.wagon.wagon.netQuantity
						&& (wagonrow.wagon.wagon.productionPlantCd != order.materialItems[0].productionPlantCd)) {
					Ext.get(wagonrow).addClass("disabled");
					continue;
				}
				// FIXME 29-Nov-2012/romek: What is the proper fix for the
				// commented code below?
				// if (order.materialItems[0].logonPackageTypeCd &&
				// order.materialItems[0].uomCd != "BW" &&
				// order.materialItems[0].uomCd != "TO") {
				// Ext.get(wagonrow).addClass("disabled");
				// continue;
				// }
				var tp = findTypePrio(wagonrow.wagon, order.wagonTypeList);
				if (tp == null) {
					Ext.get(wagonrow).addClass("disabled");
					continue;
				}
				else {
					if (order.wagonTypeList[0].evaluationPriority == tp.evaluationPriority) {
						Ext.get(wagonrow).addClass("maxprio");
					}
				}

				wagonrow.dd.addToGroup("tro" + order.order.orderNumber);
			}
		}
	};

	this.afterDD = function(order) {
		var list = Ext.query("tr[class*='wagon']", Ext.getDom('wagonsTable'));
		for (i = 0; i < list.length; i++) {
			Ext.get(list[i]).removeClass("disabled");
			Ext.get(list[i]).removeClass("maxprio");
			if (!list[i].dd) {
				continue;
			}
			list[i].dd.removeFromGroup("tro" + order.order.orderNumber)
		}
	};

	this.canCancelSelection = function() {
		var aList = this.getSelectedAssignments();
		if (aList.length === 0) {
			return false;
		}
		if (authrailspecial) {
			return true;
		}
		for (var i = 0; i < aList.length; i++) {
			var up = findPart(aList[i], 3);
			if (up.assignmentPart.orderStatusCd > 11) {
				return false;
			}
		}
		return true;
	};

	this.canTransmitSelection = function() {
		var aList = this.getSelectedAssignments();
		if (aList.length === 0) {
			return false;
		}
		for (var i = 0; i < aList.length; i++) {
			var up = findPart(aList[i], 3);
			if (up.assignmentPart.orderStatusCd != 11) {
				return false;
			}
		}
		return true;
	};

	this.canSetUnloadedSelection = function() {
		var aList = this.getSelectedWagons();
		if (aList.length === 0) {
			return false;
		}
		for (var i = 0; i < aList.length; i++) {
			if (aList[i].isReturning && aList[i].wagon.incomingAssignTrStatusCd) {
				continue;
			}
			if (!aList[i].assignment) {
				return false;
			}
			var up = findPart(aList[i].assignment, 3);
			if (up.assignmentPart.orderStatusCd != 12
					&& up.assignmentPart.orderStatusCd != 13
					&& up.assignmentPart.orderStatusCd != 14
					&& up.assignmentPart.orderStatusCd != 18) {
				return false;
			}
		}
		return true;
	};

	this.canSetAtshiptoSelection = function() {
		var aList = this.getSelectedWagons();
		if (aList.length === 0) {
			return false;
		}
		for (var i = 0; i < aList.length; i++) {
			if (aList[i].isReturning && aList[i].wagon.incomingAssignTrStatusCd) {
				continue;
			}
			if (!aList[i].assignment) {
				return false;
			}
			var up = findPart(aList[i].assignment, 3);
			if (up.assignmentPart.orderStatusCd != 12
					&& up.assignmentPart.orderStatusCd != 18) {
				return false;
			}
		}
		return true;
	};

	this.draw();
	this.drawSelection();
}

function buildWagonTypeList(tl) {
	var res = {
		cell: null,
		popup: null
	};
	if (tl.length > 0) {
		var prevprio = tl[0].evaluationPriority;
		var cell_separator = '';
		var popup_separator = '';
		res = {
			cell: '<b>' + texts.Prio + '&nbsp;' + tl[0].evaluationPriority
					+ '</b> ',
			popup: '<table><tr><td><b>' + texts.Prio + '&nbsp;'
					+ tl[0].evaluationPriority + '</b></td><td>'
		};
		for (var i = 0; i < tl.length; i++) {
			if (prevprio != tl[i].evaluationPriority) {
				res.cell += '<br><b>' + texts.Prio + '&nbsp;'
						+ tl[i].evaluationPriority + '</b> ';
				res.popup += '</td></tr><tr><td><b>' + texts.Prio + '&nbsp;'
						+ tl[i].evaluationPriority + '</b></td><td>';
				prevprio = tl[i].evaluationPriority;
				cell_separator = '';
				popup_separator = '';
			}
			res.cell += cell_separator + tl[i].wagonTypeName;
			res.popup += popup_separator + tl[i].wagonTypeName;
			cell_separator = ',&nbsp;'
			popup_separator = '<br>';
		}
		res.popup += '</td></tr><table>';
	}
	return res;
}

function minutesToHour(minutes) {
	if (!minutes) {
		return '';
	}
	var hour = Math.floor(minutes / 60);
	var min = minutes - hour * 60;

	if (hour < 10) {
		hour = '0' + hour;
	}
	if (min < 10) {
		min = '0' + min;
	}
	return hour + ': ' + min;
}

function RailOrderList(parent, orders) {

	this.selection = orderList ? orderList.selection: null;
	this.scrolltop = orderList ? orderList.scrolltop: 0;

	this.draw = function() {
		var w = Ext.get(parent).getWidth() - 30;
		var columnWidthStyles = ['width:' + Math.round(w * 0.07) + 'px',
				'width:' + Math.round(w * 0.20) + 'px',
				'width:' + Math.round(w * 0.10) + 'px',
				'width:' + Math.round(w * 0.08) + 'px',
				'width:' + Math.round(w * 0.06) + 'px;',
				'width:' + Math.round(w * 0.15) + 'px;',
				'width:' + Math.round(w * 0.10) + 'px',
				'width:' + Math.round(w * 0.03) + 'px',
				'width:' + Math.round(w * 0.21) + 'px'];

		var headerCells = [];

		headerCells[0] = {
			tag: 'th',
			html: buildCell({
				line1: texts.Nr,
				divstyle: columnWidthStyles[0]
			}),
			style: columnWidthStyles[0]
		};
		headerCells[1] = {
			tag: 'th',
			html: buildCell({
				line1: texts.Comment,
				divstyle: columnWidthStyles[1]
			}),
			style: columnWidthStyles[1]
		};
		headerCells[2] = {
			tag: 'th',
			html: buildCell({
				line1: texts.Product,
				line2: texts.OrderedQuantity,
				divstyle: columnWidthStyles[2]
			}),
			style: columnWidthStyles[2]
		};
		headerCells[3] = {
			tag: 'th',
			html: buildCell({
				line1: texts.SwitchingTimeDep,
				line2: texts.First + '&nbsp;&nbsp;&nbsp;' + texts.Last,
				divstyle: columnWidthStyles[3],
				tip1: texts.SwitchingTimeExplanation,
				tip2: texts.SwitchingTimeExplanation
			}),
			style: columnWidthStyles[3]
		};
		headerCells[4] = {
			tag: 'th',
			html: buildCell({
				line1: texts.Delivery,
				line2: texts.From + '&nbsp;&nbsp;&nbsp;' + texts.To,
				divstyle: columnWidthStyles[4]
			}),
			style: columnWidthStyles[4]
		};
		headerCells[5] = {
			tag: 'th',
			html: buildCell({
				line1: texts.Customer,
				line2: texts.Shipto,
				divstyle: columnWidthStyles[5]
			}),
			style: columnWidthStyles[5]
		};
		headerCells[6] = {
			tag: 'th',
			html: buildCell({
				line1: texts.Carrier,
				line2: texts.DestRailway,
				divstyle: columnWidthStyles[6]
			}),
			style: columnWidthStyles[6]
		};
		headerCells[7] = {
			tag: 'th',
			html: buildCell({
				line1: texts.OnDemand,
				divstyle: columnWidthStyles[7]
			}),
			style: columnWidthStyles[7]
		};
		headerCells[8] = {
			tag: 'th',
			html: buildCell({
				line1: texts.PossibleWagonTypes,
				divstyle: columnWidthStyles[8]
			}),
			style: columnWidthStyles[8]
		};

		var dispoTableHeadDiv = {
			tag: 'div',
			cls: 'dispotablehead',
			style: 'width:' + w + 'px',
			children: [{
				tag: 'table',
				cls: 'dispotable',
				children: [{
					tag: 'thead',
					children: [{
						tag: 'tr',
						children: headerCells
					}]
				}]
			}]
		};

		var head = Ext.DomHelper.overwrite(parent, dispoTableHeadDiv);

		var rows = [];
		for (var q = 0; q < orders.length; q++) {
			var rowCells = [];
			rowCells[0] = {
				tag: 'td',
				html: buildCell({
					line1: buildOrderNumber(orders[q].order),
					line2: '',
					divstyle: columnWidthStyles[0]
				}),
				style: columnWidthStyles[0]
			};
			rowCells[1] = {
				tag: 'td',
				html: buildCell({
					line1: buildCarrierNotes(orders[q]),
					line2: null,
					tip1: buildNotesPopup(orders[q].order, orders[q].order,
							'', orders[q]),
					divstyle: columnWidthStyles[1]
				}),
				style: columnWidthStyles[1]
			};
			var text = buildItemsLine(orders[q].items);
			rowCells[2] = {
				tag: 'td',
				html: buildCell({
					line1: text,
					line2: null,
					tip1: text,
					divstyle: columnWidthStyles[2]
				}),
				style: columnWidthStyles[2]
			};
			var timeerror = null;
			var planerror = null;
			if (orders[q].order.arrOffsetDays !== null) {
				if (orders[q].order.deliveryToDat.clearTime(true).add(Date.DAY,
						-orders[q].order.arrOffsetDays).getTime() < (new Date())
						.clearTime(true).getTime()) {
					timeerror = errortexts.CantArrive;
				}
			}
			else {
				planerror = errortexts.NoPlan;
			}
			var swtimepopup = null;
			if (orders[q].firstSwitchingTimeDep) {
				swtimepopup = '<b>' + texts.SwitchingTimeArr + '<b><br>'
						+ orders[q].firstSwitchingTimeArrival
						+ '<br>'
						+ orders[q].lastSwitchingTimeArrival
			}
			rowCells[3] = {
				tag: 'td',
				html: buildCell({
					line1: orders[q].firstSwitchingTimeDeparture,
					line2: orders[q].lastSwitchingTimeDeparture,
					divstyle: columnWidthStyles[3],
					tip1: swtimepopup,
					tip2: swtimepopup,
					error: planerror ? {
						msg: planerror,
						callback: null
					}: null
				}),
				style: columnWidthStyles[3]
			};
			rowCells[4] = {
				tag: 'td',
				html: buildCell({
					line1: orders[q].deliveryFrom,
					line2: orders[q].deliveryTo,
					divstyle: columnWidthStyles[4],
					error: timeerror ? {
						msg: timeerror,
						callback: null
					}: null
				}),
				style: columnWidthStyles[4]
			};
			rowCells[5] = {
				tag: 'td',
				html: buildCell({
					line1: orders[q].order.soldtoName,
					line2: orders[q].order.shiptoName,
					divstyle: columnWidthStyles[5]
				}),
				style: columnWidthStyles[5]
			};
			rowCells[6] = {
				tag: 'td',
				html: buildCell({
					line1: orders[q].order.carrierName,
					line2: orders[q].order.arrRailwayStationNo + ' '
							+ orders[q].order.arrRailwayStationName,
					divstyle: columnWidthStyles[6]
				}),
				style: columnWidthStyles[6]
			};
			rowCells[7] = {
				tag: 'td',
				html: buildCell({
					line1: orders[q].order.onHoldOrderAndLockedFlag ? ("<img src='"
							+ crhCommonUrl('../images/active.png') + "'>")
							: '-',
					line2: '',
					divstyle: columnWidthStyles[7]
				}),
				style: columnWidthStyles[7]
			};
			var wl = buildWagonTypeList(orders[q].wagonTypeList);
			rowCells[8] = {
				tag: 'td',
				html: buildCell({
					line1: wl.cell,
					line2: null,
					divstyle: columnWidthStyles[8],
					tip1: wl.popup
				}),
				style: columnWidthStyles[8]
			};

			rows[q] = {
				tag: 'tr',
				cls: 'railorder '
						+ (orders[q].order.onHoldOrderAndLockedFlag ? 'disabled'
								: 'lowprio'),
				id: 'tro' + orders[q].order.orderNumber,
				children: rowCells
			};
		}

		var domTable = {
			tag: 'div',
			cls: 'dispotablehead',
			id: 'orders',
			style: 'overflow-y:scroll; top:40px; left:0px;width:' + (w + 18)
					+ 'px;height: ' + (parent.offsetHeight - 40 - 17) + 'px',
			children: [{
				id: 'ordersTable',
				cls: 'dispotable',
				tag: 'table',
				cellpadding: 0,
				cellspacing: 0,
				children: [{
					tag: 'body',
					children: rows
				}]
			}]
		};

		Ext.DomHelper.append(parent, domTable);

		var ot = Ext.get('orders');
		ot.dom.scrollTop = this.scrolltop;
		ot.on('scroll', function(e, o) {
			this.scrolltop = o.scrollTop;
		}, this);

		for (var i = 0; i < orders.length; i++) {
			var row = Ext.get('tro' + orders[i].order.orderNumber);
			row.dom.order = orders[i];

			if (!authrail || (orders[i].order.arrOffsetDays === null)
					|| orders[i].order.onHoldOrderAndLockedFlag) {
				continue;
			}

			attachDragAndDropToRail(row, orders[i].order);
		}

	};

	function attachDragAndDropToRail(row, currentOrder) {
		var dd = row.initDDProxy("tro" + currentOrder.orderNumber, null, {
			draw: function() {
				var el = Ext.get(this.getDragEl());
				var order = this.getEl().order;
				el.setWidth(500);
				el.setHeight(37);
				el.dom.style.border = '2px solid black';
				var rowCells = [];
				rowCells[0] = {
					tag: 'td',
					html: order.order.orderNumber,
					style: 'width:125px'
				};
				rowCells[1] = {
					tag: 'td',
					html: buildItemsLine(order.items),
					style: 'width:150px'
				};
				rowCells[2] = {
					tag: 'td',
					html: order.order.soldtoName + '<br>'
							+ order.order.shiptoName,
					style: 'width:150px'
				};
				rowCells[3] = {
					tag: 'td',
					html: order.deliveryFromAsDayMonthYear4 + '<br>'
							+ order.deliveryToAsDayMonthYear4,
					style: 'width:75px'
				};
				var row = {
					tag: 'tr',
					cls: 'order',
					children: rowCells
				};
				var domTable = {
					cls: 'dispotable',
					tag: 'table',
					cellpadding: 0,
					cellspacing: 0,
					children: [{
						tag: 'body',
						children: [row]
					}]
				};

				Ext.DomHelper.overwrite(el.dom, domTable);
			},
			startDrag: function(x, y) {
				wagonList.draggingInProgress = true;
				var order = this.getEl().order;
				wagonList.beforeDD(order);
				this.draw();
				this.top = Ext.get('wagonsTable').getTop();
				this.dispobottom = this.top
						+ Ext.get('wagonsTable').getHeight();
				this.ordertop = Ext.get('orders').getTop();
				this.resetConstraints();
				var groups = {};
				groups["tro" + order.order.orderNumber] = true;
				Ext.dd.DragDropMgr.refreshCache(groups);
			},
			setDragElPos: function(x, y) {
				var el = Ext.get(this.getDragEl());
				el.setLocation(x - 20, y - 10);
			},
			onInvalidDrop: function() {
				var order = this.getEl().order;
				wagonList.afterDD(order);
				refreshOrders();
				wagonList.draggingInProgress = false;
			},
			onDragEnter: function(e, id) {
				this.current = id;
				var list = Ext.select("tr", false, Ext.getDom("wagonsTable"));
				list.removeClass("ddhover");
				el = Ext.get(id);
				if (el.hasClass("wagon")) {
					if (el.dom.selecttarget) {
						el = Ext.get(el.dom.selecttarget);
					}
					if (!el.hasClass("disabled")) {
						el.addClass("ddhover");
						el.dom.firstChild.className = "ddselect";
					}
				}
			},
			onDragOut: function(e, id) {
				if (id == this.current) {
					el = Ext.get(id);
					if (el.hasClass("wagon")) {
						if (el.dom.selecttarget) {
							el = Ext.get(el.dom.selecttarget);
						}
						el.removeClass("ddhover");
					}
					this.current = null;
				}
			},
			onDragDrop: function(e, id) {
				if (e.getPageY() > this.ordertop) {
					this.onInvalidDrop(e);
					return;
				}
				el = this.getEl();
				if (!el.order) {
					return;
				} //check object type
				del = Ext.get(id);
				if (del.hasClass("wagon")) {
					if (!del.hasClass("disabled")
							&& ((del.getTop() + del.getHeight()) > this.top)
							&& (del.getTop() < this.dispobottom)) {
						del.removeClass("ddhover");
						RailDispoAjaxService.createAssignment(
								del.dom.wagon.wagon.wagonId,
								el.order.order.orderNumber,
								del.dom.wagon.wagon.plannedStart, function() {
									refreshOrders()
								});
						wagonList.afterDD(el.order);
						wagonList.draggingInProgress = false;
						return;
					}
				}
				refreshOrders();
				wagonList.afterDD(el.order);
				wagonList.draggingInProgress = false;
			}
		});
	}
	this.draw();
}
