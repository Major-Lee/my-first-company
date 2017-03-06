package com.bhu.vas.rpc.service.advertise;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.helper.BusinessEnumType.AdvertiseStateType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.advertise.iservice.IAdvertiseRpcService;
import com.bhu.vas.api.rpc.user.model.UserConsumptiveWalletLog;
import com.bhu.vas.rpc.facade.AdvertiseUnitFacadeService;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.vto.advertise.AdCommentsVTO;
import com.bhu.vas.api.vto.advertise.AdDevicePositionVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseListVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseResponseVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseUserDetailVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseVTO;
import com.bhu.vas.api.vto.device.DeviceGEOPointCountVTO;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service("advertiseRpcService")
public class WholeCityRpcService implements IAdvertiseRpcService{
	private final Logger logger = LoggerFactory.getLogger(WholeCityRpcService.class);
	@Resource
	private AdvertiseUnitFacadeService advertiseUnitFacadeService;
	@Resource
	private UserService userService;
	
	@Override
	public RpcResponseDTO<AdDevicePositionVTO> fetchDevicePositionDistribution(int uid ,int type,String province, String city, String district) {
		logger.info(String.format("fetchDevicePositionDistribution uid[%s] type[%s] province[%s] city[%s] district[%s]",uid,type, province, city, district));
		try {
			AdDevicePositionVTO vto = advertiseUnitFacadeService.fetchDevicePositionDistribution(uid,type,province, city, district);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		} catch (BusinessI18nCodeException i18nex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(), i18nex.getPayload());
		} catch (Exception ex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	@Override
	public RpcResponseDTO<AdvertiseVTO> createNewAdvertise(int uid,Integer vuid,String adid ,int tag, int type,String image,
			String url,String domain, String province, String city, String district,String adcode,double lat,double lon,String distance,String description,String title,
			long start, long end,boolean isTop,String extparams) throws ParseException {
		logger.info(String.format("createNewAdvertise with uid[%s] vuid[%s] adid[%s] tag [%s] type[%s] image[%s] url[%s] domain[%s] province[%s] city[%s] district[%s] adcode[%s] lat[%s] lon[%s] distance[%s] title[%s] description[%s] start[%s] start[%s] isTop[%s] extparams[%s]",
				uid,vuid,adid,tag, type,image, url,domain,province, city, district,adcode,lat,lon,distance,title,description, start, end,isTop,extparams));
		if(start>end){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_TIME_TIMEERROR);
		}
		return advertiseUnitFacadeService.createNewAdvertise(uid,vuid,adid,tag,type, image, url,domain, province, city, district,adcode,lat,lon,distance,description,title, start, end,isTop,extparams);
	}

	@Override
	public RpcResponseDTO<Boolean> updateAdvertise(int uid,String advertiseId, String image,
			String url, String domain, String province, String city,
			String district, String description, String title, long start,
			long end) {
		logger.info(String.format("updateAdvertise with uid[%s] advertiseId[%s] image[%s] url[%s] domain[%s] province[%s] city[%s] district[%s] title[%s] description[%s] start[%s] end[%s]",
				uid,advertiseId, image, url,domain, province, city, district,title,description, start, end));
		return advertiseUnitFacadeService.updateAdvertise(uid,advertiseId, image, url,domain, province, city, district,description,title, start, end);
	}

	@Override
	public RpcResponseDTO<Boolean> verifyAdvertise(int uid, String advertiseId,String msg,
			int state) {
		logger.info(String.format("verifyAdvertise with uid[%s] advertiseId[%s] msg[%s] state[%s]",
				uid,advertiseId, msg,state));
		return advertiseUnitFacadeService.verifyAdvertise(uid,advertiseId,msg,state);
	}

	@Override
	public RpcResponseDTO<AdvertiseVTO> queryAdvertise(Integer uid,String advertiseId) {
		logger.info(String.format("queryAdvertise uid[%s] advertiseId[%s]",uid,
				advertiseId));
		return advertiseUnitFacadeService.queryAdvertiseInfo(uid,advertiseId);
	}

