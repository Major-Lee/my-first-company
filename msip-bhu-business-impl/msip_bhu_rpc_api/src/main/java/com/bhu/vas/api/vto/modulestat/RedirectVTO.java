package com.bhu.vas.api.vto.modulestat;

import java.util.List;

/**
 * Created by bluesand on 12/11/15.
 */
public class RedirectVTO extends BaseVTO{

    private List<ItemRedirectVTO> items;

    public List<ItemRedirectVTO> getItems() {
        return items;
    }

    public void setItems(List<ItemRedirectVTO> items) {
        this.items = items;
    }
}
