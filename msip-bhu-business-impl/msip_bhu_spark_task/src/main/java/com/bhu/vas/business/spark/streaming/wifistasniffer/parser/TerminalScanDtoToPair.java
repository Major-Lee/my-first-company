package com.bhu.vas.business.spark.streaming.wifistasniffer.parser;

import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

import com.bhu.vas.business.spark.streaming.wifistasniffer.TerminalScanStreamingDTO;

/**
 * 把dto 转成 pair
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class TerminalScanDtoToPair implements PairFunction<TerminalScanStreamingDTO, String, TerminalScanStreamingDTO> {
	@Override
	public Tuple2<String, TerminalScanStreamingDTO> call(TerminalScanStreamingDTO dto) throws Exception {
	      if(dto != null){
	    	return new Tuple2<String, TerminalScanStreamingDTO>(dto.getMac(), dto);
	      }
	      return null;
	}
}
