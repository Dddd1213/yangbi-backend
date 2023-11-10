package com.example.yangbibackend.pojo.DTO.chart;

import com.example.yangbibackend.pojo.DTO.common.PageDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
public class QueryChartDTO extends PageDTO implements Serializable {
    /**
     * id
     *
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表类型
     */
    private String chartType;

    /**
     * 用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}
