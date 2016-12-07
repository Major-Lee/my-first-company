package com.bhu.vas.plugins.quartz;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.UserMobilePositionRelationSortedSetService;
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.ArrayHelper;
import com.smartwork.msip.cores.helper.sms.SmsSenderFactory;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

public class AdvertiseSortMessageSenderTaskLoader {
	private static Logger logger = LoggerFactory.getLogger(AdvertiseSortMessageSenderTaskLoader.class);
	
	@Resource
	private AdvertiseService advertiseService;
	
	public void execute() {
		logger.info("AdvertiseSortMessageSenderTaskLoader start...");
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("type", Advertise.sortMessage).andColumnEqualTo("state", BusinessEnumType.AdvertiseType.UnPublish.getType());
		List<Advertise> ads = advertiseService.findModelByModelCriteria(mc);
		if(!ads.isEmpty()){
			for(Advertise ad : ads){
				sendMessage(ad);
				ad.setState(BusinessEnumType.AdvertiseType.Published.getType());
			}
			advertiseService.updateAll(ads);
		}
		logger.info("AdvertiseSortMessageSenderTaskLoader end...");
	}
	
	public void sendMessage(Advertise ad){
		List<String> mobilenos = UserMobilePositionRelationSortedSetService.getInstance().fetchPostionMobileno(ad.getProvince(), ad.getCity(), ad.getDistrict());
		String[] accs = ArrayHelper.toStringArray(mobilenos);
		String smsg = ad.getDescription();
		String response = SmsSenderFactory.buildSender(
				BusinessRuntimeConfiguration.InternalCaptchaCodeSMS_Gateway).send(smsg, accs);
		logger.info(String.format("sendCaptchaCodeNotifyHandle acc[%s] msg[%s] response[%s]",accs.toString(),smsg,response));
	}
	public static void main(String[] args) {
		List<String> mobilenos = UserMobilePositionRelationSortedSetService.getInstance().fetchPostionMobileno("山西省", "", "");
		for(String str : mobilenos){
			System.out.println(str);
		}
	}
}
