/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package ch.nevis.gdi.jpa.filter;

/**
 * @author AdNovum Informatik AG
 */
public class ThreadLocalIdentificationVariable {

	private static ThreadLocal<String> threadLocal = new ThreadLocal<String>();
	
	static final String IDENTIFICATION_VARIABLE_AS_PROPERTY_NAME = "IDENTIFICATION_VARIABLE_AS_PROPERTY_NAME";
	
	static void setIdentificationVariable(String identificationVariable) {
		threadLocal.set(identificationVariable);
	}

	public static String getIdentificationVariable() {
		return threadLocal.get();
	}
	
}
