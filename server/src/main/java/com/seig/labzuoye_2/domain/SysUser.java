package com.seig.labzuoye_2.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tb_sys_user")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Integer userId;
    private String username;
    private String password;
    private String realName;
    private String role;
    private String phone;
    private String status;
    private LocalDateTime createTime;

    // ✅ 数据库字段：部门ID
    private Integer deptId;

    // ========== 以下是非数据库字段，用于前端显示 ==========

    @TableField(exist = false)
    private String deptName;  // 部门名称（非数据库字段）
}