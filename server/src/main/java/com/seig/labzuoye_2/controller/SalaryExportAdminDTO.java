package com.seig.labzuoye_2.controller;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

@Data
@ColumnWidth(20)
public class SalaryExportAdminDTO {

    @ExcelProperty(value = "薪资ID", index = 0)
    @ColumnWidth(12)
    private Integer salaryId;

    @ExcelProperty(value = "学生ID", index = 1)
    @ColumnWidth(12)
    private Integer studentId;

    @ExcelProperty(value = "学生姓名", index = 2)
    @ColumnWidth(15)
    private String studentName;

    @ExcelProperty(value = "岗位名称", index = 3)
    @ColumnWidth(25)
    private String postName;

    @ExcelProperty(value = "结算月份", index = 4)
    @ColumnWidth(15)
    private String month;

    @ExcelProperty(value = "总工时(h)", index = 5)
    @ColumnWidth(15)
    private Double totalHour;

    @ExcelProperty(value = "应发薪资(元)", index = 6)
    @ColumnWidth(18)
    private Double totalSalary;

    @ExcelProperty(value = "发放状态", index = 7)
    @ColumnWidth(12)
    private String payStatus;

    @ExcelProperty(value = "发放时间", index = 8)
    @ColumnWidth(20)
    private String payTime;
}