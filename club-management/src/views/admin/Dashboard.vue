<template>
  <div>
    <div class="stats-grid">
      <div class="stat-card"><div class="icon-bg">🏛️</div><div class="label">社团总数</div><div class="value">{{ stats.totalClubs }}</div></div>
      <div class="stat-card"><div class="icon-bg">👥</div><div class="label">注册用户</div><div class="value">{{ stats.totalUsers }}</div></div>
      <div class="stat-card"><div class="icon-bg">🎉</div><div class="label">活动总数</div><div class="value">{{ stats.totalActivities }}</div></div>
      <div class="stat-card"><div class="icon-bg">⏳</div><div class="label">待审批</div><div class="value">{{ pendingApprovals.length }}</div></div>
    </div>
    <div class="grid-2">
      <div class="card">
        <div class="card-header"><h3>📈 社团增长趋势</h3></div>
        <div class="chart-placeholder">
          <div class="bar-chart"><div v-for="item in growth" :key="item.year" class="bar" :style="{ height: item.percentage, background: item.current ? 'linear-gradient(to top, #10B981, #34D399)' : 'linear-gradient(to top, #3B82F6, #60A5FA)' }"></div></div>
          <div class="bar-labels"><span v-for="item in growth" :key="item.year">{{ item.year }}</span></div>
        </div>
      </div>
      <div class="card">
        <div class="card-header"><h3>🏆 社团排行</h3></div>
        <div class="todo-item" v-for="club in rankings.slice(0, 5)" :key="club.rank"><div>{{ club.rank }}. {{ club.name }}</div><span class="tag tag-blue">{{ club.events }} 活动</span></div>
        <div v-if="rankings.length === 0" class="empty-state">暂无排行数据</div>
      </div>
    </div>
    <div class="grid-2" style="margin-top:16px">
      <div class="card">
        <div class="card-header"><h3>📝 最近日志</h3></div>
        <div class="activity-item" v-for="item in logs" :key="item.id"><div class="activity-dot"></div><div><div style="font-size:13px">{{ item.description }}</div><div style="font-size:11px;color:#9CA3AF">{{ item.operator }} · {{ formatDate(item.createTime) }}</div></div></div>
        <div v-if="logs.length === 0" class="empty-state">暂无最新日志</div>
      </div>
      <div class="card">
        <div class="card-header"><h3>⏳ 待办审批</h3><span class="tag tag-orange">{{ pendingApprovals.length }}项待处理</span></div>
        <div class="todo-item" v-for="todo in pendingApprovals" :key="todo.approvalId"><div>{{ todo.relatedTitle || todo.comments }}</div><span class="tag tag-orange">{{ todo.type }}</span></div>
        <div v-if="pendingApprovals.length === 0" class="empty-state">暂无待办事项</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import request from '../../utils/request'
import { getApprovalList } from '../../api/approval'
import { getOperationLogs } from '../../api/log'

const stats = reactive({ totalClubs: 0, totalUsers: 0, totalActivities: 0 })
const growth = ref([])
const rankings = ref([])
const logs = ref([])
const pendingApprovals = ref([])
const extractRecords = (payload) => payload?.records || payload?.list || (Array.isArray(payload) ? payload : [])
const formatDate = (value) => value ? new Date(value).toLocaleString('zh-CN') : ''

const fetchDashboard = async () => {
  const [statRes, approvalRes, logRes] = await Promise.all([
    request({ url: '/statistics/all', method: 'get' }),
    getApprovalList({ page: 1, size: 10, status: 'pending' }),
    getOperationLogs({ page: 1, size: 6 })
  ])
  Object.assign(stats, statRes.data.stats || {})
  growth.value = statRes.data.growth || []
  rankings.value = statRes.data.ranking || []
  pendingApprovals.value = extractRecords(approvalRes.data).filter(item => item.status === 'pending')
  logs.value = extractRecords(logRes.data)
}

onMounted(fetchDashboard)
</script>
