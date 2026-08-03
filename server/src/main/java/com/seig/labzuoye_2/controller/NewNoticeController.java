package com.seig.labzuoye_2.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.seig.labzuoye_2.domain.NewNotice;
import com.seig.labzuoye_2.service.INewNoticeService;
import com.seig.labzuoye_2.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/newnotice")
public class NewNoticeController {

    @Autowired
    private INewNoticeService noticeService;

    /**
     * 获取学生端首页公告列表（最新3条）
     */
    @GetMapping("/list")
    public Result<List<NewNotice>> getNoticeList() {
        LambdaQueryWrapper<NewNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NewNotice::getStatus, "已发布")
                .orderByDesc(NewNotice::getIsTop)
                .orderByDesc(NewNotice::getCreateTime)
                .last("limit 3");
        List<NewNotice> list = noticeService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 分页查询所有公告（管理员端）
     */
    @GetMapping("/page")
    public Result<Page<NewNotice>> getNoticePage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String title
    ) {
        Page<NewNotice> page = new Page<>(current, size);
        LambdaQueryWrapper<NewNotice> wrapper = new LambdaQueryWrapper<>();
        if (title != null && !title.isEmpty()) {
            wrapper.like(NewNotice::getTitle, title);
        }
        wrapper.orderByDesc(NewNotice::getIsTop)
                .orderByDesc(NewNotice::getCreateTime);
        return Result.success(noticeService.page(page, wrapper));
    }

    /**
     * 获取公告详情
     */
    @GetMapping("/{id}")
    public Result<NewNotice> getNoticeById(@PathVariable Integer id) {
        NewNotice notice = noticeService.getById(id);
        return notice != null ? Result.success(notice) : Result.error("公告不存在");
    }

    /**
     * 发布公告
     */
    @PostMapping("/save")
    public Result<String> saveNotice(@RequestBody NewNotice notice) {
        notice.setCreateTime(LocalDateTime.now());
        notice.setStatus("已发布");
        if (notice.getIsTop() == null) {
            notice.setIsTop(0);
        }
        noticeService.save(notice);
        return Result.success("发布成功");
    }

    /**
     * 更新公告
     */
    @PutMapping("/update")
    public Result<String> updateNotice(@RequestBody NewNotice notice) {
        notice.setUpdateTime(LocalDateTime.now());
        noticeService.updateById(notice);
        return Result.success("更新成功");
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteNotice(@PathVariable Integer id) {
        noticeService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 下架公告
     */
    @PutMapping("/offline/{id}")
    public Result<String> offlineNotice(@PathVariable Integer id) {
        NewNotice notice = new NewNotice();
        notice.setNoticeId(id);
        notice.setStatus("已下架");
        notice.setUpdateTime(LocalDateTime.now());
        noticeService.updateById(notice);
        return Result.success("已下架");
    }
}