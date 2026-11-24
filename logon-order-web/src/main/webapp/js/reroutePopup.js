	
	function rerouteAssignment() {
		var assignmentId;
		if (dispo && dispo.selection && dispo.selection.length == 1) {
			assignmentId = dispo.selection[0];
			hideLoad(assignmentId);
		}
		else {
			Logon.message.info(errortexts.OneAssignment);
			return;
		}
		var newOrderIds = [];
		var checkboxElements = document.getElementsByName("checkedOrderIds");
		for (var i = 0; i < checkboxElements.length; i++ ) {
			var checkboxElement = checkboxElements[i];
			if (checkboxElement.checked) {
				newOrderIds.push(parseInt(checkboxElement.value, 10));
			}
		}
		if (newOrderIds && newOrderIds.length > 0) {
			DispoAjaxService.rerouteAssignment(assignmentId, newOrderIds, refreshReroutedOrdersTrucks);
		}
		else {
			Logon.message.error(errortexts.RerouteUnsuccessful);
			return;
		}
	}
	
	function refreshReroutedOrdersTrucks(errorCd) {
		hidePopup();
		refreshOrders();
		refreshTrucks();
		if (errorCd == REROUTING_SUCCESSFUL) {
			Logon.message.success(errortexts.RerouteSuccessful);
		}
		else if (errorCd == REROUTING_ORDERS_NOMATCHING_IN_TOUR) {
			Logon.message.error(errortexts.TourNoMatching);
		}
		else if (errorCd == REROUTING_ORDERITEMS_NOMATCHING_WEIGHT_LESS_IN_LOAD) {
			Logon.message.error(errortexts.RerouteWeightLess);
		}
		else if (errorCd == REROUTING_ORDERITEMS_NOMATCHING_WEIGHT_MORE_IN_LOAD) {
			Logon.message.error(errortexts.RerouteWeightMore);
		}
		else if (errorCd == REROUTING_ORDERITEMS_NOMATCHING_MATERIAL_DIFFERENT) {
			Logon.message.error(errortexts.RerouteMaterialDiff);
		}
		else if (errorCd == REROUTING_ORDERITEMS_NOMATCHING_PRODPLANT_DIFFERENT) {
			Logon.message.error(errortexts.RerouteProdPlantDiff);
		}
		else if (errorCd == REROUTING_ORDERITEMS_NOMATCHING_STRUCTURE_DIFFERENT) {
			Logon.message.error(errortexts.RerouteStructureDiff);
		}
		else {
			Logon.message.error(errortexts.RerouteUnsuccessful);
		}
	}
	
	function toggleOtherOptionsDisabling(checkbox) {
		if (!isSelectedAssignmentTour()) {
			var checkboxElements = document.getElementsByName("checkedOrderIds");
			for (var i = 0; i < checkboxElements.length; i++ ) {
				var checkboxElement = checkboxElements[i];
				if (checkboxElement != checkbox) {
					checkboxElement.disabled = checkbox.checked;
				}
			}
		}
	}
	
	function isSelectedAssignmentTour() {
		var element = Ext.getDom("ai"+dispo.selection[0]);
		if (element && element.parentNode && element.parentNode.assignment) {
			var assignment = element.parentNode.assignment;
			if (assignment && assignment.orderCnt === 1) {
				return false;
			}
		}
		return true;
	}
	