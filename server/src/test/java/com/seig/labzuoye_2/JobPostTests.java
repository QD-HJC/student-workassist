package com.seig.labzuoye_2;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seig.labzuoye_2.domain.JobPost;
import com.seig.labzuoye_2.service.IJobPostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
public class JobPostTests {
    @Autowired
    private IJobPostService postService;

    //分页查询全部
    @Test
    void testPageAll(){
        Page<JobPost> page = postService.page(new Page<>(2,10));
        List<JobPost> list = page.getRecords();
        list.forEach(System.out::println);
        System.out.println("当前页："+page.getCurrent());
        System.out.println("总条数："+page.getTotal());
    }
    //新增岗位
    @Test
    void testSave(){
        JobPost post = new JobPost();
        post.setPostName("实验室助理");
        post.setWorkTime("周末全天");
        post.setAddress("实验楼305");
        post.setSalary(new BigDecimal("22.0"));
        post.setStatus("已发布");
        postService.save(post);
    }
    //修改
    @Test
    void testUpdate(){
        JobPost post = new JobPost();
        Integer newId = post.getPostId(); //获取刚插入的主键
//再执行更新
        post.setPostName("实验室管理员");
        postService.updateById(post);
    }
    //删除
    @Test
    void testDelete(){
        postService.removeById(1);
    }
}
