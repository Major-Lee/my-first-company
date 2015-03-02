package com.wecite.toplines.business.subject;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.subject.dto.SubjectNotifyDTO;
import com.bhu.vas.api.subject.model.SubjectClick;
import com.bhu.vas.business.subject.service.SubjectClickService;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.HttpHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.localunit.BaseTest;

public class SubjectClickServiceTest extends BaseTest{
	@Resource
	private SubjectClickService subjectClickService;
	
	//@Test
	public void doClickTest(){
		
		for(int i=0;i<3;i++){
			System.out.println(subjectClickService.click(123,SubjectClick.Field_Estimatetimes));
		}
		
		
		for(int i=0;i<4;i++){
			System.out.println(subjectClickService.click(123,SubjectClick.Field_Up));
		}
		
		for(int i=0;i<3;i++){
			System.out.println(subjectClickService.click(123,SubjectClick.Field_Down));
		}
		
		
		for(int i=0;i<23;i++){
			System.out.println(subjectClickService.click(123,SubjectClick.Field_Estimatetimes));
		}
		/*for(int i=0;i<23;i++){
			System.out.println(subjectMClickService.click("who are u",SubjectMClick.Field_UrlView));
		}*/
		
	}
	
	
	@Test
	public void doHttpTest() throws Exception{
		//SubjectNotifyDTO notifydto = SubjectNotifyDTO.fromSubject(subject);
		//notifydto.setCustom_abstract(dto.getCustom_abstract());
		SubjectNotifyDTO dto = new SubjectNotifyDTO();
		dto.setSid(10001);
		dto.setUid(2);
		dto.setUuid("85DAB336-B8D8-4CE4-AB26-6603FCBA80AA");
		dto.setSubject_code("000");
		dto.setTitle("在豌豆荚办公是怎样一种体验？选择坐哪里真是一件麻烦事");
		dto.setType("1");
		dto.setAuthors("me");
		dto.setYear("2012");
		dto.setTs(System.currentTimeMillis());
		dto.setSource("www.36kr.com");
		dto.setUrl("http://www.pingwest.com/snappea-office-tour/");
		dto.setOrginal_abstract("在国内的创业公司中，豌豆荚可以说是形象比较鲜明一家。目前，豌豆荚在海淀区东升科技园的办公场地有7000多平方米，这些场地被划分为4个区域，33个会议室。虽然面积不小，但在豌豆荚办公，选择坐在哪里其实是一件麻烦事。");
		Map<String,String> header = new HashMap<String,String>();
		header.put(RuntimeConfiguration.Param_ATokenHeader, "atoken");
		header.put(RuntimeConfiguration.Param_RTokenHeader,"rtoken");
		Map<String,String> params  = new HashMap<String,String>();
		//params.put(RuntimeConfiguration.Param_UidRequest, String.valueOf(10081));
		params.put("data", JsonHelper.getJSONString(dto));
		params.put("app", "headline");
		params.put("mod", "Index");
		params.put("act", "share");
		String response = null;
		//?app=headline&mod=Index&act=share
		//response = HttpHelper.getUrlAsString("http://wecite.cn/index.php", params);//, header);
		response = HttpHelper.getUrlAsString("http://192.168.0.188/index.php", params,"UTF-8");//, header);
		/*if(!RuntimeConfiguration.SecretInnerTest)
			response = HttpHelper.postUrlAsString("http://blink.naonaola.com/whisper_api/v1/sessions/validates", params, header);
		else
			response = HttpHelper.postUrlAsString("http://192.168.1.106/whisper_api/v1/sessions/validates", params, header);
		*/
		System.out.println("~~~~~~~~response:"+response);
	}

}
