package com.bhu.vas.api.dto.commdity.id;
/**
 * 结构化8位扩展业务实体
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class StructuredExtSegment implements java.io.Serializable{
	//支付模式
	private int paymode;

	public int getPaymode() {
		return paymode;
	}

	public void setPaymode(int paymode) {
		this.paymode = paymode;
	}
}
