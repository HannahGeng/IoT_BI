package com.yikekong.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yikekong.entity.GPSEntity;

import java.util.List;
import java.util.Map;

public interface GpsService extends IService<GPSEntity>{

    /**
     * Modify gps indicators
     * @param gpsEntity
     * @return
     */
    boolean update(GPSEntity gpsEntity);

    GPSEntity getGps();



}
