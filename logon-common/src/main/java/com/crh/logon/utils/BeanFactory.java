/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.utils;

public class BeanFactory {

	public static <T> T createBean(String beanName, Class<T> type) {
		return CDIHelper.getInstance().getBean(beanName, type);
	}
	
	public static <T> T createBean(Class<T> type) {
		return CDIHelper.getInstance().getBean(type);		
	}
	
}
