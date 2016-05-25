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
}
