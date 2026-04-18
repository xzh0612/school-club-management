<template>
  <div>
    <div class="stats-grid" style="grid-template-columns:repeat(3,1fr)">
      <div class="stat-card"><div class="icon-bg">📢</div><div class="label">招新计划</div><div class="value">{{ recruitPlans.length }}</div></div>
      <div class="stat-card"><div class="icon-bg">📋</div><div class="label">待审核申请</div><div class="value">{{ applications.filter(a => a.status === '待审').length }}</div></div>
      <div class="stat-card"><div class="icon-bg">✅</div><div class="label">本学期新社员</div><div class="value">423</div></div>
    </div>
    
    <div class="card">
      <div class="card-header">
        <h3>招新计划管理</h3>
        <button class="btn btn-primary btn-sm" @click="openRecruitForm">
          ＋ 发布招新计划
        </button>
      </div>
      
      <div class="table-controls">
        <div class="search-box">
          <input 
            v-model="searchKeyword" 
            placeholder="搜索社团或标题..." 
            class="search-input"
          />
        </div>
        <div class="filter-tabs">
          <button 
            v-for="tab in statusTabs" 
            :key="tab.value"
            :class="['tab', activeTab === tab.value ? 'active' : '']"
            @click="activeTab = tab.value"
          >
            {{ tab.label }}
          </button>
        </div>
      </div>
      
      <table class="data-table">
        <thead>
          <tr>
            <th>社团名称</th>
            <th>招新标题</th>
            <th>招新人数</th>
            <th>已报名</th>
            <th>开始时间</th>
            <th>截止时间</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="r in filteredPlans" :key="r.id">
            <td>{{ r.club }}</td>
            <td>{{ r.title }}</td>
            <td>{{ r.quota }}人</td>
            <td>{{ r.applied }}</td>
            <td>{{ r.start }}</td>
            <td>{{ r.end }}</td>
            <td>
              <span :class="['tag', r.statusClass]">{{ r.status }}</span>
            </td>
            <td>
              <button class="btn btn-outline btn-sm" @click="viewPlan(r)">查看</button>
              <button class="btn btn-warning btn-sm" @click="editPlan(r)" style="margin-left: 8px;">编辑</button>
              <button 
                v-if="r.status !== '已结束'" 
                class="btn btn-danger btn-sm" 
                @click="deletePlan(r)" 
                style="margin-left: 8px;"
              >
                删除
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      
      <div v-if="filteredPlans.length === 0" class="empty-state">
        <div class="empty-icon">📢</div>
        <p>暂无符合条件的招新计划</p>
      </div>
    </div>
    
    <div class="card" style="margin-top:16px">
      <div class="card-header">
        <h3>入社申请审核</h3>
        <span class="tag tag-red">{{ applications.filter(a => a.status === '待审').length }}条待审</span>
      </div>
      
      <div class="application-controls">
        <div class="bulk-actions">
          <button 
            class="btn btn-success btn-sm" 
            :disabled="selectedApplications.length === 0"
            @click="batchApprove"
          >
            批量通过 ({{ selectedApplications.length }})
          </button>
          <button 
            class="btn btn-danger btn-sm" 
            :disabled="selectedApplications.length === 0"
            @click="batchReject"
            style="margin-left: 8px;"
          >
            批量驳回 ({{ selectedApplications.length }})
          </button>
        </div>
      </div>
      
      <table class="data-table">
        <thead>
          <tr>
            <th style="width: 40px;">
              <input 
                type="checkbox" 
                :checked="selectedApplications.length === applications.filter(a => a.status === '待审').length && selectedApplications.length > 0"
                @change="toggleSelectAll"
              />
            </th>
            <th>学生</th>
            <th>学号</th>
            <th>申请社团</th>
            <th>申请时间</th>
            <th>个人简介</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="s in applications" :key="s.id">
            <td v-if="s.status === '待审'">
              <input 
                type="checkbox" 
                :checked="selectedApplications.includes(s.id)"
                @change="toggleSelect(s.id)"
              />
            </td>
            <td v-else></td>
            <td>
              <div style="display:flex;align-items:center;gap:6px">
                <div class="avatar" :style="{background:s.color}">{{ s.avatar }}</div>
                {{ s.name }}
              </div>
            </td>
            <td>{{ s.sid }}</td>
            <td>{{ s.club }}</td>
            <td>{{ s.time }}</td>
            <td style="max-width:200px;color:#6B7280">{{ s.intro }}</td>
            <td>
              <span :class="['tag', getStatusClass(s.status)]">{{ s.status }}</span>
            </td>
            <td>
              <button 
                v-if="s.status === '待审'" 
                class="btn btn-success btn-sm" 
                @click="approveApplication(s.id, '通过')"
              >
                通过
              </button>
              <button 
                v-if="s.status === '待审'" 
                class="btn btn-danger btn-sm" 
                @click="approveApplication(s.id, '驳回')"
                style="margin-left: 8px;"
              >
                驳回
              </button>
              <span v-else>--</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- 招新计划表单模态框 -->
    <RecruitmentForm 
      v-if="showRecruitForm"
      :is-edit="isEditing"
      :initial-data="editingPlan"
      @close="closeRecruitForm"
      @submit="handleRecruitSubmit"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import RecruitmentForm from '../../components/RecruitmentForm.vue'

