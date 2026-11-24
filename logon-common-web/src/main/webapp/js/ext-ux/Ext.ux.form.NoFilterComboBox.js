/**
 * A special combo box that doesn't change the filtering of the combo box
 */

Ext.namespace('Ext.ux.form');
Ext.ux.form.NoFilterComboBox = function (config) {
	Ext.ux.form.NoFilterComboBox.superclass.constructor.call(this, {...config, editable: false});
};
Ext.extend(Ext.ux.form.NoFilterComboBox, Ext.form.ComboBox, {

	doQuery: function (q, forceAll) {
		if (q === undefined || q === null) {
			q = '';
		}
		var qe = {
			query: q,
			forceAll: forceAll,
			combo: this,
			cancel: false
		};
		if (this.fireEvent('beforequery', qe) === false || qe.cancel) {
			return false;
		}
		q = qe.query;
		forceAll = qe.forceAll;
		if (forceAll === true || (q.length >= this.minChars)) {
			if (this.lastQuery !== q) {
				this.lastQuery = q;
				if (this.mode === 'local') {
					this.selectedIndex = -1;
					// removed the filter and clearFilter calls as we don't want to touch the filter itself
					this.onLoad();
				}
				else {
					this.store.baseParams[this.queryParam] = q;
					this.store.load({
						params: this.getParams(q)
					});
					this.expand();
				}
			}
			else {
				this.selectedIndex = -1;
				this.onLoad();
			}
		}
	}
});