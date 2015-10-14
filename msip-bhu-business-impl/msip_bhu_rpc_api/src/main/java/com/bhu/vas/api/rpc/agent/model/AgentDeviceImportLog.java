package com.bhu.vas.api.rpc.agent.model;

import com.bhu.vas.api.rpc.sequence.helper.IRedisSequenceGenable;
import com.smartwork.msip.cores.orm.model.BaseLongModel;

import java.util.Date;

/**
 * Created by bluesand on 9/15/15.
 */
@SuppressWarnings("serial")
public class AgentDeviceImportLog extends BaseLongModel implements IRedisSequenceGenable {

    /**
     * 代理商id
     */
    private int aid;

    /**
     * 创建日期
     */
    private Date created_at;

    /**
     * 导入数量
     */
    private int count;

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public void preInsert() {
        if (this.created_at == null)
            this.created_at = new Date();
        super.preInsert();
    }

    @Override
    public void setSequenceKey(Long key) {
        this.id = key;
    }
}
