package com.julysky.serialize;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.julysky.pojo.RpcCall;

/**
 * Created by haoyifen on 2017/6/16 20:35.
 */
public class SerializeUtils {
	public static <T> byte[] serialize(T object) {
		Class<T> aClass = (Class<T>) object.getClass();
		Schema<T> schema = RuntimeSchema.getSchema(aClass);
		LinkedBuffer buffer = LinkedBuffer.allocate(4096);
		return ProtostuffIOUtil.toByteArray(object, schema, buffer);
	}

	public static <T> T deserialize(byte[] bytes,Class<T> aClass) {
		Schema<T> schema = RuntimeSchema.getSchema(aClass);
		T object = schema.newMessage();
		ProtostuffIOUtil.mergeFrom(bytes,object,schema);
		return object;
	}

	public static void main(String[] args) {
		RpcCall aRequest = getARequest();
		byte[] bytes = serialize(aRequest);
		RpcCall deserializedRequest = deserialize(bytes, aRequest.getClass());
		boolean equals = aRequest.equals(deserializedRequest);
		System.out.println(equals);
	}

	public static RpcCall getARequest() {
		RpcCall rpcCall = new RpcCall();
		rpcCall.setId("100");
		rpcCall.setClassName("com.julysky.service.UserService");
		rpcCall.setMethodName("get");
		rpcCall.setParameters(new Object[]{24L});
		rpcCall.setParameterTypes(new String[]{Long.class.getName()});
		return rpcCall;
	}
}
