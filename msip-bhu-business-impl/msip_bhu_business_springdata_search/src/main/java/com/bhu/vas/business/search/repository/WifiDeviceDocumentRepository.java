package com.bhu.vas.business.search.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.bhu.vas.business.search.model.WifiDeviceDocument;

/**
 * lawliet
 */
public interface WifiDeviceDocumentRepository  extends ElasticsearchRepository<WifiDeviceDocument, String> {

}
