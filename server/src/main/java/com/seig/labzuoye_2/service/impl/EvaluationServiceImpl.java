package com.seig.labzuoye_2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seig.labzuoye_2.domain.Evaluation;
import com.seig.labzuoye_2.mapper.EvaluationMapper;
import com.seig.labzuoye_2.service.EvaluationService;
//import com.seig.labzuoye_2.service.IEvaluationService;
import org.springframework.stereotype.Service;

@Service
public class EvaluationServiceImpl extends ServiceImpl<EvaluationMapper, Evaluation>
        implements EvaluationService {

    @Override
    public IPage<Evaluation> getDeptEval(Page<Evaluation> page, Integer deptId) {
        LambdaQueryWrapper<Evaluation> wrapper = new LambdaQueryWrapper<>();
        // 子查询过滤本部门岗位的评价
        wrapper.inSql(Evaluation::getPostId,
                "SELECT post_id FROM tb_job_post WHERE dept_id = " + deptId);
        wrapper.orderByDesc(Evaluation::getEvalTime);
        return baseMapper.selectPage(page, wrapper);
    }
}