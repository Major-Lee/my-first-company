package com.bhu.vas.business.statistics;

import com.bhu.vas.api.rpc.charging.model.UserIncomeMonthRank;
import com.bhu.vas.api.rpc.statistics.dto.UserBrandDTO;
import com.bhu.vas.api.rpc.statistics.dto.UserBrandStatisticsDTO;
import com.bhu.vas.api.rpc.statistics.dto.UserBrandSubDTO;
import com.bhu.vas.api.rpc.statistics.model.UserBrandStatistics;
import com.bhu.vas.business.ds.statistics.service.UserBrandStatisticsService;
import com.bhu.vas.business.ds.statistics.service.UserIncomeMonthRankService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.localunit.BaseTest;
import org.junit.Test;

import javax.annotation.Resource;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by bluesand on 4/28/15.
 */
public class UserIncomeMouthRankStatisticsTest extends BaseTest {

	@Resource
	private UserIncomeMonthRankService userIncomeMonthRankService;



    @Test
    public void find() {
//        UserBrandStatistics userBrandStatistics = userBrandStatisticsService.getById("2015-05-23");
//
//        System.out.println(userBrandStatistics.getExtension_content());
//
//        System.out.println(userBrandStatistics.getInnerModels());
////        List<UserBrandDTO> userBrandStatisticsDTOs  =
////                JsonHelper.getDTOList(userBrandStatistics.getExtension_content(),UserBrandDTO.class);
//
//        //System.out.println(userBrandStatisticsDTOs);

    	Date date = new Date();  
		Calendar calendar = Calendar.getInstance();  
		calendar.setTime(date);  
		calendar.add(Calendar.MONTH, 0-1);  
		date = calendar.getTime();  
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");  
		String time =sdf.format(date);
		System.out.println(time);
    	List<UserIncomeMonthRank> userIncomList = userIncomeMonthRankService.findByLimit(time,5,1);
		System.out.println("userIncomList size:"+userIncomList.size());
		if(userIncomList.size()<=0){
			
			System.out.println(userIncomList.size()+"statring");
		}else{
			
			System.out.println(userIncomList.size());
		}


    }


}
