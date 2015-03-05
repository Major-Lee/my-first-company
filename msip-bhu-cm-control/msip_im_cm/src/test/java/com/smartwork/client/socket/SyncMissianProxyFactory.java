/*
 *	 Copyright [2010] Stanley Ding(Dingshengyu)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   Missian is based on hessian, mina and spring. Any project who uses 
 *	 missian must agree to hessian, mima and spring's license.
 *	  Hessian: http://hessian.caucho.com/
 *    Mina:http://mina.apache.org
 *	  Spring(Optional):http://www.springsource.org/	 
 *
 *   @author stanley
 *	 @date 2010-11-28
 */
package com.smartwork.client.socket;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SyncMissianProxyFactory {
	// tcp configurations
	private boolean connectionKeepAlive = false;
	private SocketPool socketPool;
	NetworkConfig networkConfig;
	
	public SyncMissianProxyFactory() {
		this(new NetworkConfig());
	}
	
	public SyncMissianProxyFactory(NetworkConfig networkConfig) {
		this.networkConfig = networkConfig;
		this.connectionKeepAlive = false;
	}
	public SyncMissianProxyFactory(SocketPool socketPool) {
		this.socketPool = socketPool;
		this.connectionKeepAlive = true;
	}
	public SyncMissianProxyFactory(NetworkConfig networkConfig,SocketPool socketPool) {
		this.networkConfig = networkConfig;
		this.socketPool = socketPool;
		this.connectionKeepAlive = true;
	}
	public Socket getSocket(String host, int port) throws Exception {
		if(connectionKeepAlive) {
			return socketPool.getSocket(host, port);
		}
		return createSocket(host, port);
	}
	public Socket createSocket(String host, int port) throws IOException {
		Socket conn = new Socket();
		conn.setSoTimeout(networkConfig.getReadTimeout()*1000);
		conn.setTcpNoDelay(networkConfig.isTcpNoDelay());
		conn.setReuseAddress(networkConfig.isReuseAddress());
		conn.setSoLinger(networkConfig.getSoLinger()>0, networkConfig.getSoLinger());
		conn.setSendBufferSize(networkConfig.getSendBufferSize());
		conn.setReceiveBufferSize(networkConfig.getReceiveBufferSize());
		conn.connect(new InetSocketAddress(host, port), networkConfig.getConnectTimeout()*1000);
		return conn;
	}
	
	public Socket createSocket(InetSocketAddress address) throws IOException {
		Socket conn = new Socket();
		conn.setSoTimeout(networkConfig.getReadTimeout()*1000);
		conn.setTcpNoDelay(networkConfig.isTcpNoDelay());
		conn.setReuseAddress(networkConfig.isReuseAddress());
		conn.setSoLinger(networkConfig.getSoLinger()>0, networkConfig.getSoLinger());
		conn.setSendBufferSize(networkConfig.getSendBufferSize());
		conn.setReceiveBufferSize(networkConfig.getReceiveBufferSize());
		conn.connect(address, networkConfig.getConnectTimeout()*1000);
		return conn;
	}
	
	public void destroySocket(String host, int port, Socket socket) throws Exception {
		if(connectionKeepAlive) {
			socketPool.returnSocket(host, port, socket);
		} else {
			socket.close();
		}
	}
}
