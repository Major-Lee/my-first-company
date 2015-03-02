package com.bhu.vas.api.subject.model;

import com.smartwork.msip.cores.orm.model.extjson.SetJsonExtIntModel;

@SuppressWarnings("serial")
public class SubjectTagState extends SetJsonExtIntModel<Integer>{

	@Override
	public Class<Integer> getJsonParserModel() {
		return Integer.class;
	}

}
