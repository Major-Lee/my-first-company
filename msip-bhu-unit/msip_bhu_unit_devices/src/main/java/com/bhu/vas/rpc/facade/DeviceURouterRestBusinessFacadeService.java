package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetAliasService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import redis.clients.jedis.Tuple;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.dto.redis.DeviceUsedStatisticsDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceRxPeakSectionDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceTxPeakSectionDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingAclDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingLinkModeDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingMMDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingRateControlDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingUserDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingVapDTO;
import com.bhu.vas.api.dto.wifistasniffer.TerminalDetailDTO;
import com.bhu.vas.api.dto.wifistasniffer.UserTerminalFocusDTO;
import com.bhu.vas.api.dto.wifistasniffer.WifistasnifferItemRddto;
import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.DeviceHelper;
import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.bhu.vas.api.mdto.WifiHandsetDeviceItemDetailMDTO;
import com.bhu.vas.api.mdto.WifiHandsetDeviceItemLogMDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSetting;
import com.bhu.vas.api.rpc.user.dto.UserTerminalOnlineSettingDTO;
import com.bhu.vas.api.rpc.user.dto.UserWifiSinfferSettingDTO;
import com.bhu.vas.api.rpc.user.dto.UserWifiTimerSettingDTO;
import com.bhu.vas.api.rpc.user.model.UserSettingState;
import com.bhu.vas.api.vto.URouterAdminPasswordVTO;
import com.bhu.vas.api.vto.URouterEnterVTO;
import com.bhu.vas.api.vto.URouterHdDetailVTO;
import com.bhu.vas.api.vto.URouterHdHostNameVTO;
import com.bhu.vas.api.vto.URouterHdTimeLineVTO;
import com.bhu.vas.api.vto.URouterHdVTO;
import com.bhu.vas.api.vto.URouterInfoVTO;
import com.bhu.vas.api.vto.URouterModeVTO;
import com.bhu.vas.api.vto.URouterPeakSectionsVTO;
import com.bhu.vas.api.vto.URouterRealtimeRateVTO;
import com.bhu.vas.api.vto.URouterSettingVTO;
import com.bhu.vas.api.vto.URouterVapPasswordVTO;
import com.bhu.vas.api.vto.URouterWSCommunityVTO;
import com.bhu.vas.api.vto.URouterWSHotVTO;
import com.bhu.vas.api.vto.URouterWSRecentVTO;
import com.bhu.vas.api.vto.WifiSinfferSettingVTO;
import com.bhu.vas.api.vto.config.URouterDeviceConfigMMVTO;
import com.bhu.vas.api.vto.config.URouterDeviceConfigNVTO;
import com.bhu.vas.api.vto.config.URouterDeviceConfigRateControlVTO;
import com.bhu.vas.api.vto.config.URouterDeviceConfigVTO;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.handset.HandsetStorageFacadeService;
import com.bhu.vas.business.bucache.redis.serviceimpl.marker.BusinessMarkerService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.WifiDeviceRealtimeRateStatisticsStringService;
import com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer.TerminalDetailRecentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer.TerminalDeviceTypeCountHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer.TerminalHotSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer.TerminalLastTimeStringService;
import com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer.TerminalRecentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer.UserTerminalFocusHashService;
import com.bhu.vas.business.ds.builder.BusinessModelBuilder;
import com.bhu.vas.business.ds.builder.WifiStasnifferHelper;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.device.mdto.WifiHandsetDeviceRelationMDTO;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSettingService;
import com.bhu.vas.business.ds.device.service.WifiHandsetDeviceRelationMService;
import com.bhu.vas.business.ds.user.service.UserSettingStateService;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.helper.ArrayHelper;
import com.smartwork.msip.cores.helper.DateTimeExtHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.helper.encrypt.JNIRsaHelper;
import com.smartwork.msip.cores.orm.support.page.PageHelper;
import com.smartwork.msip.cores.plugins.dictparser.impl.mac.MacDictParserFilterHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * device urouter Rest RPC组件的业务service
 * @author tangzichao
 *
 */
@Service
public class DeviceURouterRestBusinessFacadeService {
	private final Logger logger = LoggerFactory.getLogger(DeviceRestBusinessFacadeService.class);

	private static final long IGNORE_LOGIN_TIME_SPACE = 15 * 60 * 1000L;

	private static final int DAY_TIME_MILLION_SECOND = 24 * 3600 * 1000;

	private static final String HANDSET_LOGIN_TYPE = "login";
	private static final String HANDSET_LOGOUT_TYPE = "logout";
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private WifiDeviceSettingService wifiDeviceSettingService;
	
	/*@Resource
	private HandsetDeviceService handsetDeviceService;*/
	
//	@Resource
//	private WifiHandsetDeviceMarkService wifiHandsetDeviceMarkService;
	
	@Resource
	private DeviceFacadeService deviceFacadeService;
	
	@Resource
	private UserSettingStateService userSettingStateService;
	
	@Resource
	private DeliverMessageService deliverMessageService;

