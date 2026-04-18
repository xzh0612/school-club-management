<template>
  <div>
    <div class="stats-grid">
      <div class="stat-card"><div class="icon-bg">📢</div><div class="label">公告总数</div><div class="value">{{ announcements.length }}</div></div>
      <div class="stat-card"><div class="icon-bg">📌</div><div class="label">置顶公告</div><div class="value">{{ announcements.filter(a => a.pinned).length }}</div></div>
      <div class="stat-card"><div class="icon-bg">👁️</div><div class="label">今日浏览</div><div class="value">1,245</div></div>
      <div class="stat-card"><div class="icon-bg">💬</div><div class="label">本月发布</div><div class="value">12</div></div>
    </div>
    
    <div class="card">
      <div class="card-header">
        <h3>公告列表</h3>
        <button class="btn btn-primary btn-sm" @click="openAnnouncementForm">
          ＋ 发布公告
        </button>
      </div>
      
      <div class="search-bar">
        <input 
          v-model="searchKeyword" 
          class="search-input" 
          placeholder="搜索公告标题/内容..." 
          @input="handleSearch"
        />
        <select v-model="typeFilter" class="filter-select">
          <option value="">全部分类</option>
          <option value="通知公告">通知公告</option>
          <option value="活动预告">活动预告</option>
          <option value="系统公告">系统公告</option>
        </select>
        <button class="btn btn-primary btn-sm" @click="handleSearch">🔍</button>
      </div>
      
      <table class="data-table">
        <thead>
          <tr>
            <th>标题</th>
            <th>分类</th>
            <th>发布人</th>
            <th>发布时间</th>
            <th>浏览量</th>
            <th>置顶</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="a in filteredAnnouncements" :key="a.id">
            <td :style="{fontWeight: a.pinned?'500':'400'}">
              {{a.pinned?'📌 ':''}}{{a.title}}
            </td>
            <td>
              <span :class="['tag',a.typeClass]">{{a.type}}</span>
            </td>
            <td>{{a.author}}</td>
            <td>{{a.date}}</td>
            <td>{{a.views}}</td>
            <td>
              <button 
                class="pin-toggle" 
                :class="{ 'pinned': a.pinned }" 
                @click="togglePin(a.id)"
              >
                {{a.pinned?'✅':'—'}}
              </button>
            </td>
            <td>
              <button class="btn btn-outline btn-sm" @click="viewAnnouncement(a)">查看</button>
              <button 
                class="btn btn-warning btn-sm" 
                @click="editAnnouncement(a)" 
                style="margin-left: 8px;"
              >
                编辑
              </button>
              <button 
                class="btn btn-danger btn-sm" 
                @click="deleteAnnouncement(a)" 
                style="margin-left: 8px;"
              >
                删除
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      
      <div v-if="filteredAnnouncements.length === 0" class="empty-state">
        <div class="empty-icon">📢</div>
        <p>暂无符合条件的公告</p>
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
    
    <!-- 公告表单模态框 -->
    <AnnouncementForm 
      v-if="showAnnouncementForm"
      :is-edit="isEditing"
      :initial-data="editingAnnouncement"
      @close="closeAnnouncementForm"
      @submit="handleAnnouncementSubmit"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import AnnouncementForm from '../../components/AnnouncementForm.vue'

// 状态管理
const searchKeyword = ref('')
const typeFilter = ref('')
const showAnnouncementForm = ref(false)
const isEditing = ref(false)
const editingAnnouncement = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)

// 公告数据
const announcements = ref([
  { 
    id: 1, 
    title: '关于2026年春季社团年审工作的通知', 
    type: '通知公告', 
    typeClass: 'tag-red', 
    author: '团委办公室', 
    date: '2026-03-15', 
    views: '2,341', 
    pinned: true,
    content: '各社团请注意，2026年春季社团年审工作将于3月20日开始，请各社团负责人提前准备相关材料...'
  },
  { 
    id: 2, 
    title: '社团管理系统升级维护通知', 
    type: '系统公告', 
    typeClass: 'tag-blue', 
    author: '系统管理员', 
    date: '2026-03-10', 
    views: '1,856', 
    pinned: true,
    content: '为了提供更好的服务体验，系统将于本周六晚10点进行升级维护，预计持续2小时...'
  },
  { 
    id: 3, 
    title: '2026年度优秀社团评选通知', 
    type: '通知公告', 
    typeClass: 'tag-red', 
    author: '团委办公室', 
    date: '2026-03-08', 
    views: '1,523', 
    pinned: true,
    content: '2026年度优秀社团评选活动正式启动，评选标准包括活动质量、成员参与度、社会影响力等...'
  },
  { 
    id: 4, 
    title: '计算机协会编程马拉松报名开启', 
    type: '活动预告', 
    typeClass: 'tag-orange', 
    author: '奚梓恒', 
    date: '2026-03-18', 
    views: '678', 
    pinned: false,
    content: '一年一度的编程马拉松大赛即将开始，本届主题为"AI与未来"，欢迎各位同学踊跃报名参与...'
  },
  { 
    id: 5, 
    title: '篮球社新生杯赛程公布', 
    type: '活动预告', 
    typeClass: 'tag-orange', 
    author: '刘硕', 
    date: '2026-03-16', 
    views: '445', 
    pinned: false,
    content: '2026年新生杯篮球赛赛程已确定，小组赛将于下周一开始，决赛定于月底举行...'
  },
  { 
    id: 6, 
    title: '摄影社春季外拍活动通知', 
    type: '活动预告', 
    typeClass: 'tag-orange', 
    author: '马虹华', 
    date: '2026-03-12', 
    views: '324', 
    pinned: false,
    content: '摄影社将在本周末组织春季外拍活动，地点为市植物园，欢迎大家携带相机参与...'
  },
  { 
    id: 7, 
    title: '文学社读书分享会延期通知', 
    type: '通知公告', 
    typeClass: 'tag-red', 
    author: '王宇航', 
    date: '2026-03-14', 
    views: '289', 
    pinned: false,
    content: '原定于本周五的读书分享会因故延期至下周五举行，给您带来的不便敬请谅解...'
  }
])

