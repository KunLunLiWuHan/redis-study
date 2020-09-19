package com.xiaolun.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

//事务测试
public class TestTX {
    public boolean transMethod() throws InterruptedException {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        int balance;// 可用余额
        int debt;// 欠额
        int amtToSubtract = 10;// 实刷额度

        //1.为balance（key）添加watch监控
        jedis.watch("balance");
        /**
         * 1.这句话在程序运行期间改在管理员窗口操作，此时5<10，也会进入if方法。
         * 2.会发现balance对应的值发生了更改，debt对应的值没有发生更改。
         */
        //jedis.set("balance","5");
//        Thread.sleep(5000);  //模拟网络延时，正常运行时，不需要添加
        balance = Integer.parseInt(jedis.get("balance"));

        if (balance < amtToSubtract) {
            //如果金额小于实刷金额，放弃本次事务的监控和提交。
            jedis.unwatch();
            System.out.println("modify");
            return false;
        } else {
            System.out.println("***********transaction**********");
            //2.开启事务
            Transaction transaction = jedis.multi();
            //刷钱，具体操作
            transaction.decrBy("balance", amtToSubtract);
            transaction.incrBy("debt", amtToSubtract);
            //3.提交执行
            transaction.exec();
//            List<Object> exec =  transaction.exec();
            //返回的是balance和debt的金额结果
//            for (Object o : exec) {
//                System.out.println("exec执行后输出---"+o);
//            }

            //控制台显示
            balance = Integer.parseInt(jedis.get("balance"));
            debt = Integer.parseInt(jedis.get("debt"));
            System.out.println("****可用余额***" + balance);
            System.out.println("***欠额****" + debt);
            return true;
        }
    }

    /**
     * 1.通俗点讲，watch命令就是标记一个键，如果标记了一个键，
     * 在提交事务前如果该键被别人修改过，那事务就会失败，这种情况通常可以在程序中
     * 重新再尝试一次。
     * 2.首先标记了键balance，然后检查余额是否足够，不足就取消标记，并不做扣减；
     * 足够的话，就启动事务进行更新操作，
     * 如果在此期间键balance被其它人修改， 那在提交事务（执行exec）时就会报错，
     * 程序中通常可以捕获这类错误再重新执行一次，直到成功。
     */
    public static void main(String[] args) throws InterruptedException {
        TestTX test = new TestTX();
        boolean retValue = test.transMethod();
        System.out.println("main retValue-------: " + retValue);
    }
}
