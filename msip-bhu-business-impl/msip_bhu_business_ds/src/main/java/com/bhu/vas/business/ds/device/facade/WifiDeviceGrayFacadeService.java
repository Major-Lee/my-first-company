package com.bhu.vas.business.ds.device.facade;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.GrayLevel;
import com.bhu.vas.api.rpc.devices.dto.DeviceVersion;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceGray;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceGrayVersion;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceVersionFW;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceVersionOM;
import com.bhu.vas.api.rpc.devices.model.pk.WifiDeviceGrayVersionPK;
import com.bhu.vas.api.rpc.user.dto.UpgradeDTO;
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
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

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
    	mc_fw.createCriteria().andColumnEqualTo("dut", dut.getIndex()).andSimpleCaulse(" 1=1 ");
    	mc_fw.setPageNumber(1);
    	mc_fw.setPageSize(20);
    	mc_fw.setOrderByClause(" created_at desc ");
    	List<WifiDeviceVersionFW> versionfws = wifiDeviceVersionFWService.findModelByModelCriteria(mc_fw);
    	for(WifiDeviceVersionFW fw:versionfws){
    		vto.getFws().add(fw.toVersionVTO());
    	}
    	ModelCriteria mc_om = new ModelCriteria();
    	mc_om.createCriteria().andColumnEqualTo("dut", dut.getIndex()).andSimpleCaulse(" 1=1 ");
    	mc_om.setPageNumber(1);
    	mc_om.setPageSize(20);
    	mc_om.setOrderByClause(" created_at desc ");
    	List<WifiDeviceVersionOM> versionoms = wifiDeviceVersionOMService.findModelByModelCriteria(mc_om);
    	for(WifiDeviceVersionOM om:versionoms){
    		vto.getOms().add(om.toVersionVTO());
    	}
    	return vto;
    }
    
    public TailPage<VersionVTO> pagesFW(VapEnumType.DeviceUnitType dut,int pn,int ps){
    	ModelCriteria mc_fw = new ModelCriteria();
    	mc_fw.createCriteria().andColumnEqualTo("dut", dut.getIndex()).andSimpleCaulse(" 1=1 ");
    	mc_fw.setPageNumber(pn);
    	mc_fw.setPageSize(ps);
    	mc_fw.setOrderByClause(" created_at desc ");
		TailPage<WifiDeviceVersionFW> pages = this.wifiDeviceVersionFWService.findModelTailPageByModelCriteria(mc_fw);
		List<VersionVTO> vtos = new ArrayList<>();
		for(WifiDeviceVersionFW fm:pages.getItems()){
			vtos.add(fm.toVersionVTO());
		}
		TailPage<VersionVTO> result_pages = new CommonPage<VersionVTO>(pages.getPageNumber(), pages.getPageSize(), pages.getTotalItemsCount(), vtos);
    	return result_pages;
    }
    
    public TailPage<VersionVTO> pagesOM(VapEnumType.DeviceUnitType dut,int pn,int ps){
    	ModelCriteria mc_om = new ModelCriteria();
    	mc_om.createCriteria().andColumnEqualTo("dut", dut.getIndex()).andSimpleCaulse(" 1=1 ");
    	mc_om.setPageNumber(pn);
    	mc_om.setPageSize(ps);
    	mc_om.setOrderByClause(" created_at desc ");
		TailPage<WifiDeviceVersionOM> pages = this.wifiDeviceVersionOMService.findModelTailPageByModelCriteria(mc_om);
		List<VersionVTO> vtos = new ArrayList<>();
		for(WifiDeviceVersionOM om:pages.getItems()){
			vtos.add(om.toVersionVTO());
		}
		TailPage<VersionVTO> result_pages = new CommonPage<VersionVTO>(pages.getPageNumber(), pages.getPageSize(), pages.getTotalItemsCount(), vtos);
    	return result_pages;
    }
    
    /**
     * 增加指定的macs 到相对应的产品类型中的灰度中
     * 如果某个mac已经在灰度中，则需要判定 其本身的原来的灰度产品类型是否和新的灰度产品类型匹配 
     * 			匹配则强制更改其灰度等级
     * 			否则抛出异常灰度的产品类型不匹配
     * @param dut
     * @param gray
     * @param macs
     */
    public void saveMacs2Gray(VapEnumType.DeviceUnitType dut,VapEnumType.GrayLevel gray,List<String> macs){
    	validateDut(dut);
    	validateGrayEnalbe(gray);
    	for(String mac:macs){
    		WifiDeviceGray wdg = wifiDeviceGrayService.getById(mac);
    		if(wdg == null){
    			wdg = new WifiDeviceGray();
    			wdg.setId(mac);
    			wdg.setDut(dut.getIndex());
    			wdg.setGl(gray.getIndex());
    			wifiDeviceGrayService.insert(wdg);
    			WifiDeviceGrayVersion dgv = wifiDeviceGrayVersionService.getById(new WifiDeviceGrayVersionPK(dut.getIndex(),gray.getIndex()));
    			dgv.setDevices(dgv.getDevices()+1);
    			wifiDeviceGrayVersionService.update(dgv);
    		}else{
    			if(wdg.getDut() == dut.getIndex() && wdg.getGl() == gray.getIndex()){//产品类型和灰度等级没有变更
    				;
    			}else{
    				if(wdg.getDut() == dut.getIndex()){
    					wdg.setDut(dut.getIndex());
    	    			wdg.setGl(gray.getIndex());
    	    			wifiDeviceGrayService.update(wdg);
    	    			WifiDeviceGrayVersion dgv = wifiDeviceGrayVersionService.getById(new WifiDeviceGrayVersionPK(wdg.getDut(),wdg.getGl()));
    	    			dgv.setDevices(dgv.getDevices()-1);
    	    			wifiDeviceGrayVersionService.update(dgv);
    	    			wdg.setDut(dut.getIndex());
    	    			wdg.setGl(gray.getIndex());
    	    			wifiDeviceGrayService.update(wdg);
    	    			dgv = wifiDeviceGrayVersionService.getById(new WifiDeviceGrayVersionPK(dut.getIndex(),gray.getIndex()));
    	    			dgv.setDevices(dgv.getDevices()+1);
    	    			wifiDeviceGrayVersionService.update(dgv);
    				}else{
    					throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_GRAY_DeviceUnitType_NOTMATCHED);
    				}
    			}
    		}
    	}
    }
    
    /**
     * 变更指定产品类型的灰度关联的固件版本号和增值组件版本号
     * @param dut
     * @param gray
     * @param fwid
     * @param omid
     */
    public GrayUsageVTO modifyRelatedVersion4GrayVersion(VapEnumType.DeviceUnitType dut,VapEnumType.GrayLevel gray,
    		String fwid,String omid){
    	validateDut(dut);
    	validateGrayEnalbe(gray);
    	WifiDeviceGrayVersion dgv = wifiDeviceGrayVersionService.getById(new WifiDeviceGrayVersionPK(dut.getIndex(),gray.getIndex()));
    	if(dgv == null){
    		throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{"WifiDeviceGrayVersion"});
    	}
    	if(dgv.getD_fwid().equals(fwid) && dgv.getD_omid().equals(omid)){
    		return dgv.toGrayUsageVTO();
    	}
    	if(!dgv.getD_fwid().equals(fwid)){
    		WifiDeviceVersionFW dvfw = wifiDeviceVersionFWService.getById(fwid);
        	if(dvfw == null || dvfw.getDut() != dut.getIndex()){
        		throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{"WifiDeviceVersionFW"});
        	}
        	dvfw.setRelated(true);
        	wifiDeviceVersionFWService.update(dvfw);
        	dgv.setD_fwid(fwid);
    	}
    	
    	if(!dgv.getD_omid().equals(omid)){
    		WifiDeviceVersionOM dvom = wifiDeviceVersionOMService.getById(omid);
        	if(dvom == null || dvom.getDut() != dut.getIndex()){
        		throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{"WifiDeviceVersionOM"});
        	}
        	dvom.setRelated(true);
        	wifiDeviceVersionOMService.update(dvom);
        	dgv.setD_omid(omid);
    	}
    	dgv = wifiDeviceGrayVersionService.update(dgv);
    	return dgv.toGrayUsageVTO();
    }
    
    /**
     * 增加指定产品类型的灰度关联的固件版本号和增值组件版本号定义
     * @param fw
     * @param dut
     * @param versionid
     * @param upgrade_url
     */
    public VersionVTO addDeviceVersion(VapEnumType.DeviceUnitType dut,boolean fw,String versionid,String upgrade_url){
    	validateDut(dut);
    	if(StringUtils.isEmpty(versionid) || StringUtils.isEmpty(upgrade_url)) 
    		throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
    	if(fw){
    		WifiDeviceVersionFW versionfw = wifiDeviceVersionFWService.getById(versionid);
    		if(versionfw != null){
    			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_ALREADYEXIST,new String[]{"WifiDeviceVersionFW"});
    		}
    		versionfw = new WifiDeviceVersionFW();
    		versionfw.setId(versionid);
    		versionfw.setName(versionid);
    		versionfw.setDut(dut.getIndex());
    		versionfw.setUpgrade_url(upgrade_url);
    		versionfw = wifiDeviceVersionFWService.insert(versionfw);
    		return versionfw.toVersionVTO();
    	}else{
    		WifiDeviceVersionOM versionom = wifiDeviceVersionOMService.getById(versionid);
    		if(versionom != null){
    			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_ALREADYEXIST,new String[]{"WifiDeviceVersionOM"});
    		}
    		versionom = new WifiDeviceVersionOM();
    		versionom.setId(versionid);
    		versionom.setName(versionid);
    		versionom.setDut(dut.getIndex());
    		versionom.setUpgrade_url(upgrade_url);
    		versionom = wifiDeviceVersionOMService.insert(versionom);
    		return versionom.toVersionVTO();
    	}
    }
    
    /**
     * 清除macs 从灰度列表中
     * @param macs
     */
    public void cleanMacsFromAnyGray(List<String> macs){
    	if(macs != null && !macs.isEmpty())
    		wifiDeviceGrayService.deleteByIds(macs);
    }
    
    /**
     * 取出设备对应的灰度gl及相关的产品型号dut
     * @param mac
     * @return null情况下属于未知
     */
    public WifiDeviceGrayVersionPK deviceUnitGray(String mac){
    	WifiDeviceGray deviceGray = wifiDeviceGrayService.getById(mac);
    	if(deviceGray == null){
    		return null;
    	}else{
    		return new WifiDeviceGrayVersionPK(deviceGray.getDut(),deviceGray.getGl());
    	}
    }

    /**
     * 设备类型灰度动作定义
     * 用户判定设备是否需要升级的业务
     * TODO：目前先uRouter TU的设备，其他产品型号的稍后支持
     * TODO：目前先考虑设备固件版本，增值模块版本比对升级稍后增加
     * 备注：如果设备属于特殊灰度 
     * @param dmac 设备的mac地址 UpgradeDTO中的forceDeviceUpgrade强制false
     * @return
     */
    public UpgradeDTO deviceUpgradeAutoAction(String dmac,String d_orig_swver){
    	WifiDeviceGrayVersionPK deviceUnitGrayPk = this.deviceUnitGray(dmac);
    	int dut = 0;
    	int gl = 0;
		if(deviceUnitGrayPk == null){//不在灰度等级中，则采用缺省的 其他定义
			//获取d_orig_swver中的dut
			DeviceVersion dvparser = DeviceVersion.parser(d_orig_swver);
			if(dvparser.wasDutURouter()){
				dut = VapEnumType.DeviceUnitType.uRouterTU.getIndex();
				gl = VapEnumType.GrayLevel.Other.getIndex();
			}
		}else{
			dut = deviceUnitGrayPk.getDut();
			gl = deviceUnitGrayPk.getGl();
		}
    	return upgradeDecideAction(dmac,dut,gl,d_orig_swver);
    }
    
    private UpgradeDTO upgradeDecideAction(String dmac,int dut,int gl,String d_orig_swver){
    	System.out.println(String.format("A upgradeDecideAction dmac[%s] dut[%s] gl[%s] d_orig_swver[%s]",dmac,dut,gl,d_orig_swver));
    	UpgradeDTO resultDto = null;
    	GrayLevel grayLevel = VapEnumType.GrayLevel.fromIndex(gl);
    	try{
    		//灰度不存在或者无效的灰度\特殊灰度，UpgradeDTO中的forceDeviceUpgrade强制false
    		validateGrayEnalbe4Upgrade(grayLevel);
    	}catch(BusinessI18nCodeException i18nex){
    		resultDto = new UpgradeDTO(dut,gl,true,false);
    		resultDto.setDesc("灰度不存在、无效的灰度或者属于特殊灰度等级");
    		System.out.println("A1 upgradeDecideAction exception:"+resultDto);
    		return resultDto;
    	}
    	/*if(grayLevel == null || !grayLevel.isEnable() || grayLevel == VapEnumType.GrayLevel.Special){//灰度不存在或者特殊灰度，UpgradeDTO中的forceDeviceUpgrade强制false
    		resultDto = new UpgradeDTO(dut,gl,true,false);
    		resultDto.setDesc("灰度不存在、无效的灰度或者属于特殊灰度等级");
    		return resultDto;
    	}*/
    	WifiDeviceGrayVersion grayVersion = wifiDeviceGrayVersionService.getById(new WifiDeviceGrayVersionPK(dut,gl));
		if(grayVersion != null){
			int ret = DeviceVersion.compareVersions(d_orig_swver, grayVersion.getD_fwid());
			if(ret == -1){
				WifiDeviceVersionFW versionfw = wifiDeviceVersionFWService.getById(grayVersion.getD_fwid());
				if(versionfw != null){
					resultDto = new UpgradeDTO(dut,gl,true,true,
							grayVersion.getD_fwid(),versionfw.getUpgrade_url());
					System.out.println("B1 upgradeDecideAction:"+resultDto);
				}else{
					System.out.println("B2 upgradeDecideAction versionfw 未定义！");
				}
			}else{
				System.out.println(String.format("B3 upgradeDecideAction 版本比对中设备版本[%s]大于等于灰度定义版本[%s]",d_orig_swver,grayVersion.getD_fwid()));
			}
		}else{
			System.out.println("C upgradeDecideAction grayVersion 未定义！");
		}
		return resultDto;
    }
    
    
    private boolean validateDut(VapEnumType.DeviceUnitType dut){
    	if(dut == null) throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
    	return true;
    }
    private boolean validateGrayEnalbe(VapEnumType.GrayLevel gray){
    	if(gray == null) throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
    	if(!gray.isEnable()){
    		throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL);
    	}
    	return true;
    }
    private boolean validateGrayEnalbe4Upgrade(VapEnumType.GrayLevel gray){
    	if(gray == null) throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
    	if(!gray.isEnable()){
    		throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL);
    	}
    	if(gray == VapEnumType.GrayLevel.Special){
    		throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL);
    	}
    	return true;
    }
	public WifiDeviceService getWifiDeviceService() {
		return wifiDeviceService;
	}

	public WifiDeviceGrayService getWifiDeviceGrayService() {
		return wifiDeviceGrayService;
	}

	public WifiDeviceGrayVersionService getWifiDeviceGrayVersionService() {
		return wifiDeviceGrayVersionService;
	}

	public WifiDeviceVersionFWService getWifiDeviceVersionFWService() {
		return wifiDeviceVersionFWService;
	}

	public WifiDeviceVersionOMService getWifiDeviceVersionOMService() {
		return wifiDeviceVersionOMService;
	}

}
