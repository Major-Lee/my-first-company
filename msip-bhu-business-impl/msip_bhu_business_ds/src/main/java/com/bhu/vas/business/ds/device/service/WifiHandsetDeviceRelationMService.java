package com.bhu.vas.business.ds.device.service;

import java.util.*;

import javax.annotation.Resource;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.mdto.WifiHandsetDeviceItemDetailMDTO;
import com.bhu.vas.api.mdto.WifiHandsetDeviceItemLogMDTO;
import com.smartwork.msip.cores.helper.DateTimeExtHelper;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bhu.vas.business.ds.device.dao.WifiHandsetDeviceRelationMDao;
import com.bhu.vas.business.ds.device.mdto.WifiHandsetDeviceRelationMDTO;
import com.mongodb.WriteResult;
import com.smartwork.msip.cores.cache.relationcache.impl.springmongo.Pagination;
import com.smartwork.msip.cores.helper.DateTimeHelper;

/**
 * 移动设备接入wifi设备的接入记录 (mongodb)
 * 一个移动设备接入同一个wifi设备多次，只有一条记录
 * id = wifiId_handsetId (无流水id)
 * @author tangzichao
 *
 */
@Service
public class WifiHandsetDeviceRelationMService {
	
	@Resource
	private WifiHandsetDeviceRelationMDao wifiHandsetDeviceRelationMDao;
	
//	@Resource
//	private SequenceMDao sequenceMDao;
	
//	public void addRelation(String handsetId, String wifiId, Date login_at){
//		if(StringUtils.isEmpty(handsetId) || StringUtils.isEmpty(wifiId)) return;
//		if(login_at == null) login_at = new Date();
//		
//		WifiHandsetDeviceRelationMDTO mdto = new WifiHandsetDeviceRelationMDTO();
//		mdto.setId(sequenceMDao.getNextSequenceId(WifiHandsetDeviceRelationMDTO.class.getName()));
//		mdto.setHandsetId(handsetId);
//		mdto.setWifiId(wifiId);
//		mdto.setLogin_at(login_at);
//		mdto = wifiHandsetDeviceRelationMDao.save(mdto);
//	}
	
//	public void saveRelation(String wifiId, String handsetId, Date last_login_at){
//		WifiHandsetDeviceRelationMDTO mdto = new WifiHandsetDeviceRelationMDTO(wifiId, handsetId);
//		mdto.setLast_login_at(last_login_at);
//		wifiHandsetDeviceRelationMDao.save(mdto);
//	}
	public static final int AddRelation_Insert = 1;
	public static final int AddRelation_Update = 2;
	public static final int AddRelation_Fail = -1;

    /**
     *
     * @param wifiId
     * @param handsetId
     * @param last_login_at
     * @return
     */
	public int addRelation(String wifiId, String handsetId, Date last_login_at){
		if(StringUtils.isEmpty(wifiId) || StringUtils.isEmpty(handsetId)) return AddRelation_Fail;
		if(last_login_at == null) last_login_at = new Date();
		
		WifiHandsetDeviceRelationMDTO mdto = new WifiHandsetDeviceRelationMDTO(wifiId, handsetId);
		mdto.setLast_login_at(DateTimeHelper.formatDate(last_login_at, DateTimeHelper.FormatPattern1));
		
		Query query = new Query(Criteria.where(WifiHandsetDeviceRelationMDao.M_ID).is(mdto.getId()));
		Update update = new Update();
		update.set(WifiHandsetDeviceRelationMDao.M_WIFIID, wifiId);
		update.set(WifiHandsetDeviceRelationMDao.M_HANDSETID, handsetId);
		update.set(WifiHandsetDeviceRelationMDao.M_LAST_LOGIN_AT, mdto.getLast_login_at());


		WifiHandsetDeviceRelationMDTO wifiHandsetDeviceRelationMDTO =
                wifiHandsetDeviceRelationMDao.findById(mdto.getId());

        try {
            List<WifiHandsetDeviceItemLogMDTO> logs = null;

            WifiHandsetDeviceItemLogMDTO log = new WifiHandsetDeviceItemLogMDTO();
            log.setTs(last_login_at.getTime());
            log.setType(WifiHandsetDeviceRelationMDao.M_LOGS_TYPE_LOGIN);

            //无记录，第一次生成
            if (wifiHandsetDeviceRelationMDTO == null) {
                update.set(WifiHandsetDeviceRelationMDao.M_TOTAL_RX_BYTES, 0);
                logs = new ArrayList<WifiHandsetDeviceItemLogMDTO>();

            } else {
                update.set(WifiHandsetDeviceRelationMDao.M_TOTAL_RX_BYTES, wifiHandsetDeviceRelationMDTO.getTotal_rx_bytes());
                logs = wifiHandsetDeviceRelationMDTO.getLogs();
                if (logs == null ) {
                    logs = new ArrayList<WifiHandsetDeviceItemLogMDTO>(); //老数据可能为null
                }
            }

            logs.add(0, log);
            update.set(WifiHandsetDeviceRelationMDao.M_LOGS, logs);


        }catch (Exception e) {
            
        }

		WriteResult writeResult = wifiHandsetDeviceRelationMDao.upsert(query, update);
		if(writeResult.isUpdateOfExisting()){
			return AddRelation_Update;
		}else{
			return AddRelation_Insert;
		}
	}

