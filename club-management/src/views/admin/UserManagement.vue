<template>
  <div>
    <div class="stats-grid" style="grid-template-columns:repeat(3,1fr)">
      <div class="stat-card"><div class="icon-bg">👥</div><div class="label">用户总数</div><div class="value">{{ totalUsers }}</div></div>
      <div class="stat-card"><div class="icon-bg">👤</div><div class="label">管理员</div><div class="value">{{ users.filter(u => u.rawRole === 'admin').length }}</div></div>
      <div class="stat-card"><div class="icon-bg">🎓</div><div class="label">本学期新增</div><div class="value">{{ users.length }}</div></div>
    </div>
    <div class="card">
      <div class="card-header">
        <h3>用户列表</h3>
        <div style="display:flex;gap:8px">
          <button class="btn btn-outline btn-sm" @click="exportUsers">📥 导出</button>
          <button class="btn btn-primary btn-sm" @click="showAddUserDialog = true">＋ 新增用户</button>
        </div>
      </div>
      <div class="search-bar">
        <input class="search-input" placeholder="搜索用户名/学号/姓名..." v-model="keyword" @keyup.enter="handleSearch" />
        <select class="filter-select" v-model="roleFilter" @change="fetchUsers">
          <option value="">全部角色</option>
          <option value="admin">系统管理员</option>
          <option value="club_leader">社团负责人</option>
          <option value="student">普通学生</option>
          <option value="teacher">指导老师</option>
        </select>
        <select class="filter-select" v-model="statusFilter" @change="fetchUsers">
          <option value="">全部状态</option>
          <option value="active">正常</option>
          <option value="inactive">禁用</option>
        </select>
        <button class="btn btn-primary btn-sm" @click="handleSearch">🔍 搜索</button>
      </div>
      <table class="data-table">
        <thead><tr><th>用户信息</th><th>学号/工号</th><th>角色</th><th>所属社团</th><th>状态</th><th>最后登录</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="u in users" :key="u.id">
            <td><div style="display:flex;align-items:center;gap:8px"><div class="avatar" :style="{background:u.color}">{{u.avatar}}</div><div><div style="font-weight:500">{{u.name}}</div><div style="font-size:11px;color:#9CA3AF">{{u.email}}</div></div></div></td>
            <td>{{u.studentId || '—'}}</td>
            <td><span :class="['tag', u.roleClass]">{{u.role}}</span></td>
            <td>{{u.club}}</td>
            <td><span :class="['tag', u.status==='正常'?'tag-green':'tag-gray']">{{u.status}}</span></td>
            <td>{{u.lastLogin}}</td>
            <td>
              <template v-if="canEditUser(u.rawRole)">
                <button class="btn btn-outline btn-sm" @click="handleEditClick(u)">编辑</button>
                <button class="btn btn-danger btn-sm" @click="handleDeleteUser(u)" style="margin-left:8px">删除</button>
              </template>
              <span v-else style="color:#9CA3AF;font-size:12px">本人/社长维护</span>
            </td>
          </tr>
        </tbody>
      </table>
      <div class="pagination">
        <div 
          class="page-btn" 
          :class="{ active: currentPage === 1 }"
          @click="handlePageChange(1)">
          ‹
        </div>
        <div 
          v-for="page in Math.min(5, totalPages)" 
          :key="page"
          class="page-btn" 
          :class="{ active: currentPage === page }"
          @click="handlePageChange(page)">
          {{ page }}
        </div>
        <div 
          class="page-btn" 
          :class="{ active: currentPage === totalPages }"
          @click="handlePageChange(totalPages)">
          ›
        </div>
        <div class="page-info">
          第 {{ currentPage }} 页，共 {{ totalPages }} 页
        </div>
      </div>
    </div>
    <!-- 新增用户对话框 -->
    <div class="dialog-overlay" v-if="showAddUserDialog" @click="showAddUserDialog = false">
      <div class="dialog" @click.stop>
        <div class="dialog-header">
          <h3>新增用户</h3>
          <button class="dialog-close" @click="showAddUserDialog = false">×</button>
        </div>
        <div class="dialog-body">
          <div class="form-group">
            <label>用户名 *</label>
            <input v-model="newUser.username" placeholder="请输入用户名" />
          </div>
          <div class="form-group">
            <label>真实姓名 *</label>
            <input v-model="newUser.realName" placeholder="请输入真实姓名" />
          </div>
          <div class="form-group">
            <label>密码 *</label>
            <input v-model="newUser.password" type="password" placeholder="请输入密码" />
          </div>
          <div class="form-group">
            <label>角色 *</label>
            <select v-model="newUser.role">
              <option value="">请选择角色</option>
              <option value="admin">系统管理员</option>
              <option value="teacher">指导老师</option>
              <option value="club_leader">社团负责人</option>
              <option value="student">普通学生</option>
            </select>
          </div>
          <div class="form-group">
            <label>邮箱</label>
            <input v-model="newUser.email" placeholder="请输入邮箱" />
          </div>
          <div class="form-group">
            <label>电话</label>
            <input v-model="newUser.phone" placeholder="请输入电话" />
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn btn-outline" @click="showAddUserDialog = false">取消</button>
          <button class="btn btn-primary" @click="handleAddUser" :disabled="addingUser">{{ addingUser ? '创建中...' : '创建用户' }}</button>
        </div>
      </div>
    </div>
    
    <!-- 编辑用户对话框 -->
    <div class="dialog-overlay" v-if="showEditUserDialog" @click="showEditUserDialog = false">
      <div class="dialog" @click.stop>
        <div class="dialog-header">
          <h3>编辑用户</h3>
          <button class="dialog-close" @click="showEditUserDialog = false">×</button>
        </div>
        <div class="dialog-body">
          <div class="form-group">
            <label>用户名 *</label>
            <input v-model="editUser.username" placeholder="请输入用户名" />
          </div>
          <div class="form-group">
            <label>真实姓名 *</label>
            <input v-model="editUser.realName" placeholder="请输入真实姓名" />
          </div>
          <div class="form-group">
            <label>密码</label>
            <input v-model="editUser.password" type="password" placeholder="留空则不修改密码" />
          </div>
          <div class="form-group">
            <label>角色 *</label>
            <select v-model="editUser.role">
              <option value="">请选择角色</option>
              <option value="admin">系统管理员</option>
              <option value="teacher">指导老师</option>
            </select>
          </div>
          <div class="form-group">
            <label>邮箱</label>
            <input v-model="editUser.email" placeholder="请输入邮箱" />
          </div>
          <div class="form-group">
            <label>电话</label>
            <input v-model="editUser.phone" placeholder="请输入电话" />
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn btn-outline" @click="showEditUserDialog = false">取消</button>
          <button class="btn btn-primary" @click="handleEditUser" :disabled="editingUser">{{ editingUser ? '保存中...' : '保存修改' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, searchUsers, updateUser, createUser, deleteUser } from '../../api/user'

const keyword = ref('')
const roleFilter = ref('')
const statusFilter = ref('')
const users = ref([])
const totalUsers = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)

