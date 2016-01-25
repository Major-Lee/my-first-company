package com.bhu.vas.business.search.core.exception;

/**
 * 查询条件验证错误异常
 * @author lawliet
 *
 */
@SuppressWarnings("serial")
public class SearchQueryValidateException extends Exception{
	
	public SearchQueryValidateException() {
	}

	public SearchQueryValidateException(String message) {
		super(message);
	}
}
