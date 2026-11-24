/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.order.web.dto;

import com.crh.logon.utils.Reason;

public class ResponseDTO<T> {

	private final String errorMessage;

	private final String errorReason;

	private final T result;

	private ResponseDTO(String errorMessage, Reason errorReason, T result) {
		this.errorMessage = errorMessage;
		this.errorReason = errorReason != null ? errorReason.name() : null;
		this.result = result;
	}

	public static <T> ResponseDTO<T> success(T result) {
		return new ResponseDTO<>(null, null, result);
	}

	public static <T> ResponseDTO<T> error(String errorMessage, Reason errorReason) {
		return new ResponseDTO<>(errorMessage, errorReason, null);
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getErrorReason() {
		return errorReason;
	}

	public T getResult() {
		return result;
	}
}
