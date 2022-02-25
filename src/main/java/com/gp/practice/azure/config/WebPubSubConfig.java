package com.gp.practice.azure.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.azure.messaging.webpubsub.WebPubSubServiceClient;
import com.azure.messaging.webpubsub.WebPubSubServiceClientBuilder;

@Component
public class WebPubSubConfig {

	@Value("${pubsubendpoint}")
	private String pubSubEndPoint;
	@Value("${pubsub_hub_ovc}")
	private String ovcHubName;
	@Value("${pubsub_hub_notification}")
	private String notificationHubName;

	@Bean(name = "ovchubClient")
	public WebPubSubServiceClient getWebPubSubServerClient() {
		WebPubSubServiceClient client = new WebPubSubServiceClientBuilder().connectionString(pubSubEndPoint)
				.hub(ovcHubName).buildClient();
		System.out.println("ovc hub client created.. " + client);
		return client;
	}

	@Bean(name = "notificationhubClient")
	public WebPubSubServiceClient getWebPubSubServerNotificationClient() {
		WebPubSubServiceClient client = new WebPubSubServiceClientBuilder().connectionString(pubSubEndPoint)
				.hub(notificationHubName).buildClient();
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
