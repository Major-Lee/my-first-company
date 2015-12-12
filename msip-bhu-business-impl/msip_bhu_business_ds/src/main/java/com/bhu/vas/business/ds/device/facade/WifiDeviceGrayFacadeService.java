package com.bhu.vas.business.ds.device.facade;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.DownCmds;
import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.DeviceUnitType;
import com.bhu.vas.api.helper.VapEnumType.GrayLevel;
import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.bhu.vas.api.rpc.devices.dto.DeviceOMVersion;
import com.bhu.vas.api.rpc.devices.dto.DeviceVersion;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
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
import com.bhu.vas.business.ds.device.service.WifiDeviceModuleService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceVersionFWService;
import com.bhu.vas.business.ds.device.service.WifiDeviceVersionOMService;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
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
    private WifiDeviceModuleService wifiDeviceModuleService;
    
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
    	vto.getFws().add(WifiDeviceVersionFW.toEmptyVTO());
    	vto.setOms(new ArrayList<VersionVTO>());
    	vto.getOms().add(WifiDeviceVersionOM.toEmptyVTO());
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
     * @param gray 如果gray为其他，则代表从灰度中移除mac地址
     * @param macs
     */
    public List<String> saveMacs2Gray(VapEnumType.DeviceUnitType dut,VapEnumType.GrayLevel gray,List<String> macs){
    	validateDut(dut);
    	validateGrayEnalbe(gray);
    	List<WifiDevice> devices = wifiDeviceService.findByIds(macs);
    	if(devices == null || devices.isEmpty()){
    		throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_DATA_NOT_EXIST);
    	}
    	List<String> result_success = new ArrayList<String>();
    	for(WifiDevice device:devices){
    		String mac = device.getId();
    		WifiDeviceGray wdg = wifiDeviceGrayService.getById(mac);
    		DeviceVersion parser = DeviceVersion.parser(device.getOrig_swver());
    		if(!parser.valid()) {
    			System.out.println(device.getOrig_swver()+" invalid");
    			continue;
    		}
    		if(!dut.getIndex().equals(parser.toDeviceUnitTypeIndex())){
    			System.out.println("mac:"+mac+" dut:"+dut.getIndex() +" ver:"+parser.toDeviceUnitTypeIndex());
    			continue;
    		}
    		if(GrayLevel.Other == gray){
    			wifiDeviceGrayService.deleteById(mac);//.deleteByIds(ids);
    		}else{
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
        			if(wdg.getDut().equals(dut.getIndex()) && wdg.getGl() == gray.getIndex()){//产品类型和灰度等级没有变更
        				;
        			}else{
        				if(wdg.getDut().equals(dut.getIndex())){
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
        					continue;
        					//throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_GRAY_DeviceUnitType_NOTMATCHED);
        				}
        			}
        		}
        		
    		}
    		result_success.add(device.getId());
    	}
    	return result_success;
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
    	
    	//if(StringHelper.MINUS_STRING_GAP.equals(fwid)) fwid = StringHelper.EMPTY_STRING_GAP;
    	//if(StringHelper.MINUS_STRING_GAP.equals(omid)) omid = StringHelper.EMPTY_STRING_GAP;
    	if(StringUtils.isNotEmpty(fwid) && !StringHelper.MINUS_STRING_GAP.equals(fwid)){
    		this.validateVersionFormat(fwid, true);
    	}
    	if(StringUtils.isNotEmpty(omid) && !StringHelper.MINUS_STRING_GAP.equals(omid))
    		this.validateVersionFormat(omid, false);
    	
    	WifiDeviceGrayVersion dgv = wifiDeviceGrayVersionService.getById(new WifiDeviceGrayVersionPK(dut.getIndex(),gray.getIndex()));
    	if(dgv == null){
    		throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{"WifiDeviceGrayVersion"});
    	}
    	if(dgv.getD_fwid().equals(fwid) && dgv.getD_omid().equals(omid)){
    		return dgv.toGrayUsageVTO();
    	}
    	if(!dgv.getD_fwid().equals(fwid)){
    		if(StringUtils.isNotEmpty(fwid) && !StringHelper.MINUS_STRING_GAP.equals(fwid)){
    			WifiDeviceVersionFW dvfw = wifiDeviceVersionFWService.getById(fwid);
            	if(dvfw == null || !dvfw.getDut().equals(dut.getIndex()) ){
            		throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{"WifiDeviceVersionFW"});
            	}
            	dvfw.setRelated(true);
            	wifiDeviceVersionFWService.update(dvfw);
    		}
        	dgv.setD_fwid(fwid);
    	}
    	
    	if(!dgv.getD_omid().equals(omid)){
    		if(StringUtils.isNotEmpty(omid) && !StringHelper.MINUS_STRING_GAP.equals(omid)){
	    		WifiDeviceVersionOM dvom = wifiDeviceVersionOMService.getById(omid);
	        	if(dvom == null || !dvom.getDut().equals(dut.getIndex())){
	        		throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{"WifiDeviceVersionOM"});
	        	}
	        	dvom.setRelated(true);
	        	wifiDeviceVersionOMService.update(dvom);
    		}
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
    	this.validateVersionFormat(versionid, fw);
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
     * 删除指定设备类型的固件或增值组件版本
     * @param dut
     * @param fw
     * @param versionid
     * @return
     */
    public VersionVTO removeDeviceVersion(VapEnumType.DeviceUnitType dut,boolean fw,String versionid){
    	validateDut(dut);
    	if(StringUtils.isEmpty(versionid)) 
    		throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
    	this.validateVersionFormat(versionid, fw);
    	if(fw){
    		WifiDeviceVersionFW versionfw = wifiDeviceVersionFWService.getById(versionid);
    		if(versionfw == null){
    			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{"WifiDeviceVersionFW",versionid});
    		}
    		if(versionfw.isRelated()){
    			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_OPERATION_CANNOT_EXECUTE);
    		}
    		wifiDeviceVersionFWService.deleteById(versionid);
    		return versionfw.toVersionVTO();
    	}else{
    		WifiDeviceVersionOM versionom = wifiDeviceVersionOMService.getById(versionid);
    		if(versionom == null){
    			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{"WifiDeviceVersionOM",versionid});
    		}
    		if(versionom.isRelated()){
    			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_OPERATION_CANNOT_EXECUTE);
    		}
    		wifiDeviceVersionOMService.deleteById(versionid);
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
    public UpgradeDTO deviceFWUpgradeAutoAction(String dmac,String d_version){
    	WifiDeviceGrayVersionPK deviceUnitGrayPk = this.deviceUnitGray(dmac);
    	String dut = null;
    	int gl = 0;
		if(deviceUnitGrayPk == null){//不在灰度等级中，则采用缺省的 其他定义
			//获取d_version中的dut
			DeviceVersion dvparser = DeviceVersion.parser(d_version);
			DeviceUnitType dutype = VapEnumType.DeviceUnitType.fromVersionPrefix(dvparser.getDut(), dvparser.getPrefix());//.fromIndex(dvparser.getPrefix(x));//Integer.parseInt(dvparser.getHdt()));
			if(dutype != null){
				dut = dutype.getIndex();
			}else{
				System.out.println(String.format("unable catch the device unitype from:[%s] fw[%s] for[%s]", d_version,WifiDeviceHelper.WIFI_DEVICE_UPGRADE_FW,dmac));
				return null;
			}
			gl = VapEnumType.GrayLevel.Other.getIndex();
		}else{
			dut = deviceUnitGrayPk.getDut();
			gl = deviceUnitGrayPk.getGl();
		}
    	return upgradeDecideAction(dmac,dut,gl,d_version,WifiDeviceHelper.WIFI_DEVICE_UPGRADE_FW);
    }
    
    public WifiDeviceGrayVersionPK determineDeviceGray(String dmac,String d_version){
    	WifiDeviceGrayVersionPK deviceUnitGrayPk = this.deviceUnitGray(dmac);
		if(deviceUnitGrayPk == null){//不在灰度等级中，则采用缺省的 其他定义
			deviceUnitGrayPk = new WifiDeviceGrayVersionPK();
			//获取d_version中的dut
			DeviceVersion dvparser = DeviceVersion.parser(d_version);
			DeviceUnitType dutype = VapEnumType.DeviceUnitType.fromVersionPrefix(dvparser.getDut(), dvparser.getPrefix());//.fromIndex(dvparser.getPrefix(x));//Integer.parseInt(dvparser.getHdt()));
			if(dutype != null){
				deviceUnitGrayPk.setDut(dutype.getIndex());
			}else{
				deviceUnitGrayPk.setDut(null);
			}
			deviceUnitGrayPk.setGl(VapEnumType.GrayLevel.Other.getIndex());
		}/*else{
			return deviceUnitGrayPk;
		}*/
		return deviceUnitGrayPk;
    }
    
    
    
    public UpgradeDTO deviceOMUpgradeAutoAction(String dmac,String d_version,String d_om_version){
    	WifiDeviceGrayVersionPK deviceUnitGrayPk = this.deviceUnitGray(dmac);
    	String dut = null;
    	int gl = 0;
		if(deviceUnitGrayPk == null){//不在灰度等级中，则采用缺省的 其他定义
			//获取d_version中的dut
			DeviceVersion dvfmparser = DeviceVersion.parser(d_version);
			DeviceOMVersion dvomparser = DeviceOMVersion.parser(d_om_version);
			DeviceUnitType dutype = VapEnumType.DeviceUnitType.fromHdType(dvfmparser.getDut(),dvomparser.getVp());
			if(dutype != null){
				dut = dutype.getIndex();
			}else{
				System.out.println(String.format("unable catch the device unitype from:[%s] fw[%s] for[%s]", d_version,WifiDeviceHelper.WIFI_DEVICE_UPGRADE_OM,dmac));
				return null;
			}
			gl = VapEnumType.GrayLevel.Other.getIndex();
		}else{
			dut = deviceUnitGrayPk.getDut();
			gl = deviceUnitGrayPk.getGl();
		}
    	return upgradeDecideAction(dmac,dut,gl,d_om_version,WifiDeviceHelper.WIFI_DEVICE_UPGRADE_OM);
    }
    
    /*public UpgradeDTO deviceUpgradeAutoAction(String dmac,String d_version,boolean fw){
    	WifiDeviceGrayVersionPK deviceUnitGrayPk = this.deviceUnitGray(dmac);
    	String dut = null;
    	int gl = 0;
		if(deviceUnitGrayPk == null){//不在灰度等级中，则采用缺省的 其他定义
			//获取d_version中的dut
			if(WifiDeviceHelper.WIFI_DEVICE_UPGRADE_FW == fw){
				DeviceVersion dvparser = DeviceVersion.parser(d_version);
				DeviceUnitType dutype = VapEnumType.DeviceUnitType.fromVersionPrefix(dvparser.getDut(), dvparser.getPrefix());//.fromIndex(dvparser.getPrefix(x));//Integer.parseInt(dvparser.getHdt()));
				if(dutype != null){
					dut = dutype.getIndex();
				}else{
					System.out.println(String.format("unable catch the device unitype from:[%s] fw[%s] for[%s]", d_version,fw,dmac));
					return null;
				}
				gl = VapEnumType.GrayLevel.Other.getIndex();
			}else{
				DeviceOMVersion dvparser = DeviceOMVersion.parser(d_version);
				DeviceUnitType dutype = VapEnumType.DeviceUnitType.fromIndex(Integer.parseInt(dvparser.getHdt()));
				if(dutype != null){
					dut = dutype.getIndex();
				}else{
					System.out.println(String.format("unable catch the device unitype from:[%s] fw[%s] for[%s]", d_version,fw,dmac));
					return null;
				}
				gl = VapEnumType.GrayLevel.Other.getIndex();
			}
		}else{
			dut = deviceUnitGrayPk.getDut();
			gl = deviceUnitGrayPk.getGl();
		}
    	return upgradeDecideAction(dmac,dut,gl,d_version,fw);
    }*/
    
    private UpgradeDTO upgradeDecideAction(String dmac,String dut,int gl,String d_version,boolean fw){
    	System.out.println(String.format("A upgradeDecideAction dmac[%s] dut[%s] gl[%s] d_version[%s] fw[%s]",dmac,dut,gl,d_version,fw));
    	UpgradeDTO resultDto = null;
    	GrayLevel grayLevel = VapEnumType.GrayLevel.fromIndex(gl);
    	try{
    		//灰度不存在或者无效的灰度\特殊灰度，UpgradeDTO中的forceDeviceUpgrade强制false
    		validateGrayEnalbe4Upgrade(grayLevel);
    	}catch(BusinessI18nCodeException i18nex){
    		i18nex.printStackTrace(System.out);
    		resultDto = new UpgradeDTO(dut,gl,true,false);
    		resultDto.setDesc(i18nex.getMessage());
    		System.out.println("A1 upgradeDecideAction exception:"+resultDto);
    		return resultDto;
    	}
    	WifiDeviceGrayVersion grayVersion = wifiDeviceGrayVersionService.getById(new WifiDeviceGrayVersionPK(dut,gl));
		if(grayVersion != null){
			if(WifiDeviceHelper.WIFI_DEVICE_UPGRADE_FW == fw){
				if(StringUtils.isEmpty(grayVersion.getD_fwid()) || StringHelper.MINUS_STRING_GAP.equals(grayVersion.getD_fwid())){
					System.out.println(String.format("A2 upgradeDecideAction du[%s] gl[%s] fwid[%s],return null", dut,gl,grayVersion.getD_fwid()));
					return resultDto;
				}
				
				int ret = DeviceVersion.compareVersions(d_version, grayVersion.getD_fwid());
				if(ret == -1){
					WifiDeviceVersionFW versionfw = wifiDeviceVersionFWService.getById(grayVersion.getD_fwid());
					if(versionfw != null && versionfw.valid()){
						resultDto = new UpgradeDTO(dut,gl,fw,true,
								grayVersion.getD_fwid(),versionfw.getUpgrade_url());
						resultDto.setCurrentDVB(d_version);
						System.out.println("B1 upgradeDecideAction:"+resultDto);
					}else{
						System.out.println(String.format("B2 upgradeDecideAction dmac[%s] fw[%s] versionfw undefined!",dmac,fw));
					}
				}else{
					System.out.println(String.format("B3 upgradeDecideAction dmac[%s] fw[%s] ver compare d_mac_ver[%s] large or equal gray_ver[%s]",dmac,fw,d_version,grayVersion.getD_fwid()));
				}
			}else{
				if(StringUtils.isEmpty(grayVersion.getD_omid()) || StringHelper.MINUS_STRING_GAP.equals(grayVersion.getD_omid())){
					System.out.println(String.format("A3 upgradeDecideAction du[%s] gl[%s] omid[%s],return null", dut,gl,grayVersion.getD_omid()));
					return resultDto;
				}
				int ret = DeviceOMVersion.compareVersions(d_version, grayVersion.getD_omid());
				if(ret == -1){
					WifiDeviceVersionOM versionom = wifiDeviceVersionOMService.getById(grayVersion.getD_omid());
					if(versionom != null && versionom.valid()){
						resultDto = new UpgradeDTO(dut,gl,fw,true,
								grayVersion.getD_omid(),versionom.getUpgrade_url());
						resultDto.setCurrentDVB(d_version);
						System.out.println("B1 upgradeDecideAction:"+resultDto);
					}else{
						System.out.println(String.format("B2 upgradeDecideAction dmac[%s] fw[%s] versionfw undefined!",dmac,fw));
					}
				}else{
					System.out.println(String.format("B3 upgradeDecideAction dmac[%s] fw[%s] ver compare d_mac_ver[%s] large or equal gray_ver[%s]",dmac,fw,d_version,grayVersion.getD_omid()));
				}
			}
		}else{
			System.out.println(String.format("C upgradeDecideAction dmac[%s] grayVersion undefined!",dmac));
		}
		return resultDto;
    }
    
    
    public void updateRelatedDevice4GrayVersion(){
    	ModelCriteria mc_gv = new ModelCriteria();
    	mc_gv.createCriteria().andSimpleCaulse(" 1=1 ");
    	mc_gv.setPageNumber(1);
    	mc_gv.setPageSize(100);
		EntityIterator<WifiDeviceGrayVersionPK, WifiDeviceGrayVersion> it_gv = new KeyBasedEntityBatchIterator<WifiDeviceGrayVersionPK,WifiDeviceGrayVersion>(WifiDeviceGrayVersionPK.class
				,WifiDeviceGrayVersion.class, wifiDeviceGrayVersionService.getEntityDao(), mc_gv);
		while(it_gv.hasNext()){
			List<WifiDeviceGrayVersion> gvs = it_gv.next();
			for(WifiDeviceGrayVersion gv:gvs){
				if(VapEnumType.GrayLevel.Other.getIndex() == gv.getGl()){
					//需要计算出系统总的此种dut的设备总数-在灰度中的数量
					//或者在索引中搜索
					;
				}else{
					ModelCriteria mc_device_gray = new ModelCriteria();
					mc_device_gray.createCriteria().andColumnEqualTo("dut", gv.getDut()).andColumnEqualTo("gl", gv.getGl());
					int relate_count = wifiDeviceGrayService.countByModelCriteria(mc_device_gray);
					gv.setDevices(relate_count);
					wifiDeviceGrayVersionService.update(gv);
				}
			}
		}
    }
    
    /**
     * 固件版本定义表和增值组件版本定义表的关联字段重置
     */
    public void updateRelatedFieldAction(){
    	ModelCriteria mc_fw = new ModelCriteria();
    	mc_fw.createCriteria().andColumnEqualTo("related", 1);
    	mc_fw.setPageNumber(1);
    	mc_fw.setPageSize(100);
		EntityIterator<String, WifiDeviceVersionFW> it_fw = new KeyBasedEntityBatchIterator<String,WifiDeviceVersionFW>(String.class
				,WifiDeviceVersionFW.class, wifiDeviceVersionFWService.getEntityDao(), mc_fw);
		while(it_fw.hasNext()){
			List<WifiDeviceVersionFW> fws = it_fw.next();
			for(WifiDeviceVersionFW fw:fws){
				ModelCriteria mc_grayversion = new ModelCriteria();
				mc_grayversion.createCriteria().andColumnEqualTo("d_fwid", fw.getId());
				int relate_count = wifiDeviceGrayVersionService.countByModelCriteria(mc_grayversion);
				fw.setRelated(relate_count>0);
				wifiDeviceVersionFWService.update(fw);
			}
		}
		
    	ModelCriteria mc_om = new ModelCriteria();
    	mc_om.createCriteria().andColumnEqualTo("related", 1);
    	mc_om.setPageNumber(1);
    	mc_om.setPageSize(100);
		EntityIterator<String, WifiDeviceVersionOM> it_om = new KeyBasedEntityBatchIterator<String,WifiDeviceVersionOM>(String.class
				,WifiDeviceVersionOM.class, wifiDeviceVersionOMService.getEntityDao(), mc_fw);
		while(it_om.hasNext()){
			List<WifiDeviceVersionOM> oms = it_om.next();
			for(WifiDeviceVersionOM om:oms){
				ModelCriteria mc_grayversion = new ModelCriteria();
				mc_grayversion.createCriteria().andColumnEqualTo("d_omid", om.getId());
				int relate_count = wifiDeviceGrayVersionService.countByModelCriteria(mc_grayversion);
				om.setRelated(relate_count>0);
				wifiDeviceVersionOMService.update(om);
			}
		}
		
    }
    
    public List<DownCmds> forceDeviceUpgrade(boolean fw,String versionid, List<String> macs,String beginTime,String endTime){
    	this.validateVersionFormat(versionid, fw);
    	if(macs == null || macs.isEmpty()) 
    		throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR,new String[]{"macs"});
    	
    	List<WifiDevice> devices = wifiDeviceService.findByIds(macs);
    	if(devices == null || devices.isEmpty()) 
    		throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST);
    	List<String> onlineMacs = new ArrayList<String>();
    	for(WifiDevice device:devices){
    		if(device.isOnline()){
    			onlineMacs.add(device.getId());
    		}
    	}
		if(onlineMacs.isEmpty()){
			throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_DATA_NOT_ONLINE,new String[]{""});
		}
    	
    	List<DownCmds> downCmds = new ArrayList<DownCmds>();
    	String upgradeUrl = null;
    	String dut = null;
    	if(fw){
    		WifiDeviceVersionFW versionfw = wifiDeviceVersionFWService.getById(versionid);
    		if(versionfw == null){
    			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{versionid});
    		}
    		dut = versionfw.getDut();
    		upgradeUrl = versionfw.getUpgrade_url();
    		if(StringUtils.isEmpty(beginTime)) beginTime = StringUtils.EMPTY;
    		if(StringUtils.isEmpty(endTime)) endTime = StringUtils.EMPTY;
    	}else{
    		WifiDeviceVersionOM versionom = wifiDeviceVersionOMService.getById(versionid);
    		if(versionom == null){
    			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{versionid});
    		}
    		dut = versionom.getDut();
    		upgradeUrl = versionom.getUpgrade_url();
    	}
    	for(String mac:onlineMacs){
    		UpgradeDTO dto = new UpgradeDTO(fw,true);
    		dto.setDut(dut);
    		dto.setUpgradeurl(upgradeUrl);
    		downCmds.add(DownCmds.builderDownCmds(mac, dto.buildUpgradeCMD(mac, 0l, beginTime, endTime)));
    	}
    	return downCmds;
    }
    
    
    /**
     * 验证版本号格式：包括固件的和增值组件的
     * @param version
     * @param fw
     * @return
     */
    private boolean validateVersionFormat(String version,boolean fw){
    	boolean result = false;
    	if(fw){
    		result = DeviceVersion.parser(version).valid();
    	}else
    		result = DeviceOMVersion.parser(version).valid();
    	if(!result) throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_VERSION_INVALID_FORMAT);
    	return result;
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

	public WifiDeviceModuleService getWifiDeviceModuleService() {
		return wifiDeviceModuleService;
	}

	public void setWifiDeviceModuleService(
			WifiDeviceModuleService wifiDeviceModuleService) {
		this.wifiDeviceModuleService = wifiDeviceModuleService;
	}

}
