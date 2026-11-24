function MarkerManager(map) {
  var me = this;
  me.map = map;
  this.markersArray = [];
}

/**
 * Add a single marker to the map.
 * 
 * @param {Marker}
 *            marker The marker to add.
 */
MarkerManager.prototype.addMarker = function (marker) {
	this.markersArray.push(marker)
};

MarkerManager.prototype.showMarkers = function () {
	for (var i = 0; i < this.markersArray.length; i++ ) {
		this.markersArray[i].setMap(this.map);
	 }
};

MarkerManager.prototype.clearMarkers = function () {
	if (this.markersArray) {
		for (var i = 0; i < this.markersArray.length; i++ ) {
			this.markersArray[i].setMap(null);
		}
		this.markersArray = [];
	 }
};
