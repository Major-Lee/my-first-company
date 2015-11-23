package com.bhu.vas.api.test;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.bhu.vas.api.rpc.vap.dto.module.BhuModule;
import com.bhu.vas.api.rpc.vap.dto.module.ItemBrand;
import com.bhu.vas.api.rpc.vap.dto.module.ItemChannel;
import com.bhu.vas.api.rpc.vap.dto.module.ItemHttp404;
import com.bhu.vas.api.rpc.vap.dto.module.ItemRedirect;
import com.bhu.vas.api.rpc.vap.dto.module.SubBrand;
import com.bhu.vas.api.rpc.vap.dto.module.SubChannel;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.xml.jaxb.JAXBXMLHelper;

public class TestBHUModule {
/**
<bhu_module>
	<http404>
		<ITEM enable="enable" url="http://vap.bhunetworks.com/vap/rw404?bid=10002" codes="40*,50*,10*" ver="style001-00.00.03"/>
	</http404>
	<redirect>
		<ITEM enable="enable" rule="100,12:00:00,20:00:00,http://sina.cn,http://m.hao123.com/?union=1&amp;from=1012546c&amp;tn=ops1012546c,http://m.sohu.com,http://m.hao123.com/?union=1&amp;from=1012546c&amp;tn=ops1012546c,http://h5.mse.360.cn,http://m.hao123.com/?union=1&amp;from=1012546c&amp;tn=ops1012546c,http://hao.360.cn,http://www.hao123.com" ver="style004-00.00.01" />
	</redirect>
	<brand>
		<ITEM enable="enable" interval=”1800” ver="style001-00.00.03">
			<list>
				<SUB src_url=”www.sina.com.cn,www.sohu.com” dest_url=”…” />
				<SUB src_url=”11” dest_url=”22” />
			</list>
		</ITEM>
	</brand>

	<channel>
		<ITEM enable="enable" rate=”50” ver="style001-00.00.03">
			<list>
				<SUB src_url=”www.sina.com.cn/indx.html,www.sina.com.cn” param=”bid=000023, aid=2222, cid=43333” />
				<SUB src_url=”11” param=”22” />
			</list>
		</ITEM>
	</channel>
</bhu_module>
 * @throws JAXBException 
 */
	public static void main(String[] args) throws JAXBException {  
		ItemBrand brand = new ItemBrand();
		brand.setEnable("enable");
		brand.setInterval("50");
		brand.setVer("style001-00.00.03");
		SubBrand b1 = new SubBrand();
		b1.setSequence(1);
		b1.setSrc_url("www.sina.com.cn,www.sohu.com");
		b1.setDest_url("www.chinaren.com");
		SubBrand b2 = new SubBrand();
		b2.setSequence(2);
		b2.setSrc_url("www.sina1.com.cn,www.sohu1.com");
		b2.setDest_url("www.chinaren1.com");
		List<SubBrand> subbrands = new ArrayList<>();
		subbrands.add(b1);
		subbrands.add(b2);
		brand.setSubs(subbrands);
		
		ItemChannel channel = new ItemChannel();
		channel.setEnable("enable");
		//channel.setRate("50");
		channel.setVer("style002-00.00.03");
		SubChannel a1 = new SubChannel();
		a1.setSequence(1);
		a1.setSrc_url("www.sina.com.cn/indx.html,www.sina.com.cn");
		a1.setParam("bid=000023, aid=2222, cid=43333");
		a1.setRate("50");
		SubChannel a2 = new SubChannel();
		a2.setSequence(2);
		a2.setSrc_url("www.baidu.com,www.hao123.com");
		a2.setParam("bid=48765,hid=sasdfsdf123");
		a2.setRate("50");
		List<SubChannel> subchannels = new ArrayList<>();
		subchannels.add(a1);
		subchannels.add(a2);
		channel.setSubs(subchannels);
		
		
		ItemRedirect redirect = new ItemRedirect();
		redirect.setEnable("enable");
		redirect.setRule("100,12:00:00,20:00:00,http://sina.cn,http://m.hao123.com/?union=1&amp;from=1012546c&amp;tn=ops1012546c,http://m.sohu.com,http://m.hao123.com/?union=1&amp;from=1012546c&amp;tn=ops1012546c,http://h5.mse.360.cn,http://m.hao123.com/?union=1&amp;from=1012546c&amp;tn=ops1012546c,http://hao.360.cn,http://www.hao123.com");
		redirect.setVer("style004-00.00.03");
		
		
		ItemHttp404 http404 = new ItemHttp404();
		http404.setEnable("enable");
		http404.setUrl("http://vap.bhunetworks.com/vap/rw404?bid=10002");
		http404.setCodes("40*,50*,10*");
		http404.setVer("style004-00.00.03");
		
		BhuModule module = new BhuModule();
		List<ItemBrand> brands = new ArrayList<>();
		brands.add(brand);
		module.setBrands(brands);
		
		List<ItemChannel> channels = new ArrayList<>();
		channels.add(channel);
		module.setChannels(channels);
		
		List<ItemRedirect> redirects = new ArrayList<>();
		redirects.add(redirect);
		module.setRedirects(redirects);
		
		List<ItemHttp404> http404s = new ArrayList<>();
		http404s.add(http404);
		module.setHttp404s(http404s);
		//将java对象转换为XML字符串  
		JAXBXMLHelper requestBinder = new JAXBXMLHelper(BhuModule.class);//,CollectionWrapper.class);  
        String retXml = requestBinder.toXml(module, "utf-8",false);  
        System.out.println("xml:\n"+retXml);
        System.out.println("json:\n"+JsonHelper.getJSONString(module));
        
        
        BhuModule module2 = requestBinder.fromXml(retXml,BhuModule.class);
        
        System.out.println(module2);
        
        
        //JAXBContext newInstance = JAXBContext.newInstance(BhuModule.class);
        //JAXBContext newInstance2 = JAXBContext.newInstance(BhuModule.class,CollectionWrapper.class);
       // JAXBContext newInstance3 = JAXBContext.newInstance(Hotel.class,CollectionWrapper.class);
        
        System.out.println(module2);
        
        
        
        
        /*String xmlFile = TestBHUModule.class.getResource("/test_conf/TTT.xml").getFile();
        System.out.println(xmlFile);
        BhuModule module3 = requestBinder.fromXmlFile(xmlFile,false,BhuModule.class);
        System.out.println(module3.getBrands().get(0).getInterval());
        module3.getBrands().get(0).setInterval("200");*/
        
        //requestBinder.toXmlFile(xmlFile, module3, "utf-8", false);
	}
}
