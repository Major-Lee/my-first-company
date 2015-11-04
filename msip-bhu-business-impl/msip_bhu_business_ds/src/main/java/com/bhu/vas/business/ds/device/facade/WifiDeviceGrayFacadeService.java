package com.bhu.vas.business.ds.device.facade;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceGray;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceGrayVersion;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceVersionFW;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceVersionOM;
import com.bhu.vas.api.vto.device.CurrentGrayUsageVTO;
import com.bhu.vas.api.vto.device.DeviceUnitTypeVTO;
import com.bhu.vas.api.vto.device.GrayUsageVTO;
import com.bhu.vas.api.vto.device.VersionVTO;
import com.bhu.vas.business.ds.device.service.WifiDeviceGrayService;
import com.bhu.vas.business.ds.device.service.WifiDeviceGrayVersionService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceVersionFWService;
import com.bhu.vas.business.ds.device.service.WifiDeviceVersionOMService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.TailPage;

/**
 * Created by bluesand on 8/4/15.
 *
 * 避免 wifiDeviceGroupService 与 wifiDeviceGroupRelationService 同级service相互调用
 */
@Service
public class WifiDeviceGrayFacadeService {

    @Resource
    private WifiDeviceService wifiDeviceService;
    
    @Resource
    private WifiDeviceGrayService wifiDeviceGrayService;

    @Resource
    private WifiDeviceGrayVersionService wifiDeviceGrayVersionService;
    
    @Resource
    private WifiDeviceVersionFWService wifiDeviceVersionFWService;
    
    @Resource
    private WifiDeviceVersionOMService wifiDeviceVersionOMService;

    /**
     * 返回设备型号列表 root，root里面带有childen
     * 用于运营平台设备信息-左栏-产品类型列表
     * @return
     */
    public List<DeviceUnitTypeVTO> deviceUnitTypes(){
    	return VapEnumType.DeviceUnitType.getAllDeviceUnitTypeVTO();
    }
    
    /**
     * 返回当前产品类型的灰度列表 及具体灰度的应用固件版本号和增值运营版本号
     * 用于运营平台设备信息-右栏-当前灰度列表及固件版本列表和运营版本列表
     * @return
     */
    public CurrentGrayUsageVTO currentGrays(VapEnumType.DeviceUnitType dut){
    	CurrentGrayUsageVTO vto = new CurrentGrayUsageVTO();
    	vto.setGuvs(new ArrayList<GrayUsageVTO>());
    	vto.setFws(new ArrayList<VersionVTO>());
    	vto.setOms(new ArrayList<VersionVTO>());
    	ModelCriteria mc_dgv = new ModelCriteria();
    	mc_dgv.createCriteria().andColumnEqualTo("dut", dut.getIndex()).andSimpleCaulse(" 1=1 ");
    	mc_dgv.setPageNumber(1);
    	mc_dgv.setPageSize(20);
    	mc_dgv.setOrderByClause(" gl asc ");
    	List<WifiDeviceGrayVersion> deviceGrayVersions = wifiDeviceGrayVersionService.findModelByModelCriteria(mc_dgv);
    	for(WifiDeviceGrayVersion dgv:deviceGrayVersions){
    		vto.getGuvs().add(dgv.toGrayUsageVTO());
    	}
    	ModelCriteria mc_fw = new ModelCriteria();
    	mc_fw.createCriteria().andSimpleCaulse(" 1=1 ");
    	mc_fw.setPageNumber(1);
    	mc_fw.setPageSize(50);
    	mc_fw.setOrderByClause(" created_at desc ");
    	List<WifiDeviceVersionFW> versionfws = wifiDeviceVersionFWService.findModelByModelCriteria(mc_fw);
    	for(WifiDeviceVersionFW fw:versionfws){
    		vto.getFws().add(fw.toVersionVTO());
    	}
    	ModelCriteria mc_om = new ModelCriteria();
    	mc_om.createCriteria().andSimpleCaulse(" 1=1 ");
    	mc_om.setPageNumber(1);
    	mc_om.setPageSize(50);
    	mc_om.setOrderByClause(" created_at desc ");
    	List<WifiDeviceVersionOM> versionoms = wifiDeviceVersionOMService.findModelByModelCriteria(mc_om);
    	for(WifiDeviceVersionOM om:versionoms){
    		vto.getFws().add(om.toVersionVTO());
    	}
    	return vto;
    }
    
    public TailPage<VersionVTO> pagesFW(VapEnumType.DeviceUnitType dut){
    	return null;
    }
    
    public TailPage<VersionVTO> pagesOM(VapEnumType.DeviceUnitType dut){
    	return null;
    }
    
    public void addMacs2Gray(VapEnumType.GrayLevel gray,List<String> macs){
    	
    }
    
    public void cleanMacsInGray(VapEnumType.GrayLevel gray,List<String> macs){
    	
    }
    
    /**
     * 取出设备对应的灰度
     * @param mac
     * @return null情况下属于未知
     */
    public VapEnumType.GrayLevel deviceGray(String mac){
    	WifiDeviceGray deviceGray = wifiDeviceGrayService.getById(mac);
    	if(deviceGray == null){
    		return VapEnumType.GrayLevel.Unknow;
    	}else{
    		return VapEnumType.GrayLevel.fromIndex(deviceGray.getGl());
    	}
    }

}
