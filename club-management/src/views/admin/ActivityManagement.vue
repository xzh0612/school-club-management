<template>
  <div>
    <div class="stats-grid">
      <div class="stat-card"><div class="icon-bg">🎉</div><div class="label">活动总数</div><div class="value">{{ activities.length }}</div></div>
      <div class="stat-card"><div class="icon-bg">🟢</div><div class="label">进行中</div><div class="value">{{ activities.filter(a => a.status === '进行中').length }}</div></div>
      <div class="stat-card"><div class="icon-bg">📅</div><div class="label">即将开始</div><div class="value">{{ activities.filter(a => a.status === '即将开始').length }}</div></div>
      <div class="stat-card"><div class="icon-bg">✅</div><div class="label">已结束</div><div class="value">{{ activities.filter(a => a.status === '已结束').length }}</div></div>
    </div>
    
    <div class="card">
      <div class="card-header">
        <h3>活动列表</h3>
        <div style="display:flex;gap:8px">
          <button class="btn btn-primary btn-sm" @click="openActivityForm">
            ＋ 创建活动
          </button>
          <button class="btn btn-outline btn-sm" @click="handleExport">📥 导出</button>
        </div>
      </div>
      
      <div class="search-bar">
        <input 
          v-model="searchKeyword" 
          class="search-input" 
          placeholder="搜索活动名称..." 
        />
        <select v-model="statusFilter" class="filter-select">
          <option value="">全部状态</option>
          <option value="报名中">报名中</option>
          <option value="即将开始">即将开始</option>
          <option value="进行中">进行中</option>
          <option value="已结束">已结束</option>
        </select>
        <select v-model="clubFilter" class="filter-select">
          <option value="">全部社团</option>
          <option v-for="club in uniqueClubs" :key="club" :value="club">
            {{ club }}
          </option>
        </select>
        <button class="btn btn-primary btn-sm" @click="handleSearch">🔍</button>
      </div>
      
      <table class="data-table">
        <thead>
          <tr>
            <th>活动名称</th>
            <th>所属社团</th>
            <th>活动时间</th>
            <th>地点</th>
            <th>报名人数</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="a in paginatedActivities" :key="a.id">
            <td style="font-weight:500">{{ a.name }}</td>
            <td>{{ a.club }}</td>
            <td>{{ a.time }}</td>
            <td>{{ a.location }}</td>
            <td>{{ a.enrolled }}/{{ a.max }}</td>
            <td>
              <span :class="['tag', a.statusClass]">{{ a.status }}</span>
            </td>
            <td>
              <button class="btn btn-outline btn-sm" @click="viewActivity(a)">详情</button>
              <button 
                class="btn btn-warning btn-sm" 
                @click="editActivity(a)" 
                style="margin-left: 8px;"
              >
                编辑
              </button>
              <button 
                v-if="a.status !== '已结束'" 
                class="btn btn-danger btn-sm" 
                @click="deleteActivity(a)" 
                style="margin-left: 8px;"
              >
                删除
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      
      <div v-if="filteredActivities.length === 0" class="empty-state">
        <div class="empty-icon">🎉</div>
        <p>暂无符合条件的活动</p>
      </div>
      
      <div class="pagination">
        <div class="page-btn" @click="changePage('prev')">‹</div>
        <div 
          v-for="page in totalPages" 
          :key="page" 
          :class="['page-btn', currentPage === page ? 'active' : '']" 
          @click="changePage(page)"
        >
          {{ page }}
        </div>
        <div class="page-btn" @click="changePage('next')">›</div>
      </div>
    </div>
    
    <!-- 活动表单模态框 -->
    <ActivityForm 
      v-if="showActivityForm"
      :is-edit="isEditing"
      :initial-data="editingActivity"
      @close="closeActivityForm"
      @submit="handleActivitySubmit"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import ActivityForm from '../../components/ActivityForm.vue'

// 状态管理
const searchKeyword = ref('')
const statusFilter = ref('')
const clubFilter = ref('')
const showActivityForm = ref(false)
const isEditing = ref(false)
const editingActivity = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)

