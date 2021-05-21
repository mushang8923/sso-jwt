package com.mubasha.distributed.sso.distributedsecurityuaa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
@EnableDiscoveryClient
public class DistributedSecurityUaaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistributedSecurityUaaApplication.class, args);
    }

}
