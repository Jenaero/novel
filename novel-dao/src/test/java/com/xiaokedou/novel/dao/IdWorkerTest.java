package com.xiaokedou.novel.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Calendar;
import java.util.Date;

/**
 * IdWorkerTest
 *
 * @Auther: renyajian
 * @Date: 2019/12/24
 */
@SpringBootTest
public class IdWorkerTest {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取年的后两位加上一年多少天+当前小时数作为前缀
     *
     * @param date
     * @return
     */
    public String getOrderIdPrefix(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        //补两位,因为一年最多三位数
        String monthFormat = String.format("%1$02d", month + 1);
        //补两位，因为日最多两位数
        String dayFormat = String.format("%1$02d", day);
        //补两位，因为小时最多两位数
        String hourFormat = String.format("%1$02d", hour);
        return year + monthFormat + dayFormat + hourFormat;
    }

    /**
     * 生成订单
     *
     * @param prefix
     * @return
     */
    public String orderId(String prefix) {
        String key = "DEMO_ORDER_ID_" + prefix;
        String orderId = null;
        try {
            Long increment = redisTemplate.opsForValue().increment(key, 1);
            //往前补6位
            orderId = prefix + String.format("%1$06d", increment);
        } catch (Exception e) {
            System.out.println("生成订单号失败");
            e.printStackTrace();
        }
        return orderId;
    }

    @Test
    public void testRedis() {
        long startMillis = System.currentTimeMillis();
        String orderIdPrefix = getOrderIdPrefix(new Date());
        for (int i = 0; i < 1000; i++) {
            String aLong = orderId(orderIdPrefix);
            System.out.println(aLong);
        }
        long endMillis = System.currentTimeMillis();
        System.out.println("生成速度:" + (endMillis - startMillis) + ",单位毫秒");
    }

}
