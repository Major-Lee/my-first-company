package com.bhu.vas.api.rpc.user.notify;



public interface IWalletVCurrencySpendCallback {
	boolean beforeCheck(int uid,double vcurrency_cost,double vcurrency_has);
	String after(int uid);
}
