/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout.delnote;

import java.math.BigDecimal;
import java.text.MessageFormat;

import com.crh.logon.business.services.pdftools.layout.JcanPdfRenderer;
import com.crh.logon.business.services.pdftools.layout.PdfLayoutGenerationTool;
import com.crh.logon.business.services.pdftools.layout.OrderItemDTO;
import com.crh.logon.constants.Constant;
import com.crh.logon.pdftool.labelprovider.TableLabelProvider;
import com.crh.logon.pdftool.objects.PdfObject;
import com.crh.logon.pdftool.objects.SimplePdfText;


/**
 * Label provider for order items. Gives back all the necessary information for the input order item. Check the {@link
 * TableLabelProvider#getTextObject(Object, int)} method for the concrete data-row binding.
 *
 * @author AdNovum Informatik AG
 */
public class DeliveryNoteOrderItemLabelProvider implements TableLabelProvider {

	private static final String INDEX_OUT_OF_BOUNDS_ERROR_MESSAGE = "Max index: 5, actual: {0}";

	//Order's general properties for PDF generation.
	private boolean hasReturn;

	private boolean isBag;

	private boolean isServiceOnly;

	private double widthOfFirstTwoColumns = -1;

	public DeliveryNoteOrderItemLabelProvider(Boolean hasReturn, Boolean isBag, Boolean isServiceOnly) {
		this(hasReturn, isBag, isServiceOnly, -1);
	}

	public DeliveryNoteOrderItemLabelProvider(Boolean hasReturn, Boolean isBag, Boolean isServiceOnly,
			double nameDescColumnWidth) {
		this.hasReturn = hasReturn;
		this.isBag = isBag;
		this.isServiceOnly = isServiceOnly;
		this.widthOfFirstTwoColumns = nameDescColumnWidth;
	}

	/* (non-Javadoc)
	 * @see TableLabelProvider#getTextObject(java.lang.Object, int)
	 */
	public PdfObject getTextObject(Object object, int column) {
		if (!(object instanceof OrderItemDTO)) {
			throw new UnsupportedOperationException(MessageFormat.format("Class of input object: {0}. Only {1} are acceptable",
					object.getClass().getName(), OrderItemDTO.class.getName()));
		}
		OrderItemDTO dto = (OrderItemDTO) object;
		SimplePdfText textObject;
		if (isBulkType(dto.getPkgType())) {
			textObject = getBulkRowValue(dto, column);
		}
		else if (Long.valueOf(Constant.PACKAGE_TYPE_BBAG).equals(dto.getPkgType())) {
			textObject = getBagRowValue(dto, column);
		}
		else if (Long.valueOf(Constant.PACKAGE_TYPE_SERVICE).equals(dto.getPkgType())) {
			textObject = getServiceRowValue(dto, column);
		}
		else {
			textObject = getOtherRowValue(dto, column);
		}

		if (column == 0 || column == 1) {
			return JcanPdfRenderer.cutTextToWidth(textObject, widthOfFirstTwoColumns);
		}
		else {
			return textObject;
		}
	}

	boolean isBulkType(Long pkgType) {
		return pkgType != null &&
				(Constant.PACKAGE_TYPE_BULK_DUMPED == pkgType ||
						Constant.PACKAGE_TYPE_BULK_POWDER == pkgType);
	}

	/**
	 * Label provider for "BAG" case
	 */
	private SimplePdfText getBagRowValue(OrderItemDTO dto, int column) {
		switch (column) {
			case 0:
				return new SimplePdfText(dto.getMaterialName(), (int) widthOfFirstTwoColumns, -1);
			case 1:
				return new SimplePdfText(dto.getMaterialDescription(), (int) widthOfFirstTwoColumns, -1);
			case 2:
				return getWeightValueOrDashAligned(dto.getOrdered(), dto.getOrderedUom());
			case 3:
				return getWeightValueOrDashAligned(dto.getDelivered(), dto.getDeliveredUom());
			case 4: {
				//Column 4 is not ambiguous in bag case as the return value is not shown.
				if (hasReturn && dto.getReturn() != null) {
					return getWeightValueOrDashAligned(dto.getReturn(), dto.getBulkUom());
				}
				else {
					return getBagRowValue(dto, column + 1);
				}
			}
			case 5:
				return getWeightValueOrDashAligned(dto.getNetBag(), dto.getNetBagUom());
			default:
				throw new IndexOutOfBoundsException(MessageFormat.format(INDEX_OUT_OF_BOUNDS_ERROR_MESSAGE, column));
		}
	}

