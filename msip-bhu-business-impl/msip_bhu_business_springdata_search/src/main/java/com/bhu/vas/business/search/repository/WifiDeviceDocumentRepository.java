package com.bhu.vas.business.search.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.bhu.vas.business.search.model.WifiDeviceDocument;

/**
 * Created by mohsinhusen on 10/04/15.
 */
public interface WifiDeviceDocumentRepository  extends ElasticsearchRepository<WifiDeviceDocument, String> {
	Page<WifiDeviceDocument> findByOnline(Boolean online, Pageable pageable);
	Page<WifiDeviceDocument> findByOnlineFalse(Pageable pageable);
	Page<WifiDeviceDocument> findByOnlineTrue(Pageable pageable);
	Page<WifiDeviceDocument> findByGroups(String group,Pageable pageable);
	Page<WifiDeviceDocument> findByRegisteratGreaterThanOrderByRegisteratDesc(long registerat,Pageable pageable);
	Page<WifiDeviceDocument> findByRegisteratGreaterThanOrderByRegisteratAsc(long registerat,Pageable pageable);
	Page<WifiDeviceDocument> findByAddress(String address,Pageable pageable);
	Long countByAddress(String address);
	int countByOnlineTrue();
}
