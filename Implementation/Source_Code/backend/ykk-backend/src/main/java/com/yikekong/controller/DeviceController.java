package com.yikekong.controller;
import com.yikekong.dto.DeviceDTO;
import com.yikekong.service.DeviceService;
import com.yikekong.vo.DeviceQuotaVO;
import com.yikekong.vo.DeviceVO;
import com.yikekong.vo.Pager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/device")
@Slf4j
public class DeviceController{

    @Autowired
    private DeviceService deviceService;


    /**
     * Setting device status
     * @param deviceVO
     * @return
     */
    @PutMapping("/status")
    public boolean setStatus(@RequestBody DeviceVO deviceVO){

       return  deviceService.setStatus( deviceVO.getSn(),deviceVO.getStatus() );

    }



    /**
     * Setting device status
     * @param deviceVO
     * @return
     */
    @PutMapping("/tags")
    public boolean setTags(@RequestBody DeviceVO deviceVO){

        return  deviceService.updateTags( deviceVO.getSn(),deviceVO.getTags());

    }


    /**
     * Paging query device
     * @param page
     * @param pageSize
     * @param sn
     * @param tag
     * @return
     */
    @GetMapping
    public Pager<DeviceDTO> findPage(@RequestParam(value="page",required = false,defaultValue = "1") Long page,
                                     @RequestParam(value = "pageSize",required = false,defaultValue = "10") Long pageSize,
                                     @RequestParam(value = "sn",required = false) String sn,
                                     @RequestParam(value = "tag",required = false)  String tag ){

        return deviceService.queryPage(page,pageSize,sn,tag,null);
    }


    /**
     * Receive device disconnection information
     * @param param
     */
    @PostMapping("/clientAction")
    public void clientAction(@RequestBody  Map<String,String> param){
        System.out.println(param);
        String deviceId = param.get("clientid");  //Extract device id
        if( param.get("action").equals("client_connected") ){ //If it is networked
            deviceService.updateOnLine(deviceId,true);
        }
        if( param.get("action").equals("client_disconnected") ){ //If it is disconnected
            deviceService.updateOnLine(deviceId,false);
        }
    }


    /**
     * Equipment Details
     * @param page
     * @param pageSize
     * @param deviceId
     * @param tag
     * @param state
     * @return
     */
    @GetMapping("/deviceQuota")
    public  Pager<DeviceQuotaVO> queryQuotaData(@RequestParam(value="page",required = false,defaultValue = "1") Long page,
                                                @RequestParam(value = "pageSize",required = false,defaultValue = "10") Long pageSize,
                                                @RequestParam(value = "deviceId",required = false) String deviceId,
                                                @RequestParam(value = "tag",required = false)  String tag,
                                                @RequestParam(value = "state",required = false)  Integer state){


            return deviceService.queryDeviceQuota(page,pageSize,deviceId,tag,state);
    }


}