	@Resource
	private WifiHandsetDeviceRelationMService wifiHandsetDeviceRelationMService;

	
	/**
	 * urouter 入口界面数据
	 * @param uid
	 * @param wifiId
	 * @return
	 */
	public RpcResponseDTO<URouterEnterVTO> urouterEnter(Integer uid, String wifiId){
		try{
			//WifiDevice device_entity = uRouterDeviceFacadeService.validateDevice(wifiId);
			deviceFacadeService.validateUserDevice(uid, wifiId);
			//WifiDeviceSetting entity = deviceFacadeService.validateDeviceSetting(wifiId);

			//WifiDeviceSettingDTO dto = entity.getInnerModel();
			URouterEnterVTO vto = new URouterEnterVTO();
//			String power = DeviceHelper.getURouterDevicePower(dto);
//			if(!StringUtils.isEmpty(power)){
//				vto.setPower(Integer.parseInt(power));
//			}
			vto.setOhd_count(WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(wifiId));
			//vto.setWd_date_rx_rate(device_entity.getData_rx_rate());
			//vto.setData_rx_rate_peak(device_entity.getData_rx_rate());
			
			String[] ret = fetchRealtimeRate(wifiId);
			vto.setTx_rate(ret[0]);
			vto.setRx_rate(ret[1]);
			//String peak_rate = WifiDeviceRealtimeRateStatisticsStringService.getInstance().getPeak(wifiId);
			//vto.setPeak_rate(peak_rate);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}
	
	public static final int HDList_Online_Status = 1;//获取在线终端列表
	public static final int HDList_Offline_Status = 2;//获取离线终端列表
	/**
	 * 获取urouter设备在线终端列表
	 * @param uid
	 * @param wifiId
	 * @param start
	 * @param size
	 * @return
	 * modified by Edmond Lee for handset storage
	 */
	public RpcResponseDTO<Map<String,Object>> urouterHdList(Integer uid, String wifiId, int status, int start, int size){
		try{
			deviceFacadeService.validateUserDevice(uid, wifiId);
			WifiDeviceSetting entity = deviceFacadeService.validateDeviceSetting(wifiId);
			
			//用户访问终端列表时 判断上报timeout进行获取
			if(!WifiDeviceRealtimeRateStatisticsStringService.getInstance().isHDRateWaiting(wifiId)){
				deliverMessageService.sendDeviceHDRateFetchActionMessage(wifiId);
			}
			
			List<URouterHdVTO> vtos = null;
			long total = 0;
			Set<Tuple> presents = null;
			switch(status){
				case HDList_Online_Status:
					presents = WifiDeviceHandsetPresentSortedSetService.getInstance().fetchOnlinePresentWithScores(wifiId, start, size);
					total = WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(wifiId);
					break;
				case HDList_Offline_Status:
					presents = WifiDeviceHandsetPresentSortedSetService.getInstance().fetchOfflinePresentWithScores(wifiId, start, size);
					total = WifiDeviceHandsetPresentSortedSetService.getInstance().presentOfflineSize(wifiId);
					break;
				default:
					presents = WifiDeviceHandsetPresentSortedSetService.getInstance().fetchPresents(wifiId, start, size);
					total = WifiDeviceHandsetPresentSortedSetService.getInstance().presentSize(wifiId);
			}
			//System.out.println("###################presents.size():"+presents.size());


			//客户端现在获取实时速率会5秒一次请求。
			if(!presents.isEmpty()){
				List<String> hd_macs = new ArrayList<String>();
				for(Tuple tuple : presents){
					hd_macs.add(tuple.getElement());
				}
				List<HandsetDeviceDTO> handsets = HandsetStorageFacadeService.handsets(hd_macs);
				//List<HandsetDevice> hd_entitys = handsetDeviceService.findByIds(hd_macs, true, true);
				//List<WifiHandsetDeviceMark> mark_entitys = wifiHandsetDeviceMarkService.findByIds(mark_pks, true, true);
				if(!handsets.isEmpty()){
					vtos = new ArrayList<URouterHdVTO>();
					int cursor = 0;
					HandsetDeviceDTO hd_entity = null;
					WifiDeviceSettingDTO setting_dto = entity.getInnerModel();
					for(Tuple tuple : presents){
						hd_entity = handsets.get(cursor);
						boolean online = WifiDeviceHandsetPresentSortedSetService.getInstance().isOnline(tuple.getScore());
						URouterHdVTO vto = BusinessModelBuilder.toURouterHdVTO(uid, tuple.getElement(), online, hd_entity, setting_dto);
						vtos.add(vto);
						cursor++;
					}
				}
			}
			if(vtos == null)
				vtos = Collections.emptyList();
			
			Map<String, Object> payload = PageHelper.partialAllList(vtos, total, start, size);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(payload);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}


	/**
	 * 获取终端详情
	 * @param uid
	 * @param wifiId
	 * @param mac
	 * @return
	 */
	public RpcResponseDTO<URouterHdDetailVTO>  urouterHdDetail(Integer uid, String wifiId, String mac) {

		URouterHdDetailVTO vto = new URouterHdDetailVTO();
		vto.setHd_mac(mac);
		vto.setMac(wifiId);
		try {
			WifiHandsetDeviceRelationMDTO wifiHandsetDeviceRelationMDTO =
					wifiHandsetDeviceRelationMService.getRelation(wifiId, mac);

			if (wifiHandsetDeviceRelationMDTO != null) {
				HandsetDeviceDTO handsetDeviceDTO = HandsetStorageFacadeService.handset(mac);
				long total_rx_bytes = wifiHandsetDeviceRelationMDTO.getTotal_rx_bytes();

				if (handsetDeviceDTO != null) {
					if (handsetDeviceDTO.wasOnline()) { //离线的时候会加进去
						if (handsetDeviceDTO.getTx_bytes() != null ) {
							total_rx_bytes = total_rx_bytes + Long.parseLong(handsetDeviceDTO.getTx_bytes());
						}
					}
				}

				vto.setTotal_rx_bytes(String.valueOf(total_rx_bytes));

				List<String> weeks = DateTimeExtHelper.getSevenDateOfWeek();

				List<URouterHdTimeLineVTO> uRouterHdTimeLineVTOList = new ArrayList<URouterHdTimeLineVTO>();


				//集合中只有七天的在线记录
				for (String key : weeks) {
					URouterHdTimeLineVTO uRouterHdTimeLineVTO = new URouterHdTimeLineVTO();
					uRouterHdTimeLineVTO.setDate(key);
					List<WifiHandsetDeviceItemDetailMDTO> mdtos = new ArrayList<WifiHandsetDeviceItemDetailMDTO>();
					uRouterHdTimeLineVTO.setDetail(mdtos);
					uRouterHdTimeLineVTOList.add(uRouterHdTimeLineVTO);
				}

				List<WifiHandsetDeviceItemLogMDTO> logs = wifiHandsetDeviceRelationMDTO.getLogs();

				getLogs(uRouterHdTimeLineVTOList, logs);

				vto.setTimeline(uRouterHdTimeLineVTOList);
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);

		} catch (BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}


	public RpcResponseDTO<Long>  urouterHdModifyAlias(Integer uid, String wifiId, String mac, String alias) {
		try {

			Long ret = WifiDeviceHandsetAliasService.getInstance().hsetHandsetAlias(uid, mac, alias);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(ret);

		} catch (BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}



	/**
	 *
	 * @param vtos
	 * @param logs
	 * @return
	 */
	private List<URouterHdTimeLineVTO> getLogs(List<URouterHdTimeLineVTO> vtos, List<WifiHandsetDeviceItemLogMDTO> logs) {
		long currentTime = System.currentTimeMillis();
		long currentZeroTime = getDateZeroTime(new Date()).getTime();

		if (logs != null) {
			//WifiHandsetDeviceItemDetailMDTO dto = null;
			//List<WifiHandsetDeviceItemDetailMDTO> mdtos = null;
			String last_type = null;
			long last_ts = 0;

			boolean flag = false;
			for (WifiHandsetDeviceItemLogMDTO log : logs) {

				long ts = log.getTs();
				long space = currentZeroTime - ts;
				int offset = (int)(space/(DAY_TIME_MILLION_SECOND));
				if (space < 0) {
					offset = -1;
				}
				if (offset > 5) {
					//offset = 5; //7天外的数据直接忽略
					if (HANDSET_LOGOUT_TYPE.equals(last_type)) { //如果最后一天数据是隔天数据
						URouterHdTimeLineVTO vto = vtos.get(6);
						List<WifiHandsetDeviceItemDetailMDTO> mdtos_ = vto.getDetail();
						if (mdtos_ != null && !mdtos_.isEmpty()) {
							WifiHandsetDeviceItemDetailMDTO dto_ = mdtos_.get(mdtos_.size()-1);
							dto_.setLogin_at(getDateZeroTime(new Date(ts)).getTime());
							vto.setDetail(mdtos_);
						}
					}
					break;
				}

				String type = log.getType();
				long rx_bytes = log.getRx_bytes();

//				logger.info("offset[" + offset + "],type[" + type + "],last_type[" + last_type+"],ts[" + ts + "]");
//				logger.info("spacetime[" + (last_ts -ts) + "]");
				if (last_type == null) { //最新一条记录
					//处理分割记录
					if (HANDSET_LOGIN_TYPE.equals(type)) {
						flag = true;
					}
					flag = filterDay(ts, currentTime, type,last_type, rx_bytes, vtos, offset, true, flag);

				} else { //第二条数据开始

					if (HANDSET_LOGOUT_TYPE.equals(type) && HANDSET_LOGIN_TYPE.equals(last_type)) { //新的的登出记录
						flag = filterDay(ts, last_ts, type, last_type, rx_bytes, vtos, offset, false, flag);
					}

					if (HANDSET_LOGOUT_TYPE.equals(type) && HANDSET_LOGOUT_TYPE.equals(last_type)) { //连续两条登出
						//忽略记录
						//模拟一条登入的记录
//						last_type = "logout";
						type = HANDSET_LOGIN_TYPE;
						flag = filterDay(ts, last_ts, type, last_type, rx_bytes, vtos, offset, false, flag);

						last_type = HANDSET_LOGIN_TYPE;
						type = HANDSET_LOGOUT_TYPE;
						flag = filterDay(last_ts, last_ts, type, last_type, rx_bytes, vtos, offset, false, flag);

					}

					if (HANDSET_LOGIN_TYPE.equals(type) && HANDSET_LOGOUT_TYPE.equals(last_type)) {

						flag = filterDay(ts, last_ts, type, last_type, rx_bytes,vtos, offset, false, flag);
					}
					if (HANDSET_LOGIN_TYPE.equals(type) && HANDSET_LOGIN_TYPE.equals(last_type)) {
						//忽略记录
						//先模拟一条当前登出的记录
						type = HANDSET_LOGOUT_TYPE;
						flag = filterDay(last_ts, last_ts, type, last_type, rx_bytes, vtos, getOffSet(ts, currentZeroTime), false, flag);

						last_type = HANDSET_LOGOUT_TYPE;
						type = HANDSET_LOGIN_TYPE;
//						last_ts = ts;
						flag = filterDay(ts, last_ts, type, last_type, rx_bytes, vtos, getOffSet(ts, currentZeroTime), false, flag);

					}

				}

				last_type = type;
				last_ts = ts;


			}

		}

		return vtos;
	}


	private int getOffSet(long ts, long currentZeroTime) {
		long space = currentZeroTime - ts;
		int offset = (int) (space/(DAY_TIME_MILLION_SECOND));
		if (space < 0) {
			offset = -1;
		}
		if (offset > 5) {
			offset = 5;
		}
		return offset;
	}

	/**
	 *
	 * 解析终端登入登出日志，每条日志只有时间戳和类型(login/logout)
	 * 1.正常流程 login -> logout -> login -> logout -> ...
	 * 2.出现跨天的情况
	 * 3.15分钟之内的登入登出合并
	 *
	 *
	 *
	 * @param ts     时间戳
	 * @param last_ts 上一次
	 * @param type   登入还是登出
	 * @param last_type 上一次
	 * @param vtos   结果
	 * @param offset 记录是七天的第几天
	 * @param first last_type == null 时候
	 */
	private boolean filterDay(long ts, long last_ts, String type, String last_type, long rx_bytes, List<URouterHdTimeLineVTO> vtos,
						   int offset, boolean first, boolean flag) {

		//如果当前在线，当前时间与上一次登录时间相隔数天
		String tsZeroStr = DateTimeHelper.formatDate(new Date(ts), DateTimeHelper.shortDateFormat);

		long ts_zero_at =  DateTimeHelper.getDateLongTime(tsZeroStr, DateTimeHelper.shortDateFormat);

		long spaceTime = last_ts - ts_zero_at;
		//获取终端连续在线的时间段j天
		int j = (int)spaceTime / (DAY_TIME_MILLION_SECOND);
		if (j >= 6) {
			j = 6;
		}

		logger.info("ts["+ts+"],last_ts["+last_ts+"], type["+type+"], last_type["+last_type+"], offset["+offset+"],j["+j+"]");
		try {

			if (j == 0) { //当天记录
				URouterHdTimeLineVTO vto = vtos.get(offset + 1); //更新logs
				List<WifiHandsetDeviceItemDetailMDTO> mdtos = vto.getDetail();
				if (mdtos == null) {
					mdtos = new ArrayList<WifiHandsetDeviceItemDetailMDTO>();
				}

				WifiHandsetDeviceItemDetailMDTO dto = null;

//				if (type.equals("login") && last_type.equals("logout")) { //正常流程
				if (HANDSET_LOGIN_TYPE.equals(type)) { //正常流程
					if (first) { // last_type == null
						dto = new WifiHandsetDeviceItemDetailMDTO();
						dto.setLogin_at(ts);
						dto.setLogout_at(0);
						dto.setRx_bytes(0);
						mdtos.add(dto);
					} else {
						dto = mdtos.get(mdtos.size() - 1);
						dto.setLogin_at(ts);
					}
				}

				if (HANDSET_LOGOUT_TYPE.equals(type)) { //正常流程

					if (first) { //last_type == null
						dto = new WifiHandsetDeviceItemDetailMDTO();
						dto.setLogout_at(ts);
						dto.setRx_bytes(rx_bytes);
						mdtos.add(dto);
					} else {
						if (last_ts - ts < IGNORE_LOGIN_TIME_SPACE) { //小于15分钟的记录
							//忽略操作
//							logger.info("ignore 15 min" + (ts - last_ts));
							dto = mdtos.get(mdtos.size() - 1);
							if (flag) { //如果不在线的话就累加
								dto.setRx_bytes(0);
							} else {
								dto.setRx_bytes(dto.getRx_bytes() + rx_bytes);
							}
						} else {
							dto = new WifiHandsetDeviceItemDetailMDTO();
							dto.setLogout_at(ts);
							dto.setRx_bytes(rx_bytes);
							mdtos.add(dto);
							flag = false;
						}

					}
				}
//				logger.info("[mdtos]" + mdtos.size());
				vto.setDetail(mdtos);

			}

			if (j > 0) {

				WifiHandsetDeviceItemDetailMDTO dto = null;

				if (first) { //隔天的第一条记录
					URouterHdTimeLineVTO vto = vtos.get(offset + 1); //更新logs
					List<WifiHandsetDeviceItemDetailMDTO> mdtos = vto.getDetail();
					if (mdtos == null) {
						mdtos = new ArrayList<WifiHandsetDeviceItemDetailMDTO>();
					}

					if (HANDSET_LOGOUT_TYPE.equals(type)) {
						dto = new WifiHandsetDeviceItemDetailMDTO();
						dto.setLogout_at(ts);
						dto.setRx_bytes(rx_bytes);
						mdtos.add(dto);
					}

					if (HANDSET_LOGIN_TYPE.equals(type)) {
						//忽略
					}

				}

				if (HANDSET_LOGOUT_TYPE.equals(type) && HANDSET_LOGIN_TYPE.equals(last_type)) { //如果上一次正常退出

					//有隔天记录的拆分第一条记录 >>>
					URouterHdTimeLineVTO vto = vtos.get(offset + 1); //更新logs
//					logger.info("date===date[" + vto.getDate() + "]");
//					List<WifiHandsetDeviceItemDetailMDTO> mdtos = vto.getLogs(); //
					List<WifiHandsetDeviceItemDetailMDTO> mdtos = vto.getDetail();
					if (mdtos == null) {
						mdtos = new ArrayList<WifiHandsetDeviceItemDetailMDTO>();
					}

					dto = new WifiHandsetDeviceItemDetailMDTO();
					dto.setLogout_at(ts);
					dto.setRx_bytes(rx_bytes);
					mdtos.add(dto);
					vto.setDetail(mdtos);
				}


				//隔天记录
				//补齐上一天的最后一条login记录为00:00:00，当天的第一条记录11:59:59
				if (HANDSET_LOGIN_TYPE.equals(type) && HANDSET_LOGOUT_TYPE.equals(last_type)) { ////隔天仍在线

					//如果j >1 的时候 offset >= 0
					for (int i = 1; i< j + 1 ; i++) {
						// >>> j == 1
						logger.info("iiiii===" + i);
						URouterHdTimeLineVTO vto_ = vtos.get(offset - (j - i ));
						List<WifiHandsetDeviceItemDetailMDTO> mdtos_ = vto_.getDetail();
						if (mdtos_ == null) {
							mdtos_ = new ArrayList<WifiHandsetDeviceItemDetailMDTO>();
						}
						WifiHandsetDeviceItemDetailMDTO dto_ = mdtos_.get(mdtos_.size() - 1);
						long login_at_zero = DateTimeHelper.parseDate(vto_.getDate(), DateTimeHelper.shortDateFormat).getTime();
						dto_.setLogin_at(login_at_zero); //补齐零点登入
						vto_.setDetail(mdtos_);
						// <<<

						// >>>
						URouterHdTimeLineVTO vto = vtos.get(offset - (j - i ) + 1);
						List<WifiHandsetDeviceItemDetailMDTO> mdtos = vto.getDetail();

						dto = new WifiHandsetDeviceItemDetailMDTO();
						dto.setLogout_at(login_at_zero - 1);  //补齐零点登出
						dto.setRx_bytes(rx_bytes);

//						logger.info("set ts ==" + ts + ",i===" + i);
						if (i == j) {
							dto.setLogin_at(ts);  //如果最后一次的话添加一个登录时间
//							logger.info("set login_ ts ==" + ts);
						}
//						if (offset + 1 + i >= 6)  {
//							dto.setLogin_at(ts);  //如果最后一次的话添加一个登录时间
//							mdtos.add(dto);
//							vto.setDetail(mdtos);
//							break;
//						}


						mdtos.add(dto);
						vto.setDetail(mdtos);
						// <<< j == 1
					}

				}

				//vto.setLogs(mdtos);

				//有隔天记录的拆分第一条记录 <<<
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return flag;
	}


	private Date getDateZeroTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		//时分秒（毫秒数）
		long millisecond = hour*60*60*1000 + minute*60*1000 + second*1000;
		//凌晨00:00:00
		cal.setTimeInMillis(cal.getTimeInMillis()-millisecond);

		return cal.getTime();
	}

	
	/**
	 * 获取设备的实时速率
	 * a:如果存在数据null 下发实时速率指令 
	 * b:如果存在数据waiting 什么都不做
	 * c:如果存在数据非ab 返回数据
	 * @param uid
	 * @param wifiId
	 * @return
	 */
	public RpcResponseDTO<URouterRealtimeRateVTO> urouterRealtimeRate(Integer uid, String wifiId){
		try{
			deviceFacadeService.validateUserDevice(uid, wifiId);
		
			URouterRealtimeRateVTO vto = new URouterRealtimeRateVTO();
			String[] ret = fetchRealtimeRate(wifiId);
			vto.setTx_rate(ret[0]);
			vto.setRx_rate(ret[1]);
			vto.setTs(System.currentTimeMillis());
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}
	
	/**
	 * 调用设备的网速测试
	 * 下发指令进行设备测速
	 * @param uid
	 * @param wifiId
	 * @param type 测速类型
	 * @param period 测速数据上报间隔
	 * @param duration 测速时长
	 * @return
	 */
	public RpcResponseDTO<Boolean> urouterPeakSection(Integer uid, String wifiId, int type, int period, int duration){
		try{
			deviceFacadeService.validateUserDevice(uid, wifiId);
		
			deliverMessageService.sendQueryDeviceSpeedFetchActionMessage(wifiId, type, period, duration);
			
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}
	
	/**
	 * 获取设备测速分段数据
	 * @param uid
	 * @param wifiId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public RpcResponseDTO<URouterPeakSectionsVTO> urouterPeakSectionFetch(Integer uid, String wifiId){
		try{
			deviceFacadeService.validateUserDevice(uid, wifiId);
			
			URouterPeakSectionsVTO vto = new URouterPeakSectionsVTO();
			Object[] rets = WifiDeviceRealtimeRateStatisticsStringService.getInstance().getPeakSections(wifiId);
			if(rets != null && rets.length == 2){
				if(rets[0] != null){
					vto.setRxs((List<WifiDeviceRxPeakSectionDTO>)rets[0]);
				}
				if(rets[1] != null){
					vto.setTxs((List<WifiDeviceTxPeakSectionDTO>)rets[1]);
				}
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}
	
	/**
	 * 获取设备的实时上行行速率
	 * @param wifiId
	 * @return 0：上行速率 1：下行速率
	 */
	public String[] fetchRealtimeRate(String wifiId){
		List<String> result = WifiDeviceRealtimeRateStatisticsStringService.getInstance().getRate(wifiId);
		String waiting = result.get(4);
		//如果waiting没有标记 则发送指令查询
		if(StringUtils.isEmpty(waiting)){
			//调用异步消息下发实时速率指令
			deliverMessageService.sendDeviceRealtimeRateFetchActionMessage(wifiId);
		}
		
		//获取实时的上下行速率
		String realtime_tx_rate = result.get(0);
		String realtime_rx_rate = result.get(1);
		//String rx_peak_rate = result.get(5);
		//如果有实时速率数据 就直接返回
		if(!StringUtils.isEmpty(realtime_tx_rate) && !StringUtils.isEmpty(realtime_rx_rate)){
			return new String[]{realtime_tx_rate, realtime_rx_rate};
		}
		
		//返回last的速率数据
		String last_tx_rate = result.get(2);
		String last_rx_rate = result.get(3);
		return new String[]{last_tx_rate, last_rx_rate};
	}
	
	/**
	 * 获取设备的网速测试数据
	 * @param wifiId
	 * @return
	 */
/*	public String fetchPeakRate(String wifiId){
		List<String> result = WifiDeviceRealtimeRateStatisticsStringService.getInstance().getPeak(wifiId);
		String peak_rate = result.get(0);
		String peak_rate_waiting = result.get(1);
		//如果waiting没有标记 则发送指令查询
		if(StringUtils.isEmpty(peak_rate_waiting)){
			//调用异步消息下发网速测试指令
			deliverMessageService.sendQueryDeviceSpeedFetchActionMessage(wifiId);
		}
		return peak_rate;
	}*/
	
	/**
	 * 获取黑名单列表数据
	 * @param uid
	 * @param wifiId
	 * @return
	 * modified by Edmond Lee for handset storage
	 */
	public RpcResponseDTO<Map<String,Object>> urouterBlockList(Integer uid, String wifiId, int start, int size){
		try{
			deviceFacadeService.validateUserDevice(uid, wifiId);
			WifiDeviceSetting entity = deviceFacadeService.validateDeviceSetting(wifiId);
			
			List<URouterHdVTO> vtos = null;
			int total = 0;
			
			WifiDeviceSettingDTO dto = entity.getInnerModel();
			WifiDeviceSettingAclDTO acl_dto = DeviceHelper.matchDefaultAcl(dto);
			if(acl_dto != null){
				List<String> block_hd_macs_all = acl_dto.getMacs();
				if(block_hd_macs_all != null && !block_hd_macs_all.isEmpty()){
					total = block_hd_macs_all.size();
					List<String> block_hd_macs = PageHelper.partialList(acl_dto.getMacs(), start, size);
					
//					List<WifiHandsetDeviceMarkPK> mark_pks = BusinessModelBuilder.toWifiHandsetDeviceMarkPKs(wifiId, block_hd_macs);
					//List<HandsetDevice> hd_entitys = handsetDeviceService.findByIds(block_hd_macs, true, true);
					List<HandsetDeviceDTO> handsets = HandsetStorageFacadeService.handsets(block_hd_macs);
					if(!block_hd_macs.isEmpty()){
						vtos = new ArrayList<URouterHdVTO>();
//						List<WifiHandsetDeviceMark> mark_entitys = wifiHandsetDeviceMarkService.findByIds(mark_pks, true, true);
						WifiDeviceSettingDTO setting_dto = entity.getInnerModel();
						int cursor = 0;
						for(String block_hd_mac : block_hd_macs){
							URouterHdVTO vto = BusinessModelBuilder.toURouterHdVTO(uid, block_hd_mac, false, handsets.get(cursor), setting_dto);
							vtos.add(vto);
							cursor++;
						}
					}
				}
			}
			if(vtos == null)
				vtos = Collections.emptyList();
			
			Map<String, Object> payload = PageHelper.partialAllList(vtos, total, start, size);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(payload);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}
	
	/**
	 * 获取设备设置信息
	 * @param uid
	 * @param wifiId
	 * @return
	 */
	public RpcResponseDTO<URouterSettingVTO> urouterSetting(Integer uid, String wifiId){
		try{
			WifiDevice device_entity = deviceFacadeService.validateUserDevice(uid, wifiId);
			WifiDeviceSetting setting_entity = deviceFacadeService.validateDeviceSetting(wifiId);
			
			WifiDeviceSettingDTO setting_dto = setting_entity.getInnerModel();
			
			URouterSettingVTO vto = new URouterSettingVTO();
			vto.setMac(device_entity.getId());
			vto.setOem_swver(device_entity.getOem_swver());
			vto.setOem_hdver(device_entity.getOem_hdver());
			vto.setOl(device_entity.isOnline());

			String last_start_at = device_entity.getLast_start_at();
			long currentTime = System.currentTimeMillis();
			long uptime = 0;
			if (last_start_at != null) {
				uptime = currentTime - Long.parseLong(last_start_at);
			}
			
			vto.setUptime(String.valueOf(uptime)); //last_start_at -> uptime
			vto.setWan_ip(device_entity.getWan_ip());
			vto.setIp(device_entity.getIp());
			//vto.setMode(DeviceHelper.getDeviceMode(setting_dto));
			//获取正常的vap COMMENT
			WifiDeviceSettingVapDTO normal_vap = DeviceHelper.getUrouterDeviceVap(setting_dto);
			if(normal_vap != null){
				vto.setVap_auth(normal_vap.getAuth());
				vto.setVap_name(normal_vap.getName());
				vto.setVap_ssid(normal_vap.getSsid());
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}
	
	/**
	 * 获取智能插件设置数据
	 * @param uid
	 * @param wifiId
	 * @return
	 */
	public RpcResponseDTO<Map<String,Object>> urouterPlugins(Integer uid, String wifiId){
		try{
			deviceFacadeService.validateUserDevice(uid, wifiId);
			
			UserSettingState user_setting_entity = userSettingStateService.getById(wifiId);
			if(user_setting_entity == null){
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST);
			}
			
			Map<String,Object> ret = new HashMap<String,Object>();
			//终端上线通知插件
			this.urouterPlugins4TerminalOnline(user_setting_entity, ret);
			
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(ret);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}
	
	/**
	 * 设置智能插件 终端上线设置数据
	 * @param uid
	 * @param wifiId
	 * @param on
	 * @param stranger_on
	 * @param timeslot
	 * @param timeslot_mode
	 * @return
	 */
	public RpcResponseDTO<Boolean> urouterUpdPluginTerminalOnline(Integer uid,
			String wifiId, boolean on, boolean stranger_on, boolean alias_on, String timeslot,
			int timeslot_mode){
		try{
			deviceFacadeService.validateUserDevice(uid, wifiId);
			
			UserSettingState user_setting_entity = userSettingStateService.getById(wifiId);
			
			UserTerminalOnlineSettingDTO uto_dto = new UserTerminalOnlineSettingDTO();
			uto_dto.setOn(on);
			uto_dto.setStranger_on(stranger_on);
			uto_dto.setAlias_on(alias_on);
			uto_dto.setTimeslot(timeslot);
			uto_dto.setTimeslot_mode(timeslot_mode);
			
			user_setting_entity.putUserSetting(uto_dto);
			userSettingStateService.update(user_setting_entity);
			
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}
	
	/**
	 * 设置智能插件 开启关闭wifisniffer 终端探测
	 * @param uid
	 * @param wifiId
	 * @param on
	 * @return
	 */
	public RpcResponseDTO<Boolean> urouterUpdPluginWifisniffer(Integer uid,
			String wifiId, boolean on){
		try{
			deviceFacadeService.validateUserDevice(uid, wifiId);
			UserSettingState user_setting_entity = userSettingStateService.getById(wifiId);
			
			UserWifiSinfferSettingDTO uto_dto = new UserWifiSinfferSettingDTO();
			uto_dto.setOn(on);
			user_setting_entity.putUserSetting(uto_dto);
			userSettingStateService.update(user_setting_entity);
			deliverMessageService.sendWifiCmdsCommingNotifyMessage(
					wifiId,/*0,OperationCMD.ParamWifiSinffer.getNo(),*/
					CMDBuilder.builderDeviceWifiSnifferSetting(wifiId,
							on ? WifiDeviceHelper.WifiSniffer_Start_Sta_Sniffer:WifiDeviceHelper.WifiSniffer_Stop_Sta_Sniffer));
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}
	
	public RpcResponseDTO<DeviceUsedStatisticsDTO> urouterDeviceUsedStatusQuery(Integer uid,String wifiId){
		try{
			deviceFacadeService.validateUserDevice(uid, wifiId);
			DeviceUsedStatisticsDTO dto = BusinessMarkerService.getInstance().deviceUsedStatisticsGet(wifiId);
			boolean needNewRequestAndMarker = BusinessMarkerService.getInstance().needNewRequestAndMarker(wifiId,true);
			if(needNewRequestAndMarker){
				deliverMessageService.sendWifiCmdsCommingNotifyMessage(
						wifiId,/*0,OperationCMD.QueryDeviceUsedStatus.getNo(),*/
						CMDBuilder.builderDeviceUsedStatusQuery(wifiId));
			}
			if(dto == null){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_DATA_NOTEXIST);
			}else
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(dto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}
	
	
	/**
	 * 插件数据 
	 * 	终端上线通知
	 *  终端wifi定时开关
	 *  终端wifi终端探测
	 * @param user_setting_entity
	 * @return
	 */
	protected void urouterPlugins4TerminalOnline(UserSettingState user_setting_entity,
			Map<String,Object> ret){
		//终端上线设置数据
		UserTerminalOnlineSettingDTO uto_dto = user_setting_entity.getUserSetting(UserTerminalOnlineSettingDTO.
				Setting_Key, UserTerminalOnlineSettingDTO.class);
		//定时开关设置数据
		UserWifiTimerSettingDTO uwt_dto = user_setting_entity.getUserSetting(UserWifiTimerSettingDTO.
				Setting_Key, UserWifiTimerSettingDTO.class);
		uwt_dto.setEnable(uwt_dto.wifiCurrentEnable());
		//周边探测设置数据
		WifiSinfferSettingVTO uws_vto = new WifiSinfferSettingVTO();
		UserWifiSinfferSettingDTO uws_dto = user_setting_entity.getUserSetting(UserWifiSinfferSettingDTO.
				Setting_Key, UserWifiSinfferSettingDTO.class);
		if(uws_dto != null){
			uws_vto.setOn(uws_dto.isOn());
			//获取最近12小时出现的终端数量
			long current_ts = System.currentTimeMillis();
			long hours12ago_ts = current_ts - (12 * 3600 * 1000l);
			long recent12hours_count = TerminalRecentSortedSetService.getInstance().sizeByScore(user_setting_entity.getId(), 
					hours12ago_ts, current_ts);
			uws_vto.setRecent_c(recent12hours_count);
		}
		/*if(uto_dto == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST);
		}*/
		if(uto_dto != null)
			ret.put(UserTerminalOnlineSettingDTO.Setting_Key, uto_dto);
		if(uwt_dto != null)
			ret.put(UserWifiTimerSettingDTO.Setting_Key, uwt_dto);
		if(uws_dto != null)
			ret.put(UserWifiSinfferSettingDTO.Setting_Key, uws_vto);
		
	}
	
	/**
	 * 获取设备的上网方式设置
	 * @param uid
	 * @param wifiId
	 * @return
	 */
	public RpcResponseDTO<URouterModeVTO> urouterLinkMode(Integer uid, String wifiId){
		try{
			deviceFacadeService.validateUserDevice(uid, wifiId);
//			WifiDeviceSetting setting_entity = deviceFacadeService.validateDeviceSetting(wifiId);
			
			URouterModeVTO vto = new URouterModeVTO();
			
			WifiDeviceSettingLinkModeDTO mode_dto = deviceFacadeService.getDeviceModeStatus(wifiId);
			if(mode_dto != null){
				vto.setIp(mode_dto.getIp());
				vto.setMode(DeviceHelper.getDeviceMode(mode_dto.getModel()));
				vto.setNetmask(mode_dto.getNetmask());
				vto.setP_un(mode_dto.getUsername());
				vto.setP_pwd(JNIRsaHelper.jniRsaDecryptHexStr(mode_dto.getPassword_rsa()));
				vto.setGateway(mode_dto.getGateway());
				vto.setDns(mode_dto.getDns());
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}

	/**
	 * 获取urouter相关的设备配置数据
	 * @param uid
	 * @param mac
	 * @return
	 */
	public RpcResponseDTO<URouterDeviceConfigVTO> urouterConfigs(Integer uid, String mac) {
		try{
			WifiDevice device_entity = deviceFacadeService.validateUserDevice(uid, mac);
			WifiDeviceSetting entity = deviceFacadeService.validateDeviceSetting(mac);
			WifiDeviceSettingDTO setting_dto = entity.getInnerModel();
			
			URouterDeviceConfigVTO vto = new URouterDeviceConfigVTO();
			//获取正常的vap
			WifiDeviceSettingVapDTO normal_vap = DeviceHelper.getUrouterDeviceVap(setting_dto);
			if(normal_vap != null){
				vto.setVap_auth(normal_vap.getAuth());
				vto.setVap_name(normal_vap.getName());
				vto.setVap_ssid(normal_vap.getSsid());
				vto.setVap_pwd(JNIRsaHelper.jniRsaDecryptHexStr(normal_vap.getAuth_key_rsa()));
			}
			//黑名单列表
			WifiDeviceSettingAclDTO acl_dto = DeviceHelper.matchDefaultAcl(setting_dto);
			if(acl_dto != null){
				List<String> macs = acl_dto.getMacs();
				
				if(macs != null && !macs.isEmpty()){
					//老版本app的返回值
					vto.setBlock_macs(macs);
					//新版本app的返回值
					List<URouterDeviceConfigNVTO> block_with_names = new ArrayList<URouterDeviceConfigNVTO>();
					int i = 0;
					List<HandsetDeviceDTO> handsets = HandsetStorageFacadeService.handsets(macs);
					for (String dto_mac: macs) {
						URouterDeviceConfigNVTO nvto = new URouterDeviceConfigNVTO();
						nvto.setMac(dto_mac);
						HandsetDeviceDTO dto = handsets.get(i);
						if (dto != null) {
							nvto.setN(dto.getDhcp_name());
						}
						i++;
						block_with_names.add(nvto);
					}
					vto.setBlock_with_names(block_with_names);
				}
			}
			//终端别名
			List<WifiDeviceSettingMMDTO> mm_dtos = setting_dto.getMms();
			if(mm_dtos != null){
				List<URouterDeviceConfigMMVTO> mm_vtos = new ArrayList<URouterDeviceConfigMMVTO>();
				for(WifiDeviceSettingMMDTO mm_dto : mm_dtos){
					mm_vtos.add(new URouterDeviceConfigMMVTO(mm_dto.getMac(), mm_dto.getName()));
				}
				vto.setMms(mm_vtos);
			}
			//终端限速
			List<WifiDeviceSettingRateControlDTO> rateControls = setting_dto.getRatecontrols();
			if(rateControls != null){
				List<URouterDeviceConfigRateControlVTO> rcs_vtos = new ArrayList<URouterDeviceConfigRateControlVTO>();
				for(WifiDeviceSettingRateControlDTO rc_dto : rateControls){
					URouterDeviceConfigRateControlVTO rc = new URouterDeviceConfigRateControlVTO();
					rc.setMac(rc_dto.getMac());
					if(!StringUtils.isEmpty(rc_dto.getRx()))
						rc.setTx_limit(ArithHelper.unitConversionDoKbpsTobps(rc_dto.getRx()));
					if(!StringUtils.isEmpty(rc_dto.getTx()))
						rc.setRx_limit(ArithHelper.unitConversionDoKbpsTobps(rc_dto.getTx()));
					rcs_vtos.add(rc);
				}
				vto.setRcs(rcs_vtos);
			}
			//信号强度和当前信道
			String[] powerAndRealChannel = DeviceHelper.getURouterDevicePowerAndRealChannel(setting_dto);
			vto.setPower(Integer.parseInt(powerAndRealChannel[0]));
			vto.setReal_channel(Integer.parseInt(powerAndRealChannel[1]));
			//admin密码
			WifiDeviceSettingUserDTO admin_user_dto = DeviceHelper.getURouterDeviceAdminUser(setting_dto);
			if(admin_user_dto != null){
				vto.setAdmin_pwd(JNIRsaHelper.jniRsaDecryptHexStr(admin_user_dto.getPassword_rsa()));
			}
			//上网方式
			WifiDeviceSettingLinkModeDTO mode_dto = deviceFacadeService.getDeviceModeStatus(mac);
			if(mode_dto != null){
				URouterModeVTO link_vto = new URouterModeVTO();
				link_vto.setIp(mode_dto.getIp());
				link_vto.setMode(DeviceHelper.getDeviceMode(mode_dto.getModel()));
				link_vto.setNetmask(mode_dto.getNetmask());
				link_vto.setP_un(mode_dto.getUsername());
				link_vto.setP_pwd(JNIRsaHelper.jniRsaDecryptHexStr(mode_dto.getPassword_rsa()));
				link_vto.setGateway(mode_dto.getGateway());
				link_vto.setDns(mode_dto.getDns());
				vto.setLinkmode(link_vto);
			}
			//设备基本信息
			URouterInfoVTO info_vto = new URouterInfoVTO();
			info_vto.setWan_ip(device_entity.getWan_ip());
			info_vto.setAdr(device_entity.getFormatted_address());
			info_vto.setCarrier(device_entity.getCarrier());
			vto.setInfo(info_vto);
			
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		}catch(BusinessI18nCodeException bex){
			bex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}
	
	/**
	 * 获取终端的主机名
	 * @param uid
	 * @param macs
	 * @return
	 * 
	 * modified by Edmond Lee for handset storage
	 */
	public RpcResponseDTO<List<URouterHdHostNameVTO>> terminalHostnames(Integer uid, String macs) {
		try{
			List<URouterHdHostNameVTO> vto_list = null;
			if(StringUtils.isEmpty(macs)){
				vto_list = Collections.emptyList();
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto_list);
			}

			String[] macs_array = macs.split(StringHelper.COMMA_STRING_GAP);
			vto_list = new ArrayList<URouterHdHostNameVTO>(macs_array.length);
			List<HandsetDeviceDTO> handsets = HandsetStorageFacadeService.handsets(ArrayHelper.toList(macs_array));
			//List<HandsetDevice> entitys = handsetDeviceService.findByIds(ArrayHelper.toList(macs_array), true, true);
			int cursor = 0;
			//for(HandsetDevice entity : entitys){
			for(HandsetDeviceDTO entity : handsets){
				URouterHdHostNameVTO vto = new URouterHdHostNameVTO();
				if(entity != null){
					vto.setHd_mac(entity.getMac());
					vto.setHn(entity.getDhcp_name());
				}else{
					vto.setHd_mac(macs_array[cursor]);
				}
				vto_list.add(vto);
				cursor++;
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto_list);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}
	
	/**
	 * 周边探测的最近出现列表
	 * 最近12小时内出现的终端
	 * @param uid
	 * @param mac
	 * @param start
	 * @param size
	 * @return
	 */
	public RpcResponseDTO<Map<String, Object>> urouterWSRecent(Integer uid, String mac, int start, int size){
		try{
			//System.out.println("step1");
			if(StringUtils.isEmpty(mac)){
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(null);
			}
			List<URouterWSRecentVTO> vto_list = null;
			//当前时间
			long current_ts = System.currentTimeMillis();
			//12小时之前的时间
			//long hours12_ago_ts = current_ts - (12 * 3600 * 1000l);
			long hours12_ago_ts = 0;
			//System.out.println("step2");
			long count = TerminalRecentSortedSetService.getInstance().sizeByScore(mac, hours12_ago_ts, current_ts);
			if(count == 0){
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(null);
			}
			//System.out.println("step3:"+count);
			Set<Tuple> tuples = TerminalRecentSortedSetService.getInstance().fetchTerminalRecentByScoreWithScores(mac, 
					hours12_ago_ts, current_ts, start, size);
			
			vto_list = new ArrayList<URouterWSRecentVTO>();
			for(Tuple tuple : tuples){
				URouterWSRecentVTO vto = new URouterWSRecentVTO();
				vto.setHd_mac(tuple.getElement());
				vto.setLast_snifftime(Double.valueOf(tuple.getScore()).longValue());
				vto.setTt(MacDictParserFilterHelper.prefixMactch(tuple.getElement(),true,false));
				vto_list.add(vto);
			}
			//System.out.println("step4:"+vto_list.size());
			Map<String, Object> payload = PageHelper.partialAllList(vto_list, count, start, size);
			//System.out.println("step5:"+payload);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(payload);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}
	
	/**
	 * 周边探测的隔壁老王列表
	 * @param uid
	 * @param mac
	 * @param start
	 * @param size
	 * @return
	 */
	public RpcResponseDTO<Map<String, Object>> urouterWSNeighbour(Integer uid, String mac, int start, int size){
		try{
			if(StringUtils.isEmpty(mac)){
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(null);
			}

			long count = TerminalHotSortedSetService.getInstance().terminalHotSize(mac);
			if(count == 0){
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(null);
			}
			Set<Tuple> tuples = TerminalHotSortedSetService.getInstance().fetchTerminalHotWithScores(mac, 
					start, size);
			
			List<URouterWSHotVTO> vto_list = new ArrayList<URouterWSHotVTO>();
			
			String[] hd_macs = BusinessModelBuilder.toElementsArray(tuples);
			List<String> last_time_strings = TerminalLastTimeStringService.getInstance().getMulti(mac, hd_macs);
			List<UserTerminalFocusDTO> focus_dtos = UserTerminalFocusHashService.getInstance().fetchUserTerminalFocus(uid, hd_macs);
			int cursor = 0;
			for(Tuple tuple : tuples){
				URouterWSHotVTO vto = new URouterWSHotVTO();
				vto.setHd_mac(hd_macs[cursor]);
				vto.setWs_count(Double.valueOf(tuple.getScore()).longValue());
				vto.setTt(MacDictParserFilterHelper.prefixMactch(hd_macs[cursor],true,false));
				UserTerminalFocusDTO focus_dto = focus_dtos.get(cursor);
				if(focus_dto != null){
					vto.setNick(focus_dto.getNick());
					vto.setFocus(focus_dto.isFocus());
				}
				String last_time = last_time_strings.get(cursor);
				if(!StringUtils.isEmpty(last_time)){
					vto.setLast_snifftime(Long.parseLong(last_time));
				}
				vto_list.add(vto);
				cursor++;
			}
			Map<String, Object> payload = PageHelper.partialAllList(vto_list, count, start, size);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(payload);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}
	
	/**
	 * 用户设置关注或者取消关注终端
	 * @param uid
	 * @param hd_mac
	 * @param focus
	 * @return
	 */
	public RpcResponseDTO<Boolean> urouterWSFocus(Integer uid, String hd_mac, boolean focus){
		try{
			UserTerminalFocusHashService.getInstance().setFocusValue(uid, hd_mac, focus);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}
	
	/**
	 * 用户修改探测终端的昵称
	 * @param uid
	 * @param hd_mac
	 * @param nick
	 * @return
	 */
	public RpcResponseDTO<Boolean> urouterWSNick(Integer uid, String hd_mac, String nick){
		try{
			UserTerminalFocusHashService.getInstance().setNickValue(uid, hd_mac, nick);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}
	
	/**
	 * 终端探测的细节列表
	 * @param uid
	 * @param mac
	 * @param hd_mac
	 * @param start
	 * @param size
	 * @return
	 */
	public RpcResponseDTO<Map<String,Object>> urouterWSDetails(Integer uid, String mac, String hd_mac, int start, int size) {
		try{
			
			long count = TerminalDetailRecentSortedSetService.getInstance().terminalDetailRecentSize(mac, hd_mac);
			if(count == 0){
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(null);
			}
			Set<String> rets = TerminalDetailRecentSortedSetService.getInstance().fetchTerminalDetailRecent(mac, hd_mac, start, size);
			if(rets == null || rets.isEmpty()){
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(null);
			}
			List<TerminalDetailDTO> dtos = new ArrayList<TerminalDetailDTO>();
			
			int index = 0;
			for(String ret : rets){
				if(!StringUtils.isEmpty(ret)){
					TerminalDetailDTO dto = JsonHelper.getDTO(ret, TerminalDetailDTO.class);
					//设备报送周边探测有可能出现只有上线，无下线消息的情况 此处判断如果不是最新探测到的细节 其他细节均显示为离线
					if(index > 0){
						dto.setState(WifistasnifferItemRddto.State_Offline);
					}else{
						//针对最新一条数据的无下线消息情况 此处终端探测的上线时间 超过timeout时间 则认为是离线
						long life_time = System.currentTimeMillis() - dto.getSnifftime();
						if(WifistasnifferItemRddto.State_Offline_TimeoutMs - life_time <= 0){
							dto.setState(WifistasnifferItemRddto.State_Offline);
						}
					}
					dtos.add(dto);
				}
				index++;
			}
			Map<String, Object> payload = PageHelper.partialAllList(dtos, count, start, size);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(payload);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}
	
	/**
	 * 设备的周边社区类型以及周边探测终端类型对应的数量数据
	 * @param uid
	 * @param mac
	 * @return
	 */
	public RpcResponseDTO<URouterWSCommunityVTO> urouterWSCommunity(Integer uid, String mac) {
		try{
			Map<String, String> communityCountByTypes = TerminalDeviceTypeCountHashService.getInstance().getAll(mac);
			if(communityCountByTypes == null || communityCountByTypes.isEmpty()){
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(null);
			}
			URouterWSCommunityVTO vto = WifiStasnifferHelper.communityType(communityCountByTypes);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}
	
	/**
	 * urouter用户注册app移动设备信息
	 * @param uid
	 * @param d  devicetype
	 * @param dt 设备token
	 * @param dm 设备mac
	 * @param cv client 系统版本号
	 * @param pv client production 版本号
	 * @param ut 设备型号
	 * @param pt (push type 针对ios的不同证书的参数)
	 * @return
	 */
	public RpcResponseDTO<Boolean> urouterUserMobileDeviceRegister(Integer uid,
			String d, String dt, String dm, String cv, String pv, String ut, String pt) {
		try{
			deviceFacadeService.userMobileDeviceRegister(uid, d, dt, dm, cv, pv, ut, pt);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}
	
	/**
	 * 注销用户目前登录的设备及设备token
	 * @param uid
	 * @param d
	 * @param dt
	 * @return
	 */
	public RpcResponseDTO<Boolean> urouterUserMobileDeviceDestory(Integer uid,
			String d, String dt) {
		try{
			deviceFacadeService.userMobileDeviceDestory(uid, d, dt);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}


	public RpcResponseDTO<URouterAdminPasswordVTO> urouterAdminPassword(Integer uid, String wifiId) {
		try {

			deviceFacadeService.validateUserDevice(uid, wifiId);
			WifiDeviceSetting wifiDeviceSetting = deviceFacadeService.validateDeviceSetting(wifiId);

			URouterAdminPasswordVTO uRouterAdminPasswordVTO = new URouterAdminPasswordVTO();
			if (wifiDeviceSetting != null) {
				WifiDeviceSettingDTO wifiDeviceSettingDTO = wifiDeviceSetting.getInnerModel();

				WifiDeviceSettingUserDTO wifiDeviceSettingUserDTO =
						DeviceHelper.getURouterDeviceAdminUser(wifiDeviceSettingDTO);

				if (wifiDeviceSettingUserDTO !=null) {
					uRouterAdminPasswordVTO.setPassword(
							JNIRsaHelper.jniRsaDecryptHexStr(wifiDeviceSettingUserDTO.getPassword_rsa()));
				}

			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(uRouterAdminPasswordVTO);
		} catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}

	/**
	 * 获取vap密码，暂时urooter默认只取第一个
	 * @param uid
	 * @param wifiId
	 * @return
	 */
	public RpcResponseDTO<URouterVapPasswordVTO> urouterVapPassword(Integer uid, String wifiId) {
		try {

			deviceFacadeService.validateUserDevice(uid, wifiId);
			WifiDeviceSetting wifiDeviceSetting = deviceFacadeService.validateDeviceSetting(wifiId);
			URouterVapPasswordVTO uRouterVapPasswordVTO = new URouterVapPasswordVTO();
			if (wifiDeviceSetting != null) {
				WifiDeviceSettingDTO wifiDeviceSettingDTO = wifiDeviceSetting.getInnerModel();
//				List<WifiDeviceSettingVapDTO> wifiDeviceSettingVapDTOList   = wifiDeviceSettingDTO.getVaps();


				WifiDeviceSettingVapDTO frist_vap_dto = DeviceHelper.getUrouterDeviceVap(wifiDeviceSettingDTO);
				//如果没有一个可用的vap
				if(frist_vap_dto == null)
					throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_SETTING_ERROR);

				uRouterVapPasswordVTO.setPassword(
						JNIRsaHelper.jniRsaDecryptHexStr(frist_vap_dto.getAuth_key_rsa()));
				uRouterVapPasswordVTO.setSsid(frist_vap_dto.getSsid());
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(uRouterVapPasswordVTO);

			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(uRouterVapPasswordVTO);
		} catch(BusinessI18nCodeException bex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}

	}

}
