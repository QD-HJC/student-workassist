package com.seig.labzuoye_2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seig.labzuoye_2.domain.JobApply;
import com.seig.labzuoye_2.domain.JobPost;
import com.seig.labzuoye_2.service.IJobApplyService;
import com.seig.labzuoye_2.service.IJobPostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class JobApplyTests {
    @Autowired
    private IJobApplyService applyService;
    @Autowired
    private IJobPostService postService;

    //1.新增学生报名(关联已有岗位postId=1)
    @Test
    void saveApply(){
        JobApply apply = new JobApply();
        apply.setPostId(1); //关联图书馆岗位
        apply.setStudentId(1);//学生张三
        apply.setStatus("待审核");
        apply.setInterviewResult("待面试");
        applyService.save(apply);
    }

    //2.多表关联：根据岗位查所有报名（一对多，作业要求核心）
    @Test
    void queryApplyWithPost(){
        QueryWrapper<JobApply> wrapper = new QueryWrapper<>();
        wrapper.eq("post_id",1);
        List<JobApply> list = applyService.list(wrapper);
        //关联查询岗位信息
        JobPost post = postService.getById(1);
        for(JobApply apply:list){
            apply.setJobPost(post);
            System.out.println("报名信息："+apply+"====对应岗位："+apply.getJobPost());
        }
    }

    //3.修改报名
    @Test
    void updateApply(){
        JobApply apply = new JobApply();
        apply.setApplyId(1);
        apply.setStatus("面试通过");
        applyService.updateById(apply);
    }

    //4.删除报名
    @Test
    void delApply(){
        applyService.removeById(1);
    }

    //5.分页查询+关联岗位
    @Test
    void pageApply(){
        Page<JobApply> page = applyService.page(new Page<>(1,10));
        page.getRecords().forEach(ap->{
            ap.setJobPost(postService.getById(ap.getPostId()));
            System.out.println(ap);
        });
    }
}
