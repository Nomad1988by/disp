package com.example.disp;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages ={"com.example.disp"})
@EnableAutoConfiguration
public class DispApplication {

	public static void main(String[] args) {
		SpringApplication.run(DispApplication.class, args);

		

	}

}
