package com.bhu.statistics.util.enums;

public enum ErrorCodeEnum {

	/** 成功 */
	SUCCESS("0", "success"),
	/** 空对象 */
	NULLOBJECT("100000", "对象不能为空"),
	/** 空参数 */
	NULLPARAM("200000", "参数不能为空"),
	/** JSON转换错误 */
	PARAMFORMATERROE("201000", "JSON格式错误"),
	/** 参数错误 */
	PARAMERROE("202000", "参数错误"),
	/** 操作失败 */
	OPERATERROE("300000", "操作失败"),
	;

	private String code;
	private String msg;

	private ErrorCodeEnum(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public String getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

}