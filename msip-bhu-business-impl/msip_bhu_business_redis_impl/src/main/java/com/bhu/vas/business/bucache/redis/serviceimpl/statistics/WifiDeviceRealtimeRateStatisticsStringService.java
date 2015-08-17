package com.bhu.vas.business.bucache.redis.serviceimpl.statistics;

import java.util.List;

import org.springframework.util.StringUtils;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.api.dto.ret.WifiDeviceRxPeakSectionDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceTxPeakSectionDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationStringCache;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 	用于设备的实时上下行速率
 * @author lawliet
 *
 */
public class WifiDeviceRealtimeRateStatisticsStringService extends AbstractRelationStringCache{
	
	private static class ServiceHolder{ 
		private static WifiDeviceRealtimeRateStatisticsStringService instance =new WifiDeviceRealtimeRateStatisticsStringService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static WifiDeviceRealtimeRateStatisticsStringService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	private WifiDeviceRealtimeRateStatisticsStringService(){
	}
	
	private static final int exprie_realtime_seconds = 5;//5秒
	private static final int exprie_rate_waiting_seconds = 300;//300秒(跟上报时长一致 防止重复下发此类指令)
	private static final int exprie_hdrate_waiting_seconds = 300;//300秒(跟上报时长一致 防止重复下发此类指令)
	//private static final int exprie_peak_waiting_seconds = 20;//20秒
	
	public static final int Type_Tx_Rate = 1;//上行速率
	public static final int Type_Rx_Rate = 2;//下行速率
	
	private static String generateRealtimeKey(String mac, int type){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Statistics.WifiDeviceStatistics);
		sb.append(StringHelper.POINT_CHAR_GAP).append(BusinessKeyDefine.Statistics.WifiDeviceStatistics_RealtimeRate);
		sb.append(StringHelper.POINT_CHAR_GAP).append(type);
		sb.append(StringHelper.POINT_CHAR_GAP).append(mac);
		return sb.toString();
	}
	
	private static String generateLastKey(String mac, int type){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Statistics.WifiDeviceStatistics);
		sb.append(StringHelper.POINT_CHAR_GAP).append(BusinessKeyDefine.Statistics.WifiDeviceStatistics_LastRate);
		sb.append(StringHelper.POINT_CHAR_GAP).append(type);
		sb.append(StringHelper.POINT_CHAR_GAP).append(mac);
		return sb.toString();
	}
	
	private static String generatePeakKey(String mac, int type){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Statistics.WifiDeviceStatistics);
		sb.append(StringHelper.POINT_CHAR_GAP).append(BusinessKeyDefine.Statistics.WifiDeviceStatistics_PeakRate);
		sb.append(StringHelper.POINT_CHAR_GAP).append(type);
		sb.append(StringHelper.POINT_CHAR_GAP).append(mac);
		return sb.toString();
	}
	
	private static String generatePeakSectionKey(String mac, int type){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Statistics.WifiDeviceStatistics);
		sb.append(StringHelper.POINT_CHAR_GAP).append(BusinessKeyDefine.Statistics.WifiDeviceStatistics_PeakSectionRate);
		sb.append(StringHelper.POINT_CHAR_GAP).append(type);
		sb.append(StringHelper.POINT_CHAR_GAP).append(mac);
		return sb.toString();
	}
	
	private static String[] generateAllKeysWithoutWaiting(String mac){
		String[] keys = new String[4];
		String r_tx_key = generateRealtimeKey(mac, Type_Tx_Rate);
		keys[0] = r_tx_key;
		String r_rx_key = generateRealtimeKey(mac, Type_Rx_Rate);
		keys[1] = r_rx_key;
		String l_tx_key = generateLastKey(mac, Type_Tx_Rate);
		keys[2] = l_tx_key;
		String l_rx_key = generateLastKey(mac, Type_Rx_Rate);
		keys[3] = l_rx_key;
		return keys;
	}
	
	private static String[] generateRateKeys(String mac){
		String[] keys = new String[5];
		String r_tx_key = generateRealtimeKey(mac, Type_Tx_Rate);
		keys[0] = r_tx_key;
		String r_rx_key = generateRealtimeKey(mac, Type_Rx_Rate);
		keys[1] = r_rx_key;
		String l_tx_key = generateLastKey(mac, Type_Tx_Rate);
		keys[2] = l_tx_key;
		String l_rx_key = generateLastKey(mac, Type_Rx_Rate);
		keys[3] = l_rx_key;
		String waiting_key = generateRateWaitingKey(mac);
		keys[4] = waiting_key;
//		String peak_key = generatePeakKey(mac);
//		keys[5] = peak_key;
		return keys;
	}
	
