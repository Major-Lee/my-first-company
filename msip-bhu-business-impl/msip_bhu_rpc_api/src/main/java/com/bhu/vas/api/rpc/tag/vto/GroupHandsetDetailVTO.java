package com.bhu.vas.api.rpc.tag.vto;

import java.io.Serializable;

import com.smartwork.msip.cores.orm.support.page.TailPage;

@SuppressWarnings("serial")
public class GroupHandsetDetailVTO implements Serializable{
	private TailPage<TagGroupHandsetDetailVTO> tailPage;
	private int userTotal;
	private int connTotal;
	private int authTotal;
	
	public TailPage<TagGroupHandsetDetailVTO> getTailPage() {
		return tailPage;
	}
	public void setTailPage(TailPage<TagGroupHandsetDetailVTO> tailPage) {
		this.tailPage = tailPage;
	}
	public int getUserTotal() {
		return userTotal;
	}
	public void setUserTotal(int userTotal) {
		this.userTotal = userTotal;
	}
	public int getConnTotal() {
		return connTotal;
	}
	public void setConnTotal(int connTotal) {
		this.connTotal = connTotal;
	}
	public int getAuthTotal() {
		return authTotal;
	}
	public void setAuthTotal(int authTotal) {
		this.authTotal = authTotal;
	}
	
	
}
