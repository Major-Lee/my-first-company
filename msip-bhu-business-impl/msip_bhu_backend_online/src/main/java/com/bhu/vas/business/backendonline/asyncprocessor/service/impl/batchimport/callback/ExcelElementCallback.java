package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchimport.callback;

import com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchimport.dto.DeviceCallbackDTO;

public interface ExcelElementCallback {
	public DeviceCallbackDTO elementDeviceInfoFetch(String sn);
	//public int useridFetch(String mobileno);
	
}
