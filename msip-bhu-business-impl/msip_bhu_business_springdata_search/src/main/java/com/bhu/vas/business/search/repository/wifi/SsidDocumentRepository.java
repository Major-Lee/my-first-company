package com.bhu.vas.business.search.repository.wifi;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.bhu.vas.business.search.model.wifi.SsidDocument;

public interface SsidDocumentRepository extends ElasticsearchRepository<SsidDocument, String>{

}
