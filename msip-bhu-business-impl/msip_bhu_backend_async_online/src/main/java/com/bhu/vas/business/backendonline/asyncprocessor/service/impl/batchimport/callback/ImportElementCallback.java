package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchimport.callback;

import java.util.Set;

import com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchimport.dto.DeviceCallbackDTO;

public interface ImportElementCallback {
	public DeviceCallbackDTO elementDeviceInfoFetch(String sn);
	public void afterExcelImported(String opsid, Set<String> dmacs);
	//public int useridFetch(String mobileno);
	
}
