package com.yikekong.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName(value = "tb_quota")
@Data
public class QuotaEntity{
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    private String name;

    private String unit;

    private String subject;

    private String valueKey;

    private String valueType;

    private String snKey;

    private String webhook;

    private String referenceValue;
}
