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
        <input v-model="searchKeyword" class="search-input" placeholder="搜索活动名称..." />
        <select v-model="statusFilter" class="filter-select"><option value="">全部状态</option><option value="approved">已通过</option><option value="pending_approval">待审批</option><option value="completed">已结束</option><option value="cancelled">已取消</option></select>
      </div>
      <table class="data-table">
        <thead><tr><th>活动名称</th><th>所属社团</th><th>活动时间</th><th>地点</th><th>报名人数</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="a in filteredActivities" :key="a.activityId">
            <td style="font-weight:500">{{ a.title }}</td><td>{{ a.clubName }}</td><td>{{ formatDate(a.startTime) }}</td><td>{{ a.location }}</td><td>{{ a.currentParticipants || 0 }}/{{ a.maxParticipants }}</td><td><span :class="['tag', statusClass(a.status)]">{{ statusText(a.status) }}</span></td>
            <td><button class="btn btn-warning btn-sm" @click="editActivity(a)">编辑</button><button class="btn btn-danger btn-sm" @click="removeActivity(a)" style="margin-left:8px">删除</button></td>
          </tr>
        </tbody>
      </table>
      <div v-if="!loading && filteredActivities.length === 0" class="empty-state">暂无活动数据</div>
    </div>
    <ActivityForm v-if="showActivityForm" :is-edit="isEditing" :initial-data="editingActivity" @close="closeActivityForm" @submit="handleActivitySubmit" />
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import ActivityForm from '../../components/ActivityForm.vue'
import { createActivity, deleteActivity, getActivityList, updateActivity } from '../../api/activity'

const loading = ref(false)
const activities = ref([])
const searchKeyword = ref('')
const statusFilter = ref('')
const showActivityForm = ref(false)
const isEditing = ref(false)
const editingActivity = ref(null)

const extractRecords = (payload) => payload?.records || payload?.list || (Array.isArray(payload) ? payload : [])
const filteredActivities = computed(() => activities.value.filter(a => {
  const matchKeyword = !searchKeyword.value || a.title?.includes(searchKeyword.value) || a.clubName?.includes(searchKeyword.value)
  const matchStatus = !statusFilter.value || a.status === statusFilter.value
  return matchKeyword && matchStatus
}))
const statusText = (status) => ({ approved: '已通过', pending_approval: '待审批', draft: '草稿', rejected: '已驳回', completed: '已结束', cancelled: '已取消' }[status] || status)
const statusClass = (status) => ({ approved: 'tag-green', pending_approval: 'tag-orange', rejected: 'tag-red', completed: 'tag-gray', cancelled: 'tag-gray' }[status] || 'tag-blue')
const formatDate = (value) => value ? new Date(value).toLocaleString('zh-CN') : ''

const fetchActivities = async () => {
  loading.value = true
  try {
    const response = await getActivityList(1, 100, '', '')
    activities.value = extractRecords(response.data)
  } finally {
    loading.value = false
  }
}
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
const removeActivity = async (activity) => {
  await ElMessageBox.confirm(`确定删除 "${activity.title}" 吗？`, '提示')
  await deleteActivity(activity.activityId)
  ElMessage.success('删除成功')
  await fetchActivities()
}

onMounted(fetchActivities)
</script>
