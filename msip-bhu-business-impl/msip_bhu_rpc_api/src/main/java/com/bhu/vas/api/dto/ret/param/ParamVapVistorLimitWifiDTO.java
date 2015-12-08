package com.bhu.vas.api.dto.ret.param;




/**
 * 访客网络限速参数
 * @author edmond
 *
 */
@SuppressWarnings("serial")
public class ParamVapVistorLimitWifiDTO implements java.io.Serializable{
	//users_tx_rate users_rx_rate signal_limit(-30) redirect_url("www.bhuwifi.com") idle_timeout(1200) force_timeout(21600) open_resource("") ssid("BhuWIFI-访客")
	private int users_tx_rate;
	private int users_rx_rate;
	
	public Object[] builderProperties() {
		Object[] properties = new Object[2];
		properties[0] = users_tx_rate * 8;
		properties[1] = users_rx_rate * 8;
		return properties;
	}

	public int getUsers_tx_rate() {
		return users_tx_rate;
	}

	public void setUsers_tx_rate(int users_tx_rate) {
		this.users_tx_rate = users_tx_rate;
	}

	public int getUsers_rx_rate() {
		return users_rx_rate;
	}

	public void setUsers_rx_rate(int users_rx_rate) {
		this.users_rx_rate = users_rx_rate;
	}
/*
	private final static String Default_Redirect_url = "www.bhuwifi.com";
	private final static String Default_Open_resource = "";
	private final static String Default_SSID = "BhuWIFI-访客";
	////users_tx_rate users_rx_rate signal_limit(-30) redirect_url("www.bhuwifi.com") idle_timeout(1200) force_timeout(21600) open_resource("") ssid("BhuWIFI-访客")
	*/
	public static ParamVapVistorLimitWifiDTO builderDefault(){
		ParamVapVistorLimitWifiDTO dto = new ParamVapVistorLimitWifiDTO();
		dto.setUsers_tx_rate(128);
		dto.setUsers_rx_rate(128);
		return dto;
	}
	
	public static ParamVapVistorLimitWifiDTO fufillWithDefault(ParamVapVistorLimitWifiDTO param){
		if(param == null) return builderDefault();
		//if(param.getUsers_tx_rate() == 0) param.setUsers_tx_rate(128);
		//if(param.getUsers_rx_rate() == 0) param.setUsers_rx_rate(128);
		return param;
	}
	/*
	public static void main(String[] argv){
		System.out.println(JsonHelper.getJSONString(fufillWithDefault(null)));
		
		ParamVapVistorLimitWifiDTO param = new ParamVapVistorLimitWifiDTO();
		param.setSsid("GOOO理论");
		System.out.println(JsonHelper.getJSONString(fufillWithDefault(param)));
	}*/
}
