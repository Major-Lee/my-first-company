package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchimport.callback;

import java.util.Set;

import com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchimport.dto.DeviceCallbackDTO;

public interface ImportElementCallback {
	public DeviceCallbackDTO elementDeviceInfoFetch(String sn);
	public void afterExcelImported(Set<String> dmacs, Set<String> failed_sns);
	//public int useridFetch(String mobileno);
	
}
