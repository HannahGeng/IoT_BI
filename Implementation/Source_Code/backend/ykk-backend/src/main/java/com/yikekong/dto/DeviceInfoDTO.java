package com.yikekong.dto;

import lombok.Data;

import java.util.List;

@Data
public class DeviceInfoDTO {

    private DeviceDTO device;

    private List<QuotaDTO> quotaList;


}
