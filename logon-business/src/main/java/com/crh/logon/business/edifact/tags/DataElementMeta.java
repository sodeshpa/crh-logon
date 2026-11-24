/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.tags;

import com.crh.logon.business.edifact.parser.DataElement;
import com.crh.logon.business.edifact.parser.Segment;

/**
 * @author AdNovum Informatik AG
 */
public class DataElementMeta {

	private int dataElementIndex;
	private int valueIndex;
	private boolean mandatory;
	private String value; 
	
	public DataElementMeta(int dataElementIndex, int valueIndex, boolean mandatory) {
		
		this.dataElementIndex = dataElementIndex;
		this.valueIndex = valueIndex;
		this.mandatory = mandatory;
	}
	
	public int getDataElementIndex() {
		return dataElementIndex;
	}
	
	public void setDataElementIndex(int dataElementIndex) {
		this.dataElementIndex = dataElementIndex;
	}
	
	public int getValueIndex() {
		return valueIndex;
	}
	
	public void setValueIndex(int valueIndex) {
		this.valueIndex = valueIndex;
	}
	
	public boolean isMandatory() {
		return mandatory;
	}
	
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public void parse(Segment segment) {
		
		DataElement de = segment.getDataElement(dataElementIndex);
		if (de != null) {
			setValue(de.getValue(valueIndex));
		}
	}
	
	public void applyValue(Segment segment) {
		
		if (value != null) {
			DataElement de = segment.getDataElement(dataElementIndex);
			if (de == null) {
				de = new DataElement();
				segment.setDataElement(dataElementIndex, de);
			}
			de.setValue(valueIndex, value);		
		}
	}
}
