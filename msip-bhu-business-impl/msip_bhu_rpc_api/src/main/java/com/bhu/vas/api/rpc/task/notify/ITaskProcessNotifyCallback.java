package com.bhu.vas.api.rpc.task.notify;

import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;


public interface ITaskProcessNotifyCallback {
	void notify(int uid,OperationCMD opt_cmd,OperationDS ods_cmd ,String dmac,Object payload);
}
