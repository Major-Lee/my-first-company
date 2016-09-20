package com.bhu.vas.api.rpc.user.notify;



public interface IWalletVCurrencySpendCallback {
	boolean beforeCheck(int uid,long vcurrency_cost,long vcurrency_has);
	String after(int uid,long vcurrency_leave);
}
