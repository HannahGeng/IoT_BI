package com.yikekong.core;

import com.yikekong.emq.EmqClient;
import com.yikekong.service.QuotaService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class Monitor {

    @Autowired
    private EmqClient emqClient;

    @Autowired
    private QuotaService quotaService;


    @PostConstruct
    public void init(){
        System.out.println("Initialization methods, subscription topics");
        emqClient.connect();

        quotaService.getAllSubject().forEach(s -> {
            //Shared Subscription Model
            try {
                emqClient.subscribe("$queue/"+s);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        });

    }

}
