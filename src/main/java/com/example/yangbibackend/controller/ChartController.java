package com.example.yangbibackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.yangbibackend.common.enumeration.ErrorCode;
import com.example.yangbibackend.common.exception.BusinessException;
import com.example.yangbibackend.common.result.Result;
import com.example.yangbibackend.common.utils.ExcelUtils;
import com.example.yangbibackend.common.utils.ResultUtils;
import com.example.yangbibackend.manager.AiManager;
import com.example.yangbibackend.pojo.DTO.chart.AddChartDTO;
import com.example.yangbibackend.pojo.DTO.chart.GenChartByAiDTO;
import com.example.yangbibackend.pojo.DTO.chart.QueryChartDTO;
import com.example.yangbibackend.pojo.DTO.common.DeleteDTO;
import com.example.yangbibackend.pojo.VO.chart.AddChartVO;
import com.example.yangbibackend.pojo.VO.chart.BiVO;
import com.example.yangbibackend.pojo.entity.Chart;
import com.example.yangbibackend.service.ChartService;
import com.example.yangbibackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController {

    @Autowired
    ChartService chartService;

    @Autowired
    AiManager aiManager;

    @Autowired
    UserService userService;

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

    @PostMapping("/gen")
    public Result<BiVO> genChartByAi(@RequestPart("file")MultipartFile multipartFile,
                                       GenChartByAiDTO genChartByAiDTO, HttpServletRequest request) throws IOException {
        String name = genChartByAiDTO.getName();
        String goal = genChartByAiDTO.getGoal();
        String chartType = genChartByAiDTO.getChartType();

        if(StringUtils.isAnyBlank(goal)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"目标为空");
        }

//        final String prompt = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
//           "分析需求：\n" +
//           "{数据分析的需求或者目标}\n" +
//           "原始数据：\n" +
//           "{csv格式的原始数据，用,作为分隔符}\n" +
//           "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n" +
//           "【【【【【\n" +
//           "{前端 Echarts V5 的 option 配置对象js代码，合理地将数据进行可视化，不要生成任何多余的内容，比如注释}\n" +
//           "【【【【【\n" +
//           "{明确的数据分析结论、越详细越好，不要生成多余的注释}";

        StringBuilder userInput = new StringBuilder();
//        userInput.append("你是一个数据分析师，接下来我会给你我的分析目标和数据，请告诉我你的分析结论").append("\n");
        userInput.append("分析需求：").append(goal).append("\n");
        if(StringUtils.isNotBlank(chartType)){
            userInput.append("，请使用"+chartType+"进行代码生成");
        }
        String result = ExcelUtils.excelToCsv(multipartFile);
        userInput.append("原始数据").append(result).append("\n");

        String userOutput = aiManager.doChart(userInput.toString());
        String[] split = userOutput.split("【【【【【");
        if(split.length<3){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"Ai生成错误");
        }
        BiVO biVO = new BiVO();
        biVO.setGenChart(split[1]);
        biVO.setGenResult(split[2]);

        //把图表插入数据库
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(result);
        chart.setChartType(chartType);
        chart.setGenChart(split[1]);
        chart.setGenResult(split[2]);
        chart.setUserId(userService.getLoginUser(request).getId());

        boolean b = chartService.save(chart);
        if(!b){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"图表保存失败");
        }

        return ResultUtils.success(biVO);
    }

}
