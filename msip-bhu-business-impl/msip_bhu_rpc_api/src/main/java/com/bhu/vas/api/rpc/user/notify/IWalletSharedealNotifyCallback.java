package com.bhu.vas.api.rpc.user.notify;

import com.bhu.vas.api.rpc.charging.dto.SharedealInfo;

public interface IWalletSharedealNotifyCallback {
	String notifyCashSharedealOper(SharedealInfo sharedeal);
}
