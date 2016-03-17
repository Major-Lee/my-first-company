package com.bhu.vas.business.bucache.redis.serviceimpl.social;

import com.bhu.vas.api.dto.social.SocialRemarkDTO;
import com.smartwork.msip.cores.helper.JsonHelper;

/**
 * Created by bluesand on 3/17/16.
 */
public class SocialCareFacadeService {


    /**
     * 关心
     * @param uid
     * @param hd_mac
     * @param dto
     */
    public static void  care(long uid, String hd_mac, SocialRemarkDTO dto){
        SocialCareHashService.getInstance().care(uid, hd_mac, JsonHelper.getJSONString(dto));
    }


    /**
     * 获取用户备注终端昵称
     * @param uid
     * @param hd_mac
     * @return
     */
    public static String getNick(long uid, String hd_mac){
        String dtoString  = SocialCareHashService.getInstance().getCareValue(uid, hd_mac);
        SocialRemarkDTO  dto =  JsonHelper.getDTO(dtoString, SocialRemarkDTO.class);
        String nick = "";
        if (dto != null) {
            nick = dto.getNick();
        }
        return nick;
    }

    /**
     * 修改用户备注终端昵称
     * @param uid
     * @param hd_mac
     * @param nick
     */
    public static void moidfyNick(long uid, String hd_mac, String nick) {
        String dtoString  = SocialCareHashService.getInstance().getCareValue(uid, hd_mac);
        SocialRemarkDTO  dto =  JsonHelper.getDTO(dtoString, SocialRemarkDTO.class);
        if (dto == null) {
            dto = new SocialRemarkDTO();
        }
        dto.setNick(nick);
        SocialCareHashService.getInstance().care(uid, hd_mac, JsonHelper.getJSONString(dto));

    }
}
