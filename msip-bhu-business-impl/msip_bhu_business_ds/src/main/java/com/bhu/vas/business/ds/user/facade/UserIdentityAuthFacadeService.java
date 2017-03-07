package com.bhu.vas.business.ds.user.facade;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.user.model.UserIdentityAuth;
import com.bhu.vas.business.ds.user.service.UserIdentityAuthService;
import com.smartwork.msip.cores.helper.StringHelper;


@Service
public class UserIdentityAuthFacadeService {
	
	@Resource
	private UserIdentityAuthService userIdentityAuthService;
	
	public UserIdentityAuthService getUserIdentityAuthService(){
		return this.userIdentityAuthService;
	}
	
	public String fetchUserMobilenoByHdmac(String hdmac){
		 UserIdentityAuth auth = userIdentityAuthService.getById(hdmac);
		 String mobileno = null;
//		 if(auth !=null){
//			 mobileno = auth.getMobileno().substring(auth.getMobileno().indexOf(StringHelper.WHITESPACE_STRING_GAP)).trim();
//		 }
		 return mobileno;
	}
	
	public boolean matchHdmacAndUid(String hdmac,int uid){
		boolean isMatched = false;
		UserIdentityAuth auth = userIdentityAuthService.getById(hdmac);
		if(auth.getUid() == uid)
			isMatched = true;
		return isMatched;
	}
	
	public void updateLoginDevice(int uid, int countrycode, String acc, String mac){
		 UserIdentityAuth auth = userIdentityAuthService.getById(mac);
		 if(auth == null){
			 auth = new UserIdentityAuth();
			 auth.setId(mac);
			 auth.setUid(uid);
			 auth.setCountrycode(countrycode);
			 auth.setMobileno(acc);
			 userIdentityAuthService.insert(auth);
		 } else {
			 auth.setUid(uid);
			 auth.setCountrycode(countrycode);
			 auth.setMobileno(acc);
			 userIdentityAuthService.update(auth);
		 }
	}
}
