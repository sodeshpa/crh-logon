/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.ObjectUtils;

/**
 * @author AdNovum Informatik AG
 */
public class UserAdminSrchBean implements Serializable {

    private Long userId;
    private Long sapBpId;
    private String customer;
    private Long sapVendorId;
    private String carrier;
    private Long personId;
    private String firstname;
    private String name;
    private String compositeName;
    private String ldapHolspace;
    private String extLogin;
    private Long pdaUserNo;
    private Boolean activeFlag;
    private String roleNames;
    private Date certifiedFromDat;
    private Date certifiedToDat;
    private boolean readOnly;
    
	/**
	 * return the hasCode for this object
	 */
    @Override
	public int hashCode() {
		
		if (this.getUserId() == null) {
			return 0;
		}
		return this.getUserId().intValue();
	}
	
	/**
	 * Returns <code>true</code> if and only if the argument is not 
     * <code>null</code> and all properties are identical.
     *  
	 */
    @Override
	public boolean equals(Object obj) {

		if (obj instanceof UserAdminSrchBean) {
			boolean equals = true;
			
			UserAdminSrchBean bean = (UserAdminSrchBean) obj;

			equals &= ObjectUtils.equals(bean.getActiveFlag(), this.getActiveFlag());
			equals &= ObjectUtils.equals(bean.getCarrier(), this.getCarrier());
			equals &= ObjectUtils.equals(bean.getCompositeName(), this.getCompositeName());
			equals &= ObjectUtils.equals(bean.getCustomer(), this.getCustomer());
			equals &= ObjectUtils.equals(bean.getExtLogin(), this.getExtLogin());
			equals &= ObjectUtils.equals(bean.getFirstname(), this.getFirstname());
			equals &= ObjectUtils.equals(bean.getLdapHolspace(), this.getLdapHolspace());
			equals &= ObjectUtils.equals(bean.getName(), this.getName());
			equals &= ObjectUtils.equals(bean.getPdaUserNo(), this.getPdaUserNo());
			equals &= ObjectUtils.equals(bean.getPersonId(), this.getPersonId());
			equals &= ObjectUtils.equals(bean.getRoleNames(), this.getRoleNames());
			equals &= ObjectUtils.equals(bean.getSapBpId(), this.getSapBpId());
			equals &= ObjectUtils.equals(bean.getSapVendorId(), this.getSapVendorId());
			equals &= ObjectUtils.equals(bean.getUserId(), this.getUserId());
			
			return equals;
		}
		return false;

	}
       
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public Long getSapBpId() {
		return sapBpId;
	}
	
	public void setSapBpId(Long sapBpId) {
		this.sapBpId = sapBpId;
	}
	
	public String getCustomer() {
		return customer;
	}
	
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	
	public Long getSapVendorId() {
		return sapVendorId;
	}
	
	public void setSapVendorId(Long sapVendorId) {
		this.sapVendorId = sapVendorId;
	}
	
	public String getCarrier() {
		return carrier;
	}
	
	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}
	
	public Long getPersonId() {
		return personId;
	}
	
	public void setPersonId(Long personId) {
		this.personId = personId;
	}
	
	public String getFirstname() {
		return firstname;
	}
	
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCompositeName() {
		return compositeName;
	}
	
	public void setCompositeName(String compositeName) {
		this.compositeName = compositeName;
	}
	
	public String getLdapHolspace() {
		return ldapHolspace;
	}
	
	public void setLdapHolspace(String ldapHolspace) {
		this.ldapHolspace = ldapHolspace;
	}
	
	public String getExtLogin() {
		return extLogin;
	}
	
	public void setExtLogin(String extLogin) {
		this.extLogin = extLogin;
	}
	
	public Long getPdaUserNo() {
		return pdaUserNo;
	}
	
	public void setPdaUserNo(Long pdaUserNo) {
		this.pdaUserNo = pdaUserNo;
	}
	
	public Boolean getActiveFlag() {
		return activeFlag;
	}
	
	public void setActiveFlag(Boolean activeFlag) {
		this.activeFlag = activeFlag;
	}
	
	public String getRoleNames() {
		return roleNames;
	}
	
	public void setRoleNames(String roleNames) {
		this.roleNames = roleNames;
	}
	
	public boolean isReadOnly() {
		return readOnly;
	}
	
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public Date getCertifiedFromDat() {
		return certifiedFromDat;
	}

	public void setCertifiedFromDat(Date certifiedFromDat) {
		this.certifiedFromDat = certifiedFromDat;
	}

	public Date getCertifiedToDat() {
		return certifiedToDat;
	}

	public void setCertifiedToDat(Date certifiedToDat) {
		this.certifiedToDat = certifiedToDat;
	}
    
}
