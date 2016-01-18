package com.bhu.vas.business.search.service.increment;

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
public class WifiDeviceIndexIncrementProcesser implements IWifiDeviceIndexIncrement{
	private final Logger logger = LoggerFactory.getLogger(WifiDeviceIndexIncrementProcesser.class);
	//用于批量操作
	private ExecutorService multi_exec_processes = Executors.newFixedThreadPool(1);
	//用于单项操作
	private List<ExecutorService> single_exec_processes = new ArrayList<ExecutorService>();//Executors.newFixedThreadPool(1);
	//private int hash_prime = 50;
	private int hash_prime = 10;
	private int per_threads = 1;
	
	@Resource(name="wifiDeviceIndexIncrementService")
	private IWifiDeviceIndexIncrement wifiDeviceIndexIncrement;
	
	@PostConstruct
	public void initialize(){
		logger.info("WifiDeviceIndexIncrementProcesser initialize...");
		
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
	 */
	@Override
	public void locaitionUpdIncrement(final String id, final double lat, final double lon, final String d_address){
		ExecutorService executor = singleExecProcesser(id);
		if(executor != null){
			executor.submit((new Runnable() {
				@Override
				public void run() {
					try{
						wifiDeviceIndexIncrement.locaitionUpdIncrement(id, lat, lon, d_address);
					}catch(Exception ex){
						ex.printStackTrace(System.out);
					}
				}
			}));
		}
	}
	
	/**
	 * 设备模块上线发生变更
	 * @param id 设备mac
	 * @param origvapmodule 原始模块软件版本号
	 */
	@Deprecated
	public void moduleOnlineUpdIncrement(final String id, final String d_origvapmodule){
		ExecutorService executor = singleExecProcesser(id);
		if(executor != null){
			executor.submit((new Runnable() {
				@Override
				public void run() {
					try{
						wifiDeviceIndexIncrement.moduleOnlineUpdIncrement(id, d_origvapmodule);
					}catch(Exception ex){
						ex.printStackTrace(System.out);
					}
				}
			}));
		}
	}
	
	/**
	 * 设备下线发生变更
	 * @param id 设备mac
	 * @param d_uptime 设备运行总时长
	 * @param d_lastlogoutat 设备的最后下线的时间
	 */
	@Deprecated
	public void offlineUpdIncrement(final String id, final String d_uptime, final long d_lastlogoutat){
		ExecutorService executor = singleExecProcesser(id);
		if(executor != null){
			executor.submit((new Runnable() {
				@Override
				public void run() {
					try{
						wifiDeviceIndexIncrement.offlineUpdIncrement(id, d_uptime, d_lastlogoutat);
					}catch(Exception ex){
						ex.printStackTrace(System.out);
					}
				}
			}));
		}
	}
	
	/**
	 * 创建在导入的确认的设备数据multi
	 * @param importId 导入批次
	 * @param agentDeviceClaims
	 */
	@Deprecated
	public void batchConfirmMultiUpsertIncrement(final long importId, final List<AgentDeviceClaim> agentDeviceClaims){
		multiExecProcesser().submit((new Runnable() {
			@Override
			public void run() {
				try{
					wifiDeviceIndexIncrement.batchConfirmMultiUpsertIncrement(importId, agentDeviceClaims);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
				}
			}
		}));
	}
	
	/**
	 * 设备上线发生变更
	 * @param entity
	 */
	@Deprecated
	public void onlineUpdIncrement(final WifiDevice entity){
		if(entity == null) return;
		
		ExecutorService executor = singleExecProcesser(entity.getId());
		if(executor != null){
			executor.submit((new Runnable() {
				@Override
				public void run() {
					try{
						wifiDeviceIndexIncrement.onlineUpdIncrement(entity);
					}catch(Exception ex){
						ex.printStackTrace(System.out);
					}
				}
			}));
		}
	}
	
	/**
	 * 设备上线发生变更multi
	 * @param entitys 设备实体集合
	 */
	public void onlineMultiUpsertIncrement(final List<WifiDevice> entitys){
		multiExecProcesser().submit((new Runnable() {
			@Override
			public void run() {
				try{
					wifiDeviceIndexIncrement.onlineMultiUpsertIncrement(entitys);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
				}
			}
		}));
	}
	
	/**
	 * 设备认领上线处理或首次上线，按照全字段重建覆盖标准
	 * @param entity
	 */
/*	@Deprecated
	public void onlineCrdIncrement(final WifiDevice entity){
		if(entity == null) return;
		
		ExecutorService executor = singleExecProcesser(entity.getId());
		if(executor != null){
			executor.submit((new Runnable() {
				@Override
				public void run() {
					try{
						wifiDeviceIndexIncrement.onlineCrdIncrement(entity);
					}catch(Exception ex){
						ex.printStackTrace(System.out);
					}
				}
			}));
		}
	}*/
	
	/**
	 * 设备绑定或解绑的变更
	 * @param id 设备mac
	 * @param bindUser 如果为null表示解绑设备
	 */
/*	public void bindUserUpdIncrement(final String id, final User bindUser){
		ExecutorService executor = singleExecProcesser(id);
		if(executor != null){
			executor.submit((new Runnable() {
				@Override
				public void run() {
					try{
						wifiDeviceIndexIncrement.bindUserUpdIncrement(id, bindUser);
					}catch(Exception ex){
						ex.printStackTrace(System.out);
					}
				}
			}));
		}
	}*/
	
	/**
	 * 设备运营模板的变更
	 * @param id
	 * @param o_template 运营模板编号
	 */
	public void templateUpdIncrement(final String id, final String o_template){
		ExecutorService executor = singleExecProcesser(id);
		if(executor != null){
			executor.submit((new Runnable() {
				@Override
				public void run() {
					try{
						wifiDeviceIndexIncrement.templateUpdIncrement(id, o_template);
					}catch(Exception ex){
						ex.printStackTrace(System.out);
					}
				}
			}));
		}
	}
	
	/**
	 * 设备运营模板的变更multi
	 * @param ids 设备mac的集合
	 * @param o_template
	 */
	public void templateMultiUpdIncrement(final List<String> ids, final String o_template){
		multiExecProcesser().submit((new Runnable() {
			@Override
			public void run() {
				try{
					wifiDeviceIndexIncrement.templateMultiUpdIncrement(ids, o_template);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
				}
			}
		}));
	}
	
	/**
	 * 更新设备的批次号
	 * 变更涉及的更改索引字段是
	 * 1) o_batch
	 * @param id
	 * @param importId
	 * @param agentUser
	 */
	@Override
	public void agentUpdIncrement(final String id, final long importId, final User agentUser) {
		ExecutorService executor = singleExecProcesser(id);
		if(executor != null){
			executor.submit((new Runnable() {
				@Override
				public void run() {
					try{
						wifiDeviceIndexIncrement.agentUpdIncrement(id, importId, agentUser);
					}catch(Exception ex){
						ex.printStackTrace(System.out);
					}
				}
			}));
		}
	}
	
	/**
	 * 批量更新设备的批次号multi
	 * 变更涉及的更改索引字段是
	 * 1) o_batch
	 * @param ids
	 * @param importId
	 * @param agentUser
	 */
	public void agentMultiUpdIncrement(final List<String> ids, final long importId, final User agentUser){
		multiExecProcesser().submit((new Runnable() {
			@Override
			public void run() {
				try{
					wifiDeviceIndexIncrement.agentMultiUpdIncrement(ids, importId, agentUser);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
				}
			}
		}));
	}
	
	/**
	 * 设备运营灰度级别的变更
	 * @param id
	 * @param o_graylevel
	 */
	public void graylevelUpdIncrement(final String id, final String o_graylevel){
		ExecutorService executor = singleExecProcesser(id);
		if(executor != null){
			executor.submit((new Runnable() {
				@Override
				public void run() {
					try{
						wifiDeviceIndexIncrement.graylevelUpdIncrement(id, o_graylevel);
					}catch(Exception ex){
						ex.printStackTrace(System.out);
					}
				}
			}));
		}
	}
	
	/**
	 * 设备运营灰度级别的变更multi
	 * @param ids 设备mac的集合
	 * @param o_graylevel
	 */
	public void graylevelMultiUpdIncrement(final List<String> ids, final String o_graylevel){
		multiExecProcesser().submit((new Runnable() {
			@Override
			public void run() {
				try{
					wifiDeviceIndexIncrement.graylevelMultiUpdIncrement(ids, o_graylevel);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
				}
			}
		}));
	}
	
	/**
	 * 设备的终端数量的变更multi
	 * @param ids 设备mac的集合
	 * @param hocs 设备的终端数量的集合
	 */
	public void hocMultiUpdIncrement(final List<String> ids, final List<Integer> hocs){
		multiExecProcesser().submit((new Runnable() {
			@Override
			public void run() {
				try{
					wifiDeviceIndexIncrement.hocMultiUpdIncrement(ids, hocs);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
				}
			}
		}));
	}
	
}
