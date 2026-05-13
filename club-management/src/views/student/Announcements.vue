<template>
  <div>
    <div class="card" style="margin-bottom:16px">
      <div class="search-bar" style="margin-bottom:0">
        <input class="search-input" placeholder="搜索公告..." v-model="searchKeyword" />
        <select class="filter-select" v-model="typeFilter">
          <option value="">全部分类</option>
          <option value="通知公告">通知公告</option>
          <option value="活动预告">活动预告</option>
          <option value="系统公告">系统公告</option>
        </select>
        <button class="btn btn-primary" @click="searchAnnouncements">🔍</button>
      </div>
    </div>
    
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>正在加载公告...</p>
    </div>
    
    <div v-else>
      <div class="announce-card" v-for="a in filteredAnnouncements" :key="a.id" :style="{borderLeftColor: getTypeColor(a.type)}">
        <div style="display:flex;justify-content:space-between;align-items:center">
          <div>
            <h4>{{a.pinned?'📌 ':''}}{{a.title}}</h4>
            <div class="meta">{{a.author}} · {{formatDate(a.createdAt)}} · 浏览 {{a.viewCount}}</div>
          </div>
          <span :class="['tag',getTypeClass(a.type)]">{{a.pinned?'置顶':a.type}}</span>
        </div>
        <p class="content">{{a.content}}</p>
      </div>
      
      <div v-if="filteredAnnouncements.length === 0" class="empty-state">
        <div class="empty-icon">📢</div>
        <p>暂无公告信息</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAnnouncementList } from '../../api/announcement'

// 响应式数据
const searchKeyword = ref('')
const typeFilter = ref('')
const loading = ref(false)

const announcements = ref([])

const extractRecords = (payload) => {
  if (!payload) return []
  if (Array.isArray(payload.records)) return payload.records
  if (Array.isArray(payload.list)) return payload.list
  if (Array.isArray(payload)) return payload
  return []
}

const normalizeAnnouncementType = (announcement) => {
  if (announcement.type) return announcement.type
  if (announcement.targetType === 'club') return '通知公告'
  if (announcement.publisherId === 1) return '系统公告'
  return '活动预告'
}

const normalizeAnnouncement = (announcement) => ({
  ...announcement,
  id: announcement.id || announcement.announcementId,
  type: normalizeAnnouncementType(announcement),
  author: announcement.author || announcement.publisherName || `用户${announcement.publisherId ?? ''}`,
  pinned: Boolean(announcement.pinned ?? announcement.isTop),
  createdAt: announcement.createdAt || announcement.publishTime || announcement.createTime,
  viewCount: announcement.viewCount || 0
})

// 计算属性：过滤后的公告
const filteredAnnouncements = computed(() => {
  let result = [...announcements.value]
  
  // 按类型过滤
  if (typeFilter.value) {
    result = result.filter(announcement => announcement.type === typeFilter.value)
  }
  
  // 按关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(announcement => 
      announcement.title.toLowerCase().includes(keyword) ||
      announcement.content.toLowerCase().includes(keyword)
    )
  }
  
  // 置顶公告排在前面
  result.sort((a, b) => {
    if (a.pinned && !b.pinned) return -1
    if (!a.pinned && b.pinned) return 1
    return new Date(b.createdAt) - new Date(a.createdAt)
  })
  
  return result
})

// 获取类型样式类
const getTypeClass = (type) => {
  const typeMap = {
    '通知公告': 'tag-red',
    '活动预告': 'tag-orange',
    '系统公告': 'tag-blue'
  }
  return typeMap[type] || 'tag-gray'
}

// 获取类型边框颜色
const getTypeColor = (type) => {
  const colorMap = {
    '通知公告': '#EF4444',
    '活动预告': '#F59E0B',
    '系统公告': '#3B82F6'
  }
  return colorMap[type] || '#D1D5DB'
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 获取公告列表
const fetchAnnouncements = async () => {
  loading.value = true
  try {
    const response = await getAnnouncementList({ page: 1, size: 50 })
    if (response.code === 200) {
      announcements.value = extractRecords(response.data).map(normalizeAnnouncement)
    }
  } catch (error) {
    console.error('获取公告列表失败:', error)
    announcements.value = []
    ElMessage.error('公告数据加载失败')
  } finally {
    loading.value = false
  }
}

// 搜索公告
const searchAnnouncements = () => {
  // 搜索逻辑已在计算属性中实现
}

// 组件挂载时获取数据
onMounted(() => {
  fetchAnnouncements()
})
</script>

<style scoped>
.announce-card { background:#fff; border-radius:12px; padding:20px 24px; border-left:4px solid #D1D5DB; box-shadow:0 1px 3px rgba(0,0,0,0.06); margin-bottom:12px; }
.announce-card h4 { font-size:14px; margin-bottom:4px; }
.meta { font-size:12px; color:#9CA3AF; }
.content { font-size:13px; color:#6B7280; margin-top:10px; line-height:1.8; }

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
