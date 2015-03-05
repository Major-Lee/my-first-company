package com.smartwork.client.im;

import java.io.PrintWriter;

import com.smartwork.client.im.IMClientHandler.Callback;
import com.smartwork.im.message.MessageBuilder;
import com.smartwork.msip.cores.helper.JsonHelper;

public class IMCallback implements Callback{
	
	private String name;
	private PrintWriter socketWriter; 
	
	public IMCallback(String name, PrintWriter socketWriter){
		this.name = name;
		this.socketWriter = socketWriter;
	}
	
	@Override
	public void connected() {
		
	}

	@Override
	public void loggedIn() {
		String text = "I am " + name;
		/*String bmsg = JsonHelper.getJSONString(MessageBuilder.builderCliBroadcastMessage(name, text));
		socketWriter.write(bmsg + "\n");
		socketWriter.flush();
		System.out.println("send CliBroadcast Message : " + bmsg);*/
	}

	@Override
	public void loggedOut() {
		
	}

	@Override
	public void disconnected() {
		
	}

	@Override
	public void messageReceived(String message) {
		
	}

	@Override
	public void error(String message) {
		
	}
}
