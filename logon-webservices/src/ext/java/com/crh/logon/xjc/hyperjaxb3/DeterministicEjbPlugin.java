/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.xjc.hyperjaxb3;

import org.jvnet.hyperjaxb3.ejb.plugin.EjbPlugin;
import org.xml.sax.ErrorHandler;

import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.outline.Outline;

/**
 * @author AdNovum Informatik AG
 */
public final class DeterministicEjbPlugin extends EjbPlugin {

	public DeterministicEjbPlugin() {
		super();
	}

	@Override
	public boolean run(Outline outline, Options options, ErrorHandler errorHandler) {
		/*
		 * call the super implementation (original hyperjaxb3), but wrap the given Outline,
		 * with our class to provide deterministic sorting
		 */
		return super.run(new DeterministicOutlineWrapper(this, outline), options, errorHandler);
	}
}
