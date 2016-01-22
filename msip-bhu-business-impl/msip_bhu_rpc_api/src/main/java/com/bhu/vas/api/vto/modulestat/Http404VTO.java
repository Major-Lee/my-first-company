package com.bhu.vas.api.vto.modulestat;

import java.util.List;

/**
 * Created by bluesand on 12/11/15.
 */
@SuppressWarnings("serial")
public class Http404VTO extends BaseVTO {

    private List<ItemHttp404VTO> items;

    public List<ItemHttp404VTO> getItems() {
        return items;
    }

    public void setItems(List<ItemHttp404VTO> items) {
        this.items = items;
    }
}
