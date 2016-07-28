package com.bhu.vas.di.op.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetUnitPresentSortedSetService;
import com.smartwork.msip.cores.helper.DateTimeHelper;

import redis.clients.jedis.Tuple;

public class HandsetPersentFixedOp {
	public static void main(String[] args) {
		System.out.println("HandsetPersentFixedOp start...");
		Date date = DateTimeHelper.getDateDaysAgo(3);
		long lastDate = date.getTime();
		double score = WifiDeviceHandsetUnitPresentSortedSetService.getInstance().generateScore(lastDate);
		int amount = 0;
		Set<String> redisKeys = WifiDeviceHandsetUnitPresentSortedSetService.getInstance().keySet();
		for(String key : redisKeys){
			List<String> wasteData = new ArrayList<String>();
			Set<Tuple> handsets = WifiDeviceHandsetUnitPresentSortedSetService.getInstance().fetchOfflinePresentWithScores(key);
			if (handsets != null && !handsets.isEmpty()) {
				for (Tuple tuple : handsets) {
					if (score > tuple.getScore()) {
						wasteData.add(tuple.getElement());
						System.out.println(String.format("HandsetPersentFixedOp need clean handset[%s]", tuple.getElement()));
					}
				}
			 	WifiDeviceHandsetUnitPresentSortedSetService.getInstance().removePresentsWithKey(key, wasteData);
			 	System.out.println(String.format("HandsetPersentFixedOp  cleaned  wasteData key[%s] count[%s]",key, wasteData.size()));
				amount += wasteData.size();
			}
		}
		System.out.println(String.format("HandsetPersentFixedOp end... total[%s]", amount));
	}
}
