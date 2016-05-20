package com.heepay.api;

import java.text.SimpleDateFormat;
import java.util.Date;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//order();
		query();
		sign();
	}
	
	private static void order(){
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//�������ڸ�ʽ
		String time = df.format(new Date());
		String billno = "BHU"+time;
		String url = Heepay.order(billno, "0.52", "123.56.78.98");
		System.out.println(url);
	}
	
	private static void query(){
		String result = Heepay.query("MOHE1463039619159pmeo");
		System.out.println(result);
	}
	
	private static void sign(){
		//result=1&pay_message=&agent_id=2067044&jnet_bill_no=H1605114812464AV&agent_bill_id=BHU20160511170522
		//&pay_type=30&pay_amt=0.52&remark=&sign=93184de6bd847891e0f4b116fe3a68b4

		//System.out.println(Heepay.sign("H1605114812464AV", "BHU20160511170522", "0.52"));
	}

}
