package com.bhu.vas.rpc.service.device;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.dto.VapModeDefined;
import com.bhu.vas.api.rpc.vap.iservice.IVapRpcService;
import com.bhu.vas.rpc.facade.VapFacadeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by bluesand on 5/26/15.
 */
@Service("vapRpcService")
public class VapRpcService  implements IVapRpcService{
    private final Logger logger = LoggerFactory.getLogger(VapRpcService.class);

    @Resource
    private VapFacadeService vapFacadeService;

    @Override
    public void urlView(String key, String field) {
        logger.info(String.format("checkAcc with key[%s] field[%s]", key, field));

        if (vailidateVapMode(key, field)) {
            vapFacadeService.urlView(key.toLowerCase(), field);
        }


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
