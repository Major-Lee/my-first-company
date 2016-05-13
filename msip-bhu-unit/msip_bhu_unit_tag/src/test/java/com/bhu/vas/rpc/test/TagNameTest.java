package com.bhu.vas.rpc.test;



import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.rpc.tag.model.TagGroup;
import com.bhu.vas.api.rpc.tag.model.TagGroupRelation;
import com.bhu.vas.api.rpc.tag.model.TagName;
import com.bhu.vas.business.ds.tag.service.TagDevicesService;
import com.bhu.vas.business.ds.tag.service.TagGroupRelationService;
import com.bhu.vas.business.ds.tag.service.TagGroupService;
import com.bhu.vas.business.ds.tag.service.TagNameService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.localunit.BaseTest;

public class TagNameTest extends BaseTest {
    
    //@Resource
    //TagRpcService tagRpcService;
    @Resource
    TagNameService tagNameService;
    @Resource
    TagDevicesService tagDevicesService;
    
    @Resource
    TagGroupService tagGroupService;
    
    @Resource
    TagGroupRelationService tagGroupRelationService;
    
    //@Test
    public void test003(){
    	//tagRpcService.bindTag(100027,"84:82:f4:28:7a:ec", "公司");
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
    //@Test
    public void test005(){
    	String regex = "[^0-9a-zA-Z_\\u4e00-\\u9fa5]";
    	Pattern pattern = Pattern.compile(regex);
    	Matcher match=pattern.matcher("nihao");
    	boolean flag=match.matches();
    	System.out.println(flag);
    }
    //@Test
    public void test006(){
    	TagGroup tagGroup = new TagGroup();
    	tagGroup.setPid(0);
    	tagGroup.setName("BHU");
    	tagGroup.setCreator(103209);
    	tagGroup.setChildren(0);
    	System.out.println("111");
    	tagGroupService.insert(tagGroup);
    	System.out.println("222");
    }
    //@Test
    public void test007(){
    	TagGroupRelation tagGroupRelation = new TagGroupRelation();
    	tagGroupRelation.setId("84:82:f4:28:7a:ec");
    	tagGroupRelation.setGid(7);
    	tagGroupRelation.setUid(103209);
    	tagGroupRelationService.insert(tagGroupRelation);
    }
    @Test
   public void test008(){
		String[] Parents = "1/2/3/4".split("/");
		
		StringBuilder sb = new StringBuilder();
		for(String path : Parents){
			sb.append("g_").append(path).append(" ");
		}
		System.out.println(sb.toString());
   }
}



