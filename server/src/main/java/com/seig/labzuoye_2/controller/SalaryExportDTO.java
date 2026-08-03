package com.seig.labzuoye_2.controller;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

@Data
@ColumnWidth(20)
public class SalaryExportDTO {

    @ExcelProperty(value = "结算月份", index = 0)
    @ColumnWidth(15)
    private String month;

    @ExcelProperty(value = "岗位名称", index = 1)
    @ColumnWidth(25)
    private String postName;

    @ExcelProperty(value = "总工时(h)", index = 2)
    @ColumnWidth(15)
    private Double totalHour;

    @ExcelProperty(value = "应发薪资(元)", index = 3)
    @ColumnWidth(18)
    private Double totalSalary;

    @ExcelProperty(value = "发放状态", index = 4)
    @ColumnWidth(12)
    private String payStatus;

    @ExcelProperty(value = "发放时间", index = 5)
    @ColumnWidth(20)
    private String payTime;
}