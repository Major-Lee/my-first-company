package com.bhu.vas.api.dto.header;

import org.springframework.util.StringUtils;

import com.smartwork.msip.cores.helper.StringHelper;

@SuppressWarnings("serial")
public class ParserHeader implements java.io.Serializable{
	public static final int Online_Prefix = 1;
	public static final int Offline_Prefix = 2;
	public static final int DeviceOffline_Prefix = 3;
	public static final int DeviceNotExist_Prefix = 4;
	public static final int Transfer_Prefix = 5;
	
	/********************** transfer message start ************************/
	
	public static final int Transfer_mtype_0 = 0;
	public static final int Transfer_mtype_1 = 1;

//	public static final int Transfer_mtype_0_DeviceOnlineReq = 1;//3.4.2	设备上线请求
//	public static final int Transfer_mtype_0_DeviceOnilneRep = 2;//3.4.3	设备上线回应
	
	/********************** transfer message start ************************/
	
	private int type;
	//12字节mac
	private String mac;
	
	//2字节的操作指令类型
	private String opt;
	//8字节任务id
	private int taskid;
	//报文主类型
	private int mt;
	//子类型(8字节)
	private int st;
	public String getMac() {
		if(StringUtils.isEmpty(mac)) return mac;
		return mac.toLowerCase();
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public int getTaskid() {
		return taskid;
	}
	public void setTaskid(int taskid) {
		this.taskid = taskid;
	}
	public int getMt() {
		return mt;
	}
	public void setMt(int mt) {
		this.mt = mt;
	}
	public int getSt() {
		return st;
	}
	public void setSt(int st) {
		this.st = st;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	public String getOpt() {
		return opt;
	}
	public void setOpt(String opt) {
		this.opt = opt;
	}
	public static ParserHeader builder(String header,int type){
		ParserHeader pheader = new ParserHeader();
		pheader.setType(type);
		if(StringUtils.isEmpty(header) || header.length() <34) return pheader;
		pheader.setMac(StringHelper.formatMacAddress(header.substring(0, 12)));
		pheader.setOpt(header.substring(12, 14));
		pheader.setTaskid(Integer.parseInt(header.substring(14, 22)));
		pheader.setMt(Integer.parseInt(header.substring(22, 26)));
		pheader.setSt(Integer.parseInt(header.substring(26, 34)));
		return pheader;
		/*String[] array = new String[4];
		array[0] = msg.substring(0, 12);//12字节mac
		array[1] = msg.substring(12, 22);//10字节任务id
		array[2] = msg.substring(22, 26);//设备报文主类型(4字节)
		array[3] = msg.substring(26, 34);//子类型(8字节)
*/	}
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("type=").append(type).append(StringHelper.COMMA_STRING_GAP);
		if(!StringUtils.isEmpty(mac))
			sb.append("mac=").append(mac).append(StringHelper.COMMA_STRING_GAP);
		sb.append("opt=").append(opt).append(StringHelper.COMMA_STRING_GAP);
		sb.append("taskid=").append(taskid).append(StringHelper.COMMA_STRING_GAP);
		sb.append("mt=").append(mt).append(StringHelper.COMMA_STRING_GAP);
		sb.append("st=").append(st);
		return sb.toString();
	}
}