// 获取用户列表
const fetchUsers = async () => {
  loading.value = true
  try {
    let response
    if (keyword.value) {
      // 搜索用户
      response = await searchUsers(keyword.value, currentPage.value, pageSize.value, roleFilter.value, statusFilter.value)
    } else {
      // 获取用户列表
      response = await getUserList(currentPage.value, pageSize.value, roleFilter.value, statusFilter.value)
    }
    
    users.value = response.data.records.map(user => ({
      id: user.userId,
      username: user.username,
      studentId: user.studentId,
      name: user.realName,
      email: user.email,
      avatar: (user.realName || user.username || 'U').charAt(0),
      color: getColorByRole(user.role),
      rawRole: user.role,
      role: getRoleDisplayName(user.role),
      roleClass: getRoleClass(user.role),
      club: user.clubName || '—',
      status: user.status === 'inactive' ? '禁用' : '正常',
      lastLogin: formatDate(user.lastLoginTime)
    }))
    
    totalUsers.value = response.data.total
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索用户
const handleSearch = () => {
  currentPage.value = 1
  fetchUsers()
}

// 根据角色获取颜色
const getColorByRole = (role) => {
  const colorMap = {
    'admin': '#3B82F6',
    'teacher': '#8B5CF6',
    'club_leader': '#10B981',
    'student': '#F59E0B'
  }
  return colorMap[role] || '#6B7280'
}

// 获取角色显示名称
const getRoleDisplayName = (role) => {
  const roleMap = {
    'admin': '系统管理员',
    'teacher': '指导老师',
    'club_leader': '社团负责人',
    'student': '普通学生'
  }
  return roleMap[role] || role
}

// 获取角色标签样式
const getRoleClass = (role) => {
  const classMap = {
    'admin': 'tag-red',
    'teacher': 'tag-orange',
    'club_leader': 'tag-purple',
    'student': 'tag-blue'
  }
  return classMap[role] || 'tag-gray'
}

const canEditUser = (role) => ['admin', 'teacher'].includes(role)

const csvCell = (value) => {
  const text = value == null ? '' : String(value)
  return `"${text.replace(/"/g, '""')}"`
}

const exportUsers = () => {
  const rows = [
    ['用户名', '姓名', '学号/工号', '角色', '所属社团', '状态', '邮箱', '最后登录'],
    ...users.value.map(user => [
      user.username,
      user.name,
      user.studentId || '',
      user.role,
      user.club,
      user.status,
      user.email || '',
      user.lastLogin || ''
    ])
  ]
  const csv = rows.map(row => row.map(csvCell).join(',')).join('\n')
  const blob = new Blob([`\uFEFF${csv}`], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `users-page-${currentPage.value}.csv`
  link.click()
  URL.revokeObjectURL(url)
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

// 分页处理
const handlePageChange = (page) => {
  currentPage.value = page
  fetchUsers()
}

// 计算总页数
const totalPages = computed(() => {
  return Math.ceil(totalUsers.value / pageSize.value)
})

// 初始化
onMounted(() => {
  fetchUsers()
})

// 新增用户相关
const showAddUserDialog = ref(false)
const addingUser = ref(false)
const newUser = ref({
  username: '',
  realName: '',
  password: '',
  role: '',
  email: '',
  phone: ''
})

// 编辑用户相关
const showEditUserDialog = ref(false)
const editingUser = ref(false)
const editUser = ref({
  userId: null,
  username: '',
  realName: '',
  password: '',
  role: '',
  email: '',
  phone: ''
})

// 处理新增用户
const handleAddUser = async () => {
  // 表单验证
  if (!newUser.value.username.trim()) {
    ElMessage.warning('请输入用户名')
    return
  }
  if (!newUser.value.realName.trim()) {
    ElMessage.warning('请输入真实姓名')
    return
  }
  if (!newUser.value.password) {
    ElMessage.warning('请输入密码')
    return
  }
  if (!newUser.value.role) {
    ElMessage.warning('请选择角色')
    return
  }
  
  addingUser.value = true
  try {
    await createUser(newUser.value)
    ElMessage.success('用户创建成功')
    showAddUserDialog.value = false
    // 重置表单
    newUser.value = {
      username: '',
      realName: '',
      password: '',
      role: '',
      email: '',
      phone: ''
    }
    // 重新加载用户列表
    await fetchUsers()
  } catch (error) {
    console.error('创建用户失败:', error)
  } finally {
    addingUser.value = false
  }
}

// 处理编辑点击
const handleEditClick = (user) => {
  if (!canEditUser(user.rawRole)) {
    ElMessage.warning('普通社员和社团负责人资料由本人或社长维护')
    return
  }
  // 将用户数据填充到编辑表单
  editUser.value = {
    userId: user.id,
    username: user.username,
    realName: user.name,
    password: '',
    role: user.rawRole,
    email: user.email || '',
    phone: user.phone || ''
  }
  showEditUserDialog.value = true
}

const handleDeleteUser = async (user) => {
  if (!canEditUser(user.rawRole)) {
    ElMessage.warning('普通社员和社团负责人账号不能在用户管理中直接删除')
    return
  }
  try {
    await ElMessageBox.confirm(`确定停用账号 "${user.name}" 吗？`, '提示')
    await deleteUser(user.id)
    ElMessage.success('账号已停用')
    await fetchUsers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error)
    }
  }
}

// 处理编辑用户
const handleEditUser = async () => {
  // 表单验证
  if (!editUser.value.username.trim()) {
    ElMessage.warning('请输入用户名')
    return
  }
  if (!editUser.value.realName.trim()) {
    ElMessage.warning('请输入真实姓名')
    return
  }
  if (!editUser.value.role) {
    ElMessage.warning('请选择角色')
    return
  }
  
  editingUser.value = true
  try {
    // 准备更新数据
    const updateData = {
      username: editUser.value.username,
      realName: editUser.value.realName,
      role: editUser.value.role,
      email: editUser.value.email || null,
      phone: editUser.value.phone || null
    }
    
    // 如果密码不为空，则包含密码
    if (editUser.value.password) {
      updateData.password = editUser.value.password
    }
    
    await updateUser(editUser.value.userId, updateData)
    
    ElMessage.success('用户信息更新成功')
    showEditUserDialog.value = false
    // 重新加载用户列表
    await fetchUsers()
  } catch (error) {
    console.error('更新用户失败:', error)
    console.error('错误详情:', error.response?.data || error.message)
  } finally {
    editingUser.value = false
  }
}
</script>

<style scoped>
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog {
  background: white;
  border-radius: 8px;
  width: 500px;
  max-width: 90vw;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.dialog-header h3 {
  margin: 0;
  color: #333;
}

.dialog-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
}

.dialog-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: #333;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #3B82F6;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid #eee;
  background: #f8f9fa;
}
</style>
