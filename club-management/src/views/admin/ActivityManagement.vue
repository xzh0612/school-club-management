<template>
  <div>
    <div class="stats-grid">
      <div class="stat-card"><div class="icon-bg">🎉</div><div class="label">活动总数</div><div class="value">{{ activities.length }}</div></div>
      <div class="stat-card"><div class="icon-bg">🟢</div><div class="label">可报名</div><div class="value">{{ activities.filter(a => a.status === 'approved').length }}</div></div>
      <div class="stat-card"><div class="icon-bg">⏳</div><div class="label">待审批</div><div class="value">{{ activities.filter(a => a.status === 'pending_approval').length }}</div></div>
      <div class="stat-card"><div class="icon-bg">✅</div><div class="label">已结束</div><div class="value">{{ activities.filter(a => a.status === 'completed').length }}</div></div>
    </div>
    <div class="card">
      <div class="card-header"><h3>活动列表</h3><button class="btn btn-primary btn-sm" @click="openActivityForm">＋ 创建活动</button></div>
      <div class="search-bar">
        <input v-model="searchKeyword" class="search-input" placeholder="搜索活动名称..." @keyup.enter="handleSearch" />
        <select v-model="statusFilter" class="filter-select" @change="handleSearch"><option value="">全部状态</option><option value="approved">已通过</option><option value="pending_approval">待审批</option><option value="completed">已结束</option><option value="cancelled">已取消</option></select>
        <button class="btn btn-primary btn-sm" @click="handleSearch">搜索</button>
      </div>
      <table class="data-table">
        <thead><tr><th>活动名称</th><th>所属社团</th><th>活动时间</th><th>地点</th><th>报名人数</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="a in activities" :key="a.activityId">
            <td style="font-weight:500">{{ a.title }}</td><td>{{ a.clubName }}</td><td>{{ formatDate(a.startTime) }}</td><td>{{ a.location }}</td><td>{{ a.currentParticipants || 0 }}/{{ a.maxParticipants }}</td><td><span :class="['tag', statusClass(a.status)]">{{ statusText(a.status) }}</span></td>
            <td><button class="btn btn-outline btn-sm" @click="openSignups(a)">报名</button><button class="btn btn-warning btn-sm" @click="editActivity(a)" style="margin-left:8px">编辑</button><button class="btn btn-danger btn-sm" @click="removeActivity(a)" style="margin-left:8px">删除</button></td>
          </tr>
        </tbody>
      </table>
      <div v-if="!loading && activities.length === 0" class="empty-state">暂无活动数据</div>
      <div class="pagination"><button class="btn btn-outline btn-sm" :disabled="currentPage <= 1" @click="changePage(currentPage - 1)">上一页</button><span class="page-info">第 {{ currentPage }} 页，共 {{ totalPages }} 页</span><button class="btn btn-outline btn-sm" :disabled="currentPage >= totalPages" @click="changePage(currentPage + 1)">下一页</button></div>
    </div>
    <div v-if="selectedActivity" class="card" style="margin-top:16px">
      <div class="card-header"><h3>{{ selectedActivity.title }} 报名管理</h3><button class="btn btn-outline btn-sm" @click="closeSignups">关闭</button></div>
      <table class="data-table">
        <thead><tr><th>学生</th><th>学号</th><th>报名时间</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="s in signups" :key="s.signupId">
            <td>{{ s.userName || '-' }}</td>
            <td>{{ s.userSid || '-' }}</td>
            <td>{{ formatDate(s.signupTime) }}</td>
            <td><span :class="['tag', signupStatusClass(s.status)]">{{ signupStatusText(s.status) }}</span></td>
            <td>
              <template v-if="s.status === 'pending'">
                <button class="btn btn-success btn-sm" @click="approveSignup(s)">通过</button>
                <button class="btn btn-danger btn-sm" style="margin-left:8px" @click="rejectSignup(s)">驳回</button>
              </template>
              <button v-else-if="s.status === 'approved'" class="btn btn-primary btn-sm" @click="checkin(s)">签到</button>
              <span v-else>--</span>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="signups.length === 0" class="empty-state">暂无报名数据</div>
      <div class="pagination"><button class="btn btn-outline btn-sm" :disabled="signupPage <= 1" @click="changeSignupPage(signupPage - 1)">上一页</button><span class="page-info">第 {{ signupPage }} 页，共 {{ signupTotalPages }} 页</span><button class="btn btn-outline btn-sm" :disabled="signupPage >= signupTotalPages" @click="changeSignupPage(signupPage + 1)">下一页</button></div>
    </div>
    <ActivityForm v-if="showActivityForm" :is-edit="isEditing" :initial-data="editingActivity" @close="closeActivityForm" @submit="handleActivitySubmit" />
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import ActivityForm from '../../components/ActivityForm.vue'
import { checkinSignup, createActivity, deleteActivity, getActivityList, getActivitySignups, searchActivities, updateActivity, updateSignupStatus } from '../../api/activity'

