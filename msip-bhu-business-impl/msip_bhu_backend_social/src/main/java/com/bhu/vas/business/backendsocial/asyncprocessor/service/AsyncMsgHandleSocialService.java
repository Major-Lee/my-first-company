package com.bhu.vas.business.backendsocial.asyncprocessor.service;

import com.bhu.vas.business.asyn.spring.model.social.HandsetMeetDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.social.SocialStorageFacadeService;
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
        String hd_macs = dto.getHd_macs().toLowerCase();
        String uid = dto.getUid();
        String bssid = dto.getBssid().toLowerCase();
        String[] list = hd_macs.split(",");
        if (list != null && list.length > 0) {
            for (String mac : list) {
//                logger.info(String.format("AsyncMsgHandleSocialService handsetmeet mac[%s] message[%s]", mac, message));
                SocialStorageFacadeService.handsetMeet(uid, mac.trim(), bssid, dto.getMeet());
            }
        }


    }
}
