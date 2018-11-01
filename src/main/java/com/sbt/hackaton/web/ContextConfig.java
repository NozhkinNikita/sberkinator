package com.sbt.hackaton.web;

import com.sbt.hackaton.web.messages.AppMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class ContextConfig {

    @Bean(name = "queueToClient")
    public LinkedBlockingQueue<AppMessage> queueToClient() {
        return new LinkedBlockingQueue<>();
    }

    @Bean(name = "queueToApp")
    public LinkedBlockingQueue<AppMessage> queueToApp() {
        return new LinkedBlockingQueue<>();
    }
}
