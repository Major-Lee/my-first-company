package com.bhu.vas.api.dto.commdity;

import com.bhu.vas.api.rpc.user.model.UserConsumptiveWalletLog;
import com.smartwork.msip.cores.orm.support.page.TailPage;

@SuppressWarnings("serial")
public class QueryBalanceLogsVTO implements java.io.Serializable{
	private TailPage<UserConsumptiveWalletLog> logs;

	public TailPage<UserConsumptiveWalletLog> getLogs() {
		return logs;
	}

	public void setLogs(TailPage<UserConsumptiveWalletLog> logs) {
		this.logs = logs;
	}
	
}
