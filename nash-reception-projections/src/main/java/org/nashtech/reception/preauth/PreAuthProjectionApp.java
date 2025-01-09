package org.nashtech.reception.preauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PreAuthProjectionApp {
    public static void main(String[] args) {
        new SpringApplication(PreAuthProjectionApp.class).run(args);
    }

}