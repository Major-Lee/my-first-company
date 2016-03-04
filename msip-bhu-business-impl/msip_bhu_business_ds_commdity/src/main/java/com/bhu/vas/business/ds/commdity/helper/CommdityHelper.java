package com.bhu.vas.business.ds.commdity.helper;

import org.springframework.util.StringUtils;

import com.bhu.vas.api.helper.BusinessEnumType.CommdityStatus;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.localunit.RandomData;

public class CommdityHelper {
	//商品库存数量的无限量的标识
	public static final String STOCK_QUANTITY_INFINITE = "infinite";
	
	/**
	 * 判断商品状态是否为售卖状态
	 * @param status
	 * @return
	 */
	public static boolean onsale(Integer status){
		return CommdityStatus.onsale(status);
	}
	
	/**
	 * 判断价格是否是区间格式
	 * @param price 0.25-10.25
	 * @return
	 */
	public static boolean priceInterval(String price){
		if(StringUtils.isEmpty(price)) return false;
		if(price.contains(StringHelper.MINUS_STRING_GAP)){
			return true;
		}
		return false;
	}
	
	/**
	 * 随机生成区间价格，并且四舍五入保留2位小数点
	 * @param price 区间价格
	 * @return
	 */
	public static Double randomPriceInterval(String price){
		try{
			if(StringUtils.isEmpty(price)) return null;
			
			String[] price_interval = price.split(StringHelper.MINUS_STRING_GAP);
			if(price_interval.length != 2) return null;

			double min = Double.parseDouble(price_interval[0]);
			double max = Double.parseDouble(price_interval[1]);
			//随机生成区间double
			double randomPrice = RandomData.doubleNumber(min, max);
			if(randomPrice < 0) return null;
			//四舍五入保留2位小数点
			return ArithHelper.round(randomPrice, 2);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 商品金额生成
	 * 处理普通价格商品和区间价格商品
	 * @param commdity_price 商品价格
	 * @return
	 */
	public static String generateCommdityAmount(String commdity_price){
		//商品金额处理
		String amount = null;
		if(CommdityHelper.priceInterval(commdity_price)){
			Double randomPrice = CommdityHelper.randomPriceInterval(commdity_price);
			if(randomPrice != null){
				amount = String.valueOf(randomPrice);
			}
		}else{
			amount = commdity_price;
		}
		return amount;
	}
}
