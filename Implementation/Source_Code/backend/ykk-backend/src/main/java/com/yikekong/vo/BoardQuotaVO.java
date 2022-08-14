package com.yikekong.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BoardQuotaVO implements Serializable {

    private List<String> xdata;

    private List<BoardQuotaData> series;

    private String name;

}



