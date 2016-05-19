package com.bhu.statistics.util.cache;

import java.io.Closeable;

import org.apache.log4j.Logger;


/**
 * 
 * @ClassName  SerializeTranscoder 
 * @Description TODO(这里用一句话描述这个类的作用) 
 * @author Alan
 * @version 1.0
 * @date 2015年9月23日 下午8:35:57 
 *
 */
public abstract class SerializeTranscoder {

  protected static Logger logger = Logger.getLogger(SerializeTranscoder.class);
  
  public abstract byte[] serialize(Object value);
  
  public abstract Object deserialize(byte[] in);
  
  public void close(Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (Exception e) {
         logger.info("Unable to close " + closeable, e); 
      }
    }
  }
}