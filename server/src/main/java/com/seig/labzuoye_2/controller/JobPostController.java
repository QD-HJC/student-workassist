package com.seig.labzuoye_2.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seig.labzuoye_2.domain.JobPost;
import com.seig.labzuoye_2.mapper.JobPostMapper;
import com.seig.labzuoye_2.service.IJobPostService;
import com.seig.labzuoye_2.utils.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/post")  // ✅ 确保是 /api/post
public class JobPostController {

    @Autowired
    private IJobPostService jobPostService;

    @Autowired
    private JobPostMapper jobPostMapper;

    // 分页查询岗位（含搜索）
    @GetMapping("/page")
    public Result<Page<JobPost>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String postName,
            @RequestParam(required = false) Integer deptId,
            @RequestParam(required = false) String status
    ) {
        Page<JobPost> page = new Page<>(current, size);
        LambdaQueryWrapper<JobPost> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(postName)) {
            wrapper.like(JobPost::getPostName, postName);
        }
        if (deptId != null) {
            wrapper.eq(JobPost::getDeptId, deptId);
        }
        if (StringUtils.isNotBlank(status)) {
            wrapper.eq(JobPost::getStatus, status);
        }
        wrapper.orderByDesc(JobPost::getCreateTime);
        Page<JobPost> data = jobPostService.page(page, wrapper);
        return Result.success(data);
    }

    // 不分页查询全部岗位，首页最新岗位使用
    @GetMapping("/allData")
    public Result<List<JobPost>> getAllData() {
        LambdaQueryWrapper<JobPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JobPost::getStatus, "已发布")
                .orderByDesc(JobPost::getCreateTime)
                .last("limit 6");
        List<JobPost> list = jobPostService.list(wrapper);
        return Result.success(list);
    }

    // 新增岗位（用工部门发布）
    @PostMapping("/save")  // ✅ 完整路径是 /api/post/save
    public Result<String> save(@RequestBody JobPost post) {
        System.out.println("收到新增岗位请求: " + post);

        // 验证必填字段
        if (post.getDeptId() == null) {
            return Result.error("部门ID不能为空");
        }
        if (StringUtils.isBlank(post.getPostName())) {
            return Result.error("岗位名称不能为空");
        }
        if (post.getSalary() == null || post.getSalary().compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("薪资必须大于0");
        }

        post.setCreateTime(LocalDateTime.now());
        post.setStatus("待审核");
        // 默认时薪
        if (post.getSalary() == null) {
            post.setSalary(BigDecimal.valueOf(20));
        }
        boolean flag = jobPostService.save(post);
        return flag ? Result.success("发布成功，等待资助中心审核") : Result.error("发布失败");
    }

    // 修改岗位
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody JobPost post) {
        boolean flag = jobPostService.updateById(post);
        return Result.success(flag);
    }

    // 根据部门分页查询岗位
    @GetMapping("/dept")
    public Result<IPage<JobPost>> getByDept(
            @RequestParam(required = false) Integer deptId,
            @RequestParam(value = "current", defaultValue = "1") Long current,
            @RequestParam(value = "size", defaultValue = "100") Long size
    ) {
        Page<JobPost> page = new Page<>(current, size);
        LambdaQueryWrapper<JobPost> wrapper = new LambdaQueryWrapper<>();
        if (deptId != null) {
            wrapper.eq(JobPost::getDeptId, deptId);
        }
        wrapper.orderByDesc(JobPost::getCreateTime);
        IPage<JobPost> pageData = jobPostService.page(page, wrapper);
        return Result.success(pageData);
    }

    // 删除岗位
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Integer id) {
        boolean flag = jobPostService.removeById(id);
        return Result.success(flag);
    }

    // 审核岗位状态（资助中心用）
    @PutMapping("/audit")
    public Result<String> auditPost(
            @RequestParam Integer postId,
            @RequestParam String status
    ) {
        JobPost post = new JobPost();
        post.setPostId(postId);
        post.setStatus(status);
        jobPostService.updateById(post);
        return Result.success("审核操作完成");
    }

    // 获取所有不重复岗位分类
    @GetMapping("/category/list")
    public Result<List<String>> getPostTypeList() {
        LambdaQueryWrapper<JobPost> wrapper = Wrappers.lambdaQuery();
        wrapper.select(JobPost::getPostType)
                .isNotNull(JobPost::getPostType);
        List<JobPost> list = jobPostMapper.selectList(wrapper);
        List<String> typeList = list.stream()
                .map(JobPost::getPostType)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        return Result.success(typeList);
    }
}