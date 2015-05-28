package com.bhu.vas.api.rpc.devices.model;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.bhu.vas.api.rpc.sequence.helper.ISequenceGenable;
import com.smartwork.msip.cores.orm.model.extjson.ListJsonExtIntModel;
/*
 * wifi设备的组
 */
@SuppressWarnings("serial")
public class WifiDeviceGroup extends ListJsonExtIntModel<String> implements ISequenceGenable{
	private int pid;
	private String path;//树状结构path
	private String name;
	private boolean haschild;
	private int creator;
	private int updator;
	private Date created_at;
	
	public WifiDeviceGroup() {
		super();
	}
	public WifiDeviceGroup(Integer id,String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	
	public int getCreator() {
		return creator;
	}
	public void setCreator(int creator) {
		this.creator = creator;
	}
	public int getUpdator() {
		return updator;
	}
	public void setUpdator(int updator) {
		this.updator = updator;
	}
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
	
	public List<String> getWifiIds() {
        return this.getInnerModels();//.getInnerModels();//this.get(Authorities, new LinkedHashMap<Integer, String>(), true);
    }
	
	//static final String Authorities = "Authorities";
	
	/*public Set<String> getResources() {
        return this.getInnerModels();//this.get(Authorities, new LinkedHashMap<Integer, String>(), true);
    }
    public Set<String> getResourceNames() {
        Set<String> set =  PermissionDTOHelper.getDTOStringSetNames(this.getInnerModels());
        return set;
    }
    public Set<Integer> getResourceSetIds() {
        return  PermissionDTOHelper.getDTOIntSetIds(this.getInnerModels());
    }
    
    public String getResourceIds(){
    	Set<Integer> resids = PermissionDTOHelper.getDTOIntSetIds(this.getInnerModels());
    	if(resids.isEmpty()){
    		return "";
    	}else{
    		StringBuilder sb = new StringBuilder();
    		boolean first = true;
    		for(Integer resid:resids){
    			if(!first){
    				sb.append(StringHelper.COMMA_STRING_GAP);
    			}else{
    				first = false;
    			}
    			sb.append(resid);
    		}
    		return sb.toString();
    	}
    }*/
    public void setResources(Collection<String> set) {
    	this.replaceInnerModels(set);
        //this.put(Authorities, map);
    }
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	@Override
	public Class<String> getJsonParserModel() {
		return String.class;
	}
	@Override
	public void setSequenceKey(Integer key) {
		this.setId(key);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj instanceof WifiDeviceGroup){
			WifiDeviceGroup dto = (WifiDeviceGroup)obj;
			if(dto.getId().intValue() == this.id.intValue()) return true;
			else return false;
		}else return false;
	}
	@Override
	public int hashCode() {
		if(this.getId() == null) this.setId(new Integer(0));
		return this.getId().hashCode();
	}
	@Override
	public int limitSize() {
		return 100;
	}
	public boolean isHaschild() {
		return haschild;
	}
	public void setHaschild(boolean haschild) {
		this.haschild = haschild;
	}
	
}