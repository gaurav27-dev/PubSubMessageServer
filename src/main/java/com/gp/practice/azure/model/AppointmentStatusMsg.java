package com.gp.practice.azure.model;

import lombok.Data;

@Data
public class AppointmentStatusMsg {

	private int appt_Id;
	private String appt_status;
	private String clientId;
	
	
	public AppointmentStatusMsg() {
	
	}

	public AppointmentStatusMsg(int appt_Id, String appt_status, String clientId) {
		super();
		this.appt_Id = appt_Id;
		this.appt_status = appt_status;
		this.clientId = clientId;
	}



}
