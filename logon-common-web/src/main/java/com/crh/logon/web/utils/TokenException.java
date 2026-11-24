/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.utils;

import javax.servlet.ServletException;

/**
 * @author AdNovum Informatik AG
 */
public class TokenException extends ServletException {
    public final static String MSG_KEY = "errors.double.request";
    public final static String MSG_KEY_1 = "errors.double.request.1";
    public final static String MSG_KEY_2 = "errors.double.request.2";
    public final static String MSG_KEY_3 = "errors.double.request.3";
}
