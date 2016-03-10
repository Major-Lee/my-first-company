package com.bhu.vas.business.backendsocial.asyncprocessor.service;

import com.bhu.vas.business.asyn.spring.model.social.HandsetMeetDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.social.SocialHandsetMeetHashService;
import com.smartwork.msip.cores.helper.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by bluesand on 3/9/16.
 */

@Service
public class AsyncMsgHandleSocialService {
    private final Logger logger = LoggerFactory.getLogger(AsyncMsgHandleSocialService.class);

    /**
     * 终端相遇
     * @param message
     */
    public void handsetMeet(String message) {
        logger.info(String.format("AsyncMsgHandleSocialService handsetMeet message[%s]", message));

        HandsetMeetDTO dto = JsonHelper.getDTO(message, HandsetMeetDTO.class);

        String hd_macs = dto.getHd_macs();
        String hd_mac = dto.getHd_mac();
        String bssid = dto.getBssid();
       String[] list = hd_macs.split(",");
        if (list != null && list.length > 0) {
            for (String mac : list) {
                SocialHandsetMeetHashService.getInstance().handsetMeet(hd_mac, mac, bssid, JsonHelper.getJSONString(dto.getMeet()));
            }
        }


    }
}
