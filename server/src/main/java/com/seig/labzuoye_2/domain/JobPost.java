package com.seig.labzuoye_2.domain;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tb_job_post")
public class JobPost {
    @TableId(type = IdType.AUTO)
    private Integer postId;
    private Integer deptId;
    private String postName;
    private String workTime;
    private String address;
    private BigDecimal salary;
    private String status;
    private LocalDateTime createTime;
    private String imgUrl; // 存储图片访问地址
    @TableField("post_type")
    private String postType;
}
