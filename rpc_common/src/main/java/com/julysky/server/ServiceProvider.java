package com.julysky.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import com.julysky.autoConfiguration.RPCService;
import com.julysky.pojo.RpcCall;

import co.paralleluniverse.fibers.Suspendable;
import javafx.util.Pair;

/**
 * Created by haoyifen on 2017/6/1 9:00.
 */
public class ServiceProvider implements ApplicationContextAware {
	private static Logger logger = LoggerFactory.getLogger(ServiceProvider.class);

	private ApplicationContext context;

	private Map<String, Object> classNameToServiceMap = new ConcurrentHashMap<>();

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
		Map<String, Object> rpcServiceBean = context.getBeansWithAnnotation(RPCService.class);
		if (rpcServiceBean.isEmpty()) {
			return;
		}
		List<Pair<Class, Object>> services = rpcServiceBean.values().stream().flatMap(service -> {
			RPCService annotation = AnnotationUtils.findAnnotation(service.getClass(), RPCService.class);
			Class[] serviceInterfaceNames = annotation.value();
			return Arrays.stream(serviceInterfaceNames).map(interfaceName -> new Pair<>(interfaceName, service));
		}).collect(Collectors.toList());

		for (Pair<Class, Object> service : services) {
			classNameToServiceMap.put(service.getKey().getName(), service.getValue());
		}
	}

	@Suspendable
	public Object invoke(RpcCall rpcCall) {
		String className = rpcCall.getClassName();
		Object service = classNameToServiceMap.get(className);
		Assert.notNull(service, className + "service not exist");
		String[] parameterTypes = rpcCall.getParameterTypes();
		Class[] parameterClasses = Arrays.stream(parameterTypes).map(typeStr -> {
			try {
				return ClassUtils.forName(typeStr, ClassUtils.getDefaultClassLoader());
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}).toArray(Class[]::new);
		Method method = ClassUtils.getMethod(service.getClass(), rpcCall.getMethodName(), parameterClasses);
		Assert.notNull(method, rpcCall.getMethodName() + " method for service: " + className + " should not be null");
		Object result=null;
		try {
			result = method.invoke(service, rpcCall.getParameters());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		// Object result = ReflectionUtils.invokeMethod(method, service, rpcCall.getParameters());
		// logger.info("result is "+result);
		return result;
	}
}
