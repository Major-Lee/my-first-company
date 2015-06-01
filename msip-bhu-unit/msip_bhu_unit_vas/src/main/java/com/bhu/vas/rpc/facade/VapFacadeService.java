package com.bhu.vas.rpc.facade;

import com.bhu.vas.api.dto.VapModeDefined;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.vap.dto.VapModeUrlViewCountDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.VapModeHashService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by bluesand on 5/26/15.
 */
@Service
public class VapFacadeService {

    public RpcResponseDTO<VapModeUrlViewCountDTO> urlView(String key, String field) {
        VapModeUrlViewCountDTO vapModeUrlViewCountDTO = new VapModeUrlViewCountDTO();
        if (vailidateVapMode(key, field)) {
            long count = VapModeHashService.getInstance().incrStatistics(key, field, 1);
            vapModeUrlViewCountDTO.setCount(count);
            long totalCount = VapModeHashService.getInstance().getTotalCountKey(key);
            vapModeUrlViewCountDTO.setTotal_count(totalCount);
        }
        return RpcResponseDTOBuilder.builderSuccessRpcResponse(vapModeUrlViewCountDTO);
    }

    /**
     * 验证url传过来的key
     * @param key
     * @param field
     * @return
     */
    private boolean vailidateVapMode(String key, String field) {
        if (key.toLowerCase().equals(VapModeDefined.VapMode.HtmlInjectAdv.getKey())) {
            if (field.toLowerCase().equals(VapModeDefined.HtmlInjectAdv.STYLE000.getStyle())
                    || field.toLowerCase().equals(VapModeDefined.HtmlInjectAdv.STYLE001.getStyle())) {
                return true;
            }
        } else if (key.toLowerCase().equals(VapModeDefined.VapMode.HtmlInject404.getKey())) {
            if (field.toLowerCase().equals(VapModeDefined.HtmlInject404.STYLE000.getStyle())
                    || field.toLowerCase().equals(VapModeDefined.HtmlInject404.STYLE001.getStyle())) {
                return true;
            }
        } else if (key.toLowerCase().equals(VapModeDefined.VapMode.HtmlPortal.getKey())) {
            if (field.toLowerCase().equals(VapModeDefined.HtmlPortal.STYLE000.getStyle())
                    || field.toLowerCase().equals(VapModeDefined.HtmlPortal.STYLE001.getStyle())) {
                return true;
            }
        } else if (key.toLowerCase().equals(VapModeDefined.VapMode.HtmlRedirect.getKey())) {
            if (field.toLowerCase().equals(VapModeDefined.HtmlRedirect.STYLE000.getStyle())
                    || field.toLowerCase().equals(VapModeDefined.HtmlRedirect.STYLE001.getStyle())) {
                return true;
            }
        } else {
            return false;
        }
        return false;
    }
}
