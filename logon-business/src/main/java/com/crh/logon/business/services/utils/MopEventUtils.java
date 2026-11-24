/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.util.Arrays;

import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.eventbeans.LogonEventOutBeanFactory;
import com.crh.logon.persistence.entities.common.MopeventHeaderOutEntity;

/**
 * @author AdNovum Informatik AG
 */
@AdnTrace
public class MopEventUtils {
	public static final String[] tourDataEvents = {
		LogonEventOutBeanFactory.EVENT_LOADORDER.toLowerCase(),
		LogonEventOutBeanFactory.EVENT_UNLOADORDER.toLowerCase(),
		LogonEventOutBeanFactory.EVENT_PREORDER.toLowerCase(),
		LogonEventOutBeanFactory.EVENT_POSTORDER.toLowerCase()
	};
	
	static {
		Arrays.sort(tourDataEvents);
	}
	
	public static boolean isTourData(MopeventHeaderOutEntity header) {
		return Arrays.binarySearch(tourDataEvents, header.getEventName().toLowerCase()) >= 0;
	}
}
