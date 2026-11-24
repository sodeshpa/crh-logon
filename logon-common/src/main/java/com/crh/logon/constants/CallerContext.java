/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.constants;

/**
 * Different sources which are interested in possible materials, delivery settings.
 * 
 * The setting handling could somehow be replaced by different kind of filters for the different contexts.
 * 
 * @author AdNovum Informatik AG
 */
public enum CallerContext {
	DISPOKARTE,
	CALL_CENTER,
	CALL_CENTER_BIMODAL,
	WEB_ORDER,
	PICKUP_TERMINAL,
	LDS,
	ORDER_DETAIL,
	ORDER_SAVE;
}

