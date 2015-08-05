package com.bhu.vas.api.helper;

import com.smartwork.msip.cores.helper.StringHelper;


public class WifiDeviceHelper {
	public static final String WifiTimer_Default_Timeslot = "00:00:00-24:00:00";
	public static final String[] WifiTimer_Default_Timeslot_Array = {"00:00:00","24:00:00"};

	public static final String Upgrade_Default_BeginTime = "02:00:00";
	public static final String Upgrade_Default_EndTime = "04:00:00";
	
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
	public static PersistenceAction needPersistenceAction(OperationCMD opt,OperationDS subopt){
		PersistenceAction result = null;
		if(opt == null) return result;
		switch(opt){
			case ModifyDeviceSetting:
				if(subopt == null) return result;
				switch(subopt){
					case DS_Http_Ad_Start:
						result = builderPersistenceAction(opt,subopt,PersistenceAction.Oper_Update);
						break;	
					case DS_Http_404_Start:
						result = builderPersistenceAction(opt,subopt,PersistenceAction.Oper_Update);
						break;	
					case DS_Http_Redirect_Start:
						result = builderPersistenceAction(opt,subopt,PersistenceAction.Oper_Update);
						break;	
					case DS_Http_Ad_Stop:
						result = builderPersistenceAction(opt,OperationDS.DS_Http_Ad_Start,PersistenceAction.Oper_Remove);
						break;	
					case DS_Http_404_Stop:
						result = builderPersistenceAction(opt,OperationDS.DS_Http_404_Start,PersistenceAction.Oper_Remove);
						break;	
					case DS_Http_Redirect_Stop:
						result = builderPersistenceAction(opt,OperationDS.DS_Http_Redirect_Start,PersistenceAction.Oper_Remove);
						break;	
					default:
						break;	
				}
				break;
			case TurnOnDeviceDPINotify:
				result = builderPersistenceAction(opt,subopt,PersistenceAction.Oper_Update);
				break;
			case TurnOffDeviceDPINotify:
				result = builderPersistenceAction(OperationCMD.TurnOnDeviceDPINotify,subopt,PersistenceAction.Oper_Remove);
				break;
			default:
				break;	
		}
		return result;
	}
	
	public static PersistenceAction builderPersistenceAction(OperationCMD opt,OperationDS subopt,String operation){
		PersistenceAction action = new PersistenceAction();
		action.setOperation(operation);
		//action.setKey(opt.getNo().concat(StringHelper.MINUS_STRING_GAP).concat(subopt!=null?subopt.getNo():OperationDS.Empty_OperationDS));
		action.setKey(builderPersistenceKey(opt.getNo(),subopt!=null?subopt.getNo():null));
		return action;
	}
	
	public static String builderPersistenceKey(String opt,String subopt){
		return opt.concat(StringHelper.MINUS_STRING_GAP).concat(subopt!=null?subopt:OperationDS.Empty_OperationDS);
	}
	
	
	public static String[] parserPersistenceKey(String persistenceKey){
		return persistenceKey.split(StringHelper.MINUS_STRING_GAP);
	}
	
	
}
