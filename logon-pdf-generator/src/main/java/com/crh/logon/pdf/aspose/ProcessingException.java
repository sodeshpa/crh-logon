package com.crh.logon.pdf.aspose;

/**
 * Wraps any exception originating from aspose.
 */
public class ProcessingException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ProcessingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProcessingException(Throwable cause) {
		super("Error while transforming the word document.", cause);
	}

	public ProcessingException(String message) {
		super(message);
	}
}
