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
    
    public static boolean hasUserType(int source,UserType ut){
    	if(ut == null) return false;
    	return SafetyBitMarkHelper.hasBitMark(source, ut.getIndex());
    }
    
    public static int addUserType(int source,UserType ut){
    	if(ut == null) return source;
    	return SafetyBitMarkHelper.addBitMark(source, ut.getIndex());
    }
    
    public static int cancelUserType(int source,UserType ut){
    	if(ut == null) return source;
    	return SafetyBitMarkHelper.cancelBitMark(source, ut.getIndex());
    }
    
    public static List<UserType> containUserTypes(int source){
    	List<UserType> result = new ArrayList<>();
    	UserType[] types = UserType.values();
    	for(UserType type:types){
    		if(hasUserType(source,type))
    			result.add(type);
    	}
    	return result;
    }
    public static void main(String[] argv){
    	List<UserType> result = containUserTypes(UserType.SuperAdmin.getIndex());
    	for(UserType type:result){
    		System.out.println(type.getFname());
    	}
    }
}
