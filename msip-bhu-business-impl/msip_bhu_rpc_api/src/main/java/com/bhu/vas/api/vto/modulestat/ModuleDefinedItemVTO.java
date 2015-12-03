package com.bhu.vas.api.vto.modulestat;

import com.bhu.vas.api.rpc.vap.dto.module.ItemChannel;
import com.bhu.vas.api.rpc.vap.dto.module.ItemRedirect;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bluesand on 12/2/15.
 */
public class ModuleDefinedItemVTO implements Serializable {

    private String style;

    private String def;

    private int type;

    private String name;

    private List<ItemBrandVTO> brands;

    private List<ItemChannelVTO> channels;

    private List<ItemRedirectVTO> redirects;

    private List<ItemHttp404VTO> http404s;


    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ItemBrandVTO> getBrands() {
        return brands;
    }

    public void setBrands(List<ItemBrandVTO> brands) {
        this.brands = brands;
    }

    public List<ItemChannelVTO> getChannels() {
        return channels;
    }

    public void setChannels(List<ItemChannelVTO> channels) {
        this.channels = channels;
    }

    public List<ItemRedirectVTO> getRedirects() {
        return redirects;
    }

    public void setRedirects(List<ItemRedirectVTO> redirects) {
        this.redirects = redirects;
    }

    public List<ItemHttp404VTO> getHttp404s() {
        return http404s;
    }

    public void setHttp404s(List<ItemHttp404VTO> http404s) {
        this.http404s = http404s;
    }
}
