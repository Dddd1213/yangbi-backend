package com.example.yangbibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yangbibackend.common.enumeration.ErrorCode;
import com.example.yangbibackend.common.exception.BusinessException;
import com.example.yangbibackend.mapper.ChartMapper;
import com.example.yangbibackend.pojo.DTO.chart.AddChartDTO;
import com.example.yangbibackend.pojo.DTO.common.DeleteDTO;
import com.example.yangbibackend.pojo.VO.chart.AddChartVO;
import com.example.yangbibackend.pojo.VO.user.UserLoginVO;
import com.example.yangbibackend.pojo.entity.Chart;
import com.example.yangbibackend.service.ChartService;
import com.example.yangbibackend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
* @author 31067
* @description 针对表【chart(图表信息表)】的数据库操作Service实现
* @createDate 2023-11-08 16:08:43
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper,Chart> implements ChartService {

    @Autowired
    UserService userService;

    @Override
    public AddChartVO addChart(AddChartDTO addChartDTO, HttpServletRequest request) {

        Chart chart = new Chart();
        BeanUtils.copyProperties(addChartDTO,chart);
        UserLoginVO loginUser = userService.getLoginUser(request);
        chart.setUserId(loginUser.getId());
        boolean result = this.save(chart);
        if(!result)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"添加图表失败，数据库错误");
        }
        AddChartVO addChartVO = AddChartVO.builder()
                .id(chart.getId())
                .build();

        return addChartVO;
    }

    @Override
    public Boolean delete(DeleteDTO deleteDTO,HttpServletRequest request) {

        Long deleteId = deleteDTO.getId();
        UserLoginVO loginUser = userService.getLoginUser(request);

        Chart chart = this.getById(deleteId);
        if(chart==null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        Long chartUserId = chart.getUserId();
        //只有管理员和用户本人可以删除
        if(!userService.isAdmin(request)&&!chartUserId.equals(loginUser.getId())){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        boolean result = this.removeById(deleteId);
        if(!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"删除图表失败");
        }
        return true;
    }

    @Override
    public Page<Chart> listMyChartPage(long current, long size,HttpServletRequest request) {
        //分页参数
        Page<Chart> rowPage = new Page(current, size);

        Long userid = userService.getLoginUser(request).getId();

        //queryWrapper组装查询where条件
        LambdaQueryWrapper<Chart> queryWrapper = new LambdaQueryWrapper<Chart>().eq(Chart::getUserId,userid);
        rowPage = this.baseMapper.selectPage(rowPage, queryWrapper);
        return rowPage;

    }


}




