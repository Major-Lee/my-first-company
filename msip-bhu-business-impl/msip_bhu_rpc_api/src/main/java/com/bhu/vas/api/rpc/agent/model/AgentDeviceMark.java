package com.bhu.vas.api.rpc.agent.model;

import com.smartwork.msip.cores.orm.model.BaseStringModel;

/**
 * 代理商设备各种标记表
 * 第一次返现标记及时间
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class AgentDeviceMark extends BaseStringModel {
    /**
     * 设备mac号
     */
	//是否需要第一次进行返现标记 true 需要 false 不需要
	//private boolean need_afcb = true;
	
	//是否经历过第一次返现 alread first cash back
    private boolean afcb = false;
    /**
     * 第一次返现日期
     */
    private String afcb_date;


	public boolean isAfcb() {
		return afcb;
	}

	public void setAfcb(boolean afcb) {
		this.afcb = afcb;
	}

	public String getAfcb_date() {
		return afcb_date;
	}

	public void setAfcb_date(String afcb_date) {
		this.afcb_date = afcb_date;
	}

	/*public boolean isNeed_afcb() {
		return need_afcb;
	}

	public void setNeed_afcb(boolean need_afcb) {
		this.need_afcb = need_afcb;
	}*/

}
