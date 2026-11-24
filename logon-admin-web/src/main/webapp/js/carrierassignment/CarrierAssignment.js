//--- CarrierAssignment.jsp ---

function modifyFilter(params){
	addFormElements(params, "CarrierAssignmentForm");
	var diva = Ext.get('carrierAssignmentDiv');
	diva.load({
		url: 'CarrierAssignment.do', 
		params: params,
		text: texts.exjsloading,
		scripts: true
	});
}

function submitWithAction(action){
	form = Ext.getDom('CarrierAssignmentForm');
	Ext.query("input[name=formAction]", form)[0].value = action;
	form.submit();
}

// CarrierAssignmentTable.jsp
function setCheckbox( checkboxId, dropdownId ) {
	var dropdown = document.getElementById(dropdownId);
	var checkbox = document.getElementById(checkboxId);
	if ( dropdown.value == "-1" ) {
		checkbox.disabled = true;
		checkbox.checked = false;
	}
	else {
		checkbox.disabled = false;
	}
}