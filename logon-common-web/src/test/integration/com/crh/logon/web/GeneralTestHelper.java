/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web;

/**
 * @author AdNovum Informatik AG
 */
public class GeneralTestHelper {
    public static String getCurrentMethod() {
        return getCurrentMethodNameFromThread(0);
    }

    public static String getCallingMethodName() {
        return getCurrentMethodNameFromThread(1);
    }

    private static String getCurrentMethodNameFromThread(int stackLevel) {
        /*
         * 0 - dumpThreads
         * 1 - getStackTrace
         * 2 - thisMethod => getCurrentMethodNameFromThread
         * 3 - callingMethod => method calling thisMethod 
         * 4 - method calling callingMethod
         */
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4 + stackLevel];
        String methodName = stackTraceElement.getMethodName();
        return methodName;
    }
    
    public static String getCurrentClass() {
        return getCurrentClassNameFromThread(0);
    }

    private static String getCurrentClassNameFromThread(int stackLevel) {
        /*
         * 0 - dumpThreads
         * 1 - getStackTrace
         * 2 - thisMethod => getCurrentMethodNameFromThread
         * 3 - callingMethod => method calling thisMethod 
         * 4 - method calling callingMethod
         */
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4 + stackLevel];
        String className = stackTraceElement.getClassName();
        return className;
    }
}
