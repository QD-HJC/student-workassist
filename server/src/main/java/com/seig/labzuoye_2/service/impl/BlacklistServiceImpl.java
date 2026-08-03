package com.seig.labzuoye_2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seig.labzuoye_2.domain.Blacklist;
import com.seig.labzuoye_2.domain.JobPost;
import com.seig.labzuoye_2.mapper.BlacklistMapper;
import com.seig.labzuoye_2.service.IBlacklistService;
import org.springframework.stereotype.Service;

@Service
public class BlacklistServiceImpl extends ServiceImpl<BlacklistMapper, Blacklist>
        implements IBlacklistService {

    @Override
    public IPage<Blacklist> getDeptBlack(Page<Blacklist> page, Integer deptId) {
        LambdaQueryWrapper<Blacklist> wrapper = new LambdaQueryWrapper<>();
        // 关联查询：只查询该部门岗位下产生的黑名单学生
        // 子查询：查出当前部门所有岗位postId，再匹配黑名单里的学生（该部门岗位报名过的学生）
        wrapper.inSql(Blacklist::getStudentId,
                "SELECT DISTINCT student_id FROM tb_job_apply " +
                        "WHERE post_id IN (SELECT post_id FROM tb_job_post WHERE dept_id = " + deptId + ")");
        // 按创建时间倒序
        wrapper.orderByDesc(Blacklist::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }
}