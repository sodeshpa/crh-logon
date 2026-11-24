/* global LitDict, texts */

const demandPlanning = (function() {
	// From: https://pawelgrzybek.com/the-observer-pattern-in-javascript-explained/
	// define a class
	class Observable {
		// each instance of the Observer class
		// starts with an empty array of things (observers)
		// that react to a state change
		constructor() {
			this.observers = [];
		}

		// add the ability to subscribe to a new object / DOM element
		// essentially, add something to the observers array
		subscribe(f) {
			this.observers.push(f);
		}

		// add the ability to unsubscribe from a particular object
		// essentially, remove something from the observers array
		unsubscribe(f) {
			this.observers = this.observers.filter(subscriber => subscriber !== f);
		}

		// update all subscribed objects / DOM elements
		// and pass some data to each of them
		notify(...data) {
			this.observers.forEach(observer => observer(...data));
		}

		clear() {
			this.observers.splice(0, this.observers.length);
		}
	}

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

	function initSoldtoCombo(addAllSoldtoOption) {
		// --- Data record and DataStore definitions ---
		const sapBpRec = Ext.data.Record.create(['sapBpId', 'name']);

		const soldtoStore = new Ext.data.Store({
			proxy: new Ext.data.DWRProxyForPager((skip, count, sort, query, callback) => {
				// only add the all shipto option if we are not filtering and we are on the first page
				const proxy = addAllSoldtoOption && !query ? (data) => {
					// add the 'all' element to the first one, this way we have +1 element on the first page, hopefully that
					// won't actually cause issues on the second page
					if (skip === 0) {
						data.data.unshift({ 'sapBpId': null, 'name': LitDict['demandplan.expected.allshipto'], });
					}
					callback(data);
				} : callback;
				DemandPlanningAjaxService.fetchSoldtoList(skip, count, sort, query, responseHandler(proxy));
			}, true),
			reader: new Ext.data.ListRangeReader({ id: 'sapBpId', totalProperty: 'totalSize' }, sapBpRec)
		});

		return new Ext.form.ComboBox({
			id: 'soldtoCombo',
			applyTo: 'soldtoCombo',
			store: soldtoStore,
			displayField: 'name',
			valueField: 'sapBpId',
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

	function initShiptoCombo(soldtoCombo, addAllShiptoOption) {
		// --- Data record and DataStore definitions ---
		const sapBpRec = Ext.data.Record.create(['sapBpId', 'name']);

		const shiptoStore = new Ext.data.Store({
			proxy: new Ext.data.DWRProxyForPager((skip, count, sort, query, callback) => {
				// only add the all shipto option if we are not filtering and we are on the first page
				const proxy = addAllShiptoOption && !query ? (data) => {
					// add the 'all' element to the first one, this way we have +1 element on the first page, hopefully that
					// won't actually cause issues on the second page
					if (skip === 0) {
						data.data.unshift({ 'sapBpId': null, 'name': LitDict['demandplan.expected.allshipto'], });
					}
					callback(data);
				} : callback;
				DemandPlanningAjaxService.fetchShiptoList(skip, count, sort, soldtoCombo && soldtoCombo.getValue(), query, responseHandler(proxy));
			}, true),
			reader: new Ext.data.ListRangeReader({ id: 'sapBpId', totalProperty: 'totalSize' }, sapBpRec)
		});

		const shiptoCombo = new Ext.form.ComboBox({
			id: 'shiptoCombo',
			applyTo: 'shiptoCombo',
			store: shiptoStore,
			displayField: 'name',
			valueField: 'sapBpId',
			editable: true,
			forceSelection: true,
			pageSize: 20,
			width: 180,
			listWidth: 250,
			typeAhead: false,
			triggerAction: 'all',
			selectOnFocus: true,
			allowBlank: false,
			blankText: LitDict['demandplan.error.requiredfield'],
		});

		if (soldtoCombo) {
			soldtoCombo.on('select', () => {
				shiptoCombo.clearValue();
				delete shiptoCombo.lastQuery;
			});
		}

		return shiptoCombo;
	}

	function initFromToDatePickers(config, overrideConfig) {
		const fromDatePicker = new Ext.form.DateField({
			id: 'fromDatePicker',
			applyTo: 'fromDatePicker',
			format: config.dateFormat,
			altFormats: config.dateAltFormats,
			allowBlank: false,
			disabledDays: [0, 2, 3, 4, 5, 6],
			disabledDaysText: LitDict['demandplan.error.invaliddate.notmonday'],
			invalidText: LitDict['demandplan.error.invaliddate'],
			blankText: LitDict['demandplan.error.requiredfield'],
			showToday: false,
			...overrideConfig,
		});
		const toDatePicker = new Ext.form.DateField({
			id: 'toDatePicker',
			applyTo: 'toDatePicker',
			format: config.dateFormat,
			altFormats: config.dateAltFormats,
			allowBlank: false,
			disabledDays: [1, 2, 3, 4, 5, 6],
			disabledDaysText: LitDict['demandplan.error.invaliddate.notsunday'],
			invalidText: LitDict['demandplan.error.invaliddate'],
			blankText: LitDict['demandplan.error.requiredfield'],
			showToday: false,
			validator: function() {
				// the incoming raw value is useless for comparison, so I have to use this.getValue() to get the value for
				// comparison
				if (fromDatePicker.getValue() > this.getValue()) {
					return LitDict['demandplan.error.invaliddate'];
				}
				return true;
			},
			...overrideConfig,
		});
		return { fromDatePicker, toDatePicker };
	}

	function convertDateToBE(date) {
		return date.format('Y-m-d');
	}

	function validateFilter(fromDatePicker, toDatePicker, soldtoCombo, shiptoCombo, callback) {

		const fromDatePickerValid = fromDatePicker.validate();
		const toDatePickerValid = toDatePicker.validate();
		const soldtoComboValid = soldtoCombo.validate();
		const shiptoComboValid = shiptoCombo.validate();
		if (!fromDatePickerValid || !toDatePickerValid || !shiptoComboValid || !soldtoComboValid) {
			return;
		}

		// all fields are valid, call the callback with the values
		callback(convertDateToBE(fromDatePicker.getValue()), convertDateToBE(toDatePicker.getValue()), soldtoCombo.getValue(),
			shiptoCombo.getValue());
	}

	function formatTons(before, after) {
		let result = before + ' (' + texts.tons + ')';
		if (after) {
			result += ' ' + after;
		}
		return result;
	}

	function downloadFile(data) {
		const link = document.createElement('a');
		link.href = URL.createObjectURL(new Blob([new Uint8Array(data.content)], {type: data.mimeType}));
		link.download = data.name;
		document.body.appendChild(link);
		link.click();
		document.body.removeChild(link);
	}

	return {
		/**
		 * Manage planning part
		 */
		managePlanning: (function () {

			const CSS_EDITING = 'editing', CSS_ENABLED = 'enabled';
			const usedMaterialIds = [];
			const planningObservable = new Observable();

			function addPlanRow(table, newItemButton, date, shipto, materials, planning) {
				const items = document.importNode(Ext.get('planRowTemplate').dom.content, true);
				// because after appending into the DOM the DocumentFragment no longer contains the nodes, I have to extract them
				// before appending
				const
					materialCombo = items.querySelector('.materialCombo'),
					weightInput = items.querySelector('.weightInput'),
					uomlabel = items.querySelector('.uomLabelWrapper'),
					deleteButton = items.querySelector('.deleteButton'),
					editButton = items.querySelector('.editButton'),
					saveButton = items.querySelector('.saveButton');
				// we have to append the elements, before we can initialize them as fields
				// and because Ext.Element.appendChild tries to initialize elements that aren't inserted yet, we have to insert it
				// using the native dom methods
				table.dom.insertBefore(items, newItemButton.dom);

				// initialize the material store
				const materialStore = new Ext.data.JsonStore({
					data: {
						// we only show available materials in the dropdown or the existing planning's material
						'materials': materials.filter(x => x.available || (planning && planning.sapMaterialId === x.sapMaterialId))
					},
					root: 'materials',
					fields: ['sapMaterialId', 'name']
				});
				const planningChangeListener = () => {
					materialStore.filterBy((record) => {
						const sapMaterialId = record.get('sapMaterialId');
						return (planning && planning.sapMaterialId === sapMaterialId) || usedMaterialIds.indexOf(sapMaterialId) === -1;
					});
				};
				planningObservable.subscribe(planningChangeListener);

				// initialize elements
				const materialComboField = new Ext.ux.form.NoFilterComboBox({
					applyTo: materialCombo,
					store: materialStore,
					allowBlank: false,
					displayField: 'name',
					valueField: 'sapMaterialId',
					editable: false,
					forceSelection: true,
					blankText: LitDict['demandplan.error.requiredfield'],
					width: 250,
					listWidth: 320,
					typeAhead: false,
					triggerAction: 'all',
					selectOnFocus: true,
					mode: 'local',
					disabled: true
				});
				const weightInputField = new Ext.form.NumberField({
					applyTo: weightInput,
					allowBlank: false,
					allowNegative: false,
					allowDecimals: true,
					blankText: LitDict['demandplan.error.requiredfield'],
					decimalPrecision: 4,
					invalidText: LitDict['demandplan.error.invalidweight'],
					minValue: 0.0001, // this is a workaround, because allowNegative doesn't work correctly
					maxValue: 99999999.9999,
					selectOnFocus: true,
					enableKeyEvents: true,
					style: 'text-align: right',
					width: 95,
					disabled: true,
				});
				const deleteButtonElement = new Ext.Element(deleteButton);
				const editButtonElement = new Ext.Element(editButton);
				const saveButtonElement = new Ext.Element(saveButton);

				const stopEditing = () => {
					table.removeClass(CSS_EDITING);
					deleteButtonElement.removeClass(CSS_ENABLED);
					saveButtonElement.removeClass(CSS_ENABLED);
					materialComboField.disable();
					materialComboField.setValue(planning.sapMaterialId);
					weightInputField.disable();
					weightInputField.setValue(planning.plannedWeightT);
				};
				const errorHandler = (reason) => {
					if (reason === 'RES_ERROR_DEMANDPLAN_MAT_CANNOT_CHANGE') {
						stopEditing();
					}
				};

				if (planning) {
					// set values, add handled functions
					materialComboField.setValue(planning.sapMaterialId);
					weightInputField.setValue(planning.plannedWeightT);
				}
				deleteButtonElement.on('click', () => {
					function del() {
						materialComboField.destroy();
						weightInputField.destroy();
						uomlabel.remove();
						deleteButtonElement.remove();
						editButtonElement.remove();
						saveButtonElement.remove();
						table.removeClass(CSS_EDITING);
						planningObservable.unsubscribe(planningChangeListener);
					}

					if (planning) {
						DemandPlanningAjaxService.deletePlanning(planning.demandPlanId, responseHandler(() => {
							Logon.message.success(LitDict['demandplan.plan.deletesuccess']);
							del();
							usedMaterialIds.splice(usedMaterialIds.indexOf(planning.sapMaterialId), 1);
							planningObservable.notify();
						}, errorHandler));
					}
					else {
						del();
					}
				});
				editButtonElement.on('click', () => {
					deleteButtonElement.addClass(CSS_ENABLED);
					saveButtonElement.addClass(CSS_ENABLED);
					table.addClass(CSS_EDITING);
					materialComboField.enable();
					weightInputField.enable();
				});
				saveButtonElement.on('click', () => {
					const matValid = materialComboField.validate();
					const weiValid = weightInputField.validate();
					if (!matValid || !weiValid) {
						// if any of the fields is not valid, return and don't try to save it
						return;
					}
					const sapMaterialId = materialComboField.getValue();
					const weight = weightInputField.getValue();
					if (planning) {
						if (sapMaterialId !== planning.sapMaterialId || weight !== planning.plannedWeightT) {
							DemandPlanningAjaxService.updatePlanning(planning.demandPlanId, sapMaterialId, weight, responseHandler(() => {
								Logon.message.success(LitDict['demandplan.plan.savesuccess']);
								const oldPlanning = planning;
								planning = {
									demandPlanId: planning.demandPlanId,
									sapMaterialId: sapMaterialId,
									plannedWeightT: weight
								};
								usedMaterialIds.splice(usedMaterialIds.indexOf(oldPlanning.sapMaterialId), 1);
								usedMaterialIds.push(sapMaterialId);
								planningObservable.notify();
								stopEditing();
							}, errorHandler));
						}
						else {
							stopEditing();
						}
					}
					else {
						DemandPlanningAjaxService.createPlanning(date, shipto, sapMaterialId, weight, responseHandler((demandPlanId) => {
							Logon.message.success(LitDict['demandplan.plan.savesuccess']);
							planning = {
								demandPlanId: demandPlanId,
								sapMaterialId: sapMaterialId,
								plannedWeightT: weight,
							};
							usedMaterialIds.push(sapMaterialId);
							planningObservable.notify();
							stopEditing();
						}));
					}
				});
				if (!planning) {
					// to fix a bug, where before opening it resets the filter
					materialComboField.lastQuery = '';
					// trigger editing
					editButtonElement.dom.click();
					// update the list
					planningChangeListener();
				}
				else {
					usedMaterialIds.push(planning.sapMaterialId);
				}
			}

			function renderPlannings(date, shipto, materials, plannings) {
				const table = Ext.get('plan');
				// clear all rows
				table.removeClass(CSS_EDITING);
				table.query('> *').forEach(it => {
					Ext.get(it).remove();
				});
				planningObservable.clear();
				usedMaterialIds.splice(0, usedMaterialIds.length);
				if (!materials) {
					return;
				}
				// check that we have no materials, or if we have, check if we have no plannings and no available materials
				// this solves the case, where we only have unavailable materials and we would display that every material is planned for,
				// while the user cannot plan anything
				// If the user has plannings, we don't return, but show those plannings.
				if (materials.length === 0 || ((!plannings || plannings.length === 0) && !materials.some(it => it.available))) {
					// no materials, display warning about it
					Logon.message.warning(LitDict['demandplan.plan.nomaterials']);
					return;
				}

				// we have materials, so we can have a new button
				const newItemButtonFragment = document.importNode(Ext.get('newItemButtonTemplate').dom.content, true);
				const newItemButton = new Ext.Element(newItemButtonFragment.querySelector('.newItemButton'));
				newItemButton.on('click', () => {
					// check if the new planning could have any materials
					if (materials.findIndex(x => x.available && usedMaterialIds.indexOf(x.sapMaterialId) === -1) !== -1) {
						addPlanRow(table, newItemButton, date, shipto, materials, null);
					} else {
						// display warning message
						Logon.message.warning(LitDict['demandplan.error.allmaterialused']);
					}
				});
				newItemButton.appendTo(table);

				if (plannings && plannings.length > 0) {
					// display existing rows
					plannings.forEach(planning => addPlanRow(table, newItemButton, date, shipto, materials, planning));
					// once all rows are rendered, update the material stores
					planningObservable.notify();
				}
			}

			function loadDemandPlanning(date, shipto) {
				// transforming to the ISO-8601 local date format, expected by Java/BE
				const beDate = date && date.format('Y-m-d') || null;
				const promises = [];
				if (shipto && date) {
					// load the materials for the shipto
					promises.push(new Promise((resolve) => {
						DemandPlanningAjaxService.fetchMaterialList(beDate, shipto, responseHandler(resolve));
					}));
					promises.push(new Promise((resolve) => {
						DemandPlanningAjaxService.fetchPlanningList(beDate, shipto, responseHandler(resolve));
					}));
				}
				maskContent('.managePlanning', Promise.all(promises)
					.then(([materials, plannings]) => {
						renderPlannings(beDate, shipto, materials, plannings);
					}));
			}

			function initWeekDatePicker(config) {
				const now = new Date().clearTime();
				// ISO-8601 format: Monday: 1, Sunday: 7, like in Java
				const dayOfWeek = now.getDay() || 7;
				// calculate minDate (either this week's Monday for admin, or next week's Monday for others
				const minDate = now.add(Date.DAY, (config.isAdmin ? 1 : 8) - dayOfWeek);
				// calculate the minDate's Sunday (minDate + 6), calculate the week count - 1 (because we already counted one
				// week) and multiply it by 7 (the number of days in a week)
				const maxDate = minDate.add(Date.DAY, 6 + (config.isAdmin ? config.weekCount : config.weekCount - 1) * 7);

				return new Ext.form.DateField({
					id: 'weekDatePicker',
					applyTo: 'weekDatePicker',
					format: config.dateFormat,
					altFormats: config.dateAltFormats,
					allowBlank: false,
					disabledDays: [0, 2, 3, 4, 5, 6],
					disabledDaysText: LitDict['demandplan.error.invaliddate.notmonday'],
					invalidText: LitDict['demandplan.error.invaliddate'],
					blankText: LitDict['demandplan.error.requiredfield'],
					minValue: minDate,
					maxValue: maxDate,
					showToday: false,
				});
			}

			function init(config) {
				Ext.menu.Menu.prototype.shadow = false;
				Ext.form.ComboBox.prototype.forceSelection = true;
				Ext.form.ComboBox.prototype.allowBlank = true;
				Ext.form.ComboBox.prototype.minChars = 1;
				Ext.form.ComboBox.prototype.emptyText = LitDict['dummy.select'];
				Ext.form.ComboBox.prototype.shadow = false;

				const weekDatePicker = initWeekDatePicker(config);
				const shiptoCombo = initShiptoCombo(null,false);
				const onchange = () => {
					loadDemandPlanning(weekDatePicker.getValue(), shiptoCombo.getValue());
				};
				weekDatePicker.on('select', onchange);
				shiptoCombo.on('select', onchange);
			}

			return init;
		})(),

		/**
		 * Report expected part
		 */
		reportExpected: (function(){

			function padArray(arr, length, fill) {
				if (arr.length >= length) {
					return arr;
				}
				return arr.concat(Array.from({length: length - arr.length}).fill(fill));
			}

			function renderTable(data) {
				const colCount = data.dateHeaders.length;

				if (colCount === 0) {
					// no data
					Logon.message.info(LitDict['demandplan.noresults']);
				}

				// because the ExtJS frameworks lacks the capability to remove an existing table, without deleting the
				// original DOM node, we rerender an empty table
				document.querySelector('section#report').style.setProperty('--content-max-width', (250 + (colCount + 1) * 150) + 'px');

				// first we transform the data into a store that matches our needs
				const store = new Ext.data.SimpleStore({
					fields: ['materialName', ...(Array.from({ length: colCount }, (x, i) => 'c' + i)), 'total'],
					data: data.rows.map(it => [it.materialName, ...(padArray(it.amounts, colCount)), it.total])
				});
				new Ext.grid.GridPanel({
					id: 'reportTable',
					applyTo: 'reportTable',
					store: store,
					viewConfig: {
						autoFill: true,
						forceFit: true,
						scrollOffset: 0,
					},
					stripeRows: true,
					disableSelection: true,
					autoHeight: true,
					autoWidth: true,
					autoScroll: true,
					stateful: false,
					columns: [
						{
							header: LitDict['demandplan.expected.column.material'],
							dataIndex: 'materialName',
							sortable: true,
							width: 250
						},
						...(Array.from({ length: colCount }, (x, i) => {
							return {
								header: formatTons(LitDict['demandplan.expected.column.date'], '<br>' + data.dateHeaders[i]),
								dataIndex: 'c' + i,
								sortable: true,
								align: 'center',
								width: 150,
							};
						})),
						{
							header: formatTons(LitDict['demandplan.expected.column.totalamount']),
							dataIndex: 'total',
							sortable: true,
							align: 'center',
							width: 150
						},
					],
				});
			}

			function validateMaskAndCall(fromDatePicker, toDatePicker, soldtoCombo, shiptoCombo, ajaxMethod, followUp) {
				return () =>
					validateFilter(fromDatePicker, toDatePicker, soldtoCombo, shiptoCombo, (fromDate, toDate, soldtoSapBpId, shiptoSapBpId) => {
						maskContent('.reportExpected', new Promise(function(resolve, reject) {
							DemandPlanningAjaxService[ajaxMethod](soldtoSapBpId, shiptoSapBpId, fromDate, toDate, responseHandler(resolve, reject))
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
				const soldtoCombo = initSoldtoCombo(true);
				const shiptoCombo = initShiptoCombo(soldtoCombo, true);
				Ext.get('generateReportButton').on('click', validateMaskAndCall(fromDatePicker, toDatePicker, soldtoCombo, shiptoCombo, 'getExpectedReportData', renderTable));
				Ext.get('exportXLSButton').on('click', validateMaskAndCall(fromDatePicker, toDatePicker, soldtoCombo, shiptoCombo, 'getExpectedReportXLS', downloadFile));
			}

			return init;
		})(),

		/**
		 * Report Planned/Expected vs Realized part
		 */
		reportPlannedAndRealized: (function(){

			function renderTable(data) {
				if (data.length === 0) {
					// no data
					Logon.message.info(LitDict['demandplan.noresults']);
				}
				// because the ExtJS frameworks lacks the capability to remove an existing table, without deleting the
				// original DOM node, we rerender an empty table

				// first we calculate the diffs as the service doesn't return that
				// one digit precision
				data.forEach(it =>
					it.diff = it.planned !== 0 ? Math.round((it.ordered - it.planned) / it.planned * 100) : undefined);
				const store = new Ext.data.JsonStore({
					data: {
						'rows': data
					},
					root: 'rows',
					fields: ['name', 'planned', 'ordered', 'diff']
				});
				new Ext.grid.GridPanel({
					id: 'reportTable',
					applyTo: 'reportTable',
					store: store,
					viewConfig: {
						autoFill: true,
						forceFit: true,
						scrollOffset: 0,
						getRowClass: (record) => {
							const diff = record.get('diff');
							if (diff === 0) {
								return 'diff-match';
							}
							if (diff < 0) {
								return 'diff-negative';
							}
							return 'diff-positive';
						},
					},
					stripeRows: true,
					disableSelection: true,
					autoHeight: true,
					autoWidth: true,
					stateful: false,
					columns: [
						{
							header: LitDict['demandplan.expected.column.material'],
							dataIndex: 'name',
							sortable: true,
							width: 250
						},
						{
							header: formatTons(LitDict['demandplan.expectedvsrealized.column.planned']),
							dataIndex: 'planned',
							sortable: true,
							align: 'center',
							width: 150
						},
						{
							header: formatTons(LitDict['demandplan.expectedvsrealized.column.realized']),
							dataIndex: 'ordered',
							sortable: true,
							align: 'center',
							width: 150
						},
						{
							header: LitDict['demandplan.expectedvsrealized.column.delta'],
							dataIndex: 'diff',
							sortable: true,
							align: 'center',
							width: 150
						},
					],
				});
			}

			function validateMaskAndCall(fromDatePicker, toDatePicker, soldtoCombo, shiptoCombo, ajaxMethod, followUp) {
				return () =>
					validateFilter(fromDatePicker, toDatePicker, soldtoCombo, shiptoCombo, (fromDate, toDate, soldtoSapBpId, shiptoSapBpId) => {
						maskContent('.reportExpectedAndRealized', new Promise(function(resolve, reject) {
							DemandPlanningAjaxService[ajaxMethod](soldtoSapBpId, shiptoSapBpId, fromDate, toDate, responseHandler(resolve, reject))
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

				let now = new Date();
				// calculate last Sunday
				now = now.add(Date.DAY, -1 * now.getDay());

				const { fromDatePicker, toDatePicker } = initFromToDatePickers(config, { maxValue: now });
				const soldtoCombo = initSoldtoCombo(true);
				const shiptoCombo = initShiptoCombo(soldtoCombo, true);
				Ext.get('generateReportButton').on('click', validateMaskAndCall(fromDatePicker, toDatePicker, soldtoCombo, shiptoCombo, 'getDifferenceReportData', renderTable));
				Ext.get('exportXLSButton').on('click', validateMaskAndCall(fromDatePicker, toDatePicker, soldtoCombo, shiptoCombo, 'getDifferenceReportXLS', downloadFile));
			}

			return init;
		})()
	};
})();