package com.yikekong.emq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yikekong.dto.DeviceInfoDTO;
import com.yikekong.service.AlarmService;
import com.yikekong.service.DeviceService;
import com.yikekong.service.QuotaService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Message Callback Class
 */
@Component
@Slf4j
public class EmqMsgProcess implements MqttCallback {

    @Autowired
    private EmqClient emqClient;


    @Autowired
    private QuotaService quotaService;

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private DeviceService deviceService;

    @Override
    public void connectionLost(Throwable throwable) {
        //Connection lost
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

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        //Received Message
        String payload=new String(mqttMessage.getPayload());
        System.out.println("Received Message："+payload);
        ObjectMapper mapper=new ObjectMapper();
        Map payloadMap = mapper.readValue(payload, Map.class);

        //Parsing Indicators
        DeviceInfoDTO deviceInfoDTO = quotaService.analysis(topic, payloadMap);
        if(deviceInfoDTO!=null){
            //Alarm judgment
            deviceInfoDTO= alarmService.verifyDeviceInfo(deviceInfoDTO);
            //Save device information
            deviceService.saveDeviceInfo(deviceInfoDTO.getDevice());
            //Save indicator data
            quotaService.saveQuotaToInflux(deviceInfoDTO.getQuotaList());

        }

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
