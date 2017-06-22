package com.julysky.repository;

import co.paralleluniverse.fibers.Suspendable;
import com.julysky.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by haoyifen on 2017/6/1 8:52.
 */
public interface UserRepository extends CrudRepository<User,Long>{
	@Suspendable
	List<User> findByAge(@Param("age") Integer age);
}
