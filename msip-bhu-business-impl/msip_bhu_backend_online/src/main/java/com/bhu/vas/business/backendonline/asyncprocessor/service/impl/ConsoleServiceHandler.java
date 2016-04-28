package com.bhu.vas.business.backendonline.asyncprocessor.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.BusinessEnumType.OrderPaymentType;
import com.bhu.vas.api.helper.BusinessEnumType.OrderStatus;
import com.bhu.vas.api.helper.BusinessEnumType.OrderUmacType;
import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.GrayLevel;
import com.bhu.vas.api.helper.WifiDeviceDocumentEnumType;
import com.bhu.vas.api.helper.WifiDeviceDocumentEnumType.OnlineEnum;
import com.bhu.vas.api.helper.WifiDeviceDocumentEnumType.UBindedEnum;
import com.bhu.vas.api.rpc.charging.model.WifiDeviceSharedealConfigs;
import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.api.rpc.devices.dto.DeviceVersion;
import com.bhu.vas.api.rpc.user.model.UserWalletLog;
import com.bhu.vas.business.asyn.spring.model.DeviceSearchResultExportFileDTO;
import com.bhu.vas.business.asyn.spring.model.OrderSearchResultExportFileDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.AsyncMsgHandleService;
import com.bhu.vas.business.ds.commdity.service.OrderService;
import com.bhu.vas.business.ds.user.service.UserWalletLogService;
import com.bhu.vas.business.search.BusinessIndexDefine;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.FileHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;

@Service
public class ConsoleServiceHandler {
	private final Logger logger = LoggerFactory.getLogger(AsyncMsgHandleService.class);
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
	@Resource
	private OrderService orderService;
	
	@Resource
	private UserWalletLogService userWalletLogService;

//	public static final String WifiDeviceExport = "WifiDeviceExport";
//	public static final String OrderExport = "OrderExport";
	
	public static final String[] SearchWifiDeviceResultExportColumns = new String[]{"Mac","SN","软件版本","模块版本",
		"归属业务线","状态","是否绑定","绑定手机号","地理位置","在线总时长","首次上线时间",
		"末次上线时间","末次离线时间","灰度","关联模板","工作模式","在线总时长占比"};
	
	public static final String[] SearchOrderResultExportColumns = new String[]{"订单号","Mac","UMac","终端类型","打赏金额",
		"分成金额","打赏方式","打赏日期"};
	
	
	public void exportWifiDeviceFile(String jsonmessage){
		DeviceSearchResultExportFileDTO dto = JsonHelper.getDTO(jsonmessage, DeviceSearchResultExportFileDTO.class);
		if(dto == null || StringUtils.isEmpty(dto.getMessage())) return;
		
		String exportFilePath = dto.getExportFilePath();
		final List<String> lines = new ArrayList<String>();
		
		wifiDeviceDataSearchService.iteratorAll(BusinessIndexDefine.WifiDevice.IndexNameNew, 
				BusinessIndexDefine.WifiDevice.Type, dto.getMessage(), new IteratorNotify<Page<WifiDeviceDocument>>(){
			@Override
			public void notifyComming(Page<WifiDeviceDocument> pages) {
				for(WifiDeviceDocument doc : pages){
					lines.add(outputWifiDeviceStringByItem(doc));
				}
			}
		});
		
		searchResultExportFile(dto.getMessage(), exportFilePath, SearchWifiDeviceResultExportColumns, lines);
	}
	
	public void exportOrderFile(String jsonmessage){
		final OrderSearchResultExportFileDTO dto = JsonHelper.getDTO(jsonmessage, OrderSearchResultExportFileDTO.class);
		if(dto == null || StringUtils.isEmpty(dto.getMessage())) return;
		
		String exportFilePath = dto.getExportFilePath();
		final List<String> lines = new ArrayList<String>();
		
		String message = dto.getMessage();
		if(message.startsWith(StringHelper.LEFT_BRACE_STRING)){
			wifiDeviceDataSearchService.iteratorAll(BusinessIndexDefine.WifiDevice.IndexNameNew, 
					BusinessIndexDefine.WifiDevice.Type, message, new IteratorNotify<Page<WifiDeviceDocument>>(){
				@Override
				public void notifyComming(Page<WifiDeviceDocument> pages) {
					for(WifiDeviceDocument doc : pages){
						//logger.info("pages item:"+doc);
						lines.addAll(outputOrderStringByItem(doc.getId(), dto.getStart_date(), dto.getEnd_date()));
					}
				}
			});
		}else{
			String[] macs_array = message.split(StringHelper.COMMA_STRING_GAP);
			for(String mac : macs_array){
				lines.addAll(outputOrderStringByItem(mac, dto.getStart_date(), dto.getEnd_date()));
			}
		}
		
		searchResultExportFile(message, exportFilePath, SearchOrderResultExportColumns, lines);

	}
	
