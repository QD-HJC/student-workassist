package com.seig.labzuoye_2.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tb_notice")
public class NewNotice {

    @TableId(type = IdType.AUTO)
    private Integer noticeId;
    private String title;
    private String content;
    private String author;
    private String status;
    private Integer isTop;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}