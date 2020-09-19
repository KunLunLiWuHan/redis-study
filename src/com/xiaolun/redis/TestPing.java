package com.xiaolun.redis;

import redis.clients.jedis.Jedis;

public class TestPing {
	private static final String hostAddress = "192.168.10.101"; //远程
//	private static final String hostAddress = "127.0.0.1";      // 本地

	public static void main(String[] args) {
		Jedis jedis = new Jedis(hostAddress, 6379);
		System.out.println("-------连接结果-----" + jedis.ping());
	}
}
