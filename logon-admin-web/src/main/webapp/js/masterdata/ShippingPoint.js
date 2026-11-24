// LoadingStreet.jsp

function checkAllMaterials() {
	var formblock = document.getElementById('materialsDiv');
	var forminputs = formblock.getElementsByTagName('input');
	for (i = 0; i < forminputs.length; i++) {
		forminputs[i].checked = true;
	}
}

function uncheckAllMaterials() {
	var formblock = document.getElementById('materialsDiv');
	var forminputs = formblock.getElementsByTagName('input');
	for (i = 0; i < forminputs.length; i++) {
		forminputs[i].checked = false;
	}
}

function loadMaterials(packTypeCd) {
	var diva = Ext.get('materialsDiv');
	diva.load( {
		url :'ShippingPoint.do',
		params : {
			'loadMaterials' :'',
			'packTypeCd' :packTypeCd
		},
		text :texts.exjsloading,
		scripts :true
	});
}

function selectDeliveryNote(value) {
	if (value == 1) {
		Ext.getDom('printerCd').disabled = false;
		Ext.getDom('delivNoteCopiesLogon').disabled = false;
	} else {
		Ext.getDom('printerCd').disabled = true;
		Ext.getDom('printerCd').value = -1;
		Ext.getDom('delivNoteCopiesLogon').disabled = true;
		Ext.getDom('delivNoteCopiesLogon').value = '';
	}
}

// --- ShippingPoint.jsp ---

function submitWithAction(action) {
	form = Ext.getDom('ShippingPointForm');
	Ext.query("input[name=formAction]", form)[0].value = action;
	//form.submit();

	p = {
		'formAction' :action
	};
	addFormElements(p, form);

	var spDiv = Ext.get('ShippingPointDiv');
	spDiv.load( {
		url :'ShippingPoint.do',
		params :p,
		text :texts.exjsloading,
		scripts :true
	});

}

function setCurrentLPoint(lpindex) {
	//heigh should remain the same while changing loading point
	document.getElementById('DivLoadingStreet').style.height = '354px';

	selectTab("LoadingStreetTabs", "TabLS" + lpindex);

	form = Ext.getDom('ShippingPointForm');
	p = {
		'selecttab' :'',
		'lpindex' :lpindex
	};
	addFormElements(p, form);

	var diva = Ext.get('DivLoadingStreet');
	diva.load( {
		url :'ShippingPoint.do',
		params :p,
		text :texts.exjsloading,
		scripts :true
	});
}

function addLoadingPoint() {
	//heigh should remain the same while adding new loading point
	document.getElementById('DivLoadingStreet').style.height = '354px';

	form = Ext.getDom('ShippingPointForm');
	p = {
		'addtab' :''
	};

	addFormElements(p, form);

	var diva = Ext.get('DivLoadingStreet');
	diva.load( {
		url :'ShippingPoint.do',
		params :p,
		text :texts.exjsloading,
		scripts :true,
		callback: function(el){
			document.getElementById('loadingpointName').focus();
		}
	});
}

function removeLoadingPoint() {
	Ext.MessageBox.buttonText.yes = texts.messageBoxDelete;
	Ext.MessageBox.buttonText.no = texts.messageBoxCancel;

	Ext.MessageBox
			.confirm(
					texts.messageBoxConfirm,
					texts.messageBoxText,
					function(btn) {
						if (btn == 'yes') {

							//do not show content if there are no tabs
							var tabs = document.getElementById(
									"subnavigation").getElementsByTagName(
									'li').length;
							if ((tabs === 0) || (tabs === 1)) {
								document.getElementById('DivLoadingStreet').style.height = '0px';
							}

							form = Ext.getDom('ShippingPointForm');
							p = {
								'removetab' :''
							};

							var diva = Ext.get('DivLoadingStreet');
							diva
									.load( {
										url :'ShippingPoint.do',
										params :p,
										text :texts.exjsloading,
										scripts :true
									});
						}
					});

}

function onLoadTab() {
}

function onUnloadTab() {
}

function selectSPTypeCd() {
	form = Ext.getDom('ShippingPointForm');
	value = form.shippointTypeDD.options[form.shippointTypeDD.selectedIndex].value;
	
	if (value == 3) {
		form.railArrivalWarningMin.disabled = false;
		form.fvlMixShiptoPerWagonFlag.disabled = false;
	} else {
		form.railArrivalWarningMin.disabled = true;
		form.railArrivalWarningMin.value = '';
		form.fvlMixShiptoPerWagonFlag.disabled = true;
		form.fvlMixShiptoPerWagonFlag.checked = false;
	}	
}

function updateObjectLabelShowRFIDPopup(){
	var inputObjectLabel = document.getElementById("loadingpointObjectLabel").value;
	AdminRFIDPopupAjaxService.selectLoadingPointByLabel(inputObjectLabel,function(){
		showPopup('./AdminRFIDPopup.do');
		});
}
