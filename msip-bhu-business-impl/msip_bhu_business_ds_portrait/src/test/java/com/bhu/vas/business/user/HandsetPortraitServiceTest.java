package com.bhu.vas.business.user;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.smartwork.msip.localunit.BaseTest;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;

/**
 * 主键索引
 * 其他字段不索引的前提下
 * @author Edmond
 *
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HandsetPortraitServiceTest extends BaseTest{

//	@Resource
//	private HandsetPortraitService handsetPortraitService;
	@BeforeClass
    public static void setUp() throws Exception {
		System.out.println("111111111");
		Thread.sleep(1000);
    }

    @AfterClass
    public static void tearDown() throws Exception {
    	System.out.println("0000000");
    	Thread.sleep(1000);
    }

    @Test
	public void test001BatchCreateWithdraw() {
//		String userAgents = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
//		String userAgents = "User-Agent: Mozilla/5.0 (Linux; U; Android 2.3.7; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
//		String userAgents = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0); 360Spider";
//    	String userAgents ="Mozilla/5.0 (Linux; Android 5.0.2; SAMSUNG SM-A5000 Build/LRX22G) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/3.3 Chrome/38.0.2125.102 Mobile Safari/537.36";
    	String userAgents ="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Safari/537.36 Edge/13.10586";
//    	String userAgents ="Mozilla/5.0 (Linux; U; Android 5.1; zh-CN; m2 note Build/LMY47D) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 UCBrowser/10.10.8.822 U3/0.8.0 Mobile Safari/534.30";
    	
    	UserAgent userAgent = UserAgent.parseUserAgentString(userAgents);  
		Browser browser = userAgent.getBrowser(); 
		Version browserVersion = userAgent.getBrowserVersion();
		OperatingSystem os = userAgent.getOperatingSystem();
		System.out.println("browserVersion:"+browserVersion.getVersion());
		System.out.println("browserName:"+browser.getName());
		System.out.println("browserType:"+browser.getBrowserType());
		System.out.println("browser ManufacturerName:"+browser.getManufacturer());
		System.out.println("browser RenderingEngine:"+browser.getRenderingEngine());
		System.out.println("OS name:"+ os.getName());
		System.out.println("OS isMobileDevice:"+os.isMobileDevice());
		System.out.println("OS ID:"+os.getId());
		System.out.println("OS DeviceType:"+os.getDeviceType());
		System.out.println("OS Manufacturer:"+os.getManufacturer());
//		HandsetPortrait ua = new HandsetPortrait();
//		ua.setBrowserType(browser.getBrowserType().toString());
//		ua.setBrowserManufacturer(browser.getManufacturer().toString());
//		ua.setBrowserVersion(browserVersion.getVersion());
//		ua.setId("40-F0-2F-3A-A3-64");
//		ua.setMobile("18640801793");
//		ua.setOsManufacturer(os.getManufacturer().toString());
//		ua.setOsType(os.getDeviceType().toString());
//		boolean isMobileType = os.isMobileDevice();
//		if(isMobileType){
//			ua.setType(0);
//		}else{
//			ua.setType(1);
//		}
//		ua.setCreated_at(new Date());
//		handsetPortraitService.insert(ua);
	}
}
