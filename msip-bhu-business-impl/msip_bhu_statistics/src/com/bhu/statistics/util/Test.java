package com.bhu.statistics.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.expression.spel.ast.OpNE;

import com.bhu.statistics.logic.IUMLogic;
import com.bhu.statistics.logic.Impl.UMLogicImpl;
import com.bhu.statistics.util.um.OpenApiCnzzImpl;

public class Test {
	
	public static void readZipFile(String file) throws Exception {  
        ZipFile zf = new ZipFile(file);  
        InputStream in = new BufferedInputStream(new FileInputStream(file)); 
        ZipInputStream zin = new ZipInputStream(in);  
        ZipEntry ze;  
        while ((ze = zin.getNextEntry()) != null) {  
            if (ze.isDirectory()) {
            } else {  
                System.err.println("file - " + ze.getName() + " : "  
                        + ze.getSize() + " bytes");  
                long size = ze.getSize();  
                if (size > 0) {  
                    BufferedReader br = new BufferedReader(  
                            new InputStreamReader(zf.getInputStream(ze)));  
                    String line;  
                    while ((line = br.readLine()) != null) {
                    	if(StringUtils.contains(line, "HO{")){
                    		//System.out.println(line);
                    		String[] array = line.split(" - ");
                    		for (int i = 0; i < array.length; i++) {
								System.out.println(array[1]);
								String s = array[1].substring(2);
								//String str = "{\"mac\":\"84:82:f4:19:de:eb\",\"ts\":1445070529791,\"sm\":false,\"act\":\"DO\"}";
								JSONObject obj = new JSONObject(s);
								System.out.println(obj.getString("mac"));
							}
                    	}
                    }  
                    br.close();  
                }  
                System.out.println();  
            }  
        }  
        zin.closeEntry();  
    } 
	
	public static void main(String[] args) {
		//查询后的数据集合,该对象同样用户截取后的数据集合
		List obj = new ArrayList();
		//数据总数
		int totalCount = 155;
		//总的页数
		int pageCount = 0;
		//每页显示的总数
		int endNum = 20;
		//当前页码
		int startNum = 1;
		/*计算出总共能分成多少页*/
		if (totalCount % endNum > 0)      //数据总数和每页显示的总数不能整除的情况
		{
		pageCount = totalCount / endNum + 1;
		}
		else   //数据总数和每页显示的总数能整除的情况
		{
		pageCount = totalCount / endNum;
		}
		if(totalCount > 0)
		{
		if(startNum <= pageCount)
		{
		if(startNum == 1)     //当前页数为第一页
		{
		if(totalCount <= endNum)  //数据总数小于每页显示的数据条数
		{
		//截止到总的数据条数(当前数据不足一页，按一页显示)，这样才不会出现数组越界异常
		obj = obj.subList(0, totalCount);
		}
		else
		{
		obj = obj.subList(0, endNum);
		}
		}
		else
		{
		//截取起始下标
		int fromIndex = (startNum - 1) * endNum;
		//截取截止下标
		int toIndex = startNum * endNum;
		/*计算截取截止下标*/
		if ((totalCount - toIndex) % endNum >= 0)
		{
		toIndex = startNum * endNum;
		}
		else
		{
		toIndex = (startNum - 1) * endNum + (totalCount % endNum);
		}
		if (totalCount >= toIndex)
		{
		obj = obj.subList(fromIndex, toIndex);
		}
		} 
		}
		else
		{
		obj = null;
		}
		}
	}
}
