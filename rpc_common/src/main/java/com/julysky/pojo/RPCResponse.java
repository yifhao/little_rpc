package com.julysky.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by haoyifen on 2017/6/16 22:08.
 */
@Data
@NoArgsConstructor
public class RPCResponse {
	private String requestId;
	private int code;
	private String msg;
	private Object data;

	public static RPCResponse ok(String requestId,Object data) {
		RPCResponse rpcResponse = new RPCResponse();
		rpcResponse.setRequestId(requestId);
		rpcResponse.setCode(0);
		rpcResponse.setData(data);
		return rpcResponse;
	}
}
