package com.seig.labzuoye_2.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seig.labzuoye_2.domain.Evaluation;
import com.seig.labzuoye_2.domain.JobPost;
import com.seig.labzuoye_2.domain.Student;
import com.seig.labzuoye_2.mapper.JobPostMapper;
import com.seig.labzuoye_2.mapper.StudentMapper;
import com.seig.labzuoye_2.service.EvaluationService;
import com.seig.labzuoye_2.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/eval")  // ✅ 确保是 /api/eval
public class DeptEvaluationController {

    @Autowired
    private EvaluationService evaluationService;

    @Autowired
    private JobPostMapper jobPostMapper;

    @Autowired
    private StudentMapper studentMapper;

    /**
     * 部门分页查询评价
     * GET /api/eval/dept/page
     */
    @GetMapping("/dept/page")
    public Result<Page<Evaluation>> deptPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam Integer deptId
    ) {
        System.out.println("📊 查询评价: deptId=" + deptId);

        Page<Evaluation> page = new Page<>(current, size);
        LambdaQueryWrapper<Evaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.inSql(Evaluation::getPostId,
                "SELECT post_id FROM tb_job_post WHERE dept_id = " + deptId);
        wrapper.orderByDesc(Evaluation::getEvalTime);

        Page<Evaluation> result = evaluationService.page(page, wrapper);
        System.out.println("📊 查询到 " + result.getRecords().size() + " 条评价");

        // 填充岗位名称和学生姓名
        for (Evaluation eval : result.getRecords()) {
            JobPost post = jobPostMapper.selectById(eval.getPostId());
            if (post != null) {
                eval.setPostName(post.getPostName());
            }
            Student student = studentMapper.selectById(eval.getStudentId());
            if (student != null) {
                eval.setStudentName(student.getRealName());
            }
        }

        return Result.success(result);
    }

    /**
     * 新增评价
     * POST /api/eval/save
     */
    @PostMapping("/save")
    public Result<String> save(@RequestBody Evaluation evaluation) {
        System.out.println("📝 新增评价: studentId=" + evaluation.getStudentId() + ", postId=" + evaluation.getPostId());
        evaluation.setEvalTime(LocalDateTime.now());
        boolean success = evaluationService.save(evaluation);
        return success ? Result.success("评价提交成功") : Result.error("提交失败");
    }

    /**
     * 更新评价
     * PUT /api/eval/update
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody Evaluation evaluation) {
        System.out.println("📝 更新评价: evalId=" + evaluation.getEvalId());
        boolean success = evaluationService.updateById(evaluation);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除评价
     * DELETE /api/eval/delete/{evalId}
     */
    @DeleteMapping("/delete/{evalId}")
    public Result<String> delete(@PathVariable Integer evalId) {
        System.out.println("📝 删除评价: evalId=" + evalId);
        boolean success = evaluationService.removeById(evalId);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}