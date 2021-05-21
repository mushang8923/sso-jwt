package com.mubasha.distributed.sso.distributedsecurityorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
//@EnableOAuth2Sso
public class DistributedSecurityOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistributedSecurityOrderApplication.class, args);
    }

}
