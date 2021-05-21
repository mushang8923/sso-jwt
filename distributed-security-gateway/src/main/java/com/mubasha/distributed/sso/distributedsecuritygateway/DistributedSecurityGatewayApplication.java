package com.mubasha.distributed.sso.distributedsecuritygateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DistributedSecurityGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistributedSecurityGatewayApplication.class, args);
    }

}
