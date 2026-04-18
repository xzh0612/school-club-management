<template>
  <div>
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>正在加载个人信息...</p>
    </div>
    
    <div v-else>
      <div style="display:grid;grid-template-columns:1fr 1fr;gap:16px">
        <div class="card">
          <div class="card-header">
            <h3>个人信息</h3>
            <button class="btn btn-outline btn-sm" @click="editProfile">编辑资料</button>
          </div>
          <div style="display:flex;align-items:center;gap:16px;margin-bottom:20px">
            <div class="profile-avatar">{{ getAvatarText(currentUser.name) }}</div>
            <div>
              <div style="font-size:18px;font-weight:600">{{ currentUser.name }}</div>
              <div style="font-size:13px;color:#9CA3AF">学号：{{ currentUser.studentId }} · {{ currentUser.className }}</div>
            </div>
          </div>
          <table class="data-table">
            <tbody>
              <tr><td style="color:#6B7280;width:80px">姓名</td><td>{{ currentUser.name }}</td></tr>
              <tr><td style="color:#6B7280">学号</td><td>{{ currentUser.studentId }}</td></tr>
              <tr><td style="color:#6B7280">院系</td><td>{{ currentUser.department }}</td></tr>
              <tr><td style="color:#6B7280">班级</td><td>{{ currentUser.className }}</td></tr>
              <tr><td style="color:#6B7280">手机号</td><td>{{ formatPhone(currentUser.phone) }}</td></tr>
              <tr><td style="color:#6B7280">邮箱</td><td>{{ currentUser.email }}</td></tr>
              <tr><td style="color:#6B7280">注册时间</td><td>{{ formatDate(currentUser.registerTime) }}</td></tr>
            </tbody>
          </table>
        </div>
        
        <div class="card">
          <div class="card-header">
            <h3>我的社团</h3>
            <span style="font-size:13px;color:#6B7280">{{ userClubs.length }}个社团</span>
          </div>
          <div v-if="userClubs.length > 0">
            <div class="club-item" v-for="c in userClubs" :key="c.id">
              <div class="club-icon" :style="{background:getClubBackground(c.clubName)}">{{ getClubIcon(c.clubName) }}</div>
              <div>
                <div style="font-weight:500">{{ c.clubName }}</div>
                <div style="font-size:12px;color:#9CA3AF">{{ c.role }} · {{ formatDate(c.joinTime) }} 加入</div>
              </div>
            </div>
          </div>
          <div v-else class="empty-state">
            <div class="empty-icon">👥</div>
            <p>暂未加入任何社团</p>
          </div>
        </div>
      </div>
      
      <div class="card" style="margin-top:16px">
        <div class="card-header">
          <h3>我的活动记录</h3>
          <span style="font-size:13px;color:#6B7280">共参加 {{ activityRecords.length }} 次</span>
        </div>
        <div v-if="activityRecords.length > 0">
          <table class="data-table">
            <thead>
              <tr>
                <th>活动名称</th>
                <th>社团</th>
                <th>时间</th>
                <th>签到状态</th>
                <th>评价</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="a in activityRecords" :key="a.id">
                <td>{{ a.activityName }}</td>
                <td>{{ a.clubName }}</td>
                <td>{{ formatDate(a.activityTime) }}</td>
                <td><span :class="['tag',getCheckStatusClass(a.checkStatus)]">{{ a.checkStatus }}</span></td>
                <td>{{ a.rating || '—' }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div v-else class="empty-state">
          <div class="empty-icon">📅</div>
          <p>暂无活动记录</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCurrentUser, getUserClubs, getUserActivities } from '../../api/user'

// 响应式数据
const loading = ref(true)

// 当前用户信息
const currentUser = reactive({
  id: '',
  name: '',
  studentId: '',
  department: '',
  className: '',
  phone: '',
  email: '',
  registerTime: ''
})

// 用户社团信息
const userClubs = ref([])

// 活动记录
const activityRecords = ref([])

// 获取头像文字
const getAvatarText = (name) => {
  return name ? name.charAt(0) : 'U'
}

