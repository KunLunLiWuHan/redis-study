package com.xiaolun.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolUtil {
    private static volatile JedisPool jedisPool = null;

    private JedisPoolUtil(){}

    public static JedisPool getJedisPoolInstance() {
        if(null == jedisPool) {
            synchronized (JedisPoolUtil.class) {
                if(null == jedisPool) {
                    //JedisPool的配置
                    JedisPoolConfig poolConfig = new JedisPoolConfig();
                    //控制一个pool可分配多少个jedis实例
                    poolConfig.setMaxActive(1000);
                    //最多有多少个jedis空闲
                    poolConfig.setMaxIdle(32);
                    poolConfig.setMaxWait(100*1000);
                    //获得一个jedis实例的时候检查连接可用性
                    poolConfig.setTestOnBorrow(true);

                    jedisPool = new JedisPool(poolConfig,"127.0.0.1",6379);
                }
            }
        }
        return jedisPool;
    }

    //把Jedis的连接资源放到池子中。
    public static void release(JedisPool jedisPool, Jedis jedis) {
        if(null != jedis) {
            jedisPool.returnResourceObject(jedis);
        }
    }
}
