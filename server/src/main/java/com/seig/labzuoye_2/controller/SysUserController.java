package com.seig.labzuoye_2.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seig.labzuoye_2.domain.Dept;
import com.seig.labzuoye_2.domain.SysUser;
import com.seig.labzuoye_2.mapper.DeptMapper;
import com.seig.labzuoye_2.service.ISysUserService;
import com.seig.labzuoye_2.utils.JwtUtil;
import com.seig.labzuoye_2.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;  // ✅ 添加这行导入
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class SysUserController {

    private final ISysUserService userService;
    private final JwtUtil jwtUtil;
    private final DeptMapper deptMapper;

    @Autowired
    public SysUserController(ISysUserService userService, JwtUtil jwtUtil, DeptMapper deptMapper) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.deptMapper = deptMapper;
    }

    /**
     * 登录接口 /api/user/login
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody SysUser loginParam) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", loginParam.getUsername())
                .eq("password", loginParam.getPassword())
                .eq("role", loginParam.getRole());
        SysUser user = userService.getOne(wrapper);

        if (user == null) {
            return Result.error("账号、密码或角色选择错误，请重新输入");
        }

        // 如果是用工部门，查询部门信息
        if ("用工部门".equals(user.getRole())) {
            try {
                if (user.getDeptId() != null) {
                    Dept dept = deptMapper.selectById(user.getDeptId());
                    if (dept != null) {
                        user.setDeptName(dept.getDeptName());
                    } else {
                        user.setDeptName("未知部门");
                    }
                } else {
                    user.setDeptId(1);
                    user.setDeptName("图书馆");
                }
            } catch (Exception e) {
                user.setDeptId(1);
                user.setDeptName("图书馆");
            }
        }

        String token = jwtUtil.generateToken(user.getUserId(), user.getRole());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);

        return Result.success(data);
    }

    /**
     * 注册接口 /api/user/register
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody SysUser user) {
        // 检查用户名是否已存在
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", user.getUsername());
        SysUser existUser = userService.getOne(wrapper);
        if (existUser != null) {
            return Result.error("用户名已存在，请更换");
        }

        // 设置默认值
        user.setStatus("正常");
        user.setCreateTime(LocalDateTime.now());  // ✅ 现在可以正常使用了

        // 如果是用工部门，设置默认部门ID
        if ("用工部门".equals(user.getRole())) {
            user.setDeptId(1); // 默认部门ID
        }

        boolean success = userService.save(user);
        return success ? Result.success("注册成功") : Result.error("注册失败");
    }
}