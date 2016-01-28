package com.bhu.vas.api.rpc.task.model;

import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.rpc.task.model.pk.VasModuleCmdPK;
import com.bhu.vas.api.vto.device.ModuleStyleVTO;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.model.BasePKModel;

/**
 * Created by bluesand on 7/16/15.
 */
@SuppressWarnings("serial")
public class VasModuleCmdDefined extends BasePKModel<VasModuleCmdPK> {

	public final static VasModuleCmdPK stopCmdPk = new VasModuleCmdPK(OperationDS.DS_Http_VapModuleCMD_Stop.getRef(),"style---");
	private String version;
	private String template;
	private String memo;

    public String getDref() {
        if (this.getId() == null) {
            return null;
        }
        return this.getId().getDref();
    }

    public void setDref(String dref) {
        if (this.getId() == null) {
            this.setId(new VasModuleCmdPK());
        }
        this.getId().setDref(dref);
    }

    public String getStyle() {
        if (this.getId() == null) {
            return null;
        }
        return this.getId().getStyle();
    }

    public void setStyle(String style) {
        if(this.getId() == null) {
            this.setId(new VasModuleCmdPK());
        }
        this.getId().setStyle(style);
    }
    
    public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@Override
    protected Class<VasModuleCmdPK> getPKClass() {
        return VasModuleCmdPK.class;
    }

	public static ModuleStyleVTO BuildEmptyModuleStyleVTO(){
		ModuleStyleVTO vto = new ModuleStyleVTO();
		vto.setDref(OperationDS.DS_Http_VapModuleCMD_Start.getRef());
		vto.setStyle(StringHelper.MINUS_STRING_GAP);
		vto.setVersion(StringHelper.EMPTY_STRING_GAP);
		vto.setMemo(StringHelper.EMPTY_STRING_GAP);
		vto.setUpdated_at(StringHelper.EMPTY_STRING_GAP);
		return vto;
	}
	
	public ModuleStyleVTO toModuleStyleVTO(){
		ModuleStyleVTO vto = new ModuleStyleVTO();
		vto.setDref(this.getId().getDref());
		vto.setStyle(this.getId().getStyle());
		vto.setVersion(this.getVersion());
		vto.setMemo(this.getMemo());
		vto.setUpdated_at(DateTimeHelper.formatDate(this.getUpdated_at(), DateTimeHelper.FormatPattern1));
		return vto;
	}
}
