package com.bhu.vas.business.ds.device.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.mdto.WifiHandsetDeviceItemLogMDTO;
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
//@Service
@Deprecated
public class WifiHandsetDeviceRelationMService {
	
	//@Resource
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
     * 终端离线更新记录
     * @param wifiId
     * @param handsetId
     * @param tx_bytes
     * @param logout_at
     */
    public void offlineWifiHandsetDeviceItems(String wifiId, String handsetId, String tx_bytes, long logout_at) {
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
            log.setRx_bytes(Long.parseLong(tx_bytes));

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
	 * 设备非法关机，断开长连接后通知所有终端离线，设备端上报的终端流量统计为0.
	 * @param wifiId
	 */
	public void wifiDeviceIllegalOfflineAdapter(String wifiId, List<HandsetDeviceDTO> handsets) {
        if (handsets != null) {
            for(HandsetDeviceDTO dto:handsets){
                if(dto != null){
                    offlineWifiHandsetDeviceItems(wifiId, dto.getMac(), "0", System.currentTimeMillis());
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
