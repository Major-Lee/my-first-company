package com.bhu.vas.business.search.core.condition.component;

import com.bhu.vas.business.search.core.exception.SearchQueryValidateException;

public interface ICondition {
	public void check() throws SearchQueryValidateException;
}
