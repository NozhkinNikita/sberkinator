package com.sbt.hackaton.web;

//import com.vk.api.sdk.client.TransportClient;
//import com.vk.api.sdk.client.VkApiClient;
//import com.vk.api.sdk.httpclient.HttpTransportClient;
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

//    @Bean
//    public TransportClient transportClient() {
//        return HttpTransportClient.getInstance();
//    }
//
//    @Bean
//    public VkApiClient vkApiClient(TransportClient transportClient) {
//        return new VkApiClient(transportClient);
//    }
}
