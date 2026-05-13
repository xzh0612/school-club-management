<template>
  <div>
    <div class="card">
      <div class="card-header"><h3>已加入的社团（{{ clubs.length }}个）</h3></div>
      <div v-if="loading" class="empty-state">正在加载...</div>
      <div class="club-row" v-for="c in clubs" :key="c.id">
        <div class="club-cover">{{ getClubIcon(c.clubName) }}</div>
        <div class="club-detail">
          <div style="display:flex;justify-content:space-between;align-items:center"><h4>{{ c.clubName }}</h4><span class="tag tag-blue">{{ c.role }}</span></div>
          <p>加入时间：{{ formatDate(c.joinTime) }}</p>
        </div>
      </div>
      <div v-if="!loading && clubs.length === 0" class="empty-state">暂未加入任何社团</div>
    </div>
    <div class="card">
      <div class="card-header"><h3>最近活动</h3></div>
      <table class="data-table">
        <thead><tr><th>活动名称</th><th>社团</th><th>时间</th><th>状态</th></tr></thead>
        <tbody><tr v-for="a in activities" :key="a.id"><td style="font-weight:500">{{ a.activityName }}</td><td>{{ a.clubName }}</td><td>{{ formatDate(a.activityTime) }}</td><td><span class="tag tag-green">{{ a.checkStatus }}</span></td></tr></tbody>
      </table>
      <div v-if="!loading && activities.length === 0" class="empty-state">暂无活动记录</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserClubs, getUserActivities } from '../../api/user'

const loading = ref(false)
const clubs = ref([])
const activities = ref([])

const currentUserId = () => JSON.parse(sessionStorage.getItem('user') || '{}').id
const formatDate = (value) => value ? new Date(value).toLocaleString('zh-CN') : ''
const getClubIcon = (name = '') => {
  if (name.includes('计算机')) return '💻'
  if (name.includes('摄影')) return '📷'
  if (name.includes('篮球')) return '🏀'
  return '🏛️'
}

const fetchData = async () => {
  const userId = currentUserId()
  if (!userId) return
  loading.value = true
  try {
    const [clubRes, activityRes] = await Promise.all([getUserClubs(userId), getUserActivities(userId)])
    clubs.value = clubRes.data || []
    activities.value = activityRes.data || []
  } finally {
    loading.value = false
  }
}

onMounted(fetchData)
</script>

<style scoped>
.club-row { display:flex; border:1px solid #F3F4F6; border-radius:12px; overflow:hidden; margin-bottom:12px; }
.club-row:last-child { margin-bottom:0; }
.club-cover { width:120px; display:flex; align-items:center; justify-content:center; font-size:36px; flex-shrink:0; background:#EFF6FF; }
.club-detail { padding:16px; flex:1; }
.club-detail h4 { font-size:15px; margin-bottom:4px; }
.club-detail p { font-size:12px; color:#6B7280; margin-bottom:8px; }
</style>
