package com.bhu.vas.di.op.dict;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.rpc.dict.model.DictMacPrefix;
import com.bhu.vas.business.ds.dict.service.DictMacPrefixService;
import com.smartwork.msip.cores.helper.StringHelper;

public class BuilderMacOrganizationDataOp {
	private static final String HexLineGap = "(hex)";
	private static final String Base16LineGap = "(base 16)";
	public static void main(String[] argv) {//throws ElasticsearchException, ESException, IOException, ParseException{
		//if(argv.length <1) return;
		//String oper = argv[0];// ADD REMOVE
		
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		DictMacPrefixService dictMacPrefixService = (DictMacPrefixService)ctx.getBean("dictMacPrefixService");
		InputStream is = null;//CmbtParserFilterHelper.class.getResourceAsStream("/ETData/index/dic/badgedic/badges.dic");
		try {
			is = new FileInputStream("/BHUData/data/dict/mac/oui.txt");
		} catch (FileNotFoundException ex) {
			System.err.println("file not found:"+ex.getMessage());
		}
		int gapLen = Base16LineGap.length();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            String line;
            int total = 0;
            List<String> fragment = new ArrayList<String>();
            while ((line = br.readLine()) != null) {
            	if(StringHelper.EMPTY_STRING_GAP.equals(line.trim())){
            		continue;
            	}
            	int index = line.indexOf(HexLineGap);
            	if(fragment.isEmpty()){
            		if(index < 0)
            			continue;
            		else
            			fragment.add(line);
            	}else{
            		if(index>=0){
            			//入库操作
            			//System.out.println(fragment.size());
            			{
            				int size = fragment.size();
            				String baseLine = fragment.get(1);
            				int bindex = baseLine.indexOf(Base16LineGap);
                    		if(bindex >= 0){
                    			String prefixmac = baseLine.substring(0,index).trim();
                    			String org = baseLine.substring(index+gapLen).trim();
                    			if(StringHelper.EMPTY_STRING_GAP.equals(org)){
                    				org = "PRIVATE";
                    			}
                    			//System.out.println(org);
                    			String name = chopFirstWord(org);
                    			StringBuilder address = new StringBuilder();
                    			if (size>2) {
									for(int i=2;i<size;i++){
										address.append(fragment.get(i).trim()).append("\n");
									}
								}
                    			DictMacPrefix macPrefix = dictMacPrefixService.getById(prefixmac);
                    			if(macPrefix != null){
                    				System.out.println(prefixmac+" already exist!");
                    			}else{
                    				macPrefix = new DictMacPrefix();
                    				macPrefix.setId(prefixmac);
                    				macPrefix.setName(name);
                    				macPrefix.setOrg(org);
                    				if(org.contains("Yulong Computer") || org.contains("Shenzhen Siviton")){
                    					macPrefix.setName("Coolpad");
                    				}
                    				if(org.contains("Hangzhou H3C")){
                    					macPrefix.setName("HUAWEI");
                    				}
                    				if(org.contains("Shenzhen TCL")){
                    					macPrefix.setName("TCL");
                    				}
                    				if(org.contains("Beijing Huasun")){
                    					macPrefix.setName("Huasun");
                    				}
                    				if(org.contains("Jiangsu Datang")){
                    					macPrefix.setName("Datang");
                    				}
                    				 
                    				if(org.contains("SHENZHEN MERCURY")){
                    					macPrefix.setName("Mercury");
                    				}
                    				if(org.contains("Tp-Link") || org.contains("TP-LINK")){
                    					macPrefix.setName("Tp-Link");
                    				}
                    				
                    				if(org.contains("N.V. PHILIPS")){
                    					macPrefix.setName("PHILIPS");
                    				}
                    				if(org.contains("SHENZHEN HUAPU") || org.contains("Shenzhen Huapu")){
                    					macPrefix.setName("HUAPU");
                    				}
                    				if(org.contains("Hisense") || org.contains("HISENSE")){
                    					macPrefix.setName("Hisense");
                    				}
                    				if(org.contains("EDISA HEWLETT PACKARD")){
                    					macPrefix.setName("Hewlett-Packard");
                    				}
                    				
                    				macPrefix.setAddress(address.toString());
                    				macPrefix.setVisibility(true);
                    				dictMacPrefixService.insert(macPrefix);
                    			}
                    			//System.out.println(String.format("Mac:[%s] Org:[%s]", prefixMac.trim(),org.trim()));
                    		}
            				/*if(fragment.size()<=2){
            					System.out.println(fragment.size());
            				}*/
            			}
            			fragment.clear();
            			total++;
            		}/*else{
            			
            		}*/
            		fragment.add(line);
            	}
            	
            	/*if(StringHelper.EMPTY_STRING_GAP.equals(line.trim()) && fragment.isEmpty()){
            		continue;
            	}*/
            	
            	/*if(StringHelper.EMPTY_STRING_GAP.equals(line.trim())){
            		int size = fragment.size();
            		if(size >0 && size<4)
            			System.out.println("~~~size:"+size+" :"+fragment.get(0));
            		
            		fragment.clear();
            		continue;
            	}
            	fragment.add(line);*/
            	/*try{
            		int index = line.indexOf(LineGap);
            		if(index >= 0){
            			String prefixMac = line.substring(0,index).trim();
            			String org = line.substring(index+gapLen).trim();
            			//System.out.println(String.format("Mac:[%s] Org:[%s]", prefixMac.trim(),org.trim()));
            			nodeManager.insertWord(prefixMac,org);
            			total++;
            		}
            	}catch(Exception ex){
            		ex.printStackTrace();
            		System.out.println(line);
            	}*/
            }
            System.out.println("~~~~~~~~~:"+total);
            br.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(is!=null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		/*if("ADD".equals(oper)){
			if(argv.length < 5) return;
			int id = Integer.parseInt(argv[1]);
			String acc = argv[2];
			String pwd = argv[3];
			String nick = argv[4];
			Integer uid = UniqueFacadeService.fetchUidByMobileno(86,acc);
			if(uid != null){
				User byId = userService.getById(uid);
				if(byId != null){
					System.out.println(String.format("acc[%s] 已经被注册，请换个号码！", acc));
					return;
				}else{
					UniqueFacadeService.removeByMobileno(86, acc);
					System.out.println(String.format("acc[%s] 记录被移除！", acc));
					return;
				}
			}
			User user = new User();
			user.setId(id);
			user.setCountrycode(86);
			user.setMobileno(acc);
			user.setPlainpwd(pwd);
			user.setRegip("127.0.0.1");
			user.setNick(nick);
			//标记用户注册时使用的设备，缺省为DeviceEnum.Android
			user.setRegdevice(DeviceEnum.PC.getSname());
			//标记用户最后登录设备，缺省为DeviceEnum.PC
			user.setLastlogindevice(DeviceEnum.PC.getSname());
			user = userService.insert(user);
			//user.setId(id);
			System.out.println("uid:"+user.getId());
			UniqueFacadeService.uniqueRegister(user.getId(), user.getCountrycode(), user.getMobileno());
			// token validate code
			UserToken uToken = userTokenService.generateUserAccessToken(user.getId().intValue(), true, true);
			{//write header to response header
				//BusinessWebHelper.setCustomizeHeader(response, uToken);
				IegalTokenHashService.getInstance().userTokenRegister(user.getId().intValue(), uToken.getAccess_token());
			}
			System.out.println(String.format("userReg[id:%s mobileno:%s nick:%s] successfully!", user.getId(),user.getMobileno(),user.getNick()));
		}else{
			System.out.println(String.format("unimplements"));
		}*/
		
	}
	
	private static String chopFirstWord(String line){
		int len = line.length();
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<len;i++){
			char c = line.charAt(i);
			if(c == ' ' || c == ','){
				break;
			}else{
				sb.append(c);
			}
		}
		return sb.toString();
	}
}
