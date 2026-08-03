<template>
  <div class="post-page">
    <el-input v-model="keyWord" placeholder="搜索岗位名称" style="width:300px" @input="getPostList"></el-input>
    <el-table :data="postList" border style="margin-top:15px">
      <el-table-column label="岗位名称" prop="postName"/>
      <!-- <el-table-column label="所属部门" prop="deptName"/> -->
      <el-table-column label="工作地点/校区" prop="address"/>
      <el-table-column label="时薪(元)" prop="salary"/>
      <el-table-column label="工作时间" prop="workTime"/>
      <el-table-column label="岗位状态" prop="status"/>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="primary" @click="applyPost(scope.row)" v-if="scope.row.status==='已发布'">报名</el-button>
          <span v-else>不可报名</span>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination 
      v-model:current-page="page.pageNum" 
      v-model:page-size="page.pageSize"
      :total="total" 
      @change="getPostList"
      style="margin-top:15px;text-align:right"
    />
    <!-- 报名弹窗 -->
    <el-dialog v-model="dialogVisible" title="确认报名">
      <p>确定报名：{{currentPost.postName}}？</p>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApply">确认报名</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getPostPage } from '../../api/post'
import { saveApply } from '../../api/apply'
import { getUserInfo } from '../../utils/storage'
import { ElMessage } from 'element-plus'

const route = useRoute()
const userInfo = getUserInfo()
const keyWord = ref(route.query.keyWord || '')
const categoryId = ref(route.query.categoryId || '')

const postList = ref([])
const total = ref(0)
const page = ref({
  pageNum: 1,
  pageSize: 10
})
const dialogVisible = ref(false)
const currentPost = ref({})

// 查询岗位（支持分类+搜索）
const getPostList = async ()=>{
  const params = {
    pageNum: page.value.pageNum,
    pageSize: page.value.pageSize,
    postName: keyWord.value,
    postType: route.query.postType || ''
  }
  const res = await getPostPage(params)
  if(res.code === 200){
    postList.value = res.data.records
    total.value = res.data.total
  }else{
    postList.value = []
    total.value = 0
  }
}

// 打开报名弹窗
const applyPost = (row)=>{
  currentPost.value = row
  dialogVisible.value = true
}

// 提交报名
const submitApply = async ()=>{
  const params = {
    postId: currentPost.value.postId,
    studentId: userInfo.userId
  }
  await saveApply(params)
  dialogVisible.value = false
  ElMessage.success('报名成功！')
  getPostList()
}

// 监听路由分类切换，刷新列表
watch(() => route.query, () => {
  keyWord.value = route.query.keyWord || ''
  categoryId.value = route.query.categoryId || ''
  page.value.pageNum = 1
  getPostList()
}, { immediate: true })
</script>