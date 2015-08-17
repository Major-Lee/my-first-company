package com.bhu.vas.di.op;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.bhu.vas.api.rpc.devices.model.pk.WifiDeviceGroupRelationPK;
import com.smartwork.msip.cores.helper.DateTimeExtHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import org.elasticsearch.common.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by bluesand on 6/24/15.
 */
public class Test {

    public static void main(String[] args){

        long ts= 1439637254920L;
        System.out.println(DateTimeHelper.formatDate(new Date(ts), DateTimeHelper.longDateFormat));
        System.out.println(DateTimeHelper.parseDate("2015-08-17", DateTimeHelper.shortDateFormat).getTime());
        System.out.println(getDateZeroTime(new Date()).getTime());

        System.out.println((int)(1439740800779L - 1439783798513L) / (24 * 3600 * 1000));
//        System.out.println(DateTimeExtHelper.getSevenDateOfWeek());
//        long currentTime = System.currentTimeMillis();
//        String currentTimeZero = DateTimeHelper.formatDate(new Date(), DateTimeHelper.shortDateFormat);
//        long currentZeroTime = getDateZeroTime(new Date()).getTime();
//        System.out.println(DateTimeHelper.formatDate(new Date(currentZeroTime), DateTimeHelper.longDateFormat));
//        System.out.println(DateTimeHelper.formatDate(new Date(ts), DateTimeHelper.longDateFormat));
        long space = 1439740800779L - 1439783798513L;
        System.out.println(1439740800779L - 1439783798513L);
        int  offset = (int)(space/(24 * 3600 * 1000));
        if (space < 0) {
            offset = -1;
        }
        if (offset > 5) {
            offset = 5;
        }

        System.out.println(offset);


        System.out.println(DateTimeHelper.parseDate("2015-08-08 23:00:00", DateTimeHelper.longDateFormat).getTime());

//        String dateStr = "2015-08-06 19:01:35";
//        Date date = DateTimeHelper.parseDate(dateStr,DateTimeHelper.shortDateFormat);
//        System.out.println(date.getTime());
//        System.out.println(DateTimeHelper.formatDate(date, DateTimeHelper.longDateFormat));
//
//        if (new Date().getTime() - date.getTime() > 24 * 3600) {
//            System.out.println("1234");
//        }

//        Calendar cal = Calendar.getInstance();
//        cal.setTime(new Date());
//        int hour = cal.get(Calendar.HOUR_OF_DAY);
//        int minute = cal.get(Calendar.MINUTE);
//        int second = cal.get(Calendar.SECOND);
//        //时分秒（毫秒数）
//        long millisecond = hour*60*60*1000 + minute*60*1000 + second*1000;
//        //凌晨00:00:00
//        cal.setTimeInMillis(cal.getTimeInMillis()-millisecond);
//
//        System.out.println(cal.getTime().getTime());



//        List<WifiDeviceGroupRelationPK> ids = new ArrayList<WifiDeviceGroupRelationPK>();
//        WifiDeviceGroupRelationPK pk = new WifiDeviceGroupRelationPK();
//        pk.setGid(1000);
//        pk.setMac("mac");
//        ids.add(pk);
//
//        pk = new WifiDeviceGroupRelationPK();
//        pk.setGid(1001);
//        pk.setMac("mac");
//        ids.add(pk);
//
//        pk = new WifiDeviceGroupRelationPK();
//        pk.setGid(1002);
//        pk.setMac("mac");
//        ids.add(pk);
//
//        pk = new WifiDeviceGroupRelationPK();
//        pk.setGid(1003);
//        pk.setMac("mac");
//        ids.add(pk);
//
//        System.out.println(ids);


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


//        List<Person> persons = new ArrayList<Person>();
//
//        persons.add(new Person("baidu"));
//
//        persons.add(new Person("google"));
//
//        persons.add(new Person("bing"));
//
//        persons.get(2).setName("360");
//
//        for (Person person : persons) {
//            System.out.println(person.getName());
//        }
//
//        System.out.println(new Date());
//        System.out.println(new Date().getTime());
//        System.out.println(new Date(1435543431582L));


    }

    private static Date getDateZeroTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        //时分秒（毫秒数）
        long millisecond = hour*60*60*1000 + minute*60*1000 + second*1000;
        //凌晨00:00:00
        cal.setTimeInMillis(cal.getTimeInMillis()-millisecond);

        return cal.getTime();
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