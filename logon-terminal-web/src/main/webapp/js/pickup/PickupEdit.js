function removeItem(index){
	document.getElementById("selectedCustorderItemBeanIndex").value=index;
	document.getElementById("inputAction").value=actions.REMOVE_ITEM;
	document.forms[0].submit();
	disableAll();
}
function addNewItem(){
	document.getElementById("inputAction").value=actions.ADD_ITEM_ENTRY;
	document.forms[0].submit();
	disableAll();
}

function saveOrder(){
	document.getElementById("inputAction").value=actions.SAVE;
	document.forms[0].submit();
	disableAll();
}

function editPickup(){
	document.getElementById("inputAction").value=actions.EDIT_PICKUP;
	document.forms[0].submit();
	disableAll();
}

function skipReturnPallet() {
	document.getElementById("inputAction").value=actions.SKIP_RETURN_PALET_HANDLING;
	document.forms[0].submit();
	disableAll();	
}

function changeReturnPalletNumber(index) {
	document.getElementById("inputAction").value=actions.CHANGE_RETURN_PALLET_QUANTITY;
	var input = document.getElementById("selectedPalletItemBeanIndex");
	input.value=index;
	document.forms[0].submit();
	disableAll();	
}

function changePalletNumber(index) {
	document.getElementById("inputAction").value=actions.CHANGE_PALET_QUANTITY;
	var input = document.getElementById("selectedPalletItemBeanIndex");
	input.value=index;
	document.forms[0].submit();
	disableAll();	
}
