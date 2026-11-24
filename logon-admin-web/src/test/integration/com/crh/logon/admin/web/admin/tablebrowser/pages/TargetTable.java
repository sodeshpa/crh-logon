/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.admin.tablebrowser.pages;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author AdNovum Informatik AG
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TargetTable {
	public String name() ;
}
