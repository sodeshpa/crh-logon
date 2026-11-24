/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.mobile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.crh.logon.business.services.utils.KeyBasedLockMap;

/**
 * This test class demonstrates how to proceed our @KeyBasedLockMap based mutex function with a non-negative and negative test.
 * 
 */
public class MutexTest {

	//private static final Mutex<String> MUTEX = new Mutex<String>();
	private static final KeyBasedLockMap<String> MUTEX = new KeyBasedLockMap<String>();
	
	// thread pool size
	private static final Integer THREAD_POOL_SIZE = 10;
	
	//private static final String[] DEVICE_IDS = {"device1", "device2", "device3", "device4", "device5", "device6", "device7", "device8", "device9", "device10"};
	// Device id array
	private static final String[] DEVICE_IDS = {"device1"};
	
	// iteration size of test
	private static final Integer ITERATION_SIZE = 20;
	
	// Counter
	private static AtomicInteger counter = new AtomicInteger(0);

	private static final int ACQUISITION_TIMEOUT_SECONDS = 270;

	private ExecutorService initThreadPoolAndSubmitDeviceThreads(boolean negativeTest) {
		System.out.println("Init thread pool (size=" + THREAD_POOL_SIZE + ")");
		// Init threadpool
		ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

		System.out.println("Init device threads size(" +  ITERATION_SIZE + ")");
		// Init device threads for test
		List<DeviceThread> threads = new ArrayList<DeviceThread>(); 
		for (int i = 0; i < ITERATION_SIZE; i++) {
			String id = String.valueOf(i);			
			String deviceId = DEVICE_IDS[0];
			String timestamp = String.valueOf(new Date().getTime());			
			threads.add(new DeviceThread(id, deviceId, timestamp, negativeTest));
			
			// wait 0.5 second cause of timestamp
			try {
				Thread.sleep(500);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}
		
		counter.set(0);
		
		System.out.println("Submit device threads to threadpool");
		// Submit all threads to threadpool
		for (DeviceThread deviceThread : threads) {
			executorService.submit(deviceThread);
		}	
		
		return executorService;
	}
	
	/**
	 * Start non negative test which demostrates mutex function that just one thread can get the monitor by device.
	 */
	@Test
	public void startNonNegativeTest() {
		System.out.println("Start Non negative test");
		ExecutorService executorService = initThreadPoolAndSubmitDeviceThreads(false);
		
		long waitTimeout = Math.round(((double)ITERATION_SIZE / (double)DEVICE_IDS.length) + 1) * 1000;
		System.out.println("Wait for shutdown " + waitTimeout/1000 + " seconds");
		try {
			Thread.sleep(waitTimeout);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Shutdown");		
		executorService.shutdown();		
	}
	
	/**
	 * Start negative test which demostrates mutex function that just one thread can get the monitor by device. If an error occured at unlock (in our test just simulated) deadlock can not occured.
	 */
	@Test
	public void startNegativeTest() {
		System.out.println("Start negative test");		
		ExecutorService executorService = initThreadPoolAndSubmitDeviceThreads(true);
		
		long waitTimeout = (ITERATION_SIZE / 10) * ACQUISITION_TIMEOUT_SECONDS * 1000;
		System.out.println("Wait for shutdown " + waitTimeout/1000 + " seconds");
		try {
			Thread.sleep(waitTimeout);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Shutdown");		
		executorService.shutdown();		
	}
		
	/**
	 * In a lock-unlock boundary we wait 1 second. Within this boundary just one thread can get the monitor.
	 * 
	 * @param deviceThread
	 */
	private static void runNonNegativeTestOnDeviceThread(DeviceThread deviceThread) {
		try {
			try {
				// This lock will enter after 4.5 minutes wait timeout
				int waitTimeout = ACQUISITION_TIMEOUT_SECONDS * 1000;
				MUTEX.lock(deviceThread.deviceId,waitTimeout);
			}
			catch (Exception e) {
				System.out.println("Error in lock with deviceId: " + deviceThread.deviceId);					
			}				

			try {
				// wait 1 second
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
							
			System.out.println(counter.incrementAndGet() + " - " + deviceThread + " is proceeded on " + Thread.currentThread().getName());
			
			
		} finally {
			try {	
				MUTEX.unlock(deviceThread.deviceId);				
			}
			catch (Exception e) {
				System.out.println("Error in unlock with deviceId: " + deviceThread.deviceId);
			}					
		}								
	}

	/**
	 * In a lock-unlock boundary we wait 1 second. Within this boundary just one thread can get the monitor.
	 * If counter is divisible with 10 we throws a RuntimeException and do not unlock. 
	 * After 5 minutes wait timeout this lock will be invalid and all threads can get the monitors in parallel. 
	 * In this situation deadlock can not happened.
	 * 
	 * @param deviceThread
	 */
	private static void runNegativeTestOnDeviceThread(DeviceThread deviceThread) {
		try {
			try {
				// This lock will enter after 4.5 minutes wait timeout
				int waitTimeout = ACQUISITION_TIMEOUT_SECONDS * 1000;
				MUTEX.lock(deviceThread.deviceId, waitTimeout);
			}
			catch (Exception e) {
				System.out.println("Error in lock with deviceId: " + deviceThread.deviceId);					
			}				

			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
							
			counter.incrementAndGet();
							
			if(counter.get() % 10 == 0) {
				throw new RuntimeException("Something error");
			}
			
			System.out.println(counter.get() + " - " + deviceThread + " is proceeded on " + Thread.currentThread().getName());
			
			
		} finally {
			if(counter.get() % 10 == 0) {
				System.out.println("No unlock device " + deviceThread.deviceId + " on " + Thread.currentThread().getName());
				long waitTimeout = ACQUISITION_TIMEOUT_SECONDS * 1000;
				System.out.println("Wait timeout " + ACQUISITION_TIMEOUT_SECONDS + " seconds on " + Thread.currentThread().getName());
				try {
					Thread.sleep(waitTimeout);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				try {	
					MUTEX.unlock(deviceThread.deviceId);				
				}
				catch (Exception e) {
					System.out.println("Error in unlock with deviceId: " + deviceThread.deviceId);
				}					
			}
		}								
	}
	
	
	private static class DeviceThread implements Runnable {
		
		private String id;
		private String deviceId;
		private String timestamp;
		private boolean negativeTest;
		
		/**
		 * 
		 */
		public DeviceThread(String id, String deviceId, String timestamp, boolean negativeTest) {
			this.id = id;
			this.deviceId = deviceId;
			this.timestamp = timestamp;
			this.negativeTest = negativeTest;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			if(negativeTest) {
				runNegativeTestOnDeviceThread(this);				
			} else {
				runNonNegativeTestOnDeviceThread(this);
			}
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("DeviceThread [id=");
			builder.append(id);
			builder.append(", deviceId=");
			builder.append(deviceId);
			builder.append(", timestamp=");
			builder.append(timestamp);
			builder.append("]");
			return builder.toString();
		}		
		
		
	}
}
