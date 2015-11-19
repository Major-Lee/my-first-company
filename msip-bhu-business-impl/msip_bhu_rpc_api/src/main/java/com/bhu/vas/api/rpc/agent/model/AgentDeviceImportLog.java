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
    public final static int CONFIRM_DONE = 2;
    /**
     * 代理商id
     */
    private int aid;

    /**
     * 销售人员id
     */
    private int sid;

    /**
     * 公告id，如果导入成功的话，会发布一条公告
     */
    private long bid;

    /**
     * 导入状态 0:正在导入 1:导入完毕 2:确认完毕
     */
    private int status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建日期
     */
    private Date created_at;

    /**
     * 导入成功数量
     */
    private int success_count;

    /**
     * 导入失败数量
     */
    private int fail_count;

    /**
     * 导入文件大字段json
     *
     * {"aid":100146,"path":"/BHUData/agent/output/100146/1447835444309.xls",
     * "name":"山西岩涛网络NMAC对应代理商.xls","success_count":0,"fail_count":3}
     *
     */
    private String content;

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public long getBid() {
        return bid;
    }

    public void setBid(long bid) {
        this.bid = bid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public int getSuccess_count() {
        return success_count;
    }

    public void setSuccess_count(int success_count) {
        this.success_count = success_count;
    }

    public int getFail_count() {
        return fail_count;
    }

    public void setFail_count(int fail_count) {
        this.fail_count = fail_count;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
