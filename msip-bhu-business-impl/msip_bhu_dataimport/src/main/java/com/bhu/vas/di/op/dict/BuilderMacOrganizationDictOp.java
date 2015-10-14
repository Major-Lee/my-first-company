package com.bhu.vas.di.op.dict;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.rpc.dict.model.DictMacPrefix;
import com.bhu.vas.business.ds.dict.service.DictMacPrefixService;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

public class BuilderMacOrganizationDictOp {
	private static Map<String,String> macPrefixMapping = new java.util.concurrent.ConcurrentHashMap<String, String>();
	public static void main(String[] argv) {//throws ElasticsearchException, ESException, IOException, ParseException{
		//if(argv.length <1) return;
		//String oper = argv[0];// ADD REMOVE
		
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		DictMacPrefixService dictMacPrefixService = (DictMacPrefixService)ctx.getBean("dictMacPrefixService");
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1 ");
		mc.setOrderByClause("id asc");
    	mc.setPageNumber(1);
    	mc.setPageSize(500);

    	EntityIterator<String, DictMacPrefix> itit = new KeyBasedEntityBatchIterator<String, DictMacPrefix>(String.class, DictMacPrefix.class, dictMacPrefixService.getEntityDao(), mc);
		//EntityBatchIterator<Integer, User> itit = new KeyBasedEntityBatchIterator<Integer, User>(Integer.class, User.class, userService.getEntityDao(), mc);
		while(itit.hasNext()){
			List<DictMacPrefix> list = itit.next();
			for(DictMacPrefix macPrefix:list){
				
				String name = macPrefix.getName();
				String org = macPrefix.getOrg();
				if(StringUtils.isNotEmpty(org)){
					if(org.contains("Yulong Computer") || org.contains("Shenzhen Siviton")){
						name = ("Coolpad");
					}
					if(org.contains("Hangzhou H3C")){
						name = ("HUAWEI");
					}
					if(org.contains("Shenzhen TCL")){
						name = ("TCL");
					}
					if(org.contains("Beijing Huasun")){
						name = ("Huasun");
					}
					if(org.contains("Jiangsu Datang")){
						name = ("Datang");
					}
					if(org.contains("Beijing Xiaomi")){
						name = ("Xiaomi");
					} 
					if(org.contains("SHENZHEN MERCURY")){
						name = ("Mercury");
					}
					if(org.contains("Tp-Link") || org.contains("TP-LINK")){
						name = ("Tp-Link");
					}
					if(org.contains("N.V. PHILIPS")){
						name = ("PHILIPS");
					}
					if(org.contains("SHENZHEN HUAPU") || org.contains("Shenzhen Huapu")){
						name = ("HUAPU");
					}
					if(org.contains("Hisense") || org.contains("HISENSE")){
						name = ("Hisense");
					}
					if(org.contains("EDISA HEWLETT PACKARD")){
						name = ("Hewlett-Packard");
					}
					
					if(org.contains("OPPO MOBILE")){
						name = ("OPPO");
					}
				}
				macPrefixMapping.put(macPrefix.getId(), name);
			}
		}
		processDictGen();
	}
	public static void processDictGen(){
		String dictPath ="/BHUData/data/dict/mac/macprefix.dic";
		File file = new File(dictPath);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter  bw = null;
		
		try{
			fos=new FileOutputStream(new File(dictPath));
			osw=new OutputStreamWriter(fos, "UTF-8");
			bw=new BufferedWriter(osw);
			
			for (Map.Entry<String, String> entry : macPrefixMapping.entrySet()) {
				String key = entry.getKey();
				bw.write(StringHelper.replaceBlankAndLowercase(key).concat(StringHelper.EQUAL_STRING_GAP).concat(StringHelper.replaceEnterAndOtherLineChar(entry.getValue()))+"\n");
			}
		    bw.close();
		    osw.close();
		    fos.close();
		    
		}catch(IOException ex){
			ex.printStackTrace();
		}finally{
			try {
				if(bw != null)
					bw.close();
				if(osw != null)
					osw.close();
				if(fos != null)
					fos.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("dicpath:"+dictPath);
	}
}
