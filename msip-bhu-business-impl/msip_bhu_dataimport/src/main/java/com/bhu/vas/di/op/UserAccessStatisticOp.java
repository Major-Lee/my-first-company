package com.bhu.vas.di.op;

import com.bhu.vas.api.rpc.statistics.dto.UserBrandDTO;
import com.bhu.vas.api.rpc.statistics.dto.UserBrandSubDTO;
import com.bhu.vas.api.rpc.statistics.model.UserAccessStatistics;
import com.bhu.vas.api.rpc.statistics.model.UserBrandStatistics;
import com.bhu.vas.api.rpc.statistics.model.pk.UserDatePK;
import com.bhu.vas.business.ds.statistics.service.UserAccessStatisticsService;
import com.bhu.vas.business.ds.statistics.service.UserBrandStatisticsService;
import com.smartwork.msip.cores.helper.DateHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import nl.bitwalker.useragentutils.OperatingSystem;
import nl.bitwalker.useragentutils.UserAgent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by bluesand on 4/30/15.
 *
 *
 * TODO(bluesandd): 需要重构逻辑!!!
 */
public class UserAccessStatisticOp {

    private static UserAccessStatisticsService userAccessStatisticsService;

    private static UserBrandStatisticsService userBrandStatisticsService;

    private static String currentDate = DateHelper.COMMON_HELPER.getDateText(new Date());

//    public static void main(String[] args) {
//        if (args.length < 1) {
//            return;
//        }
//        String filepath = args[0];// ADD REMOVE
//        ApplicationContext ctx = new FileSystemXmlApplicationContext(
//                "classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
//        userAccessStatisticsService = (UserAccessStatisticsService)
//                ctx.getBean("userAccessStatisticsService");
//
//        userBrandStatisticsService = (UserBrandStatisticsService)
//                ctx.getBean("userBrandStatisticsService");
//
//        readTxtFile(filepath);
//
//    }

