package com.bhu.vas.api.sequence.model;

import com.smartwork.msip.cores.orm.model.BaseStringModel;

@SuppressWarnings("serial")
public class Sequence extends BaseStringModel {
	private int nextid;
	
	private int incr;
	/* Constructors */

	public Sequence() {
	}

	public Sequence(String name, int nextid) {
		this.id = name;
		this.nextid = nextid;
	}

	public String getName() {
		return id;
	}

	public void setName(String name) {
		this.id = name;
	}

	public int getNextid() {
		return nextid;
	}

	public void setNextid(int nextid) {
		this.nextid = nextid;
	}

	public int getIncr() {
		return incr;
	}

	public void setIncr(int incr) {
		this.incr = incr;
	}
}
