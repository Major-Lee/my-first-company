package com.bhu.vas.business.search.test;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.data.domain.Page;

import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;
import com.smartwork.msip.localunit.BaseTest;

public class TestIterator extends BaseTest {
    @Resource
    private WifiDeviceDataSearchService wifiDeviceDataSearchService;

    //@Test
    public void test() {
	
	String message = "{\"search_t\":1,\"search_cs\":[{\"cs\":[{\"key\":\"d_address\",\"pattern\":\"seq\",\"payload\":\"北京\"}]}]}";
	
	wifiDeviceDataSearchService.iteratorAll(message,
		new IteratorNotify<Page<WifiDeviceDocument>>() {
		    @Override
		    public void notifyComming(Page<WifiDeviceDocument> pages) {
			for (WifiDeviceDocument doc : pages) {
			    System.out.println(doc.getA_id());
			}
			System.out.println(pages.getTotalElements());
		    }
		});

    }
    
    //@Test
    public void test1() {
	for (int i = 0; i < 345; i++) {
	    if (i!=0 && i%10==0 || i==345-1) {
		System.out.println(i);
	    }
	}
    }
}
