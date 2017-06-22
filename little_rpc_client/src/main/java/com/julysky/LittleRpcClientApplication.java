package com.julysky;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.julysky.autoConfiguration.EnableServiceDiscovery;
import com.julysky.client.ServiceProxyUtil;
import com.julysky.domain.User;
import com.julysky.service.UserService;

import co.paralleluniverse.fibers.Fiber;

@SpringBootApplication
@EnableServiceDiscovery
public class LittleRpcClientApplication {
	private static Logger logger = LoggerFactory.getLogger(LittleRpcClientApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(LittleRpcClientApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(ServiceProxyUtil serviceProxyUtil) {
		return (String... args) -> {
			UserService userService = serviceProxyUtil.serviceProxy("test",UserService.class);
			for (int i = 0; i < 10; i++) {
				Instant startNew = Instant.now();
				logger.info("start new");
				int total = 10000;
				Fiber[] fibers = new Fiber[total];
				for (int j = 0; j < total; j++) {
					int finalI = j;
					Fiber<Void> fiber = new Fiber<Void>(() -> {
						List<User> users = userService.findByAge(24);
						if (finalI % (total-1) == 0) {
							logger.debug(finalI + " : " + users.toString());
						}
					}).start();
					fibers[j] = fiber;
				}
				for (Fiber fiber : fibers) {
					fiber.join();
				}
				Instant finish = Instant.now();
				long usingMills = Duration.between(startNew, finish).toMillis();
				logger.info("finish {} reqs, using {} ms, rqs is {}",total, usingMills,total*1000/usingMills);
				System.out.println(" ");
			}
		};
	}

}
