package org.nashtech.reception.preauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PreAuthAdapterApplication {
    public static void main(String[] args) {
        new SpringApplication(PreAuthAdapterApplication.class).run(args);
    }

}