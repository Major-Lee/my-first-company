package com.smartwork.client.minatest;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.smartwork.client.minatest.TestClientHandler.Callback;
//192.168.101.37:5222
public class ConnClientTest implements Callback{
	public static void main(String[] argv){
		if(argv == null) return;
		if(argv.length !=3) return;
		String ipAndPort = argv[0];
		int numClients = Integer.parseInt(argv[1]);
		String userprefix = argv[2];
		
		/*String ipAndPort = "192.168.101.37:5222";
		int numClients = 20;
		String userprefix = "edmond";*/
		
		SocketAddress address = ConnClientTest.parseSocketAddress(ipAndPort);//"127.0.0.1:5222");
		for(int i = 0;i<numClients;i++){
			String user = userprefix +i;
			ConnClientTest test = new ConnClientTest();
			test.setUser(user);
			TestClientHandler handler = new TestClientHandler(test);
			ChatClientSupport client = new ChatClientSupport(user, handler, test);
			SocketConnector connector = new NioSocketConnector();
			if (!client.connect(connector, address, false)) {
				System.out.println("~~~~~~ok");
	        }
		}
		
	}
	private String user;
    private static SocketAddress parseSocketAddress(String s) {
        s = s.trim();
        int colonIndex = s.indexOf(":");
        if (colonIndex > 0) {
            String host = s.substring(0, colonIndex);
            int port = parsePort(s.substring(colonIndex + 1));
            return new InetSocketAddress(host, port);
        } else {
            int port = parsePort(s.substring(colonIndex + 1));
            return new InetSocketAddress(port);
        }
    }
    
    private static int parsePort(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Illegal port number: " + s);
        }
    }

	@Override
	public void connected() {
		System.out.println("connected");
	}

	@Override
	public void loggedIn() {
		System.out.println("loggedIn");
	}

	@Override
	public void loggedOut() {
		
	}

	@Override
	public void disconnected() {
		System.out.println("disconnected");
	}

	@Override
	public void messageReceived(String message) {
		System.out.println(message);
	}

	@Override
	public void error(String message) {
		
	}

	@Override
	public void append(String message) {
		
	}

	@Override
	public void flRequestSuccess(String masterhost) {
		
	}

	@Override
	public void flResponseAgree(String masterhost, String message) {
		
	}

	public void setUser(String user) {
		this.user = user;
	}

	@Override
	public String getUser() {
		return user;
	}
}
