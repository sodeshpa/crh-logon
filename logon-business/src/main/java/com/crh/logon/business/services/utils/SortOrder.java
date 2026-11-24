/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

import ch.nevis.gdi.jpa.filter.Order;

/**
 * @author AdNovum Informatik AG
 */
public class SortOrder implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String DIR_ASCENDING = "ASC";

	private static final String DIR_DESCENDING = "DESC";

	private final String propertyName;

	private final boolean ascending;

	private SortOrder(String propertyName, boolean ascending) {
		this.propertyName = propertyName;
		this.ascending = ascending;
	}

	public static SortOrder asc(String propertyName) {
		return new SortOrder(propertyName, true);
	}

	public static SortOrder desc(String propertyName) {
		return new SortOrder(propertyName, false);
	}

	/**
	 * Parses the order by strings provided by ext-js. Expected format: "propertyName direction"
	 * <p>
	 * Direction should be either ASC or DESC
	 *
	 * @param orderBy the string param supplied via the ajax call
	 * @return SortOrder object that contains the correct direction and property name
	 * @throws LogonException If the input string contains more than 2 tokens or the second tokens is not either of ASC/DESC
	 */
	public static SortOrder parse(String orderBy) {
		if (StringUtils.isNotBlank(orderBy)) {
			String[] tokens = orderBy.split(" ");

			if (tokens.length == 2) {
				String propertyName = tokens[0];
				String dir = tokens[1];
				if (StringUtils.isNotBlank(propertyName)) {
					if (DIR_ASCENDING.equals(dir)) {
						return SortOrder.asc(propertyName);
					}
					else if (DIR_DESCENDING.equals(dir)) {
						return SortOrder.desc(propertyName);
					}
				}
			}
		}

		throw new LogonException("Unable to parse orderBy parameter \""
				+ orderBy + "\" because it is not in the format \"<propertyName> (ASC|DESC)\"",
				Reason.RES_ERROR_UNSPECIFIED);
	}

	public Order getOrder() {
		return (ascending ? Order.asc(propertyName) : Order.desc(propertyName));
	}

	public String getPropertyName() {
		return propertyName;
	}

	public boolean isAscending() {
		return ascending;
	}
}
