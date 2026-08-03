<template>
  <div class="attendance-wrap">
    <h3>考勤打卡</h3>
    <!-- 外层居中容器 -->
    <div class="card-center-box">
      <el-card class="clock-card" shadow="hover">
        <div class="time-text">{{ nowTime }}</div>
        <!-- 按钮改为垂直上下排列 -->
        <div class="btn-stack">
          <el-button type="success" size="large" @click="clockIn">上班打卡</el-button>
          <el-button type="danger" size="large" @click="clockOut">下班打卡</el-button>
        </div>
      </el-card>
    </div>

    <h4 style="margin-top:30px">本月打卡记录</h4>
    <el-table :data="clockList" border style="margin-top:15px">
      <el-table-column label="岗位名称" prop="postName"/>
      <el-table-column label="打卡日期" prop="workDate"/>
      <el-table-column label="上班时间" prop="checkIn"/>
      <el-table-column label="下班时间" prop="checkOut"/>
      <el-table-column label="当日工时" prop="hour"/>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserInfo } from '../../utils/storage'
import { getClockList, addClock } from '../../api/attendance'
import { ElMessage } from 'element-plus'
// 新增：获取学生在岗岗位接口
import { getRunningPost } from '../../api/student'

const user = getUserInfo()
const nowTime = ref(new Date().toLocaleString())
const clockList = ref([])
// 缺失的变量，补上定义
const currentPostId = ref(null)

// 刷新时间
setInterval(()=>{
  nowTime.value = new Date().toLocaleString()
}, 1000)

// 加载当前在岗岗位
const getCurrentPost = async () => {
  const res = await getRunningPost(user.userId)
  if(res.code === 200){
    currentPostId.value = res.data.postId
  }
}

// 加载打卡记录
const loadClock = async () => {
  const res = await getClockList(user.userId)
  console.log("打卡列表接口返回：", res)
  if(res.code === 200) {
    clockList.value = res.data
    console.log("赋值后的打卡数组：", clockList.value)
  }
}
// 上班打卡
const clockIn = async () => {
  // 增加判断，无岗位禁止打卡
  if(!currentPostId.value){
    return ElMessage.warning("暂无在岗岗位，无法打卡！")
  }
  await addClock({ 
    studentId: user.userId, 
    postId: currentPostId.value,
    status: "1"
  })
  ElMessage.success('上班打卡成功')
  loadClock()
}
// 下班打卡
const clockOut = async () => {
  if(!currentPostId.value){
    return ElMessage.warning("暂无在岗岗位，无法打卡！")
  }
  await addClock({ 
    studentId: user.userId, 
    postId: currentPostId.value,
    status: "2"
  })
  ElMessage.success('下班打卡成功')
  loadClock()
}

// 页面加载同时获取岗位+打卡记录
onMounted(()=>{
  getCurrentPost()
  loadClock()
})
</script>

<style scoped>
.attendance-wrap {
  width: 100%;
}
/* 核心：让卡片整体水平居中 */
.card-center-box {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
.clock-card {
  width: 400px;
  text-align: center;
  padding: 40px 0;
}
.time-text {
  font-size: 32px;
  margin-bottom: 30px;
}
/* 按钮垂直堆叠，上下间距 */
.btn-stack {
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: center;
}
</style>