package com.julysky.autoConfiguration;

import com.julysky.registry.ServiceDiscovery;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by haoyifen on 2017/6/19 18:02.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ServiceDiscoveryAutoConfiguration.class)
public @interface EnableServiceDiscovery {
}
