package com.bhu.vas.api.rpc.user.model;

import java.util.ArrayList;
import java.util.List;

import com.smartwork.msip.cores.helper.ArrayHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.model.extjson.KeyDtoMapJsonExtIntModel;

/**
 * 记录用户登录的标识
 * 按月份分隔
 * @author lawliet
 *
 */
@SuppressWarnings("serial")
public class UserLoginState extends KeyDtoMapJsonExtIntModel<String> {
	public static final String LoginMarkState = "1";
	public static final String NotLoginMarkState = "0";
	
	@Override
	public Class<String> getJsonParserModel() {
		return String.class;
	}
	
	public static boolean isLogin(String mark){
		if(StringHelper.isEmpty(mark)) return false;
		if(LoginMarkState.equals(mark)) return true;
		return false;
	}
	
	
	/**
	 * 判断指定日期是否登录
	 * @param date eg:2012-03-04
	 * @return
	 */
	public boolean wasCertainDayLogin(String date){
		String dateprefix = date.substring(0,7);//2013-05
		int dateday = Integer.parseInt(date.substring(8));//14
		String loginMarks = this.getInnerModel(dateprefix);
		if(loginMarks == null) return false;
		int calculatepoint = dateday - 1;
		//List<String> calculateLoginStateList = new ArrayList<String>();
		String[] userLoginStateArray = loginMarks.split(StringHelper.COMMA_STRING_GAP);
		if(userLoginStateArray == null || userLoginStateArray.length==0) return false;
		if(userLoginStateArray.length < dateday)return false;
		String loginMark = userLoginStateArray[calculatepoint];
		return isLogin(loginMark);
	}
	
	/**
	 * 后30天的登陆记录（包括本日）
	 * @param date
	 * @return
	 */
	public String[] getLoginMarksForAfter30day(String date){
		return getLoginMarksForAfterdays(date, 30);
	}
	
	public String[] getLoginMarksForAfterdays(String date, int afterdays){
		//String dateprefix = date.substring(0,7);//2013-05
		int dateday = Integer.parseInt(date.substring(8));//14
		int calculatepoint = dateday - 1;
		//List<String> calculateLoginStateList = new ArrayList<String>();
		
		//获取下个月的日期
		String after1monthdate = DateTimeHelper.getFirstDayOfNextMonth(date);
		//获取下下个月的日期
		String after2monthdate = DateTimeHelper.getFirstDayOfNextMonth(after1monthdate);
		
		//String previou1dateprefix = previou1monthdate.substring(0,7);//2013-05
		String[] after2userLoginStateArray = this.getLoginMarksForMonth(after2monthdate);
		//calculatepoint = calculatepoint + after2userLoginStateArray.length - 1;
		//ArrayHelper.addAll(calculateLoginStateList, previou2userLoginStateArray);
		
		//String previou1dateprefix = previou1monthdate.substring(0,7);//2013-05
		String[] after1userLoginStateArray = this.getLoginMarksForMonth(after1monthdate);
		//calculatepoint = calculatepoint + after1userLoginStateArray.length - 1;
		//ArrayHelper.addAll(calculateLoginStateList, previou1userLoginStateArray);
		String[] afterUserLoginStateArray = ArrayHelper.concat(after1userLoginStateArray, after2userLoginStateArray);
		//获取本月的登陆标记
		String[] userLoginStateArray = this.getLoginMarksForMonth(date);
		String[] totalUserLoginStateArray = ArrayHelper.concat(userLoginStateArray, afterUserLoginStateArray);
		//ArrayHelper.addAll(calculateLoginStateList, userLoginStateArray);
		//String userLoginKeyStateValue = this.getInnerModel(dateprefix);
		/*if(StringHelper.isNotEmpty(userLoginKeyStateValue)){
			String[] userLoginStateArray = userLoginKeyStateValue.split(StringHelper.COMMA_STRING_GAP);
			ArrayHelper.addAll(calculateLoginStateList, userLoginStateArray);
		}*/
		String[] loginMarksForDays = new String[afterdays];
		System.arraycopy(totalUserLoginStateArray, calculatepoint, loginMarksForDays, 0, afterdays);
		return loginMarksForDays;
	}
	
	/**
	 * 获取本月的登陆记录
	 * @param date
	 * @return
	 */
	public String[] getLoginMarksForMonth(String date){
		String[] userLoginStateArray = null;
		String dateprefix = date.substring(0,7);//2013-05
		//int dateday = Integer.parseInt(date.substring(8));//14
		String userLoginKeyStateValue = this.getInnerModel(dateprefix);
		if(StringHelper.isEmpty(userLoginKeyStateValue)){
			int monthtotaldays = DateTimeHelper.getTotalDaysOfThisMonth(dateprefix);
			userLoginStateArray = new String[monthtotaldays];
			for(int i = 0;i<userLoginStateArray.length;i++){
				userLoginStateArray[i] = NotLoginMarkState;
			}
		}else{
			userLoginStateArray = userLoginKeyStateValue.split(StringHelper.COMMA_STRING_GAP);
		}
		return userLoginStateArray;
	}
	
