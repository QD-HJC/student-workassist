package com.seig.labzuoye_2.controller;

import com.seig.labzuoye_2.domain.Notice;
import com.seig.labzuoye_2.utils.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    @GetMapping("/list")
    public Result<List<Notice>> getNoticeList() {
        List<Notice> list = new ArrayList<>();

        Notice n1 = new Notice();
        n1.setId(1);
        n1.setTitle("关于2026年7月份考勤确认截止日期的通知");
        n1.setTagName("紧急");
        n1.setTagType("urgent");
        n1.setSubText("截止日期: 2026-07-15 17:00");
        list.add(n1);

        Notice n2 = new Notice();
        n2.setId(2);
        n2.setTitle("新增3名学生通过了\"图书馆管理员\"岗位的初步筛选");
        n2.setTagName("普通");
        n2.setTagType("normal");
        n2.setSubText("更新时间: 2026-07-08 09:30");
        list.add(n2);

        return Result.success(list);
    }
}