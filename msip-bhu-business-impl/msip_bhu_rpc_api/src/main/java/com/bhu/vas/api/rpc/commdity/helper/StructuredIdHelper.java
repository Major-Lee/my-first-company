package com.bhu.vas.api.rpc.commdity.helper;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.dto.commdity.id.StructuredExtSegment;
import com.bhu.vas.api.dto.commdity.id.StructuredId;
import com.bhu.vas.api.helper.BusinessEnumType.OrderExtSegmentPayMode;
import com.smartwork.msip.cores.helper.DateTimeHelper;

public class StructuredIdHelper {
	public static final int StructuredIdLength = 32;
	
	/**
	 * 生成id
	 * id规则 
	 * 4位应用id+8位日期+8位扩展占位+autoid
	 * @param appid 应用id
	 * @param ext_segment 8位扩展占位
	 * @param autoId 流水id
	 * @return
	 */
	public static String generateStructuredIdString(Integer appid, String ext_segment, Long autoId){
		//应用id验证
		if(appid == null || appid < 0 || appid > 9999){
			throw new RuntimeException(String.format("Generate Id Appid [%s] Illegal ", appid));
		}
		//如果扩展占位为空 则生成8位扩展占位字符串补零
		if(StringUtils.isEmpty(ext_segment)){
			ext_segment = String.format("%08d", 0);
		}
		//扩展占位验证
		if(ext_segment.length() != 8){
			throw new RuntimeException(String.format("Generate Id Ext_segment [%s] Illegal ", ext_segment));
		}
		//流水id验证
		//Long autoId = SequenceService.getInstance().getNextId(Order.class.getName());
		if(autoId == null || String.valueOf(autoId).length() > 12){
			throw new RuntimeException(String.format("Generate Id AutoId [%s] Illegal ", autoId));
		}
		
		StringBuffer orderId = new StringBuffer();
		//4位应用id
		orderId.append(String.format("%04d", appid));
		//8位日期 yyyyMMdd
		orderId.append(DateTimeHelper.formatDate(DateTimeHelper.FormatPattern7));
		//8位扩展占位
		orderId.append(ext_segment);
		//12位autoId
		orderId.append(String.format("%012d", autoId));
		
		return orderId.toString();
	}
	
	public static String generateStructuredIdString(Integer appid, Long autoId){
		return generateStructuredIdString(appid, null, autoId);
	}
	
	/**
	 * 根据结构化id字符串生成结构化id对象
	 * @param structuredIdString 结构化id字符串
	 * @return
	 */
	public static StructuredId generateStructuredId(String structuredIdString){
		if(StringUtils.isEmpty(structuredIdString)){
			throw new RuntimeException(String.format("Generate StructuredId param [%s] Illegal ", structuredIdString));
		}
		if(structuredIdString.length() != StructuredIdLength){
			throw new RuntimeException(String.format("Generate StructuredId param length [%s] Illegal ", structuredIdString));
		}
		StructuredId id = new StructuredId();
		
		String appid = structuredIdString.substring(0,4);
		String date = structuredIdString.substring(4,12);
		String ext_segment = structuredIdString.substring(12,20);
		String autoid = structuredIdString.substring(20);
		
		id.setAppid(Integer.parseInt(appid));
		id.setDate(date);
		StructuredExtSegment extSegment = buildStructuredExtSegment(ext_segment);
		id.setExtSegment(extSegment);
		id.setAutoid(Long.parseLong(autoid));
		return id;
	}
	
	/**
	 * 生成扩展业务字符串
	 * @param ext_items 按占位顺序传入扩展占位 (没有传入的补零占位)
	 * @return
	 */
	public static String buildStructuredExtSegmentString(int... ext_items){
		if(ext_items == null || ext_items.length == 0 || ext_items.length > 8){
			return String.format("%08d", 0);
		}
		StringBuffer ext_segment = new StringBuffer();
		for(int ext_item : ext_items){
			ext_segment.append(ext_item);
		}
		int ext_segment_int = Integer.parseInt(ext_segment.toString());
		return String.format("%08d", ext_segment_int);
	}
	
	/**
	 * 根据扩展业务字符串构建扩展业务对象
	 * @param ext_segment
	 * @return
	 */
	public static StructuredExtSegment buildStructuredExtSegment(String ext_segment){
		StructuredExtSegment extSegment = new StructuredExtSegment();
		if(StringUtils.isNotEmpty(ext_segment)){
			if(ext_segment.length() == 8){
				//支付模式
				char paymode_char = ext_segment.charAt(7);
				extSegment.setPaymode(Integer.parseInt(String.valueOf(paymode_char)));
			}
		}
		return extSegment;
	}
	
	/**
	 * 判断订单扩展业务paymode是否为收入
	 * @param structuredExtSegment
	 * @return
	 */
	public static boolean isPaymodeReceipt(StructuredExtSegment structuredExtSegment){
		if(structuredExtSegment == null) return false;
		if(OrderExtSegmentPayMode.isReceipt(structuredExtSegment.getPaymode())) return true;
		return false;
	}
	/**
	 * 判断订单扩展业务paymode是否为支出
	 * @param structuredExtSegment
	 * @return
	 */
	public static boolean isPaymodeExpend(StructuredExtSegment structuredExtSegment){
		if(structuredExtSegment == null) return false;
		if(OrderExtSegmentPayMode.isExpend(structuredExtSegment.getPaymode())) return true;
		return false;
	}
}
