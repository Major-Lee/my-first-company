package com.bhu.vas.api.rpc.commdity.vto;

import com.smartwork.msip.cores.orm.support.page.TailPage;

@SuppressWarnings("serial")
public class QualityGoodsSharedealVTO implements java.io.Serializable{
	private int waitCount = 0;
	private int doneCount = 0;
	private int cancelCount = 0;
	
	private TailPage<QualityGoodsSharedealListVTO> tailPages;

	public int getWaitCount() {
		return waitCount;
	}

	public void setWaitCount(int waitCount) {
		this.waitCount = waitCount;
	}

	public int getDoneCount() {
		return doneCount;
	}

	public void setDoneCount(int doneCount) {
		this.doneCount = doneCount;
	}

	public int getCancelCount() {
		return cancelCount;
	}

	public void setCancelCount(int cancelCount) {
		this.cancelCount = cancelCount;
	}

	public TailPage<QualityGoodsSharedealListVTO> getTailPages() {
		return tailPages;
	}

	public void setTailPages(TailPage<QualityGoodsSharedealListVTO> tailPages) {
		this.tailPages = tailPages;
	}
}
