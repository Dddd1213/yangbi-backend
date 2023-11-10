package com.example.yangbibackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.yangbibackend.common.enumeration.ErrorCode;
import com.example.yangbibackend.common.exception.BusinessException;
import com.example.yangbibackend.common.result.Result;
import com.example.yangbibackend.common.utils.ResultUtils;
import com.example.yangbibackend.pojo.DTO.chart.AddChartDTO;
import com.example.yangbibackend.pojo.DTO.chart.QueryChartDTO;
import com.example.yangbibackend.pojo.DTO.common.DeleteDTO;
import com.example.yangbibackend.pojo.VO.chart.AddChartVO;
import com.example.yangbibackend.pojo.entity.Chart;
import com.example.yangbibackend.service.ChartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController {

    @Autowired
    ChartService chartService;

    @PostMapping("/add")
    public Result<AddChartVO> addChart(@RequestBody AddChartDTO addChartDTO, HttpServletRequest request){
        if(addChartDTO==null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(request==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AddChartVO addChartVO = chartService.addChart(addChartDTO,request);

        return ResultUtils.success(addChartVO);
    }

    @PostMapping("/delete")
    public Result<Boolean> deleteChart(@RequestBody DeleteDTO deleteDTO, HttpServletRequest request){

        if(deleteDTO==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(request==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean result = chartService.delete(deleteDTO,request);

        return ResultUtils.success(result);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param queryChartDTO
     * @param request
     * @return
     */
    @PostMapping("/my/list/page")
    public Result<Page<Chart>> listMyChartByPage(@RequestBody QueryChartDTO queryChartDTO,
                                                       HttpServletRequest request) {
        if (queryChartDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long current = queryChartDTO.getCurrent();
        long size = queryChartDTO.getPageSize();

        Page<Chart> chartPage = chartService.listMyChartPage(current,size,request);

        return ResultUtils.success(chartPage);
    }


}
