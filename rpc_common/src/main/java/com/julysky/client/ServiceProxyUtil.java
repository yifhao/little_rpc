package com.julysky.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.strands.SettableFuture;
import com.julysky.pojo.RPCResponse;
import com.julysky.pojo.RpcCall;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by haoyifen on 2017/6/17 23:44.
 */
public class ServiceProxyUtil {
	@Autowired
	private ConnectionManager connectionManager;
	public <T> T serviceProxy(String serviceName,Class<T> serviceInterface) {
		Object proxyInstance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{serviceInterface}, new InvocationHandler() {
			@Suspendable
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws ExecutionException, InterruptedException {
				if (Object.class.equals(method.getDeclaringClass())) {
					switch (method.getName()) {
						case "equals":
							return proxy == args[0];
						case "hashCode":
							return System.identityHashCode(proxy);
						case "toString":
							return proxy.getClass().getName() + "@" +
									Integer.toHexString(System.identityHashCode(proxy)) +
									", with InvocationHandler " + this;
						default:
							throw new IllegalStateException(String.valueOf(method));
					}
				}
				String name = method.getDeclaringClass().getName();
				String methodName = method.getName();
				RpcCall rpcCall = new RpcCall();
				rpcCall.setId(UUID.randomUUID().toString());
				rpcCall.setClassName(name);
				rpcCall.setMethodName(methodName);
				Class<?>[] parameterTypes = method.getParameterTypes();
				String[] parameterTypeStrings = Arrays.stream(parameterTypes).map(Class::getName).toArray(String[]::new);
				rpcCall.setParameterTypes(parameterTypeStrings);
				rpcCall.setParameters(args);
				RPCClientHandler rpcClientHandler = connectionManager.getHandler(serviceName);
				SettableFuture<RPCResponse> rpcResponseSettableFuture = rpcClientHandler.sendRequest(rpcCall);
				RPCResponse rpcResponse = rpcResponseSettableFuture.get();
				return rpcResponse.getData();
			}
		});
		return (T)proxyInstance;
	}
}
