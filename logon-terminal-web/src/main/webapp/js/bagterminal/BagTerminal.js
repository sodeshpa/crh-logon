function enableElement(id){
	var input = document.getElementById(id);
	if (input) {
		input.disabled=false;
		input.hidden=false;
	}
}

function disableElement(id){
	var input = document.getElementById(id);
	if (input) {
		input.disabled=true;
		input.hidden=true;
	}
} 

function logout(){
	window.location.href = logoutHref; 
	
}

var currentZebra;

function selectAssignment(id, tr){
	
	resetZebraOnSelected();
	var input = document.getElementById("inputAssignmentId");
	var table = document.getElementById("bagOrderTable");
	var trs = table.getElementsByTagName("tr");
	input.value=id;
	for (var i=0; i<trs.length; i++){
		if(trs[i].id==id){
			currentZebra = trs[i].className;
			trs[i].className="focused";
		}
	}
}

function resetZebraOnSelected(){
	if ( document.getElementById("inputAssignmentId").value ){
		var id = document.getElementById("inputAssignmentId").value;
		var table = document.getElementById("bagOrderTable");
		var trs = table.getElementsByTagName("tr");
		for (var i=0; i<trs.length; i++){
		if(trs[i].id==id){
			trs[i].className=currentZebra;
		}}
	}
}

function disableAll(){
	var array = document.getElementsByTagName('input');
	for (var i = 0 ; i<array.length; i++ ) {
		array[i].disabled = true;
	}
}

function enableAll(){
	var array = document.getElementsByTagName('input');
	for (var i = 0 ; i<array.length; i++ ) {
		array[i].disabled = false;
	}
}
