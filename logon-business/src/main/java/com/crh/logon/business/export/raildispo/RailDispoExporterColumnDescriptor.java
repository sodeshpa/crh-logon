/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.export.raildispo;

public enum RailDispoExporterColumnDescriptor {

	// Wagon Tab
	WAGON_KEEPER(1, 20),
	WAGON_ETRAN_OUT(8, 10),
	WAGON_ETRAN_RET(9, 10),
	WAGON_WEIGHING_NOTE_ORDER(40, 75),
	WAGON_WEIGHING_NOTE_WAGON(41, 75),
	WAGON_SCHEDULING_NOTE_ORDER(42, 75),
	WAGON_SCHEDULING_NOTE_WAGON(43, 75),
	// Order Tab
	ORDER_WEIGHING_NOTE_ORDER(23, 75),
	ORDER_SCHEDULING_NOTE_ORDER(24, 75);

	private int columnIndex;
	private int width;
	private RailDispoExporterColumnDescriptor(int columnIndex, int width) {
		this.columnIndex = columnIndex;
		this.width = width;
	}
	public int getColumnIndex() {
		return columnIndex;
	}
	/**
	 * The width to this enum is given in number of characters.
	 * But we need to multiply that by 256 since that is how POI handles the fonts.
	 * Then for margins (or paddings rather) we add 3 characters.
	 * Please note POI sets the width in units of 1/256th of a character width
	 * See: http://poi.apache.org/apidocs/org/apache/poi/ss/usermodel/Sheet.html#setColumnWidth%28int,%20int%29 
	 * @return calculated width
	 */
	public int getCalculatedWidth() {
		return width * 256 + 3;
	}
}
