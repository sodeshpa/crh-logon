/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.beans;

import java.io.Serializable;

import com.crh.logon.web.utils.ResourceKey;
import com.crh.logon.web.validators.TypeValidation;

/**
 * @author AdNovum Informatik AG
 */
public class WagonDetailBuildBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String brakeType;
    private String brakeBlocksType;
    private String brakeBlocksCount;
    private String brakeCylinderNo;
    private String brakeRiggingNo;
    private String brakeHosepipeNo;
    private String brakeHosepipeNbs;
    private String brakeHosepipeBs;
    private String mechanicalLoadChange;
    private String airShutOffCockType;
    private String tripleValveNo;
    private String tripleValveType;
    private String weighingValveNo;
    private String weighingValveType;
    private String pressureExchangerType;
    private String pressureExchangerNo;
    private String bufferType;
    private String bufferConstYear;
    private String bogieType;
    private String bogieNo;
    private String springType;
    
	public String getBrakeType() {
		return brakeType;
	}
	public void setBrakeType(String brakeType) {
		this.brakeType = brakeType;
	}
	public String getBrakeBlocksType() {
		return brakeBlocksType;
	}
	public void setBrakeBlocksType(String brakeBlocksType) {
		this.brakeBlocksType = brakeBlocksType;
	}
	@ResourceKey("wagon.brake_blocks_count")
	@TypeValidation(java.lang.Long.class)	
	public String getBrakeBlocksCount() {
		return brakeBlocksCount;
	}
	public void setBrakeBlocksCount(String brakeBlocksCount) {
		this.brakeBlocksCount = brakeBlocksCount;
	}
	
	public String getBrakeCylinderNo() {
		return brakeCylinderNo;
	}
	public void setBrakeCylinderNo(String brakeCylinderNo) {
		this.brakeCylinderNo = brakeCylinderNo;
	}
	public String getBrakeRiggingNo() {
		return brakeRiggingNo;
	}
	public void setBrakeRiggingNo(String brakeRiggingNo) {
		this.brakeRiggingNo = brakeRiggingNo;
	}
	public String getBrakeHosepipeNo() {
		return brakeHosepipeNo;
	}
	public void setBrakeHosepipeNo(String brakeHosepipeNo) {
		this.brakeHosepipeNo = brakeHosepipeNo;
	}
	public String getBrakeHosepipeNbs() {
		return brakeHosepipeNbs;
	}
	public void setBrakeHosepipeNbs(String brakeHosepipeNbs) {
		this.brakeHosepipeNbs = brakeHosepipeNbs;
	}
	public String getBrakeHosepipeBs() {
		return brakeHosepipeBs;
	}
	public void setBrakeHosepipeBs(String brakeHosepipeBs) {
		this.brakeHosepipeBs = brakeHosepipeBs;
	}
	public String getMechanicalLoadChange() {
		return mechanicalLoadChange;
	}
	public void setMechanicalLoadChange(String mechanicalLoadChange) {
		this.mechanicalLoadChange = mechanicalLoadChange;
	}
	public String getAirShutOffCockType() {
		return airShutOffCockType;
	}
	public void setAirShutOffCockType(String airShutOffCockType) {
		this.airShutOffCockType = airShutOffCockType;
	}
	public String getTripleValveNo() {
		return tripleValveNo;
	}
	public void setTripleValveNo(String tripleValveNo) {
		this.tripleValveNo = tripleValveNo;
	}
	public String getTripleValveType() {
		return tripleValveType;
	}
	public void setTripleValveType(String tripleValveType) {
		this.tripleValveType = tripleValveType;
	}
	public String getWeighingValveNo() {
		return weighingValveNo;
	}
	public void setWeighingValveNo(String weighingValveNo) {
		this.weighingValveNo = weighingValveNo;
	}
	public String getWeighingValveType() {
		return weighingValveType;
	}
	public void setWeighingValveType(String weighingValveType) {
		this.weighingValveType = weighingValveType;
	}
	public String getPressureExchangerType() {
		return pressureExchangerType;
	}
	public void setPressureExchangerType(String pressureExchangerType) {
		this.pressureExchangerType = pressureExchangerType;
	}
	public String getPressureExchangerNo() {
		return pressureExchangerNo;
	}
	public void setPressureExchangerNo(String pressureExchangerNo) {
		this.pressureExchangerNo = pressureExchangerNo;
	}
	public String getBufferType() {
		return bufferType;
	}
	public void setBufferType(String bufferType) {
		this.bufferType = bufferType;
	}
	
	@TypeValidation(java.lang.Long.class)
	public String getBufferConstYear() {
		return bufferConstYear;
	}
	public void setBufferConstYear(String bufferConstYear) {
		this.bufferConstYear = bufferConstYear;
	}
	public String getBogieType() {
		return bogieType;
	}
	public void setBogieType(String bogieType) {
		this.bogieType = bogieType;
	}
	public String getBogieNo() {
		return bogieNo;
	}
	public void setBogieNo(String bogieNo) {
		this.bogieNo = bogieNo;
	}
	public String getSpringType() {
		return springType;
	}
	public void setSpringType(String springType) {
		this.springType = springType;
	}
	/**
	 * Deletes the properties, which must be empty for a new wagon.
	 */
	public void resetNewWagonProperties(){
		setBrakeRiggingNo("");
		setBrakeCylinderNo("");
		setBrakeHosepipeNo("");
		setBrakeHosepipeBs("");
		setBrakeHosepipeNbs("");
		setTripleValveNo("");
		setWeighingValveNo("");
		setPressureExchangerNo("");
		setBogieNo("");
	}
}
