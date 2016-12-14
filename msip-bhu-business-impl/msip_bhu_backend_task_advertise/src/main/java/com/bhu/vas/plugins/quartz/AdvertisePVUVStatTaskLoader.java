package com.bhu.vas.plugins.quartz;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.rpc.advertise.model.AdvertiseDetails;
import com.bhu.vas.business.ds.advertise.service.AdvertiseDevicesIncomeService;
import com.bhu.vas.util.JSONObject;
import com.bhu.vas.util.um.OpenApiCnzzImpl;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

public class AdvertisePVUVStatTaskLoader {
	private static Logger logger = LoggerFactory.getLogger(AdvertisePVUVStatTaskLoader.class);
	
	@Resource
	private AdvertiseDevicesIncomeService advertiseDevicesIncomeService;
	
	public void execute() {
		String publish_time = null;
		try {
			publish_time = DateTimeHelper.getBeforeDate(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern5), 1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("publish_time", publish_time);
		
		List<AdvertiseDetails> details = advertiseDevicesIncomeService.findModelByModelCriteria(mc);
		List<AdvertiseDetails> detailwithpuv = new ArrayList<AdvertiseDetails>();
		for(AdvertiseDetails detail : details){
			detailwithpuv.add(advertisePVUVStat(detail));
		}
		advertiseDevicesIncomeService.updateAll(detailwithpuv);
	}
	
	public AdvertiseDetails advertisePVUVStat(AdvertiseDetails detail){
		//um工具对象创建
		OpenApiCnzzImpl apiCnzzImpl=new OpenApiCnzzImpl();
		
		String pcUm=apiCnzzImpl.queryCnzzStatistic("PC热播PV", detail.getPublish_time(), detail.getPublish_time(), "", "id ="+detail.getAdvertiseid(),2);
		String mobileUm=apiCnzzImpl.queryCnzzStatistic("mobile热播PV", detail.getPublish_time(), detail.getPublish_time(), "", "id ="+detail.getAdvertiseid(),2);
		
		int pcUV=0;
		int pcPV=0;
		int mobileUV=0;
		int mobilePV=0;
		
		JSONObject pcUvJson=JSONObject.fromObject(pcUm);
		String pcUvJsonStr=pcUvJson.getString("values");
		pcUvJsonStr=pcUvJsonStr.substring(1);
		pcUvJsonStr=pcUvJsonStr.substring(0, pcUvJsonStr.length()-1);
		pcUV=Integer.valueOf(pcUvJsonStr.split(",")[1].replace(".0", "").trim());
		pcPV=Integer.valueOf(pcUvJsonStr.split(",")[0].replace(".0", "").trim());
		
		JSONObject mobileUvJson=JSONObject.fromObject(mobileUm);
		String mobileUvJsonStr=mobileUvJson.getString("values");
		mobileUvJsonStr=mobileUvJsonStr.substring(1);
		mobileUvJsonStr=mobileUvJsonStr.substring(0, mobileUvJsonStr.length()-1);
		mobileUV=Integer.valueOf(mobileUvJsonStr.split(",")[1].replace(".0", "").trim());
		mobilePV=Integer.valueOf(mobileUvJsonStr.split(",")[0].replace(".0", "").trim());
		
		detail.setPv(pcPV+mobilePV);
		detail.setUv(pcUV+mobileUV);
		
		return detail;
	}
}
