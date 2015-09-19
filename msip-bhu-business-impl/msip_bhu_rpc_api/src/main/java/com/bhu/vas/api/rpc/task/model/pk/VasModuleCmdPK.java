package com.bhu.vas.api.rpc.task.model.pk;

import java.io.Serializable;

import com.smartwork.msip.cores.helper.StringHelper;
@SuppressWarnings("serial")
public class VasModuleCmdPK implements Serializable {
	private String dref;
    private String style;

    public VasModuleCmdPK() {

    }

    public VasModuleCmdPK(String dref,String style) {
    	this.dref = dref;
    	this.style = style;
    }


    public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getDref() {
		return dref;
	}

	public void setDref(String dref) {
		this.dref = dref;
	}

	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(dref).append(StringHelper.MINUS_CHAR_GAP).append(style);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VasModuleCmdPK that = (VasModuleCmdPK) o;
        if(StringHelper.isEmpty(dref) || StringHelper.isEmpty(style)) return false;
        if(dref.equals(that.getDref()) && style.equals(that.getStyle())) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
