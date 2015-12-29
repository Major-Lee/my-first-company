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

import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.GrayLevel;
import com.bhu.vas.api.helper.WifiDeviceDocumentEnumType;
import com.bhu.vas.api.helper.WifiDeviceDocumentEnumType.OnlineEnum;
import com.bhu.vas.api.helper.WifiDeviceDocumentEnumType.UBindedEnum;
import com.bhu.vas.api.rpc.devices.dto.DeviceVersion;
import com.bhu.vas.business.asyn.spring.model.DeviceSearchResultExportFileDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.AsyncMsgHandleService;
import com.bhu.vas.business.search.BusinessIndexDefine;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.FileHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;

@Service
public class ConsoleServiceHandler {
	private final Logger logger = LoggerFactory.getLogger(AsyncMsgHandleService.class);
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;

	public static final String[] SearchResultExportColumns = new String[]{"Mac","SN","软件版本","模块版本",
		"归属业务线","状态","是否绑定","绑定手机号","地理位置","在线总时长","首次上线时间",
		"末次上线时间","末次离线时间","灰度","关联模板","工作模式"};
	/**
	 * 搜索结果导出txt文件
	 * @param message
	 */
	public void searchResultExportFile(String message) {
		logger.info(String.format("ConsoleServiceHandler searchResultExportFile message[%s]", message));
		DeviceSearchResultExportFileDTO dto = JsonHelper.getDTO(message, DeviceSearchResultExportFileDTO.class);
		if(dto == null) return;
		
		String exportFilePath = RuntimeConfiguration.Search_Result_Export_Dir.concat(dto.getExportFileName());
		BufferedWriter fw = null;
		try {
			FileHelper.makeDirectory(exportFilePath);
			File file = new File(exportFilePath);
			fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8")); // 指定编码格式，以免读取时中文字符异常

			final List<String> allItemStrings = new ArrayList<String>();
			
			wifiDeviceDataSearchService.iteratorAll(BusinessIndexDefine.WifiDevice.IndexNameNew, 
					BusinessIndexDefine.WifiDevice.Type, dto.getMessage(), new IteratorNotify<Page<WifiDeviceDocument>>(){
				@Override
				public void notifyComming(Page<WifiDeviceDocument> pages) {
					for(WifiDeviceDocument doc : pages){
						allItemStrings.add(outputStringByItem(doc));
					}
					//System.out.println(pages.getTotalElements());
				}
			});
			
			//输出文件
			//System.out.println(ret);
			//输出列
			for(String columns : SearchResultExportColumns){
				fw.append(formatStr(columns));
			}
			fw.newLine();
			for(String item : allItemStrings){
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
	private String outputStringByItem(WifiDeviceDocument doc){
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
		return bw.toString();
	}
	
	public static final int TxtFileLength = 20;
	/**
	 * 格式化txt文本值，保证列间距
	 * @param str
	 * @return
	 */
	private static String formatStr(String str) {
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
		formatStr.append(StringHelper.COMMA_STRING_GAP);
		return str;
	}
}
