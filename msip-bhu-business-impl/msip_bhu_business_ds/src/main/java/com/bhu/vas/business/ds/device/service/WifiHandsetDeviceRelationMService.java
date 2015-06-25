package com.bhu.vas.business.ds.device.service;

import java.util.*;

import javax.annotation.Resource;

import com.bhu.vas.business.ds.device.mdto.WifiHandsetDeviceItemDetailMDTO;
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


//		WifiHandsetDeviceRelationMDTO wifiHandsetDeviceRelationMDTO =
//				wifiHandsetDeviceRelationMDao.findById(mdto.getId());


		WifiHandsetDeviceRelationMDTO wifiHandsetDeviceRelationMDTO =
				wifiHandsetDeviceRelationMDao.findById(mdto.getId());


        Map<String, List<WifiHandsetDeviceItemDetailMDTO>> wifiHandsetDeviceItemDetailMTDTOMap = null;
        List<String> week = generateWeekCalendar();

        try {
            if (wifiHandsetDeviceRelationMDTO == null) {
                wifiHandsetDeviceItemDetailMTDTOMap
                        = new LinkedHashMap<String, List<WifiHandsetDeviceItemDetailMDTO>>();
                int i = 0;
                for (String date : week) {
                    List<WifiHandsetDeviceItemDetailMDTO> wifiHandsetDeviceItemDetailMDTOList
                            = new ArrayList<WifiHandsetDeviceItemDetailMDTO>();
                    WifiHandsetDeviceItemDetailMDTO wifiHandsetDeviceItemDetailMDTO = new WifiHandsetDeviceItemDetailMDTO();
                    if (i == 0) {
                        wifiHandsetDeviceItemDetailMDTO.setLogin_at(mdto.getLast_login_at());
                    }
                    wifiHandsetDeviceItemDetailMDTOList.add(wifiHandsetDeviceItemDetailMDTO);
                    wifiHandsetDeviceItemDetailMTDTOMap.put(date, wifiHandsetDeviceItemDetailMDTOList);
                    i++;
                }
                update.set("total_rx_bytes", 0);
            } else {
                wifiHandsetDeviceItemDetailMTDTOMap  = wifiHandsetDeviceRelationMDTO.getItems();

                int i = 0;
                for (String date : week) {
                    List<WifiHandsetDeviceItemDetailMDTO> wifiHandsetDeviceItemDetailMDTOList =
                            wifiHandsetDeviceItemDetailMTDTOMap.get(date);

                    if (wifiHandsetDeviceItemDetailMDTOList == null) {
                        wifiHandsetDeviceItemDetailMDTOList = new ArrayList<WifiHandsetDeviceItemDetailMDTO>();
                    }
                    WifiHandsetDeviceItemDetailMDTO wifiHandsetDeviceItemDetailMDTO = new WifiHandsetDeviceItemDetailMDTO();
                    if (i == 0 ) {
                        wifiHandsetDeviceItemDetailMDTO.setLogin_at(mdto.getLast_login_at());
                        wifiHandsetDeviceItemDetailMDTOList.add(wifiHandsetDeviceItemDetailMDTO);
                    }
                    wifiHandsetDeviceItemDetailMTDTOMap.put(date, wifiHandsetDeviceItemDetailMDTOList);
                    i++;
                }
                update.set("total_rx_bytes", wifiHandsetDeviceRelationMDTO.getTotal_rx_bytes());
            }
        }catch (Exception e) {
            
        }

        update.set("items", wifiHandsetDeviceItemDetailMTDTOMap);

		WriteResult writeResult = wifiHandsetDeviceRelationMDao.upsert(query, update);
		if(writeResult.isUpdateOfExisting()){
			return AddRelation_Update;
		}else{
			return AddRelation_Insert;
		}
	}


    public void updateWifiHandsetDeviceItems(String wifiId, String handsetId, String uptime, String rx_bytes) {
        WifiHandsetDeviceRelationMDTO mdto = new WifiHandsetDeviceRelationMDTO(wifiId, handsetId);

        WifiHandsetDeviceRelationMDTO wifiHandsetDeviceRelationMDTO =
                wifiHandsetDeviceRelationMDao.findById(mdto.getId());


        Map<String, List<WifiHandsetDeviceItemDetailMDTO>> wifiHandsetDeviceItemDetailMTDTOMap = null;
        List<String> week = generateWeekCalendar();


        //离线的情况下，肯定有七天的在线记录
        try {
            wifiHandsetDeviceItemDetailMTDTOMap  = wifiHandsetDeviceRelationMDTO.getItems();

            int i = 0;
            for (String date : week) {
                List<WifiHandsetDeviceItemDetailMDTO> wifiHandsetDeviceItemDetailMDTOList =
                        wifiHandsetDeviceItemDetailMTDTOMap.get(date);

                if (i == 0 ) {
                    int size = wifiHandsetDeviceItemDetailMDTOList.size();
                    WifiHandsetDeviceItemDetailMDTO wifiHandsetDeviceItemDetailMDTO =
                            wifiHandsetDeviceItemDetailMDTOList.get(size - 1);
                    wifiHandsetDeviceItemDetailMDTO.setOnline_time(Long.parseLong(uptime));
                }
                wifiHandsetDeviceItemDetailMTDTOMap.put(date, wifiHandsetDeviceItemDetailMDTOList);
                i++;
            }

        }catch (Exception e) {

        }

        Query query = new Query(Criteria.where("_id").is(mdto.getId()));
        Update update = new Update();
        update.set("wifiId", wifiId);
        update.set("handsetId", handsetId);
        update.set("last_login_at", wifiHandsetDeviceRelationMDTO.getLast_login_at());

        update.set("total_rx_bytes", wifiHandsetDeviceRelationMDTO.getTotal_rx_bytes() + Long.parseLong(rx_bytes));

        update.set("items", wifiHandsetDeviceItemDetailMTDTOMap);

        WriteResult writeResult = wifiHandsetDeviceRelationMDao.upsert(query, update);

    }

	private List<String> generateWeekCalendar() {
		List<String> week = new ArrayList<String>();
		Calendar calendar = Calendar.getInstance();
		week.add(DateTimeHelper.formatDate(calendar.getTime(), DateTimeHelper.FormatPattern5));
		int i =0;
		while (i < 6){
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			week.add(DateTimeHelper.formatDate(calendar.getTime(), DateTimeHelper.FormatPattern5));
			System.out.println( DateTimeHelper.formatDate(calendar.getTime(), DateTimeHelper.FormatPattern5));
			i++;
		}
		return week;
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
