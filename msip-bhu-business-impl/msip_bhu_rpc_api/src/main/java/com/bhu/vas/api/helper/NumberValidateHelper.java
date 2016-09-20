package com.bhu.vas.api.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

public class NumberValidateHelper {
	//正数|0并且小数点不超过2位
	//private static String pattern = "^[+]?(([1-9]\\d*[.]?)|(0.))(\\d{0,2})?$";
	private static String pattern = "^[+]?(([0-9]\\d*[.]?)|(0.))(\\d{0,2})?$";
	public static boolean isValidNumberCharacter(final String value){
		if(StringUtils.isEmpty(value)) return false;
		//String namePattern = "[^(0-9\\s\\-\\_)+][a-z0-9\\-\\_]+";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(value);
		return m.find();
	}
	
	
	//金额有效范围0.1-10元，时长有效范围：1-24小时
	public static final double Range_Amount_Min = 0.1d;
	public static final double Range_Amount_Max = 10d;
	
	public static final int Range_Ait_Min = 1*3600;
	public static final int Range_Ait_Max = 24*3600;
	
	public static boolean validAitRange(String param, int minValue, int maxValue){
		if(StringUtils.isEmpty(param)) return false;
		try{
			int current = Integer.parseInt(param);
			return current>= minValue && current <= maxValue;
		}catch(NumberFormatException nfe){
			nfe.printStackTrace(System.out);
		}
		return false;
		
	}
	
	/**
	 * 
	 * @param param 1.0-2.5
	 * @param minValue
	 * @param maxValue
	 * @return
	 */
	public static boolean validAmountRange(String param, double minValue, double maxValue){
		if(StringUtils.isEmpty(param)) return false;
		if(!StringUtils.contains(param, StringHelper.MINUS_STRING_GAP)) return false;
		String[] array = param.split(StringHelper.MINUS_STRING_GAP);
		if(array.length != 2) return false;
		try{
			//正数或0，并且小数位数不超过2位
			//COMMON_DATA_PARAM_PLUSFLOAT_DECIMAL_PART_ERROR
			//if(isValidNumberCharacter(array[0]) && isValidNumberCharacter(array[1])){
			if(!isValidNumberCharacter(array[0]))
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_PLUSFLOAT_DECIMAL_PART_ERROR,new String[]{array[0]});
			if(!isValidNumberCharacter(array[1]))
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_PLUSFLOAT_DECIMAL_PART_ERROR,new String[]{array[1]});
			double start = Double.parseDouble(array[0]);
			double end = Double.parseDouble(array[1]);
			if(start > end)	return false;
			boolean ret1 = amountInRange(start,minValue,maxValue);
			boolean ret2 = amountInRange(end,minValue,maxValue);
			if(ret1 && ret2) return true;
		}catch(NumberFormatException nfe){
			nfe.printStackTrace(System.out);
		}
		return false;
		//return Math.min(Math.max(value, minValue), maxValue);
	}
	
	public static boolean amountInRange(double value,double minValue, double maxValue){
		return value>= minValue && value <= maxValue;
	}
	
	public static void main(String[] argv){
		
		/*System.out.println(DateTimeHelper.getTimeDiff(2*60*60*1000));
		System.out.println(NumberValidateHelper.isValidNumberCharacter("-456.5"));
		System.out.println(NumberValidateHelper.isValidNumberCharacter("456.5"));
		System.out.println(NumberValidateHelper.isValidNumberCharacter("0.15"));
		System.out.println(NumberValidateHelper.isValidNumberCharacter("0.65"));
		System.out.println(NumberValidateHelper.isValidNumberCharacter("0.00"));
		
		double sum = ArithHelper.add(Double.valueOf(0.1d), Double.valueOf(0.65d),Double.valueOf(0.25d));
		System.out.println(sum == 1);*/
		/*System.out.println(NumberValidateHelper.isValidNumberCharacter("456.56"));
		System.out.println(NumberValidateHelper.isValidNumberCharacter("456.555"));
		System.out.println(NumberValidateHelper.isValidNumberCharacter("-456.50"));
		System.out.println(NumberValidateHelper.isValidNumberCharacter("0.0000000000001"));*/
		
		
		System.out.println(NumberValidateHelper.validAmountRange("0.1-11", Range_Amount_Min, Range_Amount_Max));
		
		System.out.println(NumberValidateHelper.validAitRange("3700", Range_Ait_Min, Range_Ait_Max));
	}
}
