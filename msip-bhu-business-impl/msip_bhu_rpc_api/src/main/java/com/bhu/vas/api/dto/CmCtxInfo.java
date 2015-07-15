package com.bhu.vas.api.dto;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.smartwork.msip.cores.helper.StringHelper;

@SuppressWarnings("serial")
public class CmCtxInfo implements java.io.Serializable{
	private String name;
	private String process_seq;
	private String max_client;
	private String state;
	private int last_frag;
	private List<WifiDeviceDTO> client;
	public CmCtxInfo(String name, String process_seq) {
		super();
		this.name = name;
		this.process_seq = process_seq;
	}
	public CmCtxInfo() {
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getProcess_seq() {
		return process_seq;
	}
	public void setProcess_seq(String process_seq) {
		this.process_seq = process_seq;
	}
	
	public String getMax_client() {
		return max_client;
	}
	public void setMax_client(String max_client) {
		this.max_client = max_client;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public int getLast_frag() {
		return last_frag;
	}
	public void setLast_frag(int last_frag) {
		this.last_frag = last_frag;
	}
	public String toUpQueueString(){
		return "up_".concat(toString());
	}
	public String toDownQueueString(){
		return "down_".concat(toString());
	}
	public String toString(){
		return name.concat(StringHelper.UNDERLINE_STRING_GAP).concat(process_seq);
	}
	public List<WifiDeviceDTO> getClient() {
		return client;
	}
	public void setClient(List<WifiDeviceDTO> client) {
		this.client = client;
	}
	public static CmCtxInfo builderCtx(String ctx_name){
		if(StringUtils.isEmpty(ctx_name)) return null;
		String[] split = ctx_name.split(StringHelper.UNDERLINE_STRING_GAP);
		if(split.length != 2) return null;
		return new CmCtxInfo(split[0],split[1]);
	}
	public static String builderDownQueueName(String ctx_name){
		return "down_".concat(ctx_name);
	}
}
