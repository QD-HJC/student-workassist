<template>
  <div>
    <h3>岗位评价记录</h3>
    <el-table :data="evaList" border style="margin-top:15px">
      <el-table-column label="岗位名称" prop="postName"/>
      <el-table-column label="评价星级" prop="score">
        <template #default="scope">
          <el-rate v-model="scope.row.score" disabled />
        </template>
      </el-table-column>
      <el-table-column label="评价配图">
        <template #default="scope">
          <el-image v-if="scope.row.evalImg" :src="scope.row.evalImg" style="width:100px" fit="cover" />
          <span v-else>无配图</span>
        </template>
      </el-table-column>
      <el-table-column label="评价内容" prop="content"/>
      <el-table-column label="评价时间" prop="evalTime"/>
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button type="primary" size="small" @click="openEdit(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-divider></el-divider>
    <h4>新增/编辑岗位评价</h4>
    <el-form :model="form" label-width="100px">
      <el-form-item label="选择岗位">
        <el-select v-model="form.postId" placeholder="请选择在岗岗位" style="width:100%">
          <el-option v-for="p in myPostList" :key="p.postId" :label="p.postName" :value="p.postId"/>
        </el-select>
      </el-form-item>
      <el-form-item label="星级评分">
        <el-rate v-model="form.score" :max="5"/>
      </el-form-item>
      <el-form-item label="评价内容">
        <!-- 文本输入框 -->
        <el-input 
          v-model="form.content" 
          type="textarea" 
          rows="4" 
          placeholder="请输入评价内容"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>
      <el-form-item label="上传配图">
        <el-upload
          :http-request="handleUpload"
          list-type="picture-card"
          :file-list="imgList"
        >
          <template #default>
            <el-icon><Plus /></el-icon>
          </template>
        </el-upload>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitEva">{{ form.evalId ? '保存修改' : '提交评价' }}</el-button>
        <el-button v-if="form.evalId" @click="resetForm">取消编辑</el-button>
      </el-form-item>
    </el-form>

    <!-- 删除确认弹窗 -->
    <el-dialog v-model="delDialogVisible" title="删除提示" width="400px">
      <p>确定删除这条评价吗？删除后无法恢复！</p>
      <template #footer>
        <el-button @click="delDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="submitDel">确认删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { getUserInfo } from '../../utils/storage'
import { getMyPost, getEvaluateList, addEvaluate, delEvaluate, updateEvaluate } from '../../api/evaluate'
import { uploadPostImg } from '../../api/post'
import { ElMessage } from 'element-plus'

const user = getUserInfo()
const evaList = ref([])
const myPostList = ref([])
const delDialogVisible = ref(false)
const currentEvalId = ref(null)
const imgList = ref([])

// 表单对象
const form = ref({
  evalId: null,
  postId: '',
  score: 3,
  content: '',
  studentId: user.userId,
  evalImg: '' // 图片路径
})

// 加载岗位和评价列表
const loadData = async () => {
  const postRes = await getMyPost(user.userId)
  if(postRes.code === 200) myPostList.value = postRes.data

  const evaRes = await getEvaluateList(user.userId)
  if(evaRes.code === 200) evaList.value = evaRes.data
}

// 重置表单
const resetForm = () => {
  form.value = {
    evalId: null,
    postId: '',
    score: 3,
    content: '',
    studentId: user.userId,
    evalImg: ''
  }
  imgList.value = []
}

// 上传图片
const handleUpload = async (opts) => {
  const res = await uploadPostImg(opts.file)
  if (res.code === 200) {
    form.value.evalImg = res.data
    imgList.value = [{ url: res.data }]
    ElMessage.success('图片上传成功')
  }
}

// 打开编辑回填图片
const openEdit = (row) => {
  // 完整覆盖表单
  form.value.evalId = row.evalId
  form.value.postId = row.postId
  form.value.score = row.score
  form.value.content = row.content
  form.value.studentId = user.userId
  form.value.evalImg = row.evalImg || ''
  // 图片预览回填
  if (row.evalImg) {
    imgList.value = [{ url: row.evalImg }]
  } else {
    imgList.value = []
  }
}

// 提交新增/修改
const submitEva = async () => {
  if (!form.value.postId) return ElMessage.warning('请选择岗位')
  if (!form.value.content) return ElMessage.warning('请填写评价内容')

  if (form.value.evalId) {
    // 修改
    await updateEvaluate(form.value)
    ElMessage.success('评价修改成功')
  } else {
    // 新增
    await addEvaluate(form.value)
    ElMessage.success('评价提交成功')
  }
  resetForm()
  loadData()
}

// 打开删除弹窗
const handleDelete = (row) => {
  currentEvalId.value = row.evalId
  delDialogVisible.value = true
}

// 执行删除
const submitDel = async () => {
  await delEvaluate(currentEvalId.value)
  delDialogVisible.value = false
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>