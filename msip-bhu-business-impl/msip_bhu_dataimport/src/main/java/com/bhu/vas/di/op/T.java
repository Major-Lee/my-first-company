package com.bhu.vas.di.op;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.bitwalker.useragentutils.UserAgent;

/**
 * Created by bluesand on 5/9/15.
 */
public class T {


    public static void main(String[] args) {
        Map<String,Integer> map = new HashMap<String, Integer>();
        map.put("tanx.com", 4);
        map.put("baidu.com", 24);
        map.put("google.com", 14);

        //String agentString = "Mozilla/5.0 (Linux; Android 4.4.2; SM705 Build/SANFRANCISCO) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36";
        //String agentString = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36";
        String agentString = "Mozilla/5.0 (iPhone; CPU iPhone OS 8_3 like Mac OS X) AppleWebKit/600.1.4 (KHTML, like Gecko) Mobile/12F70 MicroMessenger/6.1.5 NetType/WIFI";
        //String agentString = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1;Miser Report)";
        //String agentString = "Mozilla/5.0 (Linux; U; Android 4.2.2; zh-cn; HUAWEI D2-2010 Build/HuaweiD2-2010) AppleWebKit/533.1 (KHTML, like Gecko)Version/4.0 MQQBrowser/5.4 TBS/025411 Mobile Safari/533.1 MicroMessenger/6.1.0.73_r1097298.543 NetType/WIFI";
        //String agentString = "Mozilla/5.0 (Linux; U; Android 4.1.2; zh-cn; GT-I9268 Build/JZO54K) AppleWebKit/533.1 (KHTML, like Gecko)Version/4.0 MQQBrowser/5.4 TBS/025411 Mobile Safari/533.1 MicroMessenger/6.1.0.66_r1062275.542 NetType/WIFI";
        UserAgent useragent = new UserAgent(agentString);
        System.out.println(useragent.getOperatingSystem());
        System.out.println(useragent.getBrowser());
        System.out.println(useragent.getId());
        System.out.println(useragent.getBrowserVersion());
        System.out.println(T.catchUserAgentTermianl(agentString));
        //OperatingSystem.
        //if(useragent.getOperatingSystem().equals(OperatingSystem.LINUX)||useragent.getOperatingSystem().equals(OperatingSystem.MAC_OS)||useragent.getOperatingSystem().equals(OperatingSystem.MAC_OS_X));{
      	
       // }

    }
    public static String catchUserAgentTermianl( String userAgent){
    	Pattern pattern = Pattern.compile(";\\s?(\\S*?\\s?\\S*?)\\s?(Build)?/");
        Matcher matcher = pattern.matcher(userAgent);
        String model = null;
        if (matcher.find()) {
          model = matcher.group(1).trim();
          //log.debug("通过userAgent解析出机型：" + model);
          return model;
        }
        return null;
    }
    

    public static <K, V extends Comparable<? super V>> Map<K, V>
    sortByValue( Map<K, V> map )
    {
        List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>( map.entrySet() );
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }
}
