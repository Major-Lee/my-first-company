package com.bhu.vas.di.op;

import com.smartwork.msip.cores.helper.DateTimeHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by bluesand on 6/24/15.
 */
public class Test {

    public static void main(String[] args){

            List<String> week = new ArrayList<String>();
            Calendar calendar = Calendar.getInstance();

            int i =0;
            while (i < 6){
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                week.add(
                        DateTimeHelper.formatDate(calendar.getTime(), DateTimeHelper.FormatPattern5));
                System.out.println( DateTimeHelper.formatDate(calendar.getTime(), DateTimeHelper.FormatPattern5));
                i++;
            }
    }
}
