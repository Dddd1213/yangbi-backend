package com.example.yangbibackend.pojo.VO.chart;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class AddChartVO implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private Long id;
}
