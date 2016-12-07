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
import com.bhu.vas.api.helper.BusinessEnumType.AdvertiseType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.advertise.iservice.IAdvertiseRpcService;
import com.bhu.vas.rpc.facade.AdvertiseUnitFacadeService;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.vto.advertise.AdDevicePositionVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseListVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseReportVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseVTO;
import com.bhu.vas.business.ds.user.service.UserService;
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
	public RpcResponseDTO<AdDevicePositionVTO> fetchDevicePositionDistribution(int type,String province, String city, String district) {
		logger.info(String.format("fetchDevicePositionDistribution type[%s] province[%s] city[%s] district[%s]",type, province, city, district));
		try {
			AdDevicePositionVTO vto = advertiseUnitFacadeService.fetchDevicePositionDistribution(type,province, city, district);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		} catch (BusinessI18nCodeException i18nex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(), i18nex.getPayload());
		} catch (Exception ex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	@Override
	public RpcResponseDTO<AdvertiseVTO> createNewAdvertise(int uid, int type,String image,
			String url,String domain, String province, String city, String district,String description,String title,
			long start, long end) throws ParseException {
		logger.info(String.format("createNewAdvertise with uid[%s] type[%s] image[%s] url[%s] domain[%s] province[%s] city[%s] district[%s] title[%s] description[%s] start[%s] start[%s]",
				uid, type,image, url,domain,province, city, district,title,description, start, end));
		if(start>end){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_TIME_TIMEERROR);
		}
		return advertiseUnitFacadeService.createNewAdvertise(uid,type, image, url,domain, province, city, district,description,title, start, end);
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
			String publishEndTime, int type, String createStartTime,
			String createEndTime, String mobileNo,int state,int pn,int ps) {
		logger.info(String.format("queryAdvertiseList uid[%s] province[%s] city[%s] district[%s] publishStartTime[%s] publishEndTime[%s] type[%s] createStartTime[%s] createEndTime[%s] mobileNo[%s] state[%s] pn[%s] ps[%s]",
				uid,province,city,district,publishStartTime,publishEndTime,type,createStartTime,createEndTime,mobileNo,state,pn,ps));
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
		
		Map<String,Object> typeMap=new HashMap<String,Object>();
		typeMap.put("name", "type");
		typeMap.put("value", type);
		maps.add(typeMap);
		try{
			AdvertiseListVTO advertiseListVTO=new AdvertiseListVTO();
			TailPage<AdvertiseVTO> advertises=advertiseUnitFacadeService.queryAdvertiseList(uid,maps,publishStartTime,publishEndTime,createStartTime,createEndTime,mobileNo,pn,ps);
			advertiseListVTO.setAdvertises(advertises);
			if(uid!=null){
				ModelCriteria pubMc=new ModelCriteria();
				pubMc.createCriteria().andColumnEqualTo("state", AdvertiseType.Published.getType()).andColumnEqualTo("uid", uid);
				int pubComNum=advertiseUnitFacadeService.getAdvertiseService().countByModelCriteria(pubMc);
				advertiseListVTO.setPubComNum(pubComNum);
				
				ModelCriteria vfMc=new ModelCriteria();
				vfMc.createCriteria().andColumnEqualTo("state", AdvertiseType.VerifyFailure.getType()).andColumnEqualTo("uid", uid);
				int vfNum=advertiseUnitFacadeService.getAdvertiseService().countByModelCriteria(vfMc);
				advertiseListVTO.setVerifyFalNum(vfNum);
				
				ModelCriteria escapeMc=new ModelCriteria();
				escapeMc.createCriteria().andColumnEqualTo("state", AdvertiseType.EscapeOrder.getType()).andColumnEqualTo("uid", uid);
				int escapeNum=advertiseUnitFacadeService.getAdvertiseService().countByModelCriteria(escapeMc);
				advertiseListVTO.setEscapeNum(escapeNum);
				
				ModelCriteria onPublishMc=new ModelCriteria();
				onPublishMc.createCriteria().andColumnEqualTo("state", AdvertiseType.OnPublish.getType()).andColumnEqualTo("uid", uid);
				int onPublishNum=advertiseUnitFacadeService.getAdvertiseService().countByModelCriteria(onPublishMc);
				advertiseListVTO.setOnPublishNum(onPublishNum);
				
				ModelCriteria unpaidMc=new ModelCriteria();
				unpaidMc.createCriteria().andColumnEqualTo("state", AdvertiseType.UnPaid.getType()).andColumnEqualTo("uid", uid);
				int unPaidNum=advertiseUnitFacadeService.getAdvertiseService().countByModelCriteria(unpaidMc);
				advertiseListVTO.setUnPaidNum(unPaidNum);
				
				ModelCriteria unPublishMc=new ModelCriteria();
				unPublishMc.createCriteria().andColumnEqualTo("state", AdvertiseType.UnPublish.getType()).andColumnEqualTo("uid", uid);
				int unPublishNum=advertiseUnitFacadeService.getAdvertiseService().countByModelCriteria(unPublishMc);
				advertiseListVTO.setUnPublishNum(unPublishNum);
				
				ModelCriteria unVerifyMc=new ModelCriteria();
				unVerifyMc.createCriteria().andColumnEqualTo("state", AdvertiseType.UnVerified.getType()).andColumnEqualTo("uid", uid);
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
	public RpcResponseDTO<AdvertiseReportVTO> fetchAdvertiseReport(int uid, String advertiseId) {
		logger.info(String.format("fetchAdvertiseReport uid[%s] advertiseId[%s]",uid,
				advertiseId));
		return advertiseUnitFacadeService.fetchAdvertiseReport(uid,advertiseId);
	}
}
