package com.bhu.vas.business.asyn.spring.model.agent;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

/**
 * Created by bluesand on 9/8/15.
 */
public class AgentDeviceClaimImportDTO extends ActionDTO {

//    private int aid;
//
//    private int wid;

    private long logId;

    private String inputPath;

    private String outputPath;

    private String originName;


    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public String getInputPath() {
        return inputPath;
    }

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    @Override
    public String getActionType() {
        return ActionMessageType.AgentDeviceClaimImport.getPrefix();
    }
}
