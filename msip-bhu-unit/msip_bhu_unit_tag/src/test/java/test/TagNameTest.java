package test;



import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.rpc.tag.model.TagName;
import com.bhu.vas.business.ds.tag.service.TagNameService;
import com.bhu.vas.rpc.service.device.TagRpcService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.localunit.BaseTest;

public class TagNameTest extends BaseTest {
    
    @Resource
    TagRpcService tagRpcService;
    @Resource
    TagNameService tagNameService;
    
    //@Test
    public void test003(){
    	tagRpcService.bindTag(100027,"84:82:f4:28:7a:ec", "公司");
    	System.out.println("111");
    }
    @Test
    public void test004(){
		ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse("1=1");
        mc.setPageSize(1);
        mc.setPageNumber(5);
        List<TagName> list = tagNameService.findModelByModelCriteria(mc);
        for(TagName tagName:list){
        	System.out.println(tagName.getTag());
        }
        System.out.println("end");
    }
}
