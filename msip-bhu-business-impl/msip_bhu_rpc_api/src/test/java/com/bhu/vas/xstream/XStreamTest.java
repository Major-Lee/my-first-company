package com.bhu.vas.xstream;

import junit.framework.TestCase;

import com.bhu.vas.api.dto.WifiDeviceDTO;
import com.bhu.vas.api.dto.header.JoinReqDTO;
import com.smartwork.msip.cores.helper.XStreamHelper;

public class XStreamTest extends TestCase {
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}

	public void testJoinReqMsg(){
		JoinReqDTO parentDto = new JoinReqDTO();
		WifiDeviceDTO dto = new WifiDeviceDTO();
		dto.setMac("34:36:3b:d0:4b:ac");
		parentDto.setDto(dto);
		
		String xml = XStreamHelper.toXML(parentDto);
		System.out.println(xml);
		
		JoinReqDTO parentDto2 = XStreamHelper.fromXML(xml, JoinReqDTO.class);
		System.out.println(parentDto2.getDto().getMac());
	}
	

}
