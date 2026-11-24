/* global LitDict, texts */

const ITrackReporting = (function () {

	function maskContent(targetQuery, promise) {
		let target = document.querySelector(targetQuery);
		if (target) {
			const mask = new Ext.LoadMask(Ext.get(target), {
				msg: loadingMsgText // this is a global variable
			});
			mask.show();
			promise.finally(() => {
				mask.hide();
			})
		}
	}

	function responseHandler(success, error) {
		return (result) => {
			if (result.hasOwnProperty('errorMessage') && !!result.errorMessage) {
				// call failed, display it
				Logon.message.warning(result.errorMessage);
				if (error) {
					error(result.errorReason);
				}
			}
			else if (result.hasOwnProperty('result') && success) {
				success(result.result);
			}
		};
	}

	function initSoldtoCombo() {
		// --- Data record and DataStore definitions ---
		const sapBpRec = Ext.data.Record.create(['id', 'name']);

		const soldtoStore = new Ext.data.Store({
			proxy: new Ext.data.DWRProxyForPager((skip, count, sort, query, callback) => {
				ITrackReportingAjaxService.fetchSoldtoList(skip, count, sort, query, responseHandler(callback));
			}, true),
			reader: new Ext.data.ListRangeReader({ id: 'soldtoSapBpId', totalProperty: 'totalSize' }, sapBpRec)
		});

		return new Ext.form.ComboBox({
			id: 'soldtoCombo',
			applyTo: 'soldtoCombo',
			store: soldtoStore,
			displayField: 'name',
			valueField: 'id',
			editable: true,
			forceSelection: false,
			pageSize: 20,
			width: 180,
			listWidth: 250,
			typeAhead: false,
			triggerAction: 'all',
			selectOnFocus: true,
			allowBlank: true,
		});
	}

	function initShippingPointCombo() {
		const shippintpointRec = Ext.data.Record.create(['id', 'name']);

		const shippingpointStore = new Ext.data.Store({
			proxy: new Ext.data.DWRProxyForPager((skip, count, sort, query, callback) => {
				ITrackReportingAjaxService.fetchShippingpointList(skip, count, sort, query, responseHandler(callback));
			}, true),
			reader: new Ext.data.ListRangeReader({ id: 'id', totalProperty: 'totalSize' }, shippintpointRec)
		});

		return new Ext.form.ComboBox({
			id: 'shippingpointCombo',
			applyTo: 'shippingpointCombo',
			store: shippingpointStore,
			displayField: 'name',
			valueField: 'id',
			editable: true,
			forceSelection: false,
			pageSize: 20,
			width: 180,
			listWidth: 250,
			typeAhead: false,
			triggerAction: 'all',
			selectOnFocus: true,
			allowBlank: true,
		});
	}

	function initCommCarrierCombo() {
		const commCarrierRec = Ext.data.Record.create(['id', 'name']);

		const commCarrierStore = new Ext.data.Store({
			proxy: new Ext.data.DWRProxyForPager((skip, count, sort, query, callback) => {
				ITrackReportingAjaxService.fetchCommCarrierList(skip, count, sort, query, responseHandler(callback));
			}, true),
			reader: new Ext.data.ListRangeReader({ id: 'id', totalProperty: 'totalSize' }, commCarrierRec)
		});

		return new Ext.form.ComboBox({
			id: 'commCarrierCombo',
			applyTo: 'commCarrierCombo',
			store: commCarrierStore,
			displayField: 'name',
			valueField: 'id',
			editable: true,
			forceSelection: false,
			pageSize: 20,
			width: 180,
			listWidth: 250,
			typeAhead: false,
			triggerAction: 'all',
			selectOnFocus: true,
			allowBlank: true,
		});
	}

	function initExecCarrierCombo() {
		const commCarrierRec = Ext.data.Record.create(['id', 'name']);

		const execCarrierStore = new Ext.data.Store({
			proxy: new Ext.data.DWRProxyForPager((skip, count, sort, query, callback) => {
				ITrackReportingAjaxService.fetchExecCarrierList(skip, count, sort, query, responseHandler(callback));
			}, true),
			reader: new Ext.data.ListRangeReader({ id: 'id', totalProperty: 'totalSize' }, commCarrierRec)
		});

		return new Ext.form.ComboBox({
			id: 'execCarrierCombo',
			applyTo: 'execCarrierCombo',
			store: execCarrierStore,
			displayField: 'name',
			valueField: 'id',
			editable: true,
			forceSelection: false,
			pageSize: 20,
			width: 180,
			listWidth: 250,
			typeAhead: false,
			triggerAction: 'all',
			selectOnFocus: true,
			allowBlank: true,
		});
	}

	function initSareaCombo() {
		const sareaRec = Ext.data.Record.create(['id', 'name']);

		const sareaStore = new Ext.data.Store({
			proxy: new Ext.data.DWRProxyForPager((skip, count, sort, query, callback) => {
				ITrackReportingAjaxService.fetchSareaList(skip, count, sort, query, responseHandler(callback));
			}, true),
			reader: new Ext.data.ListRangeReader({ id: 'id', totalProperty: 'totalSize' }, sareaRec)
		});

		return new Ext.form.ComboBox({
			id: 'sareaCombo',
			applyTo: 'sareaCombo',
			store: sareaStore,
			displayField: 'name',
			valueField: 'id',
			editable: true,
			forceSelection: false,
			pageSize: 20,
			width: 180,
			listWidth: 250,
			typeAhead: false,
			triggerAction: 'all',
			selectOnFocus: true,
			allowBlank: true
		});
	}

	function initClearSelectionButton(config) {
		const addButton = new Ext.Button({
			id: config.clearSelectionButtonId,
			applyToMarkup: config.clearSelectionButtonId,
			buttonSelector: 'Ext.query("*[id^=' + config.clearSelectionButtonId + ']")'
		});

		Ext.get(config.clearSelectionButtonId).on("click", function () {
			var el = Ext.DomQuery.selectNode("div[id=" + config.outerDivId + "]>table>tbody>tr>td>div>table>tbody>tr");
			while (el) {
				el.remove();
				el = Ext.DomQuery.selectNode("div[id=" + config.outerDivId + "]>table>tbody>tr>td>div>table>tbody>tr");
			}

			config.selectedIds.splice(0, config.selectedIds.length);
			el = Ext.DomQuery.select("div[id=" + config.outerDivId + "]>table>tbody>tr>td>div>table>tbody");
			Ext.get(el).insertFirst("<tr><td><label>" + LitDict['itrack.reporting.selectiontext.all'] +
				"</label></td><td valign='middle' style='width:1px'><nobr>");
		});

	}

	function initAddButton(config) {
		const addButton = new Ext.Button({
			id: config.addButtonId,
			applyToMarkup: config.addButtonId,
			buttonSelector: 'Ext.query("*[id^=' + config.addButtonId + ']")'
		});

		Ext.get(config.addButtonId).on("click", function () {
				const idToAdd = config.comboBox.getValue();
				if (config.selectedIds.indexOf(idToAdd) !== -1) {
					return;
				}
				else {
					config.selectedIds.push(idToAdd);
				}
				var removeButtonId = config.removeButtonIdPrefix + idToAdd;
				var el = Ext.DomQuery.select("div[id=" + config.outerDivId + "]>table>tbody>tr>td>div>table>tbody>tr")[0];
				Ext.get(el).insertSibling("<tr id='" + config.removeButtonIdPrefix + "row" + idToAdd + "'><td><label>" +
					config.comboBox.getSelectedRecord().data.name +
					"</label></td><td valign='middle' style='width: 1px'><nobr>" +
					"<input type='button' style ='background: url(/logon/admin/images/delete2.png) right top" +
					" no-repeat;width:16px;' id='" + removeButtonId + "' class='SmallButtonStyle'" +
					"/>" +
					"</nobr></td></tr>");
				if (config.selectedIds.length === 1) {
					//remove the 'all' row
					el.remove();
				}
				const removeButton = new Ext.Button({
					id: removeButtonId,
					applyToMarkup: removeButtonId,
					buttonSelector: 'Ext.query("*[id^=' + removeButtonId + ']")'
				});
				Ext.get(removeButtonId).on("click", function () {
						config.selectedIds.splice(config.selectedIds.indexOf(idToAdd), 1);
						var row = Ext.get(config.removeButtonIdPrefix + "row" + idToAdd);
						if (config.selectedIds.length === 0) {
							//add the 'all' row
							var el = Ext.DomQuery.select("div[id=" + config.outerDivId + "]>table>tbody>tr>td>div>table>tbody>tr")[0];
							row.insertSibling("<tr><td><label>" + LitDict['itrack.reporting.selectiontext.all'] +
								"</label></td><td valign='middle' style='width:" +
								" 1px'><nobr>");
						}
						row.remove();
					}
				);
				config.comboBox.reset();
			}
		)
	}

	function initShiptoCombo() {
		// --- Data record and DataStore definitions ---
		const sapBpRec = Ext.data.Record.create(['id', 'name']);

		const shiptoStore = new Ext.data.Store({
			proxy: new Ext.data.DWRProxyForPager((skip, count, sort, query, callback) => {
				ITrackReportingAjaxService.fetchShiptoList(skip, count, sort, query, responseHandler(callback));
			}, true),
			reader: new Ext.data.ListRangeReader({ id: 'id', totalProperty: 'totalSize' }, sapBpRec)
		});

		return shiptoCombo = new Ext.form.ComboBox({
			id: 'shiptoCombo',
			applyTo: 'shiptoCombo',
			store: shiptoStore,
			displayField: 'name',
			valueField: 'id',
			editable: true,
			forceSelection: true,
			pageSize: 20,
			width: 180,
			listWidth: 250,
			typeAhead: false,
			triggerAction: 'all',
			selectOnFocus: true
		});
	}

	function initFromToDatePickers(config, overrideConfig) {
		const fromDatePicker = new Ext.form.DateField({
			id: 'fromDatePicker',
			applyTo: 'fromDatePicker',
			format: config.dateFormat,
			altFormats: config.dateAltFormats,
			allowBlank: false,
			invalidText: LitDict['itrack.error.invaliddate'],
			blankText: LitDict['itrack.error.requiredfield'],
			showToday: false,
			...overrideConfig,
		});
		const toDatePicker = new Ext.form.DateField({
			id: 'toDatePicker',
			applyTo: 'toDatePicker',
			format: config.dateFormat,
			altFormats: config.dateAltFormats,
			allowBlank: false,
			invalidText: LitDict['itrack.error.invaliddate'],
			blankText: LitDict['itrack.error.requiredfield'],
			showToday: false,
			validator: function () {
				// the incoming raw value is useless for comparison, so I have to use this.getValue() to get the value for
				// comparison
				if (fromDatePicker.getValue() > this.getValue()) {
					return LitDict['itrack.error.invaliddate'];
				}
				return true;
			},
			...overrideConfig,
		});
		return { fromDatePicker, toDatePicker };
	}

	function convertDateToBE(date) {
		return date.format('d.m.y');
	}


	function validateFilter(fromDatePicker, toDatePicker, callback) {

		const fromDatePickerValid = fromDatePicker.validate();
		const toDatePickerValid = toDatePicker.validate();

		if (!fromDatePickerValid || !toDatePickerValid) {
			return;
		}

		// all fields are valid, call the callback with the values
		callback(convertDateToBE(fromDatePicker.getValue()), convertDateToBE(toDatePicker.getValue()));
	}

	function downloadFile(data) {
		const link = document.createElement('a');
		link.href = URL.createObjectURL(new Blob([new Uint8Array(data.content)], { type: data.mimeType }));
		link.download = data.name;
		document.body.appendChild(link);
		link.click();
		document.body.removeChild(link);
	}

	return {

		/**
		 * Report expected part
		 */
		reportFunctions: (function () {
			const selectedSareaIds = [];
			const selectedShippingPointIds = [];
			const selectedCommCarrierIds = [];
			const selectedExecCarrierIds = [];
			const selectedShiptoSapBpIds = [];
			const selectedSoldtoSapBpIds = [];

			function validateMaskAndCall(fromDatePicker, toDatePicker, followUp) {
				return () =>
					validateFilter(fromDatePicker, toDatePicker, (fromDate, toDate) => {
						maskContent('.reportExpected', new Promise(function (resolve, reject) {
							ITrackReportingAjaxService.getReportData({
								"startDate": fromDate,
								"endDate": toDate,
								"selectedSareaIds": selectedSareaIds,
								"selectedShippingPointIds": selectedShippingPointIds,
								"selectedCommCarrierIds": selectedCommCarrierIds,
								"selectedExecCarrierIds": selectedExecCarrierIds,
								"selectedShiptoSapBpIds": selectedShiptoSapBpIds,
								"selectedSoldtoSapBpIds": selectedSoldtoSapBpIds
							}, responseHandler(resolve, reject))
						}).then(followUp));
					});
			}

			function init(config) {
				Ext.menu.Menu.prototype.shadow = false;
				Ext.form.ComboBox.prototype.forceSelection = true;
				Ext.form.ComboBox.prototype.allowBlank = true;
				Ext.form.ComboBox.prototype.minChars = 1;
				Ext.form.ComboBox.prototype.emptyText = LitDict['dummy.select'];
				Ext.form.ComboBox.prototype.shadow = false;
				const { fromDatePicker, toDatePicker } = initFromToDatePickers(config);
				const sareaCombo = initSareaCombo();
				initAddButton({
					comboBox: sareaCombo,
					selectedIds: selectedSareaIds,
					outerDivId: 'DivSareaSelection',
					addButtonId: 'addSareaButton',
					removeButtonIdPrefix: 'removeSareaButton'
				});
				initClearSelectionButton({
					outerDivId: 'DivSareaSelection',
					clearSelectionButtonId: "clearSareaSelectionButton",
					selectedIds: selectedSareaIds
				});
				const shippingpointCombo = initShippingPointCombo();
				initAddButton({
					comboBox: shippingpointCombo,
					selectedIds: selectedShippingPointIds,
					outerDivId: 'DivShippingPointSelection',
					addButtonId: 'addShippingPointButton',
					removeButtonIdPrefix: 'removeShippingPointButton'
				});
				initClearSelectionButton({
					outerDivId: 'DivShippingPointSelection',
					clearSelectionButtonId: "clearShippingPointSelectionButton",
					selectedIds: selectedShippingPointIds
				});
				const commCarrierCombo = initCommCarrierCombo();
				initAddButton({
					comboBox: commCarrierCombo,
					selectedIds: selectedCommCarrierIds,
					outerDivId: 'DivCommCarrierSelection',
					addButtonId: 'addCommCarrierButton',
					removeButtonIdPrefix: 'removeCommCarrierButton'
				});
				initClearSelectionButton({
					outerDivId: 'DivCommCarrierSelection',
					clearSelectionButtonId: "clearCommCarrierSelectionButton",
					selectedIds: selectedCommCarrierIds
				});
				const execCarrierCombo = initExecCarrierCombo();
				initAddButton({
					comboBox: execCarrierCombo,
					selectedIds: selectedExecCarrierIds,
					outerDivId: 'DivExecCarrierSelection',
					addButtonId: 'addExecCarrierButton',
					removeButtonIdPrefix: 'removeExecCarrierButton'
				});
				initClearSelectionButton({
					outerDivId: 'DivExecCarrierSelection',
					clearSelectionButtonId: "clearExecCarrierSelectionButton",
					selectedIds: selectedExecCarrierIds
				});
				const soldtoCombo = initSoldtoCombo();
				initAddButton({
					comboBox: soldtoCombo,
					selectedIds: selectedSoldtoSapBpIds,
					outerDivId: 'DivSoldtoSapBpSelection',
					addButtonId: 'addSoldtoSapBpButton',
					removeButtonIdPrefix: 'removeSoldtoSapBpButton'
				});
				initClearSelectionButton({
					outerDivId: 'DivSoldtoSapBpSelection',
					clearSelectionButtonId: "clearSoldtoSapBpSelectionButton",
					selectedIds: selectedSoldtoSapBpIds
				});
				const shiptoCombo = initShiptoCombo();
				initAddButton({
					comboBox: shiptoCombo,
					selectedIds: selectedShiptoSapBpIds,
					outerDivId: 'DivShiptoSapBpSelection',
					addButtonId: 'addShiptoSapBpButton',
					removeButtonIdPrefix: 'removeShiptoSapBpButton'
				});
				initClearSelectionButton({
					outerDivId: 'DivShiptoSelection',
					clearSelectionButtonId: "clearShiptoSapBpSelectionButton",
					selectedIds: selectedShiptoSapBpIds
				});
				Ext.get('exportXLSButton').on('click', validateMaskAndCall(fromDatePicker, toDatePicker, downloadFile));
			}

			return init;
		})()
	};
})();