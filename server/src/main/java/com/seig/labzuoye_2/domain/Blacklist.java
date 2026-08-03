package com.seig.labzuoye_2.domain;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tb_blacklist")
public class Blacklist {
    @TableId(type = IdType.AUTO)
    private Integer blackId;
    private Integer studentId;
    private String reason;
    private LocalDateTime createTime;
    private String status;
}
