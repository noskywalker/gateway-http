package com.baidu.fbu.mtp.listoperations;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created on 15:18 11/20/2015.
 *
 * @author skywalker
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring.xml")
public class RedisTest {

    Logger logger = LoggerFactory.getLogger(RedisTest.class);

    @Resource
    RedisTemplate<String, List<String>> template;

    @Test
    public void testList() {
        List<String> l = Lists.newArrayList();
        l.add("111");
        l.add("222");
        l.add("333");
        template.opsForValue().set("_list", l, 30, TimeUnit.SECONDS);
        List<String> r = template.opsForValue().get("_list");
        logger.info("result: {}", r);
    }
}
