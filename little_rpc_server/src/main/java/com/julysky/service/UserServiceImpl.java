package com.julysky.service;

import java.util.Collections;
import java.util.List;

import co.paralleluniverse.fibers.SuspendExecution;
import org.springframework.beans.factory.annotation.Autowired;

import com.julysky.autoConfiguration.RPCService;
import com.julysky.domain.User;
import com.julysky.repository.UserRepository;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.Suspendable;

/**
 * Created by haoyifen on 2017/5/31 21:11.
 */
@RPCService(UserService.class)
public class UserServiceImpl implements UserService {
	@Autowired private UserRepository userRepository;

	@Override
	public User get(Long id) {
		return userRepository.findOne(id);
	}

	@Suspendable
	@Override
	public List<User> findByAge(Integer age)  {
//		try {
//			Fiber.sleep(1000);
//		} catch (InterruptedException | SuspendExecution e) {
//			e.printStackTrace();
//		}
		 return Collections.singletonList(new User("haoyifen", 24));
//		return userRepository.findByAge(age);
	}
}
