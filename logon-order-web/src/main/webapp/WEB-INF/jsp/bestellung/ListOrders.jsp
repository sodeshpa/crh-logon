<%@ include file="../includeTLDs.jsp" %>


<%@page import="com.crh.logon.constants.Constant"%>

<%@page import="com.crh.logon.order.web.forms.bestellung.BestellungForm"%>

<c:set var="form" scope="page" value="${order_bestellung_BestellungForm}" />

<!-- ---------- -->
<!-- New orders -->
<!-- ---------- -->

<script type="text/javascript">

	Ext.form.ComboBox.prototype.emptyText =  '...';
	Ext.form.ComboBox.prototype.shadow = false;

	var editorLocatorMsg = '<fmt:message key="bestellung.error.editorlocator"/>';

	<%-- //TODO I think these transport modes are already anyway changed, an this seems not to be used at all anymore--%>
	var delTypeMap = {
		'1': '_10',
		'2': '_50',
		'3': '_50',
		'4': '_10',
		'5': '_00'
	}

	<%-- //TODO I think these transport modes are already anyway changed, an this seems not to be used at all anymore--%>
	var delType2TrspModeMap = {
			'1': '10',
			'2': '50',
			'3': '50',
			'4': '10',
			'5': '00'
	}

	<%-- Initialize the helper with the constants set from here ( we did NOT hardcode these in the .js itself) --%>
	logonPackageHandlingHelper.init('<%=Constant.PACKAGE_TYPE_BULK_POWDER%>', '<%=Constant.PACKAGE_TYPE_BULK_DUMPED%>', '<%=Constant.PACKAGE_TYPE_BBAG%>', '<%=Constant.PACKAGE_TYPE_SERVICE%>');

	function isRoadDirect(delTypeCd) {
		return delTypeCd == <%=Constant.DELIVERY_TYPE_ROAD_DIRECT%>;
	}

	function isTruckOrder(delTypeCd) {
		return (delTypeCd == <%=Constant.DELIVERY_TYPE_ROAD_DIRECT%> || delTypeCd == <%=Constant.DELIVERY_TYPE_BIMODAL_ROAD%>);
	}

	function isRailOrder(delTypeCd) {
		return (delTypeCd == <%=Constant.DELIVERY_TYPE_RAIL_DIRECT%> || delTypeCd == <%=Constant.DELIVERY_TYPE_BIMODAL_RAIL%>);
	}

	function isRailDirectOrder(delTypeCd) {
		return delTypeCd == <%=Constant.DELIVERY_TYPE_RAIL_DIRECT%>;
	}

	function isBimodalRailPartOrder(delTypeCd) {
		return delTypeCd == <%=Constant.DELIVERY_TYPE_BIMODAL_RAIL%>;
	}

	function isBimodalRailPartWithTruckPartOrder(delTypeCd) {
		return (delTypeCd == <%=Constant.DELIVERY_TYPE_BIMODAL_RAIL%> && editorList.length == 2);
	}

	function isBimodalTruckPartOrder(delTypeCd) {
		return delTypeCd == <%=Constant.DELIVERY_TYPE_BIMODAL_ROAD%>;
	}

	function isPickupOrder(delTypeCd) {
		return delTypeCd == <%=Constant.DELIVERY_TYPE_PICKUP%>;
	}

	function isBlockTrainPickupOrder(delTypeCd) {
		return delTypeCd == <%=Constant.DELIVERY_TYPE_BLOCKTRAIN_PICKUP%>;
	}

	function isShipPickupOrder(delTypeCd) {
		return delTypeCd == <%=Constant.DELIVERY_TYPE_SHIP_PICKUP%>;
	}

	function isShipOrBlockTrainPickupOrder(delTypeCd) {
		return isBlockTrainPickupOrder(delTypeCd) || isShipPickupOrder(delTypeCd);
	}

	function isSimpleVehicleOrder(delTypeCd) {
		return (delTypeCd == <%=Constant.DELIVERY_TYPE_SHIP%> || delTypeCd == <%=Constant.DELIVERY_TYPE_BLOCK_TRAIN%>);
	}

	function isServiceOnlyOrder(delTypeCd) {
		return delTypeCd == <%=Constant.DELIVERY_TYPE_SERVICE%>;
	}

	function isServiceOnlySelected() {
		return editorList[0].serviceOnlyOrderCheckbox && editorList[0].serviceOnlyOrderCheckbox.getValue();
	}

	function isReturnPickupOrder(delTypeCd) {
		return delTypeCd == <%=Constant.DELIVERY_TYPE_RETURN_PICKUP%>;
	}

	function isReturnDeliveredOrder(delTypeCd) {
		return delTypeCd == <%=Constant.DELIVERY_TYPE_RETURN_DELIVERED%>;
	}

	function findMapValueWithWildcard(map, key) {
		if (!Ext.empty(map[key])) {
			return map[key];
		}
		return map['*'];
	}


	var EditorButton = function() {
		this.id = 'adjustDeliveriesBtn';

		this.enable = function() {
			enableButton(this.id);
		};

		this.disable = function() {
			disableButton(this.id);
		};

		this.getElement = function() {
			return Ext.get(this.id);
		};

		this.getDom = function() {
			return Ext.getDom(this.id);
		};

		this.changeLabelTo = function(newLabel) {
			var btnEl = this.getElement();

			if (!this.originalLabel) {
				this.originalLabel = btnEl.getValue();
			}

			btnEl.set({'value': newLabel});
		};

		this.changeLabelBack = function() {
			if (this.originalLabel) {
				var btnEl = this.getElement();
				btnEl.set({'value': this.originalLabel});
				delete this.originalLabel;
			}
		};
	};

	var AdjustDeliveriesBtn = function() {
		this.id = 'adjustDeliveriesBtn';

		this.update = function() {

			var btEnabled = true;

			// If no sap matId
			if (Ext.get('editorList[0].orderItems[0].sapMatId') && 
					Ext.isEmpty(Ext.get('editorList[0].orderItems[0].sapMatId').getValue())) {
				btEnabled = false;
			}

			// Only road (1) and pickup(5) are to be adjusted
			//"== null" is equivalent to "=== undefined or === null"
			if (editorList == null || editorList[0] === undefined || (editorList[0].delTypeCombo.value != 1 && editorList[0].delTypeCombo.value != 5)) {
				btEnabled = false;
			}

			if (btEnabled == true) {
				this.enable();
			}
			else {
				this.disable();
			}

		};
	};

	AdjustDeliveriesBtn.prototype = new EditorButton();

	adjustDeliveriesBtn = new AdjustDeliveriesBtn();

	var SaveOrderBtn = function() {
		this.id = 'saveOrderBtn';

		this.update = function() {
			if (Ext.get('editorList[0].orderItems[0].sapMatId') && 
					!Ext.isEmpty(Ext.get('editorList[0].orderItems[0].sapMatId').getValue())) {
				this.enable();
			}
			else {
				this.disable();
			}
		};
	};

	SaveOrderBtn.prototype = new EditorButton();

	saveOrderBtn = new SaveOrderBtn();


	spinner = {
		show: function(idx) {
			Ext.getDom('sapMatSpinner' + idx).src = crhCommonUrl("../images/default/grid/loading.gif");
		},
		hide: function(idx) {
			Ext.getDom('sapMatSpinner' + idx).src = crhCommonUrl("../images/gray/s.gif");
		}
	};

	addOrderItemBtn = {
		wasEnabled: false,
		isAllItemValid: function() {
			return this.isAllItemFilled();
		},
		isAllItemFilled: function() {
			for (var i=0; i<editorList[0].orderItems.length; i++) {
				var item = editorList[0].orderItems[i];
				if (item != null) {
					if (item.sapMatCombo.getSelectedRecord() == null) {
						return false;
					}
					var sapMaterialNo = item.sapMatCombo.getSelectedRecord().data.sapMaterialNo;
					if (item.prodPlantCombo.getSelectedRecord() == null
							|| sapMaterialNo == '<%= Constant.SAP_MATERIAL_NO_LANDFILL_PLANNING %>'
							|| sapMaterialNo == '<%= Constant.SAP_MATERIAL_NO_AGG_PLANNING %>'
							|| item.uomCombo.getSelectedRecord() == null
							|| Ext.isEmpty(item.quantityStr.getValue())) {
						return false;
					}

					if (editorList[0] && (Ext.isEmpty(editorList[0].delTypeCombo.getValue()) || editorList[0].delTypeCombo.getValue() < 0 || isBimodalTruckPartOrder(editorList[0].delTypeCombo.getValue()))) {
						if (Ext.isEmpty(editorList[0].biRailItemCombo.getValue()) && !bimodalRailPartActive){
							return false;
						}
					}
				}
			}
			return true;
		},
		isBulkSelected: function() {
	    	for (var i=0; i<editorList[0].orderItems.length; i++) {
		    	var item = editorList[0].orderItems[i];
		    	if (item != null) {
					var currentComboRec = item.sapMatCombo.getSelectedRecord();
					if (currentComboRec && logonPackageHandlingHelper.isBulk(currentComboRec.data.logonPackageTypeCd)) {
						return true;
					}
		    	}
	    	}
		},
		isBagSelected: function() {
	    	for (var i=0; i<editorList[0].orderItems.length; i++) {
		    	var item = editorList[0].orderItems[i];
		    	if (item != null) {
					var currentComboRec = item.sapMatCombo.getSelectedRecord();
					if (currentComboRec && logonPackageHandlingHelper.isBag(currentComboRec.data.logonPackageTypeCd)) {
						return true;
					}
		    	}
	    	}
		},
	    enable: function() {
	    	// Only allow enabling if all order item editor row has a selected product, quantity, and UoM
	    	// Only allow if not bulk or bulk with <= 5 editors
	    	if (addOrderItemBtn.isAllItemValid() && !isServiceOnlySelected()
	    			&& ( (this.isBulkSelected() && editorList.length < 5) || !this.isBulkSelected()) ) {
		    	Ext.getDom('imgAddOrderItem').src = '<crh-common:url value="/images/silk/basket_add.png"/>';
		    	Ext.get('linkAddOrderItem').setStyle({'color': '#000000'});
				addOrderItemBtn.wasEnabled = true;
	    	}
	    	else{
	    		addOrderItemBtn.disable();
	    	}
	    },
	    disable: function(onlyWhileSaving) {
	    	Ext.getDom('imgAddOrderItem').src = '<crh-common:url value="/images/silk/basket_add_disabled.png"/>';
	    	Ext.get('linkAddOrderItem').setStyle({'color': '#BBBBBB'});
	    	if (!onlyWhileSaving) {
				addOrderItemBtn.wasEnabled = false;
	    	}
	    },
	    handleAfterValidationError: function() {
			// --- When a validation error occurs, restore the previous state ---
			if (addOrderItemBtn.wasEnabled) {
				addOrderItemBtn.enable();
			}
	    }
	};

	addServiceOrderItemBtn = {
		wasEnabled: false,
		isAllItemValid: function() {
			for (var i=0; i<editorList[0].orderItems.length; i++) {
				var item = editorList[0].orderItems[i];
				if (item != null) {
					if (item.sapMatCombo.getSelectedRecord() == null) {
						return false;
					}
					var sapMaterialNo = item.sapMatCombo.getSelectedRecord().data.sapMaterialNo;
					if ((item.prodPlantCombo.getSelectedRecord() == null
							&& sapMaterialNo != '<%= Constant.SAP_MATERIAL_NO_LANDFILL_PLANNING %>'
							&& sapMaterialNo != '<%= Constant.SAP_MATERIAL_NO_AGG_PLANNING %>')
							|| item.uomCombo.getSelectedRecord() == null
							|| Ext.isEmpty(item.quantityStr.getValue())) {
						return false;
					}
					if (editorList[0] && (Ext.isEmpty(editorList[0].delTypeCombo.getValue()) || editorList[0].delTypeCombo.getValue() < 0 || isBimodalTruckPartOrder(editorList[0].delTypeCombo.getValue()))) {
						if (isServiceOnlySelected()) {
							return true;
						}
						if (Ext.isEmpty(editorList[0].biRailItemCombo.getValue()) && !bimodalRailPartActive){
							return false;
						}
					}
				}
			}
			return true;
		},
	    enable: function() {
	    	// --- Only allow enabling if all order item editor row has a selected product, quantity, and UoM---
	    	if (addServiceOrderItemBtn.isAllItemValid()) {
		    	Ext.getDom('imgAddServiceOrderItem').src = '<crh-common:url value="/images/silk/basket_add.png"/>';
		    	Ext.get('linkAddServiceOrderItem').setStyle({'color': '#000000'});
				addServiceOrderItemBtn.wasEnabled = true;
	    	}
	    	else{
	    		addServiceOrderItemBtn.disable();
	    	}
	    },
	    disable: function(onlyWhileSaving) {
	    	Ext.getDom('imgAddServiceOrderItem').src = '<crh-common:url value="/images/silk/basket_add_disabled.png"/>';
	    	Ext.get('linkAddServiceOrderItem').setStyle({'color': '#BBBBBB'});
	    	if (!onlyWhileSaving) {
				addServiceOrderItemBtn.wasEnabled = false;
	    	}
	    },
	    handleAfterValidationError: function() {
			// --- When a validation error occurs, restore the previous state ---
			if (addServiceOrderItemBtn.wasEnabled) {
				addServiceOrderItemBtn.enable();
			}
	    }
	};

	// Bound to the two flags: autoPoolAssignmentFlag, autoCarrierAssignmentFlag's onclick event
	// and the pool and carrier dropdown's onselect event. Updates the two flag's disabled/enabled status
	// and the checked/unchecked status of those.
	function updateAutoFlags(variable, idx){
		// This 'variable' can be:
		//- the autoPoolAssignmentFlag dom
		//- the autoCarrierAssignmentFlag dom
		//- selected record of the pool or the selected
		//- selected record of the carrier dropdown

		var poolFlagDom = document.getElementById('editorList['+idx+'].autoPoolAssignmentFlag');
		var carrFlagDom = document.getElementById('editorList['+idx+'].autoCarrierAssignmentFlag');
		var poolIdDom = document.getElementById('editorList['+idx+'].orderHelper.poolId');
		var carrIdDom = document.getElementById('editorList['+idx+'].orderHelper.commCarrierId');

		poolFlagDom.disabled = false;
		carrFlagDom.disabled = false;

		if (!Ext.isEmpty(variable.data)) {
			var poolId = variable.data.poolId;
			var carrierId = variable.data.carrierId;
		}
		if (poolId == undefined){
			//if it did not came from the selected record.data of the dropdown
			var poolId = poolIdDom.value;
		}
		if (carrierId == undefined) {
			//if it did not came from the selected record.data of the dropdown
			var carrierId = carrIdDom.value;
		}

		//if it was called from the pool autoPoolAssignmentFlag
		var poolFlagClicked = (variable.id == poolFlagDom.id);
		var carrFlagClicked = (variable.id == carrFlagDom.id);

		//Disable/enable according to rules, check/uncheck according to rules
		if (Ext.isEmpty(poolId) || poolId == -1) {
			poolFlagDom.disabled = true;
			poolFlagDom.checked = false;
			carrFlagDom.disabled = true;
			carrFlagDom.checked = false;
			return;
		}

		if (Ext.isEmpty(carrierId) || carrierId == -1) {
			carrFlagDom.disabled = true;
			carrFlagDom.checked = false;
		}
		else {
			var currentEditor = editorList[idx];
			var selectedCarrRecord = currentEditor.commercialCarrierCombo.getSelectedRecord();
			if (selectedCarrRecord && selectedCarrRecord.data.carrierRequiresPlanningFlag) {
				carrFlagDom.disabled = true;
				carrFlagDom.checked = false;
			}
		}

		if (poolFlagClicked) {
			carrFlagDom.checked = false;
		}
		else if (!carrFlagClicked) {
			carrFlagDom.disabled = Ext.isEmpty(carrierId) || carrierId == -1;
			if (carrFlagDom.disabled) {
				carrFlagDom.checked = false;
			}
		}
		else if (carrFlagClicked && carrFlagDom.checked) {
			poolFlagDom.checked = true;
		}
	}

	function updateDurationTimeAndMap(spRec, delTypeCd, railDuration, railDistance, corrFactor) {

		mapLoadingInProgress = true;
		resetTravelTimeFields();

		if (!Ext.isEmpty(spRec.data.shippingpointId) && spRec.data.shippingpointId != -1) {
			//Ext.fly('divMapLoadMask').mask('Calculating route...', 'x-mask-loading');

			if (Ext.type(spRec.data.coordLon) == 'number' && Ext.type(spRec.data.coordLat) == 'number') {

				if (isRailOrder(delTypeCd)) {
					if (railDuration == -1) {
						saveOrderBtn.disable();
						adjustDeliveriesBtn.disable();
						Logon.message.warning('<fmt:message key="bestellung.error.railway.not.possible"/>');
					}
					else if (railDuration == -2) {
						saveOrderBtn.disable();
						adjustDeliveriesBtn.disable();
						Logon.message.warning('<fmt:message key="bestellung.error.no.railway.station.for.sp"/>');
					}

					if (railDistance == -1) {
						// TODO remove if unknown distance is OK
						//Logon.message.info('Distance information between these railway stations is not available');
					}
				}

				var updateRouteRes = logonGmap.updateRoute({
					spLat: spRec.data.coordLat,
					spLng: spRec.data.coordLon,
					shiptoLat: logonGmap.shiptoLat,
					shiptoLng: logonGmap.shiptoLng,
					durationCorrFactor: corrFactor,
					durationFieldId: 'durationTime',
					distanceFieldId: 'distanceMeters',
					delTypeCd: delTypeCd,
					railDurationMinutes: railDuration,
					railDistanceMeters: railDistance,
					callback: function() {

						updateTravelTimeFields('map');
						if (!(isRailOrder(this.delTypeCd) && this.railDurationMinutes <= -1)) {
							saveOrderBtn.update();
							adjustDeliveriesBtn.update();
						}

						var editor = editorList[selectedEditorIdx];

						if (editor) {
							editor.durationTime = document.getElementById('durationTime').value;
							editor.distanceMeters = document.getElementById('distanceMeters').value;
						}

					}
				});

				if (!updateRouteRes && !isPickupOrder(delTypeCd)) {
					saveOrderBtn.disable();
				}
			}
			else if (!isPickupOrder(delTypeCd)) {
				Logon.message.warning("Invalid Shipping-Point coordinates! " + spRec.data.coordLon + ", " + spRec.data.coordLat);
			}
			else {
				saveOrderBtn.update();
				adjustDeliveriesBtn.update();
				logonGmap.loadMap();
			}
		}
		else {
			// When '...' is selected
			if (isPickupOrder(delTypeCd)) {
				saveOrderBtn.update();
				adjustDeliveriesBtn.update();
			}
			else {
				saveOrderBtn.disable();
			}
			logonGmap.loadMap();
		}
	}

	function existingOrderAsTemplate(orderItemId, isCustomerOrder) {
		var idx = 0;
		var itemIdx = 0;
		var editor = editorList[idx];
		var item = editor.orderItems[itemIdx];

		if (addToCustorderMode.enabled) {
			// Turn off the previous "add to customer order" mode.
			addToCustorderMode.toggle(null);
		}

		spinner.show(idx);
		saveOrderBtn.disable();
		adjustDeliveriesBtn.disable();

		editor.delFromDat.disable();
		editor.delToDat.disable();
		editor.delFromTime.disable();
		editor.delToTime.disable();

		var custorderCustrefNoField = Ext.getDom('editorList[' + idx + '].custorderCustrefNo');
		custorderCustrefNoField.value = '';

		var getExistingOrderFunc = (!isCustomerOrder
				? OrderAjaxService.getExistingOrder
				: OrderAjaxService.getExistingCustorder);

		getExistingOrderFunc(
			orderItemId,
			function(result) {
				var srcOrder = result[0];
				var compositeIdentifier = result[1];
				if (compositeIdentifier == '-1' ) {
					Logon.message.warning(
							'<fmt:message key="bestellung.error.cloneerror"/>');
					return;
				}
				OrderAjaxService.getCollectedEditorData(
					{
						'editorIdx': idx,
						'orderItemIdx': itemIdx,
						'sapMatId': srcOrder.sapMaterialId,
						'supplyPlantCd': -1, // derived from shipping point
						'spId': (srcOrder.shippingpointId || -1),
						'poolId': (srcOrder.poolId || -1),
						'quantity': (srcOrder.orderedQuantity || 1),
						'delTypeCd': (srcOrder.deliveryTypeCd || -1),
						'prodPlantCd': (srcOrder.productionPlantCd || -1),
						'delFromDat': editor.getDelFromDateTime(),
						'delToDat': editor.getDelToDateTime(),
						'trspZoneCd': trspZoneCd,
						'uomCd': (srcOrder.orderedUomCd || -1),
						'sareaId': srcOrder.sareaId,
						'sapContractId': srcOrder.sapContractId,
						'sapContractItemId': srcOrder.sapContractItemId
					},
					function(res) {

						<%--
						// If not possible to order this product
						/*
						 * TODO (romek, Sep 1, 2011): Temporarily disabled. For now we do not know
						 * whether we will need this check or not, because it was based material
						 * conditions.
						 */
						if (res.orderItems[itemIdx].sapMatPrice ==
								'<%= BestellungForm.NO_LIST_PRICE %>') {
							spinner.hide(idx);
							// TODO litdict
							Logon.message.warning('For this customer, it is not possible to ' +
									'order \'' + srcOrder.materialTxt + '(' + srcOrder.materialNo +
									')' + '\' now.');
							saveOrderBtn.update();
							editor.delFromDat.enable();
							editor.delToDat.enable();
							editor.delFromTime.enable();
							editor.delToTime.enable();

							return;
						}
						--%>

						// --- SHIP-TO COMMENTS ---
						Ext.getDom('orderCommon.noteDelivery').value = nvl(srcOrder.noteDelivery);
						Ext.getDom('orderCommon.noteDispatching').value = nvl(srcOrder.noteDispatching);
						Ext.getDom('orderCommon.noteTruckDriver').value = nvl(srcOrder.noteTruckDriver);
						Ext.getDom('orderCommon.noteRailwayDisp').value =
								(!isCustomerOrder ? nvl(srcOrder.noteRailwayDisp) : null);

						// --- PAPER ---
						Ext.getDom('orderCommon.delivnoteSinglePaperFlag').disabled = false;
						Ext.getDom('orderCommon.delivnoteSinglePaperFlag').checked = srcOrder.delivnoteSinglePaperFlag;

						// --- FAX, MAIL, SMS ---
						Ext.getDom('orderCommon.orderConfFaxNumber').value = nvl(srcOrder.orderConfFaxNumber);
						Ext.getDom('orderCommon.orderConfFaxFlag').disabled = false;
						Ext.getDom('orderCommon.orderConfFaxFlag').checked = srcOrder.orderConfFaxFlag;
						Ext.getDom('orderCommon.delivnoteFaxFlag').disabled = false;
						Ext.getDom('orderCommon.delivnoteFaxFlag').checked = srcOrder.delivnoteFaxFlag;

						Ext.getDom('orderCommon.orderConfEmailAdresses').value =
								nvl(srcOrder.orderConfEmailAdresses);
						Ext.getDom('orderCommon.orderConfEmailFlag').disabled = false;
						Ext.getDom('orderCommon.orderConfEmailFlag').checked = srcOrder.orderConfEmailFlag;
						Ext.getDom('orderCommon.delivnoteEmailFlag').disabled = false;
						Ext.getDom('orderCommon.delivnoteEmailFlag').checked = srcOrder.delivnoteEmailFlag;

						Ext.getDom('orderCommon.orderConfSmsNumber').value = nvl(srcOrder.orderConfSmsNumber);
						Ext.getDom('orderCommon.orderConfSmsFlag').disabled = false;
						Ext.getDom('orderCommon.orderConfSmsFlag').checked = srcOrder.orderConfSmsFlag;

						// --- CONTACT PERSON ---
						Ext.getDom('editorList[0].order.contactPersonTxt').value =
								nvl(srcOrder.contactPersonTxt);

						// --- QUANTITY ---
						item.quantityStr.enable();
						item.quantityStr.setValue(srcOrder.orderedQuantity);

						// --- UOM ---
						if (!isCustomerOrder) {
							item.uomCombo.enable();
						}
						else {
							item.uomCombo.disable();
						}

						// --- SILO ---
						editor.siloCombo.enable();
						editor.siloCombo.preselectValue(srcOrder.shiptoSiloId, true);

						// --- DELIVERY TYPE
						if (!isCustomerOrder) {
							editor.delTypeCombo.enable();
						}
						else {
							editor.delTypeCombo.disable();
						}

						editor.delTypeCombo.preselectValue(srcOrder.deliveryTypeCd, true);

						// --- BILL-TO ---
						editor.updateBilltoCombo({
							storeData: res.billtoList,
							restrictionData: srcOrder
						});

						// --- PAYER ---
						editor.updatePayerCombo({
							storeData: res.payerList,
							restrictionData: srcOrder
						});

						// --- RELEASE SAP ---
						<%--Ext.getDom('editorList['+idx+'].order.holdBackSapFlag').disabled =
								(res.shiptoSarea.releaseSapOptionalFlag);
						Ext.getDom('editorList['+idx+'].order.holdBackSapFlag').checked =
								(!srcOrder.holdBackSapFlag || !res.shiptoSarea.releaseSapOptionalFlag);

						// --- FREE DELIVERY ---
						Ext.getDom('editorList['+idx+'].order.freeDeliveryFlag').disabled =
								(!res.shiptoSarea.freeDeliveryFlag);
						Ext.getDom('editorList['+idx+'].order.freeDeliveryFlag').checked =
								(res.shiptoSarea.freeDeliveryFlag ? srcOrder.freeDeliveryFlag : false);--%>

						// --- SAP material ---
						if (isCustomerOrder) {
							item.sapMatCombo.disable();
						}
						item.sapMatCombo.setValue(srcOrder.sapMaterialId);

						// --- Price ---
						updateMaterialInfo(idx, itemIdx, res.orderItems[itemIdx]);

						// --- Enable on delivery from / to ---
						editor.delFromDat.enable();
						editor.delToDat.enable();
						editor.delFromTime.enable();
						editor.delToTime.enable();

						// --- UOM ---
						item.uomCombo.store.loadData(res.uomList);
						item.uomCombo.enable();
						item.uomCombo.preselectValue(srcOrder.orderedUomCd, true);

						// --- Production plant ---
						item.prodPlantCombo.store.loadData(res.prodPlantList);
						item.prodPlantCombo.preselectValue(srcOrder.productionPlantCd, true);

						if (!isCustomerOrder) {
							item.prodPlantCombo.enable();
						}
						else {
							item.prodPlantCombo.disable();
						}

						// --- SP (Shipping Point) ---
						editor.spCombo.store.loadData(res.spList);
						editor.spCombo.preselectValue(srcOrder.shippingpointId, true);
						editor.spCombo.enable();

						toggleRailDeliveryData(0, res.railData);

						if (isBimodalTruckPartOrder(editor.delTypeCombo.getValue())) {
							// --- Bimodal Rail Order Item list ---
							editor.biRailItemCombo.store.loadData(res.biRailItemList);
							editor.biRailItemCombo.enable();
							toggleBiRailItemCombo(idx, true);
						}
						else {
							// --- LP ---
							item.lpCombo.store.loadData(res.lpList);
							item.lpCombo.preselectValue(srcOrder.loadingpointId, true);
							item.lpCombo.enable();
							toggleBiRailItemCombo(idx, false);
						}

						// --- Supply Plant ---
						editor.supplyPlantCombo.store.loadData(res.supplyPlantList);
						editor.supplyPlantCombo.preselectValue(editor.spCombo.getSelectedRecord() != null
								? editor.spCombo.getSelectedRecord().data.plantCd : null, true);
						editor.supplyPlantCombo.enable();

						// --- Pool and carrier: only for non-pickup ---
						if (!isPickupOrder(editor.delTypeCombo.getValue())) {

							// --- Pool ---
							editor.poolCombo.store.loadData(res.poolList);
							editor.poolCombo.preselectValue(srcOrder.poolId, true);
							editor.poolCombo.enable();
							Ext.getDom('editorList['+idx+'].autoPoolAssignmentFlag').checked = false;

							// --- Carrier ---
							editor.commercialCarrierCombo.store.loadData(res.commercialCarrierList);
							editor.commercialCarrierCombo.preselectValue(srcOrder.commCarrierId, true);
							editor.commercialCarrierCombo.enable();
							Ext.getDom('editorList['+idx+'].autoCarrierAssignmentFlag').checked = false;
						}

						updateAutoFlags('', 0);

						editor.durationCorrFactor = res.durationCorrectionFactor;
						durationCorrFactor = res.durationCorrectionFactor;

						jsonData[idx] = res;

						// Derived from the Shipping Point
						jsonData[idx].supplyPlantCd = editor.supplyPlantCombo.getValue();

						// --- Update Google map, depending on the SP ---
						if (!editor.spCombo.isEmptySelection()) {
							updateDurationTimeAndMap(editor.spCombo.getSelectedRecord(), res.delTypeCd,
									res.railData.durationMinutes, res.railData.distanceMeters,
									res.durationCorrectionFactor);
						}
						else {
							logonGmap.loadMap();

							if (!editor.delTypeCombo.isEmptySelection()
									&& editor.spCombo.store.data.length < 2 && !isServiceOnlyOrder(editor.delTypeCombo.getValue())) {
								Logon.message.warning(
										'<fmt:message key="bestellung.error.no.sp.for.product"/>');
							}
						}

						// Fill-in one-time shipto/soldto addresses
						<c:if test="${isLnfShipto or isOneTimeSoldto}">
							var getOneTimeDataFunc = (!isCustomerOrder
									? OrderAjaxService.getOneTimeDataForOrder
									: OrderAjaxService.getOneTimeDataForCustorder);

							var orderId = (!isCustomerOrder ? srcOrder.orderId : srcOrder.custorderId);

							getOneTimeDataFunc(
									orderId,
									function(data) {
										<c:if test="${isLnfShipto}">
											fillInLnfShiptoAddress(data);
										</c:if>

										<c:if test="${isOneTimeSoldto}">
											fillInOneTimeSoldtoAddress(data);
										</c:if>
									}
							);
						</c:if>

						adjustDeliveriesBtn.update();
						Logon.message.success('<fmt:message key="bestellung.success.clone"/>');
						spinner.hide(idx);

						if (isCustomerOrder) {
							addToCustorderMode.toggle(srcOrder);
						}
					}
				);
			}
		);
	}

	// UC-006 Create Single-Delivery Orders based on a Customer Order
	var addToCustorderMode = {
		enabled: false,
		custorderId: null,

		// Use carefully! (Eg. when reloading the New Order editor.)
		reset: function() {
			this.enabled = false;
			this.custorderId = null;
		},

		/**
		 * Turns "add to customer order" mode on or off.
		 * Note: a few parts of the logic is in function existingOrderAsTemplate.
		 *
		 * @param srcCustorder - the customer order data to "copy" can be null when turning off this mode
		 */
		toggle: function(srcCustorder) {
			var idx = 0;
			var itemIdx = 0;
			var editor = editorList[idx];
			var item = editor.orderItems[itemIdx];
			var custorderCustrefNoField = Ext.getDom('editorList[' + idx + '].custorderCustrefNo');

			if (!this.enabled) {
				adjustDeliveriesBtn.changeLabelTo(
						'<fmt:message key="bestellung.button.addtocustomerorder"/>');

				Ext.DomHelper.insertAfter(
					saveOrderBtn.id,
					{
						tag: 'input',
						type: 'button',
						id: 'cancelAddToCustorderButton',
						cls: 'ViewButtonStyle',
						style: 'margin-left: 4px',
						value: '<fmt:message key="buttons.cancel"/>',
						onclick: 'addToCustorderMode.toggle(null)'
					}
				);

				custorderCustrefNoField.value = nvl(srcCustorder.custorderCustrefNo);
				custorderCustrefNoField.disabled = true;

				this.enabled = true;
				this.custorderId = srcCustorder.custorderId;
			}
			else {
				adjustDeliveriesBtn.changeLabelBack();

				// When reloading divEditorsAllOrders, this button does not exist.
				var cancelAddToCustorderButton = Ext.get('cancelAddToCustorderButton');

				if (cancelAddToCustorderButton) {
					cancelAddToCustorderButton.remove();
				}

				editor.delTypeCombo.enable();
				item.uomCombo.enable();
				item.sapMatCombo.enable();
				item.prodPlantCombo.enable();

				custorderCustrefNoField.disabled = false;

				this.reset();
			}

			saveOrderBtn.update();
			adjustDeliveriesBtn.update();
		}
	};
	
	function loadDivOrderProcessing (params, message){
		var divOrderProcessing = Ext.get('divOrderProcessing');

		divOrderProcessing.load({
			url: '../bestellung/Bestellung.do',
			params: params,
			text: message,
			scripts: true,
			nocache: true
		});
	}

	function initiateSetCustorderToFinishedManuallyBest(custorderId) {
		var params = {};
		params.action = 'initiateSetCustorderToFinished';
		params.custorderId = custorderId;
		
		loadDivOrderProcessing(params, '<fmt:message key="extjs.processing"/>');
	}

	function confirmSetCustorderToFinishedManuallyBest(custorderId) {
		var params = {}, message = '<fmt:message key="extjs.processing"/>';
		
		params.action = 'confirmSetCustorderToFinished';
		params.custorderId = custorderId;
		
		if(typeof allOrders !== 'undefined') {
			allOrders.reload(false, function(){
				loadDivOrderProcessing(params, message);
			});
		}
		else {
			loadDivOrderProcessing(params, message);
		}
	}

	function toggleBiRailItemCombo(idx, show) {
		if (show) {
			editorList[idx].orderItems[0].lpCombo.hide();
			editorList[idx].biRailItemCombo.show();
		}
		else {
			editorList[idx].biRailItemCombo.hide();
			editorList[idx].orderItems[0].lpCombo.show();
		}
	}

	function toggleRailDeliveryData(idx, railData) {
		var isValid = false;

		var editor = editorList[idx];
		var isBimodalRailPart = isBimodalRailPartOrder(editor.delTypeCombo.getValue());
		var isBimodalRailPartWithTruckPart = isBimodalRailPartWithTruckPartOrder(editor.delTypeCombo.getValue());
		if (isRailOrder(editor.delTypeCombo.getValue())) {
			if (idx == 0) {
				editor.delFromTime.hide();
				editor.delToTime.hide();
			}
			hideDiv('divActualDeliveryDatWarn'+idx);
			hideDiv('divActualDeliveryDatOk'+idx);
			showDiv('divRailDeliveryData'+idx);
			Ext.getDom('divRailDeliveryData'+idx).title = '';
			if (railData.actualDeliveryDatArr) {
				Ext.getDom('divRailDeliveryData'+idx).title = 'Actual rail departure (switching) time: ' + railData.formattedActualDeliveryDatDep
					+ ' / Earliest rail departure (switching) time: ' + railData.formattedEarliestDeliveryDatDep;
				Ext.getDom('divActualDeliveryDatTxt'+idx).innerHTML = '<b><fmt:message key="bestellung.rail.order.actual.delivery.date.abbreviated"/>:</b> '
					+ railData.formattedActualDeliveryDatArr;
				Ext.getDom('divEarliestDeliveryDatTxt'+idx).innerHTML = '<b><fmt:message key="bestellung.rail.order.earliest.delivery.date.abbreviated"/>:</b> '
					+ railData.formattedEarliestDeliveryDatArr;
				var delFromDateTimeStr = Ext.getDom('deliveryFromDatStr[0]').value + ' ' + (isBimodalRailPartWithTruckPart ? Ext.getDom('deliveryFromTime[0]').value : '23:59');
				var delToDateTimeStr = Ext.getDom('deliveryToDatStr[0]').value + ' ' + (isBimodalRailPartWithTruckPart ? Ext.getDom('deliveryToTime[0]').value : '23:59');
				var delFromTooEarly = railData.earliestDeliveryDatArr > Date.parseDate(delFromDateTimeStr, 'd.m.y H:i')
				var delToTooEarly = railData.actualDeliveryDatArr > Date.parseDate(delToDateTimeStr, 'd.m.y H:i')
				if (railData.actualDeliveryDatArr < editorList[0].delFromDat.getValue() && !isBimodalRailPartWithTruckPart) {
					showDiv('divActualDeliveryDatWarn'+idx);
					Logon.message.warning('<fmt:message key="bestellung.error.rail.earliest.too.early"/>');
					isValid = true;
				}
				else if (delFromTooEarly || delToTooEarly) {
					showDiv('divActualDeliveryDatOk'+idx);
					isValid = true;
					if (delFromTooEarly) {
						editorList[0].delFromDat.setValue(railData.overriddenDeliveryFromDateTime);
						if (isBimodalRailPartWithTruckPart) {
							editorList[0].delFromTime.setValue(railData.overriddenDeliveryFromDateTime);
						}
						var changedFromStr = '<b>' + (isBimodalRailPartWithTruckPart ? delFromDateTimeStr : delFromDateTimeStr.substr(0,8)) + '</b>';
						var changedToStr = '<b>' + railData.formattedOverriddenDeliveryFromDateTime + '</b>';
						Logon.message.warning('<fmt:message key="bestellung.rail.overridden.delivery.from"/>'.replace('{0}', changedFromStr).replace('{1}', changedToStr));
					}
					if (delToTooEarly) {
						editorList[0].delToDat.setValue(railData.overriddenDeliveryToDateTime);
						if (isBimodalRailPartWithTruckPart) {
							editorList[0].delToTime.setValue(railData.overriddenDeliveryToDateTime);
						}
						var changedFromStr = '<b>' + (isBimodalRailPartWithTruckPart ? delToDateTimeStr : delToDateTimeStr.substr(0,8)) + '</b>';
						var changedToStr = '<b>' + railData.formattedOverriddenDeliveryToDateTime + '</b>';
						Logon.message.warning('<fmt:message key="bestellung.rail.overridden.delivery.to"/>'.replace('{0}', changedFromStr).replace('{1}', changedToStr));
					}
				}
				else {
					showDiv('divActualDeliveryDatOk'+idx);
					isValid = true;
				}
			}
			else {
				if (!editor.spCombo.isEmptySelection()) {
					showDiv('divActualDeliveryDatWarn'+idx);
					Ext.getDom('divActualDeliveryDatTxt'+idx).innerHTML = '<b><fmt:message key="bestellung.rail.order.actual.delivery.date.abbreviated"/>:</b> <fmt:message key="bestellung.rail.order.no.train"/>';
					Ext.getDom('divEarliestDeliveryDatTxt'+idx).innerHTML = '<b><fmt:message key="bestellung.rail.order.earliest.delivery.date.abbreviated"/>:</b> <fmt:message key="bestellung.rail.order.no.train"/>';
					Logon.message.warning('<fmt:message key="bestellung.error.railway.not.possible"/>');
				}
				else {
					Ext.getDom('divActualDeliveryDatTxt'+idx).innerHTML = '<b><fmt:message key="bestellung.rail.order.actual.delivery.date.abbreviated"/>:</b> <fmt:message key="bestellung.rail.order.select.sp"/>';
					Ext.getDom('divEarliestDeliveryDatTxt'+idx).innerHTML = '<b><fmt:message key="bestellung.rail.order.earliest.delivery.date.abbreviated"/>:</b> <fmt:message key="bestellung.rail.order.select.sp"/>';
				}
			}
		}
		else {
			if (idx == 0) {
				editorList[0].delFromTime.show();
				editorList[0].delToTime.show();
			}
			hideDiv('divRailDeliveryData'+idx);
			hideDiv('divActualDeliveryDatWarn'+idx);
			hideDiv('divActualDeliveryDatOk'+idx);
			Ext.getDom('divEarliestDeliveryDatTxt'+idx).innerHTML = 'n.a.';
			Ext.getDom('divActualDeliveryDatTxt'+idx).innerHTML = 'n.a.';
		}

		return isValid;
	}

	function parseDate(str) {
	    return Date.parseDate(str, 'd.m.yH:i');
	}

	function getDecimalPrecision(valueStr) {
		var indexOfPoint = valueStr.indexOf('.');
		if (indexOfPoint != -1) {
			var fraction = valueStr.substr(indexOfPoint);
			return fraction.length - 1;
		}
		return 0;
	}

	Ext.onReady(function(){

		// --- Checkboxes of BB and LS flags ---
		Ext.get('orderCommon.orderConfFaxNumber').on('change', function() {
			var isEmpty = Ext.isEmpty(Ext.getDom('orderCommon.orderConfFaxNumber').value);
			Ext.getDom('orderCommon.orderConfFaxFlag').disabled = isEmpty;;
			Ext.getDom('orderCommon.loadConfFaxFlag').disabled = isEmpty;
			Ext.getDom('orderCommon.delivnoteFaxFlag').disabled = isEmpty;
		});

		Ext.get('orderCommon.orderConfEmailAdresses').on('change', function() {
			var isEmpty = Ext.isEmpty(Ext.getDom('orderCommon.orderConfEmailAdresses').value);
			Ext.getDom('orderCommon.orderConfEmailFlag').disabled = isEmpty;
			Ext.getDom('orderCommon.loadConfEmailFlag').disabled = isEmpty;
			Ext.getDom('orderCommon.delivnoteEmailFlag').disabled = isEmpty;
		});

		Ext.get('orderCommon.orderConfSmsNumber').on('change', function() {
			var isEmpty = Ext.isEmpty(Ext.getDom('orderCommon.orderConfSmsNumber').value);
			Ext.getDom('orderCommon.orderConfSmsFlag').disabled = isEmpty;
			Ext.getDom('orderCommon.loadConfSmsFlag').disabled = isEmpty;
		});

	});

	function addShiptoAndNewOrderFormData(params, options) {

		var firstEditor = editorList[0];

		addFormElements(params, 'bestellungForm1'); // Fields above the editor rows

		// Multiple Bag orders
		for (var i = 0; i < firstEditor.orderItems.length; i++) {
			if (firstEditor.orderItems[i] != null) {
				addFormElements(params, 'bestellungForm2Item' + i);
				params['editorList[0].distanceMeters'] = firstEditor.distanceMeters;
				params['editorList[0].durationTime'] = firstEditor.durationTime;
				params['editorList[0].durationCorrFactor'] = firstEditor.durationCorrFactor;
			}
		}

		// Multiple Bulk orders
		for (var i = 0; i < editorList.length; i++) {
			addFormElements(params, 'bestellungForm2PerEditor' + i);
			params['editorList['+i+'].distanceMeters'] = editorList[i].distanceMeters;
			params['editorList['+i+'].durationTime'] = editorList[i].durationTime;
			params['editorList['+i+'].durationCorrFactor'] = editorList[i].durationCorrFactor;
		}

		// FIXME (lajos/03112012): the parameter below is not inserted by the method above so adding it manually otherwise it is not posted a kutyafajat neki
		params['editorList[0].quantityPerSdoUomCd'] = firstEditor.quantityPerSdoUomCd.getValue();

		if (bimodalRailPartActive) {
			addFormElements(params, 'bestellungForm3'); // Editor row for bimodal rail part
		}

		if (options && options.isCustomerOrder) {
			params['addToCustorderMode.enabled'] = addToCustorderMode.enabled;
			params['addToCustorderMode.custorderId'] = addToCustorderMode.custorderId;
		}
	}

	/**
	The selected editor will be masked. This is used when multiple bulk order editors are visible
	Item divs can be missing when items are deleted from the list. In this cases we have wholes.
	This code checks for those.
	*/
	function maskEditor(idx) {

		maskDiv('divOrderEditor' + idx);

		for (var itemIdx = 1; itemIdx < editorList[idx].orderItemCnt; itemIdx++) {
			maskDiv('divOrderItem_' + idx + '_' + itemIdx);
		}

	}

	/**
	The selected editor will be masked. This is used when multiple bulk order editors are visible
	Item divs can be missing when items are deleted from the list. In this cases we have wholes.
	This code checks for those.
	*/
	function unMaskEditor(idx) {

		unMaskDiv('divOrderEditor' + idx);

		for (var itemIdx = 1; itemIdx < editorList[idx].orderItemCnt; itemIdx++) {

			unMaskDiv('divOrderItem_' + idx + '_' + itemIdx);
		}
	}

	/**
	Takes the divId and looks up the corresponding div object. Then it will look up
	all the ExtJS mask divs and only apply the masking if there is none already created.
	*/
	function maskDiv(divId) {

		var div = Ext.get(divId);

		if (div) {
			var items = div.select('.ext-el-mask').elements;
			if (items.length == 0) {
				div.mask();
			}
		}

	}

	/**
	Takes the divId and looks up the corresponding div object. Then it will look up
	all the ExtJS mask divs and delete them.
	!!! This solution is used instead of div.unmask() because of it's bugs
	*/
	function unMaskDiv(divId) {

		var div = Ext.get(divId);

		if (div && div._mask) {
			div.unmask();
		}
	}

	/**
	All bulk editors will be unmasked
	*/
	function unMaskAllEditors() {
		for (var editorIdx = 0; editorIdx < editorList.length; editorIdx++) {
			unMaskEditor(editorIdx);
		}
	}

	/**
	Repaint all editors because of IE behavior
	*/
	function repaintEditors() {
		var div = Ext.get('bestellungForm2PerEditor0');
		div.parent().parent().repaint();
	}

	/**
	The given editor will be unmasked all others will be masked
	*/
	function applyMaskingForEditors(editorToBeUnMasked) {
		for (var editorIdx = 0; editorIdx < editorList.length; editorIdx++) {
			// Current editor should be unmasked
			if (editorIdx == editorToBeUnMasked) {
				unMaskEditor(editorIdx);
			}
			else {
				maskEditor(editorIdx);
			}
		}
		//var div = Ext.get('divOrderEditor0');
		//div.parent().repaint();
		repaintEditors();
	}

	function addOrderItem() {

    	if (!addOrderItemBtn.wasEnabled) {
    		return false;
    	}

    	if (!addOrderItemBtn.isAllItemFilled()) {
			Logon.message.warning('<fmt:message key="bestellung.error.required.additem.fields"/>');
			return false;
		}

		// Can be bulk or bag
		var packageType = null;

    	if (addOrderItemBtn.isBulkSelected()) {
    		addOrderItemBulk('addOrderItem');
		}
    	else if (addOrderItemBtn.isBagSelected()) {
    		addOrderItemBag('addOrderItem');
		}

	}

	<%-- When clicking the "Add other product" button in a bag case --%>
	function addOrderItemBag(action) {

		var editorIndex = 0; // always for bag casze
		var editor = editorList[editorIndex];

		saveOrderBtn.disable();
		addOrderItemBtn.disable();
		addServiceOrderItemBtn.disable();

		editor.orderItems[0].sapMatCombo.disable();
		editor.delTypeCombo.disable();
		editor.supplyPlantCombo.disable();
		editor.spCombo.disable();

		loadingMessage = 'Adding new bag product...'; // TODO LitDict

		var prevIdx = findPrevEditorIdx(editor.orderItemCnt, editor.orderItemCnt - 1);

		var prevEditorIdx = null;

		if (prevIdx == 0) {
			prevEditorIdx = 'divOrderEditor' + selectedEditorIdx;
		}
		else {
			prevEditorIdx = 'divOrderItem_' + selectedEditorIdx + '_' + prevIdx;
		}

		var newBagItemEditorIdx = 'divOrderItem_' + selectedEditorIdx + '_' + (prevIdx + 1);

		if (Ext.get(newBagItemEditorIdx) == null) {
			Ext.DomHelper.insertAfter(prevEditorIdx, {tag: 'div', id: newBagItemEditorIdx});
		}

		params = {};

		addShiptoAndNewOrderFormData(params);

		params.action = action;
		params.packageType = 'bag';

		var divOrderItem = Ext.get(newBagItemEditorIdx);

		divOrderItem.load({
			url: '../bestellung/Bestellung.do',
			params: params,
			text: '',
			scripts: true,
			nocache: true
		});

		editor.orderItemCnt++;

		adjustDeliveriesBtn.update();

	}

	<%-- When clicking the "Add other product" button in a bulk case--%>
	function addOrderItemBulk(action) {

		saveOrderBtn.disable();
		addOrderItemBtn.disable();
		addServiceOrderItemBtn.disable();

		maskEditorIndex = 0;

    	// We mask out all editors; iterating over the order items so we also catch service items
    	for (var editorIdx = 0; editorIdx < editorList.length; editorIdx++) {
    		maskEditor(editorIdx);
    	}

		// Need to check if a service editor was already added; if so we move one index further;
		// this is needed because a service editor is not making an entry in the 'editorList'

		var idx = editorList.length;
		var prevIdx = null;

		var prevEditorIdx = editorList.length -1
		var prevEditor = editorList[prevEditorIdx];

		 // item zero is located in the editor it self not as an item hence we start from 1
		var prevItemIndex = 1;

		if (Ext.get('divOrderItem_' + prevEditorIdx + '_' + prevItemIndex) == null) {
			prevIdx = 'divOrderEditor' + prevEditorIdx;
		}
		else {
			while (Ext.get('divOrderItem_' + prevEditorIdx + '_' + prevItemIndex) != null) {

				// Save the current prev index
				prevIdx = 'divOrderItem_' + selectedEditorIdx + '_' + prevItemIndex;

				// Increase for next trial
				prevItemIndex = prevItemIndex + 1;

			}
		}

		// --- Append DIV with new editor for order item ---
		loadingMessage = 'Adding new bulk product...'; // TODO LitDict
		if (Ext.getDom('divOrderEditor' + idx) == null) {
    		Ext.DomHelper.insertAfter(prevIdx, {tag: 'div', id: 'divOrderEditor' + idx});
		}

		params = {};

		addShiptoAndNewOrderFormData(params);

		params.action = action;
		params.packageType = 'bulk';

		var divOrderEditor = Ext.get('divOrderEditor' + idx);

		divOrderEditor.load({
			url: '../bestellung/Bestellung.do',
			params: params,
			text: '',
			scripts: true,
			nocache: true
		});

		adjustDeliveriesBtn.update();

		selectedEditorIdx = editorList.length;

	}

	<%-- When clicking the "Add service item" button --%>
	function addOrderItemService() {

    	if (!addServiceOrderItemBtn.wasEnabled) {
    		return false;
    	}

		if (!addServiceOrderItemBtn.isAllItemValid()) {
			Logon.message.warning('<fmt:message key="bestellung.error.required.additem.fields"/>');
			return false;
		}

		var editor = editorList[selectedEditorIdx];

		saveOrderBtn.disable();
		addOrderItemBtn.disable();
		addServiceOrderItemBtn.disable();

		editor.orderItems[0].sapMatCombo.disable();
		editor.delTypeCombo.disable();
		editor.supplyPlantCombo.disable();
		editor.spCombo.disable();

		// --- Append DIV with new editor for order item ---

		// Pattern: divOrderItem_selectedEditorIdx_itemIdx

		loadingMessage = 'Adding new service product...'; // TODO LitDict

		var prevIdx = findPrevEditorIdx(editor.orderItemCnt, editor.orderItemCnt - 1);

		var prevEditorIdx = null;

		if (prevIdx == 0) {
			prevEditorIdx = 'divOrderEditor' + selectedEditorIdx;
		}
		else {
			prevEditorIdx = 'divOrderItem_' + selectedEditorIdx + '_' + prevIdx;
		}

		var newServiceItemEditorIdx = 'divOrderItem_' + selectedEditorIdx + '_' + (prevIdx + 1);

		if (Ext.get(newServiceItemEditorIdx) == null) {
			Ext.DomHelper.insertAfter(prevEditorIdx, {tag: 'div', id: newServiceItemEditorIdx});
		}

		params = {};

		addShiptoAndNewOrderFormData(params);

		params.action = 'addServiceItem';
		params.packageType = 'service';
		params.selectedEditorIndex = selectedEditorIdx;

		var divOrderItem = Ext.get(newServiceItemEditorIdx);

		divOrderItem.load({
			url: '../bestellung/Bestellung.do',
			params: params,
			text: '',
			scripts: true,
			nocache: true
		});

		editor.orderItemCnt++;

		adjustDeliveriesBtn.update();

	}

	function findPrevEditorIdx(orderItemCnt) {

		// If first added order item OR more than one items but there is a whole we can fill
		if (orderItemCnt == 1 || Ext.get('divOrderItem_' + selectedEditorIdx + '_1') == null) {
			return 0;
		}

		var result = null;

		// This is when there is a whole somewhere in the list
		for (var prevIdx = 1; prevIdx < orderItemCnt; prevIdx++) {
			if (Ext.get('divOrderItem_' + selectedEditorIdx + '_' + prevIdx) == null) {
				result = prevIdx - 1;
				break;
			}
		}

		// This is when we simply add to the tail of the list
		if (result == null) {
			result = orderItemCnt - 1;
		}

		return result;

	}

	function removeOrderItem(itemIdx) {

		var editor = editorList[selectedEditorIdx];

		if (itemIdx > 0) {

			addOrderItemBtn.disable();
			addServiceOrderItemBtn.disable();

			loadingMessage = 'Removing product'; // TODO (balakirev) LitDict

			OrderAjaxService.removeOrderItem(itemIdx, selectedEditorIdx, function(res) {

				var itemDivId = 'divOrderItem_' + selectedEditorIdx + '_' + itemIdx;

				Ext.get(itemDivId).remove();

				editor.orderItems[itemIdx] = null; // Null instead of remove, to prevent re-indexing
				editor.orderItemCnt--;

				if (editor.orderItemCnt == 1) {
					editor.orderItems[0].sapMatCombo.enable();
					editor.delTypeCombo.enable();
					editor.supplyPlantCombo.enable();
					editor.spCombo.enable();
				}


				updateSpecialEditorListeners(0);
				updateCustorderSplitMetaData();

				adjustDeliveriesBtn.update();

				addOrderItemBtn.enable();
				addServiceOrderItemBtn.enable();

			});
		}
	}

	function removeBulkOrderEditor() {

		addOrderItemBtn.disable();
		addServiceOrderItemBtn.disable();

		loadingMessage = 'Removing product'; // TODO (balakirev) LitDict

		OrderAjaxService.removeEditor(selectedEditorIdx, function(res) {

			Ext.get('divOrderEditor' + selectedEditorIdx).remove();

			for (var itemIdx = 1; itemIdx < editorList[selectedEditorIdx].orderItemCnt; itemIdx++) {
				var div = Ext.get('divOrderItem_' + selectedEditorIdx + '_' + itemIdx);
				div.remove();
			}

			// Previous editor should be enabled
			prevEditor = editorList[selectedEditorIdx - 1];
			prevEditor.orderItems[0].sapMatCombo.enable();
			prevEditor.delTypeCombo.enable();
			prevEditor.supplyPlantCombo.enable();
			prevEditor.spCombo.enable();

			updateSpecialEditorListeners(selectedEditorIdx);

			adjustDeliveriesBtn.update();

			addOrderItemBtn.enable();
			addServiceOrderItemBtn.enable();

			// Now we need to re-index the divs after the whole
			for (var shiftedEditorIdx = selectedEditorIdx + 1; shiftedEditorIdx < editorList.length; shiftedEditorIdx++) {
				var div = Ext.get("divOrderEditor" + shiftedEditorIdx);
				var newDivId = 'divOrderEditor' + (shiftedEditorIdx - 1);
				div.set({id: newDivId});
			}

			editorList.splice(selectedEditorIdx, 1);

			var sourceEditorIndex = selectedEditorIdx - 1;

			// Unmask the prev. editor by simulating a click on it
			eventFire(Ext.getDom('divOrderEditor' + sourceEditorIndex),'click');

		});

	}

	sapMatBeforeSelectFn = function(combo, rec) {

		if (Ext.isEmpty(rec.data.materialPackageCd) || rec.data.materialPackageCd == 'NULL') {
			Logon.message.warning('<fmt:message key="bestellung.error.no.material.package.cd"/>');
			return false;
		}
		if (Ext.isEmpty(rec.data.logonPackageTypeCd)){
			Logon.message.warning('<fmt:message key="bestellung.error.no.logon.package.cd"/>');
			return false;
		}
		var logonPackageTypeCd = null;

		var selectedEditor = editorList[selectedEditorIdx];

		// Checks for bag item rules
		for (var i=0; i < selectedEditor.orderItems.length; i++) {
			var currItem = selectedEditor.orderItems[i];
			if (currItem != null && currItem.sapMatCombo != combo && currItem.sapMatCombo.getValue() == rec.data.sapMaterialId) {
				Logon.message.warning('<fmt:message key="bestellung.error.alreadyselected"/>');
				return false;
			}
			//in case an item had been removed it is set to null!
			if (currItem) {
				//in the case of the actual combo consider the NEW selection
				currentComboRec = currItem.sapMatCombo != combo ? currItem.sapMatCombo.getSelectedRecord() : rec;
				if (  currentComboRec && !logonPackageHandlingHelper.isService(currentComboRec.data.logonPackageTypeCd) ) {
					if ( Ext.isEmpty(logonPackageTypeCd) ) {
						logonPackageTypeCd = currentComboRec.data.logonPackageTypeCd;
					}
					else if (logonPackageTypeCd != currentComboRec.data.logonPackageTypeCd) {
						Logon.message.warning('<fmt:message key="bestellung.error.combineddelivery"/>');
						return false;
					}
				}
			}
		}

		// Checks for bulk item rules
		logonPackageTypeCd = null;
		for (var i=0; i < editorList.length; i++) {

			var editor = editorList[i];
			var currItem = editor.orderItems[0];
			if (currItem != null && currItem.sapMatCombo != combo && currItem.sapMatCombo.getValue() == rec.data.sapMaterialId) {
				Logon.message.warning('<fmt:message key="bestellung.error.alreadyselected"/>');
				return false;
			}

			//in the case of the actual combo consider the NEW selection
			currentComboRec = currItem.sapMatCombo != combo ? currItem.sapMatCombo.getSelectedRecord() : rec;
			if (  currentComboRec && !logonPackageHandlingHelper.isService(currentComboRec.data.logonPackageTypeCd) ) {
				if ( Ext.isEmpty(logonPackageTypeCd) ) {
					logonPackageTypeCd = currentComboRec.data.logonPackageTypeCd;
				}
				else if (logonPackageTypeCd != currentComboRec.data.logonPackageTypeCd) {
					Logon.message.warning('<fmt:message key="bestellung.error.combineddelivery"/>');
					return false;
				}
			}

		}

	}

	// --- Validation ID = 003 ---
	prevPalletQuantityOk = true;
	checkQuantityOfPalletsFn = function() {
		var palletCnt = 0;
		//for (var item in editorList[0].orderItems) {
		for (var i=0; i<editorList[0].orderItems.length; i++) {
			var item = editorList[0].orderItems[i];
			if (item != null && (item.uomCombo.getValue() == '<%=Constant.UOM_BAG_P40%>' || item.uomCombo.getValue() == '<%=Constant.UOM_BAG_P48%>')) {
				palletCnt += Ext.num(item.quantityStr.getValue(), 0);
			}
		}
		if (palletCnt > 21 && prevPalletQuantityOk) { // TODO use tlgnr_config
			Logon.message.warning('<fmt:message key="bestellung.error.too.many.pallets"/>'); // TODO config
			prevPalletQuantityOk = false;
		}
		else if (palletCnt <= 21) {
			prevPalletQuantityOk = true;
		}
	}

	updateByDeliveryDate = function() {
		saveOrderBtn.disable();
		adjustDeliveriesBtn.disable();

		var editor = editorList[selectedEditorIdx];
		var firstOrderItem = editor.orderItems[0];
		var sapMatCombo = firstOrderItem.sapMatCombo;
		var selectedRecord = sapMatCombo.getSelectedRecord();
		var selectedRecordData = selectedRecord.data;
		editorList[0].updateBilltoCombo({
			storeData: null,
			restrictionData: selectedRecordData
		});

		var idx = bimodalRailPartActive ? 1 : 0;
		var itemIdx = 0;
		var editor = editorList[idx];
		var delType = editor.delTypeCombo.getValue();

		if (isBimodalTruckPartOrder(delType)) {
    		loadingMessage = '<fmt:message key="bestellung.indicator.loading.bimodal.rail.order.list"/>...';
			editor.biRailItemCombo.disable();
			editor.biRailItemCombo.clearValue();
		}

		if (isRailOrder(delType) || isBimodalTruckPartOrder(delType)) {

			var currDelFromDateTime = null;
			var currDelToDateTime = null;

			// The bimodal rail has to no delFromDateTime and delToDateTime attribute
			if (isBimodalRailPartOrder(delType)) {
				currDelFromDateTime = editorList[0].getDelFromDateTime();
				currDelToDateTime = editorList[0].getDelToDateTime();
			}
			else {
				currDelFromDateTime = editor.getDelFromDateTime();
				currDelToDateTime = editor.getDelToDateTime();
			}

			OrderAjaxService.getCollectedEditorData({
	    		'editorIdx': idx,
				'orderItemIdx': itemIdx,
	    		'delFromDat': currDelFromDateTime,
	    		'delToDat': currDelToDateTime
	    	},
	    	function(res) {

				if (isRailOrder(editor.delTypeCombo.getValue())) {
					toggleRailDeliveryData(idx, res.railData);
				}
				else {
					editor.biRailItemCombo.store.loadData(res.biRailItemList);
		    		editor.biRailItemCombo.enable();
				}

				jsonData[idx].railData = res.railData;
	    	});


	    	// Called because if not truck order or pickup the special editors will be inactivated
	    	saveOrderBtn.update();
			adjustDeliveriesBtn.update();

		}

	}

	function updateMaterialInfo(idx, itemIdx, itemDto, options) {
		// Deactivating this until there is no pricing interface, there is no need to refresh the List Price (it is unknown anyway)
		//Ext.getDom('editorList['+idx+'].orderItems['+itemIdx+'].label.basePrice').innerHTML =
			//itemDto.sapMatPrice;

		if (!options || !options.skipOpenQuantity) {
			// Open Quantity
			Ext.getDom('editorList['+idx+'].orderItems['+itemIdx+'].label.openQuantity').innerHTML =
				itemDto.openQuantity;
		}
	}

	/*
	 * This function is now used both for saving a single delivery order and for creating a customer order.
	 */
	function saveOrderWithAjaxStep1(isCustomerOrder) {

		if (!checkComments()){
			return false;
		}
		blockChartRefresh = true;

		delDatesValid = true;

		for (var editorIdx = 0; editorIdx < editorList.length; editorIdx++) {

			// No check in case of bi-modal rail as delivery times are not coming from this editor
			if (isBimodalRailPartOrder(editorList[editorIdx].delTypeCombo.value)) {
				continue;
			}

			if (getDelFromDateTime(editorIdx) == undefined || getDelToDateTime(editorIdx) == undefined || getDelFromDateTime(editorIdx) > getDelToDateTime(editorIdx)) {
				var deliveryDateWarningMsg = '<fmt:message key="bestellung.error.invalid.delivery.earlier.later"/>';
				Logon.message.warning(deliveryDateWarningMsg.replace('{0}', editorLocatorMsg.replace('@', editorIdx+1)));
				delDatesValid = false;
				break;
			}
		}

		if (!delDatesValid) {
			blockChartRefresh = false;
		}
		else {

			saveOrderBtn.disable();
			adjustDeliveriesBtn.disable();
			addOrderItemBtn.disable(true);
			addServiceOrderItemBtn.disable(true);

			Ext.get('divEditors').mask('Processing...', 'x-mask-loading');

			var idx = bimodalRailPartActive ? 1 : 0;
			var itemIdx = 0;
			var editor = editorList[idx];

			// --- Additional delivery date check before saving ---
			if (isRailOrder(editor.delTypeCombo.getValue())) {
		    	OrderAjaxService.getCollectedEditorData({
		    		'editorIdx': idx,
					'orderItemIdx': itemIdx,
					'spId': editor.spCombo.getValue(),
		    		'delTypeCd': editor.delTypeCombo.getValue(),
		    		'delFromDat': editorList[0].getDelFromDateTime(), // Delivery from & to is always in the 1st editor
		    		'delToDat': editorList[0].getDelToDateTime()},
		    	function(res) {

					var isValidRailDeliveryDat = toggleRailDeliveryData(idx, res.railData);
					jsonData[idx] = res;

					if (isValidRailDeliveryDat) {

						saveOrderWithAjaxStep2(isCustomerOrder);

					}
					else {
						// --- User can re-adjust the delivery date ---
						isEditorDisabled = false;
						Ext.get('divEditors').unmask();
						blockChartRefresh = false;
						addOrderItemBtn.handleAfterValidationError();
						addServiceOrderItemBtn.handleAfterValidationError();
					}
		    	});
			}
			else {
				saveOrderWithAjaxStep2(isCustomerOrder);
			}
		}
	}

	function saveOrderWithAjaxStep2(isCustomerOrder) {

		unMaskAllEditors();

		var params = {};

		addShiptoAndNewOrderFormData(params, { isCustomerOrder: isCustomerOrder });

		params.action = (!isCustomerOrder ? 'saveAjax' : 'createCustomerOrderStep1');

		loadingMessage = (!isCustomerOrder
				? '<fmt:message key="bestellung.message.savingsingledeliveryorders"/>'
				: '<fmt:message key="extjs.processing"/>');
		
		loadDivOrderProcessing(params, loadingMessage);
	}

	function cancelCustOrdCreation() {

		hidePopup();

		adjustDeliveriesBtn.update();
		saveOrderBtn.update();

		addOrderItemBtn.enable();
		addServiceOrderItemBtn.enable();
		
		// In some cases, the mask is the child of divEditors directy rather of divOrderEditor
		Ext.get('divEditors').unmask();
		
		applyMaskingForEditors(selectedEditorIdx);

	}

	// --- Common templates for comboboxes ---
	sapMatComboTpl = '<tpl for="."><div class="x-combo-list-item">{txt} <span class="dynlabel">({sapMaterialNo}, {materialPackageTxt})</span></div></tpl>';

</script>

<c:choose>
	<c:when test="${form.editorList[0].orderItems[0].sapMatId == -1}">
		<c:set scope="request" var="isEditorDisabled" value="true"/>
		<c:set scope="request" var="isEditorDisabledStr" value=" disabled "/>
	</c:when>
	<c:otherwise>
		<c:set scope="request" var="isEditorDisabled" value="false"/>
		<c:set scope="request" var="isEditorDisabledStr" value=""/>
	</c:otherwise>
</c:choose>

<div class="box" style="margin-top: 5px">

<%-- EDITOR HEADER --%>
<span class="labelblue" style="margin-left: 5px"><fmt:message key="order.list.new"/></span>

<div id="divEditorsAllOrders">
	<jsp:include page="EditorsOngoingOrders.jsp"/>
</div>

</div>

<div id="divOrderProcessing" style="display: none; visibility: hidden"></div>
