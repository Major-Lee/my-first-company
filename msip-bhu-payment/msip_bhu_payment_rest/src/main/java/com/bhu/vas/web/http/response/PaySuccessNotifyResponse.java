package com.bhu.vas.web.http.response;

import java.util.Map;

import com.bhu.vas.web.http.WxResponse;

public class PaySuccessNotifyResponse extends WxResponse {

    String bank_type;
    String cash_fee;

    String fee_type;
    String is_subscribe;
    String mch_id;
    String nonce_str;
    String openid;
    String out_trade_no;
    String result_code;
    String return_code;
    String sign;
    String time_end;
    int total_fee;
    String trade_type;
    String transaction_id;


    @Override
    public void getValueFromMap(Map map) {
        super.getValueFromMap(map);
        bank_type=map.get("bank_type").toString();
        cash_fee=map.get("cash_fee").toString();
        fee_type=map.get("fee_type").toString();
        is_subscribe=map.get("is_subscribe").toString();
        mch_id=map.get("mch_id").toString();
        nonce_str=map.get("nonce_str").toString();
        openid=map.get("openid").toString();
        out_trade_no=map.get("out_trade_no").toString();
        result_code=map.get("result_code").toString();
        return_code=map.get("return_code").toString();
        sign=map.get("sign").toString();
        time_end=map.get("time_end").toString();
        total_fee=Integer.valueOf( map.get("total_fee").toString());
        trade_type=map.get("trade_type").toString();
        transaction_id=map.get("transaction_id").toString();
    }

    public String getBank_type() {
        return bank_type;
    }

    public void setBank_type(String bank_type) {
        this.bank_type = bank_type;
    }

    public String getCash_fee() {
        return cash_fee;
    }

    public void setCash_fee(String cash_fee) {
        this.cash_fee = cash_fee;
    }

    public String getFee_type() {
        return fee_type;
    }

    public void setFee_type(String fee_type) {
        this.fee_type = fee_type;
    }

    public String getIs_subscribe() {
        return is_subscribe;
    }

    public void setIs_subscribe(String is_subscribe) {
        this.is_subscribe = is_subscribe;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getResult_code() {
        return result_code;
    }

    public void setResult_code(String result_code) {
        this.result_code = result_code;
    }

    public String getReturn_code() {
        return return_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTime_end() {
        return time_end;
    }

    public void setTime_end(String time_end) {
        this.time_end = time_end;
    }

    public int getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(int total_fee) {
        this.total_fee = total_fee;
    }

    public String getTrade_type() {
        return trade_type;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }
}
