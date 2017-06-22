package com.julysky.registry;

/**
 * Created by haoyifen on 2017/6/19 13:37.
 */

public class ServiceCommon {
	public static String serviceBasePath = "service";
	public static String getServicePath(String serviceName) {
		return "/service/" + serviceName;
	}
}