    /**
     * 初始化七天的终端详情
     * @param week
     * @param last_login_at
     */
    private Map<String, List<WifiHandsetDeviceItemDetailMDTO>> initWifiHansetDeviceItems(
                List<String> week, Date last_login_at) {

        Map<String, List<WifiHandsetDeviceItemDetailMDTO>> dataMap
                = new LinkedHashMap<String, List<WifiHandsetDeviceItemDetailMDTO>>();

        int i = 0;
        for (String date : week) {
            List<WifiHandsetDeviceItemDetailMDTO> wifiHandsetDeviceItemDetailMDTOList
                    = new ArrayList<WifiHandsetDeviceItemDetailMDTO>();
            WifiHandsetDeviceItemDetailMDTO wifiHandsetDeviceItemDetailMDTO = new WifiHandsetDeviceItemDetailMDTO();

            //初始化当天第一条数据，写入登录时间
            if (i == 0) {
                wifiHandsetDeviceItemDetailMDTO.setLogin_at(last_login_at.getTime());
                wifiHandsetDeviceItemDetailMDTOList.add(wifiHandsetDeviceItemDetailMDTO);
            }
            dataMap.put(date, wifiHandsetDeviceItemDetailMDTOList);
            i++;
        }

        return dataMap;
    }


//    /**
//     * 更新七天的终端详情上线数据
//     * @param map    老数据
//     * @param week
//     * @param last_login_at
//     */
//    private Map<String, List<WifiHandsetDeviceItemDetailMDTO>> updateOnlineWifiHandsetDeviceItems(
//                Map<String, List<WifiHandsetDeviceItemDetailMDTO>> map,
//                List<String> week, Date last_login_at) {
//
//        Map<String, List<WifiHandsetDeviceItemDetailMDTO>> dataMap
//                = new LinkedHashMap<String, List<WifiHandsetDeviceItemDetailMDTO>>();
//
//        int i = 0;
//        for (String date : week) {
//            try {
//                List<WifiHandsetDeviceItemDetailMDTO> wifiHandsetDeviceItemDetailMDTOList = map.get(date);
//
//                if (wifiHandsetDeviceItemDetailMDTOList == null) {
//                    wifiHandsetDeviceItemDetailMDTOList = new ArrayList<WifiHandsetDeviceItemDetailMDTO>();
//                }
//                WifiHandsetDeviceItemDetailMDTO wifiHandsetDeviceItemDetailMDTO = new WifiHandsetDeviceItemDetailMDTO();
//
//                //删除非法下线记录
//                //如果服务器重启后，比如：设备会先离线，此时更新一条记录。
//                if (wifiHandsetDeviceItemDetailMDTOList.size() > 0) {
//                    if (wifiHandsetDeviceItemDetailMDTOList.get(0).getLogout_at() <= 0) {
//                        wifiHandsetDeviceItemDetailMDTOList.remove(0);
//                    }
//                }
//
//                if (i == 0) {
//                    //忽略15分钟之内频繁上下线记录
//                    if (!wifiHandsetDeviceItemDetailMDTOList.isEmpty()) {
//                        if (last_login_at.getTime() - wifiHandsetDeviceItemDetailMDTOList.get(0).getLogout_at()
//                                > IGNORE_LOGIN_TIME_SPACE) {
//                            wifiHandsetDeviceItemDetailMDTO.setLogin_at(last_login_at.getTime());
//                            wifiHandsetDeviceItemDetailMDTOList.add(0, wifiHandsetDeviceItemDetailMDTO);
//                        } else {
//                            wifiHandsetDeviceItemDetailMDTOList.get(0).setLogout_at(0); //清空登出时间
//                        }
//                    } else {
//                        //如果是第一条数据
//                        wifiHandsetDeviceItemDetailMDTO.setLogin_at(last_login_at.getTime());
//                        wifiHandsetDeviceItemDetailMDTOList.add(0, wifiHandsetDeviceItemDetailMDTO);
//                    }
//                }
//                dataMap.put(date, wifiHandsetDeviceItemDetailMDTOList);
//                i++;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//
//        return dataMap;
//    }


//    /**
//     * 更新七天的终端详情下线数据
//     * @param map
//     * @param week
//     * @param logout_at
//     * @return
//     */
//    public Map<String, List<WifiHandsetDeviceItemDetailMDTO>> updateOfflineWifiHandsetDeviceItems(
//                Map<String, List<WifiHandsetDeviceItemDetailMDTO>> map,
//                List<String> week, long logout_at, String lastLogoutAt) {
//
//        Map<String, List<WifiHandsetDeviceItemDetailMDTO>> dataMap
//                = new LinkedHashMap<String, List<WifiHandsetDeviceItemDetailMDTO>>();
//
//        //格式化的时候去掉时分秒，取零点时间
//        long last_logout_at_zero = DateTimeHelper.parseDate(lastLogoutAt, DateTimeHelper.shortDateFormat).getTime();
//
//        long spaceTime = logout_at - last_logout_at_zero;
//
//        //获取终端连续在线的时间段j天
//        int j = (int)spaceTime / (24 * 3600 * 1000);
//        if (j >= 6) {
//            j = 6;
//        }
//
//        if (j == 0) { //当天下线的记录处理
//            List<WifiHandsetDeviceItemDetailMDTO> wifiHandsetDeviceItemDetailMDTOList =
//                    map.get(week.get(0));
//            WifiHandsetDeviceItemDetailMDTO wifiHandsetDeviceItemDetailMDTO =
//                    wifiHandsetDeviceItemDetailMDTOList.get(0);
//            wifiHandsetDeviceItemDetailMDTO.setLogout_at(logout_at);
//            dataMap.put(week.get(0), wifiHandsetDeviceItemDetailMDTOList);
//        }
//
//        if (j > 0) {
//            //新增当天记录
//            List<WifiHandsetDeviceItemDetailMDTO> currentDayWifiHandsetDeviceItemDetailMDTOList =
//                    new ArrayList<WifiHandsetDeviceItemDetailMDTO>();
//            WifiHandsetDeviceItemDetailMDTO currentDayWifiHandsetDeviceItemDetailMDTO = new WifiHandsetDeviceItemDetailMDTO();
//            currentDayWifiHandsetDeviceItemDetailMDTO.setLogout_at(logout_at);
//            long login_at_zero = DateTimeHelper.parseDate(week.get(0), DateTimeHelper.shortDateFormat).getTime();
//            currentDayWifiHandsetDeviceItemDetailMDTO.setLogin_at(login_at_zero);
//            currentDayWifiHandsetDeviceItemDetailMDTOList.add(currentDayWifiHandsetDeviceItemDetailMDTO);
//            dataMap.put(week.get(0), currentDayWifiHandsetDeviceItemDetailMDTOList);
//
//            //终端连续在线的时间分割补齐时间
//            for (int i = 1; i < j+1; i++) {
//                String weekDate = week.get(i);
//                long zeroTime = DateTimeHelper.parseDate(weekDate, DateTimeHelper.shortDateFormat).getTime() -
//                        (i-1) * (24 * 3600 * 1000);
//                long logout = zeroTime + (24 * 3600 - 1) * 1000;
//                List<WifiHandsetDeviceItemDetailMDTO> tempList =  map.get(week.get(i));
//                if (tempList == null || tempList.isEmpty()) {
//                    tempList = new ArrayList<WifiHandsetDeviceItemDetailMDTO>();
//
//                    WifiHandsetDeviceItemDetailMDTO dto = new WifiHandsetDeviceItemDetailMDTO();
//                    dto.setLogout_at(logout);
//                    dto.setLogin_at(zeroTime);
//
//                    tempList.add(dto);
//                } else {
//                    WifiHandsetDeviceItemDetailMDTO dto =  tempList.get(0);
//                    dto.setLogout_at(logout);
//                }
//                dataMap.put(week.get(i), tempList);
//            }
//        }
//
//        //未超过7天的,复制原来数据
//        for (; j < 6; j++) {
//            String weekDate = week.get(j);
//            List<WifiHandsetDeviceItemDetailMDTO> tempList =  map.get(weekDate);
//            dataMap.put(week.get(j), tempList);
//        }
//
//        return dataMap;
//
//    }


