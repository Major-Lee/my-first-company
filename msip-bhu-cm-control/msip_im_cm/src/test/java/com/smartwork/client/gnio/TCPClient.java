package com.smartwork.client.gnio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.smartwork.im.message.MessageBuilder;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.msg.MsgIDGenHelper;
import com.smartwork.msip.localunit.RandomPicker;
/**
* NIO TCP 客户端
 *
* @date    2010-2-3
* @time    下午03:33:26
* @version 1.00
*/
public class TCPClient{
	  // 信道选择器
	  private Selector selector;
   
	  // 与服务器通信的信道
	  SocketChannel socketChannel;
   
	  // 要连接的服务器Ip地址
	  private String hostIp;
	   
	  // 要连接的远程服务器在监听的端口
	  private int hostListenningPort;
   
	  /**
	   * 构造函数
	   * @param HostIp
	   * @param HostListenningPort
	   * @throws IOException
	   */
	  public TCPClient(String HostIp,int HostListenningPort)throws IOException{
	    this.hostIp=HostIp;
	    this.hostListenningPort=HostListenningPort;  
	     
	    initialize();
	  }
	   
	  /**
	   * 初始化
	   * @throws IOException
	   */
	  private void initialize() throws IOException{
	    // 打开监听信道并设置为非阻塞模式
	    socketChannel=SocketChannel.open(new InetSocketAddress(hostIp, hostListenningPort));
	    socketChannel.configureBlocking(false);
	     
	    // 打开并注册选择器到信道
	    selector = Selector.open();
	    socketChannel.register(selector, SelectionKey.OP_READ);
	    //socketChannel.register(selector, SelectionKey.OP_CONNECT);
	    // 启动读取线程
	    new TCPClientReadThread(selector);
	  }
	   
	  
	  public void sendLoginMsg(String from,String pwd) throws IOException{
		  	String messagejson = JsonHelper.getJSONString(MessageBuilder.builderCliSignonRequestMessage(from, null, pwd))+"\n";
			//String messagejson = JsonHelper.getJSONString(MessageBuilder.builderCliPeerMessage("user1", "user1", message))+"\n";
		    ByteBuffer writeBuffer=ByteBuffer.wrap(messagejson.getBytes("UTF-8"));
		    socketChannel.write(writeBuffer);
	  }
	  
	  /**
	   * 发送字符串到服务器
	   * "2014070415hmm.jpg"
	   * @param message
	   * @throws IOException
	   */
	  public void sendPeerMsg(String from ,String to,String cid, String message) throws IOException{
		String messagejson = JsonHelper.getJSONString(MessageBuilder.builderCliPeerMediaMessage(from, to,cid,true,'T',message))+"\n";
	    ByteBuffer writeBuffer=ByteBuffer.wrap(messagejson.getBytes("UTF-8"));
	    socketChannel.write(writeBuffer);
	  }
	  
	  public void sendPeerRefMsg(String from ,String to,String cid) throws IOException{
		String messagejson = JsonHelper.getJSONString(MessageBuilder.builderCliPeerMediaRefMessage(from, to, cid, true, 'Z', cid, "", RandomPicker.pick(refers)))+"\n";//.builderCliPeerMediaMessage(from, to,cid,true,'T',message))+"\n";
	    ByteBuffer writeBuffer=ByteBuffer.wrap(messagejson.getBytes("UTF-8"));
	    socketChannel.write(writeBuffer);
	  }
	  
	  public static final String[] froms  ={"200037","200038","200082"};
	  public static final String[] tokens ={"NCdfU1NTVUBEH0BYCVENcExWAlU=","NCVHU1NTVUBLH0BYCVEPchZSAFo=","MCpLU1NTVUtBH0BYCFZZdkgHB1I="};
	  public static final String[] refers ={"20140101010101aaaP1001-0.zip","20140101010101aaaP1001-2.zip","20140101010101aaaP1001-3.zip"};
	  
	  public static final String[] tos = {"200007"};
	  public static final SimpleDateFormat COMMON_ID_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
	  public static String[] chatContents ={
		  "h",
		  "whowhowhowhow江泽民howhowhowhowhowho",
		  "how are u!胡锦涛how are u!how are u!how are u!how are u!how are u!how are u!how are u!how are u!how are u!how are u!",
		  "who",
		  "asdfdf黄莉新gdssdf",
		  "asdfdfgdssdf是否撒厉以宁asdfdfgdssdf是否撒asdfdfgdssdf是否撒asdfdfgdssdf是否撒"
		  }; 
	  public static String generateID(Date date, String extension, String bussinessId) {
			StringBuilder sb = new StringBuilder();

			String now = COMMON_ID_FORMAT.format(date);
			sb.append(now);

			sb.append((char) (int) (97.0D + Math.random() * 26.0D));
			sb.append((char) (int) (97.0D + Math.random() * 26.0D));
			sb.append((char) (int) (97.0D + Math.random() * 26.0D));

			sb.append(bussinessId);
			sb.append('.').append(extension);
			return sb.toString();
	  } 
	  public static String generateBusinessid(String from ,String to){
		  StringBuilder sb = new StringBuilder();
		  sb.append('P').append(from).append('-').append(to);
		  return sb.toString();
	  }
	  public static void main(String[] args) throws IOException{
		//int limit = 1000000;
		//int count = 0;
		
		for(int i = 0;i<3;i++){
			final String from  = froms[i];
			final String pwd = tokens[i];
			Thread current = new Thread(){
				@Override
				public void run() {
					try {
					    TCPClient client=new TCPClient("192.168.1.106",5222);//"182.254.143.54",5222);
					    client.sendLoginMsg(from,pwd);
					    /*try {
							Thread.sleep(5*1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}*/
					    while(true){
					    	try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
					    	/*String cid = generateID(RandomData.date(DateTimeHelper.getDateDaysAgo(2000), DateTimeHelper.getDateDaysAfter(10)),
					    			"zip",generateBusinessid(from,from));
					    	
							client.sendPeerMsg(from,from,cid,RandomPicker.pick(chatContents));*/
					    	/*String cid = generateID(RandomData.date(DateTimeHelper.getDateDaysAgo(2000), DateTimeHelper.getDateDaysAfter(10)),
					    			"zip",generateBusinessid(from,from));*/
					    	String to = RandomPicker.pick(tos);
					    	String cid = MsgIDGenHelper.generate(from, to,MsgIDGenHelper.MsgID_PeerPrefix);
							client.sendPeerRefMsg(from,to,cid);
					    }
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			current.start();
		}
	 }
}
/*	  public static void main(String[] args) throws IOException{
		int limit = 1000000;
		int count = 0;
		
		
		for(int i = 0;i<5;i++){
			final String from  = froms[i];
			Thread current = new Thread(){
				@Override
				public void run() {
					try {
				    TCPClient client=new TCPClient("192.168.1.106",5222);//"182.254.143.54",5222);
				    client.sendLoginMsg(from);
				    try {
						Thread.sleep(5*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				    while(true){
				    	try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
				    	String cid = generateID(RandomData.date(DateTimeHelper.getDateDaysAgo(2000), DateTimeHelper.getDateDaysAfter(10)),
				    			"zip",generateBusinessid(from,from));
				    	
						client.sendPeerMsg(from,from,cid,RandomPicker.pick(chatContents));
						
				    }
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			current.start();
		}
	  }
	}*/
