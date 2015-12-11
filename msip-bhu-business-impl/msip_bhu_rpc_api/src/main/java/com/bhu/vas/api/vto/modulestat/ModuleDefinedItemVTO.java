package com.bhu.vas.api.vto.modulestat;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bluesand on 12/2/15.
 */
public class ModuleDefinedItemVTO implements Serializable {

    private String style;

    private String def;

    private Http404VTO http404;

    private RedirectVTO redirect;

    private BrandVTO brand;

    private ChannelVTO channel;


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


    public BrandVTO getBrand() {
        return brand;
    }

    public void setBrand(BrandVTO brand) {
        this.brand = brand;
    }

    public ChannelVTO getChannel() {
        return channel;
    }

    public void setChannel(ChannelVTO channel) {
        this.channel = channel;
    }

    public RedirectVTO getRedirect() {
        return redirect;
    }

    public void setRedirect(RedirectVTO redirect) {
        this.redirect = redirect;
    }

    public Http404VTO getHttp404() {
        return http404;
    }

    public void setHttp404(Http404VTO http404) {
        this.http404 = http404;
    }
}
