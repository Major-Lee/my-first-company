package com.bhu.vas.web.http.response;

import java.util.Map;

import com.bhu.vas.web.http.WxResponse;

public class JSAPIUnifiedOrderResponse extends WxResponse {

    String prepay_id;
    String result_code;
    String trade_type;
	String sign;
    @Override
    public void getValueFromMap(Map map) {
        super.getValueFromMap(map);
        prepay_id=map.get("prepay_id").toString();
		sign=map.get("sign").toString();
    }

	public String getPrepay_id() {
		return prepay_id;
	}

	public void setPrepay_id(String prepay_id) {
		this.prepay_id = prepay_id;
	}

	public String getResult_code() {
		return result_code;
	}

	public void setResult_code(String result_code) {
		this.result_code = result_code;
	}

	public String getTrade_type() {
		return trade_type;
	}

	public void setTrade_type(String trade_type) {
		this.trade_type = trade_type;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
}
