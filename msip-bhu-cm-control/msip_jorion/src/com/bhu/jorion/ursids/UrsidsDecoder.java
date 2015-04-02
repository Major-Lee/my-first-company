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
		int pos = 0;
		LOGGER.debug("Decoding message...");
        IoBuffer buf = (IoBuffer)session.getAttribute(BUFFER);
        if(buf == null){
        	buf = IoBuffer.allocate(JOrionConfig.PRE_ALLOC_BUFF_SIZE).setAutoExpand(true);
        	session.setAttribute(BUFFER, buf);
        }
		LOGGER.debug("original buf " + buf.toString());
        buf.put(in);
        pos = buf.position();
        buf.flip();
		LOGGER.debug("chache len " + buf.remaining() + ", " + buf);
        while(buf.remaining() > UrsidsHeader.URSIDS_HDR_LEN){
        	buf.mark();
        	UrsidsHeader hdr = new UrsidsHeader(buf);
        	LOGGER.debug("Prase result:" + hdr.toString());
    		LOGGER.debug("now buffer " + buf.toString());
        	if(hdr.getLength() <= buf.remaining()){
        		byte[] body = new byte[(int) hdr.getLength()];
        		buf.get(body);
        		UrsidsMessage msg = new UrsidsMessage(hdr, body, "");
        		LOGGER.debug("succed received a ursids pkt, write object");
        		out.write(msg);
        		count ++;
        	} else {
            	LOGGER.debug("Not enough content, count:" + count);
        		//not enough content
        		buf.reset();
        		if(count == 0){
        			LOGGER.debug("wait for next, buf " + buf.toString());
        			buf.position(pos);
        			return;
        		}
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
