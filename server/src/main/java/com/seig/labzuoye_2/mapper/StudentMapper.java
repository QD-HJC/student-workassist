package com.seig.labzuoye_2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seig.labzuoye_2.domain.Student;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {
}