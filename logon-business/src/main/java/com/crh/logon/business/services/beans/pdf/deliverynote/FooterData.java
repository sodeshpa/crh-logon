/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans.pdf.deliverynote;

import java.io.Serializable;
import java.util.List;

/**
 * @author AdNovum Informatik AG
 */
public class FooterData implements Serializable {
	private static final long serialVersionUID = 3177617214530580304L;

	private RemarkData remarkData;
	private String footerTitle;
	private String footerText;
	
	private String calibrationText;
	private List<CalibrationData> calibrationDatas;
	
	public boolean renderCalibrationData() {
		return calibrationDatas != null && calibrationDatas.size() > 0;
	}

	public String getFooterTitle() {
		return footerTitle;
	}

	public void setFooterTitle(String footerTitle) {
		this.footerTitle = footerTitle;
	}

	public String getFooterText() {
		return footerText;
	}

	public void setFooterText(String footerText) {
		this.footerText = footerText;
	}

	public RemarkData getRemarkData() {
		return remarkData;
	}

	public void setRemarkData(RemarkData remarkData) {
		this.remarkData = remarkData;
	}

	public String getCalibrationText() {
		return calibrationText;
	}

	public void setCalibrationText(String calibrationText) {
		this.calibrationText = calibrationText;
	}

	public List<CalibrationData> getCalibrationDatas() {
		return calibrationDatas;
	}

	public void setCalibrationDatas(List<CalibrationData> calibrationDatas) {
		this.calibrationDatas = calibrationDatas;
	}
}
