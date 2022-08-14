package com.yikekong.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Line Chart Wrapper Class
 */
@Data
public class LineVO implements Serializable {

    private List<String>  xdata;//x轴

    private List<Long> series;//数据

}
