package com.example.yangbibackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yangbibackend.pojo.DTO.chart.AddChartDTO;
import com.example.yangbibackend.pojo.DTO.common.DeleteDTO;
import com.example.yangbibackend.pojo.VO.chart.AddChartVO;
import com.example.yangbibackend.pojo.VO.chart.BiVO;
import com.example.yangbibackend.pojo.entity.Chart;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 31067
* @description 针对表【chart(图表信息表)】的数据库操作Service
* @createDate 2023-11-08 16:08:43
*/
public interface ChartService extends IService<Chart>{

    AddChartVO addChart(AddChartDTO addChartDTO, HttpServletRequest request);

    public Boolean delete(DeleteDTO deleteDTO,Long userid);

    Page<Chart> listMyChartPage(long current, long size, HttpServletRequest request);

    List<Chart> listMyChart(Long userid);

//    Boolean createOneChart(String result);
}
