package com.example.yangbibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yangbibackend.pojo.entity.Chart;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 31067
* @description 针对表【chart(图表信息表)】的数据库操作Mapper
* @createDate 2023-11-08 16:08:43
* @Entity generator.domain.Chart
*/
@Mapper
public interface ChartMapper extends BaseMapper<Chart> {

}




