package com.seig.labzuoye_2.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seig.labzuoye_2.domain.JobApply;
import com.seig.labzuoye_2.domain.JobPost;
import com.seig.labzuoye_2.domain.Salary;
import com.seig.labzuoye_2.service.IJobApplyService;
import com.seig.labzuoye_2.service.IJobPostService;
import com.seig.labzuoye_2.service.ISalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@EnableScheduling
public class ScheduledTask {

    @Autowired
    private IJobApplyService jobApplyService;

    @Autowired
    private IJobPostService jobPostService;

    @Autowired
    private ISalaryService salaryService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 每天凌晨2点自动将过期的岗位状态更新
     * 假设岗位发布超过30天自动过期
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void autoExpirePosts() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        LambdaQueryWrapper<JobPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JobPost::getStatus, "已发布")
                .lt(JobPost::getCreateTime, thirtyDaysAgo);

        List<JobPost> expiredPosts = jobPostService.list(wrapper);
        for (JobPost post : expiredPosts) {
            post.setStatus("已过期");
            jobPostService.updateById(post);
        }
        System.out.println("【定时任务】自动过期岗位数：" + expiredPosts.size());
    }

    /**
     * 每月1号凌晨3点生成上月薪资记录
     */
    @Scheduled(cron = "0 0 3 1 * ?")
    public void generateMonthlySalary() {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        String monthStr = lastMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        // 查询所有已录用且未生成薪资的学生
        LambdaQueryWrapper<JobApply> applyWrapper = new LambdaQueryWrapper<>();
        applyWrapper.eq(JobApply::getStatus, "已录用");
        List<JobApply> applies = jobApplyService.list(applyWrapper);

        for (JobApply apply : applies) {
            // 检查是否已存在该月薪资
            LambdaQueryWrapper<Salary> salaryWrapper = new LambdaQueryWrapper<>();
            salaryWrapper.eq(Salary::getStudentId, apply.getStudentId())
                    .eq(Salary::getMonth, monthStr);
            long count = salaryService.count(salaryWrapper);
            if (count == 0) {
                Salary salary = new Salary();
                salary.setStudentId(apply.getStudentId());
                salary.setPostId(apply.getPostId());
                salary.setMonth(monthStr);
                salary.setTotalHour(0.0);
                salary.setTotalSalary(0.0);
                salary.setPayStatus("待发放");
                salaryService.save(salary);
            }
        }
        System.out.println("【定时任务】生成薪资记录完成，月份：" + monthStr);
    }

    /**
     * 每小时清理Redis中的过期缓存
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void cleanRedisCache() {
        // 清理过期的岗位列表缓存
        redisTemplate.delete("post:list:all");
        System.out.println("【定时任务】清理Redis缓存完成");
    }
}