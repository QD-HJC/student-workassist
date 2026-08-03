package com.seig.labzuoye_2.service.impl;



import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.seig.labzuoye_2.domain.SysUser;
import com.seig.labzuoye_2.mapper.SysUserMapper;
import com.seig.labzuoye_2.service.ISysUserService;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {
}