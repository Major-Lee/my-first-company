package com.bhu.vas.api.vto.agent;

import com.smartwork.msip.cores.orm.support.page.TailPage;

import java.io.Serializable;

/**
 * Created by bluesand on 9/18/15.
 */
@SuppressWarnings("serial")
public class AgentDeviceVTO implements Serializable {

    private TailPage<AgentDeviceClaimVTO> vtos;

    private int total_count;

    private int online_count;

    private int offline_count;

    //从未上线
    private int yet_count;


    public TailPage<AgentDeviceClaimVTO> getVtos() {
        return vtos;
    }

    public void setVtos(TailPage<AgentDeviceClaimVTO> vtos) {
        this.vtos = vtos;
    }

    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public int getOnline_count() {
        return online_count;
    }

    public void setOnline_count(int online_count) {
        this.online_count = online_count;
    }

    public int getOffline_count() {
        return offline_count;
    }

    public void setOffline_count(int offline_count) {
        this.offline_count = offline_count;
    }

    public int getYet_count() {
        return yet_count;
    }

    public void setYet_count(int yet_count) {
        this.yet_count = yet_count;
    }
}