	/**
	 * 搜索结果导出txt文件
	 * @param message
	 */
	public void searchResultExportFile(String message, String export_filepath,
			String[] columns, List<String> lines) {
		logger.info(String.format("ConsoleServiceHandler searchResultExportFile message[%s]", message));
//		DeviceSearchResultExportFileDTO dto = JsonHelper.getDTO(message, DeviceSearchResultExportFileDTO.class);
//		if(dto == null) return;
		
//		String exportFilePath = BusinessRuntimeConfiguration.Search_Result_Export_Dir.concat(String.valueOf(dto.getUid()))
//				.concat(File.separator).concat(dto.getExportFileName());
		BufferedWriter fw = null;
		try {
			FileHelper.makeDirectory(export_filepath);
			File file = new File(export_filepath);
			fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8")); // 指定编码格式，以免读取时中文字符异常
			
			//输出文件
			//System.out.println(ret);
			//输出列
			int columns_length = columns.length;
			for(int i = 0;i<columns_length;i++){
				if((i+1) == columns_length){
					fw.append(formatStr(columns[i], false));
				}else{
					fw.append(formatStr(columns[i]));
				}
			}
//			for(String columns : SearchResultExportColumns){
//				fw.append(formatStr(columns));
//			}
			fw.newLine();
			//logger.info("lines:"+lines);
			for(String item : lines){
				fw.append(item);
				fw.newLine();
			}
			fw.flush(); // 全部写入缓存中的内容
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(String.format("ConsoleServiceHandler searchResultExportFile exception message[%s]", message), e);
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		logger.info(String.format("ConsoleServiceHandler searchResultExportFile successful message[%s]", message));
	}
	
	/**
	 * 输出一条设备数据的记录
	 * @param doc
	 * @throws IOException
	 */
	private String outputWifiDeviceStringByItem(WifiDeviceDocument doc){
		StringBuffer bw = new StringBuffer();
		bw.append(formatStr(doc.getD_mac()));
		bw.append(formatStr(doc.getD_sn()));
		bw.append(formatStr(doc.getD_origswver()));
		bw.append(formatStr(doc.getD_origvapmodule()));
		DeviceVersion parser = DeviceVersion.parser(doc.getD_origswver());
		bw.append(formatStr(parser.getDut()));
		OnlineEnum onlineEnum = WifiDeviceDocumentEnumType.OnlineEnum.getOnlineEnumFromType(doc.getD_online());
		if(onlineEnum != null){
			bw.append(formatStr(onlineEnum.getName()));
		}else{
			bw.append(formatStr(null));
		}
		UBindedEnum ubindedEnum = WifiDeviceDocumentEnumType.UBindedEnum.getUBindedEnumFromType(doc.getU_binded());
		if(ubindedEnum != null){
			bw.append(formatStr(ubindedEnum.getName()));
		}else{
			bw.append(formatStr(null));
		}
		bw.append(formatStr(doc.getU_mno()));
		bw.append(formatStr(doc.getD_address()));
		bw.append(formatStr(DateTimeHelper.getTimeDiff(StringUtils.isEmpty(doc.getD_uptime()) ? 0 : Long.parseLong(doc.getD_uptime()))));
		bw.append(formatStr(DateTimeHelper.formatDate(new Date(doc.getD_createdat()), DateTimeHelper.FormatPattern0)));
		bw.append(formatStr(DateTimeHelper.formatDate(new Date(doc.getD_lastregedat()), DateTimeHelper.FormatPattern0)));
		bw.append(formatStr(DateTimeHelper.formatDate(new Date(doc.getD_lastlogoutat()), DateTimeHelper.FormatPattern0)));
		
		if(!StringUtils.isEmpty(doc.getO_graylevel())){
			int o_graylevel_int = 0;
			try{
				o_graylevel_int = Integer.parseInt(doc.getO_graylevel());
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			GrayLevel gl = VapEnumType.GrayLevel.fromIndex(o_graylevel_int);
			if(gl != null){
				bw.append(formatStr(gl.getName()));
			}
		}else{
			bw.append(formatStr(null));
		}
		bw.append(formatStr(doc.getO_template()));
		bw.append(formatStr(doc.getD_workmodel()));
		//计算在线时长百分比
		long current_ts = System.currentTimeMillis();
		long theory_uptime = current_ts - doc.getD_createdat();
		long actual_uptime = 0l;
		if(!StringUtils.isEmpty(doc.getD_uptime())){
			actual_uptime = Long.parseLong(doc.getD_uptime());
		}
		//如果设备当前在线，上线时长加入当前在线时间
		if(WifiDeviceDocumentEnumType.OnlineEnum.Online.getType().equals(doc.getD_online())){
			long current_actual_uptime = current_ts - doc.getD_lastregedat();
			actual_uptime = actual_uptime + current_actual_uptime;
		}
		bw.append(formatStr(ArithHelper.percent(actual_uptime, theory_uptime, 2), false));
		return bw.toString();
	}
	
	/**
	 * 输出一条设备的打赏数据的记录
	 * @param doc
	 * @throws IOException
	 */
	private List<String> outputOrderStringByItem(String mac, String start_date, String end_date){
		List<String> mac_orderlines = new ArrayList<String>();
		
		ModelCriteria mc = new ModelCriteria();
		Criteria criteria = mc.createCriteria();
		criteria.andColumnEqualTo("mac", mac)
				.andColumnEqualTo("status", OrderStatus.DeliverCompleted.getKey());
		if(StringUtils.isNotEmpty(start_date)){
			criteria.andColumnGreaterThanOrEqualTo("created_at", start_date);
		}
		if(StringUtils.isNotEmpty(end_date)){
			criteria.andColumnLessThanOrEqualTo("created_at", end_date);
		}
    	mc.setPageNumber(1);
    	mc.setPageSize(200);
    	mc.setOrderByClause("created_at desc");
		EntityIterator<String, Order> it = new KeyBasedEntityBatchIterator<String,Order>(String.class
				,Order.class, orderService.getEntityDao(), mc);
		while(it.hasNext()){
			List<Order> orders = it.next();
			List<String> orderids = new ArrayList<String>();
			for(Order order : orders){
				orderids.add(order.getId());
			}
			//logger.info("orderids:"+orderids);
			//List<Order> orders = it.next();
			List<UserWalletLog> userWalletLogs = userWalletLogService.findModelByOrderids(orderids);
			//logger.info("userWalletLogs:"+userWalletLogs.size());
			for(Order order : orders){
				UserWalletLog userWalletLog = containOrderidByList(order.getId(), userWalletLogs);
				if(userWalletLog != null){
					//logger.info("userWalletLogs != null:"+userWalletLog.getOrderid());
					StringBuffer bw = new StringBuffer();
					bw.append(formatStr(order.getId()));
					bw.append(formatStr(order.getMac()));
					bw.append(formatStr(order.getUmac()));
					OrderUmacType orderUmacType = OrderUmacType.fromKey(order.getUmactype());
					if(orderUmacType == null){
						orderUmacType = OrderUmacType.Unknown;
					}
					bw.append(formatStr(orderUmacType.getDesc()));
					bw.append(formatStr(order.getAmount()));
					bw.append(formatStr(userWalletLog.getCash().substring(1)));
					OrderPaymentType orderPaymentType = OrderPaymentType.fromKey(order.getPayment_type());
					if(orderPaymentType == null){
						orderPaymentType = OrderPaymentType.Unknown;
					}
					bw.append(formatStr(orderPaymentType.getDesc()));
					bw.append(formatStr(DateTimeHelper.formatDate(order.getCreated_at(), DateTimeHelper.FormatPattern0)));
					mac_orderlines.add(bw.toString());
				}
			}
		}
		return mac_orderlines;
	}
	
	public static final int TxtFileLength = 20;
	/**
	 * 格式化txt文本值，保证列间距
	 * @param str
	 * @return
	 */
	private static String formatStr(String str, boolean split) {
		if(str == null) str = StringHelper.EMPTY_STRING_GAP;
		
		StringBuffer formatStr = new StringBuffer();
		formatStr.append(StringHelper.WHITESPACE_STRING_GAP);
		formatStr.append(str);
		int strLen = formatStr.toString().getBytes().length;
		if (strLen < TxtFileLength) {
			int temp = TxtFileLength - strLen;
			for (int i = 0; i < temp; i++) {
				//str += StringHelper.WHITESPACE_STRING_GAP;
				formatStr.append(StringHelper.WHITESPACE_STRING_GAP);
			}
		}
		if(split)
			formatStr.append(StringHelper.COMMA_STRING_GAP);
		return formatStr.toString();
	}
	
	private static String formatStr(String str) {
		return formatStr(str, true);
	}
	
	private static UserWalletLog containOrderidByList(String orderid, List<UserWalletLog> userWalletLogList){
		if(userWalletLogList == null || userWalletLogList.isEmpty()) return null;
		if(StringUtils.isEmpty(orderid)) return null;
		
		for(UserWalletLog userWalletLog : userWalletLogList){
			if(WifiDeviceSharedealConfigs.Default_Manufacturer == userWalletLog.getUid()){
				continue;
			}
			if(orderid.equals(userWalletLog.getOrderid())){
				return userWalletLog;
			}
		}
		return null;
	}
}
