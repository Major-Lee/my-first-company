package com.bhu.statistics.util.http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

public class MyHttpClientFactory {
	
	private static MyHttpClientFactory instance = new MyHttpClientFactory();
    private MultiThreadedHttpConnectionManager connectionManager;
    private HttpClient client;

    private MyHttpClientFactory() {
        init();
    }

    public static MyHttpClientFactory getInstance() {
        return instance;
    }

    public void init() {
        this.connectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams managerParams = new HttpConnectionManagerParams();
        managerParams.setConnectionTimeout(2000);
        managerParams.setSoTimeout(30000);
        managerParams.setDefaultMaxConnectionsPerHost(6);
        managerParams.setMaxTotalConnections(2048);
        this.connectionManager.setParams(managerParams);
        this.client = new HttpClient(this.connectionManager);
    }

    public HttpClient getHttpClient() {
        if (this.client != null) {
            return this.client;
        }
        init();
        return this.client;
    }

}
