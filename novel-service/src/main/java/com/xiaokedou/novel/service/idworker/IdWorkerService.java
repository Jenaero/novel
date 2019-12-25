package com.xiaokedou.novel.service.idworker;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * IdWorker
 *
 * @Auther: renyajian
 * @Date: 2019/12/24
 */
@Component
public class IdWorkerService {

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
        String key = "ORDER_ID_" + prefix;
        String orderId = null;
        try {
            Long increment = redisTemplate.opsForValue().increment(key, 1);
            //往前补6位
            orderId = prefix + String.format("%1$06d", increment);
        } catch (Exception e) {
            throw new RuntimeException("生成订单号失败:" + e);
        }
        return orderId;
    }

    public Long getOrderId(Date now) {

        String orderIdPrefix = getOrderIdPrefix(now);
        String orderId = orderId(orderIdPrefix);
        return Long.parseLong(orderId);
    }

    public List<Long> getOrderIds(Date now, int size) {
        List <Long> ids = Lists.newArrayList();
        String orderIdPrefix = getOrderIdPrefix(now);
        for (int i = 0; i < size; i++) {
            String orderId = orderId(orderIdPrefix);
            ids.add(Long.parseLong(orderId));
        }
        return ids;
    }
}
