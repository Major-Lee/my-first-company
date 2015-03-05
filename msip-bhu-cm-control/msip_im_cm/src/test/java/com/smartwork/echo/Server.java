package com.smartwork.echo;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class Server {
	   private static final int PORT = 8080;  
	   
	    public static void main(String[] args) throws Exception {  
	        NioSocketAcceptor acceptor = new NioSocketAcceptor();  
	  
	        // Prepare the configuration  
	        acceptor.getFilterChain().addLast("logger", new LoggingFilter());  
	        
	        TextLineCodecFactory lineCodec = new TextLineCodecFactory(Charset.forName("UTF-8"), 
	        		LineDelimiter.UNIX.getValue(),LineDelimiter.UNIX.getValue());
	        lineCodec.setDecoderMaxLineLength(2*1024);
	        lineCodec.setEncoderMaxLineLength(2*1024);
	        acceptor.getFilterChain().addLast(  
	                "codec",  
	                new ProtocolCodecFilter(lineCodec));//new TextLineCodecFactory(Charset.forName("UTF-8"))));  
	  
	        // Bind  
	        acceptor.setHandler(new ReverseProtocolHandler());  
	        acceptor.bind(new InetSocketAddress(PORT));  
	  
	        System.out.println("Listening on port " + PORT);  
	    }  
}
