package com.hh.excel.engine.config.vo;

public abstract class AbstractBaseConfig {
	public static final String XLSX = "xlsx";
	public static final String XLS = "xls";
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
