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

import com.bhu.vas.api.dto.commdity.internal.pay.ResponsePaymentInfoDTO;
import com.smartwork.msip.cores.helper.JsonHelper;

import nl.bitwalker.useragentutils.UserAgent;

/**
 * Created by bluesand on 5/9/15.
 */
public class T {


    public static void main(String[] args) {
    	String str = "{\"paypal\":[{\"count\":0,\"amount\":0}],\"alipay\":[{\"count\":0,\"amount\":0}],\"weixin\":[{\"count\":0,\"amount\":0}],\"wifiHelper\":[{\"count\":0,\"amount\":0}],\"wifiManage\":[{\"count\":0,\"amount\":0}], \"hee\":[{\"count\":0,\"amount\":0}], \"now\":[{\"count\":0,\"amount\":0}]}";
    	ResponsePaymentInfoDTO dto = JsonHelper.getDTO(str, ResponsePaymentInfoDTO.class);
    	System.out.println(dto.getNow().size());
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
