/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.workflow.bimodal;

import com.crh.logon.admin.web.pages.PropertyKey;
import org.junit.Ignore;

/**
 * @author AdNovum Informatik AG
 */
@Ignore
public abstract class BimodalWorkflowTestProperties {
	public enum BIMODAL_WORKFLOW_KEY implements PropertyKey {
		SHIPPING_POINT_SAP_NO("shippingPointSapNo"),
		SHIPPING_POINT_NAME("shippingPointName"),
		CUST_ORDER_NO("custOrderNr"),
		RAIL_PART_ORDER_NO("railPartOrderNr"),
		ROAD_PART_ORDER_NO("roadPartOrderNr"),
		WAGON_NO("wagonNo"),
		DELIVERY_FROM_DATE("deliveryFromDate"),
		TRUCK_ID("truckId");

		String keyString;

		BIMODAL_WORKFLOW_KEY(String keyString) {
			this.keyString = keyString;
		}

		public String getKeyString() {
			return this.keyString;
		}
	}
}
