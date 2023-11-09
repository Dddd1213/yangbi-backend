package com.example.yangbibackend.pojo.DTO.chart;

import lombok.Data;

import java.io.Serializable;

@Data
public class AddChartDTO  implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;
    /**
     * 名称
     */
    private String name;

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表数据
     */
    private String chartData;

    /**
     * 图表类型
     */
    private String chartType;

}
