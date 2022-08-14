package com.yikekong.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class QuotaDTO implements Serializable {

    private Integer id;

    private String quotaName;

    private String unit;

    private String subject;

    private String valueKey;

    private String valueType;

    private String snKey;

    private String webhook;

    private String referenceValue;

    private Double value;

    private String stringValue;

    private String deviceId;

    private String alarm;

    private String alarmName;

    private String level;

    private String alarmWebHook;

    private Integer cycle;


}
