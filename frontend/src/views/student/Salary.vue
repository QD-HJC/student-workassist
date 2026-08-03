<template>
  <div>
    <h3>我的薪资明细</h3>
    <el-table :data="salaryList" border style="margin-top:20px">
      <el-table-column label="结算月份" prop="month"/>
      <el-table-column label="对应岗位" prop="postName"/>
      <el-table-column label="总工时" prop="totalHour"/>
      <el-table-column label="应发薪资" prop="totalSalary"/>
      <el-table-column label="发放状态" prop="payStatus"/>
      <el-table-column label="发放时间" prop="payTime"/>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserInfo } from '../../utils/storage'
import { getStudentSalary } from '../../api/salary'

const user = getUserInfo()
const salaryList = ref([])

const loadSalary = async () => {
  const res = await getStudentSalary(user.userId)
  if(res.code === 200) salaryList.value = res.data
}

onMounted(loadSalary)
</script>