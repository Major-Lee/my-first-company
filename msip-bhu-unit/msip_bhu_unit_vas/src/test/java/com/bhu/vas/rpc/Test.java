package com.bhu.vas.rpc;

import com.smartwork.msip.cores.helper.HttpHelper;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by bluesand on 4/15/15.
 */
public class Test {
    public static void main(String[] args) throws Exception {

        //先将参数放入List，再对参数进行URL编码
        List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("acc", "18601002857"));
        params.add(new BasicNameValuePair("captcha", "860557"));

//对参数编码
        String param = URLEncodedUtils.format(params, "UTF-8");

//baseUrl
        String baseUrl = "http://192.168.66.7/bhu_api/v1/sessions/create";

//将URL与参数拼接
        HttpGet getMethod = new HttpGet(baseUrl + "?" + param);

        HttpClient httpClient = new DefaultHttpClient();

        try {
            HttpResponse response = httpClient.execute(getMethod); //发起GET请求

            System.out.println(response.getFirstHeader("Server"));
            HeaderElementIterator it = new BasicHeaderElementIterator(
                    response.headerIterator("Content-Type"));

            while (it.hasNext()) {
                HeaderElement elem = it.nextElement();
                System.out.println(elem.getName() + " = " + elem.getValue());

                NameValuePair[] paramss = elem.getParameters();
                for (int i = 0; i < paramss.length; i++) {
                    System.out.println(" " + paramss[i]);
                }
            }

        } catch (ClientProtocolException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
