package com.bhu.vas.business.search.service.wifi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.elasticsearch.common.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.agent.model.AgentDeviceClaim;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.user.model.User;
import com.smartwork.msip.cores.helper.HashAlgorithmsHelper;

@Service
public class SsidIndexIncrementProcesser implements ISsidIndexIncrement{
	private final Logger logger = LoggerFactory.getLogger(SsidIndexIncrementProcesser.class);
	//用于批量操作
	private ExecutorService multi_exec_processes = Executors.newFixedThreadPool(1);
	//用于单项操作
	private List<ExecutorService> single_exec_processes = new ArrayList<ExecutorService>();//Executors.newFixedThreadPool(1);
	//private int hash_prime = 50;
	private int hash_prime = 10;
	private int per_threads = 1;
	
	@Resource(name="ssidIndexIncrementService")
	private ISsidIndexIncrement ssidIndexIncrement;
	
	@PostConstruct
	public void initialize(){
		logger.info("SsidIndexIncrementProcesser initialize...");
		
		for(int i=0;i<hash_prime;i++){
			single_exec_processes.add(Executors.newFixedThreadPool(per_threads));
		}
	}
	/**
	 * 获取单项操作执行线程
	 * @param id
	 * @return
	 */
	public ExecutorService singleExecProcesser(String id){
		if(StringUtils.isEmpty(id)) return null;
		
		int hash = HashAlgorithmsHelper.rotatingHash(id, hash_prime);
		return single_exec_processes.get(hash);
	}
	
	/**
	 * 获取批量操作执行线程
	 * @return
	 */
	public ExecutorService multiExecProcesser(){
		return multi_exec_processes;
	}
	
	/**
	 * 设备位置发生变更
	 * @param id 设备mac
	 * @param lat 纬度
	 * @param lon 经度
	 * @param d_address 详细地址
	 * @param district 
	 * @param city 
	 * @param province 
	 */
	@Override
	public void updateIncrement(final String id, final String ssid, final String mode, final String pwd){
		ExecutorService executor = singleExecProcesser(id);
		if(executor != null){
			executor.submit((new Runnable() {
				@Override
				public void run() {
					try{
						ssidIndexIncrement.updateIncrement(id, ssid, mode, pwd);
					}catch(Exception ex){
						ex.printStackTrace(System.out);
					}
				}
			}));
		}
	}
}
