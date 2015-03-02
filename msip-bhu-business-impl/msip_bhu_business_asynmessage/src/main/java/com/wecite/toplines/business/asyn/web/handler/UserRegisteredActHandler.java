
package com.wecite.toplines.business.asyn.web.handler;import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户注册（/account/create）后调用
 * @author Edmond
 *
 */public class UserRegisteredActHandler {	private final Logger logger = LoggerFactory.getLogger(UserRegisteredActHandler.class);	private UserRegisteredActHandler(){			}		private static class ActHandlerHolder{ 		private static UserRegisteredActHandler instance =new UserRegisteredActHandler(); 	} 	/**	 * 获取工厂单例	 * @return	 */	public static UserRegisteredActHandler getInstance() { 		return ActHandlerHolder.instance; 	}		public void handlerMessage(String message){		logger.info("handle message:"+message);	}}


