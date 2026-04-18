<template>
  <div>
    <div class="stats-grid">
      <div class="stat-card">
        <div class="icon-bg">👥</div>
        <div class="label">活跃用户</div>
        <div class="value">{{ stats.activeUsers.toLocaleString() }}</div>
        <div class="change up">↑ {{ stats.userGrowth }}%</div>
      </div>
      <div class="stat-card">
        <div class="icon-bg">📊</div>
        <div class="label">社团活跃率</div>
        <div class="value">{{ stats.clubActivityRate }}%</div>
        <div class="change up">↑ {{ stats.clubGrowth }}%</div>
      </div>
      <div class="stat-card">
        <div class="icon-bg">🎯</div>
        <div class="label">活动完成率</div>
        <div class="value">{{ stats.eventCompletionRate }}%</div>
        <div class="change up">↑ {{ stats.eventGrowth }}%</div>
      </div>
      <div class="stat-card">
        <div class="icon-bg">📈</div>
        <div class="label">学生参与率</div>
        <div class="value">{{ stats.studentParticipationRate }}%</div>
        <div class="change up">↑ {{ stats.studentGrowth }}%</div>
      </div>
    </div>
    
    <div class="grid-2">
      <div class="card">
        <div class="card-header">
          <h3>📈 社团增长趋势</h3>
          <button class="btn btn-outline btn-sm" @click="refreshData">🔄 刷新</button>
        </div>
        <div class="bar-chart-area">
          <div class="bar-col" v-for="y in growthData" :key="y.year">
            <div 
              class="bar-fill" 
              :style="{ 
                height: y.pct, 
                background: y.current ? 'linear-gradient(to top,#10B981,#34D399)' : 'linear-gradient(to top,#3B82F6,#60A5FA)' 
              }"
            ></div>
            <div 
              class="bar-year" 
              :style="{ 
                color: y.current ? '#3B82F6' : '#9CA3AF', 
                fontWeight: y.current ? '700' : '400' 
              }"
            >
              {{ y.year }}
            </div>
            <div 
              class="bar-val" 
              :style="{ 
                fontWeight: y.current ? '700' : '400', 
                color: y.current ? '#3B82F6' : '' 
              }"
            >
              {{ y.count }}
            </div>
          </div>
        </div>
      </div>
      
      <div class="card">
        <div class="card-header">
          <h3>🏆 社团排行榜</h3>
          <select v-model="rankFilter" class="filter-select" @change="filterRankings">
            <option value="">全部类型</option>
            <option value="academic">学术科技类</option>
            <option value="sports">文体艺术类</option>
            <option value="service">志愿服务类</option>
          </select>
        </div>
        <table class="data-table">
          <thead>
            <tr>
              <th>排名</th>
              <th>社团名称</th>
              <th>活动数</th>
              <th>社员数</th>
              <th>活跃度</th>
              <th>星级</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="c in filteredRankings" :key="c.id">
              <td>{{ c.rank }}</td>
              <td>{{ c.name }}</td>
              <td>{{ c.events }}</td>
              <td>{{ c.members }}</td>
              <td>{{ c.activity }}%</td>
              <td>
                <span v-for="n in 5" :key="n" class="star">
                  {{ n <= c.starRating ? '⭐' : '☆' }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
        
        <div v-if="filteredRankings.length === 0" class="empty-state">
          <div class="empty-icon">🏆</div>
          <p>暂无符合条件的社团数据</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'

// 状态管理
const rankFilter = ref('')
const loading = ref(false)

// 响应式数据
const stats = reactive({
  activeUsers: 0,
  userGrowth: 0,
  clubActivityRate: 0,
  clubGrowth: 0,
  eventCompletionRate: 0,
  eventGrowth: 0,
  studentParticipationRate: 0,
  studentGrowth: 0
})

const growthData = ref([])
const rankingData = ref([])

// 计算属性：过滤后的排行榜
const filteredRankings = computed(() => {
  if (!rankFilter.value) {
    return rankingData.value
  }
  
  // 根据类型过滤（这里简化处理，实际应该根据后端返回的分类字段）
  const typeMap = {
    'academic': ['计算机协会', '辩论社'],
    'sports': ['篮球社', '足球社'],
    'service': ['志愿者协会']
  }
  
  return rankingData.value.filter(club => 
    typeMap[rankFilter.value]?.includes(club.name)
  )
})

// 模拟API调用函数
const fetchStatsData = async () => {
  loading.value = true
  try {
    // 模拟API延迟
    await new Promise(resolve => setTimeout(resolve, 800))
    
    // 模拟统计数据
    Object.assign(stats, {
      activeUsers: Math.floor(Math.random() * 1000) + 2500,
      userGrowth: Math.floor(Math.random() * 20) + 5,
      clubActivityRate: Math.floor(Math.random() * 15) + 80,
      clubGrowth: Math.floor(Math.random() * 10) + 2,
      eventCompletionRate: Math.floor(Math.random() * 10) + 90,
      eventGrowth: Math.floor(Math.random() * 5) + 1,
      studentParticipationRate: Math.floor(Math.random() * 20) + 60,
      studentGrowth: Math.floor(Math.random() * 15) + 8
    })
    
    // 模拟增长趋势数据
    growthData.value = [
      { year:'2022', count: Math.floor(Math.random() * 10) + 25, pct:'45%', current:false },
      { year:'2023', count: Math.floor(Math.random() * 10) + 32, pct:'58%', current:false },
      { year:'2024', count: Math.floor(Math.random() * 10) + 39, pct:'72%', current:false },
      { year:'2025', count: Math.floor(Math.random() * 10) + 45, pct:'88%', current:true }
    ]
    
    // 模拟排行榜数据
    rankingData.value = [
      { id:1, rank:'🥇', name:'计算机协会', events: Math.floor(Math.random() * 15) + 20, members: Math.floor(Math.random() * 100) + 120, activity: Math.floor(Math.random() * 5) + 95, starRating: 5 },
      { id:2, rank:'🥈', name:'志愿者协会', events: Math.floor(Math.random() * 15) + 25, members: Math.floor(Math.random() * 150) + 200, activity: Math.floor(Math.random() * 5) + 92, starRating: 5 },
      { id:3, rank:'🥉', name:'文学社', events: Math.floor(Math.random() * 12) + 15, members: Math.floor(Math.random() * 80) + 100, activity: Math.floor(Math.random() * 5) + 89, starRating: 4 },
      { id:4, rank:'4', name:'篮球社', events: Math.floor(Math.random() * 10) + 12, members: Math.floor(Math.random() * 120) + 150, activity: Math.floor(Math.random() * 5) + 85, starRating: 4 },
      { id:5, rank:'5', name:'摄影社', events: Math.floor(Math.random() * 8) + 10, members: Math.floor(Math.random() * 60) + 70, activity: Math.floor(Math.random() * 5) + 82, starRating: 4 },
      { id:6, rank:'6', name:'辩论社', events: Math.floor(Math.random() * 10) + 8, members: Math.floor(Math.random() * 70) + 80, activity: Math.floor(Math.random() * 5) + 78, starRating: 3 },
      { id:7, rank:'7', name:'音乐社', events: Math.floor(Math.random() * 8) + 6, members: Math.floor(Math.random() * 50) + 60, activity: Math.floor(Math.random() * 5) + 75, starRating: 3 }
    ]
    
  } catch (error) {
    console.error('获取统计数据失败:', error)
    alert('数据加载失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 刷新数据
const refreshData = () => {
  fetchStatsData()
}

// 筛选排行榜
const filterRankings = () => {
  // 筛选逻辑已在computed属性中实现
  console.log('筛选类型:', rankFilter.value)
}

// 组件挂载时获取数据
onMounted(() => {
  fetchStatsData()
})
</script>

<style scoped>
.bar-chart-area { 
  display:flex; 
  align-items:flex-end; 
  gap:16px; 
  height:180px; 
  padding:0 10px; 
}

.bar-col { 
  flex:1; 
  text-align:center; 
}

.bar-fill { 
  border-radius:6px 6px 0 0; 
  margin-bottom:4px; 
}

.bar-year { 
  font-size:11px; 
  color:#9CA3AF; 
}

.bar-val { 
  font-size:12px; 
}

.filter-select {
  padding: 4px 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 12px;
  background: white;
}

.filter-select:focus {
  outline: none;
  border-color: #3B82F6;
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

.star {
  font-size: 14px;
  margin-right: 2px;
}
</style>