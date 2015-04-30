package com.bhu.vas.di.op;

import com.bhu.vas.api.rpc.statistics.model.UserAccessStatistics;
import com.bhu.vas.api.rpc.statistics.model.pk.UserDatePK;
import com.bhu.vas.business.ds.statistics.service.UserAccessStatisticsService;
import com.smartwork.msip.cores.helper.DateHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bluesand on 4/30/15.
 */
public class UserAccessStatisticOp {

    private static UserAccessStatisticsService userAccessStatisticsService;

    public static void main(String[] args) {
        if(args.length <1)  {
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
                        userDatePK.setMac(mac.replace(" ", ""));
                        String deviceMac = part0003.substring(8);
                        String host = part0005.substring(8);


                        UserAccessStatistics userAccessStatistics = new UserAccessStatistics();
                        if (resultMapper.get(userDatePK) == null) {
                            userAccessStatistics.setMac(mac);
                            userAccessStatistics.setId(userDatePK);
                            userAccessStatistics.setDate(currentDate);
                            userAccessStatistics.setDevice_mac(deviceMac);
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
