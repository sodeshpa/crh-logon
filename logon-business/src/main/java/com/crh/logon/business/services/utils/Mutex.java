/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.util.Date;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

import org.apache.commons.lang.ObjectUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * Based on example on http://java.sun.com/javase/6/docs/api/java/util/concurrent/locks/AbstractQueuedSynchronizer.html
 * 
 * This is a reentrant mutual exclusion lock 'collection' class allowing to 
 * lock on several items identified by lockId. 
 * 
 * @author AdNovum Informatik AG
 */
public final class Mutex<K extends Comparable<K>> {

	// Our internal helper class
	private final static class Sync extends AbstractQueuedSynchronizer {
		
		private final static String CONCURRENT_RELEASE_MSG = "<the sync was released in the meantime by the owning thread>";
		
		private transient Long acquisitionTime;
		
		protected Long getAcquisitionTime() {
			return acquisitionTime;
		}

		protected void setAcquisitionTime(Long acquisitionTime) {
			this.acquisitionTime = acquisitionTime;
		}

		@Override
		protected boolean isHeldExclusively() {
            // While we must in general read state before owner,
            // we don't need to do so to check if current thread is owner
            return getExclusiveOwnerThread() == Thread.currentThread();
		}

		public boolean isLocked() {
            return getState() != 0;
        }
		
		// Acquire the lock if state is zero
		@Override
		public boolean tryAcquire(int acquires) {

			if (compareAndSetState(0, 1)) {
				setExclusiveOwnerThread(Thread.currentThread());
				setAcquisitionTime(System.currentTimeMillis());
				return true;
			}

			return false;
		}

		// Release the lock by setting state to zero
		@Override
        protected boolean tryRelease(int releases) {
            if (!isHeldExclusively()) {
                throw new IllegalMonitorStateException();
            }
            
            setState(0);
            setExclusiveOwnerThread(null);
            setAcquisitionTime(null);
			
            return true;
        }

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append("This sync object [");
			sb.append(ObjectUtils.identityToString(this));
						
			if (!isLocked()) {
				sb.append("] is not locked currently.\n");
			}
			else {				
				final Thread thread = getExclusiveOwnerThread();
				final Long lockTime = getAcquisitionTime();
				final int queueLength = getQueueLength();
				final Long now = System.currentTimeMillis();
				
				sb.append("] is currently locked, with a queque length of ");				
				sb.append(queueLength);
				sb.append(" (this thread is not included).");
				sb.append("\nCurrent time: ");		
				sb.append(new Date(now));
				sb.append("\nAcquisitionTime: ");
				if (lockTime != null) {
					sb.append(new Date(lockTime));
					sb.append("\nTime locked: ");
					sb.append(DateTimeUtils.formatDuration(now - lockTime));
				}
				else {
					sb.append(CONCURRENT_RELEASE_MSG);
				}
				sb.append("\nThread information: ");
				if (thread != null) {
					sb.append(thread);
					sb.append("\nIt's current stacktrace is:");
					
					final StackTraceElement[] stes = thread.getStackTrace(); 
		            for (StackTraceElement ste : stes) {
		            	sb.append("\n\tat " + ste);
		            }
				}
				else {
					sb.append(CONCURRENT_RELEASE_MSG);					
				}				
			}
			
