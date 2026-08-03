package com.seig.labzuoye_2.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seig.labzuoye_2.domain.Attendance;
import com.seig.labzuoye_2.domain.JobApply;
import com.seig.labzuoye_2.domain.JobPost;
import com.seig.labzuoye_2.mapper.DeptMapper;
import com.seig.labzuoye_2.mapper.JobPostMapper;

import com.seig.labzuoye_2.service.AttendanceService;
import com.seig.labzuoye_2.service.EvaluationService;
import com.seig.labzuoye_2.service.IJobApplyService;
import com.seig.labzuoye_2.service.IJobPostService;
import com.seig.labzuoye_2.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private IJobApplyService jobApplyService;

    @Autowired
    private JobPostMapper jobPostMapper;
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private DeptMapper deptMapper;
    @Autowired
    private IJobPostService  jobPostService;

    /**
     * 侧边栏-学生概览数据（解决 /api/student/overview 404）
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> getStudentOverview(@RequestParam Integer studentId) {
        Map<String, Object> resultMap = new HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);

        // 1. 计算本月总工时
        LambdaQueryWrapper<Attendance> attendanceWrapper = Wrappers.lambdaQuery();
        attendanceWrapper.eq(Attendance::getStudentId, studentId)
                .ge(Attendance::getWorkDate, monthStart)
                .isNotNull(Attendance::getCheckOut);
        List<Attendance> monthAttendanceList = attendanceService.list(attendanceWrapper);
        double totalMonthHour = 0.0;
        for (Attendance item : monthAttendanceList) {
            Duration duration = Duration.between(item.getCheckIn(), item.getCheckOut());
            // 换算小时
            totalMonthHour += duration.toMinutes() / 60.0;
        }
        resultMap.put("monthHour", Math.round(totalMonthHour * 10) / 10.0);

        // 2. 统计正在进行的在岗岗位数量
        LambdaQueryWrapper<JobApply> applyWrapper = Wrappers.lambdaQuery();
        applyWrapper.eq(JobApply::getStudentId, studentId)
                .eq(JobApply::getStatus, "在岗");
        long runningPostCount = jobApplyService.count(applyWrapper);
        resultMap.put("runningPost", runningPostCount);

        // 3. 待结算薪资（暂时默认0，薪资模块完成后再对接tb_salary）
        resultMap.put("waitSalary", 0);

        return Result.success(resultMap);
    }

    /**
     * 考勤页面获取学生当前在岗岗位（解决 currentPostId 未定义报错）
     */
    @GetMapping("/runningPost")
    public Result<JobPost> getRunningPost(@RequestParam Integer studentId) {
        LambdaQueryWrapper<JobApply> applyWrapper = Wrappers.lambdaQuery();
        applyWrapper.eq(JobApply::getStudentId, studentId)
                .eq(JobApply::getStatus, "在岗");
        JobApply runningApply = jobApplyService.getOne(applyWrapper);

        if (runningApply == null) {
            return Result.error("暂无在岗岗位，无法打卡");
        }
        JobPost post = jobPostMapper.selectById(runningApply.getPostId());
        return Result.success(post);
    }
    @GetMapping("/home/statistics")
    public Result<Map<String, Object>> getHomeStatistics() {
        Map<String, Object> data = new HashMap<>();

        // ✅ 总岗位数（已发布的岗位）
        LambdaQueryWrapper<JobPost> postWrapper = new LambdaQueryWrapper<>();
        postWrapper.eq(JobPost::getStatus, "已发布");
        long totalPosts = jobPostService.count(postWrapper);
        data.put("totalPosts", totalPosts);

        // ✅ 总报名数（使用正确的服务）
        long totalApplies = jobApplyService.count();
        data.put("totalApplies", totalApplies);

        // ✅ 部门数
        long totalDepts = deptMapper.selectCount(null);
        data.put("totalDepts", totalDepts);

        return Result.success(data);
    }
    @GetMapping("/myPost")
    public Result<List<JobPost>> getStudentPost(@RequestParam Integer studentId) {
        LambdaQueryWrapper<JobApply> applyWrapper = Wrappers.lambdaQuery();
        applyWrapper.eq(JobApply::getStudentId, studentId)
                .eq(JobApply::getStatus, "在岗");
        List<JobApply> applyList = jobApplyService.list(applyWrapper);
        List<JobPost> postList = applyList.stream()
                .map(apply -> jobPostMapper.selectById(apply.getPostId()))
                .filter(p -> p != null)
                .collect(Collectors.toList());
        return Result.success(postList);
    }
}
