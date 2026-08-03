package com.seig.labzuoye_2.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seig.labzuoye_2.domain.Attendance;
import com.seig.labzuoye_2.domain.JobPost;
import com.seig.labzuoye_2.mapper.JobPostMapper;
import com.seig.labzuoye_2.service.AttendanceService;
import com.seig.labzuoye_2.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private JobPostMapper jobPostMapper;

    /**
     * 打卡提交接口 POST /api/attendance/save
     * 前端打卡调用，status=1上班 / status=2下班
     */
    @PostMapping("/save")
    public Result<?> addClock(@RequestBody Attendance attendance) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attendance::getStudentId, attendance.getStudentId())
                .eq(Attendance::getWorkDate, today);

        Attendance todayRecord = attendanceService.getOne(wrapper);

        // 上班打卡
        if ("1".equals(attendance.getStatus())) {
            if (todayRecord != null) {
                return Result.error("今日已完成上班打卡，请勿重复操作");
            }
            attendance.setWorkDate(today);
            attendance.setCheckIn(now);
            attendance.setStatus("正常");
            attendanceService.save(attendance);
            return Result.success("上班打卡成功");
        }
        // 下班打卡
        else if ("2".equals(attendance.getStatus())) {
            if (todayRecord == null) {
                return Result.error("今日尚未上班打卡，无法下班");
            }
            if (todayRecord.getCheckOut() != null) {
                return Result.error("今日已完成下班打卡");
            }
            todayRecord.setCheckOut(now);
            // 计算工时
            Duration duration = Duration.between(todayRecord.getCheckIn(), now);
            double hours = duration.toMinutes() / 60.0;
            todayRecord.setHour(Math.round(hours * 100) / 100.0);
            attendanceService.updateById(todayRecord);
            return Result.success("下班打卡成功，今日工时：" + todayRecord.getHour() + "小时");
        }
        return Result.error("打卡类型错误");
    }

    /**
     * 查询学生当月打卡记录 GET /api/attendance/list?studentId=1
     */
    @GetMapping("/list")
    public Result<List<Attendance>> getClockList(@RequestParam Integer studentId) {
        LocalDate now = LocalDate.now();
        LocalDate startDay = now.withDayOfMonth(1);

        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attendance::getStudentId, studentId)
                .ge(Attendance::getWorkDate, startDay)
                .orderByDesc(Attendance::getWorkDate);

        List<Attendance> list = attendanceService.list(wrapper);

        // 填充岗位名称 和 工时
        for (Attendance item : list) {
            JobPost post = jobPostMapper.selectById(item.getPostId());
            if (post != null) {
                item.setPostName(post.getPostName());
            }
            // 如果已下班，计算工时
            if (item.getCheckIn() != null && item.getCheckOut() != null) {
                Duration duration = Duration.between(item.getCheckIn(), item.getCheckOut());
                double hours = duration.toMinutes() / 60.0;
                item.setHour(Math.round(hours * 100) / 100.0);
            }
        }
        return Result.success(list);
    }

    /**
     * 部门端：查询本部门所有考勤记录
     */
    @GetMapping("/dept/list")
    public Result<List<Attendance>> getDeptAttendance(
            @RequestParam Integer deptId,
            @RequestParam(required = false) String month) {

        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
        // 子查询：只查本部门岗位的考勤
        wrapper.inSql(Attendance::getPostId,
                "SELECT post_id FROM tb_job_post WHERE dept_id = " + deptId);

        if (month != null && !month.isEmpty()) {
            wrapper.apply("DATE_FORMAT(work_date, '%Y-%m') = {0}", month);
        }
        wrapper.orderByDesc(Attendance::getWorkDate);

        List<Attendance> list = attendanceService.list(wrapper);
        // 填充岗位名称和学生姓名
        for (Attendance item : list) {
            JobPost post = jobPostMapper.selectById(item.getPostId());
            if (post != null) {
                item.setPostName(post.getPostName());
            }
            if (item.getCheckIn() != null && item.getCheckOut() != null) {
                Duration duration = Duration.between(item.getCheckIn(), item.getCheckOut());
                double hours = duration.toMinutes() / 60.0;
                item.setHour(Math.round(hours * 100) / 100.0);
            }
        }
        return Result.success(list);
    }

    /**
     * 部门端：更新考勤状态（正常/迟到/早退/缺勤）
     */
    @PutMapping("/update")
    public Result<String> updateAttendance(@RequestBody Attendance attendance) {
        attendanceService.updateById(attendance);
        return Result.success("更新成功");
    }
}