// 状态管理
const activeTab = ref('all')
const searchKeyword = ref('')
const showRecruitForm = ref(false)
const isEditing = ref(false)
const editingPlan = ref(null)
const selectedApplications = ref([])

// 状态标签选项
const statusTabs = [
  { label: '全部', value: 'all' },
  { label: '进行中', value: '进行中' },
  { label: '已结束', value: '已结束' }
]

// 招新计划数据
const recruitPlans = ref([
  { 
    id: 1, 
    club: '计算机协会', 
    title: '2026春季招新', 
    quota: 50, 
    applied: 38, 
    start: '2026-03-10', 
    end: '2026-03-30', 
    status: '进行中', 
    statusClass: 'tag-green',
    requirements: '熟悉至少一门编程语言',
    description: '欢迎对编程感兴趣的同学加入我们！'
  },
  { 
    id: 2, 
    club: '摄影社', 
    title: '摄影新手招募', 
    quota: 30, 
    applied: 28, 
    start: '2026-03-08', 
    end: '2026-03-25', 
    status: '进行中', 
    statusClass: 'tag-green',
    requirements: '有相机设备优先',
    description: '无论你是新手还是老手，都欢迎加入我们的摄影大家庭！'
  },
  { 
    id: 3, 
    club: '篮球社', 
    title: '球队纳新', 
    quota: 40, 
    applied: 40, 
    start: '2026-03-01', 
    end: '2026-03-15', 
    status: '已结束', 
    statusClass: 'tag-gray',
    requirements: '有一定的篮球基础',
    description: '校篮球队成员招募，期待你的加入！'
  },
  { 
    id: 4, 
    club: '文学社', 
    title: '春季招新', 
    quota: 25, 
    applied: 18, 
    start: '2026-03-12', 
    end: '2026-04-01', 
    status: '进行中', 
    statusClass: 'tag-green',
    requirements: '热爱文学创作',
    description: '用文字记录生活，用诗歌表达情感，文学社欢迎你！'
  }
])

// 入社申请数据
const applications = ref([
  { 
    id: 1, 
    name: '陈思远', 
    avatar: '陈', 
    color: '#F59E0B', 
    sid: '2022015', 
    club: '计算机协会', 
    time: '2026-03-19 10:00', 
    intro: '热爱编程，参加过 ACM 校赛', 
    status: '待审' 
  },
  { 
    id: 2, 
    name: '赵雨晴', 
    avatar: '赵', 
    color: '#8B5CF6', 
    sid: '2022038', 
    club: '摄影社', 
    time: '2026-03-19 09:30', 
    intro: '喜欢摄影，有单反相机', 
    status: '待审' 
  },
  { 
    id: 3, 
    name: '孙浩然', 
    avatar: '孙', 
    color: '#3B82F6', 
    sid: '2022007', 
    club: '篮球社', 
    time: '2026-03-18 16:00', 
    intro: '院队主力，多次参赛', 
    status: '待审' 
  },
  { 
    id: 4, 
    name: '李小明', 
    avatar: '李', 
    color: '#10B981', 
    sid: '2022022', 
    club: '文学社', 
    time: '2026-03-18 14:20', 
    intro: '平时喜欢写作，发表过多篇散文', 
    status: '已通过' 
  }
])

// 计算属性：过滤后的招新计划
const filteredPlans = computed(() => {
  let filtered = [...recruitPlans.value]
  
  // 状态过滤
  if (activeTab.value !== 'all') {
    filtered = filtered.filter(plan => plan.status === activeTab.value)
  }
  
  // 搜索过滤
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(plan => 
      plan.club.toLowerCase().includes(keyword) || 
      plan.title.toLowerCase().includes(keyword)
    )
  }
  
  return filtered
})

// 招新计划相关函数
const openRecruitForm = () => {
  isEditing.value = false
  editingPlan.value = null
  showRecruitForm.value = true
}

const closeRecruitForm = () => {
  showRecruitForm.value = false
  editingPlan.value = null
}

