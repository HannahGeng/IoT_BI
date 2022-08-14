package com.yikekong.vo;

import com.yikekong.dto.QuotaInfo;
import lombok.Data;

import java.util.List;

/**
 * DeviceQuota vo
 */
@Data
public class DeviceQuotaVO {

    private String deviceId;

    private Boolean online;

    private Integer level;

    private List<QuotaInfo> quotaList;
        
}
