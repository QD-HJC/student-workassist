package com.seig.labzuoye_2.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seig.labzuoye_2.domain.Dept;
import com.seig.labzuoye_2.domain.JobApply;
import com.seig.labzuoye_2.domain.JobPost;
import com.seig.labzuoye_2.domain.Salary;
import com.seig.labzuoye_2.mapper.DeptMapper;
import com.seig.labzuoye_2.service.IJobApplyService;
import com.seig.labzuoye_2.service.IJobPostService;
import com.seig.labzuoye_2.service.ISalaryService;
import com.seig.labzuoye_2.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")  // ✅ 加上 /api
public class AdminController {

    @Autowired
    private IJobPostService jobPostService;

    @Autowired
    private ISalaryService salaryService;

    @Autowired
    private IJobApplyService jobApplyService;

    @Autowired
    private DeptMapper deptMapper;

    /**
     * 获取统计数据
     * GET /api/admin/statistics
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> map = new HashMap<>();
        map.put("totalPosts", jobPostService.count());
        map.put("pendingPosts", jobPostService.count(
                new LambdaQueryWrapper<JobPost>().eq(JobPost::getStatus, "待审核")
        ));
        map.put("activeStudents", jobApplyService.count(
                new LambdaQueryWrapper<JobApply>().eq(JobApply::getStatus, "在岗")
        ));
        map.put("pendingSalary", salaryService.count(
                new LambdaQueryWrapper<Salary>().eq(Salary::getPayStatus, "待发放")
        ));
        map.put("totalApplies", jobApplyService.count());
        return Result.success(map);
    }

    /**
     * 获取待审核岗位列表
     */
    @GetMapping("/post/pending")
    public Result<List<JobPost>> getPendingPosts() {
        List<JobPost> list = jobPostService.list(
                new LambdaQueryWrapper<JobPost>().eq(JobPost::getStatus, "待审核")
        );
        return Result.success(list);
    }

    /**
     * 获取待发放薪资列表
     */
    @GetMapping("/salary/pending")
    public Result<List<Salary>> getPendingSalary() {
        List<Salary> list = salaryService.list(
                new LambdaQueryWrapper<Salary>().eq(Salary::getPayStatus, "待发放")
        );
        return Result.success(list);
    }

    /**
     * 获取部门统计数据
     */
    @GetMapping("/dept/statistics")
    public Result<List<Map<String, Object>>> getDeptStatistics() {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Dept> deptList = deptMapper.selectList(null);

        for (Dept dept : deptList) {
            Map<String, Object> map = new HashMap<>();
            map.put("deptName", dept.getDeptName());
            map.put("deptId", dept.getDeptId());

            long postCount = jobPostService.count(
                    new LambdaQueryWrapper<JobPost>().eq(JobPost::getDeptId, dept.getDeptId())
            );
            map.put("postCount", postCount);

            long activeCount = jobApplyService.count(
                    new LambdaQueryWrapper<JobApply>()
                            .inSql(JobApply::getPostId, "SELECT post_id FROM tb_job_post WHERE dept_id = " + dept.getDeptId())
                            .eq(JobApply::getStatus, "在岗")
            );
            map.put("activeCount", activeCount);

            long pendingPost = jobPostService.count(
                    new LambdaQueryWrapper<JobPost>()
                            .eq(JobPost::getDeptId, dept.getDeptId())
                            .eq(JobPost::getStatus, "待审核")
            );
            map.put("pendingPost", pendingPost);

            long pendingApply = jobApplyService.count(
                    new LambdaQueryWrapper<JobApply>()
                            .inSql(JobApply::getPostId, "SELECT post_id FROM tb_job_post WHERE dept_id = " + dept.getDeptId())
                            .eq(JobApply::getStatus, "待审核")
            );
            map.put("pendingApply", pendingApply);

            long pendingSalary = salaryService.count(
                    new LambdaQueryWrapper<Salary>()
                            .inSql(Salary::getPostId, "SELECT post_id FROM tb_job_post WHERE dept_id = " + dept.getDeptId())
                            .eq(Salary::getPayStatus, "待发放")
            );
            map.put("pendingSalary", pendingSalary);

            result.add(map);
        }
        return Result.success(result);
    }
}