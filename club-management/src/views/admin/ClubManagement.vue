<template>
  <div>
    <div class="stats-grid">
      <div class="stat-card"><div class="icon-bg">🏛️</div><div class="label">社团总数</div><div class="value">{{ totalClubs }}</div></div>
      <div class="stat-card"><div class="icon-bg">✅</div><div class="label">已审批</div><div class="value">{{ approvedClubs }}</div></div>
      <div class="stat-card"><div class="icon-bg">⏳</div><div class="label">审批中</div><div class="value">{{ pendingClubs }}</div></div>
      <div class="stat-card"><div class="icon-bg">🏷️</div><div class="label">已分类</div><div class="value">{{ classifiedClubs }}</div></div>
    </div>
    <div class="card">
      <div class="card-header"><h3>社团列表</h3><div style="display:flex;gap:8px"><input class="search-input" style="width:180px" placeholder="搜索社团名称..." v-model="searchKeyword" @keyup.enter="handleSearch" /><select class="filter-select" v-model="typeFilter"><option value="">全部类型</option><option value="academic">学术科技</option><option value="culture">文化艺术</option><option value="sports">体育竞技</option><option value="volunteer">公益志愿</option><option value="innovation">创新创业</option><option value="general">综合类</option></select><button class="btn btn-primary btn-sm" @click="handleSearch">🔍 搜索</button></div></div>
      <div class="club-grid">
        <div class="club-card" v-for="c in filteredClubs" :key="c.id" @click="openMembers(c)">
          <div class="club-cover" :style="{ background: c.gradient }">{{ c.icon }}</div>
          <div class="club-info">
            <h4>{{ c.name }}</h4>
            <p class="club-desc">{{ c.desc }}</p>
            <div class="club-meta"><div><span :class="['tag', c.typeClass]">{{ c.type }}</span></div><div>{{ c.members }}人</div></div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="selectedClub" class="card" style="margin-top:16px">
      <div class="card-header">
        <h3>{{ selectedClub.name }} 成员管理</h3>
        <button class="btn btn-outline btn-sm" @click="closeMembers">关闭</button>
      </div>
      <table class="data-table">
        <thead><tr><th>成员</th><th>学号</th><th>社团身份</th><th>状态</th><th>加入时间</th><th v-if="canManageMembers">操作</th></tr></thead>
        <tbody>
          <tr v-for="m in members" :key="m.id || `${m.clubId}-${m.userId}`">
            <td>{{ m.userName || '—' }}</td>
            <td>{{ m.studentId || '—' }}</td>
            <td>
              <select v-if="canManageMembers" class="inline-select" v-model="m.role" @change="updateMember(m)">
                <option value="leader">社长</option>
                <option value="member">社员</option>
              </select>
              <span v-else>{{ roleText(m.role) }}</span>
            </td>
            <td>
              <select v-if="canManageMembers" class="inline-select" v-model="m.status" @change="updateMember(m)">
                <option value="active">正常</option>
                <option value="inactive">停用</option>
              </select>
              <span v-else :class="['tag', m.status === 'active' ? 'tag-green' : 'tag-gray']">{{ statusText(m.status) }}</span>
            </td>
            <td>{{ formatDate(m.joinTime) }}</td>
            <td v-if="canManageMembers"><button class="btn btn-danger btn-sm" @click="deleteMember(m)">移除</button></td>
          </tr>
        </tbody>
      </table>
      <div v-if="members.length === 0" class="empty-state">暂无成员数据</div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { getClubList, getClubMembers, getClubStats, removeClubMember, updateClubMember } from '../../api/club'

const clubs = ref([])
const members = ref([])
const selectedClub = ref(null)
const totalClubs = ref(0)
const approvedClubs = ref(0)
const pendingClubs = ref(0)
const classifiedClubs = ref(0)
const currentPage = ref(1)
const pageSize = ref(6)
const loading = ref(false)
const searchKeyword = ref('')
const typeFilter = ref('')
const currentRole = computed(() => JSON.parse(sessionStorage.getItem('user') || '{}').role)
const canManageMembers = computed(() => currentRole.value === 'club_leader')

