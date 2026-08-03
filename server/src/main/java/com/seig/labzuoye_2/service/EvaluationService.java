package com.seig.labzuoye_2.service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seig.labzuoye_2.domain.Evaluation;
public interface EvaluationService extends IService<Evaluation> {
    IPage<Evaluation> getDeptEval(Page<Evaluation> page, Integer deptId);
//    // 2. 学生端：查询自己收到的评价
//    IPage<Evaluation> getStudentSelfEval(Page<Evaluation> page, Integer studentId);
}
