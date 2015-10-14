package com.bhu.vas.business.device;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.localunit.BaseTest;
import com.smartwork.msip.localunit.RandomPicker;

/**
 * 模拟给wifi设备添加地理位置信息
 * @author tangzichao
 *
 */
public class WifiDeviceSimulateCoordinateTest extends BaseTest{
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private DeviceFacadeService deviceFacadeService;
	
	private static final String[] Beijings = {"39.93,116.40","39.92,116.33","39.88,116.29","39.81,116.30","39.76,116.38"};
	private static final String[] Shanghai = {"31.23,121.47","31.23,121.44","31.24,121.48","31.19,121.49","31.18,121.44"};
	private static final String[] Guangdong = {"22.22,112.40","25.09,113.21","23.42,113.01","23.21,112.41","22.15,112.48"};
	private static final String[] Zhejiang = {"29.39,121.24","30.03,119.57","30.32,120.42","30.52,120.06","29.29,119.16"};
	//private static final String[] others = {"38.91,-77.04","40.71,-74.00","41.90,12.49","46.19,6.14","1.97,73.53"};
	private static final String[] others = {"43.80,87.59","25.59,100.25","30.67,104.05","41.85,123.41","36.08,120.36"};
	private static final String[][] alls = {Beijings,Shanghai,Guangdong,Zhejiang,others};
	
	@Test
	public void simulateCoordinate(){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnIsNull("country");
    	mc.setPageNumber(1);
    	mc.setPageSize(500);
		EntityIterator<String, WifiDevice> it = new KeyBasedEntityBatchIterator<String,WifiDevice>(String.class,WifiDevice.class, wifiDeviceService.getEntityDao(), mc);
		while(it.hasNext()){
			List<WifiDevice> entitys = it.next();
			for(WifiDevice entity : entitys){
				try{
					if(resetRegions(entity)){
						wifiDeviceService.update(entity);
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
	}
	
	public boolean resetRegions(WifiDevice entity){
		if(StringUtils.isEmpty(entity.getLat()) && StringUtils.isEmpty(entity.getLon())){
			String[] random_regions = RandomPicker.pick(alls);
			String random_detail_region = RandomPicker.pick(random_regions);
			String[] coordinate = random_detail_region.split(",");
			entity.setLat(coordinate[0]);
			entity.setLon(coordinate[1]);
			return deviceFacadeService.wifiDeiviceGeocoding(entity);
		}
		return false;
	}
}
