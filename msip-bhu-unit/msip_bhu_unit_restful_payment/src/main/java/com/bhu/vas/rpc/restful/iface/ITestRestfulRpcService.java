package com.bhu.vas.rpc.restful.iface;

import com.bhu.vas.rpc.restful.model.Test;

public interface ITestRestfulRpcService {
	Test register(Test test);
	void remove(Test test);
}
