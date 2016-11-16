package com.bhu.vas.business.ds.advertise.facade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.api.vto.advertise.AdDevicePositionVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseOccupiedVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseTrashPositionVTO;
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;

@Service
public class AdvertiseFacadeService {
	@Resource
	private AdvertiseService advertiseService;
	
	public List<Advertise> fetchAdvertiseOccupy(String province,String city,String district){
		ModelCriteria mc=new ModelCriteria();
		Criteria criteria = mc.createCriteria();
			try {
				if(district !=null && !district.isEmpty()){
					criteria.andColumnEqualTo("district", district);
				}
				if(city !=null && !city.isEmpty()){
					criteria.andColumnEqualTo("city", city);
				}
				if(province !=null && !province.isEmpty()){
					criteria.andColumnEqualTo("province", province);
				}
				criteria.andColumnBetween("start", DateTimeHelper.getAfterDate(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern5), 2),
						DateTimeHelper.getAfterDate(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern5), 17))
				.andColumnNotEqualTo("state", BusinessEnumType.AdvertiseType.EscapeOrder.getType())
				.andColumnNotEqualTo("state", BusinessEnumType.AdvertiseType.VerifyFailure.getType());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			List<Advertise> advertises = advertiseService.findModelByModelCriteria(mc);
		
		return advertises;
	}
}
