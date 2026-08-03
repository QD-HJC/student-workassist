package com.seig.labzuoye_2.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seig.labzuoye_2.domain.JobPost;
import com.seig.labzuoye_2.mapper.JobPostMapper;
import com.seig.labzuoye_2.service.IJobPostService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Service
public class JobPostServiceImpl extends ServiceImpl<JobPostMapper, JobPost> implements IJobPostService {

    @Override
    @Cacheable(value = "postList", key = "'all'")
    public List<JobPost> list() {
        return super.list();
    }

    @Override
    @CacheEvict(value = "postList", allEntries = true)
    public boolean save(JobPost entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(value = "postList", allEntries = true)
    public boolean updateById(JobPost entity) {
        return super.updateById(entity);
    }

    // 移除 @Override 注解，避免方法签名冲突
    @CacheEvict(value = "postList", allEntries = true)
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    // 或者批量删除
    @CacheEvict(value = "postList", allEntries = true)
    public boolean removeByIds(Collection<?> list) {
        return super.removeByIds(list);
    }
}