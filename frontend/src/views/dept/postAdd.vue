<template>
  <div class="post-add-page">
    <div class="page-header">
      <h2 class="page-title">📝 发布新岗位</h2>
      <p class="page-desc">填写岗位信息，提交后等待资助中心审核</p>
    </div>

    <el-card class="form-card">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" label-position="left">
        <el-row :gutter="40">
          <el-col :span="12">
            <el-form-item label="岗位名称" prop="postName">
              <el-input v-model="form.postName" placeholder="请输入岗位名称" size="large" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="岗位分类" prop="postType">
              <el-select v-model="form.postType" placeholder="请选择岗位分类" size="large" style="width:100%;">
                <el-option label="行政助理" value="行政助理" />
                <el-option label="其他助理" value="其他助理" />
                <el-option label="后勤服务" value="后勤服务" />
                <el-option label="实验室管理员" value="实验室管理员" />
                <el-option label="新媒体运营" value="新媒体运营" />
                <el-option label="外语翻译" value="外语翻译" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="40">
          <el-col :span="12">
            <el-form-item label="工作地点" prop="address">
              <el-input v-model="form.address" placeholder="请输入工作地点" size="large" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="小时薪资" prop="salary">
              <el-input v-model="form.salary" placeholder="请输入小时薪资" size="large" type="number">
                <template #append>元/小时</template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="工作时间" prop="workTime">
          <el-input v-model="form.workTime" placeholder="例如：周一至周五 9:00-17:00" size="large" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" @click="submit" :loading="loading" class="submit-btn">
            <el-icon><Upload /></el-icon>
            {{ loading ? '提交中...' : '提交岗位（等待资助中心审核）' }}
          </el-button>
          <el-button size="large" @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { savePost } from '../../api/post'
import { getUserInfo } from '../../utils/storage'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { Upload } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const formRef = ref(null)
const user = ref(null)

const form = reactive({
  deptId: '',
  postName: '',
  address: '',
  workTime: '',
  salary: '',
  postType: ''
})

const rules = {
  postName: [{ required: true, message: '请输入岗位名称', trigger: 'blur' }],
  address: [{ required: true, message: '请输入工作地点', trigger: 'blur' }],
  workTime: [{ required: true, message: '请输入工作时间', trigger: 'blur' }],
  salary: [{ required: true, message: '请输入小时薪资', trigger: 'blur' }],
  postType: [{ required: true, message: '请选择岗位分类', trigger: 'change' }]
}

const initUser = () => {
  const userInfo = getUserInfo()
  if (userInfo) {
    user.value = userInfo
    form.deptId = userInfo.deptId || userInfo.userId || 1
  }
}

const submit = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await savePost(form)
    if (res.code === 200) {
      ElMessage.success('🎉 发布成功，等待资助中心审核')
      resetForm()
      setTimeout(() => {
        router.push('/dept/home')
      }, 1500)
    } else {
      ElMessage.error(res.msg || '发布失败')
    }
  } catch (err) {
    ElMessage.error('发布失败，请检查网络')
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.postName = ''
  form.address = ''
  form.workTime = ''
  form.salary = ''
  form.postType = ''
  formRef.value?.clearValidate()
}

onMounted(initUser)
</script>

<style scoped>
.post-add-page {
  width: 100%;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 22px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 4px;
}

.page-desc {
  font-size: 14px;
  color: #8a9aa9;
  margin: 0;
}

.form-card {
  border-radius: 12px;
  border: 1px solid #e8ecf1;
  max-width: 900px;
}

.form-card :deep(.el-card__body) {
  padding: 32px 40px 28px;
}

.submit-btn {
  padding: 12px 40px;
  font-size: 16px;
  border-radius: 10px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #2c3e50;
}

:deep(.el-input__wrapper) {
  border-radius: 10px;
}
</style>