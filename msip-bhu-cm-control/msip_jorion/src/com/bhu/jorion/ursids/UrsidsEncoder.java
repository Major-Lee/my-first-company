package com.bhu.jorion.ursids;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.jorion.message.UrsidsMessage;

public class UrsidsEncoder extends ProtocolEncoderAdapter {
    private final static Logger LOGGER = LoggerFactory.getLogger(UrsidsEncoder.class);

	@Override
	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		LOGGER.debug("Encoding message...");
		UrsidsMessage msg = (UrsidsMessage)message;
		IoBuffer ib = IoBuffer.allocate(msg.getTotalLength());
		msg.store(ib);
		ib.rewind();
		LOGGER.debug("encodedlen:" + ib.remaining());
		out.write(ib);
	}
}
