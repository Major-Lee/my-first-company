package com.bhu.vas.api.rpc.agent.model;

import com.bhu.vas.api.rpc.sequence.helper.IRedisSequenceGenable;
import com.smartwork.msip.cores.orm.model.BaseLongModel;

import java.util.Date;

/**
 * Created by bluesand on 9/15/15.
 */
@SuppressWarnings("serial")
public class AgentDeviceImportLog extends BaseLongModel implements IRedisSequenceGenable {

    public final static int IMPORT_DOING = 0;
    public final static int IMPORT_DONE = 1;
    public final static int IMPORT_ERROR = 2;
    /**
     * 代理商id
     */
    private int aid;

    /**
     * 销售人员id
     */
    private int wid;

    /**
     * 创建日期
     */
    private Date created_at;

    /**
     * 导入数量
     */
    private int count;

    /**
     * 导入状态 0:正在导入 1:导入完毕 2:导入失败
     */
    private int status;

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public int getWid() {
        return wid;
    }

    public void setWid(int wid) {
        this.wid = wid;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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
