Ext.namespace('Ext.ux.grid', 'Ext.ux.data', 'Ext.ux.valid');

Ext.override(Ext.data.Record, {

	equals: function(obj) {
		var result = false;

		if ((!Ext.isEmpty(this.fields)) && (!Ext.isEmpty(this.fields.keys)) && 
				(!Ext.isEmpty(obj)) && (!Ext.isEmpty(obj.fields)) && (!Ext.isEmpty(obj.fields.keys)) && 
				(this.fields.keys.length === obj.fields.keys.length) && (typeof obj.get === 'function') ) {
			var recProperties = this.fields.keys;
			var equalsByProperties = true;

			for (var index = 0; (index < recProperties.length) && equalsByProperties; index++) {
				var key = recProperties[index];
				var tValue = this.get(key);
				var oValue = obj.get(key);

				equalsByProperties = equalsByProperties && (tValue === oValue);
			}

			result = equalsByProperties;
		}

		return result;
	}

});

Ext.override(Ext.grid.CellSelectionModel, {

	init : function(grid){
		this.grid = grid;

		this.addEvents('keypressed-on-cc');

		this.initEvents();
	},

	processCheckColumnEvent : function() {
		var g = this.grid, s = this.selection;
		var r = s.cell[0], c = s.cell[1];
		var gv = g.getView();
		var ce = gv.getCell(r, c);
		var ceds = ce.getElementsByTagName('div');
		var ccss = 'x-grid3-cc-';
		var ccep = false;

		for (var cInd = 0; cInd < ceds.length; cInd++) {
			var ced = ceds[cInd];

			if (!Ext.isEmpty(ced) && (ced.id.indexOf(ccss) == 0)) {
				var i = ced.id.slice(ccss.length);

				this.fireEvent('keypressed-on-cc', r, c, i);

				ccep = true;

				break;
			}
		}

		return ccep;
	},

	handleKeyDown : function(e){
		var k = e.getKey()

		if (!e.isNavKeyPress() && (k !== e.SPACE)) {
			return;
		}

		var g = this.grid, s = this.selection;

		if (!s){
			e.stopEvent();

			var cell = g.walkCells(0, 0, 1, this.isSelectable,  this);

			if (cell) {
				this.select(cell[0], cell[1]);
			}

			return;
		}

		var sm = this;
		var walk = function(row, col, step) {
			return g.walkCells(row, col, step, sm.isSelectable,  sm);
		};
		var r = s.cell[0], c = s.cell[1];
		var newCell;

		switch (k) {
			case e.TAB:
				if (e.shiftKey) {
					newCell = walk(r, c-1, -1);
				}
				else {
					newCell = walk(r, c+1, 1);
				}
			break;

			case e.DOWN:
				newCell = walk(r+1, c, 1);
			break;

			case e.UP:
				newCell = walk(r-1, c, -1);
			break;

			case e.RIGHT:
				newCell = walk(r, c+1, 1);
			break;

			case e.LEFT:
				newCell = walk(r, c-1, -1);
			break;

			case e.ENTER:
				if (g.isEditor && !g.editing) {
					var p = this.processCheckColumnEvent();

					if (!p) {
						g.startEditing(r, c);
						e.stopEvent();
					}

					return;
				}
			break;

			case e.SPACE:
				if (g.isEditor && !g.editing) {
					this.processCheckColumnEvent();
				}
			break;
		};

		if (newCell) {
			this.select(newCell[0], newCell[1]);
			e.stopEvent();
		}
	}
});

Ext.ux.grid.OpDescriptor = function(source, pChFlag, cbFunction) {
	this.source = source;
	this.pchFlag = pChFlag;
	this.cbFunction = cbFunction;
};

Ext.ux.grid.OpDescriptor.STYPE_BUTTON = 'button';
Ext.ux.grid.OpDescriptor.STYPE_PAGER = 'pager';
Ext.ux.grid.OpDescriptor.STYPE_SORTER = 'sorter';

Ext.ux.grid.OpDescriptor.prototype = {

	isItCalledByButton : function() {
		return (this.source === Ext.ux.grid.OpDescriptor.STYPE_BUTTON);
	}, 

	isItCalledByPager : function() {
		return (this.source === Ext.ux.grid.OpDescriptor.STYPE_PAGER);
	}, 

	isItCalledBySorter : function() {
		return (this.source === Ext.ux.grid.OpDescriptor.STYPE_SORTER);
	}
};

