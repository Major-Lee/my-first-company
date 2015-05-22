package com.bhu.vas.di.op;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.bitwalker.useragentutils.OperatingSystem;
import nl.bitwalker.useragentutils.UserAgent;

/**
 * 统计操作系统分布图 根据mac地址区别重复数据 进行增加计数
 * 每个操作系统各个型号数量分布
 *
 * @author Edmond
 */
public class UserAccessStatisticAnaOp {
    public static void main(String[] argv) throws IOException {
        //操作系统
        //Map<String,Integer> opMap = new HashMap<String,Integer>();
        //设备型号 系统 型号 数量
        Map<String, Map<String, Integer>> opTypeMap = new HashMap<String, Map<String, Integer>>();
        //出现过的设备mac地址
        Set<String> macSet = new HashSet<String>();

        InputStreamReader read = new InputStreamReader(new FileInputStream("/Users/bluesand/Documents/logfile.log"), "gbk");//考虑到编码格式
        BufferedReader bufferedReader = new BufferedReader(read);
        String lineTxt = null;
        int total = 0;
        int debugCount = 0;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            //System.out.println(debugCount++);
            if (!lineTxt.startsWith("0001") || !lineTxt.endsWith("0000")) continue;
            DpiInfo dinfo = DpiInfo.fromTextLine(lineTxt);
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
                String terminal = T.catchUserAgentTermianl(dinfo.getUseragent());
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
                if (terminal.toLowerCase().startsWith("zh-cn;")) terminal = terminal.substring(7);
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
                            if (terminal.equals("rv:37.0) Gecko")) {
                                System.out.println(">>>" + lineTxt);
                            }
                            if (terminal.equals("zh_CN;S1;netType")) {
                                System.out.println(">>>" + lineTxt);
                            }
                            innermap.put(terminal, 1);
                        } else {
                            hit = hit + 1;
                            innermap.put(terminal, hit);
                        }
                    }
                }

                if (useragent.getOperatingSystem().toString().startsWith("ANDROID")) {
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
        bufferedReader.close();
        System.out.println(total);

        System.out.println(opTypeMap);
    }

    /**
     * 根据终端的名称的前缀 匹配Handset枚举进行分组并计数
     *
     * @return
     */
    public static List<String> reArrangeMap() {
        return null;
    }
}
