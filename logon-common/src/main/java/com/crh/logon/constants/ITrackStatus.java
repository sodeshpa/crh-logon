/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.constants;

import java.util.Arrays;

public enum ITrackStatus {

	OK(1L),
	DELIVERED_WRONG_LOCATION(2L),
	DELIVERED_LOCATION_APPROVED(3L),
	SHIPPING_UNLOAD_INFORMATION_MISSING(4L),
	DELIVERED_UNLOAD_INFORMATION_MISSING(5L),
	DELIVERED_PARAMETER_MISSING(6L);

	private final Long statusCode;

	ITrackStatus(Long statusCode) {
		this.statusCode = statusCode;
	}
	public Long getStatusCode() {
		return statusCode;
	}

	public static ITrackStatus valueOf(Long code){
		if (code == null) {
			return null;
		}
		return Arrays.stream(ITrackStatus.values()).filter(it -> code.equals(it.statusCode)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("" + code + " is not a valid ITrackStatus"));
	}
}
