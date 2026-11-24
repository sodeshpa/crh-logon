/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout.colldelnote;

import java.text.MessageFormat;

import com.crh.logon.business.services.pdftools.layout.PdfLayoutGenerationTool;
import com.crh.logon.pdftool.labelprovider.TableLabelProvider;
import com.crh.logon.pdftool.objects.PdfObject;
import com.crh.logon.pdftool.objects.SimplePdfText;
import com.crh.logon.utils.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
public class CollDelNoteTotalsTableLabelProvider implements TableLabelProvider {
	
	
	/**
	 * The word "TOTAL" on the desired language 
	 */
	private final String totalString;
	
	/**
	 * Constructor
	 * @param totalString The word "TOTAL" on the desired language 
	 */
	public CollDelNoteTotalsTableLabelProvider(String totalString) {
		this.totalString = totalString;
	}
	
	/**
	 * @see TableLabelProvider#getTextObject(java.lang.Object, int)
	 */
	public PdfObject getTextObject(Object object, int column) {
		// special labels for tables
		if (object instanceof LabelDTO) {
			LabelDTO dto = (LabelDTO)object;
			switch (column) {
			case 0:
				return new SimplePdfText(dto.getColumnValue(Integer.valueOf(column)), 60, 12).setFont(dto.getFont());
			case 1:
				return new SimplePdfText(dto.getColumnValue(Integer.valueOf(column)), 65, 12).setFont(dto.getFont());
			case 2:
				return new SimplePdfText(dto.getColumnValue(Integer.valueOf(column)), 322, 12).setFont(dto.getFont());
			case 3:
				/* LOGON-5512: Originally there were 5 columns (0 - 4), where column 3 was the
				 * right-aligned quantity and column 4 was the UOM.
				 * To fix the ticket, I have merged columns 3 and 4. The fact that the LabelDTO
				 * contains still values for columns 3 as well as 4 is a historical artifact.
				 */
				final String columnStr;
				if (dto.getColumnValue(Integer.valueOf(column)).equals("")) {
					columnStr = "";
				}
				else {
					// Concatenate label values for columns 3 and 4, with a space in between
					columnStr = dto.getColumnValue(Integer.valueOf(column)) + " " +
							dto.getColumnValue(Integer.valueOf(column + 1));
				}
				SimplePdfText weight = new SimplePdfText(columnStr, 80, 12).setFont(dto.getFont());
				PdfLayoutGenerationTool.rightAlignText(weight);
				return weight;
			default:
				throw new IndexOutOfBoundsException(MessageFormat.format("Max index: 3, actual: {0}", column));
			}
		} else if (!(object instanceof OrderItemTotalDTO)) {
			throw new UnsupportedOperationException(MessageFormat.format("Only instances of {0} are accepted.", OrderItemTotalDTO.class.getName()));
		}
		OrderItemTotalDTO dto = (OrderItemTotalDTO) object;
		switch (column) {
		case 0:
			return new SimplePdfText(totalString, 60, 12);
		case 1:
			return new SimplePdfText(dto.getSapNr(), 65, 12);
		case 2:
			return new SimplePdfText(dto.getMaterialName(), 322, 12);
		case 3:
			// LOGON-5512: Concatenate net weight and UOM into single column
			final String columnStr = PdfLayoutGenerationTool.WEIGHT_FORMAT.format(dto.getNetWeight()) + " " +
					StringUtils.cutStringWithDots(dto.getUom(), 9);
			SimplePdfText weight = new SimplePdfText(columnStr, 80, 12);
			PdfLayoutGenerationTool.rightAlignText(weight);
			return weight;
		default:
			throw new IndexOutOfBoundsException(MessageFormat.format("Max index: 3, actual: {0}", column));
		}
	}

}
