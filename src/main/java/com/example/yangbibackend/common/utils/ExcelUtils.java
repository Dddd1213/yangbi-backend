package com.example.yangbibackend.common.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Excel 相关工具类
 */
@Slf4j
public class ExcelUtils {

    public static String excelToCsv(MultipartFile multipartFile) throws IOException {
       //读取数据
//        File file = ResourceUtils.getFile("classpath:test_excel.xlsx");
        List<Map<Integer,String>> list = EasyExcel.read(multipartFile.getInputStream())
                .excelType(ExcelTypeEnum.XLSX)
                .sheet()
                .headRowNumber(0)
                .doReadSync();
        if(CollectionUtils.isEmpty(list)){
            return " ";
        }
        //转化为csv
        StringBuilder stringBuilder = new StringBuilder();
        //读取表头
        LinkedHashMap<Integer, String> hearderMap = (LinkedHashMap)list.get(0);
        List<String> headerList = hearderMap.values().stream().filter(Objects::nonNull).collect(Collectors.toList());
        stringBuilder.append(StringUtils.join(headerList,",")).append("\n");
        for(int i=1;i<list.size();i++){
            LinkedHashMap<Integer, String> dataMap = (LinkedHashMap)list.get(i);
            List<String> dataList = dataMap.values().stream().filter(Objects::nonNull).collect(Collectors.toList());
            stringBuilder.append(StringUtils.join(dataList,",")).append("\n");
        }
        log.info(stringBuilder.toString());
        return stringBuilder.toString();
    }

//    public static void main(String[] args) throws FileNotFoundException {
//        excelToCsv(null);
//    }
}