// 格式化手机号
const formatPhone = (phone) => {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 获取社团图标
const getClubIcon = (clubName) => {
  const icons = {
    '篮球社': '🏀',
    '计算机协会': '💻',
    '摄影社': '📷',
    '文学社': '📖',
    '音乐社': '🎵',
    '舞蹈社': '💃',
    '辩论社': '🎤',
    '志愿者协会': '❤️'
  }
  return icons[clubName] || '🎉'
}

// 获取社团背景色
const getClubBackground = (clubName) => {
  const backgrounds = {
    '篮球社': '#FFF7ED',
    '计算机协会': '#EFF6FF',
    '摄影社': '#FCE7F3',
    '文学社': '#ECFDF5',
    '音乐社': '#F0F9FF',
    '舞蹈社': '#FDF2F8',
    '辩论社': '#F5F3FF',
    '志愿者协会': '#FEF2F2'
  }
  return backgrounds[clubName] || '#F3F4F6'
}

// 获取签到状态样式类
const getCheckStatusClass = (status) => {
  const statusMap = {
    '待签到': 'tag-orange',
    '已签到': 'tag-green',
    '已报名': 'tag-blue',
    '缺席': 'tag-gray'
  }
  return statusMap[status] || 'tag-gray'
}

// 编辑资料
const editProfile = () => {
  ElMessage.info('编辑资料功能开发中...')
}

// 获取当前用户信息
const fetchCurrentUser = async () => {
  try {
    // 首先尝试从localStorage获取用户信息
    const storedUser = localStorage.getItem('user')
    if (storedUser) {
      const userData = JSON.parse(storedUser)
      Object.assign(currentUser, {
        id: userData.id || '',
        name: userData.name || '刘硕',
        studentId: userData.studentId || '2021001',
        department: userData.department || '计算机科学与技术学院',
        className: userData.className || '软件工程1班',
        phone: userData.phone || '13800138000',
        email: userData.email || 'liushuo@edu.cn',
        registerTime: userData.registerTime || new Date().toISOString()
      })
      return
    }
    
    // 如果localStorage没有，则调用API
    const response = await getCurrentUser()
    if (response.code === 200) {
      const userData = response.data
      Object.assign(currentUser, {
        id: userData.id,
        name: userData.name,
        studentId: userData.studentId,
        department: userData.department,
        className: userData.className,
        phone: userData.phone,
        email: userData.email,
        registerTime: userData.registerTime
      })
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    // 使用默认数据
    Object.assign(currentUser, {
      id: '1',
      name: '刘硕',
      studentId: '2021001',
      department: '计算机科学与技术学院',
      className: '软件工程1班',
      phone: '13800138000',
      email: 'liushuo@edu.cn',
      registerTime: new Date().toISOString()
    })
  }
}

// 获取用户社团信息
const fetchUserClubs = async () => {
  try {
    if (!currentUser.id) return
    
    const response = await getUserClubs(currentUser.id)
    if (response.code === 200) {
      userClubs.value = response.data || []
    }
  } catch (error) {
    console.error('获取社团信息失败:', error)
    // 使用模拟数据
    userClubs.value = [
      {
        id: 1,
        clubName: '篮球社',
        role: '核心成员',
        joinTime: new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString()
      },
      {
        id: 2,
        clubName: '计算机协会',
        role: '普通成员',
        joinTime: new Date(Date.now() - 45 * 24 * 60 * 60 * 1000).toISOString()
      }
    ]
  }
}

// 获取用户活动记录
const fetchUserActivities = async () => {
  try {
    if (!currentUser.id) return
    
    const response = await getUserActivities(currentUser.id)
    if (response.code === 200) {
      activityRecords.value = response.data || []
    }
  } catch (error) {
    console.error('获取活动记录失败:', error)
    // 使用模拟数据
    activityRecords.value = [
      {
        id: 1,
        activityName: '迎新篮球赛',
        clubName: '篮球社',
        activityTime: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000).toISOString(),
        checkStatus: '已签到',
        rating: '⭐⭐⭐⭐⭐'
      },
      {
        id: 2,
        activityName: '编程入门讲座',
        clubName: '计算机协会',
        activityTime: new Date(Date.now() - 10 * 24 * 60 * 60 * 1000).toISOString(),
        checkStatus: '已签到',
        rating: '⭐⭐⭐⭐'
      },
      {
        id: 3,
        activityName: '春季招新',
        clubName: '篮球社',
        activityTime: new Date(Date.now() + 2 * 24 * 60 * 60 * 1000).toISOString(),
        checkStatus: '已报名',
        rating: ''
      }
    ]
  }
}

// 初始化数据
const initializeData = async () => {
  loading.value = true
  try {
    await fetchCurrentUser()
    await Promise.all([
      fetchUserClubs(),
      fetchUserActivities()
    ])
  } catch (error) {
    console.error('初始化数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 组件挂载时获取数据
onMounted(() => {
  initializeData()
})
</script>

<style scoped>
.profile-avatar { 
  width:64px; 
  height:64px; 
  border-radius:50%; 
  background:linear-gradient(135deg,#3B82F6,#8B5CF6); 
  color:#fff; 
  display:flex; 
  align-items:center; 
  justify-content:center; 
  font-size:24px; 
  font-weight:700; 
}

.club-item { 
  display:flex; 
  align-items:center; 
  gap:12px; 
  padding:10px 0; 
  border-bottom:1px solid #F3F4F6; 
}

.club-item:last-child { 
  border:none; 
}

.club-icon { 
  width:36px; 
  height:36px; 
  border-radius:8px; 
  display:flex; 
  align-items:center; 
  justify-content:center; 
  font-size:18px; 
}

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
  padding: 40px 20px;
  color: #6B7280;
}

.empty-icon {
  font-size: 36px;
  margin-bottom: 12px;
}
</style>