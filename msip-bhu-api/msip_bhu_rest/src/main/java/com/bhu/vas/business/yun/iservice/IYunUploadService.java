package com.bhu.vas.business.yun.iservice;

import com.bhu.vas.api.rpc.vap.iservice.IVapRpcService;

public interface IYunUploadService {

	void deleteFile(String versionId, String dut, boolean fw);
	
	String[] getURL(boolean fw, String versionId, String dut);
	String getURL(String filename);
	
	void uploadYun(final byte[] bs, final int uid, final String dut, final boolean fw,
			final String versionId, final IVapRpcService rpcService);
	boolean uploadYun(final byte[] bs,String filepath);
}
