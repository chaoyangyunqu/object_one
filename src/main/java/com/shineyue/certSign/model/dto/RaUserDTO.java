package com.shineyue.certSign.model.dto;

import com.koal.ra.rpc.client.User;

public class RaUserDTO extends User {

	private int userId;

	public RaUserDTO(){
		super();
	}

	/**
	 * 构造函数
	 */
	public RaUserDTO(int userId) {
		super();
		this.userId = userId;
	}

	/**
	 * 获取唯一编号在RA系统中的字段名称
	 */
	@Override
	public String getUserIdKey() {
		return "USER_ID";
	}

	public void addValue(String key, Object value) {
		this.mapData.put(key, value);
	}

	/**
	 * 用户信息编码
	 */
	@Override
	protected void encode() {
		mapData.put(getUserIdKey(), userId);
		mapData.put("CERT_CN", mapData.get("USER_NAME"));
	}
}
