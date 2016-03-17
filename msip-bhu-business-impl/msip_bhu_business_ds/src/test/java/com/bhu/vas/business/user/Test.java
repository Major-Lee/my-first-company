package com.bhu.vas.business.user;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

import com.smartwork.msip.cores.helper.ArithHelper;

public class Test {

	public static void main(String[] args) throws UnsupportedEncodingException, InterruptedException {
		
		String aaa = "1必虎Uplink共享WiFi";
		
		System.out.println(StringEscapeUtils.unescapeHtml(aaa));
		
		System.out.println(StringEscapeUtils.escapeHtml(aaa));//.escapeXml(aaa));
		
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
		
		final String aa = new String("1");
		final String bb = new String("1");
		final Test ttt = new Test();
		Thread a1 = new Thread(){
			@Override
			public void run() {
				ttt.doPrint("a1",aa);
			}
		};
		
		Thread a2 = new Thread(){
			@Override
			public void run() {
				ttt.doPrint("a2",bb);
			}
		};
		a1.start();
		a2.start();
		Thread.currentThread().join();
	}
	
	
	public void doPrint(final String name,String lock){
		synchronized (lock) {
			System.out.println(name+":"+lock);
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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
