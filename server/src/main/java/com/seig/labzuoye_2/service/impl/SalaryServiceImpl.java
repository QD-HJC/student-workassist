package com.seig.labzuoye_2.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seig.labzuoye_2.domain.Salary;
import com.seig.labzuoye_2.mapper.SalaryMapper;
import com.seig.labzuoye_2.service.ISalaryService;
import org.springframework.stereotype.Service;

@Service
public class SalaryServiceImpl extends ServiceImpl<SalaryMapper, Salary> implements ISalaryService {
}