const loading = ref(false)
const activities = ref([])
const searchKeyword = ref('')
const statusFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const showActivityForm = ref(false)
const isEditing = ref(false)
const editingActivity = ref(null)
const selectedActivity = ref(null)
const signups = ref([])
const signupPage = ref(1)
const signupPageSize = ref(10)
const signupTotal = ref(0)

const extractRecords = (payload) => payload?.records || payload?.list || (Array.isArray(payload) ? payload : [])
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))
const signupTotalPages = computed(() => Math.max(1, Math.ceil(signupTotal.value / signupPageSize.value)))
const statusText = (status) => ({ approved: '已通过', pending_approval: '待审批', draft: '草稿', rejected: '已驳回', completed: '已结束', cancelled: '已取消' }[status] || status)
const statusClass = (status) => ({ approved: 'tag-green', pending_approval: 'tag-orange', rejected: 'tag-red', completed: 'tag-gray', cancelled: 'tag-gray' }[status] || 'tag-blue')
const signupStatusText = (status) => ({ pending: '待审核', approved: '已通过', rejected: '已驳回', cancelled: '已取消', attended: '已签到' }[status] || status)
const signupStatusClass = (status) => ({ pending: 'tag-orange', approved: 'tag-green', rejected: 'tag-red', cancelled: 'tag-gray', attended: 'tag-blue' }[status] || 'tag-gray')
const formatDate = (value) => value ? new Date(value).toLocaleString('zh-CN') : ''

const fetchActivities = async () => {
  loading.value = true
  try {
    const response = searchKeyword.value
      ? await searchActivities(searchKeyword.value, currentPage.value, pageSize.value, statusFilter.value)
      : await getActivityList(currentPage.value, pageSize.value, statusFilter.value, '')
    activities.value = extractRecords(response.data)
    total.value = response.data?.total || activities.value.length
  } finally {
    loading.value = false
  }
}
const handleSearch = async () => { currentPage.value = 1; await fetchActivities() }
const changePage = async (page) => { currentPage.value = Math.min(Math.max(1, page), totalPages.value); await fetchActivities() }
const openActivityForm = () => { isEditing.value = false; editingActivity.value = null; showActivityForm.value = true }
const closeActivityForm = () => { showActivityForm.value = false; editingActivity.value = null }
const editActivity = (activity) => {
  isEditing.value = true
  editingActivity.value = {
    ...activity,
    name: activity.title,
    startTime: activity.startTime?.slice(0, 16),
    endTime: activity.endTime?.slice(0, 16),
    registrationDeadline: activity.registrationDeadline?.slice(0, 16),
    description: activity.content
  }
  showActivityForm.value = true
}
const handleActivitySubmit = async (data) => {
  const payload = {
    clubId: Number(data.clubId),
    title: data.name,
    content: data.description || data.details || '',
    type: data.type,
    maxParticipants: data.maxParticipants,
    registrationDeadline: data.registrationDeadline,
    organizer: data.organizer,
    contact: data.contact,
    startTime: data.startTime,
    endTime: data.endTime,
    location: data.location,
    status: data.status || 'pending_approval'
  }
  if (isEditing.value) await updateActivity(editingActivity.value.activityId, payload)
  else await createActivity(payload)
  ElMessage.success('保存成功')
  closeActivityForm()
  await fetchActivities()
}

const fetchSignups = async () => {
  if (!selectedActivity.value) return
  const response = await getActivitySignups(selectedActivity.value.activityId, signupPage.value, signupPageSize.value)
  signups.value = extractRecords(response.data)
  signupTotal.value = response.data?.total || signups.value.length
}
const openSignups = async (activity) => { selectedActivity.value = activity; signupPage.value = 1; await fetchSignups() }
const closeSignups = () => { selectedActivity.value = null; signups.value = []; signupTotal.value = 0 }
const changeSignupPage = async (page) => { signupPage.value = Math.min(Math.max(1, page), signupTotalPages.value); await fetchSignups() }
const approveSignup = async (signup) => { await updateSignupStatus(selectedActivity.value.activityId, signup.signupId, 'approved'); ElMessage.success('报名已通过'); await fetchSignups(); await fetchActivities() }
const rejectSignup = async (signup) => { await updateSignupStatus(selectedActivity.value.activityId, signup.signupId, 'rejected'); ElMessage.success('报名已驳回'); await fetchSignups(); await fetchActivities() }
const checkin = async (signup) => { await checkinSignup(selectedActivity.value.activityId, signup.signupId); ElMessage.success('签到成功'); await fetchSignups(); await fetchActivities() }

const removeActivity = async (activity) => {
  await ElMessageBox.confirm(`确定删除 "${activity.title}" 吗？`, '提示')
  await deleteActivity(activity.activityId)
  ElMessage.success('删除成功')
  await fetchActivities()
}

onMounted(fetchActivities)
</script>
