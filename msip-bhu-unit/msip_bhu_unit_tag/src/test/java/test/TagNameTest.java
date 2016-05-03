package test;



import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.rpc.tag.model.TagName;
import com.bhu.vas.business.ds.tag.service.TagDevicesService;
import com.bhu.vas.business.ds.tag.service.TagNameService;
import com.bhu.vas.rpc.service.device.TagRpcService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.localunit.BaseTest;

public class TagNameTest extends BaseTest {
    
    @Resource
    TagRpcService tagRpcService;
    @Resource
    TagNameService tagNameService;
    @Resource
    TagDevicesService tagDevicesService;
    
    //@Test
    public void test003(){
    	tagRpcService.bindTag(100027,"84:82:f4:28:7a:ec", "公司");
    	System.out.println("111");
    }
    //@Test
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
    @Test
    public void test005(){
    	String regex = "[^0-9a-zA-Z_\\u4e00-\\u9fa5]";
    	Pattern pattern = Pattern.compile(regex);
    	Matcher match=pattern.matcher("nihao");
    	boolean flag=match.matches();
    	System.out.println(flag);
    }
}
