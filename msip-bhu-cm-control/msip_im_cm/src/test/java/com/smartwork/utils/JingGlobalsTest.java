package com.smartwork.utils;

import org.junit.Test;

import com.smartwork.im.utils.JingGlobals;


public class JingGlobalsTest {
	@Test
	public void globalsTest(){
		JingGlobals.setHomeDirectory(System.getProperty("user.dir"));
		System.out.println(JingGlobals.getXMLProperty("xmpp.domain", "test"));
	}
}
