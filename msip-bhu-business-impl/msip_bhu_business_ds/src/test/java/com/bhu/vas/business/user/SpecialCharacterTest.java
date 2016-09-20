package com.bhu.vas.business.user;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.rpc.user.model.DeviceEnum;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.helper.EncodeHelper;
import com.smartwork.msip.cores.helper.encrypt.Base64Helper;
import com.smartwork.msip.localunit.BaseTest;
import com.smartwork.msip.localunit.RandomData;
import com.sun.org.apache.xml.internal.security.utils.Base64;

//糖🗿子超🇨

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SpecialCharacterTest extends BaseTest{
	@Resource
	UserService userService;
	static String[] letters = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
	@Test
	public void test001BatchCreateUser() throws UnsupportedEncodingException{
		/*String encode1 = new String(EncodeHelper.encodeBase64("糖🗿子超🇨".getBytes("utf-8")));
		System.out.println(encode1);
		String encode2 = new String(Base64Helper.encode("糖🗿子超🇨".getBytes("utf-8")));
		System.out.println(encode2);
		String encode3 = Base64.encode("糖🗿子超🇨".getBytes("utf-8"));
		System.out.println(encode3);
		
		System.out.println(new String(EncodeHelper.decodeBase64(encode1)));
		System.out.println(new String(Base64Helper.decode(encode2)));
		System.out.println(new String(Base64.decode(encode3)));*/
		//int count = 10;
		User user = new User();
		user.setCountrycode(86);
		user.setMobileno(String.format("1861%s2%s", RandomData.intNumber(100,999),RandomData.intNumber(100,999)));
		user.setBirthday(String.format("%s-%s-%s", RandomData.intNumber(1969,2010),String.format("%02d", RandomData.intNumber(1,12)),String.format("%02d", RandomData.intNumber(1,28))));
		//user.setNick(Base64Helper."糖🗿子超🇨");//RandomPicker.randString(letters, 10));
		user.setNick(new String(EncodeHelper.encodeBase64("糖🗿子超🇨".getBytes())));
		user.setSex(RandomData.flag()?"男":"女");
		//user.setId(doGenKey());
		user.setRegdevice(DeviceEnum.IPhone.getSname());
		user.setRegip("192.168.1.6");
		user.setLastlogindevice(DeviceEnum.IPhone.getSname());
		user.setLastlogindevice_uuid(UUID.randomUUID().toString());
		userService.insert(user);	
	}
}
