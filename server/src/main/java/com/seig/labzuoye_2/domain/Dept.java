package com.seig.labzuoye_2.domain;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tb_dept")
public class Dept {
    @TableId(type = IdType.AUTO)
    private Integer deptId;
    private String deptName;
    private LocalDateTime createTime;
}