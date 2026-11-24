package com.crh.logon.business.services.utils;

import com.crh.logon.persistence.entities.common.CustorderEntity;

public class OrderTakingHelperUtil {

	public static MaterialType getMaterialType(CustorderEntity custorder) {
		MaterialType materialType = null;
		if (LogonPackageHandlingUtil.isBag(custorder)) {
			materialType = MaterialType.BAG;
		}
		else if (LogonPackageHandlingUtil.isBulk(custorder)) {
			materialType = MaterialType.BULK;
		}
		else if (LogonPackageHandlingUtil.hasService(custorder)) {
			materialType = MaterialType.SERVICE;
		}
		return materialType;
	}
}
