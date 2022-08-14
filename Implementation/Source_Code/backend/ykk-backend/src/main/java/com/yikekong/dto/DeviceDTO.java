package com.yikekong.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Device DTO
 */
@Data
public class DeviceDTO implements Serializable {

    private String deviceId;

    private Boolean alarm;

    private String alarmName;

    private Integer level;

    private Boolean online;

    private String tag;

    private Boolean status;

}
