<template>
  <div>
    <div class="stats-grid" style="grid-template-columns:repeat(3,1fr)">
      <div class="stat-card"><div class="icon-bg">📢</div><div class="label">招新计划</div><div class="value">{{ plans.length }}</div></div>
      <div class="stat-card"><div class="icon-bg">📋</div><div class="label">待审核申请</div><div class="value">{{ applications.filter(a => a.status === 'pending').length }}</div></div>
      <div class="stat-card"><div class="icon-bg">✅</div><div class="label">已通过申请</div><div class="value">{{ applications.filter(a => a.status === 'approved').length }}</div></div>
    </div>
    <div class="card">
      <div class="card-header"><h3>招新计划管理</h3><button class="btn btn-primary btn-sm" @click="openRecruitForm">＋ 发布招新计划</button></div>
      <table class="data-table">
        <thead><tr><th>社团名称</th><th>招新标题</th><th>招新人数</th><th>已申请</th><th>开始时间</th><th>截止时间</th><th>状态</th><th>操作</th></tr></thead>
        <tbody><tr v-for="p in plans" :key="p.recruitmentId"><td>{{ p.clubName }}</td><td>{{ p.title }}</td><td>{{ p.quota }}</td><td>{{ p.appliedCount || 0 }}</td><td>{{ p.startDate }}</td><td>{{ p.endDate }}</td><td><span :class="['tag', statusClass(p.status)]">{{ statusText(p.status) }}</span></td><td><button class="btn btn-warning btn-sm" @click="editPlan(p)">编辑</button><button class="btn btn-danger btn-sm" @click="removePlan(p)" style="margin-left:8px">归档</button></td></tr></tbody>
      </table>
      <div v-if="plans.length === 0" class="empty-state">暂无招新计划</div>
      <div class="pagination">
        <button class="btn btn-outline btn-sm" :disabled="planPage <= 1" @click="changePlanPage(planPage - 1)">上一页</button>
        <span class="page-info">第 {{ planPage }} 页，共 {{ planTotalPages }} 页</span>
        <button class="btn btn-outline btn-sm" :disabled="planPage >= planTotalPages" @click="changePlanPage(planPage + 1)">下一页</button>
      </div>
    </div>
    <div class="card" style="margin-top:16px">
      <div class="card-header"><h3>入社申请审核</h3></div>
      <table class="data-table">
        <thead><tr><th>申请人</th><th>申请社团</th><th>状态</th><th>申请内容</th><th>操作</th></tr></thead>
        <tbody><tr v-for="a in applications" :key="a.applicationId"><td>{{ a.userName }}</td><td>{{ a.clubName }}</td><td><span class="tag tag-orange">{{ a.status }}</span></td><td>{{ a.introduction }}</td><td><template v-if="a.status === 'pending'"><button class="btn btn-success btn-sm" @click="approve(a)">通过</button><button class="btn btn-danger btn-sm" @click="reject(a)" style="margin-left:8px">驳回</button></template><span v-else>--</span></td></tr></tbody>
      </table>
      <div class="pagination">
        <button class="btn btn-outline btn-sm" :disabled="appPage <= 1" @click="changeAppPage(appPage - 1)">上一页</button>
        <span class="page-info">第 {{ appPage }} 页，共 {{ appTotalPages }} 页</span>
        <button class="btn btn-outline btn-sm" :disabled="appPage >= appTotalPages" @click="changeAppPage(appPage + 1)">下一页</button>
      </div>
    </div>
    <RecruitmentForm v-if="showRecruitForm" :is-edit="isEditing" :initial-data="editingPlan" @close="closeRecruitForm" @submit="handleRecruitSubmit" />
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import RecruitmentForm from '../../components/RecruitmentForm.vue'
import { createRecruitmentPlan, deleteRecruitmentPlan, getRecruitmentPlans, updateRecruitmentPlan } from '../../api/recruitment'
import { approveJoinApplication, getApplicationList, rejectJoinApplication } from '../../api/application'

const plans = ref([])
const applications = ref([])
const planPage = ref(1)
const planSize = ref(10)
const planTotal = ref(0)
const appPage = ref(1)
const appSize = ref(10)
const appTotal = ref(0)
const showRecruitForm = ref(false)
const isEditing = ref(false)
const editingPlan = ref(null)
const extractRecords = (payload) => payload?.records || payload?.list || (Array.isArray(payload) ? payload : [])
const statusText = (status) => ({ active: '进行中', closed: '已结束', inactive: '已停用', archived: '已归档' }[status] || status)
const statusClass = (status) => ({ active: 'tag-green', closed: 'tag-gray', inactive: 'tag-gray', archived: 'tag-gray' }[status] || 'tag-blue')
const planTotalPages = computed(() => Math.max(1, Math.ceil(planTotal.value / planSize.value)))
const appTotalPages = computed(() => Math.max(1, Math.ceil(appTotal.value / appSize.value)))

const fetchPlans = async () => {
  const response = await getRecruitmentPlans({ page: planPage.value, size: planSize.value })
  plans.value = extractRecords(response.data)
  planTotal.value = response.data?.total || plans.value.length
}
const fetchApplications = async () => {
  const response = await getApplicationList({ page: appPage.value, size: appSize.value })
  applications.value = extractRecords(response.data)
  appTotal.value = response.data?.total || applications.value.length
}
const changePlanPage = async (page) => {
  planPage.value = Math.min(Math.max(1, page), planTotalPages.value)
  await fetchPlans()
}
const changeAppPage = async (page) => {
  appPage.value = Math.min(Math.max(1, page), appTotalPages.value)
  await fetchApplications()
}
const openRecruitForm = () => { isEditing.value = false; editingPlan.value = null; showRecruitForm.value = true }
const closeRecruitForm = () => { showRecruitForm.value = false; editingPlan.value = null }
const editPlan = (plan) => {
  isEditing.value = true
  editingPlan.value = { ...plan, clubId: plan.clubId, startTime: plan.startDate, endTime: plan.endDate }
  showRecruitForm.value = true
}
const handleRecruitSubmit = async (data) => {
  const payload = {
    clubId: Number(data.clubId),
    title: data.title,
    quota: data.quota,
    requirements: data.requirements,
    description: data.description,
    startDate: data.startTime,
    endDate: data.endTime,
    status: new Date(data.endTime) < new Date() ? 'closed' : 'active'
  }
  if (isEditing.value) await updateRecruitmentPlan(editingPlan.value.recruitmentId, payload)
  else await createRecruitmentPlan(payload)
  ElMessage.success('保存成功')
  closeRecruitForm()
  await fetchPlans()
}
const removePlan = async (plan) => {
  await ElMessageBox.confirm(`确定删除 "${plan.title}" 吗？`, '提示')
  await deleteRecruitmentPlan(plan.recruitmentId)
  await fetchPlans()
}
const approve = async (app) => { await approveJoinApplication(app.applicationId, '入社申请通过'); await Promise.all([fetchApplications(), fetchPlans()]) }
const reject = async (app) => { await rejectJoinApplication(app.applicationId, '入社申请驳回'); await fetchApplications() }

onMounted(() => Promise.all([fetchPlans(), fetchApplications()]))
</script>
