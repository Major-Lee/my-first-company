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
package com.smartwork.client.im;

import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.commons.pool.KeyedPoolableObjectFactory;

public class CommonSocketFactory implements KeyedPoolableObjectFactory {
	private NetworkConfig networkConfig;
	public CommonSocketFactory(NetworkConfig networkConfig) {
		super();
		this.networkConfig = networkConfig;
	}

	public void activateObject(Object key, Object obj) throws Exception {
		;
	}

	public void destroyObject(Object key, Object obj) throws Exception {
		Socket socket = (Socket)obj;
		socket.close();
	}

	public Object makeObject(Object key) throws Exception {
		ServerAddress address = (ServerAddress)key;
		Socket conn = new Socket();
		conn.setSoTimeout(networkConfig.getReadTimeout()*1000);
		conn.setTcpNoDelay(networkConfig.isTcpNoDelay());
		conn.setReuseAddress(networkConfig.isReuseAddress());
		conn.setSoLinger(networkConfig.getSoLinger()>0, networkConfig.getSoLinger());
		conn.setSendBufferSize(networkConfig.getSendBufferSize());
		conn.setReceiveBufferSize(networkConfig.getReceiveBufferSize());
		conn.connect(new InetSocketAddress(address.getHost(), address.getPort()), networkConfig.getConnectTimeout()*1000);
		return conn;
	}

	public void passivateObject(Object key, Object obj) throws Exception {
		;
	}

	public boolean validateObject(Object key, Object obj) {
		Socket socket = (Socket)obj;
		return socket.isConnected();
	}

}
