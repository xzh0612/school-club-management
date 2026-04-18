<template>
  <div>
    <div class="stats-grid">
      <div class="stat-card">
        <div class="icon-bg">🏛️</div>
        <div class="label">社团总数</div>
        <div class="value">{{ stats.totalClubs }}</div>
        <div class="change up">↑ {{ stats.clubGrowth }}% 较上学期</div>
      </div>
      <div class="stat-card">
        <div class="icon-bg">👥</div>
        <div class="label">注册社员</div>
        <div class="value">{{ stats.registeredMembers.toLocaleString() }}</div>
        <div class="change up">↑ {{ stats.memberGrowth }}% 较上学期</div>
      </div>
      <div class="stat-card">
        <div class="icon-bg">🎉</div>
        <div class="label">本学期活动</div>
        <div class="value">{{ stats.semesterActivities }}</div>
        <div class="change up">↑ {{ stats.activityGrowth }}% 较上学期</div>
      </div>
      <div class="stat-card">
        <div class="icon-bg">⏳</div>
        <div class="label">待审批</div>
        <div class="value">{{ stats.pendingApprovals }}</div>
        <div class="change down">↑ {{ stats.newPending }} 待处理</div>
      </div>
    </div>
    
    <div class="grid-2">
      <div class="card">
        <div class="card-header">
          <h3>📈 月度活动趋势</h3>
          <span class="tag tag-blue">本学期</span>
          <button class="btn btn-outline btn-sm" @click="refreshChartData" style="margin-left: auto;">🔄 刷新</button>
        </div>
        <div class="chart-placeholder">
          <div class="bar-chart">
            <div 
              v-for="(month, index) in monthlyData" 
              :key="month.name"
              class="bar" 
              :style="{ 
                height: month.height + '%', 
                background: month.isPeak ? 'linear-gradient(to top, #10B981, #34D399)' : 'linear-gradient(to top, #3B82F6, #60A5FA)',
                opacity: month.isCurrent ? 1 : 0.8
              }"
            ></div>
          </div>
          <div class="bar-labels">
            <span 
              v-for="month in monthlyData" 
              :key="month.name"
              :style="{ 
                color: month.isCurrent ? '#3B82F6' : '#6B7280', 
                fontWeight: month.isCurrent ? '600' : '400' 
              }"
            >
              {{ month.name }}
            </span>
          </div>
        </div>
      </div>
      
      <div class="card">
        <div class="card-header">
          <h3>📊 社团类型分布</h3>
          <button class="btn btn-outline btn-sm" @click="refreshPieData">🔄 刷新</button>
        </div>
        <div class="pie-area">
          <div 
            class="pie-circle" 
            :style="{ 
              background: `conic-gradient(${pieColors.academic} 0% ${pieData.academic}%, ${pieColors.cultural} ${pieData.academic}% ${pieData.academic + pieData.cultural}%, ${pieColors.sports} ${pieData.academic + pieData.cultural}% ${pieData.academic + pieData.cultural + pieData.sports}%, ${pieColors.volunteer} ${pieData.academic + pieData.cultural + pieData.sports}% 100%)`
            }"
          ></div>
          <div class="legend">
            <div class="legend-item">
              <div class="legend-dot" :style="{ background: pieColors.academic }"></div>
              学术科技 {{ pieData.academic }}%
            </div>
            <div class="legend-item">
              <div class="legend-dot" :style="{ background: pieColors.cultural }"></div>
              文化艺术 {{ pieData.cultural }}%
            </div>
            <div class="legend-item">
              <div class="legend-dot" :style="{ background: pieColors.sports }"></div>
              体育竞技 {{ pieData.sports }}%
            </div>
            <div class="legend-item">
              <div class="legend-dot" :style="{ background: pieColors.volunteer }"></div>
              公益志愿 {{ pieData.volunteer }}%
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="grid-2" style="margin-top:16px">
      <div class="card">
        <div class="card-header">
          <h3>📝 最近动态</h3>
          <a style="color:#3B82F6;font-size:12px;cursor:pointer" @click="loadMoreActivities">
            查看全部 →
          </a>
        </div>
        <div 
          class="activity-item" 
          v-for="item in recentActivities" 
          :key="item.id"
        >
          <div 
            class="activity-dot" 
            :style="{ background: item.color }"
          ></div>
          <div>
            <div style="font-size:13px">{{ item.text }}</div>
            <div style="font-size:11px;color:#9CA3AF">{{ item.time }}</div>
          </div>
        </div>
        
        <div v-if="recentActivities.length === 0" class="empty-state">
          <div class="empty-icon">📝</div>
          <p>暂无最新动态</p>
        </div>
      </div>
      
      <div class="card">
        <div class="card-header">
          <h3>⏳ 待办事项</h3>
          <span class="tag tag-orange">{{ pendingTasks.length }}项待处理</span>
          <button class="btn btn-outline btn-sm" @click="refreshTodos" style="margin-left: auto;">🔄 刷新</button>
        </div>
        <div 
          class="todo-item" 
          v-for="todo in pendingTasks" 
          :key="todo.id"
        >
          <div>
            <span :style="{ color: todo.color }">●</span> {{ todo.text }}
          </div>
          <span :class="['tag', todo.tagClass]">{{ todo.tag }}</span>
        </div>
        
        <div v-if="pendingTasks.length === 0" class="empty-state">
          <div class="empty-icon">✅</div>
          <p>暂无待办事项</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'

