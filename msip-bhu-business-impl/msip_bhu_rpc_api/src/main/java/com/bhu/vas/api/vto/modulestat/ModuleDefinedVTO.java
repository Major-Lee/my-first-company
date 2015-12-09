package com.bhu.vas.api.vto.modulestat;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bluesand on 12/2/15.
 */
public class ModuleDefinedVTO implements Serializable {

    private String style;

    private String memo;

    private long count;

    private List<ModuleDefinedDetailVTO> item;

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }


    public List<ModuleDefinedDetailVTO> getItem() {
        return item;
    }

    public void setItem(List<ModuleDefinedDetailVTO> item) {
        this.item = item;
    }
}