	/**
	 * Label provider for "BULK" case
	 */
	private SimplePdfText getBulkRowValue(OrderItemDTO dto, int column) {
		switch (column) {
			case 0:
				return new SimplePdfText(dto.getMaterialName(), (int) widthOfFirstTwoColumns, -1);
			case 1:
				return new SimplePdfText(dto.getMaterialDescription(), (int) widthOfFirstTwoColumns, -1);
			case 2:
				return getWeightValueOrDashAligned(dto.getGrossWeight(), Constant.UOM_TONS_TXT);
			case 3:
				return getWeightValueOrDashAligned(dto.getTareWeight(), Constant.UOM_TONS_TXT);
			case 4: {
				//Column 4 is ambiguous in bulk case,
				//	if there is return in any item of the order, then column 4 means the returnQty and column 5 is the
				//	deliveredQty
				//  if there is no return in any item of the order, then column 4 means the deliveredQty and there is no column 5
				if (hasReturn) {
					//there is return in any item of the order
					if (dto.getReturn() != null) {
						//if there is returned amount in this item then display it
						return getWeightValueOrDashAligned(dto.getReturn(), dto.getBulkUom());
					}
					else {
						//otherwise add empty text
						return SimplePdfText.create("");
					}
				}
				else {
					//there is no return in any item of the order, then column 4 means the deliveredQty
					return getBulkRowValue(dto, column + 1);
				}
			}
			case 5:
				return getWeightValueOrDashAligned(dto.getNetWeight(), dto.getBulkUom());
			default:
				throw new IndexOutOfBoundsException(MessageFormat.format(INDEX_OUT_OF_BOUNDS_ERROR_MESSAGE, column));
		}
	}

	private SimplePdfText getServiceRowValue(OrderItemDTO dto, int column) {

		// if the order is a mixed bulk and service order, than the weight of the service is '-'
		BigDecimal net = null;
		if (isServiceOnly) {
			net = dto.getNetBag();
		}
		SimplePdfText dashOrNet = getWeightValueOrDashAligned(net, dto.getNetBagUom());

		switch (column) {
			case 0:
				return new SimplePdfText(dto.getMaterialName(), (int) widthOfFirstTwoColumns, -1);
			case 1:
				return new SimplePdfText(dto.getMaterialDescription(), (int) widthOfFirstTwoColumns, -1);
			case 2:
			case 3:
				return dashOrNet;
			case 4: {
				//Column 4 is ambiguous in service case, depending on bag or bulk

				if (!isServiceOnly) {
					//in bulk case see comment in getBulkRowValue()
					if (hasReturn) {
						//there is return in any item of the order
						if (dto.getReturn() != null) {
							//if there is returned amount in this item then display it
							return getWeightValueOrDashAligned(dto.getReturn(), dto.getBulkUom());
						}
						else {
							//otherwise add empty text
							return SimplePdfText.create("");
						}
					}
					else {
						//there is no return in any item of the order, then column 4 means the deliveredQty
						return getServiceRowValue(dto, column + 1);
					}
				}
				else {
					//Bag or service only case return is not shown on DN, column 4 is always the delivered amount
					return getServiceRowValue(dto, column + 1);
				}
			}
			case 5:
				return getWeightValueOrDashAligned(dto.getNetBag(), dto.getNetBagUom());
			default:
				throw new IndexOutOfBoundsException(MessageFormat.format(INDEX_OUT_OF_BOUNDS_ERROR_MESSAGE, column));
		}
	}

	private SimplePdfText getOtherRowValue(OrderItemDTO dto, int column) {
		switch (column) {
			case 0:
				return new SimplePdfText(dto.getMaterialName(), (int) widthOfFirstTwoColumns, -1);
			case 1:
				return new SimplePdfText(dto.getMaterialDescription(), (int) widthOfFirstTwoColumns, -1);
			case 2:
			case 3:
			case 5:
				return SimplePdfText.create("");
			case 4: {
				//Column 4 is ambiguous in others case, depending on bag or bulk
				if (isBag) {
					//Bag case return is not shown on DN, column 4 is always the delivered amount
					return getOtherRowValue(dto, column + 1);
				}
				else {
					//in bulk case see comment in getBulkRowValue()
					if (hasReturn) {
						//there is return in any item of the order
						if (dto.getReturn() != null) {
							//if there is returned amount in this item then display it
							return PdfLayoutGenerationTool
									.getRightAlignedText(OrderItemDTO.getValueOrEmptyString(dto.getReturn(), dto.getBulkUom()));
						}
					}
					else {
						//there is no return in any item of the order, then column 4 means the deliveredQty
						getOtherRowValue(dto, column + 1);
					}

					return SimplePdfText.create("");
				}
			}
			default:
				throw new IndexOutOfBoundsException(MessageFormat.format(INDEX_OUT_OF_BOUNDS_ERROR_MESSAGE, column));
		}
	}

	/**
	 * This method will return a right aligned value with the proper Unit of Measure, OR <br> a middle aligned dash '-' without
	 * UoM if the value parameter is null.
	 */
	private SimplePdfText getWeightValueOrDashAligned(BigDecimal value, String UoM) {

		String str = OrderItemDTO.getValueOrEmptyStringWeight(value, UoM);
		if (value == null) {
			return PdfLayoutGenerationTool.centerAlignText(SimplePdfText.create(str));
		}
		else {
			return PdfLayoutGenerationTool.rightAlignText(SimplePdfText.create(str));
		}
	}

}
