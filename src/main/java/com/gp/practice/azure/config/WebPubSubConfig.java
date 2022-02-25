package com.gp.practice.azure.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.azure.messaging.webpubsub.WebPubSubServiceClient;
import com.azure.messaging.webpubsub.WebPubSubServiceClientBuilder;

@Component
public class WebPubSubConfig {

	@Bean(name = "ovchubClient")
	public WebPubSubServiceClient getWebPubSubServerClient() {
		WebPubSubServiceClient client = new WebPubSubServiceClientBuilder().connectionString(
				"Endpoint=https://rajwebpubsubdemo.webpubsub.azure.com;AccessKey=4W+BtG66BheERI6NGTT2vJV/UGkt/kWbnQIjNnEcXE8=;Version=1.0;")
				.hub("ovc").buildClient();
		System.out.println("ovc hub client created.. " + client);
		return client;
	}
	
	@Bean(name = "notificationhubClient")
	public WebPubSubServiceClient getWebPubSubServerNotificationClient() {
		WebPubSubServiceClient client = new WebPubSubServiceClientBuilder().connectionString(
				"Endpoint=https://rajwebpubsubdemo.webpubsub.azure.com;AccessKey=4W+BtG66BheERI6NGTT2vJV/UGkt/kWbnQIjNnEcXE8=;Version=1.0;")
				.hub("notification").buildClient();
		System.out.println("notification hub client created.. " + client);
		return client;
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*")
						.allowedMethods("GET", "PUT", "POST", "PATCH", "DELETE", "OPTIONS")
						.allowedHeaders("Access-Control-Allow-Headers", "Access-Control-Allow-Origin",
								"Access-Control-Request-Method", "Access-Control-Request-Headers", "Origin",
								"Cache-Control", "Content-Type", "Authorization");
			}
		};
	}

}
