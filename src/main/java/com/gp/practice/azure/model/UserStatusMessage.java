package com.gp.practice.azure.model;

import lombok.Data;

@Data
public class UserStatusMessage {

	private String id;
	private String message;
	
	
	public UserStatusMessage() {
	}


	public UserStatusMessage(String id, String message) {
		super();
		this.id = id;
		this.message = message;
	}

	
}
