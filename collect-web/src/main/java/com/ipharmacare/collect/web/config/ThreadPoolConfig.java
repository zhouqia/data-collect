package com.ipharmacare.collect.web.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 */
@Configuration
@EnableAsync
@Slf4j
public class ThreadPoolConfig {

    /**
     * 将业务层采集数据写入本地临时文件(单线程执行)
     * @return
     */
    @Bean(name = "asyncToLocalExecutor")
    public Executor asyncToLocalExecutor() {
        log.info("start asyncToLocalExecutor");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(1);
        //配置最大线程数
        executor.setMaxPoolSize(1);
        //配置队列大小
        executor.setQueueCapacity(Integer.MAX_VALUE);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("asyncToLocal-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        return executor;
    }


}
