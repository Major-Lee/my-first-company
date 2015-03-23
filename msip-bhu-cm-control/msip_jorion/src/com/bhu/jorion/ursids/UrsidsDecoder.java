package com.bhu.jorion.ursids;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.jorion.JOrionConfig;
import com.bhu.jorion.message.UrsidsHeader;
import com.bhu.jorion.message.UrsidsMessage;

public class UrsidsDecoder implements ProtocolDecoder {
    private final static Logger LOGGER = LoggerFactory.getLogger(UrsidsDecoder.class);
    private final AttributeKey BUFFER = new AttributeKey(getClass(), "buffer");
    
	@Override
	public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
			throws Exception {
		int count = 0;
		LOGGER.debug("Decoding message...");
        IoBuffer buf = (IoBuffer)session.getAttribute(BUFFER);
        if(buf == null){
        	buf = IoBuffer.allocate(JOrionConfig.PRE_ALLOC_BUFF_SIZE).setAutoExpand(true);
        }
        buf.put(in);
        buf.flip();
		LOGGER.debug("chache len " + buf.remaining());
        while(buf.remaining() > UrsidsHeader.URSIDS_HDR_LEN){
        	buf.mark();
        	UrsidsHeader hdr = new UrsidsHeader(buf);
        	LOGGER.debug("Prase result:" + hdr.toString());
        	if(hdr.getLength() <= buf.remaining()){
        		byte[] body = new byte[(int) hdr.getLength()];
        		buf.get(body);
        		UrsidsMessage msg = new UrsidsMessage(hdr, body, "");
        		out.write(msg);
        	} else {
        		//not enough content
        		buf.reset();
        		if(count == 0)
        			return;
        		break;
        	}
        }
        
        if(buf.hasRemaining()){
        	IoBuffer tmp = IoBuffer.allocate(buf.remaining());
        	tmp.put(buf);
        	tmp.flip();
        	buf.clear();
        	buf.put(tmp);
        } else {
        	buf.clear();
        }
	}

	@Override
	public void finishDecode(IoSession session, ProtocolDecoderOutput out)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose(IoSession session) throws Exception {
		// TODO Auto-generated method stub

	}

}
