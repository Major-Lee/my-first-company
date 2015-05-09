package com.bhu.vas.di.op;

import com.bhu.vas.api.rpc.statistics.model.UserAccessStatistics;
import com.smartwork.msip.cores.helper.JsonHelper;

import java.util.*;

/**
 * Created by bluesand on 5/9/15.
 */
public class T {


    public static void main(String[] args) {
        Map<String,Integer> map = new HashMap<String, Integer>();
        map.put("tanx.com", 4);
        map.put("baidu.com", 24);
        map.put("google.com", 14);


        UserAccessStatistics userAccessStatistics = new UserAccessStatistics();
        userAccessStatistics.setCreated_at(new Date());
        userAccessStatistics.setMac("123123123123");
        userAccessStatistics.replaceAll(sortByValue(map));
        System.out.println(sortByValue(map));
        System.out.println(userAccessStatistics.getExtension());
        userAccessStatistics.setExtension_content(JsonHelper.getJSONString(sortByValue(map), false));
        System.out.println(JsonHelper.getJSONString(userAccessStatistics.getExtension(), false));
        System.out.println(userAccessStatistics.getExtension_content());

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
