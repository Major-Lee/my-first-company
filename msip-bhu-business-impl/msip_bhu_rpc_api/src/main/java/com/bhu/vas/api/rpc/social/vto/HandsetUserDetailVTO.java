package com.bhu.vas.api.rpc.social.vto;

import com.bhu.vas.api.dto.social.HandsetMeetDTO;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bluesand on 3/8/16.
 */
@SuppressWarnings("serial")
public class HandsetUserDetailVTO implements Serializable {

    private HandsetUserVTO handset;

    private List<HandsetMeetDTO> meets;

    public HandsetUserVTO getHandset() {
        return handset;
    }

    public void setHandset(HandsetUserVTO handset) {
        this.handset = handset;
    }

    public List<HandsetMeetDTO> getMeets() {
        return meets;
    }

    public void setMeets(List<HandsetMeetDTO> meets) {
        this.meets = meets;
    }
}
