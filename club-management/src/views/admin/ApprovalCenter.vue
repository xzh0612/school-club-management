<template>
  <div>
    <div class="stats-grid">
      <div class="stat-card"><div class="icon-bg">📋</div><div class="label">待审批</div><div class="value">{{ approvalStats.pending }}</div></div>
      <div class="stat-card"><div class="icon-bg">✅</div><div class="label">已通过</div><div class="value">{{ approvalStats.approved }}</div></div>
      <div class="stat-card"><div class="icon-bg">❌</div><div class="label">已驳回</div><div class="value">{{ approvalStats.rejected }}</div></div>
      <div class="stat-card"><div class="icon-bg">📊</div><div class="label">总数</div><div class="value">{{ approvalStats.total }}</div></div>
    </div>
    <div class="card">
      <div class="card-header">
        <h3>审批列表</h3>
        <div style="display:flex;gap:8px">
          <button class="btn btn-primary btn-sm" :class="{'btn-outline':typeFilter!==''}" @click="typeFilter=''">全部</button>
          <button class="btn btn-primary btn-sm" :class="{'btn-outline':typeFilter!=='club_creation'}" @click="typeFilter='club_creation'">社团成立</button>
          <button class="btn btn-primary btn-sm" :class="{'btn-outline':typeFilter!=='activity_application'}" @click="typeFilter='activity_application'">活动申请</button>
        </div>
      </div>
      <table class="data-table">
        <thead><tr><th>ID</th><th>类型</th><th>申请人</th><th>关联内容</th><th>状态</th><th>提交时间</th><th>意见</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="a in filteredApprovals" :key="a.approvalId">
            <td>{{ a.approvalId }}</td>
            <td><span class="tag tag-blue">{{ typeText(a.type) }}</span></td>
            <td>{{ a.applicantName }}</td>
            <td>{{ a.relatedTitle || a.relatedId }}</td>
            <td><span :class="['tag', statusClass(a.status)]">{{ statusText(a.status) }}</span></td>
            <td>{{ formatDate(a.createTime) }}</td>
            <td>{{ a.comments || '—' }}</td>
            <td>
              <template v-if="a.status === 'pending'">
                <button class="btn btn-success btn-sm" style="margin-right:8px" @click="handleApprove(a, 'approved')">通过</button>
                <button class="btn btn-danger btn-sm" @click="handleApprove(a, 'rejected')">驳回</button>
              </template>
              <span v-else>--</span>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!loading && filteredApprovals.length === 0" class="empty-state">暂无审批数据</div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getApprovalList, approveApplication, rejectApplication } from '../../api/approval'

const loading = ref(false)
const typeFilter = ref('')
const approvals = ref([])
const approvalStats = ref({ pending: 0, approved: 0, rejected: 0, total: 0 })

const extractRecords = (payload) => payload?.records || payload?.list || (Array.isArray(payload) ? payload : [])
const filteredApprovals = computed(() => typeFilter.value ? approvals.value.filter(a => a.type === typeFilter.value) : approvals.value)

const typeText = (type) => ({ club_creation: '社团成立', activity_application: '活动申请' }[type] || type)
const statusText = (status) => ({ pending: '待审批', approved: '已通过', rejected: '已驳回' }[status] || status)
const statusClass = (status) => ({ pending: 'tag-orange', approved: 'tag-green', rejected: 'tag-red' }[status] || 'tag-gray')
const formatDate = (value) => value ? new Date(value).toLocaleString('zh-CN') : ''

const fetchApprovals = async () => {
  loading.value = true
  try {
    const [response, pending, approved, rejected] = await Promise.all([
      getApprovalList({ page: 1, size: 100 }),
      getApprovalList({ page: 1, size: 1, status: 'pending' }),
      getApprovalList({ page: 1, size: 1, status: 'approved' }),
      getApprovalList({ page: 1, size: 1, status: 'rejected' })
    ])
    approvals.value = extractRecords(response.data)
    approvalStats.value = {
      pending: pending.data?.total || 0,
      approved: approved.data?.total || 0,
      rejected: rejected.data?.total || 0,
      total: response.data?.total || approvals.value.length
    }
  } catch (error) {
    console.error('加载审批失败:', error)
    approvals.value = []
  } finally {
    loading.value = false
  }
}

const handleApprove = async (approval, action) => {
  try {
    if (action === 'approved') {
      await approveApplication(approval.approvalId, '审批通过')
      ElMessage.success('审批通过成功')
    } else {
      await rejectApplication(approval.approvalId, '审批驳回')
      ElMessage.success('审批驳回成功')
    }
    await fetchApprovals()
  } catch (error) {
    console.error('审批失败:', error)
  }
}

onMounted(fetchApprovals)
</script>

<style scoped>
.progress-track { display:flex; align-items:center; gap:24px; padding:16px; background:#F9FAFB; border-radius:8px; flex-wrap:wrap; }
</style>
