package com.bhu.vas.api.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.model.UserDevicesSharedNetworks;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;

public class SharedNetworksHelper {
	public static final String FormatTemplete = "%04d";
    //如果为DefaultCreateTemplate 则代表新建一个模板
    public static final String DefaultCreateTemplate = "0000";
    public static final String DefaultTemplate = "0001";
    public static final String DefaultSafeSecureName = "默认模板";
    
    
    private static final List<String> TemplateSequences = new ArrayList<>();
    static{
    	for(int i=1;i<BusinessRuntimeConfiguration.SharedNetworksTemplateMaxLimit+1;i++){
    		TemplateSequences.add(String.format(FormatTemplete, i));
    	}
    }
    
	public static String buildTemplateName(SharedNetworkType sharedNetwork,String template){
		StringBuilder sb = new StringBuilder();
		if(SharedNetworkType.SafeSecure == sharedNetwork){
			if(DefaultTemplate.equals(template)){
				sb.append(DefaultSafeSecureName);
			}else{
				sb.append(sharedNetwork.getName()).append(template);
			}
		}else{//uplink
			sb.append(sharedNetwork.getName()).append(template);
		}
		return sb.toString();
	}
	
	public static boolean validDefaultCreateTemplateFormat(String template){
    	return DefaultCreateTemplate.equals(template);
    }
    
    public static boolean validTemplateFormat(String template){
    	if(StringUtils.isEmpty(template)) return false;
    	if(template.length() != 4) return false;
    	try{
    		Integer.parseInt(template);
    	}catch(NumberFormatException ex){
    		return false;
    	}
    	return true;
    }
    
	@SuppressWarnings("unchecked")
	public static String fetchValidTemplate(List<ParamSharedNetworkDTO> models_fromdb){
		Set<String> templates_fromdb = null;
		Collection<String> subtract = null;
		List<String> tmp = null;
		try{
			if(!models_fromdb.isEmpty()){
				templates_fromdb = new HashSet<String>();
				for(ParamSharedNetworkDTO dto:models_fromdb){
					templates_fromdb.add(dto.getTemplate());
				}
				subtract = CollectionUtils.subtract(TemplateSequences, templates_fromdb);
				System.out.println("valid templates:"+subtract);
				if(!subtract.isEmpty()){
					tmp = new ArrayList<String>(subtract);
					return tmp.get(0);
				}
			}else{
				return DefaultTemplate;
			}
		}finally{
			if(templates_fromdb != null){
				templates_fromdb.clear();
				templates_fromdb = null;
			}
			if(tmp != null){
				tmp.clear();
				tmp = null;
			}
			if(subtract != null){
				subtract.clear();
				subtract = null;
			}
		}
		return null;
	}
	
	public static UserDevicesSharedNetworks buildDefaultUserDevicesSharedNetworks(int uid,ParamSharedNetworkDTO paramDto){
		UserDevicesSharedNetworks configs = new UserDevicesSharedNetworks();
		configs.setId(uid);
		if(StringUtils.isEmpty(paramDto.getTemplate_name())){
			SharedNetworkType sharedNetwork = VapEnumType.SharedNetworkType.fromKey(paramDto.getNtype());
			paramDto.setTemplate_name(buildTemplateName(sharedNetwork,paramDto.getTemplate()));//sharedNetwork.getName().concat(paramDto.getTemplate()));
		}
		paramDto.setTs(System.currentTimeMillis());
		List<ParamSharedNetworkDTO> sharedNetworkType_models = new ArrayList<ParamSharedNetworkDTO>();
		sharedNetworkType_models.add(paramDto);
		configs.put(paramDto.getNtype(), sharedNetworkType_models);
		return configs;
	}
	
	public static UserDevicesSharedNetworks buildDefaultUserDevicesSharedNetworks(int uid,VapEnumType.SharedNetworkType sharedNetwork,String template){
		UserDevicesSharedNetworks configs = new UserDevicesSharedNetworks();
		configs.setId(uid);
		List<ParamSharedNetworkDTO> sharedNetworkType_models = new ArrayList<ParamSharedNetworkDTO>();
		ParamSharedNetworkDTO dto = ParamSharedNetworkDTO.builderDefault(sharedNetwork.getKey());
		dto.setTs(System.currentTimeMillis());
		dto.setTemplate(template);
		dto.setTemplate_name(buildTemplateName(sharedNetwork,template));//sharedNetwork.getName().concat(template));
		sharedNetworkType_models.add(dto);
		configs.put(sharedNetwork.getKey(), sharedNetworkType_models);
		return configs;
	}
}
