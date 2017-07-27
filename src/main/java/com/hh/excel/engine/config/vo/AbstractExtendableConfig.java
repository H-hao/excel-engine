package com.hh.excel.engine.config.vo;

public abstract class AbstractExtendableConfig extends AbstractBaseConfig {

	private String extend;// 可继承自其他的vo

	public String getExtend() {
		return extend;
	}

	public void setExtend(String extend) {
		this.extend = extend;
	}

}