			return sb.toString();
		}
	}
	
	private final static JcanLogger LOG = JcanLoggerFactory.getLogger(Mutex.class);

	private final static long DEFAULT_TRY_ACQUIRE_NANOS = 1000 * 1000 * 1000 * 60 * 15; // 15 min
	private final static boolean DEFAULT_TRY_ACQUIRE_FAILURE_ONLY_LOG = true;
		
	private final ConcurrentMap<K, Sync> syncs = new ConcurrentSkipListMap<K, Sync>();
	
	private final long tryAcquireNanos;
	private final boolean tryAcquireFailuteOnlyLog;
	
	public Mutex() {
		this(DEFAULT_TRY_ACQUIRE_NANOS, DEFAULT_TRY_ACQUIRE_FAILURE_ONLY_LOG);
	}
	
	public Mutex(long tryAcquireNanos, boolean tryAcquireFailuteOnlyLog) {
		this.tryAcquireNanos = tryAcquireNanos;
		this.tryAcquireFailuteOnlyLog = tryAcquireFailuteOnlyLog;
	}
	
	public void lock(K key) {
		if (key == null) {			
			throw new IllegalMonitorStateException("Could not obtain sync object with a null key!");
		}
		
		long start = 0;
		if (LOG.isDebugEnabled()) {
			start = System.currentTimeMillis();
			if (isLocked(key)) {
				LOG.debug("Mutex with key " + key + " is locked, " + getQueueLength(key) + " queued (estimated)");
			}
		}
		
		Sync sync = syncs.get(key);
		if (sync == null) {
		    final Sync value = new Sync();
		    sync = syncs.putIfAbsent(key, value);
		    if (sync == null) {
		    	sync = value;
		    }
		}
		
		lock(sync);
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Waiting for key " + key + " took " + (System.currentTimeMillis() - start) + " ms");
		}
	}
	
	private void lock(Sync sync) {
		if (tryAcquireNanos > 0) {
			try {
				for (;;) {
					if (sync.tryAcquireNanos(1, tryAcquireNanos)) {
						return;
					}
					
					final String msg = String.format("Tried to acquire lock, and wait nanos exceeded (%d ns). Lock details: ",
							tryAcquireNanos,
							sync.toString());
					
					LOG.error(msg);
					if (!tryAcquireFailuteOnlyLog) {
						throw new LogonException(msg, Reason.RES_ERROR_IMPL);						
					}
				}
			}
			catch (InterruptedException e) {
				throw new LogonException("Thread was interrupted while waiting on lock acquisition!", e);
			}
		}
		else {
			sync.acquire(1);
		}
	}

	public void unlock(K key) {
		if (key == null) {			
			throw new IllegalMonitorStateException("Could not obtain sync object with a null key!");
		}
		
		final Sync sync = syncs.get(key);	
		if (sync != null) {	
			sync.release(1);
		}
		else {
			throw new IllegalMonitorStateException("Lock key " + key + " not found");
		}		
	}

	public boolean unlockIfHeldExclusively(K key) {
		if (key == null) {			
			throw new IllegalMonitorStateException("Could not obtain sync object with a null key!");
		}
		
		final Sync sync = syncs.get(key);	
		if (sync != null) {
			final boolean isHeldExclusively = sync.isHeldExclusively();
			if (isHeldExclusively) {
				sync.release(1);
			}
			else {
				if (LOG.isWarnEnabled()) {
					LOG.warn("We tried to release the Sync object [" + 
							ObjectUtils.identityToString(sync) + 
							"] without having it locked!");
				}
			}
			return isHeldExclusively;
		}
		else {
			throw new IllegalMonitorStateException("Lock key " + key + " not found");
		}		
	}

	public boolean isHeldExclusively(K key) {
		if (key == null) {			
			throw new IllegalMonitorStateException("Could not obtain sync object with a null key!");
		}
		
		final Sync sync = syncs.get(key);
		if (sync != null) {			
			return sync.isHeldExclusively();
		}
		else {
			return false;
		}
	}

	public boolean isLocked(K key) {
		if (key == null) {			
			throw new IllegalMonitorStateException("Could not obtain sync object with a null key!");
		}
		
		final Sync sync = syncs.get(key);
		if (sync != null) {			
			return sync.isLocked();
		}
		else {
			return false;
		}
	}
	
	public int getQueueLength(K key) {
		if (key == null) {			
			throw new IllegalMonitorStateException("Could not obtain sync object with a null key!");
		}
		
		final Sync sync = syncs.get(key);
		if (sync != null) {		
			return sync.getQueueLength();
		}
		else {
			return -1;
		}
	}
	
	public String toString(K key) {
		return String.valueOf(syncs.get(key));
	}
}