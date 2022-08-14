package com.yikekong.dto;

import lombok.Data;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

@Data
@Measurement(name = "quota")
public class QuotaInfo {

    @Column(name = "deviceId",tag = true)
    private String deviceId;

    @Column(name = "quotaId",tag = true)
    private String quotaId;

    @Column(name = "quotaName",tag = true)
    private String quotaName;

    @Column(name = "alarm" ,tag = true)
    private String alarm;

    @Column(name = "level" ,tag = true)
    private String level;

    @Column(name = "alarmName" ,tag = true)
    private String alarmName;

    @Column(name = "unit",tag = true)
    private String unit;

    @Column(name = "referenceValue",tag = true)
    private String referenceValue;

    @Column(name = "value")
    private Double value;

    @Column(name = "stringValue")
    private String stringValue;

}
