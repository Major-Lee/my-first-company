package com.bhu.vas.api.rpc.user.dto;

import java.io.Serializable;

import org.springframework.util.StringUtils;

import com.smartwork.msip.cores.helper.StringHelper;

/**
 * Created by bluesand on 15/4/10.
 */
@SuppressWarnings("serial")
public class UserDeviceDTO implements Serializable {
    private String mac;
    private int uid;
    private String device_name;
    //wifi设备是否在线
    private boolean online;
    //包含从未上线的属性
    private String d_online;

    private long ohd_count;

    private String work_mode;
    private String orig_model;
    private String d_sn;
    private String d_address;
    private int d_snk_allowturnoff;
    //设备的规模级别
    private String o_scalelevel;
	//设备的固件版本号
    private String ver;

    private String add;

    private String ip;

    private String province;
    
    private String city;
    
    private String district;
    
    //最近一次登入时间
    private long lastregedat;
    //最近一次登出时间
    private long lastlogoutat;
    
    //app获取后可判断是否需要更新位置信息
    private String lat;
    private String lon;
    private String d_distributor_type;//运营商类型
        
    public long getLastregedat() {
		return lastregedat;
	}

	public void setLastregedat(long lastregedat) {
		this.lastregedat = lastregedat;
	}

	public long getLastlogoutat() {
		return lastlogoutat;
	}

	public void setLastlogoutat(long lastlogoutat) {
		this.lastlogoutat = lastlogoutat;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public long getOhd_count() {
        return ohd_count;
    }

    public void setOhd_count(long ohd_count) {
        this.ohd_count = ohd_count;
    }

    public String getVer() {
    	if(StringUtils.isEmpty(ver)) return StringHelper.EMPTY_STRING_GAP;
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getWork_mode() {
    	if(StringUtils.isEmpty(work_mode)) return StringHelper.EMPTY_STRING_GAP;
        return work_mode;
    }

    public void setWork_mode(String work_mode) {
        this.work_mode = work_mode;
    }

	public String getOrig_model() {
		return orig_model;
	}

	public void setOrig_model(String orig_model) {
		this.orig_model = orig_model;
	}

    public String getAdd() {
        return add;
    }

    public void setAdd(String add) {
        this.add = add;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

	public String getD_sn() {
		return d_sn;
	}

	public void setD_sn(String d_sn) {
		this.d_sn = d_sn;
	}

	public String getD_address() {
		return d_address;
	}

	public void setD_address(String d_address) {
		this.d_address = d_address;
	}

	public int getD_snk_allowturnoff() {
		return d_snk_allowturnoff;
	}

	public void setD_snk_allowturnoff(int d_snk_allowturnoff) {
		this.d_snk_allowturnoff = d_snk_allowturnoff;
	}

	public String getD_online() {
		return d_online;
	}

	public void setD_online(String d_online) {
		this.d_online = d_online;
	}

	public String getO_scalelevel() {
		return o_scalelevel;
	}

	public void setO_scalelevel(String o_scalelevel) {
		this.o_scalelevel = o_scalelevel;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getD_distributor_type() {
		return d_distributor_type;
	}

	public void setD_distributor_type(String d_distributor_type) {
		this.d_distributor_type = d_distributor_type;
	}
	
}
