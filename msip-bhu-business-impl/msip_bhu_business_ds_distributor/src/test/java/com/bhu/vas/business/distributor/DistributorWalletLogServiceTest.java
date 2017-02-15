package com.bhu.vas.business.distributor;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.rpc.distributor.model.DistributorWalletLog;
import com.bhu.vas.business.ds.distributor.service.DistributorWalletLogService;
import com.smartwork.msip.localunit.BaseTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DistributorWalletLogServiceTest extends BaseTest {
	@Resource
	DistributorWalletLogService distributorWalletLogService;

	static String[] letters = { "a", "b", "c", "d", "e", "f", "g", "h", "i",
			"j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
			"w", "x", "y", "z" };

	@Test
	public void test007UpdateByTid() {
		DistributorWalletLog order =distributorWalletLogService.getById(1290l);
		System.out.println(order.getUid());
		System.out.println(order.getOrderid());
		
	}

}