// 计算属性：过滤后的公告
const filteredAnnouncements = computed(() => {
  let filtered = [...announcements.value]
  
  // 搜索过滤
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(announcement => 
      announcement.title.toLowerCase().includes(keyword) || 
      announcement.content.toLowerCase().includes(keyword)
    )
  }
  
  // 分类过滤
  if (typeFilter.value) {
    filtered = filtered.filter(announcement => announcement.type === typeFilter.value)
  }
  
  return filtered
})

// 计算属性：分页后的公告
const paginatedAnnouncements = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredAnnouncements.value.slice(start, end)
})

// 计算属性：总页数
const totalPages = computed(() => {
  return Math.ceil(filteredAnnouncements.value.length / pageSize.value)
})

// 公告相关函数
const openAnnouncementForm = () => {
  isEditing.value = false
  editingAnnouncement.value = null
  showAnnouncementForm.value = true
}

const closeAnnouncementForm = () => {
  showAnnouncementForm.value = false
  editingAnnouncement.value = null
}

const handleAnnouncementSubmit = (data) => {
  if (isEditing.value) {
    // 编辑模式
    const index = announcements.value.findIndex(a => a.id === editingAnnouncement.value.id)
    if (index !== -1) {
      announcements.value[index] = {
        ...announcements.value[index],
        ...data
      }
    }
    alert('公告更新成功！')
  } else {
    // 新增模式
    const newAnnouncement = {
      id: Math.max(...announcements.value.map(a => a.id)) + 1,
      title: data.title,
      type: data.type,
      typeClass: getTypeClass(data.type),
      author: data.author,
      date: new Date().toISOString().split('T')[0],
      views: '0',
      pinned: data.pinned || false,
      content: data.content
    }
    announcements.value.unshift(newAnnouncement)
    alert('公告发布成功！')
  }
  
  closeAnnouncementForm()
}

const viewAnnouncement = (announcement) => {
  const details = `
公告详情：

标题：${announcement.title}
分类：${announcement.type}
发布人：${announcement.author}
发布时间：${announcement.date}
浏览量：${announcement.views}
置顶状态：${announcement.pinned ? '已置顶' : '未置顶'}

内容：
${announcement.content}
  `.trim()
  
  alert(details)
}

const editAnnouncement = (announcement) => {
  isEditing.value = true
  editingAnnouncement.value = { ...announcement }
  showAnnouncementForm.value = true
}

const deleteAnnouncement = (announcement) => {
  if (confirm(`确定要删除 "${announcement.title}" 这个公告吗？`)) {
    announcements.value = announcements.value.filter(a => a.id !== announcement.id)
    alert('删除成功！')
  }
}

const togglePin = (id) => {
  const announcement = announcements.value.find(a => a.id === id)
  if (announcement) {
    announcement.pinned = !announcement.pinned
    // 重新排序：置顶的排在前面
    announcements.value.sort((a, b) => {
      if (a.pinned && !b.pinned) return -1
      if (!a.pinned && b.pinned) return 1
      return 0
    })
    alert(announcement.pinned ? '公告已置顶' : '公告已取消置顶')
  }
}

const handleSearch = () => {
  console.log('执行搜索:', searchKeyword.value)
  currentPage.value = 1 // 搜索时回到第一页
}

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

const getTypeClass = (type) => {
  const classMap = {
    '通知公告': 'tag-red',
    '活动预告': 'tag-orange',
    '系统公告': 'tag-blue'
  }
  return classMap[type] || 'tag-gray'
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

.pin-toggle {
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background 0.2s;
}

.pin-toggle:hover {
  background: #F3F4F6;
}

.pin-toggle.pinned {
  color: #10B981;
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