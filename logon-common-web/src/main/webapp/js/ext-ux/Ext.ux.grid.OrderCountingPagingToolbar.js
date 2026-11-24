Ext.namespace('Ext.ux.grid');

Ext.ux.grid.OrderCountingPagingToolbar = Ext.extend(Ext.PagingToolbar,{ 
	
	updateInfo : function() {
			this.loadInformations();
	},
	
	update : function(orderSearchInformations) {
		this.activePageIndex = -1;
    	this.uniqueCountOfOrders = orderSearchInformations.totalOrderCount;
    	this.beforeActiveUniqueOrdersCount = orderSearchInformations.countOrdersBeforeActivePage;
    	this.displayMessage();
    },
    
    setSearchArea : function(searchArea) {
    	this.cursor = 0;
    	this.searchArea = searchArea;
    },
    
    loadInformations : function() {
    	this.searchArea.updatePagingBar(this.cursor);
    },
    
    displayMessage: function() {
    	if (!Ext.isEmpty(this.displayEl) && !Ext.isEmpty(this.totalOrderCount) && this.totalOrderCount >= 0) {
    		var emptyMessage = "";
	    	var pagingMessage = String.format(
				this.displayMsg,
				this.beforeActiveUniqueOrdersCount + 1,
				Math.min(this.beforeActiveUniqueOrdersCount + this.activePageUniqueOrdersCount, this.uniqueCountOfOrders),
				this.uniqueCountOfOrders,
				this.cursor + 1,
				this.cursor + this.activePageOrderItemCount,
				this.totalOrderCount
			);
	    	if (this.uniqueCountOfOrders == 0) {
	    		this.displayEl.update(emptyMessage);
	    	}
	    	else {
	    		this.displayEl.update(pagingMessage);
	    	}
		}
    }
}
);
