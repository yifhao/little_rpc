package com.julysky;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.julysky.autoConfiguration.EnableServiceRegistry;
import com.julysky.domain.User;
import com.julysky.repository.UserRepository;

@EnableServiceRegistry
@SpringBootApplication
public class LittleRpcServerApplication {
	private static Logger logger = LoggerFactory.getLogger(LittleRpcServerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(LittleRpcServerApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(UserRepository userRepository) throws Exception {
		return args -> {
			prepareData(userRepository);
		};
	}

	public void prepareData(UserRepository userRepository) {
		User julysky = new User("julysky", 24);
		userRepository.save(julysky);
	}
}
