package test;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.rpc.tag.model.TagGroupHandsetDetail;
import com.bhu.vas.business.ds.tag.service.TagGroupHandsetDetailService;
import com.smartwork.msip.localunit.BaseTest;

public class tagGroupHandsetDetailTest extends BaseTest{
	@Resource
	private TagGroupHandsetDetailService tagGroupHandsetDetailService;
	
	@Test
	public void test001(){
		TagGroupHandsetDetail dto = new TagGroupHandsetDetail();
		dto.setGid(10000);
		dto.setHdmac("84:82:f4:2f:3a:50");
		dto.setMobileno("86 15127166171");
		dto.setNewuser(true);
		dto.setAuth("false");
		tagGroupHandsetDetailService.insert(dto);
	}
}
