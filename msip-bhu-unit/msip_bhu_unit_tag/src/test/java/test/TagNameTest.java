package test;



import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.rpc.service.device.TagRpcService;
import com.smartwork.msip.localunit.BaseTest;

public class TagNameTest extends BaseTest {
    
    @Resource
    TagRpcService tagRpcService;
    
    @Test
    public void test003(){
    	tagRpcService.bindTag("84:82:f4:28:7a:ec", "公司");
    	System.out.println("111");
    }
}
