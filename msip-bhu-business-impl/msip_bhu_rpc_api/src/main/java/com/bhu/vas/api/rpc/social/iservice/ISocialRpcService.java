package com.bhu.vas.api.rpc.social.iservice;

import com.bhu.vas.api.rpc.social.model.WifiComment;

/**
 * Created by bluesand on 3/2/16.
 */
public interface ISocialRpcService {

    /**
     * 评论
     * @param uid
     * @param bssid
     * @param message
     * @return
     */
    boolean comment(long uid, String bssid, String message);
}
