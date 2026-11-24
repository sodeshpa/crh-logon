useradmin = {
	texts: {},
	divId: 'user-details-div',
	actionUrl: 'UserAdmin.do',
	formName: 'admin_UserAdminFormUserAdmin',
	init : function(loadingText, optimisticLockText) {
		useradmin.texts.loadingText = loadingText;
		useradmin.texts.optimisticLockText = optimisticLockText;
	},
	// public functions
	search : function() {
		document.admin_UserAdminForm.inputAction.value="search";
		document.admin_UserAdminForm.submit();
	},
	submitWithActionToDiv : function (action, formId, additionalRequestParams) {
		
		return useradmin.doSubmitWithActionToDiv('UserAdmin.do', action, formId, useradmin.divId, additionalRequestParams)
	},
	submitWithActionToDivSelf : function (action, formId, additionalRequestParams) {
		
		return useradmin.doSubmitWithActionToDiv('SelfAdmin.do', action, formId, useradmin.divId, additionalRequestParams)
	},
	modifyUser : function (userId) {
		form = Ext.getDom('admin_UserAdminForm');
		Ext.query("input[name=inputAction]", form)[0].value = 'modifyUser';

		p = {
			'formAction' :'modifyUser',
			'userId' : userId
		};
		addFormElements(p, form);

		var div = Ext.get(useradmin.divId);
		div.load( {
			url :'UserAdmin.do',
			params : p,
			text : useradmin.texts.loadingText,
			scripts :true,
			timeout: 4000,
			nocache: true
		});
	},
	selectDeselectAllOptions : function (id, selectedState) {
		var el = Ext.getDom(id);
		if(selectedState === true && el.options.length > 100) { 
			alert('Too many selections, please make the assignment in smaller units !');
			return;
		}
		for(i=0; i < el.options.length; i++){
			el.options[i].selected = selectedState;
		}
	},
	handleExternalAccess : function (checked, self){
		if (checked === true) {
			if(self === true) {
				showPopup('UserAdminExtLoginPwdPopupEntry.do?self&accesschange','');
			}
			else {
				showPopup('UserAdminExtLoginPwdPopupEntry.do?accesschange','');
			}
		}
		else {
			useradmin.submitWithActionToDiv('disableExternalAccess', 'admin_UserAdminFormUserAdmin', useradmin.divId);
		}
	},
	pwdChange : function (self, type) {
		if ( (type !== 0) || (document.getElementById('userExternalAccessPanel_extAktiv').checked === true) ) {
			if(self === true) {
				showPopup('UserAdminExtLoginPwdPopupEntry.do?self=&pwdformtype=' + type,'');
			}
			else {
				showPopup('UserAdminExtLoginPwdPopupEntry.do?pwdformtype=' + type,'');
			}
		}
	},
	removePool : function (poolId) {
		useradmin.executeAction('removePool', 'poolId', poolId);
	},
	removeCarrier : function (carrierId) {
		useradmin.executeAction('removeCarrier', 'carrierId', carrierId);
	},
	removeShipTo : function (shipToId) {
		useradmin.executeAction('removeShipToCS', 'shipToId', shipToId);
	},
	removeCustomer : function (customerId) {
		useradmin.executeAction('removeCustomer', 'customerId', customerId);
	},
	removePlant : function (plantId) {
		useradmin.executeAction('removePlant', 'plantId', plantId);
	},
	removeShippingPoint : function (shippingPointId) {
		useradmin.executeAction('removeShippingPoint', 'shippingPointId', shippingPointId);
	},
	removeSarea :function (sareaId) {
		useradmin.executeAction('removeSarea', 'sareaId', sareaId);
	},
	removeSgroup : function (sgroupId) {
		useradmin.executeAction('removeSgroup', 'sgroupId', sgroupId);
	},
	removeCompCode : function (compCodeId) {
		useradmin.executeAction('removeCompCode', 'compCodeId', compCodeId);
	},
	removePurchOrg : function (purchOrgId) {
		useradmin.executeAction('removePurchOrg', 'purchOrgId', purchOrgId);
	},
	removeRole : function (roleId) {
		useradmin.executeAction('removeRole', 'roleId', roleId);
	},
	updatePdaUserNoShowRFIDPopup : function () {
		var pdaUserNumber = document.getElementById("userDetailsPanel_pdaUserNumber").value;
		if (isNaN(parseFloat(pdaUserNumber))) {
			alert(texts.alertRfid);
			return false;
		}
		AdminRFIDPopupAjaxService.selectUserByPdaUserNumber(pdaUserNumber, function() {
			showPopup('../AdminRFIDPopup.do');
		});
	},
	// private helpers
	doSubmitWithActionToDiv : function (url, action, formId, divId, additionalRequestParams) {
		form = Ext.getDom(formId);
		Ext.query("input[name=inputAction]", form)[0].value = action;
		
		p = {
				'formAction' :action
		};
		
		var param;
		for(param in additionalRequestParams) p[param]= additionalRequestParams[param];

		addFormElements(p, form);

		executeUpdate.run(divId,
				url, p,
				url, {'inputAction' : 'modifyUser'},
				'.*OptimisticLockException.*', useradmin.texts.optimisticLockText);

	},
	executeAction : function (action, parameterName, parameterValue) {
		form = Ext.getDom(useradmin.formName);
		Ext.query("input[name=inputAction]", form)[0].value = action;
		p = { formAction : action };
		p[parameterName] = parameterValue;

		addFormElements(p, form);
		
		var spDiv = Ext.get(useradmin.divId);
		spDiv.load( {
			url : useradmin.actionUrl,
			params : p,
			text : useradmin.texts.loadingText,
			scripts :true
		});
	}
}