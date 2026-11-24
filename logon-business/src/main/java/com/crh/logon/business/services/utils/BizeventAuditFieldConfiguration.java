/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;


/**
 * @author AdNovum Informatik AG
 */
public interface BizeventAuditFieldConfiguration {
		/**
		 * The config cd of the audit.
		 * @return
		 */
		public long getAuditBizeventConfigId();
		/**
		 * Returns true in case the value stored in the db is not ment to be displayed
		 * as is, but is ment to be resolved to a language dependent string.
		 * @return
		 */
		public boolean isLanguageResolutionNeeded();
		
		/**
		 * Returns a code, which can be resolved to the field type.
		 * 
		 * @return
		 */
		public int getFieldType();
	
}
	
