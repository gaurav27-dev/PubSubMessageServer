package com.gp.practice.azure.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class ResponseData {
	@NonNull
	PatientMessage patientMessage;
	@NonNull
	UserStatusMessage userStatusMessage;
	@NonNull
	AppointmentStatusMsg appointmentStatusMsg;

	public ResponseData() {
	}
}
