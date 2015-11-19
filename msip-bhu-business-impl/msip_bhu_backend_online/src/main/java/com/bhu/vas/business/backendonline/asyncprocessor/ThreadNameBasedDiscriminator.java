package com.bhu.vas.business.backendonline.asyncprocessor;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.Discriminator;

public class ThreadNameBasedDiscriminator implements Discriminator<ILoggingEvent> {
	 
    private static final String KEY = "hashid";
 
    private boolean started;
 
    @Override
    public String getDiscriminatingValue(ILoggingEvent iLoggingEvent) {
    	//System.out.println(iLoggingEvent.getMarker().getName());
        return iLoggingEvent.getMarker().getName();
    }
 
    @Override
    public String getKey() {
        return KEY;
    }
 
    public void start() {
        started = true;
    }
 
    public void stop() {
        started = false;
    }
 
    public boolean isStarted() {
        return started;
    }
}