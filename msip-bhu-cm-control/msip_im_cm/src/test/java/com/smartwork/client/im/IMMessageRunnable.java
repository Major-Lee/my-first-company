package com.smartwork.client.im;

import java.io.BufferedReader;
import java.io.IOException;

import com.smartwork.client.im.IMClientHandler.Callback;
import com.smartwork.im.message.Message;
import com.smartwork.im.message.MessageType;
import com.smartwork.im.message.cli2s.CliSignonResponse;
import com.smartwork.msip.cores.helper.JsonHelper;

public class IMMessageRunnable implements Runnable{
	BufferedReader streamReader;
	Callback callback;
	String name;
	public IMMessageRunnable(String _name, BufferedReader _reader,Callback _callback){
		streamReader = _reader;
		callback = _callback;
		name = _name;
	}
	@Override
	public void run() {
        String clientReq = null;
        while (true) {
			/*System.out.println("");
			System.out.println("please wait client's message...");
			System.out.println("");*/
			try {
				clientReq = streamReader.readLine();
				System.out.println("Recvd:"+clientReq);
				
		       	Message message = JsonHelper.getDTO(clientReq, Message.class);
		    	MessageType mtype = MessageType.getByDisplay(message.getMt());
		    	switch(mtype){
		    		case  CliSignonResponse:
		    			CliSignonResponse response = JsonHelper.getDTO(message.getPayload(), CliSignonResponse.class);
		    			if(name.equals(response.getUser())){
		    				System.out.println("response :"+name + " login success");
		    				callback.loggedIn();
		    			}
		    		/*case ChatBroadcastTextMessage:
		    			ChatBroadcastTextMessage bresponse = JsonHelper.getDTO(message.getPayload(), ChatBroadcastTextMessage.class);
		    			System.out.println("response :"+message.getFrom() + " bresponse " + bresponse.getT());*/
		    		default:
		    			System.out.println("response : unkown message " + clientReq);
		    	}
			} catch (IOException e) {
				e.printStackTrace();
			} // 读入从client传来的字符串
			//System.out.println("client said:" + clientReq); // 打印字符串
			
			/*if (clientReq.trim().equals("BYE"))
				break; // 如果是"BYE",就退出
			streamWriter.println(clientReq);*/
            //streamWriter.flush();
            
			/*System.out.print("you say:");
			s = in.readLine(); // 读取用户输入的字符串
			PS.println(s); // 将读取得字符串传给client
			if (s.trim().equals("BYE"))
				break; // 如果是"BYE",就退出
*/
		}
	}
	
}
