package com.seig.labzuoye_2.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("tb_attendance")
public class Attendance {

    @TableId(type = IdType.AUTO)
    private Integer attendId;
    private Integer studentId;
    private Integer postId;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private LocalDate workDate;
    private String status;

    @TableField(exist = false)
    private String postName;

    @TableField(exist = false)
    private String studentName;  // 新增

    @TableField(exist = false)
    private Double hour;
}