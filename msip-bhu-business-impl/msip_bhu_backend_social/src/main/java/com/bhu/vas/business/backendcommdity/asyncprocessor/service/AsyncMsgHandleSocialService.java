package com.bhu.vas.business.backendcommdity.asyncprocessor.service;

import com.bhu.vas.business.asyn.spring.model.social.HandsetMeetDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.social.SocialHandsetMeetHashService;
import com.smartwork.msip.cores.helper.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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

        SocialHandsetMeetHashService.getInstance().handsetMeet(dto.getHd_mac_self(), dto.getHd_mac(), dto.getBssid(), dto.getMeet());
    }
}
