package com.bhu.vas.api.vto.device;

import java.util.List;

import com.bhu.vas.api.vto.config.URouterDeviceConfigInterfaceVTO;
import com.bhu.vas.api.vto.config.URouterDeviceConfigVapVTO;

/**
 * 目前用于给运营商系统返回数据
 * @author Yetao
 *
 */

@SuppressWarnings("serial")
public class DeviceConfigDetailVTO implements java.io.Serializable{
	private String mac;
	private String sn;
	//固件版本号
	private String orig_swver;
	//固件 硬件版本
	private String orig_hdver;
	//工作模式
	private String work_mode;

	//绑定的uid
	private int uid;
	//绑定用户手机号
	private String mobileno;
	//是否在线
	private boolean online;
	//地理位置
	private String address;
	//首次上线时间
	private String first_reg_at;
	//最后上线时间
	private String last_reg_at;
	//最后下线时间
	private String last_logout_at;
	//在线时长
	private String dod;

	//双频合一
	private String rf_2in1;
	//admin管理密码
	private String admin_pwd;

	//vap列表
	private List<URouterDeviceConfigVapVTO> vaps;
	//interface列表
	private List<URouterDeviceConfigInterfaceVTO> ifs;
	private DeviceSharedealVTO dsv;

	//共享网络是否打开
	private boolean snk_on;
	//共享网络类型
	private String snk_type;
	private String snk_ssid;
	//portal地址
	private String remote_auth_url;
	private int users_tx_rate;
	private int users_rx_rate;
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getOrig_swver() {
		return orig_swver;
	}
	public void setOrig_swver(String orig_swver) {
		this.orig_swver = orig_swver;
	}
	public String getOrig_hdver() {
		return orig_hdver;
	}
	public void setOrig_hdver(String orig_hdver) {
		this.orig_hdver = orig_hdver;
	}
	public String getWork_mode() {
		return work_mode;
	}
	public void setWork_mode(String work_mode) {
		this.work_mode = work_mode;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	public boolean isOnline() {
		return online;
	}
	public void setOnline(boolean online) {
		this.online = online;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getFirst_reg_at() {
		return first_reg_at;
	}
	public void setFirst_reg_at(String first_reg_at) {
		this.first_reg_at = first_reg_at;
	}
	public String getLast_reg_at() {
		return last_reg_at;
	}
	public void setLast_reg_at(String last_reg_at) {
		this.last_reg_at = last_reg_at;
	}
	public String getLast_logout_at() {
		return last_logout_at;
	}
	public void setLast_logout_at(String last_logout_at) {
		this.last_logout_at = last_logout_at;
	}
	public String getDod() {
		return dod;
	}
	public void setDod(String dod) {
		this.dod = dod;
	}
	public String getRf_2in1() {
		return rf_2in1;
	}
	public void setRf_2in1(String rf_2in1) {
		this.rf_2in1 = rf_2in1;
	}
	public String getAdmin_pwd() {
		return admin_pwd;
	}
	public void setAdmin_pwd(String admin_pwd) {
		this.admin_pwd = admin_pwd;
	}
	public List<URouterDeviceConfigVapVTO> getVaps() {
		return vaps;
	}
	public void setVaps(List<URouterDeviceConfigVapVTO> vaps) {
		this.vaps = vaps;
	}
	public List<URouterDeviceConfigInterfaceVTO> getIfs() {
		return ifs;
	}
	public void setIfs(List<URouterDeviceConfigInterfaceVTO> ifs) {
		this.ifs = ifs;
	}
	public boolean isSnk_on() {
		return snk_on;
	}
	public void setSnk_on(boolean snk_on) {
		this.snk_on = snk_on;
	}
	public String getSnk_type() {
		return snk_type;
	}
	public void setSnk_type(String snk_type) {
		this.snk_type = snk_type;
	}
	public String getSnk_ssid() {
		return snk_ssid;
	}
	public void setSnk_ssid(String snk_ssid) {
		this.snk_ssid = snk_ssid;
	}
	public String getRemote_auth_url() {
		return remote_auth_url;
	}
	public void setRemote_auth_url(String remote_auth_url) {
		this.remote_auth_url = remote_auth_url;
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
	public DeviceSharedealVTO getDsv() {
		return dsv;
	}
	public void setDsv(DeviceSharedealVTO dsv) {
		this.dsv = dsv;
	}
}