//	private static String[] generatePeakRateKeys(String mac){
//		String[] keys = new String[2];
//		String peak_key = generatePeakKey(mac);
//		keys[0] = peak_key;
//		String waiting_key = generatePeakRateWaitingKey(mac);
//		keys[1] = waiting_key;
//		return keys;
//	}
	
	private static String[] generateRateValues(String tx_rate, String rx_rate){
		String[] values = new String[4];
		values[0] = tx_rate;
		values[1] = rx_rate;
		values[2] = tx_rate;
		values[3] = rx_rate;
		return values;
	}
	
	private static String generateRateWaitingKey(String mac){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Statistics.WifiDeviceStatistics);
		sb.append(StringHelper.POINT_CHAR_GAP).append(BusinessKeyDefine.Statistics.WifiDeviceStatistics_RateWaiting);
		sb.append(StringHelper.POINT_CHAR_GAP).append(mac);
		return sb.toString();
	}
	
//	private static String generatePeakRateWaitingKey(String mac){
//		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Statistics.WifiDeviceStatistics);
//		sb.append(StringHelper.POINT_CHAR_GAP).append(BusinessKeyDefine.Statistics.WifiDeviceStatistics_PeakRateWaiting);
//		sb.append(StringHelper.POINT_CHAR_GAP).append(mac);
//		return sb.toString();
//	}
	
	private static String generateHDRateWaitingKey(String mac){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Statistics.WifiDeviceStatistics);
		sb.append(StringHelper.POINT_CHAR_GAP).append(BusinessKeyDefine.Statistics.WifiDeviceStatistics_HDRateWaiting);
		sb.append(StringHelper.POINT_CHAR_GAP).append(mac);
		return sb.toString();
	}
	
	
	public static final String WaitingMark = "waiting";
	
	public void addRateWaiting(String mac){
		String key = generateRateWaitingKey(mac);
		super.set(key, WaitingMark);
		super.expire(key, exprie_rate_waiting_seconds);
	}
	
	public void removeRateWaiting(String mac){
		String key = generateRateWaitingKey(mac);
		super.del(key);
	}
	
