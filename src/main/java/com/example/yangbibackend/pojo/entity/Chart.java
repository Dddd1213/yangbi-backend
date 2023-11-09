package com.example.yangbibackend.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 图表信息表
 * </p>
 *
 * @author xiaoyangii
 * @since 2023-11-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("chart")
@ApiModel(value="Chart对象", description="图表信息表")
public class Chart implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "分析目标")
    private String goal;

    @ApiModelProperty(value = "图表数据")
    private String chartData;

    @ApiModelProperty(value = "图表类型")
    private String chartType;

    @ApiModelProperty(value = "生成图表数据")
    private String genChart;

    @ApiModelProperty(value = "生成的分析结论")
    private String genResult;

    @ApiModelProperty(value = "任务状态")
    private String status;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "执行信息")
    private String execMessage;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "是否删除")
    private Integer isDelete;


}
