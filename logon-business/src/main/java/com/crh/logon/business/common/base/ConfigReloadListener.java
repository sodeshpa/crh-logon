/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.common.base;

@FunctionalInterface
public interface ConfigReloadListener {

	/**
	 * Called when the config has changed during runtime.
	 */
	void configReloaded();
}
