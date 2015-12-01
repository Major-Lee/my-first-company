package com.bhu.vas.api.rpc.devices.model.pk;

import java.io.Serializable;

import com.smartwork.msip.cores.helper.StringHelper;

/**
 * Created by bluesand on 7/16/15.
 */
@SuppressWarnings("serial")
public class WifiDeviceGrayVersionPK implements Serializable {

	//device unit type TU_H106 TS_H103
    private String dut;
    //gray level
    private int gl;

    public WifiDeviceGrayVersionPK() {

    }

    public WifiDeviceGrayVersionPK(String dut, int gl) {
        this.dut = dut;
        this.gl = gl;
    }

    public String getDut() {
		return dut;
	}

	public void setDut(String dut) {
		this.dut = dut;
	}

	public int getGl() {
		return gl;
	}

	public void setGl(int gl) {
		this.gl = gl;
	}

	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(dut).append(StringHelper.MINUS_CHAR_GAP).append(gl);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WifiDeviceGrayVersionPK that = (WifiDeviceGrayVersionPK) o;

        if (dut == that.dut && gl == that.gl) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

}