// 获取社团列表
const fetchClubs = async () => {
  loading.value = true
  try {
    const response = await getClubList(currentPage.value, pageSize.value, '', searchKeyword.value)
    
    const records = response.data.records || []
    clubs.value = await Promise.all(records.map(async (club) => {
      let memberCount = 0
      try {
        const memberResponse = await getClubMembers(club.clubId, 1, 1)
        memberCount = memberResponse.data.total || 0
      } catch (error) {
        memberCount = 0
      }
      return {
      id: club.clubId,
      name: club.clubName,
      icon: getClubIcon(club.clubType),
      desc: club.description || '暂无描述',
      type: getClubTypeLabel(club.clubType),
      typeKey: club.clubType || 'general',
      typeClass: getClubTypeClass(club.clubType),
      members: memberCount,
      gradient: getClubGradient(club.clubType),
      status: club.status,
      founderName: club.founderName,
      advisorName: club.advisorName
    }}))
    
    await fetchClubStats()
  } catch (error) {
    console.error('获取社团列表失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchClubStats = async () => {
  const response = await getClubStats()
  totalClubs.value = response.data?.total || 0
  approvedClubs.value = response.data?.approved || 0
  pendingClubs.value = response.data?.pending || 0
  classifiedClubs.value = response.data?.classified || 0
}

// 搜索社团
const handleSearch = () => {
  currentPage.value = 1
  fetchClubs()
}

const openMembers = async (club) => {
  selectedClub.value = club
  await fetchMembers()
}

const closeMembers = () => {
  selectedClub.value = null
  members.value = []
}

const fetchMembers = async () => {
  if (!selectedClub.value) return
  const response = await getClubMembers(selectedClub.value.id, 1, 100)
  members.value = response.data?.records || []
}

const updateMember = async (member) => {
  await updateClubMember(selectedClub.value.id, member.userId, {
    role: member.role,
    status: member.status
  })
  await fetchMembers()
  await fetchClubs()
}

const deleteMember = async (member) => {
  if (!confirm(`确定移除 ${member.userName || '该成员'} 吗？`)) return
  await removeClubMember(selectedClub.value.id, member.userId)
  await fetchMembers()
  await fetchClubs()
}

const roleText = (role) => ({ leader: '社长', club_leader: '社长', member: '社员' }[role] || role)
const statusText = (status) => ({ active: '正常', inactive: '停用' }[status] || status)
const formatDate = (value) => value ? new Date(value).toLocaleString('zh-CN') : '—'

const filteredClubs = computed(() => clubs.value.filter(c => {
  const matchKeyword = !searchKeyword.value || c.name.includes(searchKeyword.value) || c.desc.includes(searchKeyword.value)
  const matchType = !typeFilter.value || c.typeKey === typeFilter.value
  return matchKeyword && matchType
}))

// 根据社团名称获取图标
const getClubIcon = (type) => {
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

// 根据真实类型获取显示名称
const getClubTypeLabel = (type) => {
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

// 根据社团名称获取类型样式
const getClubTypeClass = (type) => {
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

// 根据社团名称获取渐变背景
const getClubGradient = (type) => {
  const gradientMap = {
    academic: 'linear-gradient(135deg,#DBEAFE,#BFDBFE)',
    culture: 'linear-gradient(135deg,#FCE7F3,#FBCFE8)',
    sports: 'linear-gradient(135deg,#FEF3C7,#FDE68A)',
    volunteer: 'linear-gradient(135deg,#D1FAE5,#A7F3D0)',
    innovation: 'linear-gradient(135deg,#EDE9FE,#DDD6FE)',
    general: 'linear-gradient(135deg,#F3F4F6,#E5E7EB)'
  }
  return gradientMap[type] || 'linear-gradient(135deg,#F3F4F6,#E5E7EB)'
}

// 初始化
onMounted(() => {
  fetchClubs()
})
</script>

<style scoped>
.club-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
.club-card { background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.06); cursor: pointer; transition: transform 0.2s; }
.club-card:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
.club-cover { height: 100px; display: flex; align-items: center; justify-content: center; font-size: 36px; }
.club-info { padding: 14px 16px; }
.club-info h4 { font-size: 14px; margin-bottom: 4px; }
.club-desc { font-size: 12px; color: #6B7280; line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.club-meta { display: flex; justify-content: space-between; align-items: center; margin-top: 10px; font-size: 12px; color: #9CA3AF; }
.inline-select { min-width: 84px; padding: 6px 8px; border: 1px solid #D1D5DB; border-radius: 4px; background: #fff; }
</style>
