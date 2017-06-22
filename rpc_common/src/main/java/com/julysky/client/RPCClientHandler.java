package com.julysky.client;

import co.paralleluniverse.strands.SettableFuture;
import com.julysky.pojo.RPCResponse;
import com.julysky.pojo.RpcCall;

/**
 * Created by haoyifen on 2017/6/19 14:58.
 */
public interface RPCClientHandler {
	SettableFuture<RPCResponse> sendRequest(RpcCall rpcCall);
}