<template>
  <div>
    <div class="stats-grid">
      <div class="stat-card"><div class="icon-bg">👥</div><div class="label">活跃用户</div><div class="value">{{ stats.activeUsers }}</div></div>
      <div class="stat-card"><div class="icon-bg">📊</div><div class="label">社团活跃率</div><div class="value">{{ stats.clubActivityRate }}%</div></div>
      <div class="stat-card"><div class="icon-bg">🎯</div><div class="label">活动完成率</div><div class="value">{{ stats.activityCompletionRate }}%</div></div>
      <div class="stat-card"><div class="icon-bg">📈</div><div class="label">学生参与率</div><div class="value">{{ stats.studentParticipationRate }}%</div></div>
    </div>
    <div class="grid-2">
      <div class="card">
        <div class="card-header"><h3>📈 社团增长趋势</h3><button class="btn btn-outline btn-sm" @click="fetchStats">刷新</button></div>
        <div class="bar-chart-area">
          <div class="bar-col" v-for="y in growthData" :key="y.year">
            <div class="bar-fill" :style="{ height: y.percentage || y.pct, background: y.current ? 'linear-gradient(to top,#10B981,#34D399)' : 'linear-gradient(to top,#3B82F6,#60A5FA)' }"></div>
            <div class="bar-year">{{ y.year }}</div>
            <div class="bar-val">{{ y.count }}</div>
          </div>
        </div>
      </div>
      <div class="card">
        <div class="card-header"><h3>🏆 社团排行榜</h3></div>
        <table class="data-table">
          <thead><tr><th>排名</th><th>社团名称</th><th>活动数</th><th>社员数</th></tr></thead>
          <tbody><tr v-for="c in rankingData" :key="c.rank"><td>{{ c.rank }}</td><td>{{ c.name }}</td><td>{{ c.events }}</td><td>{{ c.members }}</td></tr></tbody>
        </table>
        <div v-if="rankingData.length === 0" class="empty-state">暂无排行数据</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import request from '../../utils/request'

const stats = reactive({
  activeUsers: 0,
  clubActivityRate: 0,
  activityCompletionRate: 0,
  studentParticipationRate: 0
})
const growthData = ref([])
const rankingData = ref([])

const fetchStats = async () => {
  const response = await request({ url: '/statistics/all', method: 'get' })
  Object.assign(stats, response.data.stats || {})
  growthData.value = response.data.growth || []
  rankingData.value = response.data.ranking || []
}

onMounted(fetchStats)
</script>

<style scoped>
.bar-chart-area { display:flex; align-items:flex-end; gap:16px; height:180px; padding:0 10px; }
.bar-col { flex:1; text-align:center; }
.bar-fill { border-radius:6px 6px 0 0; margin-bottom:4px; min-height:8px; }
.bar-year { font-size:11px; color:#9CA3AF; }
.bar-val { font-size:12px; }
</style>
