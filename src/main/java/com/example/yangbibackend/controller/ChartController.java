package com.example.yangbibackend.controller;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.yangbibackend.common.enumeration.ErrorCode;
import com.example.yangbibackend.common.exception.BusinessException;
import com.example.yangbibackend.common.result.Result;
import com.example.yangbibackend.common.utils.ExcelUtils;
import com.example.yangbibackend.common.utils.ResultUtils;
import com.example.yangbibackend.manager.AiManager;
import com.example.yangbibackend.manager.RedisLimiterManager;
import com.example.yangbibackend.mq.MessageProducer;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

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

    @Autowired
    private RedisLimiterManager redisLimiterManager;

    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    MessageProducer messageProducer;


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
        Long id = userService.getLoginUser(request).getId();
        Boolean result = chartService.delete(deleteDTO,id);

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

    @PostMapping("/my/list")
    public Result<List<Chart>> listMyChart(@RequestBody QueryChartDTO queryChartDTO,
                                          HttpServletRequest request){
        if (queryChartDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userid = userService.getLoginUser(request).getId();
        List<Chart> chartList = chartService.listMyChart(userid);

        return ResultUtils.success(chartList);
    }
    /**
     * ai生成（线程池异步）
     * @param multipartFile
     * @param genChartByAiDTO
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/gen")
    public Result<BiVO> genChartByAi(@RequestPart("file")MultipartFile multipartFile,
                                       GenChartByAiDTO genChartByAiDTO, HttpServletRequest request) throws IOException {
        String name = genChartByAiDTO.getName();
        String goal = genChartByAiDTO.getGoal();
        String chartType = genChartByAiDTO.getChartType();

        if(StringUtils.isAnyBlank(goal)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"目标为空");
        }

        //校验文件大小
        long size = multipartFile.getSize();
        final long ONE_MB = 1024*1024L;
        if(size>ONE_MB){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"不支持1M以上的文件上传");
        }
        //校验文件后缀
        String fileName = multipartFile.getOriginalFilename();
        String suffix = FileUtil.getSuffix(fileName);
        List<String> validList = Arrays.asList("xlsx");
        if(!validList.contains(suffix)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"文件格式错误");
        }
        //限流
        Long id = userService.getLoginUser(request).getId();
        redisLimiterManager.doRateLimit("genChartByAi"+id.toString());

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

        //拼接分析需求和原始数据输入
        StringBuilder userInput = new StringBuilder();
//        userInput.append("你是一个数据分析师，接下来我会给你我的分析目标和数据，请告诉我你的分析结论").append("\n");
        userInput.append("分析需求：").append(goal).append("\n");
        if(StringUtils.isNotBlank(chartType)){
            userInput.append("，请使用"+chartType+"进行代码生成");
        }
        String result = ExcelUtils.excelToCsv(multipartFile);
        userInput.append("原始数据").append(result).append("\n");

//        Boolean oneChart = chartService.createOneChart(result);
//        if(!oneChart){
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"分表操作失败");
//        }

        //把图表插入数据库
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setStatus("排队中");
        chart.setChartData(result);
        chart.setChartType(chartType);
        chart.setUserId(userService.getLoginUser(request).getId());

        boolean b = chartService.save(chart);
        if(!b){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"原始数据保存失败");
        }

        BiVO biVO = new BiVO();
        biVO.setCharId(chart.getId());
        CompletableFuture.runAsync(()->{

            Chart chart1 = new Chart();
            chart1.setId(chart.getId());
            chart1.setStatus("ai分析中");
            boolean c = chartService.updateById(chart1);
            if(!c){
                handleChartUpdateError(chart.getId(), "更新图表执行中状态失败");
                return;
            }

            //调用ai
            String userOutput = aiManager.doChart(userInput.toString());
            String[] split = userOutput.split("【【【【【");
            if(split.length<3){
                handleChartUpdateError(chart.getId(), "AI 生成错误");
                return;
            }
            biVO.setGenChart(split[1].trim());
            biVO.setGenResult(split[2].trim());

            Chart chart2 = new Chart();
            chart2.setId(chart.getId());
            chart2.setGenChart(split[1].trim());
            chart2.setGenResult(split[2].trim());
            chart2.setStatus("生成完毕");
            c = chartService.updateById(chart2);
            if(!c){
                handleChartUpdateError(chart.getId(), "更新图表成功状态失败");
            }

        },threadPoolExecutor);

        return ResultUtils.success(biVO);
    }

    /**
     * ai生成（mq异步）
     * @param multipartFile
     * @param genChartByAiDTO
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/genmq")
    public Result<BiVO> genChartByAiMq(@RequestPart("file")MultipartFile multipartFile,
                                     GenChartByAiDTO genChartByAiDTO, HttpServletRequest request) throws IOException {
        String name = genChartByAiDTO.getName();
        String goal = genChartByAiDTO.getGoal();
        String chartType = genChartByAiDTO.getChartType();

        if(StringUtils.isAnyBlank(goal)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"目标为空");
        }

        //校验文件大小
        long size = multipartFile.getSize();
        final long ONE_MB = 1024*1024L;
        if(size>ONE_MB){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"不支持1M以上的文件上传");
        }
        //校验文件后缀
        String fileName = multipartFile.getOriginalFilename();
        String suffix = FileUtil.getSuffix(fileName);
        List<String> validList = Arrays.asList("xlsx");
        if(!validList.contains(suffix)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"文件格式错误");
        }
        //限流
        Long id = userService.getLoginUser(request).getId();
        redisLimiterManager.doRateLimit("genChartByAi"+id.toString());

        String result = ExcelUtils.excelToCsv(multipartFile);

        //把原始图表插入数据库
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setStatus("排队中");
        chart.setChartData(result);
        chart.setChartType(chartType);
        chart.setUserId(userService.getLoginUser(request).getId());

        boolean b = chartService.save(chart);
        if(!b){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"原始数据保存失败");
        }

        messageProducer.sendMessage(chart.getId().toString());

        BiVO biVO = new BiVO();
        biVO.setCharId(chart.getId());
        biVO.setGenChart(chart.getGenChart());
        biVO.setGenResult(chart.getGenResult());

        return ResultUtils.success(biVO);
    }


    public void handleChartUpdateError(long chartId, String execMessage) {
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setStatus("failed");
        updateChartResult.setExecMessage("execMessage");
        boolean updateResult = chartService.updateById(updateChartResult);
        if (!updateResult) {
            log.error("更新图表失败状态失败" + chartId + "," + execMessage);
        }
    }

}
