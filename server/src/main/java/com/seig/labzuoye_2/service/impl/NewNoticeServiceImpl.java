package com.seig.labzuoye_2.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seig.labzuoye_2.domain.NewNotice;
import com.seig.labzuoye_2.mapper.NewNoticeMapper;
import com.seig.labzuoye_2.service.INewNoticeService;
import org.springframework.stereotype.Service;

@Service
public class NewNoticeServiceImpl extends ServiceImpl<NewNoticeMapper, NewNotice> implements INewNoticeService {
}