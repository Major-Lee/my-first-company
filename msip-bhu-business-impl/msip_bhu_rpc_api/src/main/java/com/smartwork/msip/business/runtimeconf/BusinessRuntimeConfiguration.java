package com.smartwork.msip.business.runtimeconf;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;

import com.smartwork.msip.cores.helper.PropertiesHelper;

public class BusinessRuntimeConfiguration extends PropertyResourceConfigurer {
	private static final Logger logger = LoggerFactory.getLogger(BusinessRuntimeConfiguration.class);
    
	@Override
	protected void processProperties(ConfigurableListableBeanFactory paramConfigurableListableBeanFactory,
			Properties paramProperties) throws BeansException {
		appendProperties(paramProperties);
	}
	
	public static void appendProperties(Properties paramProperties) {  
        {  
        	Agent_Charging_Param_Filter = PropertiesHelper.getBoolean("agent.charging.param.filter", paramProperties,false);
        	Agent_Charging_Param_TimeUnit = PropertiesHelper.getInt("agent.charging.param.timeunit", paramProperties,Agent_Charging_Param_TimeUnit);
        	Agent_Charging_Param_ValueUnit = PropertiesHelper.getDouble("agent.charging.param.valueunit", paramProperties, Agent_Charging_Param_ValueUnit);
        	Agent_Charging_Param_CashBackFirstEach = PropertiesHelper.getDouble("agent.charging.param.cashbackfirsteach", paramProperties, Agent_Charging_Param_CashBackFirstEach);
        	logger.info("loading runtime configuration successfully!");  
        }  
    }  
	//缺省值 是否针对日志分析的数据进行过滤 agent.charging.param.filter
	public static boolean Agent_Charging_Param_Filter = false;
	//缺省值 计费时间单位 4分钟 agent.charging.param.timeunit
	public static int Agent_Charging_Param_TimeUnit = 4;
	//缺省值 每间隔单位（4分钟）收入0.01元 agent.charging.param.valueunit
	public static double Agent_Charging_Param_ValueUnit = 0.01d;
	//缺省值 新设备第一次返现额度 为30.00d  agent.charging.param.cashbackfirsteach
	public static double Agent_Charging_Param_CashBackFirstEach = 30.00d;
	
}
