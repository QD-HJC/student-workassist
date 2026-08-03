package com.seig.labzuoye_2.domain;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tb_evaluation")
public class Evaluation {
    @TableId(type = IdType.AUTO)
    private Integer evalId;
    private Integer studentId;
    private Integer postId;
    private Integer score;
    private String content;
    private LocalDateTime evalTime;
    // 评价图片地址
    private String evalImg;
    @TableField(exist = false)
    private String postName;
    @TableField(exist = false)
    private String studentName;
}
