/*
 Navicat Premium Data Transfer

 Source Server         : root
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : localhost:3306
 Source Schema         : db_qgzx

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 09/07/2026 13:53:39
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_attendance
-- ----------------------------
DROP TABLE IF EXISTS `tb_attendance`;
CREATE TABLE `tb_attendance`  (
  `attend_id` int(0) NOT NULL AUTO_INCREMENT,
  `student_id` int(0) NULL DEFAULT NULL,
  `post_id` int(0) NULL DEFAULT NULL,
  `check_in` datetime(0) NULL DEFAULT NULL COMMENT '打卡上班',
  `check_out` datetime(0) NULL DEFAULT NULL COMMENT '打卡下班',
  `work_date` date NULL DEFAULT NULL COMMENT '工作日期',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '正常' COMMENT '正常/迟到/早退/缺勤',
  PRIMARY KEY (`attend_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考勤记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_attendance
-- ----------------------------
INSERT INTO `tb_attendance` VALUES (1, 1, 7, '2026-07-03 14:56:28', '2026-07-03 17:56:37', '2026-07-03', '1');
INSERT INTO `tb_attendance` VALUES (2, 1, 7, '2026-07-06 15:39:40', '2026-07-06 16:55:40', '2026-07-06', '1');
INSERT INTO `tb_attendance` VALUES (3, 1, 7, '2026-07-07 21:45:15', '2026-07-07 21:45:20', '2026-07-07', '1');
INSERT INTO `tb_attendance` VALUES (4, 1, 1, '2026-07-01 08:00:00', '2026-07-01 17:00:00', '2026-07-01', '正常');
INSERT INTO `tb_attendance` VALUES (5, 1, 1, '2026-07-02 08:10:00', '2026-07-02 17:00:00', '2026-07-02', '迟到');
INSERT INTO `tb_attendance` VALUES (6, 1, 1, '2026-07-03 08:00:00', '2026-07-03 16:30:00', '2026-07-03', '早退');
INSERT INTO `tb_attendance` VALUES (7, 1, 1, '2026-07-04 08:00:00', '2026-07-04 17:00:00', '2026-07-04', '正常');
INSERT INTO `tb_attendance` VALUES (8, 2, 1, '2026-07-01 08:00:00', '2026-07-01 17:00:00', '2026-07-01', '正常');
INSERT INTO `tb_attendance` VALUES (9, 2, 1, '2026-07-02 08:20:00', '2026-07-02 17:00:00', '2026-07-02', '迟到');
INSERT INTO `tb_attendance` VALUES (10, 3, 2, '2026-07-01 18:00:00', '2026-07-01 22:00:00', '2026-07-01', '正常');
INSERT INTO `tb_attendance` VALUES (11, 3, 2, '2026-07-02 18:00:00', '2026-07-02 22:00:00', '2026-07-02', '正常');
INSERT INTO `tb_attendance` VALUES (12, 4, 3, '2026-07-03 09:00:00', '2026-07-03 17:00:00', '2026-07-03', '正常');
INSERT INTO `tb_attendance` VALUES (13, 5, 4, '2026-07-04 08:00:00', '2026-07-04 12:00:00', '2026-07-04', '正常');

-- ----------------------------
-- Table structure for tb_blacklist
-- ----------------------------
DROP TABLE IF EXISTS `tb_blacklist`;
CREATE TABLE `tb_blacklist`  (
  `black_id` int(0) NOT NULL AUTO_INCREMENT,
  `student_id` int(0) NOT NULL,
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '拉黑原因',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '生效' COMMENT '生效/已解除',
  PRIMARY KEY (`black_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '黑名单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_blacklist
-- ----------------------------
INSERT INTO `tb_blacklist` VALUES (1, 4, '多次迟到早退，工作态度不端正', '2026-07-09 01:40:36', '生效');
INSERT INTO `tb_blacklist` VALUES (2, 5, '旷工3次，严重违反工作纪律', '2026-07-09 01:40:36', '生效');

-- ----------------------------
-- Table structure for tb_dept
-- ----------------------------
DROP TABLE IF EXISTS `tb_dept`;
CREATE TABLE `tb_dept`  (
  `dept_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '部门名称',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用工部门' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_dept
-- ----------------------------
INSERT INTO `tb_dept` VALUES (1, '图书馆', '2026-07-09 00:59:00');
INSERT INTO `tb_dept` VALUES (2, '后勤处', '2026-07-09 00:59:00');
INSERT INTO `tb_dept` VALUES (3, '学生处', '2026-07-09 00:59:00');
INSERT INTO `tb_dept` VALUES (4, '教务处', '2026-07-09 00:59:00');

-- ----------------------------
-- Table structure for tb_evaluation
-- ----------------------------
DROP TABLE IF EXISTS `tb_evaluation`;
CREATE TABLE `tb_evaluation`  (
  `eval_id` int(0) NOT NULL AUTO_INCREMENT,
  `student_id` int(0) NULL DEFAULT NULL,
  `post_id` int(0) NULL DEFAULT NULL,
  `score` int(0) NULL DEFAULT NULL COMMENT '分数1-100',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评价内容',
  `eval_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  `eval_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评价配图',
  PRIMARY KEY (`eval_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学生考核评价' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_evaluation
-- ----------------------------
INSERT INTO `tb_evaluation` VALUES (1, 1, 7, 8, '工作相对轻松，工资及时发放', '2026-07-03 16:48:12', NULL);
INSERT INTO `tb_evaluation` VALUES (2, 1, 7, 3, '工作内容简单', '2026-07-06 16:54:32', NULL);
INSERT INTO `tb_evaluation` VALUES (6, 1, 7, 3, '<p><u><span class=\"ql-cursor\">﻿﻿</span></u></p>', '2026-07-07 21:59:13', '/api/upload/20260707/1783432742178_e09c1237-b4e0-48d8-909e-a13a48293560.png');
INSERT INTO `tb_evaluation` VALUES (7, 5, 3, 3, '工作认真', '2026-07-09 01:44:59', NULL);
INSERT INTO `tb_evaluation` VALUES (8, 1, 1, 5, '工作认真负责，表现优秀！', '2026-07-09 01:46:50', NULL);
INSERT INTO `tb_evaluation` VALUES (9, 2, 1, 4, '工作态度良好，偶尔迟到需改进！', '2026-07-09 01:46:50', NULL);
INSERT INTO `tb_evaluation` VALUES (10, 3, 2, 5, '夜班工作非常认真，值得表扬！', '2026-07-09 01:46:50', NULL);
INSERT INTO `tb_evaluation` VALUES (11, 4, 3, 3, '工作能力一般，需要加强学习！', '2026-07-09 01:46:50', NULL);
INSERT INTO `tb_evaluation` VALUES (12, 5, 4, 5, '绿化维护工作完成得很好！', '2026-07-09 01:46:50', NULL);
INSERT INTO `tb_evaluation` VALUES (13, 1, 2, 5, '夜班工作认真负责，值得表扬！', '2026-07-09 01:46:50', NULL);
INSERT INTO `tb_evaluation` VALUES (14, 2, 2, 4, '工作态度端正，表现良好！', '2026-07-09 01:46:50', NULL);

-- ----------------------------
-- Table structure for tb_job_apply
-- ----------------------------
DROP TABLE IF EXISTS `tb_job_apply`;
CREATE TABLE `tb_job_apply`  (
  `apply_id` int(0) NOT NULL AUTO_INCREMENT,
  `post_id` int(0) NULL DEFAULT NULL COMMENT '岗位id',
  `student_id` int(0) NULL DEFAULT NULL COMMENT '学生user_id',
  `apply_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  `interview_result` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '待面试' COMMENT '面试结果',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '待审核' COMMENT '报名状态',
  PRIMARY KEY (`apply_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '岗位报名表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_job_apply
-- ----------------------------
INSERT INTO `tb_job_apply` VALUES (8, 7, 1, '2026-06-28 17:38:15', '已面试', '在岗');
INSERT INTO `tb_job_apply` VALUES (12, 7, 1, '2026-07-03 01:40:20', '待面试', '待审核');
INSERT INTO `tb_job_apply` VALUES (14, 2, 1, '2026-07-03 15:54:23', '待面试', '待审核');
INSERT INTO `tb_job_apply` VALUES (19, 4, 1, '2026-07-03 22:11:09', '待面试', '待审核');
INSERT INTO `tb_job_apply` VALUES (20, 6, 1, '2026-07-07 21:03:36', '待面试', '待审核');
INSERT INTO `tb_job_apply` VALUES (21, 2, 1, '2026-07-07 21:07:31', '待面试', '待审核');
INSERT INTO `tb_job_apply` VALUES (22, 2, 1, '2026-07-07 21:14:25', '待面试', '待审核');
INSERT INTO `tb_job_apply` VALUES (23, 4, 1, '2026-07-07 21:43:24', '待面试', '已录用');
INSERT INTO `tb_job_apply` VALUES (24, 5, 1, '2026-07-07 21:44:19', '待面试', '待审核');
INSERT INTO `tb_job_apply` VALUES (25, 4, 1, '2026-07-07 21:55:34', '待面试', '待审核');
INSERT INTO `tb_job_apply` VALUES (26, 2, 1, '2026-07-07 21:56:21', '待面试', '待审核');
INSERT INTO `tb_job_apply` VALUES (27, 1, 1, '2026-07-09 00:31:15', '待面试', '待审核');
INSERT INTO `tb_job_apply` VALUES (28, 2, 1, '2026-07-09 00:31:15', '面试通过', '已录用');
INSERT INTO `tb_job_apply` VALUES (29, 2, 2, '2026-07-09 00:31:15', '待面试', '待审核');
INSERT INTO `tb_job_apply` VALUES (30, 1, 1, '2026-07-09 01:16:38', '待面试', '待审核');
INSERT INTO `tb_job_apply` VALUES (31, 2, 1, '2026-07-09 01:16:38', '面试通过', '已录用');
INSERT INTO `tb_job_apply` VALUES (32, 1, 2, '2026-07-09 01:16:38', '待面试', '待审核');
INSERT INTO `tb_job_apply` VALUES (33, 3, 3, '2026-07-09 01:16:38', '面试通过', '在岗');
INSERT INTO `tb_job_apply` VALUES (34, 8, 1, '2026-07-09 02:47:25', '待面试', '待审核');

-- ----------------------------
-- Table structure for tb_job_post
-- ----------------------------
DROP TABLE IF EXISTS `tb_job_post`;
CREATE TABLE `tb_job_post`  (
  `post_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '岗位id',
  `dept_id` int(0) NULL DEFAULT NULL COMMENT '部门用户id',
  `post_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '岗位名称',
  `work_time` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '工作时间',
  `address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '工作地点',
  `salary` decimal(10, 2) NULL DEFAULT NULL COMMENT '时薪',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '待审核' COMMENT '待审核/已发布/已下架',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  `post_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '岗位分类类型',
  `img_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '岗位封面图地址',
  PRIMARY KEY (`post_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '勤工岗位表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_job_post
-- ----------------------------
INSERT INTO `tb_job_post` VALUES (2, 1, '图书馆整理助理', '周一至周五晚', '图书馆一楼', 18.50, '已发布', '2026-06-06 16:28:10', NULL, NULL);
INSERT INTO `tb_job_post` VALUES (4, 2, '实验室助理', '周末全天', '实验楼305', 22.00, '已发布', '2026-06-06 17:12:00', NULL, NULL);
INSERT INTO `tb_job_post` VALUES (5, 3, '实验室助理', '周末全天', '实验楼305', 22.00, '已发布', '2026-06-06 22:17:51', NULL, NULL);
INSERT INTO `tb_job_post` VALUES (6, 4, '实验室助理', '周末全天', '实验楼305', 22.00, '已发布', '2026-06-06 22:23:17', NULL, NULL);
INSERT INTO `tb_job_post` VALUES (7, 5, '实验室助理', '周末全天', '实验楼305', 22.00, '已发布', '2026-06-06 22:25:11', NULL, NULL);
INSERT INTO `tb_job_post` VALUES (8, 6, '辅导员助理', '周三-周五', '日新楼404', 13.00, '已发布', '2026-06-28 17:41:48', NULL, NULL);
INSERT INTO `tb_job_post` VALUES (9, 7, '资料整理', '10:00-12:00', '实验b2', 17.00, '已下架', '2026-06-29 21:36:51', NULL, NULL);
INSERT INTO `tb_job_post` VALUES (10, 1, '图书管理员助理', '周一至周五 9:00-17:00', '图书馆一楼', 20.00, '待审核', '2026-07-09 00:31:15', '行政助理', NULL);
INSERT INTO `tb_job_post` VALUES (11, 1, '图书馆夜班管理员', '周一至周五 18:00-22:00', '图书馆二楼', 25.00, '已发布', '2026-07-09 00:31:15', '行政助理', NULL);
INSERT INTO `tb_job_post` VALUES (12, 1, '图书馆周末管理员', '周六周日 9:00-17:00', '图书馆三楼', 22.00, '待审核', '2026-07-09 00:31:15', '行政助理', NULL);
INSERT INTO `tb_job_post` VALUES (13, 2, '助教', '8:00-12:00', 'A栋楼', 45.00, '待审核', '2026-07-09 01:01:14', '其他助理', NULL);

-- ----------------------------
-- Table structure for tb_salary
-- ----------------------------
DROP TABLE IF EXISTS `tb_salary`;
CREATE TABLE `tb_salary`  (
  `salary_id` int(0) NOT NULL AUTO_INCREMENT,
  `student_id` int(0) NULL DEFAULT NULL,
  `post_id` int(0) NULL DEFAULT NULL,
  `month` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '月份 2026-05',
  `total_salary` decimal(10, 2) NULL DEFAULT NULL COMMENT '当月总薪资',
  `pay_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '待发放' COMMENT '待发放/已发放',
  `pay_time` datetime(0) NULL DEFAULT NULL COMMENT '发放时间',
  `total_hour` decimal(5, 2) NULL DEFAULT NULL COMMENT '当月总工时',
  PRIMARY KEY (`salary_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '薪资结算表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_salary
-- ----------------------------
INSERT INTO `tb_salary` VALUES (1, 1, 7, '2026-07', 2500.00, '已发放', '2026-07-03 15:12:36', 42.00);
INSERT INTO `tb_salary` VALUES (2, 1, 2, '2026-07', 800.00, '待发放', NULL, 40.00);
INSERT INTO `tb_salary` VALUES (3, 2, 1, '2026-07', 600.00, '待发放', NULL, 30.00);
INSERT INTO `tb_salary` VALUES (4, 1, 1, '2026-06', 800.00, '已发放', '2026-06-30 17:00:00', 40.00);
INSERT INTO `tb_salary` VALUES (5, 2, 1, '2026-06', 700.00, '已发放', '2026-06-30 17:00:00', 35.00);
INSERT INTO `tb_salary` VALUES (6, 3, 2, '2026-06', 750.00, '已发放', '2026-06-30 17:00:00', 30.00);
INSERT INTO `tb_salary` VALUES (7, 1, 1, '2026-07', 840.00, '待发放', NULL, 42.00);
INSERT INTO `tb_salary` VALUES (8, 2, 1, '2026-07', 760.00, '待发放', NULL, 38.00);
INSERT INTO `tb_salary` VALUES (9, 3, 2, '2026-07', 800.00, '待发放', NULL, 32.00);
INSERT INTO `tb_salary` VALUES (10, 4, 3, '2026-07', 550.00, '待发放', NULL, 25.00);
INSERT INTO `tb_salary` VALUES (11, 5, 4, '2026-07', 500.00, '待发放', NULL, 20.00);
INSERT INTO `tb_salary` VALUES (12, 4, 3, '2026-06', 440.00, '已发放', '2026-06-30 17:00:00', 20.00);
INSERT INTO `tb_salary` VALUES (13, 5, 4, '2026-06', 375.00, '已发放', '2026-06-30 17:00:00', 15.00);
INSERT INTO `tb_salary` VALUES (14, 1, 1, '2026-07', 840.00, '已发放', '2026-07-09 03:30:04', 42.00);
INSERT INTO `tb_salary` VALUES (15, 1, 2, '2026-07', 750.00, '待发放', NULL, 30.00);
INSERT INTO `tb_salary` VALUES (16, 1, 1, '2026-06', 800.00, '已发放', '2026-06-30 17:00:00', 40.00);

-- ----------------------------
-- Table structure for tb_student
-- ----------------------------
DROP TABLE IF EXISTS `tb_student`;
CREATE TABLE `tb_student`  (
  `student_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '学生ID',
  `student_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学号',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `class_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '班级名称',
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '在校' COMMENT '状态：在校/毕业/休学',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`student_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学生信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_student
-- ----------------------------
INSERT INTO `tb_student` VALUES (1, '2024001', '张三', '13800138001', '计算机科学与技术1班', '在校', '2026-07-09 01:16:11');
INSERT INTO `tb_student` VALUES (2, '2024002', '李四', '13800138002', '计算机科学与技术2班', '在校', '2026-07-09 01:16:11');
INSERT INTO `tb_student` VALUES (3, '2024003', '王五', '13800138003', '软件工程1班', '在校', '2026-07-09 01:16:11');

-- ----------------------------
-- Table structure for tb_sys_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_user`;
CREATE TABLE `tb_sys_user`  (
  `user_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账号',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `real_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色:学生/用工部门/资助中心',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '正常' COMMENT '状态：正常/黑名单',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `dept_id` int(0) NULL DEFAULT NULL COMMENT '部门ID',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_sys_user
-- ----------------------------
INSERT INTO `tb_sys_user` VALUES (1, 'stu001', '123456', '张三', '学生', '13800138001', '正常', '2026-06-06 16:27:59', NULL);
INSERT INTO `tb_sys_user` VALUES (2, 'dept001', '123456', '图书馆管理员', '用工部门', '13900139001', '正常', '2026-06-06 16:27:59', 1);
INSERT INTO `tb_sys_user` VALUES (3, 'admin001', '123456', '资源中心人员', '资助中心', '17824563451', '正常', '2026-06-29 21:15:00', NULL);
INSERT INTO `tb_sys_user` VALUES (4, 'zhj', '123456', '张花', '学生', '13567854567', '正常', '2026-07-09 10:58:48', NULL);

SET FOREIGN_KEY_CHECKS = 1;
