package com.bhu.vas.business.device;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.rpc.devicegroup.model.WifiDeviceBackendTask;
import com.bhu.vas.business.ds.devicegroup.service.WifiDeviceBackendTaskService;
import com.ibm.icu.text.SimpleDateFormat;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.localunit.BaseTest;

public class WifiDeviceBackendTaskTest extends BaseTest {
	@Resource
	WifiDeviceBackendTaskService wifiDeviceBackendTaskService;
	
	//@Test
	public void test001() throws Exception {
		WifiDeviceBackendTask bean = new WifiDeviceBackendTask();
		bean.setGid(999L);
		bean.setCurrent(1);
		bean.setStarted_at(new Date());
		bean = wifiDeviceBackendTaskService.insert(bean);
		System.out.println(bean+"success...");
	}

	// @Test
	public void test002() {
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("id", 10000);
		List<WifiDeviceBackendTask> result = wifiDeviceBackendTaskService.findModelByModelCriteria(mc);
		System.out.println(result.get(0).getGid());
	}

	//@Test
	public void test003() {
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("gid", 1);
		List<WifiDeviceBackendTask> result = wifiDeviceBackendTaskService.findModelByModelCriteria(mc);

		WifiDeviceBackendTask bean = result.get(0);
		bean.setCurrent(bean.getCurrent()+500);
		wifiDeviceBackendTaskService.update(bean);
		System.out.println("更新成功");
	}

	//@Test
	public void test004() throws Exception {
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("gid", 1);
		List<WifiDeviceBackendTask> result = wifiDeviceBackendTaskService.findModelByModelCriteria(mc);

		WifiDeviceBackendTask bean = result.get(0);
		bean.setState(WifiDeviceBackendTask.State_Timeout);
		
		System.out.println("更新成功");
	}

	@Test
	public void test005() {
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("state", "completed");
		List<WifiDeviceBackendTask> result = wifiDeviceBackendTaskService.findModelByModelCriteria(mc);
		System.out.println(result.get(0).getId());
		System.out.println(result.size());
		System.out.println("success~~~");
	}
	//@Test
	public void test006(){
		Long id = 10003L;
		WifiDeviceBackendTask bean = wifiDeviceBackendTaskService.getById(id);
		System.out.println(bean.getCurrent());
	}
	
	//@Test
	public void test007() {
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse("1=1");
		System.out.println("ready");
		List<WifiDeviceBackendTask> result = wifiDeviceBackendTaskService.findModelByModelCriteria(mc);
		System.out.println(result.size());
	}
	
}