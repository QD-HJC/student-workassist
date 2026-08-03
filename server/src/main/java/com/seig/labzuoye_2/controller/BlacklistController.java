package com.seig.labzuoye_2.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seig.labzuoye_2.domain.Blacklist;
import com.seig.labzuoye_2.service.IBlacklistService;
import com.seig.labzuoye_2.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/black")
public class BlacklistController {

    @Autowired
    private IBlacklistService blackService;

    // 分页黑名单
    @GetMapping("/page")
    public Result<IPage<Blacklist>> page(
            @RequestParam Long current,
            @RequestParam Long size,
            @RequestParam(required = false) Integer deptId
    ) {
        Page<Blacklist> page = new Page<>(current, size);
        IPage<Blacklist> data;
        if (deptId != null) {
            data = blackService.getDeptBlack(page, deptId);
        } else {
            data = blackService.page(page);
        }
        return Result.success(data);
    }

    // 拉黑学生
    @PostMapping("/save")
    public Result<String> save(@RequestBody Blacklist black) {
        black.setCreateTime(LocalDateTime.now());
        black.setStatus("生效");
        blackService.save(black);
        return Result.success("拉黑成功");
    }

    // 解除黑名单 / 更新
    @PutMapping("/update")
    public Result<String> update(@RequestBody Blacklist black) {
        blackService.updateById(black);
        return Result.success("操作完成");
    }

    // 删除黑名单记录
    @DeleteMapping("/delete/{blackId}")
    public Result<String> delete(@PathVariable Integer blackId) {
        blackService.removeById(blackId);
        return Result.success("删除成功");
    }
}