/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans.pdf.deliverynote;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.crh.logon.persistence.dto.BlobDTO;
import com.crh.logon.persistence.entities.common.SareaEntity;

/**
 * @author AdNovum Informatik AG
 */
public class RemarkData implements Serializable {
	private static final long serialVersionUID = -7898887453922614695L;

	private boolean showDispatcherDriver;
	
	/**
	 * Flag from {@link SareaEntity#getDnShowFixText()} to indicate if the fix remark should be displayed.
	 */
	private boolean showFixTextInRemark;
	
	/**
	 * Sets the "show fix comment" flag.
	 * @param showFixTextInRemark the show fix Comment flag 
	 */
	public void setShowFixTextInRemark(boolean showFixTextInRemark) {
		this.showFixTextInRemark = showFixTextInRemark;
	}
	
	private String comment;
	private String lnfAddress;
	
	/**
	 * The fix remark text.
	 */
	private String fixRemark;
	
	private String customerName;
	
	/**
	 * person.getFirstname()) + " " + StringUtils.defaultString(person.getName()
	 */
	private String driverFullName;
	
	private String dispatcherName;
	private String dispatcherId;
	private String dispatcherTel;
	
	private String driverCardNo;
	
	/**
	 * Driver name from the Assignment (LDS Driver Name)
	 */
	private String driverName;
	private String driverId;
	
	private BlobDTO customerSignature;
	private BlobDTO driverSignature;
	
	public boolean renderDriverFullName() {
		return StringUtils.isNotEmpty(driverFullName);
	}
	
	public boolean renderCustomerName() {
		return StringUtils.isNotEmpty(customerName);
	}
	
	public boolean renderDriverDispatcherInfo() {
		return showDispatcherDriver && 
				(renderDispatcherTel() || renderDispatcherId() ||
				renderDispatcherName() || renderDriverId() ||
				renderDriverName() || renderDriverCardNo());
	}
	
	public boolean renderDriverName() {
		return StringUtils.isNotEmpty(driverName) && showDispatcherDriver;
	}
	public boolean renderDriverCardNo() {
		return StringUtils.isNotEmpty(driverCardNo) && showDispatcherDriver;
	}
	public boolean renderDriverId() {
		return StringUtils.isNotEmpty(driverId) && showDispatcherDriver;
	}
	public boolean renderDispatcherId() {
		return StringUtils.isNotEmpty(dispatcherId) && showDispatcherDriver;
	}
	public boolean renderDispatcherName() {
		return StringUtils.isNotEmpty(dispatcherName) && showDispatcherDriver;
	}
	public boolean renderDispatcherTel() {
		return StringUtils.isNotEmpty(dispatcherTel) && showDispatcherDriver;
	}
	public boolean renderCustomerSignature() {
		return customerSignature != null && customerSignature.getStream() != null;
	}
	public boolean renderDriverSignature() {
		return driverSignature != null && driverSignature.getStream() != null &&
				!renderDriverDispatcherInfo();
	}
	
	/**
	 * Returns true, if the fix remark should be displayed and is not null or empty.
	 * @return true, if the fix remark should be displayed and is not null or empty.
	 */
	public boolean renderFixRemark() {
		return this.showFixTextInRemark && StringUtils.isNotEmpty(fixRemark);
	}
	
	public boolean renderLnfAddress() {
		return StringUtils.isNotBlank(lnfAddress);
	}
	
	/**
	 * Returns true, if the comment can be rendered (is not null, or empty)
	 * @return true, if the comment can be rendered (is not null, or empty)
	 */
	public boolean renderComment() {
		return StringUtils.isNotEmpty(comment);
	}
	
	/**
	 * Returns the comment text. Could be null.
	 * @return the comment text. Could be null.
	 */
	public String getComment() {
		return comment;
	}
	
	/**
	 * Sets the comment text.
	 * @param comment the comment text.
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	/**
	 * Returns the fix remark. Could be null.
	 * @return the fix remark.
	 */
	public String getFixRemark() {
		return fixRemark;
	}
	
	/**
	 * Sets the fix remark. If the fix remark is null or empty, it does not displayed.
	 * @param fixRemark the Fix remark text
	 */
	public void setFixRemark(String fixRemark) {
		this.fixRemark = fixRemark;
	}
	public String getDispatcherName() {
		return dispatcherName;
	}
	public void setDispatcherName(String dispatcherName) {
		this.dispatcherName = dispatcherName;
	}
	public String getDispatcherId() {
		return dispatcherId;
	}
	public void setDispatcherId(String dispatcherId) {
		this.dispatcherId = dispatcherId;
	}
	public String getDispatcherTel() {
		return dispatcherTel;
	}
	public void setDispatcherTel(String dispatcherTel) {
		this.dispatcherTel = dispatcherTel;
	}
	public String getDriverCardNo() {
		return driverCardNo;
	}
	public void setDriverCardNo(String driverCardNo) {
		this.driverCardNo = driverCardNo;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getDriverId() {
		return driverId;
	}
	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}
	public String getLnfAddress() {
		return lnfAddress;
	}
	public void setLnfAddress(String lnfAddress) {
		this.lnfAddress = lnfAddress;
	}
	public boolean isShowDispatcherDriver() {
		return showDispatcherDriver;
	}
	public void setShowDispatcherDriver(boolean showDispatcherDriver) {
		this.showDispatcherDriver = showDispatcherDriver;
	}
	public BlobDTO getCustomerSignature() {
		return customerSignature;
	}
	public void setCustomerSignature(BlobDTO customerSignature) {
		this.customerSignature = customerSignature;
	}
	public BlobDTO getDriverSignature() {
		return driverSignature;
	}
	public void setDriverSignature(BlobDTO driverSignature) {
		this.driverSignature = driverSignature;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getDriverFullName() {
		return driverFullName;
	}
	public void setDriverFullName(String driverFullName) {
		this.driverFullName = driverFullName;
	}
}
