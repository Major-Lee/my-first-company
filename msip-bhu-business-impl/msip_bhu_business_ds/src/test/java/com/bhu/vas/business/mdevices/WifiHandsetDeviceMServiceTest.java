package com.bhu.vas.business.mdevices;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.mdto.WifiHandsetDeviceItemDetailMDTO;
import com.bhu.vas.api.mdto.WifiHandsetDeviceItemLogMDTO;
import com.bhu.vas.api.vto.URouterHdTimeLineVTO;
import com.bhu.vas.business.ds.device.dao.WifiHandsetDeviceRelationMDao;
import com.bhu.vas.business.ds.device.mdto.WifiHandsetDeviceRelationMDTO;
import com.bhu.vas.business.ds.device.service.WifiHandsetDeviceRelationMService;
import com.smartwork.msip.cores.helper.DateTimeExtHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.localunit.BaseTest;

/**
 * Created by bluesand on 9/2/15.
 */
public class WifiHandsetDeviceMServiceTest extends BaseTest{

    private static final long IGNORE_LOGIN_TIME_SPACE = 15 * 60 * 1000L;

    private static final int DAY_TIME_MILLION_SECOND = 24 * 3600 * 1000;

    private static final String HANDSET_LOGIN_TYPE = "login";
    private static final String HANDSET_LOGOUT_TYPE = "logout";

    @Resource
    private WifiHandsetDeviceRelationMService wifiHandsetDeviceRelationMService;


    @Resource
    private WifiHandsetDeviceRelationMDao wifiHandsetDeviceRelationMDao;

    @Test
    public void test() {


//        List<WifiHandsetDeviceRelationMDTO> results = wifiHandsetDeviceRelationMDao.findAll();



//        for (WifiHandsetDeviceRelationMDTO dto : results) {

            String wifiId = "84:82:f4:1c:e8:b8";
            String mac = "d0:67:e5:40:89:a2";

//            wifiId = dto.getWifiId();
//            mac = dto.getHandsetId();


            WifiHandsetDeviceRelationMDTO wifiHandsetDeviceRelationMDTO =
                    wifiHandsetDeviceRelationMService.getRelation(wifiId, mac);

            List<String> weeks = DateTimeExtHelper.getSevenDateOfWeek();

            List<URouterHdTimeLineVTO> uRouterHdTimeLineVTOList = new ArrayList<URouterHdTimeLineVTO>();


            //集合中只有七天的在线记录
            for (String key : weeks) {
                URouterHdTimeLineVTO uRouterHdTimeLineVTO = new URouterHdTimeLineVTO();
                uRouterHdTimeLineVTO.setDate(key);
                List<WifiHandsetDeviceItemDetailMDTO> mdtos = new ArrayList<WifiHandsetDeviceItemDetailMDTO>();
                uRouterHdTimeLineVTO.setDetail(mdtos);
                uRouterHdTimeLineVTOList.add(uRouterHdTimeLineVTO);
            }

            List<WifiHandsetDeviceItemLogMDTO> logs = wifiHandsetDeviceRelationMDTO.getLogs();

            try {
                getLogs(uRouterHdTimeLineVTOList, logs);
            } catch (Exception e) {


                System.out.println("wifiId[" + wifiId + "]---handsetId[" + mac + "]");
                System.out.println("db.getCollection('t_wifi_handset_relation').find({\"wifiId\":\""+wifiId+"\",\"handsetId\":\"" + mac +"\"})");
                e.printStackTrace();
                System.exit(0);
            }
            System.out.println(JsonHelper.getJSONString(uRouterHdTimeLineVTOList));
//        }
    }




