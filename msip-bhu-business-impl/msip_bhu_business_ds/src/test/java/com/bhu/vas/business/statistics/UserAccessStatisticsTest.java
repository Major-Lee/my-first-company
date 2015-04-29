package com.bhu.vas.business.statistics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.rpc.statistics.model.UserAccessStatistics;
import com.bhu.vas.api.rpc.statistics.model.pk.UserDatePK;
import com.bhu.vas.business.ds.statistics.service.UserAccessStatisticsService;
import com.smartwork.msip.cores.helper.DateHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.localunit.BaseTest;

/**
 * Created by bluesand on 4/28/15.
 */
public class UserAccessStatisticsTest extends BaseTest {

    @Resource
    private UserAccessStatisticsService userAccessStatisticsService;

    
    //@Test
    public void testGetByid() {
    	UserDatePK pk1 = new UserDatePK("38:48:4c:c5:05:6d","2015-04-28");
    	
    	/*UserAccessStatistics ss = userAccessStatisticsService.getById(pk1);
    	System.out.println(ss.getInnerModelJsons());*/
    	
    	UserDatePK pk2 = new UserDatePK("a4:5e:60:bb:86:7d","2015-04-28");
    	List<UserDatePK> pks = new ArrayList<UserDatePK>();
    	pks.add(pk1);
    	pks.add(pk2);
    	List<UserAccessStatistics> findByIds = userAccessStatisticsService.findByIds(pks);
    	//UserAccessStatistics ss = userAccessStatisticsService.getById(pk);
    	System.out.println(findByIds.size());
        /*ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andColumnEqualTo("date", "2015-04-28");
        mc.setPageNumber(1);
        mc.setPageSize(20);
        TailPage<UserAccessStatistics> userAccessStatisticses = userAccessStatisticsService.findModelTailPageByModelCriteria(mc);
        System.out.println(userAccessStatisticses.getItems());
        System.out.println(userAccessStatisticses.getTotalItemsCount());*/
    }

    @Test
    public void testPage() {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andColumnEqualTo("date", "2015-04-28");
        mc.setPageNumber(1);
        mc.setPageSize(20);
        TailPage<UserAccessStatistics> userAccessStatisticses = userAccessStatisticsService.findModelTailPageByModelCriteria(mc);
        System.out.println(userAccessStatisticses.getItems());
        System.out.println(userAccessStatisticses.getTotalItemsCount());
    }
//    @Test
//    public void insert() {
//        UserAccessStatistics userAccessStatistics = new UserAccessStatistics();
//        UserDatePK userDatePK = new UserDatePK();
//        userDatePK.setMac("84:82:f4:19:00:b4");
//        userDatePK.setDate(DateHelper.COMMON_HELPER.getDateText(new Date()));
//        userAccessStatistics.setMac("84:82:f4:19:00:b4");
//        userAccessStatistics.setId(userDatePK);
//        userAccessStatistics.setDate(DateHelper.COMMON_HELPER.getDateText(new Date()));
//        userAccessStatistics.setCreated_at(new Date());
//        Map<String, Integer> mapper = new HashMap<String, Integer>();
//        mapper.put("baidu.com", 123);
//        mapper.put("sina.com", 32323);
//        mapper.put("google.com", 123123123);
//        userAccessStatistics.replaceAll(mapper);
//        userAccessStatisticsService.insert(userAccessStatistics);
//        System.out.println(userDatePK);
//
//    }

   // @Test
    public void insertData() {
        readTxtFile("/Users/bluesand/Documents/bhu/msip_bhu_business/msip-bhu-business-impl/msip_bhu_business_ds/src/test/java/com/bhu/vas/business/statistics/logfile.log");
    }

