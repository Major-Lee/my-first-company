package com.bhu.vas.api.dto.header;

import org.springframework.util.StringUtils;

import com.smartwork.msip.cores.helper.StringHelper;

@SuppressWarnings("serial")
public class ParserHeader implements java.io.Serializable{
	//指令头长度
	public static final int Cmd_Header_Length = 45;
	public static final int Cmd_Vap_Header_Length = Cmd_Header_Length+8+3+10;
	
	public static final int Online_Prefix = 1;
	public static final int Offline_Prefix = 2;
	public static final int DeviceOffline_Prefix = 3;
	public static final int DeviceNotExist_Prefix = 4;
	public static final int Transfer_Prefix = 5;
	
	/********************** transfer message start ************************/
	
	public static final int Transfer_mtype_0 = 0;
	public static final int Transfer_mtype_1 = 1;
	//增值模块特定指令 mt=2
	//public static final int Transfer_mtype_2 = 2;
	//增值模块特定指令 st=12
	public static final int Transfer_stype_12 = 12;
//	public static final int Transfer_mtype_0_DeviceOnlineReq = 1;//3.4.2	设备上线请求
//	public static final int Transfer_mtype_0_DeviceOnilneRep = 2;//3.4.3	设备上线回应
	
	/********************** transfer message start ************************/
	//D2S Device->Server  S2D Server->Device
	public static final int Vap_Module_Register_D2S = 1;
	public static final int Vap_Module_VapSetting_D2S = 4;
	public static final int Vap_Module_VapQuery_D2S = 6;
	
	public static final int Vap_Module_Upgrade_S2D = 2;
	public static final int Vap_Module_VapSetting_S2D = 3;
	public static final int Vap_Module_VapQuery_S2D = 5;
	
	//消息类型(8字节)
	private int type;
	//12字节mac
	private String mac;
	
	//2字节的操作指令类型
	private String opt;
	//8字节任务id
	private long taskid;
	//报文主类型
	private int mt;
	//子类型(8字节)
	private int st;
	
	//增值消息类型(8字节)
	private int vaptype;
	
	public String getMac() {
		if(StringUtils.isEmpty(mac)) return mac;
		return mac.toLowerCase();
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public long getTaskid() {
		return taskid;
	}
	public void setTaskid(long taskid) {
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
		if(StringUtils.isEmpty(header) || header.length() <37) return pheader;
		String mac = StringHelper.formatMacAddress(header.substring(0, 12));
		if(!StringUtils.isEmpty(mac)){
			pheader.setMac(mac.toLowerCase());
		}
		pheader.setOpt(header.substring(12, 15));
		pheader.setTaskid(Long.parseLong(header.substring(15, 25)));
		//pheader.setTaskid(Long.parseLong(header.substring(15, 22)));
		pheader.setMt(Integer.parseInt(header.substring(25, 29)));
		pheader.setSt(Integer.parseInt(header.substring(29, 37)));
		return pheader;
	}
	
	public void append(String vapheader){
		if(StringUtils.isEmpty(vapheader)) return;
		if(vapheader.length() != 21) return;
		this.setVaptype(Integer.parseInt(vapheader.substring(0,8)));
		this.setOpt(vapheader.substring(8,8+3));
		this.setTaskid(Long.parseLong(vapheader.substring(8+3,8+3+10)));
		/*String vap_type = vapheader.substring(0,8);
		String vap_opt = vapheader.substring(8,8+3);
		String vap_taskid = vapheader.substring(8+3,8+3+10);*/
	}
	
	/*public static ParserHeader builder(String header,int type){
		ParserHeader pheader = new ParserHeader();
		pheader.setType(type);
		if(StringUtils.isEmpty(header) || header.length() <34) return pheader;
		String mac = StringHelper.formatMacAddress(header.substring(0, 12));
		if(!StringUtils.isEmpty(mac)){
			pheader.setMac(mac.toLowerCase());
		}
		pheader.setOpt(header.substring(12, 15));
		pheader.setTaskid(Long.parseLong(header.substring(15, 22)));
		pheader.setMt(Integer.parseInt(header.substring(22, 26)));
		pheader.setSt(Integer.parseInt(header.substring(26, 34)));
		return pheader;
	}*/
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("type=").append(type).append(StringHelper.COMMA_STRING_GAP);
		if(!StringUtils.isEmpty(mac))
			sb.append("mac=").append(mac).append(StringHelper.COMMA_STRING_GAP);
		sb.append("opt=").append(opt).append(StringHelper.COMMA_STRING_GAP);
		sb.append("taskid=").append(taskid).append(StringHelper.COMMA_STRING_GAP);
		sb.append("mt=").append(mt).append(StringHelper.COMMA_STRING_GAP);
		sb.append("st=").append(st).append(StringHelper.COMMA_STRING_GAP);
		sb.append("vaptype=").append(vaptype);
		return sb.toString();
	}
	
	
	public int getVaptype() {
		return vaptype;
	}
	public void setVaptype(int vaptype) {
		this.vaptype = vaptype;
	}
	public static void main(String[] argv){
		String message = "000000058482f41901101100000302384000100000012000000029998888888888<login><ITEM mac=”84:82:f4:23:07:28” version=”1.0.2.8” /></login>";
		/*String vap_type = message.substring(ParserHeader.Cmd_Header_Length,ParserHeader.Cmd_Header_Length+8);
		String vap_opt = message.substring(ParserHeader.Cmd_Header_Length+8,ParserHeader.Cmd_Header_Length+8+3);
		String vap_taskid = message.substring(ParserHeader.Cmd_Header_Length+8+3,ParserHeader.Cmd_Header_Length+8+3+10);
		String payload = message.substring(ParserHeader.Cmd_Vap_Header_Length);*/

		String vap_header = message.substring(ParserHeader.Cmd_Header_Length,ParserHeader.Cmd_Vap_Header_Length);
		String vap_type = vap_header.substring(0,8);
		String vap_opt = vap_header.substring(8,8+3);
		String vap_taskid = vap_header.substring(8+3,8+3+10);
		String payload = message.substring(ParserHeader.Cmd_Vap_Header_Length);
		
		System.out.println(vap_type);
		System.out.println(vap_opt);
		System.out.println(vap_taskid);
		System.out.println(payload);
		
		String msg = "000000058482f41901101100000302384000100000002";
		System.out.println(msg.length());
		
	}
}
