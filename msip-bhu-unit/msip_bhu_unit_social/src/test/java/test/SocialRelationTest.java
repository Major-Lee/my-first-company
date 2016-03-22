package test;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.rpc.service.device.SocialRpcService;
import com.smartwork.msip.localunit.BaseTest;

public class SocialRelationTest extends BaseTest {
    
    @Resource
    SocialRpcService socialRpcService;
    
    @Test
    public void testFollow() {
	System.out.println("123123123");
	socialRpcService.unFollow(100234, "00:11:11:11:11:01");
	System.out.println("end______");
    }
}
