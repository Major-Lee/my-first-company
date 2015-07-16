package com.bhu.vas.api.rpc.devices.model;

import com.bhu.vas.api.rpc.devices.dto.PersistenceCMDDTO;
import com.smartwork.msip.cores.orm.model.extjson.KeyDtoMapJsonExtStringModel;

/**
 * 针对设备持久化下发指令，一般都是设备不保存的指令，重启后会丢失的指令
 * 注意事项 涉及到修改配置的指令需要合并成一个
 * @author Edmond Lee
 *
 */
@SuppressWarnings("serial")
public class WifiDevicePersistenceCMDState extends KeyDtoMapJsonExtStringModel<PersistenceCMDDTO> {
	
	@Override
	public Class<PersistenceCMDDTO> getJsonParserModel() {
		return PersistenceCMDDTO.class;
	}
	
	public boolean hasPersistence(String key){
		return this.containsKey(key);
	}
	
	public boolean addOrUpdatePersistence(String key,PersistenceCMDDTO dto){
		//boolean result = false;
		//String key = dto.toKey();
		//if(!hasPersistence(key)) result = true;
		this.putInnerModel(key, dto);
		return true;
	}
	
	public boolean removePersistence(String key){
		//boolean result = false;
		//if(!hasPersistence(key)) result = true;
		this.removeInnerModel(key);
		return true;
	}
	/*public List<String> persistenceCMDs(){
		return null;
	}*/
}