    /**
     * 终端离线更新记录
     * @param wifiId
     * @param handsetId
     * @param uptime
     * @param tx_bytes
     * @param logout_at
     */
    public void offlineWifiHandsetDeviceItems(String wifiId, String handsetId, String uptime,
                                              String tx_bytes, long logout_at) {
        WifiHandsetDeviceRelationMDTO mdto = new WifiHandsetDeviceRelationMDTO(wifiId, handsetId);

        WifiHandsetDeviceRelationMDTO wifiHandsetDeviceRelationMDTO =
                wifiHandsetDeviceRelationMDao.findById(mdto.getId());

        Query query = new Query(Criteria.where(WifiHandsetDeviceRelationMDao.M_ID).is(mdto.getId()));
        Update update = new Update();
        update.set(WifiHandsetDeviceRelationMDao.M_WIFIID, wifiId);
        update.set(WifiHandsetDeviceRelationMDao.M_HANDSETID, handsetId);

        try {

            List<WifiHandsetDeviceItemLogMDTO> logs = null;
            WifiHandsetDeviceItemLogMDTO log = new WifiHandsetDeviceItemLogMDTO();
            log.setTs(logout_at);
            log.setType(WifiHandsetDeviceRelationMDao.M_LOGS_TYPE_LOGOUT);

            //离线的情况下，肯定有七天的在线记录
            if (wifiHandsetDeviceRelationMDTO != null) {
                logs = wifiHandsetDeviceRelationMDTO.getLogs();
                if (logs == null) {
                    logs = new ArrayList<WifiHandsetDeviceItemLogMDTO>();
                }

                update.set(WifiHandsetDeviceRelationMDao.M_LAST_LOGIN_AT, wifiHandsetDeviceRelationMDTO.getLast_login_at());
                update.set(WifiHandsetDeviceRelationMDao.M_TOTAL_RX_BYTES, wifiHandsetDeviceRelationMDTO.getTotal_rx_bytes() + Long.parseLong(tx_bytes));

            } else { //如果离线记录先上报
                logs = new ArrayList<WifiHandsetDeviceItemLogMDTO>();
                Date date = new Date();
                update.set(WifiHandsetDeviceRelationMDao.M_LAST_LOGIN_AT, DateTimeHelper.formatDate(date, DateTimeHelper.FormatPattern1));
                update.set(WifiHandsetDeviceRelationMDao.M_TOTAL_RX_BYTES, 0);

            }

            logs.add(0, log);
            update.set(WifiHandsetDeviceRelationMDao.M_LOGS,logs);

        }catch (Exception e) {

        }

        WriteResult writeResult = wifiHandsetDeviceRelationMDao.upsert(query, update);

    }

