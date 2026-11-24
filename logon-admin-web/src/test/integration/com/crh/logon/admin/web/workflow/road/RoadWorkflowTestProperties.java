/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.workflow.road;

import org.junit.Ignore;
import com.crh.logon.admin.web.pages.PropertyKey;

@Ignore
public abstract class RoadWorkflowTestProperties {
	
	public enum ROAD_WORKFLOW_KEY implements PropertyKey {
		
		ORDER_NO("orderNr"),
		CUST_ORDER_NO("custOrderNr"),

		//used by soapui test as well?
		TRUCK_PLATE_NO("truckLabel"),
		TRUCK_ID("truckId");
		
		String keyString;
		
		ROAD_WORKFLOW_KEY(String keyString) {
			this.keyString = keyString;
		}

		public String getKeyString() {
			return this.keyString;
		}
	}
}