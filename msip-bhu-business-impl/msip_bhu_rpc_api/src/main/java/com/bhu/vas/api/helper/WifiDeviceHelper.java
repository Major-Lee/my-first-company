package com.bhu.vas.api.helper;


public class WifiDeviceHelper {
	
	public static boolean isLocationCMDSupported(){
		return false;
	}
	
	/**
	 * opt = OperationCMD.ModifyDeviceSetting
	 * 		subopt = OperationDS.
	 * 			DS_Http_Ad_Start("01","开启广告注入"),
				DS_Http_404_Start("15","开启404错误页面"),
				DS_Http_Redirect_Start("16","开启http redirect"),
				//DS_Http_Portal_Start("17","开启http portal"),
				//DS_Http_Portal_Stop("18","关闭http portal"),
				DS_Http_Ad_Stop("19","关闭广告注入"),
				DS_Http_404_Stop("20","关闭404错误页面"),
				DS_Http_Redirect_Stop("21","关闭http redirect"),
	 * opt = OperationCMD.TurnOnDeviceDPINotify
	 * opt = OperationCMD.TurnOffDeviceDPINotify
	 * @param opt
	 * @param subopt
	 * @return
	 */
	public static Action needPersistenceAction(OperationCMD opt,OperationDS subopt){
		Action result = null;
		if(opt == null) return result;
		
		switch(opt){
			case ModifyDeviceSetting:
				if(subopt == null) return result;
				switch(subopt){
					case DS_Http_Ad_Start:
						break;
					case DS_Http_404_Start:
						break;
					case DS_Http_Redirect_Start:
						break;
					case DS_Http_Ad_Stop:
						break;	
					case DS_Http_404_Stop:
						break;
					case DS_Http_Redirect_Stop:
						break;	
					default:
						break;	
				}
				break;
			case TurnOnDeviceDPINotify:
				break;
			case TurnOffDeviceDPINotify:
				break;
			default:
				break;	
		}
		return result;
	}
	
	
	public class Action{
		public static final String Oper_Update = "update";
		public static final String Oper_Remove = "remove";
		private String operation;
		private String key;
		public String getOperation() {
			return operation;
		}
		public void setOperation(String operation) {
			this.operation = operation;
		}
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
	}
}
