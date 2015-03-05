package com.smartwork.client.socket;

import java.io.BufferedReader;
import java.io.IOException;

import com.smartwork.client.ChatCommand;
import com.smartwork.client.socket.SwingChatClientHandler.Callback;

public class ChatReceiveMessageRunnable implements Runnable{
	BufferedReader streamReader;
	Callback callback;
	public ChatReceiveMessageRunnable(BufferedReader _reader,Callback _callback){
		streamReader = _reader;
		callback = _callback;
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
		        String theMessage = (String) clientReq;
		        String[] result = theMessage.split(" ", 3);
		        if(result == null || result.length<2) continue;
		        String status = result[1];
		        String theCommand = result[0];
		        ChatCommand command = ChatCommand.valueOf(theCommand);

		        if ("OK".equals(status)) {

		            switch (command.toInt()) {

		            case ChatCommand.BROADCAST:
		                if (result.length == 3) {
		                    callback.messageReceived(result[2]);
		                }
		                break;
		            case ChatCommand.LOGIN:
		                callback.loggedIn();
		                break;

		            case ChatCommand.QUIT:
		                callback.loggedOut();
		                break;
		            }

		        } else {
		            if (result.length == 3) {
		                callback.error(result[2]);
		            }
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