    public static void main(String[] args) {

        String filepath = "/Users/bluesand/Documents/logfile-20150518.log";// ADD REMOVE
        ApplicationContext ctx = new FileSystemXmlApplicationContext(
                "classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
        userAccessStatisticsService = (UserAccessStatisticsService)
                ctx.getBean("userAccessStatisticsService");

        userBrandStatisticsService = (UserBrandStatisticsService)
                ctx.getBean("userBrandStatisticsService");

        readTxtFile(filepath);

    }


    public static void readTxtFile(String filePath) {

        //操作系统
        //Map<String,Integer> opMap = new HashMap<String,Integer>();
        //设备型号 系统 型号 数量
        Map<String, Map<String, Integer>> opTypeMap = new HashMap<String, Map<String, Integer>>();
        //出现过的设备mac地址
        Set<String> macSet = new HashSet<String>();
        int total = 0;


        Map<UserDatePK, UserAccessStatistics> resultMapper = new HashMap<UserDatePK, UserAccessStatistics>();

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
                        if (host != null) {
                            String[] hosts = host.split("\\.");
                            if (hosts.length > 2) {
                                host = host.substring(host.indexOf(".") + 1);
                            }
                            userAccessStatistics.incrKey(host);
                        }

                        resultMapper.put(userDatePK, userAccessStatistics);


                        //if(macSet.contains(dinfo.getTmac())) continue;
                        //macSet.add(dinfo.getTmac());
                        //total++;
                        //System.out.println(String.format("%s %s", handsetMac,result[7]));
                        //System.out.println(String.format("%s %s", dinfo.getTmac(),dinfo.getUseragent()));
                        if (dinfo.getUseragent() != null) {
                            UserAgent useragent = new UserAgent(dinfo.getUseragent());
                            if (useragent.getOperatingSystem() == OperatingSystem.UNKNOWN) {
                                continue;
                            }

                            if (macSet.contains(dinfo.getTmac())) {
                                continue;
                            }
                            macSet.add(dinfo.getTmac());
                            total++;

                            System.out.println(useragent.getOperatingSystem());
                /*System.out.println(useragent.getBrowser());
                System.out.println(useragent.getId());
	            System.out.println(useragent.getBrowserVersion());*/
                            String terminal = null;
                            if (terminal == null) {
                                if (useragent.getOperatingSystem().toString().startsWith("WIN")) {
                                    terminal = "UNKNOW PC";
                                }
                                if (useragent.getOperatingSystem() == OperatingSystem.MAC_OS) {
                                    terminal = "Macintosh";
                                }
                                if (useragent.getOperatingSystem() == OperatingSystem.MAC_OS_X) {
                                    terminal = "Macintosh";
                                }
                                if (useragent.getOperatingSystem() == OperatingSystem.MAC_OS_X_IPHONE) {
                                    terminal = "iPhone";
                                }
                                if (useragent.getOperatingSystem() == OperatingSystem.MAC_OS_X_IPOD) {
                                    terminal = "iPod";
                                }
                                if (useragent.getOperatingSystem() == OperatingSystem.MAC_OS_X_IPAD) {
                                    terminal = "iPad";
                                }

                                if (useragent.getOperatingSystem() == OperatingSystem.ANDROID) {
                                    terminal = "UNKNOW AND";
                                }
                            }//else terminal = "未识别";

                            //System.out.println(terminal);

                            if (useragent.getOperatingSystem().toString().startsWith("WINDOWS")) {
                                Map<String, Integer> innermap = opTypeMap.get("WINDOWS");
                                if (innermap == null) {
                                    innermap = new HashMap<String, Integer>();
                                    innermap.put(useragent.getBrowser().toString(), 1);
                                    opTypeMap.put("WINDOWS", innermap);
                                } else {
                                    Integer hit = innermap.get(useragent.getBrowser().toString());
                                    if (hit == null) {
                                        innermap.put(useragent.getBrowser().toString(), 1);
                                    } else {
                                        hit = hit + 1;
                                        innermap.put(useragent.getBrowser().toString(), hit);
                                    }
                                }
                            }

                            if (useragent.getOperatingSystem().toString().startsWith("MAC_OS")) {
                                Map<String, Integer> innermap = opTypeMap.get("MAC_OS");
                                if (innermap == null) {
                                    innermap = new HashMap<String, Integer>();
                                    innermap.put(terminal, 1);
                                    opTypeMap.put("MAC_OS", innermap);
                                } else {
                                    Integer hit = innermap.get(terminal);
                                    if (hit == null) {
                                        innermap.put(terminal, 1);
                                    } else {
                                        hit = hit + 1;
                                        innermap.put(terminal, hit);
                                    }
                                }
                            }

                            if (useragent.getOperatingSystem().toString().startsWith("ANDROID")) {
                                terminal = T.catchUserAgentTermianl(dinfo.getUseragent());
                                if (terminal == null) {
                                    if (useragent.getOperatingSystem() == OperatingSystem.ANDROID) {
                                        terminal = "UNKNOW AND";
                                    }
                                }
                                if (terminal.toLowerCase().startsWith("zh-cn;")) terminal = terminal.substring(7);
                                Map<String, Integer> innermap = opTypeMap.get("ANDROID");
                                if (innermap == null) {
                                    innermap = new HashMap<String, Integer>();
                                    innermap.put(terminal, 1);
                                    opTypeMap.put("ANDROID", innermap);
                                } else {
                                    Integer hit = innermap.get(terminal);
                                    if (hit == null) {
                                        innermap.put(terminal, 1);
                                    } else {
                                        hit = hit + 1;
                                        innermap.put(terminal, hit);
                                    }
                                }
                            }
                        }
                    }
                }
                read.close();


