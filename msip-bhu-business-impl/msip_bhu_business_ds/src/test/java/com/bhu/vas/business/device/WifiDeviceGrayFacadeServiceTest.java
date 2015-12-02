package com.bhu.vas.business.device;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.GrayLevel;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceGrayVersion;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceVersionFW;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceVersionOM;
import com.bhu.vas.api.rpc.devices.model.pk.WifiDeviceGrayVersionPK;
import com.bhu.vas.api.vto.device.CurrentGrayUsageVTO;
import com.bhu.vas.api.vto.device.DeviceUnitTypeVTO;
import com.bhu.vas.api.vto.device.VersionVTO;
import com.bhu.vas.business.ds.device.facade.WifiDeviceGrayFacadeService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.localunit.BaseTest;
import com.smartwork.msip.localunit.RandomData;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WifiDeviceGrayFacadeServiceTest extends BaseTest{
	
	@Resource
	private WifiDeviceGrayFacadeService wifiDeviceGrayFacadeService;
	private static final String fw_upgrade_url_template = "http://7xl3iu.dl1.z0.glb.clouddn.com/device/dev/fw/%s";
	private static final String om_upgrade_url_template = "http://7xl3iu.dl1.z0.glb.clouddn.com/device/dev/om/AP106/";
	//@Test
	public void test001BatchCreate(){
		// WifiDeviceVersionFW & WifiDeviceVersionOM Batch create
		String WifiDeviceVersionFW_template = "AP106P06V1.3.%sBuild%s_TU";
		String WifiDeviceVersionOM_template = "H106V1.3.%sM%s";
		for(int i=1;i<99;i++){
			String versionfw = String.format(WifiDeviceVersionFW_template, i,String.format("%04d", RandomData.intNumber(100, 9000)));
			WifiDeviceVersionFW fw = new WifiDeviceVersionFW();
			fw.setId(versionfw);
			fw.setDut(VapEnumType.DeviceUnitType.uRouterTU_106.getIndex());
			fw.setName(versionfw);
			fw.setRelated(false);
			fw.setUpgrade_url(String.format(fw_upgrade_url_template,versionfw));
			wifiDeviceGrayFacadeService.getWifiDeviceVersionFWService().insert(fw);
			
			String versionom = String.format(WifiDeviceVersionOM_template, i,String.format("%04d", RandomData.intNumber(100, 9000)));
			WifiDeviceVersionOM om = new WifiDeviceVersionOM();
			om.setId(versionom);
			om.setDut(VapEnumType.DeviceUnitType.uRouterTU_106.getIndex());
			om.setName(versionom);
			om.setRelated(false);
			om.setUpgrade_url(om_upgrade_url_template);
			wifiDeviceGrayFacadeService.getWifiDeviceVersionOMService().insert(om);
		}
		
		// WifiDeviceGrayVersion Batch create
		TailPage<VersionVTO> pagesFW = wifiDeviceGrayFacadeService.pagesFW(VapEnumType.DeviceUnitType.uRouterTU_106, 1, 10);
		TailPage<VersionVTO> pagesOM = wifiDeviceGrayFacadeService.pagesOM(VapEnumType.DeviceUnitType.uRouterTU_106, 1, 10);
		GrayLevel[] gls = VapEnumType.GrayLevel.values();
		int index = 0;
		for(GrayLevel gl:gls){
			if(!gl.isVisible()) continue;
			WifiDeviceGrayVersionPK pk = new WifiDeviceGrayVersionPK(VapEnumType.DeviceUnitType.uRouterTU_106.getIndex(),gl.getIndex());
			WifiDeviceGrayVersion dgv = new WifiDeviceGrayVersion();
			dgv.setId(pk);
			dgv.setDevices(0);
			dgv.setD_fwid(pagesFW.getItems().get(index).getId());
			dgv.setD_omid(pagesOM.getItems().get(index).getId());
			wifiDeviceGrayFacadeService.getWifiDeviceGrayVersionService().insert(dgv);
			index ++;
		}
		
		// WifiDeviceGray Batch create
		//84:82:f4:19:01:0c 84:82:f4:23:06:68
		List<String> firstGray = new ArrayList<String>();
		firstGray.add("84:82:f4:19:01:0c");
		wifiDeviceGrayFacadeService.saveMacs2Gray(VapEnumType.DeviceUnitType.uRouterTU_106,VapEnumType.GrayLevel.First,firstGray);
		
		List<String> secondGray = new ArrayList<String>();
		secondGray.add("84:82:f4:23:06:68");
		wifiDeviceGrayFacadeService.saveMacs2Gray(VapEnumType.DeviceUnitType.uRouterTU_106,VapEnumType.GrayLevel.Second,secondGray);
	}

	//@Test
	public void test002MainPageLeftData(){
		List<DeviceUnitTypeVTO> deviceUnitTypes = wifiDeviceGrayFacadeService.deviceUnitTypes();
		System.out.println(JsonHelper.getJSONString(deviceUnitTypes));
	}

	
	//@Test
	public void test003MainPageRightTopData(){
		CurrentGrayUsageVTO currentGrays = wifiDeviceGrayFacadeService.currentGrays(VapEnumType.DeviceUnitType.uRouterTU_106);
		System.out.println(JsonHelper.getJSONString(currentGrays));
	}
	
	@Test
	public void test004MainPageRightBottomData(){
		TailPage<VersionVTO> pagesFW = wifiDeviceGrayFacadeService.pagesFW(VapEnumType.DeviceUnitType.uRouterTU_106,1,10);
		System.out.println(JsonHelper.getJSONString(pagesFW));
		
		TailPage<VersionVTO> pagesOM = wifiDeviceGrayFacadeService.pagesOM(VapEnumType.DeviceUnitType.uRouterTU_106,1,10);
		System.out.println(JsonHelper.getJSONString(pagesOM));
		
	}
	
	//@Test
	public void test005deviceUnitGrayTest(){
		System.out.println(wifiDeviceGrayFacadeService.deviceUnitGray("84:82:f4:19:01:0c"));
		System.out.println(wifiDeviceGrayFacadeService.deviceUnitGray("84:82:f4:23:06:68"));
	}
}
