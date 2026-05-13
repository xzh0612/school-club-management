<template>
  <div>
    <div class="search-bar">
      <input class="search-input" placeholder="搜索活动名称..." v-model="searchKeyword" />
      <select class="filter-select" v-model="clubFilter">
        <option value="">全部社团</option>
        <option v-for="club in clubOptions" :key="club.id" :value="club.id">{{ club.name }}</option>
      </select>
      <select class="filter-select" v-model="statusFilter">
        <option value="">全部状态</option>
        <option value="报名中">报名中</option>
        <option value="即将开始">即将开始</option>
        <option value="进行中">进行中</option>
        <option value="已结束">已结束</option>
      </select>
      <button class="btn btn-primary" @click="searchActivities">🔍 搜索</button>
    </div>
    
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>正在加载活动数据...</p>
    </div>
    
    <div v-else>
      <div class="activity-grid">
        <div class="activity-card" v-for="a in filteredActivities" :key="a.id">
          <div class="activity-top" :style="{background: getStatusGradient(a.status)}"></div>
          <div class="activity-body">
            <div style="display:flex;justify-content:space-between;align-items:start;margin-bottom:8px">
              <h4>{{getActivityIcon(a.title || a.name)}} {{a.title || a.name}}</h4>
              <span :class="['tag',getStatusClass(a.status)]">{{a.status}}</span>
            </div>
            <div class="activity-meta">{{a.clubName || a.club_name}} · {{formatDateTime(a.startTime || a.start_time)}} · {{a.location}}</div>
            <p class="activity-desc">{{a.content}}</p>
            <div style="display:flex;justify-content:space-between;align-items:center">
              <span style="font-size:12px;color:#9CA3AF">报名 {{a.currentParticipants || 0}}/{{a.maxParticipants || 50}}</span>
              <button 
                v-if="a.currentParticipants < a.maxParticipants && a.status==='报名中'" 
                class="btn btn-primary btn-sm" 
                @click="signupForActivity(a.id)"
                :disabled="isSigningUp"
              >
                {{ isSigningUp ? '报名中...' : '立即报名' }}
              </button>
              <span v-else :class="['tag', a.status==='已报名'?'tag-blue':a.currentParticipants>=a.maxParticipants?'tag-gray':'']">
                {{a.currentParticipants>=a.maxParticipants?'报名截止':a.status}}
              </span>
            </div>
          </div>
        </div>
      </div>
      
      <div v-if="filteredActivities.length === 0" class="empty-state">
        <div class="empty-icon">📅</div>
        <p>暂无符合条件的活动</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getActivityList, searchActivities as searchActivitiesApi, signupActivity } from '../../api/activity'
import { getClubList } from '../../api/club'

// 响应式数据
const searchKeyword = ref('')
const clubFilter = ref('')
const statusFilter = ref('')
const loading = ref(false)
const isSigningUp = ref(false)

const activities = ref([])
const clubOptions = ref([])

const extractRecords = (payload) => {
  if (!payload) return []
  if (Array.isArray(payload.records)) return payload.records
  if (Array.isArray(payload.list)) return payload.list
  if (Array.isArray(payload)) return payload
  return []
}

const normalizeActivityStatus = (activity) => {
  const now = Date.now()
  const start = activity.startTime || activity.start_time
  const end = activity.endTime || activity.end_time
  const startTime = start ? new Date(start).getTime() : null
  const endTime = end ? new Date(end).getTime() : null

  if (endTime && endTime < now) return '已结束'
  if (startTime && startTime <= now && (!endTime || endTime >= now)) return '进行中'

  const statusMap = {
    approved: '报名中',
    pending_approval: '即将开始',
    draft: '即将开始',
    rejected: '已结束',
    cancelled: '已结束',
    completed: '已结束'
  }

  return statusMap[activity.status] || activity.status || '即将开始'
}

const normalizeActivity = (activity) => ({
  ...activity,
  id: activity.id || activity.activityId,
  activityId: activity.activityId || activity.id,
  name: activity.name || activity.title || '',
  title: activity.title || activity.name || '',
  description: activity.description || activity.content || '',
  content: activity.content || activity.description || '',
  clubId: activity.clubId,
  clubName: activity.clubName || activity.club_name || '',
  club_name: activity.club_name || activity.clubName || '',
  currentParticipants: activity.currentParticipants || 0,
  maxParticipants: activity.maxParticipants || 50,
  status: normalizeActivityStatus(activity)
})

