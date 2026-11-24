/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 * <p>
 * Each key with separate lock Using fair Reentrant locks so lock request order is maintained
 */
public class KeyBasedLockMap<K extends Comparable<K>> {


	private final ConcurrentMap<K, ReentrantLock> lockMap = new ConcurrentHashMap<>();

	public ReentrantLock getLock(K key) {
		return lockMap.computeIfAbsent(key, it -> new ReentrantLock(true));
	}

	public void lock(K key, int lockTimeout) {
		ReentrantLock lock = getLock(key);
		try {
			if (!lock.tryLock(lockTimeout, TimeUnit.SECONDS)) {
				throw new LogonException("Could not acquire lock for key: " + key, Reason.RES_ERROR_IMPL);
			}
		}
		catch (InterruptedException e) {
			throw new LogonException("Could not acquire lock for key: " + key, e);
		}
	}

	public void unlock(K key) {
		getLock(key).unlock();
	}
}
