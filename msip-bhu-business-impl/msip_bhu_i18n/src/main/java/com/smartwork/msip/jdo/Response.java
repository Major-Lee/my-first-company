package com.smartwork.msip.jdo;

import com.smartwork.msip.cores.helper.JsonHelper;

public class Response implements Cloneable{
    public static final Response SUCCESS = new Response(true, "操作成功");
    //public static final Response ERROR = new Response(false, "操作失败");
//    public static final Response ERROR_NOTEXIST = new Response(false, "实体数据不存在");
//    public static final Response ERROR_EXIST = new Response(false, "数据已经存在");
//    public static final Response ERROR_ILLEGAL = new Response(false, "数据非法格式");
    private boolean success = true;
    private String msg;
    
	public Response() {
	}

	public Response(boolean success,String message) {
		super();
		this.success = success;
		this.msg = message;
	}
    

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
    
	public static void main(String[] argv){
    	System.out.println(Response.SUCCESS);
    	try {
			System.out.println(Response.SUCCESS.clone());
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		System.out.println(JsonHelper.getJSONString(SUCCESS));
    }
}