// 计算属性：过滤后的活动
const filteredActivities = computed(() => {
  let result = activities.value
  
  // 按社团过滤
  if (clubFilter.value) {
    result = result.filter(activity => String(activity.clubId) === String(clubFilter.value))
  }
  
  // 按状态过滤
  if (statusFilter.value) {
    result = result.filter(activity => activity.status === statusFilter.value)
  }
  
  // 按关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(activity => 
      (activity.name || activity.title || '').toLowerCase().includes(keyword) ||
      (activity.description || activity.content || '').toLowerCase().includes(keyword)
    )
  }
  
  return result
})

// 获取活动图标
const getActivityIcon = (name) => {
  const icons = {
    '篮球': '🏀',
    '足球': '⚽',
    '编程': '💻',
    '摄影': '📷',
    '读书': '📖',
    '辩论': '🎤',
    '音乐': '🎵',
    '舞蹈': '💃',
    '绘画': '🎨',
    '志愿': '❤️'
  }
  
  for (const [key, icon] of Object.entries(icons)) {
    if (name.includes(key)) {
      return icon
    }
  }
  return '🎉'
}

// 获取状态样式类
const getStatusClass = (status) => {
  const statusMap = {
    '报名中': 'tag-green',
    '即将开始': 'tag-orange',
    '进行中': 'tag-blue',
    '已结束': 'tag-gray',
    '已报名': 'tag-blue'
  }
  return statusMap[status] || 'tag-gray'
}

// 获取状态渐变色
const getStatusGradient = (status) => {
  const gradientMap = {
    '报名中': 'linear-gradient(90deg,#10B981,#34D399)',
    '即将开始': 'linear-gradient(90deg,#F59E0B,#FBBF24)',
    '进行中': 'linear-gradient(90deg,#3B82F6,#60A5FA)',
    '已结束': 'linear-gradient(90deg,#6B7280,#9CA3AF)',
    '已报名': 'linear-gradient(90deg,#3B82F6,#60A5FA)'
  }
  return gradientMap[status] || 'linear-gradient(90deg,#6B7280,#9CA3AF)'
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 获取活动列表
const fetchActivities = async () => {
  loading.value = true
  
  try {
    const response = await getActivityList(1, 50, '', '')
    
    if (response && response.code === 200) {
      activities.value = extractRecords(response.data).map(normalizeActivity)
    }
  } catch (error) {
    activities.value = []
    ElMessage.error('活动数据加载失败')
  } finally {
    loading.value = false
  }
}

// 获取社团选项
const fetchClubs = async () => {
  try {
    const response = await getClubList(1, 100, 'approved', '')
    if (response.code === 200) {
      clubOptions.value = extractRecords(response.data).map(club => ({
        id: club.id || club.clubId,
        name: club.name || club.clubName
      }))
    }
  } catch (error) {
    console.error('获取社团列表失败:', error)
    clubOptions.value = []
  }
}

// 搜索活动
const searchActivities = async () => {
  if (!searchKeyword.value.trim()) {
    await fetchActivities()
    return
  }
  
  loading.value = true
  try {
    const response = await searchActivitiesApi(searchKeyword.value, 1, 50)
    if (response.code === 200) {
      activities.value = extractRecords(response.data).map(normalizeActivity)
    } else {
      // 搜索API失败时使用本地过滤
      await fetchActivities()
    }
  } catch (error) {
    console.error('搜索活动失败:', error)
    // 网络错误时使用本地过滤
    await fetchActivities()
  } finally {
    loading.value = false
  }
}

// 活动报名
const signupForActivity = async (activityId) => {
  isSigningUp.value = true
  try {
    const response = await signupActivity(activityId, {})
    if (response.code === 200) {
      ElMessage.success('报名成功！')
      // 更新本地数据
      const activity = activities.value.find(a => a.id === activityId || a.activityId === activityId)
      if (activity) {
        activity.currentParticipants += 1
        activity.status = '已报名'
      }
    }
  } catch (error) {
    console.error('报名失败:', error)
    ElMessage.error('报名失败，请稍后重试')
  } finally {
    isSigningUp.value = false
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchActivities()
  // 获取社团选项数据
  fetchClubs()
})
</script>

<style scoped>
.activity-grid { display:grid; grid-template-columns:repeat(2,1fr); gap:16px; }
.activity-card { background:#fff; border-radius:12px; overflow:hidden; box-shadow:0 1px 3px rgba(0,0,0,0.06); }
.activity-top { height:8px; }
.activity-body { padding:20px; }
.activity-body h4 { font-size:15px; margin-bottom:0; }
.activity-meta { font-size:12px; color:#6B7280; margin-bottom:12px; }
.activity-desc { font-size:12px; color:#6B7280; margin-bottom:12px; line-height:1.6; }

.loading-state {
  text-align: center;
  padding: 60px 20px;
  color: #6B7280;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #E5E7EB;
  border-top: 4px solid #3B82F6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #6B7280;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}
</style>
