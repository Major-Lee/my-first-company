package com.bhu.vas.web.http;

import java.io.IOException;
import java.util.Map;

import com.bhu.vas.business.helper.JsonUtil;
import com.bhu.vas.business.helper.XMLUtil;

/**
 * Http方式请求第三方支付接口
 * Pengyu on 2016/5/03.
 */
public class HttpResponseUtil {


    /**
     * Send a get request
     *
     * @param url
     * @return response
     * @throws IOException
     */
    static public <RESPONE extends WxResponse> RESPONE get(String url, Class<RESPONE> responeClass) throws IOException {
        return convertResponse(HttpContentUtil.get(url), responeClass);
    }

    /**
     * Send a get request
     *
     * @param url     Url as string
     * @param headers Optional map with headers
     * @return response Response as string
     * @throws IOException
     */
    static public <RESPONE extends WxResponse> RESPONE get(String url, Map<String, String> headers, Class<RESPONE> responeClass) throws IOException {
        return convertResponse(HttpContentUtil.get(url, headers), responeClass);
    }

    /**
     * Send a post request
     *
     * @param url     Url as string
     * @param body    Request body as string
     * @param headers Optional map with headers
     * @return response Response as string
     * @throws IOException
     */
    static public <RESPONE extends WxResponse> RESPONE post(String url, String body, Map<String, String> headers, Class<RESPONE> responeClass) throws IOException {
        return convertResponse(HttpContentUtil.post(url, body, headers), responeClass);
    }

    /**
     * Send a post request
     *
     * @param url  Url as string
     * @param body Request body as string
     * @return response Response as string
     * @throws IOException
     */
    static public <RESPONE extends WxResponse> RESPONE post(String url, String body, Class<RESPONE> responeClass) throws IOException {
        return convertResponse(HttpContentUtil.post(url, body), responeClass);
    }

    /**
     * Post a form with parameters
     *
     * @param url    Url as string
     * @param params map with parameters/values
     * @return response Response as string
     * @throws IOException
     */
    static public <RESPONE extends WxResponse> RESPONE postForm(String url, Map<String, String> params, Class<RESPONE> responeClass) throws IOException {
        return convertResponse(HttpContentUtil.postForm(url, params), responeClass);
    }

    /**
     * Post a form with parameters
     *
     * @param url     Url as string
     * @param params  Map with parameters/values
     * @param headers Optional map with headers
     * @return response Response as string
     * @throws IOException
     */
    static public <RESPONE extends WxResponse> RESPONE postForm(String url, Map<String, String> params, Map<String, String> headers, Class<RESPONE> responeClass) throws IOException {
        return convertResponse(HttpContentUtil.postForm(url, params, headers), responeClass);
    }

    /**
     * Send a put request
     *
     * @param url     Url as string
     * @param body    Request body as string
     * @param headers Optional map with headers
     * @return response Response as string
     * @throws IOException
     */
    static public <RESPONE extends WxResponse> RESPONE put(String url, String body, Map<String, String> headers, Class<RESPONE> responeClass) throws IOException {
        return convertResponse(HttpContentUtil.put(url, body, headers), responeClass);
    }

    /**
     * Send a put request
     *
     * @param url Url as string
     * @return response Response as string
     * @throws IOException
     */
    static public <RESPONE extends WxResponse> RESPONE put(String url, String body, Class<RESPONE> responeClass) throws IOException {
        return convertResponse(HttpContentUtil.put(url, body), responeClass);
    }

    /**
     * Send a delete request
     *
     * @param url     Url as string
     * @param headers Optional map with headers
     * @return response Response as string
     * @throws IOException
     */
    static public <RESPONE extends WxResponse> RESPONE delete(String url, Map<String, String> headers, Class<RESPONE> responeClass) throws IOException {
        return convertResponse(HttpContentUtil.delete(url, headers), responeClass);
    }

    /**
     * Send a delete request
     *
     * @param url Url as string
     * @return response Response as string
     * @throws IOException
     */
    static public <RESPONE extends WxResponse> RESPONE delete(String url, Class<RESPONE> responeClass) throws IOException {
        return convertResponse(HttpContentUtil.delete(url), responeClass);

    }

    /**
     * Append query parameters to given url
     *
     * @param url    Url as string
     * @param params Map with query parameters
     * @return url Url with query parameters appended
     * @throws IOException
     */
    static public <RESPONE extends WxResponse> RESPONE appendQueryParams(String url, Map<String, String> params, Class<RESPONE> responeClass) throws IOException {
        return convertResponse(HttpContentUtil.appendQueryParams(url, params),responeClass);
    }
    static <RESPONE extends WxResponse>  RESPONE convertResponse(String content,Class<RESPONE> responeClass){
        System.out.println("RESPONSE CONTENT "+content);
        if(content==null ){
            return null;
        }
        content=content.trim();
        if(content.length()==0){
            return null;
        }
        RESPONE response;
        if(content.startsWith("<")){
            try {
                response= responeClass.newInstance();
                if(response==null){
                    return null;
                }
                response.setResponseContent(content);
                Map map = XMLUtil.doXMLParse(content);
                response.setPropertyMap(map);
            } catch (Exception e) {
                return null;
            }
        }else{
            response= JsonUtil.fromJson(content, responeClass);
        }
        return response;
    }
}
