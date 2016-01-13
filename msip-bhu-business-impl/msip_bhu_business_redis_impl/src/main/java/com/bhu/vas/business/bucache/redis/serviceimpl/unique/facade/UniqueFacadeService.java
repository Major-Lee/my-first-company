package com.bhu.vas.business.bucache.redis.serviceimpl.unique.facade;

import com.bhu.vas.business.bucache.redis.serviceimpl.unique.impl.mobleno.UniqueMobilenoHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.impl.nick.UniqueNickHashService;

public class UniqueFacadeService {
	//public static 
	
/*	public static Integer fetchUidByEmail(String email){
		String uidStr =  UniqueEmailHashService.getInstance().fetchUidByEmail(email);
		if(uidStr == null) return null;
		try{
			return new Integer(uidStr);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return null;
		}
	}
	
	
	public static boolean checkEmailExist(String email){
		try{
			return UniqueEmailHashService.getInstance().check(email);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return true;
		}
	}*/
	
	public static Integer fetchUidByNick(String nick){
		String uidStr =  UniqueNickHashService.getInstance().fetchUidByNick(nick);
		if(uidStr == null) return null;
		try{
			return new Integer(uidStr);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return null;
		}
	}
	public static boolean checkNickExist(String nick){
		try{
			return UniqueNickHashService.getInstance().check(nick);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return true;
		}
	}
	
	public static Integer fetchUidByMobileno(int countryCode,String mobileno){
		String uidStr =  UniqueMobilenoHashService.getInstance().fetchUidByMobileno(countryCode,mobileno);
		if(uidStr == null) return null;
		try{
			return new Integer(uidStr);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return null;
		}
	}
	public static Integer fetchUidByMobileno(String mobilenoWithCountryCode){
		String uidStr =  UniqueMobilenoHashService.getInstance().fetchUidByMobileno(mobilenoWithCountryCode);
		if(uidStr == null) return null;
		try{
			return new Integer(uidStr);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return null;
		}
	}
	
	public static boolean checkMobilenoExist(int countryCode,String mobileno){
		try{
			return UniqueMobilenoHashService.getInstance().check(countryCode,mobileno);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return true;
		}
	}
	
	public static boolean removeByMobileno(int countryCode,String mobileno){
		try{
			return UniqueMobilenoHashService.getInstance().remove(countryCode,mobileno);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return true;
		}
	}
	
	public static void uniqueMobilenoRegister(int uid,int countrycode,String mobileno){
		//System.out.println(String.format("uid[%s] countrycode[%s] mobileno[%s]", uid,countrycode, mobileno));
		UniqueMobilenoHashService.getInstance().registerOrUpdate(countrycode,mobileno, uid, null);
	}
	
	public static void uniqueMobilenoChanged(int uid,int countrycode,String mobileno,String oldmobileno){
		UniqueMobilenoHashService.getInstance().registerOrUpdate(countrycode,mobileno, uid,oldmobileno);
	}
	
	
	public static void uniqueNickRegister(int uid,String nick){
		//System.out.println(String.format("uid[%s] nick[%s]", uid,nick));
		UniqueNickHashService.getInstance().registerOrUpdate(nick, uid, null);
	}
	
	public static void uniqueNickChanged(int uid,String nick,String oldnick){
		//System.out.println(String.format("uid[%s] nick[%s] oldnick[%s]", uid,nick, oldnick));
		UniqueNickHashService.getInstance().registerOrUpdate(nick, uid,oldnick);
	}
	
	/*public static void uniqueRegister(int uid,String email,String pwd,String nick,String mobileno){
	System.out.println(String.format("uid[%s] email[%s] pwd[%s] nick[%s] mobileno[%s]", uid, email, pwd, nick, mobileno));
	UniqueEmailHashService.getInstance().registerOrUpdate(email, uid, pwd,null);
	UniqueNickHashService.getInstance().registerOrUpdate(nick, uid, null);
	UniqueMobilenoHashService.getInstance().registerOrUpdate(mobileno, uid, null);
	}
	
	public static void uniquePwdChanged(int uid,String email,String pwd){
		UniqueEmailHashService.getInstance().registerOrUpdate(email, uid, pwd,null);
	}
	
	public static void uniqueEmailChanged(int uid,String email,String pwd,String oldemail){
		UniqueEmailHashService.getInstance().registerOrUpdate(email, uid, pwd,oldemail);
	}*/	
	/*public static void uniqueNickUpdate(int uid,String nick,String oldnick,boolean isNickUpd){
		if(isNickUpd)
			UniqueNickHashService.getInstance().registerOrUpdate(nick, uid, oldnick);
	}*/
	
	
	
	/*public static void uniqueUpdate(int uid,String nick,String oldnick,String mobileno,String oldmobileno,boolean isNickUpd,boolean isMobilenoUpd){
		if(isNickUpd)
			UniqueNickHashService.getInstance().registerOrUpdate(nick, uid, oldnick);
		if(isMobilenoUpd)
			UniqueMobilenoHashService.getInstance().registerOrUpdate(mobileno, uid, oldmobileno);
		//UniqueEmailHashService.getInstance().registerOrUpdate(email, uid, pwd);
		//UniqueNickHashService.getInstance().registerOrUpdate(email, uid, pwd);
	}*/
	
	/*public static void register(int uid,String email,String pwd,String nick,String permallink,){
		UniqueEmailHashService.getInstance().registerOrUpdate(email, uid, pwd);
		UniqueNickHashService.getInstance().registerOrUpdate(email, uid, pwd);
	}*/
}
