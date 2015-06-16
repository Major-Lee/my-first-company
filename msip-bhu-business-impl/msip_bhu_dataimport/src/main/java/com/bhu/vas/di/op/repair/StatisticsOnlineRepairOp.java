package com.bhu.vas.di.op.repair;

import java.util.Date;

import com.smartwork.msip.cores.helper.DateTimeHelper;


public class StatisticsOnlineRepairOp {
	
	public static void main(String[] argv) {//throws ElasticsearchException, ESException, IOException, ParseException{
		if(argv.length <3) return;
		String date = argv[0];// ADD REMOVE
		int deviceOnlineCount = Integer.parseInt(argv[1]);
		int hansetOnlineCount = Integer.parseInt(argv[2]);
		Date certainDate = DateTimeHelper.parseDate(date, DateTimeHelper.FormatPattern16);
		System.out.println(String.format(" Date[%s] deviceOnlineCount[%s] hansetOnlineCount[%s]", DateTimeHelper.formatDate(certainDate, DateTimeHelper.DefalutFormatPattern),deviceOnlineCount,hansetOnlineCount));
		
		//StatisticsFragmentMaxOnlineDeviceService.getInstance().fragmentAllReSet(certainDate, deviceOnlineCount);
		//StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentAllReSet(certainDate, hansetOnlineCount);
	}
}
