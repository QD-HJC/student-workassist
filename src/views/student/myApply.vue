<template>
  <div style="padding:10px;">
    <h3>我的岗位报名记录</h3>
    <el-table :data="applyList" border style="margin-top:15px;">
      <el-table-column label="岗位名称" prop="jobPost.postName"/>
      <!-- <el-table-column label="所属部门" prop="jobPost.deptName"/> -->
      <el-table-column label="报名时间" prop="applyTime"/>
      <el-table-column label="报名状态" prop="status"/>
      <el-table-column label="面试结果" prop="interviewResult"/>
      <el-table-column label="操作">
        <template #default="scope">
          <!-- 函数名统一为 handleCancelApply -->
          <el-button 
            v-if="scope.row.status === '待审核'" 
            type="danger" 
            size="small" 
            @click="handleCancelApply(scope.row)"
          >取消报名</el-button>
          <span v-else>不可取消</span>
        </template>
      </el-table-column>
    </el-table>
    <!-- 取消确认弹窗 -->
    <el-dialog v-model="cancelDialogVisible" title="提示" width="400px">
      <p>确定要取消报名【{{currentApply.jobPost.postName}}】吗？取消后无法恢复！</p>
      <template #footer>
        <el-button @click="cancelDialogVisible = false">再想想</el-button>
        <el-button type="danger" @click="submitCancel">确认取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserInfo } from '../../utils/storage'
import { deleteApply, getMyApply } from '../../api/apply'
import { ElMessage } from 'element-plus'

const applyList = ref([])
const user = getUserInfo()
// 弹窗相关变量
const cancelDialogVisible = ref(false)
const currentApply = ref({})

// 加载我的报名列表
const loadMyApply = async () => {
  // 增加用户登录判断
  if (!user?.userId) {
    ElMessage.warning('请先登录！')
    return
  }
  const res = await getMyApply(user.userId)
  if (res.code === 200) {
    applyList.value = res.data || []
  } else {
    applyList.value = []
    ElMessage.error(res.msg || '加载报名记录失败')
  }
}

// 打开取消弹窗（接收整行数据）
const handleCancelApply = (row) => {
  currentApply.value = row
  cancelDialogVisible.value = true
}

// 真正执行取消接口
const submitCancel = async () => {
  try {
    const applyId = currentApply.value.applyId
    const res = await deleteApply(applyId)
    // 判断后端返回码
    if (res.code === 200) {
      cancelDialogVisible.value = false
      ElMessage.success('取消报名成功')
      // 刷新列表
      loadMyApply()
    } else {
      ElMessage.error(res.msg || '取消失败，当前状态不允许取消')
    }
  } catch (error) {
    ElMessage.error('网络异常，取消报名失败，请稍后重试')
  }
}

onMounted(loadMyApply)
</script>