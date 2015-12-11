package com.bhu.vas.api.vto.modulestat;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bluesand on 12/11/15.
 */
public class BrandVTO extends BaseVTO {

    private List<ItemBrandVTO> items;

    public List<ItemBrandVTO> getItems() {
        return items;
    }

    public void setItems(List<ItemBrandVTO> items) {
        this.items = items;
    }
}
