package com.yikekong.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.yikekong.dto.DeviceInfoDTO;
import com.yikekong.dto.QuotaDTO;
import com.yikekong.dto.QuotaInfo;
import com.yikekong.entity.QuotaEntity;

import java.util.List;
import java.util.Map;

public interface QuotaService extends IService<QuotaEntity>{

    IPage<QuotaEntity> queryPage(Long page, Long pageSize,String name);
    List<String> getAllSubject();


    /**
     * analysis
     * @param topic
     * @param payloadMap
     * @return
     */
    DeviceInfoDTO analysis(String topic,Map<String,Object> payloadMap);


    /**
     * saveQuotaToInflux
     * @param quotaDTOList
     */
    void saveQuotaToInflux(List<QuotaDTO> quotaDTOList);



    List<QuotaInfo> getLastQuotaList(String deviceId);


    /**
     * queryNumberQuota
     * @param page
     * @param pageSize
     * @return
     */
    IPage<QuotaEntity> queryNumberQuota(Long page,Long pageSize);


}
