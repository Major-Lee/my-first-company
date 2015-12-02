package com.bhu.vas.api.rpc.task.model;

import com.bhu.vas.api.rpc.task.model.pk.VasModuleCmdPK;
import com.bhu.vas.api.vto.device.ModuleStyleVTO;
import com.smartwork.msip.cores.orm.model.BasePKModel;

/**
 * Created by bluesand on 7/16/15.
 */
@SuppressWarnings("serial")
public class VasModuleCmdDefined extends BasePKModel<VasModuleCmdPK> {

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

	public ModuleStyleVTO toModuleStyleVTO(){
		ModuleStyleVTO vto = new ModuleStyleVTO();
		vto.setDref(this.getId().getDref());
		vto.setStyle(this.getId().getStyle());
		vto.setVersion(this.getVersion());
		vto.setMemo(this.getMemo());
		return vto;
	}
}
