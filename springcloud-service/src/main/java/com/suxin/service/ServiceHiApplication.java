
package com.suxin.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableAutoConfiguration
@EnableEurekaClient
@RestController
public class ServiceHiApplication {
	public static void main(String[] args) {
		   SpringApplication.run(ServiceHiApplication.class, args);
	}
	@Value("${server.port}")
	private String port;
	/**
	 * hi起来
	 * @param name
	 * @return
	 */
	@RequestMapping("/hi")
	public String hi(String name) {
		return name + ",hi from " + port;
	}

}
