package com.bhu.vas.business.user.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.user.model.UserLoginState;
import com.bhu.vas.business.user.dao.UserLoginStateDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class UserLoginStateService extends AbstractCoreService<String,UserLoginState,UserLoginStateDao>{
	
	@Resource
	@Override
	public void setEntityDao(UserLoginStateDao userLoginStateDao) {
		super.setEntityDao(userLoginStateDao);
	}
/*	
	*//**
	 * 标记当前日期的登陆状态
	 *//*
	public boolean markUserLoginState(int uid){
		String currentDate = DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern5);
		return this.markUserLoginState(uid, currentDate);
	}
	*//**
	 * 标记指定日期的登陆状态
	 * @param date yyyy-MM-dd
	 *//*
	public boolean markUserLoginState(int uid, String date){
		try{
			if(StringHelper.isNotEmpty(date) && date.length() == 10){			
				//2013-05-14
				String dateprefix = date.substring(0,7);//2013-05
				int dateday = Integer.parseInt(date.substring(8));//14
				UserLoginState userLoginState = super.getOrCreateById(String.valueOf(uid));
				//检查本月登陆状态
				String userLoginKeyStateValue = this.markUserLoginStateValidate(userLoginState, date, dateprefix, dateday);
				userLoginKeyStateValue = this.markLoginState(userLoginKeyStateValue, dateprefix, dateday, UserLoginState.LoginMarkState);
				if(userLoginKeyStateValue != null){
					userLoginState.putInnerModel(dateprefix, userLoginKeyStateValue);
					super.update(userLoginState);
					return true;
				}
				if(this.markUserLoginStateValidate(userLoginState, dateprefix, dateday) || force){
					//增加本日的登陆状态
					String userLoginKeyStateValue = userLoginState.getInnerModel(dateprefix);
					userLoginKeyStateValue = this.markAddOrUpdateLoginState(userLoginKeyStateValue, dateday, UserLoginState.LoginMarkState);
					userLoginState.putInnerModel(dateprefix, userLoginKeyStateValue);
					super.update(userLoginState);
					return true;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return false;
	}
	
	*//**
	 * 检查本月登陆状态
	 * 修复补充本月未登陆的状态
	 * @param userLoginState
	 * @param dateprefix 
	 * @param dateday
	 *//*
	private String markUserLoginStateValidate(UserLoginState userLoginState, String date, String dateprefix, int dateday){
		//取出本月的登陆状态
		String userLoginKeyStateValue = userLoginState.getInnerModel(dateprefix);
		if(StringHelper.isEmpty(userLoginKeyStateValue)){
			int totaldays = DateTimeHelper.getTotalDaysOfThisMonth(date);
			String[] initUserLoginKeyStateValueArray = new String[totaldays];
			for(int i = 0;i<totaldays;i++){
				initUserLoginKeyStateValueArray[i] = UserLoginState.NotLoginMarkState;
			}
			userLoginKeyStateValue = ArrayHelper.toSplitString(initUserLoginKeyStateValueArray, StringHelper.COMMA_STRING_GAP);
		}
		return userLoginKeyStateValue;
		
		//目标日期的坐标
		int currentindex = dateday-1;
		if(currentindex != 0){
			int hascount = 0;
			//取出本月的登陆状态
			String value = userLoginState.getInnerModel(dateprefix);
			if(StringHelper.isNotEmpty(value)){
				String[] userLoginStateArray = value.split(StringHelper.COMMA_STRING_GAP);
				hascount = userLoginStateArray.length;
			}
			int repaircount = currentindex - hascount;
			//修复本月的状态
			if(repaircount >= 0) {
				for(int i = 0;i<repaircount;i++){
					value = this.markAddLoginState(value, UserLoginState.NotLoginMarkState);
				}
				userLoginState.putInnerModel(dateprefix, value);
				return true;
			}
			else{
				return false;
			}
		}
		return true;
	}
	
	*//**
	 * 增加一次MARK状态
	 * @param userLoginStateValue
	 * @param markState
	 * @return
	 *//*
	private String markAddLoginState(String userLoginStateValue, String markState){
		if(userLoginStateValue == null) userLoginStateValue = new String();
		
		if(StringHelper.isNotEmpty(userLoginStateValue)){
			userLoginStateValue = userLoginStateValue.concat(StringHelper.COMMA_STRING_GAP);
		}
		userLoginStateValue = userLoginStateValue.concat(markState);
		return userLoginStateValue;
	}
	*//**
	 * 修改MARK状态
	 * @param userLoginStateValue
	 * @param dateprefix
	 * @param dateday
	 * @param markState
	 * @return
	 *//*
	private String markLoginState(String userLoginKeyStateValue, String dateprefix, int dateday, String markState){
		//String userLoginKeyStateValue = userLoginState.getInnerModel(dateprefix);
		if(StringHelper.isEmpty(userLoginKeyStateValue)) return null;
		
		String[] userLoginStateArray = userLoginKeyStateValue.split(StringHelper.COMMA_STRING_GAP);

		int hascount = userLoginStateArray.length;
		if(hascount >= dateday){
			//目标日期的坐标
			int currentindex = dateday - 1;
			if(!userLoginStateArray[currentindex].equals(markState)){
				userLoginStateArray[currentindex] = markState;
				userLoginKeyStateValue = ArrayHelper.toSplitString(userLoginStateArray, StringHelper.COMMA_STRING_GAP);
				return userLoginKeyStateValue;
			}
		}
		return null;
			
		
		int currentindex = dateday-1;
		if(userLoginStateValue == null){
			if(currentindex == 0){
				return this.markAddLoginState(userLoginStateValue, markState);
			}
		}else{
			String[] userLoginStateArray = userLoginStateValue.split(StringHelper.COMMA_STRING_GAP);
			int hascount = userLoginStateArray.length;
			if(hascount >= dateday){
				userLoginStateArray[currentindex] = markState;
				return ArrayHelper.toSplitString(userLoginStateArray, StringHelper.COMMA_STRING_GAP);
			}else{
				return this.markAddLoginState(userLoginStateValue, markState);
			}
		}
		return userLoginStateValue;
	}
	*//**
	 * 返回当前日期前30天的登陆次数
	 * @param uid
	 * @return
	 *//*
	public int getUserLoginMarkCount(int uid){
		return this.getUserLoginMarkCount(uid, DateTimeHelper.formatDate(DateTimeHelper.FormatPattern9));
	}
	
	*//**
	 * 返回目标日期前30天的登陆次数
	 * @param uid
	 * @param date
	 * @return
	 *//*
	public int getUserLoginMarkCount(int uid, String date){
		int userLoginTimes = 0;
		String dateprefix = date.substring(0,7);//2013-05
		int dateday = Integer.parseInt(date.substring(8));//14
		UserLoginState userLoginState = super.getById(String.valueOf(uid));
		if(userLoginState == null) return 0;
		
		int calculatepoint = dateday - 1;
		List<String> calculateLoginStateList = new ArrayList<String>();
		
		//获取上个月的日期
		String previoumonthdate = DateTimeHelper.getFirstDayOfPreviousMonth(date);
		String previoudateprefix = previoumonthdate.substring(0,7);//2013-05
		//直接取本月和上月的登陆标记
		String previouUserLoginKeyStateValue = userLoginState.getInnerModel(previoudateprefix);
		if(StringHelper.isNotEmpty(previouUserLoginKeyStateValue)){
			String[] userLoginStateArray = previouUserLoginKeyStateValue.split(StringHelper.COMMA_STRING_GAP);
			calculatepoint = calculatepoint + userLoginStateArray.length - 1;
			ArrayHelper.addAll(calculateLoginStateList, userLoginStateArray);
		}
		
		//获取本月的登陆标记
		String userLoginKeyStateValue = userLoginState.getInnerModel(dateprefix);
		if(StringHelper.isNotEmpty(userLoginKeyStateValue)){
			String[] userLoginStateArray = userLoginKeyStateValue.split(StringHelper.COMMA_STRING_GAP);
			ArrayHelper.addAll(calculateLoginStateList, userLoginStateArray);
		}
		
		if(calculateLoginStateList.isEmpty()) return 0;
		int calculatemax = 30;
		int calculate = 0;
		for(int i = calculatepoint;i>=0;i--){
			if(calculate >= calculatemax) break;
			String mark = calculateLoginStateList.get(i);
			if(UserLoginState.isLogin(mark)){
				userLoginTimes++;
			}
			calculate++;
		}
		return userLoginTimes;
	}*/
	 
}
