package com.example.yangbibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yangbibackend.pojo.DTO.chart.AddChartDTO;
import com.example.yangbibackend.pojo.DTO.common.DeleteDTO;
import com.example.yangbibackend.pojo.VO.chart.AddChartVO;
import com.example.yangbibackend.pojo.entity.Chart;
import com.example.yangbibackend.pojo.entity.User;

import javax.servlet.http.HttpServletRequest;

/**
* @author 31067
* @description 针对表【chart(图表信息表)】的数据库操作Service
* @createDate 2023-11-08 16:08:43
*/
public interface ChartService extends IService<Chart>{

    AddChartVO addChart(AddChartDTO addChartDTO, HttpServletRequest request);

    Boolean delete(DeleteDTO deleteDTO,HttpServletRequest request);
}
