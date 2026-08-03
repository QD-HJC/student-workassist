package com.seig.labzuoye_2.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tb_student")
public class Student {

    @TableId(type = IdType.AUTO)
    private Integer studentId;
    private String studentNo;
    private String realName;
    private String phone;
    private String className;
    private String status;
    private LocalDateTime createTime;
}