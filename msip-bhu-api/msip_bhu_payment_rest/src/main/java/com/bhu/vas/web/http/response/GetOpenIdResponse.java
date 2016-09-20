package com.bhu.vas.web.http.response;


import java.util.Map;

import com.bhu.vas.web.http.WxResponse;

public class GetOpenIdResponse extends WxResponse {
    String openId;

    public GetOpenIdResponse() {
    }

    //{"access_token":"OezXcEiiBSKSxW0eoylIeFeP9u2z5z-m3DXECBb7zzRfQZ_tqG9B3wzZsBk4eLLR2IeEcgEtJn87f1B98ck7VP_XKYX6DPRLgWf5LSUe5szQ09hkEgD9QBScrQ8s0igN0-sD39w2ldDvtD83VAaiug","expires_in":7200,"refresh_token":"OezXcEiiBSKSxW0eoylIeFeP9u2z5z-m3DXECBb7zzRfQZ_tqG9B3wzZsBk4eLLR7VI2RpWOQTNi-plimnIzPdtF3QjQM882NN70fDhlOHkm1mrMpCQteevFhFOpOT-NXnr_XYI6SPUmYE0fIpKRzg","openid":"obTzrjnp0Oq_Es1hDxTwE2gmWtPQ","scope":"snsapi_base","unionid":"okftEt77VNNhRjGl6i-00BGBR_UU"}
    @Override
    public void getValueFromMap(Map map) {
        super.getValueFromMap(map);
        openId=map.get("openid").toString();
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
}
