package com.julysky.service;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import com.julysky.domain.User;

import java.util.List;

/**
 * Created by haoyifen on 2017/6/1 8:51.
 */
public interface UserService {
	@Suspendable
	 User get(Long id) ;

	 @Suspendable
	List<User> findByAge(Integer age);
}
