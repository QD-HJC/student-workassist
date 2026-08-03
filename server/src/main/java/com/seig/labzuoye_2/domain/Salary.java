package com.seig.labzuoye_2.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tb_salary")
public class Salary {

    @TableId(type = IdType.AUTO)
    private Integer salaryId;
    private Integer studentId;
    private Integer postId;
    private String month;
    private Double totalHour;
    private Double totalSalary;
    private String payStatus;
    private LocalDateTime payTime;

    // ✅ 非数据库字段 - 用于前端展示
    @TableField(exist = false)
    private String postName;      // 岗位名称

    @TableField(exist = false)
    private String studentName;   // 学生姓名
}