    /**
     *
     * @param vtos
     * @param logs
     * @return
     */
    private List<URouterHdTimeLineVTO> getLogs(List<URouterHdTimeLineVTO> vtos, List<WifiHandsetDeviceItemLogMDTO> logs) {
        long currentTime = System.currentTimeMillis();
        long currentZeroTime = getDateZeroTime(new Date()).getTime();

        if (logs != null) {
            //WifiHandsetDeviceItemDetailMDTO dto = null;
            //List<WifiHandsetDeviceItemDetailMDTO> mdtos = null;
            String last_type = null;
            long last_ts = 0;

            for (WifiHandsetDeviceItemLogMDTO log : logs) {

                long ts = log.getTs();
                long space = currentZeroTime - ts;
                int offset = (int) (space/(DAY_TIME_MILLION_SECOND));
                if (space < 0) {
                    offset = -1;
                }
                if (offset > 5) {
                    //offset = 5; //7天外的数据直接忽略
                    if (HANDSET_LOGOUT_TYPE.equals(last_type)) { //如果最后一天数据是隔天数据

                        //long end_space = currentTime - last_ts;
                        //int end_offset = (int) (end_space/(DAY_TIME_MILLION_SECOND));

//                        for (int i=0 ;i <=6 ;i ++) {
//                            URouterHdTimeLineVTO vto = vtos.get(i);
//                            List<WifiHandsetDeviceItemDetailMDTO> mdtos_ = vto.getDetail();
//                            if ( i ==0 || (mdtos_ != null && !mdtos_.isEmpty())) {
//                                continue;
//                            } else {
//                                URouterHdTimeLineVTO last_vto = vtos.get(i-1);
//                                List<WifiHandsetDeviceItemDetailMDTO> last_mdtos = last_vto.getDetail();
//                                WifiHandsetDeviceItemDetailMDTO last_dto = last_mdtos.get(last_mdtos.size() - 1);
//                                last_dto.setLogin_at(getDateZeroTime(new Date(last_ts - end_offset * DAY_TIME_MILLION_SECOND )).getTime());
//                                last_vto.setDetail(last_mdtos);
//                                break;
//                            }
//
//                        }

//                        URouterHdTimeLineVTO vto = vtos.get(6);
//                        List<WifiHandsetDeviceItemDetailMDTO> mdtos_ = vto.getDetail();
//                        if (mdtos_ != null && !mdtos_.isEmpty()) {
//                            WifiHandsetDeviceItemDetailMDTO dto_ = mdtos_.get(mdtos_.size()-1);
//                            dto_.setLogin_at(getDateZeroTime(new Date(ts)).getTime());
//                            vto.setDetail(mdtos_);
//                        }


                        for (int i=6 ; i >=0 ;i--) {
                            URouterHdTimeLineVTO vto = vtos.get(i);
                            List<WifiHandsetDeviceItemDetailMDTO> mdtos_ = vto.getDetail();
                            if(mdtos_ != null && !mdtos_.isEmpty()) {
                                URouterHdTimeLineVTO last_vto = vtos.get(i);
                                List<WifiHandsetDeviceItemDetailMDTO> last_mdtos = last_vto.getDetail();
                                System.out.println("=====[" + i +  "] ==>" +  last_mdtos.size());
                                WifiHandsetDeviceItemDetailMDTO last_dto = last_mdtos.get(last_mdtos.size() - 1);
                                System.out.println("====" + last_dto);

                                last_dto.setLogin_at(getDateZeroTime(DateTimeHelper.parseDate(last_vto.getDate(), DateTimeHelper.shortDateFormat)).getTime());
                                last_vto.setDetail(last_mdtos);
                                break;
                            }
                        }

                    }

                    break;
                }

                String type = log.getType();
                long rx_bytes = log.getRx_bytes();

                boolean online = false;
                //处理分割记录
                if (HANDSET_LOGIN_TYPE.equals(type)) {
                    online = true;
                }


//				logger.info("offset[" + offset + "],type[" + type + "],last_type[" + last_type+"],ts[" + ts + "]");
//				logger.info("spacetime[" + (last_ts -ts) + "]");

                if (last_type == null) { //最新一条记录

                    filterDay(ts, currentTime, type,last_type, rx_bytes, vtos, offset, true, online);

                } else { //第二条数据开始

                    if (HANDSET_LOGOUT_TYPE.equals(type) && HANDSET_LOGIN_TYPE.equals(last_type)) { //新的的登出记录
                        filterDay(ts, last_ts, type, last_type, rx_bytes,  vtos, offset, false, online);
                    }

                    if (HANDSET_LOGOUT_TYPE.equals(type) && HANDSET_LOGOUT_TYPE.equals(last_type)) { //连续两条登出
                        //忽略记录
                        //模拟一条登入的记录
//						last_type = "logout";
                        type = HANDSET_LOGIN_TYPE;
                        filterDay(ts, last_ts, type, last_type,rx_bytes,  vtos, offset, false, online);

                        last_type = HANDSET_LOGIN_TYPE;
                        type = HANDSET_LOGOUT_TYPE;
                        filterDay(last_ts, last_ts, type, last_type,rx_bytes,  vtos, offset, false, online);

                    }

                    if (HANDSET_LOGIN_TYPE.equals(type) && HANDSET_LOGOUT_TYPE.equals(last_type)) {

                        filterDay(ts, last_ts, type, last_type, rx_bytes,vtos, offset, false, online);
                    }
                    if (HANDSET_LOGIN_TYPE.equals(type) && HANDSET_LOGIN_TYPE.equals(last_type)) {
                        //忽略记录
                        //先模拟一条当前登出的记录
                        type = HANDSET_LOGOUT_TYPE;
                        filterDay(last_ts, last_ts, type, last_type, rx_bytes,vtos, getOffSet(ts, currentZeroTime), false, online);

                        last_type = HANDSET_LOGOUT_TYPE;
                        type = HANDSET_LOGIN_TYPE;
//						last_ts = ts;
                        filterDay(ts, last_ts, type, last_type, rx_bytes,vtos, getOffSet(ts, currentZeroTime), false, online);

                    }

                }

                last_type = type;
                last_ts = ts;

            }


        }

        return vtos;
    }


