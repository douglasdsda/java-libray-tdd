package com.souza.librayapi;

import com.souza.librayapi.api.service.EmailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class LibrayApiApplication {

	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}

	/* testar email
	@Autowired
	private EmailService emailService;

	@Bean
	public CommandLineRunner runner(){
		return args -> {
			List<String> emails = Arrays.asList("douglasdsda@gmail.com");
			emailService.sendMails("Testando servico de email", emails);
			System.out.println("EMAIL ENVIADO");
		};
	};
    */

	public static void main(String[] args) {
		SpringApplication.run(LibrayApiApplication.class, args);
	}

}
