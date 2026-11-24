function selectNewTruck(){
	document.getElementById("inputAction").value=actions.SELECT_NEW_TRUCK;
	document.forms[0].submit();
	disableAll();
}

function changeTruck(){
	document.getElementById("inputAction").value=actions.CHANGE_TRUCK;
	document.forms[0].submit();
	disableAll();
}

function cancelSelectTruck(){
	document.getElementById("inputAction").value=actions.CANCEL_CHANGE_TRUCK;
	document.forms[0].submit();
	disableAll();
}