Ext.ux.grid.EditableList = function() {
	this.actualPage = 0;
	this.buttonCancel = 'Cancel';
	this.buttonNo = 'No';
	this.buttonYes = 'Yes';
	this.grid = {};
	this.maskMessage = 'Processing...';
	this.nextPage = 0;
	this.noChangesMessage = 'There are no changes to save!';
	this.originalRecordStore = {};
	this.erroneousRecords = [];
	this.pageChangeTitle = 'Change pages?';
	this.pageChangeMessage = 'You are leaving a page that has unsaved changes. Would you like to save your changes?';
	this.pageSize = Ext.ux.grid.EditableList.DEFAULT_PAGE_SIZE;
};

Ext.ux.grid.EditableList.DEFAULT_PAGE_SIZE = 20;

Ext.ux.grid.EditableList.prototype = {

	callServiceForSave : function(changedRecords, callbackForSave) {
		/*
		console.log('method: callServiceForSave');
		console.log(changedRecords);
		console.log(callbackForSave);
		console.log(this);
		*/
		// implement this method in the subclasses of this EditableList in order to have 
		// the required DWR-service called.
		// Parameters:
		//				- changedRecords: array of the adjusted records (represented by Ext.data.Record objects)
		//				- callbackForSave: callback-function to call right after the DWR-service returned.
		// Example:
		// 			CreditNoteAjaxService.saveCreditNotes(changedRecords, {callback: callbackForSave});
	}, 

	changePage: function() {
		/*
		console.log('method: changePage');
		console.log(this);
		*/

		if (!Ext.isEmpty(this.grid)) {
			var pager = this.grid.getBottomToolbar();

			if (!Ext.isEmpty(pager)) {
				pager.setPageSemaphore(true);
				pager.changePage(this.nextPage);
			}
		}
	}, 

	getAdjustedRecords: function() {
		/*
		console.log('method: getAdjustedRecords');
		console.log(this);
		*/

		var result = new Array();

		if (!Ext.isEmpty(this.grid)) {
			var gridStore = this.grid.getStore();

			if (!Ext.isEmpty(gridStore)) {
				var modifiedRecords = gridStore.getModifiedRecords();

				if (!Ext.isEmpty(modifiedRecords)) {
					for (var index = 0; index < modifiedRecords.length; index++) {
						var mRecord = modifiedRecords[index];

						if (!Ext.isEmpty(mRecord)) {
							var oRecord = this.originalRecordStore.getById(mRecord.id);

							if (!mRecord.equals(oRecord)) {
								result.push(mRecord.data);
							}
						}
					}
				}
			}
		}

		return result;
	}, 

	getOpButtonById: function(buttonId) {
		/*
		console.log('method: getOpButtonById');
		console.log(buttonId);
		console.log(this);
		*/

		var result = null;

		if (!Ext.isEmpty(buttonId) && !Ext.isEmpty(this.grid)) {
			var topToolbar = this.grid.getTopToolbar();

			if (!Ext.isEmpty(topToolbar) && !Ext.isEmpty(topToolbar.items)) {
				var items = topToolbar.items;

				if (!Ext.isEmpty(items.keys) && !Ext.isEmpty(items.items)) {
					var indexOfButton = items.keys.indexOf(buttonId);

					if ((indexOfButton != -1) && (indexOfButton < items.items.length)) {
						result = items.items[indexOfButton];
					}
				}
			}
		}

		return result;
	}, 

	handler_Button_Reset: function(button) {
		/*
		console.log('method: handler_Button_Reset');
		console.log(button);
		console.log(this);
		*/
		this.rejectChanges();
	}, 

	handler_Button_Save: function(button, event) {
		/*
		console.log('method: handler_Button_Save');
		console.log(button);
		console.log(event);
		console.log(this);
		*/

		this.operation_Save(
			new Ext.ux.grid.OpDescriptor(
				Ext.ux.grid.OpDescriptor.STYPE_BUTTON, false));
	}, 

	handler_Pager_BeforeChange : function(pager, pagerProperties) {
		/*
		console.log('method: handler_Pager_BeforeChange');
		console.log(pager);
		console.log(pagerProperties);
		console.log(this);
		*/

		var res = false;

		if (pager.isPageSemaphoreOn()) {
			pager.setPageSemaphore(false);

			res = true;
		}
		else if (!Ext.isEmpty(this.grid)) {
			var changes = this.getAdjustedRecords();

			if (!Ext.isEmpty(changes) && (changes.length > 0)) {
				Ext.Msg.show({
					title:	this.pageChangeTitle,
					msg: this.pageChangeMessage,
					buttons: Ext.Msg.YESNOCANCEL,
					scope: this,
					fn: function(buttonId) {
						if (buttonId !== 'cancel') {
							this.nextPage = (pagerProperties.start/pager.pageSize) + 1;
	
							if (buttonId === 'yes') {
								/*
									as 'scope' is set to 'this' ('scope: this') and 
									handler_Pager_BeforeChange is called via a delegate, 'this' 
									operator refers to the 'editor' object in this very case.
									TAKE CARE of the modifications of the SCOPE ('this')!!! 
								*/
								/*
								console.log('handler_Pager_BeforeChange:Ext.Msg.show.fn');
								console.log(this);
								*/
								this.operation_Save(
									new Ext.ux.grid.OpDescriptor(
										Ext.ux.grid.OpDescriptor.STYPE_PAGER, true));
							}
							else if (buttonId === 'no') {
								this.rejectChanges();
								this.changePage();
							}
						}
					}, 
					icon: Ext.MessageBox.QUESTION
				});
			}
			else {
				this.rejectChanges();

				res = true;
			}
		}

		return res;
	}, 

	handler_Pager_Change: function(pager, changeEvent) {
		/*
		console.log('method: handler_Pager_Change');
		console.log(pager);
		console.log(changeEvent);
		console.log(this);
		*/

		if (!Ext.isEmpty(changeEvent.activePage)) {
			this.actualPage = changeEvent.activePage - 1;
			this.nextPage = this.actualPage;
		}
	}, 

	handler_Header_Click: function(grid, index) {
		/*
		console.log('method: handler_Header_Click');
		console.log(grid);
		console.log(index);
		console.log(this);
		*/

		this.operation_Sort(index);
	}, 

	handler_Header_Menu_Click: function(menuItem) {
		/*
		console.log('method: handler_Header_Menu_Click');
		console.log(menuItem);
		console.log(this);
		*/

		this.operation_Sort(this.grid.view.hdCtxIndex, menuItem);
	}, 

	handler_Store_Load: function(store, records, options) {
		/*
		console.log('method: handler_Store_Load');
		console.log(store);
		console.log(records);
		console.log(options);
		console.log(this);
		*/

		this.originalRecordStore = new Ext.ux.data.OriginalRecordStore(records);
	}, 

	init: function(config) {
		/*
		console.log('method: init');
		console.log(config);
		console.log(this);
		*/

		Ext.apply(this, config);

		this.actualPage = 0;
		this.nextPage = 0;
		this.originalRecordStore = {};
		this.erroneousRecords = [];

		Ext.Msg.buttonText.yes = this.buttonYes;
		Ext.Msg.buttonText.no = this.buttonNo;
		Ext.Msg.buttonText.cancel = this.buttonCancel;

		var gridStore = null;

		if (!Ext.isEmpty(this.grid)) {
			var gridPager = this.grid.getBottomToolbar();

			// event-handlers for paging
			gridPager.on('beforechange', this.handler_Pager_BeforeChange.createDelegate(this));
			gridPager.on('change', this.handler_Pager_Change.createDelegate(this));

			// event-handlers for sorting
			this.grid.un('headerclick', this.grid.view.onHeaderClick, this.grid.view);
			this.grid.on('headerclick', this.handler_Header_Click.createDelegate(this));

			this.grid.view.hmenu.un('itemclick', this.grid.view.handleHdMenuClick, this.grid.view);
			this.grid.view.hmenu.on('itemclick', this.handler_Header_Menu_Click.createDelegate(this));

			// event-handlers for loading data
			gridStore = this.grid.getStore();

			gridStore.on('load', this.handler_Store_Load.createDelegate(this));
		}

		var saveButton = this.getOpButtonById(this.saveButtonId);

		if (!Ext.isEmpty(saveButton)) {
			saveButton.on('click', this.handler_Button_Save.createDelegate(this));
		}

		var resetButton = this.getOpButtonById(this.resetButtonId);

		if (!Ext.isEmpty(resetButton)) {
			resetButton.on('click', this.handler_Button_Reset.createDelegate(this));
		}

		this.initSpecific();

		if (!Ext.isEmpty(gridStore)) {
			gridStore.load({
				params:		{start: this.actualPage, limit: this.pageSize, sort: '', query: ''},
				callback:	function(records, options, success) {
					var selModel = this.grid.selModel, store = this.grid.store;
					if (store.getTotalCount() != 0) {
						selModel.select(0, 0, false, false, store.getAt(0));
					}
				}, 
				scope:		this
			});
		}
	}, 

	initSpecific: function() {
		/*
		console.log('method: initSpecific');
		console.log(this);
		*/
		// implement this method in the subclasses of this EditableList, 
		// if you need specific init statements to be executed.
	}, 

	mask: function() {
		/*
		console.log('method: mask');
		console.log(maskMsg);
		console.log(this);
		*/

		if (!Ext.isEmpty(this.grid)) {
			var container = this.grid.getEl();

			if (!Ext.isEmpty(container)) {
				container.mask(this.maskMessage, 'x-mask-loading');
			}
		}
	}, 

	operation_Save: function(opDesc) {
		/*
		console.log('method: operation_Save');
		console.log(opDesc);
		console.log(this);
		*/

		this.mask();

		var messages = this.validateClientSide();
		var changes = this.getAdjustedRecords();
		var unmask = true;

		if (!Ext.isEmpty(messages) && messages.hasErrors()) {
			messages.displayMessagesWithType(Ext.ux.valid.ValidationMessage.TYPE_ERROR);
		}
		else if (!Ext.isEmpty(changes) && (changes.length > 0)){
			var callbackForSave = 
				this.postProcessForSave.createDelegate(this, [opDesc], true);

			this.callServiceForSave(changes, callbackForSave);

			unmask = false;
		}
		else {
			Logon.message.warning(this.noChangesMessage);
		}

		if (unmask) {
			this.unmask();
		}
	}, 

	operation_Sort : function(index, menuItem) {
		/*
		console.log('method: operation_Sort');
		console.log(index);
		console.log(menuItem);
		console.log(this);
		*/

		if (!Ext.isEmpty(this.grid) && !Ext.isEmpty(this.grid.view)) {

			if (this.grid.view.headersDisabled || !this.grid.view.cm.isSortable(index)) {
				return;
			}

			this.grid.stopEditing(false);

			var sorterCallback = this.performSort.createDelegate(this, [index, menuItem], true);
			var doSort = true;
			var changes = this.getAdjustedRecords();

			if (!Ext.isEmpty(changes) && (changes.length > 0)) {
				Ext.Msg.show({
					title:	this.pageChangeTitle,
					msg: this.pageChangeMessage,
					buttons: Ext.Msg.YESNOCANCEL,
					scope: this,
					fn: function(buttonId) {
						if (buttonId === 'yes') {
							/*
								as 'scope' is set to 'this', that refers to the 'editor' 
								object in this very case.
								TAKE CARE of the modifications of the SCOPE ('this')!!! 
							*/
							/*
							console.log('operation_Sort:Ext.Msg.show.fn');
							console.log(this);
							*/
							this.operation_Save(
								new Ext.ux.grid.OpDescriptor(
									Ext.ux.grid.OpDescriptor.STYPE_SORTER, false, sorterCallback));
						}
						else if (buttonId === 'no') {
							this.rejectChanges();

							sorterCallback.call();
						}
					}, 
					icon: Ext.MessageBox.QUESTION
				});
			} 
			else {
				sorterCallback.call();
			}
		}
	}, 

	performSort : function(index, menuItem) {
		/*
		console.log('method: performSort');
		console.log(index);
		console.log(menuItem);
		console.log(this);
		*/

		var store = this.grid.store;
		var cmodel = this.grid.view.cm;
		var actDir = null;

		if (!Ext.isEmpty(menuItem)) {
			switch (menuItem.id) {
				case "asc":
					actDir = 'ASC';
				break;

				case "desc":
					actDir = 'DESC';
				break;

				default:
					actDir = "ASC";
			}
		}

		store.sort(cmodel.getDataIndex(index), actDir);
	},

	postProcessForSave: function(saveResult, opDesc) {
		/*
		console.log('method: postProcessForSave');
		console.log(saveResult);
		console.log(opDesc);
		console.log(this);
		*/

		var serverMessages = new Ext.ux.valid.ValidationMessages();

		serverMessages.processDwrMessages(saveResult);

		if (serverMessages.hasOnlyErrors()) {
			this.unmask();

			serverMessages.displayMessagesWithType(Ext.ux.valid.ValidationMessage.TYPE_ERROR);

			return;
		}

		if (Ext.isEmpty(this.grid) || Ext.isEmpty(this.grid.getStore())) {
			alert('implementation error');

			return;
		}

		var gridStore = this.grid.getStore();

		if (!serverMessages.hasErrors()) {

			// it's needed to make the modified records 
			gridStore.commitChanges();

			this.unmask();

			serverMessages.displayMessages();

			if (opDesc.pchFlag) {
				this.changePage();
			} 
			else if (opDesc.isItCalledBySorter()) {
				opDesc.cbFunction.call();
			}
			else {
				var startValue = this.actualPage * this.pageSize ;

				gridStore.load({params:{start: startValue, limit: this.pageSize, sort: '', query: ''}});
			}
		}
		else {
			var startValue = this.actualPage * this.pageSize ;

			this.setErroneousRecords(serverMessages.getErroneousDwrRecordIds());

			this.unmask();

			serverMessages.displayMessages();

			gridStore.load({
				params: {start: startValue, limit: this.pageSize, sort: '', query: ''}, 
				callback: this.restoreChangesForErroneousRecords, 
				scope: this
			});
		}
	}, 

	rejectChanges: function() {
		/*
		console.log('method: rejectChanges');
		console.log(this);
		*/

		if (!Ext.isEmpty(this.grid)) {
			var gridStore = this.grid.getStore();

			gridStore.rejectChanges();
		}
	}, 

	restoreChangesForErroneousRecords: function(records, options, success) {
		/*
		console.log('method: restoreChangesForErroneousRecords');
		console.log(records);
		console.log(options);
		console.log(success);
		console.log(this);
		*/

		if (Ext.isEmpty(this.erroneousRecords) || 
			Ext.isEmpty(this.grid) || 
			Ext.isEmpty(this.grid.getStore())) {
			return;
		}

		var gridStore = this.grid.getStore();

		gridStore.modified = new Array();

		for (var index = 0; index < this.erroneousRecords.length; index++) {
			var eRecord = this.erroneousRecords[index];
			var aRecord = gridStore.getById(eRecord.id);

			if ((!Ext.isEmpty(eRecord)) && (!Ext.isEmpty(eRecord.fields)) && (!Ext.isEmpty(eRecord.fields.keys)) &&
				(!Ext.isEmpty(aRecord)) && (!Ext.isEmpty(aRecord.fields)) && (!Ext.isEmpty(aRecord.fields.keys)) &&
				(eRecord.fields.keys.length === aRecord.fields.keys.length)) {
				var recProperties = eRecord.fields.keys;

				for (var fIndex = 0; fIndex < recProperties.length; fIndex++) {
					var key = recProperties[fIndex];
					var eValue = eRecord.get(key);
					var aValue = aRecord.get(key);

					if (aValue != eValue) {
						aRecord.set(key, eValue);
					}
				}
			}
		}
	}, 

	setErroneousRecords: function(recordIds) {
		/*
		console.log('method: setErroneousRecords');
		console.log(recordIds);
		console.log(this);
		*/

		this.erroneousRecords = new Array();

		if (Ext.isEmpty(recordIds) || 
			Ext.isEmpty(this.grid) || 
			Ext.isEmpty(this.grid.getStore())) {
			return result;
		}

		var gridStore = this.grid.getStore();

		for (var index = 0; index < recordIds.length; index++) {
			var record = gridStore.getById(recordIds[index]);

			if (!Ext.isEmpty(record)) {
				var cRecord = record.copy();

				cRecord.dirty = record.dirty;
				cRecord.editing = record.editing;
				cRecord.error = record.error;
				cRecord.modified = record.modified;

				this.erroneousRecords.push(cRecord);
			}
		}
	}, 

	unmask: function() {
		/*
		console.log('method: unmask');
		console.log(this);
		*/

		if (!Ext.isEmpty(this.grid)) {
			var container = this.grid.getEl();

			if (!Ext.isEmpty(container)) {
				container.unmask();
			}
		}
	}, 

	validateRecords: function(pRecords, pMessages) {
		/*
		console.log('method: initSpecific');
		console.log(pRecords);
		console.log(pMessages);
		console.log(this);
		*/
		// implement this method in the subclasses of this EditableList, 
		// if you need specific validate statements to be executed.
		// Parameters:
		//				- pRecords: array of the records displayed/edited in the grid (represented with Ext.data.Record objects)
		//				- pMessages: instance of Ext.ux.valid.ValidationMessages, that we can add an Ext.ux.valid.ValidationMessage instance to.
		// Example:
		// 			pMessages.add(
		//				new Ext.ux.valid.ValidationMessage(
		//					Ext.ux.valid.ValidationMessage.TYPE_ERROR, null, null, 'some text'));
	}, 

	validateClientSide: function(messages) {
		/*
		console.log('method: validateClientSide');
		console.log(messages);
		console.log(this);
		*/

		if (Ext.isEmpty(messages)) {
			messages = new Ext.ux.valid.ValidationMessages();
		}

		if (!Ext.isEmpty(this.grid)) {
			var gridStore = this.grid.getStore();

			if (!Ext.isEmpty(gridStore)) {
				var records = gridStore.getRange();

				if ((!Ext.isEmpty(records)) && (records.length > 0)) {
					this.validateRecords(records, messages);
				}
			}
		}

		return messages;
	} 

};
