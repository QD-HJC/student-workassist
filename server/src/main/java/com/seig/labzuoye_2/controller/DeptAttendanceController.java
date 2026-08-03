package com.seig.labzuoye_2.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seig.labzuoye_2.domain.Attendance;
import com.seig.labzuoye_2.domain.JobPost;
import com.seig.labzuoye_2.domain.Student;
import com.seig.labzuoye_2.mapper.JobPostMapper;
import com.seig.labzuoye_2.mapper.StudentMapper;
import com.seig.labzuoye_2.service.AttendanceService;
import com.seig.labzuoye_2.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/attendance/dept")
public class DeptAttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private JobPostMapper jobPostMapper;

    @Autowired
    private StudentMapper studentMapper;

    @GetMapping("/page")
    public Result<Page<Attendance>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam Integer deptId,
            @RequestParam(required = false) String month
    ) {
        Page<Attendance> page = new Page<>(current, size);
        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();

        wrapper.inSql(Attendance::getPostId,
                "SELECT post_id FROM tb_job_post WHERE dept_id = " + deptId);

        if (month != null && !month.isEmpty()) {
            wrapper.apply("DATE_FORMAT(work_date, '%Y-%m') = {0}", month);
        }
        wrapper.orderByDesc(Attendance::getWorkDate);

        Page<Attendance> result = attendanceService.page(page, wrapper);

        for (Attendance att : result.getRecords()) {
            JobPost post = jobPostMapper.selectById(att.getPostId());
            if (post != null) {
                att.setPostName(post.getPostName());
            }
            Student student = studentMapper.selectById(att.getStudentId());
            if (student != null) {
                att.setStudentName(student.getRealName());
            }
            if (att.getCheckIn() != null && att.getCheckOut() != null) {
                Duration duration = Duration.between(att.getCheckIn(), att.getCheckOut());
                double hours = duration.toMinutes() / 60.0;
                att.setHour(Math.round(hours * 100) / 100.0);
            }
        }

        return Result.success(result);
    }

    // 更新考勤状态
    @PutMapping("/update")
    public Result<String> update(@RequestBody Attendance attendance) {
        attendanceService.updateById(attendance);
        return Result.success("更新成功");
    }
}