// 活动数据
const activities = ref([
  { 
    id: 1, 
    name: '编程马拉松大赛', 
    club: '计算机协会', 
    time: '2026-03-25 09:00', 
    location: '信息楼A301', 
    enrolled: 86, 
    max: 100, 
    status: '报名中', 
    statusClass: 'tag-green',
    type: '学术科技',
    organizer: '张伟',
    contact: '13800138000',
    description: '为期两天的编程竞赛活动',
    details: '第一天：算法培训；第二天：实战比赛'
  },
  { 
    id: 2, 
    name: '春季摄影外拍', 
    club: '摄影社', 
    time: '2026-03-22 08:00', 
    location: '校园+公园', 
    enrolled: 45, 
    max: 50, 
    status: '报名中', 
    statusClass: 'tag-green',
    type: '文艺体育',
    organizer: '李娜',
    contact: '13900139000',
    description: '春日美景摄影创作活动',
    details: '上午：校园拍摄；下午：公园外景'
  },
  { 
    id: 3, 
    name: '新生杯篮球赛', 
    club: '篮球社', 
    time: '2026-03-20 14:00', 
    location: '体育馆', 
    enrolled: 120, 
    max: 120, 
    status: '即将开始', 
    statusClass: 'tag-orange',
    type: '文艺体育',
    organizer: '王强',
    contact: '13700137000',
    description: '年度篮球赛事',
    details: '小组赛+淘汰赛制'
  },
  { 
    id: 4, 
    name: '读书分享会', 
    club: '文学社', 
    time: '2026-03-18 19:00', 
    location: '图书馆报告厅', 
    enrolled: 67, 
    max: 67, 
    status: '进行中', 
    statusClass: 'tag-blue',
    type: '学术科技',
    organizer: '陈静',
    contact: '13600136000',
    description: '经典文学作品分享交流',
    details: '每人分享15分钟，自由讨论环节'
  },
  { 
    id: 5, 
    name: '辩论选拔赛', 
    club: '辩论社', 
    time: '2026-03-15 14:00', 
    location: '教学楼B201', 
    enrolled: 40, 
    max: 40, 
    status: '已结束', 
    statusClass: 'tag-gray',
    type: '学术科技',
    organizer: '刘洋',
    contact: '13500135000',
    description: '校队成员选拔活动',
    details: '个人陈述+现场辩论+评委打分'
  }
])

// 计算属性：唯一的社团列表
const uniqueClubs = computed(() => {
  return [...new Set(activities.value.map(a => a.club))]
})

// 计算属性：过滤后的活动
const filteredActivities = computed(() => {
  let filtered = [...activities.value]
  
  // 搜索过滤
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(activity => 
      activity.name.toLowerCase().includes(keyword) || 
      activity.club.toLowerCase().includes(keyword)
    )
  }
  
  // 状态过滤
  if (statusFilter.value) {
    filtered = filtered.filter(activity => activity.status === statusFilter.value)
  }
  
  // 社团过滤
  if (clubFilter.value) {
    filtered = filtered.filter(activity => activity.club === clubFilter.value)
  }
  
  return filtered
})

// 计算属性：分页后的活动
const paginatedActivities = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredActivities.value.slice(start, end)
})

// 计算属性：总页数
const totalPages = computed(() => {
  return Math.ceil(filteredActivities.value.length / pageSize.value)
})

// 活动相关函数
const openActivityForm = () => {
  isEditing.value = false
  editingActivity.value = null
  showActivityForm.value = true
}

const closeActivityForm = () => {
  showActivityForm.value = false
  editingActivity.value = null
}

const handleActivitySubmit = (data) => {
  if (isEditing.value) {
    // 编辑模式
    const index = activities.value.findIndex(a => a.id === editingActivity.value.id)
    if (index !== -1) {
      activities.value[index] = {
        ...activities.value[index],
        ...data,
        club: data.clubName,
        time: `${data.startTime.split('T')[0]} ${data.startTime.split('T')[1]}`
      }
    }
    alert('活动信息更新成功！')
  } else {
    // 新增模式
    const newActivity = {
      id: Math.max(...activities.value.map(a => a.id)) + 1,
      name: data.name,
      club: data.clubName,
      time: `${data.startTime.split('T')[0]} ${data.startTime.split('T')[1]}`,
      location: data.location,
      enrolled: 0,
      max: data.maxParticipants,
      status: data.status,
      statusClass: getStatusClass(data.status),
      type: data.type,
      organizer: data.organizer,
      contact: data.contact,
      description: data.description,
      details: data.details
    }
    activities.value.push(newActivity)
    alert('活动创建成功！')
  }
  
  closeActivityForm()
}

