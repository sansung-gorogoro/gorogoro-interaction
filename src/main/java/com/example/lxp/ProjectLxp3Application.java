package com.example.lxp;

import com.example.lxp.common.messaging.config.MessagingProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(
        MessagingProps.class
)
public class ProjectLxp3Application {

    public static void main(String[] args) {
        SpringApplication.run(ProjectLxp3Application.class, args);
    }

}
