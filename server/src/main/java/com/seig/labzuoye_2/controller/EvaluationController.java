package com.seig.labzuoye_2.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seig.labzuoye_2.domain.Evaluation;
import com.seig.labzuoye_2.domain.JobPost;
import com.seig.labzuoye_2.mapper.JobPostMapper;
import com.seig.labzuoye_2.service.EvaluationService;
import com.seig.labzuoye_2.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/evaluate")
public class EvaluationController {
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private JobPostMapper postMapper;

    // 1. 学生新增评价
    @PostMapping("/save")
    public Result<String> addEva(@RequestBody Evaluation evaluation) {
        evaluation.setEvalTime(LocalDateTime.now());
        evaluationService.save(evaluation);
        return Result.success("评价提交成功");
    }

    // 2. 查询当前学生所有评价记录
    @GetMapping("/list")
    public Result<List<Evaluation>> getEvaList(@RequestParam Integer studentId) {
        LambdaQueryWrapper<Evaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Evaluation::getStudentId, studentId).orderByDesc(Evaluation::getEvalTime);
        List<Evaluation> list = evaluationService.list(wrapper);
        // 封装岗位名称
        for (Evaluation eva : list) {
            JobPost post = postMapper.selectById(eva.getPostId());
            if (post != null) eva.setPostName(post.getPostName());
        }
        return Result.success(list);
    }

    // 3. 删除评价
    @DeleteMapping("/delete/{evalId}")
    public Result<String> deleteEva(@PathVariable Integer evalId) {
        evaluationService.removeById(evalId);
        return Result.success("删除成功");
    }

    // 4. 修改评价
    @PutMapping("/update")
    public Result<String> updateEva(@RequestBody Evaluation evaluation) {
        evaluationService.updateById(evaluation);
        return Result.success("修改成功");
    }
}