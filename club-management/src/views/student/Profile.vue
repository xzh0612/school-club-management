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

      <div v-if="showEditDialog" class="edit-overlay" @click="closeEditDialog">
        <div class="edit-dialog" @click.stop>
          <div class="card-header">
            <h3>编辑资料</h3>
            <button class="close-btn" @click="closeEditDialog">&times;</button>
          </div>
          <div class="edit-form">
            <div class="form-group"><label>姓名</label><input v-model="editForm.realName" class="input" /></div>
            <div class="form-group"><label>院系</label><input v-model="editForm.department" class="input" /></div>
            <div class="form-group"><label>班级</label><input v-model="editForm.className" class="input" /></div>
            <div class="form-group"><label>手机号</label><input v-model="editForm.phone" class="input" /></div>
            <div class="form-group"><label>邮箱</label><input v-model="editForm.email" class="input" /></div>
            <div class="form-group"><label>新密码</label><input v-model="editForm.password" type="password" class="input" placeholder="留空则不修改" /></div>
          </div>
          <div class="dialog-actions">
            <button class="btn btn-outline" @click="closeEditDialog" :disabled="saving">取消</button>
            <button class="btn btn-primary" @click="submitProfile" :disabled="saving">{{ saving ? '保存中...' : '保存' }}</button>
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
import { getCurrentUser, getUserClubs, getUserActivities, updateUser } from '../../api/user'

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
const showEditDialog = ref(false)
const saving = ref(false)
const editForm = reactive({
  realName: '',
  department: '',
  className: '',
  phone: '',
  email: '',
  password: ''
})

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
  Object.assign(editForm, {
    realName: currentUser.name || '',
    department: currentUser.department || '',
    className: currentUser.className || '',
    phone: currentUser.phone || '',
    email: currentUser.email || '',
    password: ''
  })
  showEditDialog.value = true
}

const closeEditDialog = () => {
  if (!saving.value) {
    showEditDialog.value = false
  }
}

const submitProfile = async () => {
  if (!currentUser.id) return
  saving.value = true
  try {
    const payload = {
      realName: editForm.realName,
      department: editForm.department,
      className: editForm.className,
      phone: editForm.phone,
      email: editForm.email
    }
    if (editForm.password) {
      payload.password = editForm.password
    }
    const response = await updateUser(currentUser.id, payload)
    if (response.code === 200) {
      Object.assign(currentUser, {
        name: editForm.realName,
        department: editForm.department,
        className: editForm.className,
        phone: editForm.phone,
        email: editForm.email
      })
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      localStorage.setItem('user', JSON.stringify({
        ...user,
        name: editForm.realName,
        avatar: editForm.realName?.charAt(0) || user.avatar || 'U'
      }))
      showEditDialog.value = false
      ElMessage.success('资料已更新')
      await fetchCurrentUser()
    }
  } catch (error) {
    console.error('更新资料失败:', error)
  } finally {
    saving.value = false
  }
}

// 获取当前用户信息
const fetchCurrentUser = async () => {
  try {
    const response = await getCurrentUser()
    if (response.code === 200) {
      const userData = response.data
      Object.assign(currentUser, {
        id: userData.id || userData.userId,
        name: userData.name || userData.realName,
        studentId: userData.studentId || '',
        department: userData.department || '',
        className: userData.className || '',
        phone: userData.phone || '',
        email: userData.email || '',
        registerTime: userData.registerTime || ''
      })
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('个人信息加载失败')
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
    userClubs.value = []
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
    activityRecords.value = []
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

.edit-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1200;
}

.edit-dialog {
  width: min(560px, 92vw);
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.18);
}

.close-btn {
  border: none;
  background: transparent;
  font-size: 24px;
  line-height: 1;
  cursor: pointer;
}

.edit-form {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px 16px;
  margin-top: 16px;
}

.edit-form .form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.edit-form label {
  font-size: 13px;
  color: #374151;
}

.input {
  border: 1px solid #D1D5DB;
  border-radius: 6px;
  padding: 10px 12px;
  font-size: 14px;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 18px;
}

@media (max-width: 760px) {
  .edit-form {
    grid-template-columns: 1fr;
  }
}
</style>
