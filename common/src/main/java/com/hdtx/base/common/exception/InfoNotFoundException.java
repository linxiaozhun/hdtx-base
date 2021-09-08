package com.hdtx.base.common.exception;

/**
 * 没有找到信息
 */
public class InfoNotFoundException extends ServiceException {
	public InfoNotFoundException() {
	}

	public InfoNotFoundException(String message) {
		super(message);
	}

	public InfoNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public InfoNotFoundException(Throwable cause) {
		super(cause);
	}

	public InfoNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
