package com.yikekong.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yikekong.dto.DeviceInfoDTO;
import com.yikekong.dto.QuotaAllInfo;
import com.yikekong.dto.QuotaDTO;
import com.yikekong.entity.AlarmEntity;
import com.yikekong.vo.Pager;

import java.util.List;

public interface AlarmService extends IService<AlarmEntity>{
    /**
     * queryPage
     * @param page
     * @param pageSize
     * @param alarmName
     * @param quotaId
     * @return
     */
    IPage<AlarmEntity> queryPage(Long page,Long pageSize,String alarmName,Integer quotaId);

    /**
     * getByQuotaId
     * @param quotaId Quota id
     * @return
     */
    List<AlarmEntity> getByQuotaId(Integer quotaId);


    /**
     * verifyQuota
     * @param quotaDTO
     * @return
     */
    AlarmEntity verifyQuota(QuotaDTO quotaDTO);


    /**
     * verifyDeviceInfo
     * @param deviceInfoDTO
     * @return
     */
    DeviceInfoDTO verifyDeviceInfo(DeviceInfoDTO deviceInfoDTO);


    /**
     * queryAlarmLog
     * @param page
     * @param pageSize
     * @param start
     * @param end
     * @param alarmName
     * @param deviceId
     * @return
     */
    Pager<QuotaAllInfo> queryAlarmLog( Long page,Long pageSize,String start,String end,String alarmName,String deviceId );

}