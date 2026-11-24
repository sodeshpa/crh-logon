/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.persistence;

/**
 * @author AdNovum Informatik AG
 */
public class TimeTaker {
    private long start;

    /**
     * creates and starts a new TimeTaker
     */
    public TimeTaker() {
        restart();
    }

    /**
     * restarts the TimeTaker.
     */
    public void restart() {
        start = System.currentTimeMillis();
    }

    public long getElapsedTimeMillis() {
        long elapsedTimeMillis = System.currentTimeMillis()-start;
        return elapsedTimeMillis;
    }

    public String getPerformanceLogString() {
        return "QTIME: " + this.getElapsedTimeMillis() + " ms ";
    }
}
