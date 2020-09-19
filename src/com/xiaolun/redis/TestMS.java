package com.xiaolun.redis;

import redis.clients.jedis.Jedis;

public class TestMS {
    public static void main(String[] args) {
        //主机
        Jedis jedis_M = new Jedis("127.0.0.1",6379);
        //从机
        Jedis jedis_S = new Jedis("127.0.0.1",6380);

        //配置6380端口为从机
        jedis_S.slaveof("127.0.0.1",6379);

        jedis_M.set("test1","1122");

        String result = jedis_S.get("test1");
        //可能读得的数据为null，主要是程序操作太快，Redis还没操作完。
        //此时，我们应该让其多操作几次。
        System.out.println(result);
    }
}
