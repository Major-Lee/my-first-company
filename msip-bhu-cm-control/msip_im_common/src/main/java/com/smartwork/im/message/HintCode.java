package com.smartwork.im.message;


public enum HintCode {
	//101可能情况是从wifi切换到3G，本身客户端连接没有断开，但是会重新发送登录请求
	CertainUserRepeatSignedWithSameConnectionATSameCM("101","同一用户在同一连接重复登录","同一用户在同一连接重复登录，@SameCM."),
	
	//102-120区间内的消息，客户端收到后会断开连接并登出系统，客户端提示定义的消息
	CertainUserRepeatSignedWithDifferentConnectionATSameCM("102","您在另外一台设备上登录！","同一用户不同连接在另外一台设备上登录，@SameCM."),
	DiffenentUserSignedWithSameConnectionATSameCM("103","您在另外一台设备上登录！","不同用户在同一连接上发送登录请求，@SameCM."),
	
	CertainUserRepeatSignedATSameCM("105","您在另外一台设备上登录！","在另外一台设备上登录，@SameCM."),
	CertainUserRepeatSignedATDiffCM("106","您在另外一台设备上登录！","在另外一台设备上登录，@DiffCM."),
	
	CertainUserRepeatSignedATDiffDispatcher("107","您在另外一台设备上登录！","在另外一台设备上登录，@DiffDispatcher."),
	
	IllegalMessageInteractive("111","非法消息交互！","无效的消息交互秩序."),
	CertainUserPwdValidError("112","用户登录验证错误！","登录时用户名密码数据验证无效,请重新登录."),
	
	UsernameAndPwdValidWhenUserSigned("201","用户登录验证错误！","登录时用户名密码数据无效."),
	//DiffenentUserSignedInATDifferentConnection("102","已经登录的用户already logged in."),
	ConnectionWillBeClosed("100","连接即将关闭","连接即将关闭"),
	;
	private String code;
	private String hint;
	private String desc;
	private HintCode(String code,String hint,String desc){
		this.code = code;
		this.hint = hint;
		this.desc = desc;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getHint() {
		return hint;
	}
	public void setHint(String hint) {
		this.hint = hint;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
}
