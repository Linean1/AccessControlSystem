package com.accesscontrol.accesscontrolsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.accesscontrol.dao")
@EntityScan(basePackages = "com.accesscontrol.entity")
@ComponentScan(basePackages = "com.accesscontrol") // Добавляем сканирование всех пакетов
public class AccessControlSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(AccessControlSystemApplication.class, args);
	}
}