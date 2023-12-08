package com.api.weather.model;

import lombok.Data;

@Data
public class LegalDong {

	private String code;
	private String name;
	private String usage;
	
	public LegalDong() {
		super();
	}
	public LegalDong(String code, String name, String usage) {
		this();
		this.code = code;
		this.name = name;
		this.usage = usage;
	}
	
	
}
