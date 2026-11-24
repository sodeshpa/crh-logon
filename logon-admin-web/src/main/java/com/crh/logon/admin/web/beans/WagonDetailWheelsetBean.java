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
public class WagonDetailWheelsetBean implements Serializable {

	private static final long serialVersionUID = 1L;
    private String wheelset1NbsNo;
    private String wheelset1NbsBuild;
    private String wheelset1NbsDiameterMm;
    private String wheelset1NbsYear;
    private String wheelset2NbsNo;
    private String wheelset2NbsBuild;
    private String wheelset2NbsDiameterMm;
    private String wheelset2NbsYear;
    private String wheelset3BsNo;
    private String wheelset3BsBuild;
    private String wheelset3BsDiameterMm;
    private String wheelset3BsYear;
    private String wheelset4BsNo;
    private String wheelset4BsBuild;
    private String wheelset4BsDiameterMm;
    private String wheelset4BsYear;
    
    @ResourceKey("wagon.wheelset_no")
	@TypeValidation(java.lang.Long.class)
	public String getWheelset1NbsNo() {
		return wheelset1NbsNo;
	}
	public void setWheelset1NbsNo(String wheelset1NbsNo) {
		this.wheelset1NbsNo = wheelset1NbsNo;
	}
	public String getWheelset1NbsBuild() {
		return wheelset1NbsBuild;
	}
	public void setWheelset1NbsBuild(String wheelset1NbsBuild) {
		this.wheelset1NbsBuild = wheelset1NbsBuild;
	}
	
	@ResourceKey("wagon.wheelset_diameter")
	@TypeValidation(java.lang.Long.class)
	public String getWheelset1NbsDiameterMm() {
		return wheelset1NbsDiameterMm;
	}
	public void setWheelset1NbsDiameterMm(String wheelset1NbsDiameterMm) {
		this.wheelset1NbsDiameterMm = wheelset1NbsDiameterMm;
	}
	
	@ResourceKey("wagon.wheelset_year")
	@TypeValidation(java.math.BigDecimal.class)
	public String getWheelset1NbsYear() {
		return wheelset1NbsYear;
	}
	public void setWheelset1NbsYear(String wheelset1NbsYear) {
		this.wheelset1NbsYear = wheelset1NbsYear;
	}
	
	@ResourceKey("wagon.wheelset_no")
	@TypeValidation(java.lang.Long.class)
	public String getWheelset2NbsNo() {
		return wheelset2NbsNo;
	}
	public void setWheelset2NbsNo(String wheelset2NbsNo) {
		this.wheelset2NbsNo = wheelset2NbsNo;
	}
	public String getWheelset2NbsBuild() {
		return wheelset2NbsBuild;
	}
	public void setWheelset2NbsBuild(String wheelset2NbsBuild) {
		this.wheelset2NbsBuild = wheelset2NbsBuild;
	}
	
	@ResourceKey("wagon.wheelset_diameter")
	@TypeValidation(java.lang.Long.class)
	public String getWheelset2NbsDiameterMm() {
		return wheelset2NbsDiameterMm;
	}
	public void setWheelset2NbsDiameterMm(String wheelset2NbsDiameterMm) {
		this.wheelset2NbsDiameterMm = wheelset2NbsDiameterMm;
	}
	
	@ResourceKey("wagon.wheelset_year")
	@TypeValidation(java.math.BigDecimal.class)
	public String getWheelset2NbsYear() {
		return wheelset2NbsYear;
	}
	public void setWheelset2NbsYear(String wheelset2NbsYear) {
		this.wheelset2NbsYear = wheelset2NbsYear;
	}
	public String getWheelset3BsNo() {
		return wheelset3BsNo;
	}
	
	@ResourceKey("wagon.wheelset_no")
	@TypeValidation(java.lang.Long.class)
	public void setWheelset3BsNo(String wheelset3BsNo) {
		this.wheelset3BsNo = wheelset3BsNo;
	}
	public String getWheelset3BsBuild() {
		return wheelset3BsBuild;
	}
	public void setWheelset3BsBuild(String wheelset3BsBuild) {
		this.wheelset3BsBuild = wheelset3BsBuild;
	}
	
	@ResourceKey("wagon.wheelset_diameter")
	@TypeValidation(java.lang.Long.class)
	public String getWheelset3BsDiameterMm() {
		return wheelset3BsDiameterMm;
	}
	public void setWheelset3BsDiameterMm(String wheelset3BsDiameterMm) {
		this.wheelset3BsDiameterMm = wheelset3BsDiameterMm;
	}
	
	@ResourceKey("wagon.wheelset_year")
	@TypeValidation(java.math.BigDecimal.class)
	public String getWheelset3BsYear() {
		return wheelset3BsYear;
	}
	public void setWheelset3BsYear(String wheelset3BsYear) {
		this.wheelset3BsYear = wheelset3BsYear;
	}
	
	@ResourceKey("wagon.wheelset_no")
	@TypeValidation(java.lang.Long.class)
	public String getWheelset4BsNo() {
		return wheelset4BsNo;
	}
	public void setWheelset4BsNo(String wheelset4BsNo) {
		this.wheelset4BsNo = wheelset4BsNo;
	}
	public String getWheelset4BsBuild() {
		return wheelset4BsBuild;
	}
	public void setWheelset4BsBuild(String wheelset4BsBuild) {
		this.wheelset4BsBuild = wheelset4BsBuild;
	}
	
	@ResourceKey("wagon.wheelset_diameter")
	@TypeValidation(java.lang.Long.class)
	public String getWheelset4BsDiameterMm() {
		return wheelset4BsDiameterMm;
	}
	public void setWheelset4BsDiameterMm(String wheelset4BsDiameterMm) {
		this.wheelset4BsDiameterMm = wheelset4BsDiameterMm;
	}
	
	@ResourceKey("wagon.wheelset_year")
	@TypeValidation(java.math.BigDecimal.class)
	public String getWheelset4BsYear() {
		return wheelset4BsYear;
	}
	public void setWheelset4BsYear(String wheelset4BsYear) {
		this.wheelset4BsYear = wheelset4BsYear;
	}
    
    
}
