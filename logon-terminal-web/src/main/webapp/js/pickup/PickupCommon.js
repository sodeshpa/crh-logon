function removeItem(itemId, restrId){
	document.getElementById("inputOrderItemId").value=itemId;
	document.getElementById("inputRestrictionId").value=restrId;
	document.getElementById("inputAction").value=actions.REMOVE_ITEM;
	document.forms[0].submit();
	disableAll();
}

function addNewItem(){
	document.getElementById("inputAction").value=actions.ADD_ITEM_ENTRY;
	document.forms[0].submit();
	disableAll();
}

function cancelAddItem(){
	document.getElementById("inputAction").value=actions.CANCEL_ADD_ITEM;
	document.forms[0].submit();
	disableAll();
}

function cancelAddedItem(){
	document.getElementById("inputAction").value=actions.CANCEL_ADDED_ITEM;
	document.forms[0].submit();
	disableAll();
}

function selectProductAsRestriction(){
	document.getElementById("inputAction").value=actions.SELECT_PRODUCT;
	document.forms[0].submit();
	disableAll();
}

function selectBillto(){
	document.getElementById("inputAction").value=actions.SELECT_BILLTO;
	document.forms[0].submit();
	disableAll();
}

function changeReturnPalletNumber() {
	document.getElementById("inputAction").value=actions.CHANGE_RETURN_PALLET_QUANTITY;
	document.forms[0].submit();
	disableAll();	
}

function changePalletNumber() {
	document.getElementById("inputAction").value=actions.CHANGE_PALET_QUANTITY;
	document.forms[0].submit();
	disableAll();
}

function cancelCreateOrder(){
	document.getElementById("inputAction").value=actions.CANCEL_CREATE;
	document.forms[0].submit();
	disableAll();
}
function cancelEditOrder(){
	document.getElementById("inputAction").value=actions.CANCEL_EDIT;
	document.forms[0].submit();
	disableAll();
}
function cancelDefineReturn(){
	document.getElementById("inputAction").value=actions.CANCEL_DEFINE_RETURN;
	document.forms[0].submit();
	disableAll();
}

function editDeliveryNote(){
	document.getElementById("inputAction").value=actions.DELIVERY_NOTE;
	document.forms[0].submit();
	disableAll();
}

