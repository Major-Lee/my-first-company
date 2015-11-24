package com.bhu.vas.business.search.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.bhu.vas.business.search.model.WifiDeviceDocument1;

/**
 * Created by mohsinhusen on 10/04/15.
 */
public interface WifiDeviceDocument1Repository  extends ElasticsearchRepository<WifiDeviceDocument1, String> {
//	Page<WifiDeviceDocument1> findByD_online(Boolean online, Pageable pageable);
//	Page<WifiDeviceDocument1> findByOnlineFalse(Pageable pageable);
//	Page<WifiDeviceDocument1> findByOnlineTrue(Pageable pageable);
//	Page<WifiDeviceDocument1> findByGroups(String group,Pageable pageable);
//	Page<WifiDeviceDocument1> findByRegisteredatGreaterThanOrderByRegisteredatDesc(long registeredat,Pageable pageable);
//	Page<WifiDeviceDocument1> findByRegisteredatGreaterThanOrderByRegisteredatAsc(long registeredat,Pageable pageable);
//	Page<WifiDeviceDocument1> findByAddress(String address,Pageable pageable);
//	Long countByAddress(String address);
//	int countByOnlineTrue();
}
