<template>
  <div>
    <div class="search-bar">
      <input class="search-input" placeholder="搜索社团名称..." v-model="keyword" @keyup.enter="handleSearch" />
      <select class="filter-select" v-model="statusFilter" @change="handleSearch">
        <option value="approved">已成立社团</option>
        <option value="">全部状态</option>
        <option value="pending">待审批</option>
        <option value="rejected">已驳回</option>
      </select>
      <select class="filter-select" v-model="typeFilter" @change="handleSearch">
        <option value="">全部类型</option>
        <option value="academic">学术科技</option>
        <option value="culture">文化艺术</option>
        <option value="sports">体育竞技</option>
        <option value="volunteer">公益志愿</option>
        <option value="innovation">创新创业</option>
        <option value="general">综合类</option>
      </select>
      <button class="btn btn-primary" @click="handleSearch">🔍 搜索</button>
    </div>

    <div v-if="loading" class="empty-state">正在加载社团...</div>
    <div v-else class="club-grid">
      <div class="club-card" v-for="c in clubs" :key="c.id">
        <div class="club-cover" :style="{ background: c.gradient }">{{ c.icon }}</div>
        <div class="club-info">
          <h4>{{ c.name }}</h4>
          <p class="club-desc">{{ c.desc }}</p>
          <div class="club-meta">
            <div>
              <span :class="['tag', c.statusClass]">{{ c.statusText }}</span>
              <span :class="['tag', c.typeClass]" style="margin-left:8px">{{ c.typeLabel }}</span>
              <br>
              <span class="club-members">{{ c.founderName || '暂无负责人' }} · {{ c.advisorName || '暂无指导老师' }}</span>
            </div>
            <button class="btn btn-primary btn-sm" @click="applyJoin(c)" :disabled="c.status !== 'approved'">申请加入</button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="!loading && clubs.length === 0" class="empty-state">暂无社团数据</div>

    <div v-if="!loading && totalPages > 1" class="pagination">
      <button class="btn btn-outline btn-sm" :disabled="currentPage <= 1" @click="changePage(currentPage - 1)">上一页</button>
      <span class="page-info">第 {{ currentPage }} 页，共 {{ totalPages }} 页</span>
      <button class="btn btn-outline btn-sm" :disabled="currentPage >= totalPages" @click="changePage(currentPage + 1)">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getClubList } from '../../api/club'
import { submitApplication } from '../../api/application'

const keyword = ref('')
const statusFilter = ref('approved')
const typeFilter = ref('')
const loading = ref(false)
const clubs = ref([])
const currentPage = ref(1)
const pageSize = ref(9)
const total = ref(0)
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))

const extractRecords = (payload) => payload?.records || payload?.list || (Array.isArray(payload) ? payload : [])

const filteredClubs = computed(() => clubs.value.filter(club => {
  const matchKeyword = !keyword.value || club.name.includes(keyword.value) || club.desc.includes(keyword.value)
  const matchStatus = !statusFilter.value || club.status === statusFilter.value
  const matchType = !typeFilter.value || club.typeKey === typeFilter.value
  return matchKeyword && matchStatus && matchType
}))

const getClubIcon = (type = '') => {
  const iconMap = {
    academic: '💻',
    culture: '📷',
    sports: '🏀',
    volunteer: '🤝',
    innovation: '🤖',
    general: '🏛️'
  }
  return iconMap[type] || '🏛️'
}

const getClubTypeLabel = (type = '') => {
  const typeMap = {
    academic: '学术科技',
    culture: '文化艺术',
    sports: '体育竞技',
    volunteer: '公益志愿',
    innovation: '创新创业',
    general: '综合类'
  }
  return typeMap[type] || '综合类'
}

const getClubTypeClass = (type = '') => {
  const classMap = {
    academic: 'tag-blue',
    culture: 'tag-purple',
    sports: 'tag-orange',
    volunteer: 'tag-green',
    innovation: 'tag-blue',
    general: 'tag-gray'
  }
  return classMap[type] || 'tag-gray'
}

const getStatusText = (status) => ({ approved: '已成立', pending: '待审批', rejected: '已驳回', inactive: '已停用' }[status] || status || '未知')
const getStatusClass = (status) => ({ approved: 'tag-green', pending: 'tag-orange', rejected: 'tag-red', inactive: 'tag-gray' }[status] || 'tag-gray')

const normalizeClub = (club) => ({
  id: club.clubId || club.id,
  name: club.clubName || club.name,
  desc: club.description || '暂无简介',
  typeKey: club.clubType || 'general',
  typeLabel: getClubTypeLabel(club.clubType),
  typeClass: getClubTypeClass(club.clubType),
  status: club.status,
  statusText: getStatusText(club.status),
  statusClass: getStatusClass(club.status),
  founderName: club.founderName,
  advisorName: club.advisorName,
  icon: getClubIcon(club.clubType),
  gradient: 'linear-gradient(135deg,#EFF6FF,#DBEAFE)'
})

const fetchClubs = async () => {
  loading.value = true
  try {
    const response = await getClubList(currentPage.value, pageSize.value, statusFilter.value, keyword.value, typeFilter.value)
    clubs.value = extractRecords(response.data).map(normalizeClub)
    total.value = response.data?.total || clubs.value.length
  } catch (error) {
    console.error('获取社团失败:', error)
    clubs.value = []
    ElMessage.error('社团数据加载失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchClubs()
}

const changePage = (page) => {
  currentPage.value = page
  fetchClubs()
}

const applyJoin = async (club) => {
  try {
    await submitApplication(club.id, `申请加入${club.name}`)
    ElMessage.success('入社申请已提交')
  } catch (error) {
    console.error('提交入社申请失败:', error)
  }
}

onMounted(fetchClubs)
</script>

<style scoped>
.club-grid { display:grid; grid-template-columns:repeat(3,1fr); gap:16px; }
.club-card { background:#fff; border-radius:12px; overflow:hidden; box-shadow:0 1px 3px rgba(0,0,0,0.06); transition:transform 0.2s; }
.club-card:hover { transform:translateY(-2px); box-shadow:0 4px 12px rgba(0,0,0,0.1); }
.club-cover { height:120px; display:flex; align-items:center; justify-content:center; font-size:40px; }
.club-info { padding:16px; }
.club-info h4 { font-size:15px; margin-bottom:6px; }
.club-desc { font-size:12px; color:#6B7280; line-height:1.6; margin-bottom:12px; }
.club-meta { display:flex; justify-content:space-between; align-items:center; }
.club-members { font-size:11px; color:#9CA3AF; }
</style>
