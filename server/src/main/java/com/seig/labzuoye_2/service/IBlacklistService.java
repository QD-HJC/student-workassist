package com.seig.labzuoye_2.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seig.labzuoye_2.domain.Blacklist;

public interface IBlacklistService extends IService<Blacklist> {

    /**
     * 根据部门id分页查询本部门关联的黑名单学生
     * @param page 分页对象
     * @param deptId 部门id
     * @return 分页黑名单数据
     */
    IPage<Blacklist> getDeptBlack(Page<Blacklist> page, Integer deptId);
}