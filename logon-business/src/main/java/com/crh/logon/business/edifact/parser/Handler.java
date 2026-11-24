/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.parser;

import com.crh.logon.business.services.utils.EdifactException;
import com.crh.logon.business.edifact.tags.Tag;

/**
 * @author AdNovum Informatik AG
 */
public interface Handler {

	void tag(Tag tag);
	void warning(EdifactException warning);
}
