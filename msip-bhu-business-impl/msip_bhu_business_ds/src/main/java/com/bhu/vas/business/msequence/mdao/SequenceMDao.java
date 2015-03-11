package com.bhu.vas.business.msequence.mdao;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.bhu.vas.business.msequence.exception.SequenceException;
import com.bhu.vas.business.msequence.mdto.SequenceMDTO;
import com.smartwork.msip.cores.cache.relationcache.impl.springmongo.BaseMongoDAOImpl;

@Repository
public class SequenceMDao extends BaseMongoDAOImpl<SequenceMDTO> {

	public long getNextSequenceId(String key) throws SequenceException {

		Query query = new Query(Criteria.where("_id").is(key));

		Update update = new Update();
		update.inc("seq", 1);

		/*FindAndModifyOptions options = new FindAndModifyOptions();
		options.returnNew(true);*/
		//SequenceMDTO seqId = mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), SequenceMDTO.class);
		
		SequenceMDTO seqId = super.findAndModify(query, update);
		if (seqId == null) {
			throw new SequenceException("Unable to get sequence id for key : " + key);
		}
		return seqId.getSeq();

	}

	@Resource(name = "mongoTemplate")
	@Override
	protected void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

}