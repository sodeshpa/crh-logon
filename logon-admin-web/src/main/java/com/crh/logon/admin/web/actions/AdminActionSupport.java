/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions;

import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.LogonProperties;
import com.crh.logon.utils.Reason;
import com.crh.logon.web.actions.ActionSupport;

import flexjson.JSONSerializer;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 */
public class AdminActionSupport extends ActionSupport {
	
	protected void ensureInt() {
		if (!getLogonProperties().getZone().equals(LogonProperties.ZONE_INT)) {
            throw new LogonException("tablebrowser can only be used internally!", Reason.RES_ERROR_SECURITY_NOT_ALLOWED);
		}
	}
    
	protected JSONSerializer getJsonSerializer() {
    	return new JSONSerializer()
	    	.exclude("*.class")
	    	.exclude("*.ctlActCd")
	    	.exclude("*.ctlCreTs")
	    	.exclude("*.ctlCreUid")
	    	.exclude("*.ctlModTs")
	    	.exclude("*.ctlModUid");
    }
}
