/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.admin.pages;

import org.junit.Ignore;

import com.crh.logon.admin.web.pages.PropertyKey;

/**
 * @author AdNovum Informatik AG
 */
// TODO 11-Nov-2014/nhattan: move to TestProperties.java class
@Ignore
public abstract class UserAdminTestProperties {
	public enum USER_ADMIN_KEY implements PropertyKey{
		FIRSTNAME("firstname"),
		SURNAME("surname"),
		EXT_LOGIN_NAME("extLoginName"),
		HOLSPACE("holspace"),
		EMAIL("email"),
		MOBILE_NR("mobilenr"),
		PHONE_NR("phonenr"),
		PWD("pwd"),
		CARRIER_ID("carrierId"),
		SHIPPING_POINT_ID("shippingPointId"),
		SP_LABEL("spLabel"),
		CARRIER_LABEL("carrierLabel"),
		PLANT_CD("plantCd"),
		POOL_ID("poolId"),
		POOL_LABEL("poolLabel"),
		COMPANY_CODE("companyCode"),
		CUSTOMER_SAP_ID("customerSapId"),
		CUSTOMER_ASSOCIATE_NAME("customerAssociateName"),
		SAP_VENDOR_ID("sapVendorId"),
		CARRIER_ASSOCIATE_ID("carrierAssociateId"),
		CARRIER_ASSOCIATE_NAME("carrierAssociateName"),
		CRH_USER_TYPE_LABEL("crhUserTypeLabel"),
		CUSTOMER_USER_TYPE_LABEL("customerUserTypeLabel"),
		VENDO_USER_TYPE_LABEL("vendoUserTypeLabel");
		
		String keyString;
		
		USER_ADMIN_KEY(String keyString) {
			this.keyString = keyString;
		}

		public String getKeyString() {
			return this.keyString;
		}
	}
}
