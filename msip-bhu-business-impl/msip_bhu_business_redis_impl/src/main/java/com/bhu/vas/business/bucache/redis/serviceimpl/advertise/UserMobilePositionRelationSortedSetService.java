package com.bhu.vas.business.bucache.redis.serviceimpl.advertise;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationSortedSetCache;
import com.smartwork.msip.cores.helper.sms.SmsSenderFactory;

public class UserMobilePositionRelationSortedSetService extends AbstractRelationSortedSetCache{
	
	private static final long DefaultScore = 0L; 
	
    private static class ServiceHolder{
        private static UserMobilePositionRelationSortedSetService instance =new UserMobilePositionRelationSortedSetService();
    }

    /**
     * 获取工厂单例
     * @return
     */
    public static UserMobilePositionRelationSortedSetService getInstance() {
        return ServiceHolder.instance;
    }
    
    private static String generateKey(String province,String city ,String district){
        StringBuilder sb = new StringBuilder(BusinessKeyDefine.Advertise.AdvertiseMobilePostion);
        if(!StringUtils.isBlank(province)) {
        	sb.append(province);
		}
        if(!StringUtils.isBlank(city)) {
        	sb.append(city);
		}
        if(!StringUtils.isBlank(district)) {
        	sb.append(district);
		}
        return sb.toString();
    }
    
    public void mobilenoRecord(String province,String city ,String district,String mobileno){
    	this.zadd(generateKey(province,city,district), DefaultScore, mobileno);
    }
    
    public  Set<String> fetchKeys(String province,String city ,String district){
    	return this.keys(generateKey(province,city,district)+"*");
    }
    
    public List<String> fetchPostionMobileno(String province,String city ,String district){
    	Set<String> keys = fetchKeys(province,city,district);
    	List<String> mobileList = new ArrayList<String>();
    	for(String key : keys){
    		Set<String> mobileSet =   this.zrange(key, 0, -1);
    		mobileList.addAll(mobileSet);
    	}
    	return mobileList;
    }
    
    public int zcardPostionMobileno(String province,String city ,String district){
    	Set<String> keys = fetchKeys(province,city,district);
    	int count = 0;
    	for(String key : keys){
    		count += this.zcard(key);
    	}
    	return count;
    }
    
	@Override
	public String getName() {
		return UserMobilePositionRelationSortedSetService.class.getName();
	}

	@Override
	public String getRedisKey() {
		return null;
	}

	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.ADVERTISE);
	}
	public static void main(String[] args) {
		String[] accs = new String[]{
		"13126634433","13853271585","18013698663","15685176247","18530071572","13951059220","15333253977","13140433222","15229039999","15380003131","18610360797",
		"18610886331","18993875208","15017920588","13037938008","15560254138","13591333811","13979156715","15834077068","15198968109","13832345391","17717757588",
		"18731280109","13500437110","15933952909","13677443588","15155111441","13696490080","13928220488","18367609955","18394395603","18650350170","18194293422",
		"13641227245","18168572790","15954500829","15594533388","15032352512","15103556108","18204573543","13786772188","18041052468","18324224000","15699996061",
		"13555005591","17704705200","13779010603","15036657925","15873585301","15055056665","13866885241","18930225667","13176539627","18208881938","13055737713",
		"13640756246","13804469845","18800177715","13045356222","17737619516","13761059861","13833953559","15363771388","18677172286","18666820032","13969699031",
		"15598815449","13283738777","18037899591","13932213399","18660142664","15843698880","18061148653","13021887821","15153186968","13875939982","18768188976",
		"18985155313","13998543606","18998968888","13823266253","15606132130","15925956002","15618100738","18373011088","15071780388","13146393452","18753596788",
		"18945405321","18633725717","18949333300","18603094277","15966881666","13731218988","13910646016","13395490518","13502177075","15848075671","13613359433",
		"15066819698","13997874907","13897879655","18519269345","17707919925","13653060207","13878118802","13450887655","13774389351","18047915558","18395000177",
		"18580221777","18618486320","13512346215","15586867716","13805842248","13562778238","13103717686","15127166171"};
		String smsg = "打赏金额太少？全城热播不会用？今晚18点30分相聚必虎直播间：https://www.douyu.com/room/share/1461579，另有城市运营商最新优惠政策。请城市运营商准时参加";
		String response = SmsSenderFactory.buildSender(
		BusinessRuntimeConfiguration.InternalMarketingSMS_Gateway).send(smsg, accs);
		System.out.println(response);
	}
}
