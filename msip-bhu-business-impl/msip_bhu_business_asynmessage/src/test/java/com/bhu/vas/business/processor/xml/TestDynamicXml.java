package com.bhu.vas.business.processor.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.ibatis.reflection.wrapper.CollectionWrapper;

import com.bhu.vas.business.asyn.normal.activemq.xml.XmlDynamic;
import com.bhu.vas.business.asyn.normal.activemq.xml.XmlServer;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.xml.jaxb.JAXBXMLHelper;

public class TestDynamicXml {
	public static void main(String[] args) throws JAXBException {  
		
		XmlServer server1 = new XmlServer();
		server1.setHost("192.168.66.155");
		server1.setUrl("failover:(tcp://192.168.66.155:61616?tcpNoDelay=true)");
		List<String> queues_1 = new ArrayList<String>();
		queues_1.add("A");
		queues_1.add("B");
		queues_1.add("C");
		queues_1.add("D");
		server1.setQueues(queues_1);
		
		XmlServer server2 = new XmlServer();
		server2.setHost("192.168.66.7");
		server2.setUrl("failover:(tcp://192.168.66.7:61616?tcpNoDelay=true)");
		List<String> queues_2 = new ArrayList<String>();
		queues_2.add("A");
		queues_2.add("B");
		queues_2.add("C");
		queues_2.add("D");
		//server2.setQueues(queues_2);
		
		
		List<XmlServer> servers = new ArrayList<XmlServer>();
		servers.add(server1);
		servers.add(server2);
		
		XmlDynamic dyna = new XmlDynamic();
		dyna.setServers(servers);
		
		
		//将java对象转换为XML字符串  
		JAXBXMLHelper requestBinder = new JAXBXMLHelper(XmlDynamic.class);//,CollectionWrapper.class);  
        String retXml = requestBinder.toXml(dyna, "utf-8",false);  
        System.out.println("xml:\n"+retXml);
        //System.out.println("json:\n"+JsonHelper.getJSONString(module));
        
        
        /*BhuModule module2 = requestBinder.fromXml(retXml,BhuModule.class);
        
        System.out.println(module2);
        
        
        JAXBContext newInstance = JAXBContext.newInstance(BhuModule.class);
        JAXBContext newInstance2 = JAXBContext.newInstance(BhuModule.class,CollectionWrapper.class);
        JAXBContext newInstance3 = JAXBContext.newInstance(Hotel.class,CollectionWrapper.class);
        
        System.out.println(module2);*/
	}
}
