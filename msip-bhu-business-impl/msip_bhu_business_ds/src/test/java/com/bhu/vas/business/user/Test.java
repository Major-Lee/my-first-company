package com.bhu.vas.business.user;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.smartwork.msip.cores.helper.ArithHelper;

public class Test {

	public static void main(String[] args) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		List<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);
		list.add(3);
		
//		List<Integer> result = PageHelper.partialList(list, 5, 5);
//		System.out.println(result);
		System.out.println(list.subList(0, 1));
		url();
		
//		WifiDeviceStatusDTO dto = new WifiDeviceStatusDTO();
//		dto.setIndex(1);
//		dto.setCurrent_cpu_usage("1");
//		WifiDeviceStatus entity = new WifiDeviceStatus();
//		BeanUtils.copyProperties(dto, entity);
//		System.out.println(entity.getCurrent_cpu_usage());
		//System.out.println(URLEncoder.encode("北京市", "utf-8"));
		System.out.println(ArithHelper.percent(13495, 13496, 2));
	}
	

		//使用java正则表达式获取url地址中的主域名代码如下:
		/**
		 * 如果要得到 chinajavaworld.com/entry/4545/0/正则表达式最后加上 .* 即可.
		 *如要取完整域名，使用以下代码:
		 *Pattern.compile("[^//]*?\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
		 */
		public static void url() {
			String url = "http://qdan.me/list/VNOmAhi6r3LKr1AT";
			//Pattern p = Pattern.compile("(?<=http://|\\.)[^.]*?\\.(com|cn|net|org|biz|info|cc|tv)",Pattern.CASE_INSENSITIVE);
			
			//获取完整的域名
			Pattern p =Pattern.compile("[^//]*?\\.(com|cn|net|org|biz|info|cc|tv|me)", Pattern.CASE_INSENSITIVE);
			Matcher matcher = p.matcher(url);
			matcher.find();
			System.out.println(matcher.group());
			
			System.out.println(ArithHelper.div(5, 1, 2));
		}

}
