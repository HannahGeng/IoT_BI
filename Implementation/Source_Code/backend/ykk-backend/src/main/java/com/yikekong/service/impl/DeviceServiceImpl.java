package com.yikekong.service.impl;
import com.google.common.collect.Lists;
import com.yikekong.dto.DeviceDTO;
import com.yikekong.dto.QuotaInfo;
import com.yikekong.es.ESRepository;
import com.yikekong.service.DeviceService;
import com.yikekong.service.QuotaService;
import com.yikekong.vo.DeviceQuotaVO;
import com.yikekong.vo.Pager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DeviceServiceImpl implements DeviceService{


    @Autowired
    private ESRepository esRepository;

    @Override
    public boolean setStatus(String deviceId, Boolean status) {
        DeviceDTO deviceDTO = findDevice(deviceId);
        if( deviceDTO==null ) return false;
        return esRepository.updateStatus(deviceId,status);
    }

    @Override
    public boolean updateTags(String deviceId, String tags) {
        DeviceDTO deviceDTO = findDevice(deviceId);
        if( deviceDTO==null ) return false;
        return esRepository.updateDeviceTag(deviceId,tags);
    }

    @Override
    public Pager<DeviceDTO> queryPage(Long page, Long pageSize, String deviceId, String tags, Integer state) {
        return esRepository.searchDevice(page,pageSize,deviceId,tags,state);
    }

    @Override
    public boolean saveDeviceInfo(DeviceDTO deviceDTO) {
        //Query the device, determine the status of the switch, and if it is off, do not process
        DeviceDTO device= findDevice(deviceDTO.getDeviceId());
        if( device!=null && !device.getStatus() ) return false;

        // If the current device cannot be found, add
        if(device==null){
            esRepository.addDevices( deviceDTO );
        }else{
            //Update alarm information if it can be queried
            esRepository.updateDevicesAlarm(deviceDTO);
        }
        return true;
    }

    @Override
    public void updateOnLine(String deviceId, Boolean online) {

        if( deviceId.startsWith("webclient")  || deviceId.startsWith("monitor") ){
            return;
        }

        DeviceDTO deviceDTO = findDevice(deviceId);
        if(deviceDTO==null) return;
        esRepository.updateOnline(deviceId,online);

    }


    @Autowired
    private QuotaService quotaService;

    @Override
    public Pager<DeviceQuotaVO> queryDeviceQuota(Long page, Long pageSize, String deviceId, String tag, Integer state) {

        //1.Query device list

        Pager<DeviceDTO> pager = esRepository.searchDevice(page, pageSize, deviceId, tag, state);


        //2.Check the list of indicators
        List<DeviceQuotaVO> deviceQuotaVOList= Lists.newArrayList();
        pager.getItems().forEach(deviceDTO -> {
            DeviceQuotaVO deviceQuotaVO=new DeviceQuotaVO();
            BeanUtils.copyProperties(deviceDTO, deviceQuotaVO );
            //Search Indicators
            List<QuotaInfo> quotaList = quotaService.getLastQuotaList(deviceDTO.getDeviceId());
            deviceQuotaVO.setQuotaList(quotaList);
            deviceQuotaVOList.add(deviceQuotaVO);
        });

        //3.Wrapping return results
        Pager<DeviceQuotaVO> pageResult=new Pager(pager.getCounts(),pageSize);
        pageResult.setItems(deviceQuotaVOList);

        return pageResult;
    }

    /**
     * Query device by device id
     * @param deviceId
     * @return
     */
    private DeviceDTO findDevice(String deviceId){
        DeviceDTO deviceDTO = esRepository.searchDeviceById(deviceId);
        return deviceDTO;
    }


}
