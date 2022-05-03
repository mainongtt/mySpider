package com.my;

import com.my.spider.Handle.SpiderHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

@SpringBootApplication
@ComponentScan(basePackages = {"com.my.spider"})
public class MySpiderApplication {
    @Autowired
    private SpiderHandle spiderHandle;

    public static void main(String[] args) {
        SpringApplication.run(MySpiderApplication.class, args);
    }
    @PostConstruct
    public void task(){
        spiderHandle.spiderData();
    }
}
