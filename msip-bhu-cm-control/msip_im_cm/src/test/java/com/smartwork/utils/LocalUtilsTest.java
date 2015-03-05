package com.smartwork.utils;

import org.junit.Test;

import com.smartwork.im.utils.LocaleUtils;

public class LocalUtilsTest {
	@Test
	public void localTest(){
		System.out.println(LocaleUtils.getLocalizedString("admin.error.bad-stream"));
		System.out.println(System.getProperty("user.dir"));
	}
}	
