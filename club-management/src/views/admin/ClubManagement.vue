<template>
  <div>
    <div class="stats-grid">
      <div class="stat-card"><div class="icon-bg">🏛️</div><div class="label">社团总数</div><div class="value">{{ totalClubs }}</div></div>
      <div class="stat-card"><div class="icon-bg">✅</div><div class="label">已审批</div><div class="value">{{ approvedClubs }}</div></div>
      <div class="stat-card"><div class="icon-bg">⏳</div><div class="label">审批中</div><div class="value">{{ pendingClubs }}</div></div>
      <div class="stat-card"><div class="icon-bg">⭐</div><div class="label">五星社团</div><div class="value">{{ starClubs }}</div></div>
    </div>
    <div class="card">
      <div class="card-header"><h3>社团列表</h3><div style="display:flex;gap:8px"><input class="search-input" style="width:180px" placeholder="搜索社团名称..." v-model="searchKeyword" @keyup.enter="handleSearch" /><select class="filter-select" v-model="typeFilter"><option value="">全部类型</option><option value="academic">学术科技</option><option value="culture">文化艺术</option><option value="sports">体育竞技</option><option value="volunteer">公益志愿</option></select><button class="btn btn-primary btn-sm" @click="handleSearch">🔍 搜索</button></div></div>
      <div class="club-grid">
        <div class="club-card" v-for="c in clubs" :key="c.id">
          <div class="club-cover" :style="{ background: c.gradient }">{{ c.icon }}</div>
          <div class="club-info">
            <h4>{{ c.name }}</h4>
            <p class="club-desc">{{ c.desc }}</p>
            <div class="club-meta"><div><span :class="['tag', c.typeClass]">{{ c.type }}</span> ⭐{{c.stars}}星</div><div>{{ c.members }}人</div></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getClubList } from '../../api/club'

const clubs = ref([])
const totalClubs = ref(0)
const approvedClubs = ref(0)
const pendingClubs = ref(0)
const starClubs = ref(0)
const currentPage = ref(1)
const pageSize = ref(6)
const loading = ref(false)
const searchKeyword = ref('')
const typeFilter = ref('')

// 获取社团列表
const fetchClubs = async () => {
  loading.value = true
  try {
    const response = await getClubList(currentPage.value, pageSize.value, '', searchKeyword.value)
    
    clubs.value = response.data.records.map(club => ({
      id: club.clubId,
      name: club.clubName,
      icon: getClubIcon(club.clubName),
      desc: club.description || '暂无描述',
      type: getClubType(club.clubName),
      typeClass: getClubTypeClass(club.clubName),
      stars: getClubStars(club.clubName),
      members: getClubMembers(club.clubName),
      gradient: getClubGradient(club.clubName),
      status: club.status,
      founderName: club.founderName,
      advisorName: club.advisorName
    }))
    
    totalClubs.value = response.data.total
    approvedClubs.value = clubs.value.filter(c => c.status === 'approved').length
    pendingClubs.value = clubs.value.filter(c => c.status === 'pending').length
    starClubs.value = clubs.value.filter(c => c.stars >= 5).length
  } catch (error) {
    console.error('获取社团列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索社团
const handleSearch = () => {
  currentPage.value = 1
  fetchClubs()
}

// 根据社团名称获取图标
const getClubIcon = (name) => {
  const iconMap = {
    '计算机协会': '💻',
    '摄影社': '📷',
    '篮球社': '🏀',
    '文学社': '📖',
    '辩论社': '🎤',
    '音乐社': '🎵'
  }
  return iconMap[name] || '🏛️'
}

// 根据社团名称获取类型
const getClubType = (name) => {
  const typeMap = {
    '计算机协会': '学术科技',
    '摄影社': '文化艺术',
    '篮球社': '体育竞技',
    '文学社': '文化艺术',
    '辩论社': '学术科技',
    '音乐社': '文化艺术'
  }
  return typeMap[name] || '综合类'
}

// 根据社团名称获取类型样式
const getClubTypeClass = (name) => {
  const classMap = {
    '学术科技': 'tag-blue',
    '文化艺术': 'tag-purple',
    '体育竞技': 'tag-orange',
    '公益志愿': 'tag-green'
  }
  return classMap[getClubType(name)] || 'tag-gray'
}

// 根据社团名称获取星级
const getClubStars = (name) => {
  const starMap = {
    '计算机协会': 5,
    '摄影社': 4,
    '篮球社': 4,
    '文学社': 5,
    '辩论社': 4,
    '音乐社': 3
  }
  return starMap[name] || 3
}

// 根据社团名称估算成员数
const getClubMembers = (name) => {
  const memberMap = {
    '计算机协会': 156,
    '摄影社': 89,
    '篮球社': 203,
    '文学社': 127,
    '辩论社': 68,
    '音乐社': 75
  }
  return memberMap[name] || Math.floor(Math.random() * 100) + 50
}

// 根据社团名称获取渐变背景
const getClubGradient = (name) => {
  const gradientMap = {
    '计算机协会': 'linear-gradient(135deg,#DBEAFE,#BFDBFE)',
    '摄影社': 'linear-gradient(135deg,#FCE7F3,#FBCFE8)',
    '篮球社': 'linear-gradient(135deg,#FEF3C7,#FDE68A)',
    '文学社': 'linear-gradient(135deg,#D1FAE5,#A7F3D0)',
    '辩论社': 'linear-gradient(135deg,#EDE9FE,#DDD6FE)',
    '音乐社': 'linear-gradient(135deg,#FEFCE8,#FEF9C3)'
  }
  return gradientMap[name] || 'linear-gradient(135deg,#F3F4F6,#E5E7EB)'
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
</style>