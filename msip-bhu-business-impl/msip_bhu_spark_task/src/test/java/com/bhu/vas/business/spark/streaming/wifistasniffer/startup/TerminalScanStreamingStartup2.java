///*
// * Licensed to the Apache Software Foundation (ASF) under one or more
// * contributor license agreements.  See the NOTICE file distributed with
// * this work for additional information regarding copyright ownership.
// * The ASF licenses this file to You under the Apache License, Version 2.0
// * (the "License"); you may not use this file except in compliance with
// * the License.  You may obtain a copy of the License at
// *
// *    http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.bhu.vas.business.spark.streaming.wifistasniffer.startup;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.apache.spark.SparkConf;
//import org.apache.spark.streaming.Durations;
//import org.apache.spark.streaming.api.java.JavaDStream;
//import org.apache.spark.streaming.api.java.JavaPairDStream;
//import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
//import org.apache.spark.streaming.api.java.JavaStreamingContext;
//import org.apache.spark.streaming.kafka.KafkaUtils;
//
//import com.bhu.vas.business.spark.streaming.wifistasniffer.TerminalScanStreamingDTO;
//import com.bhu.vas.business.spark.streaming.wifistasniffer.parser.TerminalScanDtoToPair;
//import com.bhu.vas.business.spark.streaming.wifistasniffer.parser.TerminalScanPrint2;
//import com.bhu.vas.business.spark.streaming.wifistasniffer.parser.TerminalScanStringToDtoParser;
///**
// * Counts words in UTF8 encoded, '\n' delimited text received from the network
// * every second.
// * 
// * Usage: JavaNetworkWordCount <hostname> <port> <hostname> and <port> describe
// * the TCP server that Spark Streaming would connect to receive data.
// * 
// * To run this on your local machine, you need to first run a Netcat server `$
// * nc -lk 9999` and then run the example `$ bin/run-example
// * org.apache.spark.examples.streaming.JavaNetworkWordCount localhost 9999`
// */
//public class TerminalScanStreamingStartup2{
//
//	public static void main(String[] args) {
//		// Create the context with a 10 second batch size
//		SparkConf sparkConf = new SparkConf()
//				//.setMaster("local[2]")
//				.setMaster("spark://192.168.66.7:7077")
//				.setJars(new String[]{"/BHUData/apps/msip_bhu_spark_task/applications/msip_bhu_spark_task-jar-with-dependencies.jar"})
//				.setAppName(TerminalScanStreamingStartup2.class.getSimpleName());
//		JavaStreamingContext ssc = new JavaStreamingContext(sparkConf,
//				Durations.seconds(10));
//
//		// Create a JavaReceiverInputDStream on target ip:port and count the
//		// words in input stream of \n delimited text (eg. generated by 'nc')
//		// Note that no duplication in storage level only for running locally.
//		// Replication necessary in distributed scenario for fault tolerance.
//	    //JavaReceiverInputDStream<String> lines = ssc.receiverStream(new TerminalScanReceiver());
//		int numThreads = Integer.parseInt("2");
//	    Map<String, Integer> topicMap = new HashMap<String, Integer>();
//	    topicMap.put("wifistasniffer", numThreads);
//	    
//		JavaPairReceiverInputDStream<String, String> kafka_messages =
//	            KafkaUtils.createStream(ssc, "192.168.66.7:2181", "mygroup", topicMap);
//		//JavaDStream<String> lines = ssc.textFileStream("file:///Users/tangzichao/work/resources/spark_file"); 
//		JavaDStream<TerminalScanStreamingDTO> map_dtos = kafka_messages.map(new TerminalScanStringToDtoParser());
//		JavaPairDStream<String, TerminalScanStreamingDTO> pairs = map_dtos.mapToPair(new TerminalScanDtoToPair());
//		JavaPairDStream<String, Iterable<TerminalScanStreamingDTO>> group_dtos = pairs.groupByKey();
//		group_dtos.foreachRDD(new TerminalScanPrint2());
//		
//		//group_dtos.print();
//		//wordCounts.count();
//		//ssc.start();
//		//ssc.awaitTermination();
//		ssc.start();
//		ssc.awaitTermination();
//	}
//	
//
//
//}
