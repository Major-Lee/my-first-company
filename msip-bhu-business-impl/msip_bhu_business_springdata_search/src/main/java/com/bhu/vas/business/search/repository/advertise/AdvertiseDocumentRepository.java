package com.bhu.vas.business.search.repository.advertise;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.bhu.vas.business.search.model.advertise.AdvertiseDocument;

public interface AdvertiseDocumentRepository extends ElasticsearchRepository<AdvertiseDocument, String>{

}
