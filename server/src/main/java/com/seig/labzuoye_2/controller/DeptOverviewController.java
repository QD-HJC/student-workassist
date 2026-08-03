package com.seig.labzuoye_2.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seig.labzuoye_2.domain.JobApply;
import com.seig.labzuoye_2.domain.JobPost;
import com.seig.labzuoye_2.domain.Salary;
import com.seig.labzuoye_2.service.IJobApplyService;
import com.seig.labzuoye_2.service.IJobPostService;
import com.seig.labzuoye_2.service.ISalaryService;
import com.seig.labzuoye_2.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dept/home")
public class DeptOverviewController {

    @Autowired
    private IJobPostService postService;

    @Autowired
    private IJobApplyService applyService;

    @Autowired
    private ISalaryService salaryService;

    /**
     * 部门首页统计数据
     * GET /api/dept/home/overview?deptId=1
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> overview(@RequestParam Integer deptId) {
        Map<String, Object> map = new HashMap<>();

        // 1. 待审核岗位数 - 本部门状态为"待审核"的岗位
        LambdaQueryWrapper<JobPost> postWrap = new LambdaQueryWrapper<>();
        postWrap.eq(JobPost::getDeptId, deptId)
                .eq(JobPost::getStatus, "待审核");
        long waitAuditPost = postService.count(postWrap);
        map.put("waitAuditPost", waitAuditPost);

        // 2. 待处理报名 - 本部门岗位下状态为"待审核"的报名
        LambdaQueryWrapper<JobApply> applyWrap = new LambdaQueryWrapper<>();
        applyWrap.inSql(JobApply::getPostId,
                        "SELECT post_id FROM tb_job_post WHERE dept_id = " + deptId)
                .eq(JobApply::getStatus, "待审核");
        long waitApply = applyService.count(applyWrap);
        map.put("waitApply", waitApply);

        // 3. 待核算薪资 - 本部门岗位下状态为"待发放"的薪资
        LambdaQueryWrapper<Salary> salaryWrap = new LambdaQueryWrapper<>();
        salaryWrap.inSql(Salary::getPostId,
                        "SELECT post_id FROM tb_job_post WHERE dept_id = " + deptId)
                .eq(Salary::getPayStatus, "待发放");
        long waitSalary = salaryService.count(salaryWrap);
        map.put("waitSalary", waitSalary);

        // 4. 额外：在岗学生数
        LambdaQueryWrapper<JobApply> activeWrap = new LambdaQueryWrapper<>();
        activeWrap.inSql(JobApply::getPostId,
                        "SELECT post_id FROM tb_job_post WHERE dept_id = " + deptId)
                .eq(JobApply::getStatus, "在岗");
        long activeStudents = applyService.count(activeWrap);
        map.put("activeStudents", activeStudents);

        // 5. 本部门岗位总数
        long totalPosts = postService.count(
                new LambdaQueryWrapper<JobPost>().eq(JobPost::getDeptId, deptId)
        );
        map.put("totalPosts", totalPosts);

        return Result.success(map);
    }
}