package com.my.spider.Handle;

import com.my.spider.ThreadPool.UserRejectHandle;
import com.my.spider.ThreadPool.UserThreadFactory;
import com.my.spider.common.SysConstant;
import com.my.spider.service.impl.GoodsInfoServiceImpl;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Component
public class SpiderHandle {
    private Logger logger = LoggerFactory.getLogger(SpiderHandle.class);

    @Autowired
    private GoodsInfoServiceImpl goodsInfoService;
    @Autowired
    private RedisTemplate redisTemplate;

    public void spiderData() {
        //工作队列
        BlockingDeque queue = new LinkedBlockingDeque(1000);
        UserThreadFactory userThreadFactory = new UserThreadFactory("线程池组");
        UserRejectHandle userRejectHandle = new UserRejectHandle();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(50, 100, 60, TimeUnit.SECONDS,
                queue, userThreadFactory, userRejectHandle);

        logger.info("开始爬虫");
        Date startDate = new Date();
        //引入countDownLatch进行线程同步，使主线程等待线程池的所有任务结束，便于计时
        CountDownLatch countDownLatch = new CountDownLatch(100);
        for (int i = 1; i < 201; i += 2) {
            Map<String, String> params = new HashMap<>();
            params.put("keyword", "零食");
            params.put("enc", "utf-8");
            params.put("wc", "零食");
            params.put("page", i + "");
            threadPoolExecutor.execute(() -> {
                goodsInfoService.spiderData(SysConstant.BASE_URL, params);
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadPoolExecutor.shutdown();
        int end = redisTemplate.keys("page").size();
        List<String> pages = redisTemplate.boundListOps("page").range(0, end);
        for(String page : pages){
            // 重新爬取
            Map<String, String> params = new HashMap<>();
            params.put("keyword", "零食");
            params.put("enc", "utf-8");
            params.put("wc", "零食");
            params.put("page", page + "");
            goodsInfoService.spiderData(SysConstant.BASE_URL, params);
        }
        Date endDate = new Date();
        FastDateFormat fdf = FastDateFormat.getInstance(SysConstant.DEFAULT_DATE_FORMAT);
        logger.info("爬虫结束....");
        logger.info("[开始时间:" + fdf.format(startDate) + ",结束时间:" + fdf.format(endDate) + ",耗时:"
                + (endDate.getTime() - startDate.getTime()) + "ms]");

    }
}

