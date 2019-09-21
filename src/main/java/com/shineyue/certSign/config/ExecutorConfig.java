package com.shineyue.certSign.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Description: TODO 程序线程池的配置
 * @author: luofuwei
 * @date: wrote on 2019/9/20
 */
@Data
@Slf4j
@Configuration
@EnableAsync
public class ExecutorConfig {

    @Value("${threadPoolTaskExecutor.corePoolSize}")
    private int corePoolSize;

    @Value("${threadPoolTaskExecutor.maxPoolSize}")
    private int maxPoolSize;

    @Value("${threadPoolTaskExecutor.queueCapacity}")
    private int queueCapacity;

    @Value("${threadPoolTaskExecutor.keepAliveSeconds}")
    private int keepAliveSeconds;

    @Value("${threadPoolTaskExecutor.threadNamePrefix}")
    private String threadNamePrefix;

    @Bean
    public Executor asyncServiceExecutor() {
        log.info("start asyncServiceExecutor...");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

}
