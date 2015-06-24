package com.bhu.vas.business.ds.device.service;

import java.util.*;

import javax.annotation.Resource;

import com.bhu.vas.business.ds.device.mdto.WifiHandsetDeviceItemDetailMTDTO;
import com.bhu.vas.business.ds.device.mdto.WifiHandsetDeviceItemMDTO;
import com.smartwork.msip.cores.helper.JsonHelper;
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


        try {
            if (wifiHandsetDeviceRelationMDTO == null) {
                Map<String, WifiHandsetDeviceItemDetailMTDTO> wifiHandsetDeviceItemDetailMTDTOMap
                        = new LinkedHashMap<String, WifiHandsetDeviceItemDetailMTDTO>();
                List<Date> week = generateWeekCalendar();
                for (Date date : week) {
                    WifiHandsetDeviceItemDetailMTDTO wifiHandsetDeviceItemDetailMTDTO = new WifiHandsetDeviceItemDetailMTDTO();
                    wifiHandsetDeviceItemDetailMTDTOMap.put(
                            DateTimeHelper.formatDate(date, DateTimeHelper.FormatPattern5), wifiHandsetDeviceItemDetailMTDTO);
                }

                update.set("items", wifiHandsetDeviceItemDetailMTDTOMap);
            } else {


            }
        }catch (Exception e) {
            
        }

		WriteResult writeResult = wifiHandsetDeviceRelationMDao.upsert(query, update);
		if(writeResult.isUpdateOfExisting()){
			return AddRelation_Update;
		}else{
			return AddRelation_Insert;
		}
	}

	private List<Date> generateWeekCalendar() {
		List<Date> week = new ArrayList<Date>();
		Calendar calendar = Calendar.getInstance();
		week.add(calendar.getTime());
		int i =0;
		while (i < 6){
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			week.add(calendar.getTime());
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
