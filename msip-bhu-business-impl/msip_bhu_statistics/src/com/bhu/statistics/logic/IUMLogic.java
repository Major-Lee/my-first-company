package com.bhu.statistics.logic;

public interface IUMLogic {
	/**
	 * 查询SSID的统计数量
	 * @param data
	 * @return
	 */
	public String querySSIDStatistics(String data);
	
	/**
	 * 查询所有的pv和uv数量
	 * @param data
	 * @return
	 */
	public String queryAllPVAndUV(String data);
	
	/**
	 * 查询每天SSID的pv和uv的数量
	 * @param data
	 * @return
	 */
	public String queryDayPVAndUV(String data);
	
	/**
	 * 根据时间类型查询SSID相关统计信息
	 * @author Jason
	 * @param data
	 * @return
	 */
	public String querySSIDInfoByType(String data);
}
