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
          <button class="btn btn-primary btn-sm" :class="{'btn-outline':typeFilter!==''}" @click="changeType('')">全部</button>
          <button class="btn btn-primary btn-sm" :class="{'btn-outline':typeFilter!=='club_creation'}" @click="changeType('club_creation')">社团成立</button>
          <button class="btn btn-primary btn-sm" :class="{'btn-outline':typeFilter!=='activity_application'}" @click="changeType('activity_application')">活动申请</button>
        </div>
      </div>
      <table class="data-table">
        <thead><tr><th>ID</th><th>类型</th><th>申请人</th><th>关联内容</th><th>进度</th><th>状态</th><th>提交时间</th><th>意见</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="a in approvals" :key="a.approvalId">
            <td>{{ a.approvalId }}</td>
            <td><span class="tag tag-blue">{{ typeText(a.type) }}</span></td>
            <td>{{ a.applicantName }}</td>
            <td>{{ a.relatedTitle || a.relatedId }}</td>
            <td>{{ `${a.currentStep || 1}/${a.totalSteps || 1}` }}</td>
            <td><span :class="['tag', statusClass(a.status)]">{{ statusText(a.status) }}</span></td>
            <td>{{ formatDate(a.createTime) }}</td>
            <td>{{ a.comments || '—' }}</td>
            <td>
              <template v-if="a.status === 'pending'">
                <button class="btn btn-success btn-sm" style="margin-right:8px" @click="handleApprove(a, 'approved')">通过</button>
                <button class="btn btn-danger btn-sm" @click="handleApprove(a, 'rejected')">驳回</button>
              </template>
              <button class="btn btn-outline btn-sm" style="margin-right:8px" @click="openHistory(a)">历史</button>
              <button v-if="a.status !== 'pending'" class="btn btn-outline btn-sm" @click="handleArchive(a)">归档</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!loading && approvals.length === 0" class="empty-state">暂无审批数据</div>
      <div class="pagination">
        <button class="btn btn-outline btn-sm" :disabled="currentPage <= 1" @click="changePage(currentPage - 1)">上一页</button>
        <span class="page-info">第 {{ currentPage }} 页，共 {{ totalPages }} 页</span>
        <button class="btn btn-outline btn-sm" :disabled="currentPage >= totalPages" @click="changePage(currentPage + 1)">下一页</button>
      </div>
    </div>
    <div v-if="historyApproval" class="card" style="margin-top:16px">
      <div class="card-header">
        <h3>审批 #{{ historyApproval.approvalId }} 历史</h3>
        <button class="btn btn-outline btn-sm" @click="closeHistory">关闭</button>
      </div>
      <table class="data-table">
        <thead><tr><th>步骤</th><th>动作</th><th>处理人</th><th>意见</th><th>时间</th></tr></thead>
        <tbody>
          <tr v-for="item in histories" :key="item.historyId">
            <td>{{ item.stepNo }}</td>
            <td><span :class="['tag', historyClass(item.action)]">{{ historyText(item.action) }}</span></td>
            <td>{{ item.operatorId || '系统' }}</td>
            <td>{{ item.comments || '—' }}</td>
            <td>{{ formatDate(item.createTime) }}</td>
          </tr>
        </tbody>
      </table>
      <div v-if="histories.length === 0" class="empty-state">暂无审批历史</div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getApprovalHistories, getApprovalList, approveApplication, rejectApplication, archiveApproval } from '../../api/approval'

const loading = ref(false)
const typeFilter = ref('')
const approvals = ref([])
const approvalStats = ref({ pending: 0, approved: 0, rejected: 0, total: 0 })
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const historyApproval = ref(null)
const histories = ref([])

const extractRecords = (payload) => payload?.records || payload?.list || (Array.isArray(payload) ? payload : [])
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))

const typeText = (type) => ({ club_creation: '社团成立', activity_application: '活动申请' }[type] || type)
const statusText = (status) => ({ pending: '待审批', approved: '已通过', rejected: '已驳回' }[status] || status)
const statusClass = (status) => ({ pending: 'tag-orange', approved: 'tag-green', rejected: 'tag-red' }[status] || 'tag-gray')
const historyText = (action) => ({ created: '创建', advanced: '推进', approved: '通过', rejected: '驳回', archived: '归档' }[action] || action)
const historyClass = (action) => ({ created: 'tag-blue', advanced: 'tag-orange', approved: 'tag-green', rejected: 'tag-red', archived: 'tag-gray' }[action] || 'tag-gray')
const formatDate = (value) => value ? new Date(value).toLocaleString('zh-CN') : ''

const fetchApprovals = async () => {
  loading.value = true
  try {
    const [response, pending, approved, rejected] = await Promise.all([
      getApprovalList({ page: currentPage.value, size: pageSize.value, type: typeFilter.value }),
      getApprovalList({ page: 1, size: 1, status: 'pending' }),
      getApprovalList({ page: 1, size: 1, status: 'approved' }),
      getApprovalList({ page: 1, size: 1, status: 'rejected' })
    ])
    approvals.value = extractRecords(response.data)
    total.value = response.data?.total || approvals.value.length
    approvalStats.value = {
      pending: pending.data?.total || 0,
      approved: approved.data?.total || 0,
      rejected: rejected.data?.total || 0,
      total: total.value
    }
  } catch (error) {
    console.error('加载审批失败:', error)
    approvals.value = []
  } finally {
    loading.value = false
  }
}

const changeType = async (type) => {
  typeFilter.value = type
  currentPage.value = 1
  await fetchApprovals()
}

const changePage = async (page) => {
  currentPage.value = Math.min(Math.max(1, page), totalPages.value)
  await fetchApprovals()
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

const openHistory = async (approval) => {
  historyApproval.value = approval
  const response = await getApprovalHistories(approval.approvalId)
  histories.value = extractRecords(response.data)
}

const closeHistory = () => {
  historyApproval.value = null
  histories.value = []
}

const handleArchive = async (approval) => {
  try {
    await ElMessageBox.confirm(`确定归档审批 #${approval.approvalId} 吗？`, '提示')
    await archiveApproval(approval.approvalId)
    ElMessage.success('归档成功')
    await fetchApprovals()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('归档失败:', error)
    }
  }
}

onMounted(fetchApprovals)
</script>

<style scoped>
.progress-track { display:flex; align-items:center; gap:24px; padding:16px; background:#F9FAFB; border-radius:8px; flex-wrap:wrap; }
.pagination { display:flex; justify-content:flex-end; align-items:center; gap:12px; margin-top:16px; }
.page-info { color:#6B7280; font-size:13px; }
</style>
