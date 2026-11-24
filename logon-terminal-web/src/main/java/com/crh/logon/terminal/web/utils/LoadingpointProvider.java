/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.terminal.web.utils;

import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.PickupLoadingpointEntity;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * store the current user inside a threadlocal. be careful: since 
 * containers do threadpooling, a threadlocal might be dirty if not 
 * properly used. this class' setLoadingpoint() should only be used by the 
 * TerminalUserFilter.
 * 
 * @author AdNovum Informatik AG
 */
@SuppressWarnings("unchecked")
public class LoadingpointProvider {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LoadingpointProvider.class);
	
	private static ThreadLocal pickupLoadingpoint = new ThreadLocal();

	public static LoadingpointEntity getLoadingpoint() {
		return getPickupLoadingpoint().getLoadingpoint();
	}

	public static PickupLoadingpointEntity getPickupLoadingpoint() {
		return (PickupLoadingpointEntity) pickupLoadingpoint.get();
	}

	public static void setPickupLoadingpoint(PickupLoadingpointEntity loadingp) {
		pickupLoadingpoint.set(loadingp);
	}
}
