package com.bhu.vas.di.op;

import com.bhu.vas.api.rpc.statistics.model.UserAccessStatistics;
import com.bhu.vas.api.rpc.statistics.model.pk.UserDatePK;
import com.bhu.vas.business.ds.statistics.service.UserAccessStatisticsService;
import com.smartwork.msip.cores.helper.DateHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by bluesand on 4/30/15.
 */
public class UserAccessStatisticOp {

    private static UserAccessStatisticsService userAccessStatisticsService;

    public static void main(String[] args) {
        if (args.length < 1) {
            return;
        }
        String filepath = args[0];// ADD REMOVE
        ApplicationContext ctx = new FileSystemXmlApplicationContext(
                "classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
        userAccessStatisticsService = (UserAccessStatisticsService)
                ctx.getBean("userAccessStatisticsService");

        readTxtFile(filepath);

    }

    public static void readTxtFile(String filePath) {
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

                        DpiInfo dinfo = DpiInfo.fromTextLine(lineTxt);

                        UserDatePK userDatePK = new UserDatePK();
                        userDatePK.setDate(currentDate);
                        userDatePK.setMac(dinfo.getTmac());

                        UserAccessStatistics userAccessStatistics = new UserAccessStatistics();
                        if (resultMapper.get(userDatePK) == null) {
                            userAccessStatistics.setMac(dinfo.getTmac());
                            userAccessStatistics.setId(userDatePK);
                            userAccessStatistics.setDate(currentDate);
                            userAccessStatistics.setDevice_mac(dinfo.getDmac());
                            userAccessStatistics.setCreated_at(new Date());
                        } else {
                            userAccessStatistics = resultMapper.get(userDatePK);
                        }
                        String host = dinfo.getAhost();
                        String[] hosts = host.split("\\.");
                        if (hosts.length > 2) {
                            host = host.substring(host.indexOf(".") + 1);
                        }
                        userAccessStatistics.incrKey(host);
                        resultMapper.put(userDatePK, userAccessStatistics);
                    }
                }
                read.close();


                for (UserDatePK userDatePK : resultMapper.keySet()) {
                    UserAccessStatistics userAccessStatistics = resultMapper.get(userDatePK);
                    userAccessStatistics.setExtension_content(JsonHelper.getJSONString(sortByValue(sortByValue(userAccessStatistics.fetchAll())), false));
                    userAccessStatisticsService.insert(userAccessStatistics);
                }

            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