// 响应式数据
const stats = reactive({
  totalClubs: 0,
  clubGrowth: 0,
  registeredMembers: 0,
  memberGrowth: 0,
  semesterActivities: 0,
  activityGrowth: 0,
  pendingApprovals: 0,
  newPending: 0
})

const monthlyData = ref([])
const pieData = reactive({
  academic: 0,
  cultural: 0,
  sports: 0,
  volunteer: 0
})

const pieColors = {
  academic: '#3B82F6',
  cultural: '#10B981',
  sports: '#F59E0B',
  volunteer: '#8B5CF6'
}

const recentActivities = ref([])
const pendingTasks = ref([])

// 生成模拟数据函数
const generateStatsData = () => {
  Object.assign(stats, {
    totalClubs: Math.floor(Math.random() * 20) + 40, // 40-60个社团
    clubGrowth: Math.floor(Math.random() * 15) + 5,   // 5-20%增长
    registeredMembers: Math.floor(Math.random() * 1000) + 3000, // 3000-4000成员
    memberGrowth: Math.floor(Math.random() * 12) + 5, // 5-17%增长
    semesterActivities: Math.floor(Math.random() * 50) + 100,   // 100-150活动
    activityGrowth: Math.floor(Math.random() * 30) + 15,        // 15-45%增长
    pendingApprovals: Math.floor(Math.random() * 10) + 10,      // 10-20待审批
    newPending: Math.floor(Math.random() * 5) + 1               // 1-6新增
  })
}

const generateMonthlyData = () => {
  const months = ['9月', '10月', '11月', '12月', '1月', '2月', '3月', '4月']
  const currentMonthIndex = new Date().getMonth() - 8 // 9月是起始月
  
  monthlyData.value = months.map((month, index) => {
    const baseHeight = 40 + Math.random() * 40 // 40-80%的基础高度
    const seasonalFactor = index === 3 ? 1.3 : (index === 0 || index === 4 ? 0.7 : 1) // 12月峰值，1月和9月低谷
    
    return {
      name: month,
      height: Math.min(95, baseHeight * seasonalFactor),
      isPeak: index === 3,
      isCurrent: index === (currentMonthIndex % 8 + 8) % 8
    }
  })
}

const generatePieData = () => {
  // 保持总和为100%
  const academic = Math.floor(Math.random() * 15) + 30    // 30-45%
  const remaining1 = 100 - academic
  const cultural = Math.floor(Math.random() * 10) + 15    // 15-25%
  const remaining2 = remaining1 - cultural
  const sports = Math.floor(remaining2 / 2) + Math.floor(Math.random() * 10) - 5 // 动态分配
  const volunteer = remaining2 - sports
  
  Object.assign(pieData, {
    academic: Math.max(20, Math.min(50, academic)),
    cultural: Math.max(10, Math.min(30, cultural)),
    sports: Math.max(10, Math.min(30, sports)),
    volunteer: Math.max(15, Math.min(35, volunteer))
  })
}

const generateActivities = () => {
  const activityTemplates = [
    { template: '{club} 提交了活动申请', clubs: ['计算机协会', '摄影社', '篮球社', '文学社', '辩论社'] },
    { template: '{club} 审批通过，已正式成立', clubs: ['音乐社', '舞蹈社', '志愿者协会', '科技创新社'] },
    { template: '{club} 发布了招新计划', clubs: ['足球社', '乒乓球社', '书法社', '动漫社'] },
    { template: '{club} 完成年审材料提交', clubs: ['戏剧社', '合唱团', '棋艺社', '环保协会'] },
    { template: '{club} 活动圆满结束', clubs: ['创业协会', '英语角', '手工制作社', '心理健康社'] }
  ]
  
  const timeRanges = ['刚刚', '5分钟前', '10分钟前', '半小时前', '1小时前', '3小时前', '昨天', '前天']
  
  recentActivities.value = Array.from({ length: 6 }, (_, i) => {
    const templateGroup = activityTemplates[Math.floor(Math.random() * activityTemplates.length)]
    const club = templateGroup.clubs[Math.floor(Math.random() * templateGroup.clubs.length)]
    const text = templateGroup.template.replace('{club}', club)
    const time = timeRanges[Math.floor(Math.random() * timeRanges.length)]
    const colors = ['#3B82F6', '#10B981', '#F59E0B', '#8B5CF6', '#EC4899']
    
    return {
      id: i + 1,
      text,
      time,
      color: colors[Math.floor(Math.random() * colors.length)]
    }
  })
}

