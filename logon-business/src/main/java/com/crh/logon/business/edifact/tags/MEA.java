/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.tags;

/**
 * @author AdNovum Informatik AG
 */
public class MEA extends Tag {
	
	private DataElementMeta measurementApplicationQualifier = new DataElementMeta(0, 0, true);
	private DataElementMeta measureUnitQualifier = new DataElementMeta(2, 0, false);
	private DataElementMeta measurementValue = new DataElementMeta(2, 1, false);
	private DataElementMeta[] dataElementMetas = { measurementApplicationQualifier, measureUnitQualifier, measurementValue };
	
	public MEA() {
		
		super("MEA", "Measurements");
	}

	public String getMeasurementApplicationQualifier() {
		return measurementApplicationQualifier.getValue();
	}

	public void setMeasurementApplicationQualifier(String measurementApplicationQualifier) {
		this.measurementApplicationQualifier.setValue(measurementApplicationQualifier);
	}

	public String getMeasureUnitQualifier() {
		return measureUnitQualifier.getValue();
	}

	public void setMeasureUnitQualifier(String measureUnitQualifier) {
		this.measureUnitQualifier.setValue(measureUnitQualifier);
	}

	public String getMeasurementValue() {
		return measurementValue.getValue();
	}

	public void setMeasurementValue(String measurementValue) {
		this.measurementValue.setValue(measurementValue);
	}

	@Override
	protected DataElementMeta[] getDataElementMetas() {
		return dataElementMetas;
	}
}
