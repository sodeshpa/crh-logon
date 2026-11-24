
/*    function updateUserStatus(id, val){
		// post items via ajax call
		var callback = function(result){
				store.reload();
		};
		var callParams = [id, val, callback];
	
		UserAdminAjaxService.updateUserStatus.apply(this, callParams);
	}
*/


		
	function destroyUserDetail(){
		if(Ext.getDom('user-details-table')){
			//destroying panels
			rolePanel.destroy();
			rolePanel = undefined;
			userDetailsPanel.destroy();
			userDetailsPanel = undefined;
			userExternalAccessPanel.destroy();
			userExternalAccessPanel = undefined;
			//removing dom node of details table
			Ext.removeNode(Ext.getDom('user-details-table'));
		}
	}

	


