package com.bhu.vas.api.vto;

import java.util.List;


/**
 * urouter的周边探测的最热的vto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class URouterWSHotVTO extends URouterWSRecentVTO{
	//近期探测到的次数
	private long ws_count;
	//终端的探测细节流水
	private List<URouterWSDetailVTO> details;
	//终端昵称
	private String nick;
	//是否关注
	private boolean focus;

	public List<URouterWSDetailVTO> getDetails() {
		return details;
	}

	public void setDetails(List<URouterWSDetailVTO> details) {
		this.details = details;
	}

	public long getWs_count() {
		return ws_count;
	}

	public void setWs_count(long ws_count) {
		this.ws_count = ws_count;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public boolean isFocus() {
		return focus;
	}

	public void setFocus(boolean focus) {
		this.focus = focus;
	}
}
