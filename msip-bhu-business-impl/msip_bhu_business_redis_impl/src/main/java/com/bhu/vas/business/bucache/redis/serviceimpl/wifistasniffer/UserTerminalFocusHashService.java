package com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer;

import org.springframework.util.StringUtils;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.api.dto.wifistasniffer.UserTerminalFocusDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 用户与终端的关系
 * 周边探测扫描到会发送push通知
 * key 为 用户id
 * field 为 终端mac
 * value 为 json
 * @author lawliet
 *
 */
public class UserTerminalFocusHashService extends AbstractRelationHashCache{
	
	private static class ServiceHolder{ 
		private static UserTerminalFocusHashService instance =new UserTerminalFocusHashService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static UserTerminalFocusHashService getInstance() { 
		return ServiceHolder.instance; 
	}
	private UserTerminalFocusHashService(){
		
	}
	private static String generateMarkPrefixKey(int uid){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.WifiStasniffer.UserTerminalFocus);
		sb.append(StringHelper.POINT_STRING_GAP).append(uid);
		return sb.toString();
	}
	
	/**
	 * 关注终端
	 * @param uid
	 * @param hd_mac
	 */
	public void addFocus(int uid, String hd_mac){
		this.setFocusValue(uid, hd_mac, true);
	}
	
	/**
	 * 取消关注终端
	 * @param uid
	 * @param hd_mac
	 */
	public void removeFocus(int uid, String hd_mac){
		this.setFocusValue(uid, hd_mac, false);
	}
	
	/**
	 * 添加终端昵称
	 * @param uid
	 * @param hd_mac
	 */
	public void addNick(int uid, String hd_mac, String nick){
		this.setNickValue(uid, hd_mac, nick);
	}
	
	/**
	 * 删除终端昵称
	 * @param uid
	 * @param hd_mac
	 */
	public void removeNick(int uid, String hd_mac){
		this.setNickValue(uid, hd_mac, null);
	}
	/**
	 * 设置终端关注的数据
	 * @param uid
	 * @param hd_mac
	 * @param focus
	 */
	protected void setFocusValue(int uid, String hd_mac, boolean focus){
		String key = generateMarkPrefixKey(uid);
		String value = super.hget(key, hd_mac);
		UserTerminalFocusDTO dto = null;
		if(StringUtils.isEmpty(value)){
			dto = new UserTerminalFocusDTO();
		}else{
			dto = JsonHelper.getDTO(value, UserTerminalFocusDTO.class);
		}
		dto.setFocus(focus);
		super.hset(key, hd_mac, JsonHelper.getJSONString(dto));
	}
	/**
	 * 设置终端昵称数据
	 * @param uid
	 * @param hd_mac
	 * @param nick
	 */
	protected void setNickValue(int uid, String hd_mac, String nick){
		String key = generateMarkPrefixKey(uid);
		String value = super.hget(key, hd_mac);
		UserTerminalFocusDTO dto = null;
		if(StringUtils.isEmpty(value)){
			dto = new UserTerminalFocusDTO();
		}else{
			dto = JsonHelper.getDTO(value, UserTerminalFocusDTO.class);
		}
		dto.setNick(nick);
		super.hset(key, hd_mac, JsonHelper.getJSONString(dto));
	}
	
	@Override
	public String getRedisKey() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getName() {
		return UserTerminalFocusHashService.class.getName();
	}
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.WIFISTASNIFFER);
	}
}
