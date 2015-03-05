package com.smartwork.client.gsocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketTimeoutException;

import com.smartwork.client.gsocket.SwingChatClientHandler.Callback;
import com.smartwork.im.message.Message;
import com.smartwork.im.message.MessageType;
import com.smartwork.im.message.u2u.ChatDTO;
import com.smartwork.im.message.u2u.ChatResponseMessage;
import com.smartwork.msip.cores.helper.JsonHelper;


public class ChatReceiveMessageRunnable implements Runnable{
	BufferedReader streamReader;
	Callback callback;
	boolean open  = true;
	public ChatReceiveMessageRunnable(BufferedReader _reader,Callback _callback){
		streamReader = _reader;
		callback = _callback;
	}
	@Override
	public void run() {
        String clientReq = null;
        while (open) {
			/*System.out.println("");
			System.out.println("please wait client's message...");
			System.out.println("");*/
			try {
				clientReq = streamReader.readLine();
				if(clientReq == null){
					open = false;
				}else{
					System.out.println("Recvd:"+clientReq);
			        String theMessage = (String) clientReq;
			        
			       	Message message = JsonHelper.getDTO(theMessage, Message.class);
			    	MessageType mtype = MessageType.getByDisplay(message.getMt());
			    	switch(mtype){
			    		/*case  ChatBroadcastTextMessage:
			    			ChatBroadcastTextMessage request = JsonHelper.getDTO(message.getPayload(), ChatBroadcastTextMessage.class);
			    			callback.messageReceived(message.getFrom()+":"+request.getT());
			    			break;*/
			    		/*case  ChatPeerMediaMessage:
			    			ChatMediaMessage prequest = JsonHelper.getDTO(message.getPayload(), ChatMediaMessage.class);
			    			callback.messageReceived(message.getFrom()+":"+prequest.getT());
			    			break;*/
			    		case  ChatPeerMediaMessage:
			    			ChatResponseMessage prequest = JsonHelper.getDTO(message.getPayload(), ChatResponseMessage.class);
			    			StringBuilder sb = new StringBuilder();
			    			sb.append("cid:"+prequest.getCid()+"\n");
			    			//System.out.println(prequest.getCid());
			    			for(String inner:prequest.getBodies()){
			    				ChatDTO innerMsg = JsonHelper.getDTO(inner, ChatDTO.class);
			    				sb.append(String.format("innerMsg: body:%s t:%s context:%s", innerMsg.getBody(),innerMsg.getT(),prequest.getCid().equals(innerMsg.getBody()))).append("\n");
			    				//System.out.println(String.format("innerMsg: cid:%s body:%s t:%s context:%s", innerMsg.getCid(),innerMsg.getBody(),innerMsg.getT(),innerMsg.wasContextMsg()));
			    			}
			    			callback.messageReceived(message.getFrom()+":"+sb.toString());
			    			//System.out.println("RECVD:"+message.getPayload());
			    			break;
			    		case CliSignonResponse:
			    			//CliSignonResponse signonres = JsonHelper.getDTO(message.getPayload(), CliSignonResponse.class);
			    			callback.loggedIn();
			    			//SessionCacheFacade.getInstance().addUserLocate(request.getUser(), request.getMark());
			    			//System.out.println("~~~~~~~~~"+request.getUser()+":"+SessionCacheFacade.getInstance().getUserLocate(request.getUser()));
			    			//session.removeSession(streamID, sequenceID);
			    			//this.route(stanza);
			    			break;
			    		case CliSignoffResponse:
			    			//CliSignoffResponse signoffres = JsonHelper.getDTO(message.getPayload(), CliSignoffResponse.class);
			    			callback.loggedOut();
			    			//SessionCacheFacade.getInstance().addUserLocate(request.getUser(), request.getMark());
			    			//System.out.println("~~~~~~~~~"+request.getUser()+":"+SessionCacheFacade.getInstance().getUserLocate(request.getUser()));
			    			//session.removeSession(streamID, sequenceID);
			    			//this.route(stanza);
			    			break;	
			    		default:
			    			//this.route(stanza);
			    			break;
			    	}
				}

		        
		        
		        /*String[] result = theMessage.split(" ", 3);
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
		        }*/
				
			}catch(SocketTimeoutException ste){
				
			} catch (IOException e) {
				//e.printStackTrace();
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
