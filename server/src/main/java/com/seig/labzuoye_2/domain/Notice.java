package com.seig.labzuoye_2.domain;

import lombok.Data;

@Data
public class Notice {
    private Integer id;
    private String title;
    private String tagName;
    private String tagType;
    private String subText;
    private String time;
}