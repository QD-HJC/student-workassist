package com.seig.labzuoye_2.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.seig.labzuoye_2.domain.JobApply;
import com.seig.labzuoye_2.mapper.JobApplyMapper;
import com.seig.labzuoye_2.service.IJobApplyService;
import org.springframework.stereotype.Service;

@Service
public class JobApplyServiceImpl extends ServiceImpl<JobApplyMapper, JobApply>
        implements IJobApplyService {

}