    /**
	 * 设备非法关机，断开长连接后通知所有终端离线，设备端上报的在线时长和终端流量统计为0.
	 * @param wifiId
	 */
	public void wifiDeviceIllegalOfflineAdapter(String wifiId, List<HandsetDeviceDTO> handsets) {
        if (handsets != null) {
            for(HandsetDeviceDTO dto:handsets){
                if(dto != null){
                    offlineWifiHandsetDeviceItems(wifiId, dto.getMac(), "0", "0", System.currentTimeMillis());
                }
            }
        }
	}

	public WifiHandsetDeviceRelationMDTO getRelation(String wifiId, String handsetId){
		return wifiHandsetDeviceRelationMDao.findById(WifiHandsetDeviceRelationMDTO.generateId(wifiId, handsetId));
	}
	
	public boolean hasRelation(String wifiId, String handsetId){
		WifiHandsetDeviceRelationMDTO mdto = this.getRelation(wifiId, handsetId);
		if(mdto == null) return false;
		return true;
	}
	
	public Pagination<WifiHandsetDeviceRelationMDTO> findRelationsByWifiId(String wifiId, int pageNo, int pageSize){
		Query query = new Query(Criteria.where(WifiHandsetDeviceRelationMDao.M_WIFIID).is(wifiId));
		query.with(new Sort(Direction.DESC,WifiHandsetDeviceRelationMDao.M_LAST_LOGIN_AT));
		return wifiHandsetDeviceRelationMDao.findPagination(pageNo, pageSize, query);
	}
	
	public Pagination<WifiHandsetDeviceRelationMDTO> findRelationsByHandsetId(String handsetId, int pageNo, int pageSize){
		Query query = new Query(Criteria.where(WifiHandsetDeviceRelationMDao.M_HANDSETID).is(handsetId));
		query.with(new Sort(Direction.DESC,WifiHandsetDeviceRelationMDao.M_LAST_LOGIN_AT));
		return wifiHandsetDeviceRelationMDao.findPagination(pageNo, pageSize, query);
	}


    public static void main(String[] args) {

    }
	
}
