package com.gp.practice.azure.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.azure.messaging.webpubsub.WebPubSubServiceClient;
import com.azure.messaging.webpubsub.models.GetClientAccessTokenOptions;
import com.azure.messaging.webpubsub.models.WebPubSubClientAccessToken;
import com.azure.messaging.webpubsub.models.WebPubSubContentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gp.practice.azure.model.AppointmentStatusMsg;
import com.gp.practice.azure.model.PatientMessage;
import com.gp.practice.azure.model.ResponseData;
import com.gp.practice.azure.model.UserStatusMessage;

@RestController
public class MessagePublisherController {

	@Autowired
	@Qualifier("ovchubClient")
	WebPubSubServiceClient pubSubServiceClientOVC;

	@Autowired
	@Qualifier("notificationhubClient")
	WebPubSubServiceClient pubSubServiceClientNotification;

	@RequestMapping(value = "/negotiate", method = RequestMethod.GET)
	public ResponseEntity<?> getClientAccessToken(@RequestParam("userId") String id,
			@RequestParam(name = "role", required = false) String role) {
		System.out.println("userId: " + id + " role: " + role);
		if (StringUtils.isEmpty(id)) {
			return ResponseEntity.badRequest().body("Missing user Id");
		}
		GetClientAccessTokenOptions options = new GetClientAccessTokenOptions();
		options.setUserId(id);
		WebPubSubClientAccessToken accessToken;
		if (!StringUtils.isEmpty(role) && "notification".equals(role)) {
			accessToken = pubSubServiceClientNotification.getClientAccessToken(options);
		} else {
			accessToken = pubSubServiceClientOVC.getClientAccessToken(options);
		}
		String accessurl = accessToken.getUrl();
		PatientMessage msg = new PatientMessage();
		msg.setConnectionUrl(accessurl);
		// System.out.println("accessurl:- " + accessurl);
		// System.out.println("token:- " + accessToken.getToken());
		return ResponseEntity.ok(msg);

	}

	@RequestMapping(value = "/negotiateRoles", method = RequestMethod.GET)
	public ResponseEntity<?> getAccessToken(@RequestParam("userId") String id) {
		// System.out.println("userId: " + id);
		if (StringUtils.isEmpty(id)) {
			return ResponseEntity.badRequest().body("Missing user Id");
		}
		GetClientAccessTokenOptions options = new GetClientAccessTokenOptions();
		options.setUserId(id);
		options.addRole("webpubsub.joinLeaveGroup.providerMeet");
		options.addRole("webpubsub.sendToGroup.providerMeet");
		WebPubSubClientAccessToken accessToken = pubSubServiceClientOVC.getClientAccessToken(options);
		String accessurl = accessToken.getUrl();
		PatientMessage msg = new PatientMessage();
		msg.setConnectionUrl(accessurl);
		// System.out.println("accessurl:- " + accessurl);
		// System.out.println("token:- " + accessToken.getToken());
		return ResponseEntity.ok(msg);

	}

	@RequestMapping(value = "/eventhandler/{hubName}/{eventName}", method = RequestMethod.OPTIONS)
	public ResponseEntity<?> validate(@RequestHeader Map<String, String> headers, @PathVariable String hubName,
			@PathVariable String eventName) {
		System.out.println("validate headers options:- " + headers);
//		String allowdedOrigin = "rajwebpubsubdemo.webpubsub.azure.com";
//		if (allowdedOrigin.equals(headers.get("webhook-request-origin"))) {
//			return ResponseEntity.ok().header("WebHook-Allowed-Origin", "*").build();
//		}
//		return ResponseEntity.ok("Bad Options Request");
		return ResponseEntity.ok().header("WebHook-Allowed-Origin", "*").build();
	}

	@RequestMapping(value = "/eventhandler/{hubName}/{eventName}", method = RequestMethod.POST)
	public ResponseEntity<?> handle(@RequestHeader Map<String, String> headers, @PathVariable String hubName,
			@PathVariable String eventName, @RequestBody String json) throws Exception {
		System.out.println("handle headers:- " + headers);
		// System.out.println("hub name:- " + hubName);
		// System.out.println("eventName:- " + eventName);
		// System.out.println("body:- " + json);
		String event = headers.get("ce-type");
		String userId = headers.get("ce-userid");
		String hub = headers.get("ce-hub");
		if ("azure.webpubsub.sys.connect".equals(event)) {
			System.out.println(userId + " raised connect request");
		} else if ("azure.webpubsub.sys.connected".equals(event)) {
			UserStatusMessage msg = new UserStatusMessage();
			msg.setId(userId);
			msg.setMessage(userId + " is connected");
			ResponseData data = new ResponseData();
			data.setUserStatusMessage(msg);
			System.out.println(userId + " is connected");
			if ("notification".equals(hub)) {
				pubSubServiceClientNotification.sendToAll(new ObjectMapper().writeValueAsString(data),
						WebPubSubContentType.APPLICATION_JSON);
			} else {
				pubSubServiceClientOVC.sendToAll(new ObjectMapper().writeValueAsString(data),
						WebPubSubContentType.APPLICATION_JSON);
			}

		} else if ("azure.webpubsub.sys.disconnected".equals(event)) {
			System.out.println(userId + " is disconnected");
		} else if ("azure.webpubsub.user.message".equals(event)) {
			System.out.println(userId + " is joined and got message");
			ResponseData data = new ResponseData();
			AppointmentStatusMsg statusMsg = new AppointmentStatusMsg((int) (Math.random() * 30), json, userId);
			data.setAppointmentStatusMsg(statusMsg);
			if ("notification".equals(hub)) {
				pubSubServiceClientNotification.sendToAll(new ObjectMapper().writeValueAsString(data),
						WebPubSubContentType.APPLICATION_JSON);
			} else {
				pubSubServiceClientOVC.sendToAll(new ObjectMapper().writeValueAsString(data),
						WebPubSubContentType.APPLICATION_JSON);
			}
		}
		return ResponseEntity.ok().build();
	}

}
