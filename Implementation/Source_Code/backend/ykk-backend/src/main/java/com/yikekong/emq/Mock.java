package com.yikekong.emq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;
import com.yikekong.entity.QuotaEntity;
import com.yikekong.service.QuotaService;
import com.yikekong.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class Mock {


    @Autowired
    private QuotaService quotaService;

    @Autowired
    private EmqClient emqClient;

    @Scheduled(cron = "0/10 * * * * ?")
    public void addDatas(){
        System.out.println(LocalDateTime.now()+"Simulated data generation");

        List<QuotaEntity> quotaList = quotaService.list();  //Get all quota definitions
        //Simulation of 10 devices
        for(int i=10;i<20;i++){
            String deviceId=100000+i+""; //device id

            for( QuotaEntity quotaEntity:quotaList ){

                Map<String,Object> map= Maps.newHashMap();//message
                map.put(quotaEntity.getSnKey(),deviceId); //device sn

                //generate random
                Random random=new Random();
                int quotaValue = random.nextInt(40);  //quota value
                map.put(quotaEntity.getValueKey(),quotaValue);

                try {
                    String json = JsonUtil.serialize(map);
                    emqClient.publish( quotaEntity.getSubject(), json);
                    Thread.sleep(20);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }

}
