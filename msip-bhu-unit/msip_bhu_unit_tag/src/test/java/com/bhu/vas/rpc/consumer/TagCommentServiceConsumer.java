package com.bhu.vas.rpc.consumer;

import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.rpc.tag.iservice.ITagRpcService;
import com.bhu.vas.api.rpc.tag.model.TagName;
import com.bhu.vas.business.ds.tag.service.TagNameService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

public class TagCommentServiceConsumer {
	public static void main(String[] args) throws Exception {
		System.setProperty("appname", "BHUTag");
		System.setProperty("zookeeper", "192.168.66.7:2181");
		System.setProperty("provider.port", "");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
				"classpath*:spring/applicationContextCore-resource.xml",
				"classpath*:/com/bhu/vas/rpc/consumer/applicationContextRpcUnitConsumer.xml"});
		context.start();

		TagNameService tagNameService = (TagNameService)context.getBean("tagNameService");
		System.out.println("123123123");
		//tagRpcService.bindTag("84:82:f4:28:7a:ec", "{\"items\":[{\"tag\":\"咖啡馆\"}]}"); //{"items":[{"tag":"公司"}]}
		ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse("1=1");
        mc.setPageSize(1);
        mc.setPageNumber(5);
        List<TagName> list = tagNameService.findModelByModelCriteria(mc);
        for(TagName tagName:list){
        	System.out.println(tagName.getTag());
        }
//		socialRpcService.comment(100312,"123123","ussss", "123123123");
//		RpcResponseDTO<TailPage<WifiCommentVTO>> rpcResult=socialRpcService.pageWifiCommentVTO(100312,"123123", 1, 3);
//		RpcResponseDTO<List<CommentedWifiVTO>>rpcset=socialRpcService.fetchUserCommentWifiList("100312","222222");
		System.out.println("end");
		Thread.currentThread().join();
		context.close();
	}
}
