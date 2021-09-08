package com.hdtx.base.common.exception;

/**
 * 添加数据异常
 */
public class AddInfoException extends ServiceException {
	public AddInfoException() {
	}

	public AddInfoException(String message) {
		super(message);
	}

	public AddInfoException(String message, Throwable cause) {
		super(message, cause);
	}

	public AddInfoException(Throwable cause) {
		super(cause);
	}

	public AddInfoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
