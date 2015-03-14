package com.bhu.vas.api.dto.header;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("event")
@SuppressWarnings("serial")
public class EventTrapDTO implements Serializable{
	@XStreamAlias("trap")
	private TrapDTO trapDTO;

	public TrapDTO getTrapDTO() {
		return trapDTO;
	}

	public void setTrapDTO(TrapDTO trapDTO) {
		this.trapDTO = trapDTO;
	}
}
