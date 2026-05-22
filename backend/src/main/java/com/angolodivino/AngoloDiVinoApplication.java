package com.angolodivino;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AngoloDiVinoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AngoloDiVinoApplication.class, args);
    }
}
