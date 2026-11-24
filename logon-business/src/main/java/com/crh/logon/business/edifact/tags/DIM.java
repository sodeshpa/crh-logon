/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.tags;

/**
 * @author AdNovum Informatik AG
 */
public class DIM extends Tag {

	private DataElementMeta dimensionQualifier = new DataElementMeta(0, 0, true);
	private DataElementMeta measureUnitQualifier = new DataElementMeta(1, 0, true);
	private DataElementMeta lengthDimension = new DataElementMeta(1, 1, false);
	private DataElementMeta widthDimension = new DataElementMeta(1, 2, false);
	private DataElementMeta heightDimension = new DataElementMeta(1, 3, false);
	private DataElementMeta[] dataElementMetas = { dimensionQualifier, measureUnitQualifier, lengthDimension, widthDimension, heightDimension };
	
	public DIM() {
		
		super("DIM", "Dimensions");
	}
	
	public String getDimensionQualifier() {
		return dimensionQualifier.getValue();
	}
	
	public void setDimensionQualifier(String dimensionQualifier) {
		this.dimensionQualifier.setValue(dimensionQualifier);
	}
	
	public String getMeasureUnitQualifier() {
		return measureUnitQualifier.getValue();
	}
	
	public void setMeasureUnitQualifier(String measureUnitQualifier) {
		this.measureUnitQualifier.setValue(measureUnitQualifier);
	}
	
	public String getLengthDimension() {
		return lengthDimension.getValue();
	}
	
	public void setLengthDimension(String lengthDimension) {
		this.lengthDimension.setValue(lengthDimension);
	}
	
	public String getWidthDimension() {
		return widthDimension.getValue();
	}
	
	public void setWidthDimension(String widthDimension) {
		this.widthDimension.setValue(widthDimension);
	}
	
	public String getHeightDimension() {
		return heightDimension.getValue();
	}
	
	public void setHeightDimension(String heightDimension) {
		this.heightDimension.setValue(heightDimension);
	}
	
	@Override
	protected DataElementMeta[] getDataElementMetas() {
		return dataElementMetas;
	}	
}
