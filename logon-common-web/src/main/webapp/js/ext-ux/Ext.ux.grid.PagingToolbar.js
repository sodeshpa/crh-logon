Ext.namespace('Ext.ux.grid');

Ext.ux.grid.PagingToolbar = Ext.extend(Ext.PagingToolbar, {

	pageSemaphore: 'off', 

	constructor: function(config) {
		Ext.ux.grid.PagingToolbar.superclass.constructor.apply(this, arguments);
	},

	getPageSize : function() {
		return this.pageSize;
	},

	isPageSemaphoreOn: function() {
		return (this.pageSemaphore === 'on')
	},

	setPageSemaphore: function(indicator) {
		this.pageSemaphore = (indicator === true ? 'on' : 'off');
	},

	onLoad: function(store, records, options) {
		if (!this.rendered) {
			this.dsLoaded = [store, records, options];

			return;
		}

		var maxNbrOfItems = this.store.getTotalCount();

		this.cursor = options.params ? options.params[this.paramNames.start] : 0;

		if (this.cursor >= maxNbrOfItems) {
			var lastPageIndex = Math.floor(maxNbrOfItems/this.pageSize) - 1;

			if (lastPageIndex < 0) {
				lastPageIndex = 0;
			}

			this.cursor = lastPageIndex * this.pageSize;
		}

		var pageData = this.getPageData();

		this.afterTextEl.el.innerHTML = String.format(this.afterPageText, pageData.pages);
		this.field.dom.value = pageData.activePage;
		this.first.setDisabled(pageData.activePage == 1);
		this.prev.setDisabled(pageData.activePage == 1);
		this.next.setDisabled(pageData.activePage == pageData.pages);
		this.last.setDisabled(pageData.activePage == pageData.pages);
		this.loading.enable();
		this.updateInfo();
		this.fireEvent('change', this, pageData);
	}

});
