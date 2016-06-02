package com.bhu.statistics.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;


public class CSVUtil {
	
	private final static byte commonCsvHead[] = { (byte) 0xEF, (byte) 0xBB,(byte) 0xBF };
	
	@SuppressWarnings("rawtypes")
	  public static File createCSVFile(List exportData, LinkedHashMap map, String outPutPath,String fileName) {
	    File csvFile = null;
	    BufferedWriter csvFileOutputStream = null;
	    try {
	      File file = new File(outPutPath);
	      if (!file.exists()) {
	        file.mkdir();
	      }
	      //定义文件名格式并创建
	      csvFile = File.createTempFile(fileName, ".csv", new File(outPutPath));
	      System.out.println("csvFile：" + csvFile);
	      // UTF-8使正确读取分隔符"," 
	      csvFileOutputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile),"GB2312"),1024);
	      System.out.println("csvFileOutputStream：" + csvFileOutputStream);
	      // 写入文件头部 
	      for (Iterator propertyIterator = map.entrySet().iterator(); propertyIterator.hasNext();) {
	        java.util.Map.Entry propertyEntry = (java.util.Map.Entry) propertyIterator.next();
	        csvFileOutputStream.write(""+ (String) propertyEntry.getValue() != null ? (String) propertyEntry.getValue() : "" + "");
	        if (propertyIterator.hasNext()) {
	          csvFileOutputStream.write(",");
	        }
	      }
	      csvFileOutputStream.newLine();
	      // 写入文件内容 
	      for (Iterator iterator = exportData.iterator(); iterator.hasNext();) {
	        Object row = (Object) iterator.next();
	        for (Iterator propertyIterator = map.entrySet().iterator(); propertyIterator
	          .hasNext();) {
	          java.util.Map.Entry propertyEntry = (java.util.Map.Entry) propertyIterator
	            .next();
	          csvFileOutputStream.write((String) BeanUtils.getProperty(row,
	            (String) propertyEntry.getKey()));
	          if (propertyIterator.hasNext()) {
	            csvFileOutputStream.write(",");
	          }
	        }
	        if (iterator.hasNext()) {
	          csvFileOutputStream.newLine();
	        }
	      }
	      csvFileOutputStream.flush();
	    } catch (Exception e) {
	      e.printStackTrace();
	    } finally {
	      try {
	        csvFileOutputStream.close();
	      } catch (IOException e) {
	        e.printStackTrace();
	      }
	    }
	    return csvFile;
	  }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String args[]){
		List exportData = new ArrayList<Map>();
	    Map row1 = new LinkedHashMap<String, String>();
	    row1.put("1", "设备总数");
	    row1.put("2", "12");
	    row1.put("3", "13");
	    row1.put("4", "14");
	    row1.put("5", "24");
	    row1.put("6", "24");
	    row1.put("7", "24");
	    row1.put("8", "24");
	    row1.put("9", "24");
	    row1.put("10", "24");
	    row1.put("11", "24");
	    row1.put("12", "24");
	    row1.put("13", "24");
	    row1.put("14", "24");
	    row1.put("15", "24");
	    exportData.add(row1);
	    row1 = new LinkedHashMap<String, String>();
	    row1.put("1", "设备总数");
	    row1.put("2", "22");
	    row1.put("3", "23");
	    row1.put("4", "24");
	    row1.put("5", "24");
	    row1.put("6", "24");
	    row1.put("7", "24");
	    row1.put("8", "24");
	    row1.put("9", "24");
	    row1.put("10", "24");
	    row1.put("11", "24");
	    row1.put("12", "24");
	    row1.put("13", "24");
	    row1.put("14", "24");
	    row1.put("15", "24");
	    exportData.add(row1);
	    LinkedHashMap map = new LinkedHashMap();
	    map.put("1", "2016-05-19 - 2016-05-21  总收益：1000.0\n");
	    map.put("2", "SSID关联总次数");
	    map.put("3", "SSID连接总人数");
	    map.put("4", "在线/总数");
	    map.put("5", "单台订单(个)");
	    map.put("6", "单台收益(元)\n \n");
	    map.put("7", "(打赏认证页)UV");
	    map.put("8", "(赏字按钮)点击数");
	    map.put("9", "(赏字按钮)人均点击次数");
	    map.put("10", "(订单创建)订单数");
	    map.put("11", "(订单创建)赏转化率");
	    map.put("12", "(订单创建)订单创建率");
	    map.put("13", "(打赏成功)打赏成功数");
	    map.put("14", "(打赏成功)金额合计");
	    map.put("15", "(打赏成功)订单成功率");
	    String path = "E:/export/";
	    String fileName = "文件导出";
	    File file = createCSVFile(exportData, map, path, fileName);
	    String fileName2 = file.getName();
	    System.out.println("文件名称：" + fileName2);
	}
}
