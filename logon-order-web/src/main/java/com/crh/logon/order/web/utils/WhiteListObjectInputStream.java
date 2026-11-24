package com.crh.logon.order.web.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.Set;
import java.util.stream.Collectors;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * Whitelist based Object Input Stream to prevent Java Deserialization attacks
 * Following OWASP recommendations for explicit white listing of classes for deserialization before
 * they hit the classloader.
 * https://www.owasp.org/index.php/Deserialization_Cheat_Sheet#Java
 */
public class WhiteListObjectInputStream extends ObjectInputStream {
	private final Set<Class> classWhitelist;
	private final Set<Package> packageWhiteList;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(WhiteListObjectInputStream.class);

	/**
	 *
	 * @param in the input stream {@link ObjectInputStream}
	 * @param classWhitelist a set of allowed classes
	 * @param packageWhiteList a set of allowed packages, the listed package and all subpackages are allowed.
	 */
	public WhiteListObjectInputStream(InputStream in, Set<Class> classWhitelist, Set<Package> packageWhiteList)
			throws
			IOException {
		super(in);
		this.classWhitelist = classWhitelist;
		this.packageWhiteList = packageWhiteList;
	}

	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
		LOG.debug("Whitelist check for class: "+desc.getName());

		for (Package pkg : packageWhiteList) {
			if(desc.getName().startsWith(pkg.getName())){
				return super.resolveClass(desc);
			}
		}

		for (Class whitelistedClass : classWhitelist) {
			if(desc.getName().equals(whitelistedClass.getName())){ //NOSONAR "squid:S1872 Classes should not be compared by
				// name" is invalid here. We only have the class name before resolving the class. To prevent deserialization
				// attacks we need to whitelist BEFORE the lookup
				return super.resolveClass(desc);
			}
		}

		String packageWhiteListString = packageWhiteList.stream().map(Package::getName).collect(Collectors.joining(", "));
		String classWhiteListString = classWhitelist.stream().map(Class::getName).collect(Collectors.joining(", "));
		String errorDescription = "Class not in deserialization whitelist. Failed class is: "+desc.getName()
				+"\nClass whitelist: ["+ classWhiteListString+"] package whitelist: ["+ packageWhiteListString+"]";
		LOG.error(errorDescription);
		throw new ClassNotFoundException(errorDescription);
	}



}
