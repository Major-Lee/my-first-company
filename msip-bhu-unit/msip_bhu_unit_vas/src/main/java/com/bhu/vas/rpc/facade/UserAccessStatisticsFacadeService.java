package com.bhu.vas.rpc.facade;

import ch.qos.logback.classic.Logger;
import com.bhu.vas.api.rpc.statistics.UserAccessStatisticsDTO;
import com.bhu.vas.api.rpc.statistics.model.UserAccessStatistics;
import com.bhu.vas.api.rpc.statistics.model.pk.UserDatePK;
import com.bhu.vas.api.vto.WifiDeviceVTO;
import com.bhu.vas.business.ds.statistics.service.UserAccessStatisticsService;
import com.smartwork.msip.cores.helper.DateHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.Page;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by bluesand on 4/29/15.
 */
@Service
public class UserAccessStatisticsFacadeService {

    private final Logger logger = (Logger)LoggerFactory.getLogger(UserAccessStatisticsFacadeService.class);
    @Resource
    private UserAccessStatisticsService userAccessStatisticsService;


    public TailPage<UserAccessStatisticsDTO> fetchUserAccessStatistics(String date, int pageNo, int pageSize) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andColumnEqualTo("date", date);
        mc.setPageNumber(pageNo);
        mc.setPageSize(pageSize);

        TailPage<UserAccessStatistics> result = userAccessStatisticsService.findModelTailPageByModelCriteria(mc);
        List<UserAccessStatisticsDTO> userAccessStatisticsList = new ArrayList<UserAccessStatisticsDTO>();

        for (UserAccessStatistics userAccessStatistics : result.getItems()) {
            UserAccessStatisticsDTO userAccessStatisticsDTO = new UserAccessStatisticsDTO();
            userAccessStatisticsDTO.setDate(userAccessStatistics.getDate());
            userAccessStatisticsDTO.setMac(userAccessStatistics.getMac());
            userAccessStatisticsDTO.setCreate_at(userAccessStatistics.getCreated_at());
            userAccessStatisticsDTO.setUpdate_at(userAccessStatistics.getUpdated_at());
            userAccessStatisticsDTO.setExtension_content(userAccessStatistics.getExtension_content());
            userAccessStatisticsList.add(userAccessStatisticsDTO);
        }

        return new CommonPage<UserAccessStatisticsDTO>(pageNo, pageSize,
                result.getTotalItemsCount(), userAccessStatisticsList);
    }


    public boolean createUserAccessStatistics(String filepath) {
        readTxtFile(filepath);
        return true;
    }

    public void readTxtFile(String filePath) {
        logger.info("start....'");
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
                        logger.info(" --- 访问IP : " + part0004);
                        currentIndex = part0001.length() + part0002.length() + part0003.length() + part0004.length();

                        String part0005 = lineTxt.substring(currentIndex, currentIndex + 4) +
                                lineTxt.substring(currentIndex + 4, currentIndex + 8) +
                                lineTxt.substring(currentIndex + 8, currentIndex + 8 + Integer.parseInt(lineTxt.substring(currentIndex + 4, currentIndex + 8)));
                        logger.info(" --- HOST : " + part0005);
                        currentIndex = part0001.length() + part0002.length() + part0003.length() + part0004.length() +
                                part0005.length();

                        String part0006 = lineTxt.substring(currentIndex, currentIndex + 4) +
                                lineTxt.substring(currentIndex + 4, currentIndex + 8) +
                                lineTxt.substring(currentIndex + 8, currentIndex + 8 + Integer.parseInt(lineTxt.substring(currentIndex + 4, currentIndex + 8)));
                        logger.info(" --- URI : " + part0006);
                        currentIndex = part0001.length() + part0002.length() + part0003.length() + part0004.length() +
                                part0005.length() + part0006.length();

                        String part0007 = lineTxt.substring(currentIndex, currentIndex + 4) +
                                lineTxt.substring(currentIndex + 4, currentIndex + 8) +
                                lineTxt.substring(currentIndex + 8, currentIndex + 8 + Integer.parseInt(lineTxt.substring(currentIndex + 4, currentIndex + 8)));
                        logger.info(" --- ACCEPT : " + part0007);
                        currentIndex = part0001.length() + part0002.length() + part0003.length() + part0004.length() +
                                part0005.length() + part0006.length() + part0007.length();

                        String part0008 = lineTxt.substring(currentIndex, currentIndex + 4) +
                                lineTxt.substring(currentIndex + 4, currentIndex + 8) +
                                lineTxt.substring(currentIndex + 8, currentIndex + 8 + Integer.parseInt(lineTxt.substring(currentIndex + 4, currentIndex + 8)));
                        logger.info(" --- USER_AGENT : " + part0008);
                        currentIndex = part0001.length() + part0002.length() + part0003.length() + part0004.length() +
                                part0005.length() + part0006.length() + part0007.length() + part0008.length();

                        logger.info("------------------------------");


                        UserDatePK userDatePK = new UserDatePK();
                        userDatePK.setDate(currentDate);
                        String mac = part0002.substring(8);
                        userDatePK.setMac(mac.replace(" ", ""));
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

                for (UserDatePK userDatePK : resultMapper.keySet()) {
                    userAccessStatisticsService.insert(resultMapper.get(userDatePK));
                }

                logger.info("end....'");
            } else {
                logger.info("找不到指定的文件");
            }
        } catch (Exception e) {
            logger.info("读取文件内容出错");
            e.printStackTrace();
        }

    }


}
