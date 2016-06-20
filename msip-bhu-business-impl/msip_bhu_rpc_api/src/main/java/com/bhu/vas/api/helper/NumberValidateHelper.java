package com.bhu.vas.api.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;

public class NumberValidateHelper {
	//正数并且小数点不超过2位
	private static String pattern = "^[+]?(([1-9]\\d*[.]?)|(0.))(\\d{0,2})?$";
	public static boolean isValidNumberCharacter(final String value){
		if(StringUtils.isEmpty(value)) return false;
		//String namePattern = "[^(0-9\\s\\-\\_)+][a-z0-9\\-\\_]+";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(value);
		return m.find();
	}
	
	public static void main(String[] argv){
		
		System.out.println(DateTimeHelper.getTimeDiff(2*60*60*1000));
		System.out.println(NumberValidateHelper.isValidNumberCharacter("-456.5"));
		System.out.println(NumberValidateHelper.isValidNumberCharacter("456.5"));
		System.out.println(NumberValidateHelper.isValidNumberCharacter("0.15"));
		System.out.println(NumberValidateHelper.isValidNumberCharacter("0.65"));
		System.out.println(NumberValidateHelper.isValidNumberCharacter("0.25"));
		
		double sum = ArithHelper.add(Double.valueOf(0.1d), Double.valueOf(0.65d),Double.valueOf(0.25d));
		System.out.println(sum == 1);
		/*System.out.println(NumberValidateHelper.isValidNumberCharacter("456.56"));
		System.out.println(NumberValidateHelper.isValidNumberCharacter("456.555"));
		System.out.println(NumberValidateHelper.isValidNumberCharacter("-456.50"));
		System.out.println(NumberValidateHelper.isValidNumberCharacter("0.0000000000001"));*/
		
		
		
		
	}
}
