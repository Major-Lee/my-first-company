package com.bhu.vas.business.device;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.mdto.WifiHandsetDeviceItemDetailMDTO;
import com.bhu.vas.business.ds.device.dao.WifiHandsetDeviceRelationMDao;
import com.bhu.vas.business.ds.device.mdto.WifiHandsetDeviceRelationMDTO;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.localunit.BaseTest;

public class WifiDeviceFacadeServiceTest extends BaseTest{

//	@Resource
//	DeviceFacadeService deviceFacadeService;
//
	//@Resource
	//WifiHandsetDeviceRelationMService wifiHandsetDeviceRelationMService;

	@Resource
	WifiHandsetDeviceRelationMDao wifiHandsetDeviceRelationMDao;

	@Test
	public void test() {
		int j = (int) 91594772 / (24 * 3600 * 1000);
		System.out.println(j);

		System.out.println(getSevenDateOfWeek(2));

		System.out.println(DateTimeHelper.formatDate(new Date(1438963199000l),DateTimeHelper.longDateFormat));
	}

//	@Test
//	public void fetchWifiDevicePersistenceCMDTest() throws InterruptedException{
//		List<String> payloads = deviceFacadeService.fetchWifiDevicePersistenceCMD("84:82:f4:19:01:0c");
//		for(String payload:payloads){
//			Thread.sleep(1000);
//			System.out.println(payload);
//		}
//	}

	@SuppressWarnings("unused")
	@Test
	public void testOnline() {

		String wifiId = "84:82:f4:19:01:0c";
		String handsetId = "38:48:4c:c5:05:6d";

		WifiHandsetDeviceRelationMDTO mdto = new WifiHandsetDeviceRelationMDTO(wifiId, handsetId);

		WifiHandsetDeviceRelationMDTO wifiHandsetDeviceRelationMDTO = wifiHandsetDeviceRelationMDao.findById(mdto.getId());

		Map<String, List<WifiHandsetDeviceItemDetailMDTO>>
				wifiHandsetDeviceItemDetailMTDTOMap = wifiHandsetDeviceRelationMDTO.getItems();

		List<String> week = getSevenDateOfWeek(0);

//		Map<String, List<WifiHandsetDeviceItemDetailMDTO>> result = wifiHandsetDeviceRelationMService.updateOfflineWifiHandsetDeviceItems(
//				wifiHandsetDeviceItemDetailMTDTOMap, week, new Date().getTime() , "2015-08-01 13:36:22");

//		System.out.println(result);
	}

	public static List<String> getSevenDateOfWeek(int before)
	{
		List<String> week = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, before);
		week.add(DateTimeHelper.formatDate(calendar.getTime(), "yyyy-MM-dd"));
		int i = 0;
		while (i < 6)
		{
			calendar.add(Calendar.DATE, -1);
			week.add(DateTimeHelper.formatDate(calendar.getTime(), "yyyy-MM-dd"));
			i++;
		}
		return week;
	}


}
