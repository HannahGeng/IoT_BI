package com.yikekong.service;


import com.yikekong.dto.DeviceDTO;
import com.yikekong.vo.DeviceQuotaVO;
import com.yikekong.vo.Pager;

public interface DeviceService  {

    /**
     * setStatus
     * @param deviceId
     * @param status
     * @return
     */
    boolean setStatus(String deviceId,Boolean status);


    /**
     * updateTags
     * @param deviceId
     * @param tags
     * @return
     */
    boolean updateTags(String deviceId,String tags);


    /**
     * queryPage
     * @param page
     * @param pageSize
     * @param deviceId
     * @param tags
     * @param state
     * @return
     */
    Pager<DeviceDTO> queryPage(Long page,Long pageSize,String deviceId,String tags,Integer state );

    /**
     * saveDeviceInfo
     * @param deviceDTO
     * @return
     */
    boolean saveDeviceInfo(DeviceDTO deviceDTO);


    /**
     * updateOnLine
     * @param deviceId
     * @param online
     */
    void updateOnLine(String deviceId,Boolean online);


    /**
     * queryDeviceQuota
     * @param page
     * @param pageSize
     * @param deviceId
     * @param tag
     * @param state
     * @return
     */
    Pager<DeviceQuotaVO> queryDeviceQuota( Long page,Long pageSize,String deviceId,String tag,Integer state );

}
