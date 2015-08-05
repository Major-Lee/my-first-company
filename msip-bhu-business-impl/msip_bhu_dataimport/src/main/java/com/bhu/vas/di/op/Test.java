package com.bhu.vas.di.op;

import com.bhu.vas.api.rpc.devices.model.pk.WifiDeviceGroupRelationPK;
import com.smartwork.msip.cores.helper.DateTimeHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by bluesand on 6/24/15.
 */
public class Test {

    public static void main(String[] args){


        List<WifiDeviceGroupRelationPK> ids = new ArrayList<WifiDeviceGroupRelationPK>();
        WifiDeviceGroupRelationPK pk = new WifiDeviceGroupRelationPK();
        pk.setGid(1000);
        pk.setMac("mac");
        ids.add(pk);

        pk = new WifiDeviceGroupRelationPK();
        pk.setGid(1001);
        pk.setMac("mac");
        ids.add(pk);

        pk = new WifiDeviceGroupRelationPK();
        pk.setGid(1002);
        pk.setMac("mac");
        ids.add(pk);

        pk = new WifiDeviceGroupRelationPK();
        pk.setGid(1003);
        pk.setMac("mac");
        ids.add(pk);

        System.out.println(ids);


//            List<String> week = new ArrayList<String>();
//            Calendar calendar = Calendar.getInstance();
//
//            int i =0;
//            while (i < 6){
//                calendar.add(Calendar.DAY_OF_MONTH, -1);
//                week.add(
//                        DateTimeHelper.formatDate(calendar.getTime(), DateTimeHelper.FormatPattern5));
//                System.out.println( DateTimeHelper.formatDate(calendar.getTime(), DateTimeHelper.FormatPattern5));
//                i++;
//            }


//            List<String> timeline = new ArrayList<String>();
//
//            timeline.add("1");
//
//            timeline.add("3");
//
//            timeline.add("5");
//
//            timeline.get(2)
//
//            for (String t : timeline) {
//                System.out.println(t);
//            }

//        List<String> timeline = null;
//
//        System.out.println(timeline.isEmpty());


        List<Person> persons = new ArrayList<Person>();

        persons.add(new Person("baidu"));

        persons.add(new Person("google"));

        persons.add(new Person("bing"));

        persons.get(2).setName("360");

        for (Person person : persons) {
            System.out.println(person.getName());
        }

        System.out.println(new Date());
        System.out.println(new Date().getTime());
        System.out.println(new Date(1435543431582L));


    }

}

class Person {
    private String name;
    public Person(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}