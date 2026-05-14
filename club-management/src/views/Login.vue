<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-left">
        <h1>🏫 学校社团管理系统</h1>
        <p>一体化校园社团管理平台<br>覆盖社团全生命周期管理</p>
        <div class="features">
          <div class="feat"><div class="feat-icon">📋</div><span>全流程审批管理</span></div>
          <div class="feat"><div class="feat-icon">👥</div><span>多角色协同协作</span></div>
          <div class="feat"><div class="feat-icon">📊</div><span>数据可视化分析</span></div>
          <div class="feat"><div class="feat-icon">🔔</div><span>实时消息通知</span></div>
        </div>
      </div>
      <div class="login-right">
        <h2>欢迎登录</h2>
        <p class="subtitle">请输入您的账号信息</p>
        <div class="role-selector">
          <div
            v-for="role in roles"
            :key="role.key"
            :class="['role-btn', { active: selectedRole === role.key }]"
            @click="selectedRole = role.key"
          >
            {{ role.icon }} {{ role.label }}
          </div>
        </div>
        <div class="form-group">
          <label>用户账号</label>
          <input
            v-model="username"
            :placeholder="rolePlaceholder"
            @keyup.enter="handleLogin"
          />
        </div>
        <div class="form-group">
          <label>登录密码</label>
          <input
            v-model="password"
            type="password"
            placeholder="请输入密码"
            @keyup.enter="handleLogin"
          />
        </div>
        <button class="login-btn" @click="handleLogin" :disabled="loading">
          {{ loading ? '登录中...' : '登 录' }}
        </button>
        <p class="login-footer">© 2026 学校社团管理系统 v1.0</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api/auth'

const router = useRouter()

const roles = [
  { key: 'admin', label: '管理员', icon: '👥' },
  { key: 'club_leader', label: '负责人', icon: '🎯' },
  { key: 'student', label: '学生', icon: '🎓' },
  { key: 'teacher', label: '老师', icon: '👨‍🏫' },
]

const selectedRole = ref('admin')
const username = ref('')
const password = ref('')
const loading = ref(false)

const rolePlaceholder = computed(() => {
  const map = { admin: '请输入管理员账号', club_leader: '请输入工号/账号', student: '请输入学号', teacher: '请输入教师工号' }
  return map[selectedRole.value]
})

function normalizeRole(role) {
  const roleMap = {
    admin: 'admin',
    ADMIN: 'admin',
    '系统管理员': 'admin',
    teacher: 'teacher',
    TEACHER: 'teacher',
    '指导老师': 'teacher',
    student: 'student',
    STUDENT: 'student',
    '普通学生': 'student',
    leader: 'club_leader',
    LEADER: 'club_leader',
    club_leader: 'club_leader',
    CLUB_LEADER: 'club_leader',
    '社团负责人': 'club_leader',
    '社团管理员': 'club_leader'
  }
  return roleMap[role] || ''
}

function getRouteByRole(role) {
  const routeMap = {
    admin: '/admin/dashboard',
    club_leader: '/admin/clubs',
    teacher: '/admin/approval',
    student: '/student/square'
  }
  return routeMap[role] || '/login'
}

async function handleLogin() {
  if (!username.value.trim()) {
    ElMessage.warning('请输入账号')
    return
  }
  if (!password.value) {
    ElMessage.warning('请输入密码')
    return
  }
  
  loading.value = true
  try {
    // 调用后端登录接口
    const res = await login({
      username: username.value,
      password: password.value,
      role: selectedRole.value
    })
    
    // res.data 就是 LoginVO 对象
    const token = res.data?.token
    const userData = res.data || {}
    const actualRole = normalizeRole(userData.role)
    const currentRole = actualRole || selectedRole.value

    if (!token) {
      throw new Error('登录成功，但未获取到 token')
    }
    
    // 保存 token 和用户信息
    sessionStorage.setItem('token', token)
    sessionStorage.setItem('user', JSON.stringify({
      role: currentRole,
      name: userData.name || userData.realName,
      avatar: (userData.name || userData.realName)?.charAt(0) || 'U',
      color: userData.avatarColor || '#3B82F6',
      username: userData.username,
      id: userData.id || userData.userId
    }))
    
    const targetPath = getRouteByRole(currentRole)
    await router.replace(targetPath)
  } catch (error) {
    console.error('登录失败:', error)
    // 错误提示由 request.js 统一处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-container {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
  display: flex;
  width: 880px;
  min-height: 500px;
  overflow: hidden;
}
.login-left {
  background: linear-gradient(135deg, #3B82F6 0%, #1D4ED8 100%);
  color: #fff;
  padding: 56px 36px;
  width: 400px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.login-left h1 { font-size: 26px; margin-bottom: 14px; font-weight: 700; }
.login-left p { font-size: 14px; line-height: 1.8; opacity: 0.9; }
.features { margin-top: 36px; }
.feat {
  display: flex;
  align-items: center;
  margin-bottom: 14px;
  font-size: 13px;
}
.feat-icon {
  width: 30px; height: 30px;
  background: rgba(255,255,255,0.2);
  border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
  margin-right: 10px;
  font-size: 15px;
}
.login-right {
  flex: 1;
  padding: 56px 44px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.login-right h2 { font-size: 22px; color: #1F2937; margin-bottom: 6px; }
.subtitle { color: #9CA3AF; font-size: 13px; margin-bottom: 28px; }
.role-selector {
  display: flex;
  gap: 6px;
  margin-bottom: 22px;
}
.role-btn {
  flex: 1;
  padding: 9px 4px;
  border: 1px solid #E5E7EB;
  border-radius: 8px;
  text-align: center;
  font-size: 12px;
  color: #6B7280;
  background: #fff;
  cursor: pointer;
  transition: all 0.2s;
}
.role-btn.active {
  border-color: #3B82F6;
  color: #3B82F6;
  background: #EFF6FF;
}
.form-group { margin-bottom: 18px; }
.form-group label {
  display: block; font-size: 12px; color: #6B7280;
  margin-bottom: 5px; font-weight: 500;
}
.form-group input {
  width: 100%; height: 42px;
  border: 1px solid #E5E7EB; border-radius: 8px;
  padding: 0 14px; font-size: 14px; outline: none;
  transition: border-color 0.2s;
}
.form-group input:focus { border-color: #3B82F6; }
.form-group input::placeholder { color: #D1D5DB; }
.login-btn {
  width: 100%; height: 42px;
  background: linear-gradient(135deg, #3B82F6, #1D4ED8);
  color: #fff; border: none; border-radius: 8px;
  font-size: 14px; cursor: pointer; font-weight: 500;
  margin-top: 6px; transition: opacity 0.2s;
}
.login-btn:hover { opacity: 0.9; }
.login-footer {
  text-align: center; margin-top: 18px;
  font-size: 11px; color: #9CA3AF;
}
</style>
