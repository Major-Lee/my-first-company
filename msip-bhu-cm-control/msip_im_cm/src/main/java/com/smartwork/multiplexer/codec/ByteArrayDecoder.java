package com.smartwork.multiplexer.codec;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.smartwork.msip.cores.helper.ByteArrayHelper;

/**
 * @author BruceYang
 * 字节数组解码器
 */
public class ByteArrayDecoder extends CumulativeProtocolDecoder {

  public boolean doDecode(IoSession session, IoBuffer in,
      ProtocolDecoderOutput out) throws Exception {
    if (in.remaining() > 8) {//前8字节是包头
      // 有数据时，读取 4 字节判断消息长度
      byte[] sizeBytes = new byte[8];
      // 标记当前位置，以便 reset
      in.mark();
      // 读取钱 4 个字节
      in.get(sizeBytes);
      byte[] header_mtype = ByteArrayHelper.get(sizeBytes, 0, 1);
      byte[] header_stype = ByteArrayHelper.get(sizeBytes, 1, 1);
      byte[] header_rvse = ByteArrayHelper.get(sizeBytes, 2, 2);
      byte[] header_len = ByteArrayHelper.get(sizeBytes, 4, 4);
      
      // NumberUtil 是自己写的一个 int 转 byte[] 的工具类
      int size = NumberUtil.bytes2int(header_len);;//unpack_len(sizeBytes);//NumberUtil.bytes2int(sizeBytes);
      if (size > in.remaining()) {
        // 如果消息内容的长度不够，则重置（相当于不读取 size），返回 false
        in.reset();
        // 接收新数据，以拼凑成完整的数据~
        return false;
      } else {
        byte[] dataBytes = new byte[size];
        in.get(dataBytes, 0, size);
        out.write(dataBytes);
        if (in.remaining() > 0) {
          // 如果读取内容后还粘了包，就让父类把剩下的数据再给解析一次~
          return true;
        }
      }
    }
    // 处理成功，让父类进行接收下个包
    return false;
  }
  
  /*private int unpack_len(byte[] sizeBytes){
	  byte[] header_len = ByteArrayHelper.get(sizeBytes, 4, sizeBytes.length);
	  return NumberUtil.bytes2int(header_len);
  }*/
}
