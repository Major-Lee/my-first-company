package com.bhu.jorion.ursids;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class UrsidsCodecFactory implements ProtocolCodecFactory {
    private final UrsidsEncoder encoder;

    private final UrsidsDecoder decoder;

    
    public UrsidsCodecFactory() {
        encoder = new UrsidsEncoder();
        decoder = new UrsidsDecoder();
    }

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return encoder;
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return decoder;
	}

}
