Ext.namespace('Ext.ux');

Ext.ux.MaterialCombo = Ext.extend(Ext.form.ComboBox, {
	mode : 'local',
	forceSelection : true,
	width : 120,
	listWidth : 1000,
	allowBlank : true,
	selectOnFocus : true,
	editable : true,
	shadow : false,
	triggerAction : 'all',
	mode : 'local',
	valueField : 'compositeIdentifier',
	displayField : 'txt',
	searchByTextField : 'searchByText',
	doQuery : function(q, forceAll) {
		if (q === undefined || q === null) {
			q = '';
		}
		var qe = {
			query : q,
			forceAll : forceAll,
			combo : this,
			cancel : false
		};
		if (this.fireEvent('beforequery', qe) === false || qe.cancel) {
			return false;
		}
		q = qe.query;
		forceAll = qe.forceAll;
		if (forceAll === true || (q.length >= this.minChars)) {
			if (this.lastQuery !== q) {
				this.lastQuery = q;
				if (this.mode == 'local') {
					this.selectedIndex = -1;
					if (forceAll) {
						this.store.clearFilter();
					} else {
						this.store.filter(this.searchByTextField, q, true);
					}
					this.onLoad();
				} else {
					this.store.baseParams[this.queryParam] = q;
					this.store.load({
						params : this.getParams(q)
					});
					this.expand();
				}
			} else {
				this.selectedIndex = -1;
				this.onLoad();
			}
		}
	},
	itemSelector : 'tr.listitem',
	remoteSort : false,
	getSelectedRecord : function() {
		return this.store.getAt(this.selectedIndex);
	}
});