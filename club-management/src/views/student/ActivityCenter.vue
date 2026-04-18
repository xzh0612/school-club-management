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

// 计算属性：过滤后的活动
const filteredActivities = computed(() => {
  let result = activities.value
  
  // 按社团过滤
  if (clubFilter.value) {
    result = result.filter(activity => activity.clubId === clubFilter.value)
  }
  
  // 按状态过滤
  if (statusFilter.value) {
    result = result.filter(activity => activity.status === statusFilter.value)
  }
  
  // 按关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(activity => 
      activity.name.toLowerCase().includes(keyword) ||
      activity.description.toLowerCase().includes(keyword)
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
  console.log('=== 开始获取活动数据 ===')
  console.log('当前loading状态:', loading.value)
  loading.value = true
  console.log('设置loading为true')
  
  try {
    console.log('准备调用API: getActivityList')
    console.log('API参数:', { page: 1, size: 50, status: '', clubId: '' })
    
    const response = await getActivityList(1, 50, '', '')
    console.log('=== API调用完成 ===')
    console.log('原始响应:', response)
    console.log('响应类型:', typeof response)
    console.log('响应code:', response?.code)
    
    if (response && response.code === 200) {
      console.log('API调用成功')
      console.log('响应数据结构:', Object.keys(response))
      console.log('data字段:', response.data)
      console.log('data类型:', typeof response.data)
      
      // 处理后端返回的数据结构
      let activityData = []
      if (response.data && response.data.records) {
        console.log('使用records字段:', response.data.records.length, '条数据')
        activityData = response.data.records
      } else if (response.data && response.data.list) {
        console.log('使用list字段:', response.data.list.length, '条数据')
        activityData = response.data.list
      } else if (response.data) {
        console.log('使用data字段:', Array.isArray(response.data) ? response.data.length : '非数组')
        activityData = Array.isArray(response.data) ? response.data : []
      } else {
        console.log('data字段为空')
        activityData = []
      }
      
      console.log('最终活动数据:', activityData)
      console.log('活动数据长度:', activityData.length)
      activities.value = activityData
      console.log('activities.value设置完成，长度:', activities.value.length)
      
    } else {
      console.log('API返回错误状态或无数据:', response)
      console.log('使用模拟数据')
      // API调用失败时使用模拟数据
      generateMockActivities()
    }
  } catch (error) {
    console.error('=== 获取活动列表失败 ===')
    console.error('错误类型:', typeof error)
    console.error('错误对象:', error)
    console.error('错误消息:', error.message)
    console.error('错误堆栈:', error.stack)
    console.log('使用模拟数据')
    // 网络错误时使用模拟数据
    generateMockActivities()
  } finally {
    console.log('执行finally块')
    console.log('设置loading为false')
    loading.value = false
    console.log('loading状态:', loading.value)
    console.log('=== 获取活动数据结束 ===')
  }
}

// 生成模拟活动数据
const generateMockActivities = () => {
  console.log('=== 开始生成模拟活动数据 ===')
  const mockClubs = [
    { id: 1, name: '计算机协会' },
    { id: 2, name: '篮球社' },
    { id: 3, name: '摄影社' }
  ]
  
  const activityTemplates = [
    {
      name: '新生杯篮球赛',
      title: '新生杯篮球赛',
      content: '面向全校新生的篮球赛事，分组淘汰赛制，奖品丰厚！',
      description: '面向全校新生的篮球赛事，分组淘汰赛制，奖品丰厚！',
      location: '体育馆',
      maxParticipants: 120,
      currentParticipants: 85,
      status: '即将开始',
      clubName: '篮球社',
      club_name: '篮球社'
    },
    {
      name: '编程马拉松大赛',
      title: '编程马拉松大赛',
      content: '24小时编程挑战赛，团队参赛，完成创新项目开发。',
      description: '24小时编程挑战赛，团队参赛，完成创新项目开发。',
      location: '信息楼A301',
      maxParticipants: 100,
      currentParticipants: 67,
      status: '报名中',
      clubName: '计算机协会',
      club_name: '计算机协会'
    },
    {
      name: '春季摄影外拍',
      title: '春季摄影外拍',
      content: '走进自然，用镜头捕捉春日美好。提供相机借用，新手友好。',
      description: '走进自然，用镜头捕捉春日美好。提供相机借用，新手友好。',
      location: '校园+公园',
      maxParticipants: 50,
      currentParticipants: 32,
      status: '报名中',
      clubName: '摄影社',
      club_name: '摄影社'
    }
  ]
  
  console.log('活动模板数量:', activityTemplates.length)
  
  // 为每个模板创建活动对象
  activities.value = activityTemplates.map((template, index) => {
    const activity = {
      id: index + 1,
      activityId: index + 1,
      name: template.name,
      title: template.title,
      content: template.content,
      description: template.description,
      clubId: mockClubs[index % mockClubs.length].id,
      clubName: template.clubName,
      club_name: template.club_name,
      location: template.location,
      maxParticipants: template.maxParticipants,
      currentParticipants: template.currentParticipants,
      status: template.status
    }
    console.log(`活动${index + 1}:`, activity.name, '- 状态:', activity.status)
    return activity
  })
  
  // 设置社团选项
  clubOptions.value = mockClubs
  
  console.log('模拟数据生成完成')
  console.log('最终活动数量:', activities.value.length)
  console.log('活动列表:', activities.value.map(a => ({id: a.id, name: a.name, status: a.status})))
  ElMessage.info('使用模拟数据展示活动信息')
  console.log('=== 模拟数据生成结束 ===')
}

// 获取社团选项
const fetchClubs = async () => {
  console.log('获取社团选项数据...')
  try {
    const response = await getClubList(1, 100, 'active', '')
    console.log('社团列表响应:', response)
    if (response.code === 200) {
      clubOptions.value = response.data.list || response.data || []
      console.log('社团选项设置完成，数量:', clubOptions.value.length)
    }
  } catch (error) {
    console.error('获取社团列表失败:', error)
    // 使用模拟社团数据作为备选
    clubOptions.value = [
      { id: 1, name: '计算机协会' },
      { id: 2, name: '篮球社' },
      { id: 3, name: '摄影社' },
      { id: 4, name: '文学社' },
      { id: 5, name: '音乐社' },
      { id: 6, name: '舞蹈社' }
    ]
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
      activities.value = response.data.list || response.data || []
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
      const activity = activities.value.find(a => a.id === activityId)
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
  console.log('组件挂载完成，开始初始化数据')
  // 立即生成模拟数据确保页面能显示内容
  generateMockActivities()
  // 同时尝试获取真实数据
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