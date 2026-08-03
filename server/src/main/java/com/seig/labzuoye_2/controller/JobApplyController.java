package com.seig.labzuoye_2.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seig.labzuoye_2.domain.JobApply;
import com.seig.labzuoye_2.domain.JobPost;
import com.seig.labzuoye_2.domain.Student;
import com.seig.labzuoye_2.mapper.StudentMapper;
import com.seig.labzuoye_2.service.IJobApplyService;
import com.seig.labzuoye_2.service.IJobPostService;
import com.seig.labzuoye_2.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/apply")
public class JobApplyController {

    @Autowired
    private IJobApplyService applyService;

    @Autowired
    private IJobPostService postService;

    @Autowired
    private StudentMapper studentMapper;  // 添加这个依赖

    // 分页查询所有报名
    @GetMapping("/all")
    public Result<Page<JobApply>> getAll(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size
    ){
        Page<JobApply> page = applyService.page(new Page<>(current,size));
        page.getRecords().forEach(apply -> {
            JobPost post = postService.getById(apply.getPostId());
            apply.setJobPost(post);
            Student student = studentMapper.selectById(apply.getStudentId());
            if (student != null) {
                apply.setStudentName(student.getRealName());
            }
        });
        return Result.success(page);
    }

    // 学生提交报名
    @PostMapping("/save")
    public Result<String> save(@RequestBody JobApply apply){
        // 检查是否已经报名过
        QueryWrapper<JobApply> checkWrapper = new QueryWrapper<>();
        checkWrapper.eq("post_id", apply.getPostId())
                .eq("student_id", apply.getStudentId());
        long count = applyService.count(checkWrapper);
        if (count > 0) {
            return Result.error("您已报名该岗位，请勿重复报名");
        }

        apply.setApplyTime(LocalDateTime.now());
        apply.setStatus("待审核");
        apply.setInterviewResult("待面试");
        applyService.save(apply);
        return Result.success("报名成功");
    }

    // 审核报名
    @PutMapping("/audit")
    public Result<String> audit(
            @RequestParam Integer applyId,
            @RequestParam String status,
            @RequestParam String interviewResult
    ){
        JobApply apply = new JobApply();
        apply.setApplyId(applyId);
        apply.setStatus(status);
        apply.setInterviewResult(interviewResult);
        applyService.updateById(apply);
        return Result.success("审核完成");
    }

    // 删除报名
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Integer id){
        applyService.removeById(id);
        return Result.success("操作成功");
    }

    // 根据岗位id查询报名
    @GetMapping("/getByPost/{postId}")
    public Result<List<JobApply>> getByPostId(@PathVariable Integer postId){
        QueryWrapper<JobApply> wrapper = new QueryWrapper<>();
        wrapper.eq("post_id", postId);
        List<JobApply> list = applyService.list(wrapper);
        JobPost post = postService.getById(postId);
        for (JobApply apply : list) {
            apply.setJobPost(post);
            Student student = studentMapper.selectById(apply.getStudentId());
            if (student != null) {
                apply.setStudentName(student.getRealName());
            }
        }
        return Result.success(list);
    }

    // 学生我的报名
    @GetMapping("/my")
    public Result<List<JobApply>> getMyApply(@RequestParam Integer studentId){
        QueryWrapper<JobApply> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId);
        List<JobApply> list = applyService.list(wrapper);
        list.forEach(item -> {
            JobPost post = postService.getById(item.getPostId());
            item.setJobPost(post);
        });
        return Result.success(list);
    }
}