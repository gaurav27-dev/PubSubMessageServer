package com.gp.practice.azure.model;

import lombok.Data;

@Data
public class PatientMessage {
	private String connectionUrl;
	private String id;
	private String type;
	private String group;
	private String data;
	
	public PatientMessage() {
		// TODO Auto-generated constructor stub
	}

	public PatientMessage(String id, String type, String group, String data) {
		super();
		this.id = id;
		this.type = type;
		this.group = group;
		this.data = data;
	}



}
