package com.bhu.vas.api.rpc.statistics.model;

import com.bhu.vas.api.rpc.statistics.dto.UserUrlDTO;
import com.smartwork.msip.cores.orm.model.extjson.ListJsonExtStringModel;

import java.util.Date;

/**
 * Created by bluesand on 5/29/15.
 */
public class UserUrlStatistics extends ListJsonExtStringModel<UserUrlDTO> {

    private Date created_at;

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    @Override
    public Class<UserUrlDTO> getJsonParserModel() {
        return UserUrlDTO.class;
    }
}