//	public void addPeakRateWaiting(String mac){
//		String key = generatePeakRateWaitingKey(mac);
//		super.set(key, WaitingMark);
//		super.expire(key, exprie_peak_waiting_seconds);
//	}
	
	public void addHDRateWaiting(String mac){
		String key = generateHDRateWaitingKey(mac);
		super.set(key, WaitingMark);
		super.expire(key, exprie_hdrate_waiting_seconds);
	}
	
	/**
	 * 判断设备速率上报指令是否在有效期内
	 * @param mac
	 * @return
	 */
	public boolean isRateWaiting(String mac){
		return super.exists(generateRateWaitingKey(mac));
	}
	
	/**
	 * 判断终端速率上报指令是否在有效期内
	 * @param mac
	 * @return
	 */
	public boolean isHDRateWaiting(String mac){
		return super.exists(generateHDRateWaitingKey(mac));
	}
	
	/**
	 * 设置实时上下行速率和最后一次的实时上下行速率
	 * @param mac
	 * @param tx_rate 设备的上行速率
	 * @param rx_rate 设备的下行速率
	 */
	public void addRate(String mac, String tx_rate, String rx_rate){
		String[] keys = generateAllKeysWithoutWaiting(mac);
		String[] values = generateRateValues(tx_rate, rx_rate);
		super.mset(keys, values);
		super.expire(keys[0], exprie_realtime_seconds);
		super.expire(keys[1], exprie_realtime_seconds);
	}
	
	/**
	 * 设备设备网速峰值 (下行最大速率)
	 * @param mac
	 * @param rx_peak_rate
	 */
	public void addRxPeak(String mac, String rx_peak_rate){
		super.set(generatePeakKey(mac, Type_Rx_Rate), rx_peak_rate);
	}
	
	/**
	 * 设备设备网速峰值 (上行最大速率)
	 * @param mac
	 * @param tx_peak_rate
	 */
	public void addTxPeak(String mac, String tx_peak_rate){
		super.set(generatePeakKey(mac, Type_Tx_Rate), tx_peak_rate);
	}
	
	/**
	 * 追加设备测速下行分段数据
	 * @param mac
	 * @param rx_peak_section
	 */
	public void appendRxPeakSection(String mac, WifiDeviceRxPeakSectionDTO rx_dto){
		if(rx_dto == null) return;
		super.append(generatePeakSectionKey(mac, Type_Rx_Rate), StringHelper.COMMA_STRING_GAP.
				concat(JsonHelper.getJSONString(rx_dto)));
	}
	
	/**
	 * 获取设备测速下行分段数据 返回json
	 * @param mac
	 * @return
	 */
	public String getRxPeakSectionJson(String mac){
	    String ret = super.get(generatePeakSectionKey(mac, Type_Rx_Rate));
		return generatePeakSectionJsonArray(ret);
	}
	
	/**
	 * 获取设备测速下行分段数据 返回dtos
	 * @param mac
	 * @return
	 */
	public List<WifiDeviceRxPeakSectionDTO> getRxPeakSectionDtos(String mac){
		String ret = super.get(generatePeakSectionKey(mac, Type_Rx_Rate));
		return generatePeakSectionDtos(ret, WifiDeviceRxPeakSectionDTO.class);
	}

	
	/**
	 * 清除设备测速下行分段数据
	 * @param mac
	 */
