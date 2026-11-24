/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout.colldelnote;

import java.math.BigDecimal;
import java.text.MessageFormat;

import com.crh.logon.business.services.pdftools.layout.PdfLayoutGenerationTool;
import com.crh.logon.business.services.pdftools.layout.OrderItemDTO;
import com.crh.logon.pdftool.labelprovider.TableLabelProvider;
import com.crh.logon.pdftool.objects.PdfObject;
import com.crh.logon.pdftool.objects.SimplePdfText;
import com.crh.logon.utils.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
public class CollDelNoteOrderTableLabelProvider implements TableLabelProvider {
	
	/**
	 * Format of date for the collective delivery note
	 */
	public static String COLL_DEL_NOTE_DATE_FORMAT = "HH:mm  dd.MM.yy";

	/**
	 * @see TableLabelProvider#getTextObject(java.lang.Object, int)
	 */
	public PdfObject getTextObject(Object object, int column) {
		if (!(object instanceof OrderItemDTO)) {
			throw new UnsupportedOperationException(MessageFormat.format("Only instances of {0} are accepted.", OrderItemDTO.class.getName()));
		}
		OrderItemDTO dto = (OrderItemDTO) object;
		switch (column) {
		case 0:
			return SimplePdfText.create(StringUtils.cutStringWithDots(dto.getOrderNr(), 11));
		case 1:
			return SimplePdfText.create(StringUtils.cutStringWithDots(dto.getSapNr(), 12));
		case 2:
			return SimplePdfText.create(StringUtils.cutStringWithDots(dto.getMaterialName(), 25));
		case 3:
			return SimplePdfText.create(StringUtils.cutStringWithDots(dto.getTruckNr(), 10));
		case 4:
			return SimplePdfText.create(StringUtils.nvl(dto.getLoadingTime()));
		case 5:
			return SimplePdfText.create(StringUtils.nvl(dto.getDeliveryTime()));
		case 6:
			BigDecimal weight = OrderItemDTO.getPkgDependantWeight(dto);
			final String uomTxt = StringUtils.cutStringWithDots(OrderItemDTO.getPkgDependantUom(dto), 9);
			final String netAmountStr;
			if (weight != null) {
				netAmountStr = PdfLayoutGenerationTool.WEIGHT_FORMAT.format(weight) + " " + uomTxt;
			}
			else {
				netAmountStr = "";
			}
			return PdfLayoutGenerationTool.getRightAlignedText(netAmountStr);
		default:
			// !!! Update the max index below if the number of column changes!
			throw new IndexOutOfBoundsException(MessageFormat.format("Max index: 6, actual: {0}", column));
		}
	}

}
