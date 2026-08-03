package com.seig.labzuoye_2.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seig.labzuoye_2.domain.Attendance;
import com.seig.labzuoye_2.mapper.AttendanceMapper;
import com.seig.labzuoye_2.service.AttendanceService;
import org.springframework.stereotype.Service;

@Service
public class AttendanceServiceImpl extends ServiceImpl<AttendanceMapper, Attendance> implements AttendanceService {
}