	@Override
	public RpcResponseDTO<AdvertiseListVTO> queryAdvertiseList(Integer uid, String province,
			String city, String district, String publishStartTime,
			String publishEndTime, Integer type, String createStartTime,
			String createEndTime, String mobileNo,int state,int pn,int ps,boolean isConsole) {
		logger.info(String.format("queryAdvertiseList uid[%s] province[%s] city[%s] district[%s] publishStartTime[%s] publishEndTime[%s] type[%s] createStartTime[%s] createEndTime[%s] mobileNo[%s] state[%s] pn[%s] ps[%s] isConsole[%s]",
				uid,province,city,district,publishStartTime,publishEndTime,type,createStartTime,createEndTime,mobileNo,state,pn,ps,isConsole));
		List<Map<String,Object>> maps=new ArrayList<Map<String,Object>>();
		if(StringUtils.isNotBlank(province)){
			Map<String,Object> provinceMap=new HashMap<String,Object>();
			provinceMap.put("name", "province");
			provinceMap.put("value", province);
			maps.add(provinceMap);
		}
		if(StringUtils.isNotBlank(city)){
			Map<String,Object> cityMap=new HashMap<String,Object>();
			cityMap.put("name", "city");
			cityMap.put("value", city);
			maps.add(cityMap);
		}
		if(StringUtils.isNotBlank(district)){
			Map<String,Object> districtMap=new HashMap<String,Object>();
			districtMap.put("name", "district");
			districtMap.put("value", district);
			maps.add(districtMap);
		}
		if(state!=-1){
			Map<String,Object> stateMap=new HashMap<String,Object>();
			stateMap.put("name", "state");
			stateMap.put("value", state);
			maps.add(stateMap);
		}
		
		if(type !=null){
			Map<String,Object> typeMap=new HashMap<String,Object>();
			typeMap.put("name", "type");
			typeMap.put("value", type);
			maps.add(typeMap);
		}
		try{
			AdvertiseListVTO advertiseListVTO=new AdvertiseListVTO();
			TailPage<AdvertiseVTO> advertises=advertiseUnitFacadeService.queryAdvertiseList(uid,maps,publishStartTime,publishEndTime,createStartTime,createEndTime,mobileNo,pn,ps,isConsole);
			advertiseListVTO.setAdvertises(advertises);
			if(uid!=null){
				ModelCriteria pubMc=new ModelCriteria();
				pubMc.createCriteria().andColumnEqualTo("state", AdvertiseStateType.Published.getType()).andColumnEqualTo("uid", uid);
				int pubComNum=advertiseUnitFacadeService.getAdvertiseService().countByModelCriteria(pubMc);
				advertiseListVTO.setPubComNum(pubComNum);
				
				ModelCriteria vfMc=new ModelCriteria();
				vfMc.createCriteria().andColumnEqualTo("state", AdvertiseStateType.VerifyFailure.getType()).andColumnEqualTo("uid", uid);
				int vfNum=advertiseUnitFacadeService.getAdvertiseService().countByModelCriteria(vfMc);
				advertiseListVTO.setVerifyFalNum(vfNum);
				
				ModelCriteria escapeMc=new ModelCriteria();
				escapeMc.createCriteria().andColumnEqualTo("state", AdvertiseStateType.EscapeOrder.getType()).andColumnEqualTo("uid", uid);
				int escapeNum=advertiseUnitFacadeService.getAdvertiseService().countByModelCriteria(escapeMc);
				advertiseListVTO.setEscapeNum(escapeNum);
				
				ModelCriteria onPublishMc=new ModelCriteria();
				onPublishMc.createCriteria().andColumnEqualTo("state", AdvertiseStateType.OnPublish.getType()).andColumnEqualTo("uid", uid);
				int onPublishNum=advertiseUnitFacadeService.getAdvertiseService().countByModelCriteria(onPublishMc);
				advertiseListVTO.setOnPublishNum(onPublishNum);
				
				ModelCriteria unpaidMc=new ModelCriteria();
				unpaidMc.createCriteria().andColumnEqualTo("state", AdvertiseStateType.UnPaid.getType()).andColumnEqualTo("uid", uid);
				int unPaidNum=advertiseUnitFacadeService.getAdvertiseService().countByModelCriteria(unpaidMc);
				advertiseListVTO.setUnPaidNum(unPaidNum);
				
				ModelCriteria unPublishMc=new ModelCriteria();
				unPublishMc.createCriteria().andColumnEqualTo("state", AdvertiseStateType.UnPublish.getType()).andColumnEqualTo("uid", uid);
				int unPublishNum=advertiseUnitFacadeService.getAdvertiseService().countByModelCriteria(unPublishMc);
				advertiseListVTO.setUnPublishNum(unPublishNum);
				
				ModelCriteria unVerifyMc=new ModelCriteria();
				unVerifyMc.createCriteria().andColumnEqualTo("state", AdvertiseStateType.UnVerified.getType()).andColumnEqualTo("uid", uid);
				int unVerifyNum=advertiseUnitFacadeService.getAdvertiseService().countByModelCriteria(unVerifyMc);
				advertiseListVTO.setUnVerifyNum(unVerifyNum);
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(advertiseListVTO);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	@Override
	public RpcResponseDTO<Boolean> escapeAdvertise(int uid, String advertiseId) {
		logger.info(String.format("escapeAdvertise uid[%s] advertiseId[%s]",uid,
				advertiseId));
		return advertiseUnitFacadeService.escapeAdvertise(uid,advertiseId);
	}
	
	@Override
	public RpcResponseDTO<List<DeviceGEOPointCountVTO>> countDeviceCountByGEOPoint(int uid ,String province, String city, String district,double lat,double lon,String distances) {
		logger.info(String.format("countDeviceCountByGEOPoint uid[%s] province[%s] city[%s] district[%s] lat[%s] lon[%s]  distances[%s]",uid,
				province,city,district,lat,lon,distances));
		return advertiseUnitFacadeService.countDeviceCountByGEOPoint(province,city,district,lat,lon,distances);
	}
	
	@Override
	public RpcResponseDTO<List<TailPage<AdvertiseVTO>>> fetchBySearchConditionMessages(String mac,String umac ,int pageNo,int pageSize,boolean customize,String ... messages) {
		logger.info(String.format("fetchBySearchConditionMessages mac[%s] umac[%s] pageNo[%s] pageSize[%s] customize[%s] messages[%s]",mac,umac,pageNo,
				pageSize,customize,messages));
		List<TailPage<AdvertiseVTO>> vtos = advertiseUnitFacadeService.fetchBySearchConditionMessages(mac,umac,pageNo,pageSize,customize,messages);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(vtos);
	}
	
	@Override
	public RpcResponseDTO<Boolean> advertiseOperation(int uid, String adid , boolean isTop,boolean isRefresh) {
		logger.info(String.format("advertiseOperation uid[%s] adid[%s] isTop[%s] isRefresh[%s]",uid,
				adid,isTop,isRefresh));
		return advertiseUnitFacadeService.advertiseOperation(uid,adid,isTop,isRefresh);
	}
	
	@Override
	public RpcResponseDTO<Boolean> advertiseComment(int uid,Integer vuid , String adid,String message,int type,Double score) {
		logger.info(String.format("AdvertiseComment uid[%s] vuidp[%s] adid[%s] message[%s] type[%s] score[score]",uid,
				vuid,adid,message,type,score));
		return advertiseUnitFacadeService.AdvertiseComment(uid,vuid,adid,message,type,score);
	}
	
	@Override
	public RpcResponseDTO<List<AdCommentsVTO>> fetchCommentDetail(String[] adids,int pn,int ps) {
		logger.info(String.format("fetchCommentDetail adids[%s], pn[%s] ps[%s]" ,StringHelper.toString(adids),pn,ps ));
		return advertiseUnitFacadeService.fetchCommentDetail(adids,pn,ps);
	}
	
	@Override
	public RpcResponseDTO<AdvertiseUserDetailVTO> userAdvertiseDetail(int uid) {
		logger.info(String.format("userAdvertiseDetail uid[%s] ",uid));
		return advertiseUnitFacadeService.userAdvertiseDetail(uid);
	}
	
	@Override
	public RpcResponseDTO<List<AdvertiseVTO>> queryRandomAdvertiseDetails(String mac,String umac,Double lat,Double lon,String adcode,String sourcetype ,String systype,int type) {
		logger.info(String.format("queryRandomAdvertiseDetails ... mac[%s] umac[%s] lat[%s] lon[%s] adcode[%s] sourcetype[%s] systype[%s] type[%s]",mac,umac,lat,lon,adcode,sourcetype,systype,type));
		return advertiseUnitFacadeService.queryRandomAdvertiseDetails(mac,umac,lat,lon,adcode,sourcetype,systype,type);
	}
	
	@Override
	public RpcResponseDTO<Boolean> confirmPay(int uid,String adid) {
		logger.info(String.format("confirmPay uid[%s] adid[%s]",uid,adid));
		return advertiseUnitFacadeService.confirmPay(uid,adid);
	}
	
	@Override
	public RpcResponseDTO<Boolean> destoryTips(int uid,String adid) {
		logger.info(String.format("destoryTips uid[%s] adid[%s]",uid,adid));
		return advertiseUnitFacadeService.destoryTips(uid,adid);
	}
	
	@Override
	public RpcResponseDTO<Boolean> advertiseCPMNotify(String[] adids,String userid,String sourcetype ,String systype) {
		logger.info(String.format("advertiseCPMNotify adids[%s] userid[%s] sourcetype[%s] systype[%s]",adids,userid,sourcetype,systype));
		return advertiseUnitFacadeService.advertiseCPMNotify(adids,userid,sourcetype,systype);
	}

	@Override
	public RpcResponseDTO<AdvertiseResponseVTO> fetchAdListByPortal(String mac ,String umac ,String sourcetype ,String systype, int pageSize , int pageNo) {
		logger.info(String.format("fetchAdListByPortal mac[%s] umac[%s] sourcetype[%s] systype[%s] pageSize[%s] pageNo[%s]",mac,umac,sourcetype,systype,pageSize,pageNo));
		return advertiseUnitFacadeService.fetchAdListByPortal(mac,umac,sourcetype,systype,pageSize,pageNo);
	}
	
	@Override
	public RpcResponseDTO<List<AdvertiseVTO>> fetchAdListByAPP(double lat ,double lon,String adcode, int pageSize , int pageNo) {
		logger.info(String.format("fetchAdListByAPP lat[%s] lon[%s] adcode[%s] pageSize[%s] pageNo[%s]",lat,lon,adcode,pageSize,pageNo));
		return advertiseUnitFacadeService.fetchAdListByAPP(lat,lon,adcode,pageSize,pageNo);
	}
	
	@Override
	public RpcResponseDTO<List<UserConsumptiveWalletLog>> fetchAdvertiseReport(int uid,String advertiseId,Long start,Long end, int pageNo ,int pageSize) {
		logger.info(String.format("fetchAdvertiseReport uid[%s] advertiseId[%s] start[%s] end[%s] pageSize[%s] pageNo[%s]",uid,advertiseId,start,end,pageNo,pageSize));
		return advertiseUnitFacadeService.fetchAdvertiseReport(uid,advertiseId,start,end,pageNo,pageSize);
	}
	
	@Override
	public RpcResponseDTO<List<Map<String, Object>>> fetchAdvertiseChartReport(int uid,String advertiseId,int type,Long start,Long end,int pageNo ,int pageSize) {
		logger.info(String.format("fetchAdvertiseChartReport uid[%s] advertiseId[%s] type[%s] start[%s] end[%s] pageSize[%s] pageNo[%s]",uid,advertiseId,type,start,end,pageNo,pageSize));
		return advertiseUnitFacadeService.fetchAdvertiseChartReport(uid,advertiseId,type,start,end,pageNo,pageSize);
	}
}
