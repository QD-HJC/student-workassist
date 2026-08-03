package com.seig.labzuoye_2.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seig.labzuoye_2.domain.JobApply;
import com.seig.labzuoye_2.domain.JobPost;
import com.seig.labzuoye_2.domain.Student;
import com.seig.labzuoye_2.mapper.JobPostMapper;
import com.seig.labzuoye_2.mapper.StudentMapper;
import com.seig.labzuoye_2.service.IJobApplyService;
import com.seig.labzuoye_2.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/apply/dept")  // ✅ 确保是 /api/apply/dept
public class DeptApplyController {

    @Autowired
    private IJobApplyService applyService;

    @Autowired
    private JobPostMapper jobPostMapper;

    @Autowired
    private StudentMapper studentMapper;

    @GetMapping("/page")  // ✅ 完整路径是 /api/apply/dept/page
    public Result<Page<JobApply>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam Integer deptId,
            @RequestParam(required = false) Integer postId,
            @RequestParam(required = false) String status
    ) {
        System.out.println("收到请求: deptId=" + deptId + ", postId=" + postId + ", status=" + status);

        Page<JobApply> page = new Page<>(current, size);
        LambdaQueryWrapper<JobApply> wrapper = new LambdaQueryWrapper<>();

        // 子查询：只查本部门岗位的报名
        String sql = "SELECT post_id FROM tb_job_post WHERE dept_id = " + deptId;
        wrapper.inSql(JobApply::getPostId, sql);

        if (postId != null) {
            wrapper.eq(JobApply::getPostId, postId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(JobApply::getStatus, status);
        }
        wrapper.orderByDesc(JobApply::getApplyTime);

        Page<JobApply> result = applyService.page(page, wrapper);
        System.out.println("查询到 " + result.getRecords().size() + " 条记录");

        // 填充岗位名称和学生姓名
        for (JobApply apply : result.getRecords()) {
            JobPost post = jobPostMapper.selectById(apply.getPostId());
            if (post != null) {
                apply.setJobPost(post);
            }
            Student student = studentMapper.selectById(apply.getStudentId());
            if (student != null) {
                apply.setStudentName(student.getRealName());
            }
        }

        return Result.success(result);
    }
}