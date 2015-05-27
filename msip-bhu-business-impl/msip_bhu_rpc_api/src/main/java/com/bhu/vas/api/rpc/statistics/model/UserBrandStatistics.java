package com.bhu.vas.api.rpc.statistics.model;

import com.bhu.vas.api.rpc.statistics.dto.UserBrandDTO;
import com.smartwork.msip.cores.orm.model.extjson.ListJsonExtStringModel;

import java.util.Date;

/**
 * Created by bluesand on 5/23/15.
 */
public class UserBrandStatistics extends ListJsonExtStringModel<UserBrandDTO> {

    private Date created_at;

    @Override
    public Class<UserBrandDTO> getJsonParserModel() {
        return UserBrandDTO.class;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
