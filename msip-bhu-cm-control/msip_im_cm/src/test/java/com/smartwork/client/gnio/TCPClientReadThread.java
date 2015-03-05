package com.smartwork.client.gnio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class TCPClientReadThread implements Runnable {
	private Selector selector;

	 /*缓冲区大小*/  
    private static int BLOCK = 1024;  
    /*接受数据缓冲区*/  
    //private static ByteBuffer sendBuffer = ByteBuffer.allocate(BLOCK);  
    /*发送数据缓冲区*/  
    private static ByteBuffer receiveBuffer = ByteBuffer.allocate(BLOCK);
    private StringBuilder complete_sb = new StringBuilder();
	public TCPClientReadThread(Selector selector) {
		this.selector = selector;

		new Thread(this).start();
	}

	public void run() {
		try {
			while (selector.select() > 0) {
				
				// 遍历每个有可用IO操作Channel对应的SelectionKey
				for (SelectionKey sk : selector.selectedKeys()) {
					//if(!sk.isConnectable()) break;
					/*if (sk.isConnectable()) {
						final SocketChannel thisSc = (SocketChannel) sk.channel();
						// sc.write(ByteBuffer.wrap(new byte[1024]));
						thisSc.register(selector, SelectionKey.OP_WRITE, sk.attachment());
						if(thisSc.finishConnect()){
							System.out.println(sk.attachment() + "connect");
							ByteBuffer sendbuffer = ByteBuffer.allocate(1024);
							sendbuffer.clear();  
	                        sendbuffer.put((JsonHelper.getJSONString(MessageBuilder.builderCliSignonRequestMessage("user1", null, "1234"))+"\n").getBytes());  
	                        sendbuffer.flip();  
	                        thisSc.write(sendbuffer); 
						}
						thisSc.register(selector, SelectionKey.OP_READ, sk.attachment());
					}else*/
					// 如果该SelectionKey对应的Channel中有可读的数据
					 if (sk.isReadable()) {
						// 使用NIO读取Channel中的数据
						SocketChannel sc = (SocketChannel) sk.channel();
						//将缓冲区清空以备下次读取  
	                    receiveBuffer.clear();
						//ByteBuffer buffer = ByteBuffer.allocate(1024);
						int readBytes = sc.read(receiveBuffer);
						receiveBuffer.flip();

						if(readBytes == -1){
							sc.close();
						}
						
						// 将字节转化为为UTF-8的字符串
						String receivedString = Charset.forName("UTF-8").newDecoder().decode(receiveBuffer).toString();
						System.out.println("RECV:"+receivedString);
						if(!receivedString.trim().equals("")){
							char end = receivedString.charAt(receivedString.length()-1);
							if(end == '\n'){//消息完整性验证正确
								complete_sb.append(receivedString);
								StringReader sr = null;
								BufferedReader br = null;
								try{
									sr = new StringReader(complete_sb.toString());
									br = new BufferedReader(sr);
									String msgin = null;
									while ((msgin = br.readLine()) != null) {
										System.out.println("接收到来自服务器" + sc.socket().getRemoteSocketAddress() + "的信息:" + msgin);
										/*Message msg = new Message();
										msg.arg1 = 1;
										msg.what = 1;
										msg.obj = msgin;
										mHandler.sendMessage(msg);
										Log.i(TAG, "RCVD:" + msgin);
										Log.v(TAG,"接收到来自服务器"+ sc.socket().getRemoteSocketAddress()+ "的信息:" + msgin);*/
										//receivedString = "";
									}
								}catch (IOException e) {
									e.printStackTrace();
								}finally{
									sr.close();
									sr = null;
									try {
										br.close();
										br = null;
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
								
								complete_sb.delete(0, complete_sb.length());
							}else{//消息完整性不正确，还需要等待下个缓冲区数据合并
								complete_sb.append(receivedString);
							}
						}
						//StringReader sr = new StringReader(receivedString);
						//BufferedReader br = new BufferedReader(sr);
						//InputStreamReader input = new InputStreamReader(System.in);  
						//BufferedReader br = new BufferedReader(input);  
						//input.
						// 将字节转化为为UTF-8的字符串
						/*String receivedString = Charset.forName("UTF-8").newDecoder().decode(receiveBuffer).toString();
						
						if(receivedString != "")
							// 控制台打印出来
							System.out.println("接收到来自服务器" + sc.socket().getRemoteSocketAddress() + "的信息:" + receivedString);
*/
						// 为下一次读取作准备
						try {
							sk.interestOps(SelectionKey.OP_READ);
						}catch (CancelledKeyException e) {
							e.printStackTrace();
						}catch (Exception e) {
							e.printStackTrace();
						}
					}/*else if (sk.isWritable()) {
						SocketChannel thisSc = (SocketChannel) sk.channel();
						System.out.println(sk.attachment() + "send" + thisSc.write(ByteBuffer.wrap(new byte[1024])));
						
						thisSc.register(selector, SelectionKey.OP_READ, sk.attachment());
					}*/

					// 删除正在处理的SelectionKey
					selector.selectedKeys().remove(sk);
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}