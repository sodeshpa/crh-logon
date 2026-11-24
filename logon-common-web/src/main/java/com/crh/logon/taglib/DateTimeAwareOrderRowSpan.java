package com.crh.logon.taglib;

import com.crh.logon.business.services.utils.DateTimeAwareOrderWrapper;

/**
 * A short-term HACK for getting the Web orders working with the DateTimeAwareOrderWrapper entities.
 * In general, I think we need to move away from directly passing DB models straight to the front-end
 * since we lose the ability to enhance them with reverting to some runtime library such as CGLIB
 * (a Java Proxy would work, but the DB entities have no interfaces as far as I am aware - perhaps if
 * we generated interfaces as well, we could revert to using Java Proxies).
 *
 * @author AdNovum Informatik AG
 */
public class DateTimeAwareOrderRowSpan extends OrderRowSpan {
	@Override
	protected DateTimeAwareOrderWrapper getOrderCreateEntityByIndex(int index) {
		return ((DateTimeAwareOrderWrapper) getPageContextOrderList().get(index));
	}
}