                for (UserDatePK userDatePK : resultMapper.keySet()) {
                    UserAccessStatistics userAccessStatistics = resultMapper.get(userDatePK);
                    //userAccessStatistics.setExtension_content(JsonHelper.getJSONString(sortByValue(sortByValue(userAccessStatistics.fetchAll())), false));
                    userAccessStatisticsService.insert(userAccessStatistics);
                }
                System.out.println(opTypeMap);
                handle(opTypeMap);

            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
    }


    public static void handle(Map<String, Map<String, Integer>> resultMap) {

        UserBrandStatistics userBrandStatistics = new UserBrandStatistics();
        userBrandStatistics.setId(currentDate);
        userBrandStatistics.setCreated_at(new Date());

        if (resultMap != null) {

            int sum = 0;

            Map<String, Integer> windows = resultMap.get("WINDOWS");
            UserBrandDTO windowsUserBrandDTO = new UserBrandDTO();
            if (windows != null) {
                windowsUserBrandDTO.setBrand("WINDOWS");

                List<UserBrandSubDTO> userBrandSubDTOList = new ArrayList<UserBrandSubDTO>();

                Iterator<String> it = windows.keySet().iterator();
                UserBrandSubDTO userBrandSubDTO;

                int subSum = 0;
                while(it.hasNext()) {
                    userBrandSubDTO = new UserBrandSubDTO();
                    String key = it.next();
                    Integer value = windows.get(key);
                    userBrandSubDTO.setBrand(key);
                    userBrandSubDTO.setCount(value);
                    subSum += value;
                    userBrandSubDTOList.add(userBrandSubDTO);
                }

                for (UserBrandSubDTO dto : userBrandSubDTOList) {
                    dto.setRatio(dto.getCount() / (subSum / 100.0) + "%");
                }

                windowsUserBrandDTO.setCount(subSum);
                windowsUserBrandDTO.setDetail(userBrandSubDTOList);
                sum += subSum;
            }


            Map<String, Integer> android = resultMap.get("ANDROID");
            UserBrandDTO androidUserBrandDTO = new UserBrandDTO();
            if (android != null) {
                androidUserBrandDTO.setBrand("ANDROID");

                List<UserBrandSubDTO> userBrandSubDTOList = new ArrayList<UserBrandSubDTO>();

                Iterator<String> it = android.keySet().iterator();
                UserBrandSubDTO userBrandSubDTO;

                int subSum = 0;
                while(it.hasNext()) {
                    userBrandSubDTO = new UserBrandSubDTO();
                    String key = it.next();
                    Integer value = android.get(key);
                    userBrandSubDTO.setBrand(key);
                    userBrandSubDTO.setCount(value);
                    subSum += value;
                    userBrandSubDTOList.add(userBrandSubDTO);
                }

                for (UserBrandSubDTO dto : userBrandSubDTOList) {
                    dto.setRatio(dto.getCount() / (subSum / 100.0) + "%");
                }

                androidUserBrandDTO.setCount(subSum);
                androidUserBrandDTO.setDetail(userBrandSubDTOList);
                sum += subSum;
            }




            Map<String, Integer> macOS = resultMap.get("MAC_OS");
            UserBrandDTO maxosdUserBrandDTO = new UserBrandDTO();
            if (macOS != null) {
                maxosdUserBrandDTO.setBrand("MAC_OS");

                List<UserBrandSubDTO> userBrandSubDTOList = new ArrayList<UserBrandSubDTO>();

                Iterator<String> it = macOS.keySet().iterator();
                UserBrandSubDTO userBrandSubDTO;

                int subSum = 0;
                while(it.hasNext()) {
                    userBrandSubDTO = new UserBrandSubDTO();
                    String key = it.next();
                    Integer value = macOS.get(key);
                    userBrandSubDTO.setBrand(key);
                    userBrandSubDTO.setCount(value);
                    subSum += value;
                    userBrandSubDTOList.add(userBrandSubDTO);
                }
                for (UserBrandSubDTO dto : userBrandSubDTOList) {
                    dto.setRatio(dto.getCount() / (subSum / 100.0) + "%");
                }

                maxosdUserBrandDTO.setCount(subSum);
                maxosdUserBrandDTO.setDetail(userBrandSubDTOList);
                sum += subSum;
            }
            windowsUserBrandDTO.setRatio(maxosdUserBrandDTO.getCount()/ (sum / 100.0) + "%");
            //androidUserBrandDTO.setRatio(androidUserBrandDTO.getCount()/ (sum / 100.0) + "%");
            maxosdUserBrandDTO.setRatio(maxosdUserBrandDTO.getCount()/ (sum / 100.0) + "%");


            List<String> windowsUserBrandStatisticsDTOs = new ArrayList<String>();
            windowsUserBrandStatisticsDTOs.add(JsonHelper.getJSONString(windowsUserBrandDTO));
            userBrandStatistics.putInnerModels(windowsUserBrandStatisticsDTOs);

//            List<String> androidUserBrandStatisticsDTOs = new ArrayList<String>();
//            androidUserBrandStatisticsDTOs.add(JsonHelper.getJSONString(androidUserBrandDTO));
//            userBrandStatistics.putInnerModels(androidUserBrandStatisticsDTOs);

            filterAndroid(android, userBrandStatistics, sum);

            List<String> macosUserBrandStatisticsDTOs = new ArrayList<String>();
            macosUserBrandStatisticsDTOs.add(JsonHelper.getJSONString(maxosdUserBrandDTO));
            userBrandStatistics.putInnerModels(macosUserBrandStatisticsDTOs);
        }

        userBrandStatisticsService.insert(userBrandStatistics);
    }


    private static void filterAndroid(Map<String, Integer> android, UserBrandStatistics userBrandStatistics ,int totalSum) {

        int androidCount = android.size();

        UserBrandDTO coolpadUserBrandDTO = new UserBrandDTO();
        coolpadUserBrandDTO.setBrand("coolpad");
        List<UserBrandSubDTO> coolpadUserBrandSubDTOList = new ArrayList<UserBrandSubDTO>();

        UserBrandDTO huaweiUserBrandDTO = new UserBrandDTO();
        huaweiUserBrandDTO.setBrand("huawei");
        List<UserBrandSubDTO> huaweiUserBrandSubDTOList = new ArrayList<UserBrandSubDTO>();

        UserBrandDTO xiaomiUserBrandDTO = new UserBrandDTO();
        xiaomiUserBrandDTO.setBrand("xiaomi");
        List<UserBrandSubDTO> xiaomiUserBrandSubDTOList = new ArrayList<UserBrandSubDTO>();

        UserBrandDTO sumsangUserBrandDTO = new UserBrandDTO();
        sumsangUserBrandDTO.setBrand("sumsang");
        List<UserBrandSubDTO> sumsangUserBrandSubDTOList = new ArrayList<UserBrandSubDTO>();


        UserBrandDTO htcUserBrandDTO = new UserBrandDTO();
        htcUserBrandDTO.setBrand("htc");
        List<UserBrandSubDTO> htcUserBrandSubDTOList = new ArrayList<UserBrandSubDTO>();

        UserBrandDTO lenovoUserBrandDTO = new UserBrandDTO();
        lenovoUserBrandDTO.setBrand("lenovo");
        List<UserBrandSubDTO> lenovoUserBrandSubDTOList = new ArrayList<UserBrandSubDTO>();

        UserBrandDTO otherUserBrandDTO = new UserBrandDTO();
        otherUserBrandDTO.setBrand("other");
        List<UserBrandSubDTO> otherUserBrandSubDTOList = new ArrayList<UserBrandSubDTO>();

        Iterator<String> it = android.keySet().iterator();

        int coolpadSum = 0;
        int huaweiSum = 0;
        int xiaomiSum = 0;
        int sumsangSum = 0;
        int htcSum = 0;
        int lenovoSum = 0;
        int otherSum = 0;

        while(it.hasNext()) {
            UserBrandSubDTO userBrandSubDTO = new UserBrandSubDTO();
            String key = it.next();
            Integer value = android.get(key);
            userBrandSubDTO.setBrand(key);
            userBrandSubDTO.setCount(value);

            if (key.toLowerCase().startsWith("coolpad")) {
                coolpadUserBrandSubDTOList.add(userBrandSubDTO);
                coolpadSum += value;
            }
            else if (key.toLowerCase().startsWith("huawei") || key.toLowerCase().startsWith("h60")) {
                huaweiUserBrandSubDTOList.add(userBrandSubDTO);
                huaweiSum += value;
            }
            else if (key.toLowerCase().startsWith("mi")) {
                xiaomiUserBrandSubDTOList.add(userBrandSubDTO);
                xiaomiSum += value;
            }
            else if (key.toLowerCase().startsWith("gt") || key.toLowerCase().startsWith("sm")) {
                sumsangUserBrandSubDTOList.add(userBrandSubDTO);
                sumsangSum += value;
            }
            else if (key.toLowerCase().startsWith("htc")) {
                htcUserBrandSubDTOList.add(userBrandSubDTO);
                htcSum += value;
            }
            else if (key.toLowerCase().startsWith("lenovo")) {
                lenovoUserBrandSubDTOList.add(userBrandSubDTO);
                lenovoSum += value;
            }
            else {
                otherUserBrandSubDTOList.add(userBrandSubDTO);
                otherSum += value;
            }
        }

        coolpadUserBrandDTO.setCount(coolpadSum);
        coolpadUserBrandDTO.setDetail(coolpadUserBrandSubDTOList);
        coolpadUserBrandDTO.setRatio(coolpadSum / (totalSum / 100.0) + "%");

        huaweiUserBrandDTO.setCount(huaweiSum);
        huaweiUserBrandDTO.setDetail(huaweiUserBrandSubDTOList);
        huaweiUserBrandDTO.setRatio(huaweiSum / (totalSum / 100.0) + "%");

        xiaomiUserBrandDTO.setCount(xiaomiSum);
        xiaomiUserBrandDTO.setDetail(xiaomiUserBrandSubDTOList);
        xiaomiUserBrandDTO.setRatio(xiaomiSum / (totalSum / 100.0) + "%");

        sumsangUserBrandDTO.setCount(sumsangSum);
        sumsangUserBrandDTO.setDetail(sumsangUserBrandSubDTOList);
        sumsangUserBrandDTO.setRatio(sumsangSum / (totalSum / 100.0) + "%");

        htcUserBrandDTO.setCount(htcSum);
        htcUserBrandDTO.setDetail(htcUserBrandSubDTOList);
        htcUserBrandDTO.setRatio(htcSum / (totalSum / 100.0) + "%");

        lenovoUserBrandDTO.setCount(lenovoSum);
        lenovoUserBrandDTO.setDetail(lenovoUserBrandSubDTOList);
        lenovoUserBrandDTO.setRatio(lenovoSum / (totalSum / 100.0) + "%");

        otherUserBrandDTO.setCount(otherSum);
        otherUserBrandDTO.setDetail(otherUserBrandSubDTOList);
        otherUserBrandDTO.setRatio(otherSum / (totalSum / 100.0) + "%");


        for(UserBrandSubDTO userBrandSubDTO : coolpadUserBrandSubDTOList)  {
            userBrandSubDTO.setRatio(userBrandSubDTO.getCount()/(coolpadSum / 100.0) + "%");
        }
        for(UserBrandSubDTO userBrandSubDTO : huaweiUserBrandSubDTOList)  {
            userBrandSubDTO.setRatio(userBrandSubDTO.getCount()/(huaweiSum / 100.0) + "%");
        }
        for(UserBrandSubDTO userBrandSubDTO : xiaomiUserBrandSubDTOList)  {
            userBrandSubDTO.setRatio(userBrandSubDTO.getCount()/(xiaomiSum / 100.0) + "%");
        }
        for(UserBrandSubDTO userBrandSubDTO : sumsangUserBrandSubDTOList)  {
            userBrandSubDTO.setRatio(userBrandSubDTO.getCount()/(sumsangSum / 100.0) + "%");
        }
        for(UserBrandSubDTO userBrandSubDTO : htcUserBrandSubDTOList)  {
            userBrandSubDTO.setRatio(userBrandSubDTO.getCount()/(htcSum / 100.0) + "%");
        }
        for(UserBrandSubDTO userBrandSubDTO : lenovoUserBrandSubDTOList)  {
            userBrandSubDTO.setRatio(userBrandSubDTO.getCount()/(lenovoSum / 100.0) + "%");
        }
        for(UserBrandSubDTO userBrandSubDTO : otherUserBrandSubDTOList)  {
            userBrandSubDTO.setRatio(userBrandSubDTO.getCount()/(otherSum / 100.0) + "%");
        }


        List<String> coolpadUserBrandStatisticsDTOs = new ArrayList<String>();
        coolpadUserBrandStatisticsDTOs.add(JsonHelper.getJSONString(coolpadUserBrandDTO));
        userBrandStatistics.putInnerModels(coolpadUserBrandStatisticsDTOs);

        List<String> huaweiUserBrandStatisticsDTOs = new ArrayList<String>();
        huaweiUserBrandStatisticsDTOs.add(JsonHelper.getJSONString(huaweiUserBrandDTO));
        userBrandStatistics.putInnerModels(huaweiUserBrandStatisticsDTOs);

        List<String> xiaomiUserBrandStatisticsDTOs = new ArrayList<String>();
        xiaomiUserBrandStatisticsDTOs.add(JsonHelper.getJSONString(xiaomiUserBrandDTO));
        userBrandStatistics.putInnerModels(xiaomiUserBrandStatisticsDTOs);

        List<String> sumsangUserBrandStatisticsDTOs = new ArrayList<String>();
        sumsangUserBrandStatisticsDTOs.add(JsonHelper.getJSONString(sumsangUserBrandDTO));
        userBrandStatistics.putInnerModels(sumsangUserBrandStatisticsDTOs);

        List<String> htcUserBrandStatisticsDTOs = new ArrayList<String>();
        htcUserBrandStatisticsDTOs.add(JsonHelper.getJSONString(htcUserBrandDTO));
        userBrandStatistics.putInnerModels(htcUserBrandStatisticsDTOs);

        List<String> lenovoUserBrandStatisticsDTOs = new ArrayList<String>();
        lenovoUserBrandStatisticsDTOs.add(JsonHelper.getJSONString(lenovoUserBrandDTO));
        userBrandStatistics.putInnerModels(lenovoUserBrandStatisticsDTOs);

        List<String> otherUserBrandStatisticsDTOs = new ArrayList<String>();
        otherUserBrandStatisticsDTOs.add(JsonHelper.getJSONString(otherUserBrandDTO));
        userBrandStatistics.putInnerModels(otherUserBrandStatisticsDTOs);

    }


    enum MoblieBrand {
        COOLPAD,
        HUAWEI,
        LEONOVO,
        MI,
        SUMSANG,
        HTC,
        OTHER,
    }
}
