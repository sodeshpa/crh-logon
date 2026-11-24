// JavaScript Document

//var start_datum = "19.02.2009 08:00";
//var end_datum   = "19.02.2009 16:00";


function parseDate(datum) {
	var format = /(\d\d)\.(\d\d)\.(\d\d\d\d) (\d\d):(\d\d)/;

	format.exec(datum);
	var year = RegExp.$3;
	var month = RegExp.$2;
	var date = RegExp.$1;
	var hours = RegExp.$4;
	var minutes = RegExp.$5;
	var seconds = 0;

	return new Date(year, month, date, hours, minutes, seconds);
}

function fmt(n) {
	return n < 10 ? '0' + n : n;	
}

//function getPosition(v, start_datum, end_datum) {
function getPosition(v) {
//	alert('gugus');
	var start_datum = document.getElementById("start").value;
//	alert('gaga');
	var end_datum   = document.getElementById("end").value;
//alert(end_datum);
	
	//var start = parseDate(start_datum);
	var start = parseDate(start_datum, 'd.m.Y H:i');
	//var end = parseDate(end_datum);
	var end = parseDate(end_datum, 'd.m.Y H:i');
	
	var s = Date.parse(start, 'd.m.Y H:i'.toLocaleString());
	
	var e = Date.parse(end, 'd.m.Y H:i'.toLocaleString());
	
	var t = s + v*(e - s);
	
	var position = new Date(t);
	
	
	var year = position.getFullYear();
	var month = position.getMonth();
	var date = position.getDate();
	var hours = position.getHours();
	var minutes = position.getMinutes();
	
	var position_date = fmt(date) + '.' + fmt(month) + '.' + year + ' ' + fmt(hours) + ':' + fmt(minutes);
	
	return position_date;
}