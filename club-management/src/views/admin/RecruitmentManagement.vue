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
        <tbody><tr v-for="p in plans" :key="p.recruitmentId"><td>{{ p.clubName }}</td><td>{{ p.title }}</td><td>{{ p.quota }}</td><td>{{ p.appliedCount || 0 }}</td><td>{{ p.startDate }}</td><td>{{ p.endDate }}</td><td><span class="tag tag-green">{{ p.status }}</span></td><td><button class="btn btn-warning btn-sm" @click="editPlan(p)">编辑</button><button class="btn btn-danger btn-sm" @click="removePlan(p)" style="margin-left:8px">删除</button></td></tr></tbody>
      </table>
      <div v-if="plans.length === 0" class="empty-state">暂无招新计划</div>
    </div>
    <div class="card" style="margin-top:16px">
      <div class="card-header"><h3>入社申请审核</h3></div>
      <table class="data-table">
        <thead><tr><th>申请人</th><th>申请社团</th><th>状态</th><th>申请内容</th><th>操作</th></tr></thead>
        <tbody><tr v-for="a in applications" :key="a.applicationId"><td>{{ a.userName }}</td><td>{{ a.clubName }}</td><td><span class="tag tag-orange">{{ a.status }}</span></td><td>{{ a.introduction }}</td><td><template v-if="a.status === 'pending'"><button class="btn btn-success btn-sm" @click="approve(a)">通过</button><button class="btn btn-danger btn-sm" @click="reject(a)" style="margin-left:8px">驳回</button></template><span v-else>--</span></td></tr></tbody>
      </table>
    </div>
    <RecruitmentForm v-if="showRecruitForm" :is-edit="isEditing" :initial-data="editingPlan" @close="closeRecruitForm" @submit="handleRecruitSubmit" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import RecruitmentForm from '../../components/RecruitmentForm.vue'
import { createRecruitmentPlan, deleteRecruitmentPlan, getRecruitmentPlans, updateRecruitmentPlan } from '../../api/recruitment'
import { approveJoinApplication, getApplicationList, rejectJoinApplication } from '../../api/application'

const plans = ref([])
const applications = ref([])
const showRecruitForm = ref(false)
const isEditing = ref(false)
const editingPlan = ref(null)
const extractRecords = (payload) => payload?.records || payload?.list || (Array.isArray(payload) ? payload : [])

const fetchPlans = async () => {
  const response = await getRecruitmentPlans({ page: 1, size: 100 })
  plans.value = extractRecords(response.data)
}
const fetchApplications = async () => {
  const response = await getApplicationList({ page: 1, size: 100 })
  applications.value = extractRecords(response.data)
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