const viewActivity = (activity) => {
  const details = `
活动详情：

活动名称：${activity.name}
所属社团：${activity.club}
活动类型：${activity.type}
活动时间：${activity.time}
活动地点：${activity.location}
报名情况：${activity.enrolled}/${activity.max}人
活动状态：${activity.status}
活动负责人：${activity.organizer}
联系方式：${activity.contact}

活动简介：${activity.description || '无'}

详细安排：${activity.details || '无'}
  `.trim()
  
  alert(details)
}

const editActivity = (activity) => {
  isEditing.value = true
  editingActivity.value = {
    ...activity,
    clubId: getClubIdByName(activity.club),
    startTime: activity.time.replace(' ', 'T'),
    endTime: new Date(new Date(activity.time).getTime() + 2 * 60 * 60 * 1000).toISOString().slice(0, 16),
    maxParticipants: activity.max,
    description: activity.description || '',
    details: activity.details || ''
  }
  showActivityForm.value = true
}

const deleteActivity = (activity) => {
  if (confirm(`确定要删除 "${activity.name}" 这个活动吗？`)) {
    activities.value = activities.value.filter(a => a.id !== activity.id)
    alert('删除成功！')
  }
}

const getClubIdByName = (clubName) => {
  const clubs = [
    { id: 1, name: '计算机协会' },
    { id: 2, name: '摄影社' },
    { id: 3, name: '篮球社' },
    { id: 4, name: '文学社' },
    { id: 5, name: '辩论社' }
  ]
  return clubs.find(c => c.name === clubName)?.id || 1
}

const getStatusClass = (status) => {
  const classMap = {
    '报名中': 'tag-green',
    '即将开始': 'tag-orange',
    '进行中': 'tag-blue',
    '已结束': 'tag-gray'
  }
  return classMap[status] || 'tag-gray'
}

// 导出功能
const handleExport = () => {
  if (filteredActivities.value.length === 0) {
    alert('没有可导出的数据')
    return
  }
  
  // 生成CSV格式数据
  const headers = ['活动名称', '所属社团', '活动时间', '地点', '报名人数', '状态', '活动类型', '负责人', '联系方式']
  const csvContent = [
    headers.join(','),
    ...filteredActivities.value.map(activity => [
      activity.name,
      activity.club,
      activity.time,
      activity.location,
      `${activity.enrolled}/${activity.max}`,
      activity.status,
      activity.type,
      activity.organizer,
      activity.contact
    ].join(','))
  ].join('\n')
  
  // 创建下载链接
  const blob = new Blob(['\uFEFF' + csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)
  link.setAttribute('href', url)
  link.setAttribute('download', `活动列表_${new Date().toISOString().slice(0, 10)}.csv`)
  link.style.visibility = 'hidden'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  
  alert('导出成功！')
}

// 搜索功能
const handleSearch = () => {
  // 搜索已经在computed属性中实时处理，这里可以添加额外的逻辑
  console.log('执行搜索:', searchKeyword.value)
}

// 分页功能
const changePage = (page) => {
  if (page === 'prev') {
    if (currentPage.value > 1) {
      currentPage.value--
    }
  } else if (page === 'next') {
    if (currentPage.value < totalPages.value) {
      currentPage.value++
    }
  } else if (typeof page === 'number') {
    currentPage.value = page
  }
  
  // 滚动到页面顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
</script>

<style scoped>
.search-bar {
  display: flex;
  gap: 12px;
  padding: 16px 20px;
  border-bottom: 1px solid #eee;
  flex-wrap: wrap;
}

.search-input {
  flex: 1;
  min-width: 200px;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.search-input:focus {
  outline: none;
  border-color: #3B82F6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}

.filter-select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  background: white;
}

.filter-select:focus {
  outline: none;
  border-color: #3B82F6;
}

.pagination {
  display: flex;
  justify-content: center;
  gap: 8px;
  padding: 20px;
}

.page-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.page-btn:hover {
  border-color: #3B82F6;
  color: #3B82F6;
}

.page-btn.active {
  background: #3B82F6;
  color: white;
  border-color: #3B82F6;
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

.btn-warning {
  background: #F59E0B;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-warning:hover {
  background: #D97706;
}

.btn-sm {
  padding: 6px 12px;
  font-size: 12px;
}

@media (max-width: 768px) {
  .search-bar {
    flex-direction: column;
  }
  
  .search-input {
    min-width: auto;
  }
  
  .pagination {
    flex-wrap: wrap;
  }
}
</style>