package com.seig.labzuoye_2.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tb_job_apply")
public class JobApply {

    @TableId(type = IdType.AUTO)
    private Integer applyId;
    private Integer postId;
    private Integer studentId;
    private LocalDateTime applyTime;
    private String interviewResult;
    private String status;

    @TableField(exist = false)
    private JobPost jobPost;

    @TableField(exist = false)
    private String studentName;  // 新增：学生姓名
}