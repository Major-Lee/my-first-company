package com.bhu.vas.api.rpc.statistics.model;

import com.smartwork.msip.cores.orm.model.extjson.ListJsonExtStringModel;

import java.util.Date;

/**
 * Created by bluesand on 5/23/15.
 */
public class UserBrandStatistics extends ListJsonExtStringModel<String> {

    private Date created_at;

    @Override
    public Class<String> getJsonParserModel() {
        return String.class;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