//	public void clearRxPeakSection(String mac){
//		super.del(generatePeakSectionKey(mac, Type_Rx_Rate));
//	}
	
	/**
	 * 追加设备测速上行分段数据
	 * @param mac
	 * @param tx_peak_section
	 */
	public void appendTxPeakSection(String mac, WifiDeviceTxPeakSectionDTO tx_dto){
		if(tx_dto == null) return;
		super.append(generatePeakSectionKey(mac, Type_Tx_Rate), StringHelper.COMMA_STRING_GAP.
				concat(JsonHelper.getJSONString(tx_dto)));
	}
	
	/**
	 * 获取设备测速上行分段数据 返回json
	 * @param mac
	 * @return
	 */
	public String getTxPeakSectionJson(String mac){
	    String ret = super.get(generatePeakSectionKey(mac, Type_Tx_Rate));
		return generatePeakSectionJsonArray(ret);
	}
	/**
	 * 获取设备测速上行分段数据 返回dtos
	 * @param mac
	 * @return
	 */
	public List<WifiDeviceTxPeakSectionDTO> getTxPeakSectionDtos(String mac){
	    String ret = super.get(generatePeakSectionKey(mac, Type_Tx_Rate));
		return generatePeakSectionDtos(ret, WifiDeviceTxPeakSectionDTO.class);
	}
	
	/**
	 * 清除设备测速上行分段数据
	 * @param mac
	 */
	public void clearTxPeakSection(String mac){
		super.del(generatePeakSectionKey(mac, Type_Tx_Rate));
	}
	
	/**
	 * 清除设备测速的上下行分段数据
	 * @param mac
	 */
	public void clearPeakSections(String mac){
		super.del(generatePeakSectionKey(mac, Type_Rx_Rate), generatePeakSectionKey(mac, Type_Tx_Rate));
	}
	
	/**
	 * 获取上下行测速分段数据列表dtos
	 * @param mac
	 * 	0 为下行速率dtos
	 *  1 为上行速率dtos
	 * @return
	 */
	public Object[] getPeakSections(String mac){
		String[] keys = new String[2];
		keys[0] = generatePeakSectionKey(mac, Type_Rx_Rate);
		keys[1] = generatePeakSectionKey(mac, Type_Tx_Rate);
		List<String> rets = super.mget(keys);
		if(rets == null || rets.isEmpty()){
			return null;
		}
		Object[] ret_obj = new Object[2];
		ret_obj[0] = generatePeakSectionDtos(rets.get(0), WifiDeviceRxPeakSectionDTO.class);
		ret_obj[1] = generatePeakSectionDtos(rets.get(1), WifiDeviceTxPeakSectionDTO.class);
		return ret_obj;
	}
	
	/**
	 * 解析设备测速分段数据组装成json array
	 * @param peakSection
	 * @return
	 */
	protected String generatePeakSectionJsonArray(String peakSection){
		if(StringUtils.isEmpty(peakSection)) return null;
		
		return StringHelper.LEFT_MEDIUM_BRACKET_STRING_GAP.concat(peakSection.substring(1)).
				concat(StringHelper.RIGHT_MEDIUM_BRACKET_STRING_GAP);
	}
	
	/**
	 * 解析设备测速分段数据 并返回dto list
	 * @param peakSection
	 * @param clasz
	 * @return
	 */
	protected <T> List<T> generatePeakSectionDtos(String peakSection, Class<T> clasz){
		try{
			String generateJsonArray = generatePeakSectionJsonArray(peakSection);
			if(StringUtils.isEmpty(generateJsonArray)) return null;
			return JsonHelper.getDTOList(generateJsonArray, clasz);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}
		return null;
	}
	
	/**
	 * 获取网速测试的下行速率峰值
	 * @param mac
	 * @return
	 * 		0: peak_rate
	 */
	public String getRxPeak(String mac){
		return super.get(generatePeakKey(mac, Type_Rx_Rate));
	}
	/**
	 * 获取网速测试的上行速率峰值
	 * @param mac
	 * @return
	 * 		0: peak_rate
	 */
	public String getTxPeak(String mac){
		return super.get(generatePeakKey(mac, Type_Tx_Rate));
	}

	/**
	 * 获取实时速率所有数据
	 * @param mac
	 * @return 
	 * 		0: tx_rate
	 * 		1: rx_rate
	 * 		2: last_tx_rate
	 * 		3: last_rx_rate
	 * 		4: ratewaiting
	 */
	public List<String> getRate(String mac){
		String[] keys = generateRateKeys(mac);
		return super.mget(keys);
	}
	
	public void clearAll(String mac){
		String[] keys = new String[8];
		//清除设备测速的分段数据
		keys[0] = generatePeakSectionKey(mac, Type_Rx_Rate);
		keys[1] = generatePeakSectionKey(mac, Type_Tx_Rate);
		//清除设备实时速率的数据
		keys[2] = generateRealtimeKey(mac, Type_Tx_Rate);
		keys[3] = generateRealtimeKey(mac, Type_Rx_Rate);
		keys[4] = generateLastKey(mac, Type_Tx_Rate);
		keys[5] = generateLastKey(mac, Type_Rx_Rate);
		keys[6] = generateRateWaitingKey(mac);
		keys[7] = generateHDRateWaitingKey(mac);
		super.del(keys);
	}
	
	@Override
	public String getRedisKey() {
		return null;
	}
	
	@Override
	public String getName() {
		return WifiDeviceRealtimeRateStatisticsStringService.class.getName();
	}
	
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.STATISTICS);
	}
}
