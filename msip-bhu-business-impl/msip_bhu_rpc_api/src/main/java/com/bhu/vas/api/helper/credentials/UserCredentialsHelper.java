package com.bhu.vas.api.helper.credentials;

import java.util.ArrayList;
import java.util.List;

import com.smartwork.msip.cores.helper.authorization.SafetyBitMarkHelper;

/**
 * 用户所属角色定义
 * 
 * @author Edmond
 *
 */
public class UserCredentialsHelper {
    
    public static boolean hasUserType(int source,UserRoleType ut){
    	if(ut == null) return false;
    	return SafetyBitMarkHelper.hasBitMark(source, ut.getIndex());
    }
    
    public static int addUserType(int source,UserRoleType ut){
    	if(ut == null) return source;
    	return SafetyBitMarkHelper.addBitMark(source, ut.getIndex());
    }
    
    public static int cancelUserType(int source,UserRoleType ut){
    	if(ut == null) return source;
    	return SafetyBitMarkHelper.cancelBitMark(source, ut.getIndex());
    }
    
    public static List<UserRoleType> containUserTypes(int source){
    	List<UserRoleType> result = new ArrayList<>();
    	UserRoleType[] types = UserRoleType.values();
    	for(UserRoleType type:types){
    		if(hasUserType(source,type))
    			result.add(type);
    	}
    	return result;
    }
    public static void main(String[] argv){
    	List<UserRoleType> result = containUserTypes(UserRoleType.SuperAdmin.getIndex());
    	for(UserRoleType type:result){
    		System.out.println(type.getFname());
    	}
    }
}