const handleRecruitSubmit = (data) => {
  if (isEditing.value) {
    // 编辑模式
    const index = recruitPlans.value.findIndex(p => p.id === editingPlan.value.id)
    if (index !== -1) {
      recruitPlans.value[index] = {
        ...recruitPlans.value[index],
        ...data,
        club: data.clubName
      }
    }
    alert('招新计划更新成功！')
  } else {
    // 新增模式
    const newPlan = {
      id: Math.max(...recruitPlans.value.map(p => p.id)) + 1,
      club: data.clubName,
      title: data.title,
      quota: data.quota,
      applied: 0,
      start: data.startTime,
      end: data.endTime,
      status: new Date(data.endTime) < new Date() ? '已结束' : '进行中',
      statusClass: new Date(data.endTime) < new Date() ? 'tag-gray' : 'tag-green',
      requirements: data.requirements,
      description: data.description
    }
    recruitPlans.value.push(newPlan)
    alert('招新计划发布成功！')
  }
  
  closeRecruitForm()
}

const viewPlan = (plan) => {
  const details = `
招新计划详情：

社团：${plan.club}
标题：${plan.title}
人数：${plan.quota}人
已报名：${plan.applied}人
时间：${plan.start} 至 ${plan.end}
状态：${plan.status}

招新要求：${plan.requirements || '无'}

详细介绍：${plan.description || '无'}
  `.trim()
  
  alert(details)
}

const editPlan = (plan) => {
  isEditing.value = true
  editingPlan.value = {
    ...plan,
    clubId: getClubIdByName(plan.club),
    startTime: plan.start,
    endTime: plan.end,
    requirements: plan.requirements || '',
    description: plan.description || ''
  }
  showRecruitForm.value = true
}

const deletePlan = (plan) => {
  if (confirm(`确定要删除 "${plan.title}" 这个招新计划吗？`)) {
    recruitPlans.value = recruitPlans.value.filter(p => p.id !== plan.id)
    alert('删除成功！')
  }
}

const getClubIdByName = (clubName) => {
  const clubs = [
    { id: 1, name: '计算机协会' },
    { id: 2, name: '摄影社' },
    { id: 3, name: '篮球社' },
    { id: 4, name: '文学社' }
  ]
  return clubs.find(c => c.name === clubName)?.id || 1
}

// 申请审核相关函数
const toggleSelect = (id) => {
  const index = selectedApplications.value.indexOf(id)
  if (index === -1) {
    selectedApplications.value.push(id)
  } else {
    selectedApplications.value.splice(index, 1)
  }
}

const toggleSelectAll = () => {
  const pendingApps = applications.value
    .filter(a => a.status === '待审')
    .map(a => a.id)
  
  if (selectedApplications.value.length === pendingApps.length && pendingApps.length > 0) {
    selectedApplications.value = []
  } else {
    selectedApplications.value = [...pendingApps]
  }
}

const approveApplication = (id, action) => {
  const app = applications.value.find(a => a.id === id)
  if (app) {
    app.status = action === '通过' ? '已通过' : '已驳回'
    selectedApplications.value = selectedApplications.value.filter(sid => sid !== id)
    alert(`${app.name} 的申请已${action}`)
  }
}

const batchApprove = () => {
  if (selectedApplications.value.length === 0) return
  
  selectedApplications.value.forEach(id => {
    const app = applications.value.find(a => a.id === id)
    if (app) app.status = '已通过'
  })
  
  alert(`已批量通过 ${selectedApplications.value.length} 个申请`)
  selectedApplications.value = []
}

const batchReject = () => {
  if (selectedApplications.value.length === 0) return
  
  selectedApplications.value.forEach(id => {
    const app = applications.value.find(a => a.id === id)
    if (app) app.status = '已驳回'
  })
  
  alert(`已批量驳回 ${selectedApplications.value.length} 个申请`)
  selectedApplications.value = []
}

const getStatusClass = (status) => {
  const classMap = {
    '待审': 'tag-orange',
    '已通过': 'tag-green',
    '已驳回': 'tag-red'
  }
  return classMap[status] || 'tag-gray'
}
</script>

<style scoped>
.table-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #eee;
  flex-wrap: wrap;
  gap: 12px;
}

.search-box {
  flex: 1;
  max-width: 300px;
}

.search-input {
  width: 100%;
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

.filter-tabs {
  display: flex;
  gap: 8px;
}

.tab {
  padding: 6px 16px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 20px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.tab:hover {
  border-color: #3B82F6;
  color: #3B82F6;
}

.tab.active {
  background: #3B82F6;
  color: white;
  border-color: #3B82F6;
}

.application-controls {
  padding: 16px 20px;
  border-bottom: 1px solid #eee;
}

.bulk-actions {
  display: flex;
  gap: 12px;
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
  .table-controls {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-box {
    max-width: none;
  }
  
  .filter-tabs {
    justify-content: center;
  }
  
  .bulk-actions {
    flex-wrap: wrap;
  }
}
</style>