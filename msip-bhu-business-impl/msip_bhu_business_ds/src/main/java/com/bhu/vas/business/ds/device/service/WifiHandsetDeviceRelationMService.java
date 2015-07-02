package com.bhu.vas.business.ds.device.service;

import java.util.*;

import javax.annotation.Resource;

import com.bhu.vas.api.mdto.WifiHandsetDeviceItemDetailMDTO;
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
	
	public int addRelation(String wifiId, String handsetId, Date last_login_at){
		if(StringUtils.isEmpty(wifiId) || StringUtils.isEmpty(handsetId)) return AddRelation_Fail;
		if(last_login_at == null) last_login_at = new Date();
		
		WifiHandsetDeviceRelationMDTO mdto = new WifiHandsetDeviceRelationMDTO(wifiId, handsetId);
		mdto.setLast_login_at(DateTimeHelper.formatDate(last_login_at, DateTimeHelper.FormatPattern1));
		
		Query query = new Query(Criteria.where("_id").is(mdto.getId()));
		Update update = new Update();
		update.set("wifiId", wifiId);
		update.set("handsetId", handsetId);
		update.set("last_login_at", mdto.getLast_login_at());


		WifiHandsetDeviceRelationMDTO wifiHandsetDeviceRelationMDTO =
                wifiHandsetDeviceRelationMDao.findById(mdto.getId());

        List<String> week = DateTimeExtHelper.getSevenDateOfWeek();

        Map<String, List<WifiHandsetDeviceItemDetailMDTO>> dataMap
                = new LinkedHashMap<String, List<WifiHandsetDeviceItemDetailMDTO>>();

        try {
            if (wifiHandsetDeviceRelationMDTO == null) {
                dataMap = initWifiHansetDeviceItems(week, last_login_at);
                update.set("total_rx_bytes", 0);
            } else {
                Map<String, List<WifiHandsetDeviceItemDetailMDTO>> wifiHandsetDeviceItemDetailMTDTOMap
                        = wifiHandsetDeviceRelationMDTO.getItems();

                //旧版本数据，不存在记录
                if (wifiHandsetDeviceItemDetailMTDTOMap == null || wifiHandsetDeviceItemDetailMTDTOMap.isEmpty()) {
                    dataMap = initWifiHansetDeviceItems(week, last_login_at);
                } else {
                    dataMap = updateOnlineWifiHandsetDeviceItems(
                            wifiHandsetDeviceItemDetailMTDTOMap, week, last_login_at,
                            wifiHandsetDeviceRelationMDTO.getLast_login_at());
                }


                update.set("total_rx_bytes", wifiHandsetDeviceRelationMDTO.getTotal_rx_bytes());
            }

            update.set("items", dataMap);

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
            if (i == 0) {
                wifiHandsetDeviceItemDetailMDTO.setLogin_at(last_login_at.getTime());
                wifiHandsetDeviceItemDetailMDTOList.add(wifiHandsetDeviceItemDetailMDTO);
            }
            dataMap.put(date, wifiHandsetDeviceItemDetailMDTOList);
            i++;
        }

        return dataMap;
    }


    /**
     * 更新七天的终端详情上线数据
     * @param map    老数据
     * @param week
     * @param last_login_at
     */
    private Map<String, List<WifiHandsetDeviceItemDetailMDTO>> updateOnlineWifiHandsetDeviceItems(
                Map<String, List<WifiHandsetDeviceItemDetailMDTO>> map,
                List<String> week, Date last_login_at, String check_last_login_at) {

        Map<String, List<WifiHandsetDeviceItemDetailMDTO>> dataMap
                = new LinkedHashMap<String, List<WifiHandsetDeviceItemDetailMDTO>>();

        String check_date = DateTimeHelper.formatDate(
                DateTimeHelper.fromDateStr(check_last_login_at),DateTimeHelper.FormatPattern5);
        int i = 0;
        for (String date : week) {
            List<WifiHandsetDeviceItemDetailMDTO> wifiHandsetDeviceItemDetailMDTOList = map.get(date);

            if (wifiHandsetDeviceItemDetailMDTOList == null) {
                wifiHandsetDeviceItemDetailMDTOList = new ArrayList<WifiHandsetDeviceItemDetailMDTO>();
            }
            WifiHandsetDeviceItemDetailMDTO wifiHandsetDeviceItemDetailMDTO = new WifiHandsetDeviceItemDetailMDTO();

            if (date.equals(check_date)) {
                if (wifiHandsetDeviceItemDetailMDTOList.get(0).getLogout_at() <=0) {
                    wifiHandsetDeviceItemDetailMDTOList.remove(0);
                }
            }
            if (i == 0 ) {
                wifiHandsetDeviceItemDetailMDTO.setLogin_at(last_login_at.getTime());
                wifiHandsetDeviceItemDetailMDTOList.add(0, wifiHandsetDeviceItemDetailMDTO);
            }
            dataMap.put(date, wifiHandsetDeviceItemDetailMDTOList);
            i++;
        }



        return dataMap;
    }



    /**
     * 更新七天的终端详情下线数据
     * @param map 老数据
     * @param uptime
     * @param logout_at
     */
    private Map<String, List<WifiHandsetDeviceItemDetailMDTO>> updateOfflineWifiHandsetDeviceItems(
                Map<String, List<WifiHandsetDeviceItemDetailMDTO>> map,
                List<String> week, String uptime, long logout_at) {

        Map<String, List<WifiHandsetDeviceItemDetailMDTO>> dataMap
                = new LinkedHashMap<String, List<WifiHandsetDeviceItemDetailMDTO>>();

        int i = 0;
        for (String date : week) {
            List<WifiHandsetDeviceItemDetailMDTO> wifiHandsetDeviceItemDetailMDTOList =  map.get(date);
            if (i == 0) {
                WifiHandsetDeviceItemDetailMDTO wifiHandsetDeviceItemDetailMDTO =
                        wifiHandsetDeviceItemDetailMDTOList.get(0);
                wifiHandsetDeviceItemDetailMDTO.setOnline_time(Long.parseLong(uptime));
                wifiHandsetDeviceItemDetailMDTO.setLogout_at(logout_at);
            }
            dataMap.put(date, wifiHandsetDeviceItemDetailMDTOList);
            i++;
        }

        return dataMap;

    }


    /**
     * 终端离线更新记录
     * @param wifiId
     * @param handsetId
     * @param uptime
     * @param rx_bytes
     * @param logout_at
     */
    public void offlineWifiHandsetDeviceItems(String wifiId, String handsetId, String uptime,
                                              String rx_bytes, long logout_at) {
        WifiHandsetDeviceRelationMDTO mdto = new WifiHandsetDeviceRelationMDTO(wifiId, handsetId);

        WifiHandsetDeviceRelationMDTO wifiHandsetDeviceRelationMDTO =
                wifiHandsetDeviceRelationMDao.findById(mdto.getId());


        Map<String, List<WifiHandsetDeviceItemDetailMDTO>> wifiHandsetDeviceItemDetailMTDTOMap = null;
        List<String> week = DateTimeExtHelper.getSevenDateOfWeek();


        Query query = new Query(Criteria.where("_id").is(mdto.getId()));
        Update update = new Update();
        update.set("wifiId", wifiId);
        update.set("handsetId", handsetId);


        //离线的情况下，肯定有七天的在线记录
        try {

            Map<String, List<WifiHandsetDeviceItemDetailMDTO>> dataMap
                    = new LinkedHashMap<String, List<WifiHandsetDeviceItemDetailMDTO>>();

            if (wifiHandsetDeviceRelationMDTO != null) {

                wifiHandsetDeviceItemDetailMTDTOMap = wifiHandsetDeviceRelationMDTO.getItems();

                dataMap = updateOfflineWifiHandsetDeviceItems(wifiHandsetDeviceItemDetailMTDTOMap, week, uptime,logout_at);
                update.set("last_login_at", wifiHandsetDeviceRelationMDTO.getLast_login_at());
                update.set("total_rx_bytes", wifiHandsetDeviceRelationMDTO.getTotal_rx_bytes() + Long.parseLong(rx_bytes));

            } else { //如果离线记录先上报
                Date date = new Date();
                dataMap = initWifiHansetDeviceItems(week, date);
                update.set("last_login_at", DateTimeHelper.formatDate(date, DateTimeHelper.FormatPattern1));
                update.set("total_rx_bytes", 0);
            }

            update.set("items", dataMap);

        }catch (Exception e) {

        }

        WriteResult writeResult = wifiHandsetDeviceRelationMDao.upsert(query, update);

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
		Query query = new Query(Criteria.where("wifiId").is(wifiId));
		query.with(new Sort(Direction.DESC,"last_login_at"));
		return wifiHandsetDeviceRelationMDao.findPagination(pageNo, pageSize, query);
	}
	
	public Pagination<WifiHandsetDeviceRelationMDTO> findRelationsByHandsetId(String handsetId, int pageNo, int pageSize){
		Query query = new Query(Criteria.where("handsetId").is(handsetId));
		query.with(new Sort(Direction.DESC,"last_login_at"));
		return wifiHandsetDeviceRelationMDao.findPagination(pageNo, pageSize, query);
	}
	
//	public Pagination<WifiHandsetDeviceRelationMDTO> findRelations(String wifiId, String handsetId, int pageNo, int pageSize){
//		Query query = new Query(Criteria.where("wifiId").is(wifiId).and("handsetId").is(handsetId));
//		query.with(new Sort(Direction.DESC,"_id"));
//		return wifiHandsetDeviceRelationMDao.findPagination(pageNo, pageSize, query);
//	}
	
}
