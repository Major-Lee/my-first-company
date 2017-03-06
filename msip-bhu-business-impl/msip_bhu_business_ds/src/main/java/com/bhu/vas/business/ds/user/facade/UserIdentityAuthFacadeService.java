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
	
	public String fetchUserMobilenoByHdmac(String hdmac){
		 UserIdentityAuth auth = userIdentityAuthService.getById(hdmac);
		 String mobileno = null;
//		 if(auth !=null){
//			 mobileno = auth.getMobileno().substring(auth.getMobileno().indexOf(StringHelper.WHITESPACE_STRING_GAP)).trim();
//		 }
		 return mobileno;
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
