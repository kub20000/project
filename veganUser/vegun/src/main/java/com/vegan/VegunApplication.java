package com.vegan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VegunApplication {

    public static void main(String[] args) {
        SpringApplication.run(VegunApplication.class, args);
        System.out.println("Vegun Application Started");
    }

}
