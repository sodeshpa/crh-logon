/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.beans;

import java.io.Serializable;

/**
 * @author AdNovum Informatik AG
 */
public class WagonSearchBean implements Serializable {

	//contans settings of the search when leaving the page
//    public static final String SEARCH_PARAMETER_KEEPER = "keeper";
//    public static final String SEARCH_PARAMETER_TYPE = "type";
//    public static final String SEARCH_PARAMETER_LOCATION_STATION = "locationstation";
//    public static final String SEARCH_PARAMETER_RETURN_STATION = "returnstation";
//    public static final String SEARCH_PARAMETER_WAGON_NO = "wagonno";
//    public static final String SEARCH_PARAMETER_ACTIVE_FLAG = "activeflag" ;    
	
		String id;
		String value;
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		
}
