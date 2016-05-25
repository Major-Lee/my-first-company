package com.bhu.statistics.logic;

public interface IUMLogic {
	/**
	 * ��ѯSSID��ͳ������
	 * @param data
	 * @return
	 */
	public String querySSIDStatistics(String data);
	
	/**
	 * ��ѯ���е�pv��uv����
	 * @param data
	 * @return
	 */
	public String queryAllPVAndUV(String data);
	
	/**
	 * ��ѯÿ��SSID��pv��uv������
	 * @param data
	 * @return
	 */
	public String queryDayPVAndUV(String data);
	
	/**
	 * ���ʱ�����Ͳ�ѯSSID���ͳ����Ϣ
	 * 根据时间类型查询SSID统计信息
	 * @author Jason
	 * @param data
	 * @return
	 */
	public String querySSIDInfoByType(String data);

	/**
	 * 查询友盟统计数据
	 * @param data
	 * @return
	 */
	public String queryStatisticsByUM(String data);
	
	/**
	 * 根据时间间隔查询SSID统计信息
	 * @param data
	 * @return
	 */
	public String querySSIDInfoByTime(String data);

}