	/**
	 * 标记当前日期的登陆状态
	 */
	public boolean markUserLoginState(){
		String currentDate = DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern5);
		return this.markUserLoginState(currentDate);
	}
	
	public boolean markUserLoginStateWithDate(String certainDate){
		return this.markUserLoginState(certainDate);
	}
	
	/**
	 * 标记指定日期的登陆状态
	 * @param date yyyy-MM-dd
	 */
	public boolean markUserLoginState(String date){
		try{
			if(StringHelper.isNotEmpty(date) && date.length() == 10){			
				//2013-05-14
				String dateprefix = date.substring(0,7);//2013-05
				int dateday = Integer.parseInt(date.substring(8));//14
				//UserLoginState userLoginState = super.getOrCreateById(String.valueOf(uid));
				//检查本月登陆状态
				String userLoginKeyStateValue = this.markUserLoginStateValidate(date, dateprefix, dateday);
				userLoginKeyStateValue = this.markLoginState(userLoginKeyStateValue, dateprefix, dateday, UserLoginState.LoginMarkState);
				if(userLoginKeyStateValue != null){
					this.putInnerModel(dateprefix, userLoginKeyStateValue);
					//super.update(userLoginState);
					return true;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 检查本月登陆状态
	 * 修复补充本月未登陆的状态
	 * @param userLoginState
	 * @param dateprefix 
	 * @param dateday
	 */
	public String markUserLoginStateValidate(String date, String dateprefix, int dateday){
		//取出本月的登陆状态
		String userLoginKeyStateValue = this.getInnerModel(dateprefix);
		if(StringHelper.isEmpty(userLoginKeyStateValue)){
			int totaldays = DateTimeHelper.getTotalDaysOfThisMonth(date);
			String[] initUserLoginKeyStateValueArray = new String[totaldays];
			for(int i = 0;i<totaldays;i++){
				initUserLoginKeyStateValueArray[i] = UserLoginState.NotLoginMarkState;
			}
			userLoginKeyStateValue = ArrayHelper.toSplitString(initUserLoginKeyStateValueArray, StringHelper.COMMA_STRING_GAP);
		}
		return userLoginKeyStateValue;
	}
	
	/**
	 * 修改MARK状态
	 * @param userLoginStateValue
	 * @param dateprefix
	 * @param dateday
	 * @param markState
	 * @return
	 */
	public String markLoginState(String userLoginKeyStateValue, String dateprefix, int dateday, String markState){
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
	}
	
	/**
	 * 返回当前日期前30天的登陆次数
	 * @param uid
	 * @return
	 */
	public int getUserLoginMarkCount(){
		return this.getUserLoginMarkCount(DateTimeHelper.formatDate(DateTimeHelper.FormatPattern9));
	}
	
	/**
	 * 返回目标日期前30天的登陆次数
	 * @param uid
	 * @param date
	 * @return
	 */
	public int getUserLoginMarkCount(String date){
		int userLoginTimes = 0;
		String dateprefix = date.substring(0,7);//2013-05
		int dateday = Integer.parseInt(date.substring(8));//14
		//UserLoginState userLoginState = super.getById(String.valueOf(uid));
		//if(userLoginState == null) return 0;
		
		//int calculatepoint = dateday - 1;
		List<String> calculateLoginStateList = new ArrayList<String>();
		
		//获取上个月的日期
		String previoumonthdate = DateTimeHelper.getFirstDayOfPreviousMonth(date);
		String previoudateprefix = previoumonthdate.substring(0,7);//2013-05
		//直接取本月和上月的登陆标记
		String previouUserLoginKeyStateValue = this.getInnerModel(previoudateprefix);
		if(StringHelper.isNotEmpty(previouUserLoginKeyStateValue)){
			String[] userLoginStateArray = previouUserLoginKeyStateValue.split(StringHelper.COMMA_STRING_GAP);
			int length = userLoginStateArray.length;
			if(length > dateday){
				for(int i = dateday;i<length;i++){
					calculateLoginStateList.add(userLoginStateArray[i]);
				}
			}
			//calculatepoint = calculatepoint + userLoginStateArray.length - 1;
			//ArrayHelper.addAll(calculateLoginStateList, userLoginStateArray);
		}
		
		//获取本月的登陆标记
		String userLoginKeyStateValue = this.getInnerModel(dateprefix);
		if(StringHelper.isNotEmpty(userLoginKeyStateValue)){
			String[] userLoginStateArray = userLoginKeyStateValue.split(StringHelper.COMMA_STRING_GAP);
			for(int i = 0;i<dateday;i++){
				calculateLoginStateList.add(userLoginStateArray[i]);
			}
			//ArrayHelper.addAll(calculateLoginStateList, userLoginStateArray);
		}
		
		if(calculateLoginStateList.isEmpty()) return 0;
		/*int calculatemax = 30;
		int calculate = 0;
		for(int i = calculatepoint;i>=0;i--){
			if(calculate >= calculatemax) break;
			String mark = calculateLoginStateList.get(i);
			if(UserLoginState.isLogin(mark)){
				userLoginTimes++;
			}
			calculate++;
		}*/
		for(String state : calculateLoginStateList){
			if(UserLoginState.isLogin(state)){
				userLoginTimes++;
			}
		}
		return userLoginTimes;
	}
}
