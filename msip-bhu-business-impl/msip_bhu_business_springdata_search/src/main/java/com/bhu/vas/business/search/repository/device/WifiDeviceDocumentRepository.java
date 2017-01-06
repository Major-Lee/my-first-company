package com.bhu.vas.business.search.repository.device;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.bhu.vas.business.search.model.device.WifiDeviceDocument;

/**
 * lawliet
 */
public interface WifiDeviceDocumentRepository  extends ElasticsearchRepository<WifiDeviceDocument, String> {

}