const generateTodos = () => {
  const todoTemplates = [
    '社团成立审批 - {club}',
    '活动申请审核 - {event}',
    '经费审批 - {club}{event}',
    '年审材料审核 - {club}',
    '场地申请审批 - {event}'
  ]
  
  const clubs = ['摄影社', '计算机协会', '文学社', '篮球社', '辩论社', '音乐社']
  const events = ['春季招新', '编程马拉松', '读书分享会', '新生杯', '辩论赛']
  
  pendingTasks.value = Array.from({ length: Math.floor(Math.random() * 6) + 3 }, (_, i) => {
    const template = todoTemplates[Math.floor(Math.random() * todoTemplates.length)]
    const club = clubs[Math.floor(Math.random() * clubs.length)]
    const event = events[Math.floor(Math.random() * events.length)]
    const text = template.replace('{club}', club).replace('{event}', event)
    
    const statuses = [
      { tag: '紧急', tagClass: 'tag-red', color: '#EF4444' },
      { tag: '待审', tagClass: 'tag-orange', color: '#F59E0B' },
      { tag: '进行中', tagClass: 'tag-blue', color: '#3B82F6' }
    ]
    
    const status = statuses[Math.floor(Math.random() * statuses.length)]
    
    return {
      id: i + 1,
      text,
      ...status
    }
  })
}

// 刷新函数
const refreshAllData = () => {
  generateStatsData()
  generateMonthlyData()
  generatePieData()
  generateActivities()
  generateTodos()
}

const refreshChartData = () => {
  generateMonthlyData()
  generatePieData()
}

const refreshPieData = () => {
  generatePieData()
}

const refreshTodos = () => {
  generateTodos()
}

const loadMoreActivities = () => {
  generateActivities()
  alert('已加载更多动态')
}

// 组件挂载时初始化数据
onMounted(() => {
  refreshAllData()
  
  // 每30秒自动刷新一次数据
  setInterval(refreshAllData, 30000)
})
</script>

<style scoped>
.chart-placeholder { 
  height: 200px; 
  background: linear-gradient(135deg, #EFF6FF, #DBEAFE); 
  border-radius: 8px; 
  display: flex; 
  flex-direction: column; 
  align-items: center; 
  justify-content: center; 
}

.bar-chart { 
  display: flex; 
  align-items: flex-end; 
  gap: 12px; 
  height: 140px; 
  padding: 0 20px; 
}

.bar { 
  width: 32px; 
  border-radius: 4px 4px 0 0; 
  transition: all 0.3s ease;
}

.bar:hover {
  transform: translateY(-2px);
  opacity: 1 !important;
}

.bar-labels { 
  display: flex; 
  gap: 36px; 
  margin-top: 8px; 
  font-size: 11px; 
  color: #6B7280; 
}

.pie-area { 
  display: flex; 
  align-items: center; 
  gap: 20px; 
  justify-content: center; 
  padding: 20px; 
}

.pie-circle { 
  width: 100px; 
  height: 100px; 
  border-radius: 50%; 
  transition: transform 0.3s ease;
}

.pie-circle:hover {
  transform: scale(1.05);
}

.legend { 
  display: flex; 
  flex-direction: column; 
  gap: 6px; 
}

.legend-item { 
  display: flex; 
  align-items: center; 
  gap: 6px; 
  font-size: 12px; 
}

.legend-dot { 
  width: 10px; 
  height: 10px; 
  border-radius: 50%; 
}

.activity-item { 
  display: flex; 
  align-items: flex-start; 
  gap: 12px; 
  padding: 10px 0; 
  border-bottom: 1px solid #F3F4F6; 
}

.activity-item:last-child { 
  border: none; 
}

.activity-dot { 
  width: 8px; 
  height: 8px; 
  border-radius: 50%; 
  margin-top: 5px; 
  flex-shrink: 0; 
}

.todo-item { 
  display: flex; 
  align-items: center; 
  justify-content: space-between; 
  padding: 8px 0; 
  border-bottom: 1px solid #F3F4F6; 
}

.todo-item:last-child { 
  border: none; 
}

.btn-sm {
  padding: 4px 8px;
  font-size: 12px;
}

.btn-outline {
  background: white;
  color: #6B7280;
  border: 1px solid #D1D5DB;
}

.btn-outline:hover {
  background: #F9FAFB;
  border-color: #9CA3AF;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #6B7280;
}

.empty-icon {
  font-size: 36px;
  margin-bottom: 12px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
}
</style>