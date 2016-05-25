package com.bhu.statistics.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.bhu.statistics.util.enums.ErrorCodeEnum;
public class NotifyUtil {
	/**
	 * 返回错误方法
	 * @Title returnError 
	 * @Description 向上一层返回错误信息 
	 * @author Alan
	 * @date 2015年9月5日 上午10:18:44
	 * @updateTime 2015年9月5日 上午10:18:44
	 * @udpateAuthor  Alan
	 * @param errorCode 错误枚举值
	 * @param msg 额外的错误消息
	 * @return String  {"code":"200000","msg":"参数不能为空：data不能为空"}
	 */
	public static String error(ErrorCodeEnum errorCode,String msg) {
		String result=error(errorCode, msg, false);
		return result;
	}
	/**
	 * 返回错误方法
	 * @Title returnError 
	 * @Description TODO(这里用一句话描述这个方法的作用) 
	 * @author Alan
	 * @date 2015年9月5日 上午10:20:43
	 * @updateTime 2015年9月5日 上午10:20:43
	 * @udpateAuthor  Alan
	 * @param errorCode 错误枚举值
	 * @return
	 * @return String  {"code":"200000","msg":"参数不能为空"}
	 */
	public static String error(ErrorCodeEnum errorCode) {
		String result=error(errorCode, null);
		return result;
	}
	/**
	 * 返回错误方法
	 * @Title returnError 
	 * @Description TODO(这里用一句话描述这个方法的作用) 
	 * @author Alan
	 * @date 2015年9月5日 上午10:20:43
	 * @updateTime 2015年9月5日 上午10:20:43
	 * @udpateAuthor  Alan
	 * @param errorCode 错误枚举值
	 * @return
	 * @return String  {"code":"200000","msg":"参数不能为空"}
	 */
	public static String error(ErrorCodeEnum errorCode,String msg,boolean flag) {
		Map<String, String> map=new HashMap<String, String>();
		String code=errorCode.getCode();
		String message=errorCode.getMsg();
		if(StringUtils.isNotBlank(msg)&&!flag){
			message+="："+msg;
		}else if(StringUtils.isNotBlank(msg)&&flag){
			message=msg;
		}
		map.put("code", code);
		map.put("msg", message);
		String result=JSONObject.toJsonString(map);
		return result;
	}
	/**
	 * 返回成功结果集
	 * @Title success 
	 * @Description 返回成功结果集
	 * @author Alan
	 * @date 2015年9月5日 上午10:22:28
	 * @updateTime 2015年9月5日 上午10:22:28
	 * @udpateAuthor  Alan
	 * @param body 返回值中body参数结果 Object 
	 * @return String   {"code":"0","msg":"success","body":[{"id":"1","name":"测试"}]}
	 */
	public static String success(Object body){
		Map<String, Object> map=new HashMap<String, Object>();
		String code=ErrorCodeEnum.SUCCESS.getCode();
		String message=ErrorCodeEnum.SUCCESS.getMsg();
		map.put("code", code);
		map.put("msg", message);
		map.put("success", true);
		if(body==null){
			body="{}";
		}
		map.put("result", body);
		String result=JSONObject.toJsonString(map);
		return result;
	}
	public static String success(){
		return success(null);
	}
}
