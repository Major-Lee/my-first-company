package com.bhu.vas.api.vto.modulestat;

import java.util.List;

/**
 * Created by bluesand on 12/11/15.
 */
public class ChannelVTO extends BaseVTO{

    private List<ItemChannelVTO> items;

    public List<ItemChannelVTO> getItems() {
        return items;
    }

    public void setItems(List<ItemChannelVTO> items) {
        this.items = items;
    }
}
