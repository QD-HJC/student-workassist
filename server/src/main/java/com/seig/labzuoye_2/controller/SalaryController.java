package com.seig.labzuoye_2.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seig.labzuoye_2.domain.JobPost;
import com.seig.labzuoye_2.domain.Salary;
import com.seig.labzuoye_2.domain.Student;
import com.seig.labzuoye_2.mapper.JobPostMapper;
import com.seig.labzuoye_2.mapper.StudentMapper;
import com.seig.labzuoye_2.service.ISalaryService;
import com.seig.labzuoye_2.utils.Result;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/salary")
public class SalaryController {

    @Autowired
    private ISalaryService salaryService;

    @Autowired
    private JobPostMapper jobPostMapper;

    @Autowired
    private StudentMapper studentMapper;

    /**
     * ✅ 学生端：查询自己所有薪资记录
     * GET /api/salary/my?studentId=1
     */
    @GetMapping("/my")
    public Result<List<Salary>> getStudentSalary(@RequestParam Integer studentId) {
        System.out.println("🔍 查询学生薪资: studentId=" + studentId);

        LambdaQueryWrapper<Salary> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Salary::getStudentId, studentId)
                .orderByDesc(Salary::getMonth);
        List<Salary> salaryList = salaryService.list(wrapper);

        // 填充岗位名称
        for (Salary salary : salaryList) {
            JobPost post = jobPostMapper.selectById(salary.getPostId());
            if (post != null) {
                salary.setPostName(post.getPostName());
            } else {
                salary.setPostName("未知岗位");
            }
        }

        System.out.println("🔍 查询到 " + salaryList.size() + " 条记录");
        return Result.success(salaryList);
    }

    /**
     * 管理员分页查询薪资
     * GET /api/salary/page
     */
    @GetMapping("/page")
    public Result<IPage<Salary>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Integer studentId,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) Integer deptId,
            @RequestParam(required = false) String payStatus
    ) {
        Page<Salary> page = new Page<>(current, size);
        LambdaQueryWrapper<Salary> wrapper = new LambdaQueryWrapper<>();

        if (studentId != null) {
            wrapper.eq(Salary::getStudentId, studentId);
        }
        if (month != null && !month.isEmpty()) {
            wrapper.eq(Salary::getMonth, month);
        }
        if (payStatus != null && !payStatus.isEmpty()) {
            wrapper.eq(Salary::getPayStatus, payStatus);
        }
        if (deptId != null) {
            wrapper.inSql(Salary::getPostId,
                    "SELECT post_id FROM tb_job_post WHERE dept_id = " + deptId);
        }
        wrapper.orderByDesc(Salary::getMonth).orderByDesc(Salary::getSalaryId);

        IPage<Salary> pageData = salaryService.page(page, wrapper);

        // 填充学生姓名和岗位名称
        for (Salary salary : pageData.getRecords()) {
            // 填充岗位名称
            JobPost post = jobPostMapper.selectById(salary.getPostId());
            if (post != null) {
                salary.setPostName(post.getPostName());
            } else {
                salary.setPostName("未知岗位");
            }

            // 填充学生姓名
            Student student = studentMapper.selectById(salary.getStudentId());
            if (student != null) {
                salary.setStudentName(student.getRealName());
            } else {
                salary.setStudentName("未知学生");
            }
        }

        return Result.success(pageData);
    }

    /**
     * 新增薪资记录
     * POST /api/salary/save
     */
    @PostMapping("/save")
    public Result<String> save(@RequestBody Salary salary) {
        salaryService.save(salary);
        return Result.success("保存成功");
    }

    /**
     * 修改薪资/发放状态
     * PUT /api/salary/update
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody Salary salary) {
        if ("已发放".equals(salary.getPayStatus())) {
            salary.setPayTime(LocalDateTime.now());
        }
        salaryService.updateById(salary);
        return Result.success("更新成功");
    }

    /**
     * 删除薪资记录
     * DELETE /api/salary/delete/{id}
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        salaryService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 发放薪资（单个）
     * PUT /api/salary/pay/{id}
     */
    @PutMapping("/pay/{id}")
    public Result<String> paySalary(@PathVariable Integer id) {
        Salary salary = salaryService.getById(id);
        if (salary == null) {
            return Result.error("薪资记录不存在");
        }
        salary.setPayStatus("已发放");
        salary.setPayTime(LocalDateTime.now());
        salaryService.updateById(salary);
        return Result.success("薪资发放成功");
    }

    /**
     * 导出学生自己的薪资数据为Excel
     * GET /api/salary/export?studentId=1
     */
    @GetMapping("/export")
    public void exportSalary(HttpServletResponse response, @RequestParam Integer studentId) throws IOException {
        LambdaQueryWrapper<Salary> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Salary::getStudentId, studentId)
                .orderByDesc(Salary::getMonth);
        List<Salary> salaryList = salaryService.list(wrapper);

        List<SalaryExportDTO> exportList = new ArrayList<>();
        for (Salary salary : salaryList) {
            SalaryExportDTO dto = new SalaryExportDTO();
            dto.setMonth(salary.getMonth());
            JobPost post = jobPostMapper.selectById(salary.getPostId());
            dto.setPostName(post != null ? post.getPostName() : "未知岗位");
            dto.setTotalHour(salary.getTotalHour() != null ? salary.getTotalHour() : 0.0);
            dto.setTotalSalary(salary.getTotalSalary() != null ? salary.getTotalSalary() : 0.0);
            dto.setPayStatus(salary.getPayStatus());
            dto.setPayTime(salary.getPayTime() != null ?
                    salary.getPayTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "-");
            exportList.add(dto);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("我的薪资明细_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), SalaryExportDTO.class)
                .sheet("薪资明细")
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .doWrite(exportList);
    }

    /**
     * 管理员导出所有薪资数据
     * GET /api/salary/export/all
     */
    @GetMapping("/export/all")
    public void exportAllSalary(HttpServletResponse response,
                                @RequestParam(required = false) String month,
                                @RequestParam(required = false) String status) throws IOException {
        LambdaQueryWrapper<Salary> wrapper = new LambdaQueryWrapper<>();
        if (month != null && !month.isEmpty()) {
            wrapper.eq(Salary::getMonth, month);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Salary::getPayStatus, status);
        }
        wrapper.orderByDesc(Salary::getMonth).orderByDesc(Salary::getSalaryId);
        List<Salary> salaryList = salaryService.list(wrapper);

        List<SalaryExportAdminDTO> exportList = new ArrayList<>();
        for (Salary salary : salaryList) {
            SalaryExportAdminDTO dto = new SalaryExportAdminDTO();
            dto.setSalaryId(salary.getSalaryId());
            dto.setStudentId(salary.getStudentId());

            Student student = studentMapper.selectById(salary.getStudentId());
            dto.setStudentName(student != null ? student.getRealName() : "未知学生");

            JobPost post = jobPostMapper.selectById(salary.getPostId());
            dto.setPostName(post != null ? post.getPostName() : "未知岗位");

            dto.setMonth(salary.getMonth());
            dto.setTotalHour(salary.getTotalHour() != null ? salary.getTotalHour() : 0.0);
            dto.setTotalSalary(salary.getTotalSalary() != null ? salary.getTotalSalary() : 0.0);
            dto.setPayStatus(salary.getPayStatus());
            dto.setPayTime(salary.getPayTime() != null ?
                    salary.getPayTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "-");
            exportList.add(dto);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("薪资明细_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), SalaryExportAdminDTO.class)
                .sheet("薪资明细")
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .doWrite(exportList);
    }
}