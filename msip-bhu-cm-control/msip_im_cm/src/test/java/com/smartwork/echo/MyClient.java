package com.smartwork.echo;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class MyClient {
	   private static final String HOSTNAME = "localhost";  
	   
	    private static final int PORT = 8080;  
	  
	    private static final long CONNECT_TIMEOUT = 30*1000L; // 30 seconds  
	      
	     
	    public static void main(String[] args) throws Exception  
	    {  
	        // 创建 TCP/IP connector.  
	        NioSocketConnector connector = new NioSocketConnector();  
	        //设置日志过滤器  
	         connector.getFilterChain().addLast("logger", new LoggingFilter());  
	        //获得过滤器,设置读取数据的方式一行一行的读.  
	         TextLineCodecFactory lineCodec = new TextLineCodecFactory(Charset.forName("UTF-8"), 
		        		LineDelimiter.UNIX.getValue(),LineDelimiter.UNIX.getValue());
		        lineCodec.setDecoderMaxLineLength(2*1024);
		        lineCodec.setEncoderMaxLineLength(2*1024);
	        connector.getFilterChain().addLast("codec",new ProtocolCodecFilter(lineCodec));//new TextLineCodecFactory()));  
	        connector.setHandler(new ReverseClientHandler());  
	        //set connect timeout  
	        connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);  
	         IoSession session;  
	           for (;;) {  
	                try {  
	                    //连接到服务器  
	                    ConnectFuture future = connector.connect(new InetSocketAddress(  
	                            HOSTNAME, PORT));  
	                    future.awaitUninterruptibly();  
	                    session = future.getSession();  
	                    break;  
	                } catch (RuntimeIoException e) {  
	                    System.err.println("Failed to connect.");  
	                    e.printStackTrace();  
	                    Thread.sleep(5000);  
	                      
	                }  
	            }  
	  
	            // wait until the summation is done  
	            session.getCloseFuture().awaitUninterruptibly();  
	              
	            connector.dispose();  
	          
	          
	    }  
}
