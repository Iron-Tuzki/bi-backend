package com.yupi.springbootinit.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * @author lanshu
 * @date 2023-07-13
 */
@Configuration
public class ThreadPoolExecutorConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor1() {
        //创建一个线程工厂
        ThreadFactory threadFactory = new ThreadFactory() {
            int count = 0;

            @Override
            public Thread newThread(@NotNull Runnable r) {

                Thread thread = new Thread(r);
                thread.setName("线程" + count);
                count++;
                return thread;
            }

        };

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 4,
                5, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10), threadFactory);
        return threadPoolExecutor;
    }
}