    public void readTxtFile(String filePath) {

        Map<UserDatePK, UserAccessStatistics> resultMapper = new HashMap<UserDatePK, UserAccessStatistics>();

        String currentDate = DateHelper.COMMON_HELPER.getDateText(new Date());

        try {
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(new FileInputStream(file));//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    //System.out.println(lineTxt);
                    if (lineTxt.startsWith("0001") && lineTxt.endsWith("0000")) {
                        int currentIndex = 0;
                        String part0001 = lineTxt.substring(currentIndex, currentIndex + 4) +
                                lineTxt.substring(currentIndex + 4, currentIndex + 8) +
                                lineTxt.substring(currentIndex + 8, currentIndex + 8 + Integer.parseInt(lineTxt.substring(currentIndex + 4, currentIndex + 8)));
                        System.out.println(" --- 任务id : " + part0001);
                        currentIndex = part0001.length();

                        String part0002 = lineTxt.substring(currentIndex, currentIndex + 4) +
                                lineTxt.substring(currentIndex + 4, currentIndex + 8) +
                                lineTxt.substring(currentIndex + 8, currentIndex + 8 + Integer.parseInt(lineTxt.substring(currentIndex + 4, currentIndex + 8)) + 1);
                        System.out.println(" --- 客户端MAC : " + part0002);
                        currentIndex = part0001.length() + part0002.length();

                        String part0003 = lineTxt.substring(currentIndex, currentIndex + 4) +
                                lineTxt.substring(currentIndex + 4, currentIndex + 8) +
                                lineTxt.substring(currentIndex + 8, currentIndex + 8 + Integer.parseInt(lineTxt.substring(currentIndex + 4, currentIndex + 8)));
                        System.out.println(" --- 设备MAC : " + part0003);
                        currentIndex = part0001.length() + part0002.length() + part0003.length();

                        String part0004 = lineTxt.substring(currentIndex, currentIndex + 4) +
                                lineTxt.substring(currentIndex + 4, currentIndex + 8) +
                                lineTxt.substring(currentIndex + 8, currentIndex + 8 + Integer.parseInt(lineTxt.substring(currentIndex + 4, currentIndex + 8)));
                        System.out.println(" --- 访问IP : " + part0004);
                        currentIndex = part0001.length() + part0002.length() + part0003.length() + part0004.length();

                        String part0005 = lineTxt.substring(currentIndex, currentIndex + 4) +
                                lineTxt.substring(currentIndex + 4, currentIndex + 8) +
                                lineTxt.substring(currentIndex + 8, currentIndex + 8 + Integer.parseInt(lineTxt.substring(currentIndex + 4, currentIndex + 8)));
                        System.out.println(" --- HOST : " + part0005);
                        currentIndex = part0001.length() + part0002.length() + part0003.length() + part0004.length() +
                                part0005.length();

                        String part0006 = lineTxt.substring(currentIndex, currentIndex + 4) +
                                lineTxt.substring(currentIndex + 4, currentIndex + 8) +
                                lineTxt.substring(currentIndex + 8, currentIndex + 8 + Integer.parseInt(lineTxt.substring(currentIndex + 4, currentIndex + 8)));
                        System.out.println(" --- URI : " + part0006);
                        currentIndex = part0001.length() + part0002.length() + part0003.length() + part0004.length() +
                                part0005.length() + part0006.length();

                        String part0007 = lineTxt.substring(currentIndex, currentIndex + 4) +
                                lineTxt.substring(currentIndex + 4, currentIndex + 8) +
                                lineTxt.substring(currentIndex + 8, currentIndex + 8 + Integer.parseInt(lineTxt.substring(currentIndex + 4, currentIndex + 8)));
                        System.out.println(" --- ACCEPT : " + part0007);
                        currentIndex = part0001.length() + part0002.length() + part0003.length() + part0004.length() +
                                part0005.length() + part0006.length() + part0007.length();

                        String part0008 = lineTxt.substring(currentIndex, currentIndex + 4) +
                                lineTxt.substring(currentIndex + 4, currentIndex + 8) +
                                lineTxt.substring(currentIndex + 8, currentIndex + 8 + Integer.parseInt(lineTxt.substring(currentIndex + 4, currentIndex + 8)));
                        System.out.println(" --- USER_AGENT : " + part0008);
                        currentIndex = part0001.length() + part0002.length() + part0003.length() + part0004.length() +
                                part0005.length() + part0006.length() + part0007.length() + part0008.length();

                        System.out.println("------------------------------");


                        UserDatePK userDatePK = new UserDatePK();
                        userDatePK.setDate(currentDate);
                        String mac = part0002.substring(8);
                        userDatePK.setMac(mac);
                        String host = part0005.substring(8);
                        UserAccessStatistics userAccessStatistics = new UserAccessStatistics();
                        if (resultMapper.get(userDatePK) == null) {
                            userAccessStatistics.setMac(mac);
                            userAccessStatistics.setId(userDatePK);
                            userAccessStatistics.setDate(currentDate);
                            userAccessStatistics.setCreated_at(new Date());
                        } else {
                            userAccessStatistics = resultMapper.get(userDatePK);
                        }

                        String[] hosts = host.split("\\.");
                        if (hosts.length > 2) {
                            host = host.substring(host.indexOf(".") + 1);
                        }
                        userAccessStatistics.incrKey(host);
                        resultMapper.put(userDatePK, userAccessStatistics);

                    }
                }
                read.close();

                for(UserDatePK userDatePK : resultMapper.keySet()) {
                    userAccessStatisticsService.insert(resultMapper.get(userDatePK));
                }




            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
    }


}