    private int getOffSet(long ts, long currentZeroTime) {
        long space = currentZeroTime - ts;
        int offset = (int) (space)/(DAY_TIME_MILLION_SECOND);
        if (space < 0) {
            offset = -1;
        }
        if (offset > 5) {
            offset = 5;
        }
        return offset;
    }

    /**
     *
     * 解析终端登入登出日志，每条日志只有时间戳和类型(login/logout)
     * 1.正常流程 login -> logout -> login -> logout -> ...
     * 2.出现跨天的情况
     * 3.15分钟之内的登入登出合并
     *
     *
     *
     * @param ts     时间戳
     * @param last_ts 上一次
     * @param type   登入还是登出
     * @param last_type 上一次
     * @param vtos   结果
     * @param offset 记录是七天的第几天
     * @param first last_type == null 时候
     */
    private void filterDay(long ts, long last_ts, String type, String last_type, long rx_bytes,List<URouterHdTimeLineVTO> vtos,
                           int offset, boolean first, boolean online) {

        //如果当前在线，当前时间与上一次登录时间相隔数天
        String tsZeroStr = DateTimeHelper.formatDate(new Date(ts), DateTimeHelper.shortDateFormat);

        long ts_zero_at =  DateTimeHelper.getDateLongTime(tsZeroStr, DateTimeHelper.shortDateFormat);

        long spaceTime = last_ts - ts_zero_at;
        //获取终端连续在线的时间段j天
        int j = (int)spaceTime / (DAY_TIME_MILLION_SECOND);
        if (j >= 6) {
            j = 6;
        }

        logger.info("ts["+ts+"],last_ts["+last_ts+"], type["+type+"], last_type["+last_type+"], offset["+offset+"],j["+j+"]");

            if (j == 0) { //当天记录
                URouterHdTimeLineVTO vto = vtos.get(offset + 1); //更新logs
                List<WifiHandsetDeviceItemDetailMDTO> mdtos = vto.getDetail();
                if (mdtos == null) {
                    mdtos = new ArrayList<WifiHandsetDeviceItemDetailMDTO>();
                }

                WifiHandsetDeviceItemDetailMDTO dto = null;

//				if (type.equals("login") && last_type.equals("logout")) { //正常流程
                if (HANDSET_LOGIN_TYPE.equals(type)) { //正常流程
                    if (first) { // last_type == null
                        dto = new WifiHandsetDeviceItemDetailMDTO();
                        dto.setLogin_at(ts);
                        dto.setLogout_at(0);
                        dto.setRx_bytes(0);
                        mdtos.add(dto);
                    } else {
                        dto = mdtos.get(mdtos.size() - 1);
                        dto.setLogin_at(ts);
                    }
                }

                if (HANDSET_LOGOUT_TYPE.equals(type)) { //正常流程

                    if (first) { //last_type == null
                        dto = new WifiHandsetDeviceItemDetailMDTO();
                        dto.setLogout_at(ts);
                        dto.setRx_bytes(rx_bytes);
                        mdtos.add(dto);
                    } else {
                        if (last_ts - ts < IGNORE_LOGIN_TIME_SPACE) { //小于15分钟的记录
                            //忽略操作
//							logger.info("ignore 15 min" + (ts - last_ts));
                            System.out.println("=========" + mdtos.size());
                            if (mdtos.size()  == 0)
                                return;

                            dto = mdtos.get(mdtos.size() - 1);
                            if (!online) { //如果不在线的话就累加
                                dto.setRx_bytes(dto.getRx_bytes() + rx_bytes);
                            } else {
                                dto.setRx_bytes(0);
                            }
                        } else {
                            dto = new WifiHandsetDeviceItemDetailMDTO();
                            dto.setLogout_at(ts);
                            dto.setRx_bytes(rx_bytes);
                            mdtos.add(dto);
                        }
                    }
                }
//				logger.info("[mdtos]" + mdtos.size());
                vto.setDetail(mdtos);

            }

            if (j > 0) {

                WifiHandsetDeviceItemDetailMDTO dto = null;

                if (first) { //隔天的第一条记录
                    URouterHdTimeLineVTO vto = vtos.get(offset + 1); //更新logs
                    List<WifiHandsetDeviceItemDetailMDTO> mdtos = vto.getDetail();
                    if (mdtos == null) {
                        mdtos = new ArrayList<WifiHandsetDeviceItemDetailMDTO>();
                    }

                    if (HANDSET_LOGOUT_TYPE.equals(type)) {
                        dto = new WifiHandsetDeviceItemDetailMDTO();
                        dto.setLogout_at(ts);
                        mdtos.add(dto);
                    }

                    if (HANDSET_LOGIN_TYPE.equals(type)) {
                        //忽略
                        //忽略
                        dto = new WifiHandsetDeviceItemDetailMDTO();
                        dto.setLogin_at(ts);
                        mdtos.add(dto);
                    }

                }

                if (HANDSET_LOGOUT_TYPE.equals(type) && HANDSET_LOGIN_TYPE.equals(last_type)) { //如果上一次正常退出

                    //有隔天记录的拆分第一条记录 >>>
                    URouterHdTimeLineVTO vto = vtos.get(offset + 1); //更新logs
//					logger.info("date===date[" + vto.getDate() + "]");
//					List<WifiHandsetDeviceItemDetailMDTO> mdtos = vto.getLogs(); //
                    List<WifiHandsetDeviceItemDetailMDTO> mdtos = vto.getDetail();
                    if (mdtos == null) {
                        mdtos = new ArrayList<WifiHandsetDeviceItemDetailMDTO>();
                    }

                    dto = new WifiHandsetDeviceItemDetailMDTO();
                    dto.setLogout_at(ts);
                    mdtos.add(dto);
                    vto.setDetail(mdtos);
                }


                //隔天记录
                //补齐上一天的最后一条login记录为00:00:00，当天的第一条记录11:59:59
                if (HANDSET_LOGIN_TYPE.equals(type) && HANDSET_LOGOUT_TYPE.equals(last_type)) { ////隔天仍在线

                    //如果j >1 的时候 offset >= 0
                    for (int i = 1; i< j + 1 ; i++) {
                        // >>> j == 1
                        logger.info("iiiii===" + i);
                        URouterHdTimeLineVTO vto_ = vtos.get(offset - (j - i ));
                        List<WifiHandsetDeviceItemDetailMDTO> mdtos_ = vto_.getDetail();
                        if (mdtos_ == null) {
                            mdtos_ = new ArrayList<WifiHandsetDeviceItemDetailMDTO>();
                        }
                        WifiHandsetDeviceItemDetailMDTO dto_ = mdtos_.get(mdtos_.size() - 1);
                        long login_at_zero = DateTimeHelper.parseDate(vto_.getDate(), DateTimeHelper.shortDateFormat).getTime();
                        dto_.setLogin_at(login_at_zero); //补齐零点登入
                        vto_.setDetail(mdtos_);
                        // <<<

                        // >>>
                        URouterHdTimeLineVTO vto = vtos.get(offset - (j - i ) + 1);
                        List<WifiHandsetDeviceItemDetailMDTO> mdtos = vto.getDetail();

                        dto = new WifiHandsetDeviceItemDetailMDTO();
                        dto.setLogout_at(login_at_zero - 1);  //补齐零点登出

//						logger.info("set ts ==" + ts + ",i===" + i);
                        if (i == j) {
                            dto.setLogin_at(ts);  //如果最后一次的话添加一个登录时间
//							logger.info("set login_ ts ==" + ts);
                        }
//                        if (offset + 1 + i >= 6)  {
//                            dto.setLogin_at(ts);  //如果最后一次的话添加一个登录时间
//                            mdtos.add(dto);
//                            vto.setDetail(mdtos);
//                            break;
//                        }


                        mdtos.add(dto);
                        vto.setDetail(mdtos);
                        // <<< j == 1
                    }

                }

                //vto.setLogs(mdtos);

                //有隔天记录的拆分第一条记录 <<<
            }

    }


    private Date getDateZeroTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        //时分秒（毫秒数）
        long millisecond = hour*60*60*1000 + minute*60*1000 + second*1000;
        //凌晨00:00:00
        cal.setTimeInMillis(cal.getTimeInMillis() - millisecond);

        return cal.getTime();
    }



}
