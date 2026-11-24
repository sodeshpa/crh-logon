function createNewPickup(){
	document.getElementById("inputAction").value=actions.CREATE_NEW_PICKUP;
	document.forms[0].submit();
	disableAll();
}

function saveNewOrder(){
	document.getElementById("inputAction").value=actions.SAVE_NEW;
	document.forms[0].submit();
	disableAll();
}

function selectShipto(){
	document.getElementById("inputAction").value=actions.SELECT_SHIPTO;
	document.forms[0].submit();
	disableAll();
}