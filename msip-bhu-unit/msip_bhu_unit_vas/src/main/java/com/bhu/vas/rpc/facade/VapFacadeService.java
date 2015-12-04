package com.bhu.vas.rpc.facade;

import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.rpc.task.model.VasModuleCmdDefined;
import com.bhu.vas.api.rpc.task.model.pk.VasModuleCmdPK;
import com.bhu.vas.api.vto.modulestat.*;
import com.bhu.vas.business.bucache.redis.serviceimpl.modulestat.WifiDeviceModuleStatService;
import com.bhu.vas.business.ds.task.service.VasModuleCmdDefinedService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.VapModeDefined;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.vap.dto.VapModeUrlViewCountDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.VapModeHashService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by bluesand on 5/26/15.
 */
@Service
public class VapFacadeService {

    @Resource
    private VasModuleCmdDefinedService vasModuleCmdDefinedService;

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


    public RpcResponseDTO<List<ModuleDefinedVTO>> fetchDayStat(int uid) {

        //TODO(bluesnad): findall???
        List<VasModuleCmdDefined> vasModuleCmdDefineds = vasModuleCmdDefinedService.findAll();

        List<ModuleDefinedVTO> dtos = new ArrayList<ModuleDefinedVTO>();

        ModuleDefinedVTO dto = null;
        for (VasModuleCmdDefined vasModuleCmdDefined : vasModuleCmdDefineds) {
            dto = new ModuleDefinedVTO();
            dto.setStyle(vasModuleCmdDefined.getStyle());
            dto.setMemo(vasModuleCmdDefined.getMemo());
            dto.setItem(buildModuleDefinedItemVTO(vasModuleCmdDefined.getStyle()));
            dtos.add(dto);
        }

        return RpcResponseDTOBuilder.builderSuccessRpcResponse(dtos);
    }


    public RpcResponseDTO<ModuleDefinedItemVTO> fetchStatDetail(int uid, String style) {

        return RpcResponseDTOBuilder.builderSuccessRpcResponse(buildModuleDefinedItemVTO(style));

    }


    private ModuleDefinedItemVTO buildModuleDefinedItemVTO(String style) {
        VasModuleCmdPK pk = new VasModuleCmdPK();
        pk.setStyle(style);
        pk.setDref(OperationDS.DS_Http_VapModuleCMD_Start.getRef());

        VasModuleCmdDefined vasModuleCmdDefined = vasModuleCmdDefinedService.getById(pk);

        ModuleDefinedItemVTO vto = new ModuleDefinedItemVTO();

        vto.setStyle(style);

        vto.setDef(OperationDS.DS_Http_VapModuleCMD_Start.getRef());


        List<ItemBrandVTO> brands = new ArrayList<ItemBrandVTO>();

        List<ItemChannelVTO> channels = new ArrayList<ItemChannelVTO>();

        List<ItemRedirectVTO> redirects = new ArrayList<ItemRedirectVTO>();

        List<ItemHttp404VTO> http404s = new ArrayList<ItemHttp404VTO>();

        //1: 404, 2:重定向, 3: 品牌展示, 4:渠道号

        Map<String,Long> dayRets = WifiDeviceModuleStatService.getInstance().hgetModuleStatsWithKey(generateDStyleKey(style));
        Map<String,Long> monthRets = WifiDeviceModuleStatService.getInstance().hgetModuleStatsWithKey(generateMStyleKey(style));

        for (String key: dayRets.keySet()) {

            Long dcount = dayRets.get(key);
            Long mcount = monthRets.get(key);
            int index = key.indexOf(".");
            int lastIndex = key.lastIndexOf(".");

            int type = Integer.parseInt(key.substring(index + 1, lastIndex));
            int sequence = Integer.parseInt(key.substring(lastIndex + 1));

            if (type ==1) {
                ItemHttp404VTO item = new ItemHttp404VTO();
                item.setSequence(sequence);
                item.setDcount(dcount);
                item.setMcount(mcount);
                http404s.add(item);

            } else if (type == 2) {
                ItemRedirectVTO item = new ItemRedirectVTO();
                item.setSequence(sequence);
                item.setDcount(dcount);
                item.setMcount(mcount);
                redirects.add(item);

            } else if (type == 3) {
                ItemBrandVTO item = new ItemBrandVTO();
                item.setSequence(sequence);
                item.setDcount(dcount);
                item.setMcount(mcount);
                brands.add(item);

            } else if (type == 4) {
                ItemChannelVTO item = new ItemChannelVTO();
                item.setSequence(sequence);
                item.setDcount(dcount);
                item.setMcount(mcount);
                channels.add(item);
            }

        }

        vto.setHttp404s(http404s);
        vto.setRedirects(redirects);
        vto.setBrands(brands);
        vto.setChannels(channels);

        return vto;
    }




    private String generateMStyleKey(String style) {
        return style + "." + DateTimeHelper.formatDate(new Date(System.currentTimeMillis()), "yyyyMM");
    }

    private String generateDStyleKey(String style) {
        return style + "." + DateTimeHelper.formatDate(new Date(System.currentTimeMillis()), "yyyyMMdd");
    }

}
