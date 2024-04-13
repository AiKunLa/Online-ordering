package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import io.swagger.models.auth.In;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;


    /**
     * 计算从begin到end之间的日期
     *
     * @param begin
     * @param end
     * @return
     */
    public List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            //加一天
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        return dateList;
    }

    /**
     * 根据时间区间统计营业额
     *
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO getTurnover(LocalDate begin, LocalDate end) {
        //计算从begin到end之间的日期
        List<LocalDate> dateList = getDateList(begin, end);


        //计算每天的营业额 状态为已完成订单的总金额
        List<Double> turnoverList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap<>();
            map.put("status", Orders.COMPLETED);
            map.put("begin", beginTime);
            map.put("end", endTime);

            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }


    /**
     * 用户数据统计
     *
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        //计算时间
        List<LocalDate> dateList = getDateList(begin, end);
        //用户总数
        List<Integer> totalUserList = new ArrayList<>();
        //新增用户数
        List<Integer> newUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            //新增
            Integer newUserNum = userMapper.newUserNum(beginTime, endTime);
            newUserList.add(newUserNum);
            //总数
            Integer totalUserNum = userMapper.totalUserNum(endTime);
            totalUserList.add(totalUserNum);
        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    /**
     * 订单数据统计
     *
     * @param begin
     * @param end
     * @return
     */
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        //计算时间
        List<LocalDate> dateList = getDateList(begin, end);

        //每日订单数
        List<Integer> orderCountList = new ArrayList<>();
        //每日有效订单数
        List<Integer> validOrderCountList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", null);
            //每日订单数
            Integer orderCount = orderMapper.getOrderCount(map);
            orderCountList.add(orderCount);

            //每日有效订单数
            map.put("status", Orders.COMPLETED);
            Integer validOrder = orderMapper.getOrderCount(map);
            validOrderCountList.add(validOrder);
        }

        //订单总数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();

        //有效订单数
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();

        //订单完成率
        Double orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;


        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                //每日订单数
                .orderCountList(StringUtils.join(orderCountList, ","))
                //每日订单有效数
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                //订单总数
                .totalOrderCount(totalOrderCount)
                //订单有效数
                .validOrderCount(validOrderCount)
                //完成率
                .orderCompletionRate(orderCompletionRate)
                .build();
    }


    /**
     * 销量排名统计
     *
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        //获取商品名称
        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getSalesTop10(beginTime, endTime);

        //商品名称列表，以逗号分隔，例如：鱼香肉丝,宫保鸡丁,水煮鱼
        String nameList = StringUtils.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList()), ",");

        //销量列表，以逗号分隔，例如：260,215,200
        String numberList = StringUtils.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList()), ",");


        //统计数量
        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }


    /**
     * 导出运营数据报表
     *
     * @param response
     */
    public void exportBusinessData(HttpServletResponse response) {

        //查询30营业数据
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);

        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN)
                , LocalDateTime.of(end, LocalTime.MAX));

        //基于提供好的模板文件创建一个新的Excel表格对象
        //模板路径
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try {
            XSSFWorkbook excel = new XSSFWorkbook(inputStream);
            //获取sheet
            XSSFSheet sheet1 = excel.getSheet("Sheet1");

            //添加时间范围
            sheet1.getRow(1).getCell(1).setCellValue(begin + " 至 " + end);
            //添加概览数据
            sheet1.getRow(3).getCell(2).setCellValue(businessData.getTurnover());
            sheet1.getRow(3).getCell(4).setCellValue(businessData.getOrderCompletionRate());
            sheet1.getRow(3).getCell(6).setCellValue(businessData.getNewUsers());
            sheet1.getRow(4).getCell(2).setCellValue(businessData.getValidOrderCount());
            sheet1.getRow(4).getCell(4).setCellValue(businessData.getUnitPrice());

            //明细数据
            for (int i = 0; i < 30; i++) {
                BusinessDataVO data = workspaceService.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN),
                        LocalDateTime.of(end, LocalTime.MIN));

                XSSFRow row = sheet1.getRow(7 + i);
                row.getCell(1).setCellValue(begin.toString());
                row.getCell(2).setCellValue(data.getTurnover());
                row.getCell(3).setCellValue(data.getValidOrderCount());
                row.getCell(4).setCellValue(data.getOrderCompletionRate());
                row.getCell(5).setCellValue(data.getUnitPrice());
                row.getCell(6).setCellValue(data.getNewUsers());

                begin = begin.plusDays(1);
            }

            //输出到浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);

            //关闭资源
            outputStream.close();
            excel.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
