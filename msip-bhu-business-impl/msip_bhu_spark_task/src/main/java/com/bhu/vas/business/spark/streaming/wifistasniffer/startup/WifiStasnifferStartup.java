/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bhu.vas.business.spark.streaming.wifistasniffer.startup;

import java.io.Serializable;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.business.spark.streaming.wifistasniffer.conf.WifistasnifferRuntimeConf;

/**
 * 启动spark任务类 入口类
 * 此类由spark进行管理运行
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class WifiStasnifferStartup implements Serializable{
	
	public static void main(String[] args) {
//		if (args.length ) {
//	      System.err.println("Usage: WifiStasnifferStartup <Durations>");
//	      System.exit(1);
//	    }
		WifistasnifferRuntimeConf.load(args);

		String[] locations = {"classpath*:/spring/appCtxSpark.xml"};
		ApplicationContext ctx = new FileSystemXmlApplicationContext(locations);
//		WifiStasnifferMain main = (WifiStasnifferMain)ctx.getBean("wifiStasnifferMain");
//		main.initialize();
	}
	


}
