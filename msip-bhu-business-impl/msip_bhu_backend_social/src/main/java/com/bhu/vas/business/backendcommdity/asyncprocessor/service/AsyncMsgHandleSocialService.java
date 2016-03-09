package com.bhu.vas.business.backendcommdity.asyncprocessor.service;

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
    